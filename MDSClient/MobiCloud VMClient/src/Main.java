import java.util.*;
import java.io.*;

import javax.swing.JTree;

public class Main {
	public static void main(String[] args) throws Exception {
		

	/*	BEGIN DATABASE INIALIZATION  */
		//get list of files in this local directory
		GetFilePath filePath = new GetFilePath();
		File dir = new File("/home/saada/Desktop/FilePathTest");
		filePath.visitAllDirsAndFiles(dir);
		TreeGenerator myTree = new TreeGenerator();
		
		//pass recordslist to a local arraylist
		ArrayList<String> recordsList = new ArrayList<String>();
		recordsList.addAll(filePath.getRecordsList());
		
		//Access Database
		MySQLAccess dao = new MySQLAccess();
		String table = "T_Entity";
		
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
		
		//generate java tree directory from database entities
		myTree.generate(dao.selectAll(table));
		String treeString = toString(myTree.root);
		System.out.println(treeString);
	/*	END OF DATABASE INIALIZATION  */
		
	/*###################################################
	 *                   Messaging
	 ###################################################*/
		//send file list to another client
		// declare variables
		JabberAPI c = new JabberAPI();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String msg = "message start: <root>\n";
		FileInputStream fistream1 = new FileInputStream("/home/saada/Desktop/test.xml");
		DataInputStream in1 = new DataInputStream(fistream1);
		BufferedReader reader1 = new BufferedReader(new InputStreamReader(in1));

		String temp = "";
		while((temp = reader1.readLine()) != null) {
			msg += temp;
		}
		reader1.close();
		msg += "</root>\n";
		String yesorno = "NO";
		
		// Enter your login information here
		c.login("xin", "xin");

		c.displayBuddyList();

		System.out.println("-----");

		System.out.println("Who do you want to talk to? - Type contacts full email address:");
		String talkTo = br.readLine();

		System.out.println("-----");
		System.out.println("Do you want to send file list in your VM to that guy? YES or NO");
		System.out.println("-----\n");
		
		//send tree
		c.sendMessage(treeString, "xin@mobicloud-mds-mysqlserver");
		

		while ((yesorno = br.readLine()).equalsIgnoreCase("YES")) {
			c.sendMessage(msg, talkTo);			
		}
		
		//c.sendMessage("Request Rejected", talkTo);

		//end program and database and xmpp connections
		dao.close();
		c.disconnect();
		System.exit(0);
	}
	
	 /** Read the object from Base64 string. */
    private static Object fromString( String s ) throws IOException ,
                                                        ClassNotFoundException {
        byte [] data = Base64Coder.decode( s );
        ObjectInputStream ois = new ObjectInputStream( 
                                        new ByteArrayInputStream(  data ) );
        Object o  = ois.readObject();
        ois.close();
        return o;
    }

    /** Write the object to a Base64 string. */
    private static String toString( Serializable o ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( o );
        oos.close();
        return new String( Base64Coder.encode( baos.toByteArray() ) );
    }

}
