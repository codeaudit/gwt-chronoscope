/**
 * 
 */
package org.timepedia.util.junit;

import junit.framework.TestCase;

/**
 * Tests the essential functionality of <tt>eauals()</tt>, <tt>hashCode</tt>, and 
 * <tt>toString</tt> on classes that provide implementations for these methods.
 * 
 * @author chad takahashi &lt;chad@timepedia.org&gt;
 */
public final class ObjectSmokeTest {
	private TestObjectFactory testObjectFactory;
	
	
	public ObjectSmokeTest(TestObjectFactory testObjectFactory) {
		this.testObjectFactory = testObjectFactory;
	}
	
	
	/**
	 * Convenience method that calls all of the other <tt>test*()</tt> methods on this 
	 * object.
	 */
	public void testAll() {
		testEquals();
		testHashCode();
		testToString();
	}
	
	
	public void testEquals() {
		for (int i = 0; i < testObjectFactory.instanceCount(); i++) {
			Object obj = testObjectFactory.getInstance(i);
			
			// CASE 1: An object should always equal itself.  In other words, 
			//         <tt>x.equals(x)</tt> should always return <tt>true</tt>.
			TestCase.assertTrue("obj.equals(obj) returned false", obj.equals(obj));
			
			// CASE 2: An object should never equal null.  In other words,
			//         <tt>x.equals(null)</tt> should always return false.
			TestCase.assertFalse("obj.equals(null) returned true", obj.equals(null));
			
			// CASE 3: foo.equals(bar) should return true whenever foo and bar are 
			// instances of the same class and have the same state.
			Object foo = testObjectFactory.getInstance(i);
			Object bar = testObjectFactory.getInstance(i);
			TestCase.assertEquals("foo.equals(bar) returned false", foo, bar);
		}
	}
	
	
	/**
	 * Verifies that consecutive calls to <tt>hashCode()</tt> return the same value.
	 */
	public void testHashCode() {
		
		// CASE 1:  Verify that consecutive calls to <tt>hashCode()</tt> on the same instance
		//return the same value.
		for (int i = 0; i < testObjectFactory.instanceCount(); i++) {
			Object obj = testObjectFactory.getInstance(i);
			int expectedHashCode = obj.hashCode();
			for (int j = 0; j < 10; j++) {
				int actualHashCode = obj.hashCode();
				if (expectedHashCode != obj.hashCode()) {
					fail(j, expectedHashCode, actualHashCode);
				}
			}
		}
		
		// CASE 2: Verify that multiple instances having the same state all produce 
		// the same hashCode value.
		for (int i = 0; i < testObjectFactory.instanceCount(); i++) {
			int expectedHashCode = testObjectFactory.getInstance(i).hashCode();
			for (int j = 0; j < 10; j++) {
				int actualHashCode = testObjectFactory.getInstance(i).hashCode();
				if (expectedHashCode != actualHashCode) {
					fail(i, expectedHashCode, actualHashCode);
				}
			}
		}
	}
	
	
	/**
	 * Verifies that <tt>toString()</tt> returns a non-null value and also that it doesn't
	 * throw any exceptions.
	 */
	public void testToString() {
		for (int i = 0; i < testObjectFactory.instanceCount(); i++) {
			TestCase.assertNotNull("i=" + i + ": toString() was null", testObjectFactory.getInstance(i).toString());
		}
	}
	
	
	/**
	 * Pretty-formats a JUnit fail message for the specified inputs.
	 */
	private void fail(int i, int expectedValue, int actualValue) {
		TestCase.fail("i=" + i + ": expectedValue=" + expectedValue + 
			      " but actualValue=" + actualValue);
	}
	
}
