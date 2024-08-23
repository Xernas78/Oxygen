package dev.xernas.oxygen.logging;

import dev.xernas.oxygen.exception.OxygenException;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;

public class OLogger {

    private Level level = Level.INFO;

    public void setLevel(Level level) {
        this.level = level;
    }

    public void log(Level level, String message, PrintStream stream) {
        stream.println("[Oxygen] [" + level + "] " + message);
    }

    public void fatal(OxygenException e) {
        log(Level.FATAL, e.getMessage(), System.err);
        throw new RuntimeException(e);
    }

    public void warn(String message) {
        log(Level.WARNING, message, System.out);
    }

    public void warn(String message, Object obj) {
        warn(message.replace("{}", obj.toString()));
    }

    public void info(String message) {
        log(Level.INFO, message, System.out);
    }

    public void info(String message, Object obj) {
        info(message.replace("{}", obj.toString()));
    }

    public void debug(String message) {
        log(Level.DEBUG, message, System.out);
    }

    public void debug(String message, Object obj) {
        debug(message.replace("{}", obj == null ? "null" : obj.toString()));
    }

    public void debugList(List<?> list, String message) {
        StringBuilder builder = new StringBuilder();
        for (Object obj : list) {
            builder.append(obj).append(", ");
        }
        debug(message.replace("{}", builder.toString()));
    }

    public void debugMap(Map<?, ?> map, String message) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            builder.append(entry.getKey()).append(": ").append(entry.getValue()).append(", ");
        }
        debug(message.replace("{}", builder.toString().isEmpty() ? "Empty" : builder.toString()));
    }

}
