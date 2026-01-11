INSERT INTO nglog.log (date_log, time_log, e_class, e_code, e_text, s_ip, system_n, proc_n, proc_id)
VALUES (
        CURRENT_DATE,
        CURRENT_TIME,
        ${messageCode},
        ${errorCode},
        ${messageText},
        ${hostAddress},
        ${systemName},
        ${processName},
        ${processId}
)