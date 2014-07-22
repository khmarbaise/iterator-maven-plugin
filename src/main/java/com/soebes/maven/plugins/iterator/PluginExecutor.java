package com.soebes.maven.plugins.iterator;

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