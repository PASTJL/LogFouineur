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

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
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
public class MyDialogDateInMillis {

	/**
	 * Instantiates a new my dialog date in millis.
	 *
	 * @param titleTxt
	 *            the title txt
	 */
	public MyDialogDateInMillis(String titleTxt) {

		Stage stage = new Stage();
		String style = "-fx-font-size:12px; -fx-font-family:Arial; -fx-font-weight:bold;";

		Label label1 = new Label("TimeZone: ");
		label1.setTooltip(new Tooltip("Format  : (+|-)\\d{4} ; ex +0200 for summer hour in Paris; it can be empty"));
		Label label2 = new Label("Date  : ");
		label2.setTooltip(new Tooltip("Date format must be like : yyyy/MM/dd:HH:mm:ss.SSS \n It can be shorter"));
		Label label3 = new Label("Time In Millis: ");
		label3.setTooltip(new Tooltip("Time in Millis since 1970/01/01:00:00:00.000"));

		TextField text1 = new TextField();

		TextField text2 = new TextField();
		TextField text3 = new TextField();

		text1.setPrefWidth(300);
		text1.setMinWidth(300);
		text1.setMaxWidth(300);
		text2.setPrefWidth(300);
		text2.setMinWidth(300);
		text2.setMaxWidth(300);
		text3.setPrefWidth(300);
		text3.setMinWidth(300);
		text3.setMaxWidth(300);
		/*
		 * Vertical box container)
		 * 
		 */
		VBox vb = new VBox();
		/* Hbox for Buttons */
		HBox hb = new HBox();
		Button btCancel = new Button("Cancel");
		Button btClear = new Button("Clear");
		Button btConvert = new Button("Convert");
		HBox.setMargin(btClear, new Insets(30, 20, 10, 20));
		HBox.setMargin(btConvert, new Insets(30, 20, 10, 20));
		HBox.setMargin(btCancel, new Insets(30, 20, 10, 20));
		hb.getChildren().addAll(btClear, btConvert, btCancel);
		for (Node nd : hb.getChildren()) {
			nd.setStyle(style);

		}
		GridPane grid = new GridPane();

		grid.add(label1, 1, 1);
		grid.add(text1, 2, 1);
		grid.add(label2, 1, 2);
		grid.add(text2, 2, 2);
		grid.add(label3, 1, 3);
		grid.add(text3, 2, 3);
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
		Scene scene = new Scene(root, 500, 300, Color.LIGHTBLUE);
		stage.setTitle("Convert date to and from date in millis");
		stage.setScene(scene);

		// actions
		btClear.setOnAction(a -> {
			text1.setText("");
			text2.setText("");
			text3.setText("");
		});

		btCancel.setOnAction(a -> {
			stage.close();
		});

		btConvert.setOnAction(a -> {
			String strTZ = text1.getText();
			String strTinMillis = text3.getText();
			String strDateString = text2.getText();
			if (strTinMillis.length() > 0) {
				strDateString = new DateInMillis().numberToDate(strTinMillis, strTZ);
				text2.setText(strDateString);

				return;
			} else if (strDateString.length() > 0) {
				strTinMillis = new DateInMillis().dateToNumber(strDateString, strTZ);
				text3.setText(strTinMillis);

				return;
			}

			Calendar localCalendar = Calendar.getInstance();
			TimeZone localTimeZone = TimeZone.getDefault();
			localCalendar.setTimeZone(localTimeZone);
			strTinMillis = Long.toString(localCalendar.getTimeInMillis());
			strDateString = new DateInMillis().numberToDate(strTinMillis, strTZ);
			text3.setText(strTinMillis);
			text2.setText(strDateString);

			return;

		});
		stage.centerOnScreen();
		stage.show();
	}

}
