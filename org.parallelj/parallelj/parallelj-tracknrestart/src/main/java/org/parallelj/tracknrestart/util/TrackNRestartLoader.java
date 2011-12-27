package org.parallelj.tracknrestart.util;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public final class TrackNRestartLoader {

	private TrackNRestartLoader() {
	}

	public static void main(String[] args) {
		try {
			FileReader sqlScriptFileReader = new FileReader("scripts/quartz-track-database-init-mysql.sql");
			InputStream databaseProperties = TrackNRestartLoader.class.getResourceAsStream("/database.properties");
			ScriptRunner.runScript(sqlScriptFileReader, databaseProperties);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void cleanTrackingDatabase() {
		main(null);
	}

	public static void cleanTrackingDatabase(String sqlScriptAsString, String databasePropertiesAsString) {
		try {
			FileReader sqlScriptFileReader = new FileReader(sqlScriptAsString);
			InputStream databaseProperties = TrackNRestartLoader.class.getResourceAsStream("/"+databasePropertiesAsString);
			ScriptRunner.runScript(sqlScriptFileReader, databaseProperties);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}