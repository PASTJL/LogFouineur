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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jlp.logfouineur.records.ConfigRecord;
import org.jlp.logfouineur.ui.LogFouineurFill;
import org.jlp.logfouineur.ui.LogFouineurMain;

import javafx.collections.ObservableList;

// TODO: Auto-generated Javadoc
/**
 * The Class ParsingConfigHandler.
 */
public class ParsingConfigHandler {

	/** The file to parse. */
	public String fileToParse;

	/** The path root. */
	private String pathRoot;

	/** The name file basic. */
	private String nameFileBasic;

	/**
	 * Instantiates a new parsing config handler.
	 *
	 * @param fileToParse
	 *            the file to parse
	 */
	public ParsingConfigHandler(String fileToParse) {
		this.fileToParse = fileToParse;

		int lastSeparator = fileToParse.lastIndexOf(File.separator);
		pathRoot = fileToParse.substring(0, lastSeparator + 1);
	
		nameFileBasic = fileToParse.substring(lastSeparator + 1);
		if (nameFileBasic.endsWith(".gz")) {
			int idxTmp = nameFileBasic.indexOf(".gz");
			nameFileBasic = nameFileBasic.substring(0, idxTmp);
		}

		
	}

	/**
	 * Instantiates a new parsing config handler.
	 */
	public ParsingConfigHandler() {
		
	}

	/**
	 * Save in file.
	 *
	 * @param toFile
	 *            the to file
	 */
	public void saveInFile(String toFile) {

		File propsConfig = new File(toFile);
		if (propsConfig.exists())
			propsConfig.delete();
		
		Properties propsToSave = new Properties();
		propsToSave.setProperty("logFouineur.fileToParse", LogFouineurMain.fileToParse);

		if (null == LogFouineurFill.tfStepAgg.getText() || LogFouineurFill.tfStepAgg.getText().length() == 0) {
			propsToSave.setProperty("logFouineur.stepAgg", "1000");
		} else
			propsToSave.setProperty("logFouineur.stepAgg", LogFouineurFill.tfStepAgg.getText());

		if (null == LogFouineurFill.tfStartRegex.getText() || LogFouineurFill.tfStartRegex.getText().length() == 0) {
			propsToSave.setProperty("logFouineur.startRegex", "");
		} else
			propsToSave.setProperty("logFouineur.startRegex", LogFouineurFill.tfStartRegex.getText());

		if (null == LogFouineurFill.tfEndRegex.getText() || LogFouineurFill.tfEndRegex.getText().length() == 0) {
			propsToSave.setProperty("logFouineur.endRegex", "");
		} else
			propsToSave.setProperty("logFouineur.endRegex", LogFouineurFill.tfEndRegex.getText());

		if (null == LogFouineurFill.tfIncludeRecord.getText()
				|| LogFouineurFill.tfIncludeRecord.getText().length() == 0) {
			propsToSave.setProperty("logFouineur.includeRecord", "");
		} else
			propsToSave.setProperty("logFouineur.includeRecord", LogFouineurFill.tfIncludeRecord.getText());

		if (null == LogFouineurFill.tfExcludeRecord.getText()
				|| LogFouineurFill.tfExcludeRecord.getText().length() == 0) {
			propsToSave.setProperty("logFouineur.excludeRecord", "");
		} else
			propsToSave.setProperty("logFouineur.excludeRecord", LogFouineurFill.tfExcludeRecord.getText());

		if (LogFouineurFill.rbExplicitDate.isSelected()) {
			propsToSave.setProperty("logFouineur.explicitDate", "true");
		} else
			propsToSave.setProperty("logFouineur.explicitDate", "false");

		if (LogFouineurFill.rbNumLocale.isSelected()) {
			propsToSave.setProperty("logFouineur.numLocale", "true");
		} else
			propsToSave.setProperty("logFouineur.numLocale", "false");

		propsToSave.setProperty("logFouineur.implicitStep", "false");
		if (!LogFouineurFill.rbExplicitDate.isSelected()) {

			if (LogFouineurFill.rbImplicitStep.isSelected()) {
				propsToSave.setProperty("logFouineur.implicitStep", "true"); // The
																				// date
																				// of
																				// the
																				// first
																				// record
																				// is
																				// the
																				// origin
																				// date
																				// the
																				// date
																				// of
																				// next
																				// record
																				// is
																				// the
																				// date
																				// of
																				// the
																				// current
																				// record
																				// with
																				// adding
																				// the
																				// value
																				// of
																				// tfStepExtract
																				// with
																				// unit
																				// in
																				// cbUnitStep
			} else
				propsToSave.setProperty("logFouineur.implicitStep", "false");
		}

		propsToSave.setProperty("logFouineur.gapRegex", "false");
		if (!LogFouineurFill.rbExplicitDate.isSelected()) {
			if (LogFouineurFill.rbGapRegex.isSelected()) {
				propsToSave.setProperty("logFouineur.gapRegex", "true");// The
																		// date
																		// of
																		// the
																		// first
																		// record
																		// is
																		// the
																		// origin
																		// date
																		// the
																		// date
																		// of
																		// next
																		// record
																		// is
																		// date
																		// of
																		// the
																		// current
																		// record
																		// with
																		// adding
																		// the
																		// extraction
																		// of
																		// regex
																		// of
																		// tfStepExtract
																		// with
																		// unit
																		// in
																		// cbUnitStep
			} else
				propsToSave.setProperty("logFouineur.gapRegex", "false");
		}

		propsToSave.setProperty("logFouineur.stepExtract", "");
		propsToSave.setProperty("logFouineur.unitStep", "ms");
		if (!LogFouineurFill.rbExplicitDate.isSelected()) {
			propsToSave.setProperty("logFouineur.unitStep", LogFouineurFill.cbUnitStep.getValue());
			if (null == LogFouineurFill.tfStepExtract.getText()
					|| LogFouineurFill.tfStepExtract.getText().trim().length() == 0) {
				propsToSave.setProperty("logFouineur.stepExtract", "");
				
			} else {
				
				propsToSave.setProperty("logFouineur.stepExtract", LogFouineurFill.tfStepExtract.getText());
			}
		}

		propsToSave.setProperty("logFouineur.originDate", "");
		if (null == LogFouineurFill.tfOriginDate.getText() || LogFouineurFill.tfOriginDate.getText().length() == 0) {
			propsToSave.setProperty("logFouineur.originDate", "1970/01/01:00:00:00");
		} else
			propsToSave.setProperty("logFouineur.originDate", LogFouineurFill.tfOriginDate.getText());

		if (null == LogFouineurFill.tfRegexDate.getText() || LogFouineurFill.tfRegexDate.getText().length() == 0) {
			propsToSave.setProperty("logFouineur.regexDate", "");
		} else
			propsToSave.setProperty("logFouineur.regexDate", LogFouineurFill.tfRegexDate.getText());

		if (null == LogFouineurFill.tfJavaFormatDate.getText()
				|| LogFouineurFill.tfJavaFormatDate.getText().length() == 0) {
			propsToSave.setProperty("logFouineur.javaFormatDate", "");
		} else
			propsToSave.setProperty("logFouineur.javaFormatDate", LogFouineurFill.tfJavaFormatDate.getText());

		if (null == LogFouineurFill.tfStartDate.getText() || LogFouineurFill.tfStartDate.getText().length() == 0) {
			propsToSave.setProperty("logFouineur.startDate", "");
		} else
			propsToSave.setProperty("logFouineur.startDate", LogFouineurFill.tfStartDate.getText());

		if (null == LogFouineurFill.tfEndDate.getText() || LogFouineurFill.tfEndDate.getText().length() == 0) {
			propsToSave.setProperty("logFouineur.endDate", "");
		} else
			propsToSave.setProperty("logFouineur.endDate", LogFouineurFill.tfEndDate.getText());

		if (null == LogFouineurFill.tfFileOut.getText() || LogFouineurFill.tfFileOut.getText().length() == 0) {
			propsToSave.setProperty("logFouineur.fileOut", "");
		} else
			propsToSave.setProperty("logFouineur.fileOut", LogFouineurFill.tfFileOut.getText());

		if (null == LogFouineurFill.tfCsvSeparator.getText()
				|| LogFouineurFill.tfCsvSeparator.getText().length() == 0) {
			propsToSave.setProperty("logFouineur.csvSeparator", ";");
		} else
			propsToSave.setProperty("logFouineur.csvSeparator", LogFouineurFill.tfCsvSeparator.getText());

		if (null == LogFouineurFill.tfCsvJavaFormatDate.getText()
				|| LogFouineurFill.tfCsvJavaFormatDate.getText().length() == 0) {
			propsToSave.setProperty("logFouineur.csvJavaFormatDate", ";");
		} else
			propsToSave.setProperty("logFouineur.csvJavaFormatDate", LogFouineurFill.tfCsvJavaFormatDate.getText());

		propsToSave.setProperty("logFouineur.confOutputFile", LogFouineurFill.cbConfOutputFile.getValue());

		if (LogFouineurFill.rbNumLocaleOut.isSelected()) {
			propsToSave.setProperty("logFouineur.numLocaleOut", "true");
		} else
			propsToSave.setProperty("logFouineur.numLocaleOut", "false");

		if (LogFouineurFill.rbCompactOutput.isSelected()) {
			propsToSave.setProperty("logFouineur.compactOutput", "true");
		} else
			propsToSave.setProperty("logFouineur.compactOutput", "false");

		if (null == LogFouineurFill.tfThreads.getText() || LogFouineurFill.tfThreads.getText().length() == 0) {
			propsToSave.setProperty("logFouineur.threads", "0");
		} else
			propsToSave.setProperty("logFouineur.threads", LogFouineurFill.tfThreads.getText());

		if (null == LogFouineurFill.tfTz.getText() || LogFouineurFill.tfTz.getText().length() == 0) {
			propsToSave.setProperty("logFouineur.tz", "0");
		} else
			propsToSave.setProperty("logFouineur.tz", LogFouineurFill.tfTz.getText());

		if (LogFouineurFill.rbStDate.isSelected()) {
			propsToSave.setProperty("logFouineur.isStartTimestamp", "true");
		} else
			propsToSave.setProperty("logFouineur.isStartTimestamp", "false");

		if (LogFouineurFill.rbDirectShow.isSelected()) {
			propsToSave.setProperty("logFouineur.showSummary", "true");
		} else
			propsToSave.setProperty("logFouineur.showSummary", "false");

		if (LogFouineurFill.rbParsing.isSelected()) {
			propsToSave.setProperty("logFouineur.parsing", "true");
		} else
			propsToSave.setProperty("logFouineur.parsing", "false");

		if (LogFouineurFill.rbTrace.isSelected()) {
			propsToSave.setProperty("logFouineur.trace", "true");
		} else
			propsToSave.setProperty("logFouineur.trace", "false");

		if (LogFouineurFill.rbGetDateWhenReading.isSelected()) {
			propsToSave.setProperty("logFouineur.getDateWhenReading", "true");
		} else
			propsToSave.setProperty("logFouineur.getDateWhenReading", "false");

		propsToSave.setProperty("logFouineur.correctDate", LogFouineurFill.cbCorrectDate.getValue());

		// Saving Values table
		ObservableList<JFXValue> values = LogFouineurFill.tableValue.getItems();
		
		int idx = 0;
		boolean vrai = true;
		int idx2=0;
		while (idx2<values.size()) {
			JFXValue value = values.get(idx2);
			
			if (value.getName().length() > 0) {
				
				
				propsToSave.setProperty("logFouineur.value." + idx + ".name", value.getName());
				propsToSave.setProperty("logFouineur.value." + idx + ".regex1", value.getRegex1());
				propsToSave.setProperty("logFouineur.value." + idx + ".regex2", value.getRegex2());
				propsToSave.setProperty("logFouineur.value." + idx + ".unit", value.getUnit());
				propsToSave.setProperty("logFouineur.value." + idx + ".scale", ((Double) value.getScale()).toString());
				propsToSave.setProperty("logFouineur.value." + idx + ".isDuration",
						((Boolean) value.isIsDuration()).toString());

				idx++;
			} 
			idx2++;
		}

		// Saving Pivots table
		ObservableList<JFXPivot> pivots = LogFouineurFill.tablePivot.getItems();
		
		idx = 0;
		vrai = true;
		idx2=0;
		while (idx2<pivots.size()) {
			JFXPivot pivot = pivots.get(idx2);
			if (pivot.getName().length() > 0) {
			//	System.out.println("Saving pivots.size ="+pivot.getName());
				propsToSave.setProperty("logFouineur.pivot." + idx + ".name", pivot.getName());
				propsToSave.setProperty("logFouineur.pivot." + idx + ".regex1", pivot.getRegex1());
				propsToSave.setProperty("logFouineur.pivot." + idx + ".regex2", pivot.getRegex2());

				idx++;
			} idx2++;
		}

		try (FileOutputStream fos = new FileOutputStream(propsConfig);
				FileInputStream fis = new FileInputStream(propsConfig)) {
			propsToSave.store(fos, "");
			LogFouineurMain.currentScenarioProps.clear();
			LogFouineurMain.currentScenarioProps.load(fis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Save.
	 */
	public void save() {
		String strConfigPropertiesFile = pathRoot + "config" + File.separator + "parselog" + File.separator
				+ nameFileBasic + ".properties";

		saveInFile(strConfigPropertiesFile);
		loadProps(strConfigPropertiesFile);
		// Fill ConfigRcord
		try {
//			System.out.println("Save before fillConfigRecord LogFouineurFill.tfStepExtract.getText()="
//					+ LogFouineurFill.tfStepExtract.getText());
			fillConfigRecord(strConfigPropertiesFile);
//			System.out.println("Save after fillConfigRecord LogFouineurFill.tfStepExtract.getText()="
//					+ LogFouineurFill.tfStepExtract.getText());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Fill config record.
	 *
	 * @param tmpProps the tmp props
	 * @throws ParseException the parse exception
	 */
	public void fillConfigRecord(Properties tmpProps) throws ParseException {



		// file Props OK

		ConfigRecord.reInit();

		ConfigRecord.fileToParse = tmpProps.getProperty("logFouineur.fileToParse");
		ConfigRecord.beginRecord = Pattern.compile(tmpProps.getProperty("logFouineur.startRegex"));
		ConfigRecord.endRecord = null;
		if (tmpProps.getProperty("logFouineur.endRegex").length() > 0) {
			ConfigRecord.monoline = false;
			ConfigRecord.endRecord = Pattern.compile(tmpProps.getProperty("logFouineur.endRegex"));
			if (tmpProps.getProperty("logFouineur.endRegex").equals(tmpProps.getProperty("logFouineur.startRegex"))) {
				ConfigRecord.beginEqualsEnd = true;
			} else
				ConfigRecord.beginEqualsEnd = false;
		} else {
			ConfigRecord.monoline = true;
		}

		ConfigRecord.dateBeginOfParsing = new SimpleDateFormat("yyyy/MM/dd:HH:mm:ss")
				.parse(tmpProps.getProperty("logFouineur.startDate", "1970/01/01:00:00:00"));
		ConfigRecord.dateEndOfParsing = new SimpleDateFormat("yyyy/MM/dd:HH:mm:ss")
				.parse(tmpProps.getProperty("logFouineur.endDate", "1970/01/01:00:00:00"));
		ConfigRecord.originDate = new SimpleDateFormat("yyyy/MM/dd:HH:mm:ss")
				.parse(tmpProps.getProperty("logFouineur.originDate", "1970/01/01:00:00:00"));
		ConfigRecord.regexInclude = null;
		if (tmpProps.getProperty("logFouineur.includeRecord").length() > 0)
			ConfigRecord.regexInclude = Pattern.compile(tmpProps.getProperty("logFouineur.includeRecord"));
		ConfigRecord.regexExclude = null;
		if (tmpProps.getProperty("logFouineur.excludeRecord").length() > 0)
			ConfigRecord.regexExclude = Pattern.compile(tmpProps.getProperty("logFouineur.excludeRecord"));

		if (tmpProps.getProperty("logFouineur.explicitDate").equals("true")) {
			ConfigRecord.boolExplicitDate = true; // false => implicit date
		} else
			ConfigRecord.boolExplicitDate = false; // false => implicit date

		ConfigRecord.regexDateRecord = Pattern.compile(tmpProps.getProperty("logFouineur.regexDate"));

		ConfigRecord.stepFromOrigin = 0; // to use with implicit date and origin
											// date
		ConfigRecord.step2Records = 0; // to use with implicit date and origin
										// date
		ConfigRecord.gapRegex = null;

		ConfigRecord.boolImplicitStep = false;// to use with implicit date and

		// origin date
		// File properties OK

		if (ConfigRecord.boolExplicitDate) {
			ConfigRecord.javaDateFormatRecord = tmpProps.getProperty("logFouineur.javaFormatDate");

			ConfigRecord.isDateInMillis = false;
			if (ConfigRecord.javaDateFormatRecord.toLowerCase().contains("dateinmillis")) {
				if (ConfigRecord.javaDateFormatRecord.toLowerCase().contains(";")) {
				ConfigRecord.factorDateInMillis = Long
						.parseLong(ConfigRecord.javaDateFormatRecord.split(";")[1].trim());}
				else
				{
					ConfigRecord.factorDateInMillis =1;
				}
				ConfigRecord.isDateInMillis = true;
			}
		} else {
			ConfigRecord.javaDateFormatRecord = null;
			ConfigRecord.unitStep = tmpProps.getProperty("logFouineur.unitStep", "ms");
			if (tmpProps.getProperty("logFouineur.implicitStep").equals("true")) {
				ConfigRecord.boolImplicitStep = true;
				ConfigRecord.step2Records = Long.parseLong(tmpProps.getProperty("logFouineur.stepExtract"));
				ConfigRecord.gapRegex = null;
				ConfigRecord.unitStep = tmpProps.getProperty("logFouineur.unitStep");
			} else {
				ConfigRecord.boolImplicitStep = false;
				ConfigRecord.step2Records = 0L;
				// When logFouineur.stepExtract is in two parts separeted by a space
				// The first part is the regexp, the second part is the chain REL or ABS for
				// relative or Absolute
				// if avbsent itis Absolute
				String strTemp = tmpProps.getProperty("logFouineur.stepExtract", "");
				if (strTemp.length() > 0) {
					String[] tab = strTemp.split("\\s");
					if (tab.length == 2) {
						ConfigRecord.gapRegex = Pattern.compile(tab[0]);
						ConfigRecord.absRel = tab[1].toUpperCase();
					} else {
						ConfigRecord.gapRegex = Pattern.compile(strTemp);
						ConfigRecord.absRel = "ABS";
					}
				}
				ConfigRecord.unitStep = tmpProps.getProperty("logFouineur.unitStep");
			}

		}
		// File Properties OK

		ConfigRecord.aggregPeriodInMillis = Long.parseLong(tmpProps.getProperty("logFouineur.stepAgg", "1000")); // 1
		ConfigRecord.repOut = tmpProps.getProperty("logFouineur.fileOut", "default"); // s
		// by
		if (tmpProps.getProperty("logFouineur.parsing").equals("true")) {
			ConfigRecord.boolExhaustiveParsing = true;
		} else
			ConfigRecord.boolExhaustiveParsing = false;

		ConfigRecord.csvSeparatorOut = tmpProps.getProperty("logFouineur.csvSeparator", ";");
		// default
		ConfigRecord.boolCompactOutput = Boolean
				.parseBoolean(tmpProps.getProperty("logFouineur.compactOutput", "false"));
		ConfigRecord.decalDateDuration = Long.parseLong(tmpProps.getProperty("logFouineur.correctDate"));

		ConfigRecord.showSummary = Boolean.parseBoolean(tmpProps.getProperty("logFouineur.showSummary", "false"));
		/*
		 * 0 => no correction 1=> adding duration if duration -1 => substract duration
		 * if any
		 */

		ConfigRecord.decalAllDatesInMillis = translate(tmpProps.getProperty("logFouineur.tz", "0"));
		/*
		 * Format of tz can be a long with the regex ^(\+|-)?\d+$ directement en
		 * millseconds ou bien un format du type : ^(\+|-)?\d*H?\d*m?\d*s\d+*
		 * 
		 * 
		 */

		ConfigRecord.isStartTimestamp = Boolean
				.parseBoolean(tmpProps.getProperty("logFouineur.isStartTimestamp", "true"));
		ConfigRecord.sdfOut = new SimpleDateFormat(
				tmpProps.getProperty("logFouineur.csvJavaFormatDate", "yyyy/MM/dd:HH:mm:ss"));
		ConfigRecord.boolGetDateWhenReading = Boolean
				.parseBoolean(tmpProps.getProperty("logFouineur.getDateWhenReading", "true"));

		ConfigRecord.confOutputFile = tmpProps.getProperty("logFouineur.confOutputFile", "All+Avg");
		
		// file properties OK
	
	}
	
	/**
	 * Fill config record.
	 *
	 * @param strConfigPropertiesFile
	 *            the str config properties file
	 * @throws ParseException
	 *             the parse exception
	 */
	private void fillConfigRecord(String strConfigPropertiesFile) throws ParseException {

		Properties tmpProps = new Properties();
		System.out.println("fillConfigRecord from " + strConfigPropertiesFile);
		// file Props OK
		try (FileInputStream fis = new FileInputStream(strConfigPropertiesFile)) {
			tmpProps.load(fis);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 fillConfigRecord(tmpProps);
		// file Props OK

		
	}

	/**
	 * Translate.
	 *
	 * @param property
	 *            the property
	 * @return the long
	 */
	private long translate(String property) {
		/*
		 * Format of tz can be a long with the regex ^(\+|-)?\d+$ directement en
		 * millseconds ou bien un format du type : ^(\+|-)?\d*H?\d*m?\d*s\d+$
		 * 
		 * 
		 */
		Pattern pat1 = Pattern.compile("^(\\+|-)?\\d+$");
		Matcher match1 = pat1.matcher(property);

		if (match1.find()) {
			return Long.parseLong(match1.group());
		}

		Pattern pat2 = Pattern.compile("^(\\+|-)?\\d+$");
		Matcher match2 = pat2.matcher(property);
		long retAbs = 0L;
		long sign = 1;
		if (property.trim().startsWith("-"))
			sign = -1;
		if (match2.find()) {

			if (property.contains("H")) {
				Matcher match3 = Pattern.compile("\\d+H").matcher(property);
				if (match3.find()) {
					String str = match3.group();
					String strToparse = str.substring(0, str.length() - 1);
					retAbs += Long.parseLong(strToparse) * 3600 * 1000;
				}

			}
			if (property.contains("m")) {
				Matcher match3 = Pattern.compile("\\d+m").matcher(property);
				if (match3.find()) {
					String str = match3.group();
					String strToparse = str.substring(0, str.length() - 1);
					retAbs += Long.parseLong(strToparse) * 60 * 1000;
				}

			}
			if (property.contains("s")) {
				Matcher match3 = Pattern.compile("\\d+s").matcher(property);
				if (match3.find()) {
					String str = match3.group();
					String strToparse = str.substring(0, str.length() - 1);
					retAbs += Long.parseLong(strToparse) * 1000;
				}

			}
			Matcher match3 = Pattern.compile("\\d+$").matcher(property);
			if (match3.find()) {
				String str = match3.group();
				String strToparse = str.substring(0, str.length() - 1);
				retAbs += Long.parseLong(strToparse);
			}

			return sign * retAbs;
		} else
			return 0;
	}

	/**
	 * Load props.
	 *
	 * @param strFile
	 *            the str file
	 */
	private void loadProps(String strFile) {
		LogFouineurMain.currentScenarioProps.clear();
		if (new File(strFile).exists()) {
			try (FileInputStream fis = new FileInputStream(strFile)) {
				LogFouineurMain.currentScenarioProps.load(fis);
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
	 * Load from config.
	 */
	public void loadFromConfig() {
		loadProps(pathRoot + "config" + File.separator + "parselog" + File.separator + nameFileBasic + ".properties");
		fillFields();

	}

	/**
	 * Fill fields.
	 */
	private void fillFields() {
		// Filling all fields
		LogFouineurFill.tfStepAgg
				.setText(LogFouineurMain.currentScenarioProps.getProperty("logFouineur.stepAgg", "1000"));
		LogFouineurFill.tfStartRegex
				.setText(LogFouineurMain.currentScenarioProps.getProperty("logFouineur.startRegex", ""));
		LogFouineurFill.tfEndRegex
				.setText(LogFouineurMain.currentScenarioProps.getProperty("logFouineur.endRegex", ""));
		LogFouineurFill.tfIncludeRecord
				.setText(LogFouineurMain.currentScenarioProps.getProperty("logFouineur.includeRecord", ""));
		LogFouineurFill.tfExcludeRecord
				.setText(LogFouineurMain.currentScenarioProps.getProperty("logFouineur.excludeRecord", ""));
		if (LogFouineurMain.currentScenarioProps.getProperty("logFouineur.explicitDate").equals("true"))
			LogFouineurFill.rbExplicitDate.setSelected(true);
		else
			LogFouineurFill.rbExplicitDate.setSelected(false);

		if (LogFouineurMain.currentScenarioProps.getProperty("logFouineur.numLocale").equals("true"))
			LogFouineurFill.rbNumLocale.setSelected(true);
		else
			LogFouineurFill.rbNumLocale.setSelected(false);
		if (LogFouineurMain.currentScenarioProps.getProperty("logFouineur.explicitDate").equals("false")) {
			LogFouineurFill.rbImplicitStep.setDisable(false);
			LogFouineurFill.rbGapRegex.setDisable(false);
			LogFouineurFill.tfStepExtract.setDisable(false);
			LogFouineurFill.tfStepExtract.setEditable(true);
			LogFouineurFill.cbUnitStep.setDisable(false);

			if (LogFouineurMain.currentScenarioProps.getProperty("logFouineur.implicitStep").equals("true"))
				LogFouineurFill.rbImplicitStep.setSelected(true);
			else
				LogFouineurFill.rbImplicitStep.setSelected(false);
			if (LogFouineurMain.currentScenarioProps.getProperty("logFouineur.gapRegex").equals("true"))
				LogFouineurFill.rbGapRegex.setSelected(true);
			else
				LogFouineurFill.rbGapRegex.setSelected(false);
			LogFouineurFill.tfStepExtract.setEditable(true);
			LogFouineurFill.tfStepExtract
					.setText(LogFouineurMain.currentScenarioProps.getProperty("logFouineur.stepExtract", ""));

			LogFouineurFill.cbUnitStep
					.setValue(LogFouineurMain.currentScenarioProps.getProperty("logFouineur.unitStep"));
		}
		LogFouineurFill.tfOriginDate
				.setText(LogFouineurMain.currentScenarioProps.getProperty("logFouineur.originDate", ""));

		LogFouineurFill.tfRegexDate
				.setText(LogFouineurMain.currentScenarioProps.getProperty("logFouineur.regexDate", ""));
		LogFouineurFill.tfJavaFormatDate
				.setText(LogFouineurMain.currentScenarioProps.getProperty("logFouineur.javaFormatDate", ""));
		LogFouineurFill.tfStartDate
				.setText(LogFouineurMain.currentScenarioProps.getProperty("logFouineur.startDate", ""));
		LogFouineurFill.tfEndDate.setText(LogFouineurMain.currentScenarioProps.getProperty("logFouineur.endDate", ""));
		LogFouineurFill.tfCsvSeparator
				.setText(LogFouineurMain.currentScenarioProps.getProperty("logFouineur.csvSeparator", ";"));
		LogFouineurFill.cbConfOutputFile
				.setValue(LogFouineurMain.currentScenarioProps.getProperty("logFouineur.confOutputFile", "All+Avg"));
		if (LogFouineurMain.currentScenarioProps.getProperty("logFouineur.numLocaleOut").equals("true"))
			LogFouineurFill.rbNumLocaleOut.setSelected(true);
		else
			LogFouineurFill.rbNumLocaleOut.setSelected(false);
		LogFouineurFill.tfThreads.setText(LogFouineurMain.currentScenarioProps.getProperty("logFouineur.threads", "0"));
		LogFouineurFill.tfTz.setText(LogFouineurMain.currentScenarioProps.getProperty("logFouineur.tz", "0"));
		if (LogFouineurMain.currentScenarioProps.getProperty("logFouineur.isStartTimestamp").equals("true"))
			LogFouineurFill.rbStDate.setSelected(true);
		else
			LogFouineurFill.rbStDate.setSelected(false);
		if (LogFouineurMain.currentScenarioProps.getProperty("logFouineur.showSummary", "false").equals("true"))
			LogFouineurFill.rbDirectShow.setSelected(true);
		else
			LogFouineurFill.rbDirectShow.setSelected(false);
		if (LogFouineurMain.currentScenarioProps.getProperty("logFouineur.parsing").equals("true"))
			LogFouineurFill.rbParsing.setSelected(true);
		else
			LogFouineurFill.rbParsing.setSelected(false);
		if (LogFouineurMain.currentScenarioProps.getProperty("logFouineur.trace").equals("true"))
			LogFouineurFill.rbTrace.setSelected(true);
		else
			LogFouineurFill.rbTrace.setSelected(false);

		if (LogFouineurMain.currentScenarioProps.getProperty("logFouineur.getDateWhenReading", "true").equals("true"))
			LogFouineurFill.rbGetDateWhenReading.setSelected(true);
		else
			LogFouineurFill.rbGetDateWhenReading.setSelected(false);

		if (LogFouineurMain.currentScenarioProps.getProperty("logFouineur.compactOutput", "false").equals("true"))
			LogFouineurFill.rbCompactOutput.setSelected(true);
		else
			LogFouineurFill.rbCompactOutput.setSelected(false);
		LogFouineurFill.tfCsvJavaFormatDate.setText(LogFouineurMain.currentScenarioProps
				.getProperty("logFouineur.csvJavaFormatDate", "yyyy/MM/dd:HH:mm:ss"));
		LogFouineurFill.cbCorrectDate
				.setValue(LogFouineurMain.currentScenarioProps.getProperty("logFouineur.correctDate", "0"));

		// Fill Value Table Vue
		boolean vrai = true;
		ObservableList<JFXValue> lstValue = LogFouineurFill.tableValue.getItems();
		int i = 0;
		while (vrai) {
			if (LogFouineurMain.currentScenarioProps.containsKey("logFouineur.value." + i + ".name")) {
				String name = LogFouineurMain.currentScenarioProps.getProperty("logFouineur.value." + i + ".name");
				String regex1 = LogFouineurMain.currentScenarioProps.getProperty("logFouineur.value." + i + ".regex1");
				String regex2 = LogFouineurMain.currentScenarioProps.getProperty("logFouineur.value." + i + ".regex2");
				String unit = LogFouineurMain.currentScenarioProps.getProperty("logFouineur.value." + i + ".unit");
				double scale = Double
						.valueOf(LogFouineurMain.currentScenarioProps.getProperty("logFouineur.value." + i + ".scale"));
				boolean isDuration = Boolean.parseBoolean(LogFouineurMain.currentScenarioProps
						.getProperty("logFouineur.value." + i + ".isDuration", "false"));
				JFXValue value = new JFXValue(name, regex1, regex2, unit, scale, isDuration);
				lstValue.set(i, value);
				i++;
			} else
				vrai = false;

		}
		LogFouineurFill.tableValue.setItems(lstValue);

		// Fill Pivot Table Vue
		vrai = true;
		ObservableList<JFXPivot> lstPivot = LogFouineurFill.tablePivot.getItems();
		i = 0;
		while (vrai) {
			if (LogFouineurMain.currentScenarioProps.containsKey("logFouineur.pivot." + i + ".name")) {
				String name = LogFouineurMain.currentScenarioProps.getProperty("logFouineur.pivot." + i + ".name");
				String regex1 = LogFouineurMain.currentScenarioProps.getProperty("logFouineur.pivot." + i + ".regex1");
				String regex2 = LogFouineurMain.currentScenarioProps.getProperty("logFouineur.pivot." + i + ".regex2");

				JFXPivot pivot = new JFXPivot(name, regex1, regex2);
				lstPivot.set(i, pivot);
				i++;
			} else
				vrai = false;

		}
		LogFouineurFill.tablePivot.setItems(lstPivot);

	}

	/**
	 * Load from template.
	 */
	public void loadFromTemplate() {

		loadProps(LogFouineurMain.parseLocTemplate.trim() + LogFouineurMain.parseGenTemplate.trim());
		fillFields();
	}

}
