package indexmaker;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;

import com.itextpdf.text.DocumentException;

/*
 * This is the GUI for the Index Maker for the JPress 720 
 * "Quality Control" information index. The GUI allows you to specify which folder
 * you would like to use to create the index. 
 */

public class IndexMakerUI extends JFrame implements ActionListener
{
	public JTextField dirPathField;
	public JButton dirPathButton;
	public JButton createIndexButton;
	public JFileChooser dirFileChooser;
	
	public PdfProcessor pdfProcessor;

	private static final int WIDTH = 400;
	private static final int HEIGHT = 300;

	//Variable that eclipse demands you define... I still don't know why
	private static final long serialVersionUID = 1L;
	
	//Constructor
	public IndexMakerUI()
	{
		super("Spencer\'s Index Maker 1.0");
		
		//Construct GUI components
		dirPathField = new JTextField(30);
		dirPathButton = new JButton("Browse...");
		createIndexButton = new JButton("Create Index");
		dirFileChooser = new JFileChooser();
		
		//Create PdfProcessor Object
		pdfProcessor = new PdfProcessor();
	
		//Connect GUI components with action listening
		dirPathButton.addActionListener(this);
		createIndexButton.addActionListener(this);
		
		//Set layout
		//getContentPane().setLayout(new java.awt.GridLayout(3,1));
		getContentPane().setLayout(new java.awt.FlowLayout());
		
		//Add GUI components to the Content Pane of this JFrame
		getContentPane().add(dirPathField);
		getContentPane().add(dirPathButton);
		getContentPane().add(createIndexButton);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Set size 
		setSize(WIDTH, HEIGHT);

	    //Center the window on the screen
	    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	    
	    // Determine the new location of the window
	    int w = getSize().width;
	    int h = getSize().height;
	    int x = (dim.width-w)/2;
	    int y = (dim.height-h)/2;
	    setLocation(x, y);
		
	    // visibility
		setVisible(true);
	}

	public void actionPerformed(ActionEvent event) 
	{
		if(event.getSource() == dirPathButton)
		{
			JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			
			int returnVal = fc.showOpenDialog(this);
			
			if (returnVal == JFileChooser.APPROVE_OPTION) 
			{
				dirPathField.setText(fc.getSelectedFile().toString());
			}
		}
		else if(event.getSource() == createIndexButton)
		{
			if(!dirPathField.getText().equals(""))
			{
				try 
				{
					pdfProcessor.createIndex(dirPathField.getText());
				} 
				catch(FileNotFoundException e)
				{
					e.printStackTrace();
				}
				catch(IOException e)
				{
					System.err.println("IndexMakerUI.java - failed to open index file");
					e.printStackTrace();
				}
				catch(DocumentException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	
}
