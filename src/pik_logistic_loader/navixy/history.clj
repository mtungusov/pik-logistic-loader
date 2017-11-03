(ns pik-logistic-loader.navixy.history
  (:require [pik-logistic-loader.navixy.auth :refer [req-with-token]]))


(defn unread-events []
  (let [url "/history/unread/list"
        params {}
        path [:body :list]]
    (get-in (req-with-token url params) path)))


(defn event-mark-read [event-id]
  (let [url "/history/mark_read"
        params {:form-params {:id event-id}}
        path [:body]]
    (get-in (req-with-token url params) path)))


;(unread-events)

;[{:address "Окское ш., Серпухов, Московская обл., Россия, 142207",
;  :track_id 0,
;  :rule_id 155249,
;  :tracker_id 207502,
;  :time "2017-11-03 16:32:32",
;  :extra {},
;  :type "tracker",
;  :event "offline",
;  :id 188103011,
;  :is_read false,
;  :location {:lat 54.9211783, :lng 37.454925, :address "Окское ш., Серпухов, Московская обл., Россия, 142207"},
;  :message "р213ом750 инлоудер РСУ: Выключение маячка или потеря связи"}]


;(event-mark-read 188103231)

;{:success true}