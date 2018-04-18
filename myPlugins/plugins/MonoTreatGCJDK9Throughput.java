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

import plugins.TraiterHotspot5And6.StructHotspot5And6;

// TODO: Auto-generated Javadoc
/**
 * The Class MonoTreatGCJDK9Throughput.<p>
 * See   MonoTreatGCJDK9Throughput.StructGCJDK9Throughput javadoc for metrics details.
 */
public class MonoTreatGCJDK9Throughput implements IMyPlugins {

	/** The parameter. */
	public String parameter = "";

	/** The struct GCJDK 9 throughput. */
	public static StructGCJDK9Throughput structGCJDK9Throughput = null;
	
	/** The mem thoughputcircle array. */
	public static CircleArray memThoughputcircleArray = null;
	
	/** The evacuation frequencycircle array. */
	public static CircleArray evacuationFrequencycircleArray = null;
	
	/** The cpu throuhputcircle array. */
	public static CircleArray cpuThrouhputcircleArray = null;
	
	/** The enr current. */
	public static String enrCurrent = null;
	
	/** The is struct filled. */
	public static boolean isStructFilled = false;
	
	/** The date in millis. */
	public static Long dateInMillis = 0l;
	
	/** The date in millis evacuation. */
	public static Long dateInMillisEvacuation = 0L;

	// Patterns
	/** The pat size occuped before 1. */
	// (G1 Evacuation Pause) 403M->319M(512M) 46,053ms
	public static Pattern patSizeOccupedBefore1 = Pattern.compile("\\d+(M|K)");
	
	/** The pat size occuped after 1. */
	public static Pattern patSizeOccupedAfter1 = Pattern.compile("\\d+(M|K)->\\d+(M|K)");
	
	/** The pat total size 1. */
	public static Pattern patTotalSize1 = Pattern.compile("\\(\\d+(M|K)\\)");
	
	/** The pat duration evacuation. */
	public static Pattern patDurationEvacuation = Pattern.compile("\\d+(,|\\.)\\d+ms");
	
	/** The pat evacuation pause. */
	public static Pattern patEvacuationPause= Pattern.compile("\\(G1 Evacuation Pause\\)\\s*\\d+(M|K)");
	
	/** The pat cpu throughput. */
	public static Pattern patCpuThroughput= Pattern.compile("gc,cpu.*$");
	
	/** The pat real. */
	public static Pattern patReal=Pattern.compile("Real=\\d+(,|\\.)s");

	/**
	 * The Class StructGCJDK9Throughput. <p>
	 * In this inner class, we define all metrics that can be charted. </p>
	 * <p> A typical line in the value tab of the log parser is :<br>
	 * sizeOccupedBefore plugin=MonoTreatGCJDK9Throughput sizeOccupedBefore Mo 1.0E-6 <br>
	 * and so on for others metrics </p>
	 */
	public class StructGCJDK9Throughput {

		// Pause Young (G1 Evacuation Pause) 298M->139M(512M) 28,561ms
		/** The size occuped before. in bytes =&gt;  modifiable with the scale value */
		// Pause Initial Mark (G1 Evacuation Pause) 403M->319M(512M) 46,053ms
		public	Double sizeOccupedBefore = Double.NaN;
		
		/** The size occuped after. in bytes =&gt;  modifiable with the scale value */
		public Double sizeOccupedAfter = Double.NaN;
		
		/** The size free after.in bytes =&gt;  modifiable with the scale value */
		public Double sizeFreeAfter = Double.NaN;
		
		/** The duration evacuation. in millis =&gt;  modifiable with the scale value */
		public	Double durationEvacuation = Double.NaN;
		
		/** The mem throughput. bytes/s  =&gt;  modifiable with the scale value */
		public	Double memThroughput = Double.NaN;
		
		/** The evacuation frequency. hits/s =&gt;  modifiable with the scale value */
		Double evacuationFrequency = Double.NaN;
		
		/** The cpu throughput. % of Application CPU */
		public 	Double cpuThroughput = Double.NaN;
	}

	/**
	 * Initialize.
	 *
	 * @param strRegex2 the str regex 2
	 */
	@Override
	public void initialize(String strRegex2) {
		enrCurrent = null;
		isStructFilled = false;
		dateInMillis = 0l;
		dateInMillisEvacuation = 0L;
		memThoughputcircleArray = new CircleArray(10);
		evacuationFrequencycircleArray=new CircleArray(10);
		cpuThrouhputcircleArray=new CircleArray(10);
		parameter = strRegex2;
	}

	/**
	 * Return double.
	 *
	 * @param line the line
	 * @return the double
	 */
	@Override
	public Double returnDouble(String line) {
		
		if (MonoTreatGCJDK9Throughput.enrCurrent == null) {
			MonoTreatGCJDK9Throughput.enrCurrent = line;
			MonoTreatGCJDK9Throughput.isStructFilled = false;
			MonoTreatGCJDK9Throughput.dateInMillis = 0L;
			MonoTreatGCJDK9Throughput.dateInMillisEvacuation = 0L;
			
			traiterEnr(line);
		} else if (!MonoTreatGCJDK9Throughput.enrCurrent.equals(line)) {
			TraiterHotspot5And6.isStructFilled =false;
			traiterEnr(line); 

		}
		// Faire les retours ici
				switch (parameter) {
				case "sizeOccupedBefore":
					
					return MonoTreatGCJDK9Throughput.structGCJDK9Throughput.sizeOccupedBefore;
				case "sizeOccupedAfter":
					return MonoTreatGCJDK9Throughput.structGCJDK9Throughput.sizeOccupedAfter;
				case "sizeFreeAfter":
					return MonoTreatGCJDK9Throughput.structGCJDK9Throughput.sizeFreeAfter;
				case "memThroughput":
					return  MonoTreatGCJDK9Throughput.structGCJDK9Throughput.memThroughput ;
				case "durationEvacuation":
					return  MonoTreatGCJDK9Throughput.structGCJDK9Throughput.durationEvacuation ;
					
				case "cpuThroughput":
					return  MonoTreatGCJDK9Throughput.structGCJDK9Throughput.cpuThroughput ;	
				case "evacuationFrequency":
					return  MonoTreatGCJDK9Throughput.structGCJDK9Throughput.evacuationFrequency ;
				default:
					return null;
				}
		
	}

	/**
	 * Traiter enr.
	 *
	 * @param line the line
	 */
	private void traiterEnr(String line) {
		// trouver la date:
		Pattern regDate = Pattern.compile("\\d+(\\.|,)\\d+s");
		Matcher match0 = regDate.matcher(line);
		if (match0.find()) {
			// extraire la date
			String ext1 = match0.group();
			ext1 = ext1.substring(0, ext1.indexOf("s")); // supprimer le dernier caractere
			// var dateCurrentInMillis = ext1.split("\\.")(0).toLong * 1000 +
			// ext1.split("\\.")(1).toLong
			long dateCurrentInMillis = (long) (Double.parseDouble(ext1.replaceAll(",", ".")) * 1000);
			// println("dateCurrentInMillis="+dateCurrentInMillis)
			remplirStruct(dateCurrentInMillis, line);
			MonoTreatGCJDK9Throughput.enrCurrent=line;
		}
	}

	/**
	 * Remplir struct.
	 *
	 * @param dateCurrent the date current
	 * @param line the line
	 */
	private void remplirStruct(Long dateCurrent, String line) {
		
		Pattern patLong = Pattern.compile("\\d+");
		MonoTreatGCJDK9Throughput.structGCJDK9Throughput = new StructGCJDK9Throughput();
		// public static Pattern patSizeOccupedBefore1=Pattern.compile("\\d+(M|K)");
		// public static Pattern
		// patSizeOccupedOccuped1=Pattern.compile("\\d+(M|K)->\\d+(M|K)");
		// public static Pattern patTotalSize1=Pattern.compile("\\(\\d+(M|K)\\)");
		// public static Pattern
		// patDurationEvacuation=Pattern.compile("\\d+(,|\\.)\\d+ms");

		// Remplissage Double sizeOccupedBefore = Double.NaN;
		structGCJDK9Throughput.sizeOccupedBefore = Double.NaN;

		Matcher match1 = patSizeOccupedBefore1.matcher(line);
		if (match1.find()) {
			String ext2 = match1.group();

			if (ext2.contains("K")) {
				MonoTreatGCJDK9Throughput.structGCJDK9Throughput.sizeOccupedBefore = Double
						.parseDouble(ext2.split("K")[0]) * 1024;
			} else {
				MonoTreatGCJDK9Throughput.structGCJDK9Throughput.sizeOccupedBefore = Double
						.parseDouble(ext2.split("M")[0]) * 1024 * 1024;
			}

		}
		
		// Remplissage Double sizeOccupedAfter = Double.NaN;
		structGCJDK9Throughput.sizeOccupedAfter = Double.NaN;
		match1 = patSizeOccupedAfter1.matcher(line);
		if (match1.find()) {
			String ext2 = match1.group();
			Matcher matchTmp = Pattern.compile("\\d+(K|M)$").matcher(ext2);
			if (matchTmp.find()) {
				String ext3 = matchTmp.group();

				if (ext3.contains("K")) {
					MonoTreatGCJDK9Throughput.structGCJDK9Throughput.sizeOccupedAfter= Double
							.parseDouble(ext3.split("K")[0]) * 1024;
				} else {
					MonoTreatGCJDK9Throughput.structGCJDK9Throughput.sizeOccupedAfter = Double
							.parseDouble(ext3.split("M")[0]) * 1024 * 1024;
				}
			}
		}
		
		// Remplissage Double sizeFreeAfter = Double.NaN;
				structGCJDK9Throughput.sizeFreeAfter = Double.NaN;
				
				if(!MonoTreatGCJDK9Throughput.structGCJDK9Throughput.sizeOccupedAfter.isNaN() ) {
					match1=patTotalSize1.matcher(line);
					Double totalSize=Double.NaN;
					if (match1.find()) {
						String ext2 = match1.group();
						ext2=ext2.replaceAll("\\(", "").replaceAll("\\)", "");
						if (ext2.contains("K")) {
							totalSize= Double
									.parseDouble(ext2.split("K")[0]) * 1024;
						} else {
							totalSize = Double
									.parseDouble(ext2.split("M")[0]) * 1024 * 1024;
						}
					}
					if(!totalSize.isNaN()) {
						MonoTreatGCJDK9Throughput.structGCJDK9Throughput.sizeFreeAfter=totalSize-MonoTreatGCJDK9Throughput.structGCJDK9Throughput.sizeOccupedAfter;
						
					}
				}
				// Remplissage Double durationEvacuationr = Double.NaN;
				structGCJDK9Throughput.durationEvacuation = Double.NaN;
				match1 =patDurationEvacuation.matcher(line);
				if (match1.find()) {
					String ext2 = match1.group();
					ext2=ext2.split("ms")[0].replaceAll(",",".");
					structGCJDK9Throughput.durationEvacuation =Double.valueOf(ext2);
				}
				
				 // remplissage Throughput Memoire
			    // println("Avant Traitement throughput memor"+TraiterHotspot5And6.structHotSpot.sizeHeapBefore+ " " +TraiterHotspot5And6.structHotSpot.sizeHeapAfter)
				MonoTreatGCJDK9Throughput.structGCJDK9Throughput.memThroughput = Double.NaN;
				 if (!(MonoTreatGCJDK9Throughput.structGCJDK9Throughput.sizeOccupedBefore.isNaN()) && !(MonoTreatGCJDK9Throughput.structGCJDK9Throughput.sizeOccupedAfter.isNaN())) {
				      double sweeped = MonoTreatGCJDK9Throughput.structGCJDK9Throughput.sizeOccupedBefore - MonoTreatGCJDK9Throughput.structGCJDK9Throughput.sizeOccupedAfter;
				      // println("Traitement throughput memor")
				      MonoTreatGCJDK9Throughput.memThoughputcircleArray.put(new StrucLongDouble(dateCurrent, sweeped));
				      MonoTreatGCJDK9Throughput.structGCJDK9Throughput.memThroughput =  MonoTreatGCJDK9Throughput.memThoughputcircleArray.throughput;
				    }
		
				 // remplissage  evacuationFrequency
				 MonoTreatGCJDK9Throughput.structGCJDK9Throughput.evacuationFrequency = Double.NaN;
				 
				 match1 = patEvacuationPause.matcher(line);
					if (match1.find()) {
						MonoTreatGCJDK9Throughput.evacuationFrequencycircleArray.put(new StrucLongDouble(dateCurrent, 1.0));
						 MonoTreatGCJDK9Throughput.structGCJDK9Throughput.evacuationFrequency=MonoTreatGCJDK9Throughput.evacuationFrequencycircleArray.throughput*1000; // hits/s
					}
					
					 // remplissage  cpuThrougput
					 MonoTreatGCJDK9Throughput.structGCJDK9Throughput.cpuThroughput = Double.NaN;
					 
					 match1 = patCpuThroughput.matcher(line);
						if (match1.find()) {
							String real=match1.group().split("Real=")[1].split("s")[0];
							Double realMillis=Double.valueOf(real.replaceAll(",", "."))*1000;
							
							MonoTreatGCJDK9Throughput.cpuThrouhputcircleArray.put(new StrucLongDouble(dateCurrent, realMillis));
							 MonoTreatGCJDK9Throughput.structGCJDK9Throughput.cpuThroughput=100* ( 1 - MonoTreatGCJDK9Throughput.cpuThrouhputcircleArray.throughput); //% of application CPU
						}
	}
}
