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
 * Class DirectoryScan implements glob file system scan using include and exclude patterns.
 *
 * @author tvorschuetz
 *     Created on 16.10.20
 */
public class DirectoryScan
{

    private static final String GLOB = "glob:";

    final Path basedir;

    final int depth;

    final List<String> includes;
    final List<String> excludes;

    private Set<PathMatcher> includeMatchers;
    private Set<PathMatcher> excludeMatchers;


    /**
     * Constructor DirectoryScan creates a new DirectoryScan instance.
     *
     * @param basedir of type File
     * @param includes of type List<String>
     * @param excludes of type List<String>
     * @param depth of type int determines the depth level for recursion
     */
    public DirectoryScan(
        Path basedir,
        List<String> includes,
        List<String> excludes,
        int depth )
    {
        this.basedir = basedir;
        this.includes = null != includes ? includes : new ArrayList<>();
        this.excludes = null != excludes ? excludes : new ArrayList<>();
        this.depth = depth;
    }

    /**
     * Constructor DirectoryScan creates a new DirectoryScan instance using infinity depth.
     *
     * @param basedir of type File
     * @param includes of type List<String>
     * @param excludes of type List<String>
     */
    public DirectoryScan(
        Path basedir,
        List<String> includes,
        List<String> excludes)
    {
        this(basedir,includes,excludes,-1);
    }

    /**
     * Method getResult returns the result of this DirectoryScan object.
     *
     * @return the result (type Set<Path>) of this DirectoryScan object.
     * @throws IOException when
     */
    public Set<Path> getResult()
        throws IOException
    {
        try ( Stream<Path> stream =
                  Files.walk( basedir, depth == -1 ? Integer.MAX_VALUE : depth ) )
        {
            return stream
                .filter( path -> path != basedir )
                .filter( this::filterPath )
                .collect( Collectors.toCollection( LinkedHashSet::new ) );
        }
    }


    /**
     * Method filterPath returns true of both include and exclude filters are empty
     *
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
     * Method getIncludeMatchers returns the includeMatchers of this DirectoryScan object
     * or creates the machers lazily.
     *
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
     * Method getExcludeMatchers returns the excludeMatchers of this DirectoryScan object
     * or creates the machers lazily.
     *
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
     * Method matches evaluates the macher set against the path.
     *
     * @param nioPath of type Path to scan
     * @param matchers of type Set<PathMatcher> representing the pattern machers
     * @return boolean
     */
    private boolean matches( Path nioPath, Set<PathMatcher> matchers )
    {
        return matchers.stream().anyMatch( m -> m.matches( nioPath ) );
    }

    /**
     * Method buildPatterns ...
     *
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

}
