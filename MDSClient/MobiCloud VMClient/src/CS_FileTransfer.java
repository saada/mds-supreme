import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class CS_FileTransfer implements Runnable {
	String DomainName;
	int serverPort;
	String filename;
	Thread[] thread;
	volatile Socket sock = new Socket();
	ServerSocket listenSocket;
	int Tcounter;
	
	public CS_FileTransfer( int port, String file, boolean portInUse) throws IOException
	{
		serverPort = port;
		filename = file;
		thread = new Thread[1024];
		Tcounter = 0;
		if(!portInUse)
			listenSocket = new ServerSocket(serverPort);
	}
	
	public CS_FileTransfer() {
	}
	
	public void setDomain(String d)
	{
		DomainName=d;
		
	}
	public boolean invoke(boolean flag) throws IOException
	{
		System.out.println(DomainName+ "  " + serverPort );
		//InetAddress a = InetAddress.getLocalHost();
		sock = new Socket(DomainName,serverPort);
		//System.out.println("FUFUFUFUF");
		Invoke_Connnection invoke = new Invoke_Connnection(DomainName,filename,sock,flag);
		invoke.start();
		ProgressMonitor monitor = new ProgressMonitor(invoke);
		monitor.start();
		//System.out.println("finished");
		return(invoke.isAlive());
		
	}
	
	@Override
	public void run() {
		while(true)
		{
			System.out.println("Listening for connections...");
			try {
				sock = listenSocket.accept();
				System.out.println("Accepted");
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			thread[Tcounter] = new Accept_Connnection(filename, sock);
			thread[Tcounter].start();
			while(thread[Tcounter].isAlive())
			{
				Tcounter++;
				Tcounter%=1024;
				if(thread[Tcounter]==null)
				{
					break;
				}
			}
			System.out.println("Running");
		}
	}
	
	public void closeSocket()
	{
		try {
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	

}
