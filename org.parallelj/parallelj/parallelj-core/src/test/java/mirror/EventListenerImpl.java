package mirror;

import org.parallelj.mirror.Event;
import org.parallelj.mirror.EventListener;

public class EventListenerImpl implements EventListener {

	public static EventListenerImpl instance;

	public EventListenerImpl() {
		instance = this;
	}

	public boolean called;

	@Override
	public void handleEvent(Event<?> event) {
		this.called = true;
	}

}
