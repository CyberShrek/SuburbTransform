SELECT
    idnum,
    yyyymm,
    request_date,
    doc_reg,
    route_num,
    route_distance,
    tariff_sum,
    department_sum,
    departure_station,
    arrival_station,
    region_code,
    tarif_type

FROM zzz_rawdl2.l2_prig_cost
WHERE idnum = ANY (${idnums}::bigint[])
ORDER BY idnum