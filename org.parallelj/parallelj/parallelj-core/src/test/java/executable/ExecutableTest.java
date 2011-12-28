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
package executable;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.parallelj.Executables;
import org.parallelj.Programs;

public class ExecutableTest {

	@Test
	public void executable() {
		MyExecutable executable = new MyExecutable();
		executable.name = "name";

		Map<String, String> map = Executables.attributes(executable);
		Assert.assertTrue(map.containsKey("name"));
	}

	@Test
	public void program() {
		MyProgram program = new MyProgram();
		program.name = "name";

		Map<String, String> map = Executables.attributes(program);
		Assert.assertTrue(map.containsKey("name"));
	}
	
	@Test
	public void run() {
		MyProgram program = new MyProgram();
		program.name = "name";

		Programs.as(program).execute();
	}
	
	
	public void empty() {
		Assert.assertTrue(Executables.attributes(new Object()).isEmpty());
	}

}
