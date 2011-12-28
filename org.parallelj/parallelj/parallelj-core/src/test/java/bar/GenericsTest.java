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
package bar;

public class GenericsTest {

	public interface Test<E extends Test<E>> {

		Processor<E> get();

	}
	
	public interface Processor<E extends Test<E>> {
		void process(E e);
	}

	public abstract class MyTest<E> implements Test<MyTest<E>> {
		
	}

	@org.junit.Test
	public void testRien() {
		
	}
	
	public class FinalTest extends MyTest<FinalTest> {

		/*@Override
		public Processor<FinalTest> get() {
			return null;
		}*/
		
		@Override
		public Processor<MyTest<FinalTest>> get() {
			// TODO Auto-generated method stub
			return null;
		}

	}

}
