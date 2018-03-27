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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.ToLongFunction;

// TODO: Auto-generated Javadoc
/**
 * The Class ComparatorFile.
 */
public class ComparatorFile  {
	
	

	
	/**
	 * Compare.
	 *
	 * @param path1 the path 1
	 * @param path2 the path 2
	 * @return the int
	 */
	public static int  compare(Path path1, Path path2) {
		BasicFileAttributes attr1,attr2;
		try {
			attr1 = Files.readAttributes(path1, BasicFileAttributes.class);
			attr2 = Files.readAttributes(path2, BasicFileAttributes.class);
//			System.out.println("path1 : "+path1+" ; created at "+attr1.creationTime().toMillis());
//			System.out.println("path2 : "+path2+" ; created at "+attr2.creationTime().toMillis());
			long diff=attr1.creationTime().toMillis()-attr2.creationTime().toMillis();
			if(diff == 0) return 0;
			else if (diff > 0) return -1; else return 1;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
		
	}

}
