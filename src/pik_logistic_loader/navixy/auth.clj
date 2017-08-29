(ns pik-logistic-loader.navixy.auth
  (:require [clojure.java.io :as io]
            [pik-logistic-loader.config :refer [settings]]
            [pik-logistic-loader.navixy.core :as navixy])
  (:import [java.io File]))

(def token-filename (str "." File/separator "config" File/separator ".api_token"))
(def token-filepath (.getAbsolutePath (io/as-file token-filename)))
(def token-file (io/as-file token-filepath))
(def time-diff-in-milisec (* 60 60 24 25 1000))

(defn token-from-file []
  (let [exist? (.exists token-file)
        mtime (.lastModified token-file)
        now (.. (java.util.Date.) getTime)
        fresh?  (< (- now mtime) time-diff-in-milisec)]
    (when (and exist? fresh?)
      (slurp token-file))))

(defn token-to-file [token]
  (spit token-filename token)
  token)

(defn token-from-api []
  (let [user (:user @settings)
        pass (:pass @settings)
        resp (navixy/post "/user/auth" {:form-params {:login user
                                                      :password pass}})]
    (get-in resp [:body :hash])))

(defn get-token []
  (if-let [token (token-from-file)]
    token
    (token-to-file (token-from-api))))

;(get-token)
;(token-to-file "new token 3")
;(token-from-file)

