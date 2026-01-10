package org.vniizht.suburbtransform.ng_logger;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @author Alexander Ilyin
 * <p>
 * Пишет логи в БД
 */
public class NgLogger {

    private String processId;
    private boolean wasError = false;

    public NgLogger() throws Exception {
        NgLoggerDao.addProcess();
        processId = NgLoggerDao.getLastProcessId();
    }

    public void writeInfo(String message) throws Exception {
        writeLog("I", message);
    }

    public void writeWarning(String message) throws Exception {
        writeLog("W", message);
    }

    public void writeError(String message) throws Exception {
        writeLog("E", message);
        wasError = true;
    }

    public void writeFatalError(String message) throws Exception {
        writeLog("F", message);
        wasError = true;
    }

    public void initProcessEnd() throws Exception {
        NgLoggerDao.endProcess(processId, wasError);
        processId = "0";
        wasError = false;
    }

    private void writeLog(String code, String message) throws Exception {
        StackTraceElement traceElement = Thread.currentThread().getStackTrace()[3];
        NgLoggerDao.insertLog(NgLog.builder()
                .messageCode(code)
                .messageText(message)
                .processName(traceElement.getMethodName())
                .build(), processId, wasError ? 1 : 0);
    }
}