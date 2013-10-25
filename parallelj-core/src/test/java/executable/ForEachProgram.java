package executable;

import java.util.Arrays;
import java.util.Collection;

import org.parallelj.AndJoin;
import org.parallelj.AndSplit;
import org.parallelj.Begin;
import org.parallelj.ForEach;
import org.parallelj.Program;

@Program
public class ForEachProgram {
    Collection<String> list = Arrays.asList("1", "2", "3");
    short cpt=0;

    @Begin
    @AndSplit("b")
    public void a(@ForEach("list") String s) {
        cpt++;
    }

    @AndJoin
    @AndSplit("end")
    public void b(@ForEach("list") String s) {
        cpt++;
    }

	public short getCpt() {
		return cpt;
	}

}