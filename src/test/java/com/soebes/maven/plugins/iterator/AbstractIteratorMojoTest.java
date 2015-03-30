package com.soebes.maven.plugins.iterator;

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
