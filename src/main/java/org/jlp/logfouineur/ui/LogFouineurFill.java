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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import org.jlp.javafx.richview.CompositePanel;
import org.jlp.logfouineur.disruptor.GenerateCsvFile;
import org.jlp.logfouineur.disruptor.LogRecordEvent;
import org.jlp.logfouineur.disruptor.ParsingMain;
import org.jlp.logfouineur.models.JFXPivot;
import org.jlp.logfouineur.models.JFXValue;
import org.jlp.logfouineur.models.ParsingConfigHandler;
import org.jlp.logfouineur.records.ConfigRecord;
import org.jlp.logfouineur.records.RecordReader;
import org.jlp.logfouineur.ui.controller.MenuEventHandler;
import org.jlp.logfouineur.ui.tools.MyDialogTestRegex;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;

// TODO: Auto-generated Javadoc
/**
 * The Class LogFouineurFill.
 */
// A class with a static method, because the form is very complex
public class LogFouineurFill {

	/** The bt parse. */
	public static Button btParse = new Button("Parse");

	/** The bt save. */
	public static Button btSave = new Button("Save");

	/** The bt save as template. */
	public static Button btSaveAsTemplate = new Button("SaveAsTemplate");

	/** The bt test regex. */
	public static Button btTestRegex = new Button("Test Regex");

	/** The bt cancel. */
	public static Button btCancel = new Button("Cancel");

	/** The tf file in. */
	// All Controls with a state that must be saved
	public static TextField tfFileIn = new TextField();

	/** The tf step agg. */
	public static TextField tfStepAgg = new TextField();

	/** The tf start regex. */
	public static TextField tfStartRegex = new TextField();

	/** The tf end regex. */
	public static TextField tfEndRegex = new TextField();

	/** The tf include record. */
	public static TextField tfIncludeRecord = new TextField();

	/** The tf exclude record. */
	public static TextField tfExcludeRecord = new TextField();

	/** The rb explicit date. */
	public static RadioButton rbExplicitDate = new RadioButton("Explicit Date");

	/** The rb num locale. */
	public static RadioButton rbNumLocale = new RadioButton("Decimal English (.)?");

	/** The rb implicit step. */
	public static RadioButton rbImplicitStep = new RadioButton("Constant Step ?");

	/** The rb gap regex. */
	public static RadioButton rbGapRegex = new RadioButton("Regex for Gap ?");

	/** The tf step extract. */
	public static TextField tfStepExtract = new TextField();

	/** The tf origin date. */
	public static TextField tfOriginDate = new TextField();

	/** The cb unit step. */
	public static ComboBox<String> cbUnitStep = new ComboBox<String>();

	/** The tf regex date. */
	public static TextField tfRegexDate = new TextField();

	/** The tf java format date. */
	public static TextField tfJavaFormatDate = new TextField();

	/** The tf start date. */
	public static TextField tfStartDate = new TextField();

	/** The tf end date. */
	public static TextField tfEndDate = new TextField();

	/** The tf file out. */
	public static TextField tfFileOut = new TextField();

	/** The tf csv separator. */
	public static TextField tfCsvSeparator = new TextField();

	/** The tf csv java format date. */
	public static TextField tfCsvJavaFormatDate = new TextField();

	/** The cb conf output file. */
	public static ComboBox<String> cbConfOutputFile = new ComboBox<String>();

	/** The rb num locale out. */
	public static RadioButton rbNumLocaleOut = new RadioButton("Decimal English (.)?");

	/** The rb compact output. */
	public static RadioButton rbCompactOutput = new RadioButton("Compact Output files ?");

	/** The tf threads. */
	public static TextField tfThreads = new TextField();

	/** The tf tz. */
	public static TextField tfTz = new TextField();

	/** The rb st date. */
	public static RadioButton rbStDate = new RadioButton("Start Timestamp ?");

	/** The rb direct show. */
	public static RadioButton rbDirectShow = new RadioButton("Show Summary ?");

	/** The rb debug. */
	public static RadioButton rbDebug = new RadioButton("Debug Mode ?");

	/** The rb get date when reading. */
	public static RadioButton rbGetDateWhenReading = new RadioButton("Get Date when reading ?");

	/** The rb parsing. */
	public static RadioButton rbParsing = new RadioButton("Exhaustive Parsing ?");

	/** The rb trace. */
	public static RadioButton rbTrace = new RadioButton("Mode Trace ?");

	/** The cb correct date. */
	public static ComboBox<String> cbCorrectDate = new ComboBox<String>();

	/** The table value. */
	public static TableView<JFXValue> tableValue = new TableView<JFXValue>();

	/** The table pivot. */
	public static TableView<JFXPivot> tablePivot = new TableView<JFXPivot>();

	/** The Constant textArea. */
	public static final TextArea textArea = new TextArea();
	
	/** The bp root. */
	public static BorderPane bpRoot = new BorderPane();

	/**
	 * Inits the.
	 */
	public static void init() {
		ParsingMain.hasDuration = false;
		

		textArea.setPrefWidth(LogFouineurMain.dimScreen.getWidth());
		textArea.setPrefHeight(LogFouineurMain.dimScreen.getHeight() / 8);

		// Filling the textArea with the n firsts lines
		int nbLines = Integer.valueOf(LogFouineurMain.jlProperties.getProperty("logFouineur.linesToRead", "100"));
		StringBuilder sb = new StringBuilder();
		int lastSeparator = LogFouineurMain.fileToParse.lastIndexOf(File.separator);
		String pathRoot = LogFouineurMain.fileToParse.substring(0, lastSeparator + 1);
		int idx1 = pathRoot.indexOf(File.separator + "logs");
		LogFouineurMain.pathToScenario = pathRoot.substring(0, idx1 + 1);
		pathRoot = pathRoot.substring(0, idx1 + 1) + "csv";
		String nameFileBasic = LogFouineurMain.fileToParse.substring(lastSeparator + 1);
		if (nameFileBasic.endsWith(".gz")) {
			int idxTmp = nameFileBasic.indexOf(".gz");
			nameFileBasic = nameFileBasic.substring(0, idxTmp);
		}
		LogFouineurMain.fileToParseBasic = nameFileBasic;
		String currentDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		LogFouineurMain.csvPrefix = nameFileBasic + "_" + currentDate;
		if (LogFouineurMain.fileToParse.endsWith(".gz")) {
			try (FileInputStream fis = new FileInputStream(LogFouineurMain.fileToParse);
					GZIPInputStream gzip = new GZIPInputStream(fis);
					BufferedReader br = new BufferedReader(new InputStreamReader(gzip));) {
				int cpt = 0;
				String line = "";
				while (cpt < nbLines && (null != (line = br.readLine()))) {
					sb.append(line).append("\n");
					cpt++;
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			try (FileReader fr = new FileReader(LogFouineurMain.fileToParse);
					BufferedReader in = new BufferedReader(fr, 1024);) {
				int cpt = 0;
				String line = "";
				while (cpt < nbLines && (null != (line = in.readLine()))) {
					sb.append(line).append("\n");
					cpt++;
				}

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		textArea.setEditable(true);
		textArea.appendText(sb.toString());
		textArea.positionCaret(0);
		textArea.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		textArea.setEditable(false);

		VBox vBox = new VBox();
		vBox.getChildren().add(textArea);
		bpRoot.setTop(vBox);

		// Creation of the TabPane with 3 Tabs ( " Defining a record", "Values",
		// "Pivots")
		TabPane tabPane = new TabPane();
		Tab tabRecord = new Tab();
		Tab tabValues = new Tab();
		Tab tabPivots = new Tab();
		tabRecord.setText("Defining a record");
		tabValues.setText("    Values       ");
		tabPivots.setText("    Pivots       ");

		tabRecord.setStyle("fx-font-family: Arial; -fx-font-size: 14; -fx-font-weight: bold;");
		tabValues.setStyle("fx-font-family: Arial; -fx-font-size: 14; -fx-font-weight: bold;");
		tabPivots.setStyle("fx-font-family: Arial; -fx-font-size: 14; -fx-font-weight: bold;");

		// tab.setContent(new Rectangle(200,200, Color.LIGHTSTEELBLUE));

		tabPane.getTabs().add(tabRecord);
		tabPane.getTabs().add(tabValues);
		tabPane.getTabs().add(tabPivots);
		bpRoot.setCenter(tabPane);

		// tabRecord
		VBox vboxRec = new VBox();
		tabPane.setPrefSize(LogFouineurMain.dimScreen.getWidth(), 4 * LogFouineurMain.dimScreen.getHeight() / 6);
		GridPane panFileIn = new GridPane();
		panFileIn.setHgap(LogFouineurMain.dimScreen.getWidth() / 6);
		panFileIn.setVgap(14);

		// Name File
		Label lFileIn = new Label("File Input :");

		HBox hbFileIn = new HBox(10);
		hbFileIn.setAlignment(Pos.CENTER_LEFT);
		tfFileIn.setEditable(true);
		tfFileIn.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		tfFileIn.setText(LogFouineurMain.fileToParse);
		tfFileIn.setPrefWidth(300);
		tfFileIn.setEditable(false);
		hbFileIn.getChildren().addAll(lFileIn, tfFileIn);
		panFileIn.add(hbFileIn, 0, 0);

		// Step Agg
		tfStepAgg.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		HBox hbStepAgg = new HBox(10);
		hbStepAgg.setAlignment(Pos.CENTER_LEFT);
		Label lStepAgg = new Label("Step Agg (ms) :");
		lStepAgg.setTooltip(new Tooltip(" Step of agregation for the values, default 1000 ms"));
		tfStepAgg.setPrefWidth(100);
		hbStepAgg.getChildren().addAll(lStepAgg, tfStepAgg);
		panFileIn.add(hbStepAgg, 1, 0);

		// Regex Start Record
		HBox hbStartRegex = new HBox(10);
		hbStartRegex.setAlignment(Pos.CENTER_LEFT);
		Label lStartRegex = new Label("Begin of a Record (Regex) :");
		lStartRegex.setTooltip(new Tooltip("Regex as described in Pattern Class Java"));
		tfStartRegex.setPrefWidth(300);
		tfStartRegex.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		tfStartRegex.setPromptText("Fill it with a Regex");
		hbStartRegex.getChildren().addAll(lStartRegex, tfStartRegex);
		panFileIn.add(hbStartRegex, 0, 1);

		// Regex End Record
		HBox hbEndRegex = new HBox(10);
		hbEndRegex.setAlignment(Pos.CENTER_LEFT);
		Label lEndRegex = new Label("End of a Record (Regex) :");
		lEndRegex.setTooltip(new Tooltip("Regex as described in Pattern Class Java"));
		tfEndRegex.setPrefWidth(300);
		tfEndRegex.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		tfEndRegex.setPromptText("Fill it with a Regex");
		hbEndRegex.getChildren().addAll(lEndRegex, tfEndRegex);
		panFileIn.add(hbEndRegex, 1, 1);

		// Regex include record
		HBox hbIncludeRecord = new HBox(10);
		hbIncludeRecord.setAlignment(Pos.CENTER_LEFT);
		Label lIncludeRecord = new Label("Include Record (Regex) :");
		lIncludeRecord.setTooltip(new Tooltip("Regex as described in Pattern Class Java"));
		tfIncludeRecord.setPrefWidth(300);
		tfIncludeRecord.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		tfIncludeRecord.setPromptText("Fill it with a Regex");
		hbIncludeRecord.getChildren().addAll(lIncludeRecord, tfIncludeRecord);
		panFileIn.add(hbIncludeRecord, 0, 2);

		// Regex exclude record
		HBox hbExcludeRecord = new HBox(10);
		hbExcludeRecord.setAlignment(Pos.CENTER_LEFT);
		Label lExcludeRecord = new Label("Exclude Record (Regex) :");
		lExcludeRecord.setTooltip(new Tooltip("Regex as described in Pattern Class Java"));
		tfExcludeRecord.setPrefWidth(300);
		tfExcludeRecord.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		tfExcludeRecord.setPromptText("Fill it with a Regex");
		hbExcludeRecord.getChildren().addAll(lExcludeRecord, tfExcludeRecord);
		panFileIn.add(hbExcludeRecord, 1, 2);

		// Decimal and Implicit date rb
		HBox hbExplicitDate = new HBox(10);
		hbExplicitDate.setAlignment(Pos.CENTER_LEFT);
		rbNumLocale.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		rbNumLocale.setSelected(true);
		rbNumLocale.setOnAction(new ButtonActionEventHandler());
		rbExplicitDate.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		rbExplicitDate.setSelected(true);
		rbExplicitDate.setOnAction(new ButtonActionEventHandler());
		hbExplicitDate.getChildren().addAll(rbNumLocale, rbExplicitDate);
		panFileIn.add(hbExplicitDate, 0, 3);

		// Decimal and Implicit date rb
		HBox hbImplicitDate = new HBox(10);
		hbImplicitDate.setAlignment(Pos.CENTER_LEFT);
		ToggleGroup tgImplicitGroup = new ToggleGroup();
		rbImplicitStep.setToggleGroup(tgImplicitGroup);
		rbImplicitStep.setOnAction(new ButtonActionEventHandler());
		rbGapRegex.setToggleGroup(tgImplicitGroup);
		rbImplicitStep.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		rbImplicitStep.setDisable(false);
		rbGapRegex.setDisable(false);
		rbImplicitStep.setSelected(true);
		rbGapRegex.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		rbGapRegex.setSelected(false);
		rbImplicitStep.setDisable(true);
		rbGapRegex.setDisable(true);

		tfStepExtract.setPrefWidth(300);
		tfStepExtract.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		tfStepExtract.setTooltip(new Tooltip(
				"A Regex or value of step, if Regex a + at the beginning indicates a relative step from the precedent"));
		tfStepExtract.setDisable(true);
		hbImplicitDate.getChildren().addAll(rbImplicitStep, rbGapRegex, tfStepExtract);
		panFileIn.add(hbImplicitDate, 1, 3);

		// Origin Date for DateInMillis or Implicit Date
		HBox hbOriginDate = new HBox(10);
		hbOriginDate.setAlignment(Pos.CENTER_LEFT);
		hbOriginDate.setPrefHeight(40);
		hbOriginDate.setMinHeight(40);
		Label lOriginDate = new Label("Origin Date \n(for DateInMillis or Implicit Date) :");
		lOriginDate.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 8));
		lOriginDate.setWrapText(false);
		lOriginDate.setTooltip(new Tooltip("Fill with an Origin Date with format : yyyy/MM/dd:HH:mm:ss"));
		tfOriginDate.setPrefWidth(300);
		tfOriginDate.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		tfOriginDate.setPromptText("Origin Date with format : yyyy/MM/dd:HH:mm:ss");
		hbOriginDate.getChildren().addAll(lOriginDate, tfOriginDate);
		panFileIn.add(hbOriginDate, 0, 4);

		// unit of the step
		HBox hbUnitStep = new HBox(10);
		hbUnitStep.setAlignment(Pos.CENTER_LEFT);
		Label lUnitStep = new Label("Unit for the time step  :");
		lUnitStep.setTooltip(new Tooltip("Choose a unit in the combobox (ms by default)"));
		cbUnitStep.setPrefWidth(150);
		cbUnitStep.setStyle("-fx-font-family: Arial; -fx-font-size: 12; -fx-font-weight: Bold");
		cbUnitStep.getItems().addAll("micros", "ms", "s", "mn", "hours", "day", "month", "year");
		cbUnitStep.setValue("ms");
		cbUnitStep.setDisable(true);
		cbUnitStep.setOnAction(new ButtonActionEventHandler());
		hbUnitStep.getChildren().addAll(lUnitStep, cbUnitStep);
		panFileIn.add(hbUnitStep, 1, 4);

		// Regex of Date in the record
		HBox hbRegexDate = new HBox(10);
		hbRegexDate.setAlignment(Pos.CENTER_LEFT);
		Label lRegexDate = new Label("Regex Date (Regex) :");
		lRegexDate.setTooltip(new Tooltip("Regex as described in Pattern Class Java"));
		tfRegexDate.setPrefWidth(300);
		tfRegexDate.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		tfRegexDate.setPromptText("Fill it with a Regex");
		hbRegexDate.getChildren().addAll(lRegexDate, tfRegexDate);
		panFileIn.add(hbRegexDate, 0, 5);

		// Java Format Date in the record
		HBox hbJavaFormatDate = new HBox(10);
		hbJavaFormatDate.setAlignment(Pos.CENTER_LEFT);
		Label lJavaFormatDate = new Label("Java Date Format :");
		lJavaFormatDate.setTooltip(new Tooltip("Java Date Format (SimpleDateFormat) or \"DateInMillis\" "));
		tfJavaFormatDate.setPrefWidth(300);
		tfJavaFormatDate.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		tfJavaFormatDate.setPromptText("Java Date Format (SimpleDateFormat) or \"DateInMillis\"");
		hbJavaFormatDate.getChildren().addAll(lJavaFormatDate, tfJavaFormatDate);
		panFileIn.add(hbJavaFormatDate, 1, 5);

		// Start Date of the Parsing ( Start of the scenario by default)
		HBox hbStartDate = new HBox(10);
		hbStartDate.setAlignment(Pos.CENTER_LEFT);
		Label lStartDate = new Label("Start Date of the Parsing :");
		lStartDate.setTooltip(new Tooltip("With the format yyyy/MM/dd:HH:mm:ss"));
		tfStartDate.setPrefWidth(300);
		tfStartDate.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		tfStartDate.setPromptText("Start Date of the Parsing : With the format yyyy/MM/dd:HH:mm:ss");
		hbStartDate.getChildren().addAll(lStartDate, tfStartDate);

		panFileIn.add(hbStartDate, 0, 6);

		// End Date of the Parsing (End of the scenario by default)
		HBox hbEndDate = new HBox(10);
		hbEndDate.setAlignment(Pos.CENTER_LEFT);
		Label lEndDate = new Label("End Date of the Parsing :");
		lEndDate.setTooltip(new Tooltip("With the format yyyy/MM/dd:HH:mm:ss"));
		tfEndDate.setPrefWidth(300);
		tfEndDate.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		tfEndDate.setPromptText("End Date of the Parsing : With the format yyyy/MM/dd:HH:mm:ss");
		hbEndDate.getChildren().addAll(lEndDate, tfEndDate);
		panFileIn.add(hbEndDate, 1, 6);

		panFileIn.setPrefSize(LogFouineurMain.dimScreen.getWidth(), 5 * LogFouineurMain.dimScreen.getHeight() / 15);
		BorderedTitledPane btpFileIn = new BorderedTitledPane("FileIn", panFileIn, Pos.TOP_LEFT);
		vboxRec.getChildren().add(btpFileIn);
		panFileIn.setStyle("-fx-background-color: rgb(252,173,164);");
		panFileIn.setAlignment(Pos.TOP_CENTER);

		// __________________________________________________________________________________________________________________________________
		// PanFileOut
		GridPane panFileOut = new GridPane();
		panFileOut.setPrefSize(LogFouineurMain.dimScreen.getWidth(), 5 * LogFouineurMain.dimScreen.getHeight() / 30);
		BorderedTitledPane btpFileOut = new BorderedTitledPane("FileOut", panFileOut, Pos.TOP_LEFT);
		btpFileOut.setStyle("-fx-border-color: red;");
		vboxRec.getChildren().add(btpFileOut);
		panFileOut.setHgap(LogFouineurMain.dimScreen.getWidth() / 6);
		panFileOut.setVgap(10);
		panFileOut.setStyle("-fx-background-color: rgb(121,252,148);");
		panFileOut.setAlignment(Pos.CENTER);

		// Folder for FileOut
		HBox hbFileOut = new HBox(10);
		hbFileOut.setAlignment(Pos.CENTER_LEFT);
		Label lFileOut = new Label("Folder for File out :");
		lFileOut.setTooltip(new Tooltip("Automatically Filled "));
		tfFileOut.setEditable(true);
		tfFileOut.setText(pathRoot + File.separator + LogFouineurMain.csvPrefix);
		tfFileOut.setEditable(false);
		tfFileOut.setPrefWidth(300);
		tfFileOut.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		tfFileOut.setPromptText("Automatically Filled");
		tfFileOut.setEditable(false);
		hbFileOut.getChildren().addAll(lFileOut, tfFileOut);
		panFileOut.add(hbFileOut, 0, 0);

		// Field Separator for FileOut
		HBox hbCsvSeparator = new HBox(10);
		hbCsvSeparator.setAlignment(Pos.CENTER_LEFT);
		Label lCsvSeparator = new Label("Field Separator for FileOut :");
		lCsvSeparator.setTooltip(new Tooltip("Field character Separator \";\" by default"));
		tfCsvSeparator.setPrefWidth(60);
		tfCsvSeparator.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		tfCsvSeparator.setPromptText("Field character Separator \";\" by default");
		tfCsvSeparator.setText(";");
		hbCsvSeparator.getChildren().addAll(lCsvSeparator, tfCsvSeparator);
		panFileOut.add(hbCsvSeparator, 1, 0);

		// Java Date Format for CSV File out
		HBox hbCsvJavaFormatDate = new HBox(10);
		hbCsvJavaFormatDate.setAlignment(Pos.CENTER_LEFT);
		Label lCsvJavaFormatDate = new Label("Java date Format out :");
		lCsvJavaFormatDate.setTooltip(new Tooltip("Java date Format out like for example yyyy/MM/dd:HH:mm:ss"));
		tfCsvJavaFormatDate.setPrefWidth(300);
		tfCsvJavaFormatDate.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		tfCsvJavaFormatDate.setPromptText("Field character Separator \";\" by default");
		tfCsvJavaFormatDate.setText("yyyy/MM/dd:HH:mm:ss");
		hbCsvJavaFormatDate.getChildren().addAll(lCsvJavaFormatDate, tfCsvJavaFormatDate);
		panFileOut.add(hbCsvJavaFormatDate, 0, 1);

		// Configuration for output files
		HBox hbConfOutputFile = new HBox(10);
		hbConfOutputFile.setAlignment(Pos.CENTER_LEFT);
		Label lConfOutputFile = new Label("Type of outputs");
		lConfOutputFile.setTooltip(new Tooltip("Choose the outputs type:All,Avg,Sum,Max,Min,Pcent90,Count"));
		cbConfOutputFile.setStyle("-fx-font-family: Arial; -fx-font-size: 12; -fx-font-weight: Bold");
		cbConfOutputFile.getItems().addAll("All+Avg", "Avg", "All+Rate", "Rate", "All+Sum", "Sum", "All+Max", "Max",
				"All+Min", "Min", "All+Count", "Count");
		cbConfOutputFile.setValue("All+Avg");
		cbConfOutputFile.setOnAction(new ButtonActionEventHandler());
		rbNumLocaleOut.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		rbNumLocaleOut.setSelected(true);
		rbNumLocaleOut.setOnAction(new ButtonActionEventHandler());
		rbCompactOutput.setSelected(false);
		rbCompactOutput.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		hbConfOutputFile.getChildren().addAll(lConfOutputFile, cbConfOutputFile, rbNumLocaleOut, rbCompactOutput);
		panFileOut.add(hbConfOutputFile, 1, 1);

		// __________________________________________________________________________________________________________________________________
		// PanAdvanced
		GridPane panAdvanced = new GridPane();
		panAdvanced.setPrefSize(LogFouineurMain.dimScreen.getWidth(), 5 * LogFouineurMain.dimScreen.getHeight() / 30);
		BorderedTitledPane btpAdvanced = new BorderedTitledPane("Advanced", panAdvanced, Pos.TOP_LEFT);
		btpAdvanced.setStyle("-fx-border-color: yellow;");
		vboxRec.getChildren().add(btpAdvanced);
		panAdvanced.setStyle("-fx-background-color: rgb(50,202,234);");
		panAdvanced.setHgap(LogFouineurMain.dimScreen.getWidth() / 6);
		panAdvanced.setVgap(10);
		panAdvanced.setAlignment(Pos.CENTER);

		// Threads and TZ
		HBox hbThreadsTz = new HBox(30);
		hbThreadsTz.setAlignment(Pos.CENTER_LEFT);
		HBox hbThreads = new HBox(10);
		hbThreads.setAlignment(Pos.CENTER_LEFT);
		Label lThreads = new Label("Number of Threads :");
		lThreads.setTooltip(new Tooltip("Number of Threads >=0; 0 means that the System decides"));
		tfThreads.setPrefWidth(50);
		tfThreads.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		tfThreads.setPromptText("Number of Threads >=0; 0 means that the System decides");
		tfThreads.setText("0");
		hbThreads.getChildren().addAll(lThreads, tfThreads);
		HBox hbTz = new HBox(10);
		hbTz.setAlignment(Pos.CENTER_LEFT);
		Label lTz = new Label("Decal Time (Tz) :");
		lTz.setTooltip(new Tooltip("Correcting TimeZone in ms"));
		tfTz.setPrefWidth(300);
		tfTz.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		tfTz.setPromptText("Correcting TimeZone in ms");
		tfTz.setText("0");
		hbTz.getChildren().addAll(lTz, tfTz);
		hbThreadsTz.getChildren().addAll(hbThreads, hbTz);
		panAdvanced.add(hbThreadsTz, 0, 0);

		// Start Date and Direct show
		HBox hbstDateDirectShow = new HBox(30);
		hbstDateDirectShow.setAlignment(Pos.CENTER_LEFT);
		HBox hbStDate = new HBox(10);
		rbStDate.setSelected(true);
		hbStDate.getChildren().addAll(rbStDate);
		HBox hbDirectShow = new HBox(10);
		rbDirectShow.setSelected(false);
		hbDirectShow.getChildren().addAll(rbDirectShow);
		hbstDateDirectShow.getChildren().addAll(hbStDate, hbDirectShow);
		rbStDate.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		rbDirectShow.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		;
		rbStDate.setTooltip(new Tooltip("Cheched, the date is the start of the request"));
		rbStDate.setOnAction(new ButtonActionEventHandler());
		rbDirectShow.setOnAction(new ButtonActionEventHandler());
		panAdvanced.add(hbstDateDirectShow, 1, 0);

		// Debug and Exhaustive parsing
		HBox hbDebugParsing = new HBox(30);
		hbDebugParsing.setAlignment(Pos.CENTER_LEFT);
		HBox hbDebug = new HBox(10);
		rbDebug.setSelected(false);
		hbDebug.getChildren().addAll(rbDebug);
		HBox hbParsing = new HBox(10);
		rbParsing.setSelected(false);
		hbParsing.getChildren().addAll(rbParsing);
		rbParsing.setTooltip(new Tooltip(
				"When selected : A record can be matched with several regex else only the first regex is used if it matches"));
		HBox hbDateReading = new HBox(10);
		rbGetDateWhenReading.setSelected(true);
		hbDateReading.getChildren().addAll(rbGetDateWhenReading);
		rbGetDateWhenReading.setTooltip(new Tooltip("When selected : The date is computed when reading the  record "));
		hbDebugParsing.getChildren().addAll(hbDebug, hbParsing, hbDateReading);
		rbDebug.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		rbParsing.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		rbGetDateWhenReading.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		;
		rbDebug.setOnAction(new ButtonActionEventHandler());
		rbParsing.setOnAction(new ButtonActionEventHandler());
		panAdvanced.add(hbDebugParsing, 0, 1);

		// Trace and Correct date
		HBox hbTraceCorrectDate = new HBox(30);
		hbTraceCorrectDate.setAlignment(Pos.CENTER_LEFT);
		HBox hbTrace = new HBox(10);
		rbTrace.setSelected(false);
		hbTrace.getChildren().addAll(rbTrace);
		HBox hbCorrectDate = new HBox(10);
		Label lCorrectDate = new Label("Correction of Date");
		lCorrectDate.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		lCorrectDate.setTooltip(new Tooltip("0 no correction, +1 add the duration, -1 substract the duration"));
		cbCorrectDate.getItems().clear();
		cbCorrectDate.getItems().addAll("0", "+1", "-1");
		cbCorrectDate.setValue("0");
		hbCorrectDate.getChildren().addAll(lCorrectDate, cbCorrectDate);
		rbTrace.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		cbCorrectDate.setStyle("-fx-font-family: Arial; -fx-font-size: 12; -fx-font-weight: Bold");
		rbTrace.setOnAction(new ButtonActionEventHandler());
		cbCorrectDate.setOnAction(new ButtonActionEventHandler());
		hbTraceCorrectDate.getChildren().addAll(hbTrace, hbCorrectDate);
		panAdvanced.add(hbTraceCorrectDate, 1, 1);

		tabRecord.setContent(vboxRec);

		// Table of Values ( name, regex1,regex2,unit,scale)

		TableColumn<JFXValue, String> nameColVal = new TableColumn<JFXValue, String>("Value Name");
		nameColVal.setCellValueFactory(new PropertyValueFactory<JFXValue, String>("name"));
		nameColVal.setStyle("-fx-font-family: Arial; -fx-font-size: 12; -fx-font-weight: Bold");

		TableColumn<JFXValue, String> regex1ColVal = new TableColumn<JFXValue, String>("Regex1/Function");
		regex1ColVal.setCellValueFactory(new PropertyValueFactory<JFXValue, String>("regex1"));
		regex1ColVal.setStyle("-fx-font-family: Arial; -fx-font-size: 12; -fx-font-weight: Bold");

		TableColumn<JFXValue, String> regex2ColVal = new TableColumn<JFXValue, String>("Regex2/Params");
		regex2ColVal.setCellValueFactory(new PropertyValueFactory<JFXValue, String>("regex2"));
		regex2ColVal.setStyle("-fx-font-family: Arial; -fx-font-size: 12; -fx-font-weight: Bold");

		TableColumn<JFXValue, String> unitColVal = new TableColumn<JFXValue, String>("Unit");
		unitColVal.setCellValueFactory(new PropertyValueFactory<JFXValue, String>("unit"));
		unitColVal.setStyle("-fx-font-family: Arial; -fx-font-size: 12; -fx-font-weight: Bold");

		TableColumn<JFXValue, Double> scaleColVal = new TableColumn<JFXValue, Double>("Scale");
	

		TableColumn<JFXValue, Boolean> isDurationColVal = new TableColumn<JFXValue, Boolean>("Duration ?");
		isDurationColVal.setCellValueFactory(new PropertyValueFactory<JFXValue, Boolean>("isDuration"));
		isDurationColVal.setStyle("-fx-font-family: Arial; -fx-font-size: 12; -fx-font-weight: Bold");

		tableValue.getColumns().add(nameColVal);
		tableValue.getColumns().add(regex1ColVal);
		tableValue.getColumns().add(regex2ColVal);
		tableValue.getColumns().add(unitColVal);
		tableValue.getColumns().add(scaleColVal);
		tableValue.getColumns().add(isDurationColVal);

		nameColVal.setPrefWidth(2 * LogFouineurMain.dimScreen.getWidth() / 15);
		regex1ColVal.setPrefWidth(5 * LogFouineurMain.dimScreen.getWidth() / 15);
		regex2ColVal.setPrefWidth(5 * LogFouineurMain.dimScreen.getWidth() / 15);
		unitColVal.setPrefWidth(LogFouineurMain.dimScreen.getWidth() / 15);
		scaleColVal.setPrefWidth(LogFouineurMain.dimScreen.getWidth() / 15);
		isDurationColVal.setPrefWidth(LogFouineurMain.dimScreen.getWidth() / 15);

		for (int i = 0; i < 100; i++)
			tableValue.getItems().add(new JFXValue("", "", "", "", 0d, false));
		tableValue.setEditable(true);
		tableValue.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		tableValue.setOnKeyPressed(new MyKeyEventHandler(tableValue));

		tableValue.setEditable(true);
		;
		nameColVal.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		nameColVal.setCellFactory(col -> new EditCellString<JFXValue, String>());
	

		regex1ColVal.setCellValueFactory(cellData -> cellData.getValue().regex1Property());
		regex1ColVal.setCellFactory(col -> new EditCellString<JFXValue, String>());
		
		regex2ColVal.setCellValueFactory(cellData -> cellData.getValue().regex2Property());
		regex2ColVal.setCellFactory(col -> new EditCellString<JFXValue, String>());
		
		
		unitColVal.setCellValueFactory(cellData -> cellData.getValue().unitProperty());
		unitColVal.setCellFactory(col -> new EditCellString<JFXValue, String>());
	
		scaleColVal.setCellValueFactory(new PropertyValueFactory<JFXValue, Double>("scale"));
		scaleColVal.setCellFactory(col ->new EditCellDouble<JFXValue, Double>());
//		scaleColVal.setCellFactory(col -> {
//			TableCell<JFXValue, Double> cell = new TableCell<JFXValue, Double>() {
//				@Override
//				public void updateItem(Double item, boolean empty) {
//					if (empty || item == null) {
//						setGraphic(null);
//
//					} else {
//						setText(new MyDoubleFormatterScale().toString(item));
//						setStyle("-fx-font-weight:bold;-fx-font-size:12px;");
//
//					}
//				}
//			};
//
//			return cell;
//		});
		scaleColVal.setStyle("-fx-font-family: Arial; -fx-font-size: 12; -fx-font-weight: Bold");
	


		isDurationColVal
				.setCellValueFactory(new Callback<CellDataFeatures<JFXValue, Boolean>, ObservableValue<Boolean>>() {
					@Override
					public ObservableValue<Boolean> call(CellDataFeatures<JFXValue, Boolean> p) {
						if (p.getValue().isIsDuration()) {
							
							ParsingMain.hasDuration = true;
						}
						return p.getValue().isDurationProperty();
					}
				});
		isDurationColVal.setCellFactory(new Callback<TableColumn<JFXValue, Boolean>, TableCell<JFXValue, Boolean>>() {
			@Override
			public TableCell<JFXValue, Boolean> call(TableColumn<JFXValue, Boolean> p) {
				return new CheckBoxTableCell<>();
			}
		});

	

		tabValues.setContent(tableValue);
		

		// Table of Pivots ( name, regex1,regex2)

		TableColumn<JFXPivot, String> nameColPiv = new TableColumn<JFXPivot, String>("Pivot Name");
		nameColPiv.setCellValueFactory(new PropertyValueFactory<JFXPivot, String>("name"));
		nameColPiv.setStyle("-fx-font-family: Arial; -fx-font-size: 12; -fx-font-weight: Bold");

		TableColumn<JFXPivot, String> regex1ColPiv = new TableColumn<JFXPivot, String>("Regex1");
		regex1ColPiv.setCellValueFactory(new PropertyValueFactory<JFXPivot, String>("regex1"));
		regex1ColPiv.setStyle("-fx-font-family: Arial; -fx-font-size: 12; -fx-font-weight: Bold");

		TableColumn<JFXPivot, String> regex2ColPiv = new TableColumn<JFXPivot, String>("Regex2");
		regex2ColPiv.setCellValueFactory(new PropertyValueFactory<JFXPivot, String>("regex2"));
		regex2ColPiv.setStyle("-fx-font-family: Arial; -fx-font-size: 12; -fx-font-weight: Bold");

		tablePivot.getColumns().add(nameColPiv);
		tablePivot.getColumns().add(regex1ColPiv);
		tablePivot.getColumns().add(regex2ColPiv);

		nameColPiv.setPrefWidth(3 * LogFouineurMain.dimScreen.getWidth() / 13);
		regex1ColPiv.setPrefWidth(5 * LogFouineurMain.dimScreen.getWidth() / 13);
		regex2ColPiv.setPrefWidth(5 * LogFouineurMain.dimScreen.getWidth() / 13);

		for (int i = 0; i < 100; i++)
			tablePivot.getItems().add(new JFXPivot("", "", ""));
		tablePivot.setEditable(true);
		tablePivot.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		tablePivot.setOnKeyPressed(new MyKeyEventHandler(tablePivot));
		tablePivot.setOnMouseClicked(new MyMouseEventHandlerLocal(tablePivot));

		// A textField to update every column
		// nameColPiv.setCellFactory(TextFieldTableCell.forTableColumn());
		nameColPiv.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		nameColPiv.setCellFactory(col -> new EditCellString<JFXPivot, String>());
		
		regex1ColPiv.setCellValueFactory(cellData -> cellData.getValue().regex1Property());
		regex1ColPiv.setCellFactory(col -> new EditCellString<JFXPivot, String>());
		
		regex2ColPiv.setCellValueFactory(cellData -> cellData.getValue().regex2Property());
		regex2ColPiv.setCellFactory(col -> new EditCellString<JFXPivot, String>());
		

		
		tabPivots.setContent(tablePivot);
		tabPivots.getTabPane().setPrefWidth(LogFouineurMain.bpRoot.getWidth());

		// __________________________________________________________________________________________________________________________________
		// Defining a HBox with 5 buttons
		String styleBut = "fx-font-family: Arial; -fx-font-size: 14; -fx-font-weight: bold;";
		btParse.setStyle(styleBut);
		btSave.setStyle(styleBut);
		btSaveAsTemplate.setStyle(styleBut);
		btTestRegex.setStyle(styleBut);
		btCancel.setStyle(styleBut);
		double butWidth = 180;
		btParse.setPrefWidth(butWidth);
		btSave.setPrefWidth(butWidth);
		btSaveAsTemplate.setPrefWidth(butWidth);
		btTestRegex.setPrefWidth(butWidth);
		btCancel.setPrefWidth(butWidth);

		HBox hbox = new HBox();
		hbox.setPrefHeight(LogFouineurMain.dimScreen.getHeight() / 20);
		hbox.setMinHeight(30);
		hbox.setSpacing(LogFouineurMain.dimScreen.getWidth() / 15);
		hbox.getChildren().addAll(btParse, btSave, btSaveAsTemplate, btTestRegex, btCancel);
		btSave.setOnAction(new ButtonActionEventHandler());
		btSaveAsTemplate.setOnAction(new ButtonActionEventHandler());
		btCancel.setOnAction(new ButtonActionEventHandler());
		btTestRegex.setOnAction(new ButtonActionEventHandler());
		btParse.setOnAction(new ButtonActionEventHandler());
		hbox.setAlignment(Pos.CENTER);
		bpRoot.setBottom(hbox);

		LogFouineurMain.bpRoot.setCenter(bpRoot);
		// gestion of existing configuration or templates
		if (!new File(LogFouineurMain.pathToScenario + "logs" + File.separator + "config" + File.separator + "parselogs"
				+ File.separator + LogFouineurMain.fileToParseBasic + ".properties").exists()) {
			tfStartDate.setText(
					LogFouineurMain.scenariosProps.getProperty(LogFouineurMain.currentScenario + ".dateBegin"));
			tfEndDate.setText(LogFouineurMain.scenariosProps.getProperty(LogFouineurMain.currentScenario + ".dateEnd"));
		}
		if ((LogFouineurMain.parseGenTemplate.trim() + LogFouineurMain.parseLocTemplate.trim()).length() == 0) {
			new ParsingConfigHandler(LogFouineurMain.fileToParse).loadFromConfig();
		} else {
			new ParsingConfigHandler(LogFouineurMain.fileToParse).loadFromTemplate();
			// override the date of the scenario
			tfStartDate.setText(
					LogFouineurMain.scenariosProps.getProperty(LogFouineurMain.currentScenario + ".dateBegin"));
			tfEndDate.setText(LogFouineurMain.scenariosProps.getProperty(LogFouineurMain.currentScenario + ".dateEnd"));
		}

		// tip
		rbTrace.setTooltip(
				new Tooltip("Put the records extracted in  <workspace>/<Project>/<scenario>/logs/trace_<fileName>"));
		rbDebug.setTooltip(new Tooltip("Put errors in  <root_LogFouineur>/logs/debug_logfouineur.txt"));
		LogFouineurMain.primaryStage.show();
	}

	/**
	 * Clear.
	 */
	public static void clear() {
		LogFouineurMain.bpRoot.setCenter(null);

		LogFouineurMain.bpRoot.setBottom(null);

		LogFouineurMain.primaryStage.show();

	}
}

class ButtonActionEventHandler implements EventHandler<ActionEvent> {
	private static void closeSafely(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				// Ignore
			}
		}
	}

	public static Stream<String> linesFromGzFile(Path path) {
		InputStream fileIs = null;
		BufferedInputStream bufferedIs = null;
		GZIPInputStream gzipIs = null;
		try {
			fileIs = Files.newInputStream(path);
			// Even though GZIPInputStream has a buffer it reads individual
			// bytes
			// when processing the header, better add a buffer in-between
			bufferedIs = new BufferedInputStream(fileIs, 10 * 1024 * 1024);
			gzipIs = new GZIPInputStream(bufferedIs);
		} catch (IOException e) {
			closeSafely(gzipIs);
			closeSafely(bufferedIs);
			closeSafely(fileIs);
			throw new UncheckedIOException(e);
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(gzipIs));
		return reader.lines().onClose(() -> closeSafely(reader));
	}

	@Override
	public void handle(ActionEvent event) {
		if (event.getSource() instanceof Button) {
			if ((Button) event.getSource() == LogFouineurFill.btSave) {
				if (null == LogFouineurFill.tfStartRegex.getText()
						|| LogFouineurFill.tfStartRegex.getText().trim().length() == 0) {
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							Alert alert = new Alert(AlertType.ERROR);
							alert.setTitle("Information Dialog");

							alert.setHeaderText("A regex for start record is mandatory ");

							String text = "A regex for start record is mandatory, it can't br empty";

							alert.setContentText("Errors : \n" + text);
							alert.setResizable(true);
							alert.getDialogPane().setPrefSize(480, 320);
							System.out.println("!!----------Errors -> " + text);
							alert.showAndWait();
						}
					});
				} else {

					new ParsingConfigHandler(LogFouineurMain.fileToParse).save();
				}
			} else if ((Button) event.getSource() == LogFouineurFill.btCancel) {
				LogFouineurFill.clear();
				LogFouineurMain.bpRoot.setCenter(LogFouineurMain.imgView);
				LogFouineurMain.csvPrefix = "";
				LogFouineurMain.parseGenTemplate = "";
				LogFouineurMain.parseLocTemplate = "";
				LogFouineurMain.fileToParse = "";
				LogFouineurMain.fileToParseBasic = "";
				LogFouineurMain.currentScenarioProps.clear();
				LogFouineurMain.primaryStage.setTitle("LogFouineurMain V1.0 : Project : "
						+ LogFouineurMain.currentProject + " with scenario :" + LogFouineurMain.currentScenario);
			} else if ((Button) event.getSource() == LogFouineurFill.btSaveAsTemplate) {
				System.out.println("Saving Template Configuration");
				if (null == LogFouineurFill.tfStartRegex.getText()
						|| LogFouineurFill.tfStartRegex.getText().trim().length() == 0) {
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							Alert alert = new Alert(AlertType.ERROR);
							alert.setTitle("Information Dialog");

							alert.setHeaderText("A regex for start record is mandatory ");

							String text = "A regex for start record is mandatory, it can't br empty";

							alert.setContentText("Errors : \n" + text);
							alert.setResizable(true);
							alert.getDialogPane().setPrefSize(480, 320);
							System.out.println("!!----------Errors -> " + text);
							alert.showAndWait();
						}
					});
				} else {
					new TemplateSaving(LogFouineurMain.primaryStage);
				}
			} else if ((Button) event.getSource() == LogFouineurFill.btTestRegex) {
				System.out.println("Testing Regex against text");
				new MyDialogTestRegex("Testing Regular Expressions");
			} else if ((Button) event.getSource() == LogFouineurFill.btParse) {
				// save first
				// testing if debRegexp is not null;
				if (null == LogFouineurFill.tfStartRegex.getText()
						|| LogFouineurFill.tfStartRegex.getText().trim().length() == 0) {
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							Alert alert = new Alert(AlertType.ERROR);
							alert.setTitle("Information Dialog");

							alert.setHeaderText("A regex for start record is mandatory ");

							String text = "A regex for start record is mandatory, it can't br empty";

							alert.setContentText("Errors : \n" + text);
							alert.setResizable(true);
							alert.getDialogPane().setPrefSize(480, 320);
							System.out.println("!!----------Errors -> " + text);
							alert.showAndWait();
						}
					});
				} else {
					new ParsingConfigHandler(LogFouineurMain.fileToParse).save();
					// File properties OK
					if (LogFouineurFill.rbTrace.isSelected()) {
						// initialise file to write records
						try {
							String strFile = System.getProperty("workspace") + File.separator
									+ LogFouineurMain.currentProject + File.separator + LogFouineurMain.currentScenario
									+ File.separator + "logs" + File.separator + "trace_"
									+ LogFouineurMain.fileToParseBasic;
							System.out.println("Opening Channel for file : " + strFile);
							File file = new File(strFile);
							if (file.exists())
								file.delete();
							LogFouineurMain.fos = new FileOutputStream(file, true);
							LogFouineurMain.channel = LogFouineurMain.fos.getChannel();
							int sizeBuffer = Integer.parseInt(
									LogFouineurMain.jlProperties.getProperty("logFouineur.byteBufferSize", "10485760"));
							LogFouineurMain.buf = ByteBuffer.allocateDirect(sizeBuffer);
							LogFouineurMain.waterline = (int) sizeBuffer * 9 / 10;
							LogFouineurMain.isTrace = true;

						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} else {
						LogFouineurMain.fos = null;
						LogFouineurMain.channel = null;
						LogFouineurMain.buf = null;
						LogFouineurMain.isTrace = false;
					}
					if (LogFouineurFill.rbDebug.isSelected()) {
						// initialise file to write records
						try {
							String strFile = System.getProperty("root") + File.separator + "logs" + File.separator
									+ "debug.txt";
							System.out.println("Opening Channel for file : " + strFile);
							File file = new File(strFile);
							if (file.exists())
								file.delete();
							LogFouineurMain.fosDebug = new FileOutputStream(file, true);
							LogFouineurMain.channelDebug = LogFouineurMain.fosDebug.getChannel();
							int sizeBuffer = Integer.parseInt(
									LogFouineurMain.jlProperties.getProperty("logFouineur.byteBufferSize", "10485760"));
							LogFouineurMain.bufDebug = ByteBuffer.allocateDirect(sizeBuffer);
							LogFouineurMain.waterlineDebug = (int) sizeBuffer * 9 / 10;
							LogFouineurMain.isDebug = true;

						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} else {
						LogFouineurMain.fosDebug = null;
						LogFouineurMain.channelDebug = null;
						LogFouineurMain.bufDebug = null;
						LogFouineurMain.isDebug = false;
					}

					Task<Void> task = new Task<Void>() {
						@Override
						public Void call() throws Exception {

							Stream<String> stream = null;

							System.out.println("Parsing in LogMainFill");
							if (LogFouineurMain.fileToParse.endsWith(".gz")) {
								stream = linesFromGzFile(new File(LogFouineurMain.fileToParse).toPath());

							} else {
								try {
									stream = Files.lines(new File(LogFouineurMain.fileToParse).toPath());
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							LogFouineurMain.errorFormatDate = "";
							LogFouineurMain.errorFormatValue = "";
							Stream<LogRecordEvent> streamLREnew = new RecordReader().read(stream);

							ParsingMain.parse(streamLREnew);

							return null;
						}
					};
					// File Properties OK
					task.setOnSucceeded(e -> {
						LogFouineurMain.scene.getRoot().setDisable(false);
						LogFouineurMain.scene.setCursor(Cursor.DEFAULT);
						if (	ConfigRecord.showSummary && null!= GenerateCsvFile.strSummaryFile) {
							
							LogFouineurFill.clear();
							
							CompositePanel.tableView.getColumns().clear();
							
							LogFouineurMain.bpRoot.setCenter(null);
							System.out.println("showing summary ");
							new MenuEventHandler().showCsvViewer(GenerateCsvFile.strSummaryFile);
						}
					});

					LogFouineurMain.scene.getRoot().setDisable(true);
					LogFouineurMain.scene.setCursor(Cursor.WAIT);

					Thread t = new Thread(task);
					t.setDaemon(true);
					t.start();
					// File Properties OK

				}
			}

		} else if (event.getSource() instanceof RadioButton)

		{
			if ((RadioButton) event.getSource() == LogFouineurFill.rbExplicitDate) {
				if (!LogFouineurFill.rbExplicitDate.isSelected()) {
					LogFouineurFill.rbImplicitStep.setDisable(false);
					LogFouineurFill.rbGapRegex.setDisable(false);
					LogFouineurFill.tfStepExtract.setDisable(false);
					LogFouineurFill.tfStepExtract.setEditable(true);
					LogFouineurFill.cbUnitStep.setDisable(false);
				} else {

					LogFouineurFill.rbImplicitStep.setDisable(true);
					LogFouineurFill.rbGapRegex.setDisable(true);
					LogFouineurFill.tfStepExtract.setText("");
					;

					LogFouineurFill.tfStepExtract.setEditable(false);
					LogFouineurFill.tfStepExtract.setDisable(true);
					LogFouineurFill.cbUnitStep.setDisable(true);
				}
			} else if ((RadioButton) event.getSource() == LogFouineurFill.rbImplicitStep) {
				if (LogFouineurFill.rbImplicitStep.isSelected()) {
					LogFouineurFill.rbExplicitDate.setSelected(false);

					LogFouineurFill.tfStepExtract.setDisable(false);
					LogFouineurFill.tfStepExtract.setEditable(true);

				} else {

					LogFouineurFill.tfStepExtract.setEditable(false);
					LogFouineurFill.tfStepExtract.setDisable(true);

				}

			} else if ((RadioButton) event.getSource() == LogFouineurFill.rbTrace) {
				if (!LogFouineurFill.rbTrace.isSelected()) {
					LogFouineurMain.isTrace = true;
				} else {
					LogFouineurMain.isTrace = false;
				}

			}
		}

	}

}

class MyMouseEventHandlerLocal implements EventHandler<MouseEvent> {
	public TableView<?> table;

	public MyMouseEventHandlerLocal(TableView<?> table) {
		this.table = table;
	}

	@Override
	public void handle(MouseEvent event) {
		System.out.println("MouseEvent => " + event.getButton().name());
		if (table == LogFouineurFill.tablePivot) {
			if (!event.getButton().name().equals("PRIMARY")) {
				FileChooser fileChooser = new FileChooser();
				int lastSeparator = LogFouineurMain.fileToParse.lastIndexOf(File.separator);
				fileChooser.setInitialDirectory(new File(LogFouineurMain.fileToParse.substring(0, lastSeparator)));
				fileChooser.setTitle("Open a csv File to fill Pivots Table");
				fileChooser.getExtensionFilters().addAll(new ExtensionFilter("csvFiles", "*.csv"),
						new ExtensionFilter("All Files", "*.*"));

				File selectedFile = fileChooser.showOpenDialog(LogFouineurMain.primaryStage);

				if (selectedFile != null) {
					fillTablePivot(selectedFile);
				}
			} else if (event.getButton().name().equals("PRIMARY")) {
				TablePosition tp = table.getFocusModel().getFocusedCell();

			}
		}
	}

	private void fillTablePivot(File selectedFile) {
		try (RandomAccessFile raf=new RandomAccessFile(selectedFile,"r");){
			// skip first line which is the title
			String line= raf.readLine();
			String sep=LogFouineurFill.tfCsvSeparator.getText().trim();
			String[] tabStr=line.split(sep);
			int criteria=0;
			for (criteria=0; criteria<tabStr.length;criteria++) {
				if(tabStr[criteria].trim().toLowerCase().equals("criteria")) {
					break;
				}
			}
			LogFouineurFill.tablePivot.getItems().clear();
			while (null!=(line=raf.readLine())) {
				String strRegex=line.split(sep)[criteria];
				String name=strRegex.replaceAll("/","").replaceAll("\\$","").replaceAll("\\s","").replaceAll("%","").replaceAll("&","").
						replaceAll("\\?","").replaceAll("=","").replaceAll(",", "").replaceAll("\\:","");
				JFXPivot piv=new JFXPivot(name,strRegex,"");
				LogFouineurFill.tablePivot.getItems().add(piv);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
	}

}

class MyKeyEventHandler implements EventHandler<KeyEvent> {
	public TableView<? extends Object> table;
	public static ObservableList<JFXValue> listCopyValues = null;
	public static ObservableList<JFXPivot> listCopyPivots = null;
	public static int firstEmpty = 0;

	public MyKeyEventHandler(TableView<?> table) {
		this.table = table;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void handle(KeyEvent event) {
		System.out.println("KeyCode : " + ((KeyEvent) event).getCode());
		if (((KeyEvent) event).getCode() == KeyCode.DELETE) {
			if (table == LogFouineurFill.tableValue) {
				@SuppressWarnings("unchecked")

				ObservableList<JFXValue> listDelValues = (ObservableList<JFXValue>) table.getSelectionModel()
						.getSelectedItems();
				@SuppressWarnings("unchecked")
				ObservableList<JFXValue> listValues = (ObservableList<JFXValue>) table.getItems();
				int nbToDel = listDelValues.size();
				listValues.removeAll(listDelValues);
				//
				for (int i = nbToDel; i > 0; i--) {

					listValues.add(listValues.size(), new JFXValue());

				}
				LogFouineurFill.tableValue.setItems(listValues);
				System.out.println("listValues.size=" + listValues.size());
				LogFouineurFill.tableValue.refresh();

			} else if (table == LogFouineurFill.tablePivot) {
				@SuppressWarnings("unchecked")

				ObservableList<JFXPivot> listDelPivots = (ObservableList<JFXPivot>) table.getSelectionModel()
						.getSelectedItems();
				@SuppressWarnings("unchecked")
				ObservableList<JFXPivot> listPivots = (ObservableList<JFXPivot>) table.getItems();
				int nbToDel = listDelPivots.size();
				listPivots.removeAll(listDelPivots);

				for (int i = nbToDel; i > 0; i--) {
					listPivots.add(listPivots.size(), new JFXPivot());
				}
				LogFouineurFill.tablePivot.setItems(listPivots);
				LogFouineurFill.tablePivot.refresh();

			}
		} else if (((KeyEvent) event).isControlDown() && ((KeyEvent) event).getCode() == KeyCode.C) {
			System.out.println("Dtecting a Control C for copying");
			if (table == LogFouineurFill.tableValue) {

				listCopyValues = (ObservableList<JFXValue>) table.getSelectionModel().getSelectedItems();

			} else if (table == LogFouineurFill.tablePivot) {
				listCopyPivots = (ObservableList<JFXPivot>) table.getSelectionModel().getSelectedItems();

			}

		} else if (((KeyEvent) event).isControlDown() && ((KeyEvent) event).getCode() == KeyCode.V) {
			System.out.println("Dtecting a Control V for Paste");
			if (table == LogFouineurFill.tableValue) {
				// the lines are added at the end
				if (null != listCopyValues && !listCopyValues.isEmpty()) {
					// (ObservableList<JFXValue>) lit
					ObservableList<JFXValue> items = ((ObservableList<JFXValue>) table.getItems());
					firstEmpty = 0;
					for (JFXValue value : items) {
						if (null == value.getName() || value.getName().trim().length() == 0) {

							break;
						}
						firstEmpty++;
					}
					listCopyValues.stream().forEach(value -> {
						System.out.println("adding item => " + value.getName());
						items.add(firstEmpty, JFXValue.clone(value));
						firstEmpty++;
						System.out
								.println(" for adding item => " + value.getName() + " items.length => " + items.size());
					});
					LogFouineurFill.tableValue.setItems(items);
				}

				// LogFouineurFill.tableValue.refresh();
				listCopyValues = null;
			} else if (table == LogFouineurFill.tablePivot) {

				if (null != listCopyPivots && !listCopyPivots.isEmpty()) {
					ObservableList<JFXPivot> items = ((ObservableList<JFXPivot>) table.getItems());
					firstEmpty = 0;
					for (JFXPivot pivot : items) {
						if (null == pivot.getName() || pivot.getName().trim().length() == 0) {

							break;
						}
						firstEmpty++;
					}
					listCopyPivots.stream().forEach(pivot -> {
						items.add(firstEmpty, JFXPivot.clone(pivot));
						firstEmpty = 0;
					});
					LogFouineurFill.tablePivot.setItems(items);
				}

				LogFouineurFill.tablePivot.refresh();
				listCopyPivots = null;
			}

		}
	}

}
