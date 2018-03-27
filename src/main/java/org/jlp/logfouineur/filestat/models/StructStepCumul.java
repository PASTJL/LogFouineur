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
package org.jlp.logfouineur.filestat.models;

// TODO: Auto-generated Javadoc
/**
 * The Class StructStepCumul.
 */
public class StructStepCumul {
	
	/** The moyenne. */
	public Double moyenne;
	
	/** The nb count. */
	public Integer nbCount;
	
	/**
	 * Instantiates a new struct step cumul.
	 *
	 * @param count0 the count 0
	 * @param moyen the moyen
	 */
	public StructStepCumul( Integer count0, Double moyen) {
		  moyenne=moyen;
		 nbCount=count0;
	}
	 
 	/**
 	 * Adds the.
 	 *
 	 * @param value the value
 	 * @return the struct step cumul
 	 */
 	public StructStepCumul  add( Double value){
				moyenne = ((moyenne * nbCount + value) / (nbCount + 1));
				nbCount = nbCount + 1;
				return this;
			}

	 /**
 	 * Merge.
 	 *
 	 * @param other the other
 	 * @return the struct step cumul
 	 */
 	public StructStepCumul merge( StructStepCumul other) {
				Double  sommeGlobal = nbCount * moyenne + other.nbCount * other.moyenne;
				nbCount += other.nbCount;
				moyenne = sommeGlobal / nbCount;
				return this;
			}

}
