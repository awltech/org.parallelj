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

package util;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Processor;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * utility class that can be used to verify {@link Processor}.
 * 
 * @author Laurent Legrand
 * 
 */
public abstract class ProcessorTest {

	/**
	 * Return a list of {@link Processor}
	 * 
	 * @return
	 */
	protected abstract Iterable<? extends Processor> getProcessors();

	/**
	 * Compile a list of java files and return a list of {@link Diagnostic}.
	 * 
	 * @param resources
	 *            the list of java resources to compile; relative to classpath.
	 * @return processing diagnostics
	 * @throws Exception
	 */
	public List<Diagnostic<? extends JavaFileObject>> compile(
			String... resources) throws Exception {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(
				diagnostics, null, null);

		List<File> files = new ArrayList<File>();
		for (String s : resources) {
			files.add(new File(this.getClass().getResource(s).toURI()));
		}
		Iterable<? extends JavaFileObject> compilationUnits = fileManager
				.getJavaFileObjectsFromFiles(files);
		CompilationTask task = compiler.getTask(new PrintWriter(System.out),
				fileManager, diagnostics, null, null, compilationUnits);
		task.setProcessors(this.getProcessors());
		task.call();
		fileManager.close();
		System.out.println(diagnostics.getDiagnostics());
		return diagnostics.getDiagnostics();
	}

	/**
	 * Verify that one of the {@link Diagnostic} in the list contains a given
	 * pattern
	 * 
	 * @param diagnostics
	 * @param type
	 * @return
	 */
	public boolean assertDiagnostic(
			List<Diagnostic<? extends JavaFileObject>> diagnostics,
			Class<? extends Processor> type) {
		return this.assertDiagnostic(diagnostics, type.getSimpleName());
	}

	/**
	 * Verify that one of the {@link Diagnostic} in the list contains a given
	 * pattern
	 * 
	 * @param diagnostics
	 * @param pattern
	 * @return
	 */
	public boolean assertDiagnostic(
			List<Diagnostic<? extends JavaFileObject>> diagnostics,
			String pattern) {
		for (Diagnostic<?> diagnostic : diagnostics) {

			if (diagnostic.getMessage(null).contains(pattern)) {
				return true;
			}
		}
		return false;
	}

}
