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
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.stream.Stream;

import org.jlp.logfouineur.util.Utils;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

// TODO: Auto-generated Javadoc
/**
 * The Class LogFileChooser.
 */
public class LogFileChooser extends Stage implements EventHandler<ActionEvent> {

	/** The browse. */
	public Button browse = new Button(Messages.getString("LogFileChooser.0")); //$NON-NLS-1$
	
	/** The lab 1. */
	public Label lab1 = new Label(Messages.getString("LogFileChooser.1")); //$NON-NLS-1$
	
	/** The tf selected file. */
	public TextField tfSelectedFile = new TextField();
	
	/** The selected file. */
	public File selectedFile = null;
	
	/** The button 1.  General Template*/
	RadioButton button1 = new RadioButton(Messages.getString("LogFileChooser.2")); //$NON-NLS-1$
	
	/** The button 2. Local Templates*/
	RadioButton button2 = new RadioButton(Messages.getString("LogFileChooser.3")); //$NON-NLS-1$
	
	/** The cb. */
	ComboBox<String> cb = new ComboBox<String>();
	
	/** The ok button. */
	public Button okButton = new Button(Messages.getString("LogFileChooser.4")); //$NON-NLS-1$
	
	/** The cancel button. */
	public Button cancelButton = new Button(Messages.getString("LogFileChooser.5")); //$NON-NLS-1$

	/** The str path gen. */
	String strPathGen = LogFouineurMain.root + File.separator + Messages.getString("LogFileChooser.6") + File.separator + Messages.getString("LogFileChooser.7"); //$NON-NLS-1$ //$NON-NLS-2$
	
	/** The str path loc. */
	String strPathLoc = LogFouineurMain.workspace + File.separator + LogFouineurMain.currentProject + File.separator
			+ Messages.getString("LogFileChooser.8") + File.separator + Messages.getString("LogFileChooser.9"); //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * Instantiates a new log file chooser.
	 *
	 * @param owner the owner
	 */
	public LogFileChooser(Stage owner) {
		// clear the static attributes bound to the file to parse
		LogFouineurMain.parseLocTemplate=Messages.getString("LogFileChooser.10"); //$NON-NLS-1$
		LogFouineurMain.parseGenTemplate=Messages.getString("LogFileChooser.11"); //$NON-NLS-1$
		
		
		this.initOwner(owner);
		this.getIcons().add(new Image(Messages.getString("LogFileChooser.12"))); //$NON-NLS-1$
		lab1.setFont(Font.font(Messages.getString("LogFileChooser.13"), FontWeight.BOLD, FontPosture.REGULAR, 12)); //$NON-NLS-1$
		browse.setFont(Font.font(Messages.getString("LogFileChooser.14"), FontWeight.BOLD, FontPosture.REGULAR, 12)); //$NON-NLS-1$
		okButton.setFont(Font.font(Messages.getString("LogFileChooser.15"), FontWeight.BOLD, FontPosture.REGULAR, 12)); //$NON-NLS-1$
		cancelButton.setFont(Font.font(Messages.getString("LogFileChooser.16"), FontWeight.BOLD, FontPosture.REGULAR, 12)); //$NON-NLS-1$

		cb.setStyle(Messages.getString("LogFileChooser.17")); //$NON-NLS-1$

		tfSelectedFile.setPrefSize(500, 30);
		tfSelectedFile.setFont(Font.font(Messages.getString("LogFileChooser.18"), FontWeight.BOLD, FontPosture.REGULAR, 12)); //$NON-NLS-1$
		tfSelectedFile.setEditable(true);
		tfSelectedFile.setPromptText(Messages.getString("LogFileChooser.19")); //$NON-NLS-1$
		tfSelectedFile.setEditable(false);
		this.setTitle(Messages.getString("LogFileChooser.20") + LogFouineurMain.currentProject + Messages.getString("LogFileChooser.21") //$NON-NLS-1$ //$NON-NLS-2$
				+ LogFouineurMain.currentScenario);
		//
		BorderPane bp = new BorderPane();
		HBox hb1 = new HBox();

		GridPane gp1 = new GridPane();
		gp1.setHgap(30);
		gp1.add(lab1, 0, 0);
		gp1.add(tfSelectedFile, 1, 0);
		gp1.add(browse, 2, 0);
		hb1.setPadding( new Insets(70, 0, 00, 0));
		hb1.setAlignment(Pos.CENTER);
		hb1.getChildren().add(gp1);
		bp.setPadding(new Insets(10, 0, 10, 0));
		bp.setTop(hb1);
		browse.setOnAction(this);

		VBox vb = new VBox();
		GridPane gp2 = new GridPane();

		Label lab2 = new Label(Messages.getString("LogFileChooser.22")); //$NON-NLS-1$
		lab2.setFont(Font.font(Messages.getString("LogFileChooser.23"), FontWeight.BOLD, FontPosture.REGULAR, 16)); //$NON-NLS-1$
		ToggleGroup group = new ToggleGroup();

		button1.setToggleGroup(group);
		button1.setSelected(true);

		button2.setToggleGroup(group);

		cb.setPrefSize(200, 10);
		cb.setMaxSize(200, 10);
		lab2.setPadding(new Insets(10, 0, 30, 0));
		gp2.setAlignment(Pos.CENTER);
		gp2.add(lab2, 0, 0);
		button1.setPadding(new Insets(10, 0, 10, 0));
		button1.setFont(Font.font(Messages.getString("LogFileChooser.24"), FontWeight.BOLD, FontPosture.REGULAR, 14)); //$NON-NLS-1$
		gp2.add(button1, 0, 1);
		button2.setPadding(new Insets(10, 0, 40, 0));
		button2.setFont(Font.font(Messages.getString("LogFileChooser.25"), FontWeight.BOLD, FontPosture.REGULAR, 14)); //$NON-NLS-1$
		gp2.add(button2, 0, 2);

		gp2.add(cb, 0, 3);
		vb.getChildren().add(gp2);
		vb.setAlignment(Pos.CENTER);
		bp.setCenter(vb);
		button1.setOnAction(this);
		button2.setOnAction(this);

		button1.setId(Messages.getString("LogFileChooser.26")); //$NON-NLS-1$
		button2.setId(Messages.getString("LogFileChooser.27")); //$NON-NLS-1$
		browse.setId(Messages.getString("LogFileChooser.28")); //$NON-NLS-1$

		// Fill cb with general template and set the first value to noTemplate.
		ObservableList<String> olist = Utils.filesInDirectory(strPathGen);
		System.out.println(Messages.getString("LogFileChooser.29") + olist.size()); //$NON-NLS-1$
		olist.add(0, Messages.getString("LogFileChooser.30")); //$NON-NLS-1$
		cb.setItems(olist);
		cb.setValue(olist.get(0));
		Scene page2 = new Scene(bp, 800, 450);
		this.setScene(page2);

		// add buttons on bottom of BorderPane
		HBox hb2 = new HBox();

		GridPane gp3 = new GridPane();
		gp3.setHgap(300);
		gp3.add(okButton, 0, 0);
		gp3.add(cancelButton, 1, 0);
		hb2.getChildren().add(gp3);
		cancelButton.setOnAction(e -> this.close());
		okButton.setId(Messages.getString("LogFileChooser.31")); //$NON-NLS-1$
		okButton.setOnAction(this);
		okButton.setMinSize(90, 30);
		cancelButton.setMinSize(90, 30);
		hb2.setAlignment(Pos.CENTER);
		bp.setBottom(hb2);
		browse.requestFocus();
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
	@Override
	public void handle(ActionEvent event) {
		switch (((ButtonBase) event.getSource()).getId()) {
		case "browse": 
			FileChooser fileChooser = new FileChooser();
			String strDir = LogFouineurMain.workspace + File.separator + LogFouineurMain.currentProject + File.separator
					+ LogFouineurMain.currentScenario + File.separator + Messages.getString("LogFileChooser.33"); //$NON-NLS-1$
			fileChooser.setInitialDirectory(new File(strDir));

			selectedFile = fileChooser.showOpenDialog(this);
			if (null != selectedFile) {
				this.tfSelectedFile.setEditable(true);
				this.tfSelectedFile.setText(selectedFile.getAbsolutePath());
				LogFouineurMain.fileToParse=this.tfSelectedFile.getText();
				this.tfSelectedFile.setEditable(false);
				
			}
			break;
		case "genTemplate": 

			ObservableList<String> olist = Utils.filesInDirectory(strPathGen);

			olist.add(0, Messages.getString("LogFileChooser.35")); //$NON-NLS-1$
			cb.setItems(olist);
			cb.setValue(olist.get(0));
			break;
		case "locTemplate": 

			olist = Utils.filesInDirectory(strPathLoc);

			olist.add(0, Messages.getString("LogFileChooser.37")); //$NON-NLS-1$
			cb.setItems(olist);
			cb.setValue(olist.get(0));
			break;
		case "okButton": 
			LogFouineurMain.fileToParse=Messages.getString("LogFileChooser.39"); //$NON-NLS-1$
			LogFouineurMain.parseLocTemplate=Messages.getString("LogFileChooser.40"); //$NON-NLS-1$
			LogFouineurMain.parseGenTemplate=Messages.getString("LogFileChooser.41"); //$NON-NLS-1$
			this.tfSelectedFile.setDisable(false);
			if(null != tfSelectedFile.getText() &&  tfSelectedFile.getText().length()>0){
				
					LogFouineurMain.fileToParse=tfSelectedFile.getText();
					System.out.println(Messages.getString("LogFileChooser.42") +LogFouineurMain.fileToParse); //$NON-NLS-1$
				
			}
			if (button1.isSelected() && ! cb.getValue().equals(Messages.getString("LogFileChooser.43"))){ //$NON-NLS-1$
				LogFouineurMain.parseGenTemplate=strPathGen+File.separator+ cb.getValue();
				LogFouineurMain.parseLocTemplate=Messages.getString("LogFileChooser.44"); //$NON-NLS-1$
			}else if  (button2.isSelected() && ! cb.getValue().equals(Messages.getString("LogFileChooser.45"))){ //$NON-NLS-1$
				LogFouineurMain.parseLocTemplate=strPathLoc+File.separator+ cb.getValue();
				LogFouineurMain.parseGenTemplate=Messages.getString("LogFileChooser.46"); //$NON-NLS-1$
				
			}
			else {
				LogFouineurMain.parseLocTemplate=Messages.getString("LogFileChooser.47"); //$NON-NLS-1$
				LogFouineurMain.parseGenTemplate=Messages.getString("LogFileChooser.48"); //$NON-NLS-1$
				
			}
			this.close();
			break;
		}

	}

}
