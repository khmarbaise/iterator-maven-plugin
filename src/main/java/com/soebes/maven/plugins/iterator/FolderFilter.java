package com.soebes.maven.plugins.iterator;

import java.io.File;
import java.io.FileFilter;

public class FolderFilter implements FileFilter {

	@Override
	public boolean accept(File file) {
		return file.isDirectory() && !file.isHidden();
	}
}