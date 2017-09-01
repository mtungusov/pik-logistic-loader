(ns pik-logistic-loader.db.core
  (:require [pik-logistic-loader.config :refer [settings update-settings]]))
            ;[pik-logistic-loader.db.commands :as c]))

(update-settings)
(def db {:subprotocol (get-in @settings [:sql :subprotocol])
         :subname (get-in @settings [:sql :subname])
         :user (get-in @settings [:sql :user])
         :password (get-in @settings [:sql :password])
         :domain (get-in @settings [:sql :domain])})

;(c/trackers db)
;(c/tracker! db {:id 2 :label "test tracker 2" :group_id 0})
;(c/rule! db {:id 1 :type "type 11" :name "test name" :zone_id 0})
;(c/zone! db {:id 1 :label "test zone", :address "address!"})
;(c/tracker-state! db {:tracker_id 1 :last_update "2017-08-31 12:20:30" :movement_status "M" :connection_status "C"})

;(def p {:connection_status "offline",
;        :gps {:updated "2017-09-01 13:26:57",
;              :signal_level 100,
;              :location {:lat 55.5355233, :lng 37.6385},
;              :heading 100,
;              :speed 0},
;        :inputs_update "2017-09-01 13:26:57",
;        :actual_track_update "2017-09-01 13:26:01",
;        :outputs_update "2017-09-01 13:26:57",
;        :movement_status "stopped",
;        :inputs [false],
;        :last_update "2017-09-01 13:37:29",
;        :battery_update nil,
;        :outputs [false],
;        :battery_level nil,
;        :source_id 152411,
;        :gsm {:updated "2017-09-01 13:26:57", :signal_level 77, :network_name "25001", :roaming nil}})
;(name :207507)
;(merge p {:tracker_id (read-string (name :207507))})
;(c/tracker-state! db (merge p {:tracker_id (name  :207509)}))
