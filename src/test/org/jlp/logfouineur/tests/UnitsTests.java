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
package org.jlp.logfouineur.tests;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;


import org.jlp.logfouineur.csvviewer.csvutils.TupleFactorNewUnit;
import org.jlp.logfouineur.csvviewer.csvutils.Units;
import org.jlp.logfouineur.ui.LogFouineurMain;
import org.jlp.logfouineur.util.Utils;

// TODO: Auto-generated Javadoc
/**
 * The Class UnitsTests.
 */
public class UnitsTests {
	
	/** The workspace. */
	public static String workspace = null;
	
	/** The project. */
	public static String project = null;
	
	/** The root. */
	public static String root = null;
	
	/** The log fouineur properties. */
	public static Properties logFouineurProperties;
	
	/** The osname. */
	String osname = System.getProperty("os.name");
	static{
		workspace = System.getProperty("workspace");
		project = "projet0";
		root = System.getProperty("root");
		System.out.println("workspace="+workspace);
		System.out.println("root="+root);
		String strPath = root + File.separator + "config" + File.separator + "logFouineur.properties";
		logFouineurProperties = new Properties();
		try {
			logFouineurProperties.load(Files.newBufferedReader(new File(strPath).toPath()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Sets the up before class.
	 *
	 * @throws Exception the exception
	 */
	@BeforeAll
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@BeforeAll
	public void setUp() throws Exception {
		

	}

	/**
	 * Tear down.
	 *
	 * @throws Exception the exception
	 */
	@AfterAll
	public void tearDown() throws Exception {
	}

	
	/**
	 * Convert factor simple unit time.
	 */
	@Test
	public void convertFactorSimpleUnitTime(){
		
		double msTos=new Units().convertFactorSimpleUnit("ms", "s");
		double sToms=new Units().convertFactorSimpleUnit("s", "ms");
		double nanosToms=new Units().convertFactorSimpleUnit("nanos", "ms");
		double msTonanos=new Units().convertFactorSimpleUnit("ms", "nanos");
		double[] expected={0.001,1000,0.000001,1000000};
		double[] retrived={msTos,sToms,nanosToms,msTonanos};
		assertArrayEquals(expected,retrived, 0);
	}
	
	/**
	 * Convert factor simple unit other.
	 */
	@Test
	public void convertFactorSimpleUnitOther(){
		double mToKm=new Units().convertFactorSimpleUnit("m", "Km");
		double[] expected={0.001};
		double[] retrived={mToKm};
		assertArrayEquals(expected,retrived, 0);
		
	}
	
	/**
	 * Convert factor simple unit.
	 */
	@Test 
	public void convertFactorSimpleUnit()
	{
		double msTos=new Units().convertFactorSimpleUnit("ms", "s");
		double sToms=new Units().convertFactorSimpleUnit("s", "ms");
		double nanosToms=new Units().convertFactorSimpleUnit("nanos", "ms");
		double msTonanos=new Units().convertFactorSimpleUnit("ms", "nanos");
		double mToKm=new Units().convertFactorSimpleUnit("m", "Km");
		double KmTom=new Units().convertFactorSimpleUnit("Km", "m");
		

		double[] expected={0.001,1000,0.000001,1000000,0.001,1000};
		double[] retrived={msTos,sToms,nanosToms,msTonanos,mToKm,KmTom};
		assertArrayEquals(expected,retrived, 0);
	}
	
	/**
	 * Convert factor composite.
	 */
	@Test 
	public void 	convertFactorComposite(){
		double mMultKmTomMultm=new Units().convertFactorComposite("m * Km", "m * m");
		double mPerKmTomPertm=new Units().convertFactorComposite("m / Km", "m / m");
		double GmPerKmToPertm=new Units().convertFactorComposite("Gm / Km", "m / m");
		double mMultsToKmMultH=new Units().convertFactorComposite("m* s", "Km *  H");
		double mPersToKmPerH=new Units().convertFactorComposite("m /s", "Km /  H");
		
		double[] expected={1000,0.001,1000000,0.0000002777,3.6};
		double[] retrived={mMultKmTomMultm,mPerKmTomPertm,GmPerKmToPertm,mMultsToKmMultH,mPersToKmPerH};
		assertArrayEquals(expected,retrived, 0.0000001);
	}
	
	/**
	 * Best factor simple unit duration.
	 */
	@Test 
	public void 	bestFactorSimpleUnitDuration(){
		TupleFactorNewUnit msTos=new Units().bestFactor("ms", 100000, null);
		TupleFactorNewUnit sTomn=new Units().bestFactor("s", 350000, null);
		TupleFactorNewUnit sToH=new Units().bestFactor("s", 720000, null);
		TupleFactorNewUnit sToms=new Units().bestFactor("s", 2, null);
		TupleFactorNewUnit sToHC=new Units().bestFactor("s", 7200, "H");;
		
		double[] expected={0.001,1d/60d,1d/3600d,1000,1d/3600d};
		double[] retrieved={msTos.factor,sTomn.factor,sToH.factor,sToms.factor,sToHC.factor};
		
		String[] newUnitRetrieved={msTos.newUnit,sTomn.newUnit,sToH.newUnit,sToms.newUnit,sToHC.newUnit};
		String[] newUnitExpected={"s","mn","H","ms","H"};
		
		assertArrayEquals(expected,retrieved, 0.0000001);
		assertArrayEquals(newUnitExpected,newUnitRetrieved);
	}
	
	/**
	 * Best factor simple unit others.
	 */
	@Test 
	public void 	bestFactorSimpleUnitOthers(){
		TupleFactorNewUnit mToKm=new Units().bestFactor("m", 100000, null);
		TupleFactorNewUnit KmTom=new Units().bestFactor("Km", 9, null);
		TupleFactorNewUnit millimTom=new Units().bestFactor("millim", 9000, "\"\"");
		TupleFactorNewUnit KmToMm=new Units().bestFactor("Km", 99000, null);
		
		double[] expected={0.001,1000,0.001,0.001};
		double[] retrieved={mToKm.factor,KmTom.factor,millimTom.factor,KmToMm.factor};
		
		String[] newUnitRetrieved={mToKm.newUnit,KmTom.newUnit,millimTom.newUnit,KmToMm.newUnit};
		String[] newUnitExpected={"Km","m","m","Mm"};
		
		assertArrayEquals(expected,retrieved, 0.0000001);
		assertArrayEquals(newUnitExpected,newUnitRetrieved);
		
	}
		
	
	/**
	 * Best factor composite others per others.
	 */
	@Test 
	public void 	bestFactorCompositeOthersPerOthers()
	{
		TupleFactorNewUnit mPerKmToKmPerKm=new Units().bestFactor("m / Km", 100000, null);
		TupleFactorNewUnit KmPermToMmPerm=new Units().bestFactor("Km / m", 100000, null);
		TupleFactorNewUnit mPerKmToKmPerKmBis=new Units().bestFactor("m / Km", 1, null);
		TupleFactorNewUnit millimPermToMmPermBis=new Units().bestFactor("millim / m", 1000900, "_ / K");
		TupleFactorNewUnit millimPermToMmPermTer=new Units().bestFactor("millim / m", 1000900, "\"\" / _");
		
		double[] expected={0.001,0.001,1000,0.001,0.001};
		double[] retrieved={mPerKmToKmPerKm.factor,KmPermToMmPerm.factor,mPerKmToKmPerKmBis.factor,millimPermToMmPermBis.factor,millimPermToMmPermTer.factor};
		
		String[] newUnitRetrieved={mPerKmToKmPerKm.newUnit,KmPermToMmPerm.newUnit,mPerKmToKmPerKmBis.newUnit,millimPermToMmPermBis.newUnit,millimPermToMmPermTer.newUnit};
		String[] newUnitExpected={"Km / Km","Mm / m","millim / Km","Km / Km","m / m"};
		
		assertArrayEquals(expected,retrieved, 0.0000001);
		assertArrayEquals(newUnitExpected,newUnitRetrieved);
		
	}
	
	/**
	 * Best factor composite per unit duration.
	 */
	@Test 
	public void bestFactorCompositePerUnitDuration(){
		
		TupleFactorNewUnit mnperHTosPers=new Units().bestFactor("mn / H", 10, "s / s");
		TupleFactorNewUnit mnperHTomnPers=new Units().bestFactor("mn / H", 100000, "_ / s");
		TupleFactorNewUnit mnperHToHPerHNoC=new Units().bestFactor("mn / H", 100000,null);
		TupleFactorNewUnit mnpermToHPermNoC=new Units().bestFactor("mn / m", 100001,null);
		TupleFactorNewUnit mnpermTosPermNoC=new Units().bestFactor("mn / m", 10,null);
		
		double[] expected={60d/3600d ,60d/3600d,1d/60d,1d/60d,60d};
		double[] retrieved={mnperHTosPers.factor,mnperHTomnPers.factor,mnperHToHPerHNoC.factor,
				mnpermToHPermNoC.factor,mnpermTosPermNoC.factor};
		
		String[] newUnitRetrieved={mnperHTosPers.newUnit,mnperHTomnPers.newUnit,mnperHToHPerHNoC.newUnit,
				mnpermToHPermNoC.newUnit,mnpermTosPermNoC.newUnit};
		String[] newUnitExpected={"s / s","s / s","H / H","H / m","s / m"};
		
		assertArrayEquals(expected,retrieved, 0.0000001);
		assertArrayEquals(newUnitExpected,newUnitRetrieved);
			
		
		
		
	}
	
	/**
	 * Best factor time per other with constraint.
	 */
	@Test
	public void bestFactorTimePerOtherWithConstraint(){
		TupleFactorNewUnit mnpermTomnPerm=new Units().bestFactor("mn / m", 10, "_ / \"\"");
		TupleFactorNewUnit mnpermToHPerm=new Units().bestFactor("mn / m", 100000, "_ / \"\"");
		
		double[] expected={60d,1d/60d};
		double[] retrieved={mnpermTomnPerm.factor,mnpermToHPerm.factor};
		
		String[] newUnitRetrieved={mnpermTomnPerm.newUnit,mnpermToHPerm.newUnit};
		String[] newUnitExpected={"s / m","H / m"};
		
		assertArrayEquals(expected,retrieved, 0.0000001);
		assertArrayEquals(newUnitExpected,newUnitRetrieved);
		
	}
	
	/**
	 * Best factor other per time no constraint.
	 */
	@Test
	public void bestFactorOtherPerTimeNoConstraint()
	{
		TupleFactorNewUnit mpersTomillimPers=new Units().bestFactor("m / s", 1, null);
		TupleFactorNewUnit mpersToKmPers=new Units().bestFactor("m / s", 100000, null);
		double[] expected={1000d,0.001};
		double[] retrieved={mpersTomillimPers.factor,mpersToKmPers.factor};
		
		String[] newUnitRetrieved={mpersTomillimPers.newUnit,mpersToKmPers.newUnit};
		String[] newUnitExpected={"millim / s","Km / s"};
		
		assertArrayEquals(expected,retrieved, 0.0000001);
		assertArrayEquals(newUnitExpected,newUnitRetrieved);
	}
	
	/**
	 * Best factor other per time with constraint.
	 */
	@Test
	public void bestFactorOtherPerTimeWithConstraint(){
		 // m/s +constraint
		TupleFactorNewUnit mpersTomillimPers=new Units().bestFactor("m / s", 1, "_ / s");
		TupleFactorNewUnit mpersToKmPers=new Units().bestFactor("m / s", 100000, "_ / s");
		TupleFactorNewUnit millimpersTomPers=new Units().bestFactor("millim / s", 100000, "\"\" / s");
		
		double[] expected={1000d,0.001,0.001};
		double[] retrieved={mpersTomillimPers.factor,mpersToKmPers.factor,millimpersTomPers.factor};
		
		String[] newUnitRetrieved={mpersTomillimPers.newUnit,mpersToKmPers.newUnit,millimpersTomPers.newUnit};
		String[] newUnitExpected={"millim / s","Km / s","m / s"};
		
		assertArrayEquals(expected,retrieved, 0.0000001);
		assertArrayEquals(newUnitExpected,newUnitRetrieved);
	}
	
	/**
	 * Best factor time per time with constraint.
	 */
	@Test
	public void bestFactorTimePerTimeWithConstraint(){
		// like s/s  _/s _/_
		TupleFactorNewUnit spersTosPermn=new Units().bestFactor("s / s", 1, "s / mn");
		TupleFactorNewUnit spersTosPerH=new Units().bestFactor("s / s", 1, "_ / H");
		TupleFactorNewUnit spersTosPerD=new Units().bestFactor("s / s", 100000, "_ / H");
		TupleFactorNewUnit spersToHPermn=new Units().bestFactor("s / s", 100000, "H / _");
		TupleFactorNewUnit spersTomillisPers=new Units().bestFactor("s / s", 1, null);
		TupleFactorNewUnit spersTomnPers=new Units().bestFactor("s / s", 100000, null);
		
		double[] expected={60d,3600d,1d/24d,(1d/3600d)*60d,1000d,1d/60d};
		double[] retrieved={spersTosPermn.factor,spersTosPerH.factor,spersTosPerD.factor,spersToHPermn.factor,
				spersTomillisPers.factor,spersTomnPers.factor};
		
		String[] newUnitRetrieved={spersTosPermn.newUnit,spersTosPerH.newUnit,spersTosPerD.newUnit,spersToHPermn.newUnit,
				spersTomillisPers.newUnit,spersTomnPers.newUnit};
		String[] newUnitExpected={"s / mn","s / H", "D / H", "H / mn","millis / s","mn / s"};
		
		assertArrayEquals(expected,retrieved, 0.0000002);
		assertArrayEquals(newUnitExpected,newUnitRetrieved);
		
		
	}
	
	/**
	 * Best factor time per other no constraint.
	 */
	@Test
	public void bestFactorTimePerOtherNoConstraint(){
		// like s / m 
		TupleFactorNewUnit spermTomillisPerm=new Units().bestFactor("s / m", 1, null);
		TupleFactorNewUnit spermTomnPerm=new Units().bestFactor("s / m", 100000, null);
		
		double[] expected={1000d,1d/60d};
		double[] retrieved={spermTomillisPerm.factor,spermTomnPerm.factor};
		
		String[] newUnitRetrieved={spermTomillisPerm.newUnit,spermTomnPerm.newUnit};
		String[] newUnitExpected={"millis / m","mn / m"};
		
		assertArrayEquals(expected,retrieved, 0.0000001);
		assertArrayEquals(newUnitExpected,newUnitRetrieved);
		
	}
	
	/**
	 * Best factor time mult other no constraint.
	 */
	@Test
	public void bestFactorTimeMultOtherNoConstraint(){
		//  s * m
		TupleFactorNewUnit smultmTomillisMultm=new Units().bestFactor("s * m", 1, null);
		TupleFactorNewUnit smultmTomnmultm=new Units().bestFactor("s * m", 100000, null);
		
		double[] expected={1000d,1/60d};
		double[] retrieved={smultmTomillisMultm.factor,smultmTomnmultm.factor};
		
		String[] newUnitRetrieved={smultmTomillisMultm.newUnit,smultmTomnmultm.newUnit};
		String[] newUnitExpected={"millis * m", "mn * m"};
		
		assertArrayEquals(expected,retrieved, 0.0000001);
		assertArrayEquals(newUnitExpected,newUnitRetrieved);
	}
	
	/**
	 * Best factor time mult other with constraint.
	 */
	@Test
	public void bestFactorTimeMultOtherWithConstraint(){
	//  s * m + _ * K
		TupleFactorNewUnit smultmTomillisMultm=new Units().bestFactor("s * m", 1, "millis * \"\"");
		TupleFactorNewUnit smultmTonanosMultKm=new Units().bestFactor("s * m", 1, "nanos * K");
		TupleFactorNewUnit smultmTomnmultm=new Units().bestFactor("s * m", 100000, "_ * \"\"");
		TupleFactorNewUnit smultmToHmultm=new Units().bestFactor("s * m", 1, "_ * \"\"");
		TupleFactorNewUnit smultmToHmultmBis=new Units().bestFactor("s * m", 100, "millis * _");
		TupleFactorNewUnit smultmToHmultmTer=new Units().bestFactor("s * m", 1, "s * _");
		
		double[] expected={1000d,1000000d,1/60d,1000d,1d,1000d};
		double[] retrieved={smultmTomillisMultm.factor,smultmTonanosMultKm.factor,smultmTomnmultm.factor,
				smultmToHmultm.factor,smultmToHmultmBis.factor,smultmToHmultmTer.factor};
		
		String[] newUnitRetrieved={smultmTomillisMultm.newUnit,smultmTonanosMultKm.newUnit,smultmTomnmultm.newUnit,
				smultmToHmultm.newUnit,smultmToHmultmBis.newUnit,smultmToHmultmTer.newUnit};
		String[] newUnitExpected={"millis * m","nanos * Km","mn * m","ms * m","millis * Km","s * millim"};
		
		assertArrayEquals(expected,retrieved, 0.0000001);
		assertArrayEquals(newUnitExpected,newUnitRetrieved);
		
	}
	
	/**
	 * Best factor other mult time no constraint.
	 */
	@Test
		public void bestFactorOtherMultTimeNoConstraint(){
		//  m * s
			TupleFactorNewUnit smultmTomillisMultm=new Units().bestFactor("m * s", 1, null);
			TupleFactorNewUnit smultmTomnmultm=new Units().bestFactor("m * s", 100000, null);
			
			double[] expected={1000d,1/60d};
			double[] retrieved={smultmTomillisMultm.factor,smultmTomnmultm.factor};
			
			String[] newUnitRetrieved={smultmTomillisMultm.newUnit,smultmTomnmultm.newUnit};
			String[] newUnitExpected={"m * millis", "m * mn"};
			
			assertArrayEquals(expected,retrieved, 0.0000001);
			assertArrayEquals(newUnitExpected,newUnitRetrieved);
		}
	
	/**
	 * Best factor other mult time with constraint.
	 */
	@Test
	public void bestFactorOtherMultTimeWithConstraint(){
	//  m * s + K * _
			TupleFactorNewUnit smultmTomillisMultm=new Units().bestFactor("m * s", 1, "\"\" * millis");
			TupleFactorNewUnit smultmTonanosMultKm=new Units().bestFactor("m * s", 1, "K * nanos");
			TupleFactorNewUnit smultmTomnmultm=new Units().bestFactor("m * s", 100000, "\"\" * _");
			TupleFactorNewUnit smultmToHmultm=new Units().bestFactor("m * s", 1, "\"\" * _");
			TupleFactorNewUnit smultmToHmultmBis=new Units().bestFactor("m * s", 100, "_ * millis");
			TupleFactorNewUnit smultmToHmultmTer=new Units().bestFactor("m * s", 1, "_ * s");
			

			double[] expected={1000d,1000000d,1/60d,1000d,1d,1000d};
			double[] retrieved={smultmTomillisMultm.factor,smultmTonanosMultKm.factor,smultmTomnmultm.factor,
					smultmToHmultm.factor,smultmToHmultmBis.factor,smultmToHmultmTer.factor};
			
			
			String[] newUnitRetrieved={smultmTomillisMultm.newUnit,smultmTonanosMultKm.newUnit,smultmTomnmultm.newUnit,
					smultmToHmultm.newUnit,smultmToHmultmBis.newUnit,smultmToHmultmTer.newUnit};
			String[] newUnitExpected={"m * millis","Km * nanos","m * mn","m * ms","Km * millis","millim * s"};
			
			assertArrayEquals(expected,retrieved, 0.0000001);
			assertArrayEquals(newUnitExpected,newUnitRetrieved);
	}
	
	/**
	 * Best factor time mult time no constraint.
	 */
	@Test
	public void bestFactorTimeMultTimeNoConstraint(){
	//  ms * s
				TupleFactorNewUnit smultmTomillisMultm=new Units().bestFactor("ms * s", 1, null);
				TupleFactorNewUnit smultmTomnmultm=new Units().bestFactor("millis * s", 100000, null);
				
				double[] expected={1000d,0.001};
				double[] retrieved={smultmTomillisMultm.factor,smultmTomnmultm.factor};
				
				String[] newUnitRetrieved={smultmTomillisMultm.newUnit,smultmTomnmultm.newUnit};
				String[] newUnitExpected={"micros * s","s * s"};
				
				assertArrayEquals(expected,retrieved, 0.0000001);
				assertArrayEquals(newUnitExpected,newUnitRetrieved);
	}
	
	/**
	 * Best factor time mult time with constraint.
	 */
	@Test
	public void bestFactorTimeMultTimeWithConstraint(){
	//  ms * _ , _ * ms
					TupleFactorNewUnit smultmTomillisMultm=new Units().bestFactor("ms * s", 1, "ms * H");
					TupleFactorNewUnit smultmTomnmultm=new Units().bestFactor("millis * s", 100000, "ms * mn");
					TupleFactorNewUnit smultmTomnmultm1=new Units().bestFactor("millis * s", 1, "_ * mn");
					TupleFactorNewUnit smultmTomnmultm2=new Units().bestFactor("millis * s", 100000, "_ * mn");
					TupleFactorNewUnit smultmTomnmultm3=new Units().bestFactor("millis * s", 100000, "millis * _");
					TupleFactorNewUnit smultmTomnmultm4=new Units().bestFactor("millis * s", 1, "millis * _");
					
					double[] expected={1d/3600d,1/60d ,1000d/60d,1d/60d,1/60d,1000d};
					double[] retrieved={smultmTomillisMultm.factor,smultmTomnmultm.factor,smultmTomnmultm1.factor,
							smultmTomnmultm2.factor,smultmTomnmultm3.factor,smultmTomnmultm4.factor};
					
					String[] newUnitRetrieved={smultmTomillisMultm.newUnit,smultmTomnmultm.newUnit,smultmTomnmultm1.newUnit,
							smultmTomnmultm2.newUnit,smultmTomnmultm3.newUnit,smultmTomnmultm4.newUnit};
					String[] newUnitExpected={"ms * H","ms * mn","micros * mn","ms * mn","millis * mn","millis * ms"};
					
					assertArrayEquals(expected,retrieved, 0.0000001);
					assertArrayEquals(newUnitExpected,newUnitRetrieved);
		
	}
	
	/**
	 * Best factor composite others mult others no constraint.
	 */
	@Test
	public void bestFactorCompositeOthersMultOthersNoConstraint(){
		// Km * m
		TupleFactorNewUnit test1=new Units().bestFactor("Km * m", 1, null);
		TupleFactorNewUnit test2=new Units().bestFactor("Km * m", 100000, null);
		TupleFactorNewUnit test3=new Units().bestFactor("m * Km", 100000, null);

		double[] expected={1000d,1/1000d,1/1000d};
		double[] retrieved={test1.factor,test2.factor,test3.factor};
		
		String[] newUnitRetrieved={test1.newUnit,test2.newUnit,test3.newUnit};
		String[] newUnitExpected={"m * m","Mm * m","Km * Km"};
		

		assertArrayEquals(expected,retrieved, 0.0000001);
		assertArrayEquals(newUnitExpected,newUnitRetrieved);
		
	}
	
	/**
	 * Best factor composite others mult others constraint fois.
	 */
	@Test
	public void bestFactorCompositeOthersMultOthersConstraint_fois(){
		// _ * m
		TupleFactorNewUnit test1=new Units().bestFactor("Km * m", 1, "_ * \"\"");
		TupleFactorNewUnit test2=new Units().bestFactor("Km * m", 100000, "_ * \"\"");
		TupleFactorNewUnit test3=new Units().bestFactor("m * Km", 100000, "_ * \"\"");

		double[] expected={1000d,1/1000d,1/1000d};
		double[] retrieved={test1.factor,test2.factor,test3.factor};
		
		String[] newUnitRetrieved={test1.newUnit,test2.newUnit,test3.newUnit};
		String[] newUnitExpected={"m * m","Mm * m","Mm * m"};
		

		assertArrayEquals(expected,retrieved, 0.0000001);
		assertArrayEquals(newUnitExpected,newUnitRetrieved);
		
	}
	
	/**
	 * Best factor composite others mult others constraint fois.
	 */
	@Test
	public void bestFactorCompositeOthersMultOthersConstraintFois_(){
		// m * _
				TupleFactorNewUnit test1=new Units().bestFactor("Km * m", 1, "\"\" * _");
				TupleFactorNewUnit test2=new Units().bestFactor("Km * m", 100000, "\"\" * _");
				TupleFactorNewUnit test3=new Units().bestFactor("m * Km", 100000, "\"\" * _");
				TupleFactorNewUnit test4=new Units().bestFactor("m * Km", 100000, "\"\" * K");
				
				double[] expected={1000d,1d/1000d,1d/1000d,1d};
				double[] retrieved={test1.factor,test2.factor,test3.factor,test4.factor};
				
				String[] newUnitRetrieved={test1.newUnit,test2.newUnit,test3.newUnit,test4.newUnit};
				String[] newUnitExpected={"m * m","m * Mm","m * Mm", "m * Km"};
				

				assertArrayEquals(expected,retrieved, 0.0000001);
				assertArrayEquals(newUnitExpected,newUnitRetrieved);
				
	}
}
