package org.vniizht.suburbtransform.ng_logger;

import lombok.SneakyThrows;

import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.SQLException;

/**
 * @author Alexander Ilyin
 * <p>
 * Пишет логи в БД
 */
public class NgLogger {

    private String processId;
    private boolean wasError = false;

    public NgLogger() throws SQLException, IOException {
        NgLoggerJdbc.addProcess();
        processId = NgLoggerJdbc.getLastProcessId();
    }

    public void writeInfo(String message) throws SQLException, IOException {
        writeLog("I", message);
    }

    public void writeWarning(String message) throws SQLException, IOException {
        writeLog("W", message);
    }

    public void writeError(String message) throws SQLException, IOException {
        writeLog("E", message);
        wasError = true;
    }

    public void writeFatalError(String message) throws SQLException, IOException {
        writeLog("F", message);
        wasError = true;
    }

    public void initProcessEnd() throws SQLException, IOException {
        NgLoggerJdbc.endProcess(processId, wasError);
        processId = "0";
        wasError = false;
    }

    private void writeLog(String code, String message) throws SQLException, IOException {
        StackTraceElement traceElement = Thread.currentThread().getStackTrace()[3];
        NgLoggerJdbc.insertLog(NgLog.builder()
                .messageCode(code)
                .messageText(message)
                .processName(traceElement.getMethodName())
                .build(), processId, wasError ? 1 : 0);
    }
}