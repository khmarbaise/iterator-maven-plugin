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
import java.util.Collections;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Karl-Heinz Marbaise <a href="mailto:khmarbaise@apache.org">khmarbaise@apache.org</a>
 */
public class ExecutorMojoTest
{

    private IteratorMojo mock;

    @BeforeMethod
    public void beforeMethod()
    {
        mock = mock( IteratorMojo.class, Mockito.CALLS_REAL_METHODS );
    }

    @Test( expectedExceptions = {
        MojoExecutionException.class }, expectedExceptionsMessageRegExp = "You have to use at least one. "
            + "Either items element, " + "itemsWithProperties, content or folder element!" )
    public void shouldFailWithMEEAndMessageIfNoParameterIsSet()
        throws MojoExecutionException, MojoFailureException
    {
        // Given
        when( mock.isItemsNull() ).thenReturn( true );
        when( mock.isContentNull() ).thenReturn( true );
        when( mock.isItemsWithPropertiesNull() ).thenReturn( true );

        // Then
        mock.execute();
    }

    @Test( expectedExceptions = {
        MojoExecutionException.class }, expectedExceptionsMessageRegExp = "You can use only one element. "
            + "Either items, itemsWithProperties, content or folder element but not more than one of them." )
    public void shouldFailWithMEEAndMessageIfAllAreSet()
        throws MojoExecutionException, MojoFailureException
    {
        List<ItemWithProperties> itemWithProperties =
            Collections.singletonList( new ItemWithProperties( "x", ItemWithProperties.NO_PROPERTIES ) );

        List<String> nonEmptyItemsList = Collections.singletonList( "A" );
        String nonEmptyConent = "X";
        File nonEmptyFile = new File( "." );

        // When
        mock.setItems( nonEmptyItemsList );
        mock.setContent( nonEmptyConent );
        mock.setItemsWithProperties( itemWithProperties );
        mock.setFolder( nonEmptyFile );

        // Then
        mock.execute();
    }
}
