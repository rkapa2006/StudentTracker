package com.tutions.student.gui.entry;

import java.util.Arrays;
import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

import org.apache.log4j.Logger;

import com.tutions.student.gui.detail.PaymentDetails;
import com.tutions.student.service.AppService;
import com.tutions.student.service.StudentPersistenceService;
import com.tutions.student.service.StudentService;
import com.tutions.student.vo.Student;
import com.tutions.student.vo.StudentFactory;

/**
 * This class represents list of students available in the system.
 * 
 * @author rkapa
 * 
 * @since 1.0.00
 */
public class StudentList implements StudentService {

	private static Logger LOG = Logger.getLogger(StudentList.class);

	private AppService appService;

	private final Set<Student> studentList;

	private ListView<Student> studentListView;
	private ObservableList<Student> students;

	public StudentList(Set<Student> studentList) {
		this.studentList = studentList;
	}

	public void setAppService(AppService appService) {
		this.appService = appService;
	}

	public Pane createPane() {
		BorderPane borderPane = new BorderPane();
		Pane topPane = new Pane();

		Label title = new Label("Student List: ");
		BorderPane.setMargin(topPane, new Insets(1, 0, 5, 1));
		title.setId("sub-title");
		topPane.setId("student-list-pane");
		topPane.getChildren().add(title);
		borderPane.setTop(topPane);

		studentListView = new ListView<Student>();
		students = FXCollections.observableArrayList();
		students.addAll(studentList);
		studentListView.setItems(students);

		studentListView
				.setCellFactory(new Callback<ListView<Student>, ListCell<Student>>() {

					@Override
					public ListCell<Student> call(ListView<Student> listView) {
						return createStudentListCell();
					}
				});

		studentListView.getSelectionModel().selectedItemProperty()
				.addListener(createStudentListSelectionHandler());

		studentListView.getItems().add(StudentFactory.createNewStudent());
		studentListView.getSelectionModel().selectFirst();
		borderPane.setCenter(studentListView);
		borderPane.setId("student-entry-pane");

		return borderPane;
	}

	private ListCell<Student> createStudentListCell() {


		return new ListCell<Student>() {

			@Override
			protected void updateItem(Student student, boolean isEmpty) {
				super.updateItem(student, isEmpty);
				VBox listCell = null;
				Hyperlink paymentDetail = null;
				Hyperlink deleteLink = new StudentLink(student);

				if (!isEmpty) {

					if (!student.isEmpty()) {
						listCell = new VBox(0);


						Text studentName = new Text(student.getFirstName()
								+ " " + student.getLastName());
						studentName.setId("student-list-name");

						paymentDetail = new StudentLink(student);
						paymentDetail.setText("Payment Details");
						paymentDetail.setId("student-hyperlink");

						deleteLink.setText("Delete");
						deleteLink.setId("student-hyperlink");
						deleteLink.setOnAction(createDeleteLinkHandler());

						GridPane gridPane = new GridPane();
						gridPane.setPadding(new Insets(0, 0, 0, 4));
						ColumnConstraints column1 = new ColumnConstraints();
						column1.setHalignment(HPos.LEFT);
						column1.setPercentWidth(70);
						ColumnConstraints column2 = new ColumnConstraints();
						column2.setPercentWidth(30);
						column2.setHalignment(HPos.RIGHT);
						gridPane.getColumnConstraints()
								.addAll(column1, column2);

						gridPane.add(studentName, 0, 0);
						gridPane.add(deleteLink, 1, 0);

						listCell.getChildren().addAll(
								Arrays.asList(gridPane, paymentDetail));
					} else {
						listCell = new VBox(2);
						Label newStudent = new Label();
						newStudent.setText("New Student");
						newStudent.setId("new-student-label");
						listCell.getChildren().add(newStudent);
					}
				}

				if (listCell != null) {
					listCell.setId("new-student-label");
				}
				if (paymentDetail != null) {
					paymentDetail.setOnAction(createPaymentDetailHandler());
				}

				setGraphic(listCell);
			}

		};

	}

	private ChangeListener<Student> createStudentListSelectionHandler() {
		return new ChangeListener<Student>() {

			@Override
			public void changed(ObservableValue<? extends Student> value,
					Student oldValue, Student newValue) {

				if (newValue != null) {
					Pane studentEntryPane = createStudentEntryPane(newValue);

					appService.setStudentPane(studentEntryPane);
				} else {
					Pane studentEntryPane = createStudentEntryPane(StudentFactory
							.createNewStudent());
					appService.setStudentPane(studentEntryPane);
				}

			}
		};
	}

	private EventHandler<ActionEvent> createDeleteLinkHandler() {
		return new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent actionEvent) {
				StudentLink studentLink = (StudentLink) actionEvent.getSource();
				Student student = studentLink.getStudent();
				StudentPersistenceService.createPersistenceService()
						.deleteStudent(student);
				students.remove(student);
				if (studentListView.getSelectionModel().getSelectedItem()
						.equals(student)) {
					appService.setStudentPane(null);
				}
			}
		};
	}

	private EventHandler<ActionEvent> createPaymentDetailHandler() {
		return new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent actionEvent) {
				StudentLink studentLink = (StudentLink) actionEvent.getSource();
				PaymentDetails paymentDetails = new PaymentDetails(
						studentLink.getStudent());
				Pane paymentDetailsPane = paymentDetails.createPane();
				appService.setStudentPane(paymentDetailsPane);
			}
		};
	}

	private Pane createStudentEntryPane(Student student) {
		StudentEntry studentEntry = new StudentEntry(student);
		studentEntry.setStudentService(this);

		return studentEntry.createPane();
	}

	@Override
	public boolean addStudent(Student student) {
		boolean contains = students.contains(student);

		if (!contains) {
			students.add(0, student);
		} else {
			students.remove(student);
			students.add(0, student);
		}

		return contains;
	}
	
	private static class StudentLink extends Hyperlink {
		Student student;

		public StudentLink(Student student) {
			super();
			this.student = student;
		}

		public void setStudent(Student student) {
			this.student = student;
		}

		public Student getStudent() {
			return this.student;
		}
	}
}
