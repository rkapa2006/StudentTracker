package com.tutions.student.common;

import java.util.Random;

public class StudentUtil {
	public static String DATE_FORMAT = "MM/dd/yyyy";
	public final static String FIELD_DELIM = ":";
	public final static String STUDENTS_DIR = "data//students";
	public final static String TUTOR_NAME = "Madhuri Paruchuri";
	public final static String TUTOR_MAIL_ID = "madhu_agritha@yahoo.co.in";
	public final static String TUTOR_SALUTATION = "Thanks for the payment.";
	
	private Random random;
	private static StudentUtil studentUtil;

	public static int createStudentId() {
		return Math.abs(getStudentUtil().getRandomNumber());
	}
	
	public static int createPaymentId() {
		return Math.abs(getStudentUtil().getRandomNumber());
	}

	private static StudentUtil getStudentUtil() {

		if (studentUtil == null) {
			studentUtil = new StudentUtil();
		}

		return studentUtil;
	}



	private int getRandomNumber() {

		if (random == null) {
			random = new Random();
		}

		return random.nextInt();
	}

	public static String getNumberFromString(String string) {
		String numberString = "";

		if (string != null && !string.isEmpty()) {
			StringBuilder numBuff = new StringBuilder();
			for (char character : string.toCharArray()) {
				if (Character.isDigit(character)) {
					numBuff.append(character);
				}
			}
			numberString = numBuff.toString();
		}

		return numberString;
	}


	public static void main(String... args) {
		System.out.println(createStudentId());
	}

}
