INSERT INTO nglog.processes (system_n, proc_n, s_ip, datan, statusr)
VALUES (${systemName}, ${processName}, ${hostAddress}, CURRENT_TIMESTAMP, 'R')