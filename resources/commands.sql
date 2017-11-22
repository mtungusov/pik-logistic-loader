-- :name disable-trackers! :! :n
-- :doc disable all trackers
UPDATE trackers SET
  live = 0

-- :name tracker! :! :n
-- :doc add or update tracker
UPDATE trackers SET
  label = :label, group_id = :group_id, live = 1
WHERE id = :id
IF @@rowcount = 0
  BEGIN
    INSERT INTO trackers VALUES (:id, :label, :group_id, 1)
  END


-- :name disable-groups! :! :n
-- :doc disable all groups
UPDATE groups SET
  live = 0

-- :name group! :! :n
UPDATE groups SET
  title = :title, live = 1
WHERE id = :id
IF @@rowcount = 0
  BEGIN
    INSERT INTO groups VALUES (:id, :title, 1)
  END



-- :name rule! :! :n
UPDATE rules SET
  type = :type, name = :name, zone_id = :zone_id
WHERE id = :id
IF @@rowcount = 0
  BEGIN
    INSERT INTO rules (id, type, name, zone_id)
    VALUES (:id, :type, :name, :zone_id)
  END


-- :name disable-zones! :! :n
-- :doc disable all zones
UPDATE zones SET
  live = 0

-- :name zone! :! :n
UPDATE zones SET
  label = :label, address = :address, live = 1
WHERE id = :id
IF @@rowcount = 0
  BEGIN
    INSERT INTO zones (id, label, address, live)
    VALUES (:id, :label, :address, 1)
  END


-- :name tracker-state! :! :n
UPDATE tracker_states SET
  movement_status = :movement_status, last_update = :last_update,
  connection_status = :connection_status,
  gsm_updated = :gsm_updated, gps_updated = :gps_updated,
  gps_lat = :gps_lat, gps_lng = :gps_lng
WHERE tracker_id = :tracker_id
IF @@rowcount = 0
  BEGIN
    INSERT INTO tracker_states (tracker_id, last_update, movement_status, connection_status,
                                gsm_updated, gps_updated, gps_lat, gps_lng)
    VALUES (:tracker_id, :last_update, :movement_status, :connection_status,
            :gsm_updated, :gps_updated, :gps_lat, :gps_lng)
  END

-- :name remove-events! :! :n
DELETE FROM tracker_events
WHERE time >= :time

-- :name tracker-event! :! :n
UPDATE tracker_events SET
  event = :event, time = :time, tracker_id = :tracker_id, rule_id = :rule_id, message = :message, address = :address
WHERE id = :id
IF @@rowcount = 0
  BEGIN
    INSERT INTO tracker_events (id, event, time, tracker_id, rule_id, message, address)
    VALUES (:id, :event, :time, :tracker_id, :rule_id, :message, :address)
  END
