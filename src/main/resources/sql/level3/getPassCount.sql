SELECT count(*)
FROM prigl3.co22_meta meta
WHERE request_date = ${requestDate}
  AND l2_pass_idnum IS NOT NULL