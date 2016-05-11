package it4bi.ufrt.xwt.OWLStoPDDL;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class PDDLFileWriter {
	
	public void writePDDLFile(String filePath, String fileContent) {
		File file = new File(filePath);
	    
		try {
			file.createNewFile();
		
			FileWriter w = new FileWriter(file);
			w.write(fileContent);
			
			w.flush();
			w.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
