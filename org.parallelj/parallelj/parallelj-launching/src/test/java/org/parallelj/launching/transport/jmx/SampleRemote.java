package org.parallelj.launching.transport.jmx;


/**
 * @author fr22240
 *
 */
public class SampleRemote implements
						SampleRemoteMBean {
	
	public String process() {
		return "done.";
	}

}
