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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.soebes.maven.plugins.iterator.io.DirectoryScan;
import com.soebes.maven.plugins.iterator.resolver.ItemResolverType;
import org.apache.maven.plugin.MojoExecutionException;
import org.testng.annotations.Test;

public class DirectoryScanAndResolverTest
{

    public static class DirectoryScanTestSuite
    {
        private static final Path BASE_DIR = new File( "." ).toPath();

        @Test
        public void testGetAllPomFilesInSourceDirectory()
            throws IOException
        {
            List<String> includePatterns = Collections.singletonList( "**/pom.xml" );
            List<String> exludePatterns = new ArrayList<>();

            DirectoryScan directoryScan = new DirectoryScan( BASE_DIR, includePatterns, exludePatterns );
            Set<Path> result = directoryScan.getResult();

            assertTrue( result.size() > 1 );
        }

        @Test
        public void testGetMainPomFilesInSourceDirectoryUsingExcludes()
            throws IOException
        {
            List<String> includePatterns = Collections.singletonList( "**/pom.xml" );
            List<String> exludePatterns = Arrays.asList( "**/it/**", "**/target/**" );

            DirectoryScan directoryScan = new DirectoryScan( BASE_DIR, includePatterns, exludePatterns );
            List<Path> result = new ArrayList<>( directoryScan.getResult() );

            assertEquals( 1, result.size() );
            assertEquals( "./pom.xml", result.get( 0 ).toString() );
        }

        @Test
        public void testDepthLevel()
            throws IOException
        {
            List<String> includePatterns = Collections.singletonList( "**/pom.xml" );
            List<String> exludePatterns = new ArrayList<>();

            final int depth = 1;
            DirectoryScan directoryScan = new DirectoryScan( BASE_DIR, includePatterns, exludePatterns, depth );
            List<Path> result = new ArrayList<>( directoryScan.getResult() );

            assertEquals( 1, result.size() );
            assertEquals( "./pom.xml", result.get( 0 ).toString() );
        }
    }

    public static class ResolverTestSuite
    {
        private static final String SOURCE = "/src/main/test/level1/level2/result.extension";
        private static final String ITEM = "item";
        private static final String TOKEN = "@";
        private static final PlaceHolderPattern PATTERN = new PlaceHolderPattern( TOKEN, ITEM, TOKEN );

        @Test
        public void testBaseName()
            throws MojoExecutionException
        {

            String basename = getSubyTypePattern( ItemResolverType.BASENAME );
            String result = PATTERN.replaceAll( basename, SOURCE );
            assertEquals( "result", result );
        }

        @Test
        public void testFileExt()
            throws MojoExecutionException
        {

            String extension = getSubyTypePattern( ItemResolverType.EXTENSION );
            String result = PATTERN.replaceAll( extension, SOURCE );
            assertEquals( "extension", result );
        }

        @Test
        public void testFileName()
            throws MojoExecutionException
        {

            String filename = getSubyTypePattern( ItemResolverType.FILENAME );
            String result = PATTERN.replaceAll( filename, SOURCE );
            assertEquals( "result.extension", result );
        }

        @Test
        public void testFolder()
            throws MojoExecutionException
        {

            String folder = getSubyTypePattern( ItemResolverType.FOLDER );
            String result = PATTERN.replaceAll( folder, SOURCE );
            assertEquals( "/src/main/test/level1/level2", result );
        }

        @Test
        public void testFolderName()
            throws MojoExecutionException
        {

            String foldername = getSubyTypePattern( ItemResolverType.FOLDERNAME );
            String result = PATTERN.replaceAll( foldername, SOURCE );
            assertEquals( "level2", result );
        }

        @Test
        public void testParentFolderName()
            throws MojoExecutionException
        {

            String parentfoldername = getSubyTypePattern( ItemResolverType.PARENTFOLDERNAME );
            String result = PATTERN.replaceAll( parentfoldername, SOURCE );
            assertEquals( "level1", result );
        }

        @Test
        public void testParentFolder()
            throws MojoExecutionException
        {

            String parentfolder = getSubyTypePattern( ItemResolverType.PARENTFOLDER );
            String result = PATTERN.replaceAll( parentfolder, SOURCE );
            assertEquals( "/src/main/test/level1", result );
        }

        @Test
        public void testMultiplePlaceHolders()
            throws MojoExecutionException
        {
            String basename = getSubyTypePattern( ItemResolverType.BASENAME );
            String foldername = getSubyTypePattern( ItemResolverType.FOLDERNAME );
            String parentfoldername = getSubyTypePattern( ItemResolverType.PARENTFOLDERNAME );
            String result = PATTERN.replaceAll( basename + "-" + foldername + "-" + parentfoldername, SOURCE );
            assertEquals( "result-level2-level1", result );
        }

        private String getSubyTypePattern( ItemResolverType subtype )
        {
            return MessageFormat.format( "{0}{1}{2}{0}", TOKEN, ITEM, subtype.getSuffix() );
        }
    }
}
