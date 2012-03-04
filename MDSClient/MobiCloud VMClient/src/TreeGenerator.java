
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import Mobi.xmpp.Entity;
import Mobi.tree.Node;

public class TreeGenerator {
	Node root;
//	public String greatestCommonPrefix(String a, String b) {
//	    int minLength = Math.min(a.length(), b.length());
//	    for (int i = 0; i < minLength; i++) {
//	        if (a.charAt(i) != b.charAt(i)) {
//	            return a.substring(0, i);
//	        }
//	    }
//	    return a.substring(0, minLength);
//	}
	public boolean containsEid(ResultSet set, int e_id) throws SQLException
	{
		set.beforeFirst();
		while(set.next())
		{
			if(e_id ==  set.getInt("E_id"))
				return true;
		}
		return false;
	}
	
	public Node generatePermitted(ResultSet set) throws SQLException
	{
		Node newTree = root;
		Node cursor = newTree;
		if(root != null)
		{
			while(cursor != null)
			{
				if(cursor.isLeaf())
				{
					if(!containsEid(set,Integer.parseInt(((Entity)cursor.getOb()).e_id)))
					{
						Node temp = cursor;
						cursor=cursor.getParent();
						cursor.remove(temp);
					}
				}
				if(cursor.getChildren().size()!= 0)
					cursor = cursor.getNextNode();
				else
				{
					if(!containsEid(set,Integer.parseInt(((Entity)cursor.getOb()).e_id)))
					{
						Node temp = cursor;
						cursor=cursor.getParent();
						cursor.remove(temp);
					}
					cursor = cursor.getNextNode();
				}
			}
		}
		printTree(newTree);
		return newTree;
	}
//		root = new Node(new Entity("0","dir","SHARED ENTITIES"));
//		//get size of resultset
//		int size =0;  
//		if (set != null)   
//		{  
//		  set.beforeFirst();  
//		  set.last();  
//		  size = set.getRow();
//		}
//		//
//		if(size == 0)
//		{
//			root = new Node(new Entity("0","dir","NO SHARED FILES"));
//			return;
//		}
//		
//		//find root node
//		set.first();
//		String prefix = set.getString("E_url");
//		set.beforeFirst();
//		while(set.next())
//		{
//			String e_id = set.getString("e_id");
//			String e_type = set.getString("e_type");
//			String e_name = set.getString("e_name");
//			Long e_size = set.getLong("e_size");
//			String e_url = set.getString("e_url");
//			Date e_modate = set.getDate("e_modate");
//			set.getString("E_url");
//			if(size == 1)
//			{
//				root.add(new Node(new Entity(e_id, e_type, e_name, e_size, e_url, e_modate, 2)));
//				return;
//			}
//			else if(!set.isLast())
//			{
//				prefix = greatestCommonPrefix(prefix, set.getString("E_url"));
//			}
//		}
//		//after getting prefix...?
//		root.add(new Node(new Entity("-1", "dir",prefix.substring(prefix.lastIndexOf('/')))));

	
	
	public void generate(ResultSet set, MySQLAccess dao) throws SQLException
	{
		//create mutable tree nodes
		Date today = Calendar.getInstance().getTime();
		root = new Node(new Entity("0", "dir", "VMFILE",(long)0,System.getProperty("user.home")+"/Desktop/",today,1));
		Node node;
		String prevUrl = "";
		Entity entity;
		
		while (set.next()) {
			String e_id = set.getString("e_id");
			String e_type = set.getString("e_type");
			String e_name = set.getString("e_name");
			Long e_size = set.getLong("e_size");
			String e_url = set.getString("e_url");
			Date e_modate = set.getDate("e_modate");
			
			//store current set in an Entity data structure instance and add to tree
			entity = new Entity(e_id, e_type, e_name, e_size, e_url, e_modate, dao.getPermission(Integer.parseInt(e_id)));
			node = new Node(entity);
			if(root.isRoot())
			{
				root.add(node);
				if(e_type.equals("dir"))
				{
					root = (Node) root.getLastChildren();
					prevUrl = ((Entity)((Node) root).getUserObject()).e_url;
				}
			}
			else
			{
				if(e_url.startsWith(prevUrl) && (level(e_url) == (level(prevUrl)+1)))
				{
					root.add(node);
					if(e_type.equals("dir"))
					{
						root = (Node) root.getLastChildren();
						prevUrl = ((Entity)((Node) root).getUserObject()).e_url;
					}
				}
				else
				{
					while(!(e_url.startsWith(prevUrl) && (level(e_url) == (level(prevUrl)+1))))
					{
						root = (Node) root.getParent();
						prevUrl = ((Entity)((Node) root).getUserObject()).e_url;
					}
					root.add(node);
					if(e_type.equals("dir"))
					{
						root = (Node) root.getLastChildren();
						prevUrl = ((Entity)((Node) root).getUserObject()).e_url;
					}
				}
			}
			//printTree(root);
		}
		while(!root.isRoot())
		{
			root = (Node) root.getParent();
		}
		printTree(root);
	}
	private int level(String url) {
		int counter = 0;
		for(int i=0; i<url.length(); i++)
		{
			if(url.charAt(i) == '/')
					counter++;
		}
		//System.out.println("Level = "+counter);
		return counter;
	}
	public void printTree(Node root)
	{
		while(root != null)
		{
			for(int i=0; i<root.getLevel(); i++)
				System.out.print("|--");
			
			if(root.isRoot())
				System.out.print(((Entity)root.getUserObject()).e_name + "\n");
			else
				System.out.print(((Entity)root.getUserObject()).e_name + "\n");
			
			root = root.getNextNode();
		}
	}
}
