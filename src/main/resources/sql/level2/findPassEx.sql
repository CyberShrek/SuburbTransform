SELECT
    idnum,
    yyyymm,
    request_date,
    npp,
    ticket_ser,
    ticket_num,
    last_name,
    first_name,
    patronymic,
    snils

FROM zzz_rawdl2.l2_pass_ex
WHERE idnum = ANY (${idnums}::bigint[])
ORDER BY idnum