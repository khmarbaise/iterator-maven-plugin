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


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.List;

import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AbstractIteratorMojoTest
{

    private AbstractIteratorMojo mock;

    @BeforeMethod
    public void beforeMethod()
    {
        mock = Mockito.mock( AbstractIteratorMojo.class, Mockito.CALLS_REAL_METHODS );
        mock.setFolder( new File( ".", "src" ) );
    }

    @Test
    public void shouldReturnTheSubfoldersInOrder()
    {
        when( mock.getSortOrder() ).thenReturn( "NAME_COMPARATOR" );
        List<String> folders = mock.getFolders();
        assertThat( folders ).containsSequence( "it", "main", "site", "test" );
    }

    @Test
    public void shouldReturnTheSubfoldersInReversOrder()
    {
        when( mock.getSortOrder() ).thenReturn( "NAME_REVERSE" );
        List<String> folders = mock.getFolders();
        assertThat( folders ).containsSequence( "test", "site", "main", "it" );
    }
}
