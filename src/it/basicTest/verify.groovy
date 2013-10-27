import java.io.*
import java.util.*


t = new IntegrationBase()


def getProjectVersion() {
	def pom = new XmlSlurper().parse(new File(basedir, 'pom.xml'))
   
	  def allPlugins = pom.build.plugins.plugin;
   
	  def configurationMavenPlugin = allPlugins.find {
		  item -> item.groupId.equals("com.soebes.maven.plugins") && item.artifactId.equals("iterator-maven-plugin");
	  }
	  
	  return configurationMavenPlugin.version;
}
   
def projectVersion = getProjectVersion();
   
println "Project version: ${projectVersion}"
   

def buildLogFile = new File( basedir, "build.log");


t.checkExistenceAndContentOfAFile(buildLogFile, [
  '[INFO] --- iterator-maven-plugin:' +projectVersion + ':executor (default) @ basic-test ---',
  '[INFO] ------ (eins) com.soebes.maven.plugins:maven-echo-plugin:0.1:echo',
  '[INFO] This is a message: eins',
  '[INFO] ------ (zwei) com.soebes.maven.plugins:maven-echo-plugin:0.1:echo',
  '[INFO] This is a message: zwei',
  '[INFO] ------ (drei) com.soebes.maven.plugins:maven-echo-plugin:0.1:echo',
  '[INFO] This is a message: drei',
  '[INFO] BUILD SUCCESS',
])


return true;
