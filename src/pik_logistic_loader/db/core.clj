(ns pik-logistic-loader.db.core
  (:require [pik-logistic-loader.config :refer [settings]]
            [pik-logistic-loader.db.commands :as c]))

(def db {:subprotocol (get-in @settings [:sql :subprotocol])
         :subname (get-in @settings [:sql :subname])
         :user (get-in @settings [:sql :user])
         :password (get-in @settings [:sql :password])
         :domain (get-in @settings [:sql :domain])})

;(c/trackers db)
;(c/tracker! db {:id 2 :label "test tracker 2" :group-id 0})
;(c/rule! db {:id 1 :type "type 11" :name "test name" :zone-id 0})
;(c/zone! db {:id 1 :label "test zone", :address "address!"})
