package framework.test;

import framework.report.Log;
import org.testng.Assert;

import java.util.List;

/**
 * The Class Asserts.
 */
public class Asserts {

    /**
     * Assert true.
     *
     * @param condition the condition
     * @param message the message
     */
    public static void assertTrue(boolean condition, String message){
        try{
            Assert.assertTrue(condition, message);
        }catch (AssertionError e){
            Log.validationFail(message);
            throw e;
        }
    }

    /**
     * Assert false.
     *
     * @param condition the condition
     * @param message the message
     */
    public static void assertFalse(boolean condition, String message){
        try{
            Assert.assertFalse(condition, message);
        }catch (AssertionError e){
            Log.validationFail(message);
            throw e;
        }
    }

    /**
     * Assert equal.
     *
     * @param actual the actual
     * @param expected the expected
     * @param type the type
     */
    public static void assertEqual(String actual, String expected, String type){
        try{
            Assert.assertEquals(actual, expected);
        }catch (AssertionError e){
            Log.validationFail(String.format("Expected: %s", expected) + " (" + type + "), " +
                    String.format("Actual: %s", actual) + " (" + type + ")");
            throw e;
        }
    }

    /**
     * Assert equal.
     *
     * @param actual the actual
     * @param expected the expected
     * @param type the type
     */
    public static void assertEqual(List<String> actual, List<String> expected, String type){
        try{
            Assert.assertEquals(actual, expected);
        }catch (AssertionError e){
            Log.validationFail(String.format("Expected: %s", expected) + " (" + type + "), " +
                    String.format("Actual: %s", actual) + " (" + type + ")");
            throw e;
        }
    }

    /**
     * Assert equal.
     *
     * @param actual the actual
     * @param expected the expected
     * @param type the type
     */
    public static void assertEqual(float actual, float expected, String type){
        try{
            Assert.assertEquals(actual, expected);
        }catch (AssertionError e){
            Log.validationFail(String.format("Expected: %s", expected) + " (" + type + "), " +
                    String.format("Actual: %s", actual) + " (" + type + ")");
            throw e;
        }
    }
    
    /**
     * Fail test with log message.
     *
     * @param message the message
     */
    public static void fail(String message){
            Assert.fail(message);
            Log.validationFail(message);
    }
    
}