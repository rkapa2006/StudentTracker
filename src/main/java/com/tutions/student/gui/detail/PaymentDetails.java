package com.tutions.student.gui.detail;

import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

import com.tutions.student.common.PaymentPdf;
import com.tutions.student.common.StudentUtil;
import com.tutions.student.paymentreceipt.email.StudentMailService;
import com.tutions.student.paymentreceipt.print.PRPrintService;
import com.tutions.student.service.StudentPersistenceService;
import com.tutions.student.vo.Payment;
import com.tutions.student.vo.Student;

/**
 * Payment Detail pane.
 */
public class PaymentDetails {

	private final Student student;
	private BorderPane borderPane;

	private StudentPersistenceService persistenceService;
	private ObservableList<Payment> observablePaymentList;

	private StudentPersistenceService getPersistenceService() {

		if (persistenceService == null) {
			persistenceService = StudentPersistenceService
					.createPersistenceService();
		}

		return persistenceService;
	}

	public PaymentDetails(Student student) {
		this.student = student;
	}

	public Pane createPane() {
		borderPane = new BorderPane();
		borderPane.setPadding(new Insets(5, 5, 5, 5));
		Text headerText = createHeaderText();
		HBox headerPane = new HBox();
		headerPane.setPadding(new Insets(5, 5, 5, 5));
		headerPane.setId("payment-header-pane");
		headerPane.getChildren().add(headerText);
		borderPane.setTop(headerPane);

		BorderPane.setAlignment(headerText, Pos.CENTER);
		borderPane.setCenter(createPaymentList());
		borderPane.setId("payment-header-pane");

		return borderPane;
	}

	public ListView<Payment> createPaymentList() {
		ListView<Payment> paymentListView = new ListView<Payment>();
		List<Payment> paymentList = getPersistenceService().getAllPayments(
				student);
		observablePaymentList = FXCollections.observableList(paymentList);
		paymentListView.setItems(observablePaymentList);

		paymentListView
				.setCellFactory(new Callback<ListView<Payment>, ListCell<Payment>>() {

					@Override
					public ListCell<Payment> call(ListView<Payment> arg0) {
						return createPaymentDetailCell();
					}
				});

		paymentListView.getSelectionModel().selectedItemProperty()
				.addListener(createPaymentListSelectionHandler());
		paymentListView.setId("payment-detail-content");
		return paymentListView;
	}

	private ListCell<Payment> createPaymentDetailCell() {
		return new ListCell<Payment>() {

			@Override
			public void updateItem(Payment payment, boolean isEmpty) {
				super.updateItem(payment, isEmpty);

				if (payment != null && !isEmpty) {
					HBox cellPane = new HBox(4);

					SimpleDateFormat simpleDf = new SimpleDateFormat(
							StudentUtil.DATE_FORMAT);
					String paidDate = simpleDf.format(payment.getPaidDate());
					Text text = new Text();
					text.setText("\""
							+ payment.getPaymentMode().getDescription() + "\""
							+ " received for the month \""
							+ payment.getMonth().getDescription() + ", "
							+ payment.getYear() + "\" on \"" + paidDate
							+ "\", for an amount: ");

					text.setTextAlignment(TextAlignment.CENTER);
					Text amountText = new Text(NumberFormat
							.getCurrencyInstance().format(
									payment.getPaymentAmount()));
					amountText.setTextAlignment(TextAlignment.RIGHT);
					amountText.setId("student-list-name");

					cellPane.setAlignment(Pos.CENTER_LEFT);
					Hyperlink deleteLink = new PaymentLink(payment);
					deleteLink.setText("Delete");
					deleteLink.setId("payment-hyperlink");
					deleteLink.setOnAction(createDeleteHandler());

					Hyperlink printLink = new PaymentLink(payment);
					printLink.setText("Print");
					printLink.setId("payment-hyperlink");
					printLink.setOnAction(createPrintEventHandler());

					Hyperlink emailLink = new PaymentLink(payment);
					emailLink.setText("Email");
					emailLink.setId("payment-hyperlink");
					emailLink.setOnAction(createEmailEventHandler());

					cellPane.getChildren().addAll(
							Arrays.asList(text, amountText, emailLink,
									printLink, deleteLink));
					super.setGraphic(cellPane);
				}
			}

			private EventHandler<ActionEvent> createEmailEventHandler() {

				return new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent actionEvent) {
						PaymentLink paymentLink = (PaymentLink) actionEvent
								.getSource();
						Payment payment = paymentLink.getPayment();

						Pane pane = createPaymentDetail(payment);
						pane.setId("payment-header-pane");
						borderPane.setBottom(pane);

						PaymentPdf paymentPdf = new PaymentPdf(student, payment);
						File paymentPdfFile = paymentPdf.createPdf();

						StudentMailService emailService = StudentMailService
								.getInstance();
						String subject = "Payment Receipt of "
								+ student.getFirstName() + " "
								+ student.getLastName() + " for the Month: "
								+ payment.getMonth() + "," + payment.getYear();

						SimpleDateFormat simpleDf = new SimpleDateFormat(
								StudentUtil.DATE_FORMAT);
						String paidDate = simpleDf
								.format(payment.getPaidDate());

						StringBuilder messageText = new StringBuilder();
						messageText.append("\""
								+ payment.getPaymentMode().getDescription()
								+ "\""
								+ " received for the month of \""
								+ payment.getMonth().getDescription() + ", "
								+ payment.getYear() + "\" on \"" + paidDate
								+ "\", for an amount: "
								+ NumberFormat.getCurrencyInstance().format(
										payment.getPaymentAmount()));
						messageText.append("\n\n"
								+ StudentUtil.TUTOR_SALUTATION);
						messageText
								.append("\nPlease find enclosed the receipt for the same.");
						messageText.append("\n\nThanks");
						messageText.append("\nMadhuri Paruchuri");


						emailService.send(student.getEmail(), subject,
								messageText.toString(), paymentPdfFile);
					}
				};

			}
		};
	}

	private EventHandler<ActionEvent> createDeleteHandler() {
		return new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent actionEvent) {
				PaymentLink paymentLink = (PaymentLink) actionEvent.getSource();
				Payment payment = paymentLink.getPayment();

				StudentPersistenceService persistenceService = StudentPersistenceService
						.createPersistenceService();
				persistenceService.deletePayment(payment);
				observablePaymentList.remove(payment);
			}
		};
	}

	private EventHandler<ActionEvent> createPrintEventHandler() {
		return new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent actionEvent) {
				PaymentLink paymentLink = (PaymentLink) actionEvent.getSource();
				Payment payment = paymentLink.getPayment();
				Pane pane = createPaymentDetail(payment);
				pane.setId("payment-header-pane");
				borderPane.setBottom(pane);
				PRPrintService printService = PRPrintService
						.createPRPrintService();

				printService.printReceipt(student, payment);
			}
		};
	}

	private ChangeListener<Payment> createPaymentListSelectionHandler() {
		return new ChangeListener<Payment>() {

			@Override
			public void changed(ObservableValue<? extends Payment> value,
					Payment oldValue, Payment newValue) {
				if (newValue != null) {
					Pane pane = createPaymentDetail(newValue);
					pane.setId("payment-header-pane");
					borderPane.setBottom(pane);

				}
			}
		};
	}

	private Pane createPaymentDetail(Payment payment) {
		BorderPane borderPane = new BorderPane();
		borderPane.setPadding(new Insets(0, 10, 15, 10));
		GridPane gridPane = new GridPane();
		// gridPane.setPadding(new Insets(10, 10, 10, 10));

		ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(25);
		column1.setHalignment(HPos.LEFT);
		ColumnConstraints column2 = new ColumnConstraints();
		column2.setPercentWidth(25);
		ColumnConstraints column3 = new ColumnConstraints();
		column3.setPercentWidth(25);
		column3.setHalignment(HPos.LEFT);
		ColumnConstraints column4 = new ColumnConstraints();
		column4.setPercentWidth(25);
		column4.setHalignment(HPos.LEFT);
		gridPane.getColumnConstraints().addAll(column1, column2, column3,
				column4);

		Text header = new Text("Payment Receipt for " + student.getFirstName()
				+ " " + student.getLastName());
		header.setId("payment-receipt-title");
		borderPane.setTop(header);
		BorderPane.setAlignment(header, Pos.CENTER);

		Text paymentMonthLabel = new Text("Month: ");
		paymentMonthLabel.setId("payment-receipt-label");
		Text paymentMonthField = new Text(payment.getMonth().getDescription()
				+ ", " + payment.getYear());
		paymentMonthField.setId("payment-receipt-field");
		gridPane.add(paymentMonthLabel, 0, 1);
		gridPane.add(paymentMonthField, 1, 1);

		Text paymentAmountLabel = new Text("Amount: ");
		paymentAmountLabel.setId("payment-receipt-label");
		Text paymentAmountField = new Text("$" + payment.getPaymentAmount());
		paymentAmountField.setId("payment-receipt-field");

		gridPane.add(paymentAmountLabel, 2, 1);
		gridPane.add(paymentAmountField, 3, 1);

		Text paymentDateLabel = new Text("Paid Date:");
		paymentDateLabel.setId("payment-receipt-label");
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				StudentUtil.DATE_FORMAT);
		Text paymentDateField = new Text(dateFormat.format(payment
				.getPaidDate()));
		paymentDateField.setId("payment-receipt-field");

		gridPane.add(paymentDateLabel, 0, 2);
		gridPane.add(paymentDateField, 1, 2);

		Text paymentModeLabel = new Text("Paid with:");
		paymentModeLabel.setId("payment-receipt-label");
		Text paymentModeField = new Text(payment.getPaymentMode()
				.getDescription());
		paymentModeField.setId("payment-receipt-field");

		gridPane.add(paymentModeLabel, 2, 2);
		gridPane.add(paymentModeField, 3, 2);
		
		borderPane.setCenter(gridPane);

		borderPane.setId("payment-receipt-pane");
		Pane stackPane = new StackPane();
		stackPane.getChildren().add(borderPane);
		return stackPane;
	}

	private Text createHeaderText() {
		SimpleDateFormat simpleDf = new SimpleDateFormat(
				StudentUtil.DATE_FORMAT);

		Text headerText = new Text("Payment Details of "
				+ student.getFirstName()
				+ " " + student.getLastName() + ", "
				+ student.getGrade().getDescription()
				+ ", "
				+ student.getPhone() + ", "
				+ simpleDf.format(student.getStartDate()));
		headerText.setId("payment-detail-title");

		return headerText;
	}

	private static class PaymentLink extends Hyperlink {
		Payment payment;

		public PaymentLink(Payment payment) {
			super();
			this.payment = payment;
		}

		public Payment getPayment() {
			return this.payment;
		}
	}

}
