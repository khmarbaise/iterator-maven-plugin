Iterator Maven Plugin
=====================

[![Build Status](https://travis-ci.org/khmarbaise/iterator-maven-plugin.svg?branch=master)](https://travis-ci.org/khmarbaise/iterator-maven-plugin)
[![Maven Central](https://img.shields.io/maven-central/v/com.soebes.maven.plugins/iterator-maven-plugin.svg?label=Maven%20Central)](http://search.maven.org/#search%7Cga%7C1%7Ccom.soebes.maven.plugins)
[![Apache License, Version 2.0, January 2004](https://img.shields.io/github/license/khmarbaise/iterator-maven-plugin.svg?label=License)](http://www.apache.org/licenses/)

Build Status
------------

Overview
--------

The iterator-maven-plugin is intended to make iterations on a list possible. This means
to execute other plugins or Maven itself based on the iteratation of the given
elements.

This is in the majority of the cases based on wrong decoupling the configuration
from the artifacts (like webapp, ear etc.).


Homepage
--------
[http://khmarbaise.github.com/iterator-maven-plugin/](http://khmarbaise.github.com/iterator-maven-plugin/)

Description
-----------

It might be helpful to create a plugin which is able to call a plugin on
iterating through some kind of data.

For example:

The following possible solutions exist:

    <build>
      <plugins>
        <plugin>
          <groupId>com.soebes.maven.plugins</groupId>
          <artifactId>iterator-maven-plugin</artifactId>
          <version>0.5.0</version>
          <executions>
            <execution>
              <phase>package</phase>
              <goals>
                <goal>iterator</goal>
              </goals>
              <configuration>
                <items>
                  <item>one</item>
                  <item>two</item>
                  <item>three</item>
                </items>
                <pluginExecutors>
                  <pluginExecutor>
                    <plugin>
                      <groupId>com.soebes.maven.plugins</groupId>
                      <artifactId>echo-maven-plugin</artifactId>
                      <version>0.3</version>
                    </plugin>
                    <goal>echo</goal>
                    <configuration>
                      <echos>
                        <echo>This is a message: @item@</echo>
                      </echos>
                    </configuration>
                  </pluginExecutor>
                </pluginExecutors>
              </configuration>
            </execution>
          </executions>
        </plugin>
      </plugins>
    </build>

The result of the above configuration is that the [maven-echo-plugin](https://github.com/khmarbaise/Maven-Echo-Plugin/)
will be called three times like the following:

    [INFO] 
    [INFO] --- iterator-maven-plugin:0.5.0:iterator (default) @ basic-test ---
    [INFO]  ------ (one) com.soebes.maven.plugins:maven-echo-plugin:0.1:echo
    [INFO] This is a message: one
    [INFO]  ------ (two) com.soebes.maven.plugins:maven-echo-plugin:0.1:echo
    [INFO] This is a message: two
    [INFO]  ------ (three) com.soebes.maven.plugins:maven-echo-plugin:0.1:echo
    [INFO] This is a message: three
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS
    [INFO] ------------------------------------------------------------------------


The result of the above can of course be achieved by using an appropriate number
of execution blocks, but it will become cumbersome in particular if you have
a larger number of executions (for example 10 or more).

An other example how the iterator-maven-plugin can make your life easier will be shown 
here:


      <plugin>
        <groupId>com.soebes.maven.plugins</groupId>
        <artifactId>iterator-maven-plugin</artifactId>
        <version>0.5.0</version>
        <executions>
          <execution>
            <phase>package</phase>
            <goals>
              <goal>iterator</goal>
            </goals>
            <configuration>
              <items>
                <item>test</item>
                <item>prod</item>
                <item>dev</item>
              </items>
              <pluginExecutors>
                <pluginExecutor>
                  <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-assembly-plugin</artifactId>
                    <version>2.4</version>
                  </plugin>
                  <goal>single</goal>
                  <configuration>
                    <descriptors>
                      <descriptor>${project.basedir}/@item@.xml</descriptor>
                    </descriptors>
                  </configuration>
                </pluginExecutor>
              </pluginExecutors>
            </configuration>
          </execution>
        </executions>
      </plugin>


In the above example the iteration variable *@item@* is used to access
the different assembly descriptor files in the project base directory.


It is also possible to iterator over a list of folders which means you can 
configure iterator-maven-plugin like the following:

      <plugin>
        <groupId>com.soebes.maven.plugins</groupId>
        <artifactId>iterator-maven-plugin</artifactId>
        <version>0.5.0</version>
        <executions>
          <execution>
            <phase>package</phase>
            <goals>
              <goal>iterator</goal>
            </goals>
            <configuration>
              <folder>src/main/java/com/soebes/maven/multiple/</folder>
              <pluginExecutors>
                <pluginExecutor>
                  <plugin>
                    <groupId>com.soebes.maven.plugins</groupId>
                    <artifactId>maven-echo-plugin</artifactId>
                  </plugin>
                  <goal>echo</goal>
                  <configuration>
                    <echos>
                      <echo>This is a message: @item@</echo>
                    </echos>
                  </configuration>
                </pluginExecutor>
                <pluginExecutor>
                  <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-jar-plugin</artifactId>
                  </plugin>
                  <goal>jar</goal>
                  <configuration>
                    <includes>
                      <include>com/soebes/maven/multiple/@item@/**</include>
                    </includes>
                    <classifier>@item@</classifier>
                  </configuration>
                </pluginExecutor>
              </pluginExecutors>
            </configuration>
          </execution>
        </executions>
      </plugin>

You can see what is possible, but i can not recommend to do things
like this in particular calling the maven-jar-plugin like this.

Having a property which contains a list of servers like this:

    list_of_servers=host1, host2, host3

    for (server : list_of_servers) {
      call mvn -D$server
    }

    for (server : list_of_servers) {
      call mvn -Dgoal=$server
    }

    for (server : list_of_servers) {
      call $server/mvn -Dserver=$server clean package
    }



      <plugin>
        <groupId>com.soebes.maven.plugins</groupId>
        <artifactId>iterator-maven-plugin</artifactId>
        <version>0.5.0</version>
        <executions>
          <execution>
            <phase>package</phase>
            <goals>
              <goal>invoker</goal>
            </goals>
            <configuration>
              <items>
                <item>test</item>
                <item>prod</item>
                <item>dev</item>
              </items>

              <mavenGoals>
                <mavenGoal>clean</mavenGoal>
                <mavenGoal>package</mavenGoal>
              </mavenGoals>
              <mavenBaseDir></mavenBaseDir>
              <pomFile>${project.basedir}/maven-calls/@item@/pom.xml</pomFile>
              <properties>
                <goal>@item@</goal>
              </properties>
            </configuration>
          </execution>
        </executions>
      </plugin>


      cd maven-calls/@item@/
      mvn -Dgoal=@item@ -f pom.xml clean package



    <configuration>
      <servers>host1,host2,host3</servers>
      <separator>,</separator> <!-- RegEx? -->
      ..
    </configuration>


    <configuration>
      <content>s1,s2,s3,s4</content>

      <plugins>
        <plugin>
          <groupId>...</groupId>
          <artifactId>..</artifactId>
          <version>..</version>
          <configuration>
           ..Whatever configuration Replacement @value@
          </configuration>
        </plugin>
      </plugins>
    </configuration>

Idea to use key/value pairs. 
key1, value11, value12, value13
key2, value21, value22, value23

  First iteration:

    @item.value.0@ => value11
    @item.value.1@ => value12



    @item.key@ => key1


Note
----

Starting with version 0.3 of iterator-maven-plugin the goal executor has
been renamed to iterator cause it represents better the meaning.

Status
------


TODOs
-----

Usage
-----

see homepage.
