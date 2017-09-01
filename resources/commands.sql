-- :name tracker! :! :n
-- :doc add or update tracker
UPDATE trackers SET
  label = :label, group_id = :group-id
WHERE id = :id
IF @@rowcount = 0
  BEGIN
    INSERT INTO trackers VALUES (:id, :label, :group-id)
  END

-- :name group! :! :n
UPDATE groups SET
  title = :title
WHERE id = :id
IF @@rowcount = 0
  BEGIN
    INSERT INTO groups VALUES (:id, :title)
  END

-- :name rule! :! :n
UPDATE rules SET
  type = :type, name = :name, zone_id = :zone-id
WHERE id = :id
IF @@rowcount = 0
  BEGIN
    INSERT INTO rules (id, type, name, zone_id)
    VALUES (:id, :type, :name, :zone-id)
  END

-- :name zone! :! :n
UPDATE zones SET
  label = :label, address = :address
WHERE id = :id
IF @@rowcount = 0
  BEGIN
    INSERT INTO zones ( id, label, address)
    VALUES (:id, :label, :address)
  END

-- :name tracker_event! :! :n
UPDATE tracker_events SET
  event = :event, time = :time, tracker_id = :tracker-id, rule_id = :rule-id, message = :message, address = :address
WHERE id = :id
IF @@rowcount = 0
  BEGIN
    INSERT INTO tracker_events (id, event, time, tracker_id, rule_id, message, address)
    VALUES (:id, :event, :time, :tracker-id, :rule-id, :message, :address)
  END

-- :name tracker_state! :! :n
UPDATE tracker_states SET
  last_update = :last-update, movement_status = :movement-status, connection_status = :connection_status
WHERE tracker_id = :tracker_id
IF @@rowcount = 0
  BEGIN
    INSERT INTO tracker_states (tracker_id, last_update, movement_status, connection_status)
    VALUES (:tracker_id, :last-update, :movement-status, :connection_status)
  END
