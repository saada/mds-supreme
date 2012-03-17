import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;


public class DatabaseStarter {
	MySQLAccess dao;
	String table = "T_Entity";
	GetFilePath filePath;
	File homeDir;
	DatabaseStarter()
	{
		/*	BEGIN DATABASE INIALIZATION  */
		//get list of files in this local directory
		try {
			//Init Database, filePath and home directory
			dao = new MySQLAccess();
			filePath = new GetFilePath();
			homeDir = new File(System.getProperty("user.home")+"/Desktop/My Files/");
			
			dao.deleteAll("T_UserPermit");
			//initialize entity table
			resetDB();
			
		/*	END OF DATABASE INIALIZATION  */
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}
	public void close()
	{
		dao.close();
	}
	public void resetDB() throws Exception
	{
		filePath.visitAllDirsAndFiles(homeDir);
		//pass recordslist to a local arraylist
		ArrayList<String> recordsList = new ArrayList<String>();
		recordsList.addAll(filePath.getRecordsList());

		//clear and reset database
		dao.deleteAll(table);
		dao.resetAutoID(table);
		
		//insert entities to database
		for(int i=0; i< recordsList.size(); i++) {
			System.out.println("RECORD["+i+"] = "+recordsList.get(i));
			dao.insertEntity(recordsList.get(i));
		}
		//print on console for testing
		dao.writeResultSet(dao.selectAll(table));
	}
	public String getLocalTreeString()
	{
		//generate java tree directory from database entities
		
		TreeGenerator myTree = new TreeGenerator();
		try {
			ResultSet rs = dao.selectAll(table);
			myTree.generate(rs,dao);
			String treeString = Serialization.toString(myTree.root);
			System.out.println(treeString);
			return treeString;
		} catch (Exception e) {
			e.printStackTrace();
			return "FAIL";
		}
	}
	public String getLocalTreeString(String jid) {
		TreeGenerator myTree = new TreeGenerator();
		try {
			myTree.generate(dao.selectAll(table),dao);
			String treeString = Serialization.toString(myTree.generatePermitted(dao.selectPermittedEntites(jid)));
			System.out.println(treeString);
			return treeString;
		} catch (Exception e) {
			e.printStackTrace();
			return "FAIL";
		}
	}
	public boolean updateUserPermission(int e_id, String jid, int permission) throws Exception {
		return dao.updateUserPermission(e_id, jid, permission);
	}
	public boolean renameEntity(int e_id, String newname) throws Exception
	{
		if(e_id == 1) return false;	//cannot rename default VM folder
		// File (or directory) with old name
		String[] str = dao.getEntityPathAndName(e_id);
		File file = new File(str[0]+str[1]);

		// File (or directory) with new name
		File file2 = new File(str[0]+newname);

		// Rename file (or directory)
		boolean success = file.renameTo(file2);
		if(success)
		{
			dao.updateEntityName(e_id, newname);
			System.out.println("\t###Rename Successful!");
			return true;
		}
		else
		{
			System.out.println("\t###Rename Failed!");
			return false;
		}
		
	}
	public boolean moveEntity(int e_id, String newpath) throws Exception {
		if(e_id == 1) return false;
		//move physical entity
		String[] str = dao.getEntityPathAndName(e_id);
		
		// File (or directory) to be moved
		File file = new File(str[0]+str[1]);
		 
		// Destination directory
		File dir = new File(newpath);
		 
		// Move file to new directory
		boolean success = file.renameTo(new File(dir, file.getName()));
		if (!success) {
		    // File was not successfully moved
			System.out.println("\t###Move Failed!");
			return false;
		}
		else
		{
			dao.updateEntityLocation(e_id, newpath);
			System.out.println("\t###Move Successful!");
			return true;
		}
	}
	public boolean deleteEntity(int e_id) throws SQLException {
		String[] str = dao.getEntityPathAndName(e_id);
		String type = dao.getEntityType(e_id);
		File f1 = new File(str[0]+str[1]);
		if(type.equals("dir"))
		{
			try {
				FileUtils.deleteDirectory(f1);
				dao.deleteEntity(e_id);
				System.out.println("\t###Folder deleted.");
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("\t###Deletion failed.");
				return false;
			}
		}
		else
		{
			boolean success = f1.delete();
			if (!success){
				System.out.println("\t###Deletion failed.");
				return false;
			}
			else{
				dao.deleteEntity(e_id);
				System.out.println("\t###File deleted.");
				return true;
			}
		}
	}
	public boolean createDirectory(String name, String url) throws Exception {
		// TODO Auto-generated method stub
		String strDirectory = url+name;
		// Create one directory
		boolean success = (
		new File(strDirectory)).mkdir();
		if (success) {
			java.util.Date date = new Date();
			java.sql.Date sqlDate = new java.sql.Date(date.getTime());
			String record = "dir, "+name+", "+0+"KB, "+url+", "+sqlDate;
			dao.insertEntity(record);
			System.out.println("Directory: " + strDirectory + " created");
			return true;
		}
		else
		{
			return false;
		}
	}
	
}
