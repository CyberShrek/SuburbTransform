SELECT *
FROM rawdl2.l2_pass_main_upd
WHERE idnum = ANY (${idnums}::bigint[])
ORDER BY idnum