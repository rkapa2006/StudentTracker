package com.tutions.student.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 * This class representation data persistence.
 * 
 * H2 DB engine is used as the relational database engine.
 * 
 * @author rkapa
 * 
 * @since 1.0.0
 */
public class StudentDBConnection {

	private static Logger LOG = Logger.getLogger(StudentDBConnection.class);
	private static String H2_DB_DRIVER_CLASS = "org.h2.Driver";
	private static String DB_NAME = "student_tracking";
	private static String DB_URL = "jdbc:h2:file:data/students/" + DB_NAME;
	private static String DB_USER_NAME = "tutor";
	private static String DB_PASSWORD = "tutor123";
	

	private static StudentDBConnection studentDBConnection;

	private StudentDBConnection() {
	}

	static StudentDBConnection getStudentDBConnection() {

		if (studentDBConnection == null) {
			studentDBConnection = new StudentDBConnection();
		}

		return studentDBConnection;
	}

	private Connection dbConnection;

	Connection getConnection() throws SQLException {
		if (dbConnection == null) {
			dbConnection = createConnection();
			dbConnection.setAutoCommit(false);
		}

		return dbConnection;
	}

	private Connection createConnection() {
		Connection connection = null;

		try {
			Class.forName(H2_DB_DRIVER_CLASS);
			connection = DriverManager.getConnection(DB_URL, DB_USER_NAME,
					DB_PASSWORD);
		} catch (ClassNotFoundException cnfe) {
			LOG.error("H2_DB_DRIVER_CLASS" + "is not found.", cnfe);
		} catch (SQLException sqle) {
			LOG.error("SQLException error: ", sqle);
		}

		return connection;
	}


	void closeConnection() {
		if (dbConnection != null) {
			try {
				dbConnection.close();
				dbConnection = null;
			} catch (SQLException sqle) {
				LOG.error("Closing db connection error", sqle);
			}
		}
	}

	public void createDb() {
		Connection connection = null;
		Statement stmt = null;
		try {
			connection = getConnection();
			stmt = connection.createStatement();
			stmt.executeUpdate(StudentDBScripts.CREATE_STUDENTS_TABLE);
			stmt.executeUpdate(StudentDBScripts.CREATE_PAYMENTS_TABLE);

			connection.commit();

		} catch (SQLException sqle) {
			LOG.debug("Sql Error while creating the DB", sqle);
		}

		finally {
			if (connection != null) {
				closeConnection();
				connection = null;
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				stmt = null;
			}
		}
	}

	public void dropDb() {
		try {
			Connection dbConnection = getConnection();
			Statement stmt = dbConnection.createStatement();
			stmt.execute(StudentDBScripts.DROP_STUDENTS_TABLE);
			stmt.execute(StudentDBScripts.DROP_PAYMENTS_TABLE);
		} catch (SQLException sqle) {
			LOG.debug("SQL error while dropping the DB ", sqle);
		}

		finally {
			closeConnection();
		}
	}

	public static void main(String... args) {
		BasicConfigurator.configure();
		StudentDBConnection studentDbConnection = StudentDBConnection
				.getStudentDBConnection();
		LOG.debug("Droping DB");
		studentDbConnection.dropDb();
		LOG.debug("Creating DB");
		studentDbConnection.createDb();
	}
}
