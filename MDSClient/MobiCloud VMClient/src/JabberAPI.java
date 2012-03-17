

	/*
	 * To change this template, choose Tools | Templates
	 * and open the template in the editor.
	 */


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
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
		
		DatabaseStarter dbStarter;

		public JabberAPI(DatabaseStarter dbStarter) {
			this.dbStarter = dbStarter;
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
			entries = roster.getEntries();

			System.out.println("\n\n" + entries.size() + " buddy(ies):");
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
		
		//<modify>
		//CHANGE PERMISSION
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
					str+="<modify type = "+MsgDict.USERPERMISSION_REQUEST+"><user_permission e_id=\""+entityId
							+"\" jid=\""+jid
							+"\" permission=\""+permission
							+"\"></user_permission></modify>";
				}
				
			}
			 str+="</requests></MSG>";
			 return str;
		}
		//RENAME
		public String createRequestModifyEntityName(int entityId, String newname)
		{
			return "<MSG><requests><modify type = "+MsgDict.RENAME_REQUEST+">"
						+"<rename e_id=\""+entityId
						+"\" newname=\""+ newname 
						+"\"></rename></modify></requests></MSG>";
		}
		//MOVE
		public String createRequestModifyEntityLocation(int entityId, String newpath)
		{
			return "<MSG><requests>" +
					"<modify type = "+MsgDict.MOVE_REQUEST+">"
							+"<move e_id=\""+entityId
							+"\" newpath=\""+ newpath 
							+"\"></move>" +
						"</modify></requests></MSG>";
		}
		//CREATE DIRECTORY
		public String createRequestCreateDir(String name, String url)
		{
			return "<MSG><requests>" +
					"<modify type = "+MsgDict.CREATEDIRECTORY_REQUEST+">"
						+"<createDir name=\""+name
						+"\" url=\""+ url 
						+"\"></createDir>" +
					"</modify></requests></MSG>";
		}
		//DELETE
		public String createRequestDelete(int entityId)
		{
			return "<MSG><requests>" +
					"<modify type = "+MsgDict.DELETE_REQUEST+">"
						+"<delete e_id=\""+ entityId 
						+"\"></delete>" +
					"</modify></requests></MSG>";
		}
		
//		public String createRequestModifyGroupPermission(ArrayList<Integer> entityIds, ArrayList<String> gids, int permission)
//		{
//			String str = "<MSG><requests>";
//			int entityId;
//			String gid;
//			for(int i=0; i<entityIds.size(); i++)
//			{
//				entityId = entityIds.get(i);
//				for(int j=0; j<gids.size(); j++)
//				{
//					gid = gids.get(j);
//					str+="<modify><group_permission e_id=\""+entityId
//							+"\" gid=\""+gid
//							+"\" permission=\""+permission
//							+"\"></group_permission></modify>";
//				}
//				
//			}
//			 str+="</requests></MSG>";
//			 return str;
//		}
		//MODIFY RESPONSE
		public String createResponseModify(boolean success)
		{
			if(success)
				return "<MSG><responses><modify type=\""+MsgDict.REQUEST_SUCCESSFUL+"\"></modify></responses></MSG>";
			else
				return "<MSG><responses><modify type=\""+MsgDict.REQUEST_FAILED+"\"></modify></responses></MSG>";
		}
		//</modify>
		
///////////////////////////////////////////////////////tcp file transfer send -- client
		public void sendFileTcp(String ip, String filename){
		 Socket s = null;
	        try{

	          int serverPort = 6880;
	               

	          s = new Socket(ip, serverPort);

	          DataInputStream input = new DataInputStream( s.getInputStream());

	          DataOutputStream output = new DataOutputStream( s.getOutputStream());
	          

	          	int index = filename.indexOf('.');
		        File f = new File(filename);
		        String sub = filename.substring(index);
		        
		        FileInputStream fin = null;
		        FileChannel ch = null;

		        try {
		            fin = new FileInputStream(f);
		            ch = fin.getChannel();
		            int size = (int) ch.size();
		            MappedByteBuffer buf = ch.map(MapMode.READ_ONLY, 0, size);
			        byte[] bytes = new byte[size];
		            buf.get(bytes);


		              
		              
		              //output.writeInt(data.length());
		              output.writeInt(sub.getBytes().length);
		              output.writeInt(bytes.length);

		              //Step 2 send length



		              //output.writeBytes(data); // UTF is a string encoding
		              output.write(sub.getBytes());
		              output.write(bytes);

		        } catch (IOException e) {
		            // TODO Auto-generated catch block
		            e.printStackTrace();
		        } finally {
		            try {
		                if (fin != null) {
		                    fin.close();
		                }
		                if (ch != null) {
		                    ch.close();
		                }
		            } catch (IOException e) {
		                // TODO Auto-generated catch block
		                e.printStackTrace();
		            }
		        }

	              //Step 1 send length



	              //Step 1 read length

	              int nb = input.readInt();
	              

	              byte[] digit = new byte[nb];

	              //Step 2 read byte

	              for(int i = 0; i < nb; i++)

	                digit[i] = input.readByte();

	          

	        }

	        catch (UnknownHostException e){

	            System.out.println("Sock:"+e.getMessage());}

	        catch (EOFException e){

	            System.out.println("EOF:"+e.getMessage()); }

	        catch (IOException e){

	            System.out.println("IO:"+e.getMessage());}

	        finally {

	              if(s!=null)

	                  try {s.close();

	                  }

	                  catch (IOException e) {/*close failed*/}

	        }
		}
		  
		
		
///////////////////////////////////////////////////////tcp file transter revceive -- server
		public void receiveFileTcp(String path){
			try{

	            int serverPort = 6880;

	            ServerSocket listenSocket = new ServerSocket(serverPort);
	        System.out.println("server start listening... ... ...");

	         

	            while(true) {

	                Socket clientSocket = listenSocket.accept();
	                
	                Connection c = new Connection(clientSocket, path);

	            }

	    }

	    catch(IOException e) {

	        System.out.println("Listen :"+e.getMessage());}

	  }
		
	}

	class Connection extends Thread {

	    DataInputStream input;

	    DataOutputStream output;

	    Socket clientSocket;
	    
	    String path;

    public Connection (Socket aClientSocket, String p) {

        try {
        			path = p;
                    clientSocket = aClientSocket;

                    input = new DataInputStream( clientSocket.getInputStream());

                    output =new DataOutputStream( clientSocket.getOutputStream());

                    this.start();

        }

            catch(IOException e) {

            System.out.println("Connection:"+e.getMessage());

            }

      }

 

      public void run() {

        try { // an echo server

          //  String data = input.readUTF();

                 

              int nb = input.readInt();
              int sb = input.readInt();
              
              System.out.println("Read Length"+ nb);
              System.out.println("Read second length"+ sb);


              byte[] digit = new byte[nb];
              byte[] digit2 = new byte[sb];
              //Step 2 read byte

               System.out.println("Writing.......");

              for(int i = 0; i < nb; i++)

                digit[i] = input.readByte();
              for(int i = 0; i<sb;i++)
              {
            	  digit2[i] = input.readByte();
              }

               String st = new String(digit);
              String strFilePath = path+st;
              File file = new File(strFilePath);
              if(!file.exists()){
            	  file.createNewFile();
              }
              try
              {
            	  FileOutputStream fos = new FileOutputStream(strFilePath);
            	  fos.write(digit2);
            	  fos.close();
              }
              catch(FileNotFoundException ex)
              {
            	  System.out.println("FileNotFoundException: " + ex);
              }
              catch(IOException ioe)
              {
            	  System.out.println("IOException: " + ioe);
              }
               

               String st2 = new String(digit2);

              //Step 1 send length

              output.writeInt(st.length());

              //Step 2 send length
          output.writeBytes(st); // UTF is a string encoding

          //  output.writeUTF(data);

            }

            catch(EOFException e) {
            System.out.println("EOF:"+e.getMessage()); }
            catch(IOException e) {
            System.out.println("IO:"+e.getMessage());} 

    

            finally {

              try {

                  clientSocket.close();

              }

              catch (IOException e){/*close failed*/}

            }

        }


}
