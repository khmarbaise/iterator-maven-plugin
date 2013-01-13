Iterator Executor Invoker Maven Plugin
======================================

Better shorter name  ItExIn

itexin-maven-plugin


License
-------
[Apache License, Version 2.0, January 2004](http://www.apache.org/licenses/)

Homepage
--------


Description
-----------

It might be helpful to create a plugin which is able to call mvn based on
iterating through some kind of data.

For example:

The following possible solutions exist:

    <build>
      <plugins>
        <plugin>
          <groupId>com.soebes.maven.plugins</groupId>
          <artifactId>itexin-maven-plugin</artifactId>
          <version>0.1</version>
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
              </configuration>
            </execution>
          </executions>
        </plugin>
      </plugins>
    </build>

The result of the above configuration is that the [maven-echo-plugin](https://github.com/khmarbaise/Maven-Echo-Plugin/)
will be called three times like the following:

    [INFO] 
    [INFO] --- itexin-maven-plugin:0.1.0-SNAPSHOT:executor (default) @ basic-test ---
    [INFO] ]] com.soebes.maven.plugins:maven-echo-plugin:0.1
    [INFO] This is a message: one
    [INFO] ]] com.soebes.maven.plugins:maven-echo-plugin:0.1
    [INFO] This is a message: two
    [INFO] ]] com.soebes.maven.plugins:maven-echo-plugin:0.1
    [INFO] This is a message: three
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS
    [INFO] ------------------------------------------------------------------------


The result of the above can of course be achieved by using the following pom
file:

    <build>
      <plugins>
        <plugin>
          <groupId>com.soebes.maven.plugins</groupId>
          <artifactId>maven-echo--plugin</artifactId>
          <version>0.1</version>
          <executions>
            <execution>
              <id>run-item1</id>
              <phase>package</phase>
              <goals>
                <goal>echo</goal>
              </goals>
              <configuration>
                <echos>
                  <echo>This is a message: one</echo>
                </echos>
              </configuration>
            </execution>
            <execution>
              <id>run-item2</id>
              <phase>package</phase>
              <goals>
                <goal>echo</goal>
              </goals>
              <configuration>
                <echos>
                  <echo>This is a message: two</echo>
                </echos>
              </configuration>
            </execution>
            <execution>
              <id>run-item3</id>
              <phase>package</phase>
              <goals>
                <goal>echo</goal>
              </goals>
              <configuration>
                <echos>
                  <echo>This is a message: three</echo>
                </echos>
              </configuration>
            </execution>
          </executions>
        </plugin>
      </plugins>
    </build>

But as you may already observed that the former configuration of the 
itexin-maven-plugin is shorter the more iterations you have. 
Think of ten executions in the previous example furthermore
you can not use the iteration variable as a parameter.


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



Status
------
- First realized ideas
- Some more thoughts.


TODOs
-----
 * Make more configuration options
 * Make Mojo for the invoker based on Maven-Invoker

Usage
-----

