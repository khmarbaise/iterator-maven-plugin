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

import java.io.*
import java.util.*


t = new IntegrationBase()


def getProjectVersion() {
    def pom = new XmlSlurper().parse(new File(basedir, 'pom.xml'))

    def allPlugins = pom.build.plugins.plugin;

    def configurationMavenPlugin = allPlugins.find { item ->
        item.groupId.equals("com.soebes.maven.plugins") && item.artifactId.equals("iterator-maven-plugin");
    }

    return configurationMavenPlugin.version;
}

def projectVersion = getProjectVersion();

println "Project version: ${projectVersion}"


def buildLogFile = new File( basedir, "build.log");

//This test is currently not very sure.
//Checking the existence of those lines is ok,
//but we should check the non existinence of 
//some other lines to make 100% sure this works
//as expected.
//TODO: Think about this.
t.checkExistenceAndContentOfAFile(buildLogFile, [
    '[INFO] ------  iterator-maven-plugin ( iteration: 1 )',
    '[ERROR] ------ Maven call was NOT Ok. for iteration 1 ( return code: 1 )',
    '[INFO] [INFO] BUILD FAILURE',
    "[INFO] [ERROR] Could not find goal 'anton' in plugin org.apache.maven.plugins:maven-surefire-plugin:2.18.1 among available goals help, test -> [Help 1]",
    '[INFO] BUILD FAILURE',
])

def result = false
buildLogFile.eachLine { file_content, file_line ->
    if (file_content.equals('[INFO] ------  iterator-maven-plugin ( iteration: 2 )')) {
        result = true
    }
}

if (result) {
    //We have found a line which shouldn't be existing!
    return false
}
return true;
