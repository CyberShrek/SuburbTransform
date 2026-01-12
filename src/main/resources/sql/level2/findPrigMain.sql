SELECT *
FROM zzz_rawdl2.l2_prig_main
WHERE idnum = ANY (${idnums}::bigint[])
ORDER BY idnum