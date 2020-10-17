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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Karl-Heinz Marbaise
 *         <a href="mailto:khmarbaise@apache.org">khmarbaise@apache.org</a>
 */
public class AbstractIteratorMojoTest {

    public static class IteratorFoldersTest
    {

        private static final Class<AbstractIteratorMojoTest> MOJO_TEST_CLASS = AbstractIteratorMojoTest.class;
        private IteratorMojo mock;

        @BeforeMethod
        public void beforeMethod() {
            mock = mock(IteratorMojo.class, Mockito.CALLS_REAL_METHODS);
            mock.setFolder(new File(".", "src"));
        }

        @Test
        public void shouldReturnTheSubfoldersInOrder() throws MojoExecutionException {
            when(mock.getSortOrder()).thenReturn("NAME_COMPARATOR");
            List<String> folders = mock.getFolders();
            assertThat(folders).containsSequence("it", "main", "site", "test");
        }

        @Test
        public void shouldReturnTheSubfoldersInReversOrder() throws MojoExecutionException {
            when(mock.getSortOrder()).thenReturn("NAME_REVERSE");
            List<String> folders = mock.getFolders();
            assertThat(folders).containsSequence("test", "site", "main", "it");
        }

        @Test(expectedExceptions = MojoExecutionException.class)
        public void shouldThrowExceptionOnNonExistentFolder() throws MojoExecutionException {
            mock.setFolder(new File("nonexistent"));
            mock.getFolders();
        }


        @Test
        public void shouldReturnTheSubfoldersInOrderFullPath()
            throws MojoExecutionException,
            IOException
        {
            when( mock.getSortOrder() ).thenReturn( "NAME_COMPARATOR" );
            mock.setFullPath( true );
            final File baseDir = mock.getFolder().getCanonicalFile();
            final List<String> folders = mock.getFolders();
            final List<String> resultFolders =
                Arrays.asList( new String[]{ "it", "main", "site", "test" }.clone() );

            assertThat( folders ).containsSequence( resultFolders.stream()
                .map( s -> new File( baseDir, s ) )
                .map( File::getAbsolutePath ).toArray( String[]::new ) );
        }


        @Test
        public void shouldReturnAbstractMojoTestClass()
            throws MojoExecutionException, URISyntaxException
        {
            when( mock.getSortOrder() ).thenReturn( "NAME_COMPARATOR" );

            mock.setFolder( new File( "./" ) );
            mock.setIncludes( new String[]{
                String.format( "**/%s.class", MOJO_TEST_CLASS.getSimpleName() ) } );
            mock.setIncludeFiles( true );
            mock.setFullPath( true );
            mock.setDepth( -1 );
            final List<String> folders = mock.getFolders();
            final File f = getAbstractMojoCompiledClass();
            assertEquals( folders.size(), 1 );
            assertThat( f.getAbsolutePath() ).containsSequence( folders.get( 0 ) );
        }


        @Test
        public void shouldExcludeAbstractMojoTestClass()
            throws MojoExecutionException, URISyntaxException
        {
            when( mock.getSortOrder() ).thenReturn( "NAME_COMPARATOR" );

            mock.setFolder( new File( "./" ) );
            mock.setIncludes( new String[]{ "**/*MojoTest.class" } );
            mock.setExcludes( new String[]{
                String.format( "**/%s.class", MOJO_TEST_CLASS.getSimpleName() ) } );
            mock.setIncludeFiles( true );
            mock.setFullPath( true );
            mock.setDepth( -1 );
            final List<String> folders = mock.getFolders();
            assertFalse( folders.isEmpty() );
            final File f = getAbstractMojoCompiledClass();
            assertFalse( folders.contains( f.getAbsolutePath() ) );
        }


        private File getAbstractMojoCompiledClass()
            throws URISyntaxException
        {
            final URI location = MOJO_TEST_CLASS.getProtectionDomain().getCodeSource().getLocation().toURI();
            return new File( location.getPath(),
                MOJO_TEST_CLASS.getName().replace( ".", File.separator ) + ".class" );
        }


    }

    public static class IteratorEmptyTest
    {
        private IteratorMojo mock;

        @BeforeMethod
        public void beforeMethod() {
            mock = mock(IteratorMojo.class, Mockito.CALLS_REAL_METHODS);
        }

        @Test
        public void shouldWarnWithEmptyContent() throws MojoExecutionException, MojoFailureException {
            Log log = mock(Log.class);
            mock.setLog(log);
            mock.setContent(null);
            mock.setItems(null);
            mock.setItemsWithProperties(null);
            mock.setFolder(null);
            mock.execute();
            verify(log).warn("Neither items, itemsWithProperties, content nor folder have been set.");
        }

        @Test(expectedExceptions = {
                MojoExecutionException.class }, expectedExceptionsMessageRegExp = "You can use only one element. Either items, itemsWithProperties, content or folder element but not more than one of them.")
        public void shouldFailWhenMoreThanOneElementIsSet() throws MojoExecutionException, MojoFailureException {
            Log log = mock(Log.class);
            mock.setLog(log);
            mock.setContent("test");
            mock.setItems(Collections.<String>singletonList("Items"));
            mock.setItemsWithProperties(null);
            mock.setFolder(null);
            mock.execute();
        }

    }
}
