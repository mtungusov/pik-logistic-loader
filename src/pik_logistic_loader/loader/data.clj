(ns pik-logistic-loader.loader.data
  (:require [clojure.java.jdbc :refer [with-db-transaction]]
            [pik-logistic-loader.db.core :refer [db]]
            [pik-logistic-loader.db.commands :as c]
            [pik-logistic-loader.db.queries :as q]
            [pik-logistic-loader.navixy.data :as api]))

(defn tracker-states []
  (let [ids (q/tracker-ids db)
        values (api/tracker-states ids)]
    (with-db-transaction [tx db]
      (doseq [[id, v] values
              :let [d (merge v {:tracker_id (name id)})]]
        (c/tracker-state! tx d)))))

;(def ids (q/tracker-ids db))
;(count ids)
;(def st (api/tracker-states ids))
;(count st)
;(doseq [[id, val] st
;        :let [d (merge val {:tracker_id (name id)})]]
;  (println d))
;(tracker-states)
