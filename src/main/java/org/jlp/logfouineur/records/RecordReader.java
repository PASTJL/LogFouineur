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
package org.jlp.logfouineur.records;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.jlp.logfouineur.disruptor.LogRecordEvent;
import org.jlp.logfouineur.disruptor.LogRecordEventFactory;
import org.jlp.logfouineur.disruptor.ParsingMain;
import org.jlp.logfouineur.ui.LogFouineurMain;

// TODO: Auto-generated Javadoc
/**
 * The Class RecordReader.
 */
public class RecordReader {

	/** The nb lines input. */
	public static int nbLinesInput = 0;
	
	/** The nb lines filtered. */
	public static int nbLinesFiltered = 0;
	
	/** The nb lines eliminated. */
	public static int nbLinesEliminated = 0;
	/** The accu record. */
	private String accuRecord = "";

	/** The rest record. */
	private String restRecord = "";

	/** The sdf in. */
	public static SimpleDateFormat sdfIn = null;

	/** The regex date. */
	public static String regexDate = "";

	/** The date begin scen. */
	public static Date dateBeginScen = null;

	/** The date fin scen. */
	public static Date dateFinScen = null;

	/** The factor date in millis. */
	public static long factorDateInMillis = 1;

	/** The date origin. */
	public static Date dateOrigin = null;

	/** The flag begin detected. */
	private boolean flagBeginDetected = false;

	/**
	 * Instantiates a new record reader.
	 */
	public RecordReader() {
		nbLinesInput = 0;
		nbLinesFiltered = 0;
		nbLinesEliminated = 0;

	}

	/**
	 * Construct multiline record.
	 *
	 * @param line
	 *            the line
	 * @return the string
	 */
	private final String constructMultilineRecord(String line) {
		String retour = null;

		if (flagBeginDetected) {
			// a begin of record has been detected
			// testing if the EndRecord is in the current line
			Matcher matcher = ConfigRecord.endRecord.matcher(line);
			if (matcher.find()) {
				String extract = matcher.group();
				int idx = line.indexOf(extract);

				retour = accuRecord + " " + line.substring(0, idx + extract.length());
				restRecord = line.substring(idx + extract.length());

				flagBeginDetected = false;

				accuRecord = "";
				// System.out.println("retour dans constructMultilineRecord :\n
				// line="+retour);
				if (LogFouineurMain.isTrace) {
					trace(retour);
				}
				return retour;
			} else {
				accuRecord += " " + line;
			}

		} else {
			accuRecord = "";
			String newLine = null;
			if (restRecord.length() > 0)
				newLine = restRecord + " " + line;
			else
				newLine = line;
			Matcher matcher = ConfigRecord.beginRecord.matcher(newLine);
			if (matcher.find()) {
				flagBeginDetected = true;

				String extract = matcher.group();
				int idx = newLine.indexOf(extract);
				retour = null;
				accuRecord = newLine.substring(idx);
				// tester if the end is in the same line
				matcher = ConfigRecord.endRecord.matcher(accuRecord.substring(idx + extract.length()));
				if (matcher.find()) {
					flagBeginDetected = false;

					extract = matcher.group();
					int idx2 = accuRecord.substring(idx + extract.length()).indexOf(extract);
					retour = accuRecord.substring(0, idx2 + extract.length());

					restRecord = accuRecord.substring(idx2 + extract.length());
					// System.out.println("retour2 dans constructMultilineRecord
					// :\n line="+retour);
					accuRecord = "";
					return retour;
				} else {

				}
			}

		}

		return null;
	}

	/**
	 * Trace.
	 *
	 * @param line
	 *            the line
	 */
	private void trace(String line) {

		try {
			LogFouineurMain.buf.put((line + System.lineSeparator()).getBytes("ISO-8859-1"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {

			if (LogFouineurMain.buf.position() > LogFouineurMain.waterline) {
				int nb = 0;
				while ((nb = LogFouineurMain.channel.write(LogFouineurMain.buf.flip())) > 0) {

					System.out.println("RecordReader nb writing bytes =" + nb);

					LogFouineurMain.fos.flush();

					LogFouineurMain.buf.compact();

				}

				LogFouineurMain.buf.clear();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Construct multiline record begin equals end.
	 *
	 * @param line
	 *            the line
	 * @return the string
	 */
	private final String constructMultilineRecordBeginEqualsEnd(String line) {
		String retour = null;

		if (flagBeginDetected) {
			// a begin of record has been detected
			// testing if the EndRecord is in the current line
			Matcher matcher = ConfigRecord.endRecord.matcher(line);
			if (matcher.find()) {
				String extract = matcher.group();
				int idx = line.indexOf(extract);

				retour = accuRecord + " " + line.substring(0, idx);
				restRecord = line.substring(idx);

				flagBeginDetected = false;

				accuRecord = "";
				// System.out.println("retour dans
				// constructMultilineRecordBeginEqualsEnd :\n line="+retour);
				if (LogFouineurMain.isTrace) {
					trace(retour);
				}
				return retour;
			} else {
				accuRecord += " " + line;
				restRecord = "";
			}

		} else {
			accuRecord = "";
			String newLine = null;
			if (restRecord.length() > 0)
				newLine = restRecord + " " + line;
			else
				newLine = line;
			Matcher matcher = ConfigRecord.beginRecord.matcher(newLine);
			if (matcher.find()) {
				flagBeginDetected = true;

				String extract = matcher.group();
				int idx = newLine.indexOf(extract);
				retour = "";
				accuRecord = newLine.substring(idx);
				// System.out.println("accuRecord="+accuRecord);
				// tester if the end is in the same line
				matcher = ConfigRecord.endRecord.matcher(accuRecord.substring(extract.length()));
				if (matcher.find()) {
					flagBeginDetected = false;

					extract = matcher.group();
					int idx2 = accuRecord.substring(idx + extract.length()).indexOf(extract);
					retour = accuRecord.substring(0, idx2 + extract.length());

					restRecord = accuRecord.substring(idx2 + extract.length());
					accuRecord = "";
					// System.out.println("retour2 dans
					// constructMultilineRecordBeginEqualsEnd :\n
					// line="+retour);
					return retour;
				}
			}

		}

		return null;
	}

	/**
	 * Read.
	 *
	 * @param stream
	 *            the stream
	 * @return the stream
	 */
	public final Stream<LogRecordEvent> read(final Stream<String> stream) {
		Stream<LogRecordEvent> retSream = null;
		Stream<String> tmpStream = null;

		if (ConfigRecord.monoline) {
			tmpStream = (Stream<String>) readMonoLine(stream);
			// System.out.println("RecordReader.read monoline" +
			// tmpStream.count());tmpStream=null;
		} else {
			if (ConfigRecord.beginEqualsEnd) {
				tmpStream = ((Stream<String>) readBeginEqualsEnd(stream));
			} else {
				tmpStream = (Stream<String>) readMultiLine(stream);
				;
			}

		}
		System.out.println("RecorReader after returning the stream");

		if (null == tmpStream) {

			return null;
		}
		if (LogFouineurMain.currentScenarioProps.getProperty("logFouineur.getDateWhenReading", "true").equals("true")) {

			try {
				dateBeginScen = new SimpleDateFormat("yyyy/MM/dd:HH:mm:ss")
						.parse(LogFouineurMain.currentScenarioProps.getProperty("logFouineur.startDate"));
				dateFinScen = new SimpleDateFormat("yyyy/MM/dd:HH:mm:ss")
						.parse(LogFouineurMain.currentScenarioProps.getProperty("logFouineur.endDate"));
				dateOrigin = new SimpleDateFormat("yyyy/MM/dd:HH:mm:ss").parse(LogFouineurMain.currentScenarioProps
						.getProperty("logFouineur.originDat", "1970/01/01:00:00:00"));

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (LogFouineurMain.currentScenarioProps.getProperty("logFouineur.explicitDate", "true").equals("true")
					&& !LogFouineurMain.currentScenarioProps.getProperty("logFouineur.javaFormatDate").toLowerCase()
							.contains("dateinmillis")) {
				sdfIn = new SimpleDateFormat(
						LogFouineurMain.currentScenarioProps.getProperty("logFouineur.javaFormatDate"));
				regexDate = LogFouineurMain.currentScenarioProps.getProperty("logFouineur.regexDate");
				retSream = tmpStream.filter(line -> isLineContainsBegEnd(line)).map(line -> {

					LogRecordEvent le = new LogRecordEventFactory().newInstance();
					Date aDate = RecordReader.getDateRecordExplicitDateWithJavaFormat(line);

					if (null == aDate) {

						LogFouineurMain.errorFormatDate = "Error DateFormat in RecordReader.getDateRecordExplicitDateWithJavaFormat(String line)";
						return null;
					}
					le.set(line, aDate);
					return le;
				}).filter(le -> null != le && dateIsBetween(le.getDate()));

				// .map(line -> new
				// LogRecordEventFactory().newInstance().set(line,getDateRecordImplicitDateWithJavaFormat(line)
				// ));
			} else if (LogFouineurMain.currentScenarioProps.getProperty("logFouineur.explicitDate", "true")
					.equals("true")
					&& LogFouineurMain.currentScenarioProps.getProperty("logFouineur.javaFormatDate").toLowerCase()
							.contains("dateinmillis")) {
				String strDateInMillis = LogFouineurMain.currentScenarioProps.getProperty("logFouineur.javaFormatDate");
				if (strDateInMillis.contains(";")) {
					factorDateInMillis = Long.parseLong(strDateInMillis.split(";")[1]);

				} else {

					factorDateInMillis = 1;
				}

				regexDate = LogFouineurMain.currentScenarioProps.getProperty("logFouineur.regexDate");

				//

				retSream = tmpStream.filter(line -> isLineContainsBegEnd(line)).map(line -> {
					LogRecordEvent le = new LogRecordEventFactory().newInstance();
					Date aDate = RecordReader.getDateRecordExplicitDateWithDateInMillis(line);

					if (null == aDate) {
						LogFouineurMain.errorFormatDate = "Error DateFormat in RecordReader.getDateRecordExplicitDateWithDateInMillis(String line)";
						return null;
					}
					le.set(line, aDate);
					return le;
				}).filter(le -> {
					if (null != le && dateIsBetween(le.getDate())) {
						// System.out.println ("le => "+ le.getContent());
						// System.out.println ("-----------------------------------------------------");
						return true;
					}
					return false;

				});

			} else if (LogFouineurMain.currentScenarioProps.getProperty("logFouineur.explicitDate", "true")
					.equals("false")) {
				switch (LogFouineurMain.currentScenarioProps.getProperty("logFouineur.implicitStep", "true")) {
				case "true":
					retSream = tmpStream.filter(line -> isLineContainsBegEnd(line)).map(line -> {
						LogRecordEvent le = new LogRecordEventFactory().newInstance();
						Date aDate = getDateRecordImplicitDateConstantStep(line, ParsingMain.stepInMillis);
						if (null == aDate) {
							LogFouineurMain.errorFormatDate = "Error DateFormat in RecordReader.getDateRecordImplicitDateConstantStep(line, ParsingMain.stepInMillis))";
							return null;
						}
						le.set(line, aDate);
						return le;
					}).filter(le -> null != le && dateIsBetween(le.getDate()));
					break;

				case "false":
					switch (ConfigRecord.absRel) {
					case "ABS":
						retSream = tmpStream.filter(line -> isLineContainsBegEnd(line)).map(line -> {
							LogRecordEvent le = new LogRecordEventFactory().newInstance();
							Date aDate = RecordReader.getDateRecordImplicitDateVariableStepABS(line,
									ParsingMain.factorToMillis);
							if (null == aDate) {
								LogFouineurMain.errorFormatDate = "Error DateFormat in RecordReader.getDateRecordImplicitDateVariableStepABS(line, ParsingMain.factorToMillis)) "
										+ line;
								return null;
							}
							le.set(line, aDate);
							return le;
						}).filter(le -> null != le && dateIsBetween(le.getDate()));
						break;
					case "REL":
						retSream = tmpStream.filter(line -> isLineContainsBegEnd(line)).map(line -> {
							LogRecordEvent le = new LogRecordEventFactory().newInstance();
							Date aDate = RecordReader.getDateRecordImplicitDateVariableStepREL(line,
									ParsingMain.factorToMillis);
							if (null == aDate) {
								LogFouineurMain.errorFormatDate = "Error DateFormat in RecordReader.getDateRecordImplicitDateVariableStepREL(line, ParsingMain.factorToMillis)) "
										+ line;
								return null;
							}
							le.set(line, aDate);
							return le;
						}).filter(le -> null != le && dateIsBetween(le.getDate()));
						break;
					}

					break;
				}

			}

		} else {
			retSream = tmpStream.filter(line -> isLineContainsBegEnd(line)).map(line -> {
				LogRecordEvent le = new LogRecordEventFactory().newInstance();
				le.set(line, new Date(0L));
				return le;
			});

		}

		return retSream;

	}

	/**
	 * Checks if is line contains beg end.
	 *
	 * @param line
	 *            the line
	 * @return true, if is line contains beg end
	 */
	private boolean isLineContainsBegEnd(String line) {

		if (!ConfigRecord.beginRecord.matcher(line).find())
			return false;
		if (null != ConfigRecord.beginRecord) {
			if (!ConfigRecord.beginRecord.matcher(line).find())
				return false;
		}
		return true;
	}

	/**
	 * Gets the date record implicit date constant step.
	 *
	 * @param line
	 *            the line
	 * @param step
	 *            the step
	 * @return the date record implicit date constant step
	 */
	private final static Date getDateRecordImplicitDateConstantStep(String line, long step) {
		ParsingMain.accuForImplicitDate += step;
		long fullTime = ParsingMain.accuForImplicitDate + ConfigRecord.originDate.getTime();
		return new Date(fullTime);

	}

	/**
	 * Gets the date record implicit date variable step.
	 *
	 * @param line
	 *            the line
	 * @param factor
	 *            the factor
	 * @return the date record implicit date variable step
	 */
	private final static Date getDateRecordImplicitDateVariableStepABS(String line, double factor) {
		Matcher match = ConfigRecord.gapRegex.matcher(line);

		if (match.find()) {

			String extract = match.group().replaceAll(",", ".");

			Matcher innerMatcher = Pattern.compile("\\d+\\.?\\d*$").matcher(extract);
			if (innerMatcher.find()) {

				double extractDbl = Double.parseDouble(innerMatcher.group());

				// No accumulate date
				ParsingMain.accuForImplicitDate = (long) (extractDbl * factor);
				long fullTime = ParsingMain.accuForImplicitDate + ConfigRecord.originDate.getTime();
				return new Date(fullTime);
			}

		}

		return null;

	}

	/**
	 * Gets the date record implicit date variable step REL.
	 *
	 * @param line
	 *            the line
	 * @param factor
	 *            the factor
	 * @return the date record implicit date variable step REL
	 */
	private final static Date getDateRecordImplicitDateVariableStepREL(String line, double factor) {
		Matcher match = ConfigRecord.gapRegex.matcher(line);

		if (match.find()) {
			String extract = match.group().replaceAll(",", ".");

			Matcher innerMatcher = Pattern.compile("\\d+\\.?\\d*$").matcher(extract);
			if (innerMatcher.find()) {
				double extractDbl = Double.parseDouble(innerMatcher.group());
				// accumulate date

				ParsingMain.accuForImplicitDate += (long) (extractDbl * factor);
				long fullTime = ParsingMain.accuForImplicitDate + ConfigRecord.originDate.getTime();
				return new Date(fullTime);
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
	private final static Date getDateRecordExplicitDateWithDateInMillis(String line) {
		// Matcher matcher = Pattern.compile(regexDate).matcher(line);
		Matcher matcher = ConfigRecord.regexDateRecord.matcher(line);

		if (matcher.find()) {

			String extract = matcher.group();

			Double gapInMillisDbl = Double.valueOf(extract) * factorDateInMillis;
			long gapDateInMillis = gapInMillisDbl.longValue();

			long fullTime = gapDateInMillis + ConfigRecord.originDate.getTime();

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
	private final boolean validateExcludeInclude(String contentRecord) {

		Matcher matcher = ConfigRecord.regexExclude.matcher(contentRecord);
		if (matcher.find()) {

			return false;
		} else {
			Matcher matcher2 = ConfigRecord.regexInclude.matcher(contentRecord);
			if (matcher2.find()) {
				if (LogFouineurMain.isTrace) {
					trace(contentRecord);
				}
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

		Matcher matcher3 = ConfigRecord.regexInclude.matcher(contentRecord);
		if (matcher3.find()) {
			if (LogFouineurMain.isTrace) {
				trace(contentRecord);
			}
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
		Matcher matcher = ConfigRecord.regexExclude.matcher(contentRecord);
		if (matcher.find()) {
			return false;
		}
		if (LogFouineurMain.isTrace) {
			trace(contentRecord);
		}
		return true;

	}

	/**
	 * Read begin equals end.
	 *
	 * @param stream
	 *            the stream
	 * @return the stream
	 */
	private final Stream<String> readBeginEqualsEnd(Stream<String> stream) {
		if (ConfigRecord.boolGetDateWhenReading) {
			if (null == ConfigRecord.regexExclude && null == ConfigRecord.regexInclude) {
				return stream.map(line -> constructMultilineRecordBeginEqualsEnd(line)).filter((String line) -> {
					nbLinesInput++;
					if (null != line && line.length() > 3) {
						
						if (LogFouineurMain.isDebug) {
							LogFouineurMain.debug(
									"RecordReader: boolGetDateWhenReading true using constructMultilineRecordBeginEqualsEnd noInclude/noExclude line => "
											+ line + System.lineSeparator());
						}
						nbLinesFiltered++;
						return true;
					}
					nbLinesEliminated++;
					return false;
				});
			} else if (null != ConfigRecord.regexExclude && null == ConfigRecord.regexInclude) {
				return stream.map(line -> constructMultilineRecordBeginEqualsEnd(line)).filter((String line) -> {
					nbLinesInput++;
					if (null != line && line.length() > 3) {
						
						if (LogFouineurMain.isDebug) {
							LogFouineurMain.debug(
									"RecordReader: boolGetDateWhenReading true using constructMultilineRecordBeginEqualsEnd Include/noExclude line => "
											+ line + System.lineSeparator());
						}

						return true;
					}
					nbLinesEliminated++;
					return false;
				}).filter(line -> {
					if (validateExclude(line)) {
						nbLinesFiltered++;
						return true;
					}
					nbLinesEliminated++;
					return false;
				});

			} else if (null == ConfigRecord.regexExclude && null != ConfigRecord.regexInclude) {
				return stream.map(line -> constructMultilineRecordBeginEqualsEnd(line)).filter((String line) -> {
					nbLinesInput++;
					if (null != line && line.length() > 3) {
						
						if (LogFouineurMain.isDebug) {
							LogFouineurMain.debug(
									" RecordReader: boolGetDateWhenReading true using constructMultilineRecordBeginEqualsEnd noInclude/Exclude line => "
											+ line + System.lineSeparator());
						}
						return true;
					}
					nbLinesEliminated++;
					return false;
				}).filter(line -> {
					if (validateInclude(line)) {
						nbLinesFiltered++;
						return true;
					}
					nbLinesEliminated++;
					return false;

				});

			} else if (null != ConfigRecord.regexExclude && null != ConfigRecord.regexInclude) {
				return stream.map(line -> constructMultilineRecordBeginEqualsEnd(line)).filter((String line) -> {
					nbLinesInput++;
					if (null != line && line.length() > 3) {
						
						if (LogFouineurMain.isDebug) {
							LogFouineurMain.debug(
									"RecordReader: boolGetDateWhenReading true using constructMultilineRecordBeginEqualsEnd Include/Exclude line => "
											+ line + System.lineSeparator());
						}
						return true;
					}
					nbLinesEliminated++;
					return false;
				}).filter(line -> {
					if (validateExcludeInclude(line)) {
						nbLinesFiltered++;
						return true;
					}
					nbLinesEliminated++;
					return false;
				});
			}
			return null;
		} else {

			return stream.map(line -> constructMultilineRecordBeginEqualsEnd(line)).filter(line -> {
				nbLinesInput++;
				if (null != line && line.length() > 3) {
					
					if (LogFouineurMain.isDebug) {
						LogFouineurMain.debug(
								"RecordReader: boolGetDateWhenReading false using constructMultilineRecordBeginEqualsEnd NoInclude/NoExclude line => "
										+ line + System.lineSeparator());
					}
					nbLinesFiltered++;
					return true;
				}
				nbLinesEliminated++;
				return false;
			});
		}

	}

	/**
	 * Read mono line.
	 *
	 * @param stream
	 *            the stream
	 * @return the stream
	 */
	private final Stream<String> readMonoLine(Stream<String> stream) {
		if (ConfigRecord.boolGetDateWhenReading) {

			// Exclude / Include filter is done as under for performance. There
			// are
			// less logical tests doing so.
			Stream<String> tmpStream = null;
			// System.out.println("ReadRecord readMonoline stream ");
			if (null == ConfigRecord.regexExclude && null == ConfigRecord.regexInclude) {
				tmpStream = stream.filter((String line) -> {
					
					nbLinesInput++;
					if (null != line && line.length() > 3) {
						
						if (LogFouineurMain.isTrace) {
							trace(line);
						}
						if (LogFouineurMain.isDebug) {
							LogFouineurMain.debug(
									"RecordReader boolGetDateWhenReading  true readMonoLine NoInclude/NoExclude  line => "
											+ line + System.lineSeparator());

						}
						nbLinesFiltered++;
						return true;
					}
					nbLinesEliminated++;
					return false;
				});
				// System.out.println("ReadRecord return stream ");
				return tmpStream;
			} else if (null != ConfigRecord.regexExclude && null == ConfigRecord.regexInclude) {
				return stream.filter((String line) -> {
					nbLinesInput++;
					if (null != line && line.length() > 3) {
						
						if (LogFouineurMain.isTrace) {
							trace(line);
						}
						if (LogFouineurMain.isDebug) {
							LogFouineurMain.debug(
									"RecordReader boolGetDateWhenReading  true readMonoLine NoInclude/Exclude  line => "
											+ line + System.lineSeparator());
						}

						return true;
					}
					nbLinesEliminated++;
					return false;
				}).filter(line -> {
					if (validateExclude(line)) {
						nbLinesFiltered++;
						return true;

					}
					nbLinesEliminated++;
					return false;
				});

			} else if (null == ConfigRecord.regexExclude && null != ConfigRecord.regexInclude) {
				
				return stream.filter((String line) -> {
					nbLinesInput++;
					if (null != line && line.length() > 3) {
						
						if (LogFouineurMain.isTrace) {
							trace(line);
						}
						if (LogFouineurMain.isDebug) {
							LogFouineurMain.debug(
									"RecordReader boolGetDateWhenReading  true readMonoLine Include/NoExclude  line => "
											+ line + System.lineSeparator());
						}

						return true;
					}
					nbLinesEliminated++;
					return false;
				}).filter(line -> {
					if (validateInclude(line)) {
						nbLinesFiltered++;
						return true;
					}
					nbLinesEliminated++;
					return false;
				});
			} else if (null != ConfigRecord.regexExclude && null != ConfigRecord.regexInclude) {
				return stream.filter((String line) -> {
					nbLinesInput++;
					if (null != line && line.length() > 3) {
						
						if (LogFouineurMain.isTrace) {
							trace(line);
						}
						if (LogFouineurMain.isDebug) {
							LogFouineurMain.debug(
									"RecordReader boolGetDateWhenReading  true readMonoLine Include/Exclude  line => "
											+ line + System.lineSeparator());
						}
						return true;
					}
					nbLinesEliminated++;
					return false;
				}).filter(line -> {
					if (validateExcludeInclude(line)) {
						nbLinesFiltered++;
						return true;
					}
					nbLinesEliminated++;
					return false;
				});
			}
			return null;
		} else {
			System.out.println("before Read Monoline without reading date");
			return stream.filter(line -> {
				nbLinesInput++;
				if (null != line && line.length() > 3 ) {
					
					if (LogFouineurMain.isTrace) {
						trace(line);
					}
					if (LogFouineurMain.isDebug) {
						LogFouineurMain.debug("RecordReader boolGetDateWhenReading  false readMonoLine   line => "
								+ line + System.lineSeparator());
					}
					nbLinesFiltered++;
					return true;
				}
				nbLinesEliminated++;
				return false;
			});
		}
	}

	/**
	 * Date is between.
	 *
	 * @param date
	 *            the date
	 * @return true, if successful
	 */
	private final boolean dateIsBetween(Date date) {

		if (date.after(dateBeginScen) && date.before(dateFinScen))
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
	private final static Date getDateRecordExplicitDateWithJavaFormat(String line) {

		Matcher matcher = Pattern.compile(regexDate).matcher(line);
		if (matcher.find()) {
			String extract = matcher.group();
			try {
				return sdfIn.parse(extract);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * Read multi line.
	 *
	 * @param stream
	 *            the stream
	 * @return the stream
	 */
	private final Stream<String> readMultiLine(Stream<String> stream) {
		if (ConfigRecord.boolGetDateWhenReading) {
			if (null == ConfigRecord.regexExclude && null == ConfigRecord.regexInclude) {
				return stream.map(line -> constructMultilineRecord(line)).filter((String line) -> {
					nbLinesInput++;
					if (null != line && line.length() > 3) {
						
						if (LogFouineurMain.isDebug) {
							LogFouineurMain.debug(
									"RecordReader boolGetDateWhenReading  true readMultiLine NoInclude/NoExlude   line => "
											+ line + System.lineSeparator());
						}
						nbLinesFiltered++;
						return true;
					}
					nbLinesEliminated++;
					return false;
				});
			} else if (null != ConfigRecord.regexExclude && null == ConfigRecord.regexInclude) {
				return stream.map(line -> constructMultilineRecord(line)).filter((String line) -> {
					nbLinesInput++;
					if (null != line && line.length() > 3) {
						
						if (LogFouineurMain.isDebug) {
							LogFouineurMain.debug(
									"RecordReader boolGetDateWhenReading  true readMultiLine NoInclude/Exlude   line => "
											+ line + System.lineSeparator());
						}

						return true;
					}
					nbLinesEliminated++;
					return false;
				}).filter(line -> {
					if (validateExclude(line)) {
						nbLinesFiltered++;
						return true;
					}
					nbLinesEliminated++;
					return false;
				});

			} else if (null == ConfigRecord.regexExclude && null != ConfigRecord.regexInclude) {
				return stream.map(line -> constructMultilineRecord(line)).filter((String line) -> {
					nbLinesInput++;
					if (null != line && line.length() > 3) {
						
						if (LogFouineurMain.isDebug) {
							LogFouineurMain.debug(
									"RecordReader boolGetDateWhenReading  true readMultiLine Include/NoExlude   line => "
											+ line + System.lineSeparator());
						}

						return true;
					}
					nbLinesEliminated++;
					return false;
				}).filter(line -> {
					if (validateInclude(line)) {
						nbLinesFiltered++;
						return true;
					}
					nbLinesEliminated++;
					return false;
				});

			} else if (null != ConfigRecord.regexExclude && null != ConfigRecord.regexInclude) {
				return stream.map(line -> constructMultilineRecord(line)).filter((String line) -> {
					nbLinesInput++;
					if (null != line && line.length() > 3) {
						
						if (LogFouineurMain.isDebug) {
							LogFouineurMain.debug(
									"RecordReader boolGetDateWhenReading  true readMultiLine Include/Exlude   line => "
											+ line + System.lineSeparator());
						}

						return true;
					}
					nbLinesEliminated++;
					return false;
				}).filter(line -> {
					if (validateExcludeInclude(line)) {
						nbLinesFiltered++;
						return true;
					}
					nbLinesEliminated++;
					return false;

				});
			}
			return null;
		} else
			return stream.map(line -> constructMultilineRecord(line)).filter(line -> {
				nbLinesInput++;
				if (null != line && line.length() > 3) {
					
					if (LogFouineurMain.isDebug) {
						LogFouineurMain.debug(
								"RecordReader boolGetDateWhenReading false readMultiLine Include/Exlude   line => "
										+ line + System.lineSeparator());
					}
					nbLinesFiltered++;
					return true;
				}
				nbLinesEliminated++;
				return false;
			});
	}
}
