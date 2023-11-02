package framework.base;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.appmanagement.ApplicationState;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.touch.TapOptions;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;

import org.apache.commons.io.FileUtils;
import org.elasticsearch.common.collect.Tuple;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.FluentWait;

import com.google.common.base.Function;

import framework.base.Utils.MovementsH;
import framework.base.Utils.MovementsV;
import framework.report.Log;
import framework.test.TestUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * The Class AppiumDriverFacade.
 * 
 * This Class implements all the functionalities proper to Mobile driver
 * initialization and common operations such as element synchronization,
 * tapping, swiping etc..
 * 
 * @author carlos.cadena
 */
public class AppiumDriverFacade {

	/** The appium driver. */
	public static ThreadLocal<AppiumDriver<MobileElement>> appiumDriver = new ThreadLocal<AppiumDriver<MobileElement>>();

	/** The Constant pageTimeOut. */
	public static final int pageTimeOut = Integer.valueOf(FrameworkProperties.getTimeout()).intValue();
	
	/** The is android execution. */
	private static ThreadLocal<Boolean> isAndroidExecution = new ThreadLocal<Boolean>() ;
	
	/** The Constant APPIUM_EUROPE. */
	private static final String APPIUM_EUROPE =  "https://eu1.appium.testobject.com/wd/hub";
	
	/** The Constant APPIUM_US. */
	private static final String APPIUM_US =  "https://us1.appium.testobject.com/wd/hub";

	// region Definition Methods
	
    /**
	 * Gets the driver.
	 *
	 * @return the driver
	 */
	public static WebDriver getDriver() {
    	return appiumDriver.get();
    }
    
    /**
     * Sets the driver.
     *
     * @param driver the new driver
     */
    public static void setDriver(AppiumDriver<MobileElement> driver) {
    	appiumDriver.set(driver);
    }

	
	/**
	 * Facade method that initializes the driver instantiating an AndroidDriver or
	 * IOSDriver depending of what's provided on properties also starting a browser using config.properties/system values .
	 *
	 * @author carlos.cadena
	 * @param name the name
	 * @param deviceName the device name
	 * @param platformVersion the platform version
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws URISyntaxException the URI syntax exception
	 */
	public static void createDriverFromProperties(String name, String deviceName, String platformVersion) throws IOException, URISyntaxException {
		createDriver(name,deviceName,platformVersion, FrameworkProperties.getBrowser().isEmpty() ? null :  FrameworkProperties.getBrowser(),
				 FrameworkProperties.getBrowserVersion().isEmpty() ? null :  FrameworkProperties.getBrowserVersion(), 
			     FrameworkProperties.getOS().isEmpty() ? null :  FrameworkProperties.getOS());	
	}

	/**
	 * Facade method that initializes the driver instantiating an AndroidDriver or
	 * IOSDriver depending of what's provided on properties.
	 *
	 * @author carlos.cadena
	 * @param isHybrid the is hybrid
	 * @param name the name
	 * @param deviceName the device name
	 * @param platformVersion the platform version
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws URISyntaxException the URI syntax exception
	 */
	public static void createDriver(boolean isHybrid, String name, String deviceName, String platformVersion) throws IOException, URISyntaxException {
		if(isHybrid) {
			createDriverFromProperties(name, deviceName, platformVersion);
		}
		else {
		createDriver(name,deviceName,platformVersion, null, null, null);
		}
	}
	
	

	/**
	 * Facade method that initializes the driver instantiating an AndroidDriver or
	 * IOSDriver depending of what's provided on properties also starting a browser using custom values.
	 *
	 * @author carlos.cadena
	 * @param name the name
	 * @param deviceName the device name
	 * @param platformVersion the platform version
	 * @param browser the browser
	 * @param version the version
	 * @param os the os
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws URISyntaxException the URI syntax exception
	 */
	public static void createDriver(String name, String deviceName, String platformVersion, String browser, String version, String os) throws IOException, URISyntaxException {
		URL url = null;
		MutableCapabilities capabilities = new DesiredCapabilities();
		browser = browser != null ? TestUtils.toTitle(browser.toString()) : null;
		if (FrameworkProperties.getLocal().equalsIgnoreCase("true")) {
			url = localDriverInitialization(capabilities,  browser, version, os);
		} else {
			url = remoteDriverInitialization(capabilities, name, browser, version, os);
		}
		deviceDriverInitialization(capabilities, deviceName, platformVersion);
		switch (FrameworkProperties.getPlatformName().toUpperCase()) {
		case "ANDROID":
			androidDriverInitialization(capabilities, url);
			break;
		case "IOS":
			iosDriverInitialization(capabilities, url);
			break;
		default:
			throw new IllegalArgumentException(
					String.format("The selected driver %s is not supported", FrameworkProperties.getPlatformName()));
		}
	}
	
	/**
	 * Local driver initialization.
	 *
	 * @param capabilities the capabilities
	 * @param browser the browser
	 * @param version the version
	 * @param os the os
	 * @return the url
	 * @throws MalformedURLException the malformed URL exception
	 */
	public static URL localDriverInitialization(MutableCapabilities capabilities, String browser, String version, String os) throws MalformedURLException{
		if (FrameworkProperties.getPlatformName().equalsIgnoreCase("Android")) {
			if(browser == null) {
			capabilities.setCapability("appPackage", FrameworkProperties.getPackage());
			capabilities.setCapability("appActivity",
					FrameworkProperties.getApp().equals("SMS") ? "com.google.android.apps.messaging.home.HomeActivity" :
						"es.lacaixa.mobile.appcbk.features.splash.view.SplashActivity");
			}
		} else {
			capabilities.setCapability("startIWDP", true);
			capabilities.setCapability("udid", FrameworkProperties.getIOSUdid());
			capabilities.setCapability("xcodeSigningId", "iPhone Developer");
			capabilities.setCapability("xcodeOrgId", FrameworkProperties.getXcodeOrgId());
		}
		initializeBrowserCapabilities(capabilities, browser, version, os);
		return new URL(FrameworkProperties.props.getProperty("appium.local.url"));
	}
	
	/**
	 * Remote driver initialization.
	 *
	 * @param capabilities the capabilities
	 * @param testName the test name
	 * @param browser the browser
	 * @param version the version
	 * @param os the os
	 * @return the url
	 * @throws MalformedURLException the malformed URL exception
	 */
	public static URL remoteDriverInitialization(MutableCapabilities capabilities, String testName, String browser, String version, String os) throws MalformedURLException{
		if (System.getProperty("APP_ID") != null) {
			capabilities.setCapability("testobject_app_id", System.getProperty("APP_ID"));
		}
		capabilities.setCapability("testobject_api_key",
				FrameworkProperties.getTestObjectId(FrameworkProperties.getApp()));
		capabilities.setCapability("testobject_session_creation_timeout", "100000");
		if (FrameworkProperties.getPlatformName().equalsIgnoreCase("iOS")){
			capabilities.setCapability("appiumVersion", "1.15.1");
		}
		initializeBrowserCapabilities(capabilities, browser, version, os);

		if (System.getProperties().containsKey("allDevices")) {
			capabilities.setCapability("testobject_test_name",
					testName.substring(0, testName.indexOf("_")) + " - "
							+ testName.substring(testName.indexOf("-") + 1, testName.lastIndexOf("-"))
									.trim()
							+ " - " + testName.substring(testName.lastIndexOf("-") + 1, testName.length())
									.trim() + (testName.contains("EU") ? " - EU" : " - US"));
			return testName.contains("EU") ? new URL(APPIUM_EUROPE) : new URL(APPIUM_US);
		} else {
			capabilities.setCapability("testobject_test_name",testName.contains("_") ? testName.substring(0, testName.indexOf("_")) : testName);
			return new URL(APPIUM_EUROPE);
		}
	}
	
	/**
	 * Initialize browser capabilities.
	 *
	 * @param capabilities the capabilities
	 * @param browser the browser
	 * @param version the version
	 * @param os the os
	 */
	private static void initializeBrowserCapabilities(MutableCapabilities capabilities, String browser, String version, String os) {
		if(browser != null) {
		capabilities.setCapability("browserName", TestUtils.toTitle(browser.toString()));
		}
		if(version != null) {
			capabilities.setCapability("version", version);
		}
		if(os != null && FrameworkProperties.getLocal().equalsIgnoreCase("False")) {
			capabilities.setCapability("platform", os);
		}
	}
		
	/**
	 * Device driver initialization.
	 *
	 * @param capabilities the capabilities
	 * @param deviceName the device name
	 * @param platformVersion the platform version
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws URISyntaxException the URI syntax exception
	 */
	public static void deviceDriverInitialization(MutableCapabilities capabilities, String deviceName, String platformVersion) throws IOException, URISyntaxException{
		if(FrameworkProperties.getLocal().equalsIgnoreCase("False") && FrameworkProperties.getPlatformName().toUpperCase().equals("ANDROID") && deviceName.equalsIgnoreCase("Dynamic") && !System.getProperties().containsKey("allDevices")) {
			capabilities.setCapability("deviceName", TestUtils.geAvailableAndroidDeviceFromSauceLabs(Boolean.valueOf(FrameworkProperties.getHuawei()), Boolean.valueOf(FrameworkProperties.getHuaweiHms())));
		}
		if (!deviceName.equalsIgnoreCase("Dynamic")) {
			capabilities.setCapability("deviceName", deviceName);
		}
		if (!platformVersion.equalsIgnoreCase("Dynamic")) {
			capabilities.setCapability("platformVersion", platformVersion);
		}
		if (deviceName.equalsIgnoreCase("Dynamic")
				&& FrameworkProperties.getPrivateOnly().equalsIgnoreCase("true")) {
			capabilities.setCapability("privateDevicesOnly", "true");
		}
		if (deviceName.equalsIgnoreCase("Dynamic")
				&& FrameworkProperties.getPhoneOnly().equalsIgnoreCase("true")) {
			capabilities.setCapability("phoneOnly", "true");
		}
	}

	/**
	 * Initialize the IOS Mobile driver.
	 *
	 * @author carlos.cadena
	 * @param capabilities the capabilities
	 * @param url the url
	 */
	public static void iosDriverInitialization(MutableCapabilities capabilities, URL url) {
		capabilities.setCapability("newCommandTimeout", 120);
		capabilities.setCapability("launchTimeout", "300000");
		capabilities.setCapability("platformName", "iOS");
		capabilities.setCapability("autoAcceptAlerts", true);
		capabilities.setCapability("automationName", "XCUITest");
		capabilities.setCapability("simpleIsVisibleCheck", true);
		//capabilities.setCapability("noReset", false);
		capabilities.setCapability("bundleId", FrameworkProperties.getPackage());
		appiumDriver.set(new IOSDriver<MobileElement>(url, capabilities));
		isAndroidExecution.set(false);
	}

	/**
	 * Checks if is android execution.
	 *
	 * @return true, if is android execution
	 */
	public static boolean isAndroidExecution() {
		return isAndroidExecution.get().booleanValue();
	}
	
	/**
	 * Checks if is IOS execution.
	 *
	 * @return true, if is IOS execution
	 */
	public static boolean isIOSExecution() {
		return !isAndroidExecution.get().booleanValue();
	}

	/**
	 * Initialize the Android Mobile driver.
	 *
	 * @author carlos.cadena
	 * @param capabilities the capabilities
	 * @param url the url
	 */
	public static void androidDriverInitialization(MutableCapabilities capabilities, URL url) {
		capabilities.setCapability("platformName", "Android");
		capabilities.setCapability("newCommandTimeout", 120);
		capabilities.setCapability("autoGrantPermissions", true);
		capabilities.setCapability("autoAcceptAlerts", true);
		capabilities.setCapability("automationName", "uiautomator2");
		capabilities.setCapability("appWaitActivity", "*");
		capabilities.setCapability("autoDismissAlerts", true);
		capabilities.setCapability("adbExecTimeout", 100000);
		if (FrameworkProperties.getWeb().equalsIgnoreCase("True")) {
			WebDriverFacade.createDriverForMobileWeb(url, capabilities);
		} else {
			appiumDriver.set(new AndroidDriver<MobileElement>(url, capabilities));
		}
		isAndroidExecution.set(true);
	}

	/**
	 * Get Screen Size Point.
	 * @return A point
	 */
	public static Dimension getScreenSize() {
		return AppiumDriverFacade.appiumDriver.get().manage().window().getSize();
	}

	// endregion

	//region Wait Methods
	
	/**
	 * Wait for element visibility.
	 *
	 * @author carlos.cadena
	 * @param elements the elements
	 */
	public static void waitForAllElementsVisibility(List<MobileElement> elements) {
		waitForAllElementsVisibility(elements, pageTimeOut);
	}
	
	/**
	 * Wait for element visibility.
	 *
	 * @author carlos.cadena
	 * @param elements the elements
	 * @param timeOut the time out
	 */
	public static void waitForAllElementsVisibility(List<? extends WebElement> elements, int timeOut) {
		Utils.waitForAllElementsVisibility(appiumDriver.get(), elements, timeOut);
	}

	/**
	 * Wait for element visibility.
	 *
	 * @author carlos.cadena
	 * @param locator the locator
	 */
	public static void waitForElementVisibility(By locator) {
		waitForElementVisibility(locator, pageTimeOut);
	}

	/**
	 * Wait for element visibility.
	 *
	 * @author carlos.cadena
	 * @param locator the locator
	 * @param timeOut the time out
	 */
	public static void waitForElementVisibility(By locator, int timeOut) {
		Utils.waitForElementVisibility(appiumDriver.get(), locator);
		if (isAndroidExecution()) {
			Utils.waitForElementVisibility(appiumDriver.get(), locator, timeOut);
		} else {
			Utils.waitForElementAttributeToBePresent(appiumDriver.get(), locator, "visible", "true", timeOut);
		}
	}

	/**
	 * Wait for element visibility.
	 *
	 * @author carlos.cadena
	 * @param element the element
	 */
	public static void waitForElementVisibility(MobileElement element) {
		waitForElementVisibility(element, pageTimeOut);
	}

	/**
	 * Wait for element visibility.
	 *
	 * @author carlos.cadena
	 * @param element the element
	 * @param timeOut the time out
	 */
	public static void waitForElementVisibility(MobileElement element, int timeOut) {
		if (isAndroidExecution()) {
			Utils.waitForElementVisibility(appiumDriver.get(), element, timeOut);
		} else {
			Utils.waitForElementAttributeToBePresent(appiumDriver.get(), element, "visible", "true", timeOut);
		}
	}

	/**
	 * Wait for element visibility for an element inside a container.
	 *
	 * @author carlos.cadena
	 * @param element the element
	 * @param locator the locator
	 * @param timeOut the time out
	 */
	public static void waitForElementVisibility(MobileElement element, By locator, int timeOut) {
		if (isAndroidExecution()) {
			Utils.waitForElementVisibility(element, locator, timeOut);
		} else {
			Utils.waitForElementAttributeToBePresent(appiumDriver.get(), element, locator, "visible", "true", timeOut);
		}
	}

	/**
	 * Wait for element presence for an element inside a container.
	 *
	 * @author carlos.cadena
	 * @param element the element
	 * @param locator the locator
	 * @param timeOut the time out
	 */
	public static void waitForElementPresence(MobileElement element, By locator, int timeOut) {
		Utils.waitForElementPresence(element, locator, timeOut);
	}

	/**
	 * Wait for element visibility by index.
	 *
	 * @author carlos.cadena
	 * @param elements the elements
	 * @param index the index
	 */
	public static void waitForElementVisibility(List<MobileElement> elements, int index) {
		waitForElementVisibility(elements, index, pageTimeOut);
	}

	/**
	 * Wait for element visibility by index.
	 *
	 * @author carlos.cadena
	 * @param elements the elements
	 * @param index the index
	 * @param timeOut the time out
	 */
	public static void waitForElementVisibility(List<MobileElement> elements, int index, int timeOut) {
		if (isAndroidExecution()) {
			Utils.waitForElementVisibilityByIndex(appiumDriver.get(), elements, index, timeOut);
		} else {
			Utils.waitForElementAttributeToBePresentByIndex(appiumDriver.get(), elements, index, "visible", "true", timeOut);
		}
	}

	/**
	 * Wait for element presence.
	 *
	 * @author carlos.cadena
	 * @param locator the locator
	 */
	public static void waitForElementPresence(By locator) {
		Utils.waitForElementPresence(appiumDriver.get(), locator, pageTimeOut);
	}

	/**
	 * Wait for element presence.
	 *
	 * @author carlos.cadena
	 * @param locator the locator
	 * @param timeOut the time out
	 */
	public static void waitForElementPresence(By locator, int timeOut) {
		Utils.waitForElementPresence(appiumDriver.get(), locator);
	}

	/**
	 * Fluent wait method to check the ready state of the app meaning is in foreground.
	 *
	 * @author carlos.cadena
	 * @param timeOut the time out
	 * @return true if element is present or false otherwise
	 */
	public static boolean isApplicationReady(int timeOut) {
		Map<String, Object> mapSession = appiumDriver.get().getSessionDetails();
		String platformAndVersion = mapSession.get("platformName") + " " + mapSession.get("platformVersion").toString();
		if (!platformAndVersion.equalsIgnoreCase("Android 10")) {
			try {
				return new FluentWait<WebDriver>(appiumDriver.get()).withTimeout(Duration.ofSeconds(timeOut))
						.ignoring(RuntimeException.class).ignoring(IOException.class)
						.until(new Function<WebDriver, Boolean>() {
							public Boolean apply(WebDriver arg) {
								ApplicationState state = AppiumDriverFacade.appiumDriver.get()
										.queryAppState(FrameworkProperties.getPackage());
								Log.logger.debug("state of the app is > '" + state.toString() + "'");
								if (state != ApplicationState.RUNNING_IN_FOREGROUND) {
									return false;
								}
								return true;
							}
						});
			} catch (TimeoutException e) {
				return false;
			}
		}
		return true;
	}

	//endregion

	//region Visibility Methods

	/**
	 * Checks if a mobile element is visible by locator inside a container.
	 *
	 * @author carlos.cadena
	 * @param container the container
	 * @param locator   the locator
	 * @return boolean
	 */
	public static boolean isElementVisible(MobileElement container, By locator) {
		return isElementVisible(container, locator, pageTimeOut);
	}

	/**
	 * Checks if a mobile element is visible by locator inside a container.
	 *
	 * @author carlos.cadena
	 * @param element the element
	 * @param locator   the locator
	 * @param timeOut   the time out
	 * @return boolean
	 */
	public static boolean isElementVisible(MobileElement element, By locator, int timeOut) {
		return isAndroidExecution() ? Utils.isElementVisible(element, locator, timeOut)
				: Utils.isAttributePresentOnElement(element, locator, "visible", "true", false, timeOut);
	}

	/**
	 * Checks if a mobile element is NOT visible by locator inside a container.
	 *
	 * @author carlos.cadena
	 * @param container the element container
	 * @param locator   the locator
	 * @param timeOut   the time out
	 * @return boolean
	 */
	public static boolean isElementNotVisible(MobileElement container, By locator, int timeOut) {
		return Utils.isElementNotVisible(container, locator, timeOut);
	}

	/**
	 * Checks if a mobile element is visible.
	 *
	 * @author carlos.cadena
	 * @param element the element
	 * @param timeOut the time out
	 * @return boolean
	 */
	public static boolean isElementVisible(MobileElement element, int timeOut) {
		return isAndroidExecution() ? Utils.isElementVisible(appiumDriver.get(), element, timeOut)
				: Utils.isAttributePresentOnElement(appiumDriver.get(), element, "visible", "true", false, timeOut);

	}
	
	/**
	 * Checks if a mobile element is NOT visible.
	 *
	 * @author carlos.cadena
	 * @param element the element
	 * @return boolean
	 */
	public static boolean isElementNotVisible(MobileElement element) {
		return isElementNotVisible(element, pageTimeOut);
	}

	/**
	 * Checks if a mobile element is NOT visible.
	 *
	 * @author carlos.cadena
	 * @param element the element
	 * @param timeOut the time out
	 * @return boolean
	 */
	public static boolean isElementNotVisible(MobileElement element, int timeOut) {
		return isAndroidExecution() ? Utils.isElementNotVisible(appiumDriver.get(), element, timeOut)
				: !Utils.isAttributePresentOnElement(appiumDriver.get(), element, "visible", "true", false, timeOut);
	}

	/**
	 * Checks if a mobile element is visible by locator.
	 *
	 * @author carlos.cadena
	 * @param locator the locator
	 * @param timeOut the time out
	 * @return boolean
	 */
	public static boolean isElementVisible(By locator, int timeOut) {
		return isAndroidExecution() ? Utils.isElementVisible(appiumDriver.get(), locator, timeOut)
				: Utils.isAttributePresentOnElement(appiumDriver.get(), locator, "visible", "true", false, timeOut);
	}

	/**
	 * Checks if a mobile element is NOT visible by locator.
	 *
	 * @author carlos.cadena
	 * @param locator the locator
	 * @param timeOut the time out
	 * @return boolean
	 */
	public static boolean isElementNotVisible(By locator, int timeOut) {
		return isAndroidExecution() ? Utils.isElementNotVisible(appiumDriver.get(), locator, timeOut)
				: !Utils.isAttributePresentOnElement(appiumDriver.get(), locator, "visible", "true", false, timeOut);
	}

	/**
	 * Checks if a mobile element is visible by locator.
	 *
	 * @author carlos.cadena
	 * @param locator the locator
	 * @return true, if is element visible
	 */
	public static boolean isElementVisible(By locator) {
		return isElementVisible(locator, pageTimeOut);
	}

	/**
	 * Checks if a mobile element is visible.
	 *
	 * @author carlos.cadena
	 * @param element the element
	 * @return boolean
	 */
	public static boolean isElementVisible(MobileElement element) {
		try {
			return isAndroidExecution() ? element.isDisplayed() : isElementVisible(element, pageTimeOut);
		} catch (StaleElementReferenceException | NoSuchElementException  | ElementNotVisibleException  e) {
			return isElementVisible(element, pageTimeOut);
		}
	}

	/**
	 * Checks if a mobile element from a List is visible by index.
	 *
	 * @author carlos.cadena
	 * @param elements the elements
	 * @param index    the index
	 * @param timeOut  the time out
	 * @return boolean
	 */
	public static boolean isElementVisible(List<MobileElement> elements, int index, int timeOut) {
		TestUtils.assertListSize(elements, index);
		return isAndroidExecution() ? Utils.isElementVisible(appiumDriver.get(), elements, index, timeOut) :
				Utils.isAttributePresentOnElementByIndex(appiumDriver.get(), elements, index, "visible", "true", false, timeOut);
	}
	

	/**
	 * Are elements visible.
	 *
	 * @param elements the elements
	 * @return true, if successful
	 */
	public static boolean areElementsVisible(List<MobileElement> elements) {
		return areElementsVisible(elements, pageTimeOut);
	}

	/**
	 * Checks if all mobile elements from a List are visible.
	 *
	 * @param elements the elements
	 * @param timeOut the time out
	 * @return true, if successful
	 */
	public static boolean areElementsVisible(List<MobileElement> elements, int timeOut) {
		return isAndroidExecution() ? Utils.areElementsVisible(appiumDriver.get(), elements, timeOut) :
			Utils.isAttributePresentOnAllElements(appiumDriver.get(), elements, "visible", "true", false, timeOut);
	}

	/**
	 * Checks if a mobile element from a List is visible by index.
	 *
	 * @author carlos.cadena
	 * @param elements the elements
	 * @param index    the index
	 * @return boolean
	 */
	public static boolean isElementVisible(List<MobileElement> elements, int index) {
		if(elements == null || elements.isEmpty()) {
			Log.logger.debug("List of elements is null or empty");
			return false;
		}
		TestUtils.assertListSize(elements, index);
		return isElementVisible(elements, index, pageTimeOut);
	}

	/**
	 * Check if a mobile element is visible by text search criteria.
	 *
	 * @author carlos.cadena
	 * @param elements the elements
	 * @param text     the text
	 * @return boolean
	 */
	public static boolean isElementVisible(List<MobileElement> elements, String text) {
		if(elements == null || elements.isEmpty()) {
			Log.logger.debug("List of elements is empty is null or empty");
			return false;
		}
		MobileElement element = getElementByText(elements, text);
		return isElementVisible(element);
	}

	//endregion

	//region Find Methods

	/**
	 * Finds a mobile element first evaluating visibility.
	 *
	 * @author carlos.cadena
	 * @param locator the locator
	 * @return A {@link MobileElement}
	 */
	public static MobileElement findVisibleElement(By locator) {
		try {
			return appiumDriver.get().findElement(locator);
		}
		catch(StaleElementReferenceException | NoSuchElementException | ElementNotVisibleException e) {
			return findElement(locator, pageTimeOut, true);
		}
	}
	
	
	/**
	 * Find first visible element specifying timeout.
	 *
	 * @author carlos.cadena
	 * @param locators the locators
	 * @param timeout the timeout
	 * @return the web element
	 */
	public static MobileElement findFirstVisibleElement(List<By> locators, int timeout) {
		Optional<By> result = locators.stream().filter(locator -> AppiumDriverFacade.isElementVisible(locator, timeout))
				.findFirst();
		if (result.isPresent()) {
			Log.logger.debug("visible locator found -> " + result.get().toString());
			return AppiumDriverFacade.findElement(result.get());
		} else {
			throw new NoSuchElementException("No visible element was found with the locators provided, check..");
		}
		}
	
	/**
	 * Find first visible element specifying timeout.
	 *
	 * @author carlos.cadena
	 * @param container the container
	 * @param locators the locators
	 * @param timeout the timeout
	 * @return the web element
	 */
	public static MobileElement findFirstVisibleElement(MobileElement container, List<By> locators, int timeout) {
		Optional<By> result = locators.stream().filter(locator -> AppiumDriverFacade.isElementVisible(container, locator, timeout))
				.findFirst();
		if (result.isPresent()) {
			Log.logger.debug("visible locator found -> " + result.get().toString());
			return AppiumDriverFacade.findElement(container, result.get(), true);
		} else {
			throw new NoSuchElementException("No visible element was found inside the container with the locators provided, check..");
		}
	}

	/**
	 * Find first visible element.
	 *
	 * @author carlos.cadena
	 * @param locators the locators
	 * @return the web element
	 */
	public static MobileElement findFirstVisibleElement(List<By> locators) {
		return findFirstVisibleElement(locators,pageTimeOut);
	}
	
	/**
	 * Find first visible element inside a container.
	 *
	 * @author carlos.cadena
	 * @param container the container
	 * @param locators the locators
	 * @return the web element
	 */
	public static MobileElement findFirstVisibleElement(MobileElement container, List<By> locators) {
		return findFirstVisibleElement(container, locators,pageTimeOut);
	}

	/**
	 * Find first visible element.
	 *
	 * @author carlos.cadena
	 * @param locators the locators
	 * @return the web element
	 */
	public static MobileElement findFirstVisibleElement(By ... locators) {
		return findFirstVisibleElement(Arrays.asList(locators));
	}
	
	/**
	 * Find first visible element.
	 *
	 * @author carlos.cadena
	 * @param timeout the timeout
	 * @param locators the locators
	 * @return the web element
	 */
	public static MobileElement findFirstVisibleElement(int timeout, By ... locators) {
		return findFirstVisibleElement(Arrays.asList(locators), timeout);
	}
	
	/**
	 * Find first visible element inside a container.
	 *
	 * @author carlos.cadena
	 * @param container the container
	 * @param locators the locators
	 * @return the web element
	 */
	public static MobileElement findFirstVisibleElement(MobileElement container, By ... locators) {
		return findFirstVisibleElement(container, pageTimeOut, locators);
	}
	
	/**
	 * Find first visible element inside a container.
	 *
	 * @author carlos.cadena
	 * @param container the container
	 * @param timeout the timeout
	 * @param locators the locators
	 * @return the web element
	 */
	public static MobileElement findFirstVisibleElement(MobileElement container, int timeout, By ... locators) {
		return findFirstVisibleElement(container, Arrays.asList(locators), timeout);
	}

	/**
	 * Finds a mobile element if is present.
	 *
	 * @author carlos.cadena
	 * @param locator the locator
	 * @return {@link MobileElement} a Mobile Element
	 */
	public static MobileElement findElement(By locator) {
		try {
			return appiumDriver.get().findElement(locator);
		}
		catch(StaleElementReferenceException | NoSuchElementException e) {
			return findElement(locator, pageTimeOut, false);
		}
	}

	/**
	 * Finds mobile elements (since at least one is present that one will be
	 * returned ).
	 *
	 * @author carlos.cadena
	 * @param locator the locator
	 * @return A {@link List} of {@link MobileElement}
	 */
	public static List<MobileElement> findElements(By locator) {
		List<MobileElement>  list = appiumDriver.get().findElements(locator);
		if (!list.isEmpty()){
			return list;
		}else{
			return findElements(locator, pageTimeOut, false);
		}
	}
	
	/**
	 * Finds mobile elements with/without visibility
	 *
	 * @author carlos.cadena
	 * @param locator the locator
	 * @param visibility
	 * 
	 * @return A {@link List} of {@link MobileElement}
	 */
	public static List<MobileElement> findElements(By locator, boolean visibility) {
		List<MobileElement>  list = appiumDriver.get().findElements(locator);
		if (!list.isEmpty()){
			return !visibility ? list : list.stream().filter(x -> AppiumDriverFacade.isElementVisible(x,2)).collect(Collectors.toList());
		}else{
			return findElements(locator, pageTimeOut, visibility);
		}
	}

	/**
	 * Finds Element for Mobile.
	 *
	 * @author carlos.cadena
	 * @param locator    the locator
	 * @param timeOut    the time out
	 * @param visibility the visibility
	 * @return {@link MobileElement} a Mobile Element
	 */
	private static MobileElement findElement(By locator, int timeOut, boolean visibility) {
		return (MobileElement) Utils.findElement(appiumDriver.get(), locator, timeOut, visibility);
	}

	/**
	 * Finds Elements for Mobile.
	 *
	 * @author carlos.cadena
	 * @param locator    the locator
	 * @param timeOut    the time out
	 * @param visibility the visibility
	 * @return A {@link List} of {@link MobileElement}
	 */
	private static List<MobileElement> findElements(By locator, int timeOut, boolean visibility) {
		List<MobileElement> mobileElements = new ArrayList<MobileElement>();
		List<WebElement> elements = Utils.findElements(appiumDriver.get(), locator, pageTimeOut, visibility);
		elements.forEach(element -> mobileElements.add(((MobileElement) element)));
		if (!mobileElements.isEmpty()) {
			return mobileElements;
		}else{
			throw new NoSuchElementException(
					String.format("The element was not present on the page"));
		}
	}

	/**
	 * Finds mobile element inside a container.
	 *
	 * @author carlos.cadena
	 * @param container  the container
	 * @param locator    the locator
	 * @param visibility - if true visibility for locator will be evaluated, if
	 *                   false not
	 * @return {@link MobileElement} a Mobile Element
	 */
	public static MobileElement findElement(MobileElement container, By locator, boolean visibility) {
		return findElement(container, locator, pageTimeOut, visibility);

	}

	/**
	 * Finds mobile element inside a container.
	 *
	 * @author carlos.cadena
	 * @param container  the container
	 * @param locator    the locator
	 * @param timeOut    the time out
	 * @param visibility - if true visibility for locator will be evaluated, if
	 *                   false not
	 * @return {@link MobileElement} a Mobile Element
	 */
	public static MobileElement findElement(MobileElement container, By locator, int timeOut, boolean visibility) {
		if (visibility) {
			try {
				return container.findElement(locator);
			} catch (StaleElementReferenceException | NoSuchElementException | ElementNotVisibleException e) {
				if (isAndroidExecution()) {
					return Utils.findElement(container, locator, timeOut, true);
				} else {
					Utils.waitForElementAttributeToBePresent(appiumDriver.get(), container, locator, "visible", "true", timeOut);
					return container.findElement(locator);
				}
			}
		} else {
			try {
				return container.findElement(locator);
			} catch (StaleElementReferenceException | NoSuchElementException e) {
				if (isAndroidExecution()) {
					return Utils.findElement(container, locator, timeOut, false);
				} else {
					if(Utils.isElementPresent(appiumDriver.get(), locator, timeOut)){
						return container.findElement(locator);
					}
					else {
						throw new TimeoutException("Element is not present inside the container after waiting for '" + timeOut + "' seconds");
					}
				}
			}
		}
	}

	/**
	 * Finds mobile element inside a container.
	 *
	 * @author carlos.cadena
	 * @param container  the container
	 * @param locator    the locator
	 * @param visibility - if true visibility for locator will be evaluated, if
	 *                   false not
	 * @return A {@link List} of {@link MobileElement}
	 */
	public static List<MobileElement> findElements(MobileElement container, By locator, boolean visibility) {
		if (visibility) {
			try {
				return container.findElements(locator);
			} catch (StaleElementReferenceException | NoSuchElementException | ElementNotVisibleException e) {
				return findElements(container, locator, pageTimeOut, true);
			}
		} else {
			try {
				return container.findElements(locator);
			} catch (StaleElementReferenceException | NoSuchElementException e) {
				return findElements(container, locator, pageTimeOut, false);
			}
		}
	}

	/**
	 * Find Elements on inside element container.
	 *
	 * @author carlos.cadena
	 * @param container  the container
	 * @param locator    the locator
	 * @param timeOut    the time out
	 * @param visibility - if true visibility for locator will be evaluated, if
	 *                   false not
	 * @return A {@link List} of {@link MobileElement}
	 */
	private static List<MobileElement> findElements(MobileElement container, By locator, int timeOut,
													boolean visibility) {
		List<MobileElement> allMobile = new ArrayList<MobileElement>();
		List<WebElement> elements = Utils.findElements(container, locator, timeOut, visibility);
		for (WebElement e : elements) {
			allMobile.add((MobileElement) e);
		}
		return allMobile;
	}

	//endregion

	// region Get Methods

	/**
	 * Gets a mobile element from a list by text search criteria.
	 *
	 * @author carlos.cadena
	 * @param elements    the elements
	 * @param elementText the element text
	 * @param timeOut     the time out
	 * @return {@link MobileElement} a Mobile Element
	 */
	public static MobileElement getElementByText(List<MobileElement> elements, String elementText, int timeOut) {
		for (MobileElement element : elements) {
			if (isElementVisible(element, timeOut)
					&& element.getText().contains(elementText)) {
				return element;
			}
		}
		throw new IllegalArgumentException(
				String.format("There couldn't be found any element with the following text: %s", elementText));
	}

	/**
	 * Gets the element by text.
	 *
	 * @author carlos.cadena
	 * @param elements    the elements
	 * @param elementText the element text
	 * @return {@link MobileElement} a Mobile Element
	 */
	public static MobileElement getElementByText(List<MobileElement> elements, String elementText) {
		return getElementByText(elements, elementText, pageTimeOut);
	}

	/**
	 * Gets the element index.
	 *
	 * @author carlos.cadena
	 * @param elements    the elements
	 * @param elementText the element text
	 * @param timeOut     the time out
	 * @return the element index
	 */
	public static int getElementIndex(List<MobileElement> elements, String elementText, int timeOut) {
		Utils.waitForAllElementsVisibility(appiumDriver.get(), elements, timeOut);
		for (MobileElement element : elements) {
			if (isElementVisible(element, timeOut)
					&& element.getText().contains(elementText)) {
				return elements.indexOf(element);
			}
		}
		throw new IllegalArgumentException(
				String.format("There couldn't be found any element with the following text: %s", elementText));
	}

	/**
	 * Gets the element index.
	 *
	 * @author carlos.cadena
	 * @param elements    the elements
	 * @param elementText the element text
	 * @return the element index
	 */
	public static int getElementIndex(List<MobileElement> elements, String elementText) {
		return getElementIndex(elements, elementText, pageTimeOut);
	}

	/**
	 * This method is used to return the text of the element based on its locator.
	 *
	 * @author carlos.cadena
	 * @param element the element
	 * @param timeOut the time out
	 * @return the text
	 */
	public static String getText(MobileElement element, int timeOut) {
		try {
			return element.getText();
		} catch (StaleElementReferenceException | NoSuchElementException | ElementNotVisibleException e) {
			Utils.waitForElementVisibility(appiumDriver.get(), element, timeOut);
			return element.getText();
		}
	}

	/**
	 * Gets the element inner text.
	 *
	 * @author carlos.cadena
	 * @param element the element
	 * @return the text
	 */
	public static String getText(MobileElement element) {
		return getText(element, pageTimeOut);
	}

	/**
	 * This method is used to return the text of the element by index.
	 *
	 * @author carlos.cadena
	 * @param elements the elements
	 * @param index    the index
	 * @param timeOut  the time out
	 * @return the text
	 */
	public static String getText(List<MobileElement> elements, int index, int timeOut) {
		TestUtils.assertListSize(elements, index);
		waitForElementVisibility(elements, index, timeOut);
		return elements.get(index).getText();
	}

	/**
	 * Gets the text from a List of Elements based on index.
	 *
	 * @author carlos.cadena
	 * @param elements the elements
	 * @param index    the index
	 * @return the text
	 */
	public static String getText(List<MobileElement> elements, int index) {
		return getText(elements, index, pageTimeOut);
	}

	/**
	 * Gets the text of an element inside a container.
	 *
	 * @author carlos.cadena
	 * @param container the container
	 * @param locator the element locator
	 * @return the text
	 */
	public static String getText(MobileElement container, By locator) {
		return AppiumDriverFacade.findElement(container, locator, true).getText().trim();
	}
	
	/**
	 * This method is used to return the texts of the elements.
	 *
	 * @author carlos.cadena
	 * @param elements the elements
	 * @param timeOut  the time out
	 * @return A {@link List} of String
	 */
	public static List<String> getElementsText(List<MobileElement> elements, int timeOut) {
		Utils.waitForAllElementsVisibility(appiumDriver.get(), elements);
		List<String> Texts = new ArrayList<>();
		for (MobileElement element : elements) {
			Texts.add(element.getText());
		}
		return Texts;
	}

	/**
	 * Gets the elements text.
	 *
	 * @author carlos.cadena
	 * @param elements the elements
	 * @return A {@link List} of String
	 */
	public static List<String> getElementsText(List<MobileElement> elements) {
		return getElementsText(elements, pageTimeOut);
	}

	/**
	 * This method is used to returns the axis value of a mobile element on the
	 * page.
	 *
	 * @author carlos.cadena
	 * @param element the element
	 * @param axis    the axis (Options must be 'X' or 'Y' with lower or upper case)
	 * @return the element axis
	 */
	public static int getElementByAxis(MobileElement element, String axis) {
		switch (axis.toUpperCase()) {
		case "Y":
			return element.getLocation().y;
		case "X":
			return element.getLocation().x;
		default:
			throw new IllegalArgumentException(String.format("The axis value %s is not supported", axis));
		}
	}

	/**
	 * Gets the elements count.
	 *
	 * @author carlos.cadena
	 * @param elements the elements
	 * @return the elements count
	 */
	public static int getElementsCount(List<MobileElement> elements) {
		return elements.size();
	}

	/**
	 * Gets the elements count that corresponds to the text search criteria.
	 *
	 * @author carlos.cadena
	 * @param elements    the elements
	 * @param elementText the element text
	 * @param timeOut     the time out
	 * @return the elements count
	 */
	public static int getElementsCount(List<MobileElement> elements, String elementText, int timeOut) {
		Utils.waitForAllElementsVisibility(appiumDriver.get(), elements);
		List<MobileElement> selectedElements = new ArrayList<>();
		for (MobileElement e : elements) {
			if (e.getText().equals(elementText))
				selectedElements.add(e);
		}
		return selectedElements.size();
	}

	/**
	 * Gets the elements count that corresponds to the text search criteria.
	 *
	 * @author carlos.cadena
	 * @param elements    the elements
	 * @param elementText the element text
	 * @return the elements count
	 */
	public static int getElementsCount(List<MobileElement> elements, String elementText) {
		return getElementsCount(elements, elementText, pageTimeOut);
	}

	/**
	 * Gets the element size.
	 *
	 * @author carlos.cadena
	 * @param element the element
	 * @return the {@link Dimension} element size
	 */
	public static Dimension getElementSize(MobileElement element) {
		return element.getSize();
	}

	/**
	 * Gets the element center point.
	 *
	 * @author carlos.cadena
	 * @param element the element
	 * @return the {@link Point} indicating element center
	 */
	public static Point getElementCenterPoint(MobileElement element) {
		return element.getCenter();
	}
	
	/**
	 * Gets the element center point.
	 *
	 * @author carlos.cadena
	 * @param element the element
	 * @return the {@link Point} indicating element center
	 */
	public static Point getElementLocation(MobileElement element) {
		return element.getLocation();
	}

	// endregion

	// region Actions Methods

	/**
	 * Writes (send keys) on a mobile element.
	 *
	 * @author carlos.cadena
	 * @param element the element
	 * @param text    the text
	 * @param timeOut the time out
	 */
	public static void write(MobileElement element, String text, int timeOut) {
		try {
			element.sendKeys(text);
		} catch (StaleElementReferenceException | NoSuchElementException | InvalidElementStateException e) {
			Utils.waitForElementVisibility(appiumDriver.get(), element, timeOut);
			element.sendKeys(text);
		}
	}

	/**
	 * Writes (send keys) on a mobile element.
	 *
	 * @author carlos.cadena
	 * @param element the element
	 * @param text    the text
	 */
	public static void write(MobileElement element, String text) {
		write(element, text, pageTimeOut);
	}

	/**
	 * Writes (send keys) on a mobile element by index.
	 *
	 * @author carlos.cadena
	 * @param elements the elements
	 * @param index    the index
	 * @param text     the text
	 * @param timeout  the timeout
	 */
	public static void write(List<MobileElement> elements, int index, String text, int timeout) {
		TestUtils.assertListSize(elements, index);
		write(elements.get(index), text, timeout);
	}

	/**
	 * Writes (send keys) on a mobile element.
	 *
	 * @author carlos.cadena
	 * @param elements the elements
	 * @param index    the index
	 * @param text     the text
	 */
	public static void write(List<MobileElement> elements, int index, String text) {
		write(elements, index, text, pageTimeOut);
	}

	/**
	 * Cleans the text of a mobile element.
	 *
	 * @author carlos.cadena
	 * @param element the element
	 * @param timeOut the time out
	 */
	public static void clean(MobileElement element, int timeOut) {
		try {
			element.clear();
		} catch (StaleElementReferenceException | NoSuchElementException | ElementNotVisibleException e) {
			Utils.waitForElementVisibility(appiumDriver.get(), element, timeOut);
			element.clear();
		}
	}

	/**
	 * Cleans the text of a mobile element.
	 *
	 * @author carlos.cadena
	 * @param element the element
	 */
	public static void clean(MobileElement element) {
		clean(element, pageTimeOut);
	}

	/**
	 * Cleans the text of a mobile element from index.
	 *
	 * @author carlos.cadena
	 * @param elements the elements
	 * @param index    the index
	 */
	public static void clean(List<MobileElement> elements, int index) {
		TestUtils.assertListSize(elements, index);
		clean(elements.get(index));
	}

	/**
	 * Cleans the text of a mobile element and then write (send keys) on it.
	 *
	 * @author carlos.cadena
	 * @param element the element
	 * @param text    the text
	 */
	public static void cleanAndWrite(MobileElement element, String text) {
		clean(element);
		write(element, text);
	}

	/**
	 * Cleans the text on a mobile element and then write (send keys) on it.
	 *
	 * @author carlos.cadena
	 * @param elements      the elements
	 * @param index         the index
	 * @param text          the text
	 * @param secondsToWait the seconds to wait
	 */
	public static void cleanAndWrite(List<MobileElement> elements, int index, String text, int secondsToWait) {
		TestUtils.assertListSize(elements, index);
		clean(elements.get(index), secondsToWait);
		write(elements.get(index), text);
	}

	/**
	 * Taps on a mobile element.
	 *
	 * @author carlos.cadena
	 * @param element the element
	 * @param timeOut the time out
	 */
	public static void tap(MobileElement element, int timeOut) {
		try {
			element.click();
		} catch (StaleElementReferenceException | NoSuchElementException | ElementNotVisibleException e) {
			Utils.waitForElementVisibility(appiumDriver.get(), element, timeOut);
			element.click();
		}
	}

	/**
	 * Taps on a mobile element.
	 *
	 * @author carlos.cadena
	 * @param element the element
	 */
	public static void tap(MobileElement element) {
		tap(element, pageTimeOut);
	}
	
	/**
	 * Double Taps on a mobile element.
	 *
	 * @author carlos.cadena
	 * @param element the element
	 * @param millisecondsBetweenTaps the milliseconds between taps
	 * @param timeOut the time out
	 */
	public static void doubleTap(MobileElement element, int millisecondsBetweenTaps, int timeOut) {
		TouchAction action = new TouchAction<>(appiumDriver.get());
		TapOptions options = TapOptions.tapOptions().withPosition(PointOption.point(AppiumDriverFacade.getElementCenterPoint(element)));
		action = new TouchAction<>(appiumDriver.get()).waitAction(WaitOptions.waitOptions(Duration.ofMillis(millisecondsBetweenTaps))).tap(options).tap(options);

		try {
			action.perform();
		} catch (StaleElementReferenceException | NoSuchElementException | ElementNotVisibleException e) {
			Utils.waitForElementVisibility(appiumDriver.get(), element, timeOut);
			action.perform();
		}
	}
	
	/**
	 * Double Taps on a mobile element.
	 *
	 * @author carlos.cadena
	 * @param element the element
	 * @param millisecondsBetweenTaps the milliseconds between taps
	 */
	public static void doubleTap(MobileElement element, int millisecondsBetweenTaps) {
		doubleTap(element, millisecondsBetweenTaps, pageTimeOut);
	}
	
	/**
	 * Taps on center of mobile element.
	 *
	 * @author carlos.cadena
	 * @param element the element
	 */
	public static void tapOnCenterOfElement(MobileElement element) {
		Point center = element.getCenter();
		new TouchAction<>(appiumDriver.get()).tap(PointOption.point(center.getX(), center.getY()));
	}

	/**
	 * Taps on corner of mobile element.
	 *
	 * @author carlos.cadena
	 * @param element the element
	 */
	public static void tapOnCornerOfElement(MobileElement element) {
		Point corner = element.getLocation();
		new TouchAction<>(appiumDriver.get()).tap(PointOption.point(corner.getX(), corner.getY()));
	}

	/**
	 * Taps on coordinates.
	 *
	 * @author carlos.cadena
	 * @param x the x
	 * @param y the y
	 */
	public static void tapOnCoordinates(int x, int y) {
		new TouchAction<>(appiumDriver.get()).tap(PointOption.point(x, y)).perform();
	}

	/**
	 * Taps on a mobile element by index.
	 *
	 * @author carlos.cadena
	 * @param elements the elements
	 * @param index    the index
	 * @param timeOut  the time out
	 */
	public static void tap(List<MobileElement> elements, int index, int timeOut) {
		TestUtils.assertListSize(elements, index);
		Utils.waitForAllElementsVisibility(appiumDriver.get(), elements);
		tap(elements.get(index));
	}

	/**
	 * Taps on a mobile element by index.
	 *
	 * @author carlos.cadena
	 * @param elements the elements
	 * @param index    the index
	 */
	public static void tap(List<MobileElement> elements, int index) {
		tap(elements, index, pageTimeOut);
	}

	/**
	 * Taps a mobile element from a list by text search criteria.
	 *
	 * @author carlos.cadena
	 * @param elements    the elements
	 * @param elementText the element text
	 */
	public static void tap(List<MobileElement> elements, String elementText) {
		MobileElement element = getElementByText(elements, elementText, 5);
		tap(element);
	}

	/**
	 * Taps on a mobile element inside a container.
	 *
	 * @author carlos.cadena
	 * @param container the container
	 * @param locator the element locator
	 */
	public static void tap(MobileElement container, By locator) {
		AppiumDriverFacade.findElement(container, locator, pageTimeOut, true).click();
	}

	/**
	 * Taps on a mobile element inside a container with certain text.
	 *
	 * @author carlos.cadena
	 * @param container the container
	 * @param locator the element locator
	 * @param text the text
	 */
	public static void tap(MobileElement container, By locator, String text) {
		List<MobileElement> elements = AppiumDriverFacade.findElements(container, locator, pageTimeOut , true);
		AppiumDriverFacade.tap(elements, text);
	}

	// endregion

	// region Android Actions Methods

	/**
	 * Tap Android native Back button.
	 *
	 * @author carlos.cadena
	 *
	 */
	public static void tapAndroidBackButton() {
		WebDriverUtils.tapAndroidBackButton(appiumDriver.get());
	}

	/**
	 * Tap Android native Back button.
	 *
	 * @author carlos.cadena
	 *
	 */
	public static void tapAndroidHomeButton() {
		WebDriverUtils.tapAndroidHomeButton(appiumDriver.get());
	}

	/**
	 * Tap Android native Enter button.
	 *
	 * @author carlos.cadena
	 *
	 */
	public static void tapAndroidEnterButton() {
		WebDriverUtils.tapAndroidEnterButton(appiumDriver.get());
	}

	// endregion

	// region Boolean Methods

	/**
	 * Checks if a mobile element is present.
	 *
	 * @author carlos.cadena
	 * @param locator the locator
	 * @return true, if is element present
	 */
	public static boolean isElementPresent(By locator) {
		return Utils.isElementPresent(appiumDriver.get(), locator, pageTimeOut);
	}
	
	/**
	 * Checks if a mobile element is present inside a container.
	 *
	 * @author carlos.cadena
	 * @param container the container
	 * @param locator the locator
	 * @return true, if is element present
	 */
	public static boolean isElementPresent(MobileElement container, By locator) {
		return Utils.isElementPresent(container, locator, pageTimeOut);
	}

	/**
	 * Checks if a mobile element is present inside a container with a custom timeout.
	 *
	 * @author carlos.cadena
	 * @param container the container
	 * @param locator the locator
	 * @param timeout the timeout
	 * @return true, if is element present
	 */
	public static boolean isElementPresent(MobileElement container, By locator, int timeout) {
		return Utils.isElementPresent(container, locator, timeout);
	}

	/**
	 * Checks if a mobile element is present with a custom timeout.
	 *
	 * @author carlos.cadena
	 * @param locator the locator
	 * @param timeout the timeout
	 * @return true, if is element present
	 */
	public static boolean isElementPresent(By locator, int timeout) {
		return Utils.isElementPresent(appiumDriver.get(), locator, timeout);
	}

	/**
	 * Checks if a mobile element is enabled.
	 *
	 * @author carlos.cadena
	 * @param element the element
	 * @param timeout the timeout
	 * @return true, if is element enabled
	 */
	public static boolean isElementEnabled(MobileElement element, int timeout) {
		return Utils.isElementEnabled(appiumDriver.get(), element, timeout);
	}
	
	/**
	 * Checks if a mobile element is element enabled with a custom timeout.
	 *
	 * @author carlos.cadena
	 * @param element the element
	 * @return true, if is element enabled
	 */
	public static boolean isElementEnabled(MobileElement element) {
		try {
			return element.isEnabled();
		} catch (StaleElementReferenceException | NoSuchElementException | ElementNotSelectableException | ElementNotVisibleException e) {
			return isElementEnabled(element, pageTimeOut);
		}
	}
	
	/**
	 * Checks if a mobile element from a List is enabled.
	 *
	 * @author carlos.cadena
	 * @param elements the elements
	 * @param index    the index
	 * @param timeout  the timeout
	 * @return boolean
	 */
	public static boolean isElementEnabled(List<MobileElement> elements, int index, int timeout) {
		return Utils.isElementEnabled(appiumDriver.get(), elements, index, timeout);
	}

	/**
	 * Checks if a mobile element from a List is enabled.
	 *
	 * @author carlos.cadena
	 * @param elements the elements
	 * @param index    the index
	 * @return boolean
	 */
	public static boolean isElementEnabled(List<MobileElement> elements, int index) {
		return isElementEnabled(elements, index, pageTimeOut);
	}

	/**
	 * Checks if a mobile element is enabled from a list by text search criteria.
	 *
	 * @author carlos.cadena
	 * @param elements    the elements
	 * @param elementText the element text
	 * @param timeout     the timeout
	 * @return boolean
	 */
	public static boolean isElementEnabledByText(List<MobileElement> elements, String elementText, int timeout) {
		MobileElement element = getElementByText(elements, elementText);
		return isElementEnabled(element, timeout);
	}

	/**
	 * Checks if a mobile element is enabled from a list by text search criteria.
	 *
	 * @author carlos.cadena
	 * @param elements    the elements
	 * @param elementText the element text
	 * @return boolean
	 */
	public static boolean isElementEnabledByText(List<MobileElement> elements, String elementText) {
		return isElementEnabledByText(elements, elementText, pageTimeOut);
	}

	/**
	 * Checks if a mobile element is enabled from a list by text search criteria.
	 *
	 * @author carlos.cadena
	 * @param elements    the elements
	 * @param elementText the element text
	 * @param timeout the timeout
	 * @return true, if is element enabled no wait
	 */
	public static boolean isElementEnabled(List<MobileElement> elements, String elementText, int timeout) {
		try {
			if(elements == null || elements.isEmpty()) {
				Log.logger.debug("List of elements is empty is null or empty");
				return false;
			}
			return getElementByText(elements, elementText, timeout).isEnabled();
		} catch (NoSuchElementException | IllegalArgumentException | StaleElementReferenceException
				| IllegalStateException e) {
			return false;
		}
	}

	/**
	 * Checks if a mobile element is visible without waiting timeout.
	 *
	 * @author carlos.cadena
	 * @param element the element
	 * @return boolean
	 */
	public static boolean isElementVisibleNoWait(MobileElement element) {
			return Utils.isElementVisible(appiumDriver.get(), element, 1);
	}

	/**
	 * Checks if a mobile element is visible from a list by index without waiting
	 * timeout.
	 *
	 * @param elements the elements
	 * @param index    the index
	 * @return true, if is element visible no wait
	 */
	public static boolean isElementVisibleNoWait(List<MobileElement> elements, int index) {
		try {
			return elements.get(index).isDisplayed();
		} catch (NoSuchElementException | IllegalArgumentException | StaleElementReferenceException
				| IllegalStateException | IndexOutOfBoundsException e) {
			return false;
		}
	}

	/**
	 * Checks if a mobile element is visible from a list by text search criteria.
	 * 
	 * @author carlos.cadena
	 * @param elements    the elements
	 * @param elementText the element text
	 * @param timeOut the timeout
	 * @return true, if is element visible no wait
	 */
	public static boolean isElementVisible(List<MobileElement> elements, String elementText, int timeOut) {
		try {
			if(elements == null || elements.isEmpty()) {
				Log.logger.debug("List of elements is empty is null or empty");
				return false;
			}
			return getElementByText(elements, elementText, timeOut).isDisplayed();
		} catch (NoSuchElementException | IllegalArgumentException | StaleElementReferenceException
				| IllegalStateException e) {
			return false;
		}
	}

	/**
	 * Takes screenshot.
	 *
	 * @author carlos.cadena
	 * @param screenshotTitle the screenshot title
	 * @param saveDirectory   the save directory
	 * @param extension       > provide a valid format e.g .png or .jpeg
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void takeScreenshot(String screenshotTitle, String saveDirectory, String extension) throws IOException {
		String pathString = saveDirectory + screenshotTitle + extension;
		takeScreenshot(pathString);
	}

	/**
	 * Takes screenshot using an specific absolute path.
	 *
	 * @author carlos.cadena
	 * @param screenshotPath the screenshot path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void takeScreenshot(String screenshotPath) throws IOException {
		WebDriver augmentedDriver = new Augmenter().augment(appiumDriver.get()); 
		File screenshot = ((TakesScreenshot)augmentedDriver).getScreenshotAs(OutputType.FILE);
		File picture = new File(screenshotPath);
		picture.setReadable(true);
		picture.setWritable(true);
		if (picture.exists()) {
			picture.delete();
		}
		if(screenshot.exists()){
			FileUtils.copyFile(screenshot, picture);
		}
	}

	// endregion

	//region Application Methods

	/**
	 * Fluent wait method to check app state.
	 *
	 * @author carlos.cadena
	 * @param expectedState the expected state
	 * @param timeOut the time out
	 * @return true, if successful
	 */
	public static boolean validateApplicationState(ApplicationState expectedState, int timeOut) {
		Map<String, Object> mapSession = appiumDriver.get().getSessionDetails();
		String platformAndVersion = mapSession.get("platformName") + " " + mapSession.get("platformVersion").toString();
		try {
			return new FluentWait<WebDriver>(appiumDriver.get()).withTimeout(Duration.ofSeconds(timeOut)).pollingEvery(Duration.ofSeconds(1))
					.ignoring(RuntimeException.class).ignoring(IOException.class)
					.until((Function<WebDriver, Boolean>) arg -> {
						ApplicationState state = AppiumDriverFacade.appiumDriver.get()
								.queryAppState(FrameworkProperties.getPackage());
						Log.logger.debug("state of the app is > '" + state.toString() + "'");
						if (state == expectedState || (expectedState == ApplicationState.RUNNING_IN_FOREGROUND && platformAndVersion.equals("Android 10"))) {
							return true;
						}
						return false;
					});
		} catch (TimeoutException e) {
			return false;
		}
	}

	/**
	 * Send the current application to the background.
	 *
	 * @author carlos.cadena
	 */
	public static void sendCurrentApplicationToTheBackground(){
		appiumDriver.get().runAppInBackground(Duration.ofSeconds(-1));
	}

	/**
	 * Activate applicacion.
	 *
	 * @author carlos.cadena
	 * @param appPackage the app package
	 */
	public static void activateApplication(String appPackage){
		AppiumDriverFacade.appiumDriver.get().activateApp(appPackage);
	}

	/**
	 * Terminate application.
	 *
	 * @author carlos.cadena
	 * @param appBundleID the app bundle ID
	 */
	public static void terminateApplicationIos(String appBundleID){
		Map<String, Object> params = new HashMap<>();
		params.put("bundleId", appBundleID);
		appiumDriver.get().executeScript("mobile: terminateApp", params);
	}
	
	/**
	 * Terminate caixa application.
	 *
	 * @author carlos.cadena
	 */
	public static void closeApplication(){
		AppiumDriverFacade.appiumDriver.get().closeApp();
	}

	//endregion

	// region Swipe Methods

	/**
	 * Swipe.
	 *
	 * @author carlos.cadena
	 * @param xStart   the x start
	 * @param yStart   the y start
	 * @param xEnd     the x end
	 * @param yEnd     the y end
	 * @param duration the duration
	 */
	public static void swipe(Integer xStart, Integer yStart, Integer xEnd, Integer yEnd, long duration) {
		new TouchAction<>(appiumDriver.get()).press(PointOption.point(xStart, yStart))
				.waitAction(WaitOptions.waitOptions(Duration.ofMillis(duration))).moveTo(PointOption.point(xEnd, yEnd))
				.release().perform();
	}

	/**
	 * Vertical swipe until a certain provided element is reached.
	 *
	 * @author carlos.cadena
	 * @param element   the element
	 * @param movement  the movement
	 * @param maxSwipes the max swipes
	 * @return true, if successful
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static boolean vSwipeToElement(MobileElement element, MovementsV movement, int maxSwipes)
			throws IllegalArgumentException {
		Tuple<Point,Point> startAndEnd = getStartAndEndPositionForVerticalSwipe(movement);
		for (int i = 0; i < maxSwipes; i++) {
			if (!AppiumDriverFacade.isElementVisibleNoWait(element))
				swipe(startAndEnd.v1().x, startAndEnd.v1().y, startAndEnd.v2().x / 2, startAndEnd.v2().y, 1000);
			else {			
				centerElementOnScreen(element);
				return true;
			}
		}
		return false;
	}

	/**
	 * Vertical swipe until a certain provided element is found by locator and reached.
	 *
	 * @author carlos.cadena
	 * @param element   the element
	 * @param movement  the movement
	 * @param maxSwipes the max swipes
	 * @return true, if successful
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static boolean vSwipeToElement(By element, MovementsV movement, int maxSwipes)
			throws IllegalArgumentException {
		Tuple<Point,Point> startAndEnd = getStartAndEndPositionForVerticalSwipe(movement);
		for (int i = 0; i < maxSwipes; i++) {
			if (!AppiumDriverFacade.isElementVisible(element,2))
				swipe(startAndEnd.v1().x, startAndEnd.v1().y, startAndEnd.v2().x / 2, startAndEnd.v2().y, 1000);
			else {			
				centerElementOnScreen(AppiumDriverFacade.findElement(element));
				return true;
			}
		}
		return false;
	}

	/**
	 * Vertical swipe to element inside a container.
	 *
	 * @author carlos.cadena
	 * @param container  the container
	 * @param locator the element locator
	 * @param movement  the movement
	 * @param maxSwipes the max swipes
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static void vSwipeToElement(MobileElement container, By locator, MovementsV movement, int maxSwipes)
			throws IllegalArgumentException {
		vSwipeToElement(container.findElement(locator), movement, maxSwipes);
	}
	
	/**
	 * Center element on screen.
	 *
	 * @author carlos.cadena
	 * @param element the element
	 */
	public static void centerElementOnScreen(MobileElement element) {
		Dimension deviceScreen = getScreenSize();
		Point center = AppiumDriverFacade.getElementCenterPoint(element);
		int deviceScreenY = deviceScreen.height <= 667 ? deviceScreen.height - 70 : deviceScreen.height - 10;
		int distanceToCenter = Math.abs(center.y - deviceScreenY / 2);
		if (distanceToCenter > 100) {
			int y = center.y;
			if(y > deviceScreenY) y = deviceScreenY;
			if(y < 0) y = 0;
			Log.logger.debug("Centro el elemento");
			swipe(deviceScreen.width / 2, y, deviceScreen.width / 2, deviceScreenY / 2, 1000);
		}
	}
	
	/**
	 * Center element on screen respect to X.
	 *
	 * @author carlos.cadena
	 * @param element the element
	 */
	public static void centerElementOnScreenRespectToX(MobileElement element) {
		Dimension deviceScreen = getScreenSize();
		Point center = AppiumDriverFacade.getElementCenterPoint(element);
		Point location = AppiumDriverFacade.getElementLocation(element);
		int centerDeviceX = deviceScreen.width / 2;
		int distanceToCenter = Math.abs(center.x - centerDeviceX);
		if (distanceToCenter > 100) {
			swipe(location.x, location.y, centerDeviceX/2, location.y, 1000);
		}
	}
	
	
	/**
	 * Gets the start and end position for vertical swipe.
	 * 
	 * @author carlos.cadena
	 * @param movement the movement
	 * @return the start and end position for horizontal swipe
	 */
	public static Tuple<Point,Point> getStartAndEndPositionForVerticalSwipe(MovementsV movement){
		int xStartAndEnd, yStart, yEnd;
		Dimension deviceScreen = getScreenSize();
		xStartAndEnd = deviceScreen.width / 2;
		if (movement == MovementsV.UP) {
			yStart = 300;
			yEnd = FrameworkProperties.getPlatformName().equalsIgnoreCase("Android") ? deviceScreen.height / 2 : deviceScreen.height - 100;
		} else {
			yStart = FrameworkProperties.getPlatformName().equalsIgnoreCase("Android") ? deviceScreen.height / 2 : deviceScreen.height - 100;
			yEnd = 300;
		}
		return new Tuple<Point,Point>(new Point(xStartAndEnd,yStart),new Point(xStartAndEnd,yEnd));
	}
	
	/**
	 * Gets the start and end position for horizontal swipe.
	 *
	 * @author carlos.cadena
	 * @param startElement the start element
	 * @param movement the movement
	 * @return the start and end position for horizontal swipe
	 */
	private static Tuple<Point,Point> getStartAndEndPositionForHorizontalSwipe(MobileElement startElement,  MovementsH movement){
		int xStart, yStartAndEnd, xEnd;
		Dimension deviceScreen = getScreenSize();
		Point elementLocator = AppiumDriverFacade.getElementLocation(startElement);
		Dimension elementSize = AppiumDriverFacade.getElementSize(startElement);
		yStartAndEnd = elementLocator.y;
		if (movement == MovementsH.RIGHT) {
			xStart = elementLocator.x + elementSize.width;
			xStart = ((xStart >= deviceScreen.width) ? deviceScreen.width - 60 : xStart);
			xEnd = deviceScreen.width/4;
		} else {
			xStart = 1;
			xEnd = elementLocator.x;
			xEnd = ((xEnd >= deviceScreen.width) ? deviceScreen.width - 60 : xEnd);
		}
		return new Tuple<Point,Point>(new Point(xStart,yStartAndEnd),new Point(xEnd,yStartAndEnd));
	}
	

	/**
	 * Horizontal swipe from element.
	 *
	 * @author carlos.cadena
	 * @param startElement  the start element
	 * @param movement      the movement
	 * @param maxSwipes the max swipes
	 */
	public static void hSwipeFromElement(MobileElement startElement, MovementsH movement, int maxSwipes) {
		Tuple<Point,Point> startAndEnd = getStartAndEndPositionForHorizontalSwipe(startElement, movement);
		for (int i = 0; i < maxSwipes; i++) {
			swipe(startAndEnd.v1().x, startAndEnd.v1().y, startAndEnd.v2().x, startAndEnd.v2().y, 1000);
		}
	}

	/**
	 * Horizontal swipe, element is used to get y position for the swiping, x will be the full device viewport.
	 *
	 * @author carlos.cadena
	 * @param startElement  the start element
	 * @param movement      the movement
	 * @param maxSwipes     the max swipes
	 */
	public static void hFullSwipeFromElement(MobileElement startElement, MovementsH movement, int maxSwipes) {
		Tuple<Point,Point> startAndEnd = getStartAndEndPositionForHorizontalSwipe(startElement, movement);
		Dimension deviceScreen = getScreenSize();
		int xEnd = 0;
		int xStart = 0;
		if(movement == MovementsH.RIGHT) {
			xStart = deviceScreen.width -20;
			xEnd = 10;
		}
		else {
			xStart = 10;
			xEnd = deviceScreen.width - 60;
		}
		for (int i = 0; i < maxSwipes; i++) {
			swipe(xStart, startAndEnd.v1().y, xEnd, startAndEnd.v2().y, 1000);
		}
	}

	/**
	 * Horizontal swipe, element is used to get y position for the swiping.
	 *
	 * @author carlos.cadena
	 * @param startElement  the start element
	 * @param movement      the movement
	 * @param maxSwipes     the max swipes
	 * @param duration      the duration
	 */
	public static void hSwipeFromElement(MobileElement startElement, MovementsH movement, int maxSwipes, long duration) {
		Tuple<Point,Point> startAndEnd = getStartAndEndPositionForHorizontalSwipe(startElement, movement);
		for (int i = 0; i < maxSwipes; i++) {
			swipe(startAndEnd.v1().x, startAndEnd.v1().y, startAndEnd.v2().x, startAndEnd.v2().y, duration);
		}
	}

	/**
	 * Horizontal swipe from one element to another.
	 *
	 * @author carlos.cadena
	 * @param startElement  the start element
	 * @param movement      the movement
	 * @param maxSwipes     the max swipes
	 * @param wantedElement the wanted element
	 * @return true, if successful
	 */
	public static boolean hSwipeToElement(MobileElement startElement, MovementsH movement, int maxSwipes,
										MobileElement wantedElement) {
		Tuple<Point,Point> startAndEnd = getStartAndEndPositionForHorizontalSwipe(startElement, movement);
		for (int i = 0; i < maxSwipes; i++) {
			if(!AppiumDriverFacade.isElementVisibleNoWait(wantedElement)){
				swipe(startAndEnd.v1().x, startAndEnd.v1().y, startAndEnd.v2().x, startAndEnd.v2().y, 1000);
			}			
			else {			
				centerElementOnScreenRespectToX(wantedElement);
				return true;
			}
		}
		return false;
	}

	/**
	 * Horizontal swipe from one element to another.
	 * 
	 * @author carlos.cadena
	 * @param startElement  the start element
	 * @param movement      the movement
	 * @param maxSwipes     the max maxSwipes
	 * @param wantedElement the wanted element
	 * @return true, if successful
	 */
	public static boolean hSwipeToElement(MobileElement startElement, MovementsH movement, int maxSwipes,
										By wantedElement) {
		Tuple<Point,Point> startAndEnd = getStartAndEndPositionForHorizontalSwipe(startElement, movement);
		for (int i = 0; i < maxSwipes; i++) {
			if(!AppiumDriverFacade.isElementVisible(wantedElement, 5)){
				swipe(startAndEnd.v1().x, startAndEnd.v1().y, startAndEnd.v2().x, startAndEnd.v2().y, 1000);
			}			
			else {
				centerElementOnScreenRespectToX(AppiumDriverFacade.findElement(wantedElement));
				return true;
			}
		}
		return false;
	}

	/**
	 * Scroll to element IOS script.
	 *
	 * @param element the element
	 * @param movement the movement
	 * @return true, if successful
	 */
	public static boolean scrollToElementIOSScript(MobileElement element, MovementsV movement) {
		JavascriptExecutor js = (JavascriptExecutor) appiumDriver.get();
		HashMap<String, String> scrollObject = new HashMap<String, String>();
		scrollObject.put("direction", movement == MovementsV.DOWN ? "down" : "up");
		scrollObject.put("element", ((RemoteWebElement) element).getId());
		js.executeScript("mobile: scroll", scrollObject);
		return AppiumDriverFacade.isElementVisible(element);
	}

	// endregion

	//region Keyboard Methods

	/**
	 * Press keyboard keys.
	 *
	 * @author carlos.cadena
	 * @param keys the keys
	 */
	public static void pressKeyboardKeys(String keys) {
		WebDriverUtils.pressKeyboardKeys(appiumDriver.get(), keys);
	}

	/**
	 * Hide keyboard.
	 *
	 * @author carlos.cadena
	 */
	public static void hideKeyboard() {
		WebDriverUtils.hideKeyboard(appiumDriver.get());

	}
	
	/**
	 * Press key for Android.
	 *
	 * @author carlos.cadena
	 * @param key the key
	 */
	public static void pressKey(AndroidKey key) {
		WebDriverUtils.pressKey(appiumDriver.get(), key);
	}


	//endregion

	//region Switch Context Methods

	/**
	 * Switch to context.
	 *
	 * @author carlos.cadena
	 * @param contextName the context name
	 * @param timeout the timeout
	 * @return true, if successful
	 */
	public static boolean switchToContext(String contextName, int timeout) {
		return WebDriverUtils.switchToContext(appiumDriver.get(), contextName, timeout);
	}

	/**
	 * Switch to first web context found.
	 *
	 * @author carlos.cadena
	 * @param timeout the timeout
	 * @return true, if successful
	 */
	public static boolean switchToFirstWebContextFound(int timeout) {
		return WebDriverUtils.switchToFirstWebContextFound(appiumDriver.get(), timeout);
	}
	
	//endregion

	//region Toggle Methods

	/**
	 * Toggle airplane mode.
	 */
	public static void toggleAirplaneMode() {
		WebDriverUtils.toggleAirplaneMode(appiumDriver.get(), isAndroidExecution());
	}

	/**
	 * Toggle data.
	 */
	public static void toggleData() {
		if (isAndroidExecution()) {
			WebDriverUtils.toogleData(appiumDriver.get(), isAndroidExecution());
		}
	}

	/**
	 * Toggle location.
	 */
	public static void toggleLocation() {
		if (isAndroidExecution()) {
			WebDriverUtils.toggleLocation(appiumDriver.get(), isAndroidExecution());
		}
	}

	/**
	 * Toggle wifi.
	 */
	public static void toggleWifi() {
		WebDriverUtils.toggleWifi(appiumDriver.get(), isAndroidExecution());
	}

	//endregion

}
