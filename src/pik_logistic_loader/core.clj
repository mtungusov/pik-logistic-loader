(ns pik-logistic-loader.core
  (:require [clojure.tools.logging :as log]
            [clojure.java.io :as io])
  (:use [clojure.core.async :only [thread]]
        [pik-logistic-loader.config :only [update-settings]])
  (:gen-class))

(def state (atom {}))

(defn init [args]
  (swap! state assoc :running true))

(defn stop []
  (swap! state assoc :running false)
  (log/info "Stopping...")
  (shutdown-agents)
  (Thread/sleep 1000)
  (log/info "Stopped!"))

(defn nsi-loader []
  (log/info "NSI loaded"))

(defn data-loader []
  (log/info "Data loaded"))

(defn start []
  (log/info "Started")
  (update-settings)
  (log/info "Update NSI")
  (nsi-loader)
  (log/info "Update DATA")
  (data-loader))

(defn run-in-thread [period f]
  (thread
    (while (:running @state)
      (Thread/sleep period)
      (f))))

(defn -main [& args]
  (init args)
  (start)
  (.addShutdownHook (Runtime/getRuntime)
                    (Thread. stop))
  (run-in-thread (* 5 60 1000) nsi-loader)
  (run-in-thread (* 1 60 1000) data-loader)
  (while (:running @state) (Thread/sleep 1000)))
