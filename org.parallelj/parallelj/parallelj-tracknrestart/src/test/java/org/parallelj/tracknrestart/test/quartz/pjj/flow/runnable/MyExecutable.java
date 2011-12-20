package org.parallelj.tracknrestart.test.quartz.pjj.flow.runnable;

import org.parallelj.Attribute;
import org.parallelj.tracknrestart.annotations.TrackNRestart;
import org.parallelj.tracknrestart.test.quartz.pjj.flow.BusinessException;

//@TrackNRestart(filteredExceptions={BusinessException.class})
@TrackNRestart(filteredExceptions={RuntimeException.class, BusinessException.class})
public class MyExecutable implements Runnable {
	
	/**
	 * The string to convert.
	 */
	@Attribute
	People source;

	@Override
	public void run() {
		
		if (source.getForname().equals("chapi") && source.getLastname().equals("chapo")) {
			System.out.println("MyExecutable::run : no forname chapi");
			System.out.println();
			System.out.println();
			throw new RuntimeException("MyExecutable::run : no forname chapi, exception");
		}
		System.out.println("MyExecutable::run processed  = " + source);
		System.out.println();
		System.out.println();
	}

	public People getSource() {
		return source;
	}

	public void setSource(People source) {
		this.source = source;
	}

	public String getOID() {
		return source.getLastname();
	}
}
