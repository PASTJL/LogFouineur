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
package org.jlp.logfouineur.util;

import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.TimeZone;

// TODO: Auto-generated Javadoc
/**
 * The Class DateInMillis.
 */
public class DateInMillis {

	/** The cal. */
	public Calendar cal = Calendar.getInstance();

	/** The tz. */
	public TimeZone tz = null;

	/**
	 * Date to number.
	 *
	 * @param paramString1
	 *            the param string 1 is the date to translate in millis
	 * @param paramString2
	 *            the param string 2 is the timezone +0200
	 * @return the string
	 */
	public String dateToNumber(String paramString1, String paramString2) {
		if ((paramString2 != null) && (paramString2.trim().length() == 5)) {
			this.tz = TimeZone.getTimeZone("GMT" + paramString2);
		} else {
			this.tz = TimeZone.getDefault();
		}
		this.cal.setTimeZone(this.tz);
		if (paramString1.contains(":")) {
		// decoupage date/heure 
		StringTokenizer localStringTokenizer1 = new StringTokenizer(paramString1, ":");
		
		StringTokenizer localStringTokenizer2 = new StringTokenizer(localStringTokenizer1.nextToken(), "/");
		// traitement Dtate
		this.cal.set(1, Integer.parseInt(localStringTokenizer2.nextToken())); //annee
		this.cal.set(2, Integer.parseInt(localStringTokenizer2.nextToken()) - 1);// mois => dans cal janvier=0 decembre=11 
		this.cal.set(5, Integer.parseInt(localStringTokenizer2.nextToken())); // jour
		
		// traitement Heure 
		this.cal.set(11, Integer.parseInt(localStringTokenizer1.nextToken())); // Heure 
		this.cal.set(12, Integer.parseInt(localStringTokenizer1.nextToken()));// mn
		if ( paramString1.contains(".")) {
			StringTokenizer localStringTokenizer3 = new StringTokenizer(localStringTokenizer1.nextToken(), ".");
			this.cal.set(13, Integer.parseInt(localStringTokenizer3.nextToken())); // secondes
			// millis must be in 3 digits
			String ms=localStringTokenizer3.nextToken();
			while (ms.length()<3) {
				ms=ms+"0";
			}
			this.cal.set(14, Integer.parseInt(ms) ); // millis
		}
		else
		{
			this.cal.set(13, Integer.parseInt(localStringTokenizer1.nextToken()));// secondes
			this.cal.set(14, 0); // millis
			
		}
		
		return Long.toString(this.cal.getTimeInMillis());
		}
		else {
			// treat only with a date in format yyyy/MM/dd with 00:00:00.000 as default hour
			StringTokenizer localStringTokenizer2 = new StringTokenizer(paramString1, "/");
			// traitement Dtate
			this.cal.set(1, Integer.parseInt(localStringTokenizer2.nextToken())); //annee
			this.cal.set(2, Integer.parseInt(localStringTokenizer2.nextToken()) - 1);// mois => dans cal janvier=0 decembre=11 
			this.cal.set(5, Integer.parseInt(localStringTokenizer2.nextToken())); // jour
			
			// traitement Heure 
			this.cal.set(11, 0); // Heure 
			this.cal.set(12, 0);// mn
			this.cal.set(13, 0);// secondes
			this.cal.set(14, 0); // millis
			return Long.toString(this.cal.getTimeInMillis());
		}
	}

	/**
	 * Number to date.
	 *
	 * @param paramString1
	 *            the param string 1 is the dateInMillis to translate in human
	 *            readable date
	 * @param paramString2
	 *            the param string 2 is the timezone +0200
	 * @return the string
	 */
	public String numberToDate(String paramString1, String paramString2) {
		if ((paramString2 != null) && (paramString2.trim().length() == 5)) {
			this.tz = TimeZone.getTimeZone("GMT" + paramString2);
		} else {
			this.tz = TimeZone.getDefault();
		}
		this.cal.setTimeZone(this.tz);
		this.cal.setTimeInMillis(Long.parseLong(paramString1));
		String str = Integer.toString(this.cal.get(1)) + "/" + Integer.toString(this.cal.get(2) + 101).substring(1)
				+ "/" + Integer.toString(this.cal.get(5) + 100).substring(1) + ":"
				+ Integer.toString(this.cal.get(11) + 100).substring(1) + ":"
				+ Integer.toString(this.cal.get(12) + 100).substring(1) + ":"
				+ Integer.toString(this.cal.get(13) + 100).substring(1) + "." + Integer.toString(this.cal.get(14));
		return str;
	}
}
