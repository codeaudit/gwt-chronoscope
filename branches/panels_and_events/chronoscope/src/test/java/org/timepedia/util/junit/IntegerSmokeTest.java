/**
 * 
 */
package org.timepedia.util.junit;

import junit.framework.TestCase;

/**
 * Trivial example of how to create a JUnit test case that uses ObjectSmokeTest 
 * to test essential functionality of <tt>java.lang.Integer</tt>.
 * 
 * @author chad takahashi &lt;chad@timepedia.org&gt;
 */
public class IntegerSmokeTest extends TestCase {
	private TestObjectFactory integerFactory;
	
	
	public IntegerSmokeTest(String testName) {
		super(testName);
		
		// Create a TestObjectFactory instance that generates Integer objects.
		this.integerFactory = new TestObjectFactory() {
			public Object getInstance(int index) {
				return new Integer(index * 10);
			}
			public int instanceCount() {
				return 999;
			}
		};
	}
	
	
	public void testObjectEssentials() {
		ObjectSmokeTest smokeTest = new ObjectSmokeTest(this.integerFactory);
		smokeTest.testAll();
	}
}
