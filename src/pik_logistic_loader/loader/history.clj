(ns pik-logistic-loader.loader.history
  (:require [clojure.tools.logging :as log]
            [clojure.java.jdbc :refer [with-db-transaction]]
            [pik-logistic-loader.db.core :refer [db-data] :rename {db-data db}]
            [pik-logistic-loader.db.commands :as c]
            [pik-logistic-loader.navixy.history :as api]
            [pik-logistic-loader.loader.data :as loader-data]))


(defn- events-ids [events]
  (map :id events))

(defn- trackers-ids [events]
  (set (map :tracker_id events)))

(defn- history-unread-events-into-db []
  (when-let [events (api/unread-events)]
    (with-db-transaction [tx db]
      (doseq [e events]
        (c/tracker-event! tx e)))
    (log/info (str "History update: " (count events) " event(s)"))
    {:events (events-ids events)
     :trackers (trackers-ids events)}))


(defn- mark-events-read [ids]
  (doseq [id ids]
    (api/event-mark-read id)))


(defn process []
  (log/info "History update start")

  (when-let [all-ids (history-unread-events-into-db)]
    (when-let [t-ids (:trackers all-ids)]
      (loader-data/tracker-states t-ids)
      (log/info (str "History update: " (count t-ids) " tracker(s)")))
    (when-let [e-ids (:events all-ids)]
      (mark-events-read e-ids)))

  (log/info "History update finish"))

;(process)