SELECT *
FROM zzz_rawdl2.l2_pass_ex
WHERE idnum = ANY (${idnums}::bigint[])
ORDER BY idnum, npp