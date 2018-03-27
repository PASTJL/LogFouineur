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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

// TODO: Auto-generated Javadoc
/**
 * The Class JFXValue.
 */
public class JFXValue {
	
	/**
	 * Instantiates a new JFX value.
	 *
	 * @param name the name
	 * @param regex1 the regex 1
	 * @param regex2 the regex 2
	 * @param unit the unit
	 * @param scale the scale
	 * @param isDuration the is duration
	 */
	public JFXValue(String name, String regex1, String regex2, String unit,
			double scale, boolean isDuration) {
		super();
		this.name = new SimpleStringProperty(name);
		this.regex1 =  new SimpleStringProperty(regex1);
		this.regex2 = new SimpleStringProperty( regex2);
		this.unit = new SimpleStringProperty( unit);
		this.scale = new SimpleDoubleProperty( scale);
		this.isDuration=new SimpleBooleanProperty(isDuration);
	}

	/**
	 * Clone.
	 *
	 * @param toClone the to clone
	 * @return the JFX value
	 */
	public static JFXValue clone(JFXValue toClone) {
		JFXValue copy=new JFXValue();
		copy.name=new SimpleStringProperty(toClone.name.getValue());
		copy.regex1 =  new SimpleStringProperty(toClone.regex1.getValue());
		copy.regex2 = new SimpleStringProperty(toClone.regex2.getValue());
		copy.unit = new SimpleStringProperty(toClone.unit.getValue());
		copy.scale =new SimpleDoubleProperty(toClone.scale.getValue());
		copy.isDuration=new SimpleBooleanProperty(toClone.isDuration.getValue());
		return copy;
		
	}
	/**
	 * Instantiates a new JFX value.
	 */
	public JFXValue() {
		// TODO Auto-generated constructor stub
		this("","","","",0.0d,false);
	}

	/** The name. */
	StringProperty name;
	
	/** The regex 1. */
	StringProperty regex1;
	
	/** The regex 2. */
	StringProperty regex2;
	
	/** The unit. */
	StringProperty unit;
	
	/** The scale. */
	SimpleDoubleProperty scale;
	
	/** The is duration. */
	BooleanProperty isDuration;
	

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
	public final String getName() {
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
	
	/**
	 * Unit property.
	 *
	 * @return the string property
	 */
	public final StringProperty unitProperty() {
		return this.unit;
	}
	
	/**
	 * Gets the unit.
	 *
	 * @return the unit
	 */
	public final java.lang.String getUnit() {
		return this.unitProperty().get();
	}
	
	/**
	 * Sets the unit.
	 *
	 * @param unit the new unit
	 */
	public final void setUnit(final java.lang.String unit) {
		this.unitProperty().set(unit);
	}
	
	/**
	 * Scale property.
	 *
	 * @return the double property
	 */
	public final DoubleProperty scaleProperty() {
		return this.scale;
	}
	
	/**
	 * Gets the scale.
	 *
	 * @return the scale
	 */
	public final double getScale() {
		return this.scaleProperty().get();
	}
	
	/**
	 * Sets the scale.
	 *
	 * @param scale the new scale
	 */
	public final void setScale(final Double scale) {
		this.scaleProperty().set(scale);
	}

	/**
	 * Checks if is duration property.
	 *
	 * @return the boolean property
	 */
	public final BooleanProperty isDurationProperty() {
		return this.isDuration;
	}
	

	/**
	 * Checks if is checks if is duration.
	 *
	 * @return true, if is checks if is duration
	 */
	public final boolean isIsDuration() {
		return this.isDurationProperty().get();
	}
	

	/**
	 * Sets the checks if is duration.
	 *
	 * @param isDuration the new checks if is duration
	 */
	public final void setIsDuration(final boolean isDuration) {
		this.isDurationProperty().set(isDuration);
	}
	
	
	

}
