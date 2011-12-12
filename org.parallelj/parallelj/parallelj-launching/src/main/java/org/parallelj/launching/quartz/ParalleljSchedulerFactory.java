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

package org.parallelj.launching.quartz;

import java.util.Collection;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.SchedulerRepository;
import org.quartz.impl.StdSchedulerFactory;

/**
 * This class is a Parallelj implementation of the Quartz StdSchedulerFactory. 
 * It allows to instanciate {@link ParalleljScheduler ParalleljScheduler} using Quartz mechanism.
 * 
 */
public class ParalleljSchedulerFactory implements SchedulerFactory  {
	
	private StdSchedulerFactory stdChedulerFactory;

    public ParalleljSchedulerFactory() {
    	this.stdChedulerFactory = new StdSchedulerFactory();
    }

    public ParalleljSchedulerFactory(String fileName) throws SchedulerException {
    	this.stdChedulerFactory = new StdSchedulerFactory(fileName);
    }
	
	@Override
	public final ParalleljScheduler getScheduler() throws SchedulerException {
		Scheduler scheduler = this.stdChedulerFactory.getScheduler();
		ParalleljSchedulerRepository pSchedRep = ParalleljSchedulerRepository.getInstance();

		ParalleljScheduler pScheduler = pSchedRep.lookup(scheduler.getSchedulerName());
		if (pScheduler != null) {
            if (scheduler.isShutdown()) {
            	pSchedRep.remove(scheduler.getSchedulerName());
            } else {
                return pScheduler;
            }
			return pScheduler;
		}
		
		pScheduler = instantiate(pSchedRep, stdChedulerFactory);

        return pScheduler;
	}

	private ParalleljScheduler instantiate(ParalleljSchedulerRepository pSchedRep, StdSchedulerFactory stdChedulerFactory) throws SchedulerException {
		ParalleljScheduler pScheduler = new ParalleljScheduler();
		pSchedRep.bind(pScheduler);
		return pScheduler;
	}

	@Override
	public final Scheduler getScheduler(String schedName) throws SchedulerException {
		return ParalleljSchedulerRepository.getInstance().lookup(schedName);
	}

	@Override
	public final Collection<Scheduler> getAllSchedulers() throws SchedulerException {
		 return SchedulerRepository.getInstance().lookupAll();
	}

}
