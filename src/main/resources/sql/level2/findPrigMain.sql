SELECT *
FROM rawdl2.l2_prig_main
WHERE idnum = ANY (${idnums}::bigint[])
ORDER BY idnum