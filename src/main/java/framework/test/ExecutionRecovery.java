package framework.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.testng.ITestResult;
import org.testng.util.RetryAnalyzerCount;

import framework.base.FrameworkProperties;
import framework.report.Log;

// TODO: Auto-generated Javadoc
/**
 * The ExecutionRecovery class defined to define retry behavior for test execution.
 *
 * @author carlos.cadena
 */
public class ExecutionRecovery extends RetryAnalyzerCount {
	
	/** The is retry. */
	private boolean isRetry;
	
	/** The retry was called. */
	private boolean retryWasCalled;
	
	/** The exhausted. */
	private boolean exhausted;

	
	/**
	 * Checks if is exhausted.
	 *
	 * @return true, if is exhausted
	 */
	public boolean isExhausted() {
		return exhausted;
	}

	/**
	 * Sets the exhausted.
	 *
	 * @param exhausted the new exhausted
	 */
	public void setExhausted(boolean exhausted) {
		this.exhausted = exhausted;
	}

	/**
	 * Retry was called.
	 *
	 * @return true, if successful
	 */
	public boolean retryWasCalled() {
        return retryWasCalled;
    }
	
	/**
	 * Sets the retry mode.
	 *
	 * @param retry the new retry mode
	 */
	public void setRetryMode(boolean retry) {
		isRetry = retry;
	}
	
	
	/**
	 * Gets retry count.
	 *
	 * @return number of retries available
	 */
	public int getRetryCount() {
        return this.getCount();
    }
	
	
	/**
	 * Instantiates a new execution recovery.
	 */
	public ExecutionRecovery() {
		isRetry = false;
		exhausted = false;
        this.setCount(Integer.valueOf(FrameworkProperties.getRetryTimes()).intValue());		
	}
	
	/**
	 * Retry method.
	 *
	 * @param result the result
	 * @return true, if successful
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.testng.util.RetryAnalyzerCount#retryMethod(org.testng.ITestResult)
	 */
	@Override
	public boolean retryMethod(ITestResult result) {
		if (isRetry) {
			PrintWriter writer;
			try {
				writer = new PrintWriter(new File("./" + result.getTestContext().getName() + "_log.log"));
				writer.print("");
				writer.close();
				Log.logger.debug("Retry execution for '" + (this.getCount() + 1) + "' time(s) for test '"
						+ result.getTestContext().getName() + "'");
				result.getTestContext().setAttribute("onRetry", true);
				retryWasCalled = true;
				isRetry = false;
				return true;
			} catch (FileNotFoundException e) {
				Log.logger.debug("Log file '" + result.getTestContext().getName() + "_log.log"
						+ " was not found, test will not be retry");
				return false;
			}
		}
		return false;
	}

}
