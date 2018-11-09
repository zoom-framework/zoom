package com.jzoom.zoom.common.utils;

import org.apache.commons.logging.Log;

public class LogKit {

	public static void debug(Log log, String tag, String format, String... message) {

		log.debug(String.format("[%s] " + format, tag, message));

	}
}
