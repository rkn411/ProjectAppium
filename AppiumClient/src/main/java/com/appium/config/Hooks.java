package com.appium.config;

import java.io.File;
import java.util.concurrent.TimeUnit;
import cucumber.api.java.Before;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.appium.utils.CapsJsonParser;

import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServerHasNotBeenStartedLocallyException;
import io.appium.java_client.service.local.AppiumServiceBuilder;

@SuppressWarnings("rawtypes")
public class Hooks {

	private static ThreadLocal<AppiumDriverLocalService> service = new ThreadLocal<>();
	private static ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();
	public static String platform = null;

	/**
	 * Start Appium Server Programmatically before each scenario
	 * 
	 */
	@Before
	public void startServer() {
		AppiumServiceBuilder appiumServiceBuilder = new AppiumServiceBuilder()
				.withAppiumJS(new File("/usr/local/lib/node_modules/appium/build/lib/main.js"))
				.withIPAddress("127.0.0.1");
		service.set(appiumServiceBuilder.build());
		service.get().start();
		if (service == null || !service.get().isRunning()) {
			throw new AppiumServerHasNotBeenStartedLocallyException("An Appium server node is not started!");
		}
		if (driver.get() == null) {
			setDriver();
		}
	}

	/**
	 * This method used to set driver
	 * 
	 * @param -platform name
	 */

	public void setDriver() {
		DesiredCapabilities capabilities = new DesiredCapabilities();
		if (System.getProperty("platform").equalsIgnoreCase("ios")) {
			// include ios caps
			driver.set(new IOSDriver<IOSElement>(service.get().getUrl(), capabilities));
		} else {
			capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
			capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION,
					(String) CapsJsonParser.getJSONObjectValue("android").get("platformVersion"));
			capabilities.setCapability(MobileCapabilityType.DEVICE_NAME,
					(String) CapsJsonParser.getJSONObjectValue("android").get("deviceName"));
			capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME,
					(String) CapsJsonParser.getJSONObjectValue("android").get("automationName"));
			capabilities.setCapability(AndroidMobileCapabilityType.APP_PACKAGE,
					(String) CapsJsonParser.getJSONObjectValue("android").get("appPackage"));
			capabilities.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY,
					(String) CapsJsonParser.getJSONObjectValue("android").get("appActivity"));
			capabilities.setCapability(MobileCapabilityType.APP, System.getProperty("user.dir") + "/Resources/.apk");
			driver.set(new AndroidDriver<AndroidElement>(service.get().getUrl(), capabilities));
		}
	}

	/**
	 * This method used get driver
	 * 
	 */
	public AppiumDriver getDriver() {
		driver.get().manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		return driver.get();
	}

	/**
	 * This method used to get appium service running
	 */
	public AppiumDriverLocalService getService() {
		return service.get();
	}
}
