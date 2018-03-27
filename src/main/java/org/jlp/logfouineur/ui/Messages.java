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
package org.jlp.logfouineur.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

// TODO: Auto-generated Javadoc
/**
 * The Class Messages.
 */
public class Messages {
	
	/** The Constant BUNDLE_NAME. */
	private static final String BUNDLE_NAME = "org.jlp.logfouineur.ui.messages"; //$NON-NLS-1$
 
	/** The Constant RESOURCE_BUNDLE. */
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME+"_"+
	java.util.Locale.getDefault().toLanguageTag()+"-"+java.util.Locale.getDefault().toLanguageTag().toUpperCase());

	/**
	 * Instantiates a new messages.
	 */
	private Messages() {
		System.out.println( "Locale Messages ="+	java.util.Locale.getDefault().toLanguageTag());
	}

	/**
	 * Gets the string.
	 *
	 * @param key the key
	 * @return the string
	 */
	public static String getString(String key) {
		try {
			
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
