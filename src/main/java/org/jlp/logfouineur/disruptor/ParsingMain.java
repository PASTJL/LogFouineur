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
package org.jlp.logfouineur.disruptor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Stream;

import org.jlp.javafx.richview.CompositePanel;
import org.jlp.logfouineur.filestat.ui.DiagFileStats;
import org.jlp.logfouineur.models.AggLogRecordEvent;
import org.jlp.logfouineur.models.CloseRecordHandler;
import org.jlp.logfouineur.models.JFXPivot;
import org.jlp.logfouineur.models.JFXValue;
import org.jlp.logfouineur.models.ParsingConfigHandler;
import org.jlp.logfouineur.records.ConfigRecord;
import org.jlp.logfouineur.records.RecordReader;
import org.jlp.logfouineur.ui.LogFouineurFill;
import org.jlp.logfouineur.ui.LogFouineurMain;
import org.jlp.logfouineur.ui.controller.MenuEventHandler;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

// TODO: Auto-generated Javadoc
/**
 * The Class ParsingMain.
 */
public class ParsingMain {

	/** The tab treated. */
	public static int[] tabTreated = null;
	
	/** The tab eliminated. */
	public static int[] tabEliminated = null;
	
	/** The tab filtered. */
	public static int[] tabFiltered = null;

	/**
	 * Handle event.
	 *
	 * @param event
	 *            the event
	 * @param sequence
	 *            the sequence
	 * @param endOfBatch
	 *            the end of batch
	 */
	public static void handleEvent(LogRecordEvent event, long sequence, boolean endOfBatch) {
		System.out.println("Thread =" + Thread.currentThread().getName() + " ; line =" + event.getContent());
	}

	/** The min date of log file. */
	public static long minDateOfLogFile = Long.MAX_VALUE;

	/** The max date of log file. */
	public static long maxDateOfLogFile = 0;

	/** The values. */
	// All static structures for parsing
	public static JFXValue[] values;

	/** The pivots. */
	public static JFXPivot[] pivots;// not including "global"

	/** The value regex. */
	public static int[] valueRegex; // 0 means plugin, 1 or 2

	/** The pivot regex. */
	public static int[] pivotRegex; // 1 or 2

	/** The nb vals. */
	public static int nbVals = 1;

	/** The nb pivots. */
	public static int nbPivots = 0; // not including "global"

	/** The nb threads. */
	public static int nbThreads = 1;

	/** The is durations. */
	public static boolean[] isDurations;

	/** The threads. */
	public static EventHandler<LogRecordEvent>[] threads = null;

	/** The factor value ms. */
	public static double factorValueMs[];

	/** The hm duration. */
	public static HashMap<Integer, String> hmDuration = new HashMap<Integer, String>(1); // a
																							// tuple
																							// as
	/** The rank value duration. */
	// HashMap
	public static int rankValueDuration = -1;

	/** The has duration. */
	public static boolean hasDuration = false;

	/** The debug. */
	public static int reads = 1;

	/** The all hm values. */
	// Structure HashMap to receive the values
	public static HashMap<Long, HashMap<String, AggLogRecordEvent>> allHmValues[];

	/** The tree map. */
	public static TreeMap<Long, HashMap<String, AggLogRecordEvent>> treeMap;

	/** The dbl values. */
	public static HashMap<Long, HashMap<String, Double>> dblValues;

	/** The hm summary. */
	public static HashMap<String, HashMap<Long, Double[]>> hmSummary;

	/** The tm summary. */
	public static TreeMap<Long, HashMap<String, Double[]>> tmSummary = new TreeMap<Long, HashMap<String, Double[]>>();

	/** The factor to millis. */
	public static double factorToMillis = 1;

	/** The summary. */
	public static String summary = "Avg";

	/** The accu for implicit date. */
	public static long accuForImplicitDate = 0L;
	// all staff is done in a single Thread, so no conflict / error by acceding
	/** The step in millis. */
	// accuForImplicitDate
	public static long stepInMillis = 0L;

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
	
	/** The is parsing. */
	private static boolean isParsing;

	/**
	 * Parses the.
	 *
	 * @param stream the stream
	 */
	public static final void parse(Stream<LogRecordEvent> stream) {
		parse(stream, false);
	}

	/**
	 * Parses the.
	 *
	 * @param stream            the stream
	 * @param fromParseView the from parse view
	 */
	@SuppressWarnings("unchecked")
	public static final void parse(Stream<LogRecordEvent> stream, boolean fromParseView) {
		// Reinit all structures
		values = null;
		pivots = null;
		reads = 0;
		tmSummary.clear();
		dblValues = null;
		hmSummary = null;
		allHmValues = null;
		accuForImplicitDate = 0L;
		hmapsClass = null;
		hmapsMethod = null;
		hasPlugins = false;
		urlClassLoader = null;
		minDateOfLogFile = Long.MAX_VALUE;
		maxDateOfLogFile = 0;
		hmDuration.clear();
		treeMap = null;
		threads = null;
		isDurations = null;

		int lastSeparator = LogFouineurMain.fileToParse.lastIndexOf(File.separator);
		String pathRoot = LogFouineurMain.fileToParse.substring(0, lastSeparator + 1);

		String nameFileBasic = LogFouineurMain.fileToParse.substring(lastSeparator + 1);
		if (nameFileBasic.endsWith(".gz")) {
			int idxTmp = nameFileBasic.indexOf(".gz");
			nameFileBasic = nameFileBasic.substring(0, idxTmp);
		}
		String strConfigPropertiesFile = pathRoot + "config" + File.separator + "parselog" + File.separator
				+ nameFileBasic + ".properties";

		if (null != stream) {
			int factorHyperThreading = Integer
					.parseInt(LogFouineurMain.jlProperties.getProperty("logFouineur.factorHyperthreading", "1"));
			// prepare arrays Values/pivots
			nbVals = fillAndGetNbVals();
			nbPivots = fillPivots();
			// For ImplicitDate Fill static variable on static way
			if (!ConfigRecord.boolExplicitDate) {
				// if (ConfigRecord.boolImplicitStep) {

				switch (LogFouineurMain.currentScenarioProps.getProperty("logFouineur.implicitStep", "true")) {
				case "true":

					switch (LogFouineurMain.currentScenarioProps.getProperty("logFouineur.unitStep")) {
					case "ms":
						factorToMillis = 1;
						break;
					case "micros":
						factorToMillis = 0.001;
						break;
					case "s":
						factorToMillis = 1000;
						break;
					case "mn":
						factorToMillis = 600000;
						break;

					case "hour":
						factorToMillis = 600000 * 60;
						break;
					case "day":
						factorToMillis = 600000 * 60 * 24;
						break;

					case "month":
						factorToMillis = 600000 * 60 * 24 * 30;
						break;

					case "year":
						factorToMillis = 600000 * 60 * 24 * 30 * 365;
						break;
					}
					stepInMillis = (long) (factorToMillis * ConfigRecord.step2Records);
					break;
				case "false":

					switch (LogFouineurMain.currentScenarioProps.getProperty("logFouineur.unitStep")) {
					case "ms":
						factorToMillis = 1;
						break;
					case "micros":
						factorToMillis = 0.001;
						break;
					case "s":
						factorToMillis = 1000;
						break;
					case "mn":
						factorToMillis = 600000;
						break;

					case "hour":
						factorToMillis = 600000 * 60;
						break;
					case "day":
						factorToMillis = 600000 * 60 * 24;
						break;

					case "month":
						factorToMillis = 600000 * 60 * 24 * 30;
						break;

					case "year":
						factorToMillis = 600000 * 60 * 24 * 30 * 365;
						break;
					}

				}

			}

			// number of threads
			if (!LogFouineurMain.currentScenarioProps.getProperty("logFouineur.threads").equals("1")
					&& canIUseMultiThread()) {
				if (LogFouineurMain.currentScenarioProps.getProperty("logFouineur.threads").equals("0")) {

					nbThreads = factorHyperThreading * Runtime.getRuntime().availableProcessors();
					System.out.println("nbThreads=" + nbThreads);
				} else {
					nbThreads = Math.abs(Integer
							.parseInt(LogFouineurMain.currentScenarioProps.getProperty("logFouineur.threads", "1")));
				}
			} else {
				nbThreads = 1;
				LogFouineurMain.currentScenarioProps.setProperty("logFouineur.threads", "1");
				if (!fromParseView) {
					LogFouineurFill.tfThreads.setText("1");

					new ParsingConfigHandler(LogFouineurMain.fileToParse).save();
				}

			}
			// Executor that will be used to construct new threads for consumers

			// File properties KO
			// System.exit(0);
			final ThreadFactory threadFactory = new ThreadFactory() {
				@Override
				public Thread newThread(Runnable r) {
					final ThreadFactory threadFactory = Executors.defaultThreadFactory();
					final Thread thread = threadFactory.newThread(r);
					thread.setDaemon(true);
					return thread;
				}
			};

			// Specify the size of the ring buffer, must be power of 2.
			int bufferSize = Integer
					.parseInt(LogFouineurMain.jlProperties.getProperty("logFouineur.ringBufferSize", "1024"));

			// Construct the Disruptor
			Disruptor<LogRecordEvent> disruptor = null;
			// Producer.SIMPLE is the best choice to read huge file of records
			// and
			// when recors spend on several lines
			if (LogFouineurMain.jlProperties.getProperty("logFouineur.waitStrategy", "blockingWaitSrategy")
					.equals("blockingWaitSrategy")) {
				// disruptor = new Disruptor<LogRecordEvent>(LogRecordEvent::new, bufferSize,
				// executor,
				// ProducerType.SINGLE, new BlockingWaitStrategy());
				disruptor = new Disruptor<LogRecordEvent>(LogRecordEvent::new, bufferSize, threadFactory,
						ProducerType.SINGLE, new BlockingWaitStrategy());

				System.out.println("JLP1logFouineur.waitStrategy=blockingWaitSrategy");
			} else if (LogFouineurMain.jlProperties.getProperty("logFouineur.waitStrategy", "blockingWaitSrategy")
					.equals("sleepingWaitStrategy")) {
				disruptor = new Disruptor<LogRecordEvent>(LogRecordEvent::new, bufferSize, threadFactory,
						ProducerType.SINGLE, new SleepingWaitStrategy());

				System.out.println("logFouineur.waitStrategy=sleepingWaitStrategy");
			} else if (LogFouineurMain.jlProperties.getProperty("logFouineur.waitStrategy", "blockingWaitSrategy")
					.equals("yieldingWaitStrategy")) {
				disruptor = new Disruptor<LogRecordEvent>(LogRecordEvent::new, bufferSize, threadFactory,
						ProducerType.SINGLE, new YieldingWaitStrategy());
				System.out.println("logFouineur.waitStrategy=yieldingWaitStrategy");
			} else if (LogFouineurMain.jlProperties.getProperty("logFouineur.waitStrategy", "blockingWaitSrategy")
					.equals("busySpinWaitStrategy")) {
				disruptor = new Disruptor<LogRecordEvent>(LogRecordEvent::new, bufferSize, threadFactory,
						ProducerType.SINGLE, new BusySpinWaitStrategy());
				System.out.println("logFouineur.waitStrategy=busySpinWaitStrategy");
			} else {
				disruptor = new Disruptor<LogRecordEvent>(LogRecordEvent::new, bufferSize, threadFactory,
						ProducerType.SINGLE, new BlockingWaitStrategy());
				System.out.println("logFouineur.waitStrategy=blockingWaitSrategy");
			}

			hmSummary = new HashMap<String, HashMap<Long, Double[]>>(nbPivots + 1);
			for (int i = 0; i < nbPivots; i++)
				hmSummary.put(pivots[i].getName(), new HashMap<Long, Double[]>()); // to
																					// fill
																					// Summary
			hmSummary.put("global", new HashMap<Long, Double[]>());

			allHmValues = new HashMap[nbThreads];
			threads = new EventHandler[nbThreads];
			isDurations = new boolean[nbVals];
			factorValueMs = new double[nbVals];
			tabTreated = new int[nbThreads];
			tabEliminated = new int[nbThreads];
			tabFiltered = new int[nbThreads];
			for (int i = 0; i < nbThreads; i++) {
				tabTreated[i] = 0;
				tabEliminated[i] = 0;
				tabFiltered[i] = 0;
			}
			// Loading plugins if any
			if (hasPlugins)
				loadPlugins();

			for (int i = 0; i < nbVals; i++) {
				isDurations[i] = values[i].isIsDuration();
				factorValueMs[i] = 1;
				if (isDurations[i]) {
					hmDuration.put(i, values[i].getName());
					hasDuration = true;
					rankValueDuration = i;
					switch (values[i].getUnit()) {
					case "nanos":
						factorValueMs[i] = 0.000001;
						break;
					case "ms":
						factorValueMs[i] = 1;
						break;
					case "micros":
						factorValueMs[i] = 0.001;
						break;

					case "s":
						factorValueMs[i] = 1000;
						break;

					case "mn":
						factorValueMs[i] = 60000;
						break;

					case "H":
						factorValueMs[i] = 3600000;
						break;

					case "cs":

						factorValueMs[i] = 10;
						break;
					}
				}
			}

			// Choosing correct Handler in regards of how is treated the date
			// (at
			// Producer level, or at Consumer Level)
			if (LogFouineurMain.currentScenarioProps.getProperty("logFouineur.getDateWhenReading", "true")
					.equals("true")) {

				for (int i = 0; i < nbThreads; i++) {
					allHmValues[i] = new HashMap<Long, HashMap<String, AggLogRecordEvent>>(); // to
																								// avoid
																								// NPE
																								// don't
																								// move.
					threads[i] = new RecordEventHandlerDatedRecord();
					((RecordEventHandlerDatedRecord) threads[i]).setId(i);

					// init((RecordEventHandlerDatedRecord) threads[i]);

				}

				disruptor.handleEventsWith(threads);

			} else {
				// TO DO with non dated records
				// disruptor.handleEventsWith(ParsingMain::handleEvent);
				System.out.println(
						"--------------------------------------\nTreatement with NonDatedRecord\n-------------------------------------");
				for (int i = 0; i < nbThreads; i++) {

					allHmValues[i] = new HashMap<Long, HashMap<String, AggLogRecordEvent>>(); // to
																								// avoid
																								// NPE
																								// don't
																								// move.
					threads[i] = new RecordEventHandlerNonDatedRecord();
					((RecordEventHandlerNonDatedRecord) threads[i]).setId(i);
					// System.out.println("----------------------------\nTreatement with
					// NonDatedRecord preparation thread["+i+"]\n-------------------");

					init((RecordEventHandlerNonDatedRecord) threads[i]);
				}
				disruptor.handleEventsWith(threads);
				// System.out.println("----------------------------\nTreatement with
				// NonDatedRecord avant lancement disruptor\n-------------------");
			}

			long debut = System.currentTimeMillis();
			// stream contains the right objects no Translator needed

			// Start the Disruptor, starts all threads running
			RingBuffer<LogRecordEvent> ringBuffer = disruptor.start();

			// Get the ring buffer from the Disruptor to be used for publishing.

			LogRecordEventProducer producer = new LogRecordEventProducer(ringBuffer);
			reads = 0;
			isParsing = true;

			// Thread to follow advancement
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
					while (null == ParsingMain.tabTreated) {

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

						eliminated = RecordReader.nbLinesEliminated;
						for (int i = 0; i < ParsingMain.threads.length; i++) {
							if (null != ParsingMain.threads[i]) {
								treated += ParsingMain.tabTreated[i];

							}
							if (!fromParseView) {
								LogFouineurFill.textArea.setText("filtered = " + RecordReader.nbLinesFiltered
										+ " / eliminated = " + RecordReader.nbLinesEliminated + " / treated = "
										+ treated + " / reads = " + ParsingMain.reads + " / inputs = "
										+ RecordReader.nbLinesInput);
							}

							try {
								Thread.sleep(500);
								// to Allow Threads correctly instantiated
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}
					treated = 0;

					for (int i = 0; i < ParsingMain.threads.length; i++) {

						treated += ParsingMain.tabTreated[i];

					}
					eliminated = RecordReader.nbLinesEliminated;

					long duration = System.currentTimeMillis() - deb;
					String strDuration = "Duration :" + ((long) duration / 1000) + " sec " + ((long) duration % 1000)
							+ " millis";
					if (!fromParseView) {
						LogFouineurFill.textArea
								.setText("filtered = " + RecordReader.nbLinesFiltered + " / eliminated = "
										+ RecordReader.nbLinesEliminated + " / treated = " + treated + " / reads = "
										+ reads + " / inputs = " + RecordReader.nbLinesInput + "\n" + strDuration);
					}

				}

			}).start();

			stream.forEach(event -> {
				// System.out.println("coucouIn debug="+debug);
				reads++;

				producer.onData(event);

			});

			System.out.println("reads=" + reads);
			int loop = 0;
			while (true) {
				int totTreated = 0;
				int totEliminated = 0;
				int totFiltered = 0;
				int totInputs = 0;
				for (int i = 0; i < threads.length; i++) {

					totTreated += tabTreated[i];

					totFiltered += tabFiltered[i];
				}
				loop++;
				// String message= "treated =>" + totTreated + "/" + reads;
				// LogFouineurFill.textArea.setText(message);

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (totTreated == reads) {

					System.out.println("ParsingMain loop => " + loop + " ; treated =>" + totTreated + "/" + reads);
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					isParsing = false;
					break;
				}
			}

			if (ConfigRecord.boolGetDateWhenReading) {
				for (int i = 0; i < nbThreads; i++) {
					allHmValues[i] = ((RecordEventHandlerDatedRecord) threads[i]).localHm;
				}
			} else {
				for (int i = 0; i < nbThreads; i++) {
					allHmValues[i] = ((RecordEventHandlerNonDatedRecord) threads[i]).localHm;
				}
			}

			try {
				disruptor.shutdown(-1, java.util.concurrent.TimeUnit.MILLISECONDS);
			} catch (TimeoutException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			;

			long debClosing = System.currentTimeMillis();
			// Close all enreg
			ExecutorService pool = Executors.newFixedThreadPool(nbThreads);
			for (int i = 0; i < nbThreads; i++) {
				pool.execute(new CloseRecordHandler(i));
			}
			pool.shutdown();
			// try {
			// // Wait a while for existing tasks to terminate
			// if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
			// pool.shutdownNow(); // Cancel currently executing tasks
			// // Wait a while for tasks to respond to being cancelled
			// if (!pool.awaitTermination(60, TimeUnit.SECONDS))
			// System.err.println("Pool did not terminate");
			// } else {
			// System.err.println("Pool correctly terminated");
			// }
			// } catch (InterruptedException ie) {
			// // (Re-)Cancel if current thread also interrupted
			// pool.shutdownNow();
			// // Preserve interrupt status
			// Thread.currentThread().interrupt();
			// }

			while (!pool.isTerminated()) {

				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			long finClosing = System.currentTimeMillis();
			String str = "";
			for (int i = 0; i < nbThreads; i++) {
				str += "Thread [" + i + "] : hmValue.size() =" + allHmValues[i].size() + "\n";
			}
			if (nbThreads > 1) {
				// closeEnrInHm(allHmValues[0]);
				// merging allHmValues[i] pour i>0 in allHmValues[0].
				// using a single thread
				for (int i = 1; i < nbThreads; i++) {
					mergeHm(allHmValues[i]);
					allHmValues[i].clear(); // for Garbaging
				}
			}
			treeMap = new TreeMap<Long, HashMap<String, AggLogRecordEvent>>(allHmValues[0]);
			System.out.println("after merge  treeMap.size() =" + treeMap.size());
			allHmValues[0].clear();
			allHmValues = null;
			if (hasPlugins) {
				for (int j = 0; j < nbThreads; j++) {
					hmapsClass[j].clear();
					hmapsMethod[j].clear();
				}
			}
			hmapsClass = null;
			hmapsMethod = null;
			long finMerging = System.currentTimeMillis();
			treatParallelRequests();
			long finTreatementParallel = System.currentTimeMillis();
			System.out.println("avant Generate file =" + treeMap.size());
			new GenerateCsvFile();
			System.out.println("apres Generate file duree =" + (System.currentTimeMillis() - finTreatementParallel));

			// Save Date of CSV log
			LogFouineurMain.currentScenarioProps.setProperty("logFouineur.minDateOfLog",
					Long.toString(minDateOfLogFile));
			LogFouineurMain.currentScenarioProps.setProperty("logFouineur.maxDateOfLog",
					Long.toString(maxDateOfLogFile));
			try (FileOutputStream fos = new FileOutputStream(strConfigPropertiesFile);) {
				LogFouineurMain.currentScenarioProps.store(fos, "");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			long fin = System.currentTimeMillis();
			System.out.println("Result of Parsing :\n" + str);
			System.out.println("Closing Enreg in : " + (finClosing - debClosing) + " ms");

			System.out.println("Merging HashMaps  in : " + (finMerging - finClosing) + " ms");
			System.out.println("Treatement parallel request in : " + (finTreatementParallel - finMerging) + " ms");
			System.out.println("Generating CSV file in : " + (fin - finTreatementParallel) + " ms");
			System.out.println("par End parse treeMap.size() =" + treeMap.size());
			
		} else {
			System.out.println("Stream is null");
		}

		if ((LogFouineurMain.errorFormatDate.trim() + LogFouineurMain.errorFormatValue.trim()).length() > 5) {
			System.out.println("!!----------Errors--------------!!");

			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Information Dialog");

					alert.setHeaderText("An error occurs while parsing ");

					String text = ((LogFouineurMain.errorFormatDate.trim().length() > 5)
							? LogFouineurMain.errorFormatDate + "\n"
							: "")
							+ ((LogFouineurMain.errorFormatValue.trim().length() > 5)
									? LogFouineurMain.errorFormatValue + "\n"
									: "");

					alert.setContentText("Errors : \n" + text);
					alert.setResizable(true);
					alert.getDialogPane().setPrefSize(480, 320);
					System.out.println("!!----------Errors -> " + text);
					alert.showAndWait();
				}
			});
		}
		if (LogFouineurMain.isTrace) {
			try {
				while (LogFouineurMain.buf.hasRemaining()) {

					int nb = LogFouineurMain.channel.write(LogFouineurMain.buf.flip());
					System.out.println("Trace End Parsing nb writing bytes =" + nb);

					if (nb == 0)
						break;
					LogFouineurMain.fos.flush();

					LogFouineurMain.buf.compact();

				}
				LogFouineurMain.fos.flush();
				LogFouineurMain.fos.close();
				LogFouineurMain.channel.close();

				LogFouineurMain.channel = null;
				LogFouineurMain.fos = null;
				LogFouineurMain.buf = null;
				System.out.println("Trace Closing Channel of trace");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (LogFouineurMain.isDebug) {
			try {
				while (LogFouineurMain.bufDebug.hasRemaining()) {

					int nb = LogFouineurMain.channelDebug.write(LogFouineurMain.bufDebug.flip());
					System.out.println("Debug End Parsing nb writing bytes =" + nb);

					if (nb == 0)
						break;
					LogFouineurMain.fosDebug.flush();

					LogFouineurMain.bufDebug.compact();

				}
				LogFouineurMain.fosDebug.flush();
				LogFouineurMain.fosDebug.close();
				LogFouineurMain.channelDebug.close();

				LogFouineurMain.channelDebug = null;
				LogFouineurMain.fosDebug = null;
				LogFouineurMain.bufDebug = null;
				System.out.println("Debug Closing Channel of Debug");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		hasDuration = false;
		rankValueDuration = -1;

	}

	/**
	 * Inits the.
	 *
	 * @param aThread
	 *            the a thread
	 */
	private static final void init(RecordEventHandlerNonDatedRecord aThread) {

		aThread.aggregPeriodInMillis = ConfigRecord.aggregPeriodInMillis;
		aThread.nbVals = nbVals;
		aThread.decalAllDatesInMillis = ConfigRecord.decalAllDatesInMillis;
		aThread.values = values;
		aThread.factorValueMs = factorValueMs;
		aThread.dateBeginOfParsing = ConfigRecord.dateBeginOfParsing;
		aThread.dateEndOfParsing = ConfigRecord.dateEndOfParsing;
		aThread.regexDateRecord = ConfigRecord.regexDateRecord;

		aThread.regexExclude = ConfigRecord.regexExclude;
		aThread.regexInclude = ConfigRecord.regexInclude;

		if (null != ConfigRecord.javaDateFormatRecord
				&& !ConfigRecord.javaDateFormatRecord.toLowerCase().contains("dateinmillis")) {

			aThread.sdf = new SimpleDateFormat(ConfigRecord.javaDateFormatRecord);
		}

		aThread.factorDateInMillis = ConfigRecord.factorDateInMillis;
		aThread.originDate = ConfigRecord.originDate;

		aThread.boolExplicitDate = ConfigRecord.boolExplicitDate;
		aThread.isDateInMillis = ConfigRecord.isDateInMillis;
		aThread.implicitStep = Boolean.toString(ConfigRecord.boolImplicitStep);
		aThread.stepInMillis = ConfigRecord.step2Records;
		aThread.factorToMillis = ParsingMain.factorToMillis;

		aThread.gapRegex = ConfigRecord.gapRegex;
		aThread.absRel = ConfigRecord.absRel;

		// ParsingMain.valueRegex[i]
		aThread.valueRegex = valueRegex;
		aThread.hmapsClass = hmapsClass; // a hashMap by
		// thread

		aThread.hmapsMethod = hmapsMethod;
		aThread.nbPivots = nbPivots;
		aThread.pivotRegex = pivotRegex;// ParsingMain.pivotRegex[i]) ;
		// ParsingMain.pivots[i]
		aThread.pivots = pivots;

		aThread.boolExhaustiveParsing = ConfigRecord.boolExhaustiveParsing;

	}

	/**
	 * Can I use multi thread.
	 *
	 * @return true, if successful
	 */
	private final static boolean canIUseMultiThread() {
		// test with Implicit Date
		if (!ConfigRecord.boolExplicitDate)
			return false;

		// test with plugin that starts with Mono
		for (JFXValue value : values) {
			if (value.getName().trim().replaceAll("\\s", "").startsWith("plugin=Mono")) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Treat parallel requests.
	 */
	private final static void treatParallelRequests() {

		if (hasDuration) {
			// effectuer le calcul de parallel et remplir les hashmap pour le
			// summary
			switch (ConfigRecord.confOutputFile) {
			case "All+Avg":
			case "Avg":
				summary = "Avg";
				treeMap.forEach((periodTime, hmAgg) -> {
					if (periodTime >= maxDateOfLogFile) {
						maxDateOfLogFile = periodTime;
					}
					if (periodTime <= minDateOfLogFile) {
						minDateOfLogFile = periodTime;
					}
					hmAgg.forEach((pivot, aggRec) -> {
						HashMap<Long, Double[]> tmpHm0 = hmSummary.get(pivot);
						tmpHm0.put(periodTime, aggRec.averages);
						hmSummary.put(pivot, tmpHm0);
						// compute the duration Value
						if (aggRec.countsDurationSupPeriod[rankValueDuration] > 0) {
							int nbPeriodSup = (int) Math
									.ceil(aggRec.sumsDurationSupPeriod[rankValueDuration].doubleValue()
											* ParsingMain.factorValueMs[rankValueDuration]
											/ (aggRec.countsDurationSupPeriod[rankValueDuration]
													* ConfigRecord.aggregPeriodInMillis));

							for (int i = 1; i <= nbPeriodSup; i++) {
								long newPeriod = periodTime + i * ConfigRecord.aggregPeriodInMillis;
								if (treeMap.containsKey(newPeriod)) {
									HashMap<String, AggLogRecordEvent> tmpHm = treeMap.get(newPeriod);
									if (tmpHm.containsKey(pivot)) {
										AggLogRecordEvent tmpAgg = tmpHm.get(pivot);

										tmpAgg.countParallels[rankValueDuration] += aggRec.countsDurationSupPeriod[rankValueDuration];
										tmpHm.put(pivot, tmpAgg);
										treeMap.put(newPeriod, tmpHm);
									}

								}
							}
						}

					});
				});
				break;
			case "All+Rate":
			case "Rate":
				summary = "Rate";
				treeMap.forEach((periodTime, hmAgg) -> {
					if (periodTime >= maxDateOfLogFile) {
						maxDateOfLogFile = periodTime;
					}
					if (periodTime <= minDateOfLogFile) {
						minDateOfLogFile = periodTime;
					}
					hmAgg.forEach((pivot, aggRec) -> {
						HashMap<Long, Double[]> tmpHm0 = hmSummary.get(pivot);
						tmpHm0.put(periodTime, aggRec.rates);
						hmSummary.put(pivot, tmpHm0);
						// compute the duration Value
						if (aggRec.countsDurationSupPeriod[rankValueDuration] > 0) {
							int nbPeriodSup = (int) Math
									.ceil(aggRec.sumsDurationSupPeriod[rankValueDuration].doubleValue()
											* ParsingMain.factorValueMs[rankValueDuration]
											/ (aggRec.countsDurationSupPeriod[rankValueDuration]
													* ConfigRecord.aggregPeriodInMillis));

							for (int i = 1; i <= nbPeriodSup; i++) {
								long newPeriod = periodTime + i * ConfigRecord.aggregPeriodInMillis;
								if (treeMap.containsKey(newPeriod)) {
									HashMap<String, AggLogRecordEvent> tmpHm = treeMap.get(newPeriod);
									if (tmpHm.containsKey(pivot)) {
										AggLogRecordEvent tmpAgg = tmpHm.get(pivot);

										tmpAgg.countParallels[rankValueDuration] += aggRec.countsDurationSupPeriod[rankValueDuration];
										tmpHm.put(pivot, tmpAgg);
										treeMap.put(newPeriod, tmpHm);
									}

								}
							}
						}

					});
				});
				break;
			case "All+Max":
			case "Max":
				summary = "Max";
				treeMap.forEach((periodTime, hmAgg) -> {
					if (periodTime >= maxDateOfLogFile) {
						maxDateOfLogFile = periodTime;
					}
					if (periodTime <= minDateOfLogFile) {
						minDateOfLogFile = periodTime;
					}
					hmAgg.forEach((pivot, aggRec) -> {
						HashMap<Long, Double[]> tmpHm0 = hmSummary.get(pivot);
						tmpHm0.put(periodTime, aggRec.maxs);
						hmSummary.put(pivot, tmpHm0);
						// compute the duration Value
						if (aggRec.countsDurationSupPeriod[rankValueDuration] > 0) {
							int nbPeriodSup = (int) Math
									.ceil(aggRec.sumsDurationSupPeriod[rankValueDuration].doubleValue()
											* ParsingMain.factorValueMs[rankValueDuration]
											/ (aggRec.countsDurationSupPeriod[rankValueDuration]
													* ConfigRecord.aggregPeriodInMillis));

							for (int i = 1; i <= nbPeriodSup; i++) {
								long newPeriod = periodTime + i * ConfigRecord.aggregPeriodInMillis;
								if (treeMap.containsKey(newPeriod)) {
									HashMap<String, AggLogRecordEvent> tmpHm = treeMap.get(newPeriod);
									if (tmpHm.containsKey(pivot)) {
										AggLogRecordEvent tmpAgg = tmpHm.get(pivot);
										tmpAgg.countParallels[rankValueDuration] += aggRec.countsDurationSupPeriod[rankValueDuration];
										tmpHm.put(pivot, tmpAgg);
										treeMap.put(newPeriod, tmpHm);
									}

								}
							}
						}

					});
				});
				break;
			case "All+Min":
			case "Min":
				summary = "Min";
				treeMap.forEach((periodTime, hmAgg) -> {
					if (periodTime >= maxDateOfLogFile) {
						maxDateOfLogFile = periodTime;
					}
					if (periodTime <= minDateOfLogFile) {
						minDateOfLogFile = periodTime;
					}
					hmAgg.forEach((pivot, aggRec) -> {
						HashMap<Long, Double[]> tmpHm0 = hmSummary.get(pivot);
						tmpHm0.put(periodTime, aggRec.mins);
						hmSummary.put(pivot, tmpHm0);
						// compute the duration Value
						if (aggRec.countsDurationSupPeriod[rankValueDuration] > 0) {
							int nbPeriodSup = (int) Math
									.ceil(aggRec.sumsDurationSupPeriod[rankValueDuration].doubleValue()
											* ParsingMain.factorValueMs[rankValueDuration]
											/ (aggRec.countsDurationSupPeriod[rankValueDuration]
													* ConfigRecord.aggregPeriodInMillis));

							for (int i = 1; i <= nbPeriodSup; i++) {
								long newPeriod = periodTime + i * ConfigRecord.aggregPeriodInMillis;
								if (treeMap.containsKey(newPeriod)) {
									HashMap<String, AggLogRecordEvent> tmpHm = treeMap.get(newPeriod);
									if (tmpHm.containsKey(pivot)) {
										AggLogRecordEvent tmpAgg = tmpHm.get(pivot);
										tmpAgg.countParallels[rankValueDuration] += aggRec.countsDurationSupPeriod[rankValueDuration];
										tmpHm.put(pivot, tmpAgg);
										treeMap.put(newPeriod, tmpHm);
									}

								}
							}
						}

					});
				});
				break;
			case "All+Sum":
			case "Sum":
				summary = "Sum";
				treeMap.forEach((periodTime, hmAgg) -> {
					if (periodTime >= maxDateOfLogFile) {
						maxDateOfLogFile = periodTime;
					}
					if (periodTime <= minDateOfLogFile) {
						minDateOfLogFile = periodTime;
					}
					hmAgg.forEach((pivot, aggRec) -> {
						HashMap<Long, Double[]> tmpHm0 = hmSummary.get(pivot);
						tmpHm0.put(periodTime, aggRec.sums);
						hmSummary.put(pivot, tmpHm0);
						// compute the duration Value
						if (aggRec.countsDurationSupPeriod[rankValueDuration] > 0) {
							int nbPeriodSup = (int) Math
									.ceil(aggRec.sumsDurationSupPeriod[rankValueDuration].doubleValue()
											* ParsingMain.factorValueMs[rankValueDuration]
											/ (aggRec.countsDurationSupPeriod[rankValueDuration]
													* ConfigRecord.aggregPeriodInMillis));

							for (int i = 1; i <= nbPeriodSup; i++) {
								long newPeriod = periodTime + i * ConfigRecord.aggregPeriodInMillis;
								if (treeMap.containsKey(newPeriod)) {
									HashMap<String, AggLogRecordEvent> tmpHm = treeMap.get(newPeriod);
									if (tmpHm.containsKey(pivot)) {
										AggLogRecordEvent tmpAgg = tmpHm.get(pivot);
										tmpAgg.countParallels[rankValueDuration] += aggRec.countsDurationSupPeriod[rankValueDuration];
										tmpHm.put(pivot, tmpAgg);
										treeMap.put(newPeriod, tmpHm);
									}

								}
							}
						}

					});
				});
				break;
			case "All+Count":
			case "Count":
				summary = "Count";
				treeMap.forEach((periodTime, hmAgg) -> {
					if (periodTime >= maxDateOfLogFile) {
						maxDateOfLogFile = periodTime;
					}
					if (periodTime <= minDateOfLogFile) {
						minDateOfLogFile = periodTime;
					}
					hmAgg.forEach((pivot, aggRec) -> {
						HashMap<Long, Double[]> tmpHm0 = hmSummary.get(pivot);
						int[] ints = aggRec.counts;
						Double[] doubles = new Double[ints.length];
						for (int i = 0; i < ints.length; i++) {
							doubles[i] = Double.valueOf(Integer.toString(ints[i]));
						}
						tmpHm0.put(periodTime, doubles);
						hmSummary.put(pivot, tmpHm0);
						// compute the duration Value
						if (aggRec.countsDurationSupPeriod[rankValueDuration] > 0) {
							int nbPeriodSup = (int) Math
									.ceil(aggRec.sumsDurationSupPeriod[rankValueDuration].doubleValue()
											* ParsingMain.factorValueMs[rankValueDuration]
											/ (aggRec.countsDurationSupPeriod[rankValueDuration]
													* ConfigRecord.aggregPeriodInMillis));

							for (int i = 1; i <= nbPeriodSup; i++) {
								long newPeriod = periodTime + i * ConfigRecord.aggregPeriodInMillis;
								if (treeMap.containsKey(newPeriod)) {
									HashMap<String, AggLogRecordEvent> tmpHm = treeMap.get(newPeriod);
									if (tmpHm.containsKey(pivot)) {
										AggLogRecordEvent tmpAgg = tmpHm.get(pivot);
										tmpAgg.countParallels[rankValueDuration] += aggRec.countsDurationSupPeriod[rankValueDuration];
										tmpHm.put(pivot, tmpAgg);
										treeMap.put(newPeriod, tmpHm);
									}

								}
							}
						}

					});
				});
				break;
			}

		} else {
			switch (ConfigRecord.confOutputFile) {
			case "All+Avg":
			case "Avg":
				summary = "Avg";
				treeMap.forEach((periodTime, hmAgg) -> {
					if (periodTime >= maxDateOfLogFile) {
						maxDateOfLogFile = periodTime;
					}
					if (periodTime <= minDateOfLogFile) {
						minDateOfLogFile = periodTime;
					}
					hmAgg.forEach((pivot, aggRec) -> {
						HashMap<Long, Double[]> tmpHm = hmSummary.get(pivot);
						tmpHm.put(periodTime, aggRec.averages);
						hmSummary.put(pivot, tmpHm);

					});
				});
				break;
			case "All+Rate":
			case "Rate":
				summary = "Rate";
				treeMap.forEach((periodTime, hmAgg) -> {
					if (periodTime >= maxDateOfLogFile) {
						maxDateOfLogFile = periodTime;
					}
					if (periodTime <= minDateOfLogFile) {
						minDateOfLogFile = periodTime;
					}
					hmAgg.forEach((pivot, aggRec) -> {
						HashMap<Long, Double[]> tmpHm = hmSummary.get(pivot);
						tmpHm.put(periodTime, aggRec.rates);
						hmSummary.put(pivot, tmpHm);

					});
				});
				break;
			case "All+Max":
			case "Max":
				summary = "Max";
				treeMap.forEach((periodTime, hmAgg) -> {
					if (periodTime >= maxDateOfLogFile) {
						maxDateOfLogFile = periodTime;
					}
					if (periodTime <= minDateOfLogFile) {
						minDateOfLogFile = periodTime;
					}
					hmAgg.forEach((pivot, aggRec) -> {
						HashMap<Long, Double[]> tmpHm = hmSummary.get(pivot);
						tmpHm.put(periodTime, aggRec.maxs);
						hmSummary.put(pivot, tmpHm);

					});
				});
				break;
			case "All+Min":
			case "Min":
				summary = "Min";
				treeMap.forEach((periodTime, hmAgg) -> {
					hmAgg.forEach((pivot, aggRec) -> {
						HashMap<Long, Double[]> tmpHm = hmSummary.get(pivot);
						tmpHm.put(periodTime, aggRec.mins);
						hmSummary.put(pivot, tmpHm);

					});
				});
				break;
			case "All+Sum":
			case "Sum":
				summary = "Sum";
				treeMap.forEach((periodTime, hmAgg) -> {
					if (periodTime >= maxDateOfLogFile) {
						maxDateOfLogFile = periodTime;
					}
					if (periodTime <= minDateOfLogFile) {
						minDateOfLogFile = periodTime;
					}
					hmAgg.forEach((pivot, aggRec) -> {
						HashMap<Long, Double[]> tmpHm = hmSummary.get(pivot);
						tmpHm.put(periodTime, aggRec.sums);
						hmSummary.put(pivot, tmpHm);

					});
				});
				break;
			case "All+Count":
			case "Count":
				summary = "Count";
				treeMap.forEach((periodTime, hmAgg) -> {
					if (periodTime >= maxDateOfLogFile) {
						maxDateOfLogFile = periodTime;
					}
					if (periodTime <= minDateOfLogFile) {
						minDateOfLogFile = periodTime;
					}
					hmAgg.forEach((pivot, aggRec) -> {
						HashMap<Long, Double[]> tmpHm = hmSummary.get(pivot);
						int[] ints = aggRec.counts;
						Double[] doubles = new Double[ints.length];
						for (int i = 0; i < ints.length; i++) {
							doubles[i] = Double.valueOf(Integer.toString(ints[i]));
						}
						tmpHm.put(periodTime, doubles);
						hmSummary.put(pivot, tmpHm);

					});
				});
				break;
			}

		}
		// prepare a tree Map for summary TreeMap<Long
		// ,HashMap<String,Double[]>>

		hmSummary.forEach((pivot, tmpHm) -> {
			tmpHm.forEach((period, tabValues) -> {
				HashMap<String, Double[]> tmpHm1;
				if (tmSummary.containsKey(period)) {
					tmpHm1 = tmSummary.get(period);
					tmpHm1.put(pivot, tabValues);
				} else {
					tmpHm1 = new HashMap<String, Double[]>();
					tmpHm1.put(pivot, tabValues);
				}
				tmSummary.put(period, tmpHm1);
			});
		});
		hmSummary.clear();
	}

	/**
	 * Merge hm.
	 *
	 * @param hashMap
	 *            the hash map
	 */
	private final static void mergeHm(HashMap<Long, HashMap<String, AggLogRecordEvent>> hashMap) {
		hashMap.forEach((key, hm) -> {
			// test if key exists in ParsingMain.allHmValues[0]
			if (ParsingMain.allHmValues[0].containsKey(key)) {
				// agg+regation str is the name of Pivot
				hm.forEach((pivot, record) -> {
					HashMap<String, AggLogRecordEvent> hm0 = ParsingMain.allHmValues[0].get(key);
					if (hm0.containsKey(pivot)) {
						AggLogRecordEvent tmpALR = hm0.get(pivot);
						tmpALR.merge(record);
						hm0.put(pivot, tmpALR);

					} else {
						hm0.put(pivot, record);
					}
					ParsingMain.allHmValues[0].put(key, hm0);
				});

			} else {
				ParsingMain.allHmValues[0].put(key, hm);

			}

		});

	}

	/**
	 * Fill pivots.
	 *
	 * @return the int
	 */
	private final static int fillPivots() {
		ArrayList<JFXPivot> toSplit = new ArrayList<JFXPivot>(100);
		boolean vrai = true;
		int i = 0;
		while (vrai) {
			if (LogFouineurMain.currentScenarioProps.containsKey("logFouineur.pivot." + i + ".name")) {
				toSplit.add(i, new JFXPivot(
						LogFouineurMain.currentScenarioProps.getProperty("logFouineur.pivot." + i + ".name"),
						LogFouineurMain.currentScenarioProps.getProperty("logFouineur.pivot." + i + ".regex1"),
						LogFouineurMain.currentScenarioProps.getProperty("logFouineur.pivot." + i + ".regex2")));
				i++;
			} else
				vrai = false;
		}
		if (i != 0) {
			// Filling pivots
			pivots = new JFXPivot[i];
			pivotRegex = new int[i];
			for (int j = 0; j < i; j++) {
				pivots[j] = toSplit.get(j);
				if (pivots[j].getRegex2().length() > 0)
					pivotRegex[j] = 2;
				else
					pivotRegex[j] = 1;
			}
		}

		return i;
	}

	/**
	 * Fill and get nb vals.
	 *
	 * @return the int
	 */
	private final static int fillAndGetNbVals() {
		ArrayList<JFXValue> toSplit = new ArrayList<JFXValue>(10);
		boolean vrai = true;
		int i = 0;
		while (vrai) {
			if (LogFouineurMain.currentScenarioProps.containsKey("logFouineur.value." + i + ".name")) {
				toSplit.add(i, new JFXValue(
						LogFouineurMain.currentScenarioProps.getProperty("logFouineur.value." + i + ".name"),
						LogFouineurMain.currentScenarioProps.getProperty("logFouineur.value." + i + ".regex1"),
						LogFouineurMain.currentScenarioProps.getProperty("logFouineur.value." + i + ".regex2"),
						LogFouineurMain.currentScenarioProps.getProperty("logFouineur.value." + i + ".unit"),
						Double.parseDouble(
								LogFouineurMain.currentScenarioProps.getProperty("logFouineur.value." + i + ".scale")),
						Boolean.parseBoolean(LogFouineurMain.currentScenarioProps
								.getProperty("logFouineur.value." + i + ".isDuration", "false"))));
				if (LogFouineurMain.currentScenarioProps.getProperty("logFouineur.value." + i + ".regex1").toLowerCase()
						.replaceAll("\\s", "").startsWith("plugin=")) {
					// on cree la classe et la methode
					hasPlugins = true;

				}

				i++;
			} else {
				vrai = false;
			}

		}
		// Filling values
		values = new JFXValue[i];
		valueRegex = new int[i];
		for (int j = 0; j < i; j++) {
			values[j] = toSplit.get(j);
			if (values[j].getRegex2().length() > 0) {
				if (values[j].getRegex1().startsWith("plugin")) {
					valueRegex[j] = 0;
				} else {
					valueRegex[j] = 2;
				}

			} else {
				valueRegex[j] = 1;
			}
		}

		return i;
	}

	/**
	 * Load plugins.
	 */
	@SuppressWarnings("unchecked")
	public final static void loadPlugins() {
		if (hasPlugins) {
			if (new File(System.getProperty("root") + File.separator + "libs").exists()) {

				URL[] urls = new URL[3];
				try {
					urls[0] = new URL("file", "localhost", System.getProperty("root") + File.separator + "libs");
					urls[1] = new URL("file", "localhost",
							System.getProperty("root") + File.separator + "libs" + File.separator + "logfouineur.jar");
					if (new File(System.getProperty("root") + File.separator + "myPlugins").exists()) {
						urls[2] = new URL("file", "localhost", System.getProperty("root") + File.separator + "myPlugins"
								+ File.separator + "plugins.jar");
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

			int i = 0;
			for (JFXValue value : values) {
				if (LogFouineurMain.currentScenarioProps.getProperty("logFouineur.value." + i + ".regex1").toLowerCase()
						.replaceAll("\\s", "").startsWith("plugin=")) {
					// on cree la classe et la methode
					for (int j = 0; j < nbThreads; j++) {

						String key = "plugins." + LogFouineurMain.currentScenarioProps
								.getProperty("logFouineur.value." + i + ".regex1").split("=")[1];
						Object objUtil = null;
						String regex2tmp = LogFouineurMain.currentScenarioProps
								.getProperty("logFouineur.value." + i + ".regex2", "");

						try {
							try {
								objUtil = Class.forName(key, true, urlClassLoader).getDeclaredConstructor()
										.newInstance();
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
						key = key + "_" + i;

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
							Method metInit = hmapsClass[j].get(key).getClass().getDeclaredMethod("initialize",
									String.class);

							metInit.invoke(objUtil, new Object[] { regex2tmp }); // To intialize an instance or counters
																					// or
							// others static
							// structures
						} catch (NoSuchMethodException | SecurityException | IllegalAccessException
								| IllegalArgumentException | InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				i++;
			}
		}
	}
}
