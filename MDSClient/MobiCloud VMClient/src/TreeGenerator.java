
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import Mobi.xmpp.Entity;
import Mobi.tree.Node;


public class TreeGenerator {
	Node root;
	
	public void generate(ResultSet set) throws SQLException
	{
		//create mutable tree nodes
		Date today = Calendar.getInstance().getTime();
		root = new Node(new Entity("0", "dir", "VMFILE",(long)0,"/home/saada/Desktop/",today,"public"));
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
			entity = new Entity(e_id, e_type, e_name, e_size, e_url, e_modate, "public");
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
			printTree(root);
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
		System.out.println("Level = "+counter);
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
