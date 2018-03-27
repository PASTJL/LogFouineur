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
package org.jlp.logfouineur.filestat.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jlp.logfouineur.filestat.ui.DiagFileStats;
import org.jlp.logfouineur.util.MathUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class CumulEnregistrementStat.
 */
public class CumulEnregistrementStat {
	
	/** The rowflstats. */
	public RowTableFileStats rowflstats;
	
	/** The arraydouble. */
	Double[] arraydouble = null;
	
	/** The array list. */
	List<Double> arrayList = new ArrayList<Double>();
	
	/** The name. */
	String name = ""; // see it is same as creteria in rowflstats ??
	
	/** The hm pas. */
	Map<Long, StructStepCumul> hmPas = new HashMap<Long, StructStepCumul>();
	
	/** The sorted map. */
	TreeMap<Long, StructStepCumul> sortedMap = new TreeMap<Long, StructStepCumul>();
	
	/** The key set. */
	Set<Long> keySet = new TreeSet<Long>();

	/**
	 * Instantiates a new cumul enregistrement stat.
	 */
	public CumulEnregistrementStat() {
		rowflstats = new RowTableFileStats();
	}

	/**
	 * Adds the.
	 *
	 * @param dd the dd
	 * @param pas the pas
	 * @return the cumul enregistrement stat
	 */
	final public CumulEnregistrementStat add(Double dd, Double pas) {
		rowflstats.count += 1;
		rowflstats.sum += dd;
		if (pas == 0.0) {

			arrayList.add(dd);
		} else {

			Double dbl = dd / pas;
			Long index = dbl.longValue();

			// println("traitement du double :" + dd + " avec pas de :" + pas + " index=" +
			// index)
			if (hmPas.containsKey(index)) {
				hmPas.put(index, hmPas.get(index).add(dd));
			} else {

				hmPas.put(index, new StructStepCumul(1, dd));
			}

		}
		return this;
	}

	/**
	 * Gets the average.
	 *
	 * @return the average
	 */
	final public Double getAverage() {
		rowflstats.average = rowflstats.sum / rowflstats.count;
		return rowflstats.average;
	}

	/**
	 * Gets the mediane.
	 *
	 * @param pas the pas
	 * @return the mediane
	 */
	final public Double getMediane(Double pas) {

		if (pas == 0.0) {
			// arrayList = arrayList sortWith (_ < _)
			arrayList.sort((a, b) -> (int) (a - b));
			rowflstats.mediane = arrayList.get((int) (50 * arrayList.size() / 100));
		} else {

			Integer rang = 0;
			// set = hmPas.keySet();
			// treeSet = new TreeSet<Long>(set);
			StructStepCumul struc = null;

			// println("calcul mediane count=" + count)
			// println("mediane KeySet.lenght =" + keySet.size)
			for (Long key : keySet) {

				struc = sortedMap.get(key);

				rang = rang + struc.nbCount;
				if (rang >= (rowflstats.count / 2)) {
					// println("mediane =" + struc.moyenne + " trouve a rang =" + rang + " pour key
					// = " + key + " pour count=" + count)
					rowflstats.mediane = struc.moyenne;

					break;
				}
			}
		}
		return rowflstats.mediane;

	}

	/**
	 * Gets the percentile.
	 *
	 * @param percent the percent
	 * @param pas the pas
	 * @return the percentile
	 */
	public final Double getPercentile(Integer percent, Double pas) {
		if (pas == 0.0) {
			rowflstats.percentile = arrayList.get((int) (percent * arrayList.size() / 100));
		} else {
			
			Integer rang = 0;

			StructStepCumul struc = null;
			for (Long key : keySet) {

				struc = sortedMap.get(key);
				rang = rang + struc.nbCount;
				
				if (rang >= (percent * rowflstats.count / 100)) {
					
					rowflstats.percentile = struc.moyenne;
					// println("percentile trouve a rang =" + rang)
					break;
				}

			}
			
		}
		
		return rowflstats.percentile;
	}

	/**
	 * Gets the min.
	 *
	 * @param pas the pas
	 * @return the min
	 */
	final public Double getMin(Double pas) {
		if (pas == 0.0) {
			rowflstats.minimum = arrayList.get(0);
		} else {

			rowflstats.minimum = sortedMap.get(sortedMap.firstKey()).moyenne;
		}
		return rowflstats.minimum;
	}

	/**
	 * Gets the max.
	 *
	 * @param pas the pas
	 * @return the max
	 */
	final public Double getMax(Double pas) {
		if (pas == 0.0) {
			rowflstats.maximum = arrayList.get(arrayList.size() - 1);
		} else {
			rowflstats.maximum = sortedMap.get(sortedMap.lastKey()).moyenne;
		}

		return rowflstats.maximum;
	}

	/**
	 * Gets the std dev.
	 *
	 * @param pas the pas
	 * @return the std dev
	 */
	final public Double getStdDev(Double pas) {
		
		Double ret=0.0;
		if (pas == 0.0) {
			Double[] tabDouble= new Double[arrayList.size()];
			for ( int i =0;i< arrayList.size();i++) {
				//System.out.println("arrayList("+i+")="+arrayList.get(i));
				tabDouble[i]=arrayList.get(i);
			}
			
//			for ( int i =0;i< tabDouble.length;i++) {
//				System.out.println("tabDouble("+i+")="+tabDouble[i]);
//			}
			ret = MathUtils.getStdDev(tabDouble);
			//ret = MathUtils.getStdDev();
			
			rowflstats.stdDev=ret;
			
		} else {

			Double variance = 0.0;

			StructStepCumul struc = null;
			// set = hmPas.keySet();
			// treeSet = new TreeSet<Long>(set);

			for (Long key : keySet) {

				struc = hmPas.get(key);
				variance += struc.nbCount * Math.pow((struc.moyenne - rowflstats.average), 2);

			}
			rowflstats.stdDev = Math.sqrt(variance / (rowflstats.count - 1));
		}
		return rowflstats.stdDev;
	}

	
	/**
	 * Clear.
	 */
	final public void clear() {

		hmPas.clear();
		hmPas = null;
		sortedMap.clear();
		sortedMap = null;
		if (null != arrayList) {
			arrayList.clear();
			arrayList = null;
		}

	}

	/**
	 * Close enr.
	 *
	 * @param pas the pas
	 * @return the cumul enregistrement stat
	 */
	final public CumulEnregistrementStat closeEnr(Double pas) {
		 System.out.println("CumulEnregistrementStat debut closeEnr pas = "+pas );
		if (pas != 0.0) {
//			System.out.println("taille hmPas =" + hmPas.size());
//			 System.out.println("CumulEnregistrementStat avant sortedMap.put " );
			sortedMap.putAll(hmPas);
			// System.out.println("CumulEnregistrementStat apres sortedMap.put sortedMap.size : "+ sortedMap.size() );
			 
			keySet =  sortedMap.keySet();
		//	 System.out.println("CumulEnregistrementStat apres sortedMap.keySet" );

		} else {
			
			 
			//arrayList.sort((a, b) -> ((int) (a - b)));
			arrayList.sort((a, b) -> {return a>=b?1:-1;});
			
		}
		// System.out.println("CumulEnregistrementStat avant getMediane " );
		getMediane(pas);
	//	System.out.println("CumulEnregistrementStat avant getPercentile " );
		getPercentile(Integer.parseInt(DiagFileStats.tfPerCentile.getText()), pas);
	//	System.out.println("CumulEnregistrementStat avant getMin " );
		getMin(pas);
	//	System.out.println("CumulEnregistrementStat avant getMax " );
		getMax(pas);
	//	System.out.println("CumulEnregistrementStat avant getAverage " );
		getAverage();
	//	 System.out.println("CumulEnregistrementStat avant getStdDev" );
		getStdDev(pas);
//		 System.out.println("CumulEnregistrementStat apres getStdDev" );
		clear();
		return this;
	}

	/**
	 * Merge enr.
	 *
	 * @param enr2 the enr 2
	 * @param pas the pas
	 * @return the cumul enregistrement stat
	 */
	public final CumulEnregistrementStat mergeEnr(CumulEnregistrementStat enr2, Double pas) {

		if (pas == 0.0) {
			rowflstats.count += enr2.rowflstats.count;
			rowflstats.sum += enr2.rowflstats.sum;
			arrayList.addAll(enr2.arrayList);
			enr2.arrayList.clear();
			enr2.arrayList = null;
		} else {
			Set<Long> keySet2 = enr2.hmPas.keySet();

			StructStepCumul struc = null;

			for (Long key : keySet2) {

				struc = enr2.hmPas.get(key);
				if (hmPas.containsKey(key)) {
					hmPas.put(key, this.hmPas.get(key).merge(struc));
				} else {
					// On rajoute
					hmPas.put(key, struc);
				}

			}
			rowflstats.count += enr2.rowflstats.count;
			rowflstats.sum += enr2.rowflstats.sum;
			enr2.clear();

		}

		return this;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the name
	 * @return the cumul enregistrement stat
	 */
	public final CumulEnregistrementStat setName(String name) {
		this.name = name;
		this.rowflstats.criteria = name;
		return this;
	}

	/**
	 * Scale.
	 *
	 * @param mult the mult
	 * @return the cumul enregistrement stat
	 */
	public final CumulEnregistrementStat scale(Double mult) {
		rowflstats.sum *= mult;

		rowflstats.mediane *= mult;
		rowflstats.average *= mult;
		rowflstats.minimum *= mult;
		rowflstats.maximum *= mult;
		rowflstats.stdDev *= mult;
		rowflstats.percentile *= mult;
		return this;
	}

}
