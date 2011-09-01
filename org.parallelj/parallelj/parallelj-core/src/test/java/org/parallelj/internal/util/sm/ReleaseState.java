package org.parallelj.internal.util.sm;

public enum ReleaseState {

	@Pseudostate(kind = PseudostateKind.INITIAL)
	ALPHA, BETA, RC, RTM, GA;

}
