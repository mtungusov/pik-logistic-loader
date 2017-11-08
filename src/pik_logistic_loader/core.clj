(ns pik-logistic-loader.core
  (:require [clojure.tools.logging :as log]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.java.io :as io]
            ; [clojure.core.async :refer [thread]]
            [mount.core :as mount]
            [pik-logistic-loader.config :refer [settings]]
            [pik-logistic-loader.db.core :refer [db-data db-nsi]]
            [pik-logistic-loader.loader.nsi :as nsi]
            [pik-logistic-loader.loader.data :as data]
            [pik-logistic-loader.loader.history :as history])
  (:gen-class))

(def state (atom {}))

(def cli-options
  [["-f" "--from Date" "From Date: yyyy-MM-dd HH:mm:ss"
    :id :from-date]])


(defn- init [args]
  (swap! state assoc :running true)
  (mount/start #'settings
               #'db-data
               #'db-nsi
               (mount/with-args (parse-opts args cli-options))))


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
  (log/info "Updating NSI")
  (nsi/process-all))


(defn- data-loader
  ([from-date] (do
                 (log/info "Updating DATA")
                 (log/info (str "Data loading from: " from-date))
                 (data/process-all from-date)
                 (log/info "Data loaded")))
  ([] (do
        (log/info "Updating DATA")
        (data/process-all))))


(defn- history-loader []
  (history/process))

(defn- start []
  (if-let [from-date (get-in (mount/args) [:options :from-date])]
    (do
      (log/info "Starting only DATA load with specific time")
      (nsi-loader)
      (data-loader from-date)
      (System/exit 1))
    (do
      (log/info "Starting in daemon mode")
      (nsi-loader)
      (data-loader))))


; (defn- run-in-thread [period f stop-fun]
;   (thread
;     (while (:running @state)
;       (Thread/sleep period)
;       (let [r (try
;                 (f)
;                 :ok
;                 (catch Exception e
;                   (log/error (.getMessage e))
;                   :error))]
;         (when (= r :error)
;           (stop-fun))))))


(defn -main [& args]
  (init args)
  (.addShutdownHook (Runtime/getRuntime)
                    (Thread. stop))
  (start)
  (run-in-thread (* 1 60 1000) history-loader)
  (run-in-thread (* 5 60 1000) nsi-loader)
  
  (try
    (while (:running @state)
      (Thread/sleep 1000))
    (catch InterruptedException e)))

  ; (while (:running @state)
  ;   (data-loader)
  ;   (Thread/sleep (* 1 60 1000))))
