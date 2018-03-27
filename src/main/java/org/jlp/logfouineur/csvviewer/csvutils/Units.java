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
package org.jlp.logfouineur.csvviewer.csvutils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

import org.jlp.logfouineur.ui.LogFouineurMain;

// TODO: Auto-generated Javadoc
/**
 * The Class Units.
 */
public class Units {
	
	/** The separator. */
	public String[] separator = "/ *".split("\\s");
	
	/** The workspace. */
	public static String workspace = null;
	
	/** The project. */
	public static String project = null;
	
	/** The root. */
	public static String root = null;
	
	/** The log fouineur properties. */
	public static Properties logFouineurProperties;
	
	/** The value max Y. */
	public static double valueMaxY;
	
	/** The value min Y. */
	public static double valueMinY;
	
	/** The osname. */
	String osname = System.getProperty("os.name");

	static {
		workspace = System.getProperty("workspace");
		project = "projet0";
		root = System.getProperty("root");
		
		String strPath = root + File.separator + "config" + File.separator + "logFouineur.properties";
		logFouineurProperties = new Properties();
		try {
			logFouineurProperties.load(Files.newBufferedReader(new File(strPath).toPath()));
			valueMaxY = Double.parseDouble(logFouineurProperties.getProperty("logFouineur.yAxis.valueMax", "4000"));
			valueMinY = Double.parseDouble(logFouineurProperties.getProperty("logFouineur.yAxis.valueMin", "0"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** The lst time units. */
	public List<String> lstTimeUnits = Arrays
			.asList(logFouineurProperties.getProperty("logFouineur.basic.unit.time").split(";"));
	
	/** The lst others units. */
	public List<String> lstOthersUnits = Arrays
			.asList(logFouineurProperties.getProperty("logFouineur.basic.unit").split(";"));
	
	/** The lst mult units. */
	public List<String> lstMultUnits = Arrays
			.asList(logFouineurProperties.getProperty("logFouineur.unit.mult.list").split(";"));

	/**
	 * Split mult.
	 *
	 * @param othersUnit the others unit
	 * @return the string[]
	 */
	private String[] splitMult(String othersUnit) {
		
		String unitOnly = lstOthersUnits.stream().filter(unit -> othersUnit.trim().endsWith(unit)).findFirst()
				.orElse("null");
		String mult = "\"\"";
		if (!unitOnly.equals(othersUnit.trim())) {
			mult = othersUnit.trim().substring(0,othersUnit.trim().lastIndexOf(unitOnly));
		}
		return new String[] { mult, unitOnly };
	}

	/**
	 * Checks if is a duration.
	 *
	 * @param unit the unit
	 * @return true, if is a duration
	 */
	private boolean isADuration(String unit) {
		return lstTimeUnits.contains(unit.trim());
	}

	/**
	 * Kind of unit.
	 *
	 * @param unitToParse the unit to parse
	 * @return the string
	 */
	private String kindOfUnit(String unitToParse) {
		if (unitToParse.trim().contains("*"))
			return "*";

		if (unitToParse.trim().contains("/"))
			return "/";
		if (unitToParse.trim().contains(" "))
			return "*";
		return "S"; // for simple unit
	}

	/**
	 * Convert factor simple unit.
	 *
	 * @param unitSource the unit source
	 * @param unitTarget the unit target
	 * @return the double
	 */
	// fait and tested
	public double convertFactorSimpleUnit(String unitSource, String unitTarget) {
		double factor = 1;
		// Determine if value is a time duration =>
		// nanos;micros;millis;ms;s;mn;H;D;W;Y;
		if (lstTimeUnits.contains(unitSource)) {
			// Find that it is the first in the list
			int idxSource = lstTimeUnits.indexOf(unitSource.trim());
			int idxTarget = lstTimeUnits.indexOf(unitTarget.trim());
			
			if (idxSource <= idxTarget) {
				for (int i = idxSource; i < idxTarget; i++) {
					String key = "logFouineur.basic.unit.time." + lstTimeUnits.get(i) + "To" + lstTimeUnits.get(i + 1);
					String value = logFouineurProperties.getProperty(key);
					
					factor *= Double.parseDouble(value);
				}
			} else {
				for (int i = idxTarget; i < idxSource; i++) {
					
					String key = "logFouineur.basic.unit.time." + lstTimeUnits.get(i) + "To" + lstTimeUnits.get(i + 1);
					
					String value = logFouineurProperties.getProperty(key);
					
					factor *= Double.parseDouble(value);
				}
				factor = 1 / factor;
			}

		} else {
			// First detect the unit
			
			String unitPartSource = lstOthersUnits.stream().filter((unit) -> unitSource.endsWith(unit)).findFirst()
					.orElse(null);
			String unitPartTarget = lstOthersUnits.stream().filter((unitBis) -> unitTarget.endsWith(unitBis))
					.findFirst().orElse(null);
			if (null != unitPartSource && null != unitPartTarget && unitPartSource.equals(unitPartTarget)) {
				// the calcul of factor is possible
				// Retrieve the prefix of the 2 unit
				String pref1 = "\"\"";
				if (!unitSource.equals(unitPartSource)) {
					pref1 = unitSource.substring(0,unitSource.lastIndexOf(unitPartSource));
				}
				String pref2 = "\"\"";
				if (!unitTarget.equals(unitPartTarget)) {
					pref2 = unitTarget.substring(0,unitTarget.lastIndexOf(unitPartTarget));
				}

				factor = Double.parseDouble(logFouineurProperties.getProperty("logFouineur.unit.mult." + pref2))
						/ Double.parseDouble(logFouineurProperties.getProperty("logFouineur.unit.mult." + pref1));
			} else {
				factor = 0; // must be detected different of 0
				System.out.println("Error in choosing units");

			}

		}

		return factor;
	}

	/**
	 * Convert factor composite.
	 *
	 * @param unitSource the unit source
	 * @param unitTarget the unit target
	 * @return the double
	 */
	// fait and tested
	public double convertFactorComposite(String unitSource, String unitTarget) {
		double factor = 1;
		if (unitSource.contains("*") && unitTarget.contains("*")) {
			String[] tabUnitSource = unitSource.split("\\*");
			String[] tabUnitTarget = unitTarget.split("\\*");
			
			factor = convertFactorSimpleUnit(tabUnitSource[0].trim(), tabUnitTarget[0].trim())
					* convertFactorSimpleUnit(tabUnitSource[1].trim(), tabUnitTarget[1].trim());
		} else if (unitSource.contains("/") || unitTarget.contains("/")) {
			String[] tabUnitSource = unitSource.split("/");
			String[] tabUnitTarget = unitTarget.split("/");
			double den = convertFactorSimpleUnit(tabUnitSource[1].trim(), tabUnitTarget[1].trim());
			if (den != 0)
				factor = convertFactorSimpleUnit(tabUnitSource[0].trim(), tabUnitTarget[0].trim()) / den;
			else {
				factor = 0;
				System.out.println("Errors in units");
			}

		} else if (unitSource.trim().contains(" ") || unitTarget.trim().contains(" ")) {
			String[] tabUnitSource = unitSource.split("\\s");
			String[] tabUnitTarget = unitTarget.split("\\s");
			double den = convertFactorSimpleUnit(tabUnitSource[1].trim(), tabUnitTarget[1].trim());
			if (den != 0)
				factor = convertFactorSimpleUnit(tabUnitSource[0].trim(), tabUnitTarget[0].trim()) / den;
			else {
				factor = 0;
				System.out.println("Errors in units");
			}
		} else {
			System.out.println("Errors in units");
			return 0;
		}

		return factor;
	}

	/**
	 * Best factor simple unit duration.
	 *
	 * @param unitSource the unit source
	 * @param valueToMap the value to map
	 * @param contraintRule the contraint rule
	 * @return the tuple factor new unit
	 */
	// fait and tested
	private TupleFactorNewUnit bestFactorSimpleUnitDuration(String unitSource, double valueToMap,
			String contraintRule) {
		double bestFactor = 1;
		String newUnitToReturn = unitSource;
		if (null == contraintRule) {

			if (valueToMap > valueMaxY) {
				int idx = lstTimeUnits.indexOf(unitSource);
				boolean vrai = true;
				int i = 1;
				while (vrai && (idx + i) < lstTimeUnits.size()) {
					String newUnit = lstTimeUnits.get(idx + i);

					double factor = convertFactorSimpleUnit(unitSource, newUnit);
					if (factor * valueToMap > valueMaxY) {
						i++;
					} else {
						vrai = false;
						bestFactor = factor;
						newUnitToReturn = newUnit;
					}
				}
			} else {
				// Verifying if we can have a better factor

				int idx = lstTimeUnits.indexOf(unitSource);
				boolean vrai = true;
				int i = 1;
				while (vrai && (idx - i) >= 0) {
					String newUnit = lstTimeUnits.get(idx - i);

					double factor = convertFactorSimpleUnit(unitSource, newUnit);
					if (factor * valueToMap < valueMaxY) {
						bestFactor = factor;
						newUnitToReturn = newUnit;
						i++;
					} else {
						vrai = false;

					}
				}

			}
		} else {
			// the value of the target time unit is fixed.

			bestFactor = convertFactorSimpleUnit(unitSource, contraintRule);

			return new TupleFactorNewUnit(bestFactor, contraintRule);
		}
		return new TupleFactorNewUnit(bestFactor, newUnitToReturn);

	}

	/**
	 * Best factor simple unit others.
	 *
	 * @param unitSource the unit source
	 * @param valueToMap the value to map
	 * @param contraintRule the contraint rule
	 * @return the tuple factor new unit
	 */
	// fait and tested
	private TupleFactorNewUnit bestFactorSimpleUnitOthers(String unitSource, double valueToMap, String contraintRule) {
		
		double factor = 1;
		double bestFactor = 1;
		String newUnitToReturn = unitSource;
		// Extract the Mult Source is any
		String unitPartSource = lstOthersUnits.stream().filter((unit1) -> unitSource.endsWith(unit1)).findFirst()
				.orElse(null);
		String pref1 = "\"\"";
		String unit = unitPartSource;
		if (!unitSource.equals(unitPartSource)) {
			pref1 = unitSource.substring(0,unitSource.lastIndexOf(unitPartSource));
		}
		if (null == contraintRule) {

			if (factor * valueToMap > valueMaxY) {
				int idx = lstMultUnits.indexOf(pref1);
				boolean vrai = true;
				int i = 1;

				String newMult = "\"\"";
				while (vrai && (idx + i) < lstMultUnits.size()) {

					newMult = lstMultUnits.get(idx + i);

					String unitTarget = unitPartSource;
					if (!newMult.equals("\"\"")) {
						unitTarget = newMult + unitTarget;
					}
					factor = this.convertFactorSimpleUnit(unitSource, unitTarget);

					if (factor * valueToMap > valueMaxY) {

						i++;
					} else {
						vrai = false;
						bestFactor = factor;
						if (newMult.equals("\"\"")) {
							newUnitToReturn = unit;
						} else
							newUnitToReturn = newMult + unit;
					}
				}
			} else {
				// Verifying if we can have a better factor

				int idx = lstMultUnits.indexOf(pref1);
				boolean vrai = true;
				int i = 1;
				String newMult = "\"\"";
				while (vrai && (idx - i) >= 0) {
					newMult = lstMultUnits.get(idx - i);
					
					String unitTarget = unitPartSource;
					if (!newMult.equals("\"\"")) {
						unitTarget = newMult + unitTarget;
					}
					
					factor = this.convertFactorSimpleUnit(unitSource, unitTarget);
					
					if (factor * valueToMap < valueMaxY) {
						i++;
						bestFactor = factor;
						if (newMult.equals("\"\"")) {
							newUnitToReturn = unit;
						} else
							newUnitToReturn = newMult + unit;

					} else {
						vrai = false;

					}
				}

			}

		} else {
			// the rule is forced and applied
			
			String newMult = contraintRule.trim();
			String newUnit=unitPartSource;
			if(!newMult.equals("\"\"")){
				newUnit=newMult+newUnit;
			}
//			factor = Double.parseDouble(logFouineurProperties.getProperty("logFouineur.unit.mult." + pref1))
//					/ Double.parseDouble(logFouineurProperties.getProperty("logFouineur.unit.mult." + newMult));
			factor = this.convertFactorSimpleUnit(unitSource, newUnit);
			bestFactor = factor;
			if (newMult.equals("\"\"")) {
				newUnitToReturn = unitPartSource;
			} else
				newUnitToReturn = newMult + unitPartSource;
		}
		return new TupleFactorNewUnit(bestFactor, newUnitToReturn);
	}

	/**
	 * Best factor.
	 *
	 * @param unitSource the unit source
	 * @param valueToMap the value to map
	 * @param contraintRule the contraint rule
	 * @return the tuple factor new unit
	 */
	// fait and tested
	public TupleFactorNewUnit bestFactor(String unitSource, double valueToMap, String contraintRule)
	// like */s or */ms can be null
	// # unites used: Be care that each unit has only one dimension ( no
	// surfaces and no volumes for example)
	// logFouineur.basic.unit=metre;m;req;unit;hit;octet;byte;percent;nb;
	// ## the order is mandatory for logFouineur.basic.unit.time
	// logFouineur.basic.unit.time=nanos;micros;ms;millis;s;D;W;Y;
	//
	//
	// # Unit multiplicator
	// ## the order is mandatory for logFouineur.unit.mult.list
	// logFouineur.unit.mult.list=nano;micro;milli;"";K;M;G;T;
	{

		TupleFactorNewUnit ret = null;
		double valueToMapAbs = Math.abs(valueToMap);
		switch (kindOfUnit(unitSource)) {
		case "*":
			String[] tabUnitSource = unitSource.split("\\*");
			if (isADuration(tabUnitSource[0]) && !isADuration(tabUnitSource[1])) {
				//tested
				ret = bestFactorCompositeMultUnitDuration(unitSource, valueToMapAbs, contraintRule, 0);
			} else if (!isADuration(tabUnitSource[0]) && isADuration(tabUnitSource[1])) {
				//tested
				ret = bestFactorCompositeMultUnitDuration(unitSource, valueToMapAbs, contraintRule, 1);
			} else if (isADuration(tabUnitSource[0]) && isADuration(tabUnitSource[1])) {
				//tested
				ret = bestFactorCompositeMultUnitDuration(unitSource, valueToMapAbs, contraintRule, 2);
			} else {
				//tested
				ret = bestFactorCompositeOthersMultOthers(unitSource, valueToMapAbs, contraintRule);
			}
			break;
		case "/":
			tabUnitSource = unitSource.split("/");
			if (isADuration(tabUnitSource[0]) && !isADuration(tabUnitSource[1])) {
				//tested
				ret = bestFactorCompositePerUnitDuration(unitSource, valueToMapAbs, contraintRule, 0);
			} else if (!isADuration(tabUnitSource[0]) && isADuration(tabUnitSource[1])) {
				//tested
				ret = bestFactorCompositePerUnitDuration(unitSource, valueToMapAbs, contraintRule, 1);
			} else if (isADuration(tabUnitSource[0]) && isADuration(tabUnitSource[1])) {
				//tested
				ret = bestFactorCompositePerUnitDuration(unitSource, valueToMapAbs, contraintRule, 2);
			} else {
				//tested
				ret = bestFactorCompositeOthersPerOthers(unitSource, valueToMapAbs, contraintRule);
			}
			break;
		case "S":
			if (isADuration(unitSource)) {
				//tested
				ret = bestFactorSimpleUnitDuration(unitSource, valueToMapAbs, contraintRule);
			} else {
				//tested
				ret = bestFactorSimpleUnitOthers(unitSource, valueToMapAbs, contraintRule);
			}

			break;
		}

		return ret;
	}

	/**
	 * Best factor composite others per others.
	 *
	 * @param unitSource the unit source
	 * @param valueToMap the value to map
	 * @param contraintRule the contraint rule
	 * @return the tuple factor new unit
	 */
	// fait and tested
	private TupleFactorNewUnit bestFactorCompositeOthersPerOthers(String unitSource, double valueToMap,
			String contraintRule) {
		
		// case of constraints Rule null, _ / K, K / _ , K / M
		TupleFactorNewUnit tupleToReturn = null;
		if (null == contraintRule) {
			tupleToReturn = bestFactorCompositeOthersPerOthersNoConstraint(unitSource, valueToMap);
		} else if (contraintRule.trim().startsWith("_")) {
			tupleToReturn = bestFactorCompositeOthersPerOthersConstraint_per(unitSource, valueToMap, contraintRule);
		} else if (contraintRule.trim().endsWith("_")) {
			tupleToReturn = bestFactorCompositeOthersPerOthersConstraintPer_(unitSource, valueToMap, contraintRule);
		} else {
			// only a conversion K / M
			String[] tabUnitSource = unitSource.split("/");
			String[] tabMultUnit = contraintRule.split("/");
			String unit1 = lstOthersUnits.stream().filter(unit -> tabUnitSource[0].trim().endsWith(unit)).findFirst()
					.orElse("null");
			String unit2 = lstOthersUnits.stream().filter(unit -> tabUnitSource[1].trim().endsWith(unit)).findFirst()
					.orElse("null");
			double factor = convertFactorComposite(unitSource,
					tabMultUnit[0].trim() + unit1 + " / " + tabMultUnit[1].trim() + unit2);
			tupleToReturn = new TupleFactorNewUnit(factor,
					tabMultUnit[0].trim() + unit1 + " / " + tabMultUnit[1].trim() + unit2);
		}
		return tupleToReturn;
	}

	/**
	 * Best factor composite others per others constraint per.
	 *
	 * @param unitSource the unit source
	 * @param valueToMap the value to map
	 * @param constraintRule the constraint rule
	 * @return the tuple factor new unit
	 */
	// fait and tested
	private TupleFactorNewUnit bestFactorCompositeOthersPerOthersConstraintPer_(String unitSource, double valueToMap,
			String constraintRule) {
		
		// Constraint are like K / _
		String[] tabUnitSource = unitSource.split("/");
		String[] tabUnit1 = splitMult(tabUnitSource[0].trim());
		String[] tabUnit2 = splitMult(tabUnitSource[1].trim());
		double bestFactor = 1;
		String newUnitToReturn = unitSource;
		TupleFactorNewUnit tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
		int idx1 = this.lstMultUnits.indexOf(tabUnit1[0]);
		int idx2 = this.lstMultUnits.indexOf(tabUnit2[0]);
		double factor1 = 1;
		double factor2 = 1;
		int i = 1, j = 1;
		String mult1 = tabUnit1[0];
		String mult2 = tabUnit2[0];
		// We play firts the first rule
		String[] tabConstraint = constraintRule.split("/");
		String tmpTarget=tabUnit1[1].trim();
		if(!tabConstraint[0].trim().equals("\"\""))tmpTarget=tabConstraint[0].trim()+tmpTarget;
		factor1=this.convertFactorSimpleUnit(tabUnitSource[0].trim(), tmpTarget);
		
		
//		factor1 = Double
//				.parseDouble(logFouineurProperties.getProperty("logFouineur.unit.mult." + tabConstraint[0].trim()))
//				/ Double.parseDouble(logFouineurProperties.getProperty("logFouineur.unit.mult." + mult1));
		bestFactor = factor1;
		mult1 = tabConstraint[0].trim();
		newUnitToReturn =tmpTarget + " / " + tabUnitSource[1].trim();
		tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
		// Only test with the 2nd value
		if (bestFactor * valueToMap > valueMaxY) {
			boolean vrai = true;
			while (vrai && (idx2 - j) >= 0) {
				String tmpMult2 = lstMultUnits.get(idx2 - j);
				if(tmpMult2.equals("\"\""))tmpMult2="";
				factor2=this.convertFactorSimpleUnit(tabUnitSource[1].trim(), tmpMult2+tabUnit2[1].trim());
//				factor2 = Double.parseDouble(logFouineurProperties.getProperty("logFouineur.unit.mult." + tmpMult2))
//						/ Double.parseDouble(logFouineurProperties.getProperty("logFouineur.unit.mult." + mult2));
				if ((factor1 / factor2) * valueToMap > valueMaxY) {
					j++;

				} else {
					bestFactor = factor1 / factor2;
					
					newUnitToReturn =tmpTarget + " / " + tmpMult2 + tabUnit2[1].trim();
					tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
					vrai = false;
				}
			}
		} else {
			// try to improve with the second value
			
			boolean vrai = true;
			while (vrai && (idx2 + j) < lstMultUnits.size()) {
				String tmpMult2Bis = lstMultUnits.get(idx2 + j);
				if(tmpMult2Bis.equals("\"\""))tmpMult2Bis="";
				double tmpFactor2=this.convertFactorSimpleUnit(tabUnitSource[1].trim(), tmpMult2Bis+tabUnit2[1].trim());

//				factor2 = Double.parseDouble(logFouineurProperties.getProperty("logFouineur.unit.mult." + tmpMult2))
//						/ Double.parseDouble(logFouineurProperties.getProperty("logFouineur.unit.mult." + mult2));
				if ((factor1 / tmpFactor2) * valueToMap < valueMaxY) {
					j++;
					factor2=tmpFactor2;
												
					bestFactor = factor1 / factor2;
					
					newUnitToReturn =tmpTarget + " / " + tmpMult2Bis + tabUnit2[1].trim();
					tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);

				} else {

					vrai = false;
				}
			}

		}

		return tupleToReturn;
	}

	/**
	 * Best factor composite others per others constraint per.
	 *
	 * @param unitSource the unit source
	 * @param valueToMap the value to map
	 * @param constraintRule the constraint rule
	 * @return the tuple factor new unit
	 */
	// fait and tested
	private TupleFactorNewUnit bestFactorCompositeOthersPerOthersConstraint_per(String unitSource, double valueToMap,
			String constraintRule) {
	
		// Constraint are like _ / K
		String[] tabUnitSource = unitSource.split("/");
		String[] tabUnit1 = splitMult(tabUnitSource[0].trim());
		String[] tabUnit2 = splitMult(tabUnitSource[1].trim());
		double bestFactor = 1;
		String newUnitToReturn = unitSource;
		TupleFactorNewUnit tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
		int idx1 = this.lstMultUnits.indexOf(tabUnit1[0]);
		int idx2 = this.lstMultUnits.indexOf(tabUnit2[0]);
		double factor1 = 1;
		double factor2 = 1;
		int i = 1;
		String mult1 = tabUnit1[0];
		String mult2 = tabUnit2[0];
		// We play firts the second rule
		String[] tabConstraint = constraintRule.split("/");
		factor2 = Double
				.parseDouble(logFouineurProperties.getProperty("logFouineur.unit.mult." + tabConstraint[1].trim()))
				/ Double.parseDouble(logFouineurProperties.getProperty("logFouineur.unit.mult." + mult2));
		bestFactor = factor1 / factor2;
		mult2 = tabConstraint[1].trim();
		// Only test with the first value
		if (bestFactor * valueToMap > valueMaxY) {
			boolean vrai = true;
			while (vrai && (idx1 + i) >= 0) {
				String tmpMult1 = lstMultUnits.get(idx1 + i);
				factor1 = Double.parseDouble(logFouineurProperties.getProperty("logFouineur.unit.mult." + tmpMult1))
						/ Double.parseDouble(logFouineurProperties.getProperty("logFouineur.unit.mult." + mult1));
				if ((factor1 / factor2) * valueToMap > valueMaxY) {
					i++;

				} else {
					bestFactor = factor1 / factor2;
					newUnitToReturn = tmpMult1 + tabUnit1[1].trim() + " / " + mult2 + tabUnit2[1].trim();
					tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
					vrai = false;
				}
			}
		} else {
			// try to improve with the first value

			boolean vrai = true;
			while (vrai && (idx1 - i) > 0) {
				String tmpMult1 = lstMultUnits.get(idx1 - i);
				factor1 = Double.parseDouble(logFouineurProperties.getProperty("logFouineur.unit.mult." + tmpMult1))
						/ Double.parseDouble(logFouineurProperties.getProperty("logFouineur.unit.mult." + mult1));
				if ((factor1 / factor2) * valueToMap < valueMaxY) {
					i++;
					bestFactor = factor1 / factor2;
					newUnitToReturn = tmpMult1 + tabUnit1[1].trim() + " / " + mult2 + tabUnit2[1].trim();
					tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);

				} else {

					vrai = false;
				}
			}

		}

		return tupleToReturn;

	}

	/**
	 * Best factor composite others per others no constraint.
	 *
	 * @param unitSource the unit source
	 * @param valueToMap the value to map
	 * @return the tuple factor new unit
	 */
	// fait and tested
	private TupleFactorNewUnit bestFactorCompositeOthersPerOthersNoConstraint(String unitSource, double valueToMap) {
		// No Constraint
		
		String[] tabUnitSource = unitSource.split("/");
		String[] tabUnit1 = splitMult(tabUnitSource[0].trim());
		String[] tabUnit2 = splitMult(tabUnitSource[1].trim());
		double bestFactor = 1;
		String newUnitToReturn = unitSource;
		TupleFactorNewUnit tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
		int idx1 = this.lstMultUnits.indexOf(tabUnit1[0]);
		int idx2 = this.lstMultUnits.indexOf(tabUnit2[0]);
		double factor1 = 1;
		double factor2 = 1;
		int i = 1, j = 1;
		String mult1 = tabUnit1[0];
		String mult2 = tabUnit2[0];

		bestFactor = factor1 / factor2;

		if (bestFactor * valueToMap > valueMaxY) {
			boolean vrai = true;
			while (vrai && (idx1 + i) < lstMultUnits.size()) {
				String tmpMult1 = lstMultUnits.get(idx1 + i);
				if(tmpMult1.equals("\"\""))
				{
					tmpMult1="";
				}
				
//				factor1 = Double.parseDouble(logFouineurProperties.getProperty("logFouineur.unit.mult." + tmpMult1))
//						/ Double.parseDouble(logFouineurProperties.getProperty("logFouineur.unit.mult." + mult1));
				factor1=this.convertFactorSimpleUnit(tabUnitSource[0].trim(), tmpMult1+tabUnit1[1]);
				
				if ((factor1 / factor2) * valueToMap > valueMaxY) {
					if ((idx2 - j) >= 0) {
						String tmpMult2 = lstMultUnits.get(idx2 - j);
						if(tmpMult2.equals("\"\""))
						{
							tmpMult2="";
						}
						factor2=this.convertFactorSimpleUnit(tabUnitSource[1].trim(), tmpMult2+tabUnit2[1]);
						
//						factor2 = Double
//								.parseDouble(logFouineurProperties.getProperty("logFouineur.unit.mult." + tmpMult2))
//								/ Double.parseDouble(
//										logFouineurProperties.getProperty("logFouineur.unit.mult." + mult2));
						if ((factor1 / factor2) * valueToMap > valueMaxY) {
							j++;
						} else {
							bestFactor = factor1 / factor2;
							newUnitToReturn = tmpMult1 + tabUnit1[1].trim() + " / " + tmpMult2 + tabUnit2[1].trim();
							tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
							vrai = false;
						}

					}
					i++;

				} else {
					bestFactor = factor1 / factor2;
					if(mult2.equals("\"\""))mult2="";
					newUnitToReturn = tmpMult1 + tabUnit1[1].trim() + " / " + mult2 + tabUnit2[1].trim();
					tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
					vrai = false;
				}
			}
		} else {
			// try to improve with the 2 value

			boolean vrai = true;
			while (vrai && (idx1 - i) > 0) {
				String tmpMult1 = lstMultUnits.get(idx1 - i);
				factor1 = Double.parseDouble(logFouineurProperties.getProperty("logFouineur.unit.mult." + tmpMult1))
						/ Double.parseDouble(logFouineurProperties.getProperty("logFouineur.unit.mult." + mult1));
				if ((factor1 / factor2) * valueToMap < valueMaxY) {
					bestFactor = factor1 / factor2;
					newUnitToReturn = tmpMult1 + tabUnit1[1].trim() + " / " + mult2 + tabUnit2[1].trim();
					tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
					if ((idx2 + j) < lstMultUnits.size()) {
						String tmpMult2 = lstMultUnits.get(idx2 + j);
						factor2 = Double
								.parseDouble(logFouineurProperties.getProperty("logFouineur.unit.mult." + tmpMult2))
								/ Double.parseDouble(
										logFouineurProperties.getProperty("logFouineur.unit.mult." + mult2));

						if ((factor1 / factor2) * valueToMap < valueMaxY) {
							j++;
							bestFactor = factor1 / factor2;
							mult2 = tmpMult2;
							newUnitToReturn = tmpMult1 + tabUnit1[1].trim() + " / " + mult2 + tabUnit2[1].trim();
							tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
						} else {
							vrai = false;
						}
						i++;

					}
				} else {

					vrai = false;
				}
			}

		}

		return tupleToReturn;

	}

	/**
	 * Best factor composite per unit duration.
	 *
	 * @param unitSource the unit source
	 * @param valueToMapAbs the value to map abs
	 * @param constraintRule the constraint rule
	 * @param duration the duration
	 * @return the tuple factor new unit
	 */
	// fait and tested
	private TupleFactorNewUnit bestFactorCompositePerUnitDuration(String unitSource, double valueToMapAbs,
			String constraintRule, int duration) {
		
		// rule constraint are given by the following example pattern
		// possibilities :
		// duration * duration => null ; _ / ms ; ms / _; s / ms
		// duration * otherUnit => null; _ / K ; ms / K
		// otherUnit * duration => null; M / _ ; K / ms

		TupleFactorNewUnit toReturn = null;
		switch (duration) {
		case 0:
			// the second unit is not a duration
			if (null == constraintRule){
				// tested 
				toReturn = bestFactorTimePerOtherNoConstraint(unitSource, valueToMapAbs);
			}
			else{
				// tested
				
				toReturn = bestFactorTimePerOtherWithConstraint(unitSource, valueToMapAbs, constraintRule);
			}
			break;
		case 1:
			// the first unit is not a duration
			//tested
			if (null == constraintRule){
				
				toReturn = bestFactorOtherPerTimeNoConstraint(unitSource, valueToMapAbs);
			}
			else
			{
				// tested
				
				toReturn = bestFactorOtherPerTimeWithConstraint(unitSource, valueToMapAbs, constraintRule);
			}
			break;

		case 2:
			// both are duration
			if (null == constraintRule)
				//tested
				toReturn = bestFactorTimePerTimeNoConstraint(unitSource, valueToMapAbs);//tested
			else
				toReturn = bestFactorTimePerTimeWithConstraint(unitSource, valueToMapAbs, constraintRule); //tested
			break;
		}

		return toReturn;

	}

	/**
	 * Best factor time per time with constraint.
	 *
	 * @param unitSource the unit source
	 * @param valueToMapAbs the value to map abs
	 * @param constraintRule the constraint rule
	 * @return the tuple factor new unit
	 */
	// fait and tested
	private TupleFactorNewUnit bestFactorTimePerTimeWithConstraint(String unitSource, double valueToMapAbs,
			String constraintRule) {
		
		// the rule is like ms / ms just a convertion
		if(!constraintRule.trim().contains("_")){
			// tested
		double bestFactort = this.convertFactorComposite(unitSource, constraintRule);
		String newUnitToReturn = constraintRule;
		// TODO Auto-generated method stub
		return new TupleFactorNewUnit(bestFactort, newUnitToReturn);
		}
		else if (constraintRule.trim().startsWith("_")){
			// tested
			return bestFactorTimePerTime_perConstraint(unitSource,valueToMapAbs,constraintRule);
		}
		else {
			//tested
			return bestFactorTimePerTimePer_Constraint(unitSource,valueToMapAbs,constraintRule);
		}
	}

	/**
	 * Best factor time per time per constraint.
	 *
	 * @param unitSource the unit source
	 * @param valueToMapAbs the value to map abs
	 * @param constraintRule the constraint rule
	 * @return the tuple factor new unit
	 */
	//tested
	private TupleFactorNewUnit bestFactorTimePerTime_perConstraint(String unitSource,double valueToMapAbs, String constraintRule) {
		//case _ / s where _ is a duration
		
		String[] tabUnitSource = unitSource.split("/");
		String[] tabConstraint=constraintRule.split("/");
		double bestFactor = 1;
		String newUnitToReturn = unitSource;
		
		int idx1=this.lstTimeUnits.indexOf(tabUnitSource[0].trim());
		int idx2 = this.lstTimeUnits.indexOf(tabUnitSource[1].trim());
	
		double factor1 = 1;
		double factor2 = this.convertFactorSimpleUnit(tabUnitSource[1].trim(), tabConstraint[1].trim());
		bestFactor=factor1/factor2;
		newUnitToReturn=tabUnitSource[0].trim()+" / "+tabConstraint[1].trim().replaceAll("\"", "");
		TupleFactorNewUnit tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
		
		
		int i = 1;
		//playing with first Duration
		
		if (bestFactor * valueToMapAbs > valueMaxY ){
			boolean vrai=true;
			while(vrai && (idx1 +i) < this.lstTimeUnits.size() ){
				String newUnit1=lstTimeUnits.get(idx1+i);
				factor1=this.convertFactorSimpleUnit(tabUnitSource[0].trim(), newUnit1);
				if ((factor1 / factor2)* valueToMapAbs > valueMaxY ){
					i++;
				} else
				{
					vrai=false;
					bestFactor=factor1/factor2;
					newUnitToReturn=newUnit1+" / "+tabConstraint[1].trim().replaceAll("\"", "");
					 tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
				}
			}
			
		} else {
			//try to improve
			boolean vrai=true;
			while(vrai && (idx1 - i) >= 0 ){
				String newUnit1=lstTimeUnits.get(idx1 - i);
				double tmpFactor1=this.convertFactorSimpleUnit(tabUnitSource[0].trim(), newUnit1);
				if ((tmpFactor1 / factor2)* valueToMapAbs <  valueMaxY ){
					factor1=tmpFactor1;
					bestFactor=factor1/factor2;
					newUnitToReturn=newUnit1+" / "+tabConstraint[1].trim().replaceAll("\"", "");
					 tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
					i++;
				} else
				{
					vrai=false;
					
				}
			}
		}
		
		
		return tupleToReturn;
	}

	/**
	 * Best factor time per time per constraint.
	 *
	 * @param unitSource the unit source
	 * @param valueToMapAbs the value to map abs
	 * @param constraintRule the constraint rule
	 * @return the tuple factor new unit
	 */
	//tested
	private TupleFactorNewUnit bestFactorTimePerTimePer_Constraint(String unitSource,double valueToMapAbs, String constraintRule) {

		//case s / _ where _ is a duration
		
		String[] tabUnitSource = unitSource.split("/");
		String[] tabConstraint=constraintRule.split("/");
		double bestFactor = 1;
		String newUnitToReturn = unitSource;
		
		int idx1=this.lstTimeUnits.indexOf(tabUnitSource[0].trim());
		int idx2 = this.lstTimeUnits.indexOf(tabUnitSource[1].trim());
	
		
		double factor1 = this.convertFactorSimpleUnit(tabUnitSource[0].trim(), tabConstraint[0].trim());;
		double factor2 = 1;
		bestFactor=factor1/factor2;
		newUnitToReturn= tabConstraint[0].trim()+" / "+tabUnitSource[1].trim();
		TupleFactorNewUnit tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
		
		
		int i = 1;
		//playing with second Duration
		
		if (bestFactor * valueToMapAbs > valueMaxY ){
			boolean vrai=true;
			while(vrai && (idx2 - i) >=0 ){
				String newUnit2=lstTimeUnits.get(idx2 - i);
				factor2=this.convertFactorSimpleUnit(tabUnitSource[1].trim(), newUnit2);
				if ((factor1 / factor2)* valueToMapAbs > valueMaxY ){
					i++;
				} else
				{
					vrai=false;
					bestFactor=factor1/factor2;
					newUnitToReturn=tabConstraint[0].trim()+" / "+newUnit2;
					 tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
				}
			}
			
		} else {
			//try to improve
			boolean vrai=true;
			while(vrai && (idx1 + i) < this.lstTimeUnits.size()  ){
				String newUnit2=lstTimeUnits.get(idx2 + i);
				double tmpFactor2=this.convertFactorSimpleUnit(tabUnitSource[1].trim(), newUnit2);
				if ((factor1 / tmpFactor2)* valueToMapAbs <  valueMaxY ){
					factor2=tmpFactor2;
					bestFactor=factor1/factor2;
					newUnitToReturn=tabConstraint[0].trim()+" / "+newUnit2;
					 tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
					i++;
				} else
				{
					vrai=false;
					
				}
			}
		}
		
		
		return tupleToReturn;
	
	}

	/**
	 * Best factor time per time no constraint.
	 *
	 * @param unitSource the unit source
	 * @param valueToMapAbs the value to map abs
	 * @return the tuple factor new unit
	 */
	// fait tested
	private TupleFactorNewUnit bestFactorTimePerTimeNoConstraint(String unitSource, double valueToMapAbs) {
		String[] tabUnitSource = unitSource.split("/");
		String newUnitToReturn = unitSource;
		double factor1 = 1;
		double factor2 = 1;
		double bestFactor = factor1 / factor2;
		TupleFactorNewUnit tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
		int idx1 = lstTimeUnits.indexOf(tabUnitSource[0].trim());
		int idx2 = lstTimeUnits.indexOf(tabUnitSource[1].trim());
		int i = 1, j = 1;
		String newUnit1 = tabUnitSource[0].trim();
		String newUnit2 = tabUnitSource[1].trim();
		if (bestFactor * valueToMapAbs > valueMaxY) {
			boolean vrai = true;
			while (vrai && (idx1 + i) < lstTimeUnits.size()) {
				newUnit1 = lstTimeUnits.get(idx1 + i);
				factor1 = this.convertFactorSimpleUnit(tabUnitSource[0].trim(), newUnit1);
				if ((factor1 / factor2) * valueToMapAbs > valueMaxY) {
					if ((idx2 - j) >= 0) {
						newUnit2 = lstTimeUnits.get(idx2 - j);
						factor2 = this.convertFactorSimpleUnit(tabUnitSource[1].trim(), newUnit2);
						if ((factor1 / factor2) * valueToMapAbs > valueMaxY) {
							j++;
						} else {
							vrai = false;
							bestFactor = factor1 / factor2;
							newUnitToReturn = newUnit1 + " / " + newUnit2;
							tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
						}
					}
					i++;
				} else {
					vrai = false;
					bestFactor = factor1 / factor2;
					newUnitToReturn = newUnit1 + " / " + newUnit2;
					tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
				}

			}

		} else {
			// try to improve
			boolean vrai = true;
			while (vrai && (idx1 - i) > 0) {
				String newUnit1Bis = lstTimeUnits.get(idx1 - i);
				double tmpFactor1 = this.convertFactorSimpleUnit(tabUnitSource[0].trim(), newUnit1Bis);
				if ((tmpFactor1 / factor2) * valueToMapAbs < valueMaxY) {
					factor1=tmpFactor1;
					bestFactor = factor1 / factor2;
					newUnit1=newUnit1Bis;
					newUnitToReturn = newUnit1 + " / " + newUnit2;
					tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
					if ((idx2 + j) < lstTimeUnits.size()) {
						String newUnit2Bis = lstTimeUnits.get(idx2 + j);
						double tmpFactor2 = this.convertFactorSimpleUnit(tabUnitSource[1].trim(), newUnit2Bis);
						if ((factor1 / tmpFactor2) * valueToMapAbs < valueMaxY) {
							j++;
							factor2=tmpFactor2;
							bestFactor = factor1 / factor2;
							newUnit2=newUnit2Bis;
							newUnitToReturn = newUnit1 + " / " + newUnit2;
							tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
						} else {
							vrai = false;

						}
					}
					i++;
					bestFactor = factor1 / factor2;
					newUnitToReturn = newUnit1 + " / " + newUnit2;
					tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
				} else {
					vrai = false;

				}

			}

		}

		return tupleToReturn;
	}

	/**
	 * Best factor other per time with constraint.
	 *
	 * @param unitSource the unit source
	 * @param valueToMap the value to map
	 * @param constraintRule the constraint rule
	 * @return the tuple factor new unit
	 */
	// fait tested
	private TupleFactorNewUnit bestFactorOtherPerTimeWithConstraint(String unitSource, double valueToMap,
			String constraintRule) {

		// Rule treated are type like :
		// K / s, _ / s , "" / s
		double bestFactor = 1;
		String newUnitToReturn = "";
		String[] tabUnit = unitSource.split("/");
		String pref1 = "\"\"";
		String unitSource1 = tabUnit[0];
		String[] tabSplitUnit1=this.splitMult(tabUnit[0]);
		String unitPartSource1 = tabSplitUnit1[1].trim();
		pref1=tabSplitUnit1[0].trim();
		
		
		
		String newUnit2 = unitSource1;
		if (!constraintRule.contains("_")) {
			// just a convertion
			// treat case of rule "\"\" /  s"
			
			String newUnit1= unitPartSource1+" / "+constraintRule.split("/")[1].trim();
			if (!constraintRule.split("/")[0].trim().equals("\"\"")){
				newUnit1=constraintRule.split("/")[0].trim()+newUnit1;
			}
			bestFactor = convertFactorComposite(unitSource, newUnit1);
			return new TupleFactorNewUnit(bestFactor, newUnit1);
		}

		else if (constraintRule.trim().startsWith("_")) {
			
			// first is Other
			String newMult = constraintRule.split("/")[0].trim();

			// first fix the factor2 with the rule of 2nd Unit that is time
			
			String newUnit = constraintRule.split("/")[1].trim();
			
			double factorMandatory = convertFactorSimpleUnit(tabUnit[1].trim(), newUnit);
			 factorMandatory = 1 / factorMandatory;
			 double factor=factorMandatory;
			if (factor * valueToMap > valueMaxY) {
				boolean vrai = true;
				int i = 1;

				int idx = this.lstMultUnits.indexOf(pref1);
				// We play only with first unit which is Other
				
				// TO DO JLP
				while (vrai && (idx + i) < lstMultUnits.size()) {

					String newMult1 = lstMultUnits.get(idx + i);
					String newUnit1=newMult1.replaceAll("\"","")+unitPartSource1;
					factor = factorMandatory * convertFactorSimpleUnit(tabUnit[0].trim(), newUnit1);
					if (factor * valueToMap > valueMaxY) {

						i++;
					} else {
						vrai = false;
						bestFactor = factor;
						newUnitToReturn = newUnit1 + " / " + newUnit;
					}
				}
			} else {
				// to try to improve
			
				
				boolean vrai = true;
				int i = 1;
				// only with the first unit which is Other
				int idx = lstMultUnits.indexOf(pref1);

				while (vrai && (idx - i) >= 0) {
					String newMult1 = lstMultUnits.get(idx - i);
					String newUnit1=newMult1.replaceAll("\"","")+unitPartSource1;
					double tmpfactor = factorMandatory * convertFactorSimpleUnit(tabUnit[0].trim(), newUnit1);
					
					if (tmpfactor * valueToMap < valueMaxY) {
						
						factor=tmpfactor;
						bestFactor = factor;
						newUnitToReturn = newUnit1 + " / " + newUnit;
						i++;
					} else {
						vrai = false;
						
					}

				}
			}
		} else {
			// First is mandatory and it is Other
			// Second is time
			

			// first fix the factor with the rule of 1rst Unit
			String newUnit =  unitPartSource1;
			String newMult=constraintRule.split("/")[0].trim();
			if (!newMult.equals("\"\"")) {
				newUnit = newMult + unitPartSource1;
			}
			double factorMandatory = convertFactorSimpleUnit(tabUnit[0].trim(), newUnit);
			double factor = factorMandatory;
			if (factorMandatory * valueToMap > valueMaxY) {
				boolean vrai = true;
				int i = 1;

				int idx = lstTimeUnits.indexOf(tabUnit[1].trim());
				// We play only with second unit that is time
				while (vrai && (idx + i) < lstTimeUnits.size()) {

					String newUnit1 = lstTimeUnits.get(idx + i);
					factor =  factorMandatory * convertFactorSimpleUnit(tabUnit[1].trim(), newUnit1);
					if (factor * valueToMap > valueMaxY) {

						i++;
					} else {
						vrai = false;
						bestFactor = factor;
						newUnitToReturn = newUnit + " / " + newUnit1;
					}
				}
			} else {
				// to try to improve
				boolean vrai = true;
				int i = 1;

				int idx = lstTimeUnits.indexOf(tabUnit[1]);

				while (vrai && (idx - i) >= 0) {
					String newUnit1 = lstTimeUnits.get(idx - i);
					double tmpfactor = factorMandatory * convertFactorSimpleUnit(tabUnit[1].trim(), newUnit1);
					if (tmpfactor * valueToMap < valueMaxY) {
						factor=tmpfactor;
						bestFactor = factor;
						newUnitToReturn = newUnit + " / " + newUnit1;
						i++;
					} else {
						vrai = false;
						
					}

				}
			}

		}

		return new TupleFactorNewUnit(bestFactor, newUnitToReturn);

	}

	/**
	 * Best factor other per time no constraint.
	 *
	 * @param unitSource the unit source
	 * @param valueToMapAbs the value to map abs
	 * @return the tuple factor new unit
	 */
	// fait tested
	private TupleFactorNewUnit bestFactorOtherPerTimeNoConstraint(String unitSource, double valueToMapAbs) {
		//
		String[] tabUnitSource = unitSource.split("/");
		String unit1 = tabUnitSource[0].trim(); // Other
		String unit2 = tabUnitSource[1].trim(); // Time
		double factor1 = 1;
		double factor2 = 1;
		double bestFactor = factor1 / factor2;
		String newUnitToReturn = unitSource;
		TupleFactorNewUnit tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
		String[] tabUnit1 = this.splitMult(unit1);
		int idx1 = this.lstMultUnits.indexOf(tabUnit1[0]);
		int idx2 = this.lstTimeUnits.indexOf(unit2);
		int i = 1, j = 1;
		String newUnit2 = tabUnitSource[1].trim();
		if (bestFactor * valueToMapAbs > valueMaxY) {
			boolean vrai = true;

			while (vrai && (idx1 + i) < lstMultUnits.size()) {
				String newMult1 = lstMultUnits.get(idx1 + i);
				factor1 = this.convertFactorSimpleUnit(unit1, newMult1 + tabUnit1[1]);
				if ((factor1 / factor2) * valueToMapAbs > valueMaxY) {
					// try with 2nd
					if ((idx2 - j) > 0) {
						newUnit2 = lstTimeUnits.get(idx2 - j);
						factor2 = this.convertFactorSimpleUnit(unit2, newUnit2);
						if ((factor1 / factor2) * valueToMapAbs > valueMaxY) {
							j++;
						} else {
							vrai = false;
							bestFactor = factor1 / factor2;
							newUnitToReturn = newMult1 + tabUnit1[1] + " / " + newUnit2;
							tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
						}
					}
					i++;
				} else {
					vrai = false;
					bestFactor = factor1 / factor2;
					newUnitToReturn = newMult1 + tabUnit1[1] + " / " + newUnit2;
					tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
				}

			}

		} else {
			// try to improve

			boolean vrai = true;

			while (vrai && (idx1 - i) >= 0) {
				String newMult1 = lstMultUnits.get(idx1 - i);
				
				factor1 = this.convertFactorSimpleUnit(unit1, newMult1 + tabUnit1[1]);
				
				if ((factor1 / factor2) * valueToMapAbs < valueMaxY) {
					// try with 2nd
					if ((idx2 + j) < lstTimeUnits.size()) {
						String tmpNewUnit2 = lstTimeUnits.get(idx2 + j);
						double tmpFactor2 = this.convertFactorSimpleUnit(unit2, tmpNewUnit2);
						if ((factor1 / tmpFactor2) * valueToMapAbs < valueMaxY) {
							j++;
							factor2=tmpFactor2;
							newUnit2=tmpNewUnit2;
							bestFactor = factor1 / factor2;
							newUnitToReturn = newMult1 + tabUnit1[1] + " / " + newUnit2;
							tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
						} else {
							vrai = false;

						}
					}
					bestFactor = factor1 / factor2;
					newUnitToReturn = newMult1 + tabUnit1[1] + " / " + newUnit2;
					tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
					i++;
				} else {
					vrai = false;

				}

			}

		}

		return tupleToReturn;
	}

	/**
	 * Best factor time per other with constraint.
	 *
	 * @param unitSource the unit source
	 * @param valueToMapAbs the value to map abs
	 * @param constraintRule the constraint rule
	 * @return the tuple factor new unit
	 */
	// fait tested
	private TupleFactorNewUnit bestFactorTimePerOtherWithConstraint(String unitSource, double valueToMapAbs,
			String constraintRule) {
		// Constraint can be ms / _ , _ / K, ms / K
		String[] tabUnitSource = unitSource.split("/");
		String unit1 = tabUnitSource[0].trim(); // Time
		String unit2 = tabUnitSource[1].trim(); // Other
		double factor1 = 1;
		double factor2 = 1;
		double bestFactor = factor1 / factor2;
		String newUnitToReturn = unitSource;
		TupleFactorNewUnit tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
		String[] tabUnit2 = this.splitMult(unit2);
		int idx2 = this.lstMultUnits.indexOf(tabUnit2[0]);
		int idx1 = this.lstTimeUnits.indexOf(unit1);
		String[] tabConstraint = constraintRule.split("/");
		int i = 1, j = 1;
		if (!constraintRule.contains(("_"))) {
			// just a conversion
			factor1 = this.convertFactorSimpleUnit(unit1, tabConstraint[0].trim());
			factor2 = this.convertFactorSimpleUnit(unit2, tabConstraint[1].trim() + tabUnit2[1]);
			bestFactor = factor1 / factor2;
			newUnitToReturn = tabConstraint[0].trim().replaceAll("\"", "") + " / " + tabConstraint[1].trim().replaceAll("\"", "") + tabUnit2[1];
			tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
		} else if (constraintRule.trim().startsWith("_")) {
			// 2nd Unit Other is fixed
			String newUnit2 = tabConstraint[1].trim() + tabUnit2[1];
			factor2 = this.convertFactorSimpleUnit(unit2, tabConstraint[1].trim() + tabUnit2[1]);
			
			// playing with first Unit
			boolean vrai = true;
			bestFactor = factor1 / factor2;
			newUnitToReturn = unit1 + " / " + tabConstraint[1].trim().replaceAll("\"", "") + tabUnit2[1];
			tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
			if ((factor1 / factor2) * valueToMapAbs > valueMaxY) {
				
				vrai=true;
				while (vrai && (idx1 + i) < this.lstTimeUnits.size()) {
					String newUnit1 = lstTimeUnits.get(idx1 + i);
					factor1 = this.convertFactorSimpleUnit(unit1, newUnit1);
					
					if ((factor1 / factor2) * valueToMapAbs > valueMaxY) {
						i++;
					} else {
						vrai = false;
						bestFactor = factor1 / factor2;
						newUnitToReturn = newUnit1 + " / " + tabConstraint[1].trim().replaceAll("\"", "") + tabUnit2[1];
						tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
					}
				}

			} else {
				// try to improve
				
				vrai=true;
				while (vrai && (idx1 - i) >= 0) {
					String newUnit1 = lstTimeUnits.get(idx1 - i);
					double tmpFactor1 = this.convertFactorSimpleUnit(unit1, newUnit1);
					if ((tmpFactor1 / factor2) * valueToMapAbs < valueMaxY) {
						factor1=tmpFactor1;
						i++;
						bestFactor = factor1 / factor2;
						newUnitToReturn = newUnit1 + " / " + tabConstraint[1].trim().replaceAll("\"", "") + tabUnit2[1];
						tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
					} else {
						vrai = false;

					}
				}

			}

		} else if (constraintRule.trim().endsWith("_")) {

			// 1nd Unit Time is fixed

			factor1 = this.convertFactorSimpleUnit(unit1, tabConstraint[0].trim());
			// playing with 2n Unit Other
			boolean vrai = true;
			bestFactor = factor1 / factor2;
			newUnitToReturn = unit1 + " / " + tabConstraint[1].trim().replaceAll("\"", "") + tabUnit2[1];
			tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
			if ((factor1 / factor2) * valueToMapAbs > valueMaxY) {
				while (vrai && (idx2 - j) > 0) {
					String newMult2 = lstMultUnits.get(idx2 - j);
					factor2 = this.convertFactorSimpleUnit(unit2, newMult2 + tabUnit2[1]);
					if ((factor1 / factor2) * valueToMapAbs > valueMaxY) {
						j++;
					} else {
						vrai = false;
						bestFactor = factor1 / factor2;
						newUnitToReturn = unit1 + " / " + tabConstraint[1].trim().replaceAll("\"", "") + tabUnit2[1];
						tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
					}
				}
			} else {
				// try to improve
				while (vrai && (idx2 + j) < this.lstMultUnits.size()) {
					String newMult2 = lstMultUnits.get(idx2 + j);
					double tmpFactor2 = this.convertFactorSimpleUnit(unit2, newMult2 + tabUnit2[1]);
					if ((factor1 / tmpFactor2) * valueToMapAbs < valueMaxY) {
						i++;
						factor2=tmpFactor2;
						bestFactor = factor1 / factor2;
						newUnitToReturn = unit1 + " / " + tabConstraint[1].trim().replaceAll("\"", "") + tabUnit2[1];
						tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
					} else {
						vrai = false;

					}
				}

			}
		}
		return tupleToReturn;
	}

	/**
	 * Best factor time per other no constraint.
	 *
	 * @param unitSource the unit source
	 * @param valueToMapAbs the value to map abs
	 * @return the tuple factor new unit
	 */
	// fait tested
	private TupleFactorNewUnit bestFactorTimePerOtherNoConstraint(String unitSource, double valueToMapAbs) {
		
		// unit => H / Km
		TupleFactorNewUnit toReturn = null;
		String[] tabUnitSource = unitSource.split("/");
		String unit1 = tabUnitSource[0].trim(); // Time
		String unit2 = tabUnitSource[1].trim(); // Other
		String[] tabUnit2=splitMult( unit2);
		double factor1 = 1;
		double factor2 = 1;
		double bestFactor = factor1 / factor2;
		String newUnitToReturn = unitSource;
		TupleFactorNewUnit tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
		int idx1=this.lstTimeUnits.indexOf(unit1);
		int idx2=this.lstMultUnits.indexOf(tabUnit2[0]);
		int i=1,j=1;
		if (bestFactor * valueToMapAbs > valueMaxY) {
			String tmpMult2 = tabUnit2[0];
			String tmpBaseUnit2 = tabUnit2[1];
			boolean vrai = true;
			while (vrai && (idx1 + i) < this.lstTimeUnits.size()) {
				String tmpUnit1 = lstTimeUnits.get(idx1 + i);
				factor1 = this.convertFactorSimpleUnit(unit1, tmpUnit1);
				if ((factor1 / factor2) * valueToMapAbs > valueMaxY) {
					// try with second unit
					if ((idx2 - j) >= 0) {
						tmpMult2 = this.lstMultUnits.get(idx2 - j);
						factor2 = this.convertFactorSimpleUnit(unit2, tmpMult2.replaceAll("\"", "") + tmpBaseUnit2);
						newUnitToReturn = tmpUnit1 + " / " + tmpMult2.replaceAll("\"", "") + tmpBaseUnit2;
						bestFactor = factor1 / factor2;
						tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
						if ((factor1 / factor2) * valueToMapAbs > valueMaxY) {

							j++;
						} else {
							vrai = false;

						}
					}
					i++;

				} else {
					vrai = false;
					newUnitToReturn = tmpUnit1 + " / " + tmpMult2.replaceAll("\"", "") + tmpBaseUnit2;
					bestFactor = factor1 / factor2;
					tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
				}
			}

		}
		else
		{
			//try to improve

			String tmpMult2 = tabUnit2[0];
			String tmpBaseUnit2 = tabUnit2[1];
			boolean vrai = true;
			while (vrai && (idx1 - i) >=0 ) {
				String tmpUnit1 = lstTimeUnits.get(idx1 - i);
				double tmpFactor1 = this.convertFactorSimpleUnit(unit1, tmpUnit1);
				
				if ((tmpFactor1 / factor2) * valueToMapAbs < valueMaxY) {
					factor1=tmpFactor1;
					newUnitToReturn = tmpUnit1 + " / " + tmpMult2.replaceAll("\"", "") + tmpBaseUnit2;
					bestFactor = factor1 / factor2;
					tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
					// try with second unit
					if ((idx2 + j) < this.lstMultUnits.size() ) {
						String tmpMult2Bis = this.lstMultUnits.get(idx2 + j);
						double tmpFactor2 = this.convertFactorSimpleUnit(unit2, tmpMult2Bis.replaceAll("\"", "") + tmpBaseUnit2);
						
						if ((factor1 / tmpFactor2) * valueToMapAbs < valueMaxY) {
							factor2=tmpFactor2;
							newUnitToReturn = tmpUnit1 + " / " + tmpMult2Bis.replaceAll("\"", "") + tmpBaseUnit2;
							bestFactor = factor1 / factor2;
							tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
							j++;
						} else {
							vrai = false;
						}
					}
					i++;

				} else {
					vrai = false;
					
				}
			}

		
		}
	
		return tupleToReturn;
	}

	/**
	 * Best factor composite mult unit duration.
	 *
	 * @param unitSource the unit source
	 * @param valueToMap the value to map
	 * @param constraintRule the constraint rule
	 * @param duration the duration
	 * @return the tuple factor new unit
	 */
	// fait and tested
	private TupleFactorNewUnit bestFactorCompositeMultUnitDuration(String unitSource, double valueToMap,
			String constraintRule, int duration) {
		// rule constraint are given by the following example pattern
		// possibilities :
		// duration * duration => null ; _ * ms ; ms * _; s * ms
		// duration * otherUnit => null; _ * K ; ms * K
		// otherUnit * duration => null; M * _ ; K * ms

		TupleFactorNewUnit toReturn = null;
		switch (duration) {
		case 0:
			// the second unit is not a duration
			if (null == constraintRule)
			{ //tested
				toReturn = bestFactorTimeMultOtherNoConstraint(unitSource, valueToMap);
			}
			else{
				// tested
				toReturn = bestFactorTimeMultOtherWithConstraint(unitSource, valueToMap, constraintRule);
			}

			break;
		case 1:
			// the first unit is not a duration and the second is a duration
			if (null == constraintRule)
			{
				//tested
				toReturn = bestFactorOtherMultTimeNoConstraint(unitSource, valueToMap);
			}
			else
			{
				//tested
				toReturn = bestFactorOtherMultTimeWithConstraint(unitSource, valueToMap, constraintRule);
			}

			break;

		case 2:
			// both are duration
			if (null == constraintRule)
			{
				//tested
				toReturn = bestFactorTimeMultTimeNoConstraint(unitSource, valueToMap);
			}
			else
			{
				//tested
				toReturn = bestFactorTimeMultTimeWithConstraint(unitSource, valueToMap, constraintRule);
			}
			break;
		}

		return toReturn;
	}

	/**
	 * Best factor time mult time with constraint.
	 *
	 * @param unitSource the unit source
	 * @param valueToMap the value to map
	 * @param constraintRule the constraint rule
	 * @return the tuple factor new unit
	 */
	// fait and tested
	private TupleFactorNewUnit bestFactorTimeMultTimeWithConstraint(String unitSource, double valueToMap,
			String constraintRule) {
		// Constraint treated _ * ms, ms * _
		double bestFactor = 1;
		String newUnitToReturn = "";
		String[] tabUnitSource = unitSource.split("\\*");
		String[] tabConstraint = constraintRule.split("\\*");
		if (tabConstraint[0].trim().equals("_") && (tabConstraint[1].trim().equals("_"))) {
			return bestFactorTimeMultTimeNoConstraint(unitSource, valueToMap);
		}
		else if (!constraintRule.contains("_")){
			//just a conversion
			double bestfactor= convertFactorComposite(unitSource,constraintRule);
			return new TupleFactorNewUnit(bestfactor,constraintRule);
		}
		if (constraintRule.trim().startsWith("_")) {
			// _ * ms
			// fix the factor to second parameter
			double factorMandatory = convertFactorSimpleUnit(tabUnitSource[1].trim(), tabConstraint[1].trim());
			double factor = factorMandatory;
		
			int idx = lstTimeUnits.indexOf(tabUnitSource[0].trim());
			boolean vrai = true;
			String firstUnit = tabUnitSource[0].trim();
			int i = 1;
			bestFactor = factor;
			newUnitToReturn = firstUnit + " * " + tabConstraint[1].trim().replaceAll("\"", "");
			
			if (factorMandatory * valueToMap > valueMaxY) {
				while (vrai && (idx + i) < lstTimeUnits.size()) {
					firstUnit = lstTimeUnits.get(idx + i);
					factor = factorMandatory * convertFactorSimpleUnit(tabUnitSource[0].trim(), firstUnit);
					if (factor * valueToMap > valueMaxY) {
						i++;
					} else {
						bestFactor = factor;
						newUnitToReturn = firstUnit + " * " + tabConstraint[1].trim();
						vrai = false;

					}
				}
			} else {
				// to Improve
					vrai=true;
					while (vrai && (idx - i) > 0) {
						firstUnit = lstTimeUnits.get(idx - i);
						double tmpfactor = factor * convertFactorSimpleUnit(tabUnitSource[0].trim(), firstUnit);
						if (tmpfactor * valueToMap < valueMaxY) {
							factor=tmpfactor;
							bestFactor = factor;
							newUnitToReturn = firstUnit + " * " + tabConstraint[1].trim().replaceAll("\"", "");
							i++;
						} else {

							vrai = false;

						}
					}
				
			}
		} else {

			// ms * _
			// fix the factor to second parameter
			double factorMandatory = convertFactorSimpleUnit(tabUnitSource[0].trim(), tabConstraint[0].trim());
			double factor = factorMandatory;
			int idx = lstTimeUnits.indexOf(tabUnitSource[1].trim());
			boolean vrai = true;
			String secondtUnit = tabUnitSource[1].trim();
			int i = 1;
			bestFactor = factor;
			newUnitToReturn = tabConstraint[0].trim() + " * " + secondtUnit;
			
			if (factorMandatory * valueToMap > valueMaxY) {
				while (vrai && (idx + i) < lstTimeUnits.size()) {
					secondtUnit = lstTimeUnits.get(idx + i);
					
					factor = factorMandatory * convertFactorSimpleUnit(tabUnitSource[1].trim(), secondtUnit);
					if (factor * valueToMap > valueMaxY) {
						i++;
					} else {
						bestFactor = factor;
						newUnitToReturn = tabConstraint[0].trim() + " * " + secondtUnit;
						vrai = false;

					}
				}
			} else {
				// to Improve
				
					while (vrai && (idx - i) > 0) {
						String tmpSecondtUnit = lstTimeUnits.get(idx - i);
						double tmpfactor = factorMandatory * convertFactorSimpleUnit(tabUnitSource[1].trim(), tmpSecondtUnit);
						if (tmpfactor * valueToMap < valueMaxY) {
							factor=tmpfactor;
							bestFactor = factor;
							newUnitToReturn = tabConstraint[0].trim()+ " * " + tmpSecondtUnit;
							i++;
						} else {

							vrai = false;

						}
					}
				
			}

		}

		return new TupleFactorNewUnit(bestFactor, newUnitToReturn);
	}

	/**
	 * Best factor time mult time no constraint.
	 *
	 * @param unitSource the unit source
	 * @param valueToMap the value to map
	 * @return the tuple factor new unit
	 */
	// fait and tested
	private TupleFactorNewUnit bestFactorTimeMultTimeNoConstraint(String unitSource, double valueToMap) {
		
		String[] tabUnitSource = unitSource.split("\\*");
		double bestFactor = 1;
		String newUnitToReturn = unitSource;
		int idx1 = this.lstTimeUnits.indexOf(tabUnitSource[0].trim());
		int idx2 = this.lstTimeUnits.indexOf(tabUnitSource[1].trim());
		boolean vrai = true;
		int i = 1, j = 1;
		String newUnit1 = tabUnitSource[0].trim();
		String newUnit2 = tabUnitSource[1].trim();
		double factor2 = 1;
		if (bestFactor * valueToMap > valueMaxY) {
			while (vrai && (idx1 + i) < lstTimeUnits.size()) {
				newUnit1 = lstTimeUnits.get(idx1 + i);
				double factor1 = convertFactorSimpleUnit(tabUnitSource[0].trim(), newUnit1);
				if (factor1 * factor2 * valueToMap > valueMaxY) {
					
					// we try by modifying second unit
					if ((idx2 + j) < lstTimeUnits.size()) {
						newUnit2 = lstTimeUnits.get(idx2 + j);
						factor2 = convertFactorSimpleUnit(tabUnitSource[1].trim(), newUnit2);
						if (factor1 * factor2 * valueToMap > valueMaxY) {

							j++;
						} else {
							bestFactor=factor1 * factor2;
							newUnitToReturn = newUnit1 + " * " + newUnit2;
							vrai = false;
						}
					}
					i++;
				} else {
					bestFactor = factor1 * factor2;
					newUnitToReturn = newUnit1 + " * " + newUnit2;
					vrai = false;

				}
			}

		} else {
			// to improve
			vrai=true;
			while (vrai && (idx1 - i) >= 0) {
				String newUnit1Bis = lstTimeUnits.get(idx1 - i);
				double factor1 = convertFactorSimpleUnit(tabUnitSource[0].trim(), newUnit1Bis);
				if (factor1 * factor2 * valueToMap < valueMaxY) {
					bestFactor = factor1 * factor2;
					newUnitToReturn = newUnit1Bis + " * " + newUnit2;
					// we try by modifying second unit
					if ((idx2 - j) >= 0) {
						String newUnit2Bis = lstTimeUnits.get(idx2 - j);
						double tmpfactor2 = convertFactorSimpleUnit(tabUnitSource[1].trim(), newUnit2Bis);
						if (factor1 * tmpfactor2 * valueToMap < valueMaxY) {
							factor2=tmpfactor2;
							bestFactor = factor1 * factor2;
							newUnitToReturn = newUnit1Bis + " * " + newUnit2Bis;
							j++;
						} else {
							vrai = false;
						}
					}
					i++;
				} else {
					vrai = false;
				}
			}

		}

		return new TupleFactorNewUnit(bestFactor, newUnitToReturn);
	}

	/**
	 * Best factor other mult time with constraint.
	 *
	 * @param unitSource the unit source
	 * @param valueToMap the value to map
	 * @param constraintRule the constraint rule
	 * @return the tuple factor new unit
	 */
	// fait and tested
	private TupleFactorNewUnit bestFactorOtherMultTimeWithConstraint(String unitSource, double valueToMap,
			String constraintRule) {
		String[] tabUnitSource = unitSource.split("\\*");
		String revUnitSource = tabUnitSource[1].trim() + " * " + tabUnitSource[0];
		String[] tabConstraintRule=constraintRule.split("\\*");
		String revConstaintRule=tabConstraintRule[1].trim()+" * "+tabConstraintRule[0];
		TupleFactorNewUnit tuple = bestFactorTimeMultOtherWithConstraint(revUnitSource, valueToMap, revConstaintRule);
		String[] tabTargetUnit = tuple.newUnit.split("\\*");
		return new TupleFactorNewUnit(tuple.factor, tabTargetUnit[1].trim() + " * " + tabTargetUnit[0].trim());

	}

	/**
	 * Best factor other mult time no constraint.
	 *
	 * @param unitSource the unit source
	 * @param valueToMap the value to map
	 * @return the tuple factor new unit
	 */
	// fait and tested
	private TupleFactorNewUnit bestFactorOtherMultTimeNoConstraint(String unitSource, double valueToMap) {
		String[] tabUnitSource = unitSource.split("\\*");
		String revUnitSource = tabUnitSource[1].trim() + " * " + tabUnitSource[0];
		TupleFactorNewUnit tuple = bestFactorTimeMultOtherNoConstraint(revUnitSource, valueToMap);
		String[] tabTargetUnit = tuple.newUnit.split("\\*");
		return new TupleFactorNewUnit(tuple.factor, tabTargetUnit[1].trim() + " * " + tabTargetUnit[0].trim());

	}

	/**
	 * Best factor time mult other with constraint.
	 *
	 * @param unitSource the unit source
	 * @param valueToMap the value to map
	 * @param constraintRule the constraint rule
	 * @return the tuple factor new unit
	 */
	// fait and tested
	private TupleFactorNewUnit bestFactorTimeMultOtherWithConstraint(String unitSource, double valueToMap,
			String constraintRule) {
		// Rule treated are type like :
		// ms * K, _ * K , ms * _
		double bestFactor = 1;
		String newUnitToReturn = "";
		String[] tabUnit = unitSource.split("\\*");
		String pref1 = "\"\"";
		String unitSource1 = tabUnit[1].trim();
		String[] tabSplitUnitSource1 = this.splitMult(unitSource1);
		String unitPartSource1 =tabSplitUnitSource1[1].trim();
		String unit = unitPartSource1;
		pref1 = tabSplitUnitSource1[0];

		
		String newUnit2 = unitSource1;
		if (!constraintRule.contains("_")) {
			// just a convertion
			// construct the second unit
			//tested 
			if (constraintRule.split("\\*")[1].trim().equals("\"\"")){
				newUnit2 = unitPartSource1;
			}else
			{
				newUnit2 =constraintRule.split("\\*")[1].trim()+unitPartSource1;
			}
			
			bestFactor = convertFactorComposite(unitSource,constraintRule.split("\\*")[0].trim()+" * " +newUnit2);
			return new TupleFactorNewUnit(bestFactor, constraintRule.split("\\*")[0].trim()+" * " +newUnit2);
		}
		
		else if (constraintRule.startsWith("_")) {
			// first is time
			String newMult = constraintRule.split("\\*")[1].trim();

			// first fix the factor with the rule of 2nd Unit
			String newUnit = unit;
			if (!newMult.equals("\"\"")) {
				newUnit = newMult + unit;
			}
			
			double factorMandatory = convertFactorSimpleUnit(unitSource1, newUnit);
			double factor = factorMandatory;
			
			if (factorMandatory * valueToMap > valueMaxY) {
				boolean vrai = true;
				int i = 1;

				int idx = lstTimeUnits.indexOf(tabUnit[0].trim());
				// We play only with first unit
				while (vrai && (idx + i) < lstTimeUnits.size()) {

					String newUnit1 = lstTimeUnits.get(idx + i);
					factor = factorMandatory * convertFactorSimpleUnit(tabUnit[0].trim(), newUnit1);
					if (factor * valueToMap > valueMaxY) {

						i++;
					} else {
						vrai = false;
						bestFactor = factor;
						newUnitToReturn = newUnit1 + " * " + newUnit;
					}
				}
			} else {
				// to try to improve
				boolean vrai = true;
				int i = 1;

				int idx = lstTimeUnits.indexOf(tabUnit[0].trim());

				while (vrai && (idx - i) >= 0) {
					String newUnit1 = lstTimeUnits.get(idx - i);
					double tmpfactor = factorMandatory * convertFactorSimpleUnit(tabUnit[0].trim(), newUnit1);
					if (tmpfactor * valueToMap < valueMaxY) {
						i++;
						factor=tmpfactor;
						bestFactor = factor;
						newUnitToReturn = newUnit1 + " * " + newUnit;
					} else {
						vrai = false;
						
					}

				}
			}
		} else {

			//First is time and fixed constraints like ms * _
			String newMult = constraintRule.split("\\*")[1].trim();
			String newUnit=constraintRule.split("\\*")[0].trim();
			// first fix the factor with the rule of 1rst Unit
			
			double factorMandatory = convertFactorSimpleUnit(tabUnit[0].trim(), newUnit);
			double factor = factorMandatory;
			if (factorMandatory * valueToMap > valueMaxY) {
				boolean vrai = true;
				int i = 1;

				int idx = this.lstMultUnits.indexOf(pref1);
				// We play only with second unit
				while (vrai && (idx + i) < lstMultUnits.size()) {

					String newMult2 = lstMultUnits.get(idx + i);
					newUnit2=unitPartSource1;
					if(!newMult2.equals("\"\"")){
						newUnit2=newMult2+newUnit2;
					}
					
					factor = factorMandatory * convertFactorSimpleUnit(tabUnit[1].trim(), newUnit2);
					if (factor * valueToMap > valueMaxY) {

						i++;
					} else {
						vrai = false;
						bestFactor = factor;
						newUnitToReturn = newUnit + " * " + newUnit2;
					}
				}
			} else {
				// to try to improve
				boolean vrai = true;
				int i = 1;

				int idx = this.lstMultUnits.indexOf(pref1);
				// We play only with second unit

				while (vrai && (idx - i) >= 0) {
					String newMult2 = lstMultUnits.get(idx - i);
					newUnit2=unitPartSource1;
					if(!newMult2.equals("\"\"")){
						newUnit2=newMult2+newUnit2;
					}
					
					double tmpfactor = factorMandatory * convertFactorSimpleUnit(tabUnit[1].trim(), newUnit2);
					if (tmpfactor * valueToMap < valueMaxY) {
						factor=tmpfactor;
						i++;
						bestFactor = factor;
						newUnitToReturn = newUnit + " * " + newUnit2;
					} else {
						vrai = false;
						
					}

				}
			}

		}

		return new TupleFactorNewUnit(bestFactor, newUnitToReturn);
	}

	/**
	 * Best factor time mult other no constraint.
	 *
	 * @param unitSource the unit source
	 * @param valueToMap the value to map
	 * @return the tuple factor new unit
	 */
	// fait tested
	private TupleFactorNewUnit bestFactorTimeMultOtherNoConstraint(String unitSource, double valueToMap) {
		// modele du genre s * m
		double bestFactor = 1;
		String newUnitToReturn = "";
		String[] tabUnit = unitSource.split("\\*");
		String pref1 = "\"\"";
		String unitSource1 = tabUnit[1].trim();
		
		
		String[] tabUnit1Split=splitMult(tabUnit[1]);
		String unit = tabUnit1Split[1];;
		pref1=tabUnit1Split[0];
		
		
		String newUnit2 = unitSource1;

		if (valueToMap > valueMaxY) {
			boolean vrai = true;
			int i = 1;
			int j = 1;
			int idx = lstTimeUnits.indexOf(tabUnit[0].trim());
			int idxj = lstMultUnits.indexOf(pref1);

			while (vrai && (idx + i) < lstTimeUnits.size()) {
				
				String newUnit1 = lstTimeUnits.get(idx + i);
								double factor = convertFactorSimpleUnit(tabUnit[0].trim(), newUnit1);
				
				if (factor * valueToMap > valueMaxY) {
					
					if ((idxj - j) >= 0) {
						String newPref = lstMultUnits.get(idxj - j);
						double tmpFactor = factor
								* Double.parseDouble(
										logFouineurProperties.getProperty("logFouineur.unit.mult." + pref1))
								/ Double.parseDouble(
										logFouineurProperties.getProperty("logFouineur.unit.mult." + newPref));
						if (tmpFactor * valueToMap > valueMaxY) {
							factor = tmpFactor;
							newUnit2 = newPref + unit;
							j++;
							bestFactor = factor;
							newUnitToReturn = newUnit1 + " * " + newUnit2;
						} else {
							vrai = false; // on sort
							
						}
					}
					i++;
				} else {
					vrai = false;
					bestFactor = factor;
					newUnitToReturn = newUnit1 + " * " + newUnit2;
				}
			}
		} else {
			// to try to improve
			
			boolean vrai = true;
			int i = 1;
			int j = 1;
			int idx = lstTimeUnits.indexOf(tabUnit[0].trim());
			
			int idxj = lstMultUnits.indexOf(pref1);

			while (vrai && (idx - i) >= 0) {
				String newUnit1 = lstTimeUnits.get(idx - i); 
				double factor = convertFactorSimpleUnit(tabUnit[0].trim(), newUnit1);
				
				if (factor * valueToMap < valueMaxY) {
					bestFactor = factor;
					newUnitToReturn = newUnit1 + " * " + newUnit2;
					if ((idxj + j) < lstMultUnits.size()) {
						String newPref = lstMultUnits.get(idxj + j);
						double tmpFactor = factor
								* Double.parseDouble(
										logFouineurProperties.getProperty("logFouineur.unit.mult." + pref1))
								/ Double.parseDouble(
										logFouineurProperties.getProperty("logFouineur.unit.mult." + newPref));
						if (tmpFactor * valueToMap < valueMaxY) {
							factor = tmpFactor;
							newUnit2 = newPref + unit;
							bestFactor = factor;
							newUnitToReturn = newUnit1 + " * " + newUnit2;
							j++;
						} else {
							vrai = false; // on sort
							
						}
					}
					i++;
				} else {
					vrai = false;
					bestFactor = factor;
					newUnitToReturn = newUnit1 + " * " + newUnit2;
				}

			}
		}
		return new TupleFactorNewUnit(bestFactor, newUnitToReturn);
	}

	/**
	 * Best factor composite others mult others.
	 *
	 * @param unitSource the unit source
	 * @param valueToMap the value to map
	 * @param contraintRule the contraint rule
	 * @return the tuple factor new unit
	 */
	// fait and tested
	private TupleFactorNewUnit bestFactorCompositeOthersMultOthers(String unitSource, double valueToMap,
			String contraintRule) {
		// case of constraints Rule null, _ * K, K * _ , K * M
		TupleFactorNewUnit tupleToReturn = null;
		if (null == contraintRule) {
			//tested
			tupleToReturn = bestFactorCompositeOthersMultOthersNoConstraint(unitSource, valueToMap);
		} else if (contraintRule.trim().startsWith("_")) {
			// tested
			tupleToReturn = bestFactorCompositeOthersMultOthersConstraint_fois(unitSource, valueToMap, contraintRule);
		} else if (contraintRule.trim().endsWith("_")) {
			// tested
			tupleToReturn = bestFactorCompositeOthersMultOthersConstraintFois_(unitSource, valueToMap, contraintRule);
		} else {
			// only a conversion K * M
			//tested
			String[] tabUnitSource = unitSource.split("\\*");
			String[] tabSplitSource1=this.splitMult(tabUnitSource[0]);
			String[] tabSplitSource2=this.splitMult(tabUnitSource[1]);
			String[] tabMultUnit = contraintRule.split("\\*");
			
			String unitToReturn= tabMultUnit[0].trim().replaceAll("\"", "")+tabSplitSource1[1] + " * "
					+tabMultUnit[1].trim().replaceAll("\"", "")+tabSplitSource2[1];
			
			double factor = convertFactorComposite(unitSource,	unitToReturn);
			tupleToReturn = new TupleFactorNewUnit(factor,unitToReturn);
		}
		return tupleToReturn;
	}

	/**
	 * Best factor composite others mult others constraint fois.
	 *
	 * @param unitSource the unit source
	 * @param valueToMap the value to map
	 * @param constrainstRule the constrainst rule
	 * @return the tuple factor new unit
	 */
	// fait and tested
	private TupleFactorNewUnit bestFactorCompositeOthersMultOthersConstraintFois_(String unitSource, double valueToMap,
			String constrainstRule) {
		// Rule is K * _
		double bestFactor = 1, factor1 = 1, factor2 = 1;
		String newUnitToReturn = "";
		TupleFactorNewUnit tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
		String[] tabUnit = unitSource.split("\\*");
		String pref1 = "\"\"";
		String unitSource1 = tabUnit[0].trim();
		String[] tabSplitSource1=this.splitMult(unitSource1);
		
		String unitPartSource1 = tabSplitSource1[1];
		String unit = unitPartSource1;
		pref1 = tabSplitSource1[0];
		String unitSource2 = tabUnit[1].trim();
		String[] tabSplitSource2=this.splitMult(unitSource2);
		String pref2 = tabSplitSource2[0];
		
		String unitPartSource2 =  tabSplitSource2[1];
		// the first Unit is constraint
		String[] tabConstraint = constrainstRule.split("\\*");
		factor1 = this.convertFactorSimpleUnit(tabUnit[0].trim(), tabConstraint[0].trim().replaceAll("\"", "") + unitPartSource1);
		
		bestFactor = factor1 * factor2;
		newUnitToReturn = tabConstraint[0].trim().replaceAll("\"", "") + unitPartSource1 + " * " + tabUnit[1].trim();
		tupleToReturn = new TupleFactorNewUnit(bestFactor,newUnitToReturn);
		// playing with 2nd Unit
		int idx2 = this.lstMultUnits.indexOf(pref2);
		int j = 1;
		if (factor1 * factor2 * valueToMap > valueMaxY) {
			boolean vrai = true;
			while (vrai && (idx2 + j) < lstMultUnits.size()) {
				String newMult2 = lstMultUnits.get(idx2 + j);
				factor2 = this.convertFactorSimpleUnit(unitSource2, newMult2.replaceAll("\"", "") + unitPartSource2);
				if (factor1 * factor2 * valueToMap > valueMaxY) {
					j++;
				} else {
					vrai = false;
					bestFactor = factor1 * factor2;
					newUnitToReturn = tabConstraint[0].trim().replaceAll("\"", "") + unitPartSource1 + " * " + newMult2 + unitPartSource2;
					tupleToReturn = new TupleFactorNewUnit(bestFactor,newUnitToReturn);
				}

			}
		} else {
			// try to improve with 2nd value
			
			boolean vrai = true;
			while (vrai && (idx2 - j) >= 0) {
				String newMult2 = lstMultUnits.get(idx2 - j);
				double tmpfactor2 = this.convertFactorSimpleUnit(unitSource2, newMult2.replaceAll("\"", "") + unitPartSource2);
				if (factor1 * tmpfactor2 * valueToMap < valueMaxY) {
					factor2=tmpfactor2;
					bestFactor = factor1 * factor2;
					newUnitToReturn =tabConstraint[0].trim().replaceAll("\"","") + unitPartSource1 + " * " + newMult2.replaceAll("\"", "") + unitPartSource2;
					tupleToReturn = new TupleFactorNewUnit(bestFactor,newUnitToReturn);
					j++;
				} else {
					vrai = false;
				}
			}
		}
		return tupleToReturn;
	}

	/**
	 * Best factor composite others mult others constraint fois.
	 *
	 * @param unitSource the unit source
	 * @param valueToMap the value to map
	 * @param constraintRule the constraint rule
	 * @return the tuple factor new unit
	 */
	// fait and tested
	private TupleFactorNewUnit bestFactorCompositeOthersMultOthersConstraint_fois(String unitSource, double valueToMap,
			String constraintRule) {

		// Rule is _ * K
		double bestFactor = 1, factor1 = 1, factor2 = 1;
		String newUnitToReturn = "";
		TupleFactorNewUnit tupleToReturn = new TupleFactorNewUnit(bestFactor, newUnitToReturn);
		String[] tabUnit = unitSource.split("\\*");
		String pref1 = "\"\"";
		String unitSource1 = tabUnit[0].trim();
		String[] tabSplitSource1=this.splitMult(unitSource1);
		pref1=tabSplitSource1[0];
		String unitPartSource1 = tabSplitSource1[1];
		String unit = unitPartSource1;
		if (!unitSource1.equals(unitPartSource1)) {
			pref1 = unitSource1.substring(0,unitSource1.lastIndexOf(unitPartSource1));

		}
		String unitSource2 = tabUnit[1].trim();
		String pref2 = "\"\"";
		
		String[] tabSplitSource2=this.splitMult(unitSource2);
		pref2=tabSplitSource2[0];
		String unitPartSource2 = tabSplitSource2[1];
		

		// the Second Unit is constraint
		String[] tabConstraint = constraintRule.split("\\*");
		factor2 = this.convertFactorSimpleUnit(tabUnit[1].trim(), tabConstraint[1].trim().replaceAll("\"", "") + unitPartSource2);

		// playing with first Unit
		int idx1 = this.lstMultUnits.indexOf(pref1);
		int i = 1;
		if (factor1 * factor2 * valueToMap > valueMaxY) {
			boolean vrai = true;
			while (vrai && (idx1 + i) < lstMultUnits.size()) {
				String newMult1 = lstMultUnits.get(idx1 + i);
				factor1 = this.convertFactorSimpleUnit(unitSource1, newMult1.replaceAll("\"","") + unitPartSource1);
				if (factor1 * factor2 * valueToMap > valueMaxY) {
					i++;
				} else {
					vrai = false;
					bestFactor = factor1 * factor2;
					tupleToReturn = new TupleFactorNewUnit(bestFactor,
							newMult1.replaceAll("\"","")  + unitPartSource1 + " * " + tabConstraint[1].trim().replaceAll("\"", "") + unitPartSource2);
				}
			}
		} else {
			// try to improve with 2nd value
			boolean vrai = true;
			while (vrai && (idx1 - i) >= 0) {
				String newMult1 = lstMultUnits.get(idx1 - i);
				double tmpfactor1 = this.convertFactorSimpleUnit(unitSource1, newMult1.replaceAll("\"","")  + unitPartSource1);
				if (tmpfactor1 * factor2 * valueToMap < valueMaxY) {
					factor1=tmpfactor1;
					bestFactor = factor1 * factor2;
					tupleToReturn = new TupleFactorNewUnit(bestFactor,
							newMult1.replaceAll("\"","")  + unitPartSource1 + " * " + tabConstraint[1].trim().replaceAll("\"", "") + unitPartSource2);
					i++;
				} else {
					vrai = false;
				}
			}
		}
		return tupleToReturn;

	}

	/**
	 * Best factor composite others mult others no constraint.
	 *
	 * @param unitSource the unit source
	 * @param valueToMap the value to map
	 * @return the tuple factor new unit
	 */
	// fait and tested
	private TupleFactorNewUnit bestFactorCompositeOthersMultOthersNoConstraint(String unitSource, double valueToMap) {
		// no constraints
		double factor1 = 1;
		double factor2 = 1;
		double bestFactor = 1;
		String unitToReturn = unitSource;
		TupleFactorNewUnit tupleToReturn = new TupleFactorNewUnit(bestFactor, unitToReturn);
		String[] tabUnitSource = unitSource.split("\\*");
		// Extract the multiplicator of a unit
		String[] tabUnit1Source = splitMult(tabUnitSource[0].trim());
		String[] tabUnit2Source = splitMult(tabUnitSource[1].trim());
		String mult1 = tabUnit1Source[0];
		String mult2 = tabUnit2Source[0];
		int idx1 = lstMultUnits.indexOf(mult1);
		int idx2 = lstMultUnits.indexOf(mult2);
		int i = 1, j = 1;
		if (factor1 * factor2 * valueToMap > valueMaxY) {
			
			boolean vrai = true;
			while (vrai && (idx1 + i) < lstMultUnits.size()) {
				
				String tmpMult1 = lstMultUnits.get(idx1 + i);
				
				factor1 = Double.parseDouble(logFouineurProperties.getProperty("logFouineur.unit.mult." + tmpMult1))
						/ Double.parseDouble(logFouineurProperties.getProperty("logFouineur.unit.mult." + mult1));
				
				if (factor1 * factor2 * valueToMap > valueMaxY) {
					mult1 = tmpMult1;
					// We try with second unit
					if ((idx2 + j) < lstMultUnits.size()) {
						String tmpMult2 = lstMultUnits.get(idx2 + j);
						factor2 = Double
								.parseDouble(logFouineurProperties.getProperty("logFouineur.unit.mult." + tmpMult2))
								/ Double.parseDouble(
										logFouineurProperties.getProperty("logFouineur.unit.mult." + mult2));
						if (factor1 * factor2 * valueToMap > valueMaxY) {
							mult2 = tmpMult2;
							j++;

						} else {
							vrai = false;
							mult2 = tmpMult2;
							bestFactor = factor1 * factor2;
							unitToReturn = mult1.replaceAll("\"","") + tabUnit1Source[1] + " * " + mult2.replaceAll("\"","") + tabUnit2Source[1];
							tupleToReturn = new TupleFactorNewUnit(bestFactor, unitToReturn);
						}
					}
					i++;

				} else {
					vrai = false;
					mult1=tmpMult1;
					bestFactor = factor1 * factor2;
					unitToReturn = mult1.replaceAll("\"","") + tabUnit1Source[1] + " * " + mult2.replaceAll("\"","") + tabUnit2Source[1];
					tupleToReturn = new TupleFactorNewUnit(bestFactor, unitToReturn);
				}

			}

		} else {
			// try to improve

			boolean vrai = true;
			while (vrai && (idx1 - i) >= 0) {
				String tmpMult1 = lstMultUnits.get(idx1 - i);
				double tmpfactor1 = Double.parseDouble(logFouineurProperties.getProperty("logFouineur.unit.mult." + tmpMult1))
						/ Double.parseDouble(logFouineurProperties.getProperty("logFouineur.unit.mult." + mult1));
				if (tmpfactor1 * factor2 * valueToMap < valueMaxY) {
					factor1=tmpfactor1;
					mult1 = tmpMult1;
					bestFactor = factor1 * factor2;
					unitToReturn = mult1.replaceAll("\"","") + tabUnit1Source[1] + " * " + mult2.replaceAll("\"","") + tabUnit2Source[1];
					tupleToReturn = new TupleFactorNewUnit(bestFactor, unitToReturn);
					// We try with second unit
					if ((idx2 - j) >= 0) {
						String tmpMult2 = lstMultUnits.get(idx2 - j);
						double tmpfactor2 = Double
								.parseDouble(logFouineurProperties.getProperty("logFouineur.unit.mult." + tmpMult2))
								/ Double.parseDouble(
										logFouineurProperties.getProperty("logFouineur.unit.mult." + mult2));
						if (factor1 * tmpfactor2 * valueToMap < valueMaxY) {
							mult2 = tmpMult2;
							factor2=tmpfactor2;
							j++;
							bestFactor = factor1 * factor2;
							unitToReturn = mult1.replaceAll("\"","") + tabUnit1Source[1] + " * " + mult2.replaceAll("\"","") + tabUnit2Source[1];
							tupleToReturn = new TupleFactorNewUnit(bestFactor, unitToReturn);

						} else {
							vrai = false;
							
						}
					}
					i++;

				} else {
					vrai = false;
					bestFactor = factor1 * factor2;
					unitToReturn = mult1 + tabUnit1Source[1] + " * " + mult2 + tabUnit2Source[1];
					tupleToReturn = new TupleFactorNewUnit(bestFactor, unitToReturn);
				}

			}

		}
		return tupleToReturn;
	}

}
