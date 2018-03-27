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
package org.jlp.logfouineur.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;

// TODO: Auto-generated Javadoc
/**
 * The Class Utils.
 */
public final class Utils {
	
	/**
	 * Tail.
	 *
	 * @param file the file
	 * @return the string
	 */
	public static String tail( File file ) {
	    RandomAccessFile fileHandler = null;
	    try {
	        fileHandler = new RandomAccessFile( file, "r" );
	        long fileLength = fileHandler.length() - 1;
	        StringBuilder sb = new StringBuilder();

	        for(long filePointer = fileLength; filePointer != -1; filePointer--){
	            fileHandler.seek( filePointer );
	            int readByte = fileHandler.readByte();

	            if( readByte == 0xA ) {
	                if( filePointer == fileLength ) {
	                    continue;
	                }
	                break;

	            } else if( readByte == 0xD ) {
	                if( filePointer == fileLength - 1 ) {
	                    continue;
	                }
	                break;
	            }

	            sb.append( ( char ) readByte );
	        }

	        String lastLine = sb.reverse().toString();
	        return lastLine;
	    } catch( java.io.FileNotFoundException e ) {
	        e.printStackTrace();
	        return null;
	    } catch( java.io.IOException e ) {
	        e.printStackTrace();
	        return null;
	    } finally {
	        if (fileHandler != null )
	            try {
	                fileHandler.close();
	            } catch (IOException e) {
	                /* ignore */
	            }
	    }
	}
 

	/**
	 * Youngest rep.
	 *
	 * @param path the path
	 * @param start the start
	 * @return the path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static Path youngestRep(Path path, String start) throws IOException {
		return Files.list(path).filter(pathbis -> pathbis.toFile().isDirectory())
				.filter(pathbis -> pathbis.toFile().getName().contains(start))
				.sorted((path1, path2) -> (new ComparatorFile().compare(path2, path1))).findFirst().orElse(null);

	}

	/**
	 * Oldest rep.
	 *
	 * @param path the path
	 * @param start the start
	 * @return the path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static Path oldestRep(Path path, String start) throws IOException {
		return Files.list(path).filter(pathbis -> pathbis.toFile().isDirectory())
				.filter(pathbis -> pathbis.toFile().getName().contains(start))
				.sorted((path1, path2) -> (new ComparatorFile().compare(path1, path2))).findFirst().orElse(null);

	}
	
	/**
	 * Files in directory.
	 *
	 * @param strDir the str dir
	 * @return the observable list
	 */
	public static ObservableList<String> filesInDirectory(String strDir){
		if(new File(strDir).isFile()) return null;
		Path path=new File(strDir).toPath();
		Stream<String> stream=null;
		try {
			stream=Files.list(path).filter(pathbis -> pathbis.toFile().isFile()).
					map(pathbis-> pathbis.toFile().getName());
			String[] stringArray = (String[]) stream.toArray(size -> new String[ size ]	);
			return (ObservableListBase<String>) FXCollections.observableArrayList(stringArray);

		} catch (IOException e) {
		
			return null;
		}
		
		
	}
	
	/**
	 * Files in directory with model.
	 *
	 * @param strDir the str dir
	 * @param aregex the aregex
	 * @return the observable list
	 */
	public static ObservableList<String> filesInDirectoryWithModel(String strDir,String aregex){
		if(new File(strDir).isFile()) return null;
		Path path=new File(strDir).toPath();
		Stream<String> stream=null;
		try {
			stream=Files.list(path).filter(pathbis -> pathbis.toFile().isFile()).
					map(pathbis-> pathbis.toFile().getName()).filter(nameFile -> {System.out.println("nameFile="+nameFile); return nameFile.matches(aregex);});
			System.out.println("strDir="+strDir);
			System.out.println("stream="+stream.toString());
			String[] stringArray = (String[]) stream.toArray(size -> new String[ size ]	);
			return (ObservableListBase<String>) FXCollections.observableArrayList(stringArray);

		} catch (IOException e) {
		
			return null;
		}
		
		
	}
	
	/**
	 * Delete recursif dir.
	 *
	 * @param strDir the str dir
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void deleteRecursifDir(String strDir) throws IOException{
		if(null == new File(strDir) || new File(strDir).isFile()) return;
		try {
			Files.list(Paths.get(strDir)).forEach(( path) ->
			{
				if(path.toFile().isDirectory()){
					try {
						deleteRecursifDir(path.toFile().getCanonicalPath());
					} catch (Exception e) {}
					
				}else
				{
					path.toFile().delete();
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw(e);
		}
	}
	
}
