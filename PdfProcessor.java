package indexmaker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.List;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfDestination;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfProcessor 
{
	//format "EN_XXXX_[YYMMDD]_ZZZZZZZZZZZZZZZZZ.pdf"
	public String formatFileName(String fileName)
	{
		StringBuffer buf = new StringBuffer();
		String [] tokens = fileName.substring(0, fileName.length() - 4).split("_");
		
		if(tokens.length  <= 1)
			return "";
		
		buf.append(tokens[1]);
		
		for(int i = 2; i < tokens.length; i++)
			buf.append(" " + tokens[i]);
		
		return buf.toString();
	}
	
	/* METHOD: Create Index 
	 * RECEIVES: String dirPath - Path to root directory
	 * RETURNS: true if index was created, false if not
	 * TODO: Change zoom of links, fix the concatenation so that sideways pdf files are not rotated
	 */
	public boolean createIndex(String dirPath) throws DocumentException, FileNotFoundException, IOException
	{
		if(dirPath == null)
			return false;
		
		File rootDir;
		File [] subDirs = null;
		
		//get the root directory and list its files
		rootDir = new File(dirPath);
		
		if(rootDir.isDirectory())
			subDirs = rootDir.listFiles();
		
		OutputStream out = null;
		out = new FileOutputStream(new File("index.pdf"));
		
		//Initialize the pdf to write to
		Document document = new Document();
		PdfWriter writer = null;
		writer = PdfWriter.getInstance(document, out);
		document.open();
		
		//I don't know what this is, seems important
		PdfContentByte cb = writer.getDirectContent();
		
		//keep track of last page: this will become the link destination
		//TODO
		//Later make lastPage equal to length of the index section (it might be more than 1 page).
		//This is kind of a catch 22 because we haven't written the links yet so we don't know how long
		//the index section will be. A crappy solution would be to see how long the page would be by writing all
		//the file names to a dummy file, then redoing it with the file names and links.
		int lastPage = 2;
		
		//main index creation work. go through all subdirs of main dir and write their file names to the index
		for(int i = 0; i < subDirs.length; i++)
		{
			System.out.println(subDirs[i].getName());
			
			java.util.List<FileInputStream> list = new ArrayList<FileInputStream>();
		
			//Store a listing of the files in the current subdir
			File pdfFiles [] = subDirs[i].listFiles();
			
			//write the subdir name to the index
			document.add(new Paragraph(subDirs[i].getName()));
			
			for(int j = 0; j < pdfFiles.length; j++)
				list.add(new FileInputStream(pdfFiles[j]));

			//iterate through the list of files, and store the pdf lengths in array.	
			for(int j = 0; j < pdfFiles.length; j++)
			{
				PdfReader reader = new PdfReader(list.get(j));
		
				//Write file names to index, make the text a link to the page position of the file in the concatenated list
				String name = formatFileName(pdfFiles[j].getName());
				
				//(int type, float left, float top, float zoom) 
				
				writer.addNamedDestination(name, lastPage - 1, new PdfDestination(5, (float)0.0, (float)0.0, (float)0.0 ));	
				//writer.addNamedDestination(name, lastPage, new PdfDestination(1, 0, 0, 0));
				//writer.addNamedDestination(name, lastPage, new PdfDestination("Fit"));
				
				Anchor anchor = new Anchor(name);
				anchor.setName(name);
				anchor.setReference("#" + name); 
				
				document.add(new Paragraph());
				document.add(anchor);
				
				lastPage += reader.getNumberOfPages();
			}
		}

		for(int i = 0; i < subDirs.length; i++)
		{
			System.out.println(subDirs[i].getName());
			
			java.util.List<FileInputStream> list = new ArrayList<FileInputStream>();
		
			File pdfFiles [] = subDirs[i].listFiles();
			
			for(int j = 0; j < pdfFiles.length; j++)
				list.add(new FileInputStream(pdfFiles[j]));
			
			//Concatenate all the pdf files together (try not to touch this)
			//for (FileInputStream in : list) 
			for(int j = 0; j < pdfFiles.length; j++)
			{
				PdfReader reader = new PdfReader(list.get(j));
				//PdfReader reader = new PdfReader(in);
	
				for (int k = 1; k <= reader.getNumberOfPages(); k++) 
				{
	                document.newPage();
	                //import the page from source pdf
	                PdfImportedPage page = writer.getImportedPage(reader, k);
	                //add the page to the destination pdf
	                cb.addTemplate(page, 0, 0);
	            }
	        }
			
		}	
			  
		out.flush();
		document.close();
		out.close();
	
		return true;
	}	
}
