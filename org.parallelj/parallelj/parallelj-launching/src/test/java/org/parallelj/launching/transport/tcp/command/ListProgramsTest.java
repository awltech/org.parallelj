package org.parallelj.launching.transport.tcp.command;

import org.apache.mina.core.session.DummySession;
import org.apache.mina.core.session.IoSession;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * The class <code>ListProgramsTest</code> contains tests for the class <code>{@link ListPrograms}</code>.
 *
 * @generatedBy CodePro at 09/12/11 17:15
 * @author fr22240
 * @version $Revision: 1.0 $
 */
public class ListProgramsTest {
	/**
	 * Run the ListPrograms() constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@Test
	public void testListPrograms_1()
		throws Exception {

		ListPrograms result = new ListPrograms();

		// add additional test code here
		assertNotNull(result);
		assertEquals(90, result.getPriorityUsage());
		assertEquals("list", result.getType());
	}

	/**
	 * Run the int getPriorityUsage() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@Test
	public void testGetPriorityUsage_1()
		throws Exception {
		ListPrograms fixture = new ListPrograms();

		int result = fixture.getPriorityUsage();

		// add additional test code here
		assertEquals(90, result);
	}

	/**
	 * Run the String getType() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@Test
	public void testGetType_1()
		throws Exception {
		ListPrograms fixture = new ListPrograms();

		String result = fixture.getType();

		// add additional test code here
		assertEquals("list", result);
	}

	/**
	 * Run the String getUsage() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@Test
	public void testGetUsage_1()
		throws Exception {
		ListPrograms fixture = new ListPrograms();

		String result = fixture.getUsage();

		// add additional test code here
		assertEquals("list : Lists available programs and their associated IDs.", result);
	}

	/**
	 * Run the String process(IoSession,String[]) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@Test
	public void testProcess_1()
		throws Exception {
		ListPrograms fixture = new ListPrograms();
		IoSession session = new DummySession();

		String result = fixture.process(session);

		// add additional test code here
		assertEquals(null, result);
	}

	/**
	 * Run the String process(IoSession,String[]) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@Test
	public void testProcess_2()
		throws Exception {
		ListPrograms fixture = new ListPrograms();
		IoSession session = new DummySession();

		String result = fixture.process(session);

		// add additional test code here
		assertEquals(null, result);
	}

	/**
	 * Run the String process(IoSession,String[]) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@Test
	public void testProcess_3()
		throws Exception {
		ListPrograms fixture = new ListPrograms();
		IoSession session = new DummySession();

		String result = fixture.process(session);

		// add additional test code here
		assertEquals(null, result);
	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *         if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@Before
	public void setUp()
		throws Exception {
		// add additional set up code here
	}

	/**
	 * Perform post-test clean-up.
	 *
	 * @throws Exception
	 *         if the clean-up fails for some reason
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@After
	public void tearDown()
		throws Exception {
		// Add additional tear down code here
	}

	/**
	 * Launch the test.
	 *
	 * @param args the command line arguments
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(ListProgramsTest.class);
	}
}