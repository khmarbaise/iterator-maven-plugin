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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.assertj.core.groups.Tuple;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Karl-Heinz Marbaise <a href="mailto:khmarbaise@apache.org">khmarbaise@apache.org</a>
 */
public class AbstractInvokerMojoTest
{

    private static final String PROP_1 = "prop1";
    private static final String KEY = "key";
    private static final String ITEM = "item";
    private static final String TOKEN = "@";

    private static final String ONE = "one";
    private static final String TWO = "two";
    private static final String THREE = "three";

    private static final String VALUE = "value";
    private static final String VALUE_1 = "value1";
    private static final String VALUE_2 = "value2";
    private static final String VALUE_3 = "value3";
    private static final String VALUE_4 = "value4";

    private AbstractInvokerMojo mock;

    @BeforeMethod
    public void beforeMethod()
    {
        mock = Mockito.mock( AbstractInvokerMojo.class, Mockito.CALLS_REAL_METHODS );
        mock.setIteratorName( ITEM );
        mock.setBeginToken( TOKEN );
        mock.setEndToken( TOKEN );
        mock.setMavenProject(new MavenProject());
        mock.setProperties(new Properties());
    }

    @Test
    public void shouldReplaceInBaseDirectory() throws MojoExecutionException {
        mock.setBaseDirectory( new File( "first-@item@" ) );

        ItemWithProperties prop = new ItemWithProperties( ONE, ItemWithProperties.NO_PROPERTIES );
        InvocationRequest createAndConfigureAnInvocationRequest = mock.createAndConfigureAnInvocationRequest( prop );

        assertThat( createAndConfigureAnInvocationRequest.getBaseDirectory() ).isEqualTo( new File( "first-one" ) );
    }

    @Test
    public void shouldReplaceInGoal() throws MojoExecutionException {
        mock.setGoals( Collections.singletonList( "java:@item@-environment" ) );

        ItemWithProperties prop = new ItemWithProperties( ONE, ItemWithProperties.NO_PROPERTIES );
        InvocationRequest createAndConfigureAnInvocationRequest = mock.createAndConfigureAnInvocationRequest( prop );

        assertThat( createAndConfigureAnInvocationRequest.getGoals() ).hasSize( 1 ).containsExactly( "java:one-environment" );
    }

    @Test
    public void shouldReplaceInMultipleGoals() throws MojoExecutionException {
        mock.setGoals( Arrays.asList( "java:@item@-environment", "@item@", "selection-@item@-choice" ) );

        ItemWithProperties prop = new ItemWithProperties( ONE, ItemWithProperties.NO_PROPERTIES );
        InvocationRequest createAndConfigureAnInvocationRequest = mock.createAndConfigureAnInvocationRequest( prop );

        assertThat( createAndConfigureAnInvocationRequest.getGoals() ).hasSize( 3 ).containsExactly( "java:one-environment",
            ONE,
                                                                                                     "selection-one-choice" );
    }

    @Test
    public void shouldReplaceInProfile() throws MojoExecutionException {
        mock.setProfiles( Collections.singletonList( "profile-@item@" ) );

        ItemWithProperties prop = new ItemWithProperties( TWO, ItemWithProperties.NO_PROPERTIES );
        InvocationRequest createAndConfigureAnInvocationRequest = mock.createAndConfigureAnInvocationRequest( prop );

        assertThat( createAndConfigureAnInvocationRequest.getProfiles() ).hasSize( 1 ).containsExactly( "profile-two" );
    }

    @Test
    public void shouldReplaceInMultipleProfiles() throws MojoExecutionException {
        mock.setProfiles( Arrays.asList( "profile-@item@", "profile-second-@item@", "@item@-profile" ) );

        ItemWithProperties prop = new ItemWithProperties( TWO, ItemWithProperties.NO_PROPERTIES );
        InvocationRequest createAndConfigureAnInvocationRequest = mock.createAndConfigureAnInvocationRequest( prop );

        assertThat( createAndConfigureAnInvocationRequest.getProfiles() ).hasSize( 3 ).containsExactly( "profile-two",
                                                                                                        "profile-second-two",
                                                                                                        "two-profile" );
    }

    @Test
    public void shouldReplaceInProject() throws MojoExecutionException {
        mock.setProjects( Collections.singletonList( "project-@item@-a" ) );

        ItemWithProperties prop = new ItemWithProperties( THREE, ItemWithProperties.NO_PROPERTIES );
        InvocationRequest createAndConfigureAnInvocationRequest = mock.createAndConfigureAnInvocationRequest( prop );

        assertThat( createAndConfigureAnInvocationRequest.getProjects() ).hasSize( 1 ).containsExactly( "project-three-a" );
    }

    @Test
    public void shouldReplaceInMultipleProjects() throws MojoExecutionException {
        mock.setProjects( Arrays.asList( "project-@item@-a", "@item@project", "@item@" ) );

        ItemWithProperties prop = new ItemWithProperties( THREE, ItemWithProperties.NO_PROPERTIES );
        InvocationRequest createAndConfigureAnInvocationRequest = mock.createAndConfigureAnInvocationRequest( prop );

        assertThat( createAndConfigureAnInvocationRequest.getProjects() ).hasSize( 3 ).containsExactly( "project-three-a",
                                                                                                        "threeproject",
            THREE );
    }
    
    @Test
    public void shouldAddProjectProperties() throws MojoExecutionException {
        mock.getMavenProject().getProperties().put( PROP_1, VALUE_1 );
        
        ItemWithProperties prop = new ItemWithProperties( ITEM, ItemWithProperties.NO_PROPERTIES );
        InvocationRequest createAndConfigureAnInvocationRequest = mock.createAndConfigureAnInvocationRequest( prop );
        
        assertThat( createAndConfigureAnInvocationRequest.getProperties().entrySet() )
                .hasSize( 1 )
                .extracting( KEY, VALUE )
                .containsExactly(Tuple.tuple( PROP_1, VALUE_1 ));
    }
    
    @Test
    public void shouldAddPluginProperties() throws MojoExecutionException {
        mock.getMavenProject().getProperties().put( PROP_1, VALUE_1 );
        mock.getProperties().put( PROP_1, VALUE_2 );
        
        ItemWithProperties prop = new ItemWithProperties( ITEM, ItemWithProperties.NO_PROPERTIES );
        InvocationRequest createAndConfigureAnInvocationRequest = mock.createAndConfigureAnInvocationRequest( prop );
        
        assertThat( createAndConfigureAnInvocationRequest.getProperties().entrySet() )
                .hasSize( 1 )
                .extracting( KEY, VALUE )
                .containsExactly(Tuple.tuple( PROP_1, VALUE_2 ));
    }

    @Test
    public void shouldAddItemProperties() throws MojoExecutionException {
        mock.getMavenProject().getProperties().put( PROP_1, VALUE_1 );
        mock.getProperties().put( PROP_1, VALUE_2 );
        Properties itemProperties = new Properties();
        itemProperties.put( PROP_1, VALUE_3 );
        
        ItemWithProperties prop = new ItemWithProperties( ITEM, itemProperties );
        InvocationRequest createAndConfigureAnInvocationRequest = mock.createAndConfigureAnInvocationRequest( prop );
        
        assertThat( createAndConfigureAnInvocationRequest.getProperties().entrySet() )
                .hasSize( 1 )
                .extracting( KEY, VALUE )
                .containsExactly(Tuple.tuple( PROP_1, VALUE_3 ));
    }
    
    @Test
    public void shouldAddSystemProperties() throws MojoExecutionException {
        mock.getMavenProject().getProperties().put( PROP_1, VALUE_1 );
        mock.getProperties().put( PROP_1, VALUE_2 );
        Properties itemProperties = new Properties();
        itemProperties.put( PROP_1, VALUE_3 );
        System.getProperties().put( PROP_1, VALUE_4 );
        
        ItemWithProperties prop = new ItemWithProperties( ITEM, itemProperties );
        InvocationRequest createAndConfigureAnInvocationRequest = mock.createAndConfigureAnInvocationRequest( prop );
        
        assertThat( createAndConfigureAnInvocationRequest.getProperties().entrySet() )
                .hasSize( 1 )
                .extracting( KEY, VALUE )
                .containsExactly(Tuple.tuple( PROP_1, VALUE_4 ));
    }
}
