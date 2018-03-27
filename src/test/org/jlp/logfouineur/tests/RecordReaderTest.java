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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import org.jlp.logfouineur.records.ConfigRecord;
import org.jlp.logfouineur.records.RecordReader;
import org.junit.jupiter.api.BeforeAll;


// TODO: Auto-generated Javadoc
/**
 * The Class RecordReaderTest.
 */

public class RecordReaderTest {
	
	/** The workspace. */
	public String workspace=System.getProperty("worspace");
	
	/** The project. */
	public String project=System.getProperty("project");
	
	/** The file to test. */
	public Path fileToTest = null;
	
	/** The file to test 2. */
	public Path fileToTest2 = null;
	
	/** The file to test 3. */
	public Path fileToTest3 = null;
	
	/** The file to test 4. */
	public Path fileToTest4 = null;
	
	/** The stream. */
	public Stream<String> stream = null;
	
	/** The rr. */
	public RecordReader rr = new RecordReader();
	
	/** The gzip buff reader. */
	BufferedReader gzipBuffReader = null;
	
	/** The gzip buff reader 2. */
	BufferedReader gzipBuffReader2 = null;
	
	/** The deb in millis. */
	Long debInMillis = 0L;

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
		try {

			String osname = System.getProperty("os.name");
			System.out.println("os.name=" + osname);
			if (osname.toUpperCase().contains("WINDOWS")) {
				fileToTest = FileSystems.getDefault()
						.getPath("C:/opt/workspaceLP/projet0/scn_20130903/logs/concat_jonas.log");
				System.out.println("concat_jonas.log : a file with 736760 lines the last is empty");
				fileToTest3 = FileSystems.getDefault()
						.getPath("C:/opt/workspaceLP/projet0/scn_20130903/logs/concat_jonas.log.gz");
				FileInputStream fis = new FileInputStream(fileToTest3.toString());
				GZIPInputStream gzipReader = new GZIPInputStream(fis);
				gzipBuffReader = new BufferedReader(new InputStreamReader(gzipReader), 10 * 1024 * 1024);
				System.out.println("concat_jonas.log.gz : a gzipped file with 736760 lines the last is empty");
				fileToTest2 = FileSystems.getDefault().getPath(
						"C:/opt/workspaceLP/projet0/scn_20130903/logs/verbosegc.20130819.195715.21728.txt.001");
				System.out.println("A GC file with 985 records expected ");
				fileToTest4 = FileSystems.getDefault().getPath(
						"C:/opt/workspaceLP/projet0/scn_20130903/logs/verbosegc.20130819.195715.21728.txt.001.gz");
				FileInputStream fis2 = new FileInputStream(fileToTest4.toString());
				GZIPInputStream gzipReader2 = new GZIPInputStream(fis2);
				gzipBuffReader2 = new BufferedReader(new InputStreamReader(gzipReader2), 10 * 1024 * 1024);
			}
			else
			{
				fileToTest = FileSystems.getDefault()
						.getPath("/opt/workspaceLP/projet0/scn_20130903/logs/concat_jonas.log");
				System.out.println("concat_jonas.log : a file with 736760 lines the last is empty");
				fileToTest3 = FileSystems.getDefault()
						.getPath("/opt/workspaceLP/projet0/scn_20130903/logs/concat_jonas.log.gz");
				FileInputStream fis = new FileInputStream(fileToTest3.toString());
				GZIPInputStream gzipReader = new GZIPInputStream(fis);
				gzipBuffReader = new BufferedReader(new InputStreamReader(gzipReader), 10 * 1024 * 1024);
				System.out.println("concat_jonas.log.gz : a gzipped file with 736760 lines the last is empty");
				fileToTest2 = FileSystems.getDefault().getPath(
						"/opt/workspaceLP/projet0/scn_20130903/logs/verbosegc.20130819.195715.21728.txt.001");
				System.out.println("A GC file with 985 records expected ");
				fileToTest4 = FileSystems.getDefault().getPath(
						"/opt/workspaceLP/projet0/scn_20130903/logs/verbosegc.20130819.195715.21728.txt.001.gz");
				FileInputStream fis2 = new FileInputStream(fileToTest4.toString());
				GZIPInputStream gzipReader2 = new GZIPInputStream(fis2);
				gzipBuffReader2 = new BufferedReader(new InputStreamReader(gzipReader2), 10 * 1024 * 1024);
			}

		} catch (InvalidPathException ie) {
			fail(ie.getMessage());
		}
	}

	/**
	 * Tear down.
	 *
	 * @throws Exception the exception
	 */
	@AfterAll
	public void tearDown() throws Exception {

		ConfigRecord.reInit();

	}

	/**
	 * Read mono line.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void readMonoLine() throws IOException {
		debInMillis = System.currentTimeMillis();
		stream = Files.lines(fileToTest);
		long count = rr.read(stream).count();
		System.out.println("Monoline System.currentTime : count =" + count + " ;execute en "
				+ (System.currentTimeMillis() - debInMillis) + " ms");

		long[] expecteds = { 736759L };
		long[] currents = { count };
		assertArrayEquals(expecteds, currents);
		stream.close();
		// fail("Not yet implemented");
	}

	/**
	 * Read mono line gziped.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void readMonoLineGziped() throws IOException {
		debInMillis = System.currentTimeMillis();
		stream = gzipBuffReader.lines();
		long count = rr.read(stream).count();
		System.out.println("Monoline zipped System.currentTime : count =" + count + " ;execute en "
				+ (System.currentTimeMillis() - debInMillis) + " ms");

		long[] expecteds = { 736759L };
		long[] currents = { count };
		assertArrayEquals(expecteds, currents);

		stream.close();
		// fail("Not yet implemented");
	}

	/**
	 * Read multi line gziped.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void readMultiLineGziped() throws IOException {
		// <gc-start
		ConfigRecord.beginRecord = Pattern.compile("<gc-start");
		// </gc-start>
		ConfigRecord.endRecord = Pattern.compile("</gc-start>");
		ConfigRecord.beginEqualsEnd = false;
		ConfigRecord.monoline = false;
		ConfigRecord.regexExclude = null;
		ConfigRecord.regexInclude = null;
		ConfigRecord.alignConfig();
		debInMillis = System.currentTimeMillis();
		stream = gzipBuffReader2.lines();
		long count = rr.read(stream).count();
		System.out.println("GC Multiline zipped System.currentTime : count =" + count + " ;execute en "
				+ (System.currentTimeMillis() - debInMillis) + " ms");

		long[] expecteds = { 985L };
		long[] currents = { count };
		assertArrayEquals(expecteds, currents);

		stream.close();
		// fail("Not yet implemented");
	}

	/**
	 * Read begin equals end.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void readBeginEqualsEnd() throws IOException {

		ConfigRecord.beginRecord = Pattern.compile("(\\d{1,3}\\.){3}\\d{1,3}");
		ConfigRecord.endRecord = ConfigRecord.beginRecord;
		ConfigRecord.beginEqualsEnd = true;
		ConfigRecord.monoline = false;
		ConfigRecord.regexExclude = null;
		ConfigRecord.regexInclude = null;
		ConfigRecord.alignConfig();
		debInMillis = System.currentTimeMillis();
		stream = Files.lines(fileToTest);
		long count = rr.read(stream).count();
		System.out.println("Multiline begin=end System.currentTime : count =" + count + " ;execute en "
				+ (System.currentTimeMillis() - debInMillis) + " ms");

		long[] expecteds = { 736759L };
		long[] currents = { count };
		assertArrayEquals(expecteds, currents);

		stream.close();
	}

	/**
	 * Read multi line.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void readMultiLine() throws IOException {
		// <gc-start
		ConfigRecord.beginRecord = Pattern.compile("<gc-start");
		// </gc-start>
		ConfigRecord.endRecord = Pattern.compile("</gc-start>");
		ConfigRecord.beginEqualsEnd = false;
		ConfigRecord.monoline = false;
		ConfigRecord.regexExclude = null;
		ConfigRecord.regexInclude = null;
		ConfigRecord.alignConfig();

		debInMillis = System.currentTimeMillis();
		stream = Files.lines(fileToTest2);

		long count = rr.read(stream).count();

		System.out.println("Multiline begin!=end System.currentTime : count =" + count + " ;execute en "
				+ (System.currentTimeMillis() - debInMillis) + " ms");

		long[] expecteds = { 985L };
		long[] currents = { count };
		assertArrayEquals(expecteds, currents);

		stream.close();
	}

	/**
	 * Read mono line exclude.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void readMonoLineExclude() throws IOException {
		// excluding ip address 10.186.9.87 (295774 occurrences doublon sur la
		// derniere ligne)
		ConfigRecord.regexExclude = Pattern.compile("10\\.186\\.9\\.87");
		debInMillis = System.currentTimeMillis();

		stream = Files.lines(fileToTest);
		long count = rr.read(stream).count();
		System.out.println("Monoline Exclude System.currentTime : count =" + count + " ;execute en "
				+ (System.currentTimeMillis() - debInMillis) + " ms");

		long[] expecteds = { 440985L };
		long[] currents = { count };
		assertArrayEquals(expecteds, currents);
		stream.close();
		ConfigRecord.regexExclude = null;
		// fail("Not yet implemented");
	}

	/**
	 * Read mono line include.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void readMonoLineInclude() throws IOException {
		// including ip address 10.186.9.87 (295774 occurrences, doublon sur la
		// derniere ligne)
		ConfigRecord.regexInclude = Pattern.compile("10\\.186\\.9\\.87");
		debInMillis = System.currentTimeMillis();

		stream = Files.lines(fileToTest);
		long count = rr.read(stream).count();
		System.out.println("Monoline Include System.currentTime : count =" + count + " ;execute en "
				+ (System.currentTimeMillis() - debInMillis) + " ms");

		long[] expecteds = { 295774L };
		long[] currents = { count };
		assertArrayEquals(expecteds, currents);
		stream.close();
		ConfigRecord.regexInclude = null;
		// fail("Not yet implemented");
	}

	/**
	 * Read multi line exclude.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void readMultiLineExclude() throws IOException {
		// including ip address 10.186.9.87 (295774 occurrences, doublon sur la
		// derniere ligne)
		ConfigRecord.regexExclude = Pattern.compile("10\\.186\\.9\\.87");
		ConfigRecord.beginRecord = Pattern.compile("(\\d{1,3}\\.){3}\\d{1,3}");
		ConfigRecord.endRecord = ConfigRecord.beginRecord;
		ConfigRecord.beginEqualsEnd = true;
		ConfigRecord.monoline = false;
		ConfigRecord.alignConfig();

		debInMillis = System.currentTimeMillis();

		stream = Files.lines(fileToTest);
		long count = rr.read(stream).count();
		System.out.println("Monoline Include System.currentTime : count =" + count + " ;execute en "
				+ (System.currentTimeMillis() - debInMillis) + " ms");

		long[] expecteds = { 440985L };
		long[] currents = { count };
	assertArrayEquals(expecteds, currents);
		stream.close();
		ConfigRecord.regexExclude = null;
		// fail("Not yet implemented");
	}

	/**
	 * Read multi line include.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void readMultiLineInclude() throws IOException {
		// including ip address 10.186.9.87 (295774 occurrences, doublon sur la
		// derniere ligne)
		ConfigRecord.regexInclude = Pattern.compile("10\\.186\\.9\\.87");
		ConfigRecord.beginRecord = Pattern.compile("(\\d{1,3}\\.){3}\\d{1,3}");
		ConfigRecord.endRecord = ConfigRecord.beginRecord;
		ConfigRecord.beginEqualsEnd = true;
		ConfigRecord.monoline = false;
		ConfigRecord.alignConfig();

		debInMillis = System.currentTimeMillis();

		stream = Files.lines(fileToTest);
		long count = rr.read(stream).count();
		System.out.println("Monoline Include System.currentTime : count =" + count + " ;execute en "
				+ (System.currentTimeMillis() - debInMillis) + " ms");

		long[] expecteds = { 295774L };
		long[] currents = { count };
		assertArrayEquals(expecteds, currents);
		stream.close();
		ConfigRecord.regexInclude = null;
		// fail("Not yet implemented");
	}
}
