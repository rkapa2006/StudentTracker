package com.tutions.student.vo;

import java.util.Date;

import com.tutions.student.common.StudentGrade;

/**
 * The student to be tracked.
 */
public class Student {

	private final Integer studentId;

	private String firstName;

	private String lastName;

	private StudentGrade grade;

	private Long phone;

	private Date startDate;

	private double monthlyTution;

	private String email;

	Student(Integer studentId) {
		this.studentId = studentId;
	}

	public Integer getStudentId() {
		return studentId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public StudentGrade getGrade() {
		return grade;
	}

	public void setGrade(StudentGrade studentGrade) {
		this.grade = studentGrade;
	}

	public Long getPhone() {
		return phone;
	}

	public void setPhone(Long phone) {
		this.phone = phone;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isEmpty() {
		return firstName == null || firstName.isEmpty();
	}

	public String getName() {
		return firstName + " " + lastName;
	}

	public double getMonthlyTution() {
		return monthlyTution;
	}

	public void setMonthlyTution(double monthlyTution) {
		this.monthlyTution = monthlyTution;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result
				+ ((lastName == null) ? 0 : lastName.hashCode());
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
		Student other = (Student) obj;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Student [studentId=" + studentId + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", grade=" + grade + ", phone="
				+ phone + ", startDate=" + startDate + ", monthlyTution="
				+ monthlyTution + "]";
	}

}
