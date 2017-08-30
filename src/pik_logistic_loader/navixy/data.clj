(ns pik-logistic-loader.navixy.data
  (:require [pik-logistic-loader.navixy.core :refer [post]]
            [pik-logistic-loader.navixy.auth :refer [get-token]]))

(defn req-with-token [url params]
  (let [token (get-token)]
    (post url params token)))

(defn trackers []
  (let [url "/tracker/list"
        params {}
        path [:body :list]]
    (get-in (req-with-token url params) path)))

(defn groups []
  (let [url "/tracker/group/list"
        params {}
        path [:body :list]]
    (get-in (req-with-token url params) path)))

(defn rules []
  (let [url "/tracker/rule/list"
        params {}
        path [:body :list]]
    (get-in (req-with-token url params) path)))

(defn zones []
  (let [url "/zone/list"
        params {}
        path [:body :list]]
    (get-in (req-with-token url params) path)))



;(trackers)
;(groups)
;(zones)