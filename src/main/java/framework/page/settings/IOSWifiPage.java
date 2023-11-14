package framework.page.settings;

import framework.base.AppiumDriverFacade;
import framework.base.MobileBasePage;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * The Class iOSWifiPage access trow @iOSMainPage by making a tap on the Wi-Fi option.
 * <br><br><img src="{@docRoot}/framework/doc-files/iOSWifiPage.png" height=300>
 */
public class IOSWifiPage extends MobileBasePage {

	/** The Constant MAIN_LOCATOR. */
	private static final String MAIN_LOCATOR_IOS = "type == 'XCUIElementTypeNavigationBar' AND name == 'Wi-Fi'";

	/** The Wi-Fi switch. */
	@iOSXCUITFindBy(iOSNsPredicate = "type == 'XCUIElementTypeSwitch' AND name == 'Wi-Fi'")
	private WebElement wifiSwitch;

	public IOSWifiPage() {
		super();
	}

	/* (non-Javadoc)
	 * @see framework.base.BasePage#setMainLocatorAndroid()
	 */
	@Override
	public By setMainLocatorAndroid() {
		return null;
	}

	/* (non-Javadoc)
	 * @see framework.base.BasePage#setMainLocatorIos()
	 */
	@Override
	public By setMainLocatorIos() {
		return AppiumBy.iOSNsPredicateString(MAIN_LOCATOR_IOS);
	}

	/**
	 * Tap on the Wi-Fi switch.
	 *
	 * @author carlos.cadena
	 */
	public void tapOnWifiSwitch(){
		AppiumDriverFacade.tap(wifiSwitch);
	}

}
