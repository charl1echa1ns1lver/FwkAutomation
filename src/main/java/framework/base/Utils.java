package framework.base;

import com.google.common.base.Function;

import framework.report.Log;
import framework.test.TestUtils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;
import java.util.List;

import static framework.base.AppiumDriverFacade.pageTimeOut;

/**
 * The Class Utils.
 */
public class Utils {

	/**
	 * The Enum MovementsV.
	 */
	public enum MovementsV {UP, DOWN}
	
	/**
	 * The Enum MovementsH.
	 */
	public enum MovementsH {LEFT, RIGHT}

	//region Find Element Methods
	
	/**
	 * Finds element by selector with timeout and evaluating visibility as optional .
	 *
	 * @author carlos.cadena
	 * @param <T> the generic type
	 * @param driver the driver
	 * @param selector the selector
	 * @param timeOut the time out
	 * @param visibility the visibility
	 * @return {@link WebElement}
	 */
	public static <T extends WebElement> T findElement(WebDriver driver, By selector, int timeOut, boolean visibility) {
		if(visibility) {
			waitForElementVisibility(driver, selector, timeOut);
		}
		else {
			waitForElementPresence(driver, selector, timeOut);
		}
		return driver.findElement(selector);
	}
	
	/**
	 * Finds element inside a container by selector with timeout and evaluating visibility as optional .
	 *
	 * @author carlos.cadena
	 * @param <T> the generic type
	 * @param container the container
	 * @param selector the selector
	 * @param timeOut the time out
	 * @param visibility the visibility
	 * @return {@link T extends WebElement}
	 */
	public static <T extends WebElement> T findElement(T container, By selector, int timeOut, boolean visibility) {
		if (visibility) {
			waitForElementVisibility(container, selector, timeOut);
		} else {
			waitForElementPresence(container, selector, timeOut);
		}
		return container.findElement(selector);
	}

	
	/**
	 * Finds elements by selector with timeout and evaluating visibility as optional .
	 *
	 * @author carlos.cadena
	 * @param driver the driver
	 * @param selector the selector
	 * @param timeOut the time out
	 * @param visibility the visibility
	 * @return {@link List of WebElement} a List
	 */
	public static List<WebElement> findElements(WebDriver driver, By selector, int timeOut, boolean visibility) {
		if(visibility) {
			waitForAllElementsVisibility(driver, selector, timeOut);
		}
		else {
			waitForAllElementsPresence(driver, selector, timeOut);
		}
		return driver.findElements(selector);
	}
	
	
	/**
	 * Finds elements inside a container by selector with timeout and evaluating visibility as optional .
	 *
	 * @author carlos.cadena
	 * @param <T> the generic type
	 * @param container the container
	 * @param selector the selector
	 * @param timeOut the time out
	 * @param visibility the visibility
	 * @return {@link List of T} a List
	 */
	public static <T extends WebElement> List<T> findElements(T container, By selector, int timeOut, boolean visibility) {
		if(visibility) {
			waitForAllElementsVisibility(container, selector, timeOut);
		}
		else {
			waitForAllElementsPresence(container, selector, timeOut);
		}
		return container.findElements(selector);
	}

	/**
	 * Wait for element visibility.
	 *
	 * @author carlos.cadena
	 * @param driver the driver
	 * @param element the element
	 */
	public static void waitForElementVisibility(WebDriver driver, WebElement element) {
		waitForElementVisibility(driver, element, pageTimeOut);
	}

	//endregion

    //region Wait Methods

	/**
	 * Wait for element visibility with timeout.
	 *
	 * @param driver the driver
	 * @param element the element
	 * @param timeOut the time out
	 */
	public static void waitForElementVisibility(WebDriver driver, WebElement element, int timeOut) {
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeOut))
				.ignoring(NoSuchElementException.class).ignoring(StaleElementReferenceException.class)
				.ignoring(ElementNotVisibleException.class).ignoring(WebDriverException.class);
		wait.until(ExpectedConditions.visibilityOf(element));
	}
	
	/**
	 * Wait for element visibility.
	 *
	 * @param <T> the generic type
	 * @param container the container
	 * @param locator the locator
	 * @param timeOut the time out
	 */
	public static <T extends WebElement> void waitForElementVisibility(T container, By locator, int timeOut) {
		FluentWait<WebElement> wait = new FluentWait<WebElement>(container).withTimeout(Duration.ofSeconds(timeOut))
				.ignoring(NoSuchElementException.class).ignoring(StaleElementReferenceException.class)
				.ignoring(ElementNotVisibleException.class);
		wait.until(new Function<WebElement,Boolean>() {
            @Override
            public Boolean apply(WebElement arg) {
            	return arg.findElement(locator).isDisplayed();
            }
        });
	}

	/**
	 * Wait for element visibility.
	 *
	 * @param driver the driver
	 * @param elements the elements
	 */
	public static void waitForAllElementsVisibility(WebDriver driver, List<? extends WebElement> elements) {
		waitForAllElementsVisibility(driver, elements, pageTimeOut);
	}

	/**
	 * Wait for element visibility.
	 *
	 * @param driver the driver
	 * @param elements the elements
	 * @param timeOut the time out
	 */
	public static void waitForAllElementsVisibility(WebDriver driver, List<? extends WebElement> elements, int timeOut) {
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeOut))
				.ignoring(NoSuchElementException.class).ignoring(StaleElementReferenceException.class)
				.ignoring(ElementNotVisibleException.class).ignoring(WebDriverException.class);
		for (WebElement element : elements) {
			wait.until(ExpectedConditions.visibilityOf(element));
		}
	}
	
	/**
	 * Wait for all elements visibility.
	 *
	 * @param driver the driver
	 * @param locator the locator
	 * @param timeOut the time out
	 */
	public static void waitForAllElementsVisibility(WebDriver driver, By locator, int timeOut) {
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeOut))
				.ignoring(NoSuchElementException.class).ignoring(StaleElementReferenceException.class)
				.ignoring(ElementNotVisibleException.class).ignoring(WebDriverException.class);
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
	}
	
	/**
	 * Wait for all elements visibility.
	 *
	 * @param <T> the generic type
	 * @param container the container
	 * @param locator the locator
	 * @param timeOut the time out
	 */
	public static <T extends WebElement> void waitForAllElementsVisibility(T container, By locator, int timeOut) {
		Wait<WebElement> wait = new FluentWait<WebElement>(container).withTimeout(Duration.ofSeconds(timeOut))
				.ignoring(NoSuchElementException.class).ignoring(StaleElementReferenceException.class)
				.ignoring(ElementNotVisibleException.class).ignoring(WebDriverException.class);
		wait.until(new Function<WebElement, Boolean>() {
			@Override
			public Boolean apply(WebElement arg) {
				List<? extends WebElement> elements = arg.findElements(locator);
				for (WebElement e : elements) {
					if (!e.isDisplayed()) {
						return Boolean.FALSE;
					}
				}
				return Boolean.TRUE;
			}
		});
	}

	/**
	 * Wait for element visibility by index.
	 *
	 * @param driver the driver
	 * @param elements the elements
	 * @param index the index
	 */
	public static void waitForElementVisibilityByIndex(WebDriver driver, List<WebElement> elements, int index) {
		waitForElementVisibilityByIndex(driver, elements, index, pageTimeOut);
	}

	/**
	 * Wait for element visibility by index.
	 *
	 * @param driver the driver
	 * @param elements the elements
	 * @param index the index
	 * @param timeOut the time out
	 */
	public static void waitForElementVisibilityByIndex(WebDriver driver, List<? extends WebElement> elements, int index,
			int timeOut) {
		if(elements != null && !elements.isEmpty() && index < elements.size()) {
			Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeOut))
					.ignoring(NoSuchElementException.class).ignoring(StaleElementReferenceException.class)
					.ignoring(ElementNotVisibleException.class).ignoring(WebDriverException.class);
			wait.until(ExpectedConditions.visibilityOf(elements.get(index)));
			}
		else {
			Log.logger.debug("List of elements is null, empty or does not have an element on index '" + index + "'");
			throw new NoSuchElementException("The element is not visible ");
		}
		
	}

	/**
	 * Wait for element visibility.
	 *
	 * @param driver the driver
	 * @param locator the locator
	 */
	public static void waitForElementVisibility(WebDriver driver, By locator) {
		waitForElementVisibility(driver, locator, pageTimeOut);
	}

	/**
	 * Wait for element visibility.
	 *
	 * @param driver the driver
	 * @param locator the locator
	 * @param timeOut the time out
	 */
	public static void waitForElementVisibility(WebDriver driver, By locator, int timeOut) {
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeOut))
				.ignoring(NoSuchElementException.class).ignoring(StaleElementReferenceException.class)
				.ignoring(ElementNotVisibleException.class).ignoring(WebDriverException.class);
		wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}
	
	

	/**
	 * Wait for element attribute to be present.
	 *
	 * @param driver the driver
	 * @param locator the locator
	 * @param attribute the attribute
	 * @param value the value
	 * @param timeOut the time out
	 */
	public static void waitForElementAttributeToBePresent(WebDriver driver, By locator, String attribute, String value, int timeOut) {
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeOut))
				.ignoring(NoSuchElementException.class).ignoring(StaleElementReferenceException.class)
				.ignoring(ElementNotVisibleException.class).ignoring(WebDriverException.class);
		wait.until(ExpectedConditions.attributeToBe(locator, attribute, value));
	}
	
	
	/**
	 * Wait for element attribute to be present.
	 *
	 * @param <T> the generic type
	 * @param driver the driver
	 * @param element the element
	 * @param attribute the attribute
	 * @param value the value
	 * @param timeOut the time out
	 */
	public static <T extends WebElement> void waitForElementAttributeToBePresent(WebDriver driver, T element, String attribute, String value, int timeOut) {
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeOut))
				.ignoring(NoSuchElementException.class).ignoring(StaleElementReferenceException.class)
				.ignoring(ElementNotVisibleException.class).ignoring(WebDriverException.class);
		wait.until(ExpectedConditions.attributeToBe(element, attribute, value));
	}
	
	
	

	/**
	 * Wait for element attribute to be present for an element inside a container.
	 *
	 * @param <T> the generic type
	 * @param driver the driver
	 * @param container the container
	 * @param locator the locator
	 * @param attribute the attribute
	 * @param value the value
	 * @param timeOut the time out
	 */
	public static <T extends WebElement> void waitForElementAttributeToBePresent(WebDriver driver, T container, By locator, String attribute, String value, int timeOut) {
		Wait<WebElement> wait = new FluentWait<WebElement>(container).withTimeout(Duration.ofSeconds(timeOut))
				.ignoring(NoSuchElementException.class).ignoring(StaleElementReferenceException.class)
				.ignoring(ElementNotVisibleException.class).ignoring(WebDriverException.class);
		wait.until(new Function<WebElement,Boolean>() {
            @Override
            public Boolean apply(WebElement arg) {
            	String currentValue = arg.getAttribute(attribute);
                if (currentValue == null || currentValue.isEmpty()) {
                  currentValue = arg.getCssValue(attribute);
                }
                return value.equals(currentValue);
            }
        });
	}
	
	
	/**
	 * Wait for element attribute to be present by index.
	 *
	 * @param <T> the generic type
	 * @param driver the driver
	 * @param elements the elements
	 * @param index the index
	 * @param attribute the attribute
	 * @param value the value
	 * @param timeOut the time out
	 */
	public static <T extends WebElement> void waitForElementAttributeToBePresentByIndex(WebDriver driver, List<T> elements, int index, String attribute, String value, int timeOut) {
		if(elements != null && !elements.isEmpty() && index < elements.size()) {
			Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeOut))
					.ignoring(NoSuchElementException.class).ignoring(StaleElementReferenceException.class)
					.ignoring(ElementNotVisibleException.class).ignoring(WebDriverException.class);
			wait.until(ExpectedConditions.attributeToBe(elements.get(index), attribute, value));
			}
		else {
			Log.logger.debug("List of elements is null, empty or does not have an element on index '" + index + "'");
			throw new NoSuchElementException("The element is not visible ");
		}
		
	}

	/**
	 * Wait for element enabled.
	 *
	 * @param driver the driver
	 * @param element the element
	 */
	public static void waitForElementEnabled(WebDriver driver, WebElement element) {
		waitForElementEnabled(driver, element, pageTimeOut);
	}

	/**
	 * Wait for element enabled.
	 *
	 * @param driver the driver
	 * @param element the element
	 * @param timeOut the time out
	 */
	public static void waitForElementEnabled(WebDriver driver, WebElement element, int timeOut) {
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeOut))
				.ignoring(NoSuchElementException.class).ignoring(StaleElementReferenceException.class)
				.ignoring(ElementNotSelectableException.class).ignoring(ElementNotVisibleException.class)
				.ignoring(WebDriverException.class);
		wait.until(ExpectedConditions.elementToBeClickable(element));
	}

	/**
	 * This method is used for an Hybrid app.
	 *
	 * @param driver the driver
	 * @param locator the locator
	 */
	public static void waitForElementPresence(WebDriver driver, By locator) {
		waitForElementPresence(driver, locator, pageTimeOut);
	}

	/**
	 * Wait for element presence.
	 *
	 * @param driver the driver
	 * @param locator the locator
	 * @param timeOut the time out
	 */
	public static void waitForElementPresence(WebDriver driver, By locator, int timeOut) {
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeOut))
				.ignoring(NoSuchElementException.class).ignoring(StaleElementReferenceException.class).ignoring(WebDriverException.class);
		wait.until(ExpectedConditions.presenceOfElementLocated(locator));
	}
	
	/**
	 * Wait for element presence.
	 *
	 * @param <T> the generic type
	 * @param container the container
	 * @param locator the locator
	 * @param timeOut the time out
	 */
	public static <T extends WebElement> void waitForElementPresence(T container, By locator, int timeOut) {
		Wait<WebElement> wait = new FluentWait<WebElement>(container).withTimeout(Duration.ofSeconds(timeOut))
				.ignoring(NoSuchElementException.class).ignoring(StaleElementReferenceException.class).ignoring(WebDriverException.class);
		wait.until(new Function<WebElement,WebElement>() {
            @Override
            public WebElement apply(WebElement arg) {
                return arg.findElement(locator);
            }
        });
	}
	
	/**
	 * Wait for all elements presence.
	 *
	 * @param driver the driver
	 * @param locator the locator
	 * @param timeOut the time out
	 */
	public static void waitForAllElementsPresence(WebDriver driver, By locator, int timeOut) {
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeOut))
				.ignoring(NoSuchElementException.class).ignoring(StaleElementReferenceException.class).ignoring(WebDriverException.class);
		wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
	}
	
	/**
	 * Wait for all elements presence.
	 *
	 * @param <T> the generic type
	 * @param container the container
	 * @param locator the locator
	 * @param timeOut the time out
	 */
	public static <T extends WebElement> void waitForAllElementsPresence(T container, By locator, int timeOut) {
		Wait<WebElement> wait = new FluentWait<WebElement>(container).withTimeout(Duration.ofSeconds(timeOut))
				.ignoring(NoSuchElementException.class).ignoring(StaleElementReferenceException.class).ignoring(WebDriverException.class);
		wait.until(new Function<WebElement, List<WebElement>>() {
			@Override
			public List<WebElement> apply(WebElement arg) {
				return arg.findElements(locator);
			}
		});
	}

	//endregion

    //region Element Visibility Methods
	
	/**
	 * Checks if an element is visible inside a container explicitly.
	 * 
	 * @author carlos.cadena
	 * @param <T> the generic type
	 * @param container the container
	 * @param locator the locator
	 * @param timeOut the time out
	 * @return 
	 * @return true, if is element visible
	 */
	public static <T extends WebElement> boolean areElementsVisible(WebDriver driver, List<T> elements, int timeOut) {
		try {
			return new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeOut))
					.ignoring(NoSuchElementException.class).ignoring(ElementNotVisibleException.class)
					.ignoring(StaleElementReferenceException.class).ignoring(WebDriverException.class)
					.until(new Function<WebDriver, Boolean>() {
						public Boolean apply(WebDriver driver) {
							{
								return elements.stream().allMatch(element -> Utils.isElementVisible(driver, element, timeOut));
							}
						}
					});
		} catch (TimeoutException e) {
			return false;
		}
	}
	
	
	/**
	 * Checks if an element is visible inside a container explicitly.
	 * 
	 * @author carlos.cadena
	 * @param <T> the generic type
	 * @param container the container
	 * @param locator the locator
	 * @param timeOut the time out
	 * @return true, if is element visible
	 */
	public static <T extends WebElement> boolean isElementVisible(T container, By locator, int timeOut) {
		try {
			return new FluentWait<WebElement>(container).withTimeout(Duration.ofSeconds(timeOut))
					.ignoring(NoSuchElementException.class).ignoring(ElementNotVisibleException.class)
					.ignoring(StaleElementReferenceException.class).ignoring(WebDriverException.class)
					.until(new Function<WebElement, Boolean>() {
						public Boolean apply(WebElement arg) {
							{
								return arg.findElement(locator).isDisplayed();
							}
						}
					});
		} catch (TimeoutException e) {
			return false;
		}
	}
	
	
	
	/**
	 * Checks if an element is NOT visible inside a container explicitly.
	 * 
	 * @author carlos.cadena
	 * @param <T> the generic type
	 * @param container the container
	 * @param locator the locator
	 * @param timeOut the time out
	 * @return true, if is element visible
	 */
	public static <T extends WebElement> boolean isElementNotVisible(T container, By locator, int timeOut) {
		try {
			return new FluentWait<WebElement>(container).withTimeout(Duration.ofSeconds(timeOut))
					.ignoring(NoSuchElementException.class).ignoring(ElementNotVisibleException.class)
					.ignoring(StaleElementReferenceException.class).ignoring(WebDriverException.class)
					.until(new Function<WebElement, Boolean>() {
						public Boolean apply(WebElement arg) {
							try {
								return !arg.findElement(locator).isDisplayed();
						}catch( StaleElementReferenceException| ElementNotVisibleException | NoSuchElementException e) {
							return true;
						}
						}
					});
		} catch (TimeoutException e) {
			return false;
		}
	}


	/**
	 * Evaluates if an element is visible explicitly.
	 *
	 * @author carlos.cadena
	 * @param driver the driver
	 * @param element the element
	 * @param timeOut the time out
	 * @return true, if is element visible
	 */
	public static boolean isElementVisible(WebDriver driver, WebElement element, int timeOut) {
		try {
			return new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeOut))
					.ignoring(NoSuchElementException.class).ignoring(ElementNotVisibleException.class)
					.ignoring(StaleElementReferenceException.class).ignoring(WebDriverException.class)
					.until(new Function<WebDriver, Boolean>() {
						public Boolean apply(WebDriver arg) {
							{
								return element.isDisplayed();
							}
						}
					});
		} catch (TimeoutException e) {
			return false;
		}
	}
	
	/**
	 * Evaluates if an element is NOT visible explicitly.
	 *
	 * @author carlos.cadena
	 * @param driver the driver
	 * @param element the element
	 * @param timeOut the time out
	 * @return true, if is element visible
	 */
	public static boolean isElementNotVisible(WebDriver driver, WebElement element, int timeOut) {
		try {
			return new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeOut)).ignoring(WebDriverException.class)
					.until(new Function<WebDriver, Boolean>() {
						public Boolean apply(WebDriver arg) {
							{
								try {
									return !element.isDisplayed();
								}catch( StaleElementReferenceException| ElementNotVisibleException | NoSuchElementException e) {
									return true;
								}
							}
						}
					});
		} catch (TimeoutException e) {
			return false;
		}
	}


	/**
	 * Checks if a list of elements is visible explicitly.
	 *
	 * @param driver the driver
	 * @param elements the elements
	 * @param index the index
	 * @param timeOut the time out
	 * @return true, if is element visible
	 */
	public static boolean isElementVisible(WebDriver driver, List<? extends WebElement> elements, int index, int timeOut) {
		try {
			return new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeOut))
					.ignoring(NoSuchElementException.class).ignoring(ElementNotVisibleException.class)
					.ignoring(StaleElementReferenceException.class).ignoring(WebDriverException.class)
					.until(new Function<WebDriver, Boolean>() {
						public Boolean apply(WebDriver arg) {
							if(elements != null && !elements.isEmpty() && index < elements.size()) {
								return elements.get(index).isDisplayed();
							}
							else {
								Log.logger.debug("List of elements is null, empty or does not have an element on index '" + index + "'");
								return false;
							}
						}
					});
		} catch (TimeoutException e) {
			return false;
		}
	}

	/**
	 * Checks if is element enabled explicitly.
	 *
	 * @param driver the driver
	 * @param element the element
	 * @param timeOut the time out
	 * @return true, if is element enabled
	 */
	public static boolean isElementEnabled(WebDriver driver, WebElement element, int timeOut) {
		try {
			return new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeOut))
					.ignoring(NoSuchElementException.class).ignoring(ElementNotVisibleException.class)
					.ignoring(StaleElementReferenceException.class).ignoring(ElementNotInteractableException.class)
					.ignoring(ElementNotSelectableException.class).ignoring(WebDriverException.class)
					.until(new Function<WebDriver, Boolean>() {
						public Boolean apply(WebDriver arg) {
							return element.isEnabled();
						}
					});
		} catch (TimeoutException e) {
			return false;
		}
	}
	

	/**
	 * Checks if is element enabled explicitly.
	 *
	 * @param driver the driver
	 * @param elements the elements
	 * @param index the index
	 * @param timeOut the time out
	 * @return true, if is element enabled
	 */
	public static boolean isElementEnabled(WebDriver driver, List<? extends WebElement> elements, int index, int timeOut) {
		try {
			return new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeOut))
					.ignoring(NoSuchElementException.class).ignoring(ElementNotVisibleException.class)
					.ignoring(StaleElementReferenceException.class).ignoring(ElementNotVisibleException.class)
					.ignoring(ElementNotSelectableException.class).ignoring(WebDriverException.class)
					.until(new Function<WebDriver, Boolean>() {
						public Boolean apply(WebDriver arg) {
							if(elements != null && !elements.isEmpty() && index < elements.size()) {
								return elements.get(index).isEnabled();
							}
							else {
								Log.logger.debug("List of elements is null, empty or does not have an element on index '" + index +"'");
								return false;
							}
						}
					});
		} catch (TimeoutException e) {
			return false;
		}
	}

	/**
	 * Checks for an element to be visible explicitly.
	 *
	 * @author carlos.cadena
	 * @param driver the driver
	 * @param locator the locator
	 * @param timeOut the time out
	 * @return true if element is present or false otherwise
	 */
	public static boolean isElementVisible(WebDriver driver, By locator, int timeOut) {
		try {
			return new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeOut))
					.ignoring(NoSuchElementException.class).ignoring(ElementNotVisibleException.class)
					.ignoring(StaleElementReferenceException.class).ignoring(WebDriverException.class)
					.until(new Function<WebDriver, Boolean>() {
						public Boolean apply(WebDriver arg) {
							WebElement element = arg.findElement(locator);
							if (element != null) {
								return element.isDisplayed();
							}
							return false;
						}
					});
		} catch (TimeoutException e) {
			return false;
		}
	}
	
	/**
	 * Checks for an element to be NOT visible explicitly.
	 *
	 * @author carlos.cadena
	 * @param driver the driver
	 * @param locator the locator
	 * @param timeOut the time out
	 * @return true if element is present or false otherwise
	 */
	public static boolean isElementNotVisible(WebDriver driver, By locator, int timeOut) {
		try {
			return new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeOut)).ignoring(WebDriverException.class)
					.until(new Function<WebDriver, Boolean>() {
						public Boolean apply(WebDriver arg) {
							try {
								return !arg.findElement(locator).isDisplayed();
						}catch( StaleElementReferenceException| ElementNotVisibleException | NoSuchElementException e) {
							return true;
						}
						}
					});
		} catch (TimeoutException e) {
			return false;
		}
	}

	
	
	

	/**
	 * Checks for an element to be enabled explicitly.
	 *
	 * @author carlos.cadena
	 * @param driver the driver
	 * @param locator the locator
	 * @param timeout the timeout
	 * @return true if element is present or false otherwise
	 */
	public static boolean isElementEnabled(WebDriver driver, By locator, int timeout) {
		try {
			return new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeout))
					.ignoring(NoSuchElementException.class)
					.ignoring(StaleElementReferenceException.class).ignoring(ElementNotSelectableException.class)
					.ignoring(ElementNotVisibleException.class).until(new Function<WebDriver, Boolean>() {
						public Boolean apply(WebDriver arg) {
							WebElement element = arg.findElement(locator);
							if (element != null) {
								return element.isEnabled();
							}
							return false;
						}
					});
		} catch (TimeoutException e) {
			return false;
		}
	}
	
	
	/**
	 * Checks for a presence of an element inside a container explicitly
	 *
	 * @author carlos.cadena
	 * @param driver the driver
	 * @param locator the locator
	 * @param timeout the timeout
	 * @return true if element is present or false otherwise
	 */
	public static boolean isElementPresent(WebElement container, By locator, int timeout) {
		try {
			return new FluentWait<WebElement>(container).withTimeout(Duration.ofSeconds(timeout))
					.ignoring(NoSuchElementException.class).ignoring(StaleElementReferenceException.class)
					.ignoring(WebDriverException.class).until(new Function<WebElement, Boolean>() {
						public Boolean apply(WebElement arg) {
							WebElement element = arg.findElement(locator);
							if (element != null) {
								return true;
							}
							return false;
						}
					});
		} catch (TimeoutException e) {
			return false;
		}
	}

	/**
	 * Checks for a presence of an element explicitly.
	 *
	 * @author carlos.cadena
	 * @param driver the driver
	 * @param locator the locator
	 * @param timeout the timeout
	 * @return true if element is present or false otherwise
	 */
	public static boolean isElementPresent(WebDriver driver, By locator, int timeout) {
		try {
			return new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeout))
					.ignoring(NoSuchElementException.class).ignoring(StaleElementReferenceException.class)
					.ignoring(WebDriverException.class).until(new Function<WebDriver, Boolean>() {
						public Boolean apply(WebDriver arg) {
							WebElement element = arg.findElement(locator);
							if (element != null) {
								return true;
							}
							return false;
						}
					});
		} catch (TimeoutException e) {
			return false;
		}
	}

	
	/**
	 * Checks for an attribute value of an element explicitly.
	 *
	 * @author carlos.cadena
	 * @param driver the driver
	 * @param locator the locator
	 * @param attribute the attribute
	 * @param value the value
	 * @param contains = If true, attribute value will be evaluated as "contained", else as "equals"
	 * @param timeout the timeout
	 * @return true if element is present or false otherwise
	 */
	public static boolean isAttributePresentOnElement(WebDriver driver, By locator, String attribute, String value, boolean contains, int timeout) {
		try {
			return new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeout))
					.ignoring(NoSuchElementException.class).ignoring(StaleElementReferenceException.class)
					.ignoring(ElementNotVisibleException.class).ignoring(WebDriverException.class)
					.until(new Function<WebDriver, Boolean>() {
						public Boolean apply(WebDriver arg) {
							WebElement element = arg.findElement(locator);
							if (element != null) {
								return (contains ? element.getAttribute(attribute).contains(value) : element.getAttribute(attribute).equals(value));
							}
							return false;
						}
					});
		} catch (TimeoutException e) {
			return false;
		}
	}
	
	
	
	/**
	 * Checks for an attribute value of an element inside a container explicitly.
	 *
	 * @author carlos.cadena
	 * @param container the container
	 * @param locator the locator
	 * @param attribute the attribute
	 * @param value the value
	 * @param contains = If true, attribute value will be evaluated as "contained", else as "equals"
	 * @param timeout the timeout
	 * @return true if element is present or false otherwise
	 */
	public static boolean isAttributePresentOnElement(WebElement container, By locator, String attribute, String value, boolean contains, int timeout) {
		try {			
			return new FluentWait<WebElement>(container).withTimeout(Duration.ofSeconds(timeout))
					.ignoring(NoSuchElementException.class).ignoring(ElementNotVisibleException.class)
					.ignoring(StaleElementReferenceException.class).ignoring(WebDriverException.class)
					.until(new Function<WebElement, Boolean>() {
						public Boolean apply(WebElement arg) {
							WebElement element = arg.findElement(locator);
							if (element != null) {
								return (contains ? element.getAttribute(attribute).contains(value) : element.getAttribute(attribute).equals(value));
							}
							return false;
						}
					});
		} catch (TimeoutException e) {
			return false;
		}
	}
	
	
	/**
	 * Checks for an attribute value of an element explicitly.
	 *
	 * @author carlos.cadena
	 * @param driver the driver
	 * @param element the element
	 * @param attribute the attribute
	 * @param value the value
	 * @param contains = If true, attribute value will be evaluated as "contained", else as "equals"
	 * @param timeout the timeout
	 * @return true if element is present or false otherwise
	 */
	public static boolean isAttributePresentOnElement(WebDriver driver, WebElement element, String attribute, String value, boolean contains, int timeout) {
		try {
			return new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeout))
					.ignoring(NoSuchElementException.class).ignoring(StaleElementReferenceException.class)
					.ignoring(ElementNotVisibleException.class).ignoring(WebDriverException.class)
					.until(new Function<WebDriver, Boolean>() {
						public Boolean apply(WebDriver arg) {
								return (contains ? element.getAttribute(attribute).contains(value) : element.getAttribute(attribute).equals(value));
						}
					});
		} catch (TimeoutException e) {
			return false;
		}
	}
	
	
	/**
	 * Checks for an attribute value that should be in all elements explicitly.
	 *
	 * @author carlos.cadena
	 * @param <T> the generic type
	 * @param driver the driver
	 * @param elements the elements
	 * @param attribute the attribute
	 * @param value the value
	 * @param contains = If true, attribute value will be evaluated as "contained", else as "equals"
	 * @param timeout the timeout
	 * @return true if element is present or false otherwise
	 */
	public static <T extends WebElement> boolean isAttributePresentOnAllElements(WebDriver driver, List<T> elements, String attribute, String value, boolean contains, int timeout) {
		try {
			return new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeout))
					.ignoring(NoSuchElementException.class).ignoring(StaleElementReferenceException.class)
					.ignoring(ElementNotVisibleException.class).ignoring(WebDriverException.class)
					.until(new Function<WebDriver, Boolean>() {
						public Boolean apply(WebDriver arg) {
							return elements.stream().allMatch(element ->  contains ? element.getAttribute(attribute).contains(value) : element.getAttribute(attribute).equals(value));
						}
					});
		} catch (TimeoutException e) {
			return false;
		}
	}
	
	
	/**
	 * Checks for an attribute value that should be on an element from a List explicitly.
	 *
	 * @author carlos.cadena
	 * @param <T> the generic type
	 * @param driver the driver
	 * @param elements the elements
	 * @param index the index
	 * @param attribute the attribute
	 * @param value the value
	 * @param contains = If true, attribute value will be evaluated as "contained", else as "equals"
	 * @param timeout the timeout
	 * @return true if element is present or false otherwise
	 */
	public static <T extends WebElement> boolean isAttributePresentOnElementByIndex(WebDriver driver, List<T> elements,  int index, String attribute, String value, boolean contains, int timeout) {
		try {
			return new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeout))
					.ignoring(NoSuchElementException.class).ignoring(StaleElementReferenceException.class)
					.ignoring(ElementNotVisibleException.class).ignoring(WebDriverException.class)
					.until(new Function<WebDriver, Boolean>() {
						public Boolean apply(WebDriver arg) {
							TestUtils.assertListSize(elements, index);
							return contains ? elements.get(index).getAttribute(attribute).contains(value) : elements.get(index).getAttribute(attribute).equals(value);
						}
					});
		} catch (TimeoutException e) {
			return false;
		}
	}



	//endregion

    //region Other Methods


	/**
	 * This method is used to wait until a file is downloaded.
	 *
	 * @param downloadDirectory the download directory
	 */
	public static void waitForFileDownload(String downloadDirectory) {
		// falta Implementacion
	}

	//endregion
}
