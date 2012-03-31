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
	/*###################################################
	 *                   Messaging
	 ###################################################*/
		//send file list to another client
		// declare variables
		JabberAPI c = new JabberAPI(dbStarter);
		c.dbStarter.dao.addAllUsers(c.getMyRoster());
		for(RosterEntry r : c.getMyRoster())
		{
			System.out.print("Name: "+r.getName()
					+"\tUser: "+r.getUser()
					+"\tClass: "+r.getClass()
					+"\tGroups: "+r.getGroups()
					+"\tStatus: "+r.getStatus()
					+"\tType: "+r.getType()
					+"\n");
		}
		
		//Initialize file transfer thread pool
		c.acceptStart("", 6881, "");
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String msg = "message start: <root>\n";
		
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
