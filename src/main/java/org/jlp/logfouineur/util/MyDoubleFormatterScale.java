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

import java.util.Locale;

import javafx.util.StringConverter;

// TODO: Auto-generated Javadoc
/**
 * The Class MyLongToDateConverter.
 */
public class MyDoubleFormatterScale extends StringConverter<Double> {

	/**
	 * From string.
	 *
	 * @param aNumber the a number
	 * @return the double
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.util.StringConverter#fromString(java.lang.String)
	 */
	@Override
	public Double fromString(String aNumber) {
		Locale.setDefault(Locale.ENGLISH);
		if(aNumber.length()==0) return 0.0;
		return Double.valueOf(aNumber);
	}

	/**
	 * To string.
	 *
	 * @param aNumber the a number
	 * @return the string
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.util.StringConverter#toString(java.lang.Object)
	 */
	@Override
	public String toString(Double aNumber) {
		Locale.setDefault(Locale.ENGLISH);
		if(aNumber==0d) return "";
		if (Math.abs(aNumber) < 1000000) {
			return String.format("%6.2f", aNumber);
		} else {
			return String.format("%4.2E", aNumber);
		}

	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String args[]) {

		Double d = -50.5;
		Double d2 = -10000000000d;
		MyDoubleFormatterScale fmt = new MyDoubleFormatterScale();
		System.out.println(d + " => " + fmt.toString(d));
		System.out.println(d2 + " => " + fmt.toString(d2));
	}

}
