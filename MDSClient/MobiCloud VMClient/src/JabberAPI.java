
	/*
	 * To change this template, choose Tools | Templates
	 * and open the template in the editor.
	 */


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

	public class JabberAPI implements MessageListener {
		XMPPConnection connection;
		FileTransferManager manager;
		OutputStream out;
		FileInputStream in;

		public void setConnection(XMPPConnection connection) {
			this.connection = connection;
			manager = new FileTransferManager(connection);
		}

		public JabberAPI() {
			// turn on the enhanced debugger
			XMPPConnection.DEBUG_ENABLED = true;
		}

		public void login(String userName, String password, String resource) throws XMPPException {
			ConnectionConfiguration config = new ConnectionConfiguration(
					"10.5.18.104",
					5222);
			connection = new XMPPConnection(config);

			connection.connect();
			connection.login(userName, password, resource);
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
		      });
		      
		      /**************end of receiving files******************/
		}

		public void fileTransfer(String file, String destination) throws XMPPException, IOException {	    	 
	    	try {

                // Sets debug enabled true
                XMPPConnection.DEBUG_ENABLED=true;
                 
                // Creates the file transfer manager
                FileTransferManager manager = new FileTransferManager(connection);
                 
                // Create the outgoing file transfer with qualifier (i.e / clientname)
                OutgoingFileTransfer transfer = manager.createOutgoingFileTransfer(destination);
                                                                            // Opens a file selection dialog
                  
                File sf = new File(file);                 
                long fileSize = sf.length();
                
                if (file != null) {
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


		protected boolean shouldAccept(FileTransferRequest request) {
			// TODO Auto-generated method stub
			return true;
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
			Collection<RosterEntry> entries = roster.getEntries();

			System.out.println("\n\n" + entries.size() + " buddy(ies):");
	                System.out.println("\n" + roster.getEntry("colin"));
			for (RosterEntry r : entries) {
				System.out.println(r.getName());
			}
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

		public void disconnect() {
			connection.disconnect();
		}

		public void processMessage(Chat chat, Message message) {
			if (message.getType() == Message.Type.chat)
				System.out.println(chat.getParticipant() + " says: "
						+ message.getBody());
		}
	}
