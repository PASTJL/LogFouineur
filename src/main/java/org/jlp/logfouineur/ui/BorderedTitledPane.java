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

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

// TODO: Auto-generated Javadoc
/**
 * The Class BorderedTitledPane.
 */
public class BorderedTitledPane extends StackPane {
	
	/** The content pane. */
	public  StackPane contentPane = new StackPane();
	
	/**
	 * Instantiates a new bordered titled pane.
	 *
	 * @param titleString the title string
	 * @param content the content
	 * @param pos the pos
	 */
	public BorderedTitledPane(String titleString, Node content, Pos pos) {
	    Label title = new Label(" " + titleString + " ");
	    this.getStylesheets().add(this.getClass().getResource("/logfouineur.css").toExternalForm());
	    title.getStyleClass().add("bordered-titled-title");
	    StackPane.setAlignment(title, pos);

	   
	    content.getStyleClass().add("bordered-titled-content");
	    contentPane.getChildren().add(content);

	    getStyleClass().add("bordered-titled-border");
	    getChildren().addAll(title, contentPane);
	  }

}
