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

import java.util.List;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.jlp.logfouineur.ui.LogFouineurMain;

import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.scene.control.Button;

// TODO: Auto-generated Javadoc
/**
 * The Class MyDialogDateInMillis.
 */
public class MyDialogConcatFile {
	
	/** The with zip. */
	public boolean withZip = true;
	
	/** The br. */
	BufferedReader br;
	
	/** The gzip in. */
	GZIPInputStream gzipIn;
	
	/** The gzip out. */
	GZIPOutputStream gzipOut;
	
	/** The fis. */
	FileInputStream fis;
	
	/** The fos. */
	FileOutputStream fos;
	
	/** The bw. */
	OutputStreamWriter bw;

	/**
	 * Instantiates a new my dialog date in millis.
	 */
	public MyDialogConcatFile() {
		FileChooser fc = new FileChooser();
	
		fc.setTitle("Concatening Files");
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("csv Files", "*.csv", "*.csv.gz"));
		fc.getExtensionFilters()
				.add(new FileChooser.ExtensionFilter("log Files", "*.log", "*.log.gz", "*.txt", "*.txt.gz"));
		
		fc.setInitialDirectory(new File(LogFouineurMain.workspace));

		List<File> selectedFiles = fc.showOpenMultipleDialog(LogFouineurMain.primaryStage);
		String nameConcat ="";
		String nameFileResult="";
		if (selectedFiles != null) {
			// Choix zippe ou non
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("gzip file ?");
			alert.setResizable(true);
			((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("No zipped");
			alert.getDialogPane().setMinHeight(300);
			alert.getDialogPane().setMinWidth(400);
			alert.setContentText("Concatening the files into a gzip (.gz); file ?");
			alert.showAndWait().ifPresent(response -> {
				if (response == ButtonType.OK) {
					withZip = true;
					System.out.println("ZippedMode");
				} else {
					withZip = false;
				}
			});
			LogFouineurMain.scene.setCursor(Cursor.WAIT);
			String dirConcat = selectedFiles.get(0).getAbsolutePath().substring(0,
					selectedFiles.get(0).getAbsolutePath().lastIndexOf(File.separator) + 1);
			String nameFirstFile = selectedFiles.get(0).getName();
			nameConcat = "concat_" + nameFirstFile;
			System.out.println("dirConcat=" + dirConcat);
			System.out.println("nameFirstFile=" + nameFirstFile);
			System.out.println("nameConcat=" + nameConcat);
			switch (Boolean.toString(withZip)) {
			case "true":
				if (!nameConcat.endsWith(".gz")) {
					nameConcat += ".gz";
				}
				break;
			case "false":
				if (nameConcat.endsWith(".gz")) {
					nameConcat = nameConcat.substring(0, nameConcat.lastIndexOf("."));
				}

				break;
			}
			nameFileResult = dirConcat + File.separator + nameConcat;
			if (new File(nameFileResult).exists())new File(nameFileResult).delete();
			if (withZip) {
				try {
					gzipOut = new GZIPOutputStream(new FileOutputStream(nameFileResult));
					bw = new OutputStreamWriter(gzipOut);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				try {
					bw = new OutputStreamWriter(new FileOutputStream(nameFileResult));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			for (File file : selectedFiles) {
				if (file.getAbsolutePath().endsWith(".gz")) {
					try {
						gzipIn = new GZIPInputStream(new FileInputStream(file));
						br = new BufferedReader(new InputStreamReader(gzipIn));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					br = new BufferedReader(new InputStreamReader(gzipIn));

				} else {
					try {
						br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				String lineRead = null;
				try {
					while (null != (lineRead = br.readLine())) {
						bw.write(lineRead + "\n");

					}
					bw.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				bw.flush();
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		LogFouineurMain.scene.setCursor(Cursor.DEFAULT);
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("gzip file ?");
		alert.setResizable(true);
		alert.getDialogPane().setMinHeight(400);
		alert.getDialogPane().setMinWidth(500);
		alert.setContentText("Concatening the files into : \n"+nameFileResult + "\n is terminated");
		alert.showAndWait();
	}

	

}
