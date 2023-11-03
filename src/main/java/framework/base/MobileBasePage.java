package framework.base;

import framework.report.Log;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.PageFactory;

import static framework.base.AppiumDriverFacade.appiumDriver;

/**
 * The Class MobileBasePage that provides the main definition for a Page Class.
 */
public abstract class MobileBasePage {

    /** The main locator. */
    private By mainLocator;
    
	/** The page error. */
	private final String pageError = "Page did not load after waiting for " + AppiumDriverFacade.pageTimeOut
			+ " seconds for main element to be present";
    
	/**
	 * Set locator strategy and start logger.
	 *
	 * @author carlos.cadena
	 */
    private void setLocatorStrategyAndStartLogger() {
    	  if(FrameworkProperties.getPlatformName().equalsIgnoreCase("Android")) {
              mainLocator = setMainLocatorAndroid();
          }
          else if(FrameworkProperties.getPlatformName().equalsIgnoreCase("iOS")) {
              mainLocator = setMainLocatorIos();
          }
          else {
              throw new IllegalArgumentException("Valid values for platform name on config.properties are 'Android' or 'iOS'");
          }
    	Log.logger = LogManager.getLogger(getClass());

    }
    

	/**
	 * Check main candidate element.
	 *
	 * @author carlos.cadena
	 */
    private void checkMainElement() {
		if (AppiumDriverFacade.isElementPresent(mainLocator)) {
			PageFactory.initElements(new AppiumFieldDecorator(appiumDriver.get()), this);
		} else {
			Log.testFail(pageError);
			throw new NoSuchElementException(pageError);
		}
    }

	/**
	 * Instantiates a new base page.
	 *
	 * @throws IllegalArgumentException the illegal argument exception if platform property was not provided properly,
	 * NoSuchElementException if main element was not found
	 * @throws NoSuchElementException the no such element exception
	 */
	protected MobileBasePage() throws IllegalArgumentException, NoSuchElementException{
		setLocatorStrategyAndStartLogger();
		checkMainElement();
	}

	/**
	 * Instantiates a new base page by also first removing full screen.
	 *
	 * @param removeFullScreen the remove full screen
	 * @throws IllegalArgumentException the illegal argument exception if platform property was not provided properly,
	 * NoSuchElementException if main element was not found
	 * @throws NoSuchElementException the no such element exception
	 */
	protected MobileBasePage(boolean removeFullScreen) throws IllegalArgumentException, NoSuchElementException{
		setLocatorStrategyAndStartLogger();
		if(removeFullScreen) {
			if(FrameworkProperties.getPlatformName().equalsIgnoreCase("Android")) {
				removeFullScreenAndroid();
			}
		}
		checkMainElement();
	}

    /**
     * Sets the main locator android.
     *
     * @return the by
     */
    public abstract By setMainLocatorAndroid();

    /**
     * Sets the main locator ios.
     *
     * @return the by
     */
    public abstract By setMainLocatorIos();

    /**
     * Tap on android back button.
     */
    public void tapOnAndroidBackButton(){
        AppiumDriverFacade.tapAndroidBackButton();
    }

    /**
     * Tap on android back button.
     *
     * @param <T> the generic type
     * @param returnPage the return page
     * @return Page class instantiated
     * @throws InstantiationException the instantiation exception
     * @throws IllegalAccessException the illegal access exception
     */
	public <T extends MobileBasePage> T tapOnAndroidBackButton(Class<T> returnPage) throws ReflectiveOperationException {
        AppiumDriverFacade.tapAndroidBackButton();
        Log.testStep("Hago tap en Android Back Button");
		return returnPage.getDeclaredConstructor().newInstance();
    }
    
    /**
     * Removes the full screen android.
     */
    private void removeFullScreenAndroid(){
    	By locatorOK = By.id("android:id/ok");
    	if(AppiumDriverFacade.isElementPresent(locatorOK, 15)){
			framework.base.AppiumDriverFacade.findElement(locatorOK).click();
    	}
    }

    /**
     * Press on an specific keyboard key.
     *
     * @param keys the keys
     */
    public void pressKeyboardKeys(String keys){
        AppiumDriverFacade.pressKeyboardKeys(keys);
    }

    /**
     * Hides keyboard
     */
    public void hideKeyboard(){
        AppiumDriverFacade.hideKeyboard();
    }

	/**
	 * Toggle airplanemode
	 */
    public void toggleAirplaneMode(){ AppiumDriverFacade.toggleAirplaneMode(); }

	/**
	 * Toggle data
	 */
	public void toggleData(){ AppiumDriverFacade.toggleData(); }

	/**
	 * Toggle location
	 */
	public void toggleLocation(){ AppiumDriverFacade.toggleLocation(); }

	/**
	 * Toggle wifi
	 */
	public void toggleWifi(){ AppiumDriverFacade.toggleWifi(); }

}
