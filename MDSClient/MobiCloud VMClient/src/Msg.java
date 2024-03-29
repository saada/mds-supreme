import java.util.Hashtable;


public class Msg {
	public int type;
	private Hashtable<String, String> atr;
	private String data;
	public int count;

//	public String getData(int index) {
//		index++;
//		try {
//			return data[index];
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
	
	public String getData()
	{
		return data;
	}
	
	public String getAtr(String attribute){
		return atr.get(attribute);
	}

	public Msg(int tagtype, Hashtable<String,String> attribute, String dataset) {
		
		type = tagtype;
		atr = attribute;
		data = dataset;
	}

	public Msg(int tagtype, Hashtable<String,String> attribute) {
		type = tagtype;
		atr = (Hashtable<String, String>) attribute.clone();
	}

	public static String msgString(String[] msgs) {
		String result = "";
		for (String s : msgs) {
			result += ("%@!" + s);
		}
		return result;
	}

//	public String toString() {
//		String result = "";
//		for (String s : data) {
//			result += ("%@!" + s);
//		}
//		return result;
//	}
}
