package com.soebes.maven.plugins.iterator;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ExecutorTest
    extends UnitTestBase
{

    @Test
    public void first()
    {
        List<String> items = new ArrayList<String>();

        items.add( "one" );
        items.add( "two" );
        items.add( "three" );

    }

    @Test
    public void secondTest()
    {
        List<String> results = new ArrayList<String>();
        String items = "one, two, three";

        String[] result = items.split( "," );
        for ( String string : result )
        {
            results.add( string.trim() );
        }
    }
}
