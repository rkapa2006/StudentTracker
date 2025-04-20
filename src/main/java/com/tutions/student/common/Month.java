package com.tutions.student.common;

import java.util.Arrays;
import java.util.List;

/**
 * The enum that represents months of year.
 */
public enum Month {
	JAN(0, "January"), FEB(1, "February"), MAR(2, "March"), APR(3, "April"), MAY(
			4, "May"), JUN(5, "June"), JUL(6, "July"), AUG(7, "August"), SEP(8,
			"September"), OCT(9, "October"), NOV(10, "November"), DEC(11,
			"December");

	private int code;

	private String description;

	private Month(int code, String description) {
		this.code = code;
		this.description = description;
	}

	public int getCode() {
		return this.code;
	}

	public String getDescription() {
		return this.description;
	}

	public static List<Month> getAllMonths() {
		return Arrays.asList(JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT,
				NOV, DEC);
	}

	public static Month getMonth(int code) {
		Month month = null;

		switch (code) {
		case 0:
			month = JAN;
			break;
		case 1:
			month = FEB;
			break;
		case 2:
			month = MAR;
			break;
		case 3:
			month = APR;
			break;
		case 4:
			month = MAY;
			break;
		case 5:
			month = JUN;
			break;
		case 6:
			month = JUL;
			break;
		case 7:
			month = AUG;
			break;
		case 8:
			month = SEP;
			break;
		case 9:
			month = OCT;
			break;
		case 10:
			month = NOV;
			break;
		case 11:
			month = DEC;
			break;
		}

		return month;
	}
}
