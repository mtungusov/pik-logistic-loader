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

(defn tracker-states [tracker-ids]
  (let [url "/tracker/get_states"
        trackers (str "["(clojure.string/join "," tracker-ids)"]")
        params {:form-params {:list_blocked true
                              :trackers trackers}}
        path [:body :states]]
    (get-in (req-with-token url params) path)))

(defn tracker-events [tracker-id from to]
  (let [url "/history/tracker/list"
        trackers (str "["tracker-id"]")
        params {:form-params {:from from
                              :to to
                              :trackers trackers}}
        path [:body :list]]
    (get-in (req-with-token url params) path)))

;(get-token)
;(trackers)
;(groups)
;(rules)
;(zones)
;(tracker-events 144950 "2017-08-24 00:49:00" "2017-08-24 12:49:00")
;(def ts (tracker-states [144950,161633,207507]))
;((first (keys ts)) ts)