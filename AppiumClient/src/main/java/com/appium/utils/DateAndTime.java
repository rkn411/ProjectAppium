package com.appium.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;

public class DateAndTime extends AppiumWrapper {

	public DateAndTime(AppiumDriver<MobileElement> driver) {
		super(driver);
	}

	/**
	 * Returns current Date Time
	 *
	 * @return Date Time
	 */
	public static String getDateTime() {
		String sDateTime = "";
		try {
			SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");
			SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
			Date now = new Date();
			String strDate = sdfDate.format(now);
			String strTime = sdfTime.format(now);
			strTime = strTime.replace(":", "-");
			sDateTime = "D" + strDate + "_T" + strTime;
		} catch (Exception e) {
			System.err.println(e);
		}
		return sDateTime;
	}

}
