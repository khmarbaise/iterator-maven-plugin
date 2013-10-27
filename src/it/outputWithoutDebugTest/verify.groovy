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
  '[INFO] --- itexin-maven-plugin:' +projectVersion + ':executor (default) @ basic-test ---',
  '[INFO] ------ (one) com.soebes.maven.plugins:maven-echo-plugin:0.1:echo',
  '[INFO] This is a message: one',
  '[INFO] ------ (two) com.soebes.maven.plugins:maven-echo-plugin:0.1:echo',
  '[INFO] This is a message: two',
  '[INFO] ------ (three) com.soebes.maven.plugins:maven-echo-plugin:0.1:echo',
  '[INFO] This is a message: three',
  '[INFO] BUILD SUCCESS',
])


return true;
