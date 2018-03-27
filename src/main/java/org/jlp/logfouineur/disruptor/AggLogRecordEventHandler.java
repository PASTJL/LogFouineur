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

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.Date;

import org.jlp.logfouineur.models.AggLogRecordEvent;
import org.jlp.logfouineur.records.ConfigRecord;
import org.jlp.logfouineur.ui.LogFouineurMain;

import com.lmax.disruptor.EventHandler;

// TODO: Auto-generated Javadoc
/**
 * The Class AggLogRecordEventHandler.
 */
public class AggLogRecordEventHandler implements EventHandler<AggLogRecordEvent> {
	
	/** The treated. */
	public int treated = 0;

	/** The name pivot. */
	public String namePivot = "global";

	/** The id. */
	public int id = -1;

	/**
	 * On event.
	 *
	 * @param event the event
	 * @param sequence the sequence
	 * @param endOfBatch the end of batch
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lmax.disruptor.EventHandler#onEvent(java.lang.Object, long, boolean)
	 */
	public final void onEvent(AggLogRecordEvent event, long sequence, boolean endOfBatch) {
		// TODO Auto-generated method stub
		// System.out.println("Beginning treating namePivot="+namePivot);
		// System.out.println("Receiving event.namePivot=|"+event.namePivot+"|");
		// System.out.println("Receiving event.event.nbVals=|"+event.nbVals+"|");
		if (event.namePivot.equals(namePivot)) {

			String strDate = ConfigRecord.sdfOut.format(new Date(event.getPeriod()));
			StringBuilder strB = new StringBuilder();
			strB = new StringBuilder(strDate).append(ConfigRecord.csvSeparatorOut);

			for (int i = 0; i < ParsingMain.nbVals; i++) {
				// title += ParsingMain.pivots[i].getName() + "_" +
				// ParsingMain.values[i].getName() + "_Avg ("
				// + ParsingMain.values[i].getUnit() + ")" + ConfigRecord.csvSeparatorOut
				// + ParsingMain.pivots[i].getName() + "_" + ParsingMain.values[i].getName() +
				// "_Max ("
				// + ParsingMain.values[i].getUnit() + ")" + ConfigRecord.csvSeparatorOut
				// + ParsingMain.pivots[i].getName() + "_" + ParsingMain.values[i].getName() +
				// "_Min ("
				// + ParsingMain.values[i].getUnit() + ")" + ConfigRecord.csvSeparatorOut
				// + ParsingMain.pivots[i].getName() + "_" + ParsingMain.values[i].getName() +
				// "_Sum ("
				// + ParsingMain.values[i].getUnit() + ")" + ConfigRecord.csvSeparatorOut
				// + ParsingMain.pivots[i].getName() + "_" + ParsingMain.values[i].getName() +
				// "_Count ("
				// + "unit" + ")" + ConfigRecord.csvSeparatorOut +
				// ParsingMain.pivots[i].getName() + "_"
				// + ParsingMain.values[i].getName() + "_Concurrent (" + "unit" + ")"
				// + ConfigRecord.csvSeparatorOut;

				if (event.counts[i] > 0) {
					strB.append(event.averages[i].isNaN() ? "" : event.averages[i]).append(ConfigRecord.csvSeparatorOut)
							.append(event.rates[i].isNaN() ? "" : event.rates[i]).append(ConfigRecord.csvSeparatorOut)
							.append(event.maxs[i].isNaN() ? "" : event.maxs[i]).append(ConfigRecord.csvSeparatorOut)
							.append(event.mins[i].isNaN() ? "" : event.mins[i]).append(ConfigRecord.csvSeparatorOut)
							.append(event.sums[i].isNaN() ? "" : event.sums[i]).append(ConfigRecord.csvSeparatorOut)
							.append(event.counts[i]).append(ConfigRecord.csvSeparatorOut);
					if (ParsingMain.hasDuration) {
						if (event.countParallels[i] == 0) {
							strB.append(ConfigRecord.csvSeparatorOut);
						} else {
							strB.append(event.countParallels[i]).append(ConfigRecord.csvSeparatorOut);
						}
					}
				} else {
					strB.append("").append(ConfigRecord.csvSeparatorOut).append("").append(ConfigRecord.csvSeparatorOut)
							.append("").append(ConfigRecord.csvSeparatorOut).append("")
							.append(ConfigRecord.csvSeparatorOut).append("").append(ConfigRecord.csvSeparatorOut)
							.append("").append(ConfigRecord.csvSeparatorOut);
					if (ParsingMain.hasDuration) {
						strB.append(ConfigRecord.csvSeparatorOut);
					}
				}
			}

			strB.append("\n");
			// System.out.println("Pos 1 treating namePivot="+namePivot);
			while (null == GenerateCsvFile.bfs[id]) {
				System.out.println("GenerateCsvFile.bfs[" + id + "] is null reallocate it bufferSize="
						+ GenerateCsvFile.bufferSize);
				GenerateCsvFile.bfs[id] = ByteBuffer.allocateDirect(GenerateCsvFile.bufferSize);
				GenerateCsvFile.bfs[id] = GenerateCsvFile.bfs[id].rewind();
				GenerateCsvFile.bfs[id] = GenerateCsvFile.bfs[id].clear();
			}
			if (null != GenerateCsvFile.bfs[id]) {
				// System.out.println("Buffer position/warning =>
				// "+GenerateCsvFile.bfs[id].position()+"/" +GenerateCsvFile.positioWarning);
				try {
					GenerateCsvFile.bfs[id] = GenerateCsvFile.bfs[id].put(strB.toString().getBytes());
				} catch (BufferOverflowException bov) {
					System.out.println("strB.toString().getBytes().length => " + strB.toString().length());
					System.out.println("Buffer position/warning => " + GenerateCsvFile.bfs[id].position() + "/"
							+ GenerateCsvFile.positioWarning);
					bov.printStackTrace();
				}
				// System.out.println("Buffer position/warning =>
				// "+GenerateCsvFile.bfs[id].position()+"/" +GenerateCsvFile.positioWarning);
				if (GenerateCsvFile.bfs[id].position() > GenerateCsvFile.positioWarning) {
					try {
						GenerateCsvFile.bfs[id].flip();
						int nbRead = 1;
						// while (GenerateCsvFile.bfs[id].hasRemaining()) {
						while (nbRead > 0 || GenerateCsvFile.bfs[id].hasRemaining()) {
							nbRead = GenerateCsvFile.foss[id].getChannel().write(GenerateCsvFile.bfs[id]);
						}

						GenerateCsvFile.bfs[id] = ByteBuffer.allocateDirect(GenerateCsvFile.bufferSize);
						GenerateCsvFile.bfs[id].rewind();
						GenerateCsvFile.bfs[id].clear();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// System.out.println("Pos 2 treating namePivot="+namePivot);

			} else {
				if (LogFouineurMain.isDebug) {
					LogFouineurMain.debug("AggLogRecordEvent onEvent  strB.toString().getBytes() => something is null"
							+ System.lineSeparator());
					LogFouineurMain.debug(
							"AggLogRecordEvent onEvent strB.toString() =\"+ strB.toString()" + System.lineSeparator());
					LogFouineurMain.debug("AggLogRecordEvent onEvent GenerateCsvFile.bfs[id] ="
							+ GenerateCsvFile.bfs[id].toString() + System.lineSeparator());

				}

			}
			treated++;
		}

	}

}
