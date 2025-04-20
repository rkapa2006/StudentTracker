package com.tutions.student.gui.entry;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Callback;
import jfxtras.labs.scene.control.BigDecimalField;

import com.tutions.student.common.Month;
import com.tutions.student.common.PaymentMode;
import com.tutions.student.common.StudentUtil;
import com.tutions.student.data.PaymentDao;
import com.tutions.student.vo.Payment;
import com.tutions.student.vo.Student;

/**
 * The UI for payment.
 */
public class PaymentEntry {
    
        private static final int FIELD_WIDTH = 250;

	private BigDecimalField amountField;
	private ComboBox<PaymentMode> paymentModeCombo;
	private ComboBox<Month> forMonthCombo;
	private ComboBox<Integer> yearCombo;
	private Text errorText;
	private Button paid;

	private Student student;
	private Payment payment;


	public void setStudent(Student student) {
		this.student = student;

		if (student != null && !student.isEmpty()) {
			paid.setDisable(false);
			int paymentId = StudentUtil.createPaymentId();
			payment = new Payment(paymentId, student.getStudentId());
		}
	}

	public Pane createPane() {

		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(10, 10, 10, 10));

		gridPane.setHgap(10);
		gridPane.setVgap(10);

		Label amountLabel = new Label("Amount: ");
		amountField = createAmountField();
                amountField.setPrefWidth(250);
		gridPane.add(amountLabel, 0, 0);
		gridPane.add(amountField, 1, 0);

		Label paymentModeLabel = new Label("Payment Mode: ");
		paymentModeCombo = createPaymentModeCombo();
		paymentModeCombo.setPrefWidth(FIELD_WIDTH);
		gridPane.add(paymentModeLabel, 2, 0);
		gridPane.add(paymentModeCombo, 3, 0);
                GridPane.setHalignment(paymentModeCombo, HPos.RIGHT);

		Label forMonthLabel = new Label("For the Month: ");
		forMonthCombo = createMonthCombo();
		forMonthCombo.setPrefWidth(FIELD_WIDTH);
		gridPane.add(forMonthLabel, 0, 1);
		gridPane.add(forMonthCombo, 1, 1);

		Label yearLabel = new Label("Of the Year ");
		yearCombo = createYearCombo();
		yearCombo.setPrefWidth(FIELD_WIDTH);
		gridPane.add(yearLabel, 2, 1);
		gridPane.add(yearCombo, 3, 1);
                GridPane.setHalignment(paymentModeCombo, HPos.RIGHT);

		paid = new Button("Paid");
		paid.setDisable(true);
		paid.setOnAction(createPaidButtonHandler());
		paid.setPrefWidth(100);

		GridPane.setHalignment(paid, HPos.RIGHT);
		gridPane.add(paid, 3, 3);

		errorText = new Text();
		gridPane.add(errorText, 1, 3, 3, 1);

		ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(15);
		ColumnConstraints column2 = new ColumnConstraints();
		column2.setPercentWidth(25);
		
                ColumnConstraints column3 = new ColumnConstraints();
		column1.setPercentWidth(20);
		ColumnConstraints column4 = new ColumnConstraints();
		column2.setPercentWidth(25);
		column2.setFillWidth(true);

		gridPane.getColumnConstraints().addAll(column1, column2, column3, column4);
		gridPane.setId("student-entry-pane");
		BorderPane borderPane = createContainer(gridPane);
		borderPane.setCenter(gridPane);

		return borderPane;
	}

	private EventHandler<ActionEvent> createPaidButtonHandler() {
		return new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent actionEvent) {
				if (validate()) {
					clearErrors();
					Payment payment = new Payment(
							StudentUtil.createPaymentId(),
							student.getStudentId());
					payment.setPaymentAmount(amountField.getNumber()
							.doubleValue());
					payment.setPaymentMode(paymentModeCombo.getValue());
					payment.setMonth(forMonthCombo.getValue());
					payment.setYear(yearCombo.getValue());
					payment.setPaidDate(new Date());
					createPayment(student, payment);
				}
			}
			
		};
	}

	private void clearErrors() {
		amountField.setId("new-student-label");
		errorText.setText("");
	}

	private boolean validate() {
		double paymentAmount = amountField.getNumber().doubleValue();
		boolean isValid = paymentAmount >= 5.00;

		if (!isValid) {
			amountField.setId("error-field");
			errorText.setText("Minimum amount should be at least $5.00");
			errorText.setId("error-label");
		}

		return isValid;
	}

	private void createPayment(Student student, Payment payment) {

		if (student != null && !student.isEmpty() && payment != null
				& !payment.isEmpty()) {
			PaymentDao paymentDao = PaymentDao.getPaymentDao();
			paymentDao.createPaymentForStudent(student, payment);
			amountField.setNumber(BigDecimal.valueOf(0.0));
			errorText.setText("Payment succecessfully create for the student.");
			errorText.setId("success-label");
		}
	}

	private BigDecimalField createAmountField() {
		BigDecimalField amountField = new BigDecimalField();
		amountField.getStyleClass().add("bigDecimalField");
		amountField.setMinValue(BigDecimal.ZERO);
		amountField.setMaxValue(BigDecimal.valueOf(1000.00));
		amountField.setStepwidth(BigDecimal.valueOf(5));
		amountField.setNumber(BigDecimal.ZERO);
		NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
		amountField.setFormat(currencyFormat);

		return amountField;
	}

	private ComboBox<Integer> createYearCombo() {
		ComboBox<Integer> yearCombo = new ComboBox<Integer>();
		yearCombo.setItems(FXCollections.observableList(createYears()));

		yearCombo
				.setCellFactory(new Callback<ListView<Integer>, ListCell<Integer>>() {

					@Override
					public ListCell<Integer> call(ListView<Integer> arg0) {

						return createYearListCell();
					}

				});

		yearCombo.setButtonCell(createYearListCell());
		Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);
		yearCombo.getSelectionModel().select(currentYear);

		return yearCombo;
	}

	private List<Integer> createYears() {
		Calendar calendar = Calendar.getInstance();

		int year = calendar.get(Calendar.YEAR);
		int low = year - 3;
		int high = year + 3;

		List<Integer> yearList = new ArrayList();
		
		for (int i = low; i <= high; i++) {
			yearList.add(i);
		}

		return yearList;
	}

	private ListCell<Integer> createYearListCell() {
		return new ListCell<Integer>() {

			@Override
			public void updateItem(Integer year, boolean isEmpty) {
				super.updateItem(year, isEmpty);

				if (year != null && !isEmpty) {
					Text labelText = new Text(year.toString());
					setGraphic(labelText);
				}
			}
		};
	}

	private ComboBox<Month> createMonthCombo() {
		ComboBox<Month> monthCombo = new ComboBox<Month>();

		ObservableList<Month> monthList = FXCollections.observableList(Month
				.getAllMonths());
		monthCombo.setItems(monthList);

		monthCombo
				.setCellFactory(new Callback<ListView<Month>, ListCell<Month>>() {

					@Override
					public ListCell<Month> call(ListView<Month> arg0) {
						return createMonthListCell();
					}

				});

		monthCombo.setButtonCell(createMonthListCell());
		int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
		monthCombo.getSelectionModel().select(Month.getMonth(currentMonth));

		return monthCombo;
	}

	private ListCell<Month> createMonthListCell() {
		return new ListCell<Month>() {

			@Override
			public void updateItem(Month month, boolean isEmpty) {
				super.updateItem(month, isEmpty);
				if (month != null && !isEmpty) {
					Text labelText = new Text(month.getDescription());
					setGraphic(labelText);
				}
			}
		};
	}

	private ComboBox<PaymentMode> createPaymentModeCombo() {

		ComboBox<PaymentMode> paymentModeCombo = new ComboBox<PaymentMode>();
		paymentModeCombo.setItems(FXCollections.observableList(PaymentMode
				.getAllModes()));
		paymentModeCombo
				.setCellFactory(new Callback<ListView<PaymentMode>, ListCell<PaymentMode>>() {

					@Override
					public ListCell<PaymentMode> call(ListView<PaymentMode> listView) {
						return createPaymentModeCell();
					}
				});

		paymentModeCombo.setButtonCell(createPaymentModeCell());
		paymentModeCombo.getSelectionModel().selectFirst();

		return paymentModeCombo;
	}

	ListCell<PaymentMode> createPaymentModeCell() {
		return new ListCell<PaymentMode>() {

			@Override
			protected void updateItem(PaymentMode paymentMode, boolean isEmpty) {
				super.updateItem(paymentMode, isEmpty);

				if (paymentMode != null && !isEmpty) {
					Text labelText = new Text(paymentMode.getDescription());
					setGraphic(labelText);
				}
			}

		};
	}

	private BorderPane createContainer(Pane pane) {
		BorderPane borderPane = new BorderPane();
		Label title = new Label("Enter Payment:");
		title.setId("sub-title");
		BorderPane.setMargin(title, new Insets(5, 5, 5, 5));
		borderPane.setTop(title);
		borderPane.setId("student-entry-title");
		return borderPane;
	}
}
