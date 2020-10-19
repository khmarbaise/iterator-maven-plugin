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

def projectVersion = getProjectVersion();

println "Project version: ${projectVersion}"

def currentFolder = new File(basedir, ".").getCanonicalPath() + "/";

def logFileInput = new File(basedir, "build.log")

new File(basedir, "build-filtered.log").withWriter { out ->
    logFileInput.eachLine { line ->

        if (line.contains(currentFolder)) {
            line = line.replace(currentFolder, "/home/iterator/");
        }

        if (    !line.startsWith("Download: ")
        &&  !line.startsWith("Downloaded: ")
        &&  !line.startsWith("Downloading: ")
        &&  !line.startsWith("[INFO] Downloaded: ")
        &&  !line.startsWith("[INFO] Downloading: ")
        &&  !line.startsWith(" wagon http use")
        &&  !line.startsWith("Running post-build")
        &&  !line.startsWith("Finished post-build ")
        &&  !line.startsWith("Project version:")) {
            out.println line;
        }
    }
}


t.checkExistenceAndContentOfAFile(logFileInput, [
    '[INFO] --- iterator-maven-plugin:' +projectVersion + ':iterator (default) @ mavenAssemblyTest ---',
    '[INFO] ------ (test) org.apache.maven.plugins:maven-assembly-plugin:3.1.1:single',
    '[INFO] ------ (dev) org.apache.maven.plugins:maven-assembly-plugin:3.1.1:single',
    '[INFO] ------ (prod) org.apache.maven.plugins:maven-assembly-plugin:3.1.1:single',
    '[INFO] Reading assembly descriptor: ' + basedir + '/test.xml',
    '[INFO] Building war: ' + basedir + t.convertPathIntoPlatform('/target/mavenAssemblyTest-0.1-SNAPSHOT-test.war'),
    '[INFO] Reading assembly descriptor: ' + basedir + '/dev.xml',
    '[INFO] Building war: ' + basedir + t.convertPathIntoPlatform('/target/mavenAssemblyTest-0.1-SNAPSHOT-dev.war'),
    '[INFO] Reading assembly descriptor: ' + basedir + '/prod.xml',
    '[INFO] Building war: ' + basedir + t.convertPathIntoPlatform('/target/mavenAssemblyTest-0.1-SNAPSHOT-prod.war'),
    '[INFO] BUILD SUCCESS',
])
def test_war = new File (basedir, "/target/mavenAssemblyTest-0.1-SNAPSHOT-test.war");
if (!test_war.exists()) {
    throw new FileNotFoundException("The file " + test_war + " does not exist!");
}
def dev_war = new File (basedir, "/target/mavenAssemblyTest-0.1-SNAPSHOT-dev.war");
if (!dev_war.exists()) {
    throw new FileNotFoundException("The file " + dev_war + " does not exist!");
}
def prod_war = new File (basedir, "/target/mavenAssemblyTest-0.1-SNAPSHOT-prod.war");
if (!prod_war.exists()) {
    throw new FileNotFoundException("The file " + prod_war + " does not exist!");
}


return true;
