package com.walmart.ticketingservice;

import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.rules.TestWatchman;
import org.junit.runners.model.FrameworkMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base Class for all tests.
 */
public abstract class BaseTest {

	/**
	 * Slfj logger corresponding to the subclasses.
	 */
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * watchman for logging the start and end of tests.
	 */
	@Rule
	public MethodRule watchman = new TestWatchman() {


		public void starting(FrameworkMethod method) {
			logger.info("Run Test {}...", method.getName());
		}

		public void succeeded(FrameworkMethod method) {
			logger.info("Test {} succeeded.", method.getName());
		}

		public void failed(Throwable e, FrameworkMethod method) {
			logger.error("Test {} failed with {}.", method.getName(), e);
		}
	};

}
