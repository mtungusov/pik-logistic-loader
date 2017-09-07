(ns pik-logistic-loader.loader.data
  (:require [clojure.tools.logging :as log]
            [clojure.java.jdbc :refer [with-db-transaction]]
            [pik-logistic-loader.db.core :refer [db]]
            [pik-logistic-loader.db.commands :as c]
            [pik-logistic-loader.db.queries :as q]
            [pik-logistic-loader.navixy.data :as api]
            [clj-time.core :as t]
            [clj-time.local :as tl]))

(def default-time-shift-in-days 7)

(defn- last-tracker-event-time [tracker-id]
  (if-let [from (:time (q/last-tracker-event db {:tracker_id tracker-id}))]
    from
    (tl/format-local-time (t/minus (tl/local-now) (t/days default-time-shift-in-days)) :mysql)))

(defn- tracker-events
  ([tracker-id from] (let [to (tl/format-local-time (tl/local-now) :mysql)
                           events (api/tracker-events-all-time tracker-id from to)]
                       (with-db-transaction [tx db]
                         (doseq [e events]
                           (c/tracker-event! tx e)))
                       (log/info (str "tracker: " tracker-id " from: " from))))
  ([tracker-id] (let [from (last-tracker-event-time tracker-id)]
                  (tracker-events tracker-id from))))

(defn all-tracker-events
  ([from] (let [tracker-ids (q/tracker-ids db)]
            (c/remove-events! db {:time from})
            (doseq [tracker-id tracker-ids]
              (tracker-events tracker-id from))))
  ([] (let [tracker-ids (q/tracker-ids db)]
        (doseq [tracker-id tracker-ids]
          (tracker-events tracker-id)))))

(defn tracker-states []
  (let [ids (q/tracker-ids db)
        values (api/tracker-states ids)]
    (with-db-transaction [tx db]
                         (doseq [[id, v] values
                                 :let [d (merge v {:tracker_id (name id)})]]
                           (c/tracker-state! tx d)))
    (log/info "trackers states loaded")))

(defn process-all
  ([from] (do
            (tracker-states)
            (all-tracker-events from)))
  ([] (do
        (tracker-states)
        (all-tracker-events))))

;(q/tracker-ids db)
;(all-tracker-events "2017-01-01 00:00:00")
;(tracker-events 202802 "2017-01-01 00:00:00")
;(def ids (q/tracker-ids db))
;(count ids)
;(def st (api/tracker-states ids))
;(count st)
;(doseq [[id, val] st
;        :let [d (merge val {:tracker_id (name id)})]]
;  (println d))
;(tracker-states)
;(def to (tf/unparse navyixy-time-formatter (t/now)))
;(tf/unparse navyixy-time-formatter (tl/local-now))
;(tl/format-local-time (tl/local-now) :mysql)
;(t/minus (tl/local-now) (t/days 7))
;(tl/format-local-time (t/minus (tl/local-now) (t/days 7)) :mysql)
;(last-tracker-event-time 144948)
;(tracker-events 144948 "2017-08-30 14:50:27")
;(tracker-events 144942)
;(process-all)
