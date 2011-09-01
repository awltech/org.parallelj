package org.parallelj.internal.util.sm;

@StateMachine(states = ReleaseState.class)
public class Release {

	@Current
	ReleaseState state;

	@Trigger
	public void rc(String name) {
	}

	@Transitions({
			@Transition(source = "ALPHA", target = "RC", triggers = "rc"),
			@Transition(source = "BETA", target = "RC", triggers = "rc") })
	private void doRc(String name) {

		System.out.println("rc triggered and performed: " + name);
	}

}
