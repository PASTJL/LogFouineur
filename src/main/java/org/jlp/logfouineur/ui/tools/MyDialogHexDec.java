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
package org.jlp.logfouineur.ui.tools;

import java.util.Calendar;
import java.util.TimeZone;

import org.jlp.logfouineur.util.DateInMillis;
import org.jlp.logfouineur.util.TestRegexp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

// TODO: Auto-generated Javadoc
/**
 * The Class MyDialogDateInMillis.
 */
public class MyDialogHexDec {

	/**
	 * Instantiates a new my dialog date in millis.
	 *
	 * @param titleTxt
	 *            the title txt
	 */
	public MyDialogHexDec(String titleTxt) {

		Stage stage = new Stage();
		String style = "-fx-font-size:12px; -fx-font-family:Arial; -fx-font-weight:bold;";

		Label label1 = new Label("Decimal Number : ");
		label1.setTooltip(new Tooltip("Fill or Read   the decimal number"));
		TextField tfDec=new TextField();
		Label label2 = new Label("Hexadecimal Number : ");
		label2.setTooltip(new Tooltip("Fill or Read   the hexadecimal  number"));
		TextField tfHexa=new TextField();

	
		/*
		 * Vertical box container)
		 * 
		 */
		VBox vb = new VBox();
		/* Hbox for Buttons */
		HBox hb = new HBox();
		Button btCancel = new Button("Cancel");
		Button btClear = new Button("Clear");
		Button btParse = new Button("Translate");
		HBox.setMargin(btClear, new Insets(30, 20, 10, 20));
		HBox.setMargin(btParse, new Insets(30, 20, 10, 20));
		HBox.setMargin(btCancel, new Insets(30, 20, 10, 20));
		hb.getChildren().addAll(btClear, btParse, btCancel);
		for (Node nd : hb.getChildren()) {
			nd.setStyle(style);

		}
		GridPane grid = new GridPane();

		grid.add(label1, 1, 1);
		grid.add(tfDec, 2, 1);
		grid.add(label2, 1, 2);
		grid.add(tfHexa, 2, 2);
		
		for (Node nd : grid.getChildren()) {
			nd.setStyle(nd.getStyle() + style);

		}
		grid.setHgap(20);
		grid.setVgap(30);
		Group root = new Group();
		hb.setAlignment(Pos.CENTER);
		vb.setAlignment(Pos.CENTER);
		vb.getChildren().add(grid);
		vb.getChildren().add(hb);
		root.getChildren().add(vb);
		Scene scene = new Scene(root, 400,200, Color.LIGHTBLUE);
		stage.setTitle(titleTxt);
		stage.setScene(scene);

		// actions
		btClear.setOnAction(a -> {
			tfDec.setText("");
			tfHexa.setText("");
			
		});

		btCancel.setOnAction(a -> {
			stage.close();
		});

		btParse.setOnAction(a -> {
			if(tfDec.getText().trim().length() > 0) {
				tfHexa.setText(toHexa(tfDec.getText()));
			}
			else if (tfHexa.getText().trim().length() > 0) {
				tfDec.setText(toDec(tfHexa.getText()));
			}
			else {
				tfDec.setText("");
				tfHexa.setText("");
			}
		});
		stage.centerOnScreen();
		stage.show();
	}

	/**
	 * To dec.
	 *
	 * @param text the text
	 * @return the string
	 */
	private String toDec(String text) {
		// TODO Auto-generated method stub
		return Long.toString(Long.parseLong(text.trim(), 16 ));
	}

	/**
	 * To hexa.
	 *
	 * @param text the text
	 * @return the string
	 */
	private String toHexa(String text) {
		
		return Long.toHexString(Long.parseLong(text.trim(), 10 )).toUpperCase();
	}

}
