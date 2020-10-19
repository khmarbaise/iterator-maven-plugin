package com.soebes.maven.plugins.iterator.resolver.types;

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

import com.soebes.maven.plugins.iterator.resolver.ItemResolver;
import org.apache.maven.shared.utils.StringUtils;
import org.apache.maven.shared.utils.io.FileUtils;

/**
 * Class ParentFolderPath
 *
 * @author tvorschuetz
 *     Created on 16.10.20
 */
public class ParentFolderPath
    implements ItemResolver
{

    /**
     * Returns the parent folder path of the item.
     *
     * @param item the first function argument
     * @return the function result
     */
    @Override
    public String apply( String item )
    {
        return getParentFolder( item );
    }

    protected static String getParentFolder( String s )
    {
        String path = FileUtils.dirname( s );
        String parentFolder = !StringUtils.isBlank( path )
            ? new File( path ).getParent()
            : null;

        return StringUtils.isBlank( parentFolder ) ? "" : parentFolder;
    }
}
