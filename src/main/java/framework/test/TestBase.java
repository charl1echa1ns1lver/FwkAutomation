package framework.test;

import com.applitools.eyes.selenium.Eyes;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.MediaEntityModelProvider;
import com.aventstack.extentreports.Status;
import framework.base.*;
import framework.report.Log;
import groovy.lang.Tuple2;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.ThreadContext;
import org.openqa.selenium.NoSuchSessionException;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Base64;

import static framework.base.AppiumDriverFacade.appiumDriver;
import static framework.base.AppiumDriverFacade.isAndroidExecution;

/**
 * The Class TestBase.
 * 
 * @author carlos.cadena
 */
public abstract class TestBase {

	/** The first run. */
	private static boolean firstRun;

	/** The eyes. */
	private Eyes eyes;

	/** The report. */
	public static ThreadLocal<ExtentTest> report = new ThreadLocal<>();

	/** The Constant screenshots. */
	private static final String screenshots = new File(System.getProperty("user.dir")).getAbsolutePath()
			+ File.separator + "screenshots" + File.separator;
	
	/** The device and platform. */
	private final ThreadLocal<Tuple2<String, String>> deviceAndPlatform = new ThreadLocal<>();
	
	/** The test name. */
	private ThreadLocal<String> testName;
	
	/** The screenshot. */
	private final ThreadLocal<MediaEntityModelProvider> screenshot = new ThreadLocal<>();

	/** The screenshot size. */
	private final ThreadLocal<Integer> screenshotSize = new ThreadLocal<>();

	/**
	 * Sets the report.
	 *
	 * @param rep the new report
	 * 
	 */
	public static void setReport(ExtentTest rep) {
		report.set(rep);
	}

	/**
	 * Gets the report.
	 *
	 * @return the report
	 * 
	 */
	public static ExtentTest getReport() {
		return report.get();
	}

	/**
	 * Gets the eyes.
	 *
	 * @return the eyes
	 * 
	 */
	public Eyes getEyes() {
		if (eyes == null) {
			eyes = new Eyes();
		}
		return eyes;
	}

	/** The test count. */
	public static int testCount = 1;

	/**
	 * The before suite method which sets the main parameters common to the whole
	 * test execution creation.
	 *
	 * @author carlos.cadena
	 * @param context the new up appium
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@BeforeSuite(alwaysRun = true)
	public void setUpAppium(ITestContext context) throws IOException {
		firstRun = true;
		getEyes().setApiKey(FrameworkProperties.getApplitoolsApiKey());
		Path screenshotsPath = new File(screenshots).toPath();
		if (!Files.exists(screenshotsPath)) {
			Files.createDirectory(screenshotsPath);
		}
		if (!FrameworkProperties.getDeviceName().equalsIgnoreCase("Dynamic")
				|| FrameworkProperties.getLocal().equalsIgnoreCase("True")) {
			context.getSuite().getXmlSuite().setThreadCount(1);
			System.setProperty("threadCount", "1");
		}
		System.setProperty("log4j.configurationFile", "log4j2-config.xml");
		System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
	}

	/**
	 * The before method which sets the main parameters common to the whole test
	 * execution creation.
	 *
	 * @author carlos.cadena
	 * @param result  the result
	 * @param context the new up appium
	 * @param method the method
	 * @throws URISyntaxException URI Syntax Exception
	 */
	@BeforeMethod(alwaysRun = true)
    public void setUpTest(ITestResult result, ITestContext context, Method method) throws URISyntaxException, NoSuchMethodException {
		try {
			if (context.getAttribute("onRetry") == null) {
				testName = new ThreadLocal<>();
				testName.set(context.getName());
				Log.logger = LogManager.getLogger(getClass());
				ThreadContext.put("threadName", context.getName().substring(0, Math.round(((float) context.getName().length() / 2))).replace("'", "").replace(":", "_").replaceAll("\\s+", ""));
				context.getCurrentXmlTest().getName();
			}
			Log.testStart(context.getName());
			if (Arrays.asList(method.getAnnotation(Test.class).groups()).contains("SMS")) {
				System.setProperty("APP", "SMS");
			}
			Log.testDescription(method.getAnnotation(Test.class).description());
			if(System.getProperties().containsKey("allDevices")) {
				AppiumDriverFacade.createDriver(false,
						testName.get(),
						context.getCurrentXmlTest().getParameter("device_name"),
						context.getCurrentXmlTest().getParameter("platform_version"));
			}
			else {
				String isMobile = context.getCurrentXmlTest().getParameter("mobile");
				boolean isHybrid = FrameworkProperties.getWeb().equalsIgnoreCase("True") && isMobile != null && isMobile.equalsIgnoreCase("True");
				if (FrameworkProperties.getWeb().equalsIgnoreCase("True") && !isHybrid) {
					WebDriverFacade.createDriver();
				} else if (context.getAttribute("onRetry") != null || firstRun || FrameworkProperties.getDeviceName().equalsIgnoreCase("Dynamic")
						|| this.getThreadCount(context) != 1) {
					AppiumDriverFacade.createDriver(isHybrid, testName.get(),
							FrameworkProperties.getDeviceName(), FrameworkProperties.getPlatformVersion());
				} else {
					PerformanceUtils.startTimer();
                    if (isAndroidExecution()) {
                        ((AndroidDriver) (appiumDriver.get())).activateApp((FrameworkProperties.getPackage()));
                    } else {
                        ((IOSDriver) (appiumDriver.get())).activateApp((FrameworkProperties.getPackage()));

                    }

					PerformanceUtils.addTimeToAverage(PerformanceUtils.stopTimer());
				}
			}
			firstRun = false;
			if (FrameworkProperties.getLocal().equalsIgnoreCase("False")) {
				URL sauceURL = URI.create(appiumDriver.get().getCapabilities().getCapability("testobject_test_report_url").toString()).toURL();
				deviceAndPlatform.set(TestUtils.getDevicePlatformNameAndVersion());
				getReport().getModel().setName(context.getName() + "<br>" + "<b>Device:</b> '" +
						deviceAndPlatform.get().getFirst() + "' - <b>Platform:</b> '" +
						deviceAndPlatform.get().getSecond() + "' - <b>SauceLabs:</b> <a href=\"" +
						sauceURL + "\">Enlace Ejecucion</a>");
				Log.logger
						.info("Test executed on device: '" + deviceAndPlatform.get().getFirst() + "' and Platform: '" + deviceAndPlatform.get().getSecond() + "'");
			}
		} catch (RuntimeException | IOException e) {
			e.printStackTrace();
			skipTest(result, "Connection error or no Session was created", e);
		}
	}
	
	/**
	 * Recovery method to trigger retry for test.
	 *
	 * @author carlos.cadena
	 * @param <T> the generic type
	 * @param result the result
	 * @param errorMessage the error message
	 * @param e the exception
	 */
	private <T extends Exception> void skipTest(ITestResult result, String errorMessage, T e) {
		ExecutionRecovery recovery = (ExecutionRecovery) (result.getMethod().getRetryAnalyzer(result));
		String error = errorMessage + ((e != null && e.getMessage() != null) ? " -> " + e.getMessage()  : "");
		getReport().skip(error);
		Log.logger.debug("Test Skipped " + result.getMethod().getMethodName() + "-> " + error);
		recovery.setExhausted(true);
		throw new SkipException(error);

	}

	/**
	 * Log and take screenshot for failed and skipped tests.
	 *
	 * @author carlos.cadena
	 * @param result  the result
	 * @param context the context
	 */
	private void logAndTakeScreenshotForTest(ITestResult result, ITestContext context) {
		if ((Log.failReason != null && !Log.failReason.isEmpty()))
			Log.testFail(context.getName());
		try {
			String imageTitle = "screenshot_" + RandomStringUtils.randomAlphabetic(15);	
			String imagePath = screenshots + imageTitle + ".png";
			WebDriverUtils.takeScreenshot(AppiumDriverFacade.getDriver() == null ? WebDriverFacade.getDriver() : AppiumDriverFacade.getDriver(),imagePath);
			byte[] bytes = Files.readAllBytes(new File(imagePath).toPath());
			screenshotSize.set(bytes.length);
			screenshot.set(MediaEntityBuilder
					.createScreenCaptureFromBase64String(
							new String(Base64.getEncoder().encode(bytes)))
					.build());
			getReport().log(Status.FAIL, result.getThrowable().getMessage(), screenshot.get());
		} catch (RuntimeException | IOException e) {
			e.printStackTrace();
			getReport().log(Status.FAIL, result.getThrowable().getMessage());
			getReport().info("There was an error capturing screenshot > " + e.getMessage());
		}

	}
	
	
	/**
	 * Log and take screenshot
	 *
	 * @author carlos.cadena
	 * @param screenshotTitle  the screenshot title
	 */
	public void logAndTakeScreenshotForTest(String screenshotTitle) {
		try {
			String imageTitle =  RandomStringUtils.randomAlphabetic(10);
			String imagePath = screenshots + imageTitle + ".png";
			WebDriverUtils.takeScreenshot(AppiumDriverFacade.getDriver() == null ? WebDriverFacade.getDriver() : AppiumDriverFacade.getDriver(),imagePath);
			getReport().log(Status.INFO, screenshotTitle, MediaEntityBuilder
					.createScreenCaptureFromBase64String(
							new String(Base64.getEncoder().encode(Files.readAllBytes(new File(imagePath).toPath()))))
					.build());
		} catch (RuntimeException | IOException e) {
			getReport().info("There was an error capturing screenshot > " + e.getMessage());
		}

	}
	
	/**
	 * Log result sauce labs.
	 *
	 * @param result the result
	 */
	private void logResultSauceLabs(ITestResult result) {
		if (result.getStatus() != ITestResult.CREATED && result.getStatus() != ITestResult.STARTED) {
			if (result.getStatus() == ITestResult.SKIP) {
				logTestSkippedOnSauce();
			} else {
				logTestPassedFailedOnSauce(result.isSuccess());
			}
		}
	}

	/**
	 * Closing operations when test execution ends.
	 *
	 * @author carlos.cadena
	 * @param result  the result
	 * @param context the context
	 */
	private void closeTest(ITestResult result, ITestContext context) {
		try {
			if(FrameworkProperties.getWeb().equalsIgnoreCase("true")) {
				WebDriverFacade.shutdown();
			}
			if (FrameworkProperties.getLocal().equalsIgnoreCase("true")
					|| (!FrameworkProperties.getDeviceName().equalsIgnoreCase("Dynamic")
							&& this.getThreadCount(context) == 1)) {
				if (context.getSuite().getAllMethods().size() == testCount) {
					logResultSauceLabs(result);
					appiumDriver.get().quit();
				} else {
                    appiumDriver.get().close();
				}
			} else {
				logResultSauceLabs(result);
                appiumDriver.get().close();
				appiumDriver.get().quit();
			}
		} catch (NullPointerException | NoSuchSessionException e) {
			Log.logger.debug("Session is not up and running, so there is no need to close it for test '"
					+ context.getName() + "'");
		}
	}

	

	/**
	 * The After method which is the responsible for closing operations after each
	 * test execution.
	 *
	 * @author carlos.cadena
	 * @param result  the result
	 * @param context the context
	 * @param method  the method
	 */
	@AfterMethod(alwaysRun = true)
	public void closeApp(ITestResult result, ITestContext context, Method method) {
		ExecutionRecovery recovery = (ExecutionRecovery) (result.getMethod().getRetryAnalyzer(result));
		if (result.getStatus() == ITestResult.SKIP && recovery.retryWasCalled() && recovery.isExhausted()) {
			Log.logger.debug("Skipping test ->" + context.getName() + "-> test status :" + result.getStatus());
			TestBase.getReport().skip("Retries were exhausted and test was not executed, please re run");
		}
		try {
			switch (result.getStatus()) {
			case ITestResult.SUCCESS:
				//here you can log test result on jira or whatever tool you use for saving test results
				break;
			case ITestResult.FAILURE:
				logAndTakeScreenshotForTest(result, context);
				//here you can log test result FAILURE on jira or whatever tool you use for saving test results
				break;
			}
			Log.testEnd(context.getName());
			closeTest(result, context);
			testCount++;
		} catch (RuntimeException e) {
			e.printStackTrace();
			getReport().info("There was an error closing session");
		}
	}

	
	/**
	 * Log test skipped result on Sauce Labs.
	 *
	 * @author carlos.cadena
	 */
	private void logTestSkippedOnSauce() {
		Log.logger.debug("Skipping test on Sauce Labs");
		if (!FrameworkProperties.getLocal().equalsIgnoreCase("true") && appiumDriver.get() != null) {
			RestAssured.baseURI = "https://app.testobject.com/api/rest/v2/appium/session/"
					+ appiumDriver.get().getSessionId() + "/skiptest";
			RestAssured.put();
		}
	}

	/**
	 * Log test result on Sauce Labs.
	 *
	 * @author carlos.cadena
	 * @param result the result
	 */
	private void logTestPassedFailedOnSauce(boolean result) {
		String bodySauceResult = "{\"passed\":" + (result ? Boolean.toString(true) : Boolean.toString(false)) + "}";
		if (appiumDriver.get().getCapabilities().getCapability("testobject_api_key") != null) {
			RestAssured.baseURI = "https://app.testobject.com/api/rest/v2/appium/session/"
					+ appiumDriver.get().getSessionId() + "/test";
			RestAssured.given().body(bodySauceResult)
					.contentType(ContentType.JSON).put();
		} else {
			RestAssured.baseURI = "https://saucelabs.com/rest/v1/xxxxxx/jobs/"
					+ appiumDriver.get().getSessionId();
			RestAssured.given().auth().basic("xxxxxxx", "023e7076-3977-4c07-93c9-7630d90de9ec")
					.body(bodySauceResult)
					.contentType(ContentType.JSON).put();
		}
	}

	/**
	 * Gets the thread count.
	 *
	 * @author carlos.cadena
	 * @param context the context
	 * @return the thread count
	 */
	private int getThreadCount(ITestContext context) {
		if (FrameworkProperties.getLocal().equalsIgnoreCase("true")) {
			return context.getSuite().getXmlSuite().getThreadCount();
		}
		return Integer.parseInt(System.getProperty("threadCount"));
	}

	/**
	 * Gets the thread count.
	 *
	 * @author carlos.cadena
	 */
	@AfterSuite(alwaysRun = true)
	public void endSuite() {
		try {
			if (Files.exists(new File(screenshots).toPath())) {
				File screenshotFolder = new File(screenshots);
				File[] files = screenshotFolder.listFiles();
				assert files != null;
				for (File f : files) {
					if (!f.getCanonicalPath().contains("gitignore"))
						f.deleteOnExit();
				}
			}
		} catch (IOException e) {
			getReport().debug(
					"There was an unexpected error trying to delete files on 'screenshots' folder, files will be deleted on next run");
		}
		if (eyes.getIsOpen()) eyes.close(false);
	}
}
