package framework.base;


import com.google.common.collect.ImmutableMap;
import framework.report.Log;
import framework.test.TestUtils.Browser;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.ios.IOSDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.UnexpectedException;
import java.util.*;

/**
 * The Class WebDriverFacade.
 */
public class WebDriverFacade {

	/** The web driver. */
	private static ThreadLocal<WebDriver> webDriver = new ThreadLocal<WebDriver>();
	
	/** The Constant pageTimeOut. */
	public static final int pageTimeOut = Integer.valueOf(FrameworkProperties.getTimeout()).intValue();
	
	/** The is android execution. */
	private static ThreadLocal<Boolean> isAndroidExecution = new ThreadLocal<Boolean>() ;
	
    /**
     * Gets the driver.
     *
     * @return the driver
     */
    public static WebDriver getDriver() {
    	return webDriver.get();
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
    
    
    /** The username. */
    private static String username =  FrameworkProperties.getSauceUsername();
    
    /** The accesskey. */
    private static String accesskey = FrameworkProperties.getSauceAccessKey();
    
    /** The session id. */
    private static ThreadLocal<String> sessionId = new ThreadLocal<String>();

	/**
	 * Gets the session id.
	 *
	 * @return the session id
	 */
	public static String getSessionId() {
		return sessionId.get();
	}
	
	/**
	 * Creates the driver.
	 */
	public static void createDriver(){
	        switch (FrameworkProperties.getBrowser().toUpperCase()){
	            case "FIREFOX":
	                firefoxDriverInitialize();
	                break;
	            case "CHROME":
	                chromeDriverInitialize();
	                break;
	            case "EDGE":
	                edgeDriverInitialize();
	                break;
	            default:
	                throw new IllegalArgumentException(String.format("The selected driver %s is not supported", FrameworkProperties.getBrowser()));
	        }
	        maximize();
     }

    /**
     * Creates the driver.
     *
     * @param size the size
     * @throws UnexpectedException the unexpected exception
     * @throws MalformedURLException the malformed URL exception
     */
    public static void createDriver(String size) throws UnexpectedException, MalformedURLException{
		if (FrameworkProperties.getLocal().equalsIgnoreCase("true")) {
			  createDriver();
        switch (size.toUpperCase()){
                case "SMALL":
                    resizeWindows(400, 600);
                    break;
                case "MEDIUM":
                    resizeWindows(768, 1024);
                    break;
                case "LARGE":
                    resizeWindows(1280, 1024);
                    break;
                case "FULL":
                    maximize();
                    break;
                default:
                    throw new IllegalArgumentException(String.format("The size %s is not supported", size));
        }
		}
		else {
			createRemoteDriver();
		}
    }
    
    /**
     * Creates the remote driver.
     *
     * @throws MalformedURLException the malformed URL exception
     * @throws UnexpectedException the unexpected exception
     */
    protected static void createRemoteDriver()
            throws MalformedURLException, UnexpectedException {
        DesiredCapabilities capabilities = new DesiredCapabilities();

        // set desired capabilities to launch appropriate browser on Sauce
        capabilities.setCapability("browserName", FrameworkProperties.getBrowser());
        capabilities.setCapability("version", FrameworkProperties.getBrowserVersion());
        capabilities.setCapability("platform", FrameworkProperties.getOS());
        //capabilities.setCapability(CapabilityType.VERSION, FrameworkProperties.getOSVersion());


        // Launch remote browser and set it as the current thread
        webDriver.set(new RemoteWebDriver(
                new URL("https://" + username + ":" + accesskey + "@ondemand.saucelabs.com/wd/hub"),
                capabilities));

        // set current sessionId
        String id = ((RemoteWebDriver) getDriver()).getSessionId().toString();
        sessionId.set(id);
    }

    
    /**
     * Creates the driver.
     *
     * @param browser the browser
     */
    public static void createDriver(Browser browser){
    	switch (browser){
        case FIREFOX:
            firefoxDriverInitialize();
            break;
        case CHROME:
            chromeDriverInitialize();
            break;
        case EDGE:
            edgeDriverInitialize();
            break;
        default:
            throw new IllegalArgumentException(String.format("The selected driver %s is not supported", browser.toString()));
    }
    maximize();
    }
    
    /**
     * Creates the driver for mobile web.
     *
     * @param url the url
     * @param cap the cap
     */
    public static void createDriverForMobileWeb(URL url, MutableCapabilities cap){
    	
        switch (FrameworkProperties.getBrowser().toUpperCase()){
            case "FIREFOX":
            case "EDGE":
                webDriver.set(new AppiumDriver<WebElement>(url,cap));
                break;
            case "CHROME":
            	cap.setCapability("appium:chromeOptions", ImmutableMap.of("w3c", false));
                break;
            default:
                throw new IllegalArgumentException(String.format("The selected driver %s is not supported", FrameworkProperties.getBrowser()));
        }
        if(cap.getPlatform().equals(Platform.ANDROID)) {
        	isAndroidExecution.set(true);
        	AndroidDriver<WebElement> driver = new AndroidDriver<WebElement>(url,cap);
            webDriver.set(driver);
        }
        if(cap.getPlatform().equals(Platform.IOS)) {
        	isAndroidExecution.set(false);
        	IOSDriver<WebElement> driver = new IOSDriver<WebElement>(url,cap);
            webDriver.set(driver);
        }
    }

    /**
     *  initialize the Firefox driver.
     */
    public static void firefoxDriverInitialize(){
        WebDriverManager.firefoxdriver().setup();
        webDriver.set(new FirefoxDriver());
    }
    
    /**
     * Initialize chrome options.
     *
     * @return the chrome options
     */
    public static ChromeOptions initializeChromeOptions() {
    	  ChromeOptions chromeOptions = new ChromeOptions();
          WebDriverManager.chromedriver().setup();
          HashMap<String, Object> chromePrefs = new HashMap<>();
          chromePrefs.put("credentials_enable_service", false);
  		  if (!FrameworkProperties.getBrowserVersion().isEmpty() && FrameworkProperties.getBrowserVersion() != null
  				&& Double.parseDouble(FrameworkProperties.getBrowserVersion()) <= 74) {
  			chromePrefs.put("w3c", false);
  		  }
  		  else {
  			  chromePrefs.put("w3c", true);
  		  }
          chromeOptions.setExperimentalOption("prefs", chromePrefs);
          chromeOptions.addArguments("--disable-infobars");
          chromeOptions.addArguments("--disable-application-cache");
          chromeOptions.addArguments("--disable-web-security");
          chromeOptions.addArguments("--allow-running-insecure-content");
          chromeOptions.addArguments("--expose-internals-for-testing");
          chromeOptions.addArguments("--disable-sync");		
          chromeOptions.addArguments("test-type");
          chromeOptions.addArguments("--disable-extensions");
          chromeOptions.addArguments("no-sandbox");
          return chromeOptions;
    }
    

    /**
     *  initialize the Chrome driver.
     */
    public static void chromeDriverInitialize(){
        webDriver.set(new ChromeDriver(initializeChromeOptions()));
    }

    /**
     *  initialize the Microsoft EDGE driver.
     */
    public static void edgeDriverInitialize(){
    	WebDriverManager.edgedriver().arch64().setup();
        webDriver.set(new EdgeDriver());
    }

    //endregion

 
    /**
     *  maximize the browser windows.
     */
    public static void maximize(){
    	getDriver().manage().window().maximize();
    }

    /**
     *  resize the browser windows.
     *
     * @param width the width
     * @param height the height
     */
    public static void resizeWindows(int width, int height){
        Dimension resolution = new Dimension(width, height);
        getDriver().manage().window().setSize(resolution);
    }

    /**
     *  refresh the current windows.
     *
     * @param secondsToWait the seconds to wait
     */
    public static void refreshCurrentWindow(int secondsToWait){
    	getDriver().navigate().refresh();
    }

    /**
     *  navigate to the previous windows.
     *
     * @param secondsToWait the seconds to wait
     */
    public static void clickNavigateBackButton(int secondsToWait){
    	getDriver().navigate().back();
    }
    
    
    /**
     * Navigate to url.
     *
     * @param <T> the generic type
     * @param page the page
     * @param url the url
     * @return Page instance
     * @throws InstantiationException the instantiation exception
     * @throws IllegalAccessException the illegal access exception
     */
    public static <T extends WebBasePage> T navigateTo(Class<T> page, String url) throws InstantiationException, IllegalAccessException{
    	getDriver().get(url);
    	return page.newInstance();
    }
    
    /**
     *  take a screenshot.
     *
     * @param screenshotName the screenshot name
     * @param saveDirectory the save directory
     */
    public static void takeScreenshot(String screenshotName, String saveDirectory){
        WebDriverUtils.takeScreenshot(getDriver(),screenshotName,saveDirectory);
    }
    
	/**
	 * Takes screenshot.
	 *
	 * @author carlos.cadena
	 * @param screenshotTitle the screenshot title
	 * @param saveDirectory the save directory
	 * @param extension       > provide a valid format e.g .png or .jpeg
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void takeScreenshot(String screenshotTitle, String saveDirectory,
			String extension) throws IOException {
		WebDriverUtils.takeScreenshot(getDriver(), screenshotTitle, saveDirectory, extension);
	}

	/**
	 * Takes screenshot using an specific absolute path.
	 *
	 * @author carlos.cadena
	 * @param screenshotPath the screenshot path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void takeScreenshot(String screenshotPath) throws IOException {
		WebDriverUtils.takeScreenshot(getDriver(), screenshotPath);
	}

    /**
     *  close the current windows.
     */
    public static void closeCurrentWindow(){
        getDriver().close();
    }

    /**
     *  close the entire driver.
     */
    public static void shutdown(){
    	try {
    	getDriver().close();
        getDriver().quit();
    	}
    	catch(Exception e)
    	{
    		System.out.println(" Issue closing driver, trying re retry..." +  e.getMessage());
            getDriver().quit();
    	}
    }

    //endregion

    //region Driver Get Methods

    /**
     *  get the current page URL.
	 * @return String
     */
    public static String getPageURL() { return getDriver().getCurrentUrl();}

    /**
     *  get the current page title.
	 * @return String
     */
    public static String getPageTitle() { return getDriver().getTitle();}
    
    /**
     * Find an element first evaluating visibility.
     *
     * @author carlos.cadena
     * @param locator the locator
     * @return WebElement
     */
	public static WebElement findElementIfVisible(By locator) {
		return findElement(locator, pageTimeOut, true);
	}
	
	/**
	 * Find elements if all are visible.
	 *
	 * @author carlos.cadena
	 * @param locator the locator
     * @return A List of WebElement (with zero elements if condition was not met)
	 */
	public static List<WebElement> findElementsAllVisible(By locator) {
		return findElements(locator, pageTimeOut, true);
	}
	
	/**
	 * Find an element if is present.
	 *
	 * @author carlos.cadena
	 * @param locator the locator
	 * @return WebElement
	 */
	public static WebElement findElement(By locator) {
		return findElement(locator, pageTimeOut, false);
	}
	
	/**
	 * Find elements (since at least one is present that one will be returned ).
	 *
	 * @author carlos.cadena
	 * @param locator the locator
	 * @return WebElement
	 */
	public static List<WebElement> findElements(By locator) {
		return findElements(locator, pageTimeOut, false);
	}
	
	/**
	 * Find Element.
	 *
	 * @author carlos.cadena
	 * @param locator the locator
	 * @param timeOut the time out
	 * @param visibility the visibility
     * @return WebElement
	 */
	public static WebElement findElement(By locator, int timeOut, boolean visibility) {
		return Utils.findElement(getDriver(), locator, timeOut, visibility);
	}
	
	/**
	 * Find Element on a element container.
	 *
	 * @author carlos.cadena
	 * @param container the container
	 * @param locator the locator
	 * @param visibility the visibility
	 * @return WebElement
	 */
	public static WebElement findElement(WebElement container, By locator, boolean visibility) {
		return findElement(container, locator, pageTimeOut , visibility);
	}
	
	
	/**
	 * Find Element on a element container.
	 *
	 * @author carlos.cadena
	 * @param container the container
	 * @param locator the locator
	 * @param timeOut the time out
	 * @param visibility the visibility
	 * @return WebElement
	 */
	public static WebElement findElement(WebElement container, By locator, int timeOut, boolean visibility) {
		return Utils.findElement(container, locator, timeOut, visibility);
	}
	
	/**
	 * Find Elements.
	 *
	 * @author carlos.cadena
	 * @param locator the locator
	 * @param timeOut the time out
	 * @param visibility the visibility
     * @return A List of WebElement
	 */
	public static List<WebElement> findElements(By locator, int timeOut, boolean visibility) {
		return Utils.findElements(getDriver(), locator, pageTimeOut, visibility);
    }
	
	/**
	 * Gets the text.
	 *
	 * @param element the element
	 * @return the text
	 */
	public static String getText(WebElement element) {
		return getText(element, pageTimeOut);
	}
	
	/**
	 * Gets the text.
	 *
	 * @param element the element
	 * @param timeOut the time out
	 * @return the text
	 */
	public static String getText(WebElement element, int timeOut) {
		try {
			return element.getText();
		} catch (StaleElementReferenceException | NoSuchElementException | ElementNotVisibleException e) {
			Utils.waitForElementVisibility(webDriver.get(), element, timeOut);
			return element.getText();
		}
	}
	
	/**
	 * Checks if is element present.
	 *
	 * @param locator the locator
	 * @return true, if is element present
	 */
	public static boolean isElementPresent(By locator) {
		return Utils.isElementPresent(getDriver(), locator, pageTimeOut);
	}

	/**
	 * Checks if is element present.
	 *
	 * @param locator the locator
	 * @param timeout the timeout
	 * @return true, if is element present
	 */
	public static boolean isElementPresent(By locator, int timeout) {
		return Utils.isElementPresent(getDriver(), locator, timeout);
	}

	/**
	 * Checks if is element enabled.
	 *
	 * @param element the element
	 * @param timeout the timeout
	 * @return true, if is element enabled
	 */
	public static boolean isElementEnabled(WebElement element, int timeout) {
		return Utils.isElementEnabled(getDriver(), element, timeout);
	}
	
	/**
	 * Checks if is element enabled.
	 *
	 * @param locator the locator
	 * @return true, if is element enabled
	 */
	public static boolean isElementEnabled(By locator) {
		return isElementEnabled(locator, pageTimeOut);
	}
	
	/**
	 * Checks if is element enabled.
	 *
	 * @param locator the locator
	 * @param timeout the timeout
	 * @return true, if is element enabled
	 */
	public static boolean isElementEnabled(By locator, int timeout) {
		return Utils.isElementEnabled(getDriver(), locator, timeout);
	}


	/**
	 * Checks if is element enabled.
	 *
	 * @param element the element
	 * @return true, if is element enabled
	 */
	public static boolean isElementEnabled(WebElement element) {
		return isElementEnabled(element, pageTimeOut);
	}

	/**
	 * Checks if is element enabled.
	 *
	 * @param elements the elements
	 * @param index the index
	 * @param timeout the timeout
	 * @return true, if is element enabled
	 */
	public static boolean isElementEnabled(List<WebElement> elements, int index, int timeout) {
		return Utils.isElementEnabled(getDriver(), elements, index, timeout);
	}

	/**
	 * Checks if is element enabled.
	 *
	 * @param elements the elements
	 * @param index the index
	 * @return true, if is element enabled
	 */
	public static boolean isElementEnabled(List<WebElement> elements, int index) {
		return isElementEnabled(elements, index, pageTimeOut);
	}
	
	/**
	 * Wait for element visibility.
	 *
	 * @param locator the locator
	 */
	public static void waitForElementVisibility(By locator) {
		waitForElementVisibility(locator, pageTimeOut);
	}
	
	/**
	 * Wait for element visibility.
	 *
	 * @param locator the locator
	 * @param timeout the timeout
	 */
	public static void waitForElementVisibility(By locator, int timeout) {
		Utils.waitForElementVisibility(getDriver(), locator, timeout);
	}
	
	/**
	 * Wait for element presence.
	 *
	 * @param locator the locator
	 */
	public static void waitForElementPresence(By locator) {
		waitForElementPresence(locator, pageTimeOut);
	}
	
	/**
	 * Wait for element presence.
	 *
	 * @param locator the locator
	 * @param timeout the timeout
	 */
	public static void waitForElementPresence(By locator, int timeout) {
		Utils.waitForElementPresence(getDriver(), locator, timeout);
	}
	
	/**
	 * Wait for element visibility.
	 *
	 * @param element the element
	 * @param timeout the timeout
	 */
	public static void waitForElementVisibility(WebElement element, int timeout) {
		Utils.waitForElementVisibility(getDriver(), element, timeout);
	}
	
	/**
	 * Wait for element visibility.
	 *
	 * @param element the element
	 */
	public static void waitForElementVisibility(WebElement element) {
		waitForElementVisibility(element, pageTimeOut);
	}
	
	/**
	 * Checks if is attribute present.
	 *
	 * @param locator the locator
	 * @param attribute the attribute
	 * @param value the value
	 * @param contains the contains
	 * @return true, if is attribute present
	 */
	public static boolean isAttributePresent(By locator, String attribute, String value, boolean contains) {
		return Utils.isAttributePresentOnElement(getDriver(), locator, attribute, value, contains, pageTimeOut);
	}
	
	/**
	 * Checks if is element visible.
	 *
	 * @param element the element
	 * @return true, if is element visible
	 */
	public static boolean isElementVisible(WebElement element) {
		return isElementVisible(element, pageTimeOut);
	}

	/**
	 * Checks if is element visible.
	 *
	 * @param locator the locator
	 * @return true, if is element visible
	 */
	public static boolean isElementVisible(By locator) {
		return isElementVisible(locator, pageTimeOut);
	}
	
	/**
	 * Checks if is element visible.
	 *
	 * @param container the container
	 * @param locator the locator
	 * @param timeOut the time out
	 * @return true, if is element visible
	 */
	public static boolean isElementVisible(WebElement container, By locator, int timeOut) {
		return Utils.isElementVisible(container, locator, timeOut);	
	}
	
	
	/**
	 * Checks if is element visible.
	 *
	 * @param element the element
	 * @param timeOut the time out
	 * @return true, if is element visible
	 */
	public static boolean isElementVisible(WebElement element, int timeOut) {
		return Utils.isElementVisible(getDriver(), element, timeOut);
	}

	/**
	 * Checks if is element visible.
	 *
	 * @param locator the locator
	 * @param timeOut the time out
	 * @return true, if is element visible
	 */
	public static boolean isElementVisible(By locator, int timeOut) {
		return Utils.isElementVisible(getDriver(), locator, timeOut);
	}
	
	/**
	 * Find first visible element specifying timeout.
	 *
	 * @author carlos.cadena
	 * @param locators the locators
	 * @param timeout the timeout
	 * @return the web element
	 */
	public static WebElement findFirstVisibleElement(List<By> locators, int timeout) {
		Optional<By> result = locators.stream().filter(locator -> WebDriverFacade.isElementVisible(locator, timeout))
				.findFirst();
		if (result.isPresent()) {
			Log.logger.debug("visible locator found -> " + result.get().toString());
			return WebDriverFacade.findElement(result.get());
		} else {
			throw new NoSuchElementException("No visible element was found with the locators provided, check..");
		}
		}


	/**
	 * Find first visible element.
	 *
	 * @author carlos.cadena
	 * @param locators the locators
	 * @return the web element
	 */
	public static WebElement findFirstVisibleElement(List<By> locators) {
		return findFirstVisibleElement(locators,pageTimeOut);
	}


	/**
	 * Find first visible element.
	 *
	 * @author carlos.cadena
	 * @param locators the locators
	 * @return the web element
	 */
	public static WebElement findFirstVisibleElement(By ... locators) {
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
	public static WebElement findFirstVisibleElement(int timeout, By ... locators) {
		return findFirstVisibleElement(Arrays.asList(locators), timeout);
	}

	
	/**
	 * Accept alert.
	 */
	public static void acceptAlert() {
		WebDriverWait wait = new WebDriverWait(getDriver(), 5000);
		wait.until(ExpectedConditions.alertIsPresent());
		Alert alert = getDriver().switchTo().alert();
		alert.accept();
	}
	
	/**
	 * Click.
	 *
	 * @param element the element
	 */
	public static void click(WebElement element) {
		click(element, pageTimeOut);
	}
	
	/**
	 * Click.
	 *
	 * @param element the element
	 * @param timeOut the time out
	 */
	public static void click(WebElement element, int timeOut) {
		try {
			element.click();
		} catch (StaleElementReferenceException | NoSuchElementException | ElementNotInteractableException e) {
			Utils.waitForElementEnabled(getDriver(), element, timeOut);
			element.click();
		}
	}
	
	/**
	 * Gets the window handle.
	 *
	 * @return the window handle
	 */
	public static String getWindowHandle() {
		return getDriver().getWindowHandle();
	}
	
	
	/**
	 * Gets the number of windows.
	 *
	 * @return the number of windows
	 */
	public static int getNumberOfWindows() {
		return getDriver().getWindowHandles().size();
	}
	
	/**
	 * Switch to window.
	 *
	 * @return the string
	 */
	public static String switchToWindow() {
		String windowHandleNewWindow = "";
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Set<String> all = getDriver().getWindowHandles();
		if(getNumberOfWindows() == 2) {
			windowHandleNewWindow = all.stream().filter(x -> !x.equals(getWindowHandle())).findFirst().get();
			getDriver().switchTo().window(windowHandleNewWindow);
			return windowHandleNewWindow;
		}
		else {
			throw new NoSuchWindowException("There are not two windows/tabs opened");
		}
		
	}
	
	/**
	 * Switch to window.
	 *
	 * @param windowHandle the window handle
	 */
	public static void switchToWindow(String windowHandle) {
		Set<String> all = getDriver().getWindowHandles();
		Optional<String> optional = all.stream().filter(x -> x.equals(windowHandle)).findFirst();
		if(optional.isPresent()) {
			getDriver().switchTo().window(windowHandle);
		}
		else {
			throw new NoSuchWindowException("Window handle" + windowHandle + " was not found to make the switch");
		}
		
	}
	
	/**
	 * Click javascript.
	 *
	 * @param element the element
	 */
	public static void clickJavascript(WebElement element) {
		JavascriptExecutor executor = (JavascriptExecutor) getDriver();
		executor.executeScript("arguments[0].click();", element);
	}
	
	/**
	 * Scroll to element view.
	 *
	 * @param element the element
	 */
	public static void scrollToElementView(WebElement element) {
		((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView(true);", element);
	}
	
	/**
	 * Switch to frame.
	 *
	 * @param frameElement the frame element
	 */
	public static void switchToFrame(WebElement frameElement) {
		 getDriver().switchTo().frame(frameElement);
	}
	
	/**
	 * Switch to default content.
	 */
	public static void switchToDefaultContent() {
		 getDriver().switchTo().defaultContent();
	}
	
	/**
	 * Write.
	 *
	 * @param element the element
	 * @param text the text
	 * @param timeOut the time out
	 */
	public static void write(WebElement element, String text, int timeOut) {
		try {
			element.sendKeys(text);
		} catch (StaleElementReferenceException | NoSuchElementException | InvalidElementStateException e) {
			Utils.waitForElementVisibility(getDriver(), element, timeOut);
			element.sendKeys(text);
		}
	}

	/**
	 * Write.
	 *
	 * @param element the element
	 * @param text the text
	 */
	public static void write(WebElement element, String text) {
		write(element, text, pageTimeOut);
	}
	
	//region Keyboard Methods

	/**
	 * Press keyboard keys.
	 *
	 * @author carlos.cadena
	 * @param keys the keys
	 */
	public static void pressKeyboardKeys(String keys) {
		WebDriverUtils.pressKeyboardKeys((AppiumDriver<WebElement>) webDriver.get(), keys);
	}

	/**
	 * Hide keyboard.
	 *
	 * @author carlos.cadena
	 */
	public static void hideKeyboard() {
		WebDriverUtils.hideKeyboard((AppiumDriver<WebElement>) webDriver.get());

	}
	
	/**
	 * Press key for Android.
	 *
	 * @author carlos.cadena
	 * @param key the key
	 */
	public static void pressKeyAndroid(AndroidKey key) {
		WebDriverUtils.pressKey((AppiumDriver) webDriver.get(), key);
	}


	//endregion
	
	/**
	 * Tap Android native Back button.
	 *
	 * @author carlos.cadena
	 *
	 */
	public static void tapAndroidBackButton() {
		WebDriverUtils.tapAndroidBackButton((AppiumDriver) webDriver.get());
	}

	/**
	 * Tap Android native Back button.
	 *
	 * @author carlos.cadena
	 *
	 */
	public static void tapAndroidHomeButton() {
		WebDriverUtils.tapAndroidHomeButton((AppiumDriver) webDriver.get());
	}

	/**
	 * Tap Android native Enter button.
	 *
	 * @author carlos.cadena
	 *
	 */
	public static void tapAndroidEnterButton() {
		WebDriverUtils.tapAndroidEnterButton((AppiumDriver) webDriver.get());
	}

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
		return WebDriverUtils.switchToContext((AppiumDriver<WebElement>) webDriver.get(), contextName, timeout);
	}

	/**
	 * Switch to first web context found.
	 *
	 * @author carlos.cadena
	 * @param timeout the timeout
	 * @return true, if successful
	 */
	public static boolean switchToFirstWebContextFound(int timeout) {
		return WebDriverUtils.switchToFirstWebContextFound((AppiumDriver<WebElement>) webDriver.get(), timeout);
	}
	
	//endregion

	//region Toggle Methods

	/**
	 * Toggle airplane mode.
	 */
	public static void toggleAirplaneMode() {
		WebDriverUtils.toggleAirplaneMode((AppiumDriver<WebElement>) webDriver.get(), isAndroidExecution());
	}

	/**
	 * Toggle data.
	 */
	public static void toggleData() {
		if (isAndroidExecution()) {
			WebDriverUtils.toogleData((AppiumDriver<WebElement>) webDriver.get(), isAndroidExecution());
		}
	}


	/**
	 * Toggle location.
	 */
	public static void toggleLocation() {
		if (isAndroidExecution()) {
			WebDriverUtils.toggleLocation((AppiumDriver<WebElement>) webDriver.get(), isAndroidExecution());
		}
	}

	/**
	 * Toggle wifi.
	 */
	public static void toggleWifi() {
		WebDriverUtils.toggleWifi((AppiumDriver<WebElement>) webDriver.get(), isAndroidExecution());
	}

	//endregion
	





}