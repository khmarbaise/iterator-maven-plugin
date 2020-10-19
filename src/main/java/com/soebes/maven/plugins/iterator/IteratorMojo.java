package com.soebes.maven.plugins.iterator;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.soebes.maven.plugins.iterator.resolver.ItemResolverType;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.InvalidPluginDescriptorException;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.PluginConfigurationException;
import org.apache.maven.plugin.PluginDescriptorParsingException;
import org.apache.maven.plugin.PluginManagerException;
import org.apache.maven.plugin.PluginResolutionException;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.reporting.exec.MavenPluginManagerHelper;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomUtils;

/**
 * Executor will execute a given plugin by iterating through the given items.
 *
 * @author Karl-Heinz Marbaise <a href="mailto:khmarbaise@apache.org">khmarbaise@apache.org</a>
 */
@Mojo( name = "iterator", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.TEST, requiresProject = true, threadSafe = true )
public class IteratorMojo
    extends AbstractIteratorMojo
{

    /**
     * By using the pluginExecutors you can define a list of plugins which will be executed during executor.
     * 
     * <pre>
     * {@code
     * <pluginExecutors>
     *   <pluginExecutor>
     *     ..Plugin
     *   </pluginExecutor>
     * </pluginExecutors>
     * }
     * </pre>
     * 
     * A plugin must be defined by using the plugin tag. You can omit the version tag if you have defined the plugin's
     * version in the pluginManagement section.
     * 
     * <pre>
     * {@code
     * <plugin>
     *   <groupId>..</groupId>
     *   <artifactId>..</artifactId>
     *   <version>..</version>
     * </plugin>
     * }
     * </pre>
     * 
     * Furthermore you need to define the goal of the given plugin by using the tag:
     * 
     * <pre>
     * {@code
     * <goal>...</goal>
     * }
     * </pre>
     * 
     * And finally you need to define the configuration for the plugin by using the following:
     * 
     * <pre>
     * {@code
     * <configuration>
     *   Plugin Configuration
     * </configuration>
     * }
     * </pre>
     */
    @Parameter( required = true )
    private List<PluginExecutor> pluginExecutors;

    /**
     * This is the helper class to support Maven 3.1 and before.
     */
    @Component
    protected MavenPluginManagerHelper mavenPluginManagerHelper;

    /**
     * The Maven BuildPluginManager component.
     */
    @Component
    private BuildPluginManager pluginManager;

    @Parameter( defaultValue = "${plugin}", required = true, readonly = true )
    private PluginDescriptor pluginDescriptor;

    /**
     * This will copy the configuration from <b>src</b> to the result whereas the placeholder will be replaced with the
     * current value.
     */
    private PlexusConfiguration copyConfiguration( Xpp3Dom src, PlaceHolderPattern iteratorName, String value )
        throws MojoExecutionException
    {

        XmlPlexusConfiguration dom = new XmlPlexusConfiguration( src.getName() );

        if ( src.getValue() == null )
        {
            dom.setValue( src.getValue() );
        }
        else
        {
            if ( isIncludeFiles() )
            {
                dom.setValue( iteratorName.replaceAll( src.getValue(), value ) );
            }
            else
            {
                dom.setValue( iteratorName.replace( src.getValue(), value, ItemResolverType.DEFAULT ) );
            }
        }

        for ( String attributeName : src.getAttributeNames() )
        {
            dom.setAttribute( attributeName, src.getAttribute( attributeName ) );
        }

        for ( Xpp3Dom child : src.getChildren() )
        {
            dom.addChild( copyConfiguration( child, iteratorName, value ) );
        }

        return dom;
    }

    /**
     * This method will give back the plugin information which has been given as parameter in case of an not existing
     * pluginManagement section or if the version of the plugin is already defined. Otherwise the version will be got
     * from the pluginManagement area if the plugin can be found in it.
     * 
     * @param plugin The plugin which should be checked against the pluginManagement area.
     * @return The plugin version. If the plugin version is not defined it means that neither the version has been
     *         defined by the pluginManagement section nor by the user itself.
     */
    private Plugin getPluginVersionFromPluginManagement( Plugin plugin )
    {

        if ( !isPluginManagementDefined() )
        {
            return plugin;
        }

        if ( isPluginVersionDefined( plugin ) )
        {
            return plugin;
        }

        Map<String, Plugin> plugins = getMavenProject().getPluginManagement().getPluginsAsMap();

        Plugin result = plugins.get( plugin.getKey() );
        if ( result == null )
        {
            return plugin;
        }
        else
        {
            return result;
        }
    }

    private boolean isPluginVersionDefined( Plugin plugin )
    {
        return plugin.getVersion() != null;
    }

    private boolean isPluginManagementDefined()
    {
        return getMavenProject().getPluginManagement() != null;
    }


    @Override
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {

        if ( isSkip() )
        {
            getLog().info( "Skip by user request." );
            return;
        }

        if ( isNoneSet() )
        {
            getLog().warn("Neither items, itemsWithProperties, content nor folder have been set.");
            return;
        }

        if ( isMoreThanOneSet() )
        {
            throw new MojoExecutionException( "You can use only one element. "
                + "Either items, itemsWithProperties, content or folder element but not more than one of them." );
        }

        List<Exception> exceptions = Collections.synchronizedList( new ArrayList<>() );
        for ( ItemWithProperties item : getItemsConverted() )
        {
            invokePipeline( exceptions, item );
        }

        if ( !exceptions.isEmpty() )
        {
            for ( Exception exception : exceptions )
            {
                getLog().error( exception );
            }
            throw new MojoExecutionException( "Failures during iteration" );
        }
    }


    private synchronized void invokePipeline(
        List<Exception> exceptions,
        ItemWithProperties item )
        throws MojoExecutionException, MojoFailureException
    {
        for ( PluginExecutor pluginExecutor : pluginExecutors )
        {
            try
            {
                handlePluginExecution( item, pluginExecutor );
            }
            catch ( MojoExecutionException e )
            {
                if ( isFailAtEnd() )
                {
                    exceptions.add( e );
                }
                else
                {
                    throw e;
                }
            }
            catch ( MojoFailureException e )
            {
                // An Failure will stop the iteration under any circumstances.
                throw e;
            }
        }
    }

    /**
     * This will inject the iteration into the current properties and furthermore the properties if they have given and
     * execute the plugin with the appropriate context.
     * 
     * @param item The items which are using for the current iteration.
     * @param pluginExecutor The {@link PluginExecutor}
     * @throws MojoExecutionException in case of an error.
     * @throws MojoFailureException failure during the run of a plugin.
     */
    private void handlePluginExecution( ItemWithProperties item, PluginExecutor pluginExecutor )
        throws MojoExecutionException, MojoFailureException
    {
        Plugin executePlugin = getPluginVersionFromPluginManagement( pluginExecutor.getPlugin() );
        if ( executePlugin.getVersion() == null )
        {
            throw new MojoExecutionException(
                String.format(
                    "Unknown plugin version for %s:%s. "
                        + "You have to define the version either directly or via pluginManagement.",
                    executePlugin.getGroupId(),
                    executePlugin.getArtifactId() ) );
        }

        Xpp3Dom resultConfiguration = handlePluginConfigurationFromPluginManagement( pluginExecutor, executePlugin );

        PlexusConfiguration plexusConfiguration =
            copyConfiguration( resultConfiguration, getItemPlaceHolderPattern(), item.getName() );

        createLogOutput( pluginExecutor, executePlugin, item.getName() );

        // Put the value of the current iteration into the current context.
        getMavenProject().getProperties().put( getIteratorName(), item.getName() );

        if ( item.hasProperties() )
        {
            // Add all properties to the context.
            for ( Entry<Object, Object> entry : item.getProperties().entrySet() )
            {
                getMavenProject().getProperties().put( entry.getKey(), entry.getValue() );
            }
        }

        try
        {
            executeMojo( executePlugin, pluginExecutor.getGoal(), toXpp3Dom( plexusConfiguration ) );
        }
        catch ( MojoExecutionException | MojoFailureException e )
        {
            throw e;
        }
        catch ( PluginResolutionException e )
        {
            getLog().error( "PluginresourceException:", e );
            throw new MojoExecutionException( "PluginRescourceException", e );
        }
        catch ( PluginDescriptorParsingException e )
        {
            getLog().error( "PluginDescriptorParsingException:", e );
            throw new MojoExecutionException( "PluginDescriptorParsingException", e );
        }
        catch ( InvalidPluginDescriptorException e )
        {
            getLog().error( "InvalidPluginDescriptorException:", e );
            throw new MojoExecutionException( "InvalidPluginDescriptorException", e );
        }
        catch ( PluginConfigurationException e )
        {
            getLog().error( "PluginConfigurationException:", e );
            throw new MojoExecutionException( "PluginConfigurationException", e );
        }
        catch ( PluginManagerException e )
        {
            getLog().error( "PluginManagerException:", e );
            throw new MojoExecutionException( "PluginManagerException", e );
        }
        finally
        {
            // Remove the old key from context.
            getMavenProject().getProperties().remove( getIteratorName() );
            if ( item.hasProperties() )
            {
                // Remove all old properties from context.
                for ( Object entry : item.getProperties().keySet() )
                {
                    getMavenProject().getProperties().remove( entry );
                }
            }

        }
    }

    private Xpp3Dom handlePluginConfigurationFromPluginManagement( PluginExecutor pluginExecutor, Plugin executePlugin )
    {
        Xpp3Dom resultConfiguration = toXpp3Dom( pluginExecutor.getConfiguration() );
        getLog().debug( "Configuration: " + resultConfiguration.toString() );
        if ( executePlugin.getConfiguration() != null )
        {
            Xpp3Dom x = (Xpp3Dom) executePlugin.getConfiguration();
            resultConfiguration = Xpp3DomUtils.mergeXpp3Dom( toXpp3Dom( pluginExecutor.getConfiguration() ), x );

            getLog().debug( "ConfigurationExecutePlugin: " + executePlugin.getConfiguration().toString() );

        }
        return resultConfiguration;
    }

    /**
     * Taken from MojoExecutor of Don Brown. Make it working with Maven 3.1.
     * 
     * @param plugin
     * @param goal
     * @param configuration
     * @throws MojoExecutionException
     * @throws PluginResolutionException
     * @throws PluginDescriptorParsingException
     * @throws InvalidPluginDescriptorException
     * @throws PluginManagerException
     * @throws PluginConfigurationException
     * @throws MojoFailureException
     */
    private void executeMojo( Plugin plugin, String goal, Xpp3Dom configuration )
        throws MojoExecutionException, PluginResolutionException, PluginDescriptorParsingException,
        InvalidPluginDescriptorException, MojoFailureException, PluginConfigurationException, PluginManagerException
    {

        if ( configuration == null )
        {
            throw new NullPointerException( "configuration may not be null" );
        }

        PluginDescriptor pluginDescriptor = getPluginDescriptor( plugin );

        MojoDescriptor mojoDescriptor = pluginDescriptor.getMojo( goal );
        if ( mojoDescriptor == null )
        {
            throw new MojoExecutionException(
                String.format( "Could not find goal '%s' in plugin %s:%s:%s",
                    goal,
                    plugin.getGroupId(),
                    plugin.getArtifactId(),
                    plugin.getVersion() ) );
        }

        MojoExecution exec = mojoExecution( mojoDescriptor, configuration );
        pluginManager.executeMojo( getMavenSession(), exec );
    }

    private PluginDescriptor getPluginDescriptor( Plugin plugin )
        throws PluginResolutionException, PluginDescriptorParsingException, InvalidPluginDescriptorException
    {
        return mavenPluginManagerHelper.getPluginDescriptor( plugin, getMavenSession() );
    }

    private MojoExecution mojoExecution( MojoDescriptor mojoDescriptor, Xpp3Dom configuration )
    {
        Xpp3Dom resultConfiguration =
            Xpp3DomUtils.mergeXpp3Dom( configuration, toXpp3Dom( mojoDescriptor.getMojoConfiguration() ) );
        return new MojoExecution( mojoDescriptor, resultConfiguration );
    }

    /**
     * Taken from MojoExecutor of Don Brown. Converts PlexusConfiguration to a Xpp3Dom.
     * 
     * @param config the PlexusConfiguration. Must not be {@code null}.
     * @return the Xpp3Dom representation of the PlexusConfiguration
     */
    public Xpp3Dom toXpp3Dom( PlexusConfiguration config )
    {
        Xpp3Dom result = new Xpp3Dom( config.getName() );
        result.setValue( config.getValue( null ) );
        for ( String name : config.getAttributeNames() )
        {
            result.setAttribute( name, config.getAttribute( name ) );
        }
        for ( PlexusConfiguration child : config.getChildren() )
        {
            result.addChild( toXpp3Dom( child ) );
        }
        return result;
    }

    /**
     * Will create the output during the executions for the plugins like <code>groupId:artifactId:version:goal</code>.
     * 
     * @param pluginExecutor
     * @param executePlugin
     */
    private void createLogOutput( PluginExecutor pluginExecutor, Plugin executePlugin, String item )
    {
        StringBuilder sb = new StringBuilder( "------ " );
        sb.append( "(" );
        sb.append( item );
        sb.append( ") " );
        sb.append( executePlugin.getKey() );
        sb.append( ":" );
        sb.append( executePlugin.getVersion() );
        sb.append( ":" );
        sb.append( pluginExecutor.getGoal() );
        getLog().info( sb.toString() );
    }

}
