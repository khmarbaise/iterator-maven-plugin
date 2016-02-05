package com.soebes.maven.plugins.iterator;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

public class FirstTest
{
    @Test
    public void testName()
    {
        String s = "true";
        boolean parseBoolean = Boolean.parseBoolean( s );
        assertThat( parseBoolean )
            .as( "This %s describes it.", s )
            .isTrue();
    }
}
