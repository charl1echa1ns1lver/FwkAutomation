package framework.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import framework.base.FrameworkProperties;
import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 * The Class AllDevicesExecutor.
 */
public class AllDevicesExecutor{
	
	
	/** The queue. */
	private List<HashMap<String,String>> listOfDevicesUS;
	private List<HashMap<String,String>> listOfDevicesEU;
	
	public AllDevicesExecutor(){
		listOfDevicesUS = new ArrayList<HashMap<String,String>>();
		listOfDevicesEU = new ArrayList<HashMap<String,String>>();

	}
	
	/**
	 * Start queue with all devices.
	 */
	public void startQueueWithAllDevices(boolean usAndEu) {
		RestAssured.baseURI = "https://app.testobject.com/api/rest/v2/devices";
		Response res = RestAssured.given().auth().preemptive()
				.basic("guillermomartin", FrameworkProperties.getTestObjectId(FrameworkProperties.getApp())).get();
		List<HashMap<String,String>> list = res.jsonPath().getList("EU.findAll { it.os == '"+ FrameworkProperties.getPlatformName().toUpperCase() +"'}");
		List<HashMap<String,String>> list2 = res.jsonPath().getList("US.findAll { it.os == '"+ FrameworkProperties.getPlatformName().toUpperCase() +"'}");
		listOfDevicesEU.addAll(list);
		if(!usAndEu) {
			List<HashMap<String, String>> missingOnes = list2.stream().filter(dev2 -> list.stream().noneMatch(
					dev1 -> dev1.get("name").toLowerCase().contentEquals(dev2.get("name").toLowerCase()) && dev1
							.get("osVersion").toLowerCase().contentEquals(dev2.get("osVersion").toLowerCase())))
					.collect(Collectors.toList());
			listOfDevicesUS.addAll(missingOnes);
		}
		else {
			listOfDevicesUS.addAll(list2);

		}
	}
	
	
	/**
	 * Run tests.
	 *
	 * @param suiteName the suite name
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public void runTests(String suiteLocation, boolean usAndEu) throws ParserConfigurationException, SAXException, IOException {
		System.setProperty("allDevices", "True");
		startQueueWithAllDevices(usAndEu);
        TestNG tng = new TestNG();
        List<XmlSuite> suites = new ArrayList<XmlSuite>();
        suites.add(prepareSuite(tng, suiteLocation));
        tng.setXmlSuites(suites);
	    tng.run();
	}
	
	
	/**
	 * Prepare suite.
	 *
	 * @param tng the tng
	 * @param suiteLocation the suite location
	 * @return the xml suite
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public XmlSuite prepareSuite(TestNG tng, String suiteLocation) throws ParserConfigurationException, SAXException, IOException {
		File fXmlFile = new File(suiteLocation);
		XmlSuite suite = new XmlSuite();
		suite.setName(suiteLocation.substring(suiteLocation.lastIndexOf("/") + 1, suiteLocation.length()).replace(".xml",""));
		suite.addListener("framework.report.ReportListener");
		suite.addListener("framework.test.ExecutionListener");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		NodeList all = doc.getElementsByTagName("test");
		Element item = null;
		for(int i = 0; i < all.getLength(); i++){
			item = (Element) all.item(i);
			prepareDeviceSuite(suite, listOfDevicesEU, item, true);
		}
		for(int i = 0; i < all.getLength(); i++){
			item = (Element) all.item(i);
			prepareDeviceSuite(suite, listOfDevicesUS, item, false);
		}
		suite.getTests().forEach(test -> System.out.println(test.getName()));
		return suite;
		
		}
	
	private void prepareDeviceSuite(XmlSuite suite, List<HashMap<String,String>> list,  Element item, boolean eu) {
		list.stream().forEach(dev -> {
			XmlTest testXML = new XmlTest(suite);
			HashMap<String,String> execParams = new HashMap<String,String>();
	        List<XmlClass> classes = new ArrayList<XmlClass>();
	        String id = dev.get("id").replace("_us", " ").replace("_real", " ").replace("_", " ").trim();
			testXML.setName(item.getAttribute("name") + " : " + (id.contains("cx") ? id.replace("cx", "") + "Caixa Private" : id)
					+ (eu ? " EU" : " US") + " - Model : '" + dev.get("modelNumber") + "' - OS : '"
					+ dev.get("osVersion") + "'");
	        classes.add(new XmlClass(((Element)((Element)item.getElementsByTagName("classes").item(0)).getElementsByTagName("class").item(0)).getAttribute("name")));
			testXML.setXmlClasses(classes);
			execParams.put("device_name", dev.get("id"));
			execParams.put("platform_version", dev.get("osVersion"));
			testXML.setParameters(execParams);
		});
		
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main (String [] args) {
		try {
			AllDevicesExecutor exec = new AllDevicesExecutor();
			exec.runTests(args[0], Boolean.valueOf(args[1]));
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}
	
}
