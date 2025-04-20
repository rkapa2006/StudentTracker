package com.tutions.student.paymentreceipt.print;

import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.tutions.student.common.PaymentPdf;
import com.tutions.student.vo.Payment;
import com.tutions.student.vo.Student;

public class PRPrintService {

	private static final Logger LOG = Logger.getLogger(PRPrintService.class);
	private static PRPrintService prPrintService;

	public static PRPrintService createPRPrintService() {
		if (prPrintService == null) {
			prPrintService = new PRPrintService();
		}

		return prPrintService;
	}

	private PRPrintService() {

	}

	public void printReceipt(Student student, Payment payment) {
		PaymentPdf paymentPdf = new PaymentPdf(student, payment);

		File prFile = paymentPdf.createPdf();

		try {
			PrintPdf printPdf = new PrintPdf(prFile);
			printPdf.print();

			if (!prFile.delete()) {
				prFile.deleteOnExit();
			}
		} catch (IOException | PrinterException e) {
			LOG.error("Error Printing Payment Receipt: ", e);
		}

	}
}
