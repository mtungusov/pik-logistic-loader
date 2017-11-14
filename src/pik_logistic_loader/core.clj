(ns pik-logistic-loader.core
  (:require [clojure.tools.logging :as log]
            [clojure.tools.cli :as cli]
            [clojure.java.io :as io]
            [mount.core :as mount]
            [pik-logistic-loader.config :refer [settings]]
            [pik-logistic-loader.db.core :refer [db-data db-nsi]]
            [pik-logistic-loader.loader.nsi :as nsi]
            [pik-logistic-loader.loader.data :as data]
            [pik-logistic-loader.loader.history :as history])
  (:gen-class))

(def state (atom {}))
(def semaphores (atom {:data-loading false
                       :nsi-loading  false}))

(def cli-options
  [["-f" "--from Date" "From Date: yyyy-MM-dd HH:mm:ss"
    :id :from-date]])


(defn- init [args]
  (swap! state assoc :running true)
  (mount/start #'settings
               #'db-data
               #'db-nsi
               (mount/with-args (cli/parse-opts args cli-options))))


(defn- stop-main-loop []
  (swap! state assoc :running false))


(defn- stop []
  (log/info "Stopping...")
  (stop-main-loop)
  ; (shutdown-agents)
  ;(Thread/sleep 2000)
  (log/info "Stop!"))


(defn- run-in-thread [period f]
  (.start
    (Thread.
      (fn []
        (try
          (while (:running @state)
            (f)
            (Thread/sleep period))
          (catch InterruptedException _)
          (catch Exception e
            (log/info e)
            (stop-main-loop)))))))


(defn- nsi-loader []
  (try
    (do
      (swap! semaphores assoc :nsi-loading true)
      (log/info "NSI Updating.")
      (nsi/process-all)
      (log/info "NSI Updated."))
    (finally (swap! semaphores assoc :nsi-loading false))))


(defn- data-loader
  ([from-date] (do
                 (nsi-loader)
                 (log/info "DATA Updating.")
                 (log/info (str "DATA loading from: " from-date))
                 (data/process-all from-date)
                 (log/info "DATA updated.")))
  ([] (try
        (do
          (swap! semaphores assoc :data-loading true)
          (nsi-loader)
          (log/info "DATA Updating.")
          (data/process-all)
          (log/info "DATA updated."))
        (finally (swap! semaphores assoc :data-loading false)))))


(defn- history-loader []
  (if-not (:data-loading @semaphores)
    (history/process)
    (log/info "History update delay by DATA loading.")))


(defn- tracker-states-loader []
  (if-not (:nsi-loading @semaphores)
    (do
      (log/info "Trackers states loading.")
      (data/tracker-states)
      (log/info "Trackers states loaded."))
    (log/info "Tracker states update delay by NSI loading.")))

(defn- start []
  (if-let [from-date (get-in (mount/args) [:options :from-date])]
    (do
      (log/info "Starting only DATA load with specific time.")
      (data-loader from-date)
      (System/exit 1))
    (log/info "Starting in daemon mode.")))


(defn -main [& args]
  (init args)
  (.addShutdownHook (Runtime/getRuntime)
                    (Thread. stop))
  (start)
  (run-in-thread (* 15 60 1000) data-loader)
  (Thread/sleep 500)
  (run-in-thread (*  1 25 1000) history-loader)
  (run-in-thread (*  1 30 1000) tracker-states-loader)

  (try
    (while (:running @state)
      (Thread/sleep 1000))
    (catch InterruptedException e)))
