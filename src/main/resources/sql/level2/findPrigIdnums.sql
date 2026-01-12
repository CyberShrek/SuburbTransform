SELECT idnum
FROM rawdl2.l2_prig_main
WHERE request_date = ${requestDate}
ORDER BY idnum