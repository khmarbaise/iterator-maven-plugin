Iterator Maven Plugin
=====================

[![Build Status](https://buildhive.cloudbees.com/job/khmarbaise/job/iterator-maven-plugin/badge/icon)](https://buildhive.cloudbees.com/job/khmarbaise/job/iterator-maven-plugin/)

Overview
--------

The iterator-maven-plugin is intended to make iterations on a list possible. This means
to execute other plugins or Maven itself based on the iteratation of the given
elements.

This is in the majority of the cases based on wrong decoupling the configuration
from the artifacts (like webapp, ear etc.).


License
-------
[Apache License, Version 2.0, January 2004](http://www.apache.org/licenses/)

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
          <version>0.3</version>
          <executions>
            <execution>
              <phase>package</phase>
              <goals>
                <goal>executor</goal>
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
                      <artifactId>maven-echo-plugin</artifactId>
                      <version>0.1</version>
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
    [INFO] --- iterator-maven-plugin:0.3:executor (default) @ basic-test ---
    [INFO]  ------ com.soebes.maven.plugins:maven-echo-plugin:0.1:echo
    [INFO] This is a message: one
    [INFO]  ------ com.soebes.maven.plugins:maven-echo-plugin:0.1:echo
    [INFO] This is a message: two
    [INFO]  ------ com.soebes.maven.plugins:maven-echo-plugin:0.1:echo
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
        <version>0.3</version>
        <executions>
          <execution>
            <phase>package</phase>
            <goals>
              <goal>executor</goal>
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
        <version>0.3</version>
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


Strange Idea:

for every package (folder) which is found in src/main/java/com/soebes the plugin
will execute one iterattion with the name of the folder => @item@

    <configuration>
      <folderSet>
        <folder>${basedir}/src/main/java/com/soebes/</folder>
      </folderSet>

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


Status
------


TODOs
-----

Usage
-----

see homepage.
