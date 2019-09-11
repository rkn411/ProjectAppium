package com.appium.utils;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class Screenshots {
	public static final WebDriver driver = null;
	static Logger logger = Logger.getLogger(Screenshots.class);

	/**
	 * screenshots
	 *
	 * @param locator
	 */
	public static String takeSnapShotAndRetPath(WebDriver driver, String methodName) throws Exception {

		String FullSnapShotFilePath = "";

		try {
			File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			String sFilename = null;
			sFilename = "Screenshot-" + methodName + DateAndTime.getDateTime() + ".png";
			FullSnapShotFilePath = System.getProperty("user.dir") + "\\output\\ScreenShots\\" + sFilename;
			FileUtils.copyFile(scrFile, new File(FullSnapShotFilePath));
		} catch (Exception e) {

		}

		return FullSnapShotFilePath;
	}

	public static String takeSnapShotAndRetPath(WebDriver driver) throws Exception {
		String FullSnapShotFilePath = "";
		try {
			logger.info("Taking Screenshot");
			File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			String sFilename = null;
			sFilename = "verificationFailure_Screenshot.png";
			FullSnapShotFilePath = System.getProperty("user.dir") + "\\output\\ScreenShots\\" + sFilename;
			FileUtils.copyFile(scrFile, new File(FullSnapShotFilePath));
		} catch (Exception e) {
		}

		return FullSnapShotFilePath;
	}

}
