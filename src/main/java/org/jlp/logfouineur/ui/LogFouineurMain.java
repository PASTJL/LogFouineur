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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javafx.event.ActionEvent;

import java.io.UnsupportedEncodingException;

import org.jlp.logfouineur.filestat.ui.DiagFileStats;
import org.jlp.logfouineur.ui.controller.ActionEventHandler;
import org.jlp.logfouineur.ui.controller.MenuEventHandler;
import org.jlp.logfouineur.ui.controller.MouseEventHandler;

import javafx.application.Application;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

// TODO: Auto-generated Javadoc
/**
 * The Class LogFouineurMain.
 */
public class LogFouineurMain extends Application {
	
	/** The blank pane. */
	public static Pane blankPane=new Pane();
	/** The fos. */
	public static FileOutputStream fos =null;
	
	 /** The channel. */
 	public static WritableByteChannel channel = null;
	 
	/** The buf. */
	public static ByteBuffer buf = null;
	
	/** The waterline. */
	public static int waterline=1000;
	
	/** The fos debug. */
	public static FileOutputStream fosDebug =null;
	
	 /** The channel debug. */
 	public static WritableByteChannel channelDebug = null;
	 
	/** The buf debug. */
	public static ByteBuffer bufDebug = null;
	
	/** The waterline debug. */
	public static int waterlineDebug=1000;
	/** The menu bar. */
	// All Menu / MenuItems accessibles in static way
	public static MenuBar menuBar = new MenuBar();
	
	/** The jl properties. */
	public static Properties dateProperties = new Properties();
	
	/** The jl properties. */
	public static Properties jlProperties = new Properties();
	
	/** The scenarios props. */
	public static Properties scenariosProps = new Properties();
	
	/** The current scenario props. */
	public static Properties currentScenarioProps = new Properties();
	
	/** The workspace. */
	public static String workspace;
	
	/** The root. */
	public static String root;
	
	/** The current project. */
	public static String currentProject = "";
	
	/** The current scenario. */
	public static String currentScenario = "";
	
	/** The prefix scenario. */
	public static String prefixScenario = "scn_";
	
	/** The file to parse. */
	public static String fileToParse = "";
	
	/** The parse gen template. */
	public static String parseGenTemplate = ""; // full path to parseGenTemplate
												
												/** The parse loc template. */
												// if any
	public static String parseLocTemplate = "";// full path to
												// parseLocalTemplate if any

	/** The error format date. */
	public static String errorFormatDate="";
	
	/** The error format value. */
	public static String errorFormatValue="";
	/** The csv prefix. */
		public static String csvPrefix = "";
	
	/** The path to scenario. */
	public static String pathToScenario = "";
	
	/** The file to parse basic. */
	public static String fileToParseBasic = "";

	/**
	 * Debug.
	 *
	 * @param line the line
	 */
	public static void debug(String line) {


		try {
			LogFouineurMain.bufDebug.put((line + System.lineSeparator()).getBytes("ISO-8859-1"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			
			if (LogFouineurMain.bufDebug.position() > LogFouineurMain.waterlineDebug) {
				int nb=0;
				while ( (nb=LogFouineurMain.channelDebug.write(LogFouineurMain.buf.flip())) >0 ) {
			
					
					System.out.println("Debugb writing bytes =" + nb);
					
				
					LogFouineurMain.fosDebug.flush();
					
					LogFouineurMain.bufDebug.compact();

			}

			

					LogFouineurMain.bufDebug.clear();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	static {
		workspace = System.getProperty("workspace"); //$NON-NLS-1$
		root = System.getProperty("root"); //$NON-NLS-1$
		String strConf = root + File.separator + "config" + File.separator + "logFouineur.properties"; //$NON-NLS-1$ //$NON-NLS-2$
		String strDateProps=root + File.separator + "config" + File.separator + "logFouineurDates.properties";
		try {
			jlProperties.load(Files.newInputStream(new File(strConf).toPath()));
			dateProperties.load(Files.newInputStream(new File(strDateProps).toPath()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String locale = jlProperties.getProperty("logFouineur.locale", "EN");
		prefixScenario = jlProperties.getProperty("logFouineur.prefixscenario", "scn_");
		if (locale.equalsIgnoreCase("FR"))
			java.util.Locale.setDefault(Locale.FRENCH);
		else
			java.util.Locale.setDefault(Locale.ENGLISH);
	}

	/** The m file. */
	public static Menu mFile = new Menu(Messages.getString("LogFouineurMain.0")); //$NON-NLS-1$
	
	/** The image. */
	public static Image image = null;
	
	/** The img view. */
	public static ImageView imgView = null;

	static {
		image = new Image("/images/ImageMain.png");
		imgView = new ImageView();
		imgView.setImage(image);
	}
	
	/** The m file stats. */
	public static Menu mFileStats=   new Menu("FileStats"); 
	
	/** The mi from scratch. */
	public static MenuItem miFromScratch= new MenuItem("From scratch");
	
	/** The m from loc template. */
	public static Menu mFromLocTemplate= new Menu("From project template");
	
	/** The m from gen template. */
	public static Menu mFromGenTemplate= new Menu("From general template");
	
	/** The Tools Menu. */
	public static Menu mTools = new Menu("Tools"); 
	
	/** The Menu item Test Regex . */
	public static MenuItem miTestRegex = new MenuItem("Test regex"); 
	
	/** The Menu item date To Millis  . */
	public static MenuItem miDateToMillis = new MenuItem("Date<->millis"); 
	
	/** The mi concat files. */
	public static MenuItem miConcatFiles = new MenuItem("Concat Files"); 
	
	/** The mi dec hexa. */
	public static MenuItem miDecHexa = new MenuItem("Dec <=> Hexa"); 
	/** The mi new project. */
	// Menu File
	public static MenuItem miNewProject = new MenuItem(Messages.getString("LogFouineurMain.1")); //$NON-NLS-1$
	
	/** The mi open project. */
	public static MenuItem miOpenProject = new MenuItem(Messages.getString("LogFouineurMain.2")); //$NON-NLS-1$
	
	/** The mi scenario. */
	public static MenuItem miScenario = new MenuItem("Change/Create Scenario");
	
	/** The mi close project. */
	public static MenuItem miCloseProject = new MenuItem(Messages.getString("LogFouineurMain.3")); //$NON-NLS-1$

	/** The mi exit. */
	public static MenuItem miExit = new MenuItem(Messages.getString("LogFouineurMain.4")); //$NON-NLS-1$
	
	/** The mhe. */
	public static MenuEventHandler mhe = new MenuEventHandler();

	/** The mab parse logs. */
	// MenuAsButton for parsing logs
	public static MyMenuAsButton mabParseLogs = new MyMenuAsButton("ParseLogs");

	/** The m CSV chart. */
	// Menu for CSV Charting
	public static Menu mCSVChart = new Menu("CSV Charts");
	
	/** The mi CSV view. */
	public static MenuItem miCSVView = new MenuItem("CSV View");
	
	/** The mi parse and view. */
	public static MenuItem miParseAndView = new MenuItem("Parse & View");
	// logFouineur properties

	/** The prefixscenario. */
	public static String prefixscenario = "scn_"; //$NON-NLS-1$

	/** The primary stage. */
	public static Stage primaryStage = null;

	/** The bp root. */
	public static BorderPane bpRoot ;
	
	/** The scene. */
	public static Scene scene=null;
	
	/** The dim screen. */
	public static Dimension dimScreen = Toolkit.getDefaultToolkit().getScreenSize();

	/** The is trace. */
	public static boolean isTrace=false;
	
	/** The is debug. */
	public static boolean isDebug=false;
	
	/**
	 * Start.
	 *
	 * @param primaryStage the primary stage
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws IOException {
		bpRoot = new BorderPane();
		prefixscenario = jlProperties.getProperty("logFouineur.prefixscenario"); //$NON-NLS-1$

		// Define the root layout pane
		primaryStage.getIcons().add(new Image("/images/lflogo.png")); //$NON-NLS-1$

		primaryStage.setTitle("LogFouineur V 1.0: No project selected"); //$NON-NLS-1$
		LogFouineurMain.primaryStage = primaryStage;
		mFileStats.getItems().addAll(miFromScratch,mFromGenTemplate,mFromLocTemplate);
		mTools.getItems().addAll(miDateToMillis,miTestRegex,miConcatFiles,miDecHexa);

		VBox topContainer = new VBox();

		// Disabling miCloseProject
		miCloseProject.setDisable(true);

		mFile.getItems().addAll(miNewProject, miOpenProject, miScenario, miCloseProject, miExit);
		mCSVChart.getItems().addAll(miCSVView,miParseAndView);
		mabParseLogs.setStyle("-fx-background-color : transparent;");
		menuBar.getMenus().addAll(mFile, mabParseLogs,this.mFileStats,mCSVChart,mTools);
		
		menuBar.setId("menubar"); //$NON-NLS-1$
		// menuBar.setStyle("-fx-background-color: #ff0000;");
		topContainer.getChildren().add(menuBar);
		bpRoot.setTop(topContainer);

		// adding the button ParserLogs to Menu Bar
		System.out.println("Locale="+java.util.Locale.getDefault());

		mabParseLogs.myButton.setOnAction(new ActionEventHandler(primaryStage));

		// adding handler to the Menus Items of File
		miNewProject.setId("newProject"); //$NON-NLS-1$
		miNewProject.addEventHandler(ActionEvent.ACTION, mhe);
		miOpenProject.setId("openProject"); //$NON-NLS-1$
		miOpenProject.addEventHandler(ActionEvent.ACTION, mhe);
		miScenario.setId("scenario");
		miScenario.addEventHandler(ActionEvent.ACTION, mhe);
		miCloseProject.setId("closeProject"); //$NON-NLS-1$
		miCloseProject.addEventHandler(ActionEvent.ACTION, mhe);
		miExit.setId("exit"); //$NON-NLS-1$
		miExit.addEventHandler(ActionEvent.ACTION, mhe);
		miCSVView.addEventHandler(ActionEvent.ACTION, mhe);
		miCSVView.setId("CSVView");
		miParseAndView.addEventHandler(ActionEvent.ACTION, mhe);
		miParseAndView.setId("ParseAndView");
		
		miFromScratch.setId("fromScratch");
		miFromScratch.addEventHandler(ActionEvent.ACTION, mhe);
		addSubMenusGenTemplate();
		// adding the Image/ Logo on Center
		this.miDateToMillis.setId("dateInMillis");
		
		miTestRegex.setId("testRegex");
		
		miDateToMillis.addEventHandler(ActionEvent.ACTION, mhe);
		
		this.miTestRegex.addEventHandler(ActionEvent.ACTION, mhe);
		miConcatFiles.setId("concatFiles");
		miConcatFiles.addEventHandler(ActionEvent.ACTION, mhe);
		miDecHexa.setId("decHexa");
		miDecHexa.addEventHandler(ActionEvent.ACTION, mhe);
		imgView.setSmooth(true);
		imgView.setCache(true);

		bpRoot.setCenter(imgView);

		handleMenuState(true);
		scene = new Scene(bpRoot, dimScreen.getWidth(), dimScreen.getHeight(),Color.LIGHTBLUE);
		scene.getStylesheets().add(this.getClass().getResource("/logfouineur.css").toExternalForm()); //$NON-NLS-1$
		primaryStage.setScene(scene);

		primaryStage.show();

	}

	/**
	 * Adds the sub menus gen template.
	 */
	private void addSubMenusGenTemplate() {
		// TODO Auto-generated method stub
		String strgenTemplateString=this.root+File.separator+"templates"+File.separator+"filestat";
		
			
			File[] lstF=new File(strgenTemplateString).listFiles();
			MenuItem[] tabMi=new MenuItem[lstF.length];
			int i=0;
			for(File file:lstF) {
				String prefix=file.getName().split("\\.properties")[0];
				tabMi[i]=new MenuItem(prefix);
				tabMi[i].setStyle("-fx-text-fill : black;");
				tabMi[i].setOnAction(e -> {
					System.out.println("loading from General Template : "+file.getAbsolutePath());
					new DiagFileStats("Gen",file);
				});
				i++;
				
			}
			for ( i=0; i< tabMi.length;i++) {
				LogFouineurMain.mFromGenTemplate.getItems().add(tabMi[i]);
			}
		
		
	}

	

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Handle menu state.
	 *
	 * @param activate the activate
	 */
	public static void handleMenuState(boolean activate) {

		miNewProject.setDisable(!activate);
		miOpenProject.setDisable(!activate);
		miCloseProject.setDisable(activate);
		miScenario.setDisable(activate);
		mabParseLogs.setDisable(activate);
		mFileStats.setDisable(activate);
		mCSVChart.setDisable(activate);

	}
}
