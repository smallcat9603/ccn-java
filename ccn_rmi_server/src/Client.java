//Client thread

import java.io.*;
import java.net.*;
import java.util.*;

public class Client extends Thread { //implements Runnable {
	
	DatagramSocket _ds;
	
	public Client(DatagramSocket ds)
	{
		//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " Client side active..");
		System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " Client side active..");
		
		_ds = ds;
	}
	
	public void run()
	{
//		try {
//			this.wait(5000);
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
//		String line = null;
//		
//		//change the text color, not the error message
//		System.err.println("[##] please input the interest name under the prefix /ccn/waseda/gotolab/: ");
//		
//		try
//		{
//			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//			
////			while((line = br.readLine()) != null)
////			{
////				
////			}
//			
//			line = br.readLine();
//		}
//		catch(IOException e)
//		{
//			e.printStackTrace();
//		}
//		
//		
//		//String s = "/ccn/waseda/gotolab/news2012";
//		String s = "/ccn/waseda/gotolab/" + line;
		
		if(CCNDeamon.myrequest.size() > 0)
		{
			//131223 modified (ArrayIndexOutOfBoundsException after deleting items from my requests)
			//MyRequest mr = CCNDeamon.myrequest; (wrong)
			
			//for(int i=0; i<CCNDeamon.myrequest.size(); i++)
			for(int i=0; i<CCNDeamon.tosend.size(); i++)
			{
				//131222
				try {
					Thread.sleep(1000/Parameters.requests_per_sec); //0.1s (about 10 requests per sec)
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				String s = CCNDeamon.tosend.get(i);
				
				Interest interest = new Interest(s);
				Message message = new Message(interest);
				
				//boolean isMatch = FIB.isMatch(s);
				//boolean isMatch = CCNDeamon.fib.containsKey(s.substring(0, s.lastIndexOf("/")));	///prefix/name
				String prefix = Function.matchFIBLongestPrefix(s);
				
				//if(isMatch == true)
				//if(prefix != null)  140113 modified
				if(!prefix.equals("/"))
				{
					
					//Vector<Node> nodelist = FIB.getMatch(s);	
					//Vector<Node> nodelist = CCNDeamon.fib.get(s.substring(0, s.lastIndexOf("/")));
					Vector<Node> nodelist = CCNDeamon.fib.get(prefix);
					
					for(int j=0; j<nodelist.size(); j++)
					{
						Node node = nodelist.elementAt(j);
								
						InetAddress ia = node.getInetAddress();
						int port = node.getPort();
						
						System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " sent the interest : " + s 
																	  + " to the FIB entry : " + prefix + " : " 
																	  						+ ia.toString() + ":" + port);
						
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
					    	//140123  record sending time
					    	CCNDeamon.rtt.put(s, new Date().getTime());
						}
						catch(IOException e)
						{
							e.printStackTrace();
						}
	
					}
					
					//140113
					//CCNDeamon.myrequest.sent.add(s);
					//CCNDeamon.myrequest.notSatisfied.add(s);
					CCNDeamon.myrequest.sent.put(s, new Date().getTime());
					//CCNDeamon.myrequest.sent.add(s + "/" + new Date().getTime());
					
				}
				else if(CCNDeamon.fib.containsKey("/"))	//131031 forward the interest according to the default fib configuration (maybe flood)
				{
					
					Vector<Node> nodelist = CCNDeamon.fib.get("/");
					
					for(int j=0; j<nodelist.size(); j++)
					{
						Node node = nodelist.elementAt(j);
								
						InetAddress ia = node.getInetAddress();
						int port = node.getPort();
						
						System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " sent the interest : " + s 
																	  + " to the (default) FIB entry : " + ia.toString() + ":" + port);
						
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
					    	
					    	//140123  record sending time
					    	CCNDeamon.rtt.put(s, new Date().getTime());
						}
						catch(IOException e)
						{
							e.printStackTrace();
						}
					}
					
					//140113
					//CCNDeamon.myrequest.sent.add(s);
					//CCNDeamon.myrequest.notSatisfied.add(s);
					CCNDeamon.myrequest.sent.put(s, new Date().getTime());
					//CCNDeamon.myrequest.sent.add(s + "/" + new Date().getTime());
				}
				
			}	
		}
		

		
//		Interest interest = new Interest(s);
//		Message message = new Message(interest);
//		
//		//boolean isMatch = FIB.isMatch(s);
//		boolean isMatch = CCNDeamon.fib.containsKey("/ccn/waseda/gotolab");
//		
//		if(isMatch == true)
//		{
//			//Vector<Node> nodelist = FIB.getMatch(s);	
//			Vector<Node> nodelist = CCNDeamon.fib.get("/ccn/waseda/gotolab");
//			
//			
//			for(int i=0; i<nodelist.size(); i++)
//			{
//				Node node = nodelist.elementAt(i);
//						
//				InetAddress ia = node.getInetAddress();
//				int port = node.getPort();
//				
//				System.out.println(Entry.localhost.toString() + " : sent the interest : " + s 
//															  + " to the FIB entry : /ccn/waseda/gotolab: " + ia.toString() + " : " + port);
//				
//				try
//				{
//					ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			    	ObjectOutputStream oos = new ObjectOutputStream(baos);	    		           
//			    	oos.writeObject(message);
//			    	oos.close();
//			    	DatagramPacket dp = new DatagramPacket(baos.toByteArray(), baos.size(), ia, port);
//			    	_ds.send(dp);
//			    	
//		            //record sending time to file
//			    	FileOutputStream fos = new FileOutputStream("RTT.txt", true);
//					long sendtime = new Date().getTime();
//		            fos.write((sendtime + " sent " + s + "\r\n").getBytes());
//		            fos.close();
//				}
//				catch(IOException e)
//				{
//					e.printStackTrace();
//				}
//
//			}
//		}

		
		if(Parameters.SETUP == "Advanced")
		{
			//140112 data source mobility
			//if(Entry.localhost.getHostName().equals("smallcat-PC"))
//			if(Entry.localhost.getHostName().equals("nis-planet1.doshisha.ac.jp"))  //in at 20s
//			{
//				try {
//					Thread.sleep(20000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
			
			
			//131125
			while(true)
			{				
				discoverMaster();
				
				try {
					Thread.sleep(Parameters.hello_interval*3/2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	//131101 
	private void discoverMaster()
	{
		
		Interest interest = new Interest("/ccn/MASTER");
		Message message = new Message(interest);

		if(CCNDeamon.fib.containsKey("/"))	
		{
			Vector<Node> nodelist = CCNDeamon.fib.get("/");
			
			for(int j=0; j<nodelist.size(); j++)
			{
				Node node = nodelist.elementAt(j);
						
				InetAddress ia = node.getInetAddress();
				int port = node.getPort();
				
				System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " sent master discovery message to the (default) FIB entry : " + ia.toString() + ":" + port);
				
				try
				{
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
			    	ObjectOutputStream oos = new ObjectOutputStream(baos);	    		           
			    	oos.writeObject(message);
			    	oos.close();
			    	DatagramPacket dp = new DatagramPacket(baos.toByteArray(), baos.size(), ia, port);
			    	_ds.send(dp);
			    	
		            //record sending time to file
//			    	FileOutputStream fos = new FileOutputStream("RTT.txt", true);
//					long sendtime = new Date().getTime();
//		            fos.write((sendtime + " sent " + s + "\r\n").getBytes());
//		            fos.close();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

}
