(ns pik-logistic-loader.db.commands
  (:require [hugsql.core :as hugsql]))

(hugsql/def-db-fns "commands.sql")
