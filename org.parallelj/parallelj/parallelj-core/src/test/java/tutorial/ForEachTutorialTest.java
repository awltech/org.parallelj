package tutorial;

import java.util.Arrays;
import java.util.concurrent.Executors;

import org.junit.Assert;
import org.junit.Test;
import org.parallelj.Programs;

public class ForEachTutorialTest {

	@Test
	public void test() {
		
		// initialize the program
		ForEachTutorial tutorial = new ForEachTutorial();
		tutorial.setInput(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h",
				"i", "j", "k", "l"));

		// run the program with a cached thread pool
		// wait for the completion of the program: Programs.join()
		Programs.as(tutorial).execute(Executors.newFixedThreadPool(10)).join();

		// check that all values are in upper case
		for (String s : tutorial.getOutput()) {
			Assert.assertEquals(s, s.toUpperCase());
		}
	}

}
