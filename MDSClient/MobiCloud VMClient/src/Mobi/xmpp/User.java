package Mobi.xmpp;

import org.jivesoftware.smack.packet.Presence;

public class User {
	
	private int accountId;
	private String dispName;
	private String jid;
	private String status;
	private Presence.Mode mode;
	private boolean isOnline;
	private byte[] avatar;
	
	private boolean isselected;

	

	public User(String dispName, String jid, String status, boolean isselected) {
		super();
		this.dispName = dispName;
		this.jid = jid;
		this.status = status;
		this.isselected = isselected;
	}

	public boolean isIsselected() {
		return isselected;
	}

	public void setIsselected(boolean isselected) {
		this.isselected = isselected;
	}

	public int getAccountId() {
		synchronized (this) {
			return accountId;
		}
	}

	public void setAccountId(int accountId) {
		synchronized (this) {
			this.accountId = accountId;
		}
	}

	public String getDispName() {
		synchronized (this) {
			return dispName;
		}
	}

	public void setDispName(String dispName) {
		synchronized (this) {
			this.dispName = dispName;
		}
	}

	public String getJid() {
		synchronized (this) {
			return jid;
		}
	}

	public void setJid(String jid) {
		synchronized (this) {
			this.jid = jid;
		}
	}

	public String getStatus() {
		synchronized (this) {
			return status;
		}
	}

	public void setStatus(String status) {
		synchronized (this) {
			this.status = status;
		}
	}

	public Presence.Mode getMode() {
		synchronized (this) {
			return mode;
		}
	}

	public void setMode(Presence.Mode mode) {
		synchronized (this) {
			this.mode = mode;
		}
	}

	public boolean isOnline() {
		synchronized (this) {
			return isOnline;
		}
	}

	public void setOnline(boolean isOnline) {
		synchronized (this) {
			this.isOnline = isOnline;
		}
	}

	public byte[] getAvatar() {
		synchronized (this) {
			return avatar;
		}
	}

	public void setAvatar(byte[] avatar) {
		synchronized (this) {
			this.avatar = avatar;
		}
	}

	
}
