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

import org.jlp.logfouineur.models.ParsingConfigHandler;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

// TODO: Auto-generated Javadoc
/**
 * The Class TemplateSaving.
 */
public class TemplateSaving extends Stage implements EventHandler<ActionEvent> {
	
	/** The tf template name. */
	public TextField tfTemplateName = new TextField();
	
	/** The lab 1. */
	public Label lab1 = new Label("Template Name");
	
	/** The button 1. */
	RadioButton button1 = new RadioButton("General Templates");
	
	/** The button 2. */
	RadioButton button2 = new RadioButton("Local Templates");
	
	/** The tg gen loc template. */
	ToggleGroup tgGenLocTemplate = new ToggleGroup();
	
	/** The ok button. */
	public Button okButton = new Button("OK");
	
	/** The cancel button. */
	public Button cancelButton = new Button("Cancel");
	
	/** The cb exiting template. */
	public ComboBox<String> cbExitingTemplate=new ComboBox<String>();

	/** The str path gen. */
	String strPathGen = LogFouineurMain.root + File.separator + "templates" + File.separator + "logparser";
	
	/** The str path loc. */
	String strPathLoc = LogFouineurMain.workspace + File.separator + LogFouineurMain.currentProject + File.separator
			+ "templates" + File.separator + "logparser";

	/**
	 * Instantiates a new template saving.
	 *
	 * @param owner the owner
	 */
	public TemplateSaving(Stage owner) {
		this.initOwner(owner);
		this.getIcons().add(new Image("/images/lflogo.png"));
		button1.setToggleGroup(tgGenLocTemplate);
		button2.setToggleGroup(tgGenLocTemplate);
		lab1.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 16));
		okButton.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		cancelButton.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		tfTemplateName.setPrefSize(200, 30);
		cbExitingTemplate.setPrefSize(200, 30);
		cbExitingTemplate.setStyle("-fx-font-family: Arial; -fx-font-size: 12; -fx-font-weight: Bold");
		tfTemplateName.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		tfTemplateName.setEditable(true);
		tfTemplateName.setPromptText("Fill it with Template Name");

		this.setTitle("LogFouineurMain V1.0 : Project : " + LogFouineurMain.currentProject + ", with scenario : "
				+ LogFouineurMain.currentScenario);
		//
		button1.setPadding(new Insets(10, 0, 10, 0));
		button1.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 14));
		button2.setPadding(new Insets(10, 0, 10, 0));
		button2.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 14));
		button1.setOnAction(this);
		button2.setOnAction(this);
		BorderPane bp = new BorderPane();
		bp.setPadding(new Insets(10, 0, 10, 0));
		HBox hb1 = new HBox();

		GridPane gp1 = new GridPane();
		gp1.setHgap(30);
		gp1.setVgap(60);

		Label lab2 = new Label("Saving a Template");
		lab2.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 20));
		hb1.getChildren().add(lab2);
		hb1.setAlignment(Pos.CENTER);
		gp1.setAlignment(Pos.CENTER);
		gp1.add(hb1, 0, 0);

		gp1.add(button1, 0, 1);
		gp1.add(button2, 1, 1);
		
		// Combox for listing template
		Label lab3=new Label("Existing Templates");
		lab3.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 16));
		this.cbExitingTemplate.getItems().addAll();
		ObservableList<String> lst=org.jlp.logfouineur.util.Utils.filesInDirectoryWithModel(strPathGen, ".+\\.properties$");
		System.out.println(lst.toString());
		this.cbExitingTemplate.setItems(lst);
		this.cbExitingTemplate.setOnAction(this);;
		gp1.add(lab3, 0, 2);
		gp1.add(cbExitingTemplate, 1, 2);
		
		
		gp1.add(lab1, 0, 3);
		gp1.add(tfTemplateName, 1, 3);

		bp.setCenter(gp1);

		cancelButton.setOnAction(e -> this.close());
		okButton.setId("okButton");
		okButton.setOnAction(this);
		okButton.setMinSize(90, 30);
		cancelButton.setMinSize(90, 30);

		Scene page2 = new Scene(bp, 800, 450);
		this.setScene(page2);
		button1.setSelected(true);
		// add buttons on bottom of BorderPane
		HBox hb2 = new HBox();

		GridPane gp3 = new GridPane();
		gp3.setHgap(300);
		gp3.add(okButton, 0, 0);
		gp3.add(cancelButton, 1, 0);
		hb2.getChildren().add(gp3);
		cancelButton.setOnAction(e -> this.close());
		okButton.setId("okButton");
		okButton.setOnAction(this);
		okButton.setMinSize(90, 30);
		cancelButton.setMinSize(90, 30);
		hb2.setAlignment(Pos.CENTER);
		bp.setBottom(hb2);
		this.initModality(Modality.APPLICATION_MODAL);
		this.showAndWait();

	}

	

	/**
	 * Handle.
	 *
	 * @param event the event
	 */
	/* (non-Javadoc)
	 * @see javafx.event.EventHandler#handle(javafx.event.Event)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void handle(ActionEvent event) {
		if(event.getSource() instanceof Button){
			if ((Button)event.getSource()==okButton){
				if(this.tfTemplateName.getText().length()>0){
					String file=tfTemplateName.getText();
					if(!file.endsWith(".properties"))file+=".properties";
					if(this.button1.isSelected()){
						String genTemplate=strPathGen+File.separator+file;
						
						new ParsingConfigHandler(LogFouineurMain.fileToParse).saveInFile(genTemplate);
					}
					else{
						String locTemplate=strPathLoc+File.separator+file;
						

						new ParsingConfigHandler(LogFouineurMain.fileToParse).saveInFile(locTemplate);
					}
					this.close();
				}
			}
		} else if (event.getSource() instanceof ComboBox){
			if ((ComboBox<String>)event.getSource()==this.cbExitingTemplate){
				tfTemplateName.setText(this.cbExitingTemplate.getValue());
				
			}
		} else if (event.getSource() instanceof RadioButton){
			if ((RadioButton)event.getSource()==this.button1 ||(RadioButton)event.getSource()==this.button2 ){
				if(button1.isSelected()){
					this.cbExitingTemplate.getItems().clear();
					ObservableList<String> lst=org.jlp.logfouineur.util.Utils.filesInDirectoryWithModel(strPathGen, ".+\\.properties$");
					this.cbExitingTemplate.setItems(lst);
					
				}else
				{
					this.cbExitingTemplate.getItems().clear();
					ObservableList<String> lst=org.jlp.logfouineur.util.Utils.filesInDirectoryWithModel(strPathLoc, ".+\\.properties$");
					this.cbExitingTemplate.setItems(lst);
					
				}
			}
		}

	}

}
