package org.parallelj.internal.util.sm;

import org.junit.Assert;
import org.junit.Test;

public class ReleaseTest {

	@Test
	public void test() {
		Release release = new Release();

		Assert.assertEquals(release.state, ReleaseState.ALPHA);

		release.rc("rrr");
		Assert.assertEquals(release.state, ReleaseState.RC);
	}

}
