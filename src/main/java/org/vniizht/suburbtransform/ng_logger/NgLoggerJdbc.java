package org.vniizht.suburbtransform.ng_logger;

import org.vniizht.suburbtransform.database.SimpleJdbc;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.sql.SQLException;

/**
 * @author Alexander Ilyin
 * <p>
 * Пишет логи в nglog.log
 */
public abstract class NgLoggerJdbc {

    private final String systemName = "suburbWeb";
    private final String processName = "transformation";

    public void addProcess() throws IOException, SQLException {
        
        SimpleJdbc.queryForList("nglog/addProcess", null);
        jdbcTemplate.update("INSERT INTO nglog.processes (system_n, proc_n, s_ip, datan, statusr)" +
                " VALUES (" +
                "'" + systemName + "', " +
                "'" + processName + "', " +
                "'" + Inet4Address.getLocalHost().getHostAddress() + "', " +
                "CURRENT_TIMESTAMP, " +
                "'R')");
    }

    public String getLastProcessId() {
        return jdbcTemplate.queryForObject("SELECT id FROM nglog.processes " +
                        "WHERE system_n = '" + systemName + "' " +
                        "ORDER BY id DESC LIMIT 1",
                String.class);
    }

    public void insertLog(NgLog log, String processId, int errorCode) throws UnknownHostException {
        jdbcTemplate.update("INSERT INTO nglog.log (date_log, time_log, e_class, e_code, e_text, s_ip, system_n, proc_n, proc_id)" +
                " VALUES (" +
                "CURRENT_DATE, " +
                "CURRENT_TIME, " +
                "'" + log.getMessageCode() + "', " +
                errorCode + ", " +
                "'" + log.getMessageText().replaceAll("'", "''") + "', " +
                "'" + Inet4Address.getLocalHost().getHostAddress() + "', " +
                "'" + systemName + "', " +
                "'" + processName + "', " +
                 processId +")");
    }

    public void endProcess(String processId, boolean wasError) {
        String statusR = (wasError) ? "E" : "O";
        jdbcTemplate.update("UPDATE nglog.processes " +
                " SET datao = CURRENT_TIMESTAMP, " +
                " time_r = CURRENT_TIMESTAMP - datan, " +
                " statusr = '" + statusR + "'" +
                " WHERE id = " + processId);
    }
}
