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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jlp.logfouineur.models.AggLogRecordEvent;
import org.jlp.logfouineur.models.JFXPivot;
import org.jlp.logfouineur.models.JFXValue;
import org.jlp.logfouineur.records.ConfigRecord;
import org.jlp.logfouineur.records.RecordReader;
import org.jlp.logfouineur.ui.LogFouineurMain;

import com.lmax.disruptor.EventHandler;

// TODO: Auto-generated Javadoc
/**
 * The Class RecordEventHandlerNonDatedRecord.
 */
public class RecordEventHandlerNonDatedRecord implements EventHandler<LogRecordEvent> {

	/** The id. */
	public int id = 0;

	/** The local hm. */
	public HashMap<Long, HashMap<String, AggLogRecordEvent>> localHm = new HashMap<Long, HashMap<String, AggLogRecordEvent>>();
	// ParsingMain.allHmValues[id]= HashMap<Long, HashMap<String, AggLogRecord>>
	// the innerHashPap = HashMap<String,AggLogRecord> : the key is the Pivot of
	// the AggLogRecord. there is an extra pivot "global" that doesn't have any
	// regex for pivot

	/** The aggreg period in millis. */
	public long aggregPeriodInMillis;

	/** The nb vals. */
	public int nbVals = 1;

	/** The decal all dates in millis. */
	public long decalAllDatesInMillis = 0;

	/** The values. */
	public JFXValue[] values = null;

	/** The factor value ms. */
	public double factorValueMs[];

	/** The date end of parsing. */
	public Date dateBeginOfParsing, dateEndOfParsing;

	/** The regex include. */
	public Pattern regexDateRecord, regexExclude, regexInclude, gapRegex;

	/** The abs rel. */
	public String absRel = "ABS";
	/** The sdf. */
	public SimpleDateFormat sdf;

	/** The factor date in millis. */
	public double factorDateInMillis;

	/** The origin date. */
	public Date originDate;

	/** The is date in millis. */
	public boolean boolExplicitDate, isDateInMillis;

	/** The implicit step. */
	public String implicitStep = "true";

	/** The step in millis. */
	public long stepInMillis = 0L;

	/** The factor to millis. */
	public double factorToMillis = 1;

	/** The value regex. */
	// ParsingMain.valueRegex[i]
	public int[] valueRegex;

	/** The hmaps class. */
	public HashMap<String, Object>[] hmapsClass = null; // a hashMap by

	/** The hmaps method. */
	// thread
	public HashMap<String, Method>[] hmapsMethod = null;

	/** The nb pivots. */
	public int nbPivots = 1;

	/** The pivot regex. */
	public int[] pivotRegex;// ParsingMain.pivotRegex[i]) ;

	/** The pivots. */
	// ParsingMain.pivots[i]
	public JFXPivot[] pivots;

	/** The bool exhaustive parsing. */
	public boolean boolExhaustiveParsing = true;

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

			event = getDatedEvent(event);

			if (null != event) {
				// System.out.println("Thread ="+id+" ; line
				// ="+event.getContent());
				// Correcting with the tz textfield.
				if (decalAllDatesInMillis != 0)
					event = correctDate(event);
				String line = event.getContent();
				Date date = event.getDate();
				String[] pivotsMatched = getPivotsMatched(line);
				Long key = ((long) (date.getTime() / aggregPeriodInMillis)) * aggregPeriodInMillis;
				HashMap<String, Double> hmValues = getValues(line);

				if (null != hmValues && localHm.containsKey(key)) {
					HashMap<String, AggLogRecordEvent> innerHashMap = localHm.get(key);
					if (null != pivotsMatched) {
						for (String piv : pivotsMatched) {
							AggLogRecordEvent tmpALR = innerHashMap.getOrDefault(piv,
									new AggLogRecordEvent(piv, key, nbVals));
							for (int i = 0; i < nbVals; i++) {
								String strVal = values[i].getName();
								if (!hmValues.get(strVal).isNaN()) {
									tmpALR.counts[i]++;
									if (!tmpALR.sums[i].isNaN())
										tmpALR.sums[i] += hmValues.get(strVal);
									else
										tmpALR.sums[i] = hmValues.get(strVal);
									tmpALR.maxs[i] = Math.max(tmpALR.maxs[i], hmValues.get(strVal));
									tmpALR.mins[i] = Math.min(tmpALR.mins[i], hmValues.get(strVal));
									if (ParsingMain.values[i].isIsDuration()) {
										tmpALR.isDurations[i] = true;
										tmpALR.countParallels[i] = tmpALR.counts[i];
										if (hmValues.get(strVal) * factorValueMs[i] > aggregPeriodInMillis) {
											tmpALR.countsDurationSupPeriod[i]++;
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
					AggLogRecordEvent tmpALR = innerHashMap.getOrDefault("global",
							new AggLogRecordEvent("global", key, ParsingMain.nbVals));

					for (int i = 0; i < nbVals; i++) {

						String strVal = values[i].getName();

						if (!hmValues.get(strVal).isNaN()) {
							tmpALR.counts[i]++;
							if (!tmpALR.sums[i].isNaN() && 0d != tmpALR.sums[i]) {
								tmpALR.sums[i] += hmValues.get(strVal);
							} else
								tmpALR.sums[i] = hmValues.get(strVal);

							tmpALR.maxs[i] = Math.max(tmpALR.maxs[i], hmValues.get(strVal));
							tmpALR.mins[i] = Math.min(tmpALR.mins[i], hmValues.get(strVal));
							if (ParsingMain.values[i].isIsDuration()) {
								tmpALR.isDurations[i] = true;
								tmpALR.countParallels[i] = tmpALR.counts[i];
								if (hmValues.get(strVal) * factorValueMs[i] > aggregPeriodInMillis) {
									tmpALR.countsDurationSupPeriod[i]++;
									tmpALR.sumsDurationSupPeriod[i] += hmValues.get(strVal);
								}
							} else {
								tmpALR.isDurations[i] = false;
							}
						}
					}
					innerHashMap.put("global", tmpALR);

					localHm.put(key, innerHashMap);

				} else if (null != hmValues && !localHm.containsKey(key)) {
					HashMap<String, AggLogRecordEvent> innerHashMap = null;
					if (null != pivotsMatched) {
						innerHashMap = new HashMap<String, AggLogRecordEvent>(pivotsMatched.length + 1);
						for (String piv : pivotsMatched) {
							AggLogRecordEvent tmpALR = new AggLogRecordEvent(piv, key, nbVals);
							for (int i = 0; i < nbVals; i++) {
								String strVal = values[i].getName();

								if (!hmValues.get(strVal).isNaN()) {
									tmpALR.counts[i]++;
									if (!tmpALR.sums[i].isNaN())
										tmpALR.sums[i] += hmValues.get(strVal);
									else
										tmpALR.sums[i] = hmValues.get(strVal);
									tmpALR.maxs[i] = Math.max(tmpALR.maxs[i], hmValues.get(strVal));
									tmpALR.mins[i] = Math.min(tmpALR.mins[i], hmValues.get(strVal));
									if (values[i].isIsDuration()) {
										tmpALR.isDurations[i] = true;
										tmpALR.countParallels[i] = tmpALR.counts[i];
										if (hmValues.get(strVal) * factorValueMs[i] > aggregPeriodInMillis) {
											tmpALR.countsDurationSupPeriod[i]++;
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
						innerHashMap = new HashMap<String, AggLogRecordEvent>(1);
					}
					// adding global
					AggLogRecordEvent tmpALR = new AggLogRecordEvent("global", key, nbVals);
					for (int i = 0; i < nbVals; i++) {
						String strVal = values[i].getName();

						if (!hmValues.get(strVal).isNaN()) {
							tmpALR.counts[i]++;
							if (!tmpALR.sums[i].isNaN() && 0d != tmpALR.sums[i])
								tmpALR.sums[i] += hmValues.get(strVal);
							else
								tmpALR.sums[i] = hmValues.get(strVal);
							tmpALR.maxs[i] = Math.max(tmpALR.maxs[i], hmValues.get(strVal));
							tmpALR.mins[i] = Math.min(tmpALR.mins[i], hmValues.get(strVal));
							if (values[i].isIsDuration()) {
								tmpALR.isDurations[i] = true;
								tmpALR.countParallels[i] = tmpALR.counts[i];
								if (hmValues.get(strVal) * factorValueMs[i] > aggregPeriodInMillis) {
									tmpALR.countsDurationSupPeriod[i]++;
									tmpALR.sumsDurationSupPeriod[i] += hmValues.get(strVal);
								}
							} else {
								tmpALR.isDurations[i] = false;
							}
						}
					}
					innerHashMap.put("global", tmpALR);

					localHm.put(key, innerHashMap);
				}
				
			}

			ParsingMain.tabTreated[id] = ParsingMain.tabTreated[id] + 1;
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
		Date newDate = new Date(oldDate.getTime() + decalAllDatesInMillis);
		event.setDate(newDate);
		return event;
	}

	/**
	 * Date is between.
	 *
	 * @param date
	 *            the date
	 * @return true, if successful
	 */
	private final boolean dateIsBetween(Date date) {
		if (null == date)
			return false;
		if (date.after(dateBeginOfParsing) && date.before(dateEndOfParsing))
			return true;
		return false;
	}

	/**
	 * Gets the date record explicit date with java format.
	 *
	 * @param line
	 *            the line
	 * @return the date record explicit date with java format
	 */
	private final Date getDateRecordExplicitDateWithJavaFormat(String line) {

		Matcher matcher = regexDateRecord.matcher(line);
		if (matcher.find()) {
			String extract = matcher.group();
			try {
				// return new
				// SimpleDateFormat(ConfigRecord.javaDateFormatRecord).parse(extract);--
				return sdf.parse(extract);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * Gets the date record explicit date with date in millis.
	 *
	 * @param line
	 *            the line
	 * @return the date record explicit date with date in millis
	 */
	private final Date getDateRecordExplicitDateWithDateInMillis(String line) {
		Matcher matcher = regexDateRecord.matcher(line);
		if (matcher.find()) {
			String extract = matcher.group();
			Double gapDateInMillisDbl = Double.valueOf(extract) * factorDateInMillis;
			long gapDateInMillis = gapDateInMillisDbl.longValue();

			long fullTime = gapDateInMillis + originDate.getTime();
			return new Date(fullTime);

		}
		return null;
	}

	/**
	 * Validate exclude include.
	 *
	 * @param contentRecord
	 *            the content record
	 * @return true, if successful
	 */
	// include Exclude
	private final boolean validateExcludeInclude(String contentRecord) {

		Matcher matcher = regexExclude.matcher(contentRecord);
		if (matcher.find()) {

			return false;
		} else {
			Matcher matcher2 = regexInclude.matcher(contentRecord);
			if (matcher2.find()) {
				return true;
			} else
				return false;
		}

	}

	/**
	 * Validate include.
	 *
	 * @param contentRecord
	 *            the content record
	 * @return true, if successful
	 */
	private final boolean validateInclude(String contentRecord) {

		Matcher matcher3 = regexInclude.matcher(contentRecord);
		if (matcher3.find()) {
			return true;
		} else
			return false;

	}

	/**
	 * Validate exclude.
	 *
	 * @param contentRecord
	 *            the content record
	 * @return true, if successful
	 */
	private final boolean validateExclude(String contentRecord) {

		Matcher matcher = regexExclude.matcher(contentRecord);
		if (matcher.find()) {
			return false;
		}
		return true;

	}

	/**
	 * Gets the dated event.
	 *
	 * @param event
	 *            the event
	 * @return the dated event
	 */
	private final LogRecordEvent getDatedEvent(LogRecordEvent event) {
		// Validate include/exclude

		if (mustITakeIt(event)) {
			if (boolExplicitDate) {
				if (isDateInMillis) {
					Date date = getDateRecordExplicitDateWithDateInMillis(event.getContent());
					if (dateIsBetween(date)) {
						event.setDate(date);
						return event;
					} else
					{
						RecordReader.nbLinesEliminated++;
						RecordReader.nbLinesFiltered--;
						return null;
					}
				} else {
					Date date = getDateRecordExplicitDateWithJavaFormat(event.getContent());
					if (dateIsBetween(date)) {
						event.setDate(date);
						return event;
					} else
					{
						RecordReader.nbLinesFiltered--;
						RecordReader.nbLinesEliminated++;
						return null;
					}
				}
			} else { // implicitDate

				Date date = null;

				switch (implicitStep) {
				case "true":

					date = getDateRecordExplicitDateConstantStep(event.getContent(), stepInMillis);
					event.setDate(date);

					break;

				case "false":

					switch (this.absRel) {
					case "REL":
						date = getDateRecordExplicitDateVariableStepREL(event.getContent(), factorToMillis);
						if (null == date) {
							LogFouineurMain.errorFormatDate = "Error DateFormat in RecordEventHandlerNonDatedRecord.getDateRecordExplicitDateVariableStepREL(event.getContent(), factorToMillis)) : "
									+ event.getContent();
							
								RecordReader.nbLinesFiltered--;
								RecordReader.nbLinesEliminated++;
								return null;
							
						}
						event.setDate(date);
						break;
					case "ABS":
						date = getDateRecordExplicitDateVariableStepABS(event.getContent(), factorToMillis);
						if (null == date) {
							LogFouineurMain.errorFormatDate = "Error DateFormat in RecordEventHandlerNonDatedRecord.getDateRecordExplicitDateVariableStepABS(event.getContent(), factorToMillis)) : "
									+ event.getContent();
							RecordReader.nbLinesFiltered--;
							RecordReader.nbLinesEliminated++;
							return null;
						}
						event.setDate(date);
						break;
					}

					break;
				}
				if (dateIsBetween(date)) {
					event.setDate(date);
					return event;
				} else {
					RecordReader.nbLinesFiltered--;
					RecordReader.nbLinesEliminated++;
					return null;
				}
			}

		} else
		{
			RecordReader.nbLinesFiltered--;
			RecordReader.nbLinesEliminated++;
			return null;
		}

	}

	/**
	 * Must I take it.
	 *
	 * @param event
	 *            the event
	 * @return true, if successful
	 */
	private final boolean mustITakeIt(LogRecordEvent event) {
		if (null == regexExclude && null == regexInclude)
			return true;
		else if (null == regexExclude && null != regexInclude)
			return validateInclude(event.getContent());
		else if (null != regexExclude && null == regexInclude)
			return validateExclude(event.getContent());
		else if (null == regexExclude && null != regexInclude)
			return validateExcludeInclude(event.getContent());
		return false;
	}

	/**
	 * Gets the values.
	 *
	 * @param line
	 *            the line
	 * @return the values
	 */
	private final HashMap<String, Double> getValues(String line) {
		HashMap<String, Double> retHm = new HashMap<String, Double>(nbVals);
		for (int i = 0; i < nbVals; i++) {
			switch (valueRegex[i]) {
			case 0:

				String strRegex2 = values[i].getRegex2();
				// System.out.println("strRegex2.values["+i+"].getRegex2() ="+strRegex2);
				String nomClasse = "plugins." + values[i].getRegex1().split("=")[1];
				if (strRegex2.length() > 1) {
					// First Parameter is the string separator
					// String sep = strRegex2.substring(0, 1);
					// System.out.println("sep ="+sep);
					// String[] params = strRegex2.substring(1).split(sep);
					// System.out.println( "params.length="+ params.length);
					// String[] allParams = new String[params.length + 1];
					// allParams[0] = line;
					//
					// for (int j = 0; j < params.length; j++) {
					// allParams[j + 1] = params[j];
					// }
					Method met = hmapsMethod[id].get(nomClasse + "_" + i);
					Object obj = hmapsClass[id].get(nomClasse + "_" + i);
					Double ret = Double.NaN;

					try {
						ret = (Double) met.invoke(obj, new Object[] { line });
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (!ret.isNaN()) {
						retHm.put(values[i].getName(), ret * values[i].getScale());

					} else {
						retHm.put(values[i].getName(), Double.NaN);
					}

				}
				break;

			case 1:
				String regex1 = values[i].getRegex1();
				Double prsDouble = Double.NaN;
				Matcher match1 = Pattern.compile(regex1).matcher(line);
				if (match1.find()) {
					String extract = match1.group().trim().replaceAll(",", ".");

					try {
						prsDouble = Double.parseDouble(extract) * values[i].getScale();
					} catch (NumberFormatException nfe) {
						prsDouble = Double.NaN;
					}

				}
				retHm.put(values[i].getName(), prsDouble);
				break;
			case 2:
				regex1 = values[i].getRegex1();
				String regex2 = values[i].getRegex2();
				prsDouble = Double.NaN;
				match1 = Pattern.compile(regex1).matcher(line);
				if (match1.find()) {
					String extract = match1.group();
					Matcher match2 = Pattern.compile(regex2).matcher(extract);
					if (match2.find()) {
						String extract2 = match2.group().trim().replaceAll(",", ".");

						try {
							prsDouble = Double.parseDouble(extract2) * values[i].getScale();
						} catch (NumberFormatException nfe) {
							prsDouble = Double.NaN;
						}

					}
				}
				retHm.put(values[i].getName(), prsDouble);
				break;
			}

		}
		if (retHm.size() > 0)
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

		for (int i = 0; i < nbPivots; i++) {
			switch (pivotRegex[i]) {
			case 1:
				String regex1 = pivots[i].getRegex1();
				Matcher match1 = Pattern.compile(regex1).matcher(line);
				if (match1.find()) {
					namesPivots.append(pivots[i].getName() + ";");
				}
				break;
			case 2:
				String regex2 = pivots[i].getRegex2();
				regex1 = pivots[i].getRegex1();
				match1 = Pattern.compile(regex1).matcher(line);
				if (match1.find()) {
					String extract = match1.group();
					Matcher match2 = Pattern.compile(regex2).matcher(extract);
					if (match2.find()) {
						namesPivots.append(pivots[i].getName() + ";");
					}
				}
				break;

			}
			if (!boolExhaustiveParsing && namesPivots.length() > 0) {

				break;
			}
		}
		// System.out.println("Thread : "+id+";namesPivots=" + namesPivots);
		if (namesPivots.length() > 0)
			return namesPivots.toString().split(";");
		else
			return null;
	}

	/**
	 * Gets the date record explicit date constant step.
	 *
	 * @param line
	 *            the line
	 * @param step
	 *            the step
	 * @return the date record explicit date constant step
	 */
	private final static Date getDateRecordExplicitDateConstantStep(String line, long step) {
		ParsingMain.accuForImplicitDate += step;
		long fullTime = ParsingMain.accuForImplicitDate + ConfigRecord.originDate.getTime();
		return new Date(fullTime);

	}

	/**
	 * Gets the date record explicit date variable step.
	 *
	 * @param line
	 *            the line
	 * @param factor
	 *            the factor
	 * @return the date record explicit date variable step
	 */
	public final static Date getDateRecordExplicitDateVariableStepREL(String line, double factor) {
		Matcher match = ConfigRecord.gapRegex.matcher(line);
		if (match.find()) {
			String extract = match.group().replaceAll(",", ".");
			Matcher innerMatcher = Pattern.compile("\\d+\\.?\\d*$").matcher(extract);
			if (innerMatcher.find()) {
				double extractDbl = Double.parseDouble(innerMatcher.group());
				ParsingMain.accuForImplicitDate += (long) (extractDbl * factor);
				long fullTime = ParsingMain.accuForImplicitDate + ConfigRecord.originDate.getTime();
				return new Date(fullTime);
			}
		}

		return null;

	}

	/**
	 * Gets the date record explicit date variable step ABS.
	 *
	 * @param line
	 *            the line
	 * @param factor
	 *            the factor
	 * @return the date record explicit date variable step ABS
	 */
	public final static Date getDateRecordExplicitDateVariableStepABS(String line, double factor) {
		Matcher match = ConfigRecord.gapRegex.matcher(line);
		if (match.find()) {
			String extract = match.group().replaceAll(",", ".");
			Matcher innerMatcher = Pattern.compile("\\d+\\.?\\d*$").matcher(extract);
			if (innerMatcher.find()) {
				double extractDbl = Double.parseDouble(innerMatcher.group());
				ParsingMain.accuForImplicitDate = (long) (extractDbl * factor);
				long fullTime = ParsingMain.accuForImplicitDate + ConfigRecord.originDate.getTime();
				return new Date(fullTime);
			}
		}

		return null;

	}

}
