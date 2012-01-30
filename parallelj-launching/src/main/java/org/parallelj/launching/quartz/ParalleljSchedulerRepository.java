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
package org.parallelj.launching.quartz;

import java.util.Collection;
import java.util.HashMap;

import org.quartz.SchedulerException;

/**
 * This class is a Parallelj implementation of a Quartz scheduler repository. 
 * 
 */
public final class ParalleljSchedulerRepository {
    /**
     * 
     */
    private HashMap<String, ParalleljScheduler> schedulers;
    
    /**
     * 
     */
    private static ParalleljSchedulerRepository inst;
    
    /**
     * 
     */
    private ParalleljSchedulerRepository() {
        schedulers = new HashMap<String, ParalleljScheduler>();
    }

    /**
     * @return
     */
    public static synchronized ParalleljSchedulerRepository getInstance() {
        if (inst == null) {
            inst = new ParalleljSchedulerRepository();
        }

        return inst;
    }

    /**
     * @param sched
     * @throws SchedulerException
     */
    public synchronized void bind(final ParalleljScheduler sched) throws SchedulerException {

        if (schedulers.get(sched.getSchedulerName()) != null) {
            throw new SchedulerException("Scheduler with name '"
                    + sched.getSchedulerName() + "' already exists.");
        }

        schedulers.put(sched.getSchedulerName(), sched);
    }

    /**
     * @param schedName
     * @return
     */
    public synchronized boolean remove(final String schedName) {
        return (schedulers.remove(schedName) != null);
    }

    /**
     * @param schedName
     * @return
     */
    public synchronized ParalleljScheduler lookup(final String schedName) {
        return schedulers.get(schedName);
    }

    /**
     * @return
     */
    public synchronized Collection<ParalleljScheduler> lookupAll() {
        return java.util.Collections
                .unmodifiableCollection(schedulers.values());
    }
}
