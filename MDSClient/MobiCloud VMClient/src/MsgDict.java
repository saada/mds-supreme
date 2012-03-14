
public class MsgDict {
	public final static int LOG = 0x0000;
	
	public final static int SHARE_FILE_REQUEST = 0x1000;
	public final static int SHARE_FILE_ACCEPTED = 0x1003;
	public final static int SHARE_FILE_REJECTED = 0x1004;
	public final static int SHARE_FILE_VM_SEND_START = 0x1010;
	public final static int SHARE_FILE_VM_RECEIVE_START = 0x1011;
	public final static int SHARE_FILE_VM_SEND_ENCRYPT_CODE = 0x1012;
	public final static int SHARE_FILE_COMPLETE = 0x1020;
	public final static int SHARE_FILE_UPLOAD_AFTER_COMPLETE = 0x1021;
	
	
	public static final int S_AUTENTICATION_SUCEED = 0x0010;
	public static final int S_AUTENTICATION_FAILED = 0x0011;
	public static final int S_TOAST_S = 0x0014;
	public static final int S_TOAST_L = 0x0015;
	public static final int S_CHAT = 0x0018;
	public static final int S_DISCONNECTED = 0x001a;
	public static final int C_SERVICE_BOUND = 0x0020;
	public static final int C_ROSTER_ENTRY_ADDED = 0x26;
	public static final int C_ROSTER_ENTRY_UPDATED = 0x27;
	public static final int C_ROSTER_ENTRY_REMOVED = 0x28;
	public static final int C_PRESENCE_CHANGED = 0x29;
	public static final int C_ROSTER_RECEIVED = 0x2a;
	public static final int C_ROSTER_UPDATED = 0x30;
	public static final int S_RECEIVE_FILE = 0x33;

	public static final int CHAT_REQUEST = 0X3000;

	public static final int DES_UPDATE = 0X4000;
	
	//Booleans for results of requests
	public static final int REQUEST_FAILED = 0x7771;
	public static final int REQUEST_SUCCESSFUL = 0x7772;
	
	//File List
	public static final int FILELIST_REQUEST = 0X2000;
	public static final int FILELIST_UPDATE = 0X2001;
	
	//TCP FILE TRANSFER 3/1/2012
	public static final int FILETRANSFER_REQUEST = 0X2002;
	public static final int FILETRANSFER_RECEIVED = 0X2003;
	
	//Access Control
	public static final int USERPERMISSION_REQUEST = 0x51;
	public static final int PRIVATE = 0x5550;
	public static final int PUBLIC = 0x5551;
	public static final int SHARED = 0x5552;

	//Rename
	public static final int RENAME_REQUEST = 0x2004;
	//Move
	public static final int MOVE_REQUEST = 0x2005;
	//Create Directory
	public static final int CREATEDIRECTORY_REQUEST = 0x2006;
	//Delete
	public static final int DELETE_REQUEST = 0x2007;
	
	

	
}
