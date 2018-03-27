/*
 * Copyright 2017 Jean-Louis Pasturel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
*/
package org.jlp.logfouineur.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.stream.Stream;

import org.jlp.logfouineur.ui.controller.MouseEventHandler;
import org.jlp.logfouineur.util.ComparatorFile;

import javafx.collections.FXCollections;
import javafx.collections.ObservableListBase;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

// TODO: Auto-generated Javadoc
/**
 * The Class DialogOpen.
 */
public class DialogOpen extends Stage {

	/** The combo box project. */
	public static ComboBox<String> comboBoxProject = new ComboBox<String>();
	
	/** The combo box scn. */
	public static ComboBox<String> comboBoxScn = new ComboBox<String>();

	/** The tf begin scn. */
	public static TextField tfBeginScn = new TextField("");
	
	/** The tf end scn. */
	public static TextField tfEndScn = new TextField("");

	/**
	 * Instantiates a new dialog open.
	 *
	 * @param owner the owner
	 */
	public DialogOpen(Stage owner) {
		
		this.initOwner(owner);
		this.getIcons().add(new Image("/images/lflogo.png"));
		BorderPane bp = new BorderPane();
		Font font = Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 30);
		Text text = new Text(20, 20, "Open a Project / Scenario");
		HBox hb = new HBox();
		hb.setAlignment(Pos.CENTER);

		hb.getChildren().add(text);
		text.setFont(font);
		bp.setTop(hb);
		// Center part of BorderPane
		GridPane gp = new GridPane();
		gp.setAlignment(Pos.CENTER);
		Label lab1 = new Label("Existing Projects");

		// recuperer workspace
		Path path = FileSystems.getDefault().getPath(LogFouineurMain.workspace);

		// Filling ComboBox of projects
		Stream<String> stream = null;
		ObservableListBase<String> olist = null;

		try {
			stream = Files.list(path).filter(path1 -> path1.toFile().isDirectory()).sorted((path1, path2) -> ComparatorFile.compare(path1, path2))
					.map(path1 -> path1.toFile().getName());
					
			String[] stringArray = stream.toArray(size -> new String[size]);
			olist = (ObservableListBase<String>) FXCollections.observableArrayList(stringArray);

			comboBoxProject.setItems(olist);
			comboBoxProject.setId("comboBoxProject");
			comboBoxProject.setValue(olist.get(0));
			comboBoxProject.setStyle("-fx-font: 12px \"Arial\"; -fx-font-weight: bold");
			// adding listener to update scenarios combobox
			// Handle ComboBox event.
			LogFouineurMain.currentProject = olist.get(0);
			comboBoxProject.setOnAction(new MyComboBoxHandler());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Scene page2 = new Scene(bp, 800, 450);
		this.setScene(page2);
		lab1.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 20));
		gp.setHgap(80);
		gp.setVgap(10);
		gp.add(lab1, 0, 0);

		gp.add(comboBoxProject, 1, 0);

		// Filling ComboBox of Scenario of first project
		Stream<String> stream2 = null;
		ObservableListBase<String> olist2 = null;
		Path pathProject = FileSystems.getDefault()
				.getPath(LogFouineurMain.workspace + File.separator + comboBoxProject.getItems().get(0));

		try {
			stream2 = Files.list(pathProject).filter(path1 -> path1.toFile().isDirectory())
					.filter(path1 -> path1.toFile().getName().startsWith(LogFouineurMain.prefixScenario))
					.sorted((path1, path2) -> ComparatorFile.compare(path1, path2))
					.map(path1 -> path1.toFile().getName());
			String[] stringArray = stream2.toArray(size -> new String[size]);
			olist2 = (ObservableListBase<String>) FXCollections.observableArrayList(stringArray);

			comboBoxScn.setItems(olist2);
			comboBoxScn.setId("comboBoxScn");
			
			comboBoxScn.setValue(olist2.get(0));
			comboBoxScn.setStyle("-fx-font: 12px \"Arial\"; -fx-font-weight: bold");

			// adding listener to update TestFields Begin date / end date of the
			/// Handle ComboBox event.
			LogFouineurMain.currentScenario = olist2.get(0);
			comboBoxScn.setOnAction(new MyComboBoxHandler());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Label lab2 = new Label("Available Scenarios");
		lab2.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 20)); //$NON-NLS-1$

		gp.add(lab2, 0, 1);

		gp.add(comboBoxScn, 1, 1);

		// Load te properties of the scenario => Begin Date and End Date
		Properties propsScn = new Properties();
		String strPropsConf = pathProject + File.separator + "scenarios.properties";
		try {
			propsScn.load(Files.newInputStream(new File(strPropsConf).toPath()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		tfBeginScn.setPrefSize(300, 30);
		tfBeginScn.setText(propsScn.getProperty(olist2.get(0) + ".dateBegin")); //$NON-NLS-1$
		tfBeginScn.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12)); //$NON-NLS-1$

		tfEndScn.setPrefSize(300, 30);
		tfEndScn.setText(propsScn.getProperty(olist2.get(0) + ".dateEnd")); //$NON-NLS-1$
		tfEndScn.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12)); //$NON-NLS-1$

		// add \n
		gp.add(new Label(), 0, 3);

		// Begin Date et End Date of the sceanrio
		Label lab4 = new Label("Begin Date with format:\nyyyy/MM/dd:HH:mm:ss"); //$NON-NLS-1$
		lab4.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 16)); //$NON-NLS-1$

		Label lab5 = new Label("End Date with format:\nyyyy/MM/dd:HH:mm:ss"); //$NON-NLS-1$
		lab5.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 16)); //$NON-NLS-1$
		gp.add(lab4, 0, 4);

		gp.add(lab5, 1, 4);

		gp.add(tfBeginScn, 0, 5);

		gp.add(tfEndScn, 1, 5);

		HBox hb2 = new HBox();
		hb2.setAlignment(Pos.CENTER);
		hb2.setMinHeight(100);
		hb2.setSpacing(200);
		Button bOpen = new Button("Open");
		Button bCancel = new Button("Cancel");
		bOpen.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 20)); //$NON-NLS-1$
		bCancel.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 20)); //$NON-NLS-1$
		bOpen.setId("bOpenProject");
		hb2.getChildren().add(bOpen);
		hb2.getChildren().add(bCancel);
		// gp.add(bCancel, 1, 2);
		bp.setCenter(gp);
		bp.setBottom(hb2);
		
		

		this.setTitle("Open an existing project");
		this.initModality(Modality.APPLICATION_MODAL);

		// Add Action Handler for Create by
		bOpen.setOnMouseClicked(new MouseEventHandler(this));
		// Add Action Handler for Cancel
		bCancel.setOnMouseClicked(e -> this.close());

		this.show();
	}

}

class MyComboBoxHandler implements EventHandler<ActionEvent> {

	@SuppressWarnings("unchecked")
	@Override
	public void handle(ActionEvent event) {
		// TODO Auto-generated method stub
		if (((ComboBox<String>) event.getSource()).getId().equals("comboBoxProject")) {
			String project = ((ComboBox<String>) event.getSource()).getSelectionModel().getSelectedItem();
			if(null != project) {
			LogFouineurMain.currentProject = project;
			// Load properties off all scenarios
			
			// update scenario combobox
			Stream<String> stream2 = null;
			Path pathProject = FileSystems.getDefault().getPath(LogFouineurMain.workspace + File.separator + project);
			String pathPropsScns=LogFouineurMain.workspace + File.separator + project+File.separator+"scenarios.properties";
			try(FileInputStream fis=new FileInputStream(pathPropsScns)){
				LogFouineurMain.scenariosProps.load(fis);
				
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			ObservableListBase<String> olist2 = null;
			if (null != project) {
				try {
					stream2 = Files.list(pathProject).filter(path1 -> path1.toFile().isDirectory())
							.filter(path1 -> path1.toFile().getName().startsWith(LogFouineurMain.prefixScenario))
							.sorted((path1, path2) -> ComparatorFile.compare(path1, path2))
							.map(path1 -> path1.toFile().getName());
					String[] stringArray = stream2.toArray(size -> new String[size]);
					olist2 = (ObservableListBase<String>) FXCollections.observableArrayList(stringArray);

					DialogOpen.comboBoxScn.setItems(olist2);
					DialogOpen.comboBoxScn.setValue(olist2.get(0));
					LogFouineurMain.currentScenario = olist2.get(0);
					// RFetrieve dates in scenarios.properties for this
					// project/scenario
					Properties propsScn = new Properties();
					String strPropsConf = pathProject.toString() + File.separator + "scenarios.properties";
					try {
						propsScn.load(Files.newInputStream(new File(strPropsConf).toPath()));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					DialogOpen.tfBeginScn.setText(propsScn.getProperty(olist2.get(0) + ".dateBegin"));
					DialogOpen.tfEndScn.setText(propsScn.getProperty(olist2.get(0) + ".dateEnd"));

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// activate/deactivate Menu
			LogFouineurMain.handleMenuState(false);

			LogFouineurMain.primaryStage.setTitle("LogFouineurMain V1.0 : Project : " + LogFouineurMain.currentProject
					+ ", with scenario : " + LogFouineurMain.currentScenario);
			}
		} else if (((ComboBox<String>) event.getSource()).getId().equals("comboBoxScn")) {
			String scenario = ((ComboBox<String>) event.getSource()).getSelectionModel().getSelectedItem();
			LogFouineurMain.currentScenario = scenario;
			// update scenario combobox
			Path pathProject = FileSystems.getDefault()
					.getPath(LogFouineurMain.workspace + File.separator + LogFouineurMain.currentProject);

			// RFetrieve dates in scenarios.properties for this project/scenario
			Properties propsScn = new Properties();
			String strPropsConf = pathProject.toString() + File.separator + "scenarios.properties";
			try {
				propsScn.load(Files.newInputStream(new File(strPropsConf).toPath()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DialogOpen.tfBeginScn.setText(propsScn.getProperty(scenario + ".dateBegin"));
			DialogOpen.tfEndScn.setText(propsScn.getProperty(scenario + ".dateEnd"));

			// activate/deactivate Menu
			LogFouineurMain.handleMenuState(true);

			LogFouineurMain.primaryStage.setTitle("LogFouineurMain V1.0 : Project : " + LogFouineurMain.currentProject
					+ ", with scenario : " + LogFouineurMain.currentScenario);

		}

	}

}
