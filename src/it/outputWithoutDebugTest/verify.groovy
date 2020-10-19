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

def currentFolder = new File(basedir, ".").getCanonicalPath() + "/";

def logFileInput = new File(basedir, "build.log")

new File(basedir, "build-filtered.log").withWriter { out ->
    logFileInput.eachLine { line ->

        if (line.contains(currentFolder)) {
            line = line.replace(currentFolder, "/home/iterator/");
        }

        if (    !line.startsWith("Download")
        &&  !line.startsWith(" wagon http use")
        &&  !line.startsWith("Running post-build")
        &&  !line.startsWith("Finished post-build ")
        &&  !line.startsWith("Project version:")) {
            out.println line;
        }
    }
}


t.checkExistenceAndContentOfAFile(logFileInput, [
    '[INFO] --- iterator-maven-plugin:' + projectVersion + ':iterator (default) @ basic-test ---',
    '[INFO] ------ (one) com.soebes.maven.plugins:echo-maven-plugin:0.4.0:echo',
    '[INFO] This is a message: one',
    '[INFO] ------ (two) com.soebes.maven.plugins:echo-maven-plugin:0.4.0:echo',
    '[INFO] This is a message: two',
    '[INFO] ------ (three) com.soebes.maven.plugins:echo-maven-plugin:0.4.0:echo',
    '[INFO] This is a message: three',
    '[INFO] BUILD SUCCESS',
])


return true;
