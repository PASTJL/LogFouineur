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

import org.jlp.logfouineur.util.Utils;


// TODO: Auto-generated Javadoc
/**
 * The Class UtilsTest.
 */
public class UtilsTest {
	
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
	 * Youngest.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void youngest() throws IOException {

		Path path = Utils.youngestRep(new File(workspace + File.separator + project + File.separator).toPath(),
				"scn_");
		
		System.out.println("os.name=" + osname);
		if (osname.toUpperCase().contains("WINDOWS")) {
			assertArrayEquals("C:\\opt\\workspaceLP\\projet0\\scn_20120128".getBytes(),
					path.toString().getBytes());
			System.out.println("path youngest=" + path);
		}

	}
	
	/**
	 * Oldest.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void oldest() throws IOException {

		Path path = Utils.oldestRep(new File(workspace + File.separator + project + File.separator).toPath(),
				"scn_");
		System.out.println("path oldest =" + path);
		System.out.println("os.name=" + osname);
		if (osname.toUpperCase().contains("WINDOWS")) {
			assertArrayEquals("C:\\opt\\workspaceLP\\projet0\\scn_20130903".getBytes(),
					path.toString().getBytes());
			
		}

	}
	
}
