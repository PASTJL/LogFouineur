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
package org.jlp.logfouineur.records;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.regex.Pattern;

// TODO: Auto-generated Javadoc
/**
 * The Class ConfigRecord.
 */
public class ConfigRecord {
	
	/** The file to parse. */
	public static String fileToParse="";
	
	/** The begin record. */
	public static Pattern beginRecord = null;
	
	/** The end record. */
	public static Pattern endRecord = null;
	
	/** The monoline. */
	public static boolean  monoline=true;
	
	/** The begin equals end. */
	public static boolean beginEqualsEnd=false;
	
	/** The date begin of parsing. */
	public static Date dateBeginOfParsing=null;
	
	/** The date end of parsing. */
	public static Date dateEndOfParsing=null;
	
	/** The origin date. */
	public static Date originDate=Date.from(Instant.ofEpochMilli(0)); // set by default to 1970/01/01:00:00:00.000
	
	/** The regex include. */
	public static Pattern regexInclude=null;
	
	/** The regex exclude. */
	public static Pattern regexExclude =null;
	
	/** The Constant MYDATEFORMAT. */
	public final static SimpleDateFormat MYDATEFORMAT=new SimpleDateFormat("yyyy/MM/dd:HH:mm:ss.SSS");
	
	/** The regex date record. */
	public static Pattern regexDateRecord=null;
	
	/** The java date format record. */
	public static String javaDateFormatRecord=null;
	
	/** The factor date in millis. */
	public static long factorDateInMillis=1;
	
	/** The is date in millis. */
	public static boolean isDateInMillis=false;
	
	/** The bool explicit date. */
	public static boolean boolExplicitDate=true; // false => implicit date
	
	/** The step from origin. */
	public static long stepFromOrigin=0; // to use with implicit date  and origin date
	
	/** The step 2 records. */
	public static long step2Records=0; // to use with implicit date  and origin date
	
	/** The unit step. */
	public static String unitStep="millis";
	
	/** The bool implicit step. */
	public static boolean boolImplicitStep=false;// to use with implicit date  and origin date
	
	/** The aggreg period in millis. */
	public static long aggregPeriodInMillis=1000; // 1 s by default
	
	/** The bool exhaustive parsing. */
	public static boolean boolExhaustiveParsing=false;
	
	/** The bool compact output. */
	public static boolean boolCompactOutput=false;
	
	/** The csv separator out. */
	public static String csvSeparatorOut=";";
	
	/** The gap regex. */
	public static Pattern gapRegex=null;
	
	/** The decal date duration. */
	/* to adjust the date of the record to the beginning or the end of the action when duration is known
	 * 0 no change
	 * -1 duration is subtracted from the date of the record to obtain the new date of the record
	 * +1 duration is added from the date of the record to obtain the new date of the record
	 */
	public static long decalDateDuration = 0L;
	
	/** The decal all dates in millis. */
	/* to adjust the date of records useful when you can't handle the TimeZone easily, or to compare 
	 * 2 logs taken in 2 different days
	 * in millis
	 * 
	 */
	
	public static boolean isStartTimestamp=true;
	
	/** The decal all dates in millis. */
	public static long decalAllDatesInMillis = 0L;
	
	/** The sdf out. */
	public static SimpleDateFormat sdfOut;
	
	/** The bool get date when reading. */
	public static boolean boolGetDateWhenReading;
	
	/** The rep out. */
	public static String repOut;
	
	/** The show summary. */
	public static boolean showSummary;
	
	/** The conf output file. */
	public static String confOutputFile;

	/** The abs rel. */
	/* When using variable step from the origine => ABS, from the precedent record => REL */
	public static String absRel="ABS";
	
	
	/**
	 * Re init.
	 */
	public static void reInit(){
		fileToParse="";
		repOut="default";
		beginRecord = null;
		endRecord = null;
		monoline=true;
		beginEqualsEnd=false;
		dateBeginOfParsing=null;
		dateEndOfParsing=null;
		originDate=Date.from(Instant.ofEpochMilli(0)); // set by default to 1970/01/01:00:00:00.000
		regexInclude=null;
		regexExclude =null;
		absRel="ABS";
		regexDateRecord=null;
		javaDateFormatRecord=null;
		boolExplicitDate=true; // false => implicit date
		stepFromOrigin=0; // to use with implicit date  and origin date
		step2Records=0; // to use with implicit date  and origin date
		unitStep="ms";
		boolImplicitStep=false;// to use with implicit date  and origin date
		aggregPeriodInMillis=1000; // 1 s by default
		 boolGetDateWhenReading=true;
		 showSummary=false;
		decalDateDuration = 0L;
		boolCompactOutput=false;
		decalAllDatesInMillis = 0L;
		factorDateInMillis=1;
		isDateInMillis=false;
		confOutputFile="All+Avg";
		 sdfOut=null;
		 gapRegex=null;
		 isStartTimestamp=true;
	}
	
	/**
	 * Align config.
	 */
	public static void alignConfig(){
		if (boolExplicitDate ){
			 stepFromOrigin=0;
			 step2Records=0;
			 boolImplicitStep=false;
			 originDate=Date.from(Instant.ofEpochMilli(0));
		}
		if(monoline){
			endRecord = null;
		}
		else
		{
			if(null != beginRecord && null != endRecord && beginRecord.toString().equals(endRecord.toString()) ){
				beginEqualsEnd=true;
			}
		}
	}
	
	

}
