package framework.base;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.support.ui.FluentWait;

import com.google.common.base.Function;

import framework.page.settings.IOSMainPage;
import framework.page.settings.IOSWifiPage;
import framework.report.Log;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;

public class WebDriverUtils {
	
	
    /**
     *  take a screenshot.
     */
    public static void takeScreenshot(WebDriver driver, String screenshotName, String saveDirectory){
        File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        screenshot.renameTo(new File(saveDirectory + screenshotName + ".png"));
    }

	/**
	 * Takes screenshot.
	 *
	 * @author carlos.cadena
	 * @param screenshotTitle
	 *            the screenshot title
	 * @param saveDirectory
	 *            the save directory
	 * @param extension
	 *            > provide a valid format e.g .png or .jpeg
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void takeScreenshot(WebDriver driver, String screenshotTitle, String saveDirectory, String extension)
			throws IOException {
		String pathString = saveDirectory + screenshotTitle + extension;
		takeScreenshot(driver, pathString);
	}

	/**
	 * Takes screenshot using an specific absolute path.
	 *
	 * @author carlos.cadena
	 * @param screenshotPath
	 *            the screenshot path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void takeScreenshot(WebDriver driver, String screenshotPath) throws IOException {
		WebDriver augmentedDriver = new Augmenter().augment(driver);
		File screenshot = ((TakesScreenshot) augmentedDriver).getScreenshotAs(OutputType.FILE);
		File picture = new File(screenshotPath);
		picture.setReadable(true);
		picture.setWritable(true);
		if (picture.exists()) {
			picture.delete();
		}
		if (screenshot.exists()) {
			FileUtils.copyFile(screenshot, picture);
		}
	}

	// endregion

	// region Toggle Methods

	/**
	 * Send the current application to the background.
	 *
	 * @author carlos.cadena
	 */
	public static <T extends WebElement> void sendCurrentApplicationToTheBackground(AppiumDriver<T> driver) {
		driver.runAppInBackground(Duration.ofSeconds(-1));
	}

	/**
	 * Activate applicacion.
	 *
	 * @author carlos.cadena
	 * @param appPackage
	 *            the app package
	 */
	public static <T extends WebElement> void activateApplication(AppiumDriver<T> driver, String appPackage) {
		driver.activateApp(appPackage);
	}

	/**
	 * Terminate application.
	 *
	 * @author carlos.cadena
	 * @param appBundleID
	 *            the app bundle ID
	 */
	public static <T extends WebElement> void terminateApplicationIos(AppiumDriver<T> driver, String appBundleID) {
		Map<String, Object> params = new HashMap<>();
		params.put("bundleId", appBundleID);
		driver.executeScript("mobile: terminateApp", params);
	}

	/**
	 * Terminate caixa application.
	 *
	 * @author carlos.cadena
	 */
	public static <T extends WebElement> void closeApplication(AppiumDriver<T> driver, String appBundleID) {
		AppiumDriverFacade.appiumDriver.get().closeApp();
	}
	
	//region Toggle Methods


	/**
	 * Toggle airplane mode.
	 */
	public static <T extends WebElement> void toggleAirplaneMode(AppiumDriver<T> driver, boolean isAndroidExecution) {
		if (isAndroidExecution) {
			((AndroidDriver<T>) driver).toggleAirplaneMode();
		} else {
			sendCurrentApplicationToTheBackground(driver);
			activateApplication(driver, "com.apple.Preferences");
			IOSMainPage iosMainPage = new IOSMainPage();
			iosMainPage.tapOnPlaneModeSwitch();
			terminateApplicationIos(driver, "com.apple.Preferences");
			activateApplication(driver, FrameworkProperties.getPackage());
		}
		Log.testStep("Toggle AirplaneMode");
	}

	/**
	 * Toggle data.
	 * 
	 * @return
	 */
	public static <T extends WebElement> void toogleData(AppiumDriver<T> driver, boolean isAndroidExecution) {
		if (isAndroidExecution) {
			((AndroidDriver<T>) driver).toggleData();
		}
		Log.testStep("Toggle Data");
	}

	/**
	 * Toggle location.
	 */
	public static <T extends WebElement> void toggleLocation(AppiumDriver<T> driver, boolean isAndroidExecution) {
		if (isAndroidExecution) {
			((AndroidDriver<T>) driver).toggleLocationServices();
		}
		Log.testStep("Toggle Location");
	}

	/**
	 * Toggle wifi.
	 */
	public static <T extends WebElement> void toggleWifi(AppiumDriver<T> driver, boolean isAndroidExecution) {
		if (isAndroidExecution) {
			((AndroidDriver<T>) driver).toggleWifi();
		} else {
			sendCurrentApplicationToTheBackground(driver);
			activateApplication(driver, "com.apple.Preferences");
			IOSMainPage iosMainPage = new IOSMainPage();
			IOSWifiPage iosWifiPage = iosMainPage.tapOnWifiOption();
			iosWifiPage.tapOnWifiSwitch();
			terminateApplicationIos(driver, "com.apple.Preferences");
			activateApplication(driver, FrameworkProperties.getPackage());
		}
		Log.testStep("Toggle Wifi");
	}
	
	//endregion
	

	//region Keyboard Methods

	/**
	 * Press keyboard keys.
	 *
	 * @author carlos.cadena
	 * @param keys the keys
	 */
	public static <T extends WebElement> void pressKeyboardKeys(AppiumDriver<T> driver, String keys) {
		driver.getKeyboard().sendKeys(keys);
	}

	/**
	 * Hide keyboard.
	 *
	 * @author carlos.cadena
	 */
	public static <T extends WebElement> void hideKeyboard(AppiumDriver<T> driver) {
		driver.hideKeyboard();
	}
	
	/**
	 * Press key for Android
	 *
	 * @author carlos.cadena
	 */
	public static void pressKey(AppiumDriver driver, AndroidKey key) {
		if (FrameworkProperties.getPlatformName().equalsIgnoreCase("Android")) {
			((AndroidDriver) driver).pressKey(new KeyEvent(key));
		}
	}
	
	// region Android Actions Methods

	/**
	 * Tap Android native Back button.
	 *
	 * @author carlos.cadena
	 *
	 */
	public static void tapAndroidBackButton(AppiumDriver driver) {
		((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
	}

	/**
	 * Tap Android native Back button.
	 *
	 * @author carlos.cadena
	 *
	 */
	public static void tapAndroidHomeButton(AppiumDriver driver) {
		((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.HOME));
	}

	/**
	 * Tap Android native Enter button.
	 *
	 * @author carlos.cadena
	 *
	 */
	public static void tapAndroidEnterButton(AppiumDriver<? extends WebElement> driver) {
		((AndroidDriver<? extends WebElement>) driver).pressKey(new KeyEvent(AndroidKey.ENTER));
	}

	// endregion



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
	public static <T extends WebElement> boolean switchToContext(AppiumDriver<T> driver, String contextName, int timeout) {
		try {
			return new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeout))
					.ignoring(WebDriverException.class)
					.until(new Function<WebDriver, Boolean>() {
						public Boolean apply(WebDriver arg) {
							return driver.getContext().contains(contextName);
						}
					});
		} catch (TimeoutException e) {
			return false;
		}
	}

	/**
	 * Switch to first web context found.
	 *
	 * @author carlos.cadena
	 * @param timeout the timeout
	 * @return true, if successful
	 */
	public static <T extends WebElement> boolean switchToFirstWebContextFound(AppiumDriver<T> driver, int timeout) {
		try {
			return new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeout))
					.ignoring(WebDriverException.class).ignoring(java.util.NoSuchElementException.class)
					.until(new Function<WebDriver, Boolean>() {
						Set<String> contexts =driver.getContextHandles();
						public Boolean apply(WebDriver arg) {
							driver.context(contexts.stream().filter((contextNames -> contextNames.contains("WEBVIEW") || contextNames.contains("CHROMIUM"))).findFirst().get());
							String contextSelected = driver.getContext();
							if(driver.getContext().contains("WEBVIEW") ||driver.getContext().contains("CHROMIUM")) {
								Log.logger.debug("Context now -> " +  contextSelected);
								return true;
							}else {
								return false;
							}
						}
					});
		} catch (TimeoutException e) {
			return false;
		}
	}
	//endregion
	
	
}
