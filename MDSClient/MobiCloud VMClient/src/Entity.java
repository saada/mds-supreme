import java.io.Serializable;
import java.util.Date;

public class Entity implements Serializable{
	public String e_id;
	public String e_type;
	public String e_name;
	public Long e_size;
	public String e_url;
	public Date e_modate;
	
	public Entity(String e_id, String e_type, String e_name, 
			Long e_size, String e_url, Date e_modate)
	{
		this.e_id = e_id;
		this.e_type = e_type;
		this.e_name = e_name;
		this.e_size = e_size;
		this.e_url = e_url;
		this.e_modate = e_modate;
		
		
	}
}
