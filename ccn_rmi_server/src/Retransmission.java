import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;


public class Retransmission extends Thread {

	DatagramSocket _ds;
	
	public Retransmission(DatagramSocket ds)
	{	
		_ds = ds;
	}
	
	public void run()
	{
		while(true)
		{
			try {
				Thread.sleep(Parameters.retransmission_timeout);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			//Set set = CCNDeamon.myrequest.sent.entrySet();
			
			//140113 ConcurrentModificationException!!!
//			MyRequest mr = (MyRequest)(CCNDeamon.myrequest.clone());
//			Map<String, Long> notsatisfied = Collections.synchronizedMap(new LinkedHashMap());
//			notsatisfied = mr.sent;
//			Set set = notsatisfied.entrySet();
			
			//140113 modified 
			Map<String, Long> notsatisfied = Collections.synchronizedMap(new LinkedHashMap());
			Map<String, Long> sent = CCNDeamon.myrequest.sent;
			notsatisfied.putAll(sent);
			Set set = notsatisfied.entrySet();
			
			
			if(set.size() > 0)
			{
				for(Iterator iterator = set.iterator(); iterator.hasNext();)
				{
				    Map.Entry entry = (Map.Entry)iterator.next();

				    long send = (long)(entry.getValue());

					if(new Date().getTime() - send > Parameters.retransmission_timeout)
					{
						//entry.setValue(new Date().getTime());
						
						String s = (String)(entry.getKey());
						
						Interest interest = new Interest(s);
						Message message = new Message(interest);
						
						String prefix = Function.matchFIBLongestPrefix(s);
						
						if(!prefix.equals("/"))
						{
							Vector<Node> nodelist = CCNDeamon.fib.get(prefix);
							
							for(int j=0; j<nodelist.size(); j++)
							{
								Node node = nodelist.elementAt(j);
										
								InetAddress ia = node.getInetAddress();
								int port = node.getPort();
								
								System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " re-sent the interest : " + s 
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
							    	//140124  record sending time
							    	if(CCNDeamon.rtt.containsKey(s))
							    	{
							    		CCNDeamon.rtt.remove(s);
							    	}
							    	CCNDeamon.rtt.put(s, new Date().getTime());
								}
								catch(IOException e)
								{
									e.printStackTrace();
								}
			
							}
						}
						else if(CCNDeamon.fib.containsKey("/"))	
						{							
							Vector<Node> nodelist = CCNDeamon.fib.get("/");
							
							for(int j=0; j<nodelist.size(); j++)
							{
								Node node = nodelist.elementAt(j);
										
								InetAddress ia = node.getInetAddress();
								int port = node.getPort();
								
								System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " re-sent the interest : " + s 
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
								}
								catch(IOException e)
								{
									e.printStackTrace();
								}
							}
						}
						
					}
   
				}
			}
		}
	}
}
