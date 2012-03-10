import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;


public class DatabaseStarter {
	MySQLAccess dao;
	String table = "T_Entity";
	DatabaseStarter()
	{
		/*	BEGIN DATABASE INIALIZATION  */
		//get list of files in this local directory
		GetFilePath filePath;
		try {
			filePath = new GetFilePath();
			File dir = new File(System.getProperty("user.home")+"/Desktop/My Files/");
			filePath.visitAllDirsAndFiles(dir);
			//pass recordslist to a local arraylist
			ArrayList<String> recordsList = new ArrayList<String>();
			recordsList.addAll(filePath.getRecordsList());
			
			//Access Database
			dao = new MySQLAccess();
			
			//clear and reset database
			dao.deleteAll(table);
			dao.resetAutoID(table);
			dao.deleteAll("T_UserPermit");
			
			//insert entities to database
			for(int i=0; i< recordsList.size(); i++) {
				System.out.println("RECORD["+i+"] = "+recordsList.get(i));
				dao.insertEntity(recordsList.get(i));
			}
			//print on console for testing
			dao.writeResultSet(dao.selectAll(table));
			

			
		/*	END OF DATABASE INIALIZATION  */
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	public void close()
	{
		dao.close();
	}
	public String getLocalTreeString()
	{
		//generate java tree directory from database entities
		
		TreeGenerator myTree = new TreeGenerator();
		try {
			ResultSet rs = dao.selectAll(table);
			myTree.generate(dao.selectAll(table),dao);
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
	
}
