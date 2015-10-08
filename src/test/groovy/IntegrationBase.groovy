/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

class IntegrationBase {

	void checkExistenceAndContentOfAFile(file, contents) {
		if (!file.canRead()) {
			throw new FileNotFoundException( "Could not find the " + file)
		}

		def lines_to_check_in_unix_script_marker = [:]
		(0..contents.size()).each { index ->
			lines_to_check_in_unix_script_marker[index] = false
		}

		file.eachLine { file_content, file_line ->
			contents.eachWithIndex { contents_expected, index ->
				if (file_content.equals(contents_expected)) {
					lines_to_check_in_unix_script_marker[index] = true
				}
			}
		}

		contents.eachWithIndex { value, index ->
			if ( lines_to_check_in_unix_script_marker[index] == false ) {
				throw new Exception("The expected content in " + file + " couldn't be found." + contents[index])
			}
		}
	}
    
    def String convertPathIntoPlatform(String contents) {
        def result = "";
        if (File.separator.equals("\\")) {
            result = contents.replaceAll("/", "\\\\");
        } else {
            result = contents;
        }
        
        return result;
    }
}
