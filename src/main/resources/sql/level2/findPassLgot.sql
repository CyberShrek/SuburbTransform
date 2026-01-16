SELECT *
FROM rawdl2.l2_pass_lgots
WHERE idnum = ANY (${idnums}::bigint[])
ORDER BY idnum, npp