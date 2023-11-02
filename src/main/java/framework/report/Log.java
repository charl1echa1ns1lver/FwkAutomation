package framework.report;

import org.apache.logging.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Log class defined to establish specific logging messages.
 *
 * @author carlos.cadena
 */
public class Log {

    /** The logger. */
    public static Logger logger;
    
    /** The fail reason. */
    public static String failReason;

    /**
     * Log 'Test start' info.
     *
     * @author carlos.cadena
     * @param testName the test name
     */
    public static void testStart(String testName){
        logger.info("*************** " + testName + " Inicio ***************");
    }

    /**
     * Log 'Test description' info.
     *
     * @author carlos.cadena
     * @param testDescription the test description
     */
    public static void testDescription(String testDescription){
        logger.info("----------- " + testDescription + " -----------");
    }

    /**
     * Log 'Test fail' info.
     *
     * @author carlos.cadena
     * @param testName the test name
     */
    public static void testFail(String testName){
        logger.error("xxxxxxxxxxxxxxx " + testName + " Fallo xxxxxxxxxxxxxxx");
    }

    /**
     * Log 'Test Skipped' info.
     *
     * @author carlos.cadena
     * @param testName the test name
     */
    public static void testSkipped(String testName){
        logger.error("!!!!!!!!!!!! " + testName + " No Ejecutado !!!!!!!!!!!!");
    }

    /**
     * Log 'Test End' info.
     *
     * @author carlos.cadena
     * @param testName the test name
     */
    public static void testEnd(String testName){
        logger.info("*************** " + testName + " Final ***************\n");
    }

    /**
     * Log test step info.
     *
     * @author carlos.cadena
     * @param message the message
     */
    public static void testStep(String message){
        logger.info(message);
    }

    /**
     * Log test step info.
     *
     * @author carlos.cadena
     * @param message the message
     * @param parameter the parameter
     */
    public static void testStep(String message, String parameter){
        logger.info(String.format(message,parameter));
    }


    /**
     * Log validation failure reason.
     *
     * @author carlos.cadena
     * @param message the message
     */
    public static void validationFail(String message){failReason = message;}

    /**
     * Log test failure reason.
     *
     * @author carlos.cadena
     */
    public static void testFailRazon(){logger.error("xxxxxx " + failReason + " xxxxxx");}
}
