package cipm.consistency.vsum.test.appspace;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

public class LoggingSetup {

    private static final Level DEFAULT_LOG_LEVEL = Level.INFO;

    private static class TrimmingLogFormat extends PatternLayout {
        private List<String> trim;

        public TrimmingLogFormat(String format, List<String> trim) {
            super(format);
            this.trim = trim;
        }

        @Override
        public String format(LoggingEvent event) {
            String msg = super.format(event);
            for (var t : trim) {
                msg = msg.replace(t, "[..]");
            }
            return msg;
        }
    }

    /*
     * mute the logger
     */
    public static void setMinLogLevel(Level minLevel) {
        for (var key : getLevels().keySet()) {
            var logger = Logger.getLogger(key);
            if (logger.getLevel() == null || logger.getLevel()
                .toInt() < minLevel.toInt()) {
                logger.setLevel(minLevel);
            }
        }
    }

    public static void setMaxLogLevel(Level maxLevel) {
        for (var key : getLevels().keySet()) {
            var logger = Logger.getLogger(key);
            if (logger.getLevel()
                .toInt() > maxLevel.toInt()) {
                logger.setLevel(maxLevel);
            }
        }
    }

    public static void resetLogLevels() {
        // set log levels
        for (var entry : getLevels().entrySet()) {
            Logger.getLogger(entry.getKey())
                .setLevel(entry.getValue());
        }
    }

    private static Map<String, Level> getLevels() {
        var levels = new HashMap<String, Level>();
        levels.put("cipm.consistency", Level.INFO);
        levels.put("cipm.consistency.cpr.luapcm.seffreconstruction", Level.WARN);
        levels.put("cipm.consistency.commitintegration.lang.lua.LuaPostProcessor", Level.ERROR);
//        levels.put("mir.reactions.luaPcmUpdate", Level.INFO);
//        levels.put("mir.routines.luaPcmUpdate", Level.INFO);
//        levels.put("mir.routines.statementFunctionDeclaration_operationSignature", Level.WARN);
//        levels.put("mir.routines.statement_actions", Level.INFO);
        levels.put("tools.vitruv", Level.INFO);
        levels.put("tools.vitruv.change.atomic.id.IdResolverImpl", Level.WARN);
        levels.put("tools.vitruv.framework.vsum.internal.VirtualModelImpl", Level.WARN);
        levels.put("org.xtext.lua", Level.INFO);
        levels.put("jamopp", Level.ALL);
        
        
        // if we do not enable trace level logging, we artificially lower the
        // the coverage of the reactions
        var coverage = false;
        if (coverage) {
            levels.put("tools.vitruv.change.propagation.impl.ChangePropagator", Level.TRACE);
            levels.put("mir.reactions", Level.TRACE);
            levels.put("mir.routines", Level.TRACE);
        } else {
            levels.put("mir.reactions", Level.WARN);
            levels.put("mir.routines", Level.WARN);
        }

        return levels;
    }

    public static void setupLogging(Level minLogLevel) {
        resetLogLevels();
        var level = Level.DEBUG;
        setMinLogLevel(level);

        var rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(level);
        rootLogger.removeAllAppenders();
        var toTrim = List.of(System.getProperty("user.dir"), "org.xtext.lua.lua.impl", "cipm.consistency");
        var logFormat = new TrimmingLogFormat("%-5p: %c%n    %m%n", toTrim);
        ConsoleAppender ap = new ConsoleAppender(logFormat, ConsoleAppender.SYSTEM_OUT);
        rootLogger.addAppender(ap);
    }


    public static void setupLogging() {
        setupLogging(DEFAULT_LOG_LEVEL);
    }
}
