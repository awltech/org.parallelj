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
package org.parallelj.launching.transport.ssh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;

import org.apache.sshd.common.Factory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.parallelj.launching.transport.tcp.TcpIpHandlerAdapter;
import org.parallelj.launching.transport.tcp.command.Quit;
import org.parallelj.launching.transport.tcp.command.TcpCommand;
import org.parallelj.launching.transport.tcp.command.TcpIpCommands;

public class SshShellFactory implements Factory<Command>, Command, Runnable {

	public static final String ENDLINE = "\n\r";

	private static final String WELCOMEFILE = "/org/parallelj/launching/welcome.txt";

	private static final String CMD_UNKNOWN = "command unknown :";

	private String welcome;

	private InputStream in;

	private OutputStream out;

	private OutputStream err;

	private ExitCallback callback;

	private Thread thread;

	private static final String prompt = "Shell>";

	@Override
	public Command create() {
		return this;
	}

	@Override
	public void start(Environment env) throws IOException {
		thread = new Thread(this, "SshShellFactory");
		thread.start();
	}

	@Override
	public void run() {
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(in));
			InputStream inputStream = TcpIpHandlerAdapter.class
					.getResourceAsStream(WELCOMEFILE);
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);
			BufferedReader reader = new BufferedReader(inputStreamReader);
			StringBuilder sb = new StringBuilder();
			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					sb.append(line).append(ENDLINE);
				}
			} catch (IOException e) {
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
				}
				try {
					inputStream.close();
				} catch (IOException e) {
				}
				try {
					inputStreamReader.close();
				} catch (IOException e) {
				}
			}
			this.welcome = sb.toString();

			out.write(this.welcome.getBytes());
			out.write(ENDLINE.getBytes());

			while (true) {
				out.write(prompt.getBytes());
				out.flush();
				while (true) {

					String str = r.readLine();

					// Parse the command
					String cmd = null;
					String[] args = str.split("[\t ]");
					if (args.length > 0) {
						cmd = args[0];
					}

					// Try to launch the command
					TcpCommand command = TcpIpCommands.getCommands().get(cmd);
					String result = null;

					// launch the command and get the result
					if (command != null) {
						if (command instanceof Quit) {
							return;
						} else if (args.length > 1) {
							result = command.process(null,
									Arrays.copyOfRange(args, 1, args.length));
						} else {
							result = command.process(null, new String[] {});
						}
					} else {
						out.write((CMD_UNKNOWN + cmd).getBytes());
					}
					// If the command returned a result, write it for the user
					if (result != null) {
						out.write(result.getBytes());
					}
					out.write(ENDLINE.getBytes());
					out.write(ENDLINE.getBytes());
					out.flush();
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} finally {
			callback.onExit(0);
		}

	}

	public void setInputStream(InputStream in) {
		this.in = in;
	}

	public void setOutputStream(OutputStream out) {
		this.out = out;
	}

	public void setErrorStream(OutputStream err) {
		this.err = err;
	}

	public void setExitCallback(ExitCallback callback) {
		this.callback = callback;
	}

	public void destroy() {
		thread.interrupt();
	}
}