package messages;

import org.junit.Test;
import org.parallelj.internal.MessageKind;

public class MessageKindTest {
	
	@Test
	public void test() {
		MessageKind.I0001.format(MessageKindTest.class);
	}
	

}
