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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: Auto-generated Javadoc
/**
 * The Class TestRegexp.
 */
public class TestRegexp {
	
	/** The result groups. */
	private String resultGroups = "Gives all groups for every pattern matching \n";
	  
	  /**
  	 * Instantiates a new test regexp.
  	 *
  	 * @param paramString1 the param string 1
  	 * @param paramString2 the param string 2
  	 */
  	public TestRegexp(String paramString1, String paramString2)
	  {
	    Pattern localPattern = Pattern.compile(paramString2);
	    Matcher localMatcher = localPattern.matcher(paramString1);
	    int i = 0;
	    while (localMatcher.find() == true)
	    {
	      String str = "";
	      int j = 0;
	      for (j = 0; j <= localMatcher.groupCount(); j++)
	      {
	        str = localMatcher.group(j);
	        this.resultGroups = (this.resultGroups + " group[" + (j + i) + "]=" + str + "\n");
	      }
	      i += j;
	    }
	  }
	  
	  /**
  	 * Gets the result groups.
  	 *
  	 * @return the result groups
  	 */
  	public String getResultGroups()
	  {
	    return this.resultGroups;
	  }
	  
	  /**
  	 * Sets the result groups.
  	 *
  	 * @param paramString the new result groups
  	 */
  	public void setResultGroups(String paramString)
	  {
	    this.resultGroups = paramString;
	  }
	

}
