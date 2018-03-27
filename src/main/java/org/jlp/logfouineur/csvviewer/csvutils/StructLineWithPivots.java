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
package org.jlp.logfouineur.csvviewer.csvutils;

import java.util.Date;

// TODO: Auto-generated Javadoc
/**
 * The Class StructLineWithPivots.
 */
public class StructLineWithPivots {
	
	/** The date. */
	public Date date;
	
	/** The pivot. */
	public String pivot;
	
	/** The values. */
	public Double[] values;
	
	/**
	 * Instantiates a new struct line with pivots.
	 *
	 * @param date the date
	 * @param pivot the pivot
	 * @param values the values
	 */
	public StructLineWithPivots(Date date, String pivot, Double[] values) {
		super();
		this.date = date;
		this.pivot = pivot;
		this.values = values;
	}
	
	

}
