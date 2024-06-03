package dev.xernas.oxygen.logging;

import dev.xernas.oxygen.exception.OxygenException;

import java.io.PrintStream;

public class OLogger {

    private Level level = Level.INFO;

    public void setLevel(Level level) {
        this.level = level;
    }

    public void log(Level level, String message, PrintStream stream) {
        stream.println("[Oxygen] [" + level + "] " + message);
    }

    public void fatal(OxygenException e) throws OxygenException {
        log(Level.FATAL, e.getMessage(), System.err);
        throw e;
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
        debug(message.replace("{}", obj.toString()));
    }

}
