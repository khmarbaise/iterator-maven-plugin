package com.soebes.maven.plugins.itexin;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.FileFilter;

import org.testng.annotations.Test;

public class DirectoryIteratorTest {
	@Test
	public void shouldResultInTwoDirectoriesSrcAndTarget() {
		File folder = new File(".");
		FileFilter fileFilter = new FolderFilter();
		File[] listFiles = folder.listFiles(fileFilter);
		assertThat(listFiles).hasSize(2).containsOnly(new File("./src"), new File("./target"));
	}

}
