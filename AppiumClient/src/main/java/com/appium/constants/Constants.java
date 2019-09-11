package com.appium.constants;

import java.io.File;
import java.util.Calendar;

public class Constants {
	/**
	 * A slight misnomer, this class holds global variables in addition to
	 * constants.
	 */

	public static final String TESTNG_FAILED_XML_FILE = "testng-failed.xml";
	public static final String SELENIUM_DEFAULT_TIMEOUT_PROPERTY = "selenium.default_timeout";
	public static final String SELENIUM_ELEMENT_TIMEOUT_PROPERTY = "selenium.element_timeout";
	public static final String SELENIUM_PAGE_TIMEOUT_PROPERTY = "selenium.page_timeout";

	final static public int CURRENT_YEAR = Calendar.getInstance().get(Calendar.YEAR);

	/**
	 * Sure hope this test doesn't span over mid-night where the date will
	 * technically be different.
	 */
	final static public int CURRENT_MONTH = Calendar.getInstance().get(Calendar.MONTH);

	/**
	 * Sure hope this test doesn't span over mid-night where the date will
	 * technically be different.
	 */
	final static public int CURRENT_DAY = Calendar.getInstance().get(Calendar.DATE);

	/**
	 * In SL/Prod finding resort-room for "less than 3 days" is problematic, almost
	 * impossible, not to have test fail, we are bumping checkIn-checkOut dates
	 */
	final static public int prodDaysOffset = Integer.getInteger("SELENIUM_PROD_DAYS_OFFSET_PROPERTY", 150);

	/**
	 * Used to quickly alter check-in-date in production since availability can be
	 * difficult.
	 */
	final static public int prodMonthOffset = Integer.getInteger("SELENIUM_PROD_MONTH_OFFSET_PROPERTY", 3);
	/** The global system property line.separator */
	final static public String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

	/** An alias for the global system property line.separator */
	final static public String NEW_LINE = LINE_SEPARATOR;

	/** The OS name as returned by the system property os.name */
	final static public String OS_NAME = System.getProperty("os.name", "ERROR").toLowerCase();

	/**
	 * The architecture type of JRE, this does not mean that the physical machine
	 * matches
	 */
	final static public String OS_ARCH_TYPE = System.getProperty("os.arch", "ERROR");

	/**
	 * The architecture type of JRE, this does not mean that the physical machine
	 * matches
	 */
	final static public String ARCH_TYPE = System.getProperty("sun.arch.data.model", "ERROR");

	/** An alias for File.separator */
	final static public String DIR_SEPARATOR = File.separator;

	/** The current path of the project */
	final static public String CURRENT_DIR = determineCurrentPath();
	/** The default timeout in seconds, should be a generous default time */
	final static public long GLOBAL_DRIVER_TIMEOUT = Integer.getInteger(SELENIUM_DEFAULT_TIMEOUT_PROPERTY, 60);

	/**
	 * The timeout (seconds) for finding web elements on a page, shouldn't be too
	 * long
	 */
	final static public long ELEMENT_TIMEOUT = Integer.getInteger(SELENIUM_ELEMENT_TIMEOUT_PROPERTY, 10);

	/** The timeout (seconds) for page/DOM/transitions, should also be a generous */
	final static public long PAGE_TIMEOUT = Integer.getInteger(SELENIUM_PAGE_TIMEOUT_PROPERTY, 60);

	/**
	 * Defaults to "./" if there's an exception of any sort.
	 * 
	 * @warning Exceptions are swallowed.
	 * @return Constants.DIR_SEPARATOR
	 */
	final private static String determineCurrentPath() {
		try {
			return (new File(".").getCanonicalPath()) + Constants.DIR_SEPARATOR;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "." + Constants.DIR_SEPARATOR;
	}
}
