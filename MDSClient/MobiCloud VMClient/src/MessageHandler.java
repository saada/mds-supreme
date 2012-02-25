
import java.io.File; 
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;

//import log.Log;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.xml.sax.SAXException;




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
			parser = new XMLParser(((Message) p).getBody());
			Msg[] msgs = parser.getMsgList();
			for(Msg msg : msgs){
				if(msg!=null)
				{
					
					switch (msg.type) {
						default:
						{
							System.out.println("MSG TYPE= "+msg.type);
							
						}
						case MsgDict.FILELIST_REQUEST:
						{
							Message outMessage = new Message();
							outMessage.setType(Type.normal);
							outMessage.setBody(c.createResponseDirectoryMessage(dbStarter.getLocalTreeString(),msg.getAtr("jid")));
							outMessage.setFrom(c.getConnection().getUser());
							outMessage.setTo(msg.getAtr("jid")+"/GoogleTV");
							c.getConnection().sendPacket(outMessage);
							break;
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
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
