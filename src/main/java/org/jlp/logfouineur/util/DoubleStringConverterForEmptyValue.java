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

import javafx.util.converter.DoubleStringConverter;

// TODO: Auto-generated Javadoc
/**
 * The Class DoubleStringConverterForEmptyValue.
 */
public class DoubleStringConverterForEmptyValue extends DoubleStringConverter {
	
	/**
	 * To string.
	 *
	 * @param value the value
	 * @return the string
	 */
	/* (non-Javadoc)
	 * @see javafx.util.converter.DoubleStringConverter#toString(java.lang.Double)
	 */
	@Override 
	public String toString(Double value){
		if (value == 0.0 || value.isNaN()) return "";
		return value.toString();
	}
	
	/**
	 * From string.
	 *
	 * @param strDouble the str double
	 * @return the double
	 */
	/* (non-Javadoc)
	 * @see javafx.util.converter.DoubleStringConverter#fromString(java.lang.String)
	 */
	@Override 
	public Double fromString(String strDouble){
		Double ret=Double.NaN;
		strDouble=strDouble.replace(",",".");
		try{
		ret=Double.valueOf(strDouble);
		}
		catch(NumberFormatException nfe){
			ret=0.0;
		}
		return ret;
	}
}
