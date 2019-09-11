package com.appium.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.openqa.selenium.WebDriver;

import com.appium.constants.Constants;

public class Log {
	final static public String FOLDER_PATH = Log.initializeLogFolder();
	final private static Logger defaultLogger = Log.initializeDefaultLogger();
	// To keep track of driver-logger for each test
	private static Map<String, Logger> logNormalDriverLogger = new HashMap<String, Logger>();
	private static Map<String, Logger> logErrorDriverLogger = new HashMap<String, Logger>();
	private static Map<String, Log> logInstances = new HashMap<String, Log>();

	private Map<String, Logger> normalLoggers = new HashMap<String, Logger>();
	private Map<String, Logger> errorLoggers = new HashMap<String, Logger>();
	private String testName = "";
	private boolean isUsingStdOut = false;
	private boolean isUsingStdErr = false;

	/**
	 * A very busy constructor that initializes an essential bunch of loggers that
	 * support regular logging as well error logging.
	 *
	 * @param testName
	 * @param driver
	 * @throws Exception
	 */
	public Log(String testName, WebDriver driver) throws Exception {
		try {
			this.testName = testName;

			// We're using the driver to generate a generally unique key to
			// store into maps and so forth.
			String key = driver.toString();

			synchronized (logInstances) {
				if (!logInstances.containsKey(key)) {
					logInstances.put(key, this);
				}
			}

			String loggerKey = testName + ".log";
			String fullLogPath = FOLDER_PATH + "log_" + testName + ".log";
			Logger logger = initializeLog(driver, loggerKey, fullLogPath, false);
			normalLoggers.put(testName, logger);

			synchronized (logNormalDriverLogger) {
				logNormalDriverLogger.put(key, logger);
			}

			loggerKey = testName + ".errorlog";
			fullLogPath = FOLDER_PATH + "error_log_" + testName + ".log";
			logger = initializeLog(driver, loggerKey, fullLogPath, true);
			errorLoggers.put(testName, logger);

			synchronized (logErrorDriverLogger) {
				logErrorDriverLogger.put(key, logger);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}
	/* *//**
			 * A convenience function for intialization and error handling.
			 * 
			 * @param testName
			 * @param driver
			 * @return
			 * @throws Exception
			 *//*
				 * public static Log initialize(String testName, WebDriver driver) throws
				 * Exception { try { // Setup the log file and initialize the logger. Log log =
				 * new Log(testName, driver); Log.log(driver).info(driver.toString()); return
				 * log;
				 * 
				 * } catch (Exception ex) { throw new
				 * LogInitializationException("Log did not initialize", ex, driver); } } public
				 * LogInitializationException(String message, Throwable cause, WebDriver driver)
				 * { super("Log Initialization Failure: " + message, cause, driver); }
				 */

	/**
	 * @param driver
	 * @return If the Log class was initialized, it will attempt to return an
	 *         instance of a logger that maps to the driver. Otherwise, it will
	 *         return a default (i.e. anonymous) instance of a logger.
	 */
	public static Logger log(WebDriver driver) {
		if (driver != null) {
			String key = driver.toString();
			synchronized (logNormalDriverLogger) {
				if (logNormalDriverLogger.containsKey(key)) {
					return logNormalDriverLogger.get(key);
				}
			}
		}
		return defaultLogger;
	}

	/**
	 * This logger is a globally scoped logger intended only to be used when the
	 * web-driver has not yet been initialized or when it is unavailable i.e. NULL.
	 * <p>
	 * TODO: Update so that the default QaTest will associate this with a test.
	 * Meaning, it will have a LOG file to write to.
	 *
	 * @return
	 */
	public static final Logger getDefaultLogger() {
		return defaultLogger;
	}

	/**
	 * Initializes the logger instance based on testName and the filePath.
	 *
	 * @param testName
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	private Logger initializeLog(WebDriver driver, String testName, String filePath, boolean isErrorLog)
			throws Exception {

		Logger logger = Logger.getLogger(testName);

		// On retries (second test run within the same thread/process), we need
		// to check if the logger already has the formatter and presumably the
		// file handler.

		if (retrieveFormatter(logger) == null) {
			File file = new File(filePath);
			FileHandler fileHandler = (file.exists() && file.isFile()) ? new FileHandler(filePath, true)
					: new FileHandler(filePath);

			DefaultLogFormatter formatter = new DefaultLogFormatter(isErrorLog);
			formatter.setDriver(driver);
			formatter.setTestName(testName);
			fileHandler.setFormatter(formatter);

			logger.addHandler(fileHandler);
			logger.setUseParentHandlers(false);

		} else {
			// This probably won't show up anywhere on the console.
			logger.config("Log handler and formatter has already been added for " + filePath);
		}

		return logger;
	}

	/**
	 * Initializes the "default" logger, which is no longer the built-in anonymous
	 * logger provided by JUL.
	 *
	 * @return
	 */
	private static Logger initializeDefaultLogger() {
		try {
			Logger logger = Logger.getLogger("wdpr.default");
			Handler handler = new ConsoleHandler();
			Formatter formatter = new CompactLogFormatter(false);
			handler.setFormatter(formatter);
			logger.addHandler(handler);
			logger.setUseParentHandlers(false);
			logger.setLevel(Level.INFO);
			return logger;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	private static String initializeLogFolder() {
		String filePath = "";
		try {
			String currentDirectory = new java.io.File(".").getCanonicalPath();

			String propertiesFile = currentDirectory + Constants.DIR_SEPARATOR + "config.properties";

			FileInputStream inputStream = new FileInputStream(propertiesFile);
			Properties property = new Properties();
			property.load(inputStream);

			// We're allowing the user to override the default log file path.

			filePath = property.getProperty("logFilePath");

			// If nothing valid was provided previously, then we'll use
			// defaults.
			if (filePath == null || filePath.isEmpty() || filePath.toLowerCase().equals("default")) {

				filePath = currentDirectory + Constants.DIR_SEPARATOR + "log" + Constants.DIR_SEPARATOR;
			}

			// if the directory does not exist, create it
			File dirPath = new File(currentDirectory + Constants.DIR_SEPARATOR + filePath);
			if (!dirPath.exists())
				dirPath.mkdir();

		} catch (Exception ex) {
			throw new RuntimeException(ex.toString(), ex);
		}

		return filePath;
	}

	/**
	 * A quick way to get a hold of the formatter associated with the logger.
	 *
	 * @param logger
	 * @return The formatter or null if no formatter was found.
	 * @throws Exception
	 */
	static public DefaultLogFormatter retrieveFormatter(Logger logger) throws Exception {
		Handler[] handlers = logger.getHandlers();
		for (int i = 0; handlers != null && i < handlers.length; ++i) {
			if (handlers[i] instanceof FileHandler) {
				if (handlers[i].getFormatter() instanceof DefaultLogFormatter) {
					return (DefaultLogFormatter) handlers[i].getFormatter();
				}
			}
		}
		return null;
	}

	/**
	 * The initial implementation of a log formatter. For a log formatter that uses
	 * a slightly less "noisy" date-time-format, see {@link CompactLogFormatter}. as
	 * providing an option to forward output to the console (std out, err).
	 */
	public static class DefaultLogFormatter extends Formatter {

		private WebDriver driver = null;
		private boolean errorFormatter = false;
		private boolean stdOutEnabled = false;
		private boolean stdErrEnabled = false;
		private boolean testNamePrefixEnabled = false;
		private Level stdStreamLevel = Level.ALL;
		private String testName = "";

		public DefaultLogFormatter(boolean errorFormatter) {
			this.errorFormatter = errorFormatter;
		}

		public void setDriver(WebDriver driver) {
			this.driver = driver;
		}

		public void setStdOutEnabled(boolean stdOutEnabled) {
			this.stdOutEnabled = stdOutEnabled;
		}

		public void setStdErrEnabled(boolean stdErrEnabled) {
			this.stdErrEnabled = stdErrEnabled;
		}

		public void setTestNamePrefixEnabled(boolean testNamePrefixEnabled) {
			this.testNamePrefixEnabled = testNamePrefixEnabled;
		}

		public void setStdStreamLevel(Level stdStreamLevel) {
			this.stdStreamLevel = stdStreamLevel;
		}

		public void setTestName(String testName) {
			this.testName = testName;
		}

		protected String getDate(LogRecord record) {
			return (new Date(record.getMillis())).toString();
		}

		protected String getSource(LogRecord record) {
			String className = record.getSourceClassName();
			if (className == null || className.isEmpty()) {
				className = "UNKNOWN_CLASS";
			}
			String methodName = record.getSourceMethodName();
			if (methodName == null || methodName.isEmpty()) {
				methodName = "";
			} else {
				methodName = " " + methodName;
			}
			return "[".concat(className).concat(methodName).concat("]");
		}

		protected String getLogLevel(LogRecord record) {
			return record.getLevel().getLocalizedName();
		}

		protected String getWhitespace(LogRecord record) {
			int localizedNameSize = record.getLevel().getLocalizedName().length();
			String whitespace = ":";
			for (int i = 0; i < 11 - localizedNameSize; i++) {
				whitespace += " ";
			}
			return whitespace;
		}

		protected String getFormattedMessage(LogRecord record) {
			return getSource(record).concat(" ").concat(getLogLevel(record)).concat(getWhitespace(record))
					.concat(formatMessage(record));
		}

		@Override
		public String format(LogRecord record) {

			String msg = getFormattedMessage(record);

			StringBuilder buffer = new StringBuilder().append(getDate(record)).append(" ").append(msg)
					.append(Constants.NEW_LINE);

			if (record.getThrown() != null) {
				try {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					record.getThrown().printStackTrace(pw);
					pw.close();
					buffer.append(sw.toString());
				} catch (Exception ex) {
					// No-op. Swallow exceptions for now.
				}
			}
			String result = buffer.toString();

			if (stdErrEnabled) {
				if (stdStreamLevel.intValue() <= record.getLevel().intValue()) {
					System.err.println(testNamePrefixEnabled ? testName + " " + msg : msg);
				}
			} else if (stdOutEnabled) {
				if (stdStreamLevel.intValue() <= record.getLevel().intValue()) {
					if (record.getLevel().equals(Level.SEVERE) || record.getLevel().equals(Level.WARNING)) {
						System.err.println(testNamePrefixEnabled ? testName + " " + msg : msg);
					} else {
						System.out.println(testNamePrefixEnabled ? testName + " " + msg : msg);
					}
				}
			}

			if (!errorFormatter && driver != null && record.getLevel() != null
					&& record.getLevel().equals(Level.SEVERE)) {

			}
			return result;
		}
	}

	/**
	 * A class that overrides a few of the default implementations within
	 * {@link DefaultLogFormatter}.
	 */
	public static class CompactLogFormatter extends DefaultLogFormatter {

		public CompactLogFormatter(boolean errorFormatter) {
			super(errorFormatter);
			setTestNamePrefixEnabled(true);
		}

		@Override
		protected String getDate(LogRecord record) {
			SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
			return formatter.format(new Date(record.getMillis()));
		}

		@Override
		protected String getLogLevel(LogRecord record) {
			if (record.getLevel().equals(Level.WARNING) || record.getLevel().equals(Level.SEVERE)) {
				return record.getLevel().getLocalizedName() + " ";
			}
			return "";
		}

		@Override
		protected String getWhitespace(LogRecord record) {
			return "";
		}
	}

}
