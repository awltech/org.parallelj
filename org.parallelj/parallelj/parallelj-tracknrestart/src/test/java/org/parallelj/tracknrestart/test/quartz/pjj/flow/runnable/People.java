package org.parallelj.tracknrestart.test.quartz.pjj.flow.runnable;

import java.io.Serializable;


public class People implements Serializable {

	private String forname;
	private String lastname;
	
	public People(String forname, String lastname) {
		this.forname = forname;
		this.lastname = lastname;
	}

	public String getForname() {
		return forname;
	}

	public void setForname(String forname) {
		this.forname = forname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
	public String toString() {
		return forname + " " + lastname;
	}
	
	

}
