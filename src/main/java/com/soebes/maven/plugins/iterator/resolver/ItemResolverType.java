package com.soebes.maven.plugins.iterator.resolver;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.lang.reflect.InvocationTargetException;

import com.soebes.maven.plugins.iterator.resolver.types.BaseName;
import com.soebes.maven.plugins.iterator.resolver.types.DefaultItem;
import com.soebes.maven.plugins.iterator.resolver.types.FileExtension;
import com.soebes.maven.plugins.iterator.resolver.types.FileName;
import com.soebes.maven.plugins.iterator.resolver.types.FolderName;
import com.soebes.maven.plugins.iterator.resolver.types.FolderPath;
import com.soebes.maven.plugins.iterator.resolver.types.ParentFolderName;
import com.soebes.maven.plugins.iterator.resolver.types.ParentFolderPath;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Enum ItemResolverType
 *
 * @author tvorschuetz
 *      Created on 16.10.20
 */
public enum ItemResolverType
{
    DEFAULT( DefaultItem.class ),
    FOLDER( FolderPath.class ),
    FOLDERNAME( FolderName.class ),
    PARENTFOLDER( ParentFolderPath.class ),
    PARENTFOLDERNAME( ParentFolderName.class ),
    FILENAME( FileName.class ),
    EXTENSION( FileExtension.class ),
    BASENAME( BaseName.class );



    /** Field SUBTYPE_DELIMITTER  used for delimitting subtypes of item variable for folder and file extended types*/
    static final char SUBTYPE_DELIMITTER = '.';


    /** Field resolverClass determines the resolver class using for resolving the given subtype */
    private final Class<? extends ItemResolver> resolverClass;

    /**
     * Constructor ItemResolverType creates a new ItemResolverType instance.
     *
     * @param resolverClass of type Class<? extends ItemResolver>
     */
    ItemResolverType( Class<? extends ItemResolver> resolverClass )
    {
        this.resolverClass = resolverClass;
    }

    /**
     * Method getResolver returns the resolver of this ItemResolverType object.
     *
     * @return the resolver (type ItemResolver) of this ItemResolverType object.
     * @throws MojoExecutionException when
     */
    public ItemResolver getResolver()
        throws MojoExecutionException
    {
        try
        {
            return resolverClass.getConstructor().newInstance();
        }
        catch ( InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e )
        {
            throw new MojoExecutionException( "Cannot create resolver.", e );
        }
    }

    /**
     * Method getSuffix returns the suffix of this ItemResolverType object.
     *
     * @return the suffix (type String) of this ItemResolverType object.
     */
    public String getSuffix()
    {
        return this.equals( DEFAULT ) ? "" : SUBTYPE_DELIMITTER + this.name().toLowerCase();
    }
}
