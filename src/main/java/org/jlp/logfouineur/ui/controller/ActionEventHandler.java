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
package org.jlp.logfouineur.ui.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.jlp.logfouineur.ui.DialogScenario;
import org.jlp.logfouineur.ui.LogFileChooser;
import org.jlp.logfouineur.ui.LogFouineurFill;
import org.jlp.logfouineur.ui.LogFouineurMain;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

// TODO: Auto-generated Javadoc
/**
 * The Class ActionEventHandler.
 */
public class ActionEventHandler implements EventHandler<ActionEvent> {
	
	/** The a stage. */
	public Stage aStage = null;

	/**
	 * Instantiates a new action event handler.
	 *
	 * @param aStage the a stage
	 */
	public ActionEventHandler(Stage aStage) {
		this.aStage = aStage;
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
		if (event.getSource() instanceof ComboBox) {
			if (((ComboBox<String>) event.getSource()) == DialogScenario.comboBoxScn) {
				String newScenario = DialogScenario.comboBoxScn.getValue();
				Path pathProject = FileSystems.getDefault()
						.getPath(LogFouineurMain.workspace + File.separator + LogFouineurMain.currentProject);

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
				DialogScenario.tfBeginScn.setText(propsScn.getProperty(newScenario + ".dateBegin"));
				DialogScenario.tfEndScn.setText(propsScn.getProperty(newScenario + ".dateEnd"));
				LogFouineurMain.currentScenario = newScenario;

				LogFouineurMain.primaryStage.setTitle("LogFouineurMain V1.0 : Project : "
						+ LogFouineurMain.currentProject + ", with scenario : " + LogFouineurMain.currentScenario);

			}

		} else if (event.getSource() instanceof Button) {

			switch (((Button) event.getSource()).getId()) {
			case "idParseLogs":
				
				System.out.println("ParseLogs button");
				LogFouineurFill.clear();
				LogFouineurMain.isTrace=false;
				new LogFileChooser(aStage);
				// Filling the Scene of the primary stage of LogFouineurMain App
				// call the static method LogFouineurFill.init
				System.out.println("LogFouineurMain.fileToParse = "+LogFouineurMain.fileToParse);
				if (LogFouineurMain.fileToParse.length()>0) LogFouineurFill.init();
				else  LogFouineurFill.clear();
				
				break;

			}

		}
	}
}
