package com.soebes.maven.plugins.itexin;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Invoker will execute a maven by iterating throught the given items.
 * 
 * @author Karl-Heinz Marbaise <a href="mailto:kama@soebes.de">kama@soebes.de</a>
 *
 */
@Mojo(name = "invoker", defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true, threadSafe = true)
public class InvokerMojo extends AbstractItExInMojo {

    /**
     * The project currently being build.
     * 
     */
    @Parameter(required = true, readonly = true, defaultValue = "${project}")
    private MavenProject mavenProject;

    /**
     * The current Maven session.
     * 
     */
    @Parameter(required = true, readonly = true, defaultValue = "${session}")
    private MavenSession mavenSession;

    /**
     * The Maven BuildPluginManager component.
     * 
     */
    @Component
    private BuildPluginManager pluginManager;

    @Component
    // for Maven 3 only
    private PluginDescriptor pluginDescriptor;

    public void execute() throws MojoExecutionException {
	if (isItemsNull() && isContentNull()) {
	    throw new MojoExecutionException("You have to use at least one. Either items element or content element!");
	}

	if (isItemsSet() && isContentSet()) {
	    throw new MojoExecutionException("You can use only one element. Either items element or content element but not both!");
	}

	for (String item : getItems()) {
	    //Call mvn ....
	}
    }

}
