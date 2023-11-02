/**
 * 
 */
package framework.base;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

// TODO: Auto-generated Javadoc
/**
 * The abstract Class PageObject, that allows to model a section of a screen.
 *
 * @author carlos.cadena
 * @param <T> the generic type
 * @param <U> the generic type
 */
public abstract class PageObject<T extends WebDriver, U extends WebElement> {

	/** The locator container. */
	protected By locatorContainer;
	
	/** The container. */
	protected U container;
	
	/**
	 * Gets the container.
	 *
	 * @return the container
	 */
	public U getContainer() {
		return container;
	}
	
	/**
	 * Instantiates a new page object.
	 * @author carlos.cadena
	 * @param container the container
	 */
	public PageObject(U container) {
		this.container = container;
	}
	
	/**
	 * Instantiates a new page object.
	 * @author carlos.cadena
	 * @param driver the driver
	 * @param locator the locator
	 * @param timeout the timeout
	 */
	public PageObject(T driver, By locator, int timeout) {
		this.locatorContainer = locator;
		container = Utils.findElement(driver, locatorContainer, timeout, true);
	}
}
