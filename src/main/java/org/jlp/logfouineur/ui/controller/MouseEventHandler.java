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

import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jlp.logfouineur.filestat.ui.DiagFileStats;
import org.jlp.logfouineur.ui.DialogNew;
import org.jlp.logfouineur.ui.DialogOpen;
import org.jlp.logfouineur.ui.DialogScenario;
import org.jlp.logfouineur.ui.LogFouineurMain;
import org.jlp.logfouineur.util.AlertDialog;

import javafx.event.ActionEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class MouseEventHandler.
 */
public class MouseEventHandler implements EventHandler<MouseEvent> {

	/** The a stage. */
	public Stage aStage = null;

	/**
	 * Instantiates a new mouse event handler.
	 *
	 * @param aStage the a stage
	 */
	public MouseEventHandler(Stage aStage) {
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
	@Override
	public void handle(MouseEvent event) {

		if (event.getSource() instanceof Button) {
			System.out.println("((MouseEvent)event.getSource()).getId() =" + ((Button) event.getSource()).getId()); //$NON-NLS-1$
			switch (((Button) event.getSource()).getId()) {
			case "bNewProjectCreate": //$NON-NLS-1$
				System.out.println("Button => Create newProject =>" + DialogNew.tfNameProject.getText()); //$NON-NLS-1$
				String newProject = DialogNew.tfNameProject.getText();
				if (null == newProject || newProject.length() < 4) {
					new AlertDialog(aStage, Messages.getString("MouseEventHandler.3"), //$NON-NLS-1$
							AlertDialog.ICON_ERROR).showAndWait();
					;
					DialogNew.tfNameProject.setText(""); //$NON-NLS-1$
				} else if (DialogNew.comboBox.getItems().contains((String) newProject)) {
					new AlertDialog(aStage, Messages.getString("MouseEventHandler.5"), //$NON-NLS-1$
							AlertDialog.ICON_ERROR).showAndWait();
					;
					DialogNew.tfNameProject.setText(""); //$NON-NLS-1$
				} else {

					// dates validation
					if (validationDates(DialogNew.tfBeginScn, DialogNew.tfEndScn)) {
						String scenario = LogFouineurMain.prefixScenario + "default"; //$NON-NLS-1$
						if (DialogNew.tfNameScenario.getText().length() > 0) {
							if (DialogNew.tfNameScenario.getText().startsWith(LogFouineurMain.prefixScenario)) {
								scenario = DialogNew.tfNameScenario.getText();
							} else {
								scenario = LogFouineurMain.prefixScenario + DialogNew.tfNameScenario.getText();
							}
						}
						String strDateBegin = "1970/01/01:00:00:00";
						String strDateFin = new SimpleDateFormat("yyyy/MM/dd:HH:mm:ss").format(new Date());
						if (DialogNew.tfBeginScn.getText().length() > 0)
							strDateBegin = DialogNew.tfBeginScn.getText();
						if (DialogNew.tfEndScn.getText().length() > 0)
							strDateFin = DialogNew.tfEndScn.getText();
						LogFouineurMain.currentProject = newProject;
						// activate/deactivate Menu
						LogFouineurMain.handleMenuState(true);
						String strPath = LogFouineurMain.workspace + File.separator + newProject;
						String strPathTemp = strPath + File.separator + "tmp"; //$NON-NLS-1$
						String strPathTemplate = strPath + File.separator + "templates"; //$NON-NLS-1$
						String strPathTemplateStat = strPath + File.separator + "templates" + File.separator //$NON-NLS-1$
								+ "filestat"; //$NON-NLS-1$
						String strPathTemplateLog = strPath + File.separator + "templates" + File.separator //$NON-NLS-1$
								+ "logparser"; //$NON-NLS-1$
						String strPathScn = strPath + File.separator + scenario;
						String strPathScnLog = strPath + File.separator + scenario + File.separator + "logs"; //$NON-NLS-1$
						String strPathScnLogConf = strPath + File.separator + scenario + File.separator + "logs" //$NON-NLS-1$
								+ File.separator + "config"; //$NON-NLS-1$
						String strPathScnLogConfLogParser = strPath + File.separator + scenario + File.separator + "logs" //$NON-NLS-1$
								+ File.separator + "config"+File.separator+"parselog"; 
						String strPathScnLogConfFileStats = strPath + File.separator + scenario + File.separator + "logs" //$NON-NLS-1$
								+ File.separator + "config"+File.separator+"filestat";
						String strPathScnCsv = strPath + File.separator + scenario + File.separator + "csv"; //$NON-NLS-1$

						Path path = new File(strPath).toPath();
						Path pathTemp = new File(strPathTemp).toPath();
						Path pathTemplate = new File(strPathTemplate).toPath();
						Path pathTemplateStat = new File(strPathTemplateStat).toPath();
						Path pathTemplateLog = new File(strPathTemplateLog).toPath();
						Path pathScn = new File(strPathScn).toPath();
						Path pathScnLog = new File(strPathScnLog).toPath();
						Path pathScnLogConf = new File(strPathScnLogConf).toPath();
						Path pathScnCsv = new File(strPathScnCsv).toPath();
						Path pathScnLogConfLogParser=new File(strPathScnLogConfLogParser).toPath();
						Path pathScnLogConfFileStats=new File(strPathScnLogConfFileStats).toPath();

						try {
							Files.createDirectory(path);
							Files.createDirectory(pathTemp);
							Files.createDirectory(pathTemplate);
							Files.createDirectory(pathTemplateStat);
							Files.createDirectory(pathTemplateLog);
							Files.createDirectory(pathScn);
							Files.createDirectory(pathScnLog);
							Files.createDirectory(pathScnLogConf);
							Files.createDirectory(pathScnCsv);
							Files.createDirectory(pathScnLogConfLogParser);
							Files.createDirectory(pathScnLogConfFileStats);

							
							LogFouineurMain.scenariosProps.put("listScenarios", scenario + " ");
							LogFouineurMain.scenariosProps.put(scenario + ".dateBegin", strDateBegin);
							LogFouineurMain.scenariosProps.put(scenario + ".dateEnd", strDateFin);

							String strPropsConf = strPath + File.separator + "scenarios.properties";
							try {
								LogFouineurMain.scenariosProps.store(Files.newOutputStream(new File(strPropsConf).toPath()),
										"Creation of First scenario");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							new AlertDialog(aStage,
									Messages.getString("MouseEventHandler.1") + newProject //$NON-NLS-1$
											+ Messages.getString("MouseEventHandler.0") + scenario, //$NON-NLS-1$
									AlertDialog.ICON_INFO).showAndWait();
							LogFouineurMain.currentScenario = scenario;
							
							LogFouineurMain.primaryStage
									.setTitle("LogFouineurMain V1.0 : Project : " + LogFouineurMain.currentProject
											+ ", with scenario : " + LogFouineurMain.currentScenario);
							LogFouineurMain.handleMenuState(false);
							aStage.hide();

						} catch (IOException e) {
							LogFouineurMain.handleMenuState(false);
							LogFouineurMain.currentProject = ""; //$NON-NLS-1$
							DialogNew.tfNameProject.setText(""); //$NON-NLS-1$
							new AlertDialog(aStage, Messages.getString("MouseEventHandler.14"), //$NON-NLS-1$
									AlertDialog.ICON_ERROR).showAndWait();
							e.printStackTrace();
						}
					} else {

					}

				}
				break;
			case "bOpenProject":
				// update the begin and end date
				Path pathProject = FileSystems.getDefault()
						.getPath(LogFouineurMain.workspace + File.separator + LogFouineurMain.currentProject);

				// RFetrieve dates in scenarios.properties for this
				// project/scenario
				//Properties propsScn = new Properties();
				String strPropsConf = pathProject.toString() + File.separator + "scenarios.properties";
				try {
					LogFouineurMain.scenariosProps.load(Files.newInputStream(new File(strPropsConf).toPath()));
					// update Properties
					// Verify that dates are correct if not old date are
					// conserved
					Pattern pat = Pattern.compile("(1|2)\\d{3}/\\d\\d/\\d\\d:\\d\\d:\\d\\d:\\d\\d");
					if (pat.matcher(DialogOpen.tfBeginScn.getText()).find())
						LogFouineurMain.scenariosProps.setProperty(LogFouineurMain.currentScenario + ".dateBegin",
								DialogOpen.tfBeginScn.getText());

					if (pat.matcher(DialogOpen.tfEndScn.getText()).find())
						LogFouineurMain.scenariosProps.setProperty(LogFouineurMain.currentScenario + ".dateEnd",
								DialogOpen.tfEndScn.getText());
					LogFouineurMain.scenariosProps.store(Files.newOutputStream(new File(strPropsConf).toPath()), "");

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// activate/deactivate Menu
				LogFouineurMain.handleMenuState(false);

				LogFouineurMain.primaryStage.setTitle("LogFouineurMain V1.0 : Project : "
						+ LogFouineurMain.currentProject + ", with scenario : " + LogFouineurMain.currentScenario);
				
				addSubMenusLocalTemplate();
				aStage.hide();
				break;
			case "bCreateChange":
				if (DialogScenario.bCreateChange.getText().equals("Change")) {
					pathProject = FileSystems.getDefault()
							.getPath(LogFouineurMain.workspace + File.separator + LogFouineurMain.currentProject);

					// RFetrieve dates in scenarios.properties for this
					// project/scenario
					// retrieve the current scenario fot the project
					String newScenario = DialogScenario.comboBoxScn.getValue();
					LogFouineurMain.currentScenario = newScenario;
					//propsScn = new Properties();
					strPropsConf = pathProject.toString() + File.separator + "scenarios.properties";
					try {
						LogFouineurMain.scenariosProps.load(Files.newInputStream(new File(strPropsConf).toPath()));
						// update Properties
						// Verify that dates are correct if not old date are
						// conserved
						Pattern pat = Pattern.compile("(1|2)\\d{3}/\\d\\d/\\d\\d:\\d\\d:\\d\\d:\\d\\d");
						if (pat.matcher(DialogScenario.tfBeginScn.getText()).find())
							LogFouineurMain.scenariosProps.setProperty(LogFouineurMain.currentScenario + ".dateBegin",
									DialogScenario.tfBeginScn.getText());

						if (pat.matcher(DialogScenario.tfEndScn.getText()).find())
							LogFouineurMain.scenariosProps.setProperty(LogFouineurMain.currentScenario + ".dateEnd",
									DialogScenario.tfEndScn.getText());
						LogFouineurMain.scenariosProps.store(Files.newOutputStream(new File(strPropsConf).toPath()), "");
						LogFouineurMain.primaryStage
								.setTitle("LogFouineurMain V1.0 : Project : " + LogFouineurMain.currentProject
										+ ", with scenario : " + LogFouineurMain.currentScenario);
						aStage.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					// creating a new scenario

					// dates validation
					if (validationDates(DialogScenario.tfBeginScn, DialogScenario.tfEndScn)) {
						String scenario = DialogScenario.tfNameScenario.getText(); // $NON-NLS-1$
						if (DialogScenario.tfNameScenario.getText().length() > 0) {
							if (!DialogScenario.tfNameScenario.getText().startsWith(LogFouineurMain.prefixScenario)) {

								scenario = LogFouineurMain.prefixScenario + DialogScenario.tfNameScenario.getText();
							}
						}
						String strDateBegin = "1970/01/01:00:00:00";
						String strDateFin = new SimpleDateFormat("yyyy/MM/dd:HH:mm:ss").format(new Date());
						if (DialogScenario.tfBeginScn.getText().length() > 0)
							strDateBegin = DialogScenario.tfBeginScn.getText();
						if (DialogScenario.tfEndScn.getText().length() > 0)
							strDateFin = DialogScenario.tfEndScn.getText();

						// activate/deactivate Menu
						LogFouineurMain.handleMenuState(true);
						String strPath = LogFouineurMain.workspace + File.separator + LogFouineurMain.currentProject;
						String strPathTemp = strPath + File.separator + "tmp"; //$NON-NLS-1$
						String strPathTemplate = strPath + File.separator + "templates"; //$NON-NLS-1$
						String strPathTemplateStat = strPath + File.separator + "templates" + File.separator //$NON-NLS-1$
								+ "filestat"; //$NON-NLS-1$
						String strPathTemplateLog = strPath + File.separator + "templates" + File.separator //$NON-NLS-1$
								+ "logparser"; //$NON-NLS-1$
						String strPathScn = strPath + File.separator + scenario;
						String strPathScnLog = strPath + File.separator + scenario + File.separator + "logs"; //$NON-NLS-1$
						String strPathScnLogConf = strPath + File.separator + scenario + File.separator + "logs" //$NON-NLS-1$
								+ File.separator + "config"; //$NON-NLS-1$
						String strPathScnCsv = strPath + File.separator + scenario + File.separator + "csv"; //$NON-NLS-1$

						Path path = new File(strPath).toPath();
						Path pathTemp = new File(strPathTemp).toPath();
						Path pathTemplate = new File(strPathTemplate).toPath();
						Path pathTemplateStat = new File(strPathTemplateStat).toPath();
						Path pathTemplateLog = new File(strPathTemplateLog).toPath();
						Path pathScn = new File(strPathScn).toPath();
						Path pathScnLog = new File(strPathScnLog).toPath();
						Path pathScnLogConf = new File(strPathScnLogConf).toPath();
						Path pathScnCsv = new File(strPathScnCsv).toPath();

						try {

							Files.createDirectory(pathScn);
							Files.createDirectory(pathScnLog);
							Files.createDirectory(pathScnLogConf);
							Files.createDirectory(pathScnCsv);

							//Properties scenariosProps = new Properties();
							try {
								strPropsConf = strPath + File.separator + "scenarios.properties";
								LogFouineurMain.scenariosProps.load(Files.newInputStream(new File(strPropsConf).toPath()));
								LogFouineurMain.scenariosProps.put("listScenarios", LogFouineurMain.scenariosProps.getProperty("listScenarios")+scenario + " ");
								LogFouineurMain.scenariosProps.put(scenario + ".dateBegin", strDateBegin);
								LogFouineurMain.scenariosProps.put(scenario + ".dateEnd", strDateFin);

								LogFouineurMain.scenariosProps.store(Files.newOutputStream(new File(strPropsConf).toPath()),
										"Creation of a new scenario");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							LogFouineurMain.currentScenario = scenario;
							new AlertDialog(aStage,
									"Adding to the project :" + LogFouineurMain.currentProject //$NON-NLS-1$
											+ " the new scenario : " +  LogFouineurMain.currentScenario, //$NON-NLS-1$
									AlertDialog.ICON_INFO).showAndWait();
							LogFouineurMain.primaryStage
									.setTitle("LogFouineurMain V1.0 : Project : " + LogFouineurMain.currentProject
											+ ", with scenario : " + LogFouineurMain.currentScenario);
							LogFouineurMain.handleMenuState(false);
							aStage.hide();

						} catch (IOException e) {
							LogFouineurMain.handleMenuState(false);
							LogFouineurMain.currentProject = ""; //$NON-NLS-1$
							LogFouineurMain.currentScenario="";
							DialogNew.tfNameProject.setText(""); //$NON-NLS-1$
							LogFouineurMain.csvPrefix="";
							LogFouineurMain.pathToScenario="";
							LogFouineurMain. fileToParseBasic="";

							new AlertDialog(aStage, Messages.getString("MouseEventHandler.14"), //$NON-NLS-1$
									AlertDialog.ICON_ERROR).showAndWait();
							e.printStackTrace();
						}
					} else {
						System.out.println("Creation nouveau scenrio manque");
					}

				}

				break;
			case "idParseLogs" :
				System.out.println("ParseLogs button");
				break;
			case "exit": //$NON-NLS-1$
				System.out.println("Menu Item =>exit"); //$NON-NLS-1$
				System.exit(0);
				break;
			}
		} else if (event.getSource() instanceof TextField) {
			switch (((TextField) event.getSource()).getId()) {
			case "tfNameScenarioCreate":
				if (event.getEventType() == MouseEvent.MOUSE_ENTERED) {
					DialogScenario.bCreateChange.setText("Create");

				} else if (event.getEventType() == MouseEvent.MOUSE_EXITED) {
					if (DialogScenario.tfNameScenario.getText().length() > 0) {
						DialogScenario.bCreateChange.setText("Create");
					} else {
						DialogScenario.bCreateChange.setText("Change");
					}

				}
				break;
			}
		}

	}
	
	/**
	 * Adds the sub menus local template.
	 */
	private void addSubMenusLocalTemplate() {
		// TODO Auto-generated method stub
		String strLocTemplateString=LogFouineurMain.workspace+File.separator+LogFouineurMain.currentProject+
				File.separator+"templates"+File.separator+"filestat";
		
			
			File[] lstF=new File(strLocTemplateString).listFiles();
			MenuItem[] tabMi=new MenuItem[lstF.length];
			int i=0;
			for(File file:lstF) {
				String prefix=file.getName().split("\\.properties")[0];
				tabMi[i]=new MenuItem(prefix);
				tabMi[i].setStyle("-fx-text-fill : black;");
				tabMi[i].setOnAction(e -> {
					System.out.println("loading from localTemplate : "+file.getAbsolutePath());
					new DiagFileStats("Loc",file);
				});
				i++;
				
			}
			for ( i=0; i< tabMi.length;i++) {
				LogFouineurMain.mFromLocTemplate.getItems().add(tabMi[i]);
			}
	}
		
	/**
	 * Validation dates.
	 *
	 * @param tfDateBegin the tf date begin
	 * @param tfDateEnd the tf date end
	 * @return true, if successful
	 */
	private boolean validationDates(TextField tfDateBegin, TextField tfDateEnd) {
		Pattern patDate = Pattern.compile("(1|2)\\d\\d\\d/\\d\\d/\\d\\d:\\d\\d:\\d\\d:\\d\\d"); //$NON-NLS-1$
		if (tfDateBegin.getText().length() == 0 && tfDateEnd.getText().length() == 0) {
			return true;
		} else if (tfDateBegin.getText().length() != 0 && tfDateEnd.getText().length() == 0) {

			if (patDate.matcher(tfDateBegin.getText()).find()) {
				return true;
			} else {
				new AlertDialog(aStage, Messages.getString("MouseEventHandler.10"), AlertDialog.ICON_ERROR) //$NON-NLS-1$
						.showAndWait();
				return false;
			}
		} else if (tfDateBegin.getText().length() == 0 && tfDateEnd.getText().length() != 0) {

			if (patDate.matcher(tfDateEnd.getText()).find()) {
				return true;
			} else {
				new AlertDialog(aStage, Messages.getString("MouseEventHandler.11"), AlertDialog.ICON_ERROR) //$NON-NLS-1$
						.showAndWait();
				return false;
			}
		} else if (tfDateBegin.getText().length() != 0 && tfDateEnd.getText().length() != 0) {

			if (patDate.matcher(tfDateEnd.getText()).find() && patDate.matcher(tfDateBegin.getText()).find()) {
				return true;
			} else {
				new AlertDialog(aStage, Messages.getString("MouseEventHandler.12"), AlertDialog.ICON_ERROR) //$NON-NLS-1$
						.showAndWait();
				return false;
			}
		}

		return false;
	}
}
