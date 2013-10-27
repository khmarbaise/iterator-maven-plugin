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

t.checkExistenceAndContentOfAFile(logFileInput, [
	'[INFO] --- iterator-maven-plugin:' + projectVersion + ':executor (default) @ directoryIteratorTest ---',
	'[INFO] ------ (first) com.soebes.maven.plugins:maven-echo-plugin:0.1:echo',
	'[INFO] This is a message: first',
	'[INFO] ------ (second) com.soebes.maven.plugins:maven-echo-plugin:0.1:echo',
	'[INFO] This is a message: second',
	'[INFO] ------ (third) com.soebes.maven.plugins:maven-echo-plugin:0.1:echo',
	'[INFO] This is a message: third',
	'[INFO] BUILD SUCCESS',
])

return true;
