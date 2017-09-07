(ns pik-logistic-loader.navixy.data
  (:require [pik-logistic-loader.navixy.core :refer [post]]
            [pik-logistic-loader.navixy.auth :refer [get-token]]
            [clj-time.core :as t]
            [clj-time.format :as tf]
            [clj-time.local :as tl]
            [clojure.tools.logging :as log]))

(def max-history-limit 1000)
(def max-report-time-span 120)
(def navyixy-time-formatter (tf/formatter "yyyy-MM-dd HH:mm:ss"))

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
  (log/info (str "POST " tracker-id " from: " from " to: " to))
  (let [url "/history/tracker/list"
        trackers (str "["tracker-id"]")
        params {:form-params {:from from
                              :to to
                              :trackers trackers}}
        path [:body :list]]
    (get-in (req-with-token url params) path)))

;Выход при условиях:
;(size result) < 1000 and (to - from) <= 120_days

;Рекурсия:
;if (+ from 120_days) < to then to = (+ from 120_days)
;if (size result) == 1000 then from = (:time (last result))

; Получает все события если (to - from) <= 120 days
(defn- tracker-events-all
  ([tracker-id from to] (tracker-events-all tracker-id from to []))
  ([tracker-id from to acc] (let [events (tracker-events tracker-id from to)]
                              (if (< (count events) max-history-limit)
                                (into acc events)
                                (let [from-new-time (t/plus (tf/parse navyixy-time-formatter (:time (last events))) (t/seconds 1))
                                      from-new (tf/unparse navyixy-time-formatter from-new-time)]
                                  (tracker-events-all tracker-id from-new to (into acc events)))))))

;(defn get-new-from-time [event]
;  (if-let [time (:time event)]
;    (tl/format-local-time (t/plus (tf/parse navyixy-time-formatter time) (t/seconds 1)) :mysql)
;    (tl/format-local-time (t/minus (tl/local-now) (t/days max-report-time-span)) :mysql)))

(defn get-new-from-time [time]
  (tl/format-local-time (t/plus (tf/parse navyixy-time-formatter time) (t/seconds 1)) :mysql))

; Получает все событие даже если (to - from) > 120 days
(defn tracker-events-all-time
  ([tracker-id from to acc] (let [from-time (tf/parse navyixy-time-formatter from)
                                  to-time (tf/parse navyixy-time-formatter to)
                                  from-to-in-days (t/in-days (t/interval from-time to-time))]
                              (if (> from-to-in-days max-report-time-span)
                                (let [to-new (tf/unparse navyixy-time-formatter (t/plus from-time (t/days 120)))
                                      events (tracker-events-all tracker-id from to-new acc)
                                      from-new (get-new-from-time to-new)]
                                  (tracker-events-all-time tracker-id from-new to events))
                                (into acc (tracker-events-all tracker-id from to)))))
  ([tracker-id from to] (tracker-events-all-time tracker-id from to [])))


;(tracker-events-all-time 144942 "2017-01-01 00:00:00" "2017-09-07 10:49:00")
;(tracker-events-all-time 202802 "2017-01-01 00:00:00" "2017-09-07 10:49:00")

;(get-new-from-time {:time "2017-09-01 10:00:00"})
;(get-new-from-time {})

;(get-token)
;(trackers)
;(groups)
;(rules)
;(zones)
;(def ts (tracker-states [144950,161633,207507]))
;((first (keys ts)) ts)
;(def my-tr (tracker-events 144950 "2017-08-01 00:49:00" "2017-08-24 12:49:00"))
;(def my-tr (tracker-events 144950 "2017-01-01 00:49:00" "2017-05-01 00:49:00"))
;(count my-tr)
;(def my-tr-2 (tracker-events-all 144950 "2017-01-01 00:49:00" "2017-05-01 00:49:00"))
;(count my-tr-2)
;(last my-tr-2)
;(def my-tr-3 (tracker-events-all-time 144950 "2017-01-01 00:49:00" "2017-09-06 12:29:00"))
;(count my-tr-3)
;(last my-tr-3)

;(require '[clj-time.core :as t])
;(require '[clj-time.format :as f])
;;(require '[clj-time.coerce :as c])
;(def navyixy-time-formater (f/formatter "yyyy-MM-dd HH:mm:ss"))
;(def t1 (t/plus (f/parse-local "2017-01-01 00:49:00") (t/days 120)))
;(f/unparse-local navyixy-time-formater t1)
;(def t1 (tf/parse navyixy-time-formatter "2017-01-01 00:49:00"))
;(def t2 (tf/parse navyixy-time-formatter "2017-08-24 12:49:00"))
;(t/in-days (t/interval t1 t2))
;(tf/unparse navyixy-time-formatter (t/plus t1 (t/days 120)))

