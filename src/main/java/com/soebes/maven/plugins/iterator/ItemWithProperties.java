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

import java.util.Objects;
import java.util.Properties;

/**
 * Container for items with properties.
 * 
 * @author Karl-Heinz Marbaise <a href="mailto:khmarbaise@apache.org">khmarbaise@apache.org</a>
 */
public class ItemWithProperties
{
    private String name;

    private Properties properties;

    public static final Properties NO_PROPERTIES = new Properties();

    public static final String NO_NAME = "";

    public ItemWithProperties()
    {
        this( NO_NAME, NO_PROPERTIES );
    }

    /**
     * @param name
     * @param properties The object which contains the properties. If you don't have properties simply give
     *            {@link #NO_PROPERTIES} as parameter.
     */
    public ItemWithProperties( String name, Properties properties )
    {
        super();
        this.name = Objects.requireNonNull( name, "name is not allowed to be null." );
        this.properties = Objects.requireNonNull( properties, "properties is not allowed to be null." );
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = Objects.requireNonNull( name, "name is not allowed to be null." );
    }

    public Properties getProperties()
    {
        return properties;
    }

    public void setProperties( Properties properties )
    {
        this.properties = Objects.requireNonNull( properties, "properties is not allowed to be null." );
    }

    public boolean hasProperties()
    {
        if ( properties == NO_PROPERTIES )
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean hasName()
    {
        if ( name == NO_NAME )
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
        result = prime * result + ( ( properties == null ) ? 0 : properties.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        ItemWithProperties other = (ItemWithProperties) obj;
        if ( name == null )
        {
            if ( other.name != null )
                return false;
        }
        else if ( !name.equals( other.name ) )
            return false;
        if ( properties == null )
        {
            if ( other.properties != null )
                return false;
        }
        else if ( !properties.equals( other.properties ) )
            return false;
        return true;
    }

}
