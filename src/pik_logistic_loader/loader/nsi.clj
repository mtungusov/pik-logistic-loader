(ns pik-logistic-loader.loader.nsi
  (:require [clojure.java.jdbc :refer [with-db-transaction]]
            [clojure.tools.logging :as log]
            [pik-logistic-loader.db.core :refer [db-nsi] :rename {db-nsi db}]
            [pik-logistic-loader.db.commands :as c]
            [pik-logistic-loader.navixy.data :as api]))


(defn trackers []
  (let [values (api/trackers)]
    (with-db-transaction [tx db]
      (c/disable-trackers! tx)
      (doseq [v values]
        (c/tracker! tx v)))
    (log/info "nsi trackers")))


(defn groups []
  (let [values (api/groups)]
    (with-db-transaction [tx db]
      (c/disable-groups! tx)
      (doseq [v values]
        (c/group! tx v)))
    (log/info "nsi groups")))


(defn rules []
  (let [values (api/rules)]
    (with-db-transaction [tx db]
      (doseq [v values]
        (c/rule! tx v)))
    (log/info "nsi rules")))


(defn zones []
  (let [values (api/zones)]
    (with-db-transaction [tx db]
      (c/disable-zones! tx)
      (doseq [v values]
        (c/zone! tx v)))
    (log/info "nsi zones")))

(defn process-all []
  (trackers)
  (groups)
  (rules)
  (zones))

;(identity db)
;(api/trackers)
;(trackers)
;(groups)
;(rules)
;(zones)
;(process-all)