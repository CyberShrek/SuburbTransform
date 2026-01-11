SELECT count(*)
FROM prigl3.co22_meta meta
WHERE request_date = ${requestDate}
  AND l2_prig_idnum IS NOT NULL