package dev.emi.emi.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmiLog {

	public static final Logger LOG = LoggerFactory.getLogger("EMI");

	public static void info(String str) {
		LOG.info("[EMI] " + str);
	}

	public static void warn(String str) {
		LOG.warn("[EMI] " + str);
	}

	public static void error(String str) {
		LOG.error("[EMI] " + str);
	}

	public static void error(String str, Throwable t) {
		LOG.error(str, t);
	}
}
