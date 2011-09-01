package org.parallelj.mirror;

/**
 * 
 * 
 * @author Laurent Legrand
 */
public interface EventListener {

	/**
	 * Called when an event occurred
	 * 
	 * @param event
	 */
	public void handleEvent(Event<?> event);

}
