UPDATE nglog.processes
SET datao = CURRENT_TIMESTAMP,
time_r    = CURRENT_TIMESTAMP - datan,
statusr   = ${statusR}
WHERE id  = ${processId}