package openblocks;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class Log {
	private Log() {}
	
	public static Logger logger;

	public static void log(Level level, String format, Object... data) {
        logger.log(level, String.format(format, data));
    }

    public static void log(Level level, Throwable ex, String format, Object... data) {
        logger.log(level, String.format(format, data), ex);
    }

    public static void severe(Throwable ex, String format, Object... data) {
        log(Level.SEVERE, ex, format, data);
    }

    public static void severe(String format, Object... data) {
        log(Level.SEVERE, format, data);
    }

    public static void warn(Throwable ex, String format, Object... data) {
        log(Level.WARNING, ex, format, data);
    }

    public static void warn(String format, Object... data) {
        log(Level.WARNING, format, data);
    }

    public static void info(String format, Object... data) {
        log(Level.INFO, format, data);
    }

    public static void fine(String format, Object... data) {
        log(Level.FINE, format, data);
    }

    public static void finer(String format, Object... data) {
        log(Level.FINER, format, data);
    }

    public static void finest(String format, Object... data) {
        log(Level.FINEST, format, data);
    }
}
