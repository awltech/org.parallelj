package tutorial;

import java.util.concurrent.Callable;

import org.parallelj.Attribute;
import org.parallelj.Executable;

/**
 * Converts a string to its upper case value.
 * 
 * @author Laurent Legrand
 *
 */
@Executable
public class ToUpperCase implements Callable<String> {
	
	/**
	 * The string to convert.
	 */
	@Attribute
	String source;

	/**
	 * Returns the upper case value of the source
	 */
	@Override
	public String call() throws Exception {
		Thread.sleep(1000);
		System.out.println(this.source + ":" + Thread.currentThread());
		return this.source.toUpperCase();
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

}
