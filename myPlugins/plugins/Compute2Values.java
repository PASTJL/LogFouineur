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
package plugins;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: Auto-generated Javadoc
/**
 * The Class Compute2Values.
 */
public class Compute2Values implements IMyPlugins {
	String regex1_1,regex1_2,regex2_1,regex2_2;
	String op;
	
	

	/**
	 * Initialize.
	 */
	public void initialize(String strRegex2){
	/*	The first character is the separator for splitting
	 * Defining a tab of parameters param
	 * params[0] is the operation "+","-";"*";"/";"%";"<";">"
		params[1] is the regex1 to extract first number
		params[2] is the regex2 to extract first number
		params[3] is the regex1 to extract second number
		params[4] is the regex2 to extract second number
		
		*/
		
	
		/* retrieve separator*/
		String sep = strRegex2.substring(0, 1);
		
		String[] params = strRegex2.substring(1).split(sep);
	
		op=params[0];
		regex1_1=params[1];
		regex1_2=params[2];
		regex2_1=params[3];
		regex2_2=params[4];
		
	};
	
	/**
	 * Return double.
	 *
	 * @param params the params
	 * @return the double
	 */
	public Double returnDouble(String line){
		/**
		line is the record
		
		for regex a blank character " " means no regex
		
		*/
//		System.out.println("----------------------------------------------------------");
//		System.out.println("params.length"+params.length);
//		for (int i =0;i< params.length;i++) {
//			System.out.println("param["+i+"]="+ params[i] );
//		}
//		System.out.println("----------------------------------------------------------\n");
	
		Double ret=Double.NaN;
		double firstNumber=Double.NaN;
		double secondNumber=Double.NaN;
		
		// Extraction FirstNumber
		Matcher match1=Pattern.compile(regex1_1).matcher(line);
		if(match1.find()){
			String extract1=match1.group();
			if(regex1_2.trim().length()>0){
				Matcher match2=Pattern.compile(regex1_2).matcher(extract1);
				if(match2.find()){
					String extract2=match2.group();
					firstNumber=Double.parseDouble(extract2.replaceAll(",", "."));
					
				}else return Double.NaN;
				
			} else {
				firstNumber=Double.parseDouble(extract1.replaceAll(",", "."));
			}
			
		}else return Double.NaN;
		
		// Extraction SecondNumber
		Matcher match3=Pattern.compile(regex2_1).matcher(line);
		if(match3.find()){
			String extract1=match3.group();
			if(regex2_2.trim().length()>0){
				Matcher match4=Pattern.compile(regex2_2).matcher(extract1);
				if(match4.find()){
					String extract2=match4.group();
					secondNumber=Double.parseDouble(extract2.replaceAll(",", "."));
					
				}else return Double.NaN;
				
			} else {
				secondNumber=Double.parseDouble(extract1.replaceAll(",", "."));
			}
			
		}else return Double.NaN;
		
		// All 2 Number are correct compute now
		switch (op){
		case "+" :
			ret = firstNumber+secondNumber;
			break;
		case "-" :
			ret = firstNumber-secondNumber;
			break;
		case "*" :
			ret = firstNumber*secondNumber;
			break;
		case "/" :
			if (secondNumber == 0d) ret=Double.NaN;
			else  ret=firstNumber/secondNumber;
			
			break;
		case "%" :
			if (secondNumber == 0d) ret=Double.NaN;
			else  ret=firstNumber%secondNumber;
			
			break;
		case "<" :
			ret= Math.min(firstNumber, secondNumber);
			break;
		case ">" : 
			ret= Math.max(firstNumber, secondNumber);
			break;
			
				
		}
		
		return ret;
	}

}
