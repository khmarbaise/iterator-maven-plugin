import java.io.*
import java.util.*


t = new IntegrationBase()


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
    '[INFO] ------ org.apache.maven.plugins:maven-assembly-plugin:2.4:single',
    '[INFO] Reading assembly descriptor: ' + basedir + '/test.xml',
    '[INFO] Building war: ' + basedir + t.convertPathIntoPlatform('/target/mavenAssemblyTest-0.1-SNAPSHOT-test.war'),
    '[INFO] Reading assembly descriptor: ' + basedir + '/dev.xml',
    '[INFO] Building war: ' + basedir + t.convertPathIntoPlatform('/target/mavenAssemblyTest-0.1-SNAPSHOT-dev.war'),
    '[INFO] Reading assembly descriptor: ' + basedir + '/prod.xml',
    '[INFO] Building war: ' + basedir + t.convertPathIntoPlatform('/target/mavenAssemblyTest-0.1-SNAPSHOT-production.war'),
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
def prod_war = new File (basedir, "/target/mavenAssemblyTest-0.1-SNAPSHOT-production.war");
if (!prod_war.exists()) {
    throw new FileNotFoundException("The file " + prod_war + " does not exist!");
}


return true;
