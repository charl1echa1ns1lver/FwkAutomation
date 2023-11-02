package framework.base;

import framework.report.Log;
import org.openqa.selenium.By;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;

/**
 * The Class MobilePageObject, that allows to model a section of a Mobile screen
 *
 * @author carlos.cadena
 */
public class MobilePageObject extends PageObject<AppiumDriver<MobileElement>, MobileElement> {

	/**
	 * Instantiates a new mobile page object.
	 * @author carlos.cadena
	 * @param container the container
	 */
	public MobilePageObject(MobileElement container) {
		super(container);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Instantiates a new mobile page object.
	 * @author carlos.cadena
	 * @param locatorContainer the locator container
	 */
	public MobilePageObject(By locatorContainer) {
		super(AppiumDriverFacade.appiumDriver.get(), locatorContainer, AppiumDriverFacade.pageTimeOut);
	}

	public void vSwipeToContainer(){
		AppiumDriverFacade.vSwipeToElement(container, Utils.MovementsV.DOWN, 5);
		Log.testStep("Hago swipe vertical hasta el contenedor del elemento");
	}

	public void hSwipeToContainer(int switches){
		AppiumDriverFacade.hSwipeFromElement(container, Utils.MovementsH.RIGHT, switches);
		Log.testStep("Hago swipe horizontal hasta el contenedor del elemento");
	}
}
