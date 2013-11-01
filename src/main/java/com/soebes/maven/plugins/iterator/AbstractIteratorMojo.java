package com.soebes.maven.plugins.iterator;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;


public abstract class AbstractIteratorMojo extends AbstractMojo {

    /**
     * The token the iterator placeholder begins with.
     */
    @Parameter(required = true, defaultValue = "@")
    private String beginToken;

    /**
     * The token the iterator placeholder ends with.
     */
    @Parameter(required = true, defaultValue = "@")
    private String endToken;

    /**
     * The name of the iterator variable.
     */
    @Parameter(required = true, defaultValue = "item")
    private String iteratorName;
    
    /**
     * Here you can define the items which will be iterated through.
     * <pre>{@code 
     *   <items>
     *     <item>one</item>
     *     <item>two</item>
     *     <item>three</item>
     *     ..
     *   </items>}</pre>
     */
    @Parameter(property = "iterator.items")
    private List<String> items;
    
    /**
     * The list of items which will be iterated through.
     * 
     * {@code <content>one, two, three</content>}
     */
    @Parameter
    private String content;
    
    /**
     * By using this folder you define a folder which sub
     * folders will be used to iterate over.
     */
    @Parameter
    private File folder;

    /**
     * The delimiter which will be used to split the
     * {@link #content}.
     */
    @Parameter(defaultValue = ",")
    private String delimiter;

    private List<String> getContentAsList() {
        List<String> result = new ArrayList<String>();
        String[] resultArray = content.split(delimiter);
        for (String item : resultArray) {
            result.add(item.trim());
        }
        return result;
    }

    protected List<String> getItems() throws MojoExecutionException {
        List<String> result = new ArrayList<String>();
        if (isItemsSet()) {
            result = items;
        } else if (isContentSet()) {
            result = getContentAsList();
        } else if (isFolderSet()) {
        	result = getFolders();
        }
            
        return result;
    }

    private List<String> getFolders() {
    	List <String> result = new ArrayList<String>();
		FileFilter fileFilter = new FolderFilter();
		File[] listFiles = folder.listFiles(fileFilter);
		for (int i = 0; i < listFiles.length; i++) {
			result.add(listFiles[i].getName());
		}
		return result;
    }

    /**
     * This is just a convenience method to get the combination
     * of {@link #getBeginToken()}, {@link #getIteratorName()} and {@link #getEndToken()}.
     * @return The combined string.
     */
    protected String getPlaceHolder() {
        return getBeginToken() + getIteratorName() + getEndToken();
    }

    protected boolean isItemsNull() {
        return items == null;
    }

    protected boolean isItemsSet() {
        return !isItemsNull() && !items.isEmpty(); 
    }

    protected boolean isContentNull() {
        return content == null;
    }

    protected boolean isContentSet() {
        //@TODO: Check if content.trim() couldn't be done more efficient?
        return content != null && content.trim().length() > 0;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getBeginToken() {
        return beginToken;
    }

    public void setBeginToken(String beginToken) {
        this.beginToken = beginToken;
    }

    public String getEndToken() {
        return endToken;
    }

    public void setEndToken(String endToken) {
        this.endToken = endToken;
    }

    public String getIteratorName() {
        return iteratorName;
    }

    public void setIteratorName(String iteratorName) {
        this.iteratorName = iteratorName;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public boolean isFolderSet() {
    	return this.folder != null;
    }
    public File getFolder() {
    	return this.folder;
    }
}
