/*Copyright 2012 Jean-Louis PASTUREL 
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*  limitations under the License.
*/
package plugins;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class TraiterHotspot5And6 implements IMyPlugins {
	public String parameter = "";

	class StructHotspot5And6 {
		Double sizeYoungGenerationBefore = Double.NaN;
		Double sizeYoungGenerationAfter = Double.NaN;
		Double sizeOldGenerationBefore = Double.NaN;
		Double sizeOldGenerationAfter = Double.NaN;
		Double sizeHeapBefore = Double.NaN;
		Double sizeHeapAfter = Double.NaN;
		Double sizePermGenBefore = Double.NaN;
		Double sizePermGenAfter = Double.NaN;
		Double minorGCDuration = Double.NaN;
		Double throughput = Double.NaN;
		Double fullGCDuration = Double.NaN;
		Double cmsRemarkDuration = Double.NaN;
		Double cmsInitialMarkDuration = Double.NaN;
		Double cmsConcurrentMarkDuration = Double.NaN;
		Double cmsConcurrentSweep = Double.NaN;
		Double cmsConcurrentPreclean = Double.NaN;
		Double cmsConcurrentResetDuration = Double.NaN;
		Double fullGCSizeYoungGenerationBefore = Double.NaN;
		Double fullGCSizeYoungGenerationAfter = Double.NaN;
		Double fullGCSizeOldGenerationBefore = Double.NaN;
		Double fullGCSizeOldGenerationAfter = Double.NaN;
		Double fullGCSizeHeapBefore = Double.NaN;
		Double fullGCSizeHeapAfter = Double.NaN;
		Double sysTimeSpent = Double.NaN;
		Double userTimeSpent = Double.NaN;
		Double realTimeSpent = Double.NaN;
		Double memThroughput = Double.NaN;
		Double minorGCFrequency = Double.NaN;
		Double majorGCFrequency = Double.NaN;
		Double cmsSweepGCFrequency = Double.NaN;
		Double minorGCPeriod = Double.NaN;
		Double majorGCPeriod = Double.NaN;
		Double cmsSweepGCPeriod = Double.NaN;
	};

	public static StructHotspot5And6 structHotSpot = null;
	public static CircleArray circleArray = null;
	public static CircleArray minorGCCircleArray = null;
	public static CircleArray majorGCCircleArray = null;
	public static CircleArray cmsSweepGCCircleArray = null;
	public static String enrCurrent = null;
	public static boolean isStructFilled = false;
	public static Long dateInMillis = 0l;
	public static Long dateInMillisFullGC = 0L;
	public static Long dateInMillisMinorGC = 0L;

	public void initialize(String strRegex2) {
		// To reinitialise static variable if necessary

		TraiterHotspot5And6.structHotSpot = null;
		TraiterHotspot5And6.enrCurrent = null;
		TraiterHotspot5And6.dateInMillis = 0L;
		TraiterHotspot5And6.dateInMillisFullGC = 0L;
		TraiterHotspot5And6.dateInMillisMinorGC = 0L;
		TraiterHotspot5And6.isStructFilled = false;
		TraiterHotspot5And6.circleArray = new CircleArray(10);
		TraiterHotspot5And6.minorGCCircleArray = new CircleArray(10);
		TraiterHotspot5And6.majorGCCircleArray = new CircleArray(10);
		TraiterHotspot5And6.cmsSweepGCCircleArray = new CircleArray(10);
		parameter = strRegex2;
	}

	public Double returnDouble(String line) {

		if (TraiterHotspot5And6.enrCurrent == null) {
			TraiterHotspot5And6.enrCurrent = line;
			TraiterHotspot5And6.isStructFilled = false;
			TraiterHotspot5And6.dateInMillis = 0L;
			TraiterHotspot5And6.dateInMillisFullGC = 0L;
			TraiterHotspot5And6.dateInMillisMinorGC = 0L;
			traiterEnr(line);
		} else if (!TraiterHotspot5And6.enrCurrent.equals(line)) {
			TraiterHotspot5And6.isStructFilled = false;
			traiterEnr(line);

		}

		// Faire les retours ici
		switch (parameter) {
		case "sizeYoungGenerationBefore":
			return TraiterHotspot5And6.structHotSpot.sizeYoungGenerationBefore;
		case "sizeYoungGenerationAfter":
			return TraiterHotspot5And6.structHotSpot.sizeYoungGenerationAfter;
		case "sizeOldGenerationBefore":
			return TraiterHotspot5And6.structHotSpot.sizeOldGenerationBefore;
		case "sizeOldGenerationAfter":
			return TraiterHotspot5And6.structHotSpot.sizeOldGenerationAfter;
		case "sizeHeapBefore":
			return TraiterHotspot5And6.structHotSpot.sizeHeapBefore;
		case "sizeHeapAfter":
			return TraiterHotspot5And6.structHotSpot.sizeHeapAfter;
		case "sizePermGenBefore":
			return TraiterHotspot5And6.structHotSpot.sizePermGenBefore;
		case "sizePermGenAfter":
			return TraiterHotspot5And6.structHotSpot.sizePermGenAfter;
		case "minorGCDuration":
			return TraiterHotspot5And6.structHotSpot.minorGCDuration;
		case "throughput":
			return TraiterHotspot5And6.structHotSpot.throughput;
		case "fullGCDuration":
			return TraiterHotspot5And6.structHotSpot.fullGCDuration;
		case "cmsInitialMarkDuration":
			return TraiterHotspot5And6.structHotSpot.cmsInitialMarkDuration;
		case "cmsRemarkDuration":
			return TraiterHotspot5And6.structHotSpot.cmsRemarkDuration;
		case "cmsConcurrentResetDuration":
			return TraiterHotspot5And6.structHotSpot.cmsConcurrentResetDuration;
		case "cmsConcurrentMarkDuration":
			return TraiterHotspot5And6.structHotSpot.cmsConcurrentMarkDuration;
		case "cmsConcurrentSweep":
			return TraiterHotspot5And6.structHotSpot.cmsConcurrentSweep;
		case "cmsConcurrentPreclean":
			return TraiterHotspot5And6.structHotSpot.cmsConcurrentPreclean;
		case "fullGCSizeYoungGenerationBefore":
			return TraiterHotspot5And6.structHotSpot.fullGCSizeYoungGenerationBefore;
		case "fullGCSizeYoungGenerationAfter":
			return TraiterHotspot5And6.structHotSpot.fullGCSizeYoungGenerationAfter;
		case "fullGCSizeOldGenerationBefore":
			return TraiterHotspot5And6.structHotSpot.fullGCSizeOldGenerationBefore;
		case "fullGCSizeOldGenerationAfter":
			return TraiterHotspot5And6.structHotSpot.fullGCSizeOldGenerationAfter;
		case "fullGCSizeHeapBefore":
			return TraiterHotspot5And6.structHotSpot.fullGCSizeHeapBefore;
		case "fullGCSizeHeapAfter":
			return TraiterHotspot5And6.structHotSpot.fullGCSizeHeapAfter;
		case "sysTimeSpent":
			return TraiterHotspot5And6.structHotSpot.sysTimeSpent;
		case "userTimeSpent":
			return TraiterHotspot5And6.structHotSpot.userTimeSpent;
		case "realTimeSpent":
			return TraiterHotspot5And6.structHotSpot.realTimeSpent;
		case "memThroughput":
			return TraiterHotspot5And6.structHotSpot.memThroughput;
		case "minorGCFrequency":
			return TraiterHotspot5And6.structHotSpot.minorGCFrequency;
		case "majorGCFrequency":
			return TraiterHotspot5And6.structHotSpot.majorGCFrequency;
		case "cmsSweepGCFrequency":
			return TraiterHotspot5And6.structHotSpot.cmsSweepGCFrequency;
		case "minorGCPeriod":
			return TraiterHotspot5And6.structHotSpot.minorGCPeriod;
		case "majorGCPeriod":
			return TraiterHotspot5And6.structHotSpot.majorGCPeriod;
		case "cmsSweepGCPeriod":
			return TraiterHotspot5And6.structHotSpot.cmsSweepGCPeriod;
		default:
			return Double.NaN;

		}

	}

	private void  traiterEnr(String line) {
    // trouver la date:

    Pattern regDate = Pattern.compile("\\d+(\\.|,)\\d+:");
    Matcher match0 = regDate.matcher(line);
    if ( match0.find()) {
      // extraire la date
      String ext1 = match0.group();
      ext1 = ext1.substring(0, ext1.indexOf(":")); // supprimer le dernier caractere
      //var dateCurrentInMillis = ext1.split("\\.")(0).toLong * 1000 + ext1.split("\\.")(1).toLong
      long dateCurrentInMillis = (long) (Double.parseDouble(ext1.replaceAll(",", ".")) * 1000);
    //  println("dateCurrentInMillis="+dateCurrentInMillis)
      remplirStruct(dateCurrentInMillis, line);

    }
  }

	private void  remplirStruct(Long dateCurrent, String line) {
	  Pattern patLong=Pattern.compile("\\d+");

    // println("enr="+tabStr(0))
    TraiterHotspot5And6.structHotSpot = new StructHotspot5And6();
    // remplissage  sizeYoungGenerationBefore
    TraiterHotspot5And6.structHotSpot.sizeYoungGenerationBefore = Double.NaN;
    Pattern reg1 = Pattern.compile("\\[GC.+?(DefNew:|ParNew:|PSYoungGen:)\\s+\\d+(K|M)");
    Matcher match1 = reg1.matcher(line);
    if (match1.find()) {
    	String ext2=match1.group();
    	Matcher matchTmp =Pattern.compile("\\d+(K|M)").matcher(ext2);
    	if (matchTmp.find()) {
     String ext3=matchTmp.group();
     
      if (ext3.contains("K")) {
    	  
        TraiterHotspot5And6.structHotSpot.sizeYoungGenerationBefore = Double.parseDouble(ext3.split("K")[0]) * 1024;
      } else {
        TraiterHotspot5And6.structHotSpot.sizeYoungGenerationBefore =  Double.parseDouble(ext3.split("M")[0]) * 1024 * 1024;
      }
    }
    }

    // remplissage  sizeYoungGenerationAfter
    TraiterHotspot5And6.structHotSpot.sizeYoungGenerationAfter = Double.NaN;
    		reg1 = Pattern.compile("\\[GC.+?(DefNew:|ParNew:|PSYoungGen:)\\s+\\d+(K|M)->\\d+(K|M)");
   match1 = reg1.matcher(line);
   if (match1.find()) {
   	String ext2=match1.group();
   	Matcher matchTmp =Pattern.compile("\\d+(K|M)$").matcher(ext2);
   	if (matchTmp.find()) {
        String ext3=matchTmp.group();
      if (ext3.contains("K")) {
        TraiterHotspot5And6.structHotSpot.sizeYoungGenerationAfter = Double.parseDouble(ext3.split("K")[0]) * 1024;
      } else {
        TraiterHotspot5And6.structHotSpot.sizeYoungGenerationAfter =  Double.parseDouble(ext3.split("M")[0]) * 1024 * 1024;
      }
   	}
    }

    // remplissage  sizeHeapBefore
    TraiterHotspot5And6.structHotSpot.sizeHeapBefore = Double.NaN;
    reg1 = Pattern.compile("\\[(GC|Full\\s+GC).+?(DefNew:|ParNew:|PSYoungGen:|Tenured:|PSOldGen:|ParOldGen:)[^\\]]+\\]\\s+\\d+(K|M)");
    match1 = reg1.matcher(line);
    if (match1.find()) {
    	String ext2=match1.group();
       	Matcher matchTmp =Pattern.compile("\\d+(K|M)$").matcher(ext2);
    	if (matchTmp.find()) {
            String ext3=matchTmp.group();
      if ( ext3.contains("K")) {
        TraiterHotspot5And6.structHotSpot.sizeHeapBefore = Double.parseDouble(ext3.split("K")[0]) * 1024;
      } else {
        TraiterHotspot5And6.structHotSpot.sizeHeapBefore = Double.parseDouble(ext3.split("M")[0]) * 1024 * 1024;
      }
    	}
    }
    // remplissage  sizeHeapAfter
    TraiterHotspot5And6.structHotSpot.sizeHeapAfter = Double.NaN;
    reg1 = Pattern.compile("\\[(GC|Full\\s+GC).+?(DefNew:|ParNew:|PSYoungGen|Tenured:|PSOldGen:|ParOldGen:)[^\\]]+\\]\\s+\\d+(K|M)->\\d+(K|M)");
    match1 = reg1.matcher(line);
    if (match1.find()) {
    	String ext2=match1.group();
       	Matcher matchTmp =Pattern.compile("\\d+(K|M)$").matcher(ext2);
    	if (matchTmp.find()) {
            String ext3=matchTmp.group();
      if (ext3.contains("K")) {
        TraiterHotspot5And6.structHotSpot.sizeHeapAfter = Double.parseDouble(ext3.split("K")[0]) * 1024;
      } else {
        TraiterHotspot5And6.structHotSpot.sizeHeapAfter = Double.parseDouble(ext3.split("M")[0]) * 1024 * 1024;
      }
    	}
    }
    // remplissage Throughput Memoire
    // println("Avant Traitement throughput memor"+TraiterHotspot5And6.structHotSpot.sizeHeapBefore+ " " +TraiterHotspot5And6.structHotSpot.sizeHeapAfter)
    TraiterHotspot5And6.structHotSpot.memThroughput = Double.NaN;
    if (!(TraiterHotspot5And6.structHotSpot.sizeHeapBefore.isNaN()) && !(TraiterHotspot5And6.structHotSpot.sizeHeapAfter.isNaN())) {
      double sweeped = TraiterHotspot5And6.structHotSpot.sizeHeapBefore - TraiterHotspot5And6.structHotSpot.sizeHeapAfter;
      // println("Traitement throughput memor")
      TraiterHotspot5And6.circleArray.put(new StrucLongDouble(dateCurrent, sweeped));
      TraiterHotspot5And6.structHotSpot.memThroughput = TraiterHotspot5And6.circleArray.throughput;
    }
    //      else
    //      {
    //        println("Cannot compute throughput memory for : datetime :"+dateCurrent )
    //        println("TraiterHotspot5And6.structHotSpot.sizeHeapBefore="+TraiterHotspot5And6.structHotSpot.sizeHeapBefore)
    //         println("TraiterHotspot5And6.structHotSpot.sizeHeapAfter="+TraiterHotspot5And6.structHotSpot.sizeHeapAfter)
    //        
    //      }

    // remplissage   sizeOldGenerationBefore
    TraiterHotspot5And6.structHotSpot.sizeOldGenerationBefore = Double.NaN;
    if (!TraiterHotspot5And6.structHotSpot.sizeHeapBefore.isNaN() && !TraiterHotspot5And6.structHotSpot.sizeYoungGenerationBefore.isNaN()) {

      TraiterHotspot5And6.structHotSpot.sizeOldGenerationBefore = TraiterHotspot5And6.structHotSpot.sizeHeapBefore - TraiterHotspot5And6.structHotSpot.sizeYoungGenerationBefore;
    }

    // remplissage   sizeOldGenerationAfter
    TraiterHotspot5And6.structHotSpot.sizeOldGenerationAfter = Double.NaN;
    if (!TraiterHotspot5And6.structHotSpot.sizeHeapAfter.isNaN() && !TraiterHotspot5And6.structHotSpot.sizeYoungGenerationAfter.isNaN()) {

      TraiterHotspot5And6.structHotSpot.sizeOldGenerationAfter = TraiterHotspot5And6.structHotSpot.sizeHeapAfter - TraiterHotspot5And6.structHotSpot.sizeYoungGenerationAfter;
    }

    // remplissage   sizePermGenBefore
    TraiterHotspot5And6.structHotSpot.sizePermGenBefore = Double.NaN;
    reg1 = Pattern.compile("(CMS Perm :|PSPermGen:|Perm\\s+:)\\s+\\d+(K|M)");
    match1 = reg1.matcher(line);
    if (match1.find()) {
    	String ext2=match1.group();
    	Matcher matchTmp =Pattern.compile("\\d+(K|M)$").matcher(ext2);
    	if (matchTmp.find()) {
            String ext3=matchTmp.group();
      if (ext3.contains("K")) {
        TraiterHotspot5And6.structHotSpot.sizePermGenBefore = Double.parseDouble(ext3.split("K")[0]) * 1024;
      } else {
        TraiterHotspot5And6.structHotSpot.sizePermGenBefore =Double.parseDouble(ext3.split("M")[0]) * 1024 * 1024;
      }
    	}
    }
    // remplissage   sizePermGenAfter
    TraiterHotspot5And6.structHotSpot.sizePermGenAfter = Double.NaN;
    reg1 = Pattern.compile("(CMS Perm :|PSPermGen:|Perm\\s+:)\\s+\\d+(K|M)->\\d+(K|M)");
    match1 = reg1.matcher(line);
    if (match1.find()) {
    	String ext2=match1.group();
    	Matcher matchTmp =Pattern.compile("\\d+(K|M)$").matcher(ext2);
    	if (matchTmp.find()) {
            String ext3=matchTmp.group();
      if (ext3.contains("K")) {
        TraiterHotspot5And6.structHotSpot.sizePermGenAfter =  Double.parseDouble(ext3.split("K")[0]) * 1024;
      } else {
        TraiterHotspot5And6.structHotSpot.sizePermGenAfter =Double.parseDouble(ext3.split("M")[0]) * 1024 * 1024;
      }
    	}
    }

    // remplissage   minorGCDuration
    TraiterHotspot5And6.structHotSpot.minorGCFrequency = Double.NaN;
    TraiterHotspot5And6.structHotSpot.minorGCPeriod = Double.NaN;
    TraiterHotspot5And6.structHotSpot.minorGCDuration = Double.NaN;
    
    reg1 = Pattern.compile("\\[GC.+?(\\[Times|\\]\\s*$)");
    match1 = reg1.matcher(line);
    if (match1.find()) {
      //  println("ext1.get="+ext1.get)
      TraiterHotspot5And6.minorGCCircleArray.put(new StrucLongDouble(dateCurrent, 1.0));
      TraiterHotspot5And6.structHotSpot.minorGCFrequency = TraiterHotspot5And6.minorGCCircleArray.freq;
       TraiterHotspot5And6.structHotSpot.minorGCPeriod = TraiterHotspot5And6.minorGCCircleArray.period;
       Pattern pat12=Pattern.compile("\\d+(\\.|,)\\d+\\s*secs\\]\\s*(\\[Times$|$)");
     Matcher match2 =pat12.matcher(match1.group());;
     if (match2.find()) {
       String  ext2 = match2.group();
       Pattern pat13= Pattern.compile("\\d+(\\.|,)\\d+");
       Matcher match3=pat13.matcher(ext2);
       if (match3.find()) {
     
      TraiterHotspot5And6.structHotSpot.minorGCDuration = Double.parseDouble(match3.group().replaceAll(",", ".")) * 1000;
       }
     }
    }
    // remplissage   fullGCDuration
    TraiterHotspot5And6.structHotSpot.fullGCDuration = Double.NaN;
    TraiterHotspot5And6.structHotSpot.majorGCPeriod = Double.NaN;
    TraiterHotspot5And6.structHotSpot.majorGCFrequency = Double.NaN;
    
    reg1 = Pattern.compile("Full\\s+GC.+?(\\[Times|\\]\\s*$)");
    match1 = reg1.matcher(line);
    if (match1.find()) {
      TraiterHotspot5And6.majorGCCircleArray.put(new StrucLongDouble(dateCurrent, 1.0));
      TraiterHotspot5And6.structHotSpot.majorGCFrequency = TraiterHotspot5And6.majorGCCircleArray.freq;
      TraiterHotspot5And6.structHotSpot.majorGCPeriod = TraiterHotspot5And6.majorGCCircleArray.period;
    		  Pattern pat12=Pattern.compile("\\d+(\\.|,)\\d+\\s+secs\\]\\s*(\\[Times$|$)");
      Matcher match2 =pat12.matcher(match1.group());;
      if (match2.find()) {
          String  ext2 = match2.group();
          Pattern pat13= Pattern.compile("\\d+(\\.|,)\\d+");
          Matcher match3=pat13.matcher(ext2);
          if (match3.find()) {
        
      TraiterHotspot5And6.structHotSpot.fullGCDuration = Double.parseDouble(match3.group().replaceAll(",", ".")) * 1000;
          }
    }
    }

    // remplissage  throughput
    TraiterHotspot5And6.structHotSpot.throughput = Double.NaN;
    if (!TraiterHotspot5And6.structHotSpot.minorGCDuration.isNaN()) {
      // println("throughput minorGC minorDuration="+TraiterHotspot5And6.structHotSpot.minorGCDuration+" ; dateCurrent ="+dateCurrent+ " ; datePrev="+TraiterHotspot5And6.dateInMillis)
      TraiterHotspot5And6.structHotSpot.throughput = 100 * (1 - (TraiterHotspot5And6.structHotSpot.minorGCDuration /(double) (dateCurrent - TraiterHotspot5And6.dateInMillisMinorGC)));
      if (line.contains("[GC") || line.contains("[Full GC")) TraiterHotspot5And6.dateInMillisMinorGC = dateCurrent;
    }
    if (!TraiterHotspot5And6.structHotSpot.fullGCDuration.isNaN()) {
      //  println("throughput majorGC fullGCDuration="+TraiterHotspot5And6.structHotSpot.fullGCDuration+" ; dateCurrent ="+dateCurrent+ " ; datePrev="+TraiterHotspot5And6.dateInMillis)

      TraiterHotspot5And6.structHotSpot.throughput = 100 * (1 - (TraiterHotspot5And6.structHotSpot.fullGCDuration /(double) (dateCurrent - TraiterHotspot5And6.dateInMillisFullGC)));
      if (line.contains("[GC") || line.contains("[Full GC")) TraiterHotspot5And6.dateInMillisFullGC = dateCurrent;
    }
    
  
 // remplissage    cmsConcurrentResetDuration
    TraiterHotspot5And6.structHotSpot.cmsConcurrentResetDuration = Double.NaN;
    		 reg1 = Pattern.compile("CMS-concurrent-reset:\\s+[^\\]]+");
    match1 = reg1.matcher(line);
    if (match1.find()) {
    	String ext2=match1.group();
    	Matcher matchTmp =Pattern.compile("\\d+(\\.|,)\\d+\\s+secs").matcher(ext2);
    	if (matchTmp.find()) {
            String ext3=matchTmp.group();
      Pattern pat23=Pattern.compile("\\d+(\\.|,)\\d+");
      Matcher matchtmp2=pat23.matcher(ext3);
      if (matchtmp2.find()) {
     
    
      TraiterHotspot5And6.structHotSpot.cmsConcurrentResetDuration = Double.parseDouble(matchtmp2.group().replaceAll(",", ".")) * 1000;
      }
    	}
    }
     
     // remplissage    cmsInitialMarkDuration
    TraiterHotspot5And6.structHotSpot.cmsInitialMarkDuration = Double.NaN;
    reg1 = Pattern.compile("CMS-initial-mark:\\s+[^\\]]+\\][^\\]]+");
  
    		 match1 = reg1.matcher(line);
    if (match1.find()) {
    	String ext2=match1.group();
    	
    	Matcher matchTmp =Pattern.compile("\\d+(\\.|,)\\d+\\s+secs").matcher(ext2);
    	
          if (matchTmp.find()) {
        	  String ext3=matchTmp.group();
              Pattern pat23=Pattern.compile("\\d+(\\.|,)\\d+");
              Matcher matchtmp2=pat23.matcher(ext3);
              if (matchtmp2.find()) {
        	  
          
      TraiterHotspot5And6.structHotSpot.cmsInitialMarkDuration = Double.parseDouble(matchtmp2.group().replaceAll(",", ".")) * 1000;
          }
          }

    }
    
 // remplissage    cmsRemarkDuration
    TraiterHotspot5And6.structHotSpot.cmsRemarkDuration = Double.NaN;
    reg1 = Pattern.compile("CMS-remark:\\s+[^\\]]+\\][^\\]]+");
	 match1 = reg1.matcher(line);
if (match1.find()) {
	String ext2=match1.group();
	Matcher matchTmp =Pattern.compile("\\d+(\\.|,)\\d+\\s+secs").matcher(ext2);
	  if (matchTmp.find()) {
    	 String ext3=matchTmp.group();
          Pattern pat23=Pattern.compile("\\d+(\\.|,)\\d+");
          Matcher matchtmp2=pat23.matcher(ext3);
          if (matchtmp2.find()) {
      TraiterHotspot5And6.structHotSpot.cmsRemarkDuration = Double.parseDouble(matchtmp2.group().replaceAll(",", ".")) * 1000;

    }
	  }
}
        

    // remplissage    cmsConcurrentMarkDuration
    TraiterHotspot5And6.structHotSpot.cmsConcurrentMarkDuration = Double.NaN;
    reg1 = Pattern.compile("CMS-concurrent-mark:\\s+[^\\]]+");
    match1 = reg1.matcher(line);
    if (match1.find()) {
    	String ext2=match1.group();
    	Matcher matchTmp =Pattern.compile("\\d+(\\.|,)\\d+\\s+secs").matcher(ext2);
    	  if (matchTmp.find()) {
        	  String ext3=matchTmp.group();
              Pattern pat23=Pattern.compile("\\d+(\\.|,)\\d+");
              Matcher matchtmp2=pat23.matcher(ext3);
              if (matchtmp2.find()) {
      TraiterHotspot5And6.structHotSpot.cmsConcurrentMarkDuration =  Double.parseDouble(matchtmp2.group().replaceAll(",", ".")) * 1000;

    }
    	  }
    }
    // remplissage    cmsConcurrentSweep
    TraiterHotspot5And6.structHotSpot.cmsConcurrentSweep = Double.NaN;
    TraiterHotspot5And6.structHotSpot.cmsSweepGCFrequency= Double.NaN;
    TraiterHotspot5And6.structHotSpot.cmsSweepGCPeriod= Double.NaN;
    reg1 = Pattern.compile("CMS-concurrent-sweep:\\s+[^\\]]+");
    match1 = reg1.matcher(line);
    if (match1.find()) {
    	String ext2=match1.group();
    	Matcher matchTmp =Pattern.compile("\\d+(\\.|,)\\d+\\s+secs").matcher(ext2);
      TraiterHotspot5And6.cmsSweepGCCircleArray.put(new StrucLongDouble(dateCurrent, 1.0));
      TraiterHotspot5And6.structHotSpot.cmsSweepGCFrequency = TraiterHotspot5And6.cmsSweepGCCircleArray.freq;
        TraiterHotspot5And6.structHotSpot.cmsSweepGCPeriod = TraiterHotspot5And6.cmsSweepGCCircleArray.period;
        if (matchTmp.find()) {
      	  String ext3=matchTmp.group();
            Pattern pat23=Pattern.compile("\\d+(\\.|,)\\d+");
            Matcher matchtmp2=pat23.matcher(ext3);
            if (matchtmp2.find()) {
      TraiterHotspot5And6.structHotSpot.cmsConcurrentSweep =  Double.parseDouble(matchtmp2.group().replaceAll(",", ".")) * 1000;
            }
        }
    }
    // remplissage   cmsConcurrentPreclean
    TraiterHotspot5And6.structHotSpot.cmsConcurrentPreclean = Double.NaN;
    reg1 = Pattern.compile("CMS-concurrent-preclean:\\s+[^\\]]+");
   
    		match1 = reg1.matcher(line);
    if (match1.find()) {
    	String ext2=match1.group();
    	Matcher matchTmp =Pattern.compile("\\d+(\\.|,)\\d+\\s+secs").matcher(ext2);
    	if (matchTmp.find()) {
        	  String ext3=matchTmp.group();
              Pattern pat23=Pattern.compile("\\d+(\\.|,)\\d+");
              Matcher matchtmp2=pat23.matcher(ext3);
              if (matchtmp2.find()) {
      TraiterHotspot5And6.structHotSpot.cmsConcurrentPreclean = Double.parseDouble(matchtmp2.group().replaceAll(",", ".")) * 1000;

    }
    	}
    }

    // remplissage  fullGCSizeOldGenerationBefore
    TraiterHotspot5And6.structHotSpot.fullGCSizeOldGenerationBefore = Double.NaN;
    reg1 = Pattern.compile("Full GC.+(PSOldGen:|CMS:|Tenured:)\\s+\\d+(K|M)");
    match1 = reg1.matcher(line);
    if (match1.find()) {
    	String ext2=match1.group();
    	Matcher matchTmp =Pattern.compile("\\d+(K|M)$").matcher(ext2);
     if (matchTmp.find()) {
    	 String ext3=matchTmp.group();
     
      if (ext3.contains("K")) {
        TraiterHotspot5And6.structHotSpot.fullGCSizeOldGenerationBefore =  Double.parseDouble(ext3.split("K")[0]) * 1024;;
      } else {
        TraiterHotspot5And6.structHotSpot.fullGCSizeOldGenerationBefore = Double.parseDouble(ext3.split("M")[0]) * 1024 * 1024;
      }
     }
    }
    // remplissage  fullGCSizeOldGenerationAfter
    TraiterHotspot5And6.structHotSpot.fullGCSizeOldGenerationAfter = Double.NaN;
    reg1 = Pattern.compile("Full GC.+(PSOldGen:|CMS:|Tenured:)\\s+\\d+(K|M)->\\d+(K|M)");
    match1 = reg1.matcher(line);
    if (match1.find()) {
    	String ext2=match1.group();
    	Matcher matchTmp =Pattern.compile("\\d+(K|M)$").matcher(ext2);
        if (matchTmp.find()) {
       	 String ext3=matchTmp.group();
      if (ext3.contains("K")) {
        TraiterHotspot5And6.structHotSpot.fullGCSizeOldGenerationAfter = Double.parseDouble(ext3.split("K")[0]) * 1024;;
      } else {
        TraiterHotspot5And6.structHotSpot.fullGCSizeOldGenerationAfter =  Double.parseDouble(ext3.split("M")[0]) * 1024 * 1024;
      }
        }
    }
    // remplissage  fullGCSizeHeapBefore
    TraiterHotspot5And6.structHotSpot.fullGCSizeHeapBefore = Double.NaN;

    reg1 = Pattern.compile("Full GC.+(PSOldGen:|CMS:|Tenured:)[^\\]]+?\\]\\s+\\d+(K|M)");
    match1 = reg1.matcher(line);
    if (match1.find()) {
    	String ext2=match1.group();
    	Matcher matchTmp =Pattern.compile("\\d+(K|M)$").matcher(ext2);
        if (matchTmp.find()) {
       	 String ext3=matchTmp.group();
      if (ext3.contains("K")) {
        TraiterHotspot5And6.structHotSpot.fullGCSizeHeapBefore = Double.parseDouble(ext3.split("K")[0]) * 1024;;
      } else {
        TraiterHotspot5And6.structHotSpot.fullGCSizeHeapBefore = Double.parseDouble(ext3.split("M")[0]) * 1024 * 1024;
      }
      //System.out.println("remplissage fullGCSizeHeapBefore="+TraiterHotspot5And6.structHotSpot.fullGCSizeHeapBefore);
    }
    }

    // remplissage  fullGCSizeHeapAfter
    TraiterHotspot5And6.structHotSpot.fullGCSizeHeapAfter = Double.NaN;
    reg1 = Pattern.compile("Full GC.+(PSOldGen:|CMS:|Tenured:)[^\\]]+?\\]\\s+\\d+(K|M)->\\d+(K|M)");
    match1 = reg1.matcher(line);
    if (match1.find()) {
    	String ext2=match1.group();
    	Matcher matchTmp =Pattern.compile("\\d+(K|M)$").matcher(ext2);
    	 if (matchTmp.find()) {
           	 String ext3=matchTmp.group();
      if (ext3.contains("K")) {
        TraiterHotspot5And6.structHotSpot.fullGCSizeHeapAfter =  Double.parseDouble(ext3.split("K")[0]) * 1024;
      } else {
        TraiterHotspot5And6.structHotSpot.fullGCSizeHeapAfter = Double.parseDouble(ext3.split("M")[0]) * 1024 * 1024;
      }
    }
    }
    // remplissage fullGCSizeYoungGenerationBefore
    TraiterHotspot5And6.structHotSpot.fullGCSizeYoungGenerationBefore = Double.NaN;
    if (!TraiterHotspot5And6.structHotSpot.fullGCSizeHeapBefore.isNaN() && !TraiterHotspot5And6.structHotSpot.fullGCSizeOldGenerationBefore.isNaN()) {

      TraiterHotspot5And6.structHotSpot.fullGCSizeYoungGenerationBefore = TraiterHotspot5And6.structHotSpot.fullGCSizeHeapBefore - TraiterHotspot5And6.structHotSpot.fullGCSizeOldGenerationBefore;
    }
    // remplissage fullGCSizeYoungGenerationAfter
    TraiterHotspot5And6.structHotSpot.fullGCSizeYoungGenerationAfter = Double.NaN;
    if (!TraiterHotspot5And6.structHotSpot.fullGCSizeYoungGenerationAfter.isNaN() && !TraiterHotspot5And6.structHotSpot.fullGCSizeOldGenerationAfter.isNaN()) {

      TraiterHotspot5And6.structHotSpot.fullGCSizeYoungGenerationAfter = TraiterHotspot5And6.structHotSpot.fullGCSizeHeapAfter - TraiterHotspot5And6.structHotSpot.fullGCSizeOldGenerationAfter;
    }

    // Test de presence des temps syst, user et real
    TraiterHotspot5And6.structHotSpot.sysTimeSpent = Double.NaN;
    TraiterHotspot5And6.structHotSpot.userTimeSpent = Double.NaN;
    TraiterHotspot5And6.structHotSpot.realTimeSpent = Double.NaN;
    reg1 = Pattern.compile("\\[Times[^\\]]+");
    
    match1 = reg1.matcher(line);
    if (match1.find()) {
   
      // remplissage  sysTimeSpent
      // [Times: user=0.13 sys=0.00, real=0.03 secs] 
    	Pattern pat31=Pattern.compile("sys=\\d+(\\.|,)\\d+");
      Matcher match31=pat31.matcher(match1.group());
      
      if (match31.find()) {
    	  String ext31=match31.group();
    	  Matcher match32=Pattern.compile("\\d+(\\.|,)\\d+").matcher(ext31);
    	  if (match32.find()) {
      
      TraiterHotspot5And6.structHotSpot.sysTimeSpent = Double.parseDouble(match32.group().replaceAll(",", ".")) * 1000;
    	  }
      }
      
      // remplissage  userTimeSpent
      Pattern pat41=Pattern.compile("user=\\d+(\\.|,)\\d+");
  Matcher match41=pat41.matcher(match1.group());
      
      if (match41.find()) {
    	  String ext41=match41.group();
    	  Matcher match42=Pattern.compile("\\d+(\\.|,)\\d+").matcher(ext41);
    	  if (match42.find()) {
      TraiterHotspot5And6.structHotSpot.userTimeSpent = Double.parseDouble(match42.group().replaceAll(",", ".")) * 1000;
    	  }
      }
      // remplissage  realTimeSpent
      Pattern pat51=Pattern.compile("real=\\d+(\\.|,)\\d+");
 Matcher match51=pat51.matcher(match1.group());
      
      if (match51.find()) {
    	  String ext51=match51.group();
    	  Matcher match52=Pattern.compile("\\d+(\\.|,)\\d+").matcher(ext51);
    	  if (match52.find()) {
      TraiterHotspot5And6.structHotSpot.realTimeSpent = Double.parseDouble(match52.group().replaceAll(",", ".")) * 1000;
    }
      }

    // On ne mesure les dates  qu entre les GC
    if (line.contains("[GC") || line.contains("[Full GC")) TraiterHotspot5And6.dateInMillis = dateCurrent;
    TraiterHotspot5And6.enrCurrent = line;
    TraiterHotspot5And6.isStructFilled = true;
  }
    }

}
