package Mobi.xmpp;

public class Group {
	
	private String groupName;	
	
	public Group(String groupName, boolean selected) {
		super();
		this.setGroupName(groupName);
		
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
}
