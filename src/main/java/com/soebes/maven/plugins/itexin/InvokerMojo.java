package com.soebes.maven.plugins.itexin;

import java.io.File;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;

/**
 * Invoker will execute a maven by iterating through the given items.
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
    
    private Invoker invoker;


    public void execute() throws MojoExecutionException {
        if (isItemsNull() && isContentNull()) {
            throw new MojoExecutionException("You have to use at least one. Either items element or content element!");
        }

        if (isItemsSet() && isContentSet()) {
            throw new MojoExecutionException("You can use only one element. Either items element or content element but not both!");
        }


        for (String item : getItems()) {
            // Call mvn ....
//            mavenCall(item);
        }
    }

    /**
     * The list of goals which will be called during
     * each invocation of Maven.
     * 
     */
    @Parameter(defaultValue = "clean")
    private List<String> goals;
    
    private void mavenCall(String item) throws MavenInvocationException {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setBaseDirectory( new File("./") );
        request.setInteractive( false );
        request.setGoals( goals );
 
//        request.setMavenOpts(arg0);

        InvocationResult result = invoker.execute( request );
        
        if ( result.getExitCode() != 0 )
        {
            if ( result.getExecutionException() != null )
            {
//                throw new PublishException( "Failed to publish site.",
//                                            result.getExecutionException() );
            }
            else
            {
//                throw new PublishException( "Failed to publish site. Exit code: " + 
//                                             result.getExitCode() );
            }
        }    }
}
