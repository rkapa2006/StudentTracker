package com.tutions.student.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.tutions.student.common.StudentGrade;
import com.tutions.student.vo.Student;
import com.tutions.student.vo.StudentFactory;

public class StudentDao {
	private static Logger LOG = Logger.getLogger(StudentDao.class);

	private static StudentDao studentDao;

	public static StudentDao getStudentDao() {

		if (studentDao == null) {
			studentDao = new StudentDao();
		}

		return studentDao;
	}

	public Set<Student> readStudents() {
		Set<Student> students = new LinkedHashSet<Student>();
		StudentDBConnection studentDBConnection = StudentDBConnection
				.getStudentDBConnection();
		Connection dbConnection = null;

		try {

			dbConnection = studentDBConnection.getConnection();
			PreparedStatement studentStmt = dbConnection
					.prepareStatement(StudentDBScripts.STUDENT_SELECT);
			ResultSet studentResultSet = studentStmt.executeQuery();

			while (studentResultSet.next()) {
				Student student = parseStudent(studentResultSet);
				students.add(student);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return students;
	}

	public Student parseStudent(ResultSet studentRs) throws SQLException {

		int studentId = studentRs.getInt(1);

		Student student = StudentFactory.createNewStudent(studentId);

		student.setFirstName(studentRs.getString(2));
		student.setLastName(studentRs.getString(3));
		student.setPhone(studentRs.getLong(4));

		StudentGrade studentGrade = StudentGrade.getStudentGrade(studentRs
				.getInt(5));
		student.setGrade(studentGrade);

		Date startDate = new Date(studentRs.getLong(6));
		student.setStartDate(startDate);
		student.setMonthlyTution(studentRs.getLong(7));
		student.setEmail(studentRs.getString(8));

		return student;
	}

	public boolean updateStudent(Student student) {
		boolean isWritten = false;

		StudentDBConnection studentDBConnection = StudentDBConnection
				.getStudentDBConnection();
		Connection dbConnection = null;

		try {
			dbConnection = studentDBConnection.getConnection();
			PreparedStatement studentStmt = createStudentUpdateStmt(
					dbConnection, StudentDBScripts.STUDENT_UPDATE, student);
			isWritten = studentStmt.execute();

			dbConnection.commit();

		} catch (SQLException sqle) {
			LOG.error("SQL Error while updating the student in DB: " + sqle);
		} finally {
			if (dbConnection != null) {
				studentDBConnection.closeConnection();
				dbConnection = null;
			}
		}

		return isWritten;
	}

	private PreparedStatement createStudentUpdateStmt(Connection dbConnection,
			String studentUpdate, Student student) throws SQLException {

		PreparedStatement studentStmt = dbConnection
				.prepareStatement(studentUpdate);
		studentStmt.setLong(1, student.getPhone());
		studentStmt.setInt(2, student.getGrade().getCode());
		studentStmt.setLong(3, student.getStartDate().getTime());
		studentStmt.setDouble(4, student.getMonthlyTution());
		studentStmt.setString(5, student.getEmail());

		studentStmt.setString(6, student.getFirstName());
		studentStmt.setString(7, student.getLastName());

		return studentStmt;
	}

	/**
	 * Writes the given {@code student} into the database.
	 */
	public boolean writeStudent(Student student) {
		boolean isWritten = false;
		StudentDBConnection studentDBConnection = StudentDBConnection
				.getStudentDBConnection();
		Connection dbConnection = null;

		try {
			dbConnection = studentDBConnection.getConnection();
			PreparedStatement studentStmt = createStudentStmt(dbConnection,
					StudentDBScripts.STUDENT_INSERT, student);
			isWritten = studentStmt.execute();

			dbConnection.commit();

		} catch (SQLException sqle) {
			LOG.error("SQL Error while creating the student in DB: " + sqle);
		} finally {
			if (dbConnection != null) {
				studentDBConnection.closeConnection();
				dbConnection = null;
			}
		}

		return isWritten;
	}

	private PreparedStatement createStudentStmt(Connection dbConnection,
			String insertString,
			Student student) throws SQLException {
		PreparedStatement studentStmt = dbConnection
				.prepareStatement(insertString);
		studentStmt.setInt(1, student.getStudentId());
		studentStmt.setString(2, student.getFirstName());
		studentStmt.setString(3, student.getLastName());
		studentStmt.setLong(4, student.getPhone());
		studentStmt.setInt(5, student.getGrade().getCode());
		studentStmt.setLong(6, student.getStartDate().getTime());
		studentStmt.setDouble(7, student.getMonthlyTution());
		studentStmt.setString(8, student.getEmail());

		return studentStmt;
	}

	public void deleteStudent(Student student) {

		StudentDBConnection studentDbConnection = StudentDBConnection
				.getStudentDBConnection();
		Connection dbConnection = null;

		try {
			dbConnection = studentDbConnection.getConnection();

			PreparedStatement studentDeleteStmt = dbConnection
					.prepareStatement(StudentDBScripts.STUDENT_DELETE);
			studentDeleteStmt.setInt(1, student.getStudentId());
			studentDeleteStmt.execute();

			PreparedStatement paymentDeleteStmt = dbConnection
					.prepareStatement(StudentDBScripts.STUDENT_DELETE);
			paymentDeleteStmt.setInt(1, student.getStudentId());
			paymentDeleteStmt.execute();

			dbConnection.commit();

		} catch (SQLException sqle) {
			LOG.debug("SQL Error while reading payments: " + sqle);
		}

		finally {
			if (dbConnection != null) {
				studentDbConnection.closeConnection();
				dbConnection = null;
			}
		}
	}

	public static void main(String... args) {
		BasicConfigurator.configure();
		StudentDao studentDao = StudentDao.getStudentDao();
		Set<Student> studentList = studentDao.readStudents();
	}

}
