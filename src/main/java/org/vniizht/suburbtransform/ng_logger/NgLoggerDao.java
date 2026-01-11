package org.vniizht.suburbtransform.ng_logger;

import org.vniizht.suburbtransform.database.SimpleJdbcLogger;

import java.io.IOException;
import java.net.Inet4Address;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * @author Alexander Ilyin
 * <p>
 * Пишет логи в nglog.log
 */
public class NgLoggerDao { private NgLoggerDao() {}

    private final static String systemName = "SuburbL3";
    private final static String processName = "transformation";

    public static void addProcess() throws Exception {
        SimpleJdbcLogger.update("nglog/addProcess", new HashMap<String, Object>(){{
            put("systemName", systemName);
            put("processName", processName);
            put("hostAddress", Inet4Address.getLocalHost().getHostName());
        }});
    }

    public static String getLastProcessId() throws Exception {
        return (String) SimpleJdbcLogger.queryForMatrix("nglog/findLastProcessId", new HashMap<String, Object>(){{
            put("systemName", systemName);
        }}).get(0).get(0);
    }

    public static void insertLog(NgLog log, String processId, int errorCode) throws Exception {
        SimpleJdbcLogger.update("nglog/insertLog", new HashMap<String, Object>(){{
            put("messageCode", log.getMessageCode());
            put("errorCode", errorCode);
            put("messageText", log.getMessageText().replaceAll("'", "''"));
            put("hostAddress", Inet4Address.getLocalHost().getHostName());
            put("systemName", systemName);
            put("processName", processName);
            put("processId", processId);
        }});
    }

    public static void endProcess(String processId, boolean wasError) throws Exception {
        String statusR = (wasError) ? "E" : "O";
        SimpleJdbcLogger.update("nglog/endProcess", new HashMap<String, Object>(){{
            put("statusR", statusR);
            put("processId", processId);
        }});
    }
}
