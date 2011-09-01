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

package org.parallelj.util;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Assert;
import org.junit.Test;
import org.parallelj.util.BufferedIterable;
import org.parallelj.util.BatchIterable;
import org.parallelj.util.FlattenIterable;
import org.parallelj.util.ReaderIterable;

public class IterableTest {
	
	@SuppressWarnings("unchecked")
	@Test(expected=UnsupportedOperationException.class)
	public void removeBatchIterator() {
		Iterator<?> iterator = new BatchIterable(Collections.emptyList()).iterator();
		iterator.remove();
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected=UnsupportedOperationException.class)
	public void removeBufferedIterator() {
		Iterator<?> iterator = new BufferedIterable(Collections.emptyList()).iterator();
		iterator.remove();
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected=UnsupportedOperationException.class)
	public void removeFlattenIterator() {
		Iterator<?> iterator = new FlattenIterable(Collections.emptyList()).iterator();
		iterator.remove();
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected=NoSuchElementException.class)
	public void emtpyBatchIterator() {
		Iterator<?> iterator = new BatchIterable(Collections.emptyList()).iterator();
		iterator.next();
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected=NoSuchElementException.class)
	public void emtpyBufferedIterator() {
		Iterator<?> iterator = new BufferedIterable(Collections.emptyList()).iterator();
		iterator.next();
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected=NoSuchElementException.class)
	public void emtpyFlattenIterator() {
		Iterator<?> iterator = new FlattenIterable(Collections.emptyList()).iterator();
		iterator.next();
	}

	@Test
	public void testBufferedIterable() {
		Assert.assertEquals(100, this.count(new BufferedIterable<String>(
				Collections.nCopies(100, "s"))));
	}

	@Test
	public void testBatchIterable() {
		Assert.assertEquals(50, this.count(new BatchIterable<String>(
				Collections.nCopies(100, "s"), 2)));
	}

	@Test
	public void testFlattenIterable() {
		Assert.assertEquals(100, this.count(new FlattenIterable<String>(
				new BatchIterable<String>(Collections.nCopies(100, "s")))));
	}

	@Test
	public void testReaderIterable() throws Exception {
		/*Reader reader = new InputStreamReader(IterableTest.class
				.getResourceAsStream("lorem-ipsum.txt"));
		for (String s : new ReaderIterable(reader)) {
			System.out.println(s);
		}*/
	}

	int count(Iterable<?> iterable) {
		int count = 0;
		for (@SuppressWarnings("unused")
		Object o : iterable) {
			count++;
		}
		return count;
	}

}
