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

import java.util.Properties;

import org.testng.annotations.Test;

/**
 * @author Karl-Heinz Marbaise <a href="mailto:khmarbaise@apache.org">khmarbaise@apache.org</a>
 */
public class ItemWithPropertiesTest
{
    public class ItemNullTest
    {
        @Test( expectedExceptions = {
            NullPointerException.class }, expectedExceptionsMessageRegExp = "name is not allowed to be null." )
        public void shouldReturnNPEForNameParameter()
        {
            new ItemWithProperties( null, new Properties() );
        }

        @Test( expectedExceptions = {
            NullPointerException.class }, expectedExceptionsMessageRegExp = "properties is not allowed to be null." )
        public void shouldReturnNPEForPropertiesParameters()
        {
            new ItemWithProperties( "name", null );
        }

        @Test( expectedExceptions = {
            NullPointerException.class }, expectedExceptionsMessageRegExp = "properties is not allowed to be null." )
        public void setPropertiesShouldReturnNPEForNullParameter()
        {
            new ItemWithProperties( "name", ItemWithProperties.NO_PROPERTIES ).setProperties( null );
        }

        @Test( expectedExceptions = {
            NullPointerException.class }, expectedExceptionsMessageRegExp = "name is not allowed to be null." )
        public void setNameShouldReturnNPEForNullParameter()
        {
            new ItemWithProperties( "name", new Properties() ).setName( null );
        }

    }

    public class ItemContentTest
    {
        @Test
        public void hasNameShouldBeFalseForNO_NAMEValue()
        {
            assertThat( new ItemWithProperties( ItemWithProperties.NO_NAME,
                                                ItemWithProperties.NO_PROPERTIES ).hasName() ).isFalse();
        }

        @Test
        public void hasNameShouldBeTrueForAnyOtherValue()
        {
            assertThat( new ItemWithProperties( "x", ItemWithProperties.NO_PROPERTIES ).hasName() ).isTrue();
        }

        @Test
        public void hasPropertiesShouldBeFalseForNO_PROPERTIESValue()
        {
            assertThat( new ItemWithProperties( "name", ItemWithProperties.NO_PROPERTIES ).hasProperties() ).isFalse();
        }

        @Test
        public void hasPropertiesShouldBeTrueForAnyOtherProperties()
        {
            assertThat( new ItemWithProperties( "name", new Properties() ).hasProperties() ).isTrue();
        }
    }
}
