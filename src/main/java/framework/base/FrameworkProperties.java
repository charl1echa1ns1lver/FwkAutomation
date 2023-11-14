package framework.base;

import framework.report.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * The Class FrameworkProperties.
 */
public class FrameworkProperties {

	/** The props. */
	public static Properties props = FrameworkProperties.GetConfig();
	
	/** The apiKeys. */
	public static Properties apiKeys = FrameworkProperties.GetApiKeys();

	
	/** The packages. */
	public static Properties packages = FrameworkProperties.getPackages();

	// region Initialize Properties

	/**
	 * Singleton to initialize config.properties
	 *
	 * @author carlos.cadena
	 * @return the mapped properties included in the file
	 */
	private static Properties GetConfig() {
		if (props == null) {
			props = initializeProperties("config.properties");
		}
		return props;

	}
	
	/**
	 * Singleton to initialize apiKeys.properties
	 *
	 * @author carlos.cadena
	 * @return the mapped properties included in the file
	 */
	private static Properties GetApiKeys() {
		if (apiKeys == null) {
			apiKeys = initializeProperties("apiKeys.properties");
		}
		return apiKeys;
	}
	
	/**
	 * Singleton to initialize packages.properties
	 *
	 * @author carlos.cadena
	 * @return the mapped properties included in the file
	 */
	private static Properties getPackages() {
		if (packages == null) {
			packages = initializeProperties("packages.properties");
		}
		return packages;

	}

	/**
	 * Method to initialize property files located in default properties location.
	 *
	 * @author carlos.cadena
	 * @param fileName the file name
	 * @return the properties object
	 */
	private static Properties initializeProperties(String fileName) {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream("src/test/resources/" + fileName);
			// load a properties file
			prop.load(input);
		} catch (IOException ex) {
			Log.logger.fatal("Loading properties file '" + fileName + "' failed, check configuration file location");
		}
		return prop;
	}

	// endregion

	// region General Config

	/**
	 * Return TIMEOUT environment variable, and if null 'timeout' property value is
	 * returned.
	 *
	 * @author carlos.cadena
	 * @return the property value
	 */
	public static String getTimeout() {
		if (System.getProperty("TIMEOUT") == null) {
			return props.getProperty("timeout");
		}
		return System.getProperty("TIMEOUT");
	}
	

	/**
	 * Return IOS_UDID variable, and if null 'ios.udid' property
	 * value is returned
	 *
	 * @author carlos.cadena
	 * @return the property value
	 */
	public static String getIOSUdid() {
		if (System.getProperty("IOS_UDID") == null) {
			return props.getProperty("ios.udid");
		}
		return System.getProperty("IOS_UDID");
	}
	

	/**
	 * Return IOS_XCODE_ORG_ID variable, and if null 'ios.xcodeOrgId' property
	 * value is returned
	 *
	 * @author carlos.cadena
	 * @return the property value
	 */
	public static String getXcodeOrgId() {
		if (System.getProperty("IOS_XCODE_ORG_ID") == null) {
			return props.getProperty("ios.xcodeOrgId");
		}
		return System.getProperty("IOS_XCODE_ORG_ID");
	}
	
	/**
	 * Return RETRY_TIMES variable, and if null 'retry.times' property
	 * value is returned
	 *
	 * @author carlos.cadena
	 * @return the property value
	 */
	public static String getRetryTimes() {
		// TODO Auto-generated method stub
		if (System.getProperty("RETRY_TIMES") == null) {
			return props.getProperty("retry.times");
		}
		return System.getProperty("RETRY_TIMES");
	}

	/**
	 * Return OLD_VERSION variable, and if null 'old.version' property
	 * value is returned
	 *
	 * @author carlos.cadena
	 * @return the property value
	 */
	public static String getOldVersion() {
		// TODO Auto-generated method stub
		if (System.getProperty("OLD_VERSION") == null) {
			return props.getProperty("old.version");
		}
		return System.getProperty("OLD_VERSION");
	}

	// endregion

	// region Local Config

	/**
	 * Return LOCAL environment variable, and if null 'local' property value is
	 * returned.
	 *
	 * @author carlos.cadena
	 * @return the property value
	 */
	public static String getLocal() {
		if (System.getProperty("LOCAL") == null) {
			return props.getProperty("local");
		}
		return System.getProperty("LOCAL");
	}

	/**
	 * Return LANGUAGE environment variable, and if null 'local.language' property
	 * value is returned
	 *
	 * @author carlos.cadena
	 * @return the property value
	 */
	public static String getLanguage() {
		if (System.getProperty("LANGUAGE") == null) {
			return props.getProperty("local.language");
		}
		return System.getProperty("LANGUAGE");
	}

	// endregion

	//region Applitools Config

	/**
	 * Return APPLITOOLS_API_KEY variable, and if null 'applitools.apikey' property
	 * value is returned
	 *
	 * @author carlos.cadena
	 * @return the property value
	 */
	public static String getApplitoolsApiKey() {
		// TODO Auto-generated method stub
		if (System.getProperty("APPLITOOLS_API_KEY") == null) {
			return props.getProperty("applitools.apikey");
		}
		return System.getProperty("APPLITOOLS_API_KEY");
	}

	//endregion

	//region Rally Config

	/**
	 * Return LOG_RALLY variable, and if null 'logRally' property value is returned.
	 *
	 * @author carlos.cadena
	 * @return the property value
	 */
	public static String getRallyLogResults() {
		if (System.getProperty("LOG_RALLY") == null) {
			return props.getProperty("rally.log");
		}
		return System.getProperty("LOG_RALLY");
	}
	
	/**
	 * Return LOG_RALLY_PASSED_ONLY variable, and if null 'rally.log.passed.only' property value is returned.
	 *
	 * @author carlos.cadena
	 * @return the property value
	 */
	public static String getRallyLogPassedOnly() {
		if (System.getProperty("LOG_RALLY_PASSED_ONLY") == null) {
			return props.getProperty("rally.log.passed.only");
		}
		return System.getProperty("LOG_RALLY_PASSED_ONLY");
	}

	/**
	 * Return LOG_RALLY variable, and if null 'logRally' property value is returned.
	 *
	 * @author carlos.cadena
	 * @return the property value
	 */
	public static String getVersionBuildNumber() {
		if(System.getProperty("BUILD_NUMBER") == null)
		{
			return props.getProperty("rally.build.number");
		}
		return System.getProperty("BUILD_NUMBER");
	}

	//endregion

	// region SauceLabs Config

	/**
	 * Return iOS APP environment variable
	 * returned.
	 *
	 * @author carlos.cadena
	 * @return the property value
	 */
	public static String getApp() {
		if (System.getProperty("APP") == null) {
			if (getPlatformName().equalsIgnoreCase("Android")) {
				return props.getProperty("sauce.android.app");

			} else {
				return props.getProperty("sauce.ios.app");
			}
		}
		return System.getProperty("APP");
	}

	/**
	 * Return PRIVATE_ONLY environment variable, and if null
	 * 'sauce.privateDevicesOnly' property value is returned
	 *
	 * @author carlos.cadena
	 * @return the property value
	 */
	public static String getPrivateOnly() {
		if (System.getProperty("PRIVATE_ONLY") == null) {
			return props.getProperty("sauce.privateDevicesOnly");
		}
		return System.getProperty("PRIVATE_ONLY");
	}

	/**
	 * Return SAUCE_USER_NAME  variable, and if null 'sauce.username' property value is returned telling if
	 *
	 * @author carlos.cadena
	 * @return the property value
	 */
	public static String getSauceUsername() {
		if(System.getProperty("SAUCE_USER_NAME") == null)
		{
			return props.getProperty("sauce.username");
		}
		return System.getProperty("SAUCE_USER_NAME");
	}

	/**
	 * Return SAUCE_ACCESS_KEY  variable, and if null 'sauce.accesskey' property value is returned telling if
	 *
	 * @author carlos.cadena
	 * @return the property value
	 */
	public static String getSauceAccessKey() {
		if(System.getProperty("SAUCE_ACCESS_KEY") == null)
		{
			return props.getProperty("sauce.access.key");
		}
		return System.getProperty("SAUCE_ACCESS_KEY");
	}

	// endregion

	// region Confluence Config

	/**
	 * Return CONFLUENCE_REPORT environment variable, and if null
	 * 'confluence.report' property value is returned
	 *
	 * @author carlos.cadena
	 * @return the property value
	 */
	public static String getConfluenceReport() {
		if (System.getProperty("CONFLUENCE_REPORT") == null) {
			return props.getProperty("confluence.report");
		}
		return System.getProperty("CONFLUENCE_REPORT");
	}

	/**
	 * Return CONFLUENCE_SPACE environment variable, and if null 'confluence.space'
	 * property value is returned
	 *
	 * @author carlos.cadena
	 * @return the property value
	 */
	public static String getConfluenceSpace() {
		if (System.getProperty("CONFLUENCE_SPACE") == null) {
			return props.getProperty("confluence.space");
		}
		return System.getProperty("CONFLUENCE_SPACE");
	}

	/**
	 * Return CONFLUENCE_PAGE environment variable, and if null 'confluence.page'
	 * property value is returned
	 *
	 * @author carlos.cadena
	 * @return the property value
	 */
	public static String getConfluencePage() {
		if (System.getProperty("CONFLUENCE_PAGE") == null) {
			return props.getProperty("confluence.page");
		}
		return System.getProperty("CONFLUENCE_PAGE");
	}

	// endregion

	//region Web Config

	/**
	 * Return BASE_URL environment variable, and if null 'base.url' property value is returned
	 *
	 * @author carlos.cadena
	 * @return the property value
	 */
	public static String getBaseUrl() {
		if(System.getProperty("BASE_URL") == null)
		{
			return props.getProperty("base.url");
		}
		return System.getProperty("BASE_URL");
	}

	/**
	 * Return BROWSER_VERSION environment variable, and if null 'browser.version' property value is returned
	 *
	 * @author carlos.cadena
	 * @return the property value
	 */
	public static String getBrowserVersion() {
		if(System.getProperty("BROWSER_VERSION") == null)
		{
			return props.getProperty("browser.version");
		}
		return System.getProperty("BROWSER_VERSION");
	}

	/**
	 * Return BROWSER variable, and if null 'browser' property value is returned.
	 *
	 * @author carlos.cadena
	 * @return the property value
	 */
	public static String getBrowser() {
		if(System.getProperty("BROWSER") == null)
		{
			return props.getProperty("browser");
		}
		return System.getProperty("BROWSER");
	}

	/**
	 * Return OS variable, and if null 'remote.os' property value is returned
	 *
	 * @author carlos.cadena
	 * @return the property value
	 */
	public static String getOS() {
		if(System.getProperty("OS") == null)
		{
			return props.getProperty("remote.os");
		}
		return System.getProperty("OS");
	}

	/**
	 * Return IS_WEB  variable, and if null 'web' property value is returned telling if
	 * is or not a web execution, True for Web execution, anything different for Mobile Execution.
	 *
	 * @author carlos.cadena
	 * @return the property value
	 */
	public static String getWeb() {
		if(System.getProperty("IS_WEB") == null)
		{
			return props.getProperty("web");
		}
		return System.getProperty("IS_WEB");
	}

	//endregion

	// region Device Config

	/**
	 * Return PLATFORM_NAME environment variable, and if null 'platform.name'
	 * property value is returned
	 *
	 * @author carlos.cadena
	 * @return the property value
	 */
	public static String getPlatformName() {
		if (System.getProperty("PLATFORM_NAME") == null) {
			return props.getProperty("platform.name");
		}
		return System.getProperty("PLATFORM_NAME");
	}

	/**
	 * Return PLATFORM_VERSION environment variable, and if null 'platform.version'
	 * property value is returned
	 *
	 * @author carlos.cadena
	 * @return the property value
	 */
	public static String getPlatformVersion() {
		if (System.getProperty("PLATFORM_VERSION") == null) {
			return props.getProperty("platform.version");
		}
		return System.getProperty("PLATFORM_VERSION");
	}

	/**
	 * Return DEVICE_NAME environment variable, and if null 'device.name' property
	 * value is returned
	 *
	 * @author carlos.cadena
	 * @return the property value
	 */
	public static String getDeviceName() {
		if (System.getProperty("DEVICE_NAME") == null) {
			return props.getProperty("device.name");
		}
		return System.getProperty("DEVICE_NAME");
	}
	
	/**
	 * Return HUAWEI_HMS environment variable, and if null 'huawei.no.hms' property
	 * value is returned
	 *
	 * @author carlos.cadena
	 * @return the property value
	 */
	public static String getHuaweiHms() {
		if (System.getProperty("HUAWEI_HMS") == null) {
			return props.getProperty("huawei.hms");
		}
		return System.getProperty("HUAWEI_HMS");
	}
	
	/**
	 * Return HUAWEI environment variable, and if null 'huawei' property
	 * value is returned
	 *
	 * @author carlos.cadena
	 * @return the property value
	 */
	public static String getHuawei() {
		if (System.getProperty("HUAWEI") == null) {
			return props.getProperty("huawei");
		}
		return System.getProperty("HUAWEI");
	}

	// endregion

	//region Parallel Config

	/**
	 * Return PARALLEL environment variable, and if null 'timeout' property value is
	 * returned.
	 *
	 * @author carlos.cadena
	 * @return the property value
	 */
	public static String getParallel() {
		if (System.getProperty("PARALLEL") == null) {
			return props.getProperty("parallel.devices");
		}
		return System.getProperty("PARALLEL");
	}

	//endregion

	// region ApiKeys

	/**
	 * Gets the test object id of an app deployed in sauce labs from envs.properties
	 * file
	 *
	 * @author carlos.cadena
	 * @param app which is the actual app that was selected for execution
	 * @return the test object id
	 */
	public static String getTestObjectId(String app) {
		return apiKeys.getProperty(app);
	}
	
	
	/**
	 * Gets the environment .
	 *
	 * @author carlos.cadena
	 * @return the env
	 */
	public static String getEnvironment() {
		if (System.getProperty("ENV") == null) {
			return props.getProperty("env");
		}
		return System.getProperty("ENV");
	}

	// endregion
	
	// region packages

	/**
	 * Gets the package .
	 *
	 * @author carlos.cadena
	 * @return the env
	 */
	public static String getPackage() {
		return packages.getProperty(getApp());		
	}
	
	/**
	 * Return PHONE_ONLY environment variable, and if null 'phone.only' property value is
	 * returned.
	 *
	 * @author carlos.cadena
	 * @return the property value
	 */
	public static String getPhoneOnly() {
		if (System.getProperty("PHONE_ONLY") == null) {
			return props.getProperty("phone.only");
		}
		return System.getProperty("PHONE_ONLY");
	}

	public static String getAppActivity() {
		if (System.getProperty("APP_ACTIVITY") == null) {
			return props.getProperty("app.activity");
		}
		return System.getProperty("APP_ACTIVITY");
	}
}
