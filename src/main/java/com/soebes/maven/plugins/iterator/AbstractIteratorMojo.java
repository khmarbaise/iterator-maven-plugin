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
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.soebes.maven.plugins.iterator.io.DirectoryScan;
import org.apache.commons.io.comparator.NameFileComparator;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.utils.StringUtils;

/**
 * @author Karl-Heinz Marbaise <a href="mailto:khmarbaise@apache.org">khmarbaise@apache.org</a>
 */
public abstract class AbstractIteratorMojo
    extends AbstractMojo
{
    /**
     * The project currently being build.
     */
    @Parameter( required = true, readonly = true, defaultValue = "${project}" )
    private MavenProject mavenProject;

    /**
     * The current Maven session.
     */
    @Parameter( required = true, readonly = true, defaultValue = "${session}" )
    private MavenSession mavenSession;

    /**
     * If you like to skip the execution.
     */
    @Parameter( property = "iterator.skip", defaultValue = "false" )
    private boolean skip;

    /**
     * Fail the execution at the end of all iterations or fail the whole if one iteration step fails.
     */
    @Parameter( property = "iterator.failAtEnd", defaultValue = "false" )
    private boolean failAtEnd;

    /**
     * The token the iterator placeholder begins with.
     */
    @Parameter( required = true, defaultValue = "@" )
    private String beginToken;

    /**
     * The token the iterator placeholder ends with.
     */
    @Parameter( required = true, defaultValue = "@" )
    private String endToken;

    /**
     * The name of the iterator variable.
     */
    @Parameter( required = true, defaultValue = "item" )
    private String iteratorName;

    /**
     * Here you can define the items which will be iterated through.
     *
     * <pre>
     * {@code
     *   <items>
     *     <item>one</item>
     *     <item>two</item>
     *     <item>three</item>
     *     ..
     *   </items>}
     * </pre>
     */
    @Parameter( property = "iterator.items" )
    private List<String> items;

    /**
     * If you like to have items to iterate through which also contain supplemental properties. This can be done by
     * using the following:
     *
     * <pre>
     * {@code
     *   <itemsWithProperties>
     *     <itemWithProperty>
     *       <name>one</name>
     *       <properties>
     *         <xyz>google</xyz>
     *       </properties>
     *     </itemWithProperty>
     *     <itemWithProperty>
     *       <name>two</name>
     *       <properties>
     *         <xyz>theseverside</xyz>
     *       </properties>
     *     </itemWithProperty>
     *     ..
     *   </items>}
     * </pre>
     * <p>
     * You are not determined having the same properties for the iterations. You can use different properties for each
     * iteration.
     */
    @Parameter
    private List<ItemWithProperties> itemsWithProperties;

    /**
     * The list of items which will be iterated through. {@code <content>one, two, three</content>}
     */
    @Parameter
    private String content;

    /**
     * The delimiter which will be used to split the {@link #content}.
     */
    @Parameter( defaultValue = "," )
    private String delimiter;

    /**
     * By using this folder you define a folder which sub folders will be used to iterate over. It will be iterated over
     * the directories but not the sub folders so no recursion will be done. The order of the iterated elements is done
     * by {@link #sortOrder}.
     */
    @Parameter
    private File folder;

    /**
     * This defines the sort order for the folders which will be iterated over.
     * {@link NameFileComparator#NAME_COMPARATOR} {@link NameFileComparator#NAME_INSENSITIVE_COMPARATOR}
     * {@link NameFileComparator#NAME_INSENSITIVE_REVERSE} {@link NameFileComparator#NAME_REVERSE}
     * {@link NameFileComparator#NAME_SYSTEM_COMPARATOR} {@link NameFileComparator#NAME_SYSTEM_REVERSE}
     */
    @Parameter( defaultValue = "NAME_COMPARATOR" )
    private String sortOrder;

    /**
     * This defines if the folder search should include files
     * @since 0.5.2
     */
    @Parameter( defaultValue = "false" )
    private boolean includeFiles;

    /**
     * This defines if it should return the full path of the folder / files
     */
    @Parameter( defaultValue = "false" )
    private boolean fullPath;

    /**
     * A set of file patterns that allow you to exclude certain files/folders from
     * being processed.
     * @since 0.5.2
     */
    @Parameter( property = "excludes" )
    private String[] excludes;

    /**
     * A set of file patterns that dictate which files or folders should be included.
     * @since 0.5.2
     */
    @Parameter( property = "includes" )
    private String[] includes;

    /**
     * Set the maximum depth for file scanning. Only applies to include and exclude filters.
     * @since 0.5.2
     */
    @Parameter( property = "depth", defaultValue = "-1" )
    private int depth;


    public boolean isSortOrderValid( String sortOrder )
    {

        return sortOrder.equalsIgnoreCase( "NAME_COMPARATOR" )
            || sortOrder.equalsIgnoreCase( "NAME_INSENSITIVE_COMPARATOR" )
            || sortOrder.equalsIgnoreCase( "NAME_INSENSITIVE_REVERSE" )
            || sortOrder.equalsIgnoreCase( "NAME_REVERSE" )
            || sortOrder.equalsIgnoreCase( "NAME_SYSTEM_COMPARATOR" )
            || sortOrder.equalsIgnoreCase( "NAME_SYSTEM_REVERSE" );
    }

    protected Comparator<File> convertSortOrder()
    {
        Comparator<File> result = NameFileComparator.NAME_COMPARATOR;
        if ( getSortOrder().equalsIgnoreCase( "NAME_INSENSITIVE_COMPARATOR" ) )
        {
            result = NameFileComparator.NAME_INSENSITIVE_COMPARATOR;
        }
        else if ( getSortOrder().equalsIgnoreCase( "NAME_INSENSITIVE_REVERSE" ) )
        {
            result = NameFileComparator.NAME_INSENSITIVE_REVERSE;
        }
        else if ( getSortOrder().equalsIgnoreCase( "NAME_REVERSE" ) )
        {
            result = NameFileComparator.NAME_REVERSE;
        }
        else if ( getSortOrder().equalsIgnoreCase( "NAME_SYSTEM_COMPARATOR" ) )
        {
            result = NameFileComparator.NAME_SYSTEM_COMPARATOR;
        }
        else if ( getSortOrder().equalsIgnoreCase( "NAME_SYSTEM_REVERSE" ) )
        {
            result = NameFileComparator.NAME_SYSTEM_REVERSE;
        }
        return result;
    }

    public List<String> getFolders()
        throws MojoExecutionException
    {

        if ( !folder.canRead() )
        {
            throw new MojoExecutionException( "The specified folder doesn't exist: " + folder );
        }
        initializeIncludes();
        initializeExcludes();
        initializeDepth();
        try
        {
            return find().stream().map( File::toString ).collect( Collectors.toList() );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Cannot scan directory", e );
        }
    }

    private void initializeDepth()
    {
        if ( includes.length == 0 && excludes.length == 0 )
        {
            depth = 1;
        }
    }

    private List<String> getContentAsList()
    {
        List<String> result = new ArrayList<>();
        String[] resultArray = content.split( delimiter );
        for ( String item : resultArray )
        {
            result.add( item.trim() );
        }
        return result;
    }

    /**
     * Convert all types {@code content}, {@code items} or {@code ItemsWithProperties} into the same type.
     * @return list {@link ItemWithProperties}
     * @throws MojoExecutionException In case of an error.
     */
    protected List<ItemWithProperties> getItemsConverted()
        throws MojoExecutionException
    {
        List<ItemWithProperties> result = new ArrayList<>();

        if ( isItemsWithPropertiesSet() )
        {
            result = getItemsWithProperties();
        }
        else if ( isContentSet() )
        {
            for ( String itemName : getContentAsList() )
            {
                result.add( new ItemWithProperties( itemName, ItemWithProperties.NO_PROPERTIES ) );
            }
        }
        else if ( isItemsSet() )
        {
            for ( String itemName : getItems() )
            {
                result.add( new ItemWithProperties( itemName, ItemWithProperties.NO_PROPERTIES ) );
            }
        }
        else if ( isFolderSet() )
        {
            for ( String itemName : getFolders() )
            {
                result.add( new ItemWithProperties( itemName, ItemWithProperties.NO_PROPERTIES ) );
            }
        }

        return result;
    }

    /**
     * This is just a convenience method to get the combination of {@link #getBeginToken()}, {@link #getIteratorName()}
     * and {@link #getEndToken()}.
     * @return The combined FolderResult.
     */
    protected PlaceHolderPattern getItemPlaceHolderPattern()
    {
        return new PlaceHolderPattern( getBeginToken(), getIteratorName(), getEndToken() );
    }

    protected boolean isItemsNull()
    {
        return items == null;
    }

    protected boolean isItemsWithPropertiesNull()
    {
        return itemsWithProperties == null;
    }

    protected boolean isItemsWithPropertiesSet()
    {
        return !isItemsWithPropertiesNull() && !itemsWithProperties.isEmpty();
    }

    protected boolean isItemsSet()
    {
        return !isItemsNull() && !items.isEmpty();
    }

    protected boolean isContentNull()
    {
        return content == null;
    }

    protected boolean isContentSet()
    {
        return !StringUtils.isBlank( content );
    }

    protected boolean isFolderSet()
    {
        return this.folder != null;
    }

    protected boolean isFolderNull()
    {
        return this.folder == null;
    }

    public File getFolder()
    {
        return this.folder;
    }

    public void setFolder( File folder )
    {
        this.folder = folder;
    }

    protected boolean isMoreThanOneSet()
    {
        // a ^ b ^ c && ! (a && b && c)
        boolean result = isItemsSet() ^ isContentSet() ^ isItemsWithPropertiesSet() ^ isFolderSet()
            && !(isItemsSet() && isContentSet() && isItemsWithPropertiesSet() && isFolderSet());
        return !result;
    }

    protected boolean isNoneSet()
    {
        return isItemsNull() && isContentNull() && isItemsWithPropertiesNull() && isFolderNull();
    }

    public void setSortOrder( String sortOrder )
    {
        this.sortOrder = sortOrder;
    }

    public String getSortOrder()
    {
        return sortOrder;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent( String content )
    {
        this.content = content;
    }

    public String getDelimiter()
    {
        return delimiter;
    }

    public void setDelimiter( String delimiter )
    {
        this.delimiter = delimiter;
    }

    public String getBeginToken()
    {
        return beginToken;
    }

    public void setBeginToken( String beginToken )
    {
        this.beginToken = beginToken;
    }

    public String getEndToken()
    {
        return endToken;
    }

    public void setEndToken( String endToken )
    {
        this.endToken = endToken;
    }

    public String getIteratorName()
    {
        return iteratorName;
    }

    public void setIteratorName( String iteratorName )
    {
        this.iteratorName = iteratorName;
    }

    public void setItems( List<String> items )
    {
        this.items = items;
    }

    public List<String> getItems()
    {
        return items;
    }

    public boolean isSkip()
    {
        return skip;
    }

    public boolean isFailAtEnd()
    {
        return failAtEnd;
    }

    public List<ItemWithProperties> getItemsWithProperties()
    {
        return itemsWithProperties;
    }

    public void setItemsWithProperties( List<ItemWithProperties> itemsWithProperties )
    {
        this.itemsWithProperties = itemsWithProperties;
    }

    public MavenProject getMavenProject()
    {
        return mavenProject;
    }

    public void setMavenProject( MavenProject mavenProject )
    {
        this.mavenProject = mavenProject;
    }

    public MavenSession getMavenSession()
    {
        return mavenSession;
    }

    public void setMavenSession( MavenSession mavenSession )
    {
        this.mavenSession = mavenSession;
    }

    public boolean isIncludeFiles()
    {
        return includeFiles;
    }

    public void setIncludeFiles( boolean includeFiles )
    {
        this.includeFiles = includeFiles;
    }

    public void setExcludes( final String[] excludes )
    {

        this.excludes = excludes;
    }

    public void setIncludes( final String[] includes )
    {

        this.includes = includes;
    }

    public boolean isFullPath()
    {
        return fullPath;
    }

    public void setFullPath( boolean fullPath )
    {
        this.fullPath = fullPath;
    }

    public void setDepth( final int depth )
    {

        this.depth = depth;
    }

    protected List<File> find()
        throws IOException
    {

        DirectoryScan dirScanner = new DirectoryScan( folder.toPath(), Arrays.asList( includes.clone() ), Arrays.asList( excludes.clone() ), depth
        );

        final List<Path> result = dirScanner.getResult().stream()
            .map( Path::toFile )
            .map( File::getAbsoluteFile )
            .filter( this::filterFiles )
            .sorted( convertSortOrder() )
            .map( File::toPath )
            .map( Path::normalize )
            .collect( Collectors.toList() );

        final Path root = folder.getAbsoluteFile().toPath().normalize();

        final List<Path> paths = isFullPath()
            ? result
            : result.stream()
            .map( root::relativize )
            .collect( Collectors.toList() );

        return paths.stream().map( Path::toFile ).collect( Collectors.toList() );
    }


    private boolean filterFiles( final File p )
    {
        return includeFiles || p.isDirectory();
    }

    protected void initializeExcludes()
    {
        if ( excludes == null || excludes.length == 0 )
        {
            excludes = new String[0];
        }
    }

    protected void initializeIncludes()
    {
        if ( includes == null || includes.length == 0 )
        {
            includes = new String[0];
        }
    }
}
