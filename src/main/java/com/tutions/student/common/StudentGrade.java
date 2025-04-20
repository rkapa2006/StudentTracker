package com.tutions.student.common;

import java.util.Arrays;
import java.util.List;

public enum StudentGrade {
	DEFAULT(1000, "Not Applicable"), FIRST(1001, "First Grade"), SECOND(1002,
			"Second Grade"), THIRD(1003, "Third Grade"), FOURTH(1004,
			"Fourth Grade"), FIFTH(1005, "Fifth Grade"), SIXTH(1006,
			"Sixth Grade"), SEVENTH(1007, "Seventh Grade"), EIGHTH(1008,
			"Eigth Grade"), NINTH(1009, "Ninth Grade"), TENTH(1010,
			"Tenth Grade"), ELEVENTH(1011, "Eleventh Grade"), TWELFTH(1012,
			"Twelvefth Grade");

	private String description;
	private int code;

	private StudentGrade(int code, String description) {
		this.description = description;
		this.code = code;
	}

	public String getDescription() {
		return this.description;
	}

	public int getCode() {
		return this.code;
	}

	public static List<StudentGrade> getAllGrades() {
		return Arrays.asList(FIRST, SECOND, THIRD, FOURTH, FIFTH, SIXTH,
				SEVENTH, EIGHTH, NINTH, TENTH, ELEVENTH, TWELFTH, DEFAULT);
	}

	/**
	 * Returns the student grade given the {@code code}
	 * 
	 * @param code
	 *            the integer code corresponding to the student grade required.
	 */
	public static StudentGrade getStudentGrade(int code) {
		StudentGrade studentGrade = StudentGrade.DEFAULT;

		switch (code) {
		case 1001:
			studentGrade = FIRST;
			break;
		case 1002:
			studentGrade = SECOND;
			break;
		case 1003:
			studentGrade = THIRD;
			break;
		case 1004:
			studentGrade = FOURTH;
			break;
		case 1005:
			studentGrade = FIFTH;
			break;
		case 1006:
			studentGrade = SIXTH;
			break;
		case 1007:
			studentGrade = SEVENTH;
			break;
		case 1008:
			studentGrade = EIGHTH;
			break;
		case 1009:
			studentGrade = NINTH;
			break;
		case 1010:
			studentGrade = TENTH;
			break;
		case 1011:
			studentGrade = ELEVENTH;
			break;
		case 1012:
			studentGrade = TWELFTH;
			break;
		default:
			studentGrade = DEFAULT;
		}

		return studentGrade;
	}
}
