package framework.report;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import framework.base.FrameworkProperties;
import framework.base.PerformanceUtils;
import framework.test.ExecutionRecovery;
import framework.test.TestBase;
import framework.test.TestUtils;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.testng.*;
import org.testng.annotations.Test;

import java.io.*;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The listener interface for receiving report events.
 * The class that is interested in processing a report
 * event implements this interface, and the object created
 * with that class is registered inside a TestNG suite xml. When
 * the report event occurs, that object's appropriate
 * method is invoked.
 *
 * @author carlos.cadena
 * 
 */
public class ReportListener implements ITestListener, ISuiteListener{
	/** The extent. */
	private ExtentReports extent;
	
	/** The num passed. */
	private int numPassed;
	
	/** The num failed. */
	private int numFailed;
	
	/** The num skipped. */
	private int numSkipped;
	
	/** The parent test. */
	private static ExtentTest parentTest;
	
	boolean isWeb;
	
	private int totalCount, androidCount, iosCount = 0;


	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.testng.ITestListener#onStart(org.testng.ITestContext)
	 */
	@Override
	public synchronized void onStart(ITestContext context) {
		boolean createTestRecord = false;
        Optional<ITestNGMethod> methodFound = Arrays.stream(context.getAllTestMethods())
				.filter(method ->  method.getInstance().toString()
						.contains(context.getName().contains("_") ? context.getName().split("_")[0] : context.getName()))
				.findFirst();
		
		if(methodFound.isPresent()) {

		List<String> groupsOnTest = Arrays.asList(methodFound.get().getGroups());
		
		if (context.getIncludedGroups().length == 0 && context.getExcludedGroups().length == 0) {
			createTestRecord = true;
		}
		
		else {
	
		if (context.getIncludedGroups().length != 0) {
            createTestRecord = Arrays.stream(context.getIncludedGroups())
                    .anyMatch(groupsOnTest::contains);
		}
		if (context.getExcludedGroups().length != 0) {
            createTestRecord = Arrays.stream(context.getExcludedGroups())
                    .noneMatch(groupsOnTest::contains);
		}
		}
		if (createTestRecord) {
			ExtentTest child = parentTest.createNode(context.getName());
			TestBase.setReport(child);
		}
		}
	}

	/* (non-Javadoc)
	 * @see org.testng.ITestListener#onFinish(org.testng.ITestContext)
	 */
	@Override
	public synchronized void onFinish(ITestContext context) {

	}

	/* (non-Javadoc)
	 * @see org.testng.ITestListener#onTestStart(org.testng.ITestResult)
	 */
	@Override
	public synchronized void onTestStart(ITestResult result) {
	}

	/* (non-Javadoc)
	 * @see org.testng.ITestListener#onTestSuccess(org.testng.ITestResult)
	 */
	@Override
	public synchronized void onTestSuccess(ITestResult result) {
		String message = "Test finalizado OK";
		numPassed++;
		TestBase.getReport().pass(message);
	}

	/* (non-Javadoc)
	 * @see org.testng.ITestListener#onTestFailure(org.testng.ITestResult)
	 */
	@Override
	public synchronized void onTestFailure(ITestResult result) {
		numFailed++;
	}

	/* (non-Javadoc)
	 * @see org.testng.ITestListener#onTestFailedButWithinSuccessPercentage(org.testng.ITestResult)
	 */
	@Override
	public synchronized void onTestFailedButWithinSuccessPercentage(ITestResult result) {
	}

	/* (non-Javadoc)
	 * @see org.testng.ITestListener#onTestSkipped(org.testng.ITestResult)
	 */
	@Override
	public synchronized void onTestSkipped(ITestResult result) {
		ExecutionRecovery recovery = (ExecutionRecovery) (result.getMethod().getRetryAnalyzer(result));
		if (recovery.retryWasCalled() && recovery.getRetryCount() > 0) {
			extent.removeTest(TestBase.getReport());
		}
	}
	
	
	
	/**
	 * Count tests for Android, iOS and total that were executed in the suite
	 * 
	 * @author carlos.cadena
	 * @param suite the suite
	 */
	public void countTests(ISuite suite)  {
        Set<String> all = new HashSet<>();
        suite.getAllMethods().forEach(x ->
		{ 
		  String test = x.getConstructorOrMethod().getMethod().getAnnotation(Test.class).testName();
		  List<String> groups = Arrays.asList(x.getConstructorOrMethod().getMethod().getAnnotation(Test.class).groups());
		  boolean isAndroid = groups.stream().anyMatch(g -> g.equalsIgnoreCase("Android"));
		  boolean isIOS = groups.stream().anyMatch(g -> g.equalsIgnoreCase("iOS"));
		  Pattern patt = Pattern.compile("(((TC|AND|IOS)\\d{3,10})(_{1}))+");
		  Matcher matcher = patt.matcher(test);
		  if(matcher.lookingAt()) {
			  List<String> allIds = Arrays.asList(matcher.group().split("_"));
              allIds.forEach(id -> {
				if (all.add(id.replace("AND", "TC").replace("IOS", "TC"))) {
					if (id.contains("AND")) {
						androidCount++;
						totalCount++;
					}
					if (id.contains("IOS")) {
						iosCount++;
						totalCount++;
					}
					if (id.contains("TC")) {
						if (isAndroid) {
							androidCount++;
						}
						if (isIOS) {
							iosCount++;
						}
						totalCount++;
					}
				}
		  });
		  }
		});
		Log.logger.info("Number of tests - Android: '" + androidCount + "' - iOS: '" + iosCount + "' - Total: '" + totalCount + "'"  );
	}


	/**
	 * On this method the report instance is created for the suite and the environment information is retrieved.
	 *
	 * @author carlos.cadena
	 * @param suite the suite
	 */
	@Override
	public void onStart(ISuite suite) {
		extent = ExtentManager.getInstance(suite.getName());
		extent.setSystemInfo("Sist. Op.", FrameworkProperties.getPlatformName());
		if(!FrameworkProperties.getLocal().equalsIgnoreCase("True")) {
			extent.setSystemInfo("Version", FrameworkProperties.getVersionBuildNumber());
		}
		extent.setSystemInfo("Aplicacion", !FrameworkProperties.getLocal().equalsIgnoreCase("true") ? FrameworkProperties.getApp() : "Local");
		parentTest = extent.createTest(suite.getName());
		numPassed=0;
		numFailed=0;
		numSkipped=0;
	}

	/**
	 * On this method the logging area in the report gets populated with the info collected on 
	 * the .log files generated for each test
	 *
	 * @author carlos.cadena
	 * @param suite the suite
	 */
	@Override
	public void onFinish(ISuite suite) {
        StringBuilder results = new StringBuilder();
		
		try {
			File dir = new File(".");
            File[] files = dir.listFiles((dir1, name) -> name.endsWith(".log"));
            assert files != null;
            for (File file : files) {
                results.append(TestUtils.readFile(file, true));
				file.deleteOnExit();
			}
			if(FrameworkProperties.getConfluenceReport().equalsIgnoreCase("true"))this.setConfluenceResume(suite);
            this.setResumePDF(suite);

		} catch (IOException | DocumentException x) {
            x.printStackTrace();
		}
        if(PerformanceUtils.getTimeAverageInSeconds() != 0.0)
            extent.setSystemInfo("T.P. Inicio APP", String.valueOf(PerformanceUtils.getTimeAverageInSeconds()).replace(".", ",") + " Segundos");
        extent.setTestRunnerOutput(results.toString());
		extent.flush();
		this.modifyHtmlImageNames();
		if(FrameworkProperties.getRallyLogResults().equalsIgnoreCase("True")) {
		countTests(suite);
		}

    }

	/**
	 * This private method modifies the label "base-64" on report since MediaEntityBuilder class on Extent Reports
	 * does not have a function to do so when you want to encode in base64 the screenshot image.
	 *
	 * @author carlos.cadena
	 */
	private void modifyHtmlImageNames() {
		try {
			ExtentHtmlReporter htmlReporter = (ExtentHtmlReporter) extent.getStartedReporters().get(0);
			String path = htmlReporter.config().getFilePath();
			File reportFile = new File(path);
			Document doc = Jsoup.parse(reportFile, "UTF-8", "");
			Elements imageLabels = doc.select("span[class*='label grey white-text']");
			for (Element x : imageLabels) {
				x.text("image");
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(path));
			writer.write(doc.outerHtml());
			writer.close();

		} catch (IOException x) {
            x.printStackTrace();
		}
	}

	/**
	 * Sets the Confluence Resume that should be appended on Confluence Page configured in Jenkins.
	 *
	 * @param suite the new resume PDF
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws DocumentException the document exception
	 * @author carlos.cadena
	 */
	private void setResumePDF(ISuite suite) throws IOException, DocumentException {
        // Get the total of the executed tests
		int totalTest= suite.getAllMethods().size();
	    int numSkipped = totalTest - numFailed - numPassed;
        // Create the PDF document
        com.itextpdf.text.Document document = new com.itextpdf.text.Document();
        FileOutputStream pdfFile = new FileOutputStream("test-output/ResultadoPruebasResumen.pdf", false);
        PdfWriter.getInstance(document,pdfFile).setInitialLeading(20);
        document.open();
        // Create the font styles for the content of the document
        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN,18,Font.ITALIC,new BaseColor(51,102,255));
        Font subTitleFont = new Font(Font.FontFamily.TIMES_ROMAN,13,Font.NORMAL,BaseColor.BLACK);
        Font itemFont = new Font(Font.FontFamily.TIMES_ROMAN,11,Font.BOLD,BaseColor.BLACK);
        Font valueFont = new Font(Font.FontFamily.TIMES_ROMAN,11,Font.ITALIC,BaseColor.BLACK);
        // Create paragraphs for the document
        Paragraph title = new Paragraph("Resultados de las Pruebas Automatizadas:",titleFont);
        Paragraph subTitle = new Paragraph("Configuracion:",subTitleFont);
        Paragraph subTitle2 = new Paragraph("Resultados:",subTitleFont);
        // Fill the document with the tests results
        Image img = Image.getInstance("src/test/resources/images/logo-caixabank.jpg");
        document.add(img);
        document.add(title);
        document.add(new Paragraph("\n"));
        document.add(subTitle);
        document.add(addResultParagraph("Suite: ", itemFont, suite.getName(), valueFont));
        document.add(addResultParagraph("Sistema Operativo: ", itemFont, FrameworkProperties.getPlatformName(),
                valueFont));
        document.add(addResultParagraph("Version SO: ", itemFont,
                FrameworkProperties.getPlatformVersion(), valueFont));
        document.add(addResultParagraph("Dispositivo: ", itemFont,
                FrameworkProperties.getDeviceName(), valueFont));
        document.add(addResultParagraph("Aplicacion: ", itemFont, FrameworkProperties.getApp(), valueFont));
        document.add(new Paragraph("\n"));
        document.add(subTitle2);
        document.add(addResultParagraph("Numero de Pruebas Ejecutadas:  ", itemFont,
                String.valueOf(totalTest), valueFont));
        document.add(addResultParagraph("Numero de Pruebas Exitosas:  ", itemFont,
                String.valueOf(numPassed), valueFont));
        document.add(addResultParagraph("Numero de Pruebas Fallidas:  ", itemFont,
                String.valueOf(numFailed), valueFont));
        document.add(addResultParagraph("Numero de Pruebas Omitidas:  ", itemFont,
                String.valueOf(numSkipped), valueFont));
        document.close();
	}

	/**
	 * Adds the result paragraph.
	 *
	 * @param itemText the item text
	 * @param itemFont the item font
	 * @param valueText the value text
	 * @param valueFont the value font
	 * @return the paragraph
	 * @author carlos.cadena
	 */
	private Paragraph addResultParagraph(String itemText, Font itemFont, String valueText, Font valueFont){
        Paragraph p = new Paragraph("\t");
        Font bulletFont = new Font(Font.FontFamily.COURIER,14,Font.NORMAL,
                new BaseColor(0,112,192));
        Chunk bullet = new Chunk(".",bulletFont);
        p.add("\t\t\t\t\t\t");
        p.add(bullet);
        p.add(new Phrase(itemText,itemFont));
        p.add(new Phrase(valueText,valueFont));
        return p;
    }
	

	/**
	 * Sets the Confluence Resume that should be appended on Confluence Page using Confluence REST API.
	 * @param suite ISuite object to get suite name
	 * @author carlos.cadena
	 * 
	 */
	private void setConfluenceResume(ISuite suite)
	{
		String docSpace = FrameworkProperties.getConfluenceSpace();
        String docTitle = FrameworkProperties.getConfluencePage();
		String resume = 
				"<h2 style='color:rgb(51,102,255);'><b>Resultados de las Pruebas Automatizadas</b></h2>"
                + "<ul><b>Configuracion:</b>"
                + "<li style='margin-left: 3em'><b> Suite: </b>" + suite.getName() + "</li>"
                + "<li style='margin-left: 3em'><b> Sistema Operativo: </b>" + FrameworkProperties.getPlatformName() + "</li>"
                + "<li style='margin-left: 3em'><b> Version SO: </b>" + FrameworkProperties.getPlatformVersion() + "</li>"
                + "<li style='margin-left: 3em'><b> Dispositivo: </b>" + FrameworkProperties.getDeviceName() + "</li>"
                + "<li style='margin-left: 3em'><b> Aplicacion: </b>" + FrameworkProperties.getApp() + "</li>"
				+ "<ul><b>Resultados:</b>"
				+ "<li style='margin-left: 3em'><b> Numero de pruebas Ejecutadas: </b>" + (numFailed + numPassed + numSkipped) + "</li>"
				+ "<li style='margin-left: 3em'><b> Numero de Pruebas Exitosas: </b>" + numPassed + "</li>"
                + "<li style='margin-left: 3em'><b> Numero de pruebas Fallidas: </b>" + numFailed + "</li>"
                + "<li style='margin-left: 3em'><b> Numero de Pruebas Omitidas: </b>" + numSkipped + "</li></ul>";

		RestAssured.baseURI = "confluence_url_here";
		Response res = RestAssured.given().auth().preemptive().basic("xxxxxxxxx", "xxxxxxx")
				.queryParam("spaceKey", docSpace).queryParam("title", docTitle).queryParam("expand", "version")
				.get();
		String version = res.jsonPath().getString("results.version.number").replace("[","").replace("]", "");
		String id = res.jsonPath().getString("results.id").replace("[","").replace("]", "");
        RestAssured.baseURI = "https://project-barcelona.net/rest/api/content/" + id;
		RestAssured.given().auth().preemptive().basic("xxxxxxxxxxxx", "xxxxxxxx").contentType("application/json")
				.body("{" +
                        "    \"version\": {" +
                        "        \"number\": " + (Integer.parseInt(version) + 1) +
                        "    }," +
				"    \"title\": \""+ docTitle +"\"," + 
				"    \"type\": \"page\"," + 
				"    \"body\": {" + 
				"        \"storage\": {" + 
				"            \"value\": \""+ resume +"\"," + 
				"            \"representation\": \"storage\"" + 
				"        }" + 
				"    }" + 
				"}").put();
	}
}
