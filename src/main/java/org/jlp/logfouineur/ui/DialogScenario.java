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
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.jlp.logfouineur.ui.controller.ActionEventHandler;
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
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

// TODO: Auto-generated Javadoc
/**
 * The Class DialogScenario.
 */
public class DialogScenario extends Stage {
	
	/** The new project. */
	public static String newProject=""; //$NON-NLS-1$
	
	/** The combo box scn. */
	public static ComboBox<String> comboBoxScn = new ComboBox<String>();
	
	/** The tf name scenario. */
	public static TextField tfNameScenario = new TextField("");
	
	/** The tf begin scn. */
	public static TextField tfBeginScn = new TextField("");
	
	/** The tf end scn. */
	public static TextField tfEndScn = new TextField("");
	
	/** The b create change. */
	public static Button bCreateChange =new Button("Change");;
	
	/**
	 * Instantiates a new dialog scenario.
	 *
	 * @param owner the owner
	 */
	public DialogScenario(Stage owner) {
		this.initOwner(owner);
		BorderPane bp = new BorderPane();
		Font font = Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 26); //$NON-NLS-1$
		Text text = new Text(20, 20, "Add or change scenario for project : "+LogFouineurMain.currentProject);
		HBox hb = new HBox();
		hb.setAlignment(Pos.CENTER);
		tfNameScenario .setText("");
		hb.getChildren().add(text);
		text.setFont(font);
		bp.setTop(hb);
		// Center part of BorderPane
		GridPane gp = new GridPane();
		gp.setAlignment(Pos.CENTER);
		Label lab1 = new Label("Existing Scenario"); 
		
		// recuperer scenarios
		Path pathProject = FileSystems.getDefault().getPath(LogFouineurMain.workspace+File.separator+LogFouineurMain.currentProject);
		
		Stream<String> stream = null;
		ObservableListBase<String> olist = null;

		try {
			stream = Files.list(pathProject).filter(path1 -> path1.toFile().isDirectory())
					.filter(path1 -> path1.toFile().getName().startsWith(LogFouineurMain.prefixScenario))
					.sorted((path1, path2) -> ComparatorFile.compare(path1, path2))
					.map(path1 -> path1.toFile().getName());
			
			String[] stringArray = stream.toArray(size -> new String[size]);
			olist = (ObservableListBase<String>) FXCollections.observableArrayList(stringArray);

			comboBoxScn.setItems(olist);
			comboBoxScn.setValue(LogFouineurMain.currentScenario);
			comboBoxScn.setStyle("-fx-font: 12px \"Arial\"; -fx-font-weight: bold");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Scene page2 = new Scene(bp, 800, 450);
		this.setScene(page2);
		lab1.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 20)); //$NON-NLS-1$
		gp.setHgap(80);
		gp.setVgap(10);
		gp.add(lab1, 0, 0);

		gp.add(comboBoxScn, 1, 0);
		Label lab2 = new Label("Create a new Scenario");
		lab2.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 20)); //$NON-NLS-1$

		
		tfNameScenario.setPrefSize(300, 30);
		tfNameScenario.setPromptText("Name of new Scenario"); 
		tfNameScenario.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12)); //$NON-NLS-1$
		gp.add(lab2, 0, 1);

		gp.add(tfNameScenario, 1, 1);
		
		
		
		tfBeginScn.setPrefSize(300, 30);
		tfBeginScn.setPromptText(Messages.getString("DialogNew.14")); //$NON-NLS-1$
		tfBeginScn.setTooltip(new Tooltip(Messages.getString("DialogNew.15"))); //$NON-NLS-1$
		tfBeginScn.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12)); //$NON-NLS-1$
		
		tfEndScn.setPrefSize(300, 30);
		tfEndScn.setPromptText(Messages.getString("DialogNew.17")); //$NON-NLS-1$
		tfEndScn.setTooltip(new Tooltip(Messages.getString("DialogNew.18")+ new SimpleDateFormat("yyyy/MM/dd:HH:mm:ss").format(new Date()))); //$NON-NLS-1$ //$NON-NLS-2$
		tfEndScn.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12)); //$NON-NLS-1$
		
				// RFetrieve dates in scenarios.properties for this
		// project/scenario
		//Properties propsScn = new Properties();
		String strPropsConf = pathProject.toString() + File.separator + "scenarios.properties";
		try {
			LogFouineurMain.scenariosProps.load(Files.newInputStream(new File(strPropsConf).toPath()));
			// update Properties
			// Verify that dates are correct if not old date are
			// conserved
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tfBeginScn.setText(LogFouineurMain.scenariosProps.getProperty(LogFouineurMain.currentScenario+".dateBegin"));
		tfEndScn.setText(LogFouineurMain.scenariosProps.getProperty(LogFouineurMain.currentScenario+".dateEnd"));
		// add \n
		gp.add(new Label(), 0, 3);
		
		// Begin Date et End Date of the sceanrio
		Label lab4 = new Label(Messages.getString("DialogNew.21"));  //$NON-NLS-1$
		lab4.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 16));  //$NON-NLS-1$
		
		Label lab5 = new Label(Messages.getString("DialogNew.23"));  //$NON-NLS-1$
		lab5.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 16));  //$NON-NLS-1$
		gp.add(lab4, 0, 4);

		gp.add(lab5, 1, 4);
		
		gp.add(tfBeginScn, 0, 5);

		gp.add(tfEndScn, 1, 5);
		
		
		
		HBox hb2 = new HBox();
		hb2.setAlignment(Pos.CENTER);
		hb2.setMinHeight(100);
		hb2.setSpacing(200);
		 //$NON-NLS-1$
		Button bCancel =new Button(Messages.getString("DialogNew.8")); //$NON-NLS-1$
		bCreateChange.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 20)); //$NON-NLS-1$
		bCancel.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 20)); //$NON-NLS-1$
		bCreateChange.setId("bNewProjectCreate"); //$NON-NLS-1$
		hb2.getChildren().add(bCreateChange);
		hb2.getChildren().add(bCancel);
		//gp.add(bCancel, 1, 2);
		bp.setCenter(gp);
		bp.setBottom(hb2);
		
		this.setTitle(Messages.getString("DialogNew.12")); //$NON-NLS-1$
		this.initModality(Modality.APPLICATION_MODAL);
		
		// Add Action Handler for Create by
		bCreateChange.setId("bCreateChange");
		tfNameScenario.setId("tfNameScenarioCreate");
		tfNameScenario.setOnMouseEntered(new MouseEventHandler(this));
		tfNameScenario.setOnMouseExited(new MouseEventHandler(this));
		// Add Action Handler for Cancel
		bCancel.setOnMouseClicked(e -> this.close());
		bCreateChange.setOnMouseClicked(new MouseEventHandler(this));
		comboBoxScn.setOnAction(new ActionEventHandler(this));
		
		this.show();
	}

}
