(ns pik-logistic-loader.config
  (:require [clojure.java.io :as io]
            [cprop.core :refer [load-config]])
  (:import [java.io File]))

(def token-filename (str "." File/separator "config" File/separator ".api_token"))
(def secrets-filename (str "." File/separator "config" File/separator "secrets.edn"))

(def settings (atom {:token-filepath   (.getAbsolutePath (io/as-file token-filename))
                     :secrets-filepath (.getAbsolutePath (io/as-file secrets-filename))}))

(defn load-secrets []
  (load-config :file (:secrets-filepath @settings)
               :merge [@settings]))

(defn update-settings []
  (reset! settings (load-secrets)))
