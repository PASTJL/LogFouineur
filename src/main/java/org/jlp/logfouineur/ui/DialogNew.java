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

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;

import org.jlp.logfouineur.ui.controller.MouseEventHandler;

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
import javafx.scene.image.Image;
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
 * The Class DialogNew.
 */
public class DialogNew extends Stage {
	
	/** The new project. */
	public static String newProject=""; //$NON-NLS-1$
	
	/** The combo box. */
	public static ComboBox<String> comboBox = new ComboBox<String>();
	
	/** The tf name project. */
	public static TextField tfNameProject = new TextField();
	
	/** The tf name scenario. */
	public static TextField tfNameScenario = new TextField("");
	
	/** The tf begin scn. */
	public static TextField tfBeginScn = new TextField("");
	
	/** The tf end scn. */
	public static TextField tfEndScn = new TextField("");
	
	/**
	 * Instantiates a new dialog new.
	 *
	 * @param owner the owner
	 */
	public DialogNew(Stage owner) {
		this.initOwner(owner);
		getIcons().add(new Image("/images/lflogo.png"));
		BorderPane bp = new BorderPane();
		Font font = Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 30); //$NON-NLS-1$
		Text text = new Text(20, 20, Messages.getString("DialogNew.2")); //$NON-NLS-1$
		HBox hb = new HBox();
		hb.setAlignment(Pos.CENTER);

		hb.getChildren().add(text);
		text.setFont(font);
		bp.setTop(hb);
		// Center part of BorderPane
		GridPane gp = new GridPane();
		gp.setAlignment(Pos.CENTER);
		Label lab1 = new Label(Messages.getString("DialogNew.3")); //$NON-NLS-1$
		
		// recuperer workspace
		Path path = FileSystems.getDefault().getPath(LogFouineurMain.workspace);
		Stream<String> stream = null;
		ObservableListBase<String> olist = null;

		try {
			stream = Files.list(path).filter(path1 -> path1.toFile().isDirectory())
					.map(path1 -> path1.toFile().getName());
			String[] stringArray = stream.toArray(size -> new String[size]);
			olist = (ObservableListBase<String>) FXCollections.observableArrayList(stringArray);

			comboBox.setItems(olist);
			comboBox.setValue(olist.get(0));
			comboBox.setStyle("-fx-font: 12px \"Arial\"; -fx-font-weight: bold");
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

		gp.add(comboBox, 1, 0);
		Label lab2 = new Label(Messages.getString("DialogNew.5")); //$NON-NLS-1$
		lab2.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 20)); //$NON-NLS-1$

		
		tfNameProject.setPrefSize(300, 30);
		tfNameProject.setPromptText(Messages.getString("DialogNew.1")); //$NON-NLS-1$
		tfNameProject.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12)); //$NON-NLS-1$
		gp.add(lab2, 0, 1);

		gp.add(tfNameProject, 1, 1);
		
		Label lab3 = new Label("Scenario name");  //$NON-NLS-1$
		lab3.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 20));  //$NON-NLS-1$
		tfNameScenario.setPrefSize(300, 30);
		tfNameScenario.setPromptText(Messages.getString("DialogNew.9")); //$NON-NLS-1$
		tfNameScenario.setTooltip(new Tooltip(Messages.getString("DialogNew.10")+LogFouineurMain.prefixscenario+"default")); //$NON-NLS-1$ //$NON-NLS-2$
		tfNameScenario.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12)); //$NON-NLS-1$
		
		
		tfBeginScn.setPrefSize(300, 30);
		tfBeginScn.setPromptText(Messages.getString("DialogNew.14")); //$NON-NLS-1$
		tfBeginScn.setTooltip(new Tooltip(Messages.getString("DialogNew.15"))); //$NON-NLS-1$
		tfBeginScn.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12)); //$NON-NLS-1$
		
		tfEndScn.setPrefSize(300, 30);
		tfEndScn.setPromptText(Messages.getString("DialogNew.17")); //$NON-NLS-1$
		tfEndScn.setTooltip(new Tooltip(Messages.getString("DialogNew.18")+ new SimpleDateFormat("yyyy/MM/dd:HH:mm:ss").format(new Date()))); //$NON-NLS-1$ //$NON-NLS-2$
		tfEndScn.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12)); //$NON-NLS-1$
		
		gp.add(lab3, 0, 2);

		gp.add(tfNameScenario, 1, 2);
		
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
		Button bCreate =new Button(Messages.getString("DialogNew.0")); //$NON-NLS-1$
		Button bCancel =new Button(Messages.getString("DialogNew.8")); //$NON-NLS-1$
		bCreate.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 20)); //$NON-NLS-1$
		bCancel.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 20)); //$NON-NLS-1$
		bCreate.setId("bNewProjectCreate"); //$NON-NLS-1$
		hb2.getChildren().add(bCreate);
		hb2.getChildren().add(bCancel);
		//gp.add(bCancel, 1, 2);
		bp.setCenter(gp);
		bp.setBottom(hb2);
		
		this.setTitle(Messages.getString("DialogNew.12")); //$NON-NLS-1$
		this.initModality(Modality.APPLICATION_MODAL);
		
		// Add Action Handler for Create by
		bCreate.setOnMouseClicked(new MouseEventHandler(this));
		// Add Action Handler for Cancel
		bCancel.setOnMouseClicked(e -> this.close());
		
		this.show();
	}

}
