import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Upload_Acc extends Thread {

	DataInputStream input;

	DataOutputStream output;
	    
	String path;
	    
	String file;
	
	int serverPort;

    public Upload_Acc (String p, String f) {
    	path = p;
    	file = f;
    	serverPort = 6880;
	  }
	  public void run() {
	
	    	Socket sock = null;
	  	  	ServerSocket serverListener;
	  	  	try {
	  	  		System.out.println("UPLOAD: before");
	  	  		serverListener = new ServerSocket(serverPort);
				sock = serverListener.accept();
				System.out.println("UPLOAD: middle");
				sock.setReuseAddress(true);
//				sock.setSoTimeout(10000);
				System.out.println("UPLOAD: after");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	      	try {
	      			System.out.println("UPLOAD: "+path+"\t"+file);
	      			DataInputStream input = new DataInputStream( sock.getInputStream());
	      			DataOutputStream output = new DataOutputStream( sock.getOutputStream());
					String fpath = path+file;		
//					output.writeInt(fpath.length());
//		        	output.writeBytes(fpath);
					  
					try
					{        	
			        	int leng=input.readInt();
			        	byte[] digit2 = new byte[leng];
	
		            	File file = new File(fpath);
		    			if(!file.exists()){
		    				file.createNewFile();
		    			}
		    			FileOutputStream fos = new FileOutputStream(fpath);
		    			for(int i=0; i<leng; i++)
		    			{
		    				digit2[i] = input.readByte();
		    				//setProgress(leng);
		    			}
		    			fos.write(digit2);
		    			fos.close();
			        	
	
						output.writeInt(8);
						output.writeBytes("finished");
						Thread.sleep(1000);
						//this.sleep(1000);
					}
					catch(FileNotFoundException ex)
					{
						System.out.println("FileNotFoundException: " + ex);
					}
					catch(IOException ioe)
					{
						System.out.println("IOException: " + ioe);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
			}
	    }
}
