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
import java.util.List;

import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;

/**
 * Invoker will execute an Maven instance by iterating through the given items.
 * 
 * @author Karl-Heinz Marbaise <a href="mailto:khmarbaise@apache.org">khmarbaise@apache.org</a>
 */
@Mojo( name = "invoker", defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true, threadSafe = true )
public class InvokerMojo
    extends AbstractInvokerMojo
{

    /**
     * The Maven BuildPluginManager component.
     */
    @Component
    private BuildPluginManager pluginManager;

    @Parameter( defaultValue = "${plugin}", required = true, readonly = true )
    private PluginDescriptor pluginDescriptor;

    @Parameter
    private File workingDirectory;

    @Component
    private Invoker invoker;

    /**
     * Possibly to multithreading..
     * 
     * @throws MojoFailureException in case of failure.
     */
    // private int numberOfThreads;

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
            throw new MojoExecutionException( "You have to use at least one. " + "Either items, "
                + "itemsWithProperties, content or folder element!" );
        }

        if ( isMoreThanOneSet() )
        {
            throw new MojoExecutionException( "You can use only one element. "
                + "Either items, itemsWithProperties, content or folder element but not more than one of them." );
        }

        File localRepository = new File( getMavenSession().getSettings().getLocalRepository() );

        invoker.setLocalRepositoryDirectory( localRepository );
        // invoker.setOutputHandler(outputHandler);
        // TODO: Check how it looks if we will use the invokerLogger?
        // invoker.setLogger();

        // getLog().info("local repository: " + localRepository);
        // // getLog().isDebugEnabled()
        // getLog().info("Invoker:" + invoker);

        List<Exception> exceptions = new ArrayList<>();

        for ( ItemWithProperties item : getItemsConverted() )
        {
            try
            {
                createLogOutput( item );
                InvocationResult result = mavenCall( item );

                if ( result.getExitCode() == 0 )
                {
                    getLog().info( "------ Maven call was Ok." );
                    continue;
                }

                getLog().error( "------ Maven call was NOT Ok. for iteration " + item.getName() + " ( return code: " + result.getExitCode() + " )" );
                if ( result.getExecutionException() != null )
                {
                    getLog().error( result.getExecutionException().getMessage(),
                                    result.getExecutionException().getCause() );
                }

                String failureMessage =
                    "Maven call failed with return code " + result.getExitCode() + " for iteration: " + item.getName();

                if ( isFailAtEnd() )
                {
                    exceptions.add( new RuntimeException( failureMessage ) );
                }
                else
                {
                    throw new MojoFailureException( failureMessage );
                }

            }
            catch ( MavenInvocationException e )
            {
                // This will stop any iteration.
                getLog().error( "------ ***** Command line options are wrong:", e );
                throw new MojoExecutionException( "Command line options are wrong:", e );
            }
        }

        if ( !exceptions.isEmpty() )
        {
            for ( Exception exception : exceptions )
            {
                getLog().error( exception );
            }
            throw new MojoExecutionException( "Failures during iterations." );
        }
    }

    private File getWorkingDirectoryAfterPlaceHolderIsReplaced( ItemWithProperties currentValue )
    {
        File baseDir = getWorkingDirectory();
        if ( baseDir != null && baseDir.toString().contains( getPlaceHolder() ) )
        {
            baseDir = new File( baseDir.toString().replaceAll( getPlaceHolder(), currentValue.getName() ) );
        }
        return baseDir;
    }

    private InvocationResult mavenCall( ItemWithProperties item )
        throws MavenInvocationException
    {
        InvocationRequest request = createAndConfigureAnInvocationRequest( item );

        OutputConsumer output = new OutputConsumer( getLog() );
        request.setOutputHandler( output );

        invoker.setWorkingDirectory( getWorkingDirectoryAfterPlaceHolderIsReplaced( item ) );

        return invoker.execute( request );
    }

    private void createLogOutput( ItemWithProperties item )
    {
        StringBuilder sb = new StringBuilder( "------ " );
        sb.append( " iterator-maven-plugin " );
        sb.append( "( iteration: " );
        sb.append( item.getName() );
        sb.append( " )" );
        getLog().info( sb.toString() );
    }

    public void setThreads( String threads )
    {
        this.threads = threads;
    }

    public File getWorkingDirectory()
    {
        return workingDirectory;
    }

    public void setWorkingDirectory( File workingDirectory )
    {
        this.workingDirectory = workingDirectory;
    }

}
