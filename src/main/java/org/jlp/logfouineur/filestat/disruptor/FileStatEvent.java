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

// TODO: Auto-generated Javadoc
/**
 * The Class FileStatEvent.
 */
public class FileStatEvent {
	
	/** The content. */
	String content="";
	
	/**
	 * Gets the content.
	 *
	 * @return the content
	 */
	public String getContent() {
		return content;
	}
	
	/**
	 * Sets the content.
	 *
	 * @param content the new content
	 */
	public void setContent(String content) {
		this.content = content;
	}
	
	/**
	 * Sets the.
	 *
	 * @param content the content
	 */
	public void set(String content) {
		this.content=content;
	}
	
	/**
	 * Sets the.
	 *
	 * @param content the content
	 */
	public void set(FileStatEvent content) {
		this.content=content.getContent();
	}

}
