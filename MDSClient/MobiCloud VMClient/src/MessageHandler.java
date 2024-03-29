
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Message.Type;




public class MessageHandler extends Thread  {
	private boolean isRunning_;
	private LinkedList<Packet> msgs_;
	private Hashtable<String, Boolean> allowList_;
	private DatabaseStarter dbStarter;
	private JabberAPI c;

	public MessageHandler(JabberAPI jabberAPI, DatabaseStarter dbStarter) {
		msgs_ = new LinkedList<Packet>();
		allowList_ = new Hashtable<String, Boolean>();
		setDaemon(false);
		this.c = jabberAPI;
		this.dbStarter = dbStarter;
	}

	public void addAllow(String jid) {
		synchronized (allowList_) {
			allowList_.put(jid, new Boolean(true));
		}
	}

	public void removeAllow(String jid) {
		synchronized (allowList_) {
			allowList_.remove(jid);
		}
	}

	public boolean checkAllow(String jid) {
		synchronized (allowList_) {
			return (allowList_.get(jid) != null);
		}
	}

	public void run() {
		isRunning_ = true;
		Packet msg = null;
		while (isRunning_) {
			synchronized (msgs_) {
				while (isRunning_ && (msg = msgs_.poll()) == null) {
					try {
						msgs_.wait(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			if (isRunning_) {
				try {
					//String fromJID = msg.getFrom().split("/")[0];
					//if (checkAllow(fromJID)) {
						// Log.i("Allowed " + fromJID +
						// " to perform doWork()...");
						doWork(msg);
					//} else {
						// Log.i("Disallowed " + fromJID + ", Bye!");
					//}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void doWork(Packet p) {
		// Log.i("DoWork: " + ((Message) p).getBody());
		XMLParser parser;
		try {
			String from = p.getFrom(); System.out.println("FROM"+from);
			parser = new XMLParser((Message) p);
			ArrayList<Msg> msgs = new ArrayList<Msg>();
			msgs = parser.getMsgList();
			Message outMessage = new Message();
			boolean success = true;
			int msgType = 0;
			for(Msg msg : msgs){
				if(msg!=null)
				{
					msgType = msg.type;
					outMessage = new Message();
					outMessage.setType(Type.normal);
					outMessage.setFrom(c.getConnection().getUser());
					outMessage.setTo(from.split("/")[0]+"/GoogleTV");
					switch (msg.type) {
						case MsgDict.FILELIST_REQUEST:
						{
							//check if the user requesting is owner
							if(c.getConnection().getUser().split("/")[0].equals(from.split("/")[0]))
							{
								outMessage.setBody(c.createResponseDirectoryMessage(dbStarter.getLocalTreeString(),from.split("@")[0]));
							}
							else
							{
								outMessage.setBody(c.createResponseDirectoryMessage(dbStarter.getLocalTreeString(from.split("@")[0]),from.split("@")[0]));
							}
							c.getConnection().sendPacket(outMessage);
							break;
						}
						case MsgDict.USERPERMISSION_REQUEST:
						{
							if(c.getConnection().getUser().split("/")[0].equals(from.split("/")[0]))
							{
								if(!(dbStarter.updateUserPermission(Integer.parseInt(msg.getAtr("e_id")),msg.getAtr("jid"),
												Integer.parseInt(msg.getAtr("permission")))))
									success = false;
							}
							break;
						}
						case MsgDict.RENAME_REQUEST:
						{
							if(c.getConnection().getUser().split("/")[0].equals(from.split("/")[0]))
							{
								//return true if successfully renamed entity
								outMessage.setBody(c.createResponse(
										dbStarter.renameEntity(Integer.parseInt(msg.getAtr("e_id")),msg.getAtr("newname")),msgType)
								);
								c.getConnection().sendPacket(outMessage);
								
								//update tree in all devices (resources)
								updateAllOwners(from);
							}
							break;
						}
						case MsgDict.MOVE_REQUEST:
						{
							if(c.getConnection().getUser().split("/")[0].equals(from.split("/")[0]))
							{
								//return true if successfully moved entity
								outMessage.setBody(c.createResponse(
										dbStarter.moveEntity(Integer.parseInt(msg.getAtr("e_id")),msg.getAtr("newpath")),msgType)
								);
								c.getConnection().sendPacket(outMessage);
								
								//update tree
								updateAllOwners(from);
							}
							break;
						}
						case MsgDict.CREATEDIRECTORY_REQUEST:
						{
							if(c.getConnection().getUser().split("/")[0].equals(from.split("/")[0]))
							{
								//return true if successfully created directory
								outMessage.setBody(c.createResponse(
										dbStarter.createDirectory(msg.getAtr("name"),msg.getAtr("url")),msgType)
								);
								c.getConnection().sendPacket(outMessage);
								//update tree
								updateAllOwners(from);
							}
							break;
						}
						case MsgDict.DELETE_REQUEST:
						{
							if(c.getConnection().getUser().split("/")[0].equals(from.split("/")[0]))
							{
								//return true if successfully deleted entity
								outMessage.setBody(c.createResponse(
										dbStarter.deleteEntity(Integer.parseInt(msg.getAtr("e_id"))),msgType)
								);
								c.getConnection().sendPacket(outMessage);
								//update tree
								updateAllOwners(from);
							}
							break;
						}
						case MsgDict.UPLOAD_REQUEST:
						{
							//if owner
							if(c.getConnection().getUser().split("/")[0].equals(from.split("/")[0]))
							{
								//String domain = from.split("@")[0]+".mobicloud.asu.edu";
								boolean threadStarted;
								String filename = msg.getAtr("filename");
								String path = System.getProperty("user.home")+"/Desktop/My Files/";
								System.out.println("UPLOAD STUFF: \""+msg.getAtr("destination")+"\"");
								if(msg.getAtr("destination").equals(""))
									threadStarted = c.acceptUpload(filename, path);
								else
								{
									path=msg.getAtr("destination");
									threadStarted = c.acceptUpload(filename, path);
								}
								outMessage.setBody(c.createResponse(threadStarted, msgType));
								//update database
								if(threadStarted)
									dbStarter.insertNewEntity(c.getMyRoster(), path+filename);
								c.getConnection().sendPacket(outMessage);
							}
							break;
						}
						case MsgDict.DOWNLOAD_REQUEST:
						{
							if(c.getConnection().getUser().split("/")[0].equals(from.split("/")[0])
									|| from.split("/")[1].equals("VM"))
							{
								String domain = from.split("@")[0]+".mobicloud.asu.edu";
								//boolean invokeStarted = c.invokeToVM(domain, 6881, msg.getAtr("destination")+msg.getAtr("filename"));
								boolean invokeStarted = true;
								outMessage.setBody(c.createResponse(invokeStarted, msgType));
								c.getConnection().sendPacket(outMessage);
							}
							break;
						}
						case MsgDict.DOWNLOAD_REQUEST_FRIEND:
						{
							String path = System.getProperty("user.home")+"/Desktop/My Files/";
							String fileWithPath = msg.getAtr("destination")+msg.getAtr("filename");
							String domain = msg.getAtr("jid").split("@")[0]+".mobicloud.asu.edu";
							boolean invokeStarted = c.invokeToVM(domain, 6881,fileWithPath);
							outMessage.setBody(c.createResponse(invokeStarted, msgType));
							if(invokeStarted)
								dbStarter.insertNewEntity(c.getMyRoster(), path+msg.getAtr("filename"));
							c.getConnection().sendPacket(outMessage);
							break;
						}
					}
				}
			}
			if(msgType == MsgDict.USERPERMISSION_REQUEST)
			{
				outMessage.setBody(c.createResponse(success,msgType));
				c.getConnection().sendPacket(outMessage);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}
	public void updateAllOwners(String owner)
	{
		Message outMessage = new Message();
		outMessage.setType(Type.normal);
		outMessage.setFrom(c.getConnection().getUser());
		outMessage.setTo(owner.split("/")[0]);
		outMessage.setBody(c.createResponseDirectoryMessage(dbStarter.getLocalTreeString(),owner.split("@")[0]));
		c.getConnection().sendPacket(outMessage);
	}
	public void add(Packet msg) {
		synchronized (msgs_) {
			msgs_.add(msg);
			msgs_.notify();
		}
	}

	public void terminate() {
		synchronized (msgs_) {
			isRunning_ = false;
			msgs_.notify();
		}
	}
}
