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

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

// TODO: Auto-generated Javadoc
/**
 * The Class JFXPivot.
 */
public class JFXPivot {
	
	/**
	 * Instantiates a new JFX pivot.
	 *
	 * @param name the name
	 * @param regex1 the regex 1
	 * @param regex2 the regex 2
	 */
	public JFXPivot(String name, String regex1, String regex2) {
		super();
		this.name = new SimpleStringProperty(name);
		this.regex1 =  new SimpleStringProperty(regex1);
		this.regex2 = new SimpleStringProperty( regex2);
		
	}
	
	/**
	 * Clone.
	 *
	 * @param toClone the to clone
	 * @return the JFX pivot
	 */
	public static JFXPivot clone(JFXPivot toClone) {
		JFXPivot copy=new JFXPivot();
		copy.name=new SimpleStringProperty(toClone.name.getValue());
		copy.regex1 =  new SimpleStringProperty(toClone.regex1.getValue());
		copy.regex2 = new SimpleStringProperty(toClone.regex2.getValue());
		
		return copy;
		
	}
	/**
	 * Instantiates a new JFX pivot.
	 */
	public JFXPivot() {
		// TODO Auto-generated constructor stub
		this("","","");
	}

	/** The name. */
	StringProperty name;
	
	/** The regex 1. */
	StringProperty regex1;
	
	/** The regex 2. */
	StringProperty regex2;
	
	/**
	 * Name property.
	 *
	 * @return the string property
	 */
	public final StringProperty nameProperty() {
		return this.name;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public final java.lang.String getName() {
		return this.nameProperty().get();
	}
	
	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public final void setName(final java.lang.String name) {
		this.nameProperty().set(name);
	}
	
	/**
	 * Regex 1 property.
	 *
	 * @return the string property
	 */
	public final StringProperty regex1Property() {
		return this.regex1;
	}
	
	/**
	 * Gets the regex 1.
	 *
	 * @return the regex 1
	 */
	public final java.lang.String getRegex1() {
		return this.regex1Property().get();
	}
	
	/**
	 * Sets the regex 1.
	 *
	 * @param regex1 the new regex 1
	 */
	public final void setRegex1(final java.lang.String regex1) {
		this.regex1Property().set(regex1);
	}
	
	/**
	 * Regex 2 property.
	 *
	 * @return the string property
	 */
	public final StringProperty regex2Property() {
		return this.regex2;
	}
	
	/**
	 * Gets the regex 2.
	 *
	 * @return the regex 2
	 */
	public final java.lang.String getRegex2() {
		return this.regex2Property().get();
	}
	
	/**
	 * Sets the regex 2.
	 *
	 * @param regex2 the new regex 2
	 */
	public final void setRegex2(final java.lang.String regex2) {
		this.regex2Property().set(regex2);
	}
	

	
	

}
