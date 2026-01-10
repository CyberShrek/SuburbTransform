package org.vniizht.suburbtransform.util;

//import org.vniizht.suburbsweb.ng_logger.NgLogger;

import org.vniizht.suburbtransform.ng_logger.NgLogger;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

    private final NgLogger nglog = new NgLogger();
    private final StringBuilder logBuilder = new StringBuilder();

    public Log() throws Exception {
    }


    public void addLine(String... messages) throws Exception {
        addLine(true, messages);
    }

    private void addLine(boolean withNG, String... messages) throws Exception {
        for (String line : messages) {
            System.out.println(line);
            if (withNG) nglog.writeInfo(line);
            logBuilder
                    .append(line)
                    .append("\n");
        }
    }

    public void addTimeLine(String... messages) throws Exception {
        for (String line : messages) {
            addLine(false, new SimpleDateFormat("HH:mm:ss\t").format(new Date()) + line);
            nglog.writeInfo(line);
        }
    }

    public String sumUp() throws Exception {
        addLine(false, "-------------------------------------\n");
        return logBuilder.toString();
    }

    public String sumUp(String... finalMessages) throws Exception {
        addLine(false, "-------------------------------------");
        for (String message : finalMessages) {
            addLine(message);
        }
        return logBuilder
                .append("\n")
                .toString();
    }

    public void error(String message) throws Exception {
        nglog.writeError(message);
        sumUp(message);
    }

    public String toString() {
        return logBuilder
                .toString();
    }

    public void finish(String... finalMessages) throws Exception {
        sumUp(finalMessages);
        nglog.initProcessEnd();
    }
}
