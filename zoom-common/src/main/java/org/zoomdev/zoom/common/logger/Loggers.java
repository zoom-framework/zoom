package org.zoomdev.zoom.common.logger;

import org.apache.commons.logging.LogFactory;

public class Loggers {

	public static class ZoomLog implements Logger {
		
		private final org.apache.commons.logging.Log log;
		
		private boolean trace;
		private boolean debug;

		public ZoomLog(org.apache.commons.logging.Log log) {
			this.log = log;
			trace = log.isTraceEnabled();
			debug = log.isDebugEnabled();
		}

		public boolean isDebugEnabled() {
			return debug;
		}
		
		public boolean isTraceEnabled() {
			return trace;
		}

		@Override
		public void info(String format, Object... args) {
			log.info( String.format(format, args) );
		}

		@Override
		public void error(Throwable exception, String format, Object... args) {
			log.error(String.format(format, args), exception);
		}


		@Override
		public void error(String format, Object... args) {
			log.error(String.format(format, args));
		}


		@Override
		public void warn(String format, Object... args) {
			log.warn(String.format(format, args));
		}


		@Override
		public void debug(String format, Object... args) {
			log.debug(String.format(format, args));
		}

	}

	public static Logger getLogger() {

		StackTraceElement[] sts = Thread.currentThread().getStackTrace();
		return new ZoomLog(LogFactory.getLog(sts[2].getClassName()));

	}

	public static Logger getLogger(Class<?> clazz) {
		assert (clazz != null);
		return new ZoomLog(LogFactory.getLog(clazz));

	}

}
