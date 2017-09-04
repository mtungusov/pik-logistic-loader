(ns pik-logistic-loader.navixy.auth
  (:require [clojure.java.io :as io]
            [pik-logistic-loader.config :refer [settings]]
            [pik-logistic-loader.navixy.core :as navixy])
  (:import [java.io File]))

(def hash-life-in-days 25)
(def time-diff-in-milisec (* 1000 60 60 24 hash-life-in-days))

(defn token-from-file []
  (let [token-file (io/as-file (:token-filepath settings))
        exist? (.exists token-file)
        mtime (.lastModified token-file)
        now (.. (java.util.Date.) getTime)
        fresh?  (< (- now mtime) time-diff-in-milisec)]
    (when (and exist? fresh?)
      (slurp token-file))))

(defn token-to-file [path token]
  (spit path token)
  token)

(defn token-from-api []
  (let [user (get-in settings [:navixy :user])
        pass (get-in settings [:navixy :pass])
        resp (navixy/post "/user/auth" {:form-params {:login user
                                                      :password pass}})]
    (get-in resp [:body :hash])))

(defn get-token []
  (if-let [token (token-from-file)]
    token
    (token-to-file (:token-filepath settings) (token-from-api))))

;(get-in settings [:navixy :user])
;(token-from-file)
;(token-to-file "new token 3")
;(token-from-api)
;(get-token)
;(:token-filepath settings)
