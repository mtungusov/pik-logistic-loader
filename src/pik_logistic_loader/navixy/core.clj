(ns pik-logistic-loader.navixy.core
  (:require [clj-http.client :as client]
            [pik-logistic-loader.config :refer [settings update-settings]]
            [safely.core :refer [safely]]))

(update-settings)
(def root-url (get-in @settings [:navixy :root-url]))
(def default-param {:insecure? true
                    :accept :json
                    ;:query-params {:hash auth-hash}
                    :as :json
                    :debug false})

(defn post
  ([url params]
   (let [full-url (str root-url url)
         full-params (merge default-param params)
         resp (safely (client/post full-url full-params)
                      :on-error
                      :log-errors true
                      :default {}
                      :max-retry 1
                      :retry-delay [:rand-cycle [1000 2500 5000 10000 20000] :+/- 0.50])
         status (:status resp)
         body (:body resp)]
     {:status status :body body}))
  ([url]
   (post url {}))
  ([url params token]
   (let [params-with-hash (merge params {:query-params {:hash token}})]
     (post url params-with-hash))))
