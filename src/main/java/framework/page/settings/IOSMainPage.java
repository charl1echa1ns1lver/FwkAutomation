package framework.page.settings;

import framework.base.AppiumDriverFacade;
import framework.base.MobileBasePage;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.By;

/**
 * The Class iOSMainPage.
 * <br><br><img src="{@docRoot}/framework/doc-files/iOSMainPage.png" height=300>
 */
public class IOSMainPage extends MobileBasePage {

	/** The Constant MAIN_LOCATOR. */
	private static final String MAIN_LOCATOR_IOS = "type == 'XCUIElementTypeApplication' AND name == 'Settings'";

	/** The plane mode switch. */
	@iOSXCUITFindBy(iOSNsPredicate = "type == 'XCUIElementTypeSwitch' AND name == 'Modo Avión'")
	private MobileElement planeModeSwitch;

	/** The Wi-Fi option. */
	@iOSXCUITFindBy(iOSNsPredicate = "type == 'XCUIElementTypeCell' AND name == 'Wi-Fi'")
	private MobileElement wifiOption;

	public IOSMainPage() {
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
		return MobileBy.iOSNsPredicateString(MAIN_LOCATOR_IOS);
	}

	/**
	 * Tap on the plane mode switch.
	 *
	 * @author carlos.cadena
	 */
	public void tapOnPlaneModeSwitch(){
		AppiumDriverFacade.tap(planeModeSwitch);
	}

	/**
	 * Tap on the wifi option.
	 *
	 * @author carlos.cadena
	 */
	public IOSWifiPage tapOnWifiOption(){
		AppiumDriverFacade.tap(wifiOption);
		return new IOSWifiPage();
	}

}
