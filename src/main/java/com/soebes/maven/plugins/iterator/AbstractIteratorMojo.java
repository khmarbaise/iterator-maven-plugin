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
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.comparator.NameFileComparator;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

import edu.emory.mathcs.backport.java.util.Collections;

public abstract class AbstractIteratorMojo
    extends AbstractMojo
{

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
     * The list of items which will be iterated through. {@code <content>one, two, three</content>}
     */
    @Parameter
    private String content;

    /**
     * By using this folder you define a folder which sub folders will be used to iterate over. It will be iterated over
     * the directories but not the sub folders so no recursion will be done. The order of the iterated elements is done
     * by
     */
    @Parameter
    private File folder;

    /**
     * The delimiter which will be used to split the {@link #content}.
     */
    @Parameter( defaultValue = "," )
    private String delimiter;

    /**
     * This defines the sort order for the folders which will be iterated over.
     * {@link NameFileComparator#NAME_COMPARATOR} {@link NameFileComparator#NAME_INSENSITIVE_COMPARATOR}
     * {@link NameFileComparator#NAME_INSENSITIVE_REVERSE} {@link NameFileComparator#NAME_REVERSE}
     * {@link NameFileComparator#NAME_SYSTEM_COMPARATOR} {@link NameFileComparator#NAME_SYSTEM_REVERSE}
     */
    @Parameter( defaultValue = "NAME_COMPARATOR" )
    private String sortOrder;

    private List<String> getContentAsList()
    {
        List<String> result = new ArrayList<String>();
        String[] resultArray = content.split( delimiter );
        for ( String item : resultArray )
        {
            result.add( item.trim() );
        }
        return result;
    }

    protected List<String> getItems()
        throws MojoExecutionException
    {
        List<String> result = new ArrayList<String>();
        if ( isItemsSet() )
        {
            result = items;
        }
        else if ( isContentSet() )
        {
            result = getContentAsList();
        }
        else if ( isFolderSet() )
        {
            result = getFolders();
        }

        return result;
    }

    protected List<String> getFolders()
    {
        IOFileFilter folders = FileFilterUtils.and( HiddenFileFilter.VISIBLE, DirectoryFileFilter.DIRECTORY );
        IOFileFilter makeSVNAware = FileFilterUtils.makeSVNAware( folders );
        IOFileFilter makeCVSAware = FileFilterUtils.makeCVSAware( makeSVNAware );

        String[] list = folder.list( makeCVSAware );
        List<File> listOfDirectories = new ArrayList<File>();
        for ( String item : list )
        {
            listOfDirectories.add( new File( folder, item ) );
        }

        Collections.sort( listOfDirectories, convertSortOrder() );
        List<String> resultList = new ArrayList<String>();
        for ( File file : listOfDirectories )
        {
            resultList.add( file.getName() );
        }
        return resultList;
    }

    /**
     * This is just a convenience method to get the combination of {@link #getBeginToken()}, {@link #getIteratorName()}
     * and {@link #getEndToken()}.
     * 
     * @return The combined string.
     */
    protected String getPlaceHolder()
    {
        return getBeginToken() + getIteratorName() + getEndToken();
    }

    protected boolean isItemsNull()
    {
        return items == null;
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
        // @TODO: Check if content.trim() couldn't be done more efficient?
        return content != null && content.trim().length() > 0;
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

    public boolean isFolderSet()
    {
        return this.folder != null;
    }

    public File getFolder()
    {
        return this.folder;
    }

    public void setFolder( File folder )
    {
        this.folder = folder;
    }

    public boolean isSortOrderValid( String sortOrder )
    {
        boolean result =
            sortOrder.equalsIgnoreCase( "NAME_COMPARATOR" )
                || sortOrder.equalsIgnoreCase( "NAME_INSENSITIVE_COMPARATOR" )
                || sortOrder.equalsIgnoreCase( "NAME_INSENSITIVE_REVERSE" )
                || sortOrder.equalsIgnoreCase( "NAME_REVERSE" )
                || sortOrder.equalsIgnoreCase( "NAME_SYSTEM_COMPARATOR" )
                || sortOrder.equalsIgnoreCase( "NAME_SYSTEM_REVERSE" );
        return result;
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

    public void setSortOrder( String sortOrder )
    {
        this.sortOrder = sortOrder;
    }

    public String getSortOrder()
    {
        return sortOrder;
    }
}
