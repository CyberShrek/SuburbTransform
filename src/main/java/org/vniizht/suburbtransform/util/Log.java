package org.vniizht.suburbtransform.util;

//import org.vniizht.suburbsweb.ng_logger.NgLogger;

import lombok.SneakyThrows;
import org.vniizht.suburbtransform.ng_logger.NgLogger;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

    private final NgLogger nglog;

    {
        try {
            nglog = new NgLogger();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final StringBuilder logBuilder = new StringBuilder();

    public Log() {
    }


    public void nextLine(String... messages) {
        nextLine(true, messages);
    }

    @SneakyThrows
    private void nextLine(boolean withNG, String... messages){
        if (messages.length == 0)
            System.out.println();
        else for (String line : messages) {
            System.out.println(line);
            if (withNG) nglog.writeInfo(line);
            logBuilder
                    .append(line)
                    .append("\n");
        }
    }

    @SneakyThrows
    public void nextTimeLine(String... messages) {
        for (String line : messages) {
            nextLine(false, new SimpleDateFormat("HH:mm:ss\t").format(new Date()) + line);
            nglog.writeInfo(line);
        }
    }

    public String sumUp() {
        nextLine(false, "-------------------------------------\n");
        return logBuilder.toString();
    }

    public String sumUp(String... finalMessages) {
        nextLine(false, "-------------------------------------");
        for (String message : finalMessages) {
            nextLine(message);
        }
        return logBuilder
                .append("\n")
                .toString();
    }

    public void reline(String message) {
        System.out.print("\r");
        System.out.print(message);
    }

    @SneakyThrows
    public void error(String message) {
        nglog.writeError(message);
        sumUp(message);
    }

    public String toString() {
        return logBuilder
                .toString();
    }

    @SneakyThrows
    public void finish(String... finalMessages) {
        sumUp(finalMessages);
        nglog.initProcessEnd();
    }
}