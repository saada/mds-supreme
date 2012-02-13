import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.swing.tree.DefaultMutableTreeNode;
public class TreeGenerator {
	
	public void generate(ResultSet set) throws SQLException
	{
		//create mutable tree nodes
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("ROOT");
		DefaultMutableTreeNode node;
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
			entity = new Entity(e_id, e_type, e_name, e_size, e_url, e_modate);
			node = new DefaultMutableTreeNode(entity);
			if(root.isRoot())
			{
				root.add(node);
				if(e_type.equals("dir"))
				{
					root = (DefaultMutableTreeNode) root.getLastChild();
					prevUrl = ((Entity)((DefaultMutableTreeNode) root).getUserObject()).e_url;
				}
			}
			else
			{
				if(e_type.equals("dir"))
				{
					if(e_url.length() > prevUrl.length() && e_url.startsWith(prevUrl))
					{
						root.add(node);
						root = (DefaultMutableTreeNode) root.getLastChild();
						prevUrl = ((Entity)((DefaultMutableTreeNode) root).getUserObject()).e_url;
					}
					else
					{
						while(!root.isRoot() && !e_url.startsWith(prevUrl))
						{
							root = (DefaultMutableTreeNode) root.getParent();
						}
						if(!root.isRoot())
							prevUrl = ((Entity)((DefaultMutableTreeNode) root).getUserObject()).e_url;
						
						root.add(node);
					}
				}
				else //if (e_type == "file")
				{
					if(e_url.length() > prevUrl.length() && e_url.startsWith(prevUrl))
					{
						root.add(node);
					}
				}
			}
		}
		printTree(root);
	}
	public void printTree(DefaultMutableTreeNode root)
	{
		while(root != null)
		{
			for(int i=0; i<root.getLevel(); i++)
				System.out.print("|--");
			
			if(root.isRoot())
				System.out.print(root.getUserObject()+"\n");
			else
				System.out.print(((Entity)root.getUserObject()).e_name + "\n");
			
			root = root.getNextNode();
		}
	}
}
