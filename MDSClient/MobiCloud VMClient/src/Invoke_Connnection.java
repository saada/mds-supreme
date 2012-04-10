import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;


public class Invoke_Connnection extends Thread{

	DataInputStream input;
	DataOutputStream output;
    String DomainName;
    Socket sock;
    String filename;
    FileInputStream fin;
    FileChannel ch;
    boolean flag = true;
    int current;
    double progress;
    
    public Invoke_Connnection(String domain,String file, Socket sock1, boolean f)
    {
    	DomainName=domain;
    	sock = sock1;
    	filename = file;
    	flag = f;
    }
    
    public double getProgress()
    {
    	return progress;
    }
    
    public void setProgress(int leng)
    {
    	progress = ((double)current)/((double)leng);
    }

    public void run()
    {
    	try {
				input = new DataInputStream( sock.getInputStream());
				output = new DataOutputStream( sock.getOutputStream());
				String[] s = filename.split("/");
				String fname = s[s.length -1];
				String path = System.getProperty("user.home")+"/Desktop/My Files/"+fname;		
				output.writeInt(filename.length());
	        	output.writeBytes(filename);
				  
				try
				{        	
		        	int leng=input.readInt();
		        	byte[] digit2 = new byte[leng];
		        	byte[] digitSin = new byte[48000];
		        	current = 0;
		        	if(flag)
		        	{
		            	File file = new File(path);
		    			if(!file.exists()){
		    				file.createNewFile();
		    			}
		    			FileOutputStream fos = new FileOutputStream(path);
		    			for(int i=0; i<leng; i++)
		    			{
		    				digit2[i] = input.readByte();
		    				current++;
		    				setProgress(leng);
		    			}
		    			fos.write(digit2);
		    			fos.close();
		        	}
		        	else{
		    			MappedByteBuffer mbb;
		    			FileChannel fc = new RandomAccessFile(path,"rw").getChannel();
		    			mbb = fc.map(FileChannel.MapMode.READ_WRITE,0,leng);
		    			ByteBuffer bytebuffer;
		            	for(int i=0; i<=leng/48000; i++)
		            	{
		            		if(i==leng/48000)
		            		{
		            			for(int j = 0; j<leng%48000; j++)
		                		{
		                			digitSin[j] = input.readByte();
		                			current++;
		                			setProgress(leng);
		                		}
		            			bytebuffer = ByteBuffer.wrap(digitSin);
		            			fc.write(bytebuffer,i*48000);
		            		}
		            		else
		            		{
		    	        		for(int j = 0; j<48000; j++)
		    	        		{
		    	        			digitSin[j] = input.readByte();
		    	        			current++;
		    	        			setProgress(leng);
		    	        		}
		    	        		bytebuffer = ByteBuffer.wrap(digitSin);
		    	        		fc.write(bytebuffer,i*48000);
		            		}
		            		
		            	}
		        	}
					output.writeInt(8);
					output.writeBytes("finished");		
					this.sleep(1000);
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
