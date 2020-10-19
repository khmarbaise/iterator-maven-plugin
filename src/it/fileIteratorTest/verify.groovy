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
t = new IntegrationBase()

def getProjectVersion() {
    def pom = new XmlSlurper().parse(new File(basedir, 'pom.xml'))

    def allPlugins = pom.build.plugins.plugin;

    def configurationMavenPlugin = allPlugins.find { item ->
        item.groupId.equals("com.soebes.maven.plugins") && item.artifactId.equals("iterator-maven-plugin");
    }

    return configurationMavenPlugin.version;
}

def projectVersion = getProjectVersion()

println "Project version: ${projectVersion}"

def currentFolder = new File(basedir, ".").getCanonicalPath() + "/"

def logFileInput = new File(basedir, "build.log")

t.checkExistenceAndContentOfAFile(logFileInput, [
    '[INFO] --- iterator-maven-plugin:' + projectVersion + ':iterator (default) @ fileIteratorTest ---',
    '[INFO] ------ (maven/multiple/first/test1.txt) com.soebes.maven.plugins:echo-maven-plugin:0.4.0:echo',
    '[INFO] Item: maven/multiple/first/test1.txt',
    '[INFO] Filename: test1.txt',
    '[INFO] Basename: test1',
    '[INFO] Extension: txt',
    '[INFO] Folder: maven/multiple/first',
    '[INFO] Foldername: first',
    '[INFO] Parent: maven/multiple',
    '[INFO] Parentname: multiple',
    '[INFO] ------ (maven/multiple/third/test1.txt) com.soebes.maven.plugins:echo-maven-plugin:0.4.0:echo',
    '[INFO] Item: maven/multiple/third/test1.txt',
    '[INFO] Filename: test1.txt',
    '[INFO] Basename: test1',
    '[INFO] Extension: txt',
    '[INFO] Folder: maven/multiple/third',
    '[INFO] Foldername: third',
    '[INFO] Parent: maven/multiple',
    '[INFO] Parentname: multiple',
    '[INFO] ------ (maven/multiple/second/test1.txt) com.soebes.maven.plugins:echo-maven-plugin:0.4.0:echo',
    '[INFO] Item: maven/multiple/second/test1.txt',
    '[INFO] Filename: test1.txt',
    '[INFO] Basename: test1',
    '[INFO] Extension: txt',
    '[INFO] Folder: maven/multiple/second',
    '[INFO] Foldername: second',
    '[INFO] Parent: maven/multiple',
    '[INFO] Parentname: multiple',
    '[INFO] ------ (maven/multiple/first/test2.txt) com.soebes.maven.plugins:echo-maven-plugin:0.4.0:echo',
    '[INFO] Item: maven/multiple/first/test2.txt',
    '[INFO] Filename: test2.txt',
    '[INFO] Basename: test2',
    '[INFO] Extension: txt',
    '[INFO] Folder: maven/multiple/first',
    '[INFO] Foldername: first',
    '[INFO] Parent: maven/multiple',
    '[INFO] Parentname: multiple',
    '[INFO] ------ (maven/multiple/third/test2.txt) com.soebes.maven.plugins:echo-maven-plugin:0.4.0:echo',
    '[INFO] Item: maven/multiple/third/test2.txt',
    '[INFO] Filename: test2.txt',
    '[INFO] Basename: test2',
    '[INFO] Extension: txt',
    '[INFO] Folder: maven/multiple/third',
    '[INFO] Foldername: third',
    '[INFO] Parent: maven/multiple',
    '[INFO] Parentname: multiple',
    '[INFO] ------ (maven/multiple/second/test2.txt) com.soebes.maven.plugins:echo-maven-plugin:0.4.0:echo',
    '[INFO] Item: maven/multiple/second/test2.txt',
    '[INFO] Filename: test2.txt',
    '[INFO] Basename: test2',
    '[INFO] Extension: txt',
    '[INFO] Folder: maven/multiple/second',
    '[INFO] Foldername: second',
    '[INFO] Parent: maven/multiple',
    '[INFO] Parentname: multiple',
    '[INFO] BUILD SUCCESS',
])

return true
