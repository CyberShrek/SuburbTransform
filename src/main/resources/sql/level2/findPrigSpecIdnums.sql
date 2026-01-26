SELECT idnum
FROM rawdl2.l2_prig_main
WHERE abonement_type != '0  '
  AND request_date between '2024-11-01' and '2025-10-29'
  AND ticket_begdate <= '2025-11-30'
  AND ticket_enddate >= '2025-11-01'

ORDER BY idnum