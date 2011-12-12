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
//			FileInputStream in = new FileInputStream("src/main/resources/database.properties");
//			prop.load(in);
//			in.close();
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

//	public static void runScript(final String scriptName) throws IOException, SQLException {
//
//		if (log.isDebugEnabled()) {
//			log.debug("runScript() : execute script : " + scriptName);
//		}
//		getScriptRunner().runScript(new FileReader(scriptName));
//	}

	public static void runScript(final FileReader fr, final InputStream pDatabaseProperties) throws IOException, SQLException {

		databaseProperties = pDatabaseProperties;
//		if (log.isDebugEnabled()) {
//			log.debug("Execute SQL script " + pSqlScript + " with database properties " + pDatabaseProperties);
//		}
		getScriptRunner().runScript(fr);
	}

}
