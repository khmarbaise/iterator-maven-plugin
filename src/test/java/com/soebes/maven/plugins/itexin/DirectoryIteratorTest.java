package com.soebes.maven.plugins.itexin;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.FileFilter;

import org.codehaus.plexus.util.DirectoryScanner;
import org.testng.annotations.Test;

import edu.emory.mathcs.backport.java.util.Collections;

public class DirectoryIteratorTest {
	@Test
	public void shouldResultInTwoDirectoriesSrcAndTarget() {
		File folder = new File(".");
		FileFilter fileFilter = new FolderFilter();
		File[] listFiles = folder.listFiles(fileFilter);
		assertThat(listFiles).hasSize(2).containsOnly(new File("./src"), new File("./target"));
	}
	
	@Test
	public void secondTest() {
		DirectoryScanner dc = new DirectoryScanner();
		dc.addDefaultExcludes();
		dc.setBasedir(new File("."));
		dc.setIncludes(new String[]{"*"});
		dc.scan();
		String[] includedDirectories = dc.getIncludedDirectories();
		for (int i = 0; i < includedDirectories.length; i++) {
			System.out.println("Folder: " + includedDirectories[i]);
		}
	}

}
