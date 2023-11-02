package framework.base;

import com.applitools.eyes.selenium.Eyes;

// TODO: Auto-generated Javadoc
/**
 * The Visualizable interface which allows a page class to implement visual tests.
 *
 * @author carlos.cadena
 */
public interface Visualizable {
	
	/**
	 * Perform visual test.
	 * @author carlos.cadena
	 *
	 * @param eyes the eyes
	 */
	public void performVisualTest(Eyes eyes);

}
