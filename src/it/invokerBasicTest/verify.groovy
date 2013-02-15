import java.io.*
import java.util.*


t = new IntegrationBase()


def getProjectVersion() {
	def pom = new XmlSlurper().parse(new File(basedir, 'pom.xml'))
   
	  def allPlugins = pom.build.plugins.plugin;
   
	  def configurationMavenPlugin = allPlugins.find {
		  item -> item.groupId.equals("com.soebes.maven.plugins") && item.artifactId.equals("itexin-maven-plugin");
	  }
	  
	  return configurationMavenPlugin.version;
}
   
def projectVersion = getProjectVersion();
   
println "Project version: ${projectVersion}"
   

def buildLogFile = new File( basedir, "build.log");


t.checkExistenceAndContentOfAFile(buildLogFile, [
	'[INFO] mvn one',
	'[INFO] [INFO] Building itexin-maven-plugin InvokerBasicTest-1 0.1-SNAPSHOT',
	'[INFO] mvn two',
	'[INFO] [INFO] Building itexin-maven-plugin InvokerBasicTest-2 0.1-SNAPSHOT',
	'[INFO] mvn three',
	'[INFO] [INFO] Building itexin-maven-plugin InvokerBasicTest-3 0.1-SNAPSHOT',
])


return true;
