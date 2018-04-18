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
package org.jlp.logfouineur.parseview;

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
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import org.jlp.logfouineur.disruptor.LogRecordEvent;
import org.jlp.logfouineur.disruptor.ParsingMain;
import org.jlp.logfouineur.models.ParsingConfigHandler;
import org.jlp.logfouineur.records.ConfigRecord;
import org.jlp.logfouineur.records.RecordReader;
import org.jlp.logfouineur.ui.LogFouineurMain;
import org.jlp.logfouineur.ui.controller.MenuEventHandler;

import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

// TODO: Auto-generated Javadoc
/**
 * The Class ParseView.
 */
public class ParseView {

	/** The generated csv file. */
	public static String generatedCsvFile="";
	
	
	/** The cancel. */
	private static boolean cancel;
	
	/** The props popular. */
	Properties propsPopular = new Properties();
	
	/** The fit. */
	String fit = "";
	
	/** The zipped. */
	boolean zipped = false;
	
	/** The file log unzipped. */
	String fileLogUnzipped;
	
	/** The is fit. */
	boolean isFit = false;
	
	/** The local explicit date. */
	boolean localExplicitDate = true;

	/**
	 * Instantiates a new parses the view.
	 *
	 * @param selectedFile the selected file
	 */
	public ParseView(File selectedFile) {
		generatedCsvFile=null;
		String strProps = System.getProperty("root") + File.separator + "templates" + File.separator + "logparser"
				+ File.separator + "popular" + File.separator + "popular.properties";
		try (FileInputStream fis = new FileInputStream(strProps);) {

			propsPopular.load(fis);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fileLogUnzipped = selectedFile.getAbsolutePath();
		String filelog = selectedFile.getAbsolutePath();
		String[] listTemplates = propsPopular.getProperty("popular.list").split(";");
		if (filelog.endsWith(".gz")) {
			System.out.println("on dezippe");
			// decompacter le gz.
			zipped = true;
			int idx = selectedFile.getAbsolutePath().lastIndexOf(".gz");
			fileLogUnzipped = filelog.substring(0, idx);
			dzip(filelog);
		}

		while (!isFit) {
			for (String templ : listTemplates) {

				// var raf = new RandomAccessFile(new File(fileLog), "r")
				RandomAccessFile raf = null;
				try {
					raf = new RandomAccessFile(new File(fileLogUnzipped), "r");
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				String debEnr = propsPopular.getProperty("popular." + templ + ".debEnr", "");
				String finEnr = propsPopular.getProperty("popular." + templ + ".finEnr", "");
				boolean isDateExplicit = Boolean
						.parseBoolean(propsPopular.getProperty("popular." + templ + ".isDateExplicit"));
				String reg1 = propsPopular.getProperty("popular." + templ + ".reg1");
				String excl = propsPopular.getProperty("popular." + templ + ".excl", "");
				Pattern patExcl = null;
				Pattern patReg1 = null;
				Pattern patFinEnr = null;
				Pattern patDebEnr = null;

				int nbPoints = Integer.parseInt(propsPopular.getProperty("popular." + templ + ".nbPoints", "500"));
				Boolean boolExcl = excl.length() == 0 ? false : true;
				System.out.println(templ + " => boolExcl=" + boolExcl);
				// Initiliaze Patterns
				if (boolExcl) {
					patExcl = Pattern.compile(excl);
				}
				if (reg1.length() > 0) {
					patReg1 = Pattern.compile(reg1);
				}
				if (debEnr.length() > 0) {
					patDebEnr = Pattern.compile(debEnr);
				}
				if (finEnr.length() > 0) {
					patFinEnr = Pattern.compile(finEnr);
				}
				System.out.println ("finEnr="+finEnr);
				if (finEnr.trim().equals("")) {
					ConfigRecord.monoline=true;
					try {
						raf.seek(0);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// JLP TODO
					// cas monoligne
					System.out.println("traitement templ " + templ + " en Monoligne");

					// lecture de 10 lignes pour verif debut et reg1
					for (int i = 0; i < 100; i++) {
						String line = null;
						try {
							line = raf.readLine();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (null != line) {
							//System.out.println("line="+line);
							//System.out.println("debEn="+debEnr+ " ; reg1="+reg1);
							
							// println ("MonoLine line="+line)
							// println ("MonoLine debEnr="+debEnr)
							// println ("MonoLine reg1="+reg1)

							if (!boolExcl || (boolExcl && !patExcl.matcher(line).find())) {
								Matcher matchDebenr = patDebEnr.matcher(line);
								Matcher matchReg1 = patReg1.matcher(line);
								String ext1 = null;
								String ext2 = null;

								if ((matchDebenr.find()) && (matchReg1.find())) {
									isFit = true;
									//System.out.println("fit OK with line="+line);
								}
//								} else {
//									isFit = false	;
//								}
							}

						}
					}
					if (isFit) {
						fit = templ;
						localExplicitDate = isDateExplicit;
					}

					try {
						raf.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					// cas multiligne
					// creation de 1 enregistrement
					ConfigRecord.monoline=false;
					if(debEnr.equals(finEnr)) {
						ConfigRecord.beginEqualsEnd=true;
					}else
					{
						ConfigRecord.beginEqualsEnd=false;
					}
					System.out.println("traitement templ " + templ + " en multiligne");
					java.util.Properties properties = new Properties();
					System.out.println("avant de charger proprietes");
					System.out.println(System.getProperty("root") + File.separator + "templates" + File.separator
							+ "logparser" + File.separator + "popular" + File.separator + templ + ".properties");
					try {
						properties.load(new FileInputStream(new File(
								System.getProperty("root") + File.separator + "templates" + File.separator + "logparser"
										+ File.separator + "popular" + File.separator + templ + ".properties")));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("logFouineur.endRegex=" + properties.getProperty("logFouineur.endRegex", ""));
					if (properties.getProperty("logFouineur.endRegex", "") != "") {
						try {
							raf.seek(0);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						String enr = "";
						boolean bool = true;
						int i = 0;
						boolean debTrouve = false;
						boolean finTrouve = false;
						while (bool && i < 1000) {

							// trouver le debut
							String line = null;
							try {
								line = raf.readLine();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							if (null != line) {
								
								if (!boolExcl || (boolExcl && !patExcl.matcher(line).find())) {
									i += 1;
									String ext1 = null;
									Matcher matchDebEnr = patDebEnr.matcher(line);
									if (matchDebEnr.find()) {
										ext1 = matchDebEnr.group();
									}

									if (null != ext1 && !debTrouve) {

										enr = line.substring(line.indexOf(ext1) + ext1.length());

										debTrouve = true;
									} else if (debTrouve) {
										// chercher la fin

										String ext2 = null;
										Matcher matchFinEnr = patFinEnr.matcher(line);
										if (matchFinEnr.find()) {
											ext2 = matchFinEnr.group();
										}
										if (null != ext2) {
											bool = false;
											finTrouve = true;

											// enr = enr + " " + line.substring(line.indexOf(ext2.get) +
											// ext2.get.length)
										} else {
											enr = enr + " " + line;
										}
									}
									if (finTrouve)
										bool = false;
								}
							} else {
								bool = false;
							}
						}
						if (finTrouve && enr != "") {
							String ext3 = null;
							Matcher matchReg1 = patReg1.matcher(enr);
							if (matchReg1.find()) {
								ext3 = matchReg1.group();
							}
							if (null != ext3) {
								fit = templ;
								isFit = true;
								localExplicitDate = isDateExplicit;
							} else {
								System.out.println("pas trouve reg1 " + reg1 + " dans " + enr);
							}
						}
						try {
							raf.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				if (isFit)break;
			}
		}
		if (!fit.trim().equals("") ) {

			String nbPoints = propsPopular.getProperty("popular." + fit + ".nbPoints", "500");
			System.out.println("Le template correct de :" + filelog + " est :" + fit);
			// on va crrer le template a partir de ce fichier fit
			String popularTemplates = System.getProperty("root") + File.separator + "templates" + File.separator
					+ "logparser" + File.separator + "popular";
			Properties props = new java.util.Properties();
			FileInputStream fis=null;
			try {
				fis = new FileInputStream(
						new File(popularTemplates + File.separator + fit + ".properties"));
				props.load(fis);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// Fill ConfigRecord with the template
			try {
				new ParsingConfigHandler().fillConfigRecord(props);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String regExcl=props.getProperty("logFouineur.excludeRecord","").trim();
			String regInc=props.getProperty("logFouineur.includeRecord","").trim();
			if ( regExcl.equals("")) {
				ConfigRecord.regexExclude=null;
			}else
			{
				ConfigRecord.regexExclude=Pattern.compile(regExcl);
			}
			if ( regInc.equals("")) {
				ConfigRecord.regexInclude=null;
			}else
			{
				ConfigRecord.regexInclude=Pattern.compile(regInc);
			}
			ConfigRecord.boolExplicitDate=Boolean.parseBoolean(props.getProperty("logFouineur.explicitDate","true"));
			ConfigRecord.aggregPeriodInMillis=Integer.parseInt(props.getProperty("logFouineur.stepAgg","1000"));
			 ConfigRecord.gapRegex=Pattern.compile(props.getProperty("logFouineur.stepExtract"));
			
			LogFouineurMain.currentScenarioProps=props;
			ConfigRecord.boolGetDateWhenReading=Boolean.parseBoolean(props.getProperty("logFouineur.getDateWhenReading","true"));	
			LogFouineurMain.fileToParse=filelog;
			props.put("logFouineur.fileToParse", filelog);
			props.put("nbPoints", nbPoints);
			SimpleDateFormat dtf = new SimpleDateFormat("_yyyyMMdd_HHmmss");
			SimpleDateFormat dtfbis = new SimpleDateFormat("_yyMMdd_HHmmss");
			Calendar cal = Calendar.getInstance();

			String date = dtf.format(cal.getTime());
			String prefixFile = "";

			String fileOnly = filelog.substring(filelog.lastIndexOf(File.separator) + 1);
			prefixFile = fileOnly;
			if (prefixFile.contains(".")) {
				prefixFile = prefixFile.substring(0, prefixFile.indexOf("."));
			}

			String path = filelog.substring(0, filelog.lastIndexOf("logs" + File.separator)) + "csv" + File.separator
					+ prefixFile + date + File.separator;
			props.put("logFouineur.fileOut", path);
			props.put("logFouineur.showSummary", "true");
			//props.put("logFouineur.confOutputFile", "true");
			props.put("logFouineur.endDate", LogFouineurMain.scenariosProps
					.getProperty(LogFouineurMain.currentScenario + ".dateEnd", "3000/01/01 00:00:00"));
			props.put("logFouineur.startDate", LogFouineurMain.scenariosProps
					.getProperty(LogFouineurMain.currentScenario + ".dateBegin", "1970/01/01 00:00:00"));
			// val isExplicitDate = props.getProperty("fileIn.explicitDate").toBoolean
			fileOnly = filelog.substring(filelog.lastIndexOf(File.separator) + 1);

			if (!localExplicitDate) {

				// essayer de trouver la date dans le nom du fichier
				SimpleDateFormat dtf3 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				// props.put("fileIn.dateFormatIn", "yyyy/MM/dd HH:mm:ss")

				SimpleDateFormat dtf2 = new SimpleDateFormat("yyyyMMdd_HHmmss");
				SimpleDateFormat dtf2bis = new SimpleDateFormat("yyMMdd_HHmmss");
				Pattern reg = Pattern.compile("\\d{8}_\\d{6}");
				Pattern reg2 = Pattern.compile("\\d{6}_\\d{6}");
				String ext1 = null;
				String ext2 = null;

				Matcher matchReg = reg.matcher(fileOnly);
				Matcher matchReg2 = reg.matcher(fileOnly);
				if (matchReg.find()) {
					ext1 = matchReg.group();
				}

				if (matchReg2.find()) {
					ext2 = matchReg2.group();
				}

				if (null != ext1) {
					// Le nom du fichier porte la date au format reg
					Date date1=null;
					try {
						date1 = dtf2.parse(ext1);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					props.put("logFouineur.startDate", dtf3.format(date1));
					// On peut parser
					System.out.println ( "date de depart prise dans le nom de fichier : "+dtf3.format(date1));
					 ConfigRecord.originDate=date1;
					parser(props);
					// CsvClean.clean
				} else if (null != ext2) {
					// // Le nom du fichier porte la date au format reg2
					Date date1=null;
					try {
						date1 = dtf2bis.parse(ext2);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					props.put("logFouineur.startDate", dtf3.format(date1));
					System.out.println ( "date de depart prise dans le nom de fichier : "+dtf3.format(date1));
					 ConfigRecord.originDate=date1;
					parser(props);
					// CsvClean.clean
				} else if (props.getProperty("logFouineur.stepExtract", "").length() > 0
						
						&& props.getProperty("logFouineur.endRegex").trim().equals("")) {
					// On on ne peut avoir que la date de derniere modification pour tous les OS
					File file = new File(fileLogUnzipped);
					long lastModified = file.lastModified();

					String regDeb = props.getProperty("logFouineur.startRegex");
					// on lit toute les lignes du fichiers
					Pattern patRegDeb = Pattern.compile(regDeb);

					String line = "";
					String lastLine = "";
					long debRead = System.currentTimeMillis();
					
					debRead = System.currentTimeMillis();

					// method tail the best :
					// 50000 lines / 4.5 Mo => lastLine in 4 ms
					// tail(file,10) return the lastLine where length is > 10
					lastLine = tail(file, 10);

					String strdecal = props.getProperty("logFouineur.stepExtract");
					Pattern patDecal = Pattern.compile(strdecal);
					Matcher matchDecal = patDecal.matcher(lastLine);
					String decalExt = null;
					if (matchDecal.find()) {
						decalExt = matchDecal.group();
					}
					double mutMs = 1L;
					if (null != decalExt) {
						switch (props.getProperty("logFouineur.unitStep")) {
						case "ms":
							mutMs = 1L;
							break;
						case "s":
							mutMs = 1000L;
							break;
						case "seconds":
							mutMs = 1000L;
							break;
						case "secs":
							mutMs = 1000L;
							break;
						case "micros":
							mutMs = 0.001;
							break;
						case "mn":
							mutMs = 60000L;
							break;
						case "hour":
							mutMs = 60000L * 60;
							break;
						case "day":
							mutMs = 60000L * 60 * 24;
							break;
							
						case "month":
							mutMs = 600000 * 60 * 24 * 30;
							break;

						case "year":
							mutMs = 600000 * 60 * 24 * 30 * 365;
							break;
						default:
							mutMs = 1D;
							break;
						}

						long decal = (long) (Double.parseDouble(decalExt.replace(",", ".")) * mutMs);
						long dateCreation = lastModified - decal;
						Date newDate = new Date(dateCreation);
						props.put("fileIn.startDate", dtf3.format(newDate));
						 ConfigRecord.originDate=newDate;
						// On peut parser
						parser(props);
						// CsvClean.clean
					} else {
						// //JLP modif
						ParseView.cancel = false;
						;
						new DialogDirectParser("", props);
						// JOptionPane.showMessageDialog(null, "Rename file :" + fileOnly + " including
						// dateOfCreation with the format : yyyyMMdd_HHmmss")

						if (!ParseView.cancel) {
							parser(props);
							// CsvClean.clean
						}

					}
				} 
				else {
					
					Alert alert =new Alert(AlertType.ERROR);
					alert.setTitle("Rename file");
					alert.setContentText("You need to rename file with the Pattern :\n name_yyyyMMdd_HHmmss");
					alert.showAndWait();
				}

			} else {
				// on peut directement parser
				parser(props);
				// CsvClean.clean
			}

		} else

		{
			// JOptionPane.showMessageDialog(null, "Unknown file format , create a template
			// and add the config in the <swingScaViewer_Home>/templates/scaparser/popular
			// directory")
			Alert alert =new Alert(AlertType.ERROR);
			alert.setTitle("No template found file");
			alert.setContentText("Unknown file format , create a template\r\n" + 
					"			 and add the config in the <logfouineur_home>/templates/logparser/popular " + 
					"			 directory \n ");
			alert.showAndWait();

		}
	}

	/**
	 * Parser.
	 *
	 * @param props the props
	 */
	private void parser(java.util.Properties props) {
		LogFouineurMain.fos = null;
		LogFouineurMain.channel = null;
		LogFouineurMain.buf = null;
		LogFouineurMain.isTrace = false;
		LogFouineurMain.fosDebug = null;
		LogFouineurMain.channelDebug = null;
		LogFouineurMain.bufDebug = null;
		LogFouineurMain.isDebug = false;

		Task<Void> task = new Task<Void>() {
			@Override
			public Void call() throws Exception {

				Stream<String> stream = null;

				System.out.println("Parsing in ParseView :"+LogFouineurMain.fileToParse );
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
				System.out.println("Parsing in ParseView avant RecordReader");
				Stream<LogRecordEvent> streamLREnew = new RecordReader().read(stream);
				System.out.println("Parsing in ParseView avant ParsingMain");
				ParsingMain.parse(streamLREnew,true);
				System.out.println("Parsing in ParseView apres ParsingMain");
				return null;
			}
		};
		// File Properties OK
		task.setOnSucceeded(e -> {
			LogFouineurMain.scene.getRoot().setDisable(false);
			LogFouineurMain.scene.setCursor(Cursor.DEFAULT);
			System.out.println("Generated File => "+ generatedCsvFile);
			new MenuEventHandler().showCsvViewer(generatedCsvFile);
		});

		LogFouineurMain.scene.getRoot().setDisable(true);
		LogFouineurMain.scene.setCursor(Cursor.WAIT);

		Thread t = new Thread(task);
		t.setDaemon(true);
		t.start();
		// TODO
	}
	
	/**
	 * Lines from gz file.
	 *
	 * @param path the path
	 * @return the stream
	 */
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
	
	/**
	 * Close safely.
	 *
	 * @param closeable the closeable
	 */
	private static void closeSafely(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				// Ignore
			}
		}
	}
	
	/**
	 * Dzip.
	 *
	 * @param filelog the filelog
	 */
	private void dzip(String filelog) {
		if (zipped) {

			try (GZIPInputStream in = new GZIPInputStream(new FileInputStream(filelog));
					OutputStream out = new FileOutputStream(fileLogUnzipped);) {

				byte[] buf = new byte[1024 * 4];
				int len = 0;
				boolean bool = true;
				while (bool) {
					len = in.read(buf);
					if (len > 0) {
						out.write(buf, 0, len);
					} else
						bool = false;
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Tail.
	 *
	 * @param file the file
	 * @param minLengthLine the min length line
	 * @return the string
	 */
	private String tail(File file, int minLengthLine) {
		String lastLine = "";

		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(file, "r");
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		long lengthFile = 0;
		try {
			lengthFile = raf.length();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		long pointer = lengthFile - 1;

		boolean boolContinue = true;

		byte readByte = '0';
		int lengthLineCurrent = 0;
		while (boolContinue) {
			try {
				raf.seek(pointer);

				readByte = raf.readByte();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (readByte == 0xA || readByte == 0xD) {
				if (lengthLineCurrent > minLengthLine) {
					lengthLineCurrent = 0;

					boolContinue = false;
				} else {
					lengthLineCurrent += 1;
					pointer -= 1;
				}
			} else {
				lengthLineCurrent += 1;
				pointer -= 1;
			}
		}

		try {
			lastLine = raf.readLine();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			raf.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lastLine;
	}
}
