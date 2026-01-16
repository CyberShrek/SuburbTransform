SELECT *
FROM rawdl2.l2_prig_adi
WHERE idnum = ANY (${idnums}::bigint[])
ORDER BY idnum