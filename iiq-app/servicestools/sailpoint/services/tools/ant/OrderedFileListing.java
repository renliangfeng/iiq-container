package sailpoint.services.tools.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Return a CSV of file paths in a given folder with a given extension in order of date created 
 * (or date modified if date created not available) from earliest to latest.
 * Used to determine the order in which to apply e-fixes. 
 */

public class OrderedFileListing extends Task{
    private String _folderPath;
    private String _fileExtensions;
    private String _property;

    
    // The setter for the "folderPath" attribute
    public void setFolderPath(String folderPath) {
        this._folderPath = folderPath;
    }
    
    // The setter for the "fileExtensions" attribute
    public void setFileExtensions(String fileExtensions) {
        this._fileExtensions = fileExtensions;
    }
    
    // The setter for the "property" attribute
    public void setProperty(String prop) { this._property = prop; }
    
    
    // The method executing the task
    public void execute() throws BuildException {
    	
	    String orderedFileListing = null;
	 
	    File folder = new File(_folderPath);
	    File[] listOfFiles = folder.listFiles(new FilenameFilter() {
	       public boolean accept(File dir, String name) {
	         if (null != _fileExtensions) {
	        	    List<String> fileExtensionList = Arrays.asList(_fileExtensions.split("\\s*,\\s*"));
	            for (String fileExtension : fileExtensionList) {
	              if (name.toLowerCase().endsWith(fileExtension.toLowerCase())) {
	                 return true;
	              }
	            }
	            return false;
	         } else {
	            return true;
	         }
	       } 
	      
	    
	    });
	 
	    Map fileMap = new HashMap();
	      for (int i = 0; i < listOfFiles.length; i++) {
	        if (listOfFiles[i].isFile()) {
	          Path myFile = Paths.get(listOfFiles[i].getPath());
	          BasicFileAttributes attr = null;
			  try {
				attr = Files.readAttributes(myFile, BasicFileAttributes.class);
			  } catch (IOException e) {
				throw new BuildException(e);
			  }
	          String timeStamp = null;
	          timeStamp = attr.creationTime().toString();
	          if (null == timeStamp) {
	             timeStamp = attr.lastModifiedTime().toString();
	          }
	          // Handle files that have the same timestamp by appending an incrementing integer to the timestamp
	          int j = 0;
	          String timeStampUnique = timeStamp;
	          while (fileMap.get(timeStampUnique) != null) {
	            j++;
	            timeStampUnique = timeStamp + String.valueOf(j);
	          }
	          fileMap.put(timeStampUnique, listOfFiles[i].getPath());
	        } 
	      }
	    SortedSet<String> keys = new TreeSet(fileMap.keySet());
	    for (String key : keys) { 
	       if (null == orderedFileListing) {
	    	      orderedFileListing = (String) fileMap.get(key);
	       } else {
	    	      orderedFileListing = orderedFileListing + "," + (String) fileMap.get(key);
	       }
	    }
	    getProject().setProperty(_property, orderedFileListing);
	 }
}