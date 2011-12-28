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
package org.parallelj.jee.webapp;

import java.io.InputStream;
import java.util.concurrent.Executors;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.parallelj.Process;
import org.parallelj.Program;
import org.parallelj.Programs;

@Path("/programs")
public class ProgramResource {

	@POST
	@Produces("application/xml")
	@Path("/{class}")
	public Object startProgram(@PathParam("class") String classname,
			@HeaderParam("Content-Length") int contentLength, InputStream stream)
			throws JAXBException, InstantiationException,
			IllegalAccessException {
		
		Class<?> clazz;

		try {
			clazz = Class.forName(classname);
		} catch (ClassNotFoundException e) {
			return Response.status(Status.NOT_FOUND)
				.entity("The class " + classname + " does not exist.")
				.type("text/plain")
				.build();
		}
		
		if (!clazz.isAnnotationPresent(Program.class)) {
			return Response.status(Status.NOT_FOUND)
			.entity("The class " + classname + " is not annotated @Program.")
			.type("text/plain")
			.build();
		}

		Object program;
		if (contentLength > 0) {
			Unmarshaller unmarshaller = JAXBContext.newInstance(clazz)
					.createUnmarshaller();
			program = unmarshaller.unmarshal(stream);
		} else {
			program = clazz.newInstance();
		}

		Process<Object> process = Programs.as(program);
		process.execute(Executors.newSingleThreadExecutor());
		return program;
	}
}
