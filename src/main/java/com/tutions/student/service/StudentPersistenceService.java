package com.tutions.student.service;

import java.util.List;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;

import com.tutions.student.data.PaymentDao;
import com.tutions.student.data.StudentDao;
import com.tutions.student.vo.Payment;
import com.tutions.student.vo.Student;

/**
 * This is the student tracking service.
 * 
 * @author rkapa
 * @since 1.0.00
 */
public class StudentPersistenceService {

	private static StudentPersistenceService persistenceService;

	private StudentPersistenceService() {
	}
	
	private StudentDao studentDao;

	private PaymentDao paymentDao;

	private PaymentDao getPaymentDao() {

		if (paymentDao == null) {
			paymentDao = PaymentDao.getPaymentDao();
		}

		return paymentDao;
	}

	private StudentDao getStudentDao() {

		if (studentDao == null) {
			studentDao = StudentDao.getStudentDao();
		}

		return studentDao;
	}

	public static StudentPersistenceService createPersistenceService() {

		if (persistenceService == null) {
			persistenceService = new StudentPersistenceService();
		}

		return persistenceService;
	}
	
	public List<Payment> getAllPayments(Student student) {
		return getPaymentDao().readPayment(student);
	}

	public void deleteStudent(Student student) {
		getStudentDao().deleteStudent(student);
	}

	public void deletePayment(Payment payment) {
		getPaymentDao().deletePayment(payment);
	}

	public void writeStudent(Student student, boolean modified) {

		if (modified) {
			getStudentDao().updateStudent(student);
		} else {
			getStudentDao().writeStudent(student);
		}
	}

	public static void main(String[] args) {
		BasicConfigurator.configure();
		StudentDao studentDao = StudentDao.getStudentDao();
		PaymentDao paymentDao = PaymentDao.getPaymentDao();

		Set<Student> students = studentDao.readStudents();

		for (Student student : students) {
			List<Payment> payments = paymentDao.readPayment(student);
			System.out.println(student);

			for (Payment payment : payments) {
				System.out.println(payment);
			}
		}
	}
}
