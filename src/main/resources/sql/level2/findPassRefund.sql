SELECT *
FROM zzz_rawdl2.l2_pass_refund
WHERE idnum = ANY (${idnums}::bigint[])
ORDER BY idnum