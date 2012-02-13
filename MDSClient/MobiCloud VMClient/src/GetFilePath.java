import java.io.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
 
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


public class GetFilePath {
	static FileOutputStream fostream = null;
	static DataOutputStream out = null;
	static BufferedWriter writer = null;
	static ArrayList<String> recordsList;
	private JTree tree;
	
	public GetFilePath() throws IOException {
		fostream = new FileOutputStream("/home/saada/Desktop/format.txt");
		out = new DataOutputStream(fostream);
		writer = new BufferedWriter(new OutputStreamWriter(out));
		recordsList = new ArrayList<String>();
	}
	
	public static long computeSize(File dir)
	{
        return dir.length() / 1024;
	}
	

	static File docFile = new File("/home/saada/Desktop/file.xml");
	static FileOutputStream outStream; 
	//static Buff
	static String xmlString = "";


	public static void buildXML (File dir) throws IOException
	{
		//FileWriter fstream = new FileWriter("out.xml", true);
		//BufferedWriter out = new BufferedWriter(fstream);	
	}
	public static void processXML(File entity) throws ParserConfigurationException, TransformerException, SAXException, IOException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			String str;
			Document doc = docBuilder.newDocument();			
			
			if(entity.isDirectory())
			{
				str = "dir";
				Element rootElement = doc.createElement("Path");
				doc.appendChild(rootElement);
				
				Attr attr = doc.createAttribute(str);
				attr.setValue(entity.getParent());
				rootElement.setAttributeNode(attr);
				
				Element staff = doc.createElement("Folder");
				rootElement.appendChild(staff);
		 
				// set attribute to staff element
				Attr attr_file = doc.createAttribute("name");
				attr_file.setValue(entity.getName());
				staff.setAttributeNode(attr_file);
				
			}
			else
			{
				str = "file";
				String st = "";
				Element rootElement = doc.createElement("Path");
				doc.appendChild(rootElement);
				
				Attr attr = doc.createAttribute(str);
				attr.setValue(entity.getParent());
				rootElement.setAttributeNode(attr);
				
				Element staff = doc.createElement("File");
				rootElement.appendChild(staff);
		 
				// set attribute to staff element
				Attr attr_file = doc.createAttribute("name");
				attr_file.setValue(entity.getName());
				staff.setAttributeNode(attr_file);
				
				// firstname elements
				long filesize = entity.length();
		        long filesizeInKB = filesize / 1024;
		        String size = filesizeInKB + "KB";
				
				Element file_size = doc.createElement("file_size");
				file_size.appendChild(doc.createTextNode(size));
				staff.appendChild(file_size);
		 
				// lastname elements
				Date d = new Date(entity.lastModified());
				String string = d.toString();
				Element modified_date = doc.createElement("modified_date");
				modified_date.appendChild(doc.createTextNode(string));
				staff.appendChild(modified_date);			
				
			}
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			
			//create string from eml tree
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult (sw); 
			DOMSource source = new DOMSource(doc);
			transformer.transform(source, result);
			xmlString +=  sw.toString();
			
			Date d = new Date(entity.lastModified());
            long filesizeInKB = computeSize(entity);
            

			str += ", "+entity.getName()+ ", " +  filesizeInKB + "KB"+", "+entity.getParent()+", "+ d ;
			str = str.replace("\\", "\\\\");
			System.out.println(str + "\n");
			System.out.println(xmlString);		
	}


	public void visitAllDirsAndFiles(File dir) throws ParserConfigurationException, TransformerException, SAXException, IOException {
		 	process(dir);
	        processXML(dir);

	    if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i=0; i<children.length; i++) {
	            visitAllDirsAndFiles(new File(dir, children[i]));
	        }
	        BufferedWriter out = new BufferedWriter(new FileWriter("/home/saada/Desktop/test.xml"));
			out.write(xmlString);
			out.close();
	    }
	}

	// Process only directories under dir
	public static void visitAllDirs(File dir) throws ParserConfigurationException, TransformerException, SAXException, IOException {
	    if (dir.isDirectory()) {
	        process(dir);
	        processXML(dir);

	        String[] children = dir.list();
	        for (int i=0; i<children.length; i++) {
	            visitAllDirs(new File(dir, children[i]));
	        }
	    }
	}

	// Process only files under dir
	public static void visitAllFiles(File dir) throws ParserConfigurationException, TransformerException, SAXException, IOException {
	    if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i=0; i<children.length; i++) {
	            visitAllFiles(new File(dir, children[i]));
	        }
	    } else {
	    	 	process(dir);
		        processXML(dir);
	    }
	}
	
	public static void process(File dir) throws ParserConfigurationException, TransformerException, SAXException, IOException {		
		
		String str;
		
		if(dir.isDirectory())
		{
			str = "dir";
		}
		else
		{
			str = "file";		
			
		}	
		
		Date d = new Date(dir.lastModified());
		long filesize = dir.length();
        long filesizeInKB = filesize / 1024;        

		str += ", "+dir.getName()+ ", " +  filesizeInKB + "KB"+", "+dir.getParent()+"/, "+ d ;
		recordsList.add(str);
		str = str.replace("\\", "\\\\");
		writer.append(str + "\n");
		writer.flush();
	}
	public ArrayList<String> getRecordsList()
	{
		return recordsList;
	}
}

