package framework.test;

import framework.base.FrameworkProperties;

import org.testng.IAlterSuiteListener;
import org.testng.annotations.ITestAnnotation;
import org.testng.internal.annotations.IAnnotationTransformer;
import org.testng.xml.XmlGroups;
import org.testng.xml.XmlRun;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlSuite.ParallelMode;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * The ExecutionListener interface for receiving execution events.
 * The class that is interested in processing an execution
 * event implements this interface, and the object created
 * with that class is registered inside a TestNG suite xml. When
 * the execution event occurs, that object's appropriate
 * method is invoked.
 * 
 * @author carlos.cadena
 * 
 */
public class ExecutionListener implements IAlterSuiteListener, IAnnotationTransformer {

	/* (non-Javadoc)
	 * @see org.testng.IAlterSuiteListener#alter(java.util.List)
	 */
	@Override
	public void alter(List<XmlSuite> suites) {
		XmlSuite suite = suites.get(0);
		this.alterParallel(suite);
	}
	
	/**
	 * Alter parallel execution parameters for suite.
	 *
	 * @author carlos.cadena
	 * @param suite the suite
	 */
	private void alterParallel(XmlSuite suite)
	{
		suite.setParallel(ParallelMode.getValidParallel(FrameworkProperties.props.getProperty("parallel.type")));
		String maxParallel = FrameworkProperties.getParallel();
		suite.setThreadCount(Integer.valueOf(suite.getTests().size() < Integer.valueOf(maxParallel) ? String.valueOf(suite.getTests().size()) : maxParallel));
		if(System.getProperties().containsKey("groups")) {
		XmlGroups group  = new XmlGroups();
		XmlRun run = new XmlRun();
		System.getProperties().list(System.out);
		Arrays.asList(System.getProperty("groups").split(",")).forEach(prop -> run.onInclude(prop));
		group.setRun(run);
		suite.setGroups(group);
		}
	}


	/* (non-Javadoc)
	 * @see org.testng.IAnnotationTransformer#transform(org.testng.annotations.ITestAnnotation, java.lang.Class, java.lang.reflect.Constructor, java.lang.reflect.Method)
	 */
	@Override
	public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
		annotation.setRetryAnalyzer(ExecutionRecovery.class);
	}

}
