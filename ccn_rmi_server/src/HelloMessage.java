import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;


public class HelloMessage extends Thread {
	
	DatagramSocket _ds;
	
	public HelloMessage(DatagramSocket ds)
	{		
		_ds = ds;
	}
	
	public void run()
	{
		//140112 data source mobility
		//if(Entry.localhost.getHostName().equals("smallcat-PC"))
//		if(Entry.localhost.getHostName().equals("nis-planet1.doshisha.ac.jp"))  //in at 20s
//		{
//			try {
//				Thread.sleep(20000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
		
		
		while(true)
		{
			
			//131104
			CCNDeamon.neighbors = "";
			
			Interest interest = new Interest("/ccn/HELLO/" + new Date().getTime());
			Message message = new Message(interest);
			
			//131101 broadcast hello message according to the default fib configuration (flood)
			if(CCNDeamon.fib.containsKey("/"))	
			{
				Vector<Node> nodelist = CCNDeamon.fib.get("/");
				
				for(int j=0; j<nodelist.size(); j++)
				{
					Node node = nodelist.elementAt(j);
							
					InetAddress ia = node.getInetAddress();
					int port = node.getPort();
					
					System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " sent hello message to the (default) FIB entry : " + ia.toString() + ":" + port);
					
					try
					{
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
				    	ObjectOutputStream oos = new ObjectOutputStream(baos);	    		           
				    	oos.writeObject(message);
				    	oos.close();
				    	DatagramPacket dp = new DatagramPacket(baos.toByteArray(), baos.size(), ia, port);
				    	_ds.send(dp);
				    	
			            //record sending time to file
//				    	FileOutputStream fos = new FileOutputStream("RTT.txt", true);
//						long sendtime = new Date().getTime();
//			            fos.write((sendtime + " sent " + s + "\r\n").getBytes());
//			            fos.close();
					}
					catch(IOException e)
					{
						e.printStackTrace();
					}
				}
			}
			
			try {
				Thread.sleep(Parameters.hello_interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		}
	}

}
