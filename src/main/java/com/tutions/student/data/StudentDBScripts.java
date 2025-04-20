package com.tutions.student.data;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 * This class contains all the sql scripts required for the application.
 * 
 */
public class StudentDBScripts {
	private static Logger LOG = Logger.getLogger(StudentDBScripts.class);


	public static final String CREATE_STUDENTS_TABLE = "CREATE TABLE STUDENTS "
			+ "( STUDENT_ID INT PRIMARY KEY, FIRST_NAME VARCHAR(255), LAST_NAME VARCHAR(255), "
			+ "PHONE BIGINT, GRADE INT, START_DATE BIGINT, MONTHLY_TUTION DECIMAL(20, 2), MAIL_ID VARCHAR(255) )";

	public static final String CREATE_PAYMENTS_TABLE = "CREATE TABLE PAYMENTS "
			+ "( PAYMENT_ID INT PRIMARY KEY, STUDENT_ID INT, PAYMENT_AMOUNT DECIMAL(20, 2),"
			+ " PAYMENT_MODE INT, PAYMENT_MONTH INT, PAYMENT_YEAR INT, PAYMENT_DATE BIGINT"
			+ ") ";

	public static final String DROP_STUDENTS_TABLE = "DROP TABLE STUDENTS";

	public static final String DROP_PAYMENTS_TABLE = "DROP TABLE PAYMENTS";

	public static final String STUDENT_INSERT = "INSERT INTO STUDENTS (STUDENT_ID, FIRST_NAME, LAST_NAME, "
			+ "PHONE, GRADE, START_DATE, MONTHLY_TUTION, MAIL_ID) "
			+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

	public static final String STUDENT_UPDATE = "UPDATE STUDENTS SET PHONE = ?, "
			+ "GRADE = ?, START_DATE = ?, MONTHLY_TUTION = ?, MAIL_ID = ? WHERE FIRST_NAME = ? AND LAST_NAME = ? ";

	public static final String STUDENT_DELETE = "DELETE FROM STUDENTS WHERE STUDENT_ID = ?";

	public static final String PAYMENTS_FOR_STUDENT_DELETE = "DELETE FROM PAYMENTS WHERE STUDENT_ID = ?";

	public static final String STUDENT_SELECT = "SELECT STUDENT_ID, FIRST_NAME, LAST_NAME, "
			+ "PHONE, GRADE, START_DATE, MONTHLY_TUTION, MAIL_ID FROM STUDENTS";

	public static final String PAYMENT_INSERT = "INSERT INTO PAYMENTS (PAYMENT_ID, STUDENT_ID, PAYMENT_AMOUNT, "
			+ " PAYMENT_MODE, PAYMENT_MONTH, PAYMENT_YEAR, PAYMENT_DATE) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?)";

	public static final String PAYMENT_SELECT = "SELECT PAYMENT_ID, STUDENT_ID, PAYMENT_AMOUNT, "
			+ "PAYMENT_MODE, PAYMENT_MONTH, PAYMENT_YEAR, PAYMENT_DATE FROM PAYMENTS WHERE STUDENT_ID = ?";

	public static final String PAYMENT_DELETE = "DELETE FROM PAYMENTS WHERE PAYMENT_ID = ?";

	public static void main(String... args) {
		BasicConfigurator.configure();
		LOG.debug("Students script: " + CREATE_STUDENTS_TABLE);
		LOG.debug("Payments Script: " + CREATE_PAYMENTS_TABLE);

		LOG.debug("Student Insert: " + STUDENT_INSERT);
		LOG.debug("Payment Insert: " + PAYMENT_INSERT);

		LOG.debug("Student Select: " + STUDENT_SELECT);
		LOG.debug("Payment Select: " + PAYMENT_SELECT);
	}

}
