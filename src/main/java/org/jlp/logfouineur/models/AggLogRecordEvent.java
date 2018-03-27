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

import org.jlp.logfouineur.disruptor.ParsingMain;
import org.jlp.logfouineur.records.ConfigRecord;

// TODO: Auto-generated Javadoc
/**
 * The Class AggLogRecordEvent.
 */
public class AggLogRecordEvent {

	/** The name pivot. */
	// for a period,and a pivotName, contains all values as arrays
	public String namePivot = "global";
	
	/** The is durations. */
	public Boolean[] isDurations;
	
	/** The nb vals. */
	public int nbVals = 1;
	
	/** The counts. */
	public int[] counts;
	
	/** The period. */
	public long period = 0L; // The key of the ParsingMain.allHmValue HashMap , not the aggregate gap !!!
	
	/** The averages. */
	public Double[] averages;
	
	/** The mins. */
	public Double[] mins;
	
	/** The maxs. */
	public Double[] maxs;
	
	/** The sums. */
	public Double[] sums;
	
	/** The rates. */
	public Double[] rates;
	
	/** The count parallels. */
	public int[] countParallels;
	
	/** The sums duration sup period. */
	public Double[] sumsDurationSupPeriod;
	
	/** The counts duration sup period. */
	public int[] countsDurationSupPeriod;
	
	/**
	 * Sets the.
	 *
	 * @param name the name
	 * @param period the period
	 * @param nbVals the nb vals
	 */
	/* For Disruptor pattern */
	public final void set(String name, long period, int nbVals){
		this.namePivot = name;
		this.nbVals = nbVals;
		this.period = period;
		reInit();
	}
	
	/**
	 * Sets the.
	 *
	 * @param agg the agg
	 */
	public final void set(AggLogRecordEvent agg){
		this.averages=agg.averages;
		this.counts=agg.counts;
		this.maxs=agg.maxs;
		this.mins=agg.mins;
		this.isDurations=agg.isDurations;
		this.sums=agg.sums;
		this.rates=agg.rates;
		this.countParallels=agg.countParallels;
		this.namePivot=agg.namePivot;
		this.nbVals=agg.nbVals;
		this.period=agg.period;
		this.isDurations=agg.isDurations;
	}
	
	/**
	 * Instantiates a new agg log record event.
	 *
	 * @param name the name
	 * @param period the period
	 * @param nbVals the nb vals
	 */
	public AggLogRecordEvent(String name, long period, int nbVals) {
		this.namePivot = name;
		this.nbVals = nbVals;
		this.period = period;
		reInit();
	}

	/**
	 * Gets the name pivot.
	 *
	 * @return the name pivot
	 */
	public final String getNamePivot() {
		return namePivot;
	}

	/**
	 * Sets the name pivot.
	 *
	 * @param namePivot the new name pivot
	 */
	public final void setNamePivot(String namePivot) {
		this.namePivot = namePivot;
	}

	/**
	 * Gets the nb vals.
	 *
	 * @return the nb vals
	 */
	public int getNbVals() {
		return nbVals;
	}

	/**
	 * Sets the nb vals.
	 *
	 * @param nbVals the new nb vals
	 */
	public void setNbVals(int nbVals) {
		this.nbVals = nbVals;
	}

	/**
	 * Gets the period.
	 *
	 * @return the period
	 */
	public long getPeriod() {
		return period;
	}

	/**
	 * Sets the period.
	 *
	 * @param period the new period
	 */
	public void setPeriod(long period) {
		this.period = period;
	}

	/**
	 * Instantiates a new agg log record event.
	 */
	/* For Disruptor pattern */
	public AggLogRecordEvent() {
		// TODO Auto-generated constructor stub
		this.namePivot ="";
		this.nbVals = 0;
		this.period = 0;
	}

	/**
	 * Re init.
	 */
	public final void reInit() {
		counts = new int[nbVals];
		averages = new Double[nbVals];
		mins = new Double[nbVals];
		maxs = new Double[nbVals];
		sums = new Double[nbVals];
		rates = new Double[nbVals];
		countParallels = new int[nbVals];
		sumsDurationSupPeriod=new Double[nbVals];
		countsDurationSupPeriod=new int[nbVals];
		isDurations=new Boolean[nbVals];

		for (int i = 0; i < nbVals; i++) {
			counts[i] = 0;
			mins[i] = Double.MAX_VALUE;
			averages[i] = Double.NaN;
			maxs[i] = Double.MIN_VALUE;
			sums[i] = 0d;
			rates[i] = Double.NaN;
			countParallels[i] =0;
			sumsDurationSupPeriod[i]=0d;
			countsDurationSupPeriod[i]=0;
			isDurations[i]=false;
		}

	}

	/**
	 * Close enr.
	 *
	 * @return the agg log record event
	 */
	final public AggLogRecordEvent closeEnr() {
		for (int i = 0; i < nbVals; i++) {
			if (counts[i] == 0) {
				averages[i] = Double.NaN;
				rates[i] = Double.NaN;
				
			} else {
				averages[i] = sums[i] / counts[i];
				// period must be in ms and the rate is evaluated by seconds
				rates[i] = ((Double) (1000d * counts[i])) / ConfigRecord.aggregPeriodInMillis;
				
				
			}
			// System.out.println("close enr count ="+count+" ; period
			// ="+period);
			// la period est en ms

		}
		return this;
	}

	/**
	 * Increment count parallel.
	 *
	 * @param i the i
	 * @return the agg log record event
	 */
	final public AggLogRecordEvent incrementCountParallel(int i) {
		countParallels[i]++ ;
		return this;
	}

//	final public AggLogRecord addValues(Double[] values) {
//
//		for (int indexValue = 0; indexValue < nbVals; indexValue++) {
//			if (!values[indexValue].isNaN() && !values[indexValue].isInfinite()) {
//				counts[indexValue] += 1;
//				if (sums[indexValue].isNaN())
//					sums[indexValue] = values[indexValue];
//				else
//					sums[indexValue] += values[indexValue];
//				mins[indexValue] = Math.min(mins[indexValue], values[indexValue]);
//				maxs[indexValue] = Math.max(maxs[indexValue], values[indexValue]);
//				incrementCountParallel(indexValue);
//			}
//		}
//		return this;
//	}

	/**
 * Merge.
 *
 * @param that the that
 * @return the agg log record event
 */
final  public AggLogRecordEvent  merge(
			AggLogRecordEvent that) {
		    for (int i= 0;i < nbVals;i++) {
		      sums[i] = this.sums[i] + that.sums[i];
		      counts[i] = this.counts[i] + that.counts[i];
		     
		      
		        averages[i] = sums[i] / counts[i];
		        rates[i] =  ((Double) (1000d * counts[i])) /ConfigRecord.aggregPeriodInMillis;
		        mins[i] = Math.min(this.mins[i], that.mins[i]);
		        maxs[i] = Math.max(this.maxs[i], that.maxs[i]);
		        if(ParsingMain.isDurations[i]){
		        	countParallels[i]+=that.countParallels[i];
		        	sumsDurationSupPeriod[i]=this.sumsDurationSupPeriod[i]+that.sumsDurationSupPeriod[i];
					countsDurationSupPeriod[i]=this.countsDurationSupPeriod[i]+that.countsDurationSupPeriod[i];
		        }
		      

		    }
		    return this;
		  }
	
}
