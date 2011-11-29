package tests.jmx;
import java.io.IOException;

import org.junit.Test;
import org.parallelj.launching.transport.jmx.JmxServer;
import org.parallelj.launching.transport.tcp.TcpIpServer;


public class TransportTest {

	@Test
	public void tcpIpServerTest() {
		TcpIpServer server = new TcpIpServer("localhost", 8000);
		try {
			server.start();
			Thread.sleep(1000);
			server.stop();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void jmxServerTest() {
		JmxServer server = new JmxServer("localhost", 9000);
		try {
			server.start();
			Thread.sleep(1000);
			server.stop();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
