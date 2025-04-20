package com.tutions.student.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.tutions.student.common.Month;
import com.tutions.student.common.PaymentMode;
import com.tutions.student.vo.Payment;
import com.tutions.student.vo.Student;

/**
 * The payment dao {@PaymentDao}
 */
public class PaymentDao {

	private static Logger LOG = Logger.getLogger(PaymentDao.class);

	private static PaymentDao paymentDao;

	public static PaymentDao getPaymentDao() {

		if (paymentDao == null) {
			paymentDao = new PaymentDao();
		}

		return paymentDao;
	}

	/**
	 * Create {@code Payment} for the student in the db.
	 * 
	 * @param student
	 *            the student corresponding to the given {@code payment}.
	 * 
	 * @param payment
	 *            the payment to be persisted.
	 */
	public boolean createPaymentForStudent(Student student, Payment payment) {
		boolean isCreated = false;
		StudentDBConnection studentDbConection = StudentDBConnection
				.getStudentDBConnection();
		Connection dbConnection = null;

		try {
			dbConnection = studentDbConection.getConnection();
			PreparedStatement studentStmt = dbConnection
					.prepareStatement(StudentDBScripts.PAYMENT_INSERT);
			PreparedStatement statement = createPaymentStmt(studentStmt,
					payment);
			isCreated = statement.execute();

			dbConnection.commit();
		} catch (SQLException sqe) {
			LOG.debug("SQL Error while writing payment tod Db: ", sqe);
		}

		finally {
			if (dbConnection != null) {
				studentDbConection.closeConnection();
				dbConnection = null;
			}
		}

		return isCreated;
	}

	private PreparedStatement createPaymentStmt(PreparedStatement studentStmt,
			Payment payment) throws SQLException {
		studentStmt.setInt(1, payment.getPaymentId());
		studentStmt.setInt(2, payment.getStudentId());
		studentStmt.setDouble(3, payment.getPaymentAmount());
		studentStmt.setInt(4, payment.getPaymentMode().getCode());
		studentStmt.setInt(5, payment.getMonth().getCode());
		studentStmt.setInt(6, payment.getYear());
		studentStmt.setLong(7, payment.getPaidDate().getTime());

		return studentStmt;
	}


	/**
	 * Reads all payments for the given students.
	 * 
	 * @param student
	 *            the student for which the payments are to be read.
	 */
	public List<Payment> readPayment(Student student) {
		List<Payment> payments = new ArrayList();
		StudentDBConnection studentDbConnection = StudentDBConnection
				.getStudentDBConnection();
		Connection dbConnection = null;

		try {
			dbConnection = studentDbConnection.getConnection();
			PreparedStatement paymentStmt = dbConnection
					.prepareStatement(StudentDBScripts.PAYMENT_SELECT);
			paymentStmt.setInt(1, student.getStudentId());
			ResultSet paymentRs = paymentStmt.executeQuery();
			while(paymentRs.next()) {
				Payment payment = parsePayment(paymentRs);
				payments.add(payment);
			}
			
		} catch (SQLException sqle) {
			LOG.debug("SQL Error while reading payments: " + sqle);
		} 
		
		finally {
			if (dbConnection != null) {
				studentDbConnection.closeConnection();
				dbConnection = null;
			}
		}

		return payments;
	}

	private Payment parsePayment(ResultSet paymentRs) throws SQLException {

		int paymentId = paymentRs.getInt(1);
		int studentId = paymentRs.getInt(2);
		Payment payment = new Payment(paymentId, studentId);

		payment.setPaymentAmount(paymentRs.getDouble(3));

		int paymentModeCode = paymentRs.getInt(4);
		PaymentMode paymentMode = PaymentMode.getPaymentMode(paymentModeCode);
		payment.setPaymentMode(paymentMode);

		int monthCode = paymentRs.getInt(5);
		Month month = Month.getMonth(monthCode);
		payment.setMonth(month);

		int year = paymentRs.getInt(6);
		payment.setYear(year);

		long paidDate = paymentRs.getLong(7);
		Date datePaid = new Date(paidDate);
		payment.setPaidDate(datePaid);

		return payment;
	}

	public void deletePayment(Payment payment) {

		StudentDBConnection studentDbConnection = StudentDBConnection
				.getStudentDBConnection();
		Connection dbConnection = null;

		try {
			dbConnection = studentDbConnection.getConnection();
			PreparedStatement paymentStmt = dbConnection
					.prepareStatement(StudentDBScripts.PAYMENT_DELETE);
			paymentStmt.setInt(1, payment.getPaymentId());
			paymentStmt.execute();

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

}
