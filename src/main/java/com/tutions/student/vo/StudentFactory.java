package com.tutions.student.vo;

import com.tutions.student.common.StudentUtil;

public class StudentFactory {

	public static Student createNewStudent() {
		Integer studentId = StudentUtil.createStudentId();

		return new Student(studentId);
	}

	public static Student createNewStudent(Integer studentId) {
		return new Student(studentId);
	}
}
