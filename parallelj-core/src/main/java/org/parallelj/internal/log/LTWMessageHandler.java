/*
 *     ParallelJ, framework for parallel computing
 *     
 *     Copyright (C) 2010 Atos Worldline or third-party contributors as
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
package org.parallelj.internal.log;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.apache.log4j.Logger;
import org.aspectj.bridge.AbortException;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.IMessage.Kind;
import org.aspectj.bridge.IMessageHandler;

public class LTWMessageHandler implements IMessageHandler {

	static Logger logger = Logger.getLogger("org.parallelj.weaving");

	Map<Kind, Priority> levelMapping = new HashMap<Kind, Priority>();

	boolean isVerbose = true;
	boolean isDebug = true;
	boolean showWeaveInfo = true;
	boolean showWarn = true;

	public LTWMessageHandler() {
		levelMapping.put(IMessage.ABORT, Level.TRACE);
		levelMapping.put(IMessage.DEBUG, Level.DEBUG);
		levelMapping.put(IMessage.ERROR, Level.ERROR);
		levelMapping.put(IMessage.FAIL, Level.TRACE);
		levelMapping.put(IMessage.INFO, Level.INFO);
		levelMapping.put(IMessage.TASKTAG, Level.TRACE);
		levelMapping.put(IMessage.WARNING, Level.WARN);
		levelMapping.put(IMessage.WEAVEINFO, Level.INFO);
	}

	@Override
	public boolean handleMessage(IMessage message) throws AbortException {
		if (isIgnoring(message.getKind())) {
			return false;
		} else {
			logger.log(levelMapping.get(message.getKind()), message.toString());
		}
		return true;
	}

	public boolean isIgnoring(IMessage.Kind kind) {
		if (kind.equals(IMessage.WEAVEINFO)) {
			return !showWeaveInfo;
		}
		if (kind.isSameOrLessThan(IMessage.INFO)) {
			return !isVerbose;
		}
		if (kind.isSameOrLessThan(IMessage.DEBUG)) {
			return !isDebug;
		}
		return !showWarn;
	}

	public void dontIgnore(IMessage.Kind kind) {
		if (kind.equals(IMessage.WEAVEINFO)) {
			showWeaveInfo = true;
		} else if (kind.equals(IMessage.DEBUG)) {
			isVerbose = true;
		} else if (kind.equals(IMessage.WARNING)) {
			showWarn = false;
		}
	}

	public void ignore(IMessage.Kind kind) {
		if (kind.equals(IMessage.WEAVEINFO)) {
			showWeaveInfo = false;
		} else if (kind.equals(IMessage.DEBUG)) {
			isVerbose = false;
		} else if (kind.equals(IMessage.WARNING)) {
			showWarn = true;
		}
	}

}
