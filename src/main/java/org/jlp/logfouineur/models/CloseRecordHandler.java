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
package org.jlp.logfouineur.models;

import java.util.HashMap;

import org.jlp.logfouineur.disruptor.ParsingMain;
import org.jlp.logfouineur.ui.LogFouineurFill;
import org.jlp.logfouineur.ui.LogFouineurMain;

// TODO: Auto-generated Javadoc
/**
 * The Class CloseRecordHandler.
 */
public class CloseRecordHandler implements Runnable {

	/** The id thread. */
	private final int idThread;

	/**
	 * Instantiates a new close record handler.
	 *
	 * @param id
	 *            the id
	 */
	public CloseRecordHandler(int id) {
		this.idThread = id;
	}

	/**
	 * Run.
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		//if (LogFouineurFill.cbCorrectDate.getSelectionModel().getSelectedItem().equals("0")) {
		if (LogFouineurMain.currentScenarioProps.getProperty("logFouineur.correctDate","0").equals("0")){
			closeEnrInHm(ParsingMain.allHmValues[idThread]);
		} else {
			// Duration must be the first value on the table
			Long firstKey = ParsingMain.allHmValues[idThread].keySet().iterator().next();
			String firstKeyAgg= ParsingMain.allHmValues[idThread].get(firstKey).keySet().iterator().next();
				System.out.println("cle record = "+ firstKeyAgg);
			boolean hasDuration = ParsingMain.allHmValues[idThread].get(firstKey).get(firstKeyAgg).isDurations[0];
			if (!hasDuration) {
				closeEnrInHm(ParsingMain.allHmValues[idThread]);
			} else {
				closeEnrInHm(ParsingMain.allHmValues[idThread],
						LogFouineurMain.currentScenarioProps.getProperty("logFouineur.correctDate","0"));
					//	LogFouineurFill.cbCorrectDate.getSelectionModel().getSelectedItem());
			}
		}
	}

	/**
	 * Close enr in hm.
	 *
	 * @param hashMap the hash map
	 * @param selectedItem the selected item
	 */
	private void closeEnrInHm(HashMap<Long, HashMap<String, AggLogRecordEvent>> hashMap, String selectedItem) {
		HashMap<Long, HashMap<String, AggLogRecordEvent>> tmphmGlobal = new HashMap<Long, HashMap<String, AggLogRecordEvent>>();
		System.out.println("Correcting date with : " + selectedItem);
		switch (selectedItem) {
		case "+1":

			hashMap.forEach((key, hm) -> {
				hm.forEach((str, record) -> {

					record.closeEnr();
					record.period += record.averages[0];
					hm.put(str, record);
					if (!tmphmGlobal.containsKey(record.period)) {
						tmphmGlobal.put(record.period, hm);
					} else {
						AggLogRecordEvent toMerge = tmphmGlobal.get(record.period).getOrDefault(str, null);
						if (null != toMerge) {
							record = record.merge(toMerge);
							hm.put(str, record);
							tmphmGlobal.put(record.period, hm);
						}

					}
				});

			});
			break;
		case "-1":
			hashMap.forEach((key, hm) -> {
				hm.forEach((str, record) -> {

					record.closeEnr();
					record.period -= record.averages[0];
					hm.put(str, record);
					if (!tmphmGlobal.containsKey(record.period)) {
						tmphmGlobal.put(record.period, hm);
					} else {
						AggLogRecordEvent toMerge = tmphmGlobal.get(record.period).getOrDefault(str, null);
						if (null != toMerge) {
							record = record.merge(toMerge);
							hm.put(str, record);
							tmphmGlobal.put(record.period, hm);
						}

					}
				});

			});

			break;
		}
		ParsingMain.allHmValues[idThread] = tmphmGlobal;
	}

	/**
	 * Close enr in hm.
	 *
	 * @param hashMap
	 *            the hash map
	 */
	private final void closeEnrInHm(HashMap<Long, HashMap<String, AggLogRecordEvent>> hashMap) {
		// if the first val is a duration and decal !=0
		hashMap.forEach((key, hm) -> {
			hm.forEach((str, record) -> {
				record.closeEnr();
				hm.put(str, record);

			});
			hashMap.put(key, hm);
		});

	}
}
