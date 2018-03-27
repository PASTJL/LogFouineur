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

// TODO: Auto-generated Javadoc
/**
 * The Class MathUtils.
 */
public class MathUtils {
	
	/**
	 * Gets the mean.
	 *
	 * @param tabDbl the tab dbl
	 * @return the mean
	 */
	static double getMean(Double[] tabDbl) {
		
	        double sum = 0.0;
	        for(double a : tabDbl)
	            sum += a;
	        return sum/tabDbl.length;
	    }
	
	/**
	 * Gets the variance.
	 *
	 * @param tabDbl the tab dbl
	 * @return the variance
	 */
	static public Double getVariance(Double[] tabDbl) {
	
		double mean = getMean(tabDbl);
        double temp = 0;
        for(double a :tabDbl)
            temp += (a-mean)*(a-mean);
        return temp/(tabDbl.length-1);
	   
	}
//	static public Double getStdDev() {
//		System.out.println("MathUtils call getStdDev test ");
//        return 0.0;
//    }
	
	/**
 * Gets the std dev.
 *
 * @param tabDbl the tab dbl
 * @return the std dev
 */
static public Double getStdDev(Double[] tabDbl) {
		
        return Math.sqrt(getVariance(tabDbl));
    }
}
