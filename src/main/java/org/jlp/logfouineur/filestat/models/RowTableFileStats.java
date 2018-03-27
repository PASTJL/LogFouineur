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

// TODO: Auto-generated Javadoc
/**
 * The Class RowTableFileStats.
 */
public class RowTableFileStats {
	
	/** The num row. */
	public Integer numRow;
	
	/** The criteria. */
	public String criteria;
	
	/** The count. */
	public Integer count;
	
	/** The per cent. */
	public Double perCent;
	
	/** The sum. */
	public Double sum;
	
	/** The average. */
	public Double average;
	
	/** The minimum. */
	public Double minimum;
	
	/** The maximum. */
	public Double maximum;
	
	/** The mediane. */
	public Double mediane; // percentile = 50%
	
	/** The percentile. */
	public Double percentile ; // 0<percentile<100
	
	/** The std dev. */
	public Double stdDev; // Standard deviation / Ecart type
	
	/**
	 * Gets the percentile.
	 *
	 * @return the percentile
	 */
	public Double getPercentile() {
		return percentile;
	}

	/**
	 * Sets the percentile.
	 *
	 * @param percentile the new percentile
	 */
	public void setPercentile(Double percentile) {
		this.percentile = percentile;
	}

	/**
	 * Gets the std dev.
	 *
	 * @return the std dev
	 */
	public Double getStdDev() {
		return stdDev;
	}

	/**
	 * Sets the std dev.
	 *
	 * @param stdDev the new std dev
	 */
	public void setStdDev(Double stdDev) {
		this.stdDev = stdDev;
	}

	

	/**
	 * Gets the mediane.
	 *
	 * @return the mediane
	 */
	public Double getMediane() {
		return mediane;
	}

	/**
	 * Sets the mediane.
	 *
	 * @param mediane the new mediane
	 */
	public void setMediane(Double mediane) {
		this.mediane = mediane;
	}

	/**
	 * Gets the maximum.
	 *
	 * @return the maximum
	 */
	public Double getMaximum() {
		return maximum;
	}

	/**
	 * Sets the maximum.
	 *
	 * @param maximum the new maximum
	 */
	public void setMaximum(Double maximum) {
		this.maximum = maximum;
	}

	/**
	 * Gets the minimum.
	 *
	 * @return the minimum
	 */
	public Double getMinimum() {
		return minimum;
	}

	/**
	 * Sets the minimum.
	 *
	 * @param minimum the new minimum
	 */
	public void setMinimum(Double minimum) {
		this.minimum = minimum;
	}

	/**
	 * Gets the average.
	 *
	 * @return the average
	 */
	public Double getAverage() {
		return average;
	}

	/**
	 * Sets the average.
	 *
	 * @param average the new average
	 */
	public void setAverage(Double average) {
		this.average = average;
	}

	/**
	 * Gets the sum.
	 *
	 * @return the sum
	 */
	public Double getSum() {
		return sum;
	}

	/**
	 * Sets the sum.
	 *
	 * @param sum the new sum
	 */
	public void setSum(Double sum) {
		this.sum = sum;
	}

	/**
	 * Gets the per cent.
	 *
	 * @return the per cent
	 */
	public Double getPerCent() {
		return perCent;
	}

	/**
	 * Sets the per cent.
	 *
	 * @param perCent the new per cent
	 */
	public void setPerCent(Double perCent) {
		this.perCent = perCent;
	}

	/**
	 * Gets the count.
	 *
	 * @return the count
	 */
	public Integer getCount() {
		return count;
	}

	/**
	 * Sets the count.
	 *
	 * @param count the new count
	 */
	public void setCount(Integer count) {
		this.count = count;
	}

	/**
	 * Gets the criteria.
	 *
	 * @return the criteria
	 */
	public String getCriteria() {
		return criteria;
	}

	/**
	 * Sets the criteria.
	 *
	 * @param criteria the new criteria
	 */
	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	/**
	 * Gets the num row.
	 *
	 * @return the num row
	 */
	public Integer getNumRow() {
		return numRow;
	}

	/**
	 * Sets the num row.
	 *
	 * @param numRow the new num row
	 */
	public void setNumRow(Integer numRow) {
		this.numRow = numRow;
	}

	/**
	 * Instantiates a new row table file stats.
	 *
	 * @param numRow the num row
	 * @param criteria the criteria
	 * @param count the count
	 * @param perCent the per cent
	 * @param sum the sum
	 * @param average the average
	 * @param minimum the minimum
	 * @param maximum the maximum
	 * @param mediane the mediane
	 * @param percentile the percentile
	 * @param stdDev the std dev
	 */
	public RowTableFileStats(Integer numRow, String criteria, Integer count, Double perCent, Double sum, Double average,
			Double minimum, Double maximum, Double mediane, Double percentile, Double stdDev) {
		
		this.numRow = numRow;
		this.criteria = criteria;
		this.count = count;
		this.perCent = perCent;
		this.sum = sum;
		this.average = average;
		this.minimum = minimum;
		this.maximum = maximum;
		this.mediane = mediane;
		this.percentile = percentile;
		this.stdDev = stdDev;
	}

	/**
	 * Instantiates a new row table file stats.
	 */
	public RowTableFileStats() {
		this(0,"",0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
		
		
	}
	

}
