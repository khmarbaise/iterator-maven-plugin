import java.io.*
import java.util.*
import java.util.zip.ZipFile;


t = new IntegrationBase()

def getInformation (File warFile) {
    def devZipFile = new ZipFile(warFile)
    def result = null

    devZipFile.entries().findAll { !it.directory && it.name.equals("WEB-INF/server.properties") }.each {
//	println "Entry: " + it.name
	result = devZipFile.getInputStream(it).text
    }
    return result
}

def getProjectVersion() {
    def pom = new XmlSlurper().parse(new File(basedir, 'pom.xml'))

    def allPlugins = pom.build.plugins.plugin;

    def configurationMavenPlugin = allPlugins.find { item ->
	item.groupId.equals("com.soebes.maven.plugins") && item.artifactId.equals("itexin-maven-plugin");
    }

    return configurationMavenPlugin.version;
}

def projectVersion = getProjectVersion();

println "Project version: ${projectVersion}"

def buildLogFile = new File( basedir, "build.log");


t.checkExistenceAndContentOfAFile(buildLogFile, [
    '[INFO] --- itexin-maven-plugin:' +projectVersion + ':executor (default) @ basic-test ---',
    '[INFO] ]] org.apache.maven.plugins:maven-assembly-plugin:2.4',
        
    '[INFO] Reading assembly descriptor: ' + basedir + '/src/main/assembly/archive.xml',
    '[INFO] Building war: ' + basedir + '/target/mavenAssemblyPluginWithPropertiesTest-0.1-SNAPSHOT-dev.war',
    
    '[INFO] Reading assembly descriptor: ' + basedir + '/src/main/assembly/archive.xml',
    '[INFO] Building war: ' + basedir + '/target/mavenAssemblyPluginWithPropertiesTest-0.1-SNAPSHOT-test.war',

    '[INFO] Reading assembly descriptor: ' + basedir + '/src/main/assembly/archive.xml',
    '[INFO] Building war: ' + basedir + '/target/mavenAssemblyPluginWithPropertiesTest-0.1-SNAPSHOT-production.war',
    
    '[INFO] Reading assembly descriptor: ' + basedir + '/src/main/assembly/archive.xml',
    '[INFO] Building war: ' + basedir + '/target/mavenAssemblyPluginWithPropertiesTest-0.1-SNAPSHOT-qa.war',
    '[INFO] BUILD SUCCESS',
])


def dev_war = new File (basedir, "/target/mavenAssemblyPluginWithPropertiesTest-0.1-SNAPSHOT-dev.war");
if (!dev_war.exists()) {
    throw new FileNotFoundException("The file " + dev_war + " does not exist!");
}

def test_war = new File (basedir, "/target/mavenAssemblyPluginWithPropertiesTest-0.1-SNAPSHOT-test.war");
if (!test_war.exists()) {
    throw new FileNotFoundException("The file " + test_war + " does not exist!");
}

def production_war = new File (basedir, "/target/mavenAssemblyPluginWithPropertiesTest-0.1-SNAPSHOT-production.war");
if (!production_war.exists()) {
    throw new FileNotFoundException("The file " + production_war + " does not exist!");
}

def qa_war = new File (basedir, "/target/mavenAssemblyPluginWithPropertiesTest-0.1-SNAPSHOT-qa.war");
if (!qa_war.exists()) {
    throw new FileNotFoundException("The file " + qa_war + " does not exist!");
}

def dev_war_properties = getInformation(dev_war)
//println "DevWar: " + dev_war_properties

if (!dev_war_properties.equals("server=http://www.dev-server.com")) {
    throw new IllegalArgumentException("The server.properties does not contain the correct contents.")
}

def test_war_properties = getInformation(test_war)
//println "TestWar: " + test_war_properties

if (!test_war_properties.equals("server=http://www.test-server.com")) {
    throw new IllegalArgumentException("The server.properties does not contain the correct contents.")
}

def production_war_properties = getInformation(production_war)
//println "ProdWar: " + production_war_properties

if (!production_war_properties.equals("server=http://www.production-server.com")) {
    throw new IllegalArgumentException("The server.properties does not contain the correct contents.")
}

def qa_war_properties = getInformation(qa_war)
//println "QAWar: " + qa_war_properties

if (!qa_war_properties.equals("server=http://www.qa-server.com")) {
    throw new IllegalArgumentException("The server.properties does not contain the correct contents.")
}

return true;
