-- :name trackers :? :*
SELECT * FROM trackers
WHERE live = 1

-- :name last-tracker-event :? :1
SELECT TOP(1) * FROM tracker_events
WHERE tracker_id = :tracker_id
ORDER BY time DESC
