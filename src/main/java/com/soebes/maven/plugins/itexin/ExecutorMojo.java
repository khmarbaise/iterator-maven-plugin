package com.soebes.maven.plugins.itexin;

import java.util.List;
import java.util.Map;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.twdata.maven.mojoexecutor.MojoExecutor;
import org.twdata.maven.mojoexecutor.PlexusConfigurationUtils;

/**
 * Executor will execute a given plugin by iterating through the given items.
 * 
 * @author Karl-Heinz Marbaise <a href="mailto:kama@soebes.de">kama@soebes.de</a>
 * 
 */
@Mojo(name = "executor", defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true, threadSafe = true)
public class ExecutorMojo extends AbstractItExInMojo {

    @Parameter(required = true)
    private List<PluginExecutor> pluginExecutors;

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

    /**
     * This will copy the configuration from src to the result whereas the
     * placeholder will be replaced with the current value.
     * 
     */
    private PlexusConfiguration copyConfiguration(PlexusConfiguration src, String iteratorName, String value) {

        XmlPlexusConfiguration dom = new XmlPlexusConfiguration(src.getName());

        if (src.getValue() != null) {
            if (src.getValue().contains(iteratorName)) {
                dom.setValue(src.getValue().replaceAll(iteratorName, value));
            } else {
                dom.setValue(src.getValue());
            }
        } else {
            dom.setValue(src.getValue(null));
        }

        for (String attributeName : src.getAttributeNames()) {
            dom.setAttribute(attributeName, src.getAttribute(attributeName, null));
        }

        for (PlexusConfiguration child : src.getChildren()) {
            dom.addChild(copyConfiguration(child, iteratorName, value));
        }

        return dom;
    }


    /**
     * 
     * This method will give back the plugin information which has been given
     * as parameter in case of an not existing pluginManagement section or
     * if the version of the plugin is already defined.
     * Otherwise the version will be got from the pluginManagement
     * area if the plugin can be found in it.
     * @param plugin The plugin which should be checked against the pluginManagement area.
     * @return The plugin version. If the plugin version is not defined it means
     *  that neither the version has been defined by the pluginManagement section
     *  nor by the user itself.
     */
    private Plugin getPluginVersionFromPluginManagement(Plugin plugin) {

        if (!isPluginManagementDefined()) {
            return plugin;
        }
        
        if (isPluginVersionDefined(plugin)) {
            return plugin;
        }
        
        Map<String, Plugin> plugins = mavenProject.getPluginManagement().getPluginsAsMap();

        Plugin result = plugins.get(plugin.getKey());
        if (result == null) {
            return plugin;
        } else {
            return result;
        }
    }


    private boolean isPluginVersionDefined(Plugin plugin) {
        return plugin.getVersion() != null;
    }

    private boolean isPluginManagementDefined() {
        return mavenProject.getPluginManagement() != null;
    }

    public void execute() throws MojoExecutionException {
        if (isItemsNull() && isContentNull()) {
            throw new MojoExecutionException("You have to use at least one. Either items element or content element!");
        }

        if (isItemsSet() && isContentSet()) {
            throw new MojoExecutionException("You can use only one element. Either items element or content element but not both!");
        }

        for (String item : getItems()) {
            for (PluginExecutor pluginExecutor : pluginExecutors) {

                Plugin executePlugin = getPluginVersionFromPluginManagement(pluginExecutor.getPlugin());
                if (executePlugin.getVersion() == null) {
                    throw new MojoExecutionException("Unknown plugin version. You have to define the version either directly or via pluginManagement.");
                }

                getLog().debug("Configuration(before): " + pluginExecutor.getConfiguration().toString());
                PlexusConfiguration plexusConfiguration = copyConfiguration(pluginExecutor.getConfiguration(), getPlaceHolder(), item);
                getLog().debug("plexusConfiguration(after): " + plexusConfiguration.toString());
                
                createLogOutput(pluginExecutor, executePlugin);
                
                // Put the value of the current iteration into the properties
                mavenProject.getProperties().put(getIteratorName(), item);
                
                MojoExecutor.executeMojo(executePlugin, pluginExecutor.getGoal(), PlexusConfigurationUtils.toXpp3Dom(plexusConfiguration),
                        MojoExecutor.executionEnvironment(mavenProject, mavenSession, pluginManager));
                
            }

        }
    }


	/**
	 * Will create the output during the executions for the plugins
	 * like <code>groupId:artifactId:version:goal</code>.
	 * @param pluginExecutor
	 * @param executePlugin
	 */
	private void createLogOutput(PluginExecutor pluginExecutor, Plugin executePlugin) {
		StringBuilder sb = new StringBuilder("------ ");
		sb.append(executePlugin.getKey());
		sb.append(":");
		sb.append(executePlugin.getVersion());
		sb.append(":");
		sb.append(pluginExecutor.getGoal());
		getLog().info(sb.toString());
	}

    public MavenProject getMavenProject() {
        return mavenProject;
    }

    public void setMavenProject(MavenProject mavenProject) {
        this.mavenProject = mavenProject;
    }

    public MavenSession getMavenSession() {
        return mavenSession;
    }

    public void setMavenSession(MavenSession mavenSession) {
        this.mavenSession = mavenSession;
    }

}
