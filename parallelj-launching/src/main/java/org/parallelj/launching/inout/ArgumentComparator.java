package org.parallelj.launching.inout;

import java.util.Comparator;

public class ArgumentComparator implements Comparator<Argument> {

    @Override
    public int compare(Argument o1, Argument o2) {
    	if(o1.getindex()>o2.getindex())
    		return +1;
    	else if(o1.getindex()<o2.getindex())
    		return -1;
    	else
    		return 0;
    }
}           


