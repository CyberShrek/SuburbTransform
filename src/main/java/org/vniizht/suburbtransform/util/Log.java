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

    public Log() throws SQLException, IOException {
    }


    public void addLine(String... messages) throws SQLException, IOException {
        addLine(true, messages);
    }

    private void addLine(boolean withNG, String... messages) throws SQLException, IOException {
        for (String line : messages) {
            System.out.println(line);
            if (withNG) nglog.writeInfo(line);
            logBuilder
                    .append(line)
                    .append("\n");
        }
    }

    public void addTimeLine(String... messages) throws SQLException, IOException {
        for (String line : messages) {
            addLine(false, new SimpleDateFormat("HH:mm:ss\t").format(new Date()) + line);
            nglog.writeInfo(line);
        }
    }

    public String sumUp() throws SQLException, IOException {
        addLine(false, "-------------------------------------\n");
        return logBuilder.toString();
    }

    public String sumUp(String... finalMessages) throws SQLException, IOException {
        addLine(false, "-------------------------------------");
        for (String message : finalMessages) {
            addLine(message);
        }
        return logBuilder
                .append("\n")
                .toString();
    }

    public void error(String message) throws SQLException, IOException {
        nglog.writeError(message);
        sumUp(message);
    }

    public String toString() {
        return logBuilder
                .toString();
    }

    public void finish(String... finalMessages) throws SQLException, IOException {
        sumUp(finalMessages);
        nglog.initProcessEnd();
    }
}
