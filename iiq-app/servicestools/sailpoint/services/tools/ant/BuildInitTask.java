package sailpoint.services.tools.ant;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.commons.lang.ArrayUtils;
import java.util.Vector;
import java.util.Iterator;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BuildInitTask extends Task {
	private String _initFile;
	private String _prefix;
	private Vector<FileSet> filesets = new Vector<FileSet>();
	private static final String _header = "<?xml version='1.0' encoding='UTF-8'?>\n"
		+ "<!DOCTYPE sailpoint PUBLIC 'sailpoint.dtd' 'sailpoint.dtd'>\n"
		+ "<sailpoint>\n";
	private static final String _importTag = "<ImportAction name='include' value='";
	private static final String _footer = "</sailpoint>\n";
	private static final String _acceleratorPackInit = "init-acceleratorpack.xml";
	private static final String _spCustomInit = "sp.init-custom.xml";
	
	public void execute() throws BuildException {
		if (_prefix == null)
			_prefix = "";
		if (_initFile == null)
			throw new BuildException("You must sepecify init file name.");
		else if (filesets.size() == 0 )
			throw new BuildException("You must include a file set to create a init file for");
		else { // lets create an init file 
			// log("Creating init file " + _initFile);
			try {
				BufferedWriter output = new BufferedWriter(new FileWriter(new File(_initFile)));
				output.write(_header);
				// iterate over the filset and write the files
				for (Iterator<FileSet> it=filesets.iterator(); it.hasNext(); ) {
					FileSet fs = (FileSet)it.next();
					DirectoryScanner ds = fs.getDirectoryScanner(getProject());
					String[] files = ds.getIncludedFiles();
					List<String> filesList = new ArrayList(Arrays.asList(files));
					// Ensure the files are processed in the correct order
					filesList = moveToEndOfList(filesList, _acceleratorPackInit);
					filesList = moveToEndOfList(filesList, _spCustomInit);

					// Loop through files
					for (String fileName : filesList) {
						output.write("  " + _importTag + _prefix + fileName + "'/>\n");
						// log("Including file " + fileName);
					}
				}
				output.write(_footer);
				output.close();
			} catch (IOException e) {
				throw new BuildException(e);
			}
		}
	}
	
	 public void addFileSet(FileSet fileset) {
		if (!filesets.contains(fileset)) {
			filesets.add(fileset);
		}
	 }
	
	public void setInitFile (String file) {
		_initFile = file;
	}
	
	public void setPrefix (String prefix) {
		_prefix = prefix;
	}

	public List<String> moveToEndOfList(List<String> list, String element) {
		if (list.contains(element)) {
			list.remove(element);
			list.add(element);   
		}
		return list;
	}
	
}
