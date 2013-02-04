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

import org.aspectj.bridge.AbortException;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.IMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LTWMessageHandler implements IMessageHandler {

	static Logger logger = LoggerFactory.getLogger("org.parallelj.weaving");

	boolean isVerbose = true;
	boolean isDebug = true;
	boolean showWeaveInfo = true;
	boolean showWarn = true;

	public LTWMessageHandler() {
	}

	@Override
	public boolean handleMessage(IMessage message) throws AbortException {
		if (isIgnoring(message.getKind())) {
			return false;
		} else {
			if (message.isDebug()) {
				logger.debug(message.toString());
			} else if (message.isInfo()) {
				logger.info(message.toString());
			} else if (message.isWarning()) {
				logger.warn(message.toString());
			} else if (message.isError()) {
				logger.error(message.toString());
			} else {
				logger.trace(message.toString());
			}
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
