SELECT
    idnum,
    yyyymm,
    request_date,
    id,
    request_subtype,
    request_time,
    oper,
    oper_g,
    oper_date,
    departure_date,
    arrival_date,
    train_num,
    train_thread,
    agent_code,
    subagent_code,
    carrier_code,
    saleregion_code,
    paymenttype,
    sale_station,
    departure_station,
    arrival_station,
    f_tick,
    carriage_class,
    benefit_code,
    benefitcnt_code,
    military_code,
    trip_direction,
    distance,
    persons_qty,
    seats_qty

FROM zzz_rawdl2.l2_pass_main
WHERE request_date = ${requestDate}
  AND f_r10af3[8]
ORDER BY idnum