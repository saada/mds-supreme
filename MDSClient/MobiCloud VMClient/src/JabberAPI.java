

	/*
	 * To change this template, choose Tools | Templates
	 * and open the template in the editor.
	 */


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

	public class JabberAPI implements MessageListener {
		XMPPConnection connection;

		Collection<RosterEntry> entries;
		PacketTypeFilter filter = new PacketTypeFilter(Message.class);
		MessageHandler mHandler;
		
		FileTransferManager manager;
		OutputStream out;
		FileInputStream in;


		public JabberAPI(DatabaseStarter dbStarter) {
			// turn on the enhanced debugger
			XMPPConnection.DEBUG_ENABLED = true;
			
			// Enter your login information here
			try {
				login("xin", "xin", "VM");
			} catch (XMPPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			mHandler = new MessageHandler(this, dbStarter);
			mHandler.start(); 
			connection.addPacketListener(new PacketListener() {
				
				@Override
				public void processPacket(Packet arg0) {
					if( arg0.getClass().equals(Message.class))
					{
						System.out.println(((Message)arg0).getBody());
						if(((Message)arg0).getBody().contains("<MSG>"))
							mHandler.add(arg0);	
					}
				}
			}, filter);
			/**********************
			 * Receiving Files
			 */
			// Create the file transfer manager
		      final FileTransferManager manager = new FileTransferManager(connection);

		      // Create the listener
		      manager.addFileTransferListener(new FileTransferListener() {
		            public void fileTransferRequest(FileTransferRequest request) {
		                  // Check to see if the request should be accepted
		            	System.out.println("FileTransferListener starts!");
		                  if(shouldAccept(request)) {
		                        // Accept it
		                        IncomingFileTransfer transfer = request.accept();
		                        try {
									transfer.recieveFile(new File("C:\\test\\mds\\receive\\test_txt.txt"));
								} catch (XMPPException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
		                  } else {
		                        // Reject it
		                        request.reject();
		                  }
		                  System.out.println("FileTransferListener ends!");
		            }

					private boolean shouldAccept(FileTransferRequest request) {
						// TODO Auto-generated method stub
						return true;
					}
		      });
		      
		      /**************end of receiving files******************/
		}

		public void login(String userName, String password, String resource) throws XMPPException {
			ConnectionConfiguration config = new ConnectionConfiguration(
					"10.5.18.104",
					5222);
			connection = new XMPPConnection(config);

			connection.connect();
			connection.login(userName, password, resource);
		}

		public void fileTransfer(String file, String destination) throws XMPPException, IOException {	    	 
	    	try 
	    	{

                // Sets debug enabled true
                XMPPConnection.DEBUG_ENABLED=true;
                 
                // Creates the file transfer manager
                FileTransferManager manager = new FileTransferManager(connection);
                 
                // Create the outgoing file transfer with qualifier (i.e / clientname)
                OutgoingFileTransfer transfer = manager.createOutgoingFileTransfer(destination);
                                                                            // Opens a file selection dialog
                  
                File sf = new File(file);                 
                long fileSize = sf.length();
                
                if (file != null) 
                {
                    try {
                        // output is an OutputStream declared as a global variable.
                        /* Note that transfer.sendFile(fileName, fileSize, description);
                        returns an OutputStream */
                    	out =  (OutputStream) transfer.sendFile
                                (file, fileSize, "");
                        //This is an InputStream declared as a global variable.
                    	in = new FileInputStream(sf);
                        
                        int i;                          
                            while((i = in.read())!= -1){                                   
                                out.write((byte)i);
                            }                                                                                
                    	} catch (FileNotFoundException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }   
                        //Close the streams when finished
                        in.close();
                        out.close();                                                                                
                }
        	} catch (XMPPException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }
		}

		public void sendMessage(String message, String to) {
			Chat chat = connection.getChatManager().createChat(to, this);
			try {
				chat.sendMessage(message);
			} catch(XMPPException e) {
				e.printStackTrace();
			}
		}

		public void displayBuddyList() {
			Roster roster = connection.getRoster();
	                roster.getEntry("colin");
			entries = roster.getEntries();

			System.out.println("\n\n" + entries.size() + " buddy(ies):");
	                System.out.println("\n" + roster.getEntry("colin"));
			for (RosterEntry r : entries) {
				System.out.println(r.getName());
			}
		}

		public void disconnect() {
			connection.disconnect();
		}

		public void processMessage(Chat chat, Message message) {
			if (message.getType() == Message.Type.chat)
			{
				System.out.println(chat.getParticipant() + " says: "
						+ message.getBody());
			}
		}
		
		public Collection<RosterEntry> getBuddyList()
		{
			return entries;
		}
		
		public Collection<RosterEntry> getMyRoster() {
			Roster roster = connection.getRoster();
            Collection<RosterEntry> entries = roster.getEntries();
            
            return entries;
		}
		
		public Collection<RosterGroup> getMyGroup() {
			Roster roster = connection.getRoster();
            Collection<RosterGroup> entries = roster.getGroups();
            
            return entries;
		}
		
		public XMPPConnection getConnection() {
			return connection;
		}
		
		public void setConnection(XMPPConnection connection) {
			this.connection = connection;
			manager = new FileTransferManager(connection);
		}
		//OUR PROTOCOL MESSAGING FUNCTION
		
		//<view>
		public static String createRequestDirectoryMessage(String jid)
		{
			return "<MSG><requests><view type=\""+MsgDict.FILELIST_REQUEST+"\" jid="+jid+"></view></requests></MSG>";
		}
		public static String createResponseDirectoryMessage(String serializedFile, String jid)
		{
			return "<MSG><responses><view type=\""+MsgDict.FILELIST_UPDATE+"\" jid=\""+jid+"\"><![CDATA["+serializedFile+"]]></view></responses></MSG>";
		}
		//</view>
		//<download>
		public String createRequestDownload(String jid, int entityId)
		{
			return "<MSG><requests><download jid="+jid+" e_id="+entityId+"></download></requests></MSG>";
		}
		public String createResponeDownload(String jid)
		{
			return "<MSG><responses><download jid="+jid+">FILE WILL BE DOWNLOADED IN A MOMENT</download></responses></MSG>";
		}
		//</download>
		//<delete>
		public String createRequestDelete(String jid, int entityId)
		{
			return "<MSG><requests><download jid="+jid+" e_id="+entityId+"></download></requests></MSG>";
		}
		public String createResponeDelete(String jid)
		{
			return "<MSG><responses><download jid="+jid+">FILE DELETED SUCCESSFULLY</download></responses></MSG>";
		}
		//</delete>
		//<modify>
		public String createRequestModifyEntityName(int entityId, String newname)
		{
			return "<MSG><requests><modify<entityname e_id="+entityId+">"+ newname +"</entityname></modify></requests></MSG>";
		}
		public String createRequestModifyEntityLocation(int entityId, String newpath)
		{
			return "<MSG><requests><modify<entitylocation e_id="+entityId+">"+ newpath +"</entitylocation></modify></requests></MSG>";
		}
		public String createRequestModifyUserPermission(ArrayList<Integer> entityIds, ArrayList<String> jids, int permission)
		{
			String str = "<MSG><requests>";
			int entityId;
			String jid;
			for(int i=0; i<entityIds.size(); i++)
			{
				entityId = entityIds.get(i);
				for(int j=0; j<jids.size(); j++)
				{
					jid = jids.get(j);
					str+="<modify><user_permission e_id=\""+entityId
							+"\" jid=\""+jid
							+"\" permission=\""+permission
							+"\"></user_permission></modify>";
				}
				
			}
			 str+="</requests></MSG>";
			 return str;
		}
		public String createRequestModifyGroupPermission(ArrayList<Integer> entityIds, ArrayList<String> gids, int permission)
		{
			String str = "<MSG><requests>";
			int entityId;
			String gid;
			for(int i=0; i<entityIds.size(); i++)
			{
				entityId = entityIds.get(i);
				for(int j=0; j<gids.size(); j++)
				{
					gid = gids.get(j);
					str+="<modify><group_permission e_id=\""+entityId
							+"\" gid=\""+gid
							+"\" permission=\""+permission
							+"\"></group_permission></modify>";
				}
				
			}
			 str+="</requests></MSG>";
			 return str;
		}
		public String createResponseModify(boolean success)
		{
			if(success)
				return "<MSG><responses><modify type=\""+MsgDict.REQUEST_SUCCESSFUL+"\"></modify></responses></MSG>";
			else
				return "<MSG><responses><modify type=\""+MsgDict.REQUEST_FAILED+"\"></modify></responses></MSG>";
		}
		//</modify>
		//<createDir>
		public String createRequestCreateDir(String name, String url)
		{
			return "<MSG><requests><createDir url="+url+">"+name+"</createDir></requests></MSG>";
		}
		public String createResponseCreateDir(String name, String url)
		{
			return "<MSG><responses><createDir>DIRECTORY CREATED SUCCESSFULLY</createDir></responses></MSG>";
		}
		//</createDir>
	}

