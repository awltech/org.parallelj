package org.parallelj.tracknrestart.test.quartz.pjj.flow.runnable;

import javax.annotation.Generated;
import org.parallelj.Program;

import java.util.Arrays;
import java.util.List;
import org.parallelj.Begin;
import org.parallelj.Capacity;
import org.parallelj.ForEach;
import org.parallelj.AndSplit;
import org.parallelj.launching.In;
import org.parallelj.launching.QuartzExecution;
//import org.parallelj.tracknrestart.databinding.In;

import org.parallelj.tracknrestart.test.quartz.pjj.parsers.ListPeopleParser;

/**
 * Program tutorial.ForEachMyElement
 * Description :
 **/
@Generated("//J")
@Program
@QuartzExecution
public class ForEachMyElement {
	/**
	 * elementList field
	 * Description :
	 **/
	@In(parser=ListPeopleParser.class)
	private List<People> data1 = Arrays.asList(new People[]{});

	public List<People> getData1() {
		return data1;
	}

	public void setData1(List<People> elementList) {
		this.data1 = elementList;
	}

	/**
	 * Exit method of procedure forEachLoop. This procedure is bound to
	 * tutorial.MyExecutable Description :
	 * 
	 * @generated //J
	 */
	@Generated(value = "//J", comments = "3677788")
	@AndSplit({ "end" })
	public void forEachLoop(MyExecutable executable) {
		System.out.println("ForEachMyElement::forEachLoop :: @AndSplit({ 'end' }) avec un executable :" + executable.getSource());
	}

	/**
	 * Entry method of procedure forEachLoop. This procedure is bound to
	 * tutorial.MyExecutable Description :
	 * 
	 * @generated //J
	 */
	@Generated(value = "//J", comments = "316001396")
	@Begin
	@Capacity(1)
	public MyExecutable forEachLoop(@ForEach("data1") People val) {
		System.out.println("ForEachMyElement::forEachLoop :: @ForEach with val :" +val);
		MyExecutable m = new MyExecutable();
		m.setSource(val);
		return m;
	}
}
