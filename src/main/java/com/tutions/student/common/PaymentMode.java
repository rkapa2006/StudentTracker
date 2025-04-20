package com.tutions.student.common;

import java.util.ArrayList;
import java.util.List;

public enum PaymentMode {

	CHECK(2001, "Check"), CASH(2002, "Cash"), CARD(2003, "Card");

	private int code;

	private String description;

	private PaymentMode(int code, String description) {
		this.code = code;
		this.description = description;
	}

	public int getCode() {
		return this.code;
	}

	public String getDescription() {
		return this.description;
	}

	public static PaymentMode getPaymentMode(int code) {
		PaymentMode mode = null;

		switch (code) {

		case 2001:
			mode = CHECK;
			break;
		case 2002:
			mode = CASH;
			break;
		case 2003:
			mode = CARD;
			break;
		default:
			mode = CASH;
		}

		return mode;
	}

	public static List<PaymentMode> getAllModes() {
		List<PaymentMode> modeList = new ArrayList<PaymentMode>();

		modeList.add(CHECK);
		modeList.add(CASH);
		modeList.add(CARD);

		return modeList;
	}
}
