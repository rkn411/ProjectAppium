package com.appium.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;

public class AppiumWrapper {
	public static AppiumDriver driver;

	public AppiumWrapper(AppiumDriver<MobileElement> driver) {
		this.driver = driver;
	}

}
