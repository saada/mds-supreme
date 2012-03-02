package Mobi.xmpp;

import java.io.Serializable;
import java.util.Date;

public class Entity implements Serializable{
	
	
	public String e_type;
	
	public String e_id;	
	public String e_owner;
	public int e_shareby;
	public int getE_shareby() {
		return e_shareby;
	}
	public void setE_shareby(int e_shareby) {
		this.e_shareby = e_shareby;
	}

	public String e_name;
	public Long e_size;
	public String e_url;
	public Date e_modate;
	
	public Entity(String e_id, String e_type, String e_name,
			Long e_size, String e_url, Date e_modate, int e_shareby){
		this.e_id = e_id;
		this.e_type = e_type;
		this.e_name = e_name;
		this.e_size = e_size;
		this.e_url = e_url; 
		this.e_modate = e_modate;
		this.e_shareby = e_shareby;
	}
	public Entity(String e_id, String e_type, String e_name
			){
		this.e_id = e_id;
		this.e_type = e_type;
		this.e_name = e_name;
		
	}
	

	public String getE_id() {
		return e_id;
	}

	public void setE_id(String e_id) {
		this.e_id = e_id;
	}

	public String getE_type() {
		return e_type;
	}

	public void setE_type(String e_type) {
		this.e_type = e_type;
	}

	public String getE_owner() {
		return e_owner;
	}

	public void setE_owner(String e_owner) {
		this.e_owner = e_owner;
	}

	public String getE_name() {
		return e_name;
	}

	public void setE_name(String e_name) {
		this.e_name = e_name;
	}

	public Long getE_size() {
		return e_size;
	}

	public void setE_size(Long e_size) {
		this.e_size = e_size;
	}

	public String getE_url() {
		return e_url;
	}

	public void setE_url(String e_url) {
		this.e_url = e_url;
	}

	public Date getE_modate() {
		return e_modate;
	}

	public void setE_modate(Date e_modate) {
		this.e_modate = e_modate;
	}

}
