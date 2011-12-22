package org.parallelj.tracknrestart.jdbc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.tracknrestart.TrackNRestartMessageKind;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobPersistenceException;
import org.quartz.SchedulerException;
import org.quartz.impl.jdbcjobstore.AttributeRestoringConnectionInvocationHandler;
import org.quartz.impl.jdbcjobstore.Constants;
import org.quartz.impl.jdbcjobstore.TablePrefixAware;
import org.quartz.impl.jdbcjobstore.Util;
import org.quartz.utils.DBConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDBCSupport implements Serializable {

	protected String dataSource;

    protected String tablePrefix = Constants.DEFAULT_TABLE_PREFIX + "TRACK_";

    protected boolean useProperties = false;
    
	public static final String TABLE_PREFIX_SUBST = "{0}";

	public static final String SCHED_NAME_SUBST = "{1}";

	public static final String COL_UID_SUBST = "UID";
	
	public static final String COL_RESULT_SUBST = "RESULT";

	public static final String COL_RETURN_CODE_SUBST = "RETURN_CODE";

	public static final String COL_RESTARTED_UID_SUBST = "RESTARTED_UID";


	private final Logger log = LoggerFactory.getLogger(getClass());

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

    public String getTablePrefix() {
		return tablePrefix;
	}

	public void setTablePrefix(String tablePrefix) {
		this.tablePrefix = tablePrefix;
	}

	public boolean canUseProperties() {
		return useProperties;
	}

	public void setUseProperties(boolean useProperties) {
		this.useProperties = useProperties;
	}

    protected Logger getLog() {
        return log;
    }

	public Connection getNonManagedTXConnection()
			throws JobPersistenceException {
		return getConnection();
	}

	protected Connection getAttributeRestoringConnection(Connection conn) {
		return (Connection) Proxy.newProxyInstance(Thread.currentThread()
				.getContextClassLoader(), new Class[] { Connection.class },
				new AttributeRestoringConnectionInvocationHandler(conn));
	}

	protected Connection getConnection() throws JobPersistenceException {
		Connection conn = null;
		try {
			conn = DBConnectionManager.getInstance().getConnection(
					getDataSource());
		} catch (SQLException sqle) {
			throw new JobPersistenceException(
					"Failed to obtain DB connection from data source '"
							+ getDataSource() + "': " + sqle.toString(), sqle);
		} catch (Throwable e) {
			throw new JobPersistenceException(
					"Failed to obtain DB connection from data source '"
							+ getDataSource() + "': " + e.toString(), e);
		}

		if (conn == null) {
			throw new JobPersistenceException(
					"Could not get connection from DataSource '"
							+ getDataSource() + "'");
		}

		// Protect connection attributes we might change.
		conn = getAttributeRestoringConnection(conn);

		// Set any connection connection attributes we are to override.
		try {
			// if (!isDontSetAutoCommitFalse()) {
			// conn.setAutoCommit(false);
			// }
			conn.setAutoCommit(true);

			// if(isTxIsolationLevelSerializable()) {
			// conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			// }
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		} catch (SQLException sqle) {
			TrackNRestartMessageKind.WTNRJDBC0003.format();
			getLog().warn(
					"Failed to override connection auto commit/transaction isolation.",
					sqle);
		} catch (Throwable e) {
			try {
				conn.close();
			} catch (Throwable tt) {
			}

			throw new JobPersistenceException("Failure setting up connection.",
					e);
		}

		return conn;
	}

	public void closeStatement(Statement statement) {
		if (null != statement) {
			try {
				statement.close();
			} catch (SQLException ignore) {
			}
		}
	}

	public final String rtp(String query, String schedName) {
		String rtp = Util.rtp(query, tablePrefix, "'" + schedName + "'");
		return rtp;
	}

	protected ByteArrayOutputStream serializeJobData(JobDataMap data)
			throws IOException {
		if (canUseProperties()) {
			return serializeProperties(data);
		}

		try {
			return serializeObject(data);
		} catch (NotSerializableException e) {
			throw new NotSerializableException(
					"Unable to serialize JobDataMap for insertion into "
							+ "database because the value of property '"
							+ getKeyOfNonSerializableValue(data)
							+ "' is not serializable: " + e.getMessage());
		}
	}

	private ByteArrayOutputStream serializeProperties(JobDataMap data)
			throws IOException {
		ByteArrayOutputStream ba = new ByteArrayOutputStream();
		if (null != data) {
			Properties properties = convertToProperty(data.getWrappedMap());
			properties.store(ba, "");
		}

		return ba;
	}

	protected Properties convertToProperty(Map<?, ?> data) throws IOException {
		Properties properties = new Properties();

		for (Iterator<?> entryIter = data.entrySet().iterator(); entryIter
				.hasNext();) {
			Map.Entry<?, ?> entry = (Map.Entry<?, ?>) entryIter.next();

			Object key = entry.getKey();
			Object val = (entry.getValue() == null) ? "" : entry.getValue();

			if (!(key instanceof String)) {
				throw new IOException("JobDataMap keys/values must be Strings "
						+ "when the 'useProperties' property is set. "
						+ " offending Key: " + key);
			}

			if (!(val instanceof String)) {
				throw new IOException("JobDataMap values must be Strings "
						+ "when the 'useProperties' property is set. "
						+ " Key of offending value: " + key);
			}

			properties.put(key, val);
		}

		return properties;
	}

	protected ByteArrayOutputStream serializeObject(Object obj)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (null != obj) {
			ObjectOutputStream out = new ObjectOutputStream(baos);
			out.writeObject(obj);
			out.flush();
		}
		return baos;
	}

	protected Object getKeyOfNonSerializableValue(Map<?, ?> data) {
		for (Iterator<?> entryIter = data.entrySet().iterator(); entryIter
				.hasNext();) {
			Map.Entry<?, ?> entry = (Map.Entry<?, ?>) entryIter.next();

			ByteArrayOutputStream baos = null;
			try {
				baos = serializeObject(entry.getValue());
			} catch (IOException e) {
				return entry.getKey();
			} finally {
				if (baos != null) {
					try {
						baos.close();
					} catch (IOException ignore) {
					}
				}
			}
		}

		// As long as it is true that the Map was not serializable, we should
		// not hit this case.
		return null;
	}

	public void cleanupConnection(Connection conn) {
		if (conn != null) {
			if (conn instanceof Proxy) {
				Proxy connProxy = (Proxy) conn;

				InvocationHandler invocationHandler = Proxy
						.getInvocationHandler(connProxy);
				if (invocationHandler instanceof AttributeRestoringConnectionInvocationHandler) {
					AttributeRestoringConnectionInvocationHandler connHandler = (AttributeRestoringConnectionInvocationHandler) invocationHandler;

					connHandler.restoreOriginalAtributes();
					closeConnection(connHandler.getWrappedConnection());
					return;
				}
			}

			// Wan't a Proxy, or was a Proxy, but wasn't ours.
			closeConnection(conn);
		}
	}

	protected void closeConnection(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				TrackNRestartMessageKind.ETNRJDBC0001.format();
//				getLog().error("Failed to close Connection", e);
			} catch (Throwable e) {
				TrackNRestartMessageKind.ETNRJDBC0002.format(e);
//				getLog().error(
//						"Unexpected exception closing Connection."
//								+ "  This is often due to a Connection being returned after or during shutdown.",
//						e);
			}
		}
	}

    public JobDataMap getJobDataMapResultFromBlob(ResultSet rs)
            throws ClassNotFoundException, IOException, SQLException {
            Map<?, ?> map;
            JobDataMap jobDataMap = (JobDataMap) getJobDataFromBlob(rs, COL_RESULT_SUBST);
            return jobDataMap;
        }

    protected Object getJobDataFromBlob(ResultSet rs, String colName)
            throws ClassNotFoundException, IOException, SQLException {
            if (canUseProperties()) {
                Blob blobLocator = rs.getBlob(colName);
                if (blobLocator != null) {
                    InputStream binaryInput = blobLocator.getBinaryStream();
                    return binaryInput;
                } else {
                    return null;
                }
            }

            return getObjectFromBlob(rs, colName);
        }

    protected Object getObjectFromBlob(ResultSet rs, String colName)
            throws ClassNotFoundException, IOException, SQLException {
            Object obj = null;

            Blob blobLocator = rs.getBlob(colName);
            if (blobLocator != null && blobLocator.length() != 0) {
                InputStream binaryInput = blobLocator.getBinaryStream();

                if (null != binaryInput) {
                    if (binaryInput instanceof ByteArrayInputStream
                        && ((ByteArrayInputStream) binaryInput).available() == 0 ) {
                        //do nothing
                    } else {
                        ObjectInputStream in = new ObjectInputStream(binaryInput);
                        try {
                            obj = in.readObject();
                        } finally {
                            in.close();
                        }
                    }
                }

            }
            return obj;
        }

    public Map<?, ?> getMapFromProperties(ResultSet rs)
            throws ClassNotFoundException, IOException, SQLException {
            Map<?, ?> map;
            InputStream is = (InputStream) getJobDataFromBlob(rs, Constants.COL_JOB_DATAMAP);
            if(is == null) {
                return null;
            }
            Properties properties = new Properties();
            if (is != null) {
                try {
                    properties.load(is);
                } finally {
                    is.close();
                }
            }
            map = convertFromProperty(properties);
            return map;
        }

    protected Map<?, ?> convertFromProperty(Properties properties) throws IOException {
        return new HashMap<Object, Object>(properties);
    }


}