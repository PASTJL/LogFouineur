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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jlp.logfouineur.disruptor.LogRecordEvent;
import org.jlp.logfouineur.models.AggLogRecordEvent;
import org.jlp.logfouineur.records.ConfigRecord;

import com.lmax.disruptor.EventHandler;

// TODO: Auto-generated Javadoc
/**
 * The Class RecordEventHandlerDatedRecord.
 */
public class RecordEventHandlerDatedRecord implements EventHandler<LogRecordEvent> {

	/** The id. */
	public int id = 0;

	/** The local hm. */
	public HashMap<Long, HashMap<String, AggLogRecordEvent>> localHm = new HashMap<Long, HashMap<String, AggLogRecordEvent>>();

	// ParsingMain.allHmValues[id]= HashMap<Long, HashMap<String, AggLogRecord>>
	// the innerHashPap = HashMap<String,AggLogRecord> : the key is the Pivot of
	// the AggLogRecord. there is an extra pivot "global" that doesn't have any
	// regex for pivot

	/**
	 * On event.
	 *
	 * @param event the event
	 * @param sequence the sequence
	 * @param endOfBatch the end of batch
	 * @throws Exception the exception
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lmax.disruptor.EventHandler#onEvent(java.lang.Object, long, boolean)
	 */
	@Override
	public final void onEvent(LogRecordEvent event, long sequence, boolean endOfBatch) throws Exception {

		if (sequence % ParsingMain.nbThreads == id) {

			// System.out.println("Thread ="+id+" ; line ="+event.getContent());
			// Correcting with the tz textfield.
			if (ConfigRecord.decalAllDatesInMillis != 0)
				event = correctDate(event);

			String line = event.getContent();
			Date date = event.getDate();

			String[] pivotsMatched = getPivotsMatched(line);
			Long key = ((long) (date.getTime() / ConfigRecord.aggregPeriodInMillis))
					* ConfigRecord.aggregPeriodInMillis;

			HashMap<String, Double> hmValues = getValues(line);
			// if (null != hmValues && ParsingMain.allHmValues[id].containsKey(key)) {
			// HashMap<String, AggLogRecordEvent> innerHashMap =
			// ParsingMain.allHmValues[id].get(key);
			if (null != hmValues && localHm.containsKey(key)) {
				HashMap<String, AggLogRecordEvent> innerHashMap = localHm.get(key);

				if (null != pivotsMatched) {
					for (String piv : pivotsMatched) {
						AggLogRecordEvent tmpALR = innerHashMap.getOrDefault(piv,
								new AggLogRecordEvent(piv, key, ParsingMain.nbVals));
						for (int i = 0; i < ParsingMain.nbVals; i++) {
							String strVal = ParsingMain.values[i].getName();
							if (!hmValues.get(strVal).isNaN()) {
							tmpALR.counts[i] += 1;

							if (! tmpALR.sums[i].isNaN() && 0d != tmpALR.sums[i]) {
								tmpALR.sums[i] += hmValues.get(strVal);
							} else {
								tmpALR.sums[i] = hmValues.get(strVal);
							}

							tmpALR.maxs[i] = Math.max(tmpALR.maxs[i], hmValues.get(strVal));
							tmpALR.mins[i] = Math.min(tmpALR.mins[i], hmValues.get(strVal));
							if (ParsingMain.values[i].isIsDuration()) {
								tmpALR.isDurations[i] = true;
								tmpALR.countParallels[i] = tmpALR.counts[i];
								if (hmValues.get(strVal)
										* ParsingMain.factorValueMs[i] > ConfigRecord.aggregPeriodInMillis) {
									tmpALR.countsDurationSupPeriod[i] += 1;
									tmpALR.sumsDurationSupPeriod[i] += hmValues.get(strVal);
								}
							} else {
								tmpALR.isDurations[i] = false;
							}
						}
						}
						
						innerHashMap.put(piv, tmpALR);

					}
				}
				// adding global
				// AggLogRecordEvent tmpALR = new AggLogRecordEvent("global",
				// key, ParsingMain.nbVals);
				AggLogRecordEvent tmpALR = innerHashMap.getOrDefault("global",
						new AggLogRecordEvent("global", key, ParsingMain.nbVals));

				for (int i = 0; i < ParsingMain.nbVals; i++) {

					String strVal = ParsingMain.values[i].getName();
					if (! hmValues.get(strVal).isNaN()) {
					tmpALR.counts[i] += 1;

					if (0d != tmpALR.sums[i] && !tmpALR.sums[i].isNaN()) {
						tmpALR.sums[i] += hmValues.get(strVal);
					} else {
						tmpALR.sums[i] = hmValues.get(strVal);
					}

					tmpALR.maxs[i] = Math.max(tmpALR.maxs[i], hmValues.get(strVal));
					tmpALR.mins[i] = Math.min(tmpALR.mins[i], hmValues.get(strVal));
					if (ParsingMain.values[i].isIsDuration()) {
						tmpALR.countParallels[i] = tmpALR.counts[i];
						tmpALR.isDurations[i] = true;
						if (hmValues.get(strVal) * ParsingMain.factorValueMs[i] > ConfigRecord.aggregPeriodInMillis) {
							tmpALR.countsDurationSupPeriod[i] += 1;
							tmpALR.sumsDurationSupPeriod[i] += hmValues.get(strVal);
						}
					} else {
						tmpALR.isDurations[i] = false;
					}
				}
				}

				innerHashMap.put("global", tmpALR);
				// ParsingMain.allHmValues[id].put(key, innerHashMap);
				localHm.put(key, innerHashMap);
				// } else if (null != hmValues && !ParsingMain.allHmValues[id].containsKey(key))
				// {
			} else if (null != hmValues && !localHm.containsKey(key)) {
				HashMap<String, AggLogRecordEvent> innerHashMap = null;
				if (null != pivotsMatched) {
					innerHashMap = new HashMap<String, AggLogRecordEvent>(ParsingMain.nbPivots + 1);
					for (String piv : pivotsMatched) {
						AggLogRecordEvent tmpALR = new AggLogRecordEvent(piv, key, ParsingMain.nbVals);
						for (int i = 0; i < ParsingMain.nbVals; i++) {
							String strVal = ParsingMain.values[i].getName();
							if (! hmValues.get(strVal).isNaN()) {
							tmpALR.counts[i] += 1;
							if (0d != tmpALR.sums[i] && ! tmpALR.sums[i].isNaN()) {
								tmpALR.sums[i] += hmValues.get(strVal);
							} else {
								tmpALR.sums[i] = hmValues.get(strVal);
							}
							tmpALR.maxs[i] = Math.max(tmpALR.maxs[i], hmValues.get(strVal));
							tmpALR.mins[i] = Math.min(tmpALR.mins[i], hmValues.get(strVal));
							if (ParsingMain.values[i].isIsDuration()) {
								tmpALR.countParallels[i] = tmpALR.counts[i];
								tmpALR.isDurations[i] = true;
								if (hmValues.get(strVal)
										* ParsingMain.factorValueMs[i] > ConfigRecord.aggregPeriodInMillis) {
									tmpALR.countsDurationSupPeriod[i] += 1;
									tmpALR.sumsDurationSupPeriod[i] += hmValues.get(strVal);
								}
							} else {
								tmpALR.isDurations[i] = false;
							}
						}
						}
						innerHashMap.put(piv, tmpALR);

					}
				} else {
					innerHashMap = new HashMap<String, AggLogRecordEvent>(ParsingMain.nbPivots + 1);
				}
				// adding global
				AggLogRecordEvent tmpALR = new AggLogRecordEvent("global", key, ParsingMain.nbVals);

				for (int i = 0; i < ParsingMain.nbVals; i++) {
					String strVal = ParsingMain.values[i].getName();
					if (!hmValues.get(strVal).isNaN()) {
						tmpALR.counts[i] += 1;

						if (0d != tmpALR.sums[i] && Double.NaN != tmpALR.sums[i])
							tmpALR.sums[i] += hmValues.get(strVal);
						else
							tmpALR.sums[i] = hmValues.get(strVal);

						tmpALR.maxs[i] = Math.max(tmpALR.maxs[i], hmValues.get(strVal));
						tmpALR.mins[i] = Math.min(tmpALR.mins[i], hmValues.get(strVal));
						if (ParsingMain.values[i].isIsDuration()) {
							tmpALR.isDurations[i] = true;
							tmpALR.countParallels[i] = tmpALR.counts[i];
							if (hmValues.get(strVal)
									* ParsingMain.factorValueMs[i] > ConfigRecord.aggregPeriodInMillis) {
								tmpALR.countsDurationSupPeriod[i] += 1;
								tmpALR.sumsDurationSupPeriod[i] += hmValues.get(strVal);
							}
						} else {
							tmpALR.isDurations[i] = false;
						}
					}
				}
				innerHashMap.put("global", tmpALR);
				// ParsingMain.allHmValues[id].put(key, innerHashMap);
				localHm.put(key, innerHashMap);
			}
			
			ParsingMain.tabTreated[id] += 1;
		}

	}

	/**
	 * Correct date.
	 *
	 * @param event
	 *            the event
	 * @return the log record event
	 */
	private final LogRecordEvent correctDate(LogRecordEvent event) {
		Date oldDate = event.getDate();
		Date newDate = new Date(oldDate.getTime() + ConfigRecord.decalAllDatesInMillis);
		event.setDate(newDate);
		return event;
	}

	/**
	 * Gets the values.
	 *
	 * @param line
	 *            the line
	 * @return the values
	 */
	private final HashMap<String, Double> getValues(String line) {
		HashMap<String, Double> retHm = new HashMap<String, Double>(ParsingMain.nbVals);
		for (int i = 0; i < ParsingMain.nbVals; i++) {
			switch (ParsingMain.valueRegex[i]) {
			case 0:
				// TO DO with plugin
				String strRegex2 = "";
				Double ret = Double.NaN;
				if (null != ParsingMain.values[i].getRegex2())
					strRegex2 = ParsingMain.values[i].getRegex2();

				String nomClasse = "plugins." + ParsingMain.values[i].getRegex1().split("=")[1];
				if (null != strRegex2) {
					// First Parameter is the string separator
					// String sep = strRegex2.substring(0, 1);
					// String[] params = strRegex2.substring(1).split(sep);
					// String[] allParams = new String[params.length + 1];
					// allParams[0] = line;
					//
					// for (int j = 0; j < params.length; j++) {
					// allParams[j + 1] = params[j];
					// }
					Method met = ParsingMain.hmapsMethod[id].get(nomClasse + "_" + i);
					Object obj = ParsingMain.hmapsClass[id].get(nomClasse + "_" + i);
				

					try {
						ret = (Double) met.invoke(obj, new Object[] { line });
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (null != ret && !ret.isNaN()) {
						retHm.put(ParsingMain.values[i].getName(), ret * ParsingMain.values[i].getScale());

					} else {
						retHm.put(ParsingMain.values[i].getName(), Double.NaN);
					}

				}
				break;

			case 1:
				String regex1 = ParsingMain.values[i].getRegex1();
				Matcher match1 = Pattern.compile(regex1).matcher(line);
				Double prsDouble = Double.NaN;
				if (match1.find()) {
					String extract = match1.group().trim().replaceAll(",", ".");
					
					try {
						prsDouble = Double.parseDouble(extract) * ParsingMain.values[i].getScale();
					} catch (NumberFormatException nfe) {
						prsDouble = Double.NaN;
					}
					
				}
				retHm.put(ParsingMain.values[i].getName(), prsDouble);
				break;
			case 2:

				regex1 = ParsingMain.values[i].getRegex1();
				prsDouble = Double.NaN;
				String regex2 = ParsingMain.values[i].getRegex2();
				match1 = Pattern.compile(regex1).matcher(line);
				if (match1.find()) {

					String extract = match1.group();
					Matcher match2 = Pattern.compile(regex2).matcher(extract);
					if (match2.find()) {

						String extract2 = match2.group().trim().replaceAll(",", ".");
						
						try {
							prsDouble = Double.parseDouble(extract2) * ParsingMain.values[i].getScale();

						} catch (NumberFormatException nfe) {
							prsDouble = Double.NaN;
						}
						
					}
				}
				retHm.put(ParsingMain.values[i].getName(), prsDouble);
				break;
			}

		}
		if (!retHm.keySet().isEmpty())
			return retHm;
		else
			return null;
	}

	/**
	 * Sets the id.
	 *
	 * @param id
	 *            the new id
	 */
	public void setId(int id) {
		this.id = id;

		ParsingMain.allHmValues[id].clear();
	}

	/**
	 * Gets the pivots matched.
	 *
	 * @param line
	 *            the line
	 * @return the pivots matched
	 */
	private final String[] getPivotsMatched(String line) {
		StringBuilder namesPivots = new StringBuilder("");

		for (int i = 0; i < ParsingMain.nbPivots; i++) {
			switch (ParsingMain.pivotRegex[i]) {
			case 1:
				String regex1 = ParsingMain.pivots[i].getRegex1();
				Matcher match1 = Pattern.compile(regex1).matcher(line);
				if (match1.find()) {
					namesPivots.append(ParsingMain.pivots[i].getName() + ";");
				}
				break;
			case 2:
				String regex2 = ParsingMain.pivots[i].getRegex2();
				regex1 = ParsingMain.pivots[i].getRegex1();
				match1 = Pattern.compile(regex1).matcher(line);
				if (match1.find()) {
					String extract = match1.group();
					Matcher match2 = Pattern.compile(regex2).matcher(extract);
					if (match2.find()) {
						namesPivots.append(ParsingMain.pivots[i].getName() + ";");
					}
				}
				break;

			}
			if (!ConfigRecord.boolExhaustiveParsing && namesPivots.length() > 0) {

				break;
			}
		}
		// System.out.println("Thread : "+id+";namesPivots=" + namesPivots);
		if (namesPivots.length() > 0)
			return namesPivots.toString().split(";");
		else
			return null;
	}

}
