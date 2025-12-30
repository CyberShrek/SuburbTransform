SELECT
    idnum,
    yyyymm,
    request_date,
    sum_code,
    cnt_code,
    dor_code,
    paymenttype,
    sum_te,
    sum_nde

FROM zzz_rawdl2.l2_pass_cost
WHERE idnum = ANY ${idnums}