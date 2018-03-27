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
package org.jlp.logfouineur.filestat.ui;

import java.awt.Dimension;


import org.jlp.logfouineur.disruptor.LogRecordEvent;
import org.jlp.logfouineur.disruptor.LogRecordEventProducer;
import org.jlp.logfouineur.disruptor.ParsingMain;
import org.jlp.logfouineur.disruptor.RecordEventHandlerDatedRecord;
import org.jlp.logfouineur.filestat.disruptor.FileStatEvent;
import org.jlp.logfouineur.filestat.disruptor.FileStatsLineHandler;
import org.jlp.logfouineur.filestat.disruptor.FileStatsLineHandlerDated;
import org.jlp.logfouineur.filestat.disruptor.FileStatsLineProducer;
import org.jlp.logfouineur.filestat.models.CumulEnregistrementStat;
import org.jlp.logfouineur.models.AggLogRecordEvent;
import org.jlp.logfouineur.models.JFXValue;
import org.jlp.logfouineur.models.ParsingConfigHandler;
import org.jlp.logfouineur.records.ConfigRecord;
import org.jlp.logfouineur.records.RecordReader;
import org.jlp.logfouineur.ui.LogFouineurFill;
import org.jlp.logfouineur.ui.LogFouineurMain;
import org.jlp.logfouineur.ui.Messages;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.TimeoutException;

import java.util.Properties;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.channels.Channel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

// TODO: Auto-generated Javadoc
/**
 * The Class DiagFileStats.
 */
public class DiagFileStats extends Stage {
	/** The debug. */
	public static int reads = 1;

	/** The tab treated. */
	public static int[] tabTreated=null;
	
	/** The tab filtered. */
	public static int[] tabFiltered=null;
	
	/** The tab eliminated. */
	public static int[] tabEliminated=null;
	
	/** The extract. */
	public static String extract = "";

	/** The is parsing. */
	public static boolean isParsing = false;
	
	/** The hmaps class. */
	// Hasmap for plugin to compute values
	public static HashMap<String, Object>[] hmapsClass = null; // a hashMap by

	/** The hmaps method. */
	// thread
	public static HashMap<String, Method>[] hmapsMethod = null; // a hashMap by

	/** The has plugins. */
	// thread
	public static boolean hasPlugins = false;

	/** The url class loader. */
	public static URLClassLoader urlClassLoader = null;

	/** The all hm cumul. */
	// Structure accueil
	public static HashMap<String, CumulEnregistrementStat> allHmCumul = new HashMap<String, CumulEnregistrementStat>();
	
	/** The threads. */
	public static EventHandler<FileStatEvent>[] threads = null;

	/** The nb record reads. */
	public static int nbRecordReads = 0;
	
	/** The scene. */
	public static Scene scene;
	
	/** The file stat props. */
	public static Properties fileStatProps = new Properties();
	
	/** The is dated file. */
	public boolean isDatedFile = true;
	
	/** The file name. */
	public static String fileName = "";
	
	/** The prefix file name. */
	public String prefixFileName = "";
	
	/** The regex date. */
	public static String regexDate = "";
	
	/** The java date format. */
	public static String javaDateFormat = "";
	
	/** The sdf. */
	public static SimpleDateFormat sdf = null;
	
	/** The str dir config. */
	public static String strDirConfig = "";

	/** The dim screen. */
	public static Dimension dimScreen = Toolkit.getDefaultToolkit().getScreenSize();
	
	/** The vbox top. */
	VBox vboxTop = new VBox();
	
	/** The vbox bottom. */
	VBox vboxBottom = new VBox();

	/** The content pane. */
	VBox contentPane = new VBox();

	// In vboxTop
	/** The hb file choose. */
	// 1 - Choose a file
	HBox hbFileChoose = new HBox();
	
	/** The lb file choose. */
	Label lbFileChoose = new Label("Choosen File :");
	
	/** The tf file choose. */
	TextField tfFileChoose = new TextField();
	
	/** The bt file choose. */
	Button btFileChoose = new Button("Browse");

	/** The gr gen params. */
	// 2 - general parameters for the file
	GridPane grGenParams = new GridPane();
	
	/** The lb beg analyse. */
	Label lbBegAnalyse = new Label("Beginning of Analysis :");
	
	/** The tf beg analyse. */
	public static TextField tfBegAnalyse = new TextField();
	
	/** The lb end analyse. */
	Label lbEndAnalyse = new Label("End of Analysis :");
	
	/** The tf end analyse. */
	public static TextField tfEndAnalyse = new TextField();

	/** The lb per centile. */
	Label lbPerCentile = new Label("Percentile (0<per<100)  :");
	
	/** The tf per centile. */
	public static TextField tfPerCentile = new TextField("90");
	
	/** The lb step per cent. */
	Label lbStepPerCent = new Label("Agg Step Value (for percentile) :");
	
	/** The tf step per cent. */
	TextField tfStepPerCent = new TextField("1.0");

	/** The lb by column. */
	Label lbByColumn = new Label("Analyse by column Number(Start 0) :");
	
	/** The rb by column. */
	RadioButton rbByColumn = new RadioButton();
	
	/** The lb csv sep. */
	Label lbCsvSep = new Label("Csv separator ( ; by default) :");
	
	/** The tf csv sep. */
	public static TextField tfCsvSep = new TextField(";");

	/** The lb locale decimal. */
	Label lbLocaleDecimal = new Label("Decimal separator English => .) :");
	
	/** The rb locale decimal. */
	RadioButton rbLocaleDecimal = new RadioButton();
	
	/** The lb nb threads. */
	Label lbNbThreads = new Label("Threads Number( 0 => CPUs dependant) :");
	
	/** The tf nb threads. */
	TextField tfNbThreads = new TextField("0");
	
	/** The nb threads. */
	public static int nbThreads = 0;

	/** The gr extract params. */
	// 3 - Defining extracting parameters for the file
	GridPane grExtractParams = new GridPane();
	
	/** The lb piv regexp 1. */
	Label lbPivRegexp1 = new Label("First regexp Pivot :");
	
	/** The tf piv regexp 1. */
	TextField tfPivRegexp1 = new TextField();
	
	/** The lb piv regexp 2. */
	Label lbPivRegexp2 = new Label("Second regexp Pivot :");
	
	/** The tf piv regexp 2. */
	TextField tfPivRegexp2 = new TextField();
	
	/** The lb val regexp 1. */
	Label lbValRegexp1 = new Label("First regexp Value :");
	
	/** The tf val regexp 1. */
	static TextField tfValRegexp1 = new TextField();
	
	/** The lb val regexp 2. */
	Label lbValRegexp2 = new Label("Second regexp Value :");
	
	/** The tf val regexp 2. */
	static TextField tfValRegexp2 = new TextField();
	
	/** The lb val scale. */
	Label lbValScale = new Label("Scale for Value (10, 1, 0.1,0.001 ...) :");
	
	/** The tf val scale. */
	TextField tfValScale = new TextField("1.0");
	
	/** The lb top N. */
	Label lbTopN = new Label("Number of items for top N :");
	
	/** The tf top N. */
	TextField tfTopN = new TextField("20");

	/** The gr buttons. */
	// 4 - GridPane of buttons
	GridPane grButtons = new GridPane();
	
	/** The bt cancel. */
	Button btCancel = new Button("Cancel");
	
	/** The bt save as template. */
	Button btSaveAsTemplate = new Button("Save As Template");
	
	/** The bt analyse. */
	Button btAnalyse = new Button("Analyse");

	/** The txa logs. */
	public static TextArea txaLogs = new TextArea();
	
	/** The first alert. */
	public static boolean firstAlert = true;
	
	/** The deb parsing. */
	public static long debParsing = System.currentTimeMillis();
	
	/** The pat date. */
	public static Pattern patDate = null;
	
	/** The date begin. */
	public static Date dateBegin = null;
	
	/** The date end. */
	public static Date dateEnd = null;
	
	/** The top N. */
	public static int topN=20;

	/**
	 * Instantiates a new diag file stats.
	 *
	 * @param typeProps the type props
	 * @param fileTemplate the file template
	 */
	public DiagFileStats(String typeProps, File fileTemplate) {
		super();
		allHmCumul.clear();
		rbLocaleDecimal.setSelected(true);
		btSaveAsTemplate.setDisable(true);
		btAnalyse.setDisable(true);

		this.setTitle("Analyze a file");
		System.out.println("large : " + Integer.toString((int) (dimScreen.getWidth() / 4.0)));
		String boldStyle = "-fx-font-family:Arial; -fx-font-size:12px;-fx-font-weight:bold;";

		hbFileChoose.setPrefWidth(vboxTop.getWidth());

		this.initOwner(LogFouineurMain.primaryStage);
		// Styling
		lbFileChoose.setStyle(boldStyle);
		hbFileChoose.setSpacing(10);

		// Setting the margin to the nodes
		HBox.setMargin(lbFileChoose, new Insets(10, 0, 10, 20));
		lbFileChoose.setTextAlignment(TextAlignment.RIGHT);
		tfFileChoose.setStyle(boldStyle);

		tfFileChoose.setMinWidth((int) (dimScreen.getWidth() / 4.0));
		tfFileChoose.setPrefWidth((int) (dimScreen.getWidth() / 2.0));
		tfFileChoose.setMaxWidth((int) (dimScreen.getWidth() / 2.0));

		HBox.setMargin(tfFileChoose, new Insets(5, 20, 5, 0));
		HBox.setMargin(btFileChoose, new Insets(5, 20, 5, 60));
		btFileChoose.setStyle(boldStyle);

		tfBegAnalyse.setMinWidth((int) (dimScreen.getWidth() / 8.0));
		tfBegAnalyse.setPrefWidth((int) (dimScreen.getWidth() / 5.0));
		tfBegAnalyse.setMaxWidth((int) (dimScreen.getWidth() / 5.0));
		tfEndAnalyse.setMinWidth((int) (dimScreen.getWidth() / 8.0));
		tfEndAnalyse.setPrefWidth((int) (dimScreen.getWidth() / 5.0));
		tfEndAnalyse.setMaxWidth((int) (dimScreen.getWidth() / 5.0));

		lbBegAnalyse.setStyle(boldStyle);
		tfBegAnalyse.setStyle(boldStyle);
		lbEndAnalyse.setStyle(boldStyle);
		tfEndAnalyse.setStyle(boldStyle);
		GridPane.setHalignment(lbBegAnalyse, HPos.RIGHT);
		GridPane.setHalignment(lbEndAnalyse, HPos.RIGHT);
		GridPane.setMargin(tfBegAnalyse, new Insets(0, 40, 0, 0));

		hbFileChoose.getChildren().addAll(lbFileChoose, tfFileChoose, btFileChoose);

		grGenParams.add(lbBegAnalyse, 0, 0);
		grGenParams.add(tfBegAnalyse, 1, 0);
		grGenParams.add(lbEndAnalyse, 2, 0);
		grGenParams.add(tfEndAnalyse, 3, 0);

		tfStepPerCent.setMinWidth((int) (dimScreen.getWidth() / 12.0));
		tfStepPerCent.setPrefWidth((int) (dimScreen.getWidth() / 12.0));
		tfStepPerCent.setMaxWidth((int) (dimScreen.getWidth() / 12.0));
		tfPerCentile.setMinWidth((int) (dimScreen.getWidth() / 50.0));
		tfPerCentile.setPrefWidth((int) (dimScreen.getWidth() / 50.0));
		tfPerCentile.setMaxWidth((int) (dimScreen.getWidth() / 50.0));

		grGenParams.add(lbPerCentile, 0, 1);
		grGenParams.add(tfPerCentile, 1, 1);
		grGenParams.add(lbStepPerCent, 2, 1);
		grGenParams.add(tfStepPerCent, 3, 1);
		lbPerCentile.setStyle(boldStyle);
		tfPerCentile.setStyle(boldStyle);
		lbStepPerCent.setStyle(boldStyle);
		tfStepPerCent.setStyle(boldStyle);
		GridPane.setHalignment(lbPerCentile, HPos.RIGHT);
		GridPane.setHalignment(lbStepPerCent, HPos.RIGHT);
		GridPane.setMargin(tfPerCentile, new Insets(0, 40, 0, 0));

		tfCsvSep.setDisable(true);
		tfCsvSep.setMinWidth((int) (dimScreen.getWidth() / 50.0));
		tfCsvSep.setPrefWidth((int) (dimScreen.getWidth() / 50.0));
		tfCsvSep.setMaxWidth((int) (dimScreen.getWidth() / 50.0));
		grGenParams.add(lbByColumn, 0, 2);
		grGenParams.add(rbByColumn, 1, 2);
		grGenParams.add(lbCsvSep, 2, 2);
		grGenParams.add(tfCsvSep, 3, 2);
		lbByColumn.setStyle(boldStyle);
		lbCsvSep.setStyle(boldStyle);
		tfCsvSep.setStyle(boldStyle);
		GridPane.setHalignment(lbByColumn, HPos.RIGHT);
		GridPane.setHalignment(lbCsvSep, HPos.RIGHT);
		GridPane.setMargin(rbByColumn, new Insets(0, 40, 0, 0));

		tfNbThreads.setMinWidth((int) (dimScreen.getWidth() / 50.0));
		tfNbThreads.setPrefWidth((int) (dimScreen.getWidth() / 50.0));
		tfNbThreads.setMaxWidth((int) (dimScreen.getWidth() / 50.0));
		grGenParams.add(lbLocaleDecimal, 0, 3);
		grGenParams.add(rbLocaleDecimal, 1, 3);
		grGenParams.add(lbNbThreads, 2, 3);
		grGenParams.add(tfNbThreads, 3, 3);
		lbLocaleDecimal.setStyle(boldStyle);
		lbNbThreads.setStyle(boldStyle);
		tfNbThreads.setStyle(boldStyle);
		GridPane.setHalignment(lbLocaleDecimal, HPos.RIGHT);
		GridPane.setHalignment(lbNbThreads, HPos.RIGHT);
		GridPane.setMargin(rbLocaleDecimal, new Insets(0, 40, 0, 0));
		grGenParams.setHgap(5);
		grGenParams.setVgap(5);
		VBox.setMargin(grGenParams, new Insets(10, 30, 10, 20));

		lbPivRegexp1.setStyle(boldStyle);
		tfPivRegexp1.setStyle(boldStyle);
		lbPivRegexp2.setStyle(boldStyle);
		tfPivRegexp2.setStyle(boldStyle);
		lbValRegexp1.setStyle(boldStyle);
		tfValRegexp1.setStyle(boldStyle);
		lbValRegexp2.setStyle(boldStyle);
		tfValRegexp2.setStyle(boldStyle);

		tfPivRegexp1.setMinWidth((int) (dimScreen.getWidth() / 4.0));
		tfPivRegexp1.setPrefWidth((int) (dimScreen.getWidth() / 2.0));
		tfPivRegexp1.setMaxWidth((int) (dimScreen.getWidth() / 2.0));
		tfPivRegexp2.setMinWidth((int) (dimScreen.getWidth() / 4.0));
		tfPivRegexp2.setPrefWidth((int) (dimScreen.getWidth() / 2.0));
		tfPivRegexp2.setMaxWidth((int) (dimScreen.getWidth() / 2.0));

		tfValRegexp1.setMinWidth((int) (dimScreen.getWidth() / 4.0));
		tfValRegexp1.setPrefWidth((int) (dimScreen.getWidth() / 2.0));
		tfValRegexp1.setMaxWidth((int) (dimScreen.getWidth() / 2.0));
		tfValRegexp2.setMinWidth((int) (dimScreen.getWidth() / 4.0));
		tfValRegexp2.setPrefWidth((int) (dimScreen.getWidth() / 2.0));
		tfValRegexp2.setMaxWidth((int) (dimScreen.getWidth() / 2.0));

		grExtractParams.add(lbPivRegexp1, 0, 0);
		grExtractParams.add(tfPivRegexp1, 1, 0);
		grExtractParams.add(lbPivRegexp2, 0, 1);
		grExtractParams.add(tfPivRegexp2, 1, 1);

		grExtractParams.add(lbValRegexp1, 0, 2);
		grExtractParams.add(tfValRegexp1, 1, 2);
		grExtractParams.add(lbValRegexp2, 0, 3);
		grExtractParams.add(tfValRegexp2, 1, 3);

		tfValScale.setMinWidth((int) (dimScreen.getWidth() / 12.0));
		tfValScale.setPrefWidth((int) (dimScreen.getWidth() / 8.0));
		tfValScale.setMaxWidth((int) (dimScreen.getWidth() / 8.0));
		tfTopN.setMinWidth((int) (dimScreen.getWidth() / 40.0));
		tfTopN.setPrefWidth((int) (dimScreen.getWidth() / 40.0));
		tfTopN.setMaxWidth((int) (dimScreen.getWidth() / 40.0));

		lbValScale.setStyle(boldStyle);
		tfValScale.setStyle(boldStyle);
		lbTopN.setStyle(boldStyle);
		tfTopN.setStyle(boldStyle);

		grExtractParams.add(lbValScale, 0, 4);
		grExtractParams.add(tfValScale, 1, 4);
		grExtractParams.add(lbTopN, 0, 5);
		grExtractParams.add(tfTopN, 1, 5);
		grExtractParams.setHgap(5);
		grExtractParams.setVgap(5);
		grExtractParams.setPrefWidth(vboxTop.getWidth());
		VBox.setMargin(grExtractParams, new Insets(10, 30, 10, 20));

		grButtons.add(btCancel, 0, 0);
		grButtons.add(btSaveAsTemplate, 1, 0);
		grButtons.add(btAnalyse, 2, 0);
		btCancel.setStyle(boldStyle);
		btSaveAsTemplate.setStyle(boldStyle);
		btAnalyse.setStyle(boldStyle);
		GridPane.setHalignment(btCancel, HPos.CENTER);
		GridPane.setHalignment(btSaveAsTemplate, HPos.CENTER);
		GridPane.setHalignment(btAnalyse, HPos.CENTER);
		grButtons.setHgap(dimScreen.getWidth() * 3 / 20);
		grButtons.setVgap(5);
		grButtons.setMinWidth(this.getWidth());
		VBox.setMargin(grButtons, new Insets(10, 30, 10, dimScreen.getWidth() / 8));

		vboxTop.getChildren().addAll(hbFileChoose, grGenParams, grExtractParams, grButtons);

		vboxBottom.getChildren().addAll(txaLogs);
		VBox.setMargin(vboxTop, new Insets(30, 0, 0, 0));
		contentPane.getChildren().addAll(vboxTop, vboxBottom);

		scene = new javafx.scene.Scene(contentPane, dimScreen.getWidth() * 3 / 4, dimScreen.getHeight() * 3 / 4);
		this.setScene(scene);
		System.out.println("hauter textArea => " + (scene.getHeight() - vboxTop.getHeight()));
		vboxBottom.setMinHeight(scene.getHeight() - vboxTop.getHeight());
		txaLogs.setMinHeight(scene.getHeight() - vboxTop.getHeight());

		// Add Listeners
		this.btFileChoose.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();

			String strDir = LogFouineurMain.workspace + File.separator + LogFouineurMain.currentProject + File.separator
					+ LogFouineurMain.currentScenario + File.separator + "logs";

			if (!new File(strDir).exists()) {
				strDir = LogFouineurMain.workspace;
			} else {
				strDirConfig = strDir + File.separator + "config" + File.separator + "filestat";
			}
			fileChooser.setInitialDirectory(new File(strDir));

			File selectedFile = fileChooser.showOpenDialog(this);
			if (null != selectedFile) {
				this.btAnalyse.setDisable(false);
				this.btSaveAsTemplate.setDisable(false);
				this.tfFileChoose.setEditable(true);
				this.tfFileChoose.setText(selectedFile.getAbsolutePath());
				fileName = selectedFile.getName();
				prefixFileName = fileName.split("\\.")[0];
				this.tfFileChoose.setEditable(false);
				// Write 20 lines to txaLogs
				RandomAccessFile raf = null;
				StringBuilder strBuild = new StringBuilder("");
				try {

					raf = new RandomAccessFile(selectedFile, "r");
					int i = 0;
					String line = "";

					while (true) {
						try {
							line = raf.readLine();

							if (line.trim().length() > 4) {
								i++;
								strBuild.append(line).append(System.lineSeparator());
							}
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						if (i > 20 || line == null) {
							DiagFileStats.txaLogs.setText(strBuild.toString());
							DiagFileStats.extract = strBuild.toString();
							break;
						}
					}

				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} finally {
					try {
						raf.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

				fillTfDates(strBuild.toString());
				
				// gestion of type
				switch (typeProps) {
				case "Scratch" :
					if (strDirConfig.trim().length() > 0
							&& new File(strDirConfig + File.separator + prefixFileName + ".properties").exists()) {
						String fileConfig=strDirConfig + File.separator + prefixFileName + ".properties";
						loadProperties(fileConfig);
					}
					break;
				default:
					
					loadProperties(fileTemplate.getAbsolutePath());
					break;
				}
				
				

			}

		});
		rbByColumn.setOnAction(e -> {
			if (rbByColumn.isSelected()) {
				DiagFileStats.tfCsvSep.setDisable(false);
				this.lbPivRegexp1.setText("Num column of Pivot :");
				this.lbValRegexp1.setText("Num column of Value :");
			} else {

				this.lbPivRegexp1.setText("First regexp Pivot :");
				this.lbValRegexp1.setText("First regexp Value :");
				DiagFileStats.tfCsvSep.setDisable(true);
			}
		});
		this.btSaveAsTemplate.setOnAction(e -> {
			new TemplateSavingFileStats(this);
		});
		this.btAnalyse.setOnAction(e -> {
			analyseFile();
		});
		this.initModality(Modality.APPLICATION_MODAL);
		this.showAndWait();

	}

	/**
	 * Instantiates a new diag file stats.
	 */
	public DiagFileStats() {
		// From Scratch
		this("Scratch",null);
	}

	/**
	 * Analyse file.
	 */
	private final void analyseFile() {
		tabTreated=null;
		tabEliminated=null;
		tabFiltered=null;
		topN=Integer.valueOf(tfTopN.getText());
		if (this.tfPivRegexp1.getText().trim().length() == 0
				|| DiagFileStats.tfValRegexp1.getText().trim().length() == 0) {
			Alert alert = new Alert(AlertType.INFORMATION);

			alert.setResizable(true);

			alert.setTitle("Information Dialog");

			alert.setHeaderText("At least, First Regexp or Column for Pivot and Value must be filled");

			alert.showAndWait();
			return;
		}
		DiagFileStats.firstAlert = true;
		// System.out.println("DiagFileStats.extract=" + DiagFileStats.extract);
		// System.out.println("DiagFileStats.regexDate=" + DiagFileStats.regexDate);
		// System.out.println("DiagFileStats.patDate=" +
		// DiagFileStats.patDate.toString());
		// System.out.println("DiagFileStats.javaDateFormat=" +
		// DiagFileStats.javaDateFormat);

		if (null == DiagFileStats.sdf || null == tfBegAnalyse.getText()
				|| DiagFileStats.tfBegAnalyse.getText().trim().length() == 0) {
			// re init date
			System.out.println("DiagFileStats.extract=" + DiagFileStats.extract);
			fillTfDates(DiagFileStats.extract);

		}
		isParsing = true;
		System.out.println("Analyse before saving strDirConfig=" + strDirConfig);
		if (strDirConfig.trim().length() > 0 && new File(strDirConfig).exists()) {
			saveProperties(strDirConfig);
		}
		// launch 2 threads => 1 to count records read/treated
		// the other to launch the producer for disruptor;
		
		if (DiagFileStats.tfValRegexp1.getText().startsWith("plugin=")) {
			hasPlugins = true;
			if (DiagFileStats.tfValRegexp1.getText().split("=")[1].startsWith("Mono")) {
				// Mono Threads
				nbThreads = 1;
				DiagFileStats.this.tfNbThreads.setText("1");
			} else if (fileStatProps.getProperty("filestat.nbthreads").equals("0")) {

				nbThreads = Runtime.getRuntime().availableProcessors();
			}
			loadPlugins();
		}
		if (!fileStatProps.getProperty("filestat.nbthreads").equals("1") && canIUseMultiThread()) {
			if (fileStatProps.getProperty("filestat.nbthreads").equals("0")) {

				nbThreads = Runtime.getRuntime().availableProcessors();
				System.out.println("nbThreads=" + nbThreads);
			} else {
				nbThreads = Math.abs(Integer.parseInt(fileStatProps.getProperty("filestat.nbthreads", "1")));
			}
		} else {
			nbThreads = 1;
			fileStatProps.setProperty("filestat.nbthreads", "1");
			tfNbThreads.setText("1");

			saveProperties(strDirConfig);

		}
		tabTreated=new  int[nbThreads];
		tabEliminated=new  int[nbThreads];
		tabFiltered=new  int[nbThreads];
		for (int i=0;i <nbThreads;i++) {
			tabTreated[i]=0;
			tabEliminated[i]=0;
			tabFiltered[i]=0;
		}
		
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// TODO generate info advancing
				System.out.println("thread for disruptor");
				

				String strFile = tfFileChoose.getText();

				Task<Void> task = new Task<Void>() {
					@Override
					public Void call() throws Exception {

						Stream<String> stream = null;
						

						if (strFile.endsWith(".gz")) {
							stream = DiagFileStats.linesFromGzFile(new File(strFile).toPath());

						} else {

							stream = DiagFileStats.linesFromFile(new File(strFile).toPath());

						}

						parse(stream.map(str -> {
							FileStatEvent fse = new FileStatEvent();
							fse.setContent(str);
							return fse;
						}));

						return null;
					}

				};
				// File Properties OK
				task.setOnSucceeded(e -> {
					scene.getRoot().setDisable(false);
					scene.setCursor(Cursor.DEFAULT);
					isParsing = false;
					System.out.println("task.setOnSucceeded End Of Parsing");
					long duration = System.currentTimeMillis() - DiagFileStats.debParsing;
					String strDuration = "Duration :" + ((long) duration / 1000) + " sec " +((long) duration % 1000) + " millis";
					 DiagFileStats.txaLogs.setText(DiagFileStats.txaLogs.getText()+ "\n" + strDuration);
				//Generate the dailog with tableview
					new DiagStatsTableView();

				});
				scene.getRoot().setDisable(true);
				scene.setCursor(Cursor.WAIT);

				Thread t = new Thread(task);
				t.setDaemon(true);
				DiagFileStats.debParsing = System.currentTimeMillis();
				t.start();
			}
		});
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO generate info advancing

				long deb = System.currentTimeMillis();
				int treated = 0;
				int filtered = 0;
				int eliminated = 0;
				// verify that allThreadds have started
				
				// System.out.println("Avant whileboucle All Threads Not started");
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				while (null == DiagFileStats.tabTreated) {
					
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					
				}
				System.out.println("All Threads started");
				while (isParsing) {
					treated = 0;
					filtered = 0;
					eliminated = 0;
					boolean isNull = false;
					for (int i = 0; i < DiagFileStats.threads.length; i++) {
						if (null != DiagFileStats.threads[i]) {
							treated +=  DiagFileStats.tabTreated[i];
							filtered +=  DiagFileStats.tabFiltered[i];
							eliminated += DiagFileStats.tabEliminated[i];
						} else
							isNull = true;
					}
					if (!isNull) {
						scene.getRoot().setDisable(false);
						DiagFileStats.txaLogs.setText("filtered = " + filtered + " / eliminated = " + eliminated
								+ " / treated = " + treated + " / reads = " + DiagFileStats.reads);
						scene.getRoot().setDisable(true);
					}

					try {
						Thread.sleep(500);
						// to Allow Threads correctly instantiated
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				scene.getRoot().setDisable(false);
				long duration = System.currentTimeMillis() - deb;
				String strDuration = "Duration :" + ((long) duration / 1000) + " sec " + ((long) duration % 1000)
						+ " millis";

				DiagFileStats.txaLogs.setText("filtered = " + filtered + " / eliminated = " + eliminated
						+ " / treated = " + treated + " / reads = " + DiagFileStats.reads + "\n" + strDuration);

			}

		}).start();
	}

	/**
	 * Parses the.
	 *
	 * @param astream the astream
	 */
	private final void parse(Stream<FileStatEvent> astream) {
		// number of threads
	
		final ThreadFactory threadFactory = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				final ThreadFactory threadFactory = Executors.defaultThreadFactory();
				final Thread thread = threadFactory.newThread(r);
				thread.setDaemon(true);
				return thread;
			}
		};

		int bufferSize = Integer
				.parseInt(LogFouineurMain.jlProperties.getProperty("logFouineur.ringBufferSize", "1024"));

		System.out.println("Disruptor buffer size=" + bufferSize);

		Disruptor<FileStatEvent> disruptor = null;
		// Producer.SIMPLE is the best choice to read huge file of records

		if (LogFouineurMain.jlProperties.getProperty("logFouineur.waitStrategy", "blockingWaitSrategy")
				.equals("blockingWaitSrategy")) {
			// disruptor = new Disruptor<LogRecordEvent>(LogRecordEvent::new, bufferSize,
			// executor,
			// ProducerType.SINGLE, new BlockingWaitStrategy());
			disruptor = new Disruptor<FileStatEvent>(FileStatEvent::new, bufferSize, threadFactory, ProducerType.SINGLE,
					new BlockingWaitStrategy());

			System.out.println("JLP1logFouineur.waitStrategy=blockingWaitSrategy");
		} else if (LogFouineurMain.jlProperties.getProperty("logFouineur.waitStrategy", "blockingWaitSrategy")
				.equals("sleepingWaitStrategy")) {
			disruptor = new Disruptor<FileStatEvent>(FileStatEvent::new, bufferSize, threadFactory, ProducerType.SINGLE,
					new SleepingWaitStrategy());

			System.out.println("logFouineur.waitStrategy=sleepingWaitStrategy");
		} else if (LogFouineurMain.jlProperties.getProperty("logFouineur.waitStrategy", "blockingWaitSrategy")
				.equals("yieldingWaitStrategy")) {
			disruptor = new Disruptor<FileStatEvent>(FileStatEvent::new, bufferSize, threadFactory, ProducerType.SINGLE,
					new YieldingWaitStrategy());
			System.out.println("logFouineur.waitStrategy=yieldingWaitStrategy");
		} else if (LogFouineurMain.jlProperties.getProperty("logFouineur.waitStrategy", "blockingWaitSrategy")
				.equals("busySpinWaitStrategy")) {
			disruptor = new Disruptor<FileStatEvent>(FileStatEvent::new, bufferSize, threadFactory, ProducerType.SINGLE,
					new BusySpinWaitStrategy());
			System.out.println("logFouineur.waitStrategy=busySpinWaitStrategy");
		} else {
			disruptor = new Disruptor<FileStatEvent>(FileStatEvent::new, bufferSize, threadFactory, ProducerType.SINGLE,
					new BlockingWaitStrategy());
			System.out.println("logFouineur.waitStrategy=blockingWaitSrategy");
		}

		threads = new EventHandler[nbThreads];
		if (DiagFileStats.tfBegAnalyse.getText().toLowerCase().contains("nodate")) {
			for (int i = 0; i < nbThreads; i++) {

				threads[i] = new FileStatsLineHandler();
				((FileStatsLineHandler) threads[i]).setId(i);
				
				// init((RecordEventHandlerDatedRecord) threads[i]);

			}
		} else {
			for (int i = 0; i < nbThreads; i++) {

				threads[i] = new FileStatsLineHandlerDated();
				((FileStatsLineHandlerDated) threads[i]).setId(i);
				
				// init((RecordEventHandlerDatedRecord) threads[i]);

			}
		}
		disruptor.handleEventsWith(threads);

		// Start the Disruptor, starts all threads running
		// RingBuffer<FileStatEvent> ringBuffer = disruptor.start();

		FileStatsLineProducer producer = new FileStatsLineProducer(disruptor.start());

		reads = 0;
		long deb = System.currentTimeMillis();
		astream.forEach(event -> {
			// System.out.println("coucouIn debug="+debug);
			reads++;

			producer.onData(event);

		});

		int loop = 0;
		while (true) {
			int totTreated = 0;
			int totFiltered = 0;
			int totEliminated = 0;
			for (int i = 0; i < threads.length; i++) {
				totFiltered += tabFiltered[i];
				totTreated += tabTreated[i];
				totEliminated += tabEliminated[i];
			}
			loop++;

			try {

				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (totTreated == reads) {
				
				long duration = System.currentTimeMillis() - deb;
				String strDuration = "Duration :" + ((long) duration / 1000) + " secs " + ((long) duration % 1000)
						+ " millis";
				System.out.println("DiagFileStats loop => " + loop + " eliminated = " + totEliminated + " ;Filtered => "
						+ totFiltered + " ; treated =>" + totTreated + "/" + reads);
				scene.getRoot().setDisable(false);
				DiagFileStats.txaLogs.setText("filtered = " + totFiltered + " / eliminated = " + totEliminated
						+ " / treated = " + totTreated + " / reads = " + reads + "\n" + strDuration);
				scene.getRoot().setDisable(true);
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("totTreated == reads End Of Parsing");
				break;
			}
		}
		try {
			disruptor.shutdown(-1, java.util.concurrent.TimeUnit.MILLISECONDS);
		} catch (TimeoutException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (DiagFileStats.tfBegAnalyse.getText().toLowerCase().contains("nodate")) {
			// retrieve all hmap to put in DiagFileStats.allHmpap
			for (int i = 0; i < nbThreads; i++) {
				System.out.println("DiagFileStats.threads[" + i + "].hmLocalCumul.size="
						+ ((FileStatsLineHandler) threads[i]).hmLocalCumul.size());
			}
			// DiagFileStats.allHmCumul.putAll(((FileStatsLineHandler)
			// threads[0]).hmLocalCumul);
			// =((FileStatsLineHandler) threads[0]).hmLocalCumul;
			DiagFileStats.allHmCumul.clear();
			for (int i = 0; i < nbThreads; i++) {
				for (Entry<String, CumulEnregistrementStat> entry : ((FileStatsLineHandler) threads[i]).hmLocalCumul
						.entrySet()) {
					CumulEnregistrementStat tmpCumul = null;
					if (DiagFileStats.allHmCumul.containsKey(entry.getKey())) {
						tmpCumul = DiagFileStats.allHmCumul.get(entry.getKey());
						tmpCumul = tmpCumul.mergeEnr(entry.getValue(), ((FileStatsLineHandler) threads[i]).pas);
						DiagFileStats.allHmCumul.put(entry.getKey(), tmpCumul);
					} else {
						DiagFileStats.allHmCumul.put(entry.getKey(), entry.getValue());
					}
				}

			}

			System.out.println("DiagFileStats begin Close Enr");

			// Close all CumulEnregistrement
			for (Entry<String, CumulEnregistrementStat> entry : DiagFileStats.allHmCumul.entrySet()) {

				CumulEnregistrementStat tmpCumul = DiagFileStats.allHmCumul.get(entry.getKey());

				tmpCumul = tmpCumul.closeEnr(((FileStatsLineHandler) threads[0]).pas);

				DiagFileStats.allHmCumul.put(entry.getKey(), tmpCumul);

			}
		} else {

			// retrieve all hmap to put in DiagFileStats.allHmpap
			for (int i = 0; i < nbThreads; i++) {
				System.out.println("DiagFileStats.threads[" + i + "].hmLocalCumul.size="
						+ ((FileStatsLineHandlerDated) threads[i]).hmLocalCumul.size());
			}
			// DiagFileStats.allHmCumul.putAll(((FileStatsLineHandler)
			// threads[0]).hmLocalCumul);
			// =((FileStatsLineHandler) threads[0]).hmLocalCumul;
			DiagFileStats.allHmCumul.clear();
			for (int i = 0; i < nbThreads; i++) {
				for (Entry<String, CumulEnregistrementStat> entry : ((FileStatsLineHandlerDated) threads[i]).hmLocalCumul
						.entrySet()) {
					CumulEnregistrementStat tmpCumul = null;
					if (DiagFileStats.allHmCumul.containsKey(entry.getKey())) {
						tmpCumul = DiagFileStats.allHmCumul.get(entry.getKey());
						tmpCumul = tmpCumul.mergeEnr(entry.getValue(), ((FileStatsLineHandlerDated) threads[i]).pas);
						DiagFileStats.allHmCumul.put(entry.getKey(), tmpCumul);
					} else {
						DiagFileStats.allHmCumul.put(entry.getKey(), entry.getValue());
					}
				}

			}

			System.out.println("DiagFileStats begin Close Enr");

			// Close all CumulEnregistrement
			for (Entry<String, CumulEnregistrementStat> entry : DiagFileStats.allHmCumul.entrySet()) {

				CumulEnregistrementStat tmpCumul = DiagFileStats.allHmCumul.get(entry.getKey());

				tmpCumul = tmpCumul.closeEnr(((FileStatsLineHandlerDated) threads[0]).pas);

				DiagFileStats.allHmCumul.put(entry.getKey(), tmpCumul);

			}

		}

		for (int i = 0; i < nbThreads; i++) {
			threads[i] = null;
		}
		System.out.println("DiagFileStats.allHmCumul.size=" + DiagFileStats.allHmCumul.size());
		for (Entry<String, CumulEnregistrementStat> entry : allHmCumul.entrySet()) {
			System.out.println("DiagFileStats.allHmCumul key => " + entry.getKey());
		}
		
		

	}

	/**
	 * Can I use multi thread.
	 *
	 * @return true, if successful
	 */
	private final static boolean canIUseMultiThread() {
		// test with Implicit Date

		// test with plugin that starts with Mono

		if (tfValRegexp1.getText().trim().replaceAll("\\s", "").startsWith("plugin=Mono")) {
			return false;

		}

		return true;
	}

	/**
	 * Load plugins.
	 */
	@SuppressWarnings("unchecked")
	private final void loadPlugins() {
		if (new File(System.getProperty("root") + File.separator + "libs").exists()) {

			URL[] urls = new URL[3];
			try {
				urls[0] = new URL("file", "localhost", System.getProperty("root") + File.separator + "libs");
				urls[1] = new URL("file", "localhost",
						System.getProperty("root") + File.separator + "libs" + File.separator + "logfouineur.jar");
				if (new File(
						System.getProperty("root") + File.separator + "myPlugins" + File.separator + "plugins.jar")
								.exists()) {
					urls[2] = new URL("file", "localhost", System.getProperty("root") + File.separator + "myPlugins"
							+ File.separator + "plugins.jar");
				} else {
					urls[2] = urls[1];
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			urlClassLoader = new URLClassLoader(urls);

		}
		// Testing if regexp1 is a plugin
		hmapsClass = (HashMap<String, Object>[]) new HashMap[nbThreads];
		hmapsMethod = (HashMap<String, Method>[]) new HashMap[nbThreads];
		for (int j = 0; j < nbThreads; j++) {
			hmapsClass[j] = new HashMap<String, Object>();
			hmapsMethod[j] = new HashMap<String, Method>();
		}

		// on cree la classe et la methode
		for (int j = 0; j < nbThreads; j++) {

			String key = "plugins." + DiagFileStats.tfValRegexp1.getText().split("=")[1].trim();
			Object objUtil = null;

			String regex2tmp = "";
			if (null != DiagFileStats.tfValRegexp2.getText())
				regex2tmp = DiagFileStats.tfValRegexp2.getText();

			try {
				try {
					objUtil = Class.forName(key, true, urlClassLoader).getDeclaredConstructor().newInstance();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			hmapsClass[j].put(key, objUtil);
			try {
				// The method that return the value
				// The parameters in the array are array[0] the
				// record extracted from the log file
				// the others array[1]...array[n] are the result of
				// splitting the regex2 of the value,
				// the separator is given by the first character of
				// regex2
				hmapsMethod[j].put(key,
						hmapsClass[j].get(key).getClass().getDeclaredMethod("returnDouble", String.class));// The
																											// method
																											// that
																											// return
																											// the
																											// value
				Method metInit = hmapsClass[j].get(key).getClass().getDeclaredMethod("initialize", String.class);

				metInit.invoke(objUtil, new Object[] { regex2tmp }); // To intialize an instance or counters or
				// others static
				// structures
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * Load properties.
	 *
	 * @param strFileConfig the str file config
	 */
	private final void loadProperties(String strFileConfig) {
		fileStatProps.clear();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File(strFileConfig));
			fileStatProps.load(fis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// update TF
		DiagFileStats.tfBegAnalyse.setText(fileStatProps.getProperty("filestat.beganalysis", ""));
		DiagFileStats.tfEndAnalyse.setText(fileStatProps.getProperty("filestat.endanalysis", ""));
		tfPerCentile.setText(fileStatProps.getProperty("filestat.percentile", "90"));
		this.tfStepPerCent.setText(fileStatProps.getProperty("filestat.steppercent", "0.0"));
		if (fileStatProps.getProperty("filestat.bycolumn", "false").toLowerCase().equals("true")) {
			rbByColumn.setSelected(true);
			DiagFileStats.tfCsvSep.setEditable(true);
			DiagFileStats.tfCsvSep.setText(fileStatProps.getProperty("filestat.sepcolumn", ";"));
		} else {
			DiagFileStats.tfCsvSep.setEditable(false);
			rbByColumn.setSelected(false);
		}
		if (fileStatProps.getProperty("filestat.englishdecimal", "true").toLowerCase().equals("true")) {
			rbLocaleDecimal.setSelected(true);
		} else {
			rbLocaleDecimal.setSelected(false);
		}
		this.tfNbThreads.setText(fileStatProps.getProperty("filestat.nbthreads", "0"));
		nbThreads = Integer.parseInt(fileStatProps.getProperty("filestat.nbthreads", "0"));
		this.tfPivRegexp1.setText(fileStatProps.getProperty("filestat.pivregex1", ""));
		this.tfPivRegexp2.setText(fileStatProps.getProperty("filestat.pivregex2", ""));
		DiagFileStats.tfValRegexp1.setText(fileStatProps.getProperty("filestat.valregex1", ""));
		DiagFileStats.tfValRegexp2.setText(fileStatProps.getProperty("filestat.valregex2", ""));
		this.tfValScale.setText(fileStatProps.getProperty("filestat.scaleval", "1.0"));
		this.tfTopN.setText(fileStatProps.getProperty("filestat.topn", "20"));

	}

	/**
	 * Save properties.
	 *
	 * @param strDirConfig the str dir config
	 */
	private final void saveProperties(String strDirConfig) {
		fileStatProps.clear();

		fileStatProps.setProperty("filestat.pathfile", this.tfFileChoose.getText());
		fileStatProps.setProperty("filestat.beganalysis", DiagFileStats.tfBegAnalyse.getText());
		fileStatProps.setProperty("filestat.endanalysis", DiagFileStats.tfEndAnalyse.getText());
		fileStatProps.setProperty("filestat.percentile", tfPerCentile.getText());
		fileStatProps.setProperty("filestat.steppercent", this.tfStepPerCent.getText());
		if (this.rbByColumn.isSelected()) {
			fileStatProps.setProperty("filestat.bycolumn", "true");
			fileStatProps.setProperty("filestat.sepcolumn", DiagFileStats.tfCsvSep.getText());
		} else {
			fileStatProps.setProperty("filestat.bycolumn", "false");
			fileStatProps.setProperty("filestat.sepcolumn", "");
		}
		if (this.rbLocaleDecimal.isSelected()) {
			fileStatProps.setProperty("filestat.englishdecimal", "true");

		} else {
			fileStatProps.setProperty("filestat.englishdecimal", "false");

		}

		fileStatProps.setProperty("filestat.nbthreads", this.tfNbThreads.getText());
		nbThreads = Integer.parseInt(this.tfNbThreads.getText());
		fileStatProps.setProperty("filestat.pivregex1", this.tfPivRegexp1.getText());
		fileStatProps.setProperty("filestat.pivregex2", this.tfPivRegexp2.getText());
		fileStatProps.setProperty("filestat.valregex1", DiagFileStats.tfValRegexp1.getText());

		fileStatProps.setProperty("filestat.valregex2", DiagFileStats.tfValRegexp2.getText());
		fileStatProps.setProperty("filestat.scaleval", this.tfValScale.getText());
		fileStatProps.setProperty("filestat.topn", this.tfTopN.getText());
		fileStatProps.setProperty("filestat.isdatedfile", Boolean.toString(isDatedFile));
		fileStatProps.setProperty("filestat.filename", DiagFileStats.fileName);

		fileStatProps.setProperty("filestat.prefixfilename", this.prefixFileName);
		FileOutputStream fos = null;
		System.out.println("Saving properties in : " + strDirConfig + File.separator + prefixFileName + ".properties");
		try {
			fos = new FileOutputStream(new File(strDirConfig + File.separator + prefixFileName + ".properties"));
			fileStatProps.store(fos, "Configuration for file => " + fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * Fill tf dates.
	 *
	 * @param lines the lines
	 */
	private final void fillTfDates(String lines) {

		String[] regsDate = new String[2];
		String[] tablines = lines.split(System.lineSeparator());
		regsDate[0] = "";
		regsDate[1] = "";
		// println ( " detection format date avec la ligne :\n"+input)
		String regexLongestr = "";

		// println("getFormatDate input=" + input)
		HashMap<String, String> tabDateTimeRegexp = new HashMap<String, String>();
		Enumeration<Object> keys = LogFouineurMain.dateProperties.keys();
		// println("propsDate.size="+propsDate.size)
		// println("input="+input)
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = LogFouineurMain.dateProperties.getProperty(key);
			// if(!key.contains("format."))
			tabDateTimeRegexp.put(key, value);

		}
		// println("tabDateTimeRegexp=" + tabDateTimeRegexp)

		String formatDate = "";
		Set<String> kkeys = tabDateTimeRegexp.keySet();
		String kkeyLongest = "";
		for (String kkey : kkeys) {
			if (!kkey.contains("format.")) {
				Pattern regex = Pattern.compile(tabDateTimeRegexp.getOrDefault(kkey, ""));
				Matcher matcher = regex.matcher(tablines[1]);
				if (matcher.find()) {
					if (tabDateTimeRegexp.getOrDefault(kkey, "").length() > regexLongestr.length()) {
						regexLongestr = tabDateTimeRegexp.getOrDefault(kkey, "");
						kkeyLongest = kkey;
					}
				}
			}

		}
		String str = "";
		if (regexLongestr.length() > 4) {
			Pattern regexLong = Pattern.compile(regexLongestr);
			Matcher matcher = regexLong.matcher(tablines[1]);
			if (matcher.find()) {
				str = matcher.group();
				if (null != str && str.length() > 4 && (str.contains("Feb") || str.contains("Apr")
						|| str.contains("May") || str.contains("Jun") || str.contains("Jul") || str.contains("Aug"))) {
					java.util.Locale.setDefault(Locale.ENGLISH);
				} else {

					java.util.Locale.setDefault(Locale.FRENCH);
				}
			}
		}
		if (regexLongestr.length() > 4) {
			isDatedFile = true;
			// System.out.println("retour=(" + regexLongestr + ","
			// + tabDateTimeRegexp.getOrDefault("format." + kkeyLongest, "") + ")");
			// System.out.println("Extract => " + str);
			DiagFileStats.regexDate = regexLongestr;
			DiagFileStats.javaDateFormat = tabDateTimeRegexp.getOrDefault("format." + kkeyLongest, "");
			DiagFileStats.sdf = new SimpleDateFormat(javaDateFormat);
			DiagFileStats.tfBegAnalyse.setText(str);
			sdf = new SimpleDateFormat(tabDateTimeRegexp.getOrDefault("format." + kkeyLongest, ""));

			Date deb;
			try {
				deb = sdf.parse(str);
				// Set the End to a day after, but it can be modified as the beginning date too.
				Long fin = deb.getTime() + 24 * 3600 * 1000;
				DiagFileStats.tfEndAnalyse.setText(sdf.format(new Date(fin)));
				patDate = Pattern.compile(DiagFileStats.regexDate);
				dateBegin = deb;
				dateEnd = new Date(fin);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// (regexLongestr, tabDateTimeRegexp.getOrElse("format." + kkeyLongest, ""))

		} else {
			isDatedFile = false;
			// println("retour=(null,null)")
			// (null, null)
			DiagFileStats.tfBegAnalyse.setText("noDateFound");
			DiagFileStats.tfEndAnalyse.setText("noDateFound");
			DiagFileStats.regexDate = "";
			DiagFileStats.javaDateFormat = "";
			DiagFileStats.sdf = null;
			DiagFileStats.patDate = null;
		}

	}

	/**
	 * Close safely.
	 *
	 * @param closeable the closeable
	 */
	private final static void closeSafely(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				// Ignore
			}
		}
	}

	/**
	 * Lines from file.
	 *
	 * @param path the path
	 * @return the stream
	 */
	public final static Stream<String> linesFromFile(Path path) {
		InputStream fileIs = null;
		BufferedInputStream bufferedIs = null;

		try {
			fileIs = Files.newInputStream(path);
			// Even though GZIPInputStream has a buffer it reads individual
			// bytes
			// when processing the header, better add a buffer in-between
			int sizeBuffer = Integer
					.parseInt(LogFouineurMain.jlProperties.getProperty("logFouineur.byteBufferSize", "10485760"));
			System.out.println("sizeBuffer=" + sizeBuffer);
			bufferedIs = new BufferedInputStream(fileIs, sizeBuffer);

		} catch (IOException e) {

			closeSafely(bufferedIs);
			closeSafely(fileIs);
			throw new UncheckedIOException(e);
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(bufferedIs));
		return reader.lines().onClose(() -> closeSafely(reader));
	}

	/**
	 * Lines from gz file.
	 *
	 * @param path the path
	 * @return the stream
	 */
	public final static Stream<String> linesFromGzFile(Path path) {
		InputStream fileIs = null;
		BufferedInputStream bufferedIs = null;
		GZIPInputStream gzipIs = null;
		try {
			fileIs = Files.newInputStream(path);
			// Even though GZIPInputStream has a buffer it reads individual
			// bytes
			// when processing the header, better add a buffer in-between
			int sizeBuffer = Integer
					.parseInt(LogFouineurMain.jlProperties.getProperty("logFouineur.byteBufferSize", "10485760"));
			bufferedIs = new BufferedInputStream(fileIs, sizeBuffer);
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

}
