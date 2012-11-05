package tutorial;

import javax.annotation.Generated;

import org.parallelj.Capacity;
import org.parallelj.Program;
import org.parallelj.Begin;
import org.parallelj.AndSplit;
import org.parallelj.AndJoin;

/**
 * Program org.parallelj.test.demo.MyInnerProgram Description :
 **/
@Generated("//J")
@Program
public class MyInnerProgram {

	private String val;

	public MyInnerProgram(String val) {
		super();
		this.val = val;
	}

	@Begin
	@Capacity(1)
	public ToUpperCase procone() {
		System.out.println("Display procone 1a: " + val);
		ToUpperCase toUpperCase = new ToUpperCase();
		toUpperCase.setSource(val);
		return toUpperCase;
	}

	@AndSplit({ "proctwo" })
	@Capacity(1)
	public void procone(ToUpperCase toUpperCase, String result) {
		System.out.println("RES one: " + result);
	}

	@AndJoin
	@Capacity(1)
	public ToUpperCase proctwo() {
		System.out.println("Display proctwo 1b: " + val);
		ToUpperCase toUpperCase = new ToUpperCase();
		toUpperCase.setSource(val);
		return toUpperCase;
	}

	@AndSplit({ "procthree" })
	@Capacity(1)
	public void proctwo(ToUpperCase toUpperCase, String result) {
		// System.out.println("Display proctwo 1b: " + iteratortwo.next());
		System.out.println("RES two: " + result);
	}

	@AndJoin
	@Capacity(1)
	public ToUpperCase procthree() {
		System.out.println("Display procthree 1c: " + val);

		ToUpperCase toUpperCase = new ToUpperCase();
		toUpperCase.setSource(val);
		return toUpperCase;
	}

	@AndSplit({ "end" })
	@Capacity(1)
	public void procthree(ToUpperCase toUpperCase, String result) {
		System.out.println("RES three: " + result);
	}

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}
}
