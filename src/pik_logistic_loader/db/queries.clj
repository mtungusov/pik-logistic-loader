(ns pik-logistic-loader.db.queries
  (:require [hugsql.core :as hugsql]))

(hugsql/def-db-fns "queries.sql")

(defn tracker-ids [conn]
  (map :id (trackers conn)))
