SELECT
    idnum,
    yyyymm,
    request_date,
    no_use

FROM zzz_rawdl2.l2_pass_main_upd
WHERE idnum = ANY ${idnums}
ORDER BY idnum