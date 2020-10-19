package com.soebes.maven.plugins.iterator;

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

import java.util.Objects;

import com.soebes.maven.plugins.iterator.resolver.ItemResolverType;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Class PlaceHolderPattern ...
 * @author tvorschuetz
 *     Created on 16.10.20
 */
public class PlaceHolderPattern
{

    private final String itemProperty;
    private final String beginToken;
    private final String endToken;


    /**
     * Constructor PlaceHolderPattern creates a new PlaceHolderPattern instance.
     * @param beginToken of type String
     * @param itemProperty of type String
     * @param endToken of type String
     */
    public PlaceHolderPattern(
        final String beginToken,
        final String itemProperty,
        final String endToken )
    {
        this.itemProperty = itemProperty;
        this.beginToken = beginToken;
        this.endToken = endToken;
        validate();
    }


    /**
     * Method validate takes care not to use null values
     */
    private void validate()
    {
        Objects.requireNonNull( itemProperty, "name is not allowed to be null." );
        Objects.requireNonNull( beginToken, "beginToken is not allowed to be null." );
        Objects.requireNonNull( endToken, "endToken is not allowed to be null." );
    }


    /**
     * Method replaceAll invokes all pattern
     * @param value of type String
     * @param item of type String
     * @return String
     * @throws MojoExecutionException when
     */
    protected String replaceAll( final String value, final String item )
        throws MojoExecutionException
    {
        return replace( value, item, ItemResolverType.values() );
    }


    /**
     * Method replace invokes the give pattern
     * @param value of type String to be replaced
     * @param item of type String contains the resolved value
     * @param itemResolverTypes of type ItemResolverType...
     * @return String
     * @throws MojoExecutionException when
     */
    protected String replace(
        final String value,
        final String item,
        final ItemResolverType... itemResolverTypes )
        throws MojoExecutionException
    {
        String result = value;
        for ( final ItemResolverType resolverType : itemResolverTypes )
        {
            result = result.replace(
                getPlaceHolder( resolverType ), resolverType.getResolver().apply( item ) );
        }
        return result;
    }


    /**
     * Method getPlaceHolder returns the item derivate including pre- and postfix token
     * @param placeholder of type ItemResolverType
     * @return String
     */
    protected String getPlaceHolder( final ItemResolverType placeholder )
    {
        return String.format( "%s%s%s%s",
            beginToken,
            itemProperty,
            placeholder.getSuffix(),
            endToken );
    }

}
