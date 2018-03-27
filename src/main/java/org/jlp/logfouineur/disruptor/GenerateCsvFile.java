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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.jlp.logfouineur.models.AggLogRecordEvent;
import org.jlp.logfouineur.models.JFXPivot;
import org.jlp.logfouineur.models.JFXValue;
import org.jlp.logfouineur.parseview.ParseView;
import org.jlp.logfouineur.records.ConfigRecord;
import org.jlp.logfouineur.ui.LogFouineurMain;
import org.jlp.logfouineur.util.Utils;


import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;

// TODO: Auto-generated Javadoc
/**
 * The Class GenerateCsvFile.
 */
public class GenerateCsvFile {
	
	/** The str summary file. */
	public static String strSummaryFile=null;

	/** The disruptor. */
	Disruptor<AggLogRecordEvent> disruptor;

	/** The thread factory. */
	final ThreadFactory threadFactory;

	/** The ring buffer size. */
	static int ringBufferSize = Integer
			.parseInt(LogFouineurMain.jlProperties.getProperty("logFouineur.ringBufferSize", "1024"));

	/** The buffer size. */
	static int bufferSize = Integer
			.parseInt(LogFouineurMain.jlProperties.getProperty("logFouineur.byteBufferSize", "10485760"));

	/** The threads. */
	public static EventHandler<AggLogRecordEvent>[] threads = null;

	/** The rep csv. */
	String repCsv = ConfigRecord.repOut;

	/** The positio warning. */
	static int positioWarning = bufferSize * 90 / 100;

	/** The bf. */
	ByteBuffer bf = ByteBuffer.allocateDirect(bufferSize);

	/** The bfs. */
	static ByteBuffer[] bfs = null;

	/** The foss. */
	static FileOutputStream[] foss = null;

	/**
	 * Instantiates a new generate csv file.
	 */
	@SuppressWarnings("unchecked")
	public GenerateCsvFile() {
		strSummaryFile=null;
		bfs = new ByteBuffer[ParsingMain.nbPivots + 1];
		foss = new FileOutputStream[ParsingMain.nbPivots + 1];

		threads = (EventHandler<AggLogRecordEvent>[]) new EventHandler<?>[ParsingMain.nbPivots + 1];
		repCsv = ConfigRecord.repOut;
		bf = ByteBuffer.allocateDirect(bufferSize);
		threadFactory = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				final ThreadFactory threadFactory = Executors.defaultThreadFactory();
				final Thread thread = threadFactory.newThread(r);
				thread.setDaemon(true);
				return thread;
			}
		};
		System.out.println("repCsv =" + repCsv);
		// create the csv folder for the scenario

		if (new File(repCsv).exists()) {
			try {
				Utils.deleteRecursifDir(repCsv);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Deleting " + repCsv + " failed");
			}
		} else {
			new File(repCsv).mkdir();
		}
		// ParsingMain.dblValues = new HashMap<Long, HashMap<String, Double>>();

		if (!ConfigRecord.boolCompactOutput) {

			// Prepare CSV Files

			// Prepare CSV Files
			// A csv file has the name : <namePivot>.csv

			if (ConfigRecord.confOutputFile.startsWith("All+")) {

				splited();
			}

		} else {

			if (ConfigRecord.confOutputFile.startsWith("All+")) {
				compacted();
			}
		}
		generateSummaryCsv();
	}

	/**
	 * Generate summary csv.
	 */
	private final void generateSummaryCsv() {
		// create File with title
		String title = "DateTime" + ConfigRecord.csvSeparatorOut;
		if (!ParsingMain.summary.equals("Count") && !ParsingMain.summary.equals("Rate")) {
			if (null != ParsingMain.pivots) {
				for (JFXPivot pivot : ParsingMain.pivots) {
					String namePivot = pivot.getName();
					for (JFXValue value : ParsingMain.values) {

						title += namePivot + "_" + value.getName() + "_" + ParsingMain.summary + "(" + value.getUnit()
								+ ")" + ConfigRecord.csvSeparatorOut;
					}
				}
			}
			for (JFXValue value : ParsingMain.values) {

				title += "global" + "_" + value.getName() + "_" + ParsingMain.summary + "(" + value.getUnit() + ")"
						+ ConfigRecord.csvSeparatorOut;
			}
		}

		else if (ParsingMain.summary.equals("Count")) {
			if (null != ParsingMain.pivots) {
				for (JFXPivot pivot : ParsingMain.pivots) {
					String namePivot = pivot.getName();
					for (JFXValue value : ParsingMain.values) {

						title += namePivot + "_" + value.getName() + "_" + ParsingMain.summary + "(unit)"
								+ ConfigRecord.csvSeparatorOut;
					}
				}
			}
			for (JFXValue value : ParsingMain.values) {

				title += "global" + "_" + value.getName() + "_" + ParsingMain.summary + "(unit)"
						+ ConfigRecord.csvSeparatorOut;
			}
		} else if (ParsingMain.summary.equals("Rate")) {

			if (null != ParsingMain.pivots) {
				for (JFXPivot pivot : ParsingMain.pivots) {
					String namePivot = pivot.getName();
					for (JFXValue value : ParsingMain.values) {

						title += namePivot + "_" + value.getName() + "_" + ParsingMain.summary + "(hits/s)"
								+ ConfigRecord.csvSeparatorOut;
					}
				}
			}
			for (JFXValue value : ParsingMain.values) {

				title += "global" + "_" + value.getName() + "_" + ParsingMain.summary + "(hits/s)"
						+ ConfigRecord.csvSeparatorOut;
			}

		}

		try (FileOutputStream fos = new FileOutputStream(
				new File(repCsv + File.separator + "fl_All" + ParsingMain.summary + ".csv"));
				FileChannel fc = fos.getChannel()) {
			// When using parseAndView menuItem
			ParseView.generatedCsvFile = repCsv + File.separator + "fl_All" + ParsingMain.summary + ".csv";
			strSummaryFile=repCsv + File.separator + "fl_All" + ParsingMain.summary + ".csv";
			bf.clear();
			bf = bf.put((title + "\n").getBytes());
			// fc.write(bf);

			ParsingMain.tmSummary.forEach((timeInMillis, tmpHm) -> {
				String strDate = ConfigRecord.sdfOut.format(new Date(timeInMillis));
				StringBuilder strB = new StringBuilder();
				strB = new StringBuilder(strDate).append(ConfigRecord.csvSeparatorOut);
				if (null != ParsingMain.pivots) {
					for (JFXPivot pivot : ParsingMain.pivots) {
						String namePivot = pivot.getName();
						for (int i = 0; i < ParsingMain.nbVals; i++) {
							if (tmpHm.containsKey(namePivot)) {
								if (tmpHm.get(namePivot)[i].isNaN())
									strB.append(ConfigRecord.csvSeparatorOut);
								else
									strB.append(tmpHm.get(namePivot)[i]).append(ConfigRecord.csvSeparatorOut);
								;
							} else
								strB.append(ConfigRecord.csvSeparatorOut);
						}
					}
				}
				// adding global value
				for (int i = 0; i < ParsingMain.nbVals; i++) {
					if (tmpHm.containsKey("global")) {
						if (tmpHm.get("global")[i].isNaN())
							strB.append(ConfigRecord.csvSeparatorOut);
						else
							strB.append(tmpHm.get("global")[i]).append(ConfigRecord.csvSeparatorOut);
					} else
						strB.append(ConfigRecord.csvSeparatorOut);
				}
				strB.append("\n");
				bf = bf.put(strB.toString().getBytes());
				if (bf.position() > positioWarning) {
					try {
						int read = -1;
						bf.flip();
						while (read > 0 || bf.hasRemaining()) {
							read = fc.write(bf);
						}
						bf = ByteBuffer.allocateDirect(bufferSize);
						bf = (ByteBuffer) bf.rewind();
						bf.clear();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			});
			bf.flip();
			int read = -1;
			while (read > 0 || bf.hasRemaining()) {
				read = fc.write(bf);
			}
			bf = ByteBuffer.allocateDirect(bufferSize);
			bf = (ByteBuffer) bf.rewind();
			bf.clear();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Compacted.
	 */
	public final void compacted() {
		// One File generated in Mono-Thread and sorted by date and Pivot
		// reusing existing variable

		String title = "DateTime" + ConfigRecord.csvSeparatorOut + "Pivot" + ConfigRecord.csvSeparatorOut;

		for (int i = 0; i < ParsingMain.nbVals; i++) {
			title += ParsingMain.values[i].getName() + "_Avg (" + ParsingMain.values[i].getUnit() + ")"
					+ ConfigRecord.csvSeparatorOut + ParsingMain.values[i].getName() + "_rates (" + "hits/s)"
					+ ConfigRecord.csvSeparatorOut + ParsingMain.values[i].getName() + "_Max ("
					+ ParsingMain.values[i].getUnit() + ")" + ConfigRecord.csvSeparatorOut
					+ ParsingMain.values[i].getName() + "_Min (" + ParsingMain.values[i].getUnit() + ")"
					+ ConfigRecord.csvSeparatorOut + ParsingMain.values[i].getName() + "_Sum ("
					+ ParsingMain.values[i].getUnit() + ")" + ConfigRecord.csvSeparatorOut
					+ ParsingMain.values[i].getName() + "_Count (" + "unit" + ")" + ConfigRecord.csvSeparatorOut;
			if (ParsingMain.hasDuration) {
				title += ParsingMain.values[i].getName() + "_Concurrent (" + "unit" + ")"
						+ ConfigRecord.csvSeparatorOut;
			}

		}

		try (FileOutputStream fos = new FileOutputStream(
				new File(repCsv + File.separator + "fl_All_compacted" + ".csv"), true);
				FileChannel fc = fos.getChannel();) {
			bf = ByteBuffer.allocateDirect(bufferSize);
			bf.clear();
			bf = bf.put((title + "\n").getBytes());

			ParsingMain.treeMap.forEach((time, hm) -> {
				hm.forEach((pivot, event) -> {
					// producer.onData(event);
					String strDate = ConfigRecord.sdfOut.format(new Date(event.getPeriod()));
					StringBuilder strB = new StringBuilder(strDate).append(ConfigRecord.csvSeparatorOut).append(pivot)
							.append(ConfigRecord.csvSeparatorOut);

					for (int i = 0; i < ParsingMain.nbVals; i++) {
						if (event.counts[i] != 0) {

							strB.append(event.averages[i].isNaN() ? "" : event.averages[i])
									.append(ConfigRecord.csvSeparatorOut)
									.append(event.rates[i].isNaN() ? "" : event.rates[i])
									.append(ConfigRecord.csvSeparatorOut)
									.append(event.maxs[i].isNaN() ? "" : event.maxs[i])
									.append(ConfigRecord.csvSeparatorOut)
									.append(event.mins[i].isNaN() ? "" : event.mins[i])
									.append(ConfigRecord.csvSeparatorOut)
									.append(event.sums[i].isNaN() ? "" : event.sums[i])
									.append(ConfigRecord.csvSeparatorOut).append(event.counts[i])
									.append(ConfigRecord.csvSeparatorOut);
							if (ParsingMain.hasDuration) {
								if (event.countParallels[i] == 0) {
									strB.append(ConfigRecord.csvSeparatorOut);
								} else {
									strB.append(event.countParallels[i]).append(ConfigRecord.csvSeparatorOut);
								}
							}
						} else {
							strB.append("").append(ConfigRecord.csvSeparatorOut).append("")
									.append(ConfigRecord.csvSeparatorOut).append("")
									.append(ConfigRecord.csvSeparatorOut).append("")
									.append(ConfigRecord.csvSeparatorOut).append("")
									.append(ConfigRecord.csvSeparatorOut).append("")
									.append(ConfigRecord.csvSeparatorOut);
							if (ParsingMain.hasDuration) {
								strB.append(ConfigRecord.csvSeparatorOut);
							}
						}
					}
					strB.append("\n");
					bf = bf.put(strB.toString().getBytes());
					if (bf.position() > GenerateCsvFile.positioWarning) {
						try {
							bf.flip();
							int nbRead = 1;
							// while (bf.hasRemaining()) {
							while (nbRead > 0 || bf.hasRemaining()) {
								nbRead = fos.getChannel().write(bf);
							}
							bf = ByteBuffer.allocateDirect(GenerateCsvFile.bufferSize);
							bf = (ByteBuffer) bf.rewind();
							bf.clear();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				});

			});
			try {

				int nbRead = 1;
				bf.flip();
				// while (bf.hasRemaining()) {
				while (nbRead > 0 || bf.hasRemaining()) {

					nbRead = fos.getChannel().write(bf);
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				bf.clear();
				fos.getChannel().close();
				fos.flush();

				fos.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Splited.
	 */
	public final void splited() {

		if (LogFouineurMain.jlProperties.getProperty("logFouineur.waitStrategy", "blockingWaitSrategy")
				.equals("blockingWaitSrategy")) {
			disruptor = new Disruptor<AggLogRecordEvent>(AggLogRecordEvent::new, ringBufferSize, threadFactory,
					ProducerType.SINGLE, new BlockingWaitStrategy());
			System.out.println("Generating CSV logFouineur.waitStrategy=blockingWaitSrategy");
		} else if (LogFouineurMain.jlProperties.getProperty("logFouineur.waitStrategy", "blockingWaitSrategy")
				.equals("sleepingWaitStrategy")) {
			disruptor = new Disruptor<AggLogRecordEvent>(AggLogRecordEvent::new, ringBufferSize, threadFactory,
					ProducerType.SINGLE, new SleepingWaitStrategy());
			System.out.println("Generating CSV logFouineur.waitStrategy=sleepingWaitStrategy");
		} else if (LogFouineurMain.jlProperties.getProperty("logFouineur.waitStrategy", "blockingWaitSrategy")
				.equals("yieldingWaitStrategy")) {
			disruptor = new Disruptor<AggLogRecordEvent>(AggLogRecordEvent::new, ringBufferSize, threadFactory,
					ProducerType.SINGLE, new YieldingWaitStrategy());
			System.out.println("Generating CSV logFouineur.waitStrategy=yieldingWaitStrategy");
		} else if (LogFouineurMain.jlProperties.getProperty("logFouineur.waitStrategy", "blockingWaitSrategy")
				.equals("busySpinWaitStrategy")) {
			disruptor = new Disruptor<AggLogRecordEvent>(AggLogRecordEvent::new, ringBufferSize, threadFactory,
					ProducerType.SINGLE, new BusySpinWaitStrategy());
			System.out.println("Generating CSV logFouineur.waitStrategy=busySpinWaitStrategy");
		} else {
			disruptor = new Disruptor<AggLogRecordEvent>(AggLogRecordEvent::new, ringBufferSize, threadFactory,
					ProducerType.SINGLE, new BlockingWaitStrategy());
			System.out.println("Generating CSV logFouineur.waitStrategy=blockingWaitSrategy");
		}
		String base = "DateTime" + ConfigRecord.csvSeparatorOut;
		threads = new AggLogRecordEventHandler[ParsingMain.nbPivots + 1];
		// Traitement Pivots
		System.out.println("ParsingMain.nbPivots =" + ParsingMain.nbPivots);
		for (int i = 0; i < ParsingMain.nbPivots; i++) {
			threads[i] = new AggLogRecordEventHandler();
			((AggLogRecordEventHandler) threads[i]).id = i;
			((AggLogRecordEventHandler) threads[i]).namePivot = ParsingMain.pivots[i].getName();
			((AggLogRecordEventHandler) threads[i]).treated = 0;
			try {
				bfs[i] = ByteBuffer.allocateDirect(bufferSize);
				foss[i] = new FileOutputStream(
						new File(repCsv + File.separator + "fl_" + ParsingMain.pivots[i].getName() + ".csv"), true);

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		AggLogRecordEventHandler global = new AggLogRecordEventHandler();

		// ((AggLogRecordEventHandler) threads[ParsingMain.nbPivots]).id =
		// ParsingMain.nbPivots;
		//
		// ((AggLogRecordEventHandler) threads[ParsingMain.nbPivots]).namePivot =
		// "global";

		global.id = ParsingMain.nbPivots;
		global.namePivot = "global";
		global.treated = 0;
		threads[ParsingMain.nbPivots] = global;

		// "global";
		try {
			foss[ParsingMain.nbPivots] = new FileOutputStream(
					new File(repCsv + File.separator + "fl_" + "global" + ".csv"), true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Generate all files

		for (int j = 0; j < ParsingMain.nbPivots; j++) {
			String title = base;

			for (int i = 0; i < ParsingMain.nbVals; i++) {
				title += ParsingMain.pivots[j].getName() + "_" + ParsingMain.values[i].getName() + "_Avg ("
						+ ParsingMain.values[i].getUnit() + ")" + ConfigRecord.csvSeparatorOut
						+ ParsingMain.pivots[j].getName() + "_" + ParsingMain.values[i].getName() + "_rates ("
						+ "hits/s)" + ConfigRecord.csvSeparatorOut + ParsingMain.pivots[j].getName() + "_"
						+ ParsingMain.values[i].getName() + "_Max (" + ParsingMain.values[i].getUnit() + ")"
						+ ConfigRecord.csvSeparatorOut + ParsingMain.pivots[j].getName() + "_"
						+ ParsingMain.values[i].getName() + "_Min (" + ParsingMain.values[i].getUnit() + ")"
						+ ConfigRecord.csvSeparatorOut + ParsingMain.pivots[j].getName() + "_"
						+ ParsingMain.values[i].getName() + "_Sum (" + ParsingMain.values[i].getUnit() + ")"
						+ ConfigRecord.csvSeparatorOut + ParsingMain.pivots[j].getName() + "_"
						+ ParsingMain.values[i].getName() + "_Count (" + "unit" + ")" + ConfigRecord.csvSeparatorOut;
				if (ParsingMain.hasDuration) {
					title += ParsingMain.pivots[j].getName() + "_" + ParsingMain.values[i].getName() + "_Concurrent ("
							+ "unit" + ")" + ConfigRecord.csvSeparatorOut;
				}
			}
			bfs[j] = ByteBuffer.allocateDirect(bufferSize);
			bfs[j].rewind();
			bfs[j].clear();
			bfs[j].put((title + "\n").getBytes());

		}
		// without pivot global values
		String title = base;
		for (int i = 0; i < ParsingMain.nbVals; i++) {
			title += "global_" + ParsingMain.values[i].getName() + "_Avg (" + ParsingMain.values[i].getUnit() + ")"
					+ ConfigRecord.csvSeparatorOut + "global_" + ParsingMain.values[i].getName() + "_rates ("
					+ "hits/s)" + ConfigRecord.csvSeparatorOut + "global_" + ParsingMain.values[i].getName() + "_Max ("
					+ ParsingMain.values[i].getUnit() + ")" + ConfigRecord.csvSeparatorOut + "global_"
					+ ParsingMain.values[i].getName() + "_Min (" + ParsingMain.values[i].getUnit() + ")"
					+ ConfigRecord.csvSeparatorOut + "global_" + ParsingMain.values[i].getName() + "_Sum ("
					+ ParsingMain.values[i].getUnit() + ")" + ConfigRecord.csvSeparatorOut + "global_"
					+ ParsingMain.values[i].getName() + "_Count (" + "unit" + ")" + ConfigRecord.csvSeparatorOut;
			if (ParsingMain.hasDuration) {
				title += "global_" + ParsingMain.values[i].getName() + "_Concurrent (" + "unit" + ")"
						+ ConfigRecord.csvSeparatorOut;
			}
		}
		bfs[ParsingMain.nbPivots] = ByteBuffer.allocateDirect(bufferSize);
		bfs[ParsingMain.nbPivots].rewind();
		bfs[ParsingMain.nbPivots].clear();

		bfs[ParsingMain.nbPivots].put((title + "\n").getBytes());

		disruptor.handleEventsWith(threads);

		// Start the Disruptor, starts all threads running
		RingBuffer<AggLogRecordEvent> ringBuffer = disruptor.start();

		// Get the ring buffer from the Disruptor to be used for publishing.
		// RingBuffer<AggLogRecordEvent> ringBuffer = disruptor.getRingBuffer();
		System.out.println("Bis ParsingMain.nbPivots  =" + ParsingMain.nbPivots);
		AggLogRecordEventProducer producer = new AggLogRecordEventProducer(ringBuffer);

		ParsingMain.treeMap.forEach((time, hm) -> {
			hm.forEach((pivot, event) -> {
				producer.onData(event);

			});
		});

		// Waiting for all Consumers to complete
		int loops = 0;
		while (true) {
			int totTreated = ((AggLogRecordEventHandler) threads[ParsingMain.nbPivots]).treated;
			// for (int i = 0; i < ParsingMain.nbPivots + 1; i++) {
			// totTreated += ((AggLogRecordEventHandler) threads[i]).treated;
			// }
			loops++;
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (totTreated == ParsingMain.treeMap.size()) {
				System.out.println("GenerateCsv waiting loops => " + loops + " ;  ParsingMain.treeMap.size() = "
						+ ParsingMain.treeMap.size() + " ; Total Treated=> " + totTreated);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}

		}

		disruptor.shutdown();

		// flush all file
		for (int i = 0; i < ParsingMain.nbPivots + 1; i++) {
			try {

				int nbRead = 1;
				// while (bfs[i].hasRemaining()) {
				bfs[i].flip();
				while (nbRead > 0 || bfs[i].hasRemaining()) {

					nbRead = foss[i].getChannel().write(GenerateCsvFile.bfs[i]);
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					foss[i].getChannel().close();
					foss[i].flush();
					foss[i].close();
					bfs[i].clear();
					bfs[i] = null;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}

}
