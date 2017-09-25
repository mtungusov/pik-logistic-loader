(ns pik-logistic-loader.navixy.core
  (:require [clj-http.client :as client]
            [slingshot.slingshot :refer [try+]]
            [pik-logistic-loader.config :refer [settings]]
            [safely.core :refer [safely]]
            [clojure.tools.logging :as log]
            [cheshire.core :refer [parse-string]]))

(defn root-url []
  (get-in settings [:navixy :root-url]))

(def default-param {:insecure? true
                    :accept :json
                    ;:query-params {:hash auth-hash}
                    :as :json
                    :debug false})

(defn post-try [url params]
  (try+
    (client/post url params)
    (catch [:status 400] {:keys [status body]}
      (let [body-from-json (parse-string body true)
            status-code (get-in body-from-json [:status :code])]
        {:status status :body {:status-code status-code}}))))

(defn post
  ([url params]
   (let [full-url (str (root-url) url)
         full-params (merge default-param params)
         ;resp (safely (client/post full-url full-params))
         resp (safely (post-try full-url full-params)
                      :on-error
                      :log-errors true
                      :default {}
                      :max-retry 7
                      :retry-delay [:rand-cycle [1000 2500 5000 10000 20000 40000 80000 160000] :+/- 0.50])
         status (:status resp)
         body (:body resp)]
     {:status status :body body}))
  ([url]
   (post url {}))
  ([url params token]
   (let [params-with-hash (merge params {:query-params {:hash token}})]
     (post url params-with-hash))))

;(def url "/history/tracker/list")
;(def full-url (str (root-url) url))
;(def params {:form-params {:from "2017-09-18 11:59:59"
;                           :to "2017-09-25 11:59:59"
;                           :trackers [144942]}})
;(def full-params (merge default-param params))
;
;(post-try full-url full-params)
;(post url params)

;;(println full-params)
;
;(let [resp (client/post full-url (merge full-params {:throw-exceptions false}))
;      status (:status resp)
;      body (:body resp)]
;  {:status status :body (parse-string body true)})
;
;(try+
;  (client/post full-url full-params)
;  (catch [:status 400] {:keys [status body]}
;    println "400" status body))

