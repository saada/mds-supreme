
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class XMLParser {
	public String str;
	private DocumentBuilderFactory dbf;
	private DocumentBuilder db;
	private NodeList nl;
	private int type;
	private Hashtable<String, String> atr;
	private String[] data;
	private Msg[] msg;

	
	public XMLParser(String str) throws ParserConfigurationException, SAXException, IOException {
		super();
		this.str = str;
		dbf=DocumentBuilderFactory.newInstance();		
		db=dbf.newDocumentBuilder();
		InputStream is=new ByteArrayInputStream(str.getBytes());
		Document dm=db.parse(is);
		nl=dm.getElementsByTagName("requests");
		atr = new Hashtable<String, String>();
		msg = new Msg[99999];
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


	public Msg[] getMsgList() {
		if(!this.isEmpty()){
			for(int i=0; i<nl.getLength();i++){
				Element el=(Element)nl.item(i);
				NodeList nll=el.getChildNodes();
				
				for(int j=0; j<nll.getLength();j++){
					String type = nll.item(j).getNodeName();
					if(type.equals("view")){
						atr.put("type", ((Element)nll.item(j)).getAttribute("type"));
						atr.put("jid", ((Element)nll.item(j)).getAttribute("jid"));
						//data =((Element)nll.item(j)).getNodeValue().split("%@!");
						msg[j]= new Msg(Integer.parseInt(atr.get("type")), atr);
					}
					if(type.equals("dd")){
						
					}
					
				}
				
			}
		}
		return msg;
	}
	
	
}
