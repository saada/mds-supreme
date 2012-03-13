import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;

public class MySQLAccess {
        private Connection connect = null;
        private Statement statement = null;
        private PreparedStatement preparedStatement = null;
        private ResultSet resultSet = null;
        
        public MySQLAccess() {
                try {
                        setUpConn();
                        createStmt();
                } catch(Exception e) {
                        e.printStackTrace();
                }
        }
        
        public void setUpConn() throws Exception {
                // This will load the MySQL driver, each DB has its own driver
                Class.forName("com.mysql.jdbc.Driver");
                // Setup the connection with the DB
                connect = DriverManager.getConnection("jdbc:mysql://localhost/mds_db?"
                                                           + "user=root&password=saada");
        }
        
        public void createStmt() throws Exception {
                // Statements allow to issue SQL queries to the database
                statement = connect.createStatement();
        }

        //read all attributes from a certain table
        public ResultSet selectAll(String table) throws Exception {
                try {                   
                        statement = connect.createStatement();
                        // Result set get the result of the SQL query
                        resultSet = statement.executeQuery("select * from mds_db." + table + ";");
                        
                        return resultSet;
                } catch (Exception e) {
                        throw e;
                }
        }
        
      //read all attributes from a certain table
        public ResultSet selectEntityID() throws Exception {
                try {                   
                        statement = connect.createStatement();
                        // Result set get the result of the SQL query
                        resultSet = statement.executeQuery("select E_id from mds_db.T_Entity;");
                        
                        return resultSet;
                } catch (Exception e) {
                        throw e;
                }
        }
        
//        public ResultSet selectEntityIDByPath(String url, String name) throws Exception {
//            try {                   
//                    statement = connect.createStatement();
//                    // Result set get the result of the SQL query
//                    resultSet = statement.executeQuery("select E_id from mds_db.t_entity where E_url=" + url + "and E_name=" + name + ";");
//                    
//                    return resultSet;
//            } catch (Exception e) {
//                    throw e;
//            }
//    }
//        
        public void delete(String table, int id) throws Exception {
                try {
                        preparedStatement = connect
                                        .prepareStatement("delete from mds_db." + table + " where E_id= ? ; ");
                        preparedStatement.setInt(1, id);
                        preparedStatement.executeUpdate();
                } catch (Exception e) {
                        throw e;
                }
        }
        
        public void deleteAll(String table) throws Exception {
                try {
                        preparedStatement = connect
                                        .prepareStatement("delete from mds_db." + table + " ; ");
                        preparedStatement.executeUpdate();
                } catch (Exception e) {
                        throw e;
                }
        }
        
        public void resetAutoID(String table) throws Exception {
                try {
                        preparedStatement = connect
                                        .prepareStatement("alter table mds_db." + table + " auto_increment = 0 ; ");
                        preparedStatement.executeUpdate();
                } catch (Exception e) {
                        throw e;
                }
        }
        
        public void insertEntity(String record) throws Exception {
                try {
                        // PreparedStatements can use variables and are more efficient
                        preparedStatement = connect
                                        .prepareStatement("insert into  mds_db.T_Entity values (default, ?, ?, ?, ?, ?)");
                        
                        String[] attr = record.split(", ");
                        
                        for(int i=0; i<attr.length; i++) {
                                switch(i)
                                {
                                        default:
                                        {
                                                //System.out.println(i+":"+attr[i]+", casedef");
                                                preparedStatement.setString(i+1, attr[i]);
                                                break;
                                        }
                                        case 2:
                                        {
                                                //System.out.println(i+":"+attr[i]+", case2");
                                                preparedStatement.setDouble(i+1,
                                                                        Double.parseDouble(
                                                                                attr[i].substring(0, attr[i].indexOf("KB"))));
                                                break;
                                        }
                                        case 4:
                                        {
                                                //System.out.println(i+":"+attr[i]+", case4");
                                                preparedStatement.setDate(i+1, java.sql.Date.valueOf(attr[i]));
                                                break;
                                        }
                                        
                                };
                        }
                        preparedStatement.executeUpdate();
                }catch (Exception e) {
                        throw e;
                }       
        }
        
        public void insertUserPermit(int entityID, String userID, int permission) throws Exception {
        	 preparedStatement = connect
                     .prepareStatement("insert into  mds_db.T_UserPermit values (default, '" + entityID + "', '" + permission + "', '" + userID + "')");
        	 
        	 preparedStatement.executeUpdate();
        }
        
        public void insertGroupPermit(int entityID, String groupID, int permission) throws Exception {
       	 preparedStatement = connect
                    .prepareStatement("insert into  mds_db.t_grouppermit values (default, '" + entityID + "', '" + permission + "', '" + groupID + "')");
       	 
       	 preparedStatement.executeUpdate();
       }
        
        public void updateEntityModate(String newDate, int id) throws Exception {
                try {
                        // PreparedStatements can use variables and are more efficient
                        preparedStatement = connect
                                        .prepareStatement("update mds_db.T_Entity set modate = " + java.sql.Date.valueOf(newDate) +"where id = ?;)");
                        
                        preparedStatement.setInt(1, id);
                        preparedStatement.executeUpdate();
                }catch (Exception e) {
                        throw e;
                }               
        }

        public void writeMetaData(ResultSet resultSet) throws SQLException {
                //      Now get some metadata from the database
                // Result set get the result of the SQL query
                
                System.out.println("The columns in the table are: ");
                
                System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
                for  (int i = 1; i<= resultSet.getMetaData().getColumnCount(); i++){
                        System.out.println("Column " +i  + " "+ resultSet.getMetaData().getColumnName(i));
                }
        }

        public void writeResultSet(ResultSet resultSet) throws SQLException {
                // ResultSet is initially before the first data set
                while (resultSet.next()) {
                        // It is possible to get the columns via name
                        // also possible to get the columns via the column number
                        // which starts at 1
                        // e.g. resultSet.getSTring(2);
                        String id = resultSet.getString("e_id");
                        String type = resultSet.getString("e_type");
                        String name = resultSet.getString("e_name");
                        Long size = resultSet.getLong("e_size");
                        String url = resultSet.getString("e_url");
                        Date modifiedDate = resultSet.getDate("e_modate");
                        
                        System.out.print("id: " + id);
                        System.out.print(", type: " + type);
                        System.out.print(", name: " + name);
                        System.out.print(", size: "+ size);
                        System.out.print(", url: " + url);
                        System.out.print(", modified: " + modifiedDate);
                        System.out.print("\n**************************\n");
                }
        }
        
        //Access Control
        public void addAllUsers(Collection<RosterEntry> users) throws Exception
        {                         
              for (RosterEntry r : users) {
            	  resultSet = selectEntityID();   
            	  
            	  while(resultSet.next()) {            		  
          	    	int entityID = resultSet.getInt("E_id");          	    	
          	    	insertUserPermit(entityID, r.getName(), 0);
          	    }
  			}
        }
        
        public void addAllGroups(Collection<RosterGroup> groups) throws Exception
        {                         
              for (RosterGroup r : groups) {
            	  resultSet = selectEntityID();   
            	  
            	  while(resultSet.next()) {            		  
          	    	int entityID = resultSet.getInt("E_id");          	    	
          	    	insertGroupPermit(entityID, r.getName(), 0);
          	    }
  			}
            	  
        }
        
//        public void setEntitytoPublicOrPrivate(String path, int permission) throws Exception {
//        	int i = path.lastIndexOf('\\');
//        	String entity_name = path.substring(i);
//        	String url = path.substring(0, i);
//        	
//        	ResultSet resultSet = selectEntityIDByPath(url, entity_name);
//        	
//        	int e_id = -1;
//        	while (resultSet.next()) {
//        		e_id = resultSet.getInt("E_id"); 
//        	}
//        	
//        	boolean test = false;
//        	if(e_id != -1) {
//        		test = updateEntityPermission(e_id, permission);
//        	}
//        	
//        	if(test)	System.out.println("Successful!");
//        	else	System.out.println("Unsuccessful!");
//        }
        
        public boolean updateEntityPermission(int e_id, int permission)
        {
        	try {
                //change entity permission
                preparedStatement = connect
                        .prepareStatement("UPDATE mds_db.T_UserPermit set UP_permission = ? where E_id = ?;");
                preparedStatement.setInt(1, permission);
                preparedStatement.setInt(2, e_id);
                preparedStatement.executeUpdate();

                //get specific entity to check if its type is directory
        		preparedStatement = connect.prepareStatement("SELECT * from mds_db.T_Entity where E_id = ?;");
        		preparedStatement.setInt(1, e_id);
                preparedStatement.executeQuery();
                ResultSet rs = preparedStatement.getResultSet();
                
                //get entire entity table to find sub-entities
        		preparedStatement = connect.prepareStatement("SELECT * from mds_db.T_Entity;");
        		preparedStatement.executeQuery();
                ResultSet sub_rs = preparedStatement.getResultSet();
                
                //change sub-entity permissions if applicable (directory entity)
                while(rs.next())
                {
                	if(rs.getString("E_type").equals("dir"))
        			{
                		String dir_url = rs.getString("E_url")+rs.getString("E_name")+"/";
                		while(sub_rs.next())
                		{	
                			if(sub_rs.getString("E_type").equals("dir") && sub_rs.getString("E_url").startsWith(dir_url))
                			{
                				updateEntityPermission(sub_rs.getInt("E_id"), permission);
                			}
                			else
                			{
	                			String sub_url = sub_rs.getString("E_url");
	                			if(sub_url.startsWith(dir_url))
	                			{
	                				preparedStatement = connect
	                                        .prepareStatement("UPDATE mds_db.T_UserPermit set UP_permission = ? where E_id = ?;");
			                        preparedStatement.setInt(1, permission);
			                        preparedStatement.setInt(2, sub_rs.getInt("E_id"));
			                        preparedStatement.executeUpdate();
	                			}
                			}
                		}
        			}
                }
            	return true;
	        }
        	catch (Exception e) {
	            return false;
	        }  
        }
        
        public boolean updateUserPermission(int e_id, String jid, int permission) throws Exception
        {
        	/////////////////////check if the entity is public. If yes, we should set the entity to private before we update user permission
        	boolean isEntityPublic = true;
        	
        	preparedStatement = connect.prepareStatement("SELECT * from mds_db.T_UserPermit where E_id = ?;");
    		preparedStatement.setInt(1, e_id);
            preparedStatement.executeQuery();
            ResultSet allUserPermissions = preparedStatement.getResultSet();
        	
        	while(allUserPermissions.next()) {            		  
      	    	int p = allUserPermissions.getInt("UP_permission");
      	    	if(p != 1)	{
      	    		isEntityPublic = false;
      	    		break;
      	    	}
      	    }
        	///////////////////check if public ends
        	
        	switch(permission)
        	{
	        	case MsgDict.PUBLIC:
	        	{
	        		permission = 1;
	        		break;
	        	}
	        	case MsgDict.PRIVATE:
	        	{
	        		permission = 0;
	        		break;
	        	}
	        	case MsgDict.SHARED:
	        	{
	        		permission = 2;
	        		break;
	        	}
        	
        	}
        		
        	try {
        		///////////////////if the entity is public, we set the entity as private first
        		if(isEntityPublic) {
        			updateEntityPermission(e_id, 0);
        		}
                //change entity permission
                preparedStatement = connect
                        .prepareStatement("UPDATE mds_db.T_UserPermit set UP_permission = ? where E_id = ? and U_id = ?;");
                preparedStatement.setInt(1, permission);
                preparedStatement.setInt(2, e_id);
                preparedStatement.setString(3, jid);
                preparedStatement.executeUpdate();
        		

                //get specific entity to check if its type is directory
        		preparedStatement = connect.prepareStatement("SELECT * from mds_db.T_Entity where E_id = ?;");
        		preparedStatement.setInt(1, e_id);
                preparedStatement.executeQuery();
                ResultSet rs = preparedStatement.getResultSet();
                
                //get entire entity table to find sub-entities
        		preparedStatement = connect.prepareStatement("SELECT * from mds_db.T_Entity;");
        		preparedStatement.executeQuery();
                ResultSet sub_rs = preparedStatement.getResultSet();
                
                //change sub-entity permissions if applicable (directory entity)
                while(rs.next())
                {
                	if(rs.getString("E_type").equals("dir"))
        			{
                		String dir_url = rs.getString("E_url")+rs.getString("E_name")+"/";
                		while(sub_rs.next())
                		{	
                			if(sub_rs.getString("E_type").equals("dir") && sub_rs.getString("E_url").startsWith(dir_url))
                			{
                				updateUserPermission(sub_rs.getInt("E_id"), jid, permission);
                			}
                			else
                			{
	                			String sub_url = sub_rs.getString("E_url");
	                			if(sub_url.startsWith(dir_url))
	                			{
	                				preparedStatement = connect
	                                        .prepareStatement("UPDATE mds_db.T_UserPermit set UP_permission = ? where E_id = ? and U_id = ?;");
			                        preparedStatement.setInt(1, permission);
			                        preparedStatement.setInt(2, sub_rs.getInt("E_id"));
			                        preparedStatement.setString(3, jid);
			                        preparedStatement.executeUpdate();
	                			}
                			}
                		}
        			}
                }
            	return true;
	        }
        	catch (Exception e) {
	            return false;
	        }  
        }
        
        
        public boolean updateGroupPermission(int e_id, String gid, int permission)
        {
        	//Change group permission means we have to change the permission of each entity for each user of that group.
        	//In other words, we have to change
        	//1. Group entity permissions
        	//2. User entity permissions that belong to the corresponding group       	
        	
        	
        	
        	
        	try {
        		//change group permission
                preparedStatement = connect
                                .prepareStatement("UPDATE mds_db.T_UserPermit set GP_permission = ? where E_id = ? , G_id = ?)");
                preparedStatement.setInt(1, permission);
                preparedStatement.setInt(2, e_id);
                preparedStatement.setString(3, gid);
                preparedStatement.executeUpdate();
            	return true;
	        }
        	catch (Exception e) {
	            return false;
	        }  
        }
        
        // You need to close the resultSet
        public void close() {
                try {
                        if (resultSet != null) {
                                resultSet.close();
                        }

                        if (statement != null) {
                                statement.close();
                        }

                        if (connect != null) {
                                connect.close();
                        }
                } catch (Exception e) {

                }
        }

		public ResultSet selectPermittedEntites(String jid) throws SQLException {
			preparedStatement = connect.prepareStatement("select mds_db.T_Entity.* from ((SELECT * FROM mds_db.T_UserPermit WHERE U_id = ? and (UP_permission = 2 or UP_permission = 1)) as per join mds_db.T_Entity on per.E_id=mds_db.T_Entity.E_id);");
			preparedStatement.setString(1,jid);
			return preparedStatement.executeQuery();
		}

		public int getPermission(int e_id) throws SQLException {
			int perm = 0;
			preparedStatement = connect.prepareStatement("select UP_permission from mds_db.T_UserPermit where E_id = ?;");
			preparedStatement.setInt(1,e_id);
			ResultSet result = preparedStatement.executeQuery();
			while(result.next())
			{
				switch (result.getInt("UP_permission"))
				{
					case 1:
					{
						perm = 1;
						break;
					}
					case 2:
					{
						perm = 2;
						break;
					}
				};
			}
			return perm;
		}

		public String getSharedBy(int e_id) throws SQLException {
			preparedStatement = connect.prepareStatement("select U_id from mds_db.T_UserPermit where E_id = ? and UP_permission = ?;");
			preparedStatement.setInt(1, e_id);
			preparedStatement.setInt(2, 1);
			ResultSet result = preparedStatement.executeQuery();
			result.last();
			if(result.getRow() != 0)
				return "PUBLIC";
			
			preparedStatement.setInt(2, 2);
			result = preparedStatement.executeQuery();
			result.last();
			if(result.getRow() == 0)
				return "PRIVATE";
			
			result.beforeFirst();
			String str = "";
			while(result.next())
			{
				if(result.isLast())
					str += result.getString("U_id");
				else
					str += result.getString("U_id")+",";
			}
			return str;
			
		}
}