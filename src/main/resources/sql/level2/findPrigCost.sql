SELECT *
FROM rawdl2.l2_prig_cost
WHERE idnum = ANY (${idnums}::bigint[])
ORDER BY idnum