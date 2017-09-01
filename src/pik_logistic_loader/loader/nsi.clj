(ns pik-logistic-loader.loader.nsi
  (:require [clojure.java.jdbc :refer [with-db-transaction]]
            [pik-logistic-loader.db.core :refer [db]]
            [pik-logistic-loader.db.commands :as c]
            [pik-logistic-loader.navixy.data :as api]))

(defn trackers []
  (let [values (api/trackers)]
    (with-db-transaction [tx db]
      (doseq [v values]
        (c/tracker! tx v)))))

(defn groups []
  (let [values (api/groups)]
    (with-db-transaction [tx db]
      (doseq [v values]
        (c/group! tx v)))))

(defn rules []
  (let [values (api/rules)]
    (with-db-transaction [tx db]
      (doseq [v values]
        (c/rule! tx v)))))

(defn zones [])
(let [values (api/zones)]
  (with-db-transaction [tx db]
    (doseq [v values]
      (c/zone! tx v))))

(defn load-all []
  (trackers)
  (groups)
  (rules)
  (zones))

;(trackers)
;(groups)
;(rules)
;(zones)