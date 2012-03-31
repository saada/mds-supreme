import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;


public class Accept_Connnection extends Thread {
	int serverPort;
	String file;
	DataInputStream input;
    DataOutputStream output;
    Socket clientSocket;
	public Accept_Connnection (String filename, Socket acc_sock){
		file= filename;
		clientSocket = acc_sock;
	}
	public void run(){
	    try{
			input = new DataInputStream( clientSocket.getInputStream());
			output =new DataOutputStream( clientSocket.getOutputStream());
			int sb = input.readInt();
			byte[] digit2 = new byte[sb];
			input.readFully(digit2, 0, sb);
			String file=new String(digit2);
			File f = new File(file);
			FileInputStream fin = null;
			FileChannel ch = null;    
			fin = new FileInputStream(f);
			ch = fin.getChannel();
			int size = (int) ch.size();
			MappedByteBuffer buf = ch.map(MapMode.READ_ONLY, 0, size);
			byte[] bytes = new byte[size];
			buf.get(bytes);
			output.writeInt(bytes.length);
			output.write(bytes);
			output.flush();
			try{
				int leng = input.readInt();
				byte[] fin2 = new byte[leng];
				input.readFully(fin2, 0, leng);
				String info = new String(fin2);
				System.out.println(info);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				System.out.println("Unable to indentify the successful signal");
			}
	    }
	    catch (IOException e) {
				e.printStackTrace();
	    }
	    finally{

	    }
	}
}
