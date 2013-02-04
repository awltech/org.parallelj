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
package org.parallelj.tracknrestart.util;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Runs database script.
 *
 */
public class ScriptRunner {

	private static com.ibatis.common.jdbc.ScriptRunner scriptRunner= null;

	private static InputStream databaseProperties= null;

	private static com.ibatis.common.jdbc.ScriptRunner getScriptRunner() throws IOException {

		// Instanciate ScriptRunner once.
		if (scriptRunner == null) {

			Properties prop = new Properties();
			prop.load(databaseProperties);
			databaseProperties.close();
			
			scriptRunner = new com.ibatis.common.jdbc.ScriptRunner(
			       prop.getProperty("database.driver"),
			       prop.getProperty("database.url"),
			       prop.getProperty("database.user"),
			       prop.getProperty("database.password"),
			       false,                                                      
			       false);
		}
		
		return scriptRunner;
	}

	public static void runScript(final FileReader fr, final InputStream pDatabaseProperties) throws IOException, SQLException {

		databaseProperties = pDatabaseProperties;
		getScriptRunner().runScript(fr);
	}

}
