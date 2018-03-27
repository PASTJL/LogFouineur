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
package org.jlp.logfouineur.filestat.disruptor;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



import org.jlp.logfouineur.filestat.models.CumulEnregistrementStat;
import org.jlp.logfouineur.filestat.ui.DiagFileStats;
import java.lang.reflect.InvocationTargetException;

import com.lmax.disruptor.EventHandler;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

// TODO: Auto-generated Javadoc
/**
 * The Class FileStatsLineHandlerDated.
 */
public class FileStatsLineHandlerDated implements EventHandler<FileStatEvent> {
	
	/** The id. */
	public int id = 0;
	

	/** The is by column. */
	public String isByColumn = "false";
	
	/** The has plugin. */
	public boolean hasPlugin = false;
	
	/** The csv sep. */
	public String csvSep = ";";
	
	/** The num col piv. */
	int numColPiv = -1;
	
	/** The num col val. */
	int numColVal = -1;
	
	/** The reg piv 1. */
	String regPiv1 = "";
	
	/** The reg piv 2. */
	String regPiv2 = "";
	
	/** The reg val 1. */
	String regVal1 = "";
	
	/** The reg val 2. */
	String regVal2 = "";
	
	/** The scale. */
	Double scale = 1.0;
	
	/** The pas. */
	public Double pas = 0.0;
	
	/** The sdf. */
	public SimpleDateFormat sdf;
	
	/** The pat date. */
	Pattern patDate;
	
	/** The date begin. */
	public Date dateBegin;
	
	/** The date end. */
	public Date dateEnd;
	
	/** The ldate begin. */
	public long ldateBegin;
	
	/** The ldate end. */
	public long ldateEnd;
	/** The local hm. */
	public HashMap<String, CumulEnregistrementStat> hmLocalCumul = new HashMap<String, CumulEnregistrementStat>();

	/**
	 * Instantiates a new file stats line handler dated.
	 */
	public FileStatsLineHandlerDated() {
		this.sdf = new SimpleDateFormat(DiagFileStats.javaDateFormat);
		patDate = Pattern.compile(DiagFileStats.regexDate);
	
		try {
		dateBegin = sdf.parse(DiagFileStats.tfBegAnalyse.getText().trim());
		dateEnd = sdf.parse(DiagFileStats.tfEndAnalyse.getText().trim());
		}
		catch(ParseException pe ) {
			
		}
		
		ldateBegin = dateBegin.getTime();
		ldateEnd = dateEnd.getTime();
		
		if (DiagFileStats.fileStatProps.getProperty("filestat.bycolumn", "false").trim().equals("true")) {
			isByColumn = "true";
			csvSep = DiagFileStats.fileStatProps.getProperty("filestat.sepcolumn", ";");
			numColPiv = Integer.parseInt(DiagFileStats.fileStatProps.getProperty("filestat.pivregex1", "-1"));
			numColVal = Integer.parseInt(DiagFileStats.fileStatProps.getProperty("filestat.valregex1", "-1"));
		} else {
			isByColumn = "false";
			regPiv1 = DiagFileStats.fileStatProps.getProperty("filestat.pivregex1", "");
			regVal1 = DiagFileStats.fileStatProps.getProperty("filestat.valregex1", "");
		}

		if (DiagFileStats.fileStatProps.getProperty("filestat.valregex1", "").trim().toLowerCase()
				.startsWith("plugin=")) {
			hasPlugin = true;
		} else {
			hasPlugin = false;
		}
		regPiv2 = DiagFileStats.fileStatProps.getProperty("filestat.pivregex2", "");
		regVal2 = DiagFileStats.fileStatProps.getProperty("filestat.valregex2", "");
		scale = Double.parseDouble(DiagFileStats.fileStatProps.getProperty("filestat.scaleval", "1.0"));
		pas = Double.parseDouble(
				DiagFileStats.fileStatProps.getProperty("filestat.steppercent", "0.0").replaceAll(",", "."));
		System.out.println("FileStatsLineHandlerDated DiagFileStats.nbThreads ====>" + DiagFileStats.nbThreads);
		
	}

	

	/**
	 * On event.
	 *
	 * @param event the event
	 * @param sequence the sequence
	 * @param endOfBatch the end of batch
	 * @throws Exception the exception
	 */
	@Override
	public void onEvent(FileStatEvent event, long sequence, boolean endOfBatch) throws Exception {
		if (sequence % DiagFileStats.nbThreads == id) {
			// First found the Pivot with the Regexp or column

			if (!isDateCorrect(event.getContent())) {
				//System.out.println("badDate  line => "+event.getContent());
				DiagFileStats.tabTreated[id]=DiagFileStats.tabTreated[id]+1;
				DiagFileStats.tabEliminated[id]=DiagFileStats.tabEliminated[id]+1;
				return;
			}
			String pivot = findPivot(event.getContent());
			if (null == pivot) {
				//System.out.println("badPivot line => "+event.getContent());
				DiagFileStats.tabTreated[id]=DiagFileStats.tabTreated[id]+1;
				DiagFileStats.tabEliminated[id]=DiagFileStats.tabEliminated[id]+1;
				return;
			}
			Double value = findValue(event.getContent());

			if (null == value || value == Double.NaN) {
				//System.out.println("badValueline =>" +event.getContent());
				DiagFileStats.tabTreated[id]=DiagFileStats.tabTreated[id]+1;
				DiagFileStats.tabEliminated[id]=DiagFileStats.tabEliminated[id]+1;
				return;
			}

			// Retrieve or create the pair key/object in hmLocalCumul
			if (hmLocalCumul.containsKey(pivot)) {
				CumulEnregistrementStat cumulRec = hmLocalCumul.get(pivot);
				cumulRec.add(value, pas);
				hmLocalCumul.put(pivot, cumulRec);
			} else {
				// Create new :
				CumulEnregistrementStat cumulRec = new CumulEnregistrementStat();
				cumulRec.setName(pivot);

				cumulRec.add(value, pas);
				hmLocalCumul.put(pivot, cumulRec);
			}
			DiagFileStats.tabFiltered[id]=DiagFileStats.tabFiltered[id]+1;
			DiagFileStats.tabTreated[id]=DiagFileStats.tabTreated[id]+1;
		}

	}

	/**
	 * Checks if is date correct.
	 *
	 * @param line the line
	 * @return true, if is date correct
	 */
	private boolean isDateCorrect(String line) {
		if (line.trim().length() < 5)
			return false;
		// if (null == sdf) {
		// if (DiagFileStats.firstAlert) {
		// Alert alert = new Alert(AlertType.INFORMATION);
		//
		// alert.setResizable(true);
		//
		// alert.setTitle("SimpleDateFormat is null");
		//
		// alert.setHeaderText(
		// "An error occurs because SimpleDateFormat sdf is null in DiagFileStats");
		//
		// alert.showAndWait();
		// DiagFileStats.firstAlert = false;
		// }
		// return false;
		// }
		//
		Matcher matcher = patDate.matcher(line);
		if (matcher.find()) {
			String strDate = matcher.group();
			try {

				// Date date = sdf.parse(strDate);
				long ldate = sdf.parse(strDate).getTime();
				// if (date.before(dateBegin)
				// || date.after(dateEnd)) {
				// System.out.println ("horsDate");
				if (ldate < ldateBegin || ldate > ldateEnd) {
					
					return false;
				} else {
					return true;
				}
			} catch (ParseException e) {
				return false;
			} catch (NumberFormatException e) {
				System.out.println("Erreur line = " + line + " strDate =" + strDate);
				System.out.println("patDate =" + patDate.pattern() + " ;Format =" + sdf.toPattern());
				return false;
			}

		} else {
			return false;
		}
	}

	/**
	 * Find value.
	 *
	 * @param event the event
	 * @return the double
	 */
	private Double findValue(String event) {
		/* returnValue is scaled */
		Double returnValue = null;
		if (hasPlugin) {
			returnValue = findValueWithPlugin(event);
		} else {
			returnValue = findValueNoPlugin(event);
		}
		if (null != returnValue && returnValue != Double.NaN) {
			/* returnValue is scaled */
			returnValue *= scale;
		}
		return returnValue;
	}

	/**
	 * Find value with plugin.
	 *
	 * @param event the event
	 * @return the double
	 */
	private Double findValueWithPlugin(String event) {
		// always by regexp, never by column
		Double returnValue = null;

		String nomClasse = "plugins." + regVal1.split("=")[1];

		Method met = DiagFileStats.hmapsMethod[id].get(nomClasse);
		Object obj = DiagFileStats.hmapsClass[id].get(nomClasse);
		try {
			returnValue = (Double) met.invoke(obj, new Object[] { event });
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return returnValue;
	}

	/**
	 * Find value no plugin.
	 *
	 * @param event the event
	 * @return the double
	 */
	private Double findValueNoPlugin(String event) {
		Double returnValue = null;
		switch (isByColumn) {
		case "true":
			String fullVal = event.split(csvSep)[numColVal];
			if (regVal2.trim().length() > 0) {
				Matcher matcher = Pattern.compile(regVal2).matcher(fullVal);
				if (matcher.find()) {
					try {
						returnValue = Double.parseDouble(matcher.group().replaceAll(",", "."));
					} catch (NumberFormatException nfe) {
						returnValue = Double.NaN;
					}
				} else {
					returnValue = Double.NaN;
				}
			} else {
				try {
					returnValue = Double.parseDouble(fullVal.replaceAll(",", "."));
				} catch (NumberFormatException nfe) {
					returnValue = Double.NaN;
				}
			}

			break;
		case "false":
			Matcher matcher = Pattern.compile(regVal1).matcher(event);
			if (matcher.find()) {

				if (regVal2.trim().length() > 0) {

					Matcher matcher2 = Pattern.compile(regVal2).matcher(matcher.group());
					if (matcher2.find()) {
						try {

							returnValue = Double.parseDouble(matcher2.group().replaceAll(",", "."));
						} catch (NumberFormatException nfe) {
							returnValue = Double.NaN;
						}
					} else {
						returnValue = Double.NaN;
					}
				} else {
					try {

						returnValue = Double.parseDouble(matcher.group().replaceAll(",", "."));
					} catch (NumberFormatException nfe) {
						returnValue = Double.NaN;
					}
				}

			} else {
				returnValue = Double.NaN;
			}

			break;
		}
		return returnValue;
	}

	/**
	 * Find pivot.
	 *
	 * @param event the event
	 * @return the string
	 */
	private String findPivot(String event) {
		// TODO Auto-generated method stub
		String returnPivot = "";
		switch (isByColumn) {
		case "true":
			String fullPivot = event.split(csvSep)[numColPiv];
			if (regPiv2.trim().length() > 0) {
				Matcher matcher = Pattern.compile(regPiv2).matcher(fullPivot);
				if (matcher.find()) {
					returnPivot = matcher.group();
				} else {
					returnPivot = null;
				}
			} else {
				returnPivot = fullPivot;
			}

			break;
		case "false":
			Matcher matcher = Pattern.compile(regPiv1).matcher(event);
			if (matcher.find()) {
				fullPivot = matcher.group();
				if (regPiv2.trim().length() > 0) {
					Matcher matcher2 = Pattern.compile(regPiv2).matcher(fullPivot);
					if (matcher2.find()) {
						returnPivot = matcher2.group();
					} else {
						returnPivot = null;
					}
				} else {
					returnPivot = fullPivot;
				}
			}

			else {
				returnPivot = null;
			}

			break;

		}
		return returnPivot;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(int id) {
		// TODO Auto-generated method stub
		this.id = id;
	}
}
