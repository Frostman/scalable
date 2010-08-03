package ru.frostman.scalable.log;

import java.util.HashMap;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public enum LoggingLevel {
    TRACE,
    DEBUG,
    INFO,
    WARN,
    ERROR,
    FATAL;

    private static final HashMap<String, LoggingLevel> abbrevs;

    public static LoggingLevel getByAbbrev(String str) {
        return abbrevs.get(str.toLowerCase());
    }

    static {
        abbrevs = new HashMap<String, LoggingLevel>();
        //abbrevs.put("",LoggingLevel.);
        abbrevs.put("trace", LoggingLevel.TRACE);
        abbrevs.put("debug", LoggingLevel.DEBUG);
        abbrevs.put("info", LoggingLevel.INFO);
        abbrevs.put("warn", LoggingLevel.WARN);
        abbrevs.put("error", LoggingLevel.ERROR);
        abbrevs.put("fatal", LoggingLevel.FATAL);
    }
}
