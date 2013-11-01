package com.soebes.maven.plugins.iterator;

import java.io.File;
import java.net.URL;

/**
 * @author Karl-Heinz Marbaise
 * 
 */
public class UnitTestBase {

    /**
     * This method will give you back the filename incl. the absolute path name
     * to the resource. If the resource does not exist it will give you back the
     * resource name incl. the path.
     * 
     * It will give you back an absolute path incl. the name which is in the
     * same directory as the the class you've called it from.
     * 
     * @param name
     * @return The resource as a String.
     */
    public String getResource(String name) {
	URL url = this.getClass().getResource(name);
	if (url != null) {
	    return url.getFile();
	} else {
	    // We have a file which does not exists
	    // We got the path
	    url = this.getClass().getResource(".");
	    return url.getFile() + name;
	}
    }

    /**
     * Return the base directory of the project.
     * 
     * @return The base directory.
     */
    public String getMavenBaseDir() {
	// basedir is defined by Maven
	// but the above will not work under Eclipse.
	// So there I'm using user.dir
	return System.getProperty("basedir", System.getProperty("user.dir", "."));
    }

    public File getMavenBaseDirFile() {
	return new File(getMavenBaseDir());
    }

    public File getTargetDirectoryFile() {
	return new File(getMavenBaseDirFile(), "target");
    }

    public File getSrcDirectoryFile() {
	return new File(getMavenBaseDirFile(), "src");
    }

    public File getSrcTestDirectoryFile() {
	return new File(getSrcDirectoryFile(), "test");
    }

    public File getSrcMainDirectoryFile() {
	return new File(getSrcDirectoryFile(), "main");
    }

    public File getSrcTestResourcesDirectory() {
	return new File(getSrcTestDirectoryFile(), "resources");
    }

}
