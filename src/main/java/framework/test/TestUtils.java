package framework.test;

import framework.base.FrameworkProperties;
import groovy.lang.Tuple2;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.SkipException;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static framework.base.AppiumDriverFacade.appiumDriver;

/**
 * A TestUtils class with the common functionalities.
 *
 * @author carlos.cadena
 */
public class TestUtils {
	
	public enum Browser{
        FIREFOX, CHROME, EDGE
	}
	

	/**
	 * Read file.
	 *
	 * @author carlos.cadena
	 * @param file the file
	 * @param addSeparator the add separator
	 * @return the string with the text contained in
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static String readFile(File file, boolean addSeparator) throws IOException {
		StringBuilder text = new StringBuilder();
		CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
		decoder.onMalformedInput(CodingErrorAction.IGNORE);
		FileInputStream input = new FileInputStream(file);
		InputStreamReader reader = new InputStreamReader(input, decoder);
		BufferedReader bufferedreader = new BufferedReader(reader);
		String line;
		while ((line = bufferedreader.readLine()) != null) {
			text.append(line);
			if (addSeparator) {
				text.append(System.getProperty("line.separator")).append("<br>");
			}
		}
		bufferedreader.close();
		return text.toString();
	}
	
	  /**
  	 * Gets the file from resource.
  	 *
  	 * @param fileName the file name
  	 * @return the file from resource
  	 * @throws URISyntaxException the URI syntax exception
  	 */
  	private static File getFileFromResource(String fileName) throws URISyntaxException{
	        ClassLoader classLoader = TestUtils.class.getClassLoader();
	        URL resource = classLoader.getResource(fileName);
	        if (resource == null) {
	            throw new IllegalArgumentException("file not found! " + fileName);
	        } else {
	            return new File(resource.toURI());
	        }
	  }
	


	/**
	 * Ge available android device from sauce labs.
	 *
	 * @param huawei the huawei
	 * @param hms the hms
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws URISyntaxException the URI syntax exception
	 */
	public static String geAvailableAndroidDeviceFromSauceLabs(boolean huawei, boolean hms) throws IOException, URISyntaxException {
		Random rand = new Random();
		List<String> huaweiHmsDevices = Arrays
				.asList(TestUtils.readFile(getFileFromResource("hmsDevices.txt"), false).split(","));
		if (FrameworkProperties.getLocal().equalsIgnoreCase("true")) {
			return FrameworkProperties.getDeviceName();
		} else {
			RestAssured.baseURI = "https://app.testobject.com/api/rest/v2/devices/available";
			Response response = RestAssured.given().auth().preemptive()
					.basic("guillermomartin", FrameworkProperties.getTestObjectId(FrameworkProperties.getApp())).get();
			List<String> devices = response.getBody().jsonPath().getList("EU");
			devices = devices.stream().filter(x -> !(x.contains("iPhone") || x.contains("iPad")) && (FrameworkProperties.getPhoneOnly().equalsIgnoreCase("True") ? !x.contains("Tab") && !x.contains("MediaPad") && !x.contains("Google Pixel C") : true)
					&& (huawei ? x.contains("Huawei") ||  x.contains("HUAWEI") : true)
					&& (hms ? huaweiHmsDevices.contains(x) : !huaweiHmsDevices.contains(x)))
					.collect(Collectors.toList());
			if(!devices.isEmpty()) {
			return devices.get(rand.nextInt(devices.size()));
			}
			else {
			throw new SkipException("No devices were found, skipping test....");
			}
		}
	}
	
	/**
	 * Gets the Device and Platform Name and Version information.
	 * 
	 * @author carlos.cadena
	 * @return A {@link Tuple2} with the Device on first and platform name and
	 *         version on second value
	 */
	public static Tuple2<String, String> getDevicePlatformNameAndVersion() {
		String deviceNameSearch, device;
		Map<String, Object> mapSession = appiumDriver.get().getSessionDetails();
		String platformAndVersion = mapSession.get("platformName") + " " + mapSession.get("platformVersion").toString();
		if (FrameworkProperties.getLocal().equalsIgnoreCase("true")) {
			device = FrameworkProperties.getDeviceName();
		} else {
			RestAssured.baseURI = "https://app.testobject.com/api/rest/v2/devices";
			Response response = RestAssured.given().auth().preemptive()
					.basic("xxxxxxx", FrameworkProperties.getTestObjectId(FrameworkProperties.getApp())).get();
			deviceNameSearch = appiumDriver.get().getCapabilities().asMap().get("testobject_device").toString();
			device = response.getBody().jsonPath().getString((deviceNameSearch.contains("_us") ? "US":"EU") +".find { it.id == '" + deviceNameSearch + "' }.name");
			if(device == null) {
				device = response.getBody().jsonPath().getString("US.find { it.id == '" + deviceNameSearch + "' }.name");
			}
            device = device.replace("HUAWEI", "Huawei").replace("bq", "BQ");
		}
		return new Tuple2<>(device, platformAndVersion);
	}
	
	/**
	 * Assert list size.
	 *
	 * @author carlos.cadena
	 * @param <T> the generic type
	 * @param elements the elements
	 * @param index the index
	 */
	public static <T> void assertListSize(List<T> elements, int index) {
		if(index > elements.size() - 1){
			Asserts.fail("Failing test as element with index '" + index + "' is not present");
		}
	}

	/**
	 * Return a {@link List} of {@link LocalDate} with the dates ordered providing a
	 * a {@link List} of{@link String} of dates as obtained from format like (e.g Hoy, Ayer, Martes, 16 May, 15 Oct)
	 *
	 * @author carlos.cadena
	 * @param dates the dates
	 * @return the list
	 */
	public static List<LocalDate> asDates(String ... dates){
		return asDates(Arrays.asList(dates));
	}


	/**
	 * Return a {@link List} of {@link LocalDate} with the dates ordered providing a 
	 * a {@link List} of{@link String} of dates as obtained from format like (e.g Hoy, Ayer, Martes, 16 May, 15 Oct)
	 * 
	 * @author carlos.cadena
	 * @param dates the dates
	 * @return the list
	 */
	public static List<LocalDate> asDates(List<String> dates){
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("d MMM yyyy");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("d MMM yy");
		dates = dates.stream()
				.map(date -> date.contains("Ene") ? date.replace("Ene", "Jan")
						: date.contains("Abr") ? date.replace("Abr", "Apr")
								: date.contains("Ago") ? date.replace("Ago", "Aug")
										: date.contains("Dic") ? date.replace("Dic", "Dec") : date.trim())
				.collect(Collectors.toList());
		Map<String, LocalDate> daysAndDates = TestUtils.daysOfWeekWithDate();
		return dates.stream()
				.map(d -> daysAndDates.containsKey(d) ? daysAndDates.get(d)
						: LocalDate.parse((d.split("\\s").length == 2 ? d + " " + LocalDate.now().getYear() : d),
								(d.split("\\s").length == 3 && Arrays.asList(d.split("\\s")).get(2).length() == 2 ? formatter2 : formatter1)))
				.collect(Collectors.toList());
	}
	
	
	/**
	 * Returns a Map with the name day as key and date as value.
	 *
	 * @return the map
	 */
	public static Map<String,LocalDate> daysOfWeekWithDate(){
        Map<String, LocalDate> dayAndDate = new HashMap<>();
 		for(int i = 0; i < 8; i++) {
 			if(i == 0) {
 			dayAndDate.put("Hoy", LocalDate.now());
 			}
 			else if(i == 1) {
 			dayAndDate.put("Ayer", LocalDate.now().minusDays(1));
 			}
 			else {
                dayAndDate.put(toTitle(LocalDate.now().minusDays(i).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.of("es", "ES"))), LocalDate.now().minusDays(i));
 		    }
 		}
 		return dayAndDate;
	}
	
	/**
	 * Converts a single text to 'title' format (first letter as Uppercase).
	 *
	 * @param text the text
	 * @return the string
	 */
	public static String toTitle(String text) {
        StringBuilder sb = new StringBuilder();
        Stream.of(text.split(" ")).forEach(stringPart -> {
            char[] charArray = stringPart.toLowerCase().toCharArray();
            charArray[0] = Character.toUpperCase(charArray[0]);
            sb.append(new String(charArray)).append(" ");
        }); return sb.toString().trim();
	}
	
	

}
