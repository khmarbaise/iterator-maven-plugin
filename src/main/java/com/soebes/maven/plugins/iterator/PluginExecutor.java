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

import org.apache.maven.model.Plugin;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;

public class PluginExecutor
{
    /**
     * The plugin to be executed.
     * 
     * <pre>
     * {@code
     * <plugin>
     *   <groupId>..</groupId>
     *   <artifactId>..</artifactId>
     *   <version>..</version>
     * </plugin>
     * }
     * </pre>
     */
    private Plugin plugin;

    /**
     * The plugin goal to be executed.
     */
    private String goal;

    /**
     * Plugin configuration to use in the execution.
     * 
     * <pre>
     * {@code
     * <configuration>
     *   Plugin Configuration
     * </configuration>
     * }
     * </pre>
     */
    private XmlPlexusConfiguration configuration;

    public PluginExecutor()
    {
    }

    public Plugin getPlugin()
    {
        return plugin;
    }

    public void setPlugin( Plugin plugin )
    {
        this.plugin = plugin;
    }

    public String getGoal()
    {
        return goal;
    }

    public void setGoal( String goal )
    {
        this.goal = goal;
    }

    public XmlPlexusConfiguration getConfiguration()
    {
        return configuration;
    }

    public void setConfiguration( XmlPlexusConfiguration configuration )
    {
        this.configuration = configuration;
    }
}