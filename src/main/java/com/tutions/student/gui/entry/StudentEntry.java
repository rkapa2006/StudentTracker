package com.tutions.student.gui.entry;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import jfxtras.labs.scene.control.BigDecimalField;

import org.apache.log4j.Logger;

import com.tutions.student.common.StudentGrade;
import com.tutions.student.common.StudentUtil;
import com.tutions.student.service.StudentPersistenceService;
import com.tutions.student.service.StudentService;
import com.tutions.student.vo.Student;
import com.tutions.student.vo.StudentFactory;

public class StudentEntry {

	private static final Logger LOG = Logger.getLogger(StudentEntry.class);

	private static final String BUTTON_ENTER = "Enter";
	private static final String BUTTON_EDIT = "Edit";
	private static final int FIELD_WIDTH = 275;

	private final Student student;
	private TextField firstNameField;
	private TextField lastNameField;
	private TextField phoneField;
	private ComboBox<StudentGrade> gradeCombo;
	private TextField startDateField;
	private BigDecimalField monthlyTutionField;
	private TextField mailIdField;

	private StudentService studentService;
	private PaymentEntry paymentEntry;
	private Text errorText;
	private Button enter;

	public StudentEntry(Student student) {
		this.student = student;
	}

	public void setStudentService(StudentService studentService) {
		this.studentService = studentService;
	}

	public StudentService getStudentService() {
		return this.studentService;
	}

	public Pane createPane() {

		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(10, 10, 10, 10));

		gridPane.setHgap(10);
		gridPane.setVgap(10);

		Label firstNameLabel = new Label("Name: ");
		firstNameField = new TextField();
		firstNameField.setPromptText("First Name");
		gridPane.add(firstNameLabel, 0, 0);
		HBox nameBox = new HBox(5);
		HBox.setHgrow(firstNameField, Priority.ALWAYS);
		lastNameField = new TextField();
		lastNameField.setPromptText("Last Name");
		nameBox.getChildren().addAll(
				Arrays.asList(firstNameField, lastNameField));
		gridPane.add(nameBox, 1, 0, 3, 1);

		Label phoneLabel = new Label("Phone: ");
		phoneField = new TextField();
		phoneField.setPromptText("999.999.9999");
		gridPane.add(phoneLabel, 0, 1);
		gridPane.add(phoneField, 1, 1);

		Label gradeLabel = new Label("Grade:");
		gradeCombo = createStudentGradeCombo();

		gridPane.add(gradeLabel, 2, 1);
		gridPane.add(gradeCombo, 3, 1);
		gradeCombo.setPrefWidth(FIELD_WIDTH);
		GridPane.setHalignment(gradeLabel, HPos.RIGHT);
		GridPane.setHalignment(gradeCombo, HPos.RIGHT);

		Label startDateLabel = new Label("Start Date:");
		startDateField = new TextField();
		startDateField.setPromptText(StudentUtil.DATE_FORMAT);
		gridPane.add(startDateLabel, 0, 2);
		gridPane.add(startDateField, 1, 2);

		Label monthlyTutionLabel = new Label("Monthly Tution:");
		monthlyTutionField = createMonthlyTution();
		gridPane.add(monthlyTutionLabel, 2, 2);
		gridPane.add(monthlyTutionField, 3, 2);
		monthlyTutionField.setPrefWidth(FIELD_WIDTH);
		GridPane.setHalignment(monthlyTutionLabel, HPos.RIGHT);
		GridPane.setHalignment(monthlyTutionField, HPos.RIGHT);

		Label mailIdLabel = new Label("Email Id:");
		mailIdField = new TextField();
		mailIdField.setPromptText("yourId@mail.com");
		gridPane.add(mailIdLabel, 0, 3);
		gridPane.add(mailIdField, 1, 3);

		enter = new Button(BUTTON_ENTER);
		enter.setPrefWidth(100);
		enter.setOnAction(createEnterActionHandler());

		GridPane.setHalignment(enter, HPos.RIGHT);
		gridPane.add(enter, 3, 4);

		errorText = new Text();
		gridPane.add(errorText, 1, 5, 3, 1);
		errorText.setWrappingWidth(300);

		checkAndMakeEdit();

		ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(15);

		ColumnConstraints column2 = new ColumnConstraints();
		column2.setPercentWidth(30);

		ColumnConstraints column3 = new ColumnConstraints();
		column3.setPercentWidth(15);

		ColumnConstraints column4 = new ColumnConstraints();
		column4.setPercentWidth(30);
		column4.setFillWidth(true);

		setData();

		gridPane.getColumnConstraints().addAll(column1, column2, column3,
				column4);
		gridPane.setId("student-entry-pane");
		BorderPane borderPane = createContainer(gridPane);
		borderPane.setCenter(gridPane);

		paymentEntry = new PaymentEntry();
		Pane paymentEntryPane = paymentEntry.createPane();
		paymentEntry.setStudent(student);

		VBox vBox = new VBox();
		vBox.getChildren().addAll(borderPane, paymentEntryPane);
		VBox.setVgrow(paymentEntryPane, Priority.ALWAYS);

		ScrollPane scroll = new ScrollPane();
		scroll.setContent(scroll);
		return vBox;
	}

	private BigDecimalField createMonthlyTution() {
		BigDecimalField monthlyTution = new BigDecimalField();
		monthlyTution.getStyleClass().add("bigDecimalField");
		monthlyTution.setMinValue(BigDecimal.ZERO);
		monthlyTution.setMaxValue(BigDecimal.valueOf(1000.00));
		monthlyTution.setStepwidth(BigDecimal.valueOf(5));
		monthlyTution.setNumber(BigDecimal.ZERO);
		NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
		monthlyTution.setFormat(currencyFormat);

		return monthlyTution;
	}

	private void checkAndMakeEdit() {
		if (!student.isEmpty()) {
			firstNameField.setEditable(false);
			lastNameField.setEditable(false);
			enter.setText(BUTTON_EDIT);
		}
	}

	private ComboBox<StudentGrade> createStudentGradeCombo() {
		ComboBox<StudentGrade> studentGradeCombo = new ComboBox<StudentGrade>();
		ObservableList<StudentGrade> gradeList = FXCollections
				.observableArrayList(StudentGrade.getAllGrades());
		studentGradeCombo.setItems(gradeList);

		studentGradeCombo
				.setCellFactory(new Callback<ListView<StudentGrade>, ListCell<StudentGrade>>() {

					@Override
					public ListCell<StudentGrade> call(
							ListView<StudentGrade> listView) {
						return createListCell();
					}
				});

		studentGradeCombo.setButtonCell(createListCell());
		studentGradeCombo.getSelectionModel().selectLast();
		return studentGradeCombo;
	}
	
	private ListCell<StudentGrade> createListCell() {
		return new ListCell<StudentGrade>() {

			@Override
			protected void updateItem(StudentGrade studentGrade, boolean isEmpty) {
				super.updateItem(studentGrade, isEmpty);
				if (studentGrade != null && !isEmpty) {
					Text gradeDescription = new Text(
							studentGrade.getDescription());
					setGraphic(gradeDescription);
				}
			}

		};
	}

	private boolean validate() {
		boolean isValid = true;
		StringBuilder builder = new StringBuilder();

		if (isFirstNameEmpty()) {
			isValid = false;
			builder.append("First Name can not be empty. ");
			firstNameField.setId("error-field");
		}

		if (isLastNameEmpty()) {
			isValid = false;
			builder.append("Last Name can not be Empty. ");
			lastNameField.setId("error-field");
		}

		if (isPhoneNumberEmpty()) {
			isValid = false;
			builder.append("Phone Number can not be empty. ");
			phoneField.setId("error-field");
		}

		if (!isPhoneNumberValid()) {
			isValid = false;
			builder.append("Phone Number is not valid. ");
			phoneField.setId("error-field");
		}

		if (isStartDateEmpty()) {
			isValid = false;
			builder.append("Start Date can not be empty. ");
			startDateField.setId("error-field");
		} else if (!isStartDateValid()) {
			isValid = false;
			builder.append("Start Date is not valid. ");
			startDateField.setId("error-field");
		}

		if (!isMonthlyTutionValid()) {
			isValid = false;
			builder.append("Monthly Tution can not be under $5.00");
		}

		if (!isValidEmail()) {
			isValid = false;
			builder.append("Email is not valid.");
		}


		if (!isValid) {
			errorText.setText(builder.toString());
			errorText.setId("error-label");
		}

		return isValid;
	}

	private boolean isValidEmail() {
		boolean isValid = false;
		String eMail = mailIdField.getText();

		if (eMail != null && !eMail.isEmpty()) {

			try {
				InternetAddress emailAddress = new InternetAddress(eMail);
				isValid = true;
			} catch (AddressException e) {
				LOG.warn("Invalid email." + eMail);
			}
		}

		return isValid;
	}

	private boolean isMonthlyTutionValid() {
		BigDecimal monthlyTutionAmount = monthlyTutionField.getNumber();

		return monthlyTutionAmount.doubleValue() > 5.0;
	}

	private boolean isStartDateValid() {
		boolean isValid = false;

		SimpleDateFormat sdf = new SimpleDateFormat(StudentUtil.DATE_FORMAT);

		try {
			Date date = sdf.parse(startDateField.getText());
			isValid = true;
		} catch (ParseException e) {
			// Just validation checking.
		}
		return isValid;
	}

	private boolean isStartDateEmpty() {
		return startDateField.getText() == null
				|| startDateField.getText().isEmpty();
	}

	private boolean isPhoneNumberEmpty() {
		return phoneField.getText() == null || phoneField.getText().isEmpty();
	}

	private boolean isPhoneNumberValid() {
		boolean isValid = false;

		String phoneNumString = StudentUtil.getNumberFromString(phoneField
				.getText());

		if (phoneNumString.length() == 10) {
			isValid = true;
		}

		return isValid;
	}

	private boolean isLastNameEmpty() {
		return lastNameField.getText() == null
				|| lastNameField.getText().isEmpty();
	}

	private boolean isFirstNameEmpty() {
		return firstNameField.getText() == null
				|| firstNameField.getText().isEmpty();
	}

	private void setData() {

		if (!student.isEmpty()) {
			firstNameField.setText(student.getFirstName());
			lastNameField.setText(student.getLastName());
			phoneField.setText(Long.valueOf(student.getPhone()).toString());
			DateFormat dateFormat = new SimpleDateFormat(
					StudentUtil.DATE_FORMAT);
			startDateField.setText(dateFormat.format(student.getStartDate()));
			gradeCombo.getSelectionModel().select(student.getGrade());
			monthlyTutionField.setNumber(BigDecimal.valueOf(student
					.getMonthlyTution()));
			mailIdField.setText(student.getEmail());
		}
	}

	private EventHandler<ActionEvent> createEnterActionHandler() {
		return new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent actionEvent) {
				if (validate()) {
					Student student = StudentFactory.createNewStudent();
					student.setFirstName(firstNameField.getText());
					student.setLastName(lastNameField.getText());
					String phoneString = StudentUtil
							.getNumberFromString(phoneField.getText());
					student.setPhone(Long.valueOf(phoneString));
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							StudentUtil.DATE_FORMAT);

					try {
						student.setStartDate(dateFormat.parse(startDateField
								.getText()));
					} catch (ParseException e) {
						LOG.debug("Date Parse Error...."
								+ startDateField.getText());
						e.printStackTrace();
					}

					student.setGrade(gradeCombo.getSelectionModel()
							.getSelectedItem());
					student.setMonthlyTution(monthlyTutionField.getNumber()
							.doubleValue());
					student.setEmail(mailIdField.getText());
					studentService.addStudent(student);

					enterStudent(student);
					paymentEntry.setStudent(student);
					clearFields();
				}
			}

			private void enterStudent(Student student) {
				StudentPersistenceService persistenceService = StudentPersistenceService
						.createPersistenceService();
				if (enter.getText().equals(BUTTON_ENTER)) {
					persistenceService.writeStudent(student, false);
				} else if (enter.getText().equals(BUTTON_EDIT)) {
					persistenceService.writeStudent(student, true);
				}
			}

			private void clearFields() {
				firstNameField.setId("new-student-field");
				lastNameField.setId("new-student-field");
				phoneField.setId("new-student-field");
				startDateField.setId("new-student-field");

				errorText.setText("Successfully Entered the Student.");
				errorText.setId("success-label");
			}
		};
	}


	private BorderPane createContainer(Pane pane) {
		BorderPane borderPane = new BorderPane();
		Label title = new Label("Enter Student Info:");
		title.setId("sub-title");
		BorderPane.setMargin(title, new Insets(5, 5, 5, 5));
		borderPane.setTop(title);
		borderPane.setId("student-entry-title");
		return borderPane;
	}
}
