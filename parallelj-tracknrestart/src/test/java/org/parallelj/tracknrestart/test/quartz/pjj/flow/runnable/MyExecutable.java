/*
 *     ParallelJ, framework for parallel computing
 *
 *     Copyright (C) 2010, 2011, 2012 Atos Worldline or third-party contributors as
 *     indicated by the @author tags or express copyright attribution
 *     statements applied by the authors.
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
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
