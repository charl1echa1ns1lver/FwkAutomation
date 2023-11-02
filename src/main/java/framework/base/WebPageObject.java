package framework.base;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


/**
 * The Class WebPageObject, that allows to model a section of a screen
 *
 * @author carlos.cadena
 */
public class WebPageObject extends PageObject<WebDriver, WebElement> {

	/**
	 * Instantiates a new web page object.
	 * @author carlos.cadena
	 * @param container the container
	 */
	public WebPageObject(WebElement container) {
		super(container);
	}
	
	/**
	 * Instantiates a new web page object.
	 * @author carlos.cadena
	 * @param locatorContainer the locator container
	 */
	public WebPageObject(By locatorContainer) {
		super(WebDriverFacade.getDriver(), locatorContainer, WebDriverFacade.pageTimeOut);
	}
}
