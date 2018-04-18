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
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import org.jlp.logfouineur.ui.DialogNew;
import org.jlp.logfouineur.ui.DialogOpen;
import org.jlp.logfouineur.ui.DialogScenario;
import org.jlp.logfouineur.ui.LogFileChooser;
import org.jlp.logfouineur.ui.LogFouineurFill;
import org.jlp.logfouineur.ui.LogFouineurMain;
import org.jlp.logfouineur.ui.Messages;
import org.jlp.logfouineur.ui.tools.MyDialogDateInMillis;
import org.jlp.logfouineur.ui.tools.MyDialogHexDec;
import org.jlp.logfouineur.ui.tools.MyDialogTestRegex;
import org.jlp.logfouineur.ui.tools.MyDialogConcatFile;

import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jlp.javafx.SimpleLineChartsMultiYAxis;
import org.jlp.javafx.ZoomableLineChartsMultiYAxis;
import org.jlp.javafx.common.CSVFileAndStrategy;
import org.jlp.javafx.common.Project;
import org.jlp.javafx.ext.MyLongToDateConverter;
import org.jlp.javafx.ext.MyTypeAxis;
import org.jlp.javafx.richview.CompositePanel;
import org.jlp.logfouineur.filestat.ui.DiagFileStats;
import org.jlp.logfouineur.parseview.ParseView;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class MenuEventHandler.
 */
public class MenuEventHandler implements EventHandler<ActionEvent> {

	/**
	 * Handle.
	 *
	 * @param event the event
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.event.EventHandler#handle(javafx.event.Event)
	 */
	@Override
	public void handle(ActionEvent event) {

		if (event.getSource() instanceof MenuItem) {
			System.out.println("((MenuItem)event.getSource()).getId() =" + ((MenuItem) event.getSource()).getId()); //$NON-NLS-1$
			switch (((MenuItem) event.getSource()).getId()) {
			case "newProject": //$NON-NLS-1$
				LogFouineurFill.clear();
				System.out.println("Menu Item => newProject"); //$NON-NLS-1$
				DialogNew dialog = new DialogNew(LogFouineurMain.primaryStage);
				dialog = null; // Elligible for GC
				break;
			case "openProject": //$NON-NLS-1$
				LogFouineurFill.clear();
				System.out.println("Menu Item => openProject"); //$NON-NLS-1$
				new DialogOpen(LogFouineurMain.primaryStage);
				break;
			case "scenario":
				LogFouineurFill.clear();
				System.out.println("Menu Item => scenario"); //$NON-NLS-1$
				new DialogScenario(LogFouineurMain.primaryStage);
				break;
			case "closeProject": //$NON-NLS-1$
				// activate/deactivate Menu
				LogFouineurFill.clear();
				LogFouineurMain.handleMenuState(true);
				LogFouineurMain.currentProject = "";
				LogFouineurMain.currentScenario = "";
				LogFouineurMain.primaryStage.setTitle("LogFouineurMain V1.0 : no Project selected");
				LogFouineurMain.bpRoot.setCenter(LogFouineurMain.imgView);
				LogFouineurMain.csvPrefix = "";
				LogFouineurMain.pathToScenario = "";
				LogFouineurMain.fileToParseBasic = "";
				LogFouineurMain.parseGenTemplate = "";
				LogFouineurMain.parseLocTemplate = "";
				LogFouineurMain.scenariosProps.clear();

				break;
			case "exit": //$NON-NLS-1$
				System.out.println("Menu Item =>exit"); //$NON-NLS-1$
				System.exit(0);
				break;
			case "parseLogs":
				
				LogFouineurFill.clear();

				new LogFileChooser(LogFouineurMain.primaryStage);
				break;
			case "CSVView":
				LogFouineurMain.bpRoot.setCenter(LogFouineurMain.blankPane);
				//LogFouineurFill.clear();
				CompositePanel.tableView.getItems().clear();
				CompositePanel.tableView.getColumns().clear();
				
				showCsvViewer();
				// TODOnew CSVViewer(LogFouineurMain.primaryStage);
				break;
			case "ParseAndView":
				//LogFouineurFill.clear();
				CompositePanel.tableView.getItems().clear();
				CompositePanel.tableView.getColumns().clear();
				LogFouineurMain.bpRoot.setCenter(LogFouineurMain.blankPane);
				parseAndView();
				break;
			case "dateInMillis":
				new MyDialogDateInMillis("Date <-> DateInMillis");
				break;
			case "testRegex":
				new MyDialogTestRegex("Testing Regular Expressions");

				break;
			case "concatFiles":
				new MyDialogConcatFile();

				break;
			case "decHexa":
				new MyDialogHexDec("Translate Decimal/Hexa");

				break;
			case "fromScratch":
				new DiagFileStats();
				break;
			}

		}
	}

	/**
	 * Parses the and view.
	 */
	private void parseAndView() {
		FileChooser fileChooser = new FileChooser();
		String strDir = LogFouineurMain.workspace + File.separator + LogFouineurMain.currentProject + File.separator
				+ LogFouineurMain.currentScenario + File.separator + Messages.getString("LogFileChooser.33"); //$NON-NLS-1$
		fileChooser.setInitialDirectory(new File(strDir));

		File selectedFile = fileChooser.showOpenDialog(LogFouineurMain.primaryStage);
		if (null != selectedFile) {
			
			new ParseView(selectedFile);
			
		}
		if (null!=ParseView.generatedCsvFile ) {
		 showCsvViewer(ParseView.generatedCsvFile);
		}
	}
	
	/**
	 * Show csv viewer.
	 *
	 * @param generatedCsvFile the generated csv file
	 */
	public void showCsvViewer(String generatedCsvFile) {
		showCsvViewer(generatedCsvFile,1) ;
	}
	
	/**
	 * Show csv viewer.
	 *
	 * @param generatedCsvFile the generated csv file
	 * @param pas the pas
	 */
	public void showCsvViewer(String generatedCsvFile,long pas) {
		HBox menuGen = new HBox();

		VBox screen = new VBox();
		double width=Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		Project.root = System.getProperty("root");
		Project.loadDateProperties(System.getProperty("root"));
		Project.loadLogfouineurProperties(System.getProperty("root"));
		Project.workspace = System.getProperty("workspace");
		SimpleLineChartsMultiYAxis chart = new ZoomableLineChartsMultiYAxis(1.0, true);
		SimpleLineChartsMultiYAxis.isPopupMuted = false;
		SimpleLineChartsMultiYAxis.isPopupFullVisible = false;
		
		CompositePanel cp = new CompositePanel(chart);
		
		Label lDateFormat = new Label("Date Format: yyyy/MM/dd:HH:mm:ss");
		HBox.setMargin(lDateFormat, new Insets(5, 10, 5, 10));
		Button btBrowse = new Button("Browse");
		btBrowse.setStyle("-fx-font-weight:bold;-fx-font-size:12px;");

		HBox.setMargin(btBrowse, new Insets(5, 10, 5, 10));
		menuGen.getChildren().addAll(lDateFormat, btBrowse);
		screen.getChildren().addAll(menuGen, cp);
		LogFouineurMain.bpRoot.setCenter(screen);
		((NumberAxis) CompositePanel.chart.getBaseChart().getXAxis())
				.setTickLabelFormatter(MyTypeAxis.DATECONVERTER.dateConverter(
						((NumberAxis) CompositePanel.chart.getBaseChart().getXAxis()).upperBoundProperty().longValue()
								- ((NumberAxis) CompositePanel.chart.getBaseChart().getXAxis()).lowerBoundProperty()
										.longValue(),
						Locale.FRANCE));
		
		CompositePanel.chart.timeConverter = MyTypeAxis.DATECONVERTER.myConverter;

		CompositePanel.chart.setXLabel(((MyLongToDateConverter) CompositePanel.chart.timeConverter).getTimeFormat());
		//On choisit un pas de 10s que lon pourra modifier ensuite
		cp.nbSteps.setText(Long.toString(pas));
		cp.units.getSelectionModel().select("s");
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("csv Files", "*.csv", "*.csv.gz"));
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
		fc.setInitialDirectory(new File(Project.workspace + File.separator + LogFouineurMain.currentProject
				+ File.separator + LogFouineurMain.currentScenario + File.separator + "csv"));
		btBrowse.setOnAction(event -> {
			List<File> selectedFiles = fc.showOpenMultipleDialog(LogFouineurMain.primaryStage);
			List<CSVFileAndStrategy> list = new ArrayList<CSVFileAndStrategy>();

			if (selectedFiles != null) {
				for (File file : selectedFiles) {
					list.add(new CSVFileAndStrategy(file, cp.cbStrategy.getValue().trim().toLowerCase()));
				}
				cp.proceedCSVs(list);
			}
		});
		// directly graph the summary file
		List<CSVFileAndStrategy> selectedFiles=new ArrayList<CSVFileAndStrategy>();
		selectedFiles.add(new CSVFileAndStrategy(new File(generatedCsvFile), cp.cbStrategy.getValue().trim().toLowerCase()));
		cp.proceedCSVs(selectedFiles);
		LogFouineurMain.scene.widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				 System.out.println("Change width => " + LogFouineurMain.scene.getWidth());
				cp.setMaxWidth(LogFouineurMain.scene.getWidth());
				cp.setPrefWidth(LogFouineurMain.scene.getWidth());
				cp.sp.setMaxWidth(LogFouineurMain.scene.getWidth());
				cp.sp.setPrefWidth(LogFouineurMain.scene.getWidth());
				CompositePanel.chart.setMaxWidth(LogFouineurMain.scene.getWidth());
				CompositePanel.chart.setMinWidth(LogFouineurMain.scene.getWidth());
				cp.getTableView().setMinWidth(LogFouineurMain.scene.getWidth());
				cp.getTableView().setPrefWidth(LogFouineurMain.scene.getWidth());
				cp.getTableView().setMaxWidth(LogFouineurMain.scene.getWidth());
				Double div = cp.sp.getDividerPositions()[0];
				cp.getTableView().setPrefHeight(LogFouineurMain.primaryStage.getHeight() * div);
				cp.getTableView().setMaxHeight(LogFouineurMain.primaryStage.getHeight() * div);
				CompositePanel.tableView.setEditable(true);
			}
		});
		CompositePanel.tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		CompositePanel.cbVerbosity.getSelectionModel().select("Normal");
		//LogFouineurMain.primaryStage.show();
		LogFouineurMain.primaryStage.setWidth(width-10);
		
	}

	/**
	 * Show csv viewer.
	 */
	private void showCsvViewer() {
		double width=Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		Button btBrowse = new Button("Browse");
		btBrowse.setStyle("-fx-font-weight:bold;-fx-font-size:12px;");

		HBox menuGen = new HBox();

		VBox screen = new VBox();
		
		SimpleLineChartsMultiYAxis chart = new ZoomableLineChartsMultiYAxis(1.0, true);
		FileChooser fc = new FileChooser();

		Project.root = System.getProperty("root");
		Project.loadDateProperties(System.getProperty("root"));
		Project.loadLogfouineurProperties(System.getProperty("root"));
		Project.workspace = System.getProperty("workspace");

		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("csv Files", "*.csv", "*.csv.gz"));
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
		fc.setInitialDirectory(new File(Project.workspace + File.separator + LogFouineurMain.currentProject
				+ File.separator + LogFouineurMain.currentScenario + File.separator + "csv"));

		SimpleLineChartsMultiYAxis.isPopupMuted = false;
		SimpleLineChartsMultiYAxis.isPopupFullVisible = false;
		CompositePanel cp = new CompositePanel(chart);
		Label lDateFormat = new Label("Date Format: yyyy/MM/dd:HH:mm:ss");
		HBox.setMargin(lDateFormat, new Insets(5, 10, 5, 10));
		HBox.setMargin(btBrowse, new Insets(5, 10, 5, 10));
		menuGen.getChildren().addAll(lDateFormat, btBrowse);
		screen.getChildren().addAll(menuGen, cp);
		LogFouineurMain.bpRoot.setCenter(screen);
		((NumberAxis) CompositePanel.chart.getBaseChart().getXAxis())
				.setTickLabelFormatter(MyTypeAxis.DATECONVERTER.dateConverter(
						((NumberAxis) CompositePanel.chart.getBaseChart().getXAxis()).upperBoundProperty().longValue()
								- ((NumberAxis) CompositePanel.chart.getBaseChart().getXAxis()).lowerBoundProperty()
										.longValue(),
						Locale.FRANCE));
		
		// ((NumberAxis) chart.getBaseChart().getXAxis())
		// .setTickUnit(MyTypeAxis.DATECONVERTER.getTickUnitDefaults()[MyTypeAxis.DATECONVERTER.idxUnit
		// - 1]);
		CompositePanel.chart.timeConverter = MyTypeAxis.DATECONVERTER.myConverter;

		CompositePanel.chart.setXLabel(((MyLongToDateConverter) CompositePanel.chart.timeConverter).getTimeFormat());
		//On choisit un pas de 10s que lon pourra modifier ensuite
		cp.nbSteps.setText("10");
		cp.units.getSelectionModel().select("s");
		btBrowse.setOnAction(event -> {
			List<File> selectedFiles = fc.showOpenMultipleDialog(LogFouineurMain.primaryStage);
			List<CSVFileAndStrategy> list = new ArrayList<CSVFileAndStrategy>();

			if (selectedFiles != null) {
				for (File file : selectedFiles) {
					list.add(new CSVFileAndStrategy(file, cp.cbStrategy.getValue().trim().toLowerCase()));
				}
				cp.proceedCSVs(list);
			}
		});
		
		LogFouineurMain.scene.widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				 System.out.println("Change width => " + LogFouineurMain.scene.getWidth());
				cp.setMaxWidth(LogFouineurMain.scene.getWidth());
				cp.setPrefWidth(LogFouineurMain.scene.getWidth());
				cp.sp.setMaxWidth(LogFouineurMain.scene.getWidth());
				cp.sp.setPrefWidth(LogFouineurMain.scene.getWidth());
				CompositePanel.chart.setMaxWidth(LogFouineurMain.scene.getWidth());
				CompositePanel.chart.setMinWidth(LogFouineurMain.scene.getWidth());
				cp.getTableView().setMinWidth(LogFouineurMain.scene.getWidth());
				cp.getTableView().setPrefWidth(LogFouineurMain.scene.getWidth());
				cp.getTableView().setMaxWidth(LogFouineurMain.scene.getWidth());
				Double div = cp.sp.getDividerPositions()[0];
				cp.getTableView().setPrefHeight(LogFouineurMain.primaryStage.getHeight() * div);
				cp.getTableView().setMaxHeight(LogFouineurMain.primaryStage.getHeight() * div);
				CompositePanel.tableView.setEditable(true);
			}
		});
		CompositePanel.tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		CompositePanel.cbVerbosity.getSelectionModel().select("Normal");
		LogFouineurMain.primaryStage.show();
		LogFouineurMain.primaryStage.setWidth(width-10);
		
		
		
	}
}
