package com.tutions.student;

import java.util.Set;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.tutions.student.data.StudentDao;
import com.tutions.student.gui.entry.StudentList;
import com.tutions.student.service.AppService;
import com.tutions.student.vo.Student;

public class Main extends Application implements AppService {

	private static Logger LOG = Logger.getLogger(Main.class);
	private static String TITLE = "Student Tracking System";
	private static String FOOT_NOTE = "CopyRight: 2012 Ramesh Kapa and Madhuri Paruchuri";

	private double mouseDragOffsetX = 0;
	private double mouseDragOffsetY = 0;

	private BorderPane parent;
	private final Image titleIcon = new Image("/images/student.png");

	public static void main(String... args) {
		BasicConfigurator.configure();
		LOG.debug("Starting Student Tracking System...");
		launch(args);
	}

	@Override
	public void start(final Stage stage) throws Exception {
		parent = new BorderPane();
		parent.setId("rootPane");

		Scene scene = new Scene(parent);
		scene.getStylesheets().add("stylesheets/studentstyle.css");

		Pane titlePane = createTitlePane();

		// add window dragging
		titlePane.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				mouseDragOffsetX = event.getSceneX();
				mouseDragOffsetY = event.getSceneY();
			}
		});

		titlePane.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {

					stage.setX(event.getScreenX() - mouseDragOffsetX);
					stage.setY(event.getScreenY() - mouseDragOffsetY);

			}
		});

		Pane closeButton = createCloseButtonPane();
		BorderPane topPane = new BorderPane();
		topPane.setCenter(titlePane);
		topPane.setRight(closeButton);
		BorderPane.setAlignment(closeButton, Pos.TOP_RIGHT);
		topPane.setId("title-pane");
		parent.setTop(topPane);

		Pane footerPane = createFooterPane();
		parent.setBottom(footerPane);

		Pane studentList = createStudentListPane();
		parent.setLeft(studentList);

		stage.setScene(scene);

		initStage(stage);
		stage.show();
	}

	public void setContent(Pane contentPane) {
		parent.setCenter(contentPane);

	}

	@Override
	public void setStudentPane(Pane studentPane) {
		setContent(studentPane);
	}

	private Pane createStudentListPane() {
		StudentDao studentDao = StudentDao.getStudentDao();
		Set<Student> students = studentDao.readStudents();
		StudentList studentList = new StudentList(students);
		studentList.setAppService(this);
		return studentList.createPane();
	}

	private Pane createCloseButtonPane() {
		VBox pane = new VBox();
		final Hyperlink exitLink = new Hyperlink("x");
		pane.getChildren().add(exitLink);
		VBox.setMargin(exitLink, new Insets(0, 5, 0, 0));

		exitLink.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				Platform.exit();
			}
		});

		exitLink.setId("button-close");

		return pane;
	}

	private Pane createTitlePane() {
		BorderPane titlePane = new BorderPane();
		titlePane.setPrefHeight(100);

		Text title = new Text(TITLE);
		title.setId("title");
		Reflection reflection = new Reflection();
		reflection.setFraction(0.8);
		title.setEffect(reflection);

		BorderPane.setAlignment(title, Pos.CENTER);
		titlePane.setCenter(title);

		titlePane.setLeft(new ImageView(titleIcon));
		titlePane.setId("title-pane");

		return titlePane;
	}

	private Pane createFooterPane() {
		StackPane footerPane = new StackPane();

		Text copyRight = new Text(FOOT_NOTE);
		copyRight.setId("footer");
		Reflection reflection = new Reflection();
		reflection.setFraction(0.8);
		copyRight.setEffect(reflection);

		StackPane.setAlignment(copyRight, Pos.CENTER);
		footerPane.getChildren().add(copyRight);
		footerPane.setPrefHeight(60);
		footerPane.setId("footer-pane");
		return footerPane;
	}


	private void initStage(Stage stage) {
		stage.initStyle(StageStyle.UNDECORATED);
		Pair<Double, Double> stageSize = computeSize();
		stage.setWidth(stageSize.getKey());
		stage.setHeight(stageSize.getValue());
		stage.centerOnScreen();
		stage.getIcons().add(titleIcon);
	}

	private Pair<Double, Double> computeSize() {
		Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();

		double width = (0.75) * screenSize.getWidth();
		double height = (0.85) * screenSize.getHeight();

		return new Pair<Double, Double>(width, height);
	}

}
