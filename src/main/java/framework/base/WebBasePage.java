package framework.base;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.PageFactory;

public abstract class WebBasePage {
	
	private By mainLocator;
	
	public abstract By setMainLocator();

	public WebBasePage() {
		mainLocator = setMainLocator();
		String pageError = "Page did not load after waiting for " + WebDriverFacade.pageTimeOut
				+ " seconds for main element to be present";
		if (WebDriverFacade.isElementPresent(mainLocator)) {
			PageFactory.initElements(WebDriverFacade.getDriver(), this);
		} else {
			System.out.println(pageError);
			throw new NoSuchElementException(pageError);
		}
	}

}