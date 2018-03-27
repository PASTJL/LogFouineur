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

import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.layout.Border;

// TODO: Auto-generated Javadoc
/**
 * The Class MyMenuAsButton.
 */
public class MyMenuAsButton extends Menu {
	
	/** The my button. */
	public Button myButton=new Button();
	
	/**
	 * Instantiates a new my menu as button.
	 *
	 * @param label the label
	 */
	public MyMenuAsButton(String label){
		this.myButton.setText(label);
		
		this.myButton.setId("id"+label);
		this.setStyle("-fx-background-color: transparent;");
		myButton.setStyle("-fx-background-color: transparent; -fx-alignment: center; -fx-padding: 0;");
		this.setGraphic(myButton);
		//myButton.setStyle("-fx-padding: 0 ;"); // To fill entirely the Men
		myButton.setOnMouseEntered(e -> {
			this.setStyle("-fx-background-color: GREY;");
		});
		myButton.setOnMouseExited(e -> {
			this.setStyle("-fx-background-color: transparent;");
		});
	}

}
