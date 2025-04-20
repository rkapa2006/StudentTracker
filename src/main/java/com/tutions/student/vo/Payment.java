package com.tutions.student.vo;

import java.util.Date;

import com.tutions.student.common.Month;
import com.tutions.student.common.PaymentMode;

public class Payment {
	
	private final int paymentId;

	private final int studentId;

	private double paymentAmount;

	private Month month;

	private int year;

	private Date paidDate;
	
	private PaymentMode paymentMode;

	public Payment(int paymentId, int studentId) {
		this.paymentId = paymentId;
		this.studentId = studentId;
	}

	public double getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(double paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public Month getMonth() {
		return month;
	}

	public void setMonth(Month month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Date getPaidDate() {
		return paidDate;
	}

	public void setPaidDate(Date paidDate) {
		this.paidDate = paidDate;
	}

	public int getPaymentId() {
		return paymentId;
	}

	public int getStudentId() {
		return studentId;
	}

	public void setPaymentMode(PaymentMode paymentMode) {
		this.paymentMode = paymentMode;
	}

	public PaymentMode getPaymentMode() {
		return this.paymentMode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + month.getCode();
		result = prime * result + studentId;
		result = prime * result + year;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Payment other = (Payment) obj;
		if (month != other.month)
			return false;
		if (studentId != other.studentId)
			return false;
		if (year != other.year)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Payment [paymentId=" + paymentId + ", studentId=" + studentId
				+ ", paymentAmount=" + paymentAmount + ", month=" + month
				+ ", year=" + year + ", paidDate=" + paidDate
				+ ", paymentMode=" + paymentMode + "]";
	}

	public boolean isEmpty() {
		return paymentAmount < 5;
	}
	
}
