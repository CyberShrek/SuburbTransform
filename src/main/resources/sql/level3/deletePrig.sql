WITH t1_ids AS (
    SELECT DISTINCT t1_id
    FROM prigl3.co22_meta meta
    WHERE request_date = ${requestDate}
      AND meta.l2_prig_idnum IS NOT NULL
),
delete_t1 AS (
    DELETE FROM prigl3.co22_t1
    WHERE p2 IN (SELECT t1_id FROM t1_ids)
),
delete_t2 AS (
    DELETE FROM prigl3.co22_t2
    WHERE p3 IN (SELECT t1_id FROM t1_ids)
),
delete_t3 AS (
    DELETE FROM prigl3.co22_t3
    WHERE p3 IN (SELECT t1_id FROM t1_ids)
),
delete_t4 AS (
    DELETE FROM prigl3.co22_t4
    WHERE p3 IN (SELECT t1_id FROM t1_ids)
),
delete_t6 AS (
    DELETE FROM prigl3.co22_t6
    WHERE p3 IN (SELECT t1_id FROM t1_ids)
)
DELETE FROM prigl3.co22_meta
WHERE t1_id IN (SELECT t1_id FROM t1_ids)