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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.sshd.common.Factory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.transport.tcp.TcpIpHandlerAdapter;
import org.parallelj.launching.transport.tcp.command.Quit;
import org.parallelj.launching.transport.tcp.command.TcpCommand;
import org.parallelj.launching.transport.tcp.command.TcpIpCommands;

public class SshShellFactory implements Factory<Command> {

	@Override
	public Command create() {
		return new SshShell();
	}

	public class SshShell implements Command, Runnable {
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
	
						String str = readLine(r);
	
						// Parse the command
						String cmd = null;
						String[] args = str.trim().split("[\t ]");
						if (args.length > 0) {
							cmd = args[0];
						}
	
						List<String> lstArg = new ArrayList<String>();
						if (args.length>0) {
							boolean isArg=false;
							int index=0;
							for (String val : args) {
								if (val.equals("-a")) {
									lstArg.add(val);
									index++;
									isArg=true;
								} else {
									if (val.length()>0) {
										if (isArg && val.charAt(0)!='-' && val.indexOf('=')==-1) {
											lstArg.set(index-1, lstArg.get(index-1)+" "+val);
										} else {
											lstArg.add(val);
											index++;
										}
									} else {
										lstArg.set(index-1, lstArg.get(index-1)+" ");
									}
								}
							}
						}
						
						// Try to launch the command
						TcpCommand command = TcpIpCommands.getCommands().get(cmd);
						String result = null;
	
						// launch the command and get the result
						if (command != null) {
							if (command instanceof Quit) {
								return;
							} else if (args.length>1) {
								try {
								String[] finalArgs = new String[lstArg.size()];
								for (int i=0; i< finalArgs.length; i++) {
									String[] argSplit=lstArg.get(i).split("=");
									if (argSplit.length==2 && argSplit[1].charAt(0)=='"' && argSplit[1].charAt(argSplit[1].length()-1)=='"' ) {
										finalArgs[i]=argSplit[0]+"="+argSplit[1].substring(1, argSplit[1].length()-1);
									} else {
										finalArgs[i]=lstArg.get(i);
									}
								}
								result = command.process(null, Arrays.copyOfRange(finalArgs, 1, finalArgs.length));
								} catch (Exception e) {
									result = LaunchingMessageKind.ELAUNCH0007.format(cmd, e);
								}
							} else {
								result = command.process(null, new String[]{});
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
	
		private String readLine(BufferedReader r) throws IOException {
			StringBuffer str = new StringBuffer();
			int ch = r.read();
			this.out.write(ch);
			this.out.flush();
			while ((char) ch != '\n' && ((char) ch != '\r')) {
				str.append((char) ch);
				ch = r.read();
				this.out.write(ch);
				this.out.flush();
			}
			for (int i = 0; i < ENDLINE.length(); i++) {
				this.out.write(ENDLINE.charAt(i));
			}
			this.out.flush();
	
			return str.toString();
		}
	}
}