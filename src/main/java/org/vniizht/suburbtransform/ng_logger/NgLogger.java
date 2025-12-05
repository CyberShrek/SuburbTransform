package org.vniizht.suburbtransform.ng_logger;

import lombok.SneakyThrows;

import java.net.UnknownHostException;

/**
 * @author Alexander Ilyin
 * <p>
 * Пишет логи в БД
 */
public abstract class NgLogger {

    private String processId = "0";
    private boolean wasError = false;

    public void initProcessStart() throws UnknownHostException {
        ngLoggerJdbc.addProcess();
        processId = ngLoggerJdbc.getLastProcessId();
    }

    public void writeInfo(String message) {
        writeLog("I", message);
    }

    public void writeWarning(String message) {
        writeLog("W", message);
    }

    public void writeError(String message) {
        writeLog("E", message);
        wasError = true;
    }

    public void writeFatalError(String message) {
        writeLog("F", message);
        wasError = true;
    }

    public void initProcessEnd() {
        ngLoggerJdbc.endProcess(processId, wasError);
        processId = "0";
        wasError = false;
    }

    @SneakyThrows
    private void writeLog(String code, String message) {
        StackTraceElement traceElement = Thread.currentThread().getStackTrace()[3];
        ngLoggerJdbc.insertLog(NgLog.builder()
                .messageCode(code)
                .messageText(message)
                .processName(traceElement.getMethodName())
                .build(), processId, wasError ? 1 : 0);
    }
}