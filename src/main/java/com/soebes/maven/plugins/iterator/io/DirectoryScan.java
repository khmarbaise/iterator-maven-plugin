package com.soebes.maven.plugins.iterator.io;

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
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class DirectoryScan
 * @author tvorschuetz
 *     Created on 16.10.20
 */
public class DirectoryScan
{

    private static final String GLOB = "glob:";

    final File basedir;

    final int depth;

    final List<String> includes;
    final List<String> excludes;

    private Set<PathMatcher> includeMatchers;
    private Set<PathMatcher> excludeMatchers;


    /**
     * Constructor DirectoryScan creates a new DirectoryScan instance.
     * @param basedir of type File
     * @param depth of type int
     * @param includes of type List<String>
     * @param excludes of type List<String>
     */
    public DirectoryScan(
        File basedir,
        int depth,
        List<String> includes,
        List<String> excludes )
    {
        this.basedir = basedir;
        this.depth = depth;
        this.includes = null != includes ? includes : new ArrayList<>();
        this.excludes = null != excludes ? excludes : new ArrayList<>();
    }


    /**
     * Constructor DirectoryScan creates a new DirectoryScan instance.
     * @param basedir of type File
     * @param includes of type List<String>
     * @param excludes of type List<String>
     */
    public DirectoryScan(
        File basedir,
        List<String> includes,
        List<String> excludes )
    {
        this.basedir = basedir;
        this.depth = -1;
        this.includes = includes;
        this.excludes = excludes;
    }


    /**
     * Method getResult returns the result of this DirectoryScan object.
     * @return the result (type Set<Path>) of this DirectoryScan object.
     * @throws IOException when
     */
    public Set<Path> getResult()
        throws IOException
    {
        return listFilesUsingFileWalk();
    }


    /**
     * Method filterPath ...
     * @param nioPath of type Path
     * @return boolean
     */
    public boolean filterPath( Path nioPath )
    {
        if ( getIncludeMatchers().isEmpty() && getExcludeMatchers().isEmpty() )
        {
            return true;
        }

        return (getIncludeMatchers().isEmpty() || matches( nioPath, getIncludeMatchers() ))
            && !matches( nioPath, getExcludeMatchers() );
    }

    /**
     * Method getIncludeMatchers returns the includeMatchers of this DirectoryScan object.
     * @return the includeMatchers (type Set<PathMatcher>) of this DirectoryScan object.
     */
    private Set<PathMatcher> getIncludeMatchers()
    {
        if ( includeMatchers == null )
        {
            includeMatchers = buildPatterns( includes );
        }
        return includeMatchers;
    }

    /**
     * Method getExcludeMatchers returns the excludeMatchers of this DirectoryScan object.
     * @return the excludeMatchers (type Set<PathMatcher>) of this DirectoryScan object.
     */
    private Set<PathMatcher> getExcludeMatchers()
    {
        if ( excludeMatchers == null )
        {
            excludeMatchers = buildPatterns( excludes );
        }
        return excludeMatchers;
    }

    /**
     * Method matches ...
     * @param nio of type Path
     * @param matchers of type Set<PathMatcher>
     * @return boolean
     */
    private boolean matches( Path nio, Set<PathMatcher> matchers )
    {
        return matchers.stream().anyMatch( m -> m.matches( nio ) );
    }

    /**
     * Method buildPatterns ...
     * @param patterns of type List<String>
     * @return Set<PathMatcher>
     */
    private Set<PathMatcher> buildPatterns( List<String> patterns )
    {
        FileSystem fileSystem = FileSystems.getDefault();
        return patterns.stream()
            .map( s -> fileSystem.getPathMatcher( GLOB + s ) )
            .collect( Collectors.toSet() );
    }

    /**
     * Method listFilesUsingFileWalk ...
     * @return Set<Path>
     * @throws IOException when
     */
    private Set<Path> listFilesUsingFileWalk()
        throws IOException
    {
        try ( Stream<Path> stream =
                  Files.walk( basedir.toPath(), depth == -1 ? Integer.MAX_VALUE : depth ) )
        {
            return stream
                .filter( path -> path != basedir.toPath() )
                .filter( this::filterPath )
                .collect( Collectors.toCollection( LinkedHashSet::new ) );
        }
    }
}
