package gui.logger;

@FunctionalInterface
public interface Logger {
    /**
     * Log a string with a priority level
     * @param level The level of logging
     * @param message The message to print
     */
    void log(LogLevel level, String message);
}
