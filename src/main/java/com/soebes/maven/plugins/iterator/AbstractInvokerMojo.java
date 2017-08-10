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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.InvocationRequest;

/**
 * @author Karl-Heinz Marbaise <a href="mailto:khmarbaise@apache.org">khmarbaise@apache.org</a>
 */
public abstract class AbstractInvokerMojo
    extends AbstractIteratorMojo
{

    /**
     * The list of goals which will be called during each invocation of Maven. TODO: Change the name, cause we are
     * calling life cycle phases instead of goal but we can if we like things like site:site ?
     */
    @Parameter
    private List<String> goals = Collections.singletonList( "clean" );

    /**
     * This will set <code>--batch-mode</code>
     */
    @Parameter( defaultValue = "false" )
    private boolean interactive;

    /**
     * This will <code>--also-make</code>
     */
    @Parameter( defaultValue = "false" )
    private boolean alsoMake;

    /**
     * This will activate <code>--also-make-dependents</code>
     */
    @Parameter( defaultValue = "false" )
    private boolean alsoMakeDependents;

    /**
     * Sets the path to the base directory of the POM for the Maven invocation. If {@link #getPomFile()} does not return
     * <code>null</code>, this setting only affects the working directory for the Maven invocation. Here you can use the
     * placeholder like: EXAMPLE
     */
    @Parameter
    private File baseDirectory;

    /**
     * <code>-Ddebug=true</code>
     */
    @Parameter( defaultValue = "false" )
    private boolean debug;

    /**
     * The valid values are {@link InvocationRequest#REACTOR_FAIL_AT_END}, {@link InvocationRequest#REACTOR_FAIL_FAST},
     * {@link InvocationRequest#REACTOR_FAIL_NEVER}.
     * 
     * @see {@link InvocationRequest#REACTOR_FAIL_AT_END}, {@link InvocationRequest#REACTOR_FAIL_FAST},
     *      {@link InvocationRequest#REACTOR_FAIL_NEVER}
     * @FIXME: Check if we need to create a setter which checks the valid values
     */
    @Parameter( defaultValue = InvocationRequest.REACTOR_FAIL_FAST )
    private String failureBehaviour;

    /**
     * Sets the checksum mode of the Maven invocation. Equivalent of {@code -c} or {@code --lax-checksums}, {@code -C}
     * or {@code --strict-checksums}
     * 
     * @param globalChecksumPolicy The checksum mode, must be one of {@link InvocationRequest#CHECKSUM_POLICY_WARN} and
     *            {@link InvocationRequest#CHECKSUM_POLICY_FAIL}.
     */
    @Parameter( defaultValue = InvocationRequest.CHECKSUM_POLICY_WARN )
    private String globalChecksumPolicy;

    /**
     * The path to the global settings for the Maven invocation or <code>null</code> to load the global settings from
     * the default location.
     */
    @Parameter
    private File globalSettingsFile;

    /**
     * If <code>null</code> the default location from <code>settings.xml</code> will be used.
     */
    @Parameter
    private File localRepositoryDirectory;

    /**
     * If <code>null</code>.
     */
    @Parameter
    private String mavenOpts;

    /**
     * If true <code>--no-plugin-updates</code> will be used.
     */
    @Parameter
    private boolean nonPluginUpdates;

    /**
     * <code>true</code> Maven will be executed in off-line mode ( <code>--offline</code>).
     */
    @Parameter
    private boolean offline;

    /**
     * The list of profiles.
     */
    @Parameter
    private List<String> profiles;

    /**
     * Sets the reactor project list. Equivalent of {@code -pl} or {@code --projects} projects the reactor project list
     */
    @Parameter
    private List<String> projects;

    /**
     * Sets the system properties for the Maven invocation, may be <code>null</code> if not set.
     */
    @Parameter
    private Properties properties;

    /**
     * Sets the recursion behavior of a reactor invocation. <em>Inverse</em> equivalent of {@code -N} and
     * {@code --non-recursive}
     */
    @Parameter( defaultValue = "false" )
    private boolean recursive;

    /**
     * Specify the reactor project to resume from.
     */
    @Parameter
    private String resumeFrom;

    /**
     * Indicates whether the environment variables of the current process should be propagated to the Maven invocation.
     * By default, the current environment variables are inherited by the new Maven invocation.
     */
    @Parameter( defaultValue = "true" )
    private boolean shellEnvironmentInherited;

    /**
     * Gets the exception output mode of the Maven invocation. By default, Maven will not print stack traces of build
     * exceptions.
     */
    @Parameter( defaultValue = "false" )
    private boolean showErrors;

    /**
     * The show version behaviour {@code -V} option.
     */
    @Parameter( defaultValue = "false" )
    private boolean showVersion;

    /**
     * {@code -T} option of Maven.
     */
    @Parameter
    protected String threads;

    /**
     * Set the path to the custom toolchains file
     */
    @Parameter
    private File toolchains;

    /**
     * Indicates whether Maven should enforce an update check for plugins and snapshots. By default, no update check is
     * performed {@code --update-snapshots}.
     */
    @Parameter( defaultValue = "false" )
    private boolean updateSnapshots;

    /**
     * Sets the path to the user settings for the Maven invocation.
     */
    @Parameter
    private File userSettings;
        
    private File getBaseDirectoryAfterPlaceHolderIsReplaced( String currentValue )
    {
        File baseDir = getBaseDirectory();
        if ( baseDir != null && baseDir.toString().contains( getPlaceHolder() ) )
        {
            baseDir = new File( baseDir.toString().replaceAll( getPlaceHolder(), currentValue ) );
        }
        return baseDir;
    }

    private List<String> replacePlaceholderInElements( String currentValue, List<String> goals )
    {
        List<String> result = new ArrayList<String>();
        for ( String string : goals )
        {
            if ( string.contains( getPlaceHolder() ) )
            {
                result.add( string.replaceAll( getPlaceHolder(), currentValue ) );
            }
            else
            {
                result.add( string );
            }
        }
        return result;
    }

    private List<String> getGoalsAfterPlaceHolderIsReplaced( String currentValue )
    {
        List<String> goals = getGoals();
        if ( goals == null )
        {
            return null;
        }

        List<String> result = replacePlaceholderInElements( currentValue, goals );
        return result;
    }

    private List<String> getProfilesAfterPlaceHolderIsReplaced( String currentValue )
    {
        List<String> profiles = getProfiles();
        if ( profiles == null )
        {
            return null;
        }

        List<String> result = replacePlaceholderInElements( currentValue, profiles );
        return result;
    }

    private List<String> getProjectsAfterPlaceHolderIsReplaced( String currentValue )
    {
        List<String> projects = getProjects();
        if ( projects == null )
        {
            return null;
        }

        List<String> result = replacePlaceholderInElements( currentValue, projects );
        return result;
    }

    protected InvocationRequest createAndConfigureAnInvocationRequest( ItemWithProperties currentValue )
    {
        InvocationRequest request = new DefaultInvocationRequest();

        request.setAlsoMake( isAlsoMake() );
        request.setAlsoMakeDependents( isAlsoMakeDependents() );
        request.setDebug( isDebug() );
        request.setFailureBehavior( getFailureBehaviour() );
        request.setGlobalChecksumPolicy( getGlobalChecksumPolicy() );
        request.setGlobalSettingsFile( getGlobalSettingsFile() );
        request.setInteractive( isInteractive() );

        request.setLocalRepositoryDirectory( getLocalRepositoryDirectory() );
        request.setMavenOpts( getMavenOpts() );
        request.setNonPluginUpdates( isNonPluginUpdates() );
        request.setOffline( isOffline() );

//        request.setProperties( properties )
//        ;
        // @TODO: Think about it.
        // request.setPomFile(pomFile);
        // @TODO: Think about it.
        // request.setPomFileName(pomFilename);

        // The following parameter do make sense to use a placeholder
        // base directory
        // cd @item@
        // mvn clean package
        request.setBaseDirectory( getBaseDirectoryAfterPlaceHolderIsReplaced( currentValue.getName() ) );
        // goals:
        // mvn plugin-name:@item@
        //
        request.setGoals( getGoalsAfterPlaceHolderIsReplaced( currentValue.getName() ) );
        // Profiles:
        // mvn -Pxyz-@item@ clean package
        // mvn -P@item@
        request.setProfiles( getProfilesAfterPlaceHolderIsReplaced( currentValue.getName() ) );
        // Projects:
        // mvn -pl xyz-@item@ clean package
        request.setProjects( getProjectsAfterPlaceHolderIsReplaced( currentValue.getName() ) );

        Properties props = getMergedProperties( currentValue );
        request.setProperties( props );

        request.setRecursive( isRecursive() );
        request.setResumeFrom( getResumeFrom() );
        request.setShellEnvironmentInherited( isShellEnvironmentInherited() );
        request.setShowErrors( isShowErrors() );
        request.setShowVersion( isShowVersion() );
        request.setThreads( getThreads() );
        request.setToolchainsFile( getToolchains() );
        request.setUpdateSnapshots( isUpdateSnapshots() );
        request.setUserSettingsFile( getUserSettings() );
        
        return request;
    }

    private Properties getMergedProperties( ItemWithProperties item ) 
    {
        Properties props = new Properties();
        
        if ( getProperties() != null ) 
        {
            props.putAll( getProperties() );
        }
        
        if ( item.getProperties() != null ) 
        {
            props.putAll( item.getProperties() );
        }
        
        Set<Object> relevantKeys = new HashSet<>(props.keySet());
        
        // Simply adding all project properties can cause issues with the command line character limit
        // Thus only explicitly "activated" properties are added (plugin, item and relevant system-properties)
        if ( getMavenProject().getProperties() != null ) 
        {
            relevantKeys.addAll( getMavenProject().getProperties().keySet() );
        }
                
        for ( Object key : relevantKeys ) 
        {
            String systemPropertyValue = System.getProperty( (String) key );

            if ( systemPropertyValue != null ) 
            {
                props.put( key, systemPropertyValue );
            }
        }
        
        return props;
    }

    public List<String> getGoals()
    {
        return goals;
    }

    public boolean isInteractive()
    {
        return interactive;
    }

    public void setInteractive( boolean interactive )
    {
        this.interactive = interactive;
    }

    public boolean isAlsoMake()
    {
        return alsoMake;
    }

    public void setAlsoMake( boolean alsoMake )
    {
        this.alsoMake = alsoMake;
    }

    public boolean isAlsoMakeDependents()
    {
        return alsoMakeDependents;
    }

    public void setAlsoMakeDependents( boolean alsoMakeDependents )
    {
        this.alsoMakeDependents = alsoMakeDependents;
    }

    public File getBaseDirectory()
    {
        return baseDirectory;
    }

    public void setBaseDirectory( File baseDirectory )
    {
        this.baseDirectory = baseDirectory;
    }

    public boolean isDebug()
    {
        return debug;
    }

    public void setDebug( boolean debug )
    {
        this.debug = debug;
    }

    public String getFailureBehaviour()
    {
        return failureBehaviour;
    }

    public void setFailureBehaviour( String failureBehaviour )
    {
        this.failureBehaviour = failureBehaviour;
    }

    public String getGlobalChecksumPolicy()
    {
        return globalChecksumPolicy;
    }

    public void setGlobalChecksumPolicy( String globalChecksumPolicy )
    {
        this.globalChecksumPolicy = globalChecksumPolicy;
    }

    public File getGlobalSettingsFile()
    {
        return globalSettingsFile;
    }

    public void setGlobalSettingsFile( File globalSettingsFile )
    {
        this.globalSettingsFile = globalSettingsFile;
    }

    public File getLocalRepositoryDirectory()
    {
        return localRepositoryDirectory;
    }

    public void setLocalRepositoryDirectory( File localRepositoryDirectory )
    {
        this.localRepositoryDirectory = localRepositoryDirectory;
    }

    public String getMavenOpts()
    {
        return mavenOpts;
    }

    public void setMavenOpts( String mavenOpts )
    {
        this.mavenOpts = mavenOpts;
    }

    public boolean isNonPluginUpdates()
    {
        return nonPluginUpdates;
    }

    public void setNonPluginUpdates( boolean nonPluginUpdates )
    {
        this.nonPluginUpdates = nonPluginUpdates;
    }

    public boolean isOffline()
    {
        return offline;
    }

    public void setOffline( boolean offline )
    {
        this.offline = offline;
    }

    public List<String> getProfiles()
    {
        return profiles;
    }

    public void setProfiles( List<String> profiles )
    {
        this.profiles = profiles;
    }

    public List<String> getProjects()
    {
        return projects;
    }

    public void setProjects( List<String> projects )
    {
        this.projects = projects;
    }

    public Properties getProperties()
    {
        return properties;
    }

    public void setProperties( Properties properties )
    {
        this.properties = properties;
    }

    public boolean isRecursive()
    {
        return recursive;
    }

    public void setRecursive( boolean recursive )
    {
        this.recursive = recursive;
    }

    public String getResumeFrom()
    {
        return resumeFrom;
    }

    public void setResumeFrom( String resumeFrom )
    {
        this.resumeFrom = resumeFrom;
    }

    public boolean isShellEnvironmentInherited()
    {
        return shellEnvironmentInherited;
    }

    public void setShellEnvironmentInherited( boolean shellEnvironmentInherited )
    {
        this.shellEnvironmentInherited = shellEnvironmentInherited;
    }

    public boolean isShowErrors()
    {
        return showErrors;
    }

    public void setShowErrors( boolean showErrors )
    {
        this.showErrors = showErrors;
    }

    public boolean isShowVersion()
    {
        return showVersion;
    }

    public void setShowVersion( boolean showVersion )
    {
        this.showVersion = showVersion;
    }

    public String getThreads()
    {
        return threads;
    }

    public File getToolchains()
    {
        return toolchains;
    }

    public void setToolchains( File toolchains )
    {
        this.toolchains = toolchains;
    }

    public boolean isUpdateSnapshots()
    {
        return updateSnapshots;
    }

    public void setUpdateSnapshots( boolean updateSnapshots )
    {
        this.updateSnapshots = updateSnapshots;
    }

    public File getUserSettings()
    {
        return userSettings;
    }

    public void setUserSettings( File userSettings )
    {
        this.userSettings = userSettings;
    }

    public void setGoals( List<String> goals )
    {
        this.goals = goals;
    }

}
