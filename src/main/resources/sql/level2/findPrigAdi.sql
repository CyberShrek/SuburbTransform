SELECT
    idnum,
    yyyymm,
    request_date,
    employee_cat,
    bilgroup_secur,
    bilgroup_code,
    benefit_doc,
    employee_unit,
    surname,
    initials,
    dependent_surname,
    dependent_initials,
    snils

FROM zzz_rawdl2.l2_prig_adi
WHERE idnum = ANY (${idnums}::bigint[])
ORDER BY idnum