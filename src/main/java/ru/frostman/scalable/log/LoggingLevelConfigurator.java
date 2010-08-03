package ru.frostman.scalable.log;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.Properties;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class LoggingLevelConfigurator {
    private static final Logger log = Logger.getLogger(LoggingLevelConfigurator.class);

    public static void configure(LoggingLevel loggingLevel) {
        Properties properties = new Properties();

        properties.put("log4j.rootLogger", loggingLevel + ", A1");
        properties.put("log4j.appender.A1", "org.apache.log4j.ConsoleAppender");
        properties.put("log4j.appender.A1.layout", "org.apache.log4j.PatternLayout");
        properties.put("log4j.appender.A1.layout.ConversionPattern", "%d %-5p - %m%n");
        // "%d [%t] %-5p %c - %m%n" - prints class name
        
        PropertyConfigurator.configure(properties);

        log.info("Logger configurated successfully");
        log.debug("Logging level: "+ loggingLevel);
    }
}
