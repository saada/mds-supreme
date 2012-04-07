
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jivesoftware.smack.packet.Message;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class XMLParser {
	public String str;
	private DocumentBuilderFactory dbf;
	private DocumentBuilder db;
	private NodeList nl;
	private Hashtable<String, String> atr;
	private ArrayList<Msg> msg;
	private String from;

	
	public XMLParser(Message p) throws ParserConfigurationException, SAXException, IOException {
		super();
		this.from = p.getFrom();
		this.str = p.getBody();
		dbf=DocumentBuilderFactory.newInstance();		
		db=dbf.newDocumentBuilder();
		InputStream is=new ByteArrayInputStream(p.getBody().getBytes());
		Document dm=db.parse(is);
		nl=dm.getElementsByTagName("requests");
		atr = new Hashtable<String, String>();
		msg = new ArrayList<Msg>();
	}
	
	
	public boolean isEmpty(){
		if(nl.getLength()==0){
			return true;
		}
		return false;
	}
	
	//suppose there is only one response in one message and only one tag in response
	
//	public List<String> getView(){
//		List<String> value = new ArrayList<String>();
//		if(!this.isEmpty()){
//			for(int i=0; i<nl.getLength();i++){
//				Element el=(Element)nl.item(i);
//				NodeList nll=el.getElementsByTagName("view");
//				
//				for(int j=0; j<nll.getLength();j++){
//					String jid = ((Element)nll.item(j)).getAttribute("jid");
//					data =((Element)nll.item(j)).getNodeValue().split("%@!");
//				}
//				
//			}
//		}
//		return null;
//	
//	}


	public int getType() {
		
		return 0;
	}


	public ArrayList<Msg> getMsgList() {
		if(!this.isEmpty()){
			for(int i=0; i<nl.getLength();i++){
				Element el=(Element)nl.item(i);
				NodeList nll=el.getChildNodes();
				
				for(int j=0; j<nll.getLength();j++){
					String type = nll.item(j).getNodeName();
					if(type.equals("view")){
						atr.clear();
						atr.put("type", ((Element)nll.item(j)).getAttribute("type"));
						atr.put("jid", from.split("/")[0]);
						msg.add(new Msg(Integer.parseInt(atr.get("type")), atr));
					}
					if(type.equals("modify")){
						Element mod = (Element)nll.item(j);
						NodeList modlist =mod.getChildNodes();
						for(int k=0; k<modlist.getLength(); k++)
						{
							 atr.clear();
							 String t = modlist.item(k).getNodeName();
							 if(t.equals("user_permission"))
							 {
								 atr.put("e_id", ((Element) modlist.item(k)).getAttribute("e_id"));
								 atr.put("jid", ((Element) modlist.item(k)).getAttribute("jid"));
								 atr.put("permission",((Element)modlist.item(k)).getAttribute("permission"));
								 msg.add(new Msg(MsgDict.USERPERMISSION_REQUEST, atr));
							 }
							 if(t.equals("rename"))
							 {
								 atr.put("e_id", ((Element) modlist.item(k)).getAttribute("e_id"));
								 atr.put("newname", ((Element) modlist.item(k)).getAttribute("newname"));
								 msg.add(new Msg(MsgDict.RENAME_REQUEST,atr));
							 }
							 if(t.equals("move"))
							 {
								 atr.put("e_id", ((Element) modlist.item(k)).getAttribute("e_id"));
								 atr.put("newpath", ((Element) modlist.item(k)).getAttribute("newpath"));
								 msg.add(new Msg(MsgDict.MOVE_REQUEST,atr));
							 }
							 if(t.equals("createDir"))
							 {
								 atr.put("name", ((Element) modlist.item(k)).getAttribute("name"));
								 atr.put("url", ((Element) modlist.item(k)).getAttribute("url"));
								 msg.add(new Msg(MsgDict.CREATEDIRECTORY_REQUEST,atr));
							 }
							 if(t.equals("delete"))
							 {
								 atr.put("e_id", ((Element) modlist.item(k)).getAttribute("e_id"));
								 msg.add(new Msg(MsgDict.DELETE_REQUEST,atr));
							 }
							 
						}
					}
					if(type.equals("upload"))
					{
						Element mod = (Element)nll.item(j);
						NodeList modlist =mod.getChildNodes();
						for(int k=0; k<modlist.getLength(); k++)
						{
							 atr.clear();
							 //String t = modlist.item(k).getNodeName();
							 atr.put("jid", ((Element) modlist.item(k)).getAttribute("jid"));
							 atr.put("destination", ((Element) modlist.item(k)).getAttribute("destination"));
							 atr.put("filename", ((Element) modlist.item(k)).getAttribute("filename"));
							 msg.add(new Msg(MsgDict.UPLOAD_REQUEST,atr));
						}
					}
					if(type.equals("download"))
					{
						Element mod = (Element)nll.item(j);
						NodeList modlist =mod.getChildNodes();
						for(int k=0; k<modlist.getLength(); k++)
						{
							 atr.clear();
							 //String t = modlist.item(k).getNodeName();
							 int downloadType = Integer.parseInt(((Element) modlist.item(k)).getAttribute("type"));
							 if(downloadType == MsgDict.DOWNLOAD_REQUEST)
							 {
								 atr.put("jid", ((Element) modlist.item(k)).getAttribute("jid"));
								 atr.put("destination", ((Element) modlist.item(k)).getAttribute("destination"));
								 atr.put("filename", ((Element) modlist.item(k)).getAttribute("filename"));
								 msg.add(new Msg(MsgDict.DOWNLOAD_REQUEST,atr));
							 }
							 else if(downloadType == MsgDict.DOWNLOAD_REQUEST_FRIEND)
							 {
								 atr.put("jid", ((Element) modlist.item(k)).getAttribute("jid"));
								 atr.put("destination", ((Element) modlist.item(k)).getAttribute("destination"));
								 atr.put("filename", ((Element) modlist.item(k)).getAttribute("filename"));
								 msg.add(new Msg(MsgDict.DOWNLOAD_REQUEST_FRIEND,atr));
							 }
						}
					}
				}
				
			}
		}
		return msg;
	}
}
