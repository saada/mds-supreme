import java.io.*;

import org.jivesoftware.smack.RosterEntry;

public class Main {
	public static void main(String[] args) throws Exception {
		
		
		//Check OS and define directory path variables
	    String HOME = System.getProperty("user.home");
	    String OS = System.getProperty("os.name");
	    System.out.println("home="+HOME+"\nos="+OS);
	    
	    DatabaseStarter dbStarter = new DatabaseStarter();
	    dbStarter.getLocalTreeString();
	    //dbStarter.getLocalTreeString("tom");
	/*###################################################
	 *                   Messaging
	 ###################################################*/
		//send file list to another client
		// declare variables
		JabberAPI c = new JabberAPI(dbStarter);
		c.dbStarter.dao.addAllUsers(c.getMyRoster());

		for(RosterEntry r : c.getMyRoster())
		{
			System.out.println(r.getName());
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String msg = "message start: <root>\n";

//		FileInputStream fistream1 = new FileInputStream("/home/saada/Desktop/test.xml");
//		DataInputStream in1 = new DataInputStream(fistream1);
//		BufferedReader reader1 = new BufferedReader(new InputStreamReader(in1));
//
//		String temp = "";
//		while((temp = reader1.readLine()) != null) {
//			msg += temp;
//		}
//		reader1.close();
//		msg += "</root>\n";
		
		String yesorno = "NO";
		


		c.displayBuddyList();

		System.out.println("-----");

		System.out.println("Who do you want to talk to? - Type contacts full email address:");
		String talkTo = br.readLine();

		System.out.println("-----");
		System.out.println("Do you want to send file list in your VM to that guy? YES or NO");
		System.out.println("-----\n");
		
		//send tree
		//c.sendMessage(treeString, "xin@mobicloud-mds-mysqlserver");
		

		while ((yesorno = br.readLine()).equalsIgnoreCase("YES")) {
			c.sendMessage(msg, talkTo);			
		}
		
		//c.sendMessage("Request Rejected", talkTo);

		//end program and database and xmpp connections
		
		c.disconnect();
		dbStarter.close();
		System.exit(0);
	}

}
