import java.util.*;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.*;

public class Main {
	public static void main(String[] args) throws Exception {
		//get files on VM
		File dir = new File("C:\\FilePathTest");
		GetFilePath filePath = new GetFilePath();
		GetFilePath.visitAllDirsAndFiles(dir);
		
		//Access Database
		MySQLAccess dao = new MySQLAccess();
		String table = "t_entity";
		
		ArrayList<String> recordsList = new ArrayList<String>();
		
		FileInputStream fistream = new FileInputStream("C:\\test\\format.txt");
		DataInputStream in = new DataInputStream(fistream);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String temp = "";
		
		while((temp = reader.readLine()) != null) {
			recordsList.add(temp);
		}
		reader.close();
		
		dao.deleteAll(table);
		dao.resetAutoID(table);
		for(int i=0; i<recordsList.size(); i++) {
			dao.insertEntity(recordsList.get(i));
		}
		
		dao.writeResultSet(dao.selectAll(table));
		//dao.delete(table, 1);
		//dao.writeResultSet(dao.selectAll(table));
		
		dao.close();	
		
		//send file list to another client
		// declare variables
		JabberAPI c = new JabberAPI();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String msg = "message start: <root>\n";
		FileInputStream fistream1 = new FileInputStream("C:\\test\\test.xml");
		DataInputStream in1 = new DataInputStream(fistream1);
		BufferedReader reader1 = new BufferedReader(new InputStreamReader(in1));
		
		while((temp = reader1.readLine()) != null) {
			msg += temp;
		}
		reader1.close();
		msg += "</root>\n";
		String yesorno = "NO";
		
		// Enter your login information here
		c.login("lin", "lin");

		c.displayBuddyList();

		System.out.println("-----");

		System.out.println("Who do you want to talk to? - Type contacts full email address:");
		String talkTo = br.readLine();

		System.out.println("-----");
		System.out.println("Do you want to send file list in your VM to that guy? YES or NO");
		System.out.println("-----\n");	
		

		while ((yesorno = br.readLine()).equalsIgnoreCase("YES")) {
			c.sendMessage(msg, talkTo);			
		}
		
		//c.sendMessage("Request Rejected", talkTo);

		c.disconnect();
		System.exit(0);
	}
}
