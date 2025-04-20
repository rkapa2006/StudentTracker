package com.tutions.student.common;


import java.io.File;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.tutions.student.vo.Payment;
import com.tutions.student.vo.Student;

public class PaymentPdf {
	private final static Logger LOG = Logger.getLogger(PaymentPdf.class);
	private static String DIR = "temp";
	private static String EXT = ".pdf";
	private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
			Font.BOLD);
	private final Student student;
	private final Payment payment;

	private final File paymentFile;

	public PaymentPdf(Student student, Payment payment) {
		this.student = student;
		this.payment = payment;
		new File(DIR).mkdirs();
		paymentFile = new File(DIR, Integer.valueOf(payment.getPaymentId())
				+ EXT);
	}

	public File createPdf() {
		try {
			Document document = new Document();
			PdfWriter.getInstance(document, new FileOutputStream(paymentFile));
			document.open();
			addTitlePage(document);
			document.close();
		} catch (Exception e) {
			LOG.debug("Error writing payment pdf.");
		}

		return paymentFile;
	}

	private void addTitlePage(Document document) throws DocumentException {
		Paragraph preface = new Paragraph();

		String paymentTitle = "Payment Receipt for the Month of "
				+ payment.getMonth().getDescription() + ", "
				+ payment.getYear();

		Paragraph titlePara = new Paragraph(paymentTitle, smallBold);
		titlePara.setAlignment(Element.ALIGN_CENTER);
		preface.add(titlePara);
		addEmptyLine(preface, 1);

		createTable(preface);
		addEmptyLine(preface, 1);

		Paragraph footer = new Paragraph("Thank u for the payment", smallBold);
		footer.setAlignment(Element.ALIGN_CENTER);
		preface.add(footer);

		Paragraph signature = new Paragraph(StudentUtil.TUTOR_NAME + "\nEmail:"
				+ StudentUtil.TUTOR_MAIL_ID);
		signature.setAlignment(Element.ALIGN_CENTER);
		preface.add(signature);

		document.add(preface);
	}

	private void createTable(Paragraph preface) throws DocumentException {
		PdfPTable table = new PdfPTable(2);

		table.addCell("Student Name");
		table.addCell(student.getName());

		table.addCell("Payment Amount");
		table.addCell(NumberFormat.getCurrencyInstance().format(
				payment.getPaymentAmount()));

		table.addCell("Paid On");
		SimpleDateFormat simpleDf = new SimpleDateFormat(
				StudentUtil.DATE_FORMAT);
		table.addCell(simpleDf.format(payment.getPaidDate()));

		table.addCell("Paid With");
		table.addCell(payment.getPaymentMode().getDescription());
		float[] widths = { 1, 3 };
		table.setWidths(widths);
		table.setWidthPercentage(80);
		preface.add(table);
	}

	private void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}
}
