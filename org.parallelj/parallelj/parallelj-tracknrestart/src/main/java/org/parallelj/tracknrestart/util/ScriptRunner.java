package org.parallelj.tracknrestart.util;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 
 * Runs database script.
 *
 */
public class ScriptRunner {

	private static com.ibatis.common.jdbc.ScriptRunner scriptRunner= null;

	private static InputStream databaseProperties= null;

	private static final Logger log = Logger.getLogger(ScriptRunner.class);

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
