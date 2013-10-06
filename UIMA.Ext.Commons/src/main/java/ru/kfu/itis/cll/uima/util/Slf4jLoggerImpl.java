/**
 * 
 */
package ru.kfu.itis.cll.uima.util;

import java.io.OutputStream;
import java.io.PrintStream;
import java.text.MessageFormat;

import org.apache.uima.internal.util.I18nUtil;
import org.apache.uima.resource.ResourceManager;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.apache.uima.util.impl.Log4jLogger_impl;
import org.slf4j.LoggerFactory;

/**
 * UIMA Logger implementation that wraps an SLF4J Logger. Some code is kindly
 * borrowed from {@link Log4jLogger_impl}.
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class Slf4jLoggerImpl implements Logger {

	public static void forceUsingThisImplementation() {
		System.setProperty("org.apache.uima.logger.class", Slf4jLoggerImpl.class.getName());
	}

	private static final String EXCEPTION_MESSAGE = "Exception occurred";

	private org.slf4j.Logger logger = null;

	/**
	 * ResourceManager whose extension ClassLoader will be used to locate the
	 * message digests. Null will cause the ClassLoader to default to
	 * this.class.getClassLoader().
	 */
	private ResourceManager mResourceManager = null;

	/**
	 * create a new instance for the specified source class
	 * 
	 * @param component
	 *            specified source class
	 */
	private Slf4jLoggerImpl(Class<?> component) {
		if (component != null) {
			logger = LoggerFactory.getLogger(component);
		} else {
			logger = LoggerFactory.getLogger("org.apache.uima");
		}
	}

	/**
	 * create a new instance with the default logger from the Slf4j
	 */
	private Slf4jLoggerImpl() {
		this(null);
	}

	public static synchronized Logger getInstance(Class<?> component) {
		return new Slf4jLoggerImpl(component);
	}

	public static synchronized Logger getInstance() {
		return new Slf4jLoggerImpl();
	}

	/**
	 * Logs a message with level INFO.
	 * 
	 * @deprecated use new function with log level
	 * @param aMessage
	 *            the message to be logged
	 */
	@Deprecated
	public void log(String aMessage) {
		if (isLoggable(Level.INFO)) {
			if (aMessage == null || aMessage.equals(""))
				return;
			String[] sourceInfo = getStackTraceInfo(new Throwable());
			LoggerFactory.getLogger(sourceInfo[0]).info(aMessage);
		}
	}

	/**
	 * Logs a message with a message key and the level INFO
	 * 
	 * @deprecated use new function with log level
	 */
	@Deprecated
	public void log(String aResourceBundleName, String aMessageKey,
			Object[] aArguments) {
		if (isLoggable(Level.INFO)) {
			if (aMessageKey == null || aMessageKey.equals(""))
				return;

			String[] sourceInfo = getStackTraceInfo(new Throwable());
			LoggerFactory.getLogger(sourceInfo[0]).info(
					I18nUtil.localizeMessage(aResourceBundleName, aMessageKey,
							aArguments, getExtensionClassLoader()));
		}
	}

	/**
	 * Logs an exception with level INFO
	 * 
	 * @deprecated use new function with log level
	 * @param aException
	 *            the exception to be logged
	 */
	@Deprecated
	public void logException(Exception aException) {
		if (isLoggable(Level.INFO)) {
			if (aException == null)
				return;

			String[] sourceInfo = getStackTraceInfo(new Throwable());

			// log exception
			LoggerFactory.getLogger(sourceInfo[0]).info(EXCEPTION_MESSAGE, aException);
		}
	}

	/**
	 * @deprecated use external configuration possibility
	 */
	@Deprecated
	public void setOutputStream(OutputStream out) {
		throw new UnsupportedOperationException(
				"Method setOutputStream(OutputStream out) not supported");
	}

	/**
	 * @deprecated use external configuration possibility
	 */
	@Deprecated
	public void setOutputStream(PrintStream out) {
		throw new UnsupportedOperationException(
				"Method setOutputStream(PrintStream out) not supported");
	}

	public boolean isLoggable(Level level) {
		switch (level.toInteger()) {
		case Level.OFF_INT:
			return false;
		case Level.SEVERE_INT:
			return logger.isErrorEnabled();
		case Level.WARNING_INT:
			return logger.isWarnEnabled();
		case Level.INFO_INT:
		case Level.CONFIG_INT:
			return logger.isInfoEnabled();
		case Level.FINE_INT:
			return logger.isDebugEnabled();
		default:
			return logger.isTraceEnabled();
		}
	}

	public void setLevel(Level level) {
		// TODO use external adapters, e.g., for logback
		// for casting logger instance to its implementation
		// and using its methods
		System.err.println("Logging level changing is not implemented in Slf4jLoggerImpl");
	}

	private static void doLog(org.slf4j.Logger logger, Level level, String msg) {
		switch (level.toInteger()) {
		case Level.OFF_INT:
			break;
		case Level.SEVERE_INT:
			logger.error(msg);
			break;
		case Level.WARNING_INT:
			logger.warn(msg);
			break;
		case Level.INFO_INT:
		case Level.CONFIG_INT:
			logger.info(msg);
			break;
		case Level.FINE_INT:
			logger.debug(msg);
			break;
		default:
			logger.trace(msg);
		}
	}

	private static void doLog(org.slf4j.Logger logger, Level level, String msg, Throwable thrown) {
		switch (level.toInteger()) {
		case Level.OFF_INT:
			break;
		case Level.SEVERE_INT:
			logger.error(msg, thrown);
			break;
		case Level.WARNING_INT:
			logger.warn(msg, thrown);
			break;
		case Level.INFO_INT:
		case Level.CONFIG_INT:
			logger.info(msg, thrown);
			break;
		case Level.FINE_INT:
			logger.debug(msg, thrown);
			break;
		default:
			logger.trace(msg, thrown);
		}
	}

	private void doLog(Level level, String msg) {
		doLog(logger, level, msg);
	}

	private void doLog(Level level, String msg, Throwable thrown) {
		doLog(logger, level, msg, thrown);
	}

	public void log(Level level, String aMessage) {
		if (isLoggable(level)) {
			if (aMessage == null || aMessage.equals(""))
				return;
			doLog(level, aMessage);
		}
	}

	public void log(Level level, String aMessage, Object param1) {
		if (isLoggable(level)) {
			if (aMessage == null || aMessage.equals(""))
				return;

			doLog(level, MessageFormat.format(aMessage,
					new Object[] { param1 }));
		}
	}

	public void log(Level level, String aMessage, Object[] params) {
		if (isLoggable(level)) {
			if (aMessage == null || aMessage.equals(""))
				return;

			doLog(level, MessageFormat.format(aMessage, params));
		}
	}

	public void log(Level level, String aMessage, Throwable thrown) {
		if (isLoggable(level)) {
			if (aMessage != null && !aMessage.equals("")) {
				doLog(level, aMessage, thrown);
			}
			if (thrown != null && (aMessage == null || aMessage.equals(""))) {
				doLog(level, EXCEPTION_MESSAGE, thrown);
			}
		}

	}

	public void logrb(Level level, String sourceClass, String sourceMethod,
			String bundleName, String msgKey, Object param1) {
		if (isLoggable(level)) {
			if (msgKey == null || msgKey.equals(""))
				return;
			doLog(level, I18nUtil.localizeMessage(bundleName, msgKey,
					new Object[] { param1 }, getExtensionClassLoader()));
		}
	}

	public void logrb(Level level, String sourceClass, String sourceMethod,
			String bundleName, String msgKey, Object[] params) {
		if (isLoggable(level)) {
			if (msgKey == null || msgKey.equals(""))
				return;
			doLog(level, I18nUtil.localizeMessage(bundleName, msgKey,
					params, getExtensionClassLoader()));
		}
	}

	public void logrb(Level level, String sourceClass, String sourceMethod,
			String bundleName, String msgKey, Throwable thrown) {
		if (isLoggable(level)) {
			if (msgKey != null && !msgKey.equals("")) {
				doLog(level,
						I18nUtil.localizeMessage(bundleName, msgKey, null,
								getExtensionClassLoader()), thrown);
			}

			if (thrown != null && (msgKey == null || msgKey.equals(""))) {
				doLog(level, EXCEPTION_MESSAGE, thrown);
			}
		}
	}

	public void logrb(Level level, String sourceClass, String sourceMethod,
			String bundleName, String msgKey) {
		if (isLoggable(level)) {

			if (msgKey == null || msgKey.equals(""))
				return;

			doLog(level,
					I18nUtil.localizeMessage(bundleName, msgKey, null,
							getExtensionClassLoader()));
		}
	}

	@Override
	public void log(String wrapperFQCN, Level level, String message, Throwable thrown) {
		// FIXME
		throw new UnsupportedOperationException();
		// doLog(level, message, thrown);
	}

	public void setResourceManager(ResourceManager resourceManager) {
		mResourceManager = resourceManager;
	}

	/**
	 * Gets the extension ClassLoader to used to locate the message digests. If
	 * this returns null, then message digests will be searched for using
	 * this.class.getClassLoader().
	 */
	private ClassLoader getExtensionClassLoader() {
		if (mResourceManager == null)
			return null;
		else
			return mResourceManager.getExtensionClassLoader();
	}

	/**
	 * returns the method name and the line number if available
	 * 
	 * @param thrown
	 *            the thrown
	 * @return String[] - fist element is the souce class, second element is the
	 *         method name with linenumber if available
	 */
	private String[] getStackTraceInfo(Throwable thrown) {
		StackTraceElement[] stackTraceElement = thrown.getStackTrace();

		String sourceMethod = "";
		String sourceClass = "";
		int lineNumber = 0;
		try {
			lineNumber = stackTraceElement[1].getLineNumber();
			sourceMethod = stackTraceElement[1].getMethodName();
			sourceClass = stackTraceElement[1].getClassName();
		} catch (Exception ex) {
			// do nothing, use the initialized string members
		}

		if (lineNumber > 0) {
			StringBuffer buffer = new StringBuffer(25);
			buffer.append(sourceMethod);
			buffer.append("(");
			buffer.append(lineNumber);
			buffer.append(")");
			sourceMethod = buffer.toString();
		}

		return new String[] { sourceClass, sourceMethod };
	}
}