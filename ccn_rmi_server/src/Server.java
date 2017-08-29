import java.net.*;
import java.io.*;
import java.util.*;

//server thread

public class Server extends Thread { //implements Runnable {
	
	private DatagramPacket _dp;	//received packet
	private DatagramSocket _ds;	//local socket
	private InetAddress ia;	//ip address of sender
	private int port;	//port of sender
	
	private Message received;	//received message
	private String message_type;	//interest or data
	
//	public Server (DatagramSocket ds, DatagramPacket dp) 
//	{
//		System.out.println("Server side active..");
//		
//		this._dp = dp;	//received packet
//		this._ds = ds;	//this node
//
//	}
	
	public Server (DatagramSocket ds) 
	{
		//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " Server side active..");
		System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " Server side active..");
		
		this._ds = ds;	//this node

	}
	
	public void run()
	{
		try
		{
			//140112 data source mobility
			//if(Entry.localhost.getHostName().equals("smallcat-PC"))
//			if(Entry.localhost.getHostName().equals("planetlab2.sfc.wide.ad.jp"))  //out at 15s
//			{
//				Timer timer1 = new Timer();
//				timer1.schedule(new TimerTask(){
//
//				   @Override
//				   public void run() {
//						System.err.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : datagram socket close..system exit..");
//				        _ds.close();
//				        System.exit(1);
//				   }
//				   
//				  }, 15000);
//			}
//			//if(Entry.localhost.getHostName().equals("smallcat-PC"))
//			if(Entry.localhost.getHostName().equals("nis-planet1.doshisha.ac.jp"))  //in at 20s
//			{
//				try {
//					Thread.sleep(20000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
			
	
			while(true) 
			{								
				byte[] buffer = new byte[Parameters.buffer_size];
				_dp = new DatagramPacket(buffer, Parameters.buffer_size);
		    	         	    
		    	try {
		    		//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " Server is ready to receive message..");
		    		System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " Server is ready to receive message..");
					_ds.receive(_dp);
				} catch (IOException e) {
					e.printStackTrace();
				}
		    	
//			} 
//		}
//		finally 
//		{
//	      	   System.out.println("datagram socket close..system exit..");
//	           _ds.close();
//	           System.exit(1);
//	    }
		
				ia = _dp.getAddress();
				port = _dp.getPort();
				
				Node node = new Node(ia, port);	//sender
				
				
				try
				{
					ByteArrayInputStream bais = new ByteArrayInputStream(_dp.getData(), 0, _dp.getLength());
					ObjectInputStream ois = new ObjectInputStream(bais);
					received = (Message) ois.readObject();		
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
		 	 	catch (ClassNotFoundException e) 
		 	 	{
					//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " Class not found exception..");
		 	 		System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " Class not found exception..");
		 	 	}  
				
				message_type = received.getMessageType();
				
				if(message_type.equals("Interest"))
				{
					//131222
					CCNDeamon.received++;
					CCNDeamon.received_interest++;
					
					Interest interest = received.getInterest();
					String name = interest.getName();
					
					System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " received interest: " + name + " from " + ia.toString() + ":" + port);
					//System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " received interest: " + name + " from " + node.getInetAddress().getHostName() + ":" + port);
					
					String result = processInterest(name);
					
					switch(result)
					{
					
					case "PATHS":
						
						String routers = name.substring(name.indexOf("/ccn/PATHS/")+"/ccn/PATHS/".length(), name.lastIndexOf("/ccn/")+1);
						String temp = routers.substring(0, routers.lastIndexOf("/"));
						String des = temp.substring(temp.lastIndexOf("/")+1);
						
						if(!Entry.localhost.getHostName().equals(des))
						{
							
							temp = routers.substring(routers.indexOf(Entry.localhost.getHostName())+Entry.localhost.getHostName().length()+1);
							String next = temp.substring(0, temp.indexOf("/"));
							
							//131119 forward interest according to fib 131223 rectified
							next = "/ccn/ROUTER_" + next;
							Vector<Node> nexthop = CCNDeamon.fib.get(next);	
							
							for (int i=0; i<nexthop.size(); i++)
							{
								Node n = nexthop.elementAt(i);
								
								//140111  add lifetime for /prefix (not start with /ccn) 
								//n.setMatchTimeForFIB(new Date().getTime());
								
								//if(n.getInetAddress().toString() != node.getInetAddress().toString())  131223
								if(!n.getInetAddress().getHostName().equals(node.getInetAddress().getHostName()))	
								{
									InetAddress ia = n.getInetAddress();
									int port = n.getPort();
									
									try
									{							
										ByteArrayOutputStream baos = new ByteArrayOutputStream();
								    	ObjectOutputStream oos = new ObjectOutputStream(baos);	    		           
								    	oos.writeObject(received);
								    	oos.close();
								    	DatagramPacket dp = new DatagramPacket(baos.toByteArray(), baos.size(), ia, port);
								    	_ds.send(dp);
								    	
								    	//System.err.println(Entry.localhost.toString() + " : PATHS");
								    	System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : The Interest: " + name + " is (following FIB) forwarded to " + ia.toString() + " : " + port);
								    	//System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : The Interest: " + name + " is (following FIB) forwarded to " + ia.getHostName() + " : " + port);
									}
									catch(IOException e)
									{
										e.printStackTrace();
									}
								}
							}
							
							
							//140120 
							Vector<Node> vn1 = new Vector<Node>();
							vn1.addAll(nexthop);
							for (int i=0; i<vn1.size(); i++)
							{
								Node n = vn1.elementAt(i);
								n.setMatchTimeForFIB(new Date().getTime());  //add lifetime for /prefix (not start with /ccn)
							}
							//131119 add fib entries to prefix
							String fibPrefix = name.substring(name.lastIndexOf("/ccn/")+"/ccn/".length()-1, name.lastIndexOf("/")); 
							
							if(CCNDeamon.fib.containsKey(fibPrefix))
							{
								CCNDeamon.fib.remove(fibPrefix);
							}
							
							
							CCNDeamon.fib.put(fibPrefix, vn1);
							//CCNDeamon.fib.put(fibPrefix, nexthop);
							//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : fib entry added for prefix: " + fibPrefix);
							System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : fib entry added for prefix: " + fibPrefix);
							
							
							//131119 add fib entries to router
							while(!temp.equals(""))
							{
								//140120
								Vector<Node> vn2 = new Vector<Node>();
								vn2.addAll(nexthop);
								
								fibPrefix = temp.substring(0, temp.indexOf("/"));
								
								if(!CCNDeamon.fib.containsKey("/ccn/ROUTER_"+fibPrefix))
								{
									CCNDeamon.fib.put("/ccn/ROUTER_"+fibPrefix, vn2);
									//CCNDeamon.fib.put("/ccn/ROUTER_"+fibPrefix, nexthop);
									//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : fib entry added for prefix: " + "/ccn/ROUTER_"+fibPrefix);
									System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : fib entry added for prefix: " + "/ccn/ROUTER_"+fibPrefix);
								}
								else
								{
									CCNDeamon.fib.remove("/ccn/ROUTER_"+fibPrefix);
									CCNDeamon.fib.put("/ccn/ROUTER_"+fibPrefix, vn2);
									//CCNDeamon.fib.put("/ccn/ROUTER_"+fibPrefix, nexthop);
									//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : fib entry updated for prefix: " + "/ccn/ROUTER_"+fibPrefix);
									System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : fib entry updated for prefix: " + "/ccn/ROUTER_"+fibPrefix);
								}
								
								temp = temp.substring(temp.indexOf("/")+1);
							}
							
							
							//131119 add entry to pit 
							if(CCNDeamon.pit.containsKey(name))
							{
								Vector<Node> nlist = CCNDeamon.pit.get(name);
								boolean conflict = false;
								for(int i=0; i<nlist.size(); i++)
								{
									if(nlist.elementAt(i).getInetAddress().getHostName().equals(node.getInetAddress().getHostName()))
									{
										conflict = true;
										nlist.elementAt(i).setCreationTimeForPIT(new Date().getTime());
										
										System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : PIT entry already existed: " + name + " / " + node.getInetAddress().toString() + ":" + node.getPort());
										//System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : PIT entry already existed: " + name + " / " + node.getInetAddress().getHostName() + ":" + node.getPort());
										
										break;	
									}
								}
								
								if(conflict == false)
								{
									node.setCreationTimeForPIT(new Date().getTime());
									nlist.add(node);
									CCNDeamon.pit.remove(name);
									CCNDeamon.pit.put(name, nlist);
									
									System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : PIT entry added: " + name + " / " + node.getInetAddress().toString() + ":" + node.getPort());
									//System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : PIT entry added: " + name + " / " + node.getInetAddress().getHostName() + ":" + node.getPort());
								}
							}
							else
							{
								Vector<Node> nlist = new Vector<Node>(); 
								node.setCreationTimeForPIT(new Date().getTime());
								nlist.add(node);
								CCNDeamon.pit.put(name, nlist);
								
								System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : PIT entry added: " + name + " / " + node.getInetAddress().toString() + ":" + node.getPort());
								//System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : PIT entry added: " + name + " / " + node.getInetAddress().getHostName() + ":" + node.getPort());
							}
							

//							Vector<Node> previoushop = new Vector<Node>(); 
//							
//							node.setCreationTimeForPIT(new Date().getTime());
//							
//							previoushop.add(node);
//							
//							CCNDeamon.pit.put(name, previoushop);
							
							//System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : PATHS");
//							System.out.println(Entry.localhost.toString() + " : PIT entry added: " + name + " / " + node.getInetAddress().toString() + ":" + node.getPort());
						

						}
						else
						{
							try
							{			
								Data data = new Data(name, CCNDeamon.repo.get(name.substring(name.lastIndexOf("/ccn/"))));
								Message message = new Message(data);
								
								ByteArrayOutputStream baos = new ByteArrayOutputStream();
						    	ObjectOutputStream oos = new ObjectOutputStream(baos);	    		           
						    	oos.writeObject(message);
						    	oos.close();
						    	DatagramPacket dp = new DatagramPacket(baos.toByteArray(), baos.size(), ia, port);
						    	_ds.send(dp);
							}
							catch(IOException e)
							{
								e.printStackTrace();
							}														
							
							//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : repo hit for data : " + name);
							System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : repo hit for data : " + name);
							
							//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : data field: " + new String(CCNDeamon.repo.get(name.substring(name.lastIndexOf("/ccn/")))));
							System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : data field: " + new String(CCNDeamon.repo.get(name.substring(name.lastIndexOf("/ccn/")))));
							
							//System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : PATHS");
						}
						
						break;

						
					case "CAP_INTEREST":	//(master) return routing information
						
						String prefix = name.substring(name.lastIndexOf("/ccn/"), name.lastIndexOf("/"));	// name = /ccn/MASTER/MASTER_.../SRCROUTER_ROUTER_.../ccn/prefix/..
						String srcRouter = name.substring(name.indexOf("/SRCROUTER_ROUTER_")+"/SRCROUTER_ROUTER_".length(), name.lastIndexOf("/ccn/"));
						
						String paths = srcRouter+";";
						
						String desRouter = "NORESULT";
						
						//140117 print prefix-ID & neighborlist
						Set set = CCNDeamon.prefix_ID.entrySet();
						if(set.size() == 0)
						{
							System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + "prefix-ID (empty)");
						}
						for(Iterator iterator = set.iterator(); iterator.hasNext();)
						{
							   Map.Entry entry = (Map.Entry)iterator.next();
							   System.out.println((String)(entry.getKey()) +" : " + (String)(entry.getValue()));
						}
						set = CCNDeamon.neighborList.entrySet();
						if(set.size() == 0)
						{
							System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + "neighborlist (empty)");
						}
						for(Iterator iterator = set.iterator(); iterator.hasNext();)
						{
							   Map.Entry entry = (Map.Entry)iterator.next();
							   System.out.println((String)(entry.getKey()) +" : " + (String)(entry.getValue()));
						}
						
						
						if(CCNDeamon.prefix_ID.containsKey(prefix))
						{
							desRouter = CCNDeamon.prefix_ID.get(prefix);
							//131127 rectified 	the FIRST des is chosen (temp)
							desRouter = desRouter.substring(0, desRouter.indexOf(";"));
							System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " data provider: " + desRouter);
							paths = Function.calculateRoute(srcRouter, desRouter);
							System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " path: " + paths);
							
							//141218 hop-minimal path added
							if(Parameters.multipath == true)
							{
								paths += ("MULTIPATH_HOPS/" + Function.calculateRouteByHops(srcRouter, desRouter));
							}
						}
						else
						{
							System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " no data provider for the prefix: " + prefix);
						}
						
						try
						{
							String datafield = desRouter+":"+paths;
							
							Data data = new Data(name, datafield.getBytes());
							Message message = new Message(data);
							
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
					    	ObjectOutputStream oos = new ObjectOutputStream(baos);	    		           
					    	oos.writeObject(message);
					    	oos.close();
					    	DatagramPacket dp = new DatagramPacket(baos.toByteArray(), baos.size(), ia, port);
					    	_ds.send(dp);
						}
						catch(IOException e)
						{
							e.printStackTrace();
						}
						
						
						//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : paths: " + paths);
						System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : paths: " + paths);
						
						System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : returned routing information to : " + ia.toString() + ":" + port);
						//System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : returned routing information to : " + ia.getHostName() + ":" + port);
					
						//System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : CAP_INTEREST");
						
						break;
						
					
					case "REG_PREFIX":	//(routers and master) add fib entry to prefix
						
						String regPrefix = name.substring(name.lastIndexOf("/ccn/"));
						
						if(CCNDeamon.fib.containsKey(regPrefix))
						{
							//CCNDeamon.fib.remove(regPrefix);
							
							Vector<Node> nodes = CCNDeamon.fib.get(regPrefix);
							boolean conflict = false;
							
							for(int i=0; i<nodes.size(); i++)
							{
								if(nodes.elementAt(i).getInetAddress().toString() == node.getInetAddress().toString())
								{
									conflict = true;
									break;
								}
							}

							if(conflict == false)
							{
								nodes.add(node);
								CCNDeamon.fib.remove(regPrefix);
								CCNDeamon.fib.put(regPrefix, nodes);
								System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : fib entry (face) added: " + regPrefix + " : " 
	  									  + node.getInetAddress().toString() + ":" + node.getPort());
//								System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : fib entry (face) added: " + regPrefix + " : " 
//	  									  + node.getInetAddress().getHostName() + ":" + node.getPort());
								
							}
						}
						else
						{
							Vector<Node> list = new Vector<Node>();	
							list.add(node);
							CCNDeamon.fib.put(regPrefix, list);
							System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : fib entry added: " + regPrefix + " : " 
									  									  + node.getInetAddress().toString() + ":" + node.getPort());
//							System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : fib entry added: " + regPrefix + " : " 
//									  + node.getInetAddress().getHostName() + ":" + node.getPort());
						}

							
						
						if(Parameters.role == "MASTER")	
						{
							//send back ack message
							try
							{
								
								Data data = new Data(name, "ACCEPTED".getBytes());
								Message message = new Message(data);
								
								ByteArrayOutputStream baos = new ByteArrayOutputStream();
						    	ObjectOutputStream oos = new ObjectOutputStream(baos);	    		           
						    	oos.writeObject(message);
						    	oos.close();
						    	DatagramPacket dp = new DatagramPacket(baos.toByteArray(), baos.size(), ia, port);
						    	_ds.send(dp);
							}
							catch(IOException e)
							{
								e.printStackTrace();
							}
							
							//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : REG_PREFIX");
							//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : sent back ack message");
							System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : sent back ack message");
							
							//record prefix registry information (prefix-ID map)
							String routerID = name.substring(name.indexOf("/REGPREFIX_ROUTER_")+"/REGPREFIX_ROUTER_".length(), name.lastIndexOf("/ccn/"));
							
							if(CCNDeamon.prefix_ID.containsKey(regPrefix))
							{
								String id = CCNDeamon.prefix_ID.get(regPrefix);
								
								if(id.indexOf(routerID+";") == -1)
								{
									//140117 new->before
									//id += (routerID+";");
									id = routerID + ";" + id;
									CCNDeamon.prefix_ID.remove(regPrefix);
									CCNDeamon.prefix_ID.put(regPrefix, id);
								}
							}
							else
							{
								CCNDeamon.prefix_ID.put(regPrefix, routerID+";");
							}

							//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : recorded prefix-ID");
							System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : recorded prefix-ID");
							//System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : REG_PREFIX");
						}
						else	//intermediate node
						{
							//forward interest according to fib
							Vector<Node> nexthop = CCNDeamon.fib.get(name.substring(0, name.indexOf("/REGPREFIX_ROUTER_")));	///ccn/MASTER/MASTER_.../REGPREFIX_ROUTER_.../ccn/...
							
							for (int i=0; i<nexthop.size(); i++)
							{
								Node n = nexthop.elementAt(i);
								
								//if(n.getInetAddress().toString() != node.getInetAddress().toString())  131223
								if(!n.getInetAddress().getHostName().equals(node.getInetAddress().getHostName()))	
								{
									InetAddress ia = n.getInetAddress();
									int port = n.getPort();
									
									try
									{							
										ByteArrayOutputStream baos = new ByteArrayOutputStream();
								    	ObjectOutputStream oos = new ObjectOutputStream(baos);	    		           
								    	oos.writeObject(received);
								    	oos.close();
								    	DatagramPacket dp = new DatagramPacket(baos.toByteArray(), baos.size(), ia, port);
								    	_ds.send(dp);
								    	
								    	//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : REG_PREFIX");
								    	System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : The Interest: " + name + " is (following FIB) forwarded to " + ia.toString() + " : " + port);
								    	//System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : The Interest: " + name + " is (following FIB) forwarded to " + ia.getHostName()+ " : " + port);
									}
									catch(IOException e)
									{
										e.printStackTrace();
									}
								}
							}
							
							
							//add entry to pit 131108 modified
							if(CCNDeamon.pit.containsKey(name))
							{
								Vector<Node> nlist = CCNDeamon.pit.get(name);
								boolean conflict = false;
								for(int i=0; i<nlist.size(); i++)
								{
									if(nlist.elementAt(i).getInetAddress().getHostName().equals(node.getInetAddress().getHostName()))
									{
										conflict = true;
										nlist.elementAt(i).setCreationTimeForPIT(new Date().getTime());
										
										System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : PIT entry already existed: " + name + " / " + node.getInetAddress().toString() + ":" + node.getPort());
										//System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : PIT entry already existed: " + name + " / " + node.getInetAddress().getHostName() + ":" + node.getPort());
										
										break;	
									}
								}
								
								if(conflict == false)
								{
									node.setCreationTimeForPIT(new Date().getTime());
									nlist.add(node);
									CCNDeamon.pit.remove(name);
									CCNDeamon.pit.put(name, nlist);
									
									System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : PIT entry added: " + name + " / " + node.getInetAddress().toString() + ":" + node.getPort());
									//System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : PIT entry added: " + name + " / " + node.getInetAddress().getHostName() + ":" + node.getPort());
								}
							}
							else
							{
								Vector<Node> nlist = new Vector<Node>(); 
								node.setCreationTimeForPIT(new Date().getTime());
								nlist.add(node);
								CCNDeamon.pit.put(name, nlist);
								
								System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : PIT entry added: " + name + " / " + node.getInetAddress().toString() + ":" + node.getPort());
								//System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : PIT entry added: " + name + " / " + node.getInetAddress().getHostName() + ":" + node.getPort());
							}
							

//							Vector<Node> previoushop = new Vector<Node>(); 
//							
//							node.setCreationTimeForPIT(new Date().getTime());
//							
//							previoushop.add(node);
//							
//							CCNDeamon.pit.put(name, previoushop);
							
							//System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : REG_PREFIX");
//							System.out.println(Entry.localhost.toString() + " : PIT entry added: " + name + " / " + node.getInetAddress().toString() + ":" + node.getPort());
							
						}
						
						break;
					
					case "NEIGHBORS_QUERY":	//(requester) return neighbors
						
						try
						{

							String neighbors = CCNDeamon.neighbors;
							
							Data data = new Data(name, neighbors.getBytes());
							Message message = new Message(data);
							
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
					    	ObjectOutputStream oos = new ObjectOutputStream(baos);	    		           
					    	oos.writeObject(message);
					    	oos.close();
					    	DatagramPacket dp = new DatagramPacket(baos.toByteArray(), baos.size(), ia, port);
					    	_ds.send(dp);
					    		
							//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : " + new Date().getTime() + " returned data : " + name);
					    	System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : " + new Date().getTime() + " returned data : " + name);
							//System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : NEIGHBORS_QUERY");
						}
						catch(IOException e)
						{
							e.printStackTrace();
						}
						
						break;
						
					
					case "REG_ROUTER":	//(routers and master) add fib entry to router
						
						String fibPrefix = "/ccn/ROUTER_" + name.substring(name.indexOf("/REGROUTER_ROUTER_") + "/REGROUTER_ROUTER_".length());
						
						if(CCNDeamon.fib.containsKey(fibPrefix))
						{
							CCNDeamon.fib.remove(fibPrefix);
						}
						
						Vector<Node> list = new Vector<Node>();	
						list.add(node);
						CCNDeamon.fib.put(fibPrefix, list);
						System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : fib entry added: " + fibPrefix + " : " 
								  									  + node.getInetAddress().toString() + ":" + node.getPort());
						
//						System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : fib entry added: " + fibPrefix + " : " 
//								  + node.getInetAddress().getHostName() + ":" + node.getPort());
							
						
						if(Parameters.role == "MASTER")	
						{
							//send back ack message
							try
							{
								
								Data data = new Data(name, "ACCEPTED".getBytes());
								Message message = new Message(data);
								
								ByteArrayOutputStream baos = new ByteArrayOutputStream();
						    	ObjectOutputStream oos = new ObjectOutputStream(baos);	    		           
						    	oos.writeObject(message);
						    	oos.close();
						    	DatagramPacket dp = new DatagramPacket(baos.toByteArray(), baos.size(), ia, port);
						    	_ds.send(dp);
							}
							catch(IOException e)
							{
								e.printStackTrace();
							}
							
							//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : REG_ROUTER");
							//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : sent back ack message");
							System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : sent back ack message");
							
							
							//ask for neighbor information
							Interest askforneighbors = new Interest("/ccn/ROUTER_" + name.substring(name.indexOf("/REGROUTER_ROUTER_") + "/REGROUTER_ROUTER_".length()) + "/NEIGHBORS");
							Message message = new Message(askforneighbors);
							
							try
							{
								ByteArrayOutputStream baos = new ByteArrayOutputStream();
						    	ObjectOutputStream oos = new ObjectOutputStream(baos);	    		           
						    	oos.writeObject(message);
						    	oos.close();
						    	DatagramPacket dp = new DatagramPacket(baos.toByteArray(), baos.size(), ia, port);
						    	_ds.send(dp);
						    	
					            //record sending time to file
//								    	FileOutputStream fos = new FileOutputStream("RTT.txt", true);
//										long sendtime = new Date().getTime();
//							            fos.write((sendtime + " sent " + s + "\r\n").getBytes());
//							            fos.close();
							}
							catch(IOException e)
							{
								e.printStackTrace();
							}
							
							//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : asked for neighbor information");
							System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : asked for neighbor information");
							//System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : REG_ROUTER");
	
						}
						else	//intermediate node
						{
							//forward interest according to fib
							Vector<Node> nexthop = CCNDeamon.fib.get(name.substring(0, name.lastIndexOf("/")));	///ccn/MASTER/MASTER_.../REGROUTER_ROUTER_...
							//Vector<Node> nexthop = CCNDeamon.fib.get(Function.matchFIBLongestPrefix(name));	///ccn/MASTER/MASTER_.../REGROUTER_ROUTER_...
							
							for (int i=0; i<nexthop.size(); i++)
							{
								Node n = nexthop.elementAt(i);
								
								//if(n.getInetAddress().toString() != node.getInetAddress().toString())  131223
								if(!n.getInetAddress().getHostName().equals(node.getInetAddress().getHostName()))	
								{
									InetAddress ia = n.getInetAddress();
									int port = n.getPort();
									
									try
									{							
										ByteArrayOutputStream baos = new ByteArrayOutputStream();
								    	ObjectOutputStream oos = new ObjectOutputStream(baos);	    		           
								    	oos.writeObject(received);
								    	oos.close();
								    	DatagramPacket dp = new DatagramPacket(baos.toByteArray(), baos.size(), ia, port);
								    	_ds.send(dp);
								    	
								    	//System.out.println(Entry.localhost.toString() + " : REG_ROUTER");
								    	System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : The Interest: " + name + " is (following FIB) forwarded to " + ia.toString() + " : " + port);
								    	//System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : The Interest: " + name + " is (following FIB) forwarded to " + ia.getHostName() + " : " + port);
									}
									catch(IOException e)
									{
										e.printStackTrace();
									}
								}
							}
							
							
							//add entry to pit 131108 modified
							if(CCNDeamon.pit.containsKey(name))
							{
								Vector<Node> nlist = CCNDeamon.pit.get(name);
								boolean conflict = false;
								for(int i=0; i<nlist.size(); i++)
								{
									if(nlist.elementAt(i).getInetAddress().getHostName().equals(node.getInetAddress().getHostName()))
									{
										conflict = true;
										nlist.elementAt(i).setCreationTimeForPIT(new Date().getTime());
										
										System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : PIT entry already existed: " + name + " / " + node.getInetAddress().toString() + ":" + node.getPort());
										//System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : PIT entry already existed: " + name + " / " + node.getInetAddress().getHostName() + ":" + node.getPort());
										
										break;	
									}
								}
								
								if(conflict == false)
								{
									node.setCreationTimeForPIT(new Date().getTime());
									nlist.add(node);
									CCNDeamon.pit.remove(name);
									CCNDeamon.pit.put(name, nlist);
									
									System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : PIT entry added: " + name + " / " + node.getInetAddress().toString() + ":" + node.getPort());
									//System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : PIT entry added: " + name + " / " + node.getInetAddress().getHostName() + ":" + node.getPort());
								}
							}
							else
							{
								Vector<Node> nlist = new Vector<Node>(); 
								node.setCreationTimeForPIT(new Date().getTime());
								nlist.add(node);
								CCNDeamon.pit.put(name, nlist);
								
								System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : PIT entry added: " + name + " / " + node.getInetAddress().toString() + ":" + node.getPort());
								//System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : PIT entry added: " + name + " / " + node.getInetAddress().getHostName() + ":" + node.getPort());
							}
							
							
//							Vector<Node> previoushop = new Vector<Node>(); 
//							
//							node.setCreationTimeForPIT(new Date().getTime());
//							
//							previoushop.add(node);
//							
//							CCNDeamon.pit.put(name, previoushop);
							
							//System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : REG_ROUTER");
//							System.out.println(Entry.localhost.toString() + " : PIT entry added: " + name + " / " + node.getInetAddress().toString() + ":" + node.getPort());
						}
						
						break;
					
					case "IS_MASTER":	//return master ID
						
						try
						{
							String data_name = "/ccn/MASTER";

							Data data = new Data(data_name, Entry.localhost.getHostName().getBytes());
							Message message = new Message(data);
							
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
					    	ObjectOutputStream oos = new ObjectOutputStream(baos);	    		           
					    	oos.writeObject(message);
					    	oos.close();
					    	DatagramPacket dp = new DatagramPacket(baos.toByteArray(), baos.size(), ia, port);
					    	_ds.send(dp);
					    		
							
							//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : " + new Date().getTime() + " returned data : " + data_name);
					    	System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : " + new Date().getTime() + " returned data : " + data_name);
							//System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : IS_MASTER");
						}
						catch(IOException e)
						{
							e.printStackTrace();
						}
						
						break;
						
					case "IS_NOT_MASTER":	//flood
						
						if(CCNDeamon.fib.containsKey("/"))	
						{
							Vector<Node> nodelist = CCNDeamon.fib.get("/");
							
							for (int i=0; i<nodelist.size(); i++)
							{
								Node n = nodelist.elementAt(i);
								
								//if(n.getInetAddress().toString() != node.getInetAddress().toString())  131223
								if(!n.getInetAddress().getHostName().equals(node.getInetAddress().getHostName()))	
								{
									InetAddress ia = n.getInetAddress();
									int port = n.getPort();
									
									try
									{							
										ByteArrayOutputStream baos = new ByteArrayOutputStream();
								    	ObjectOutputStream oos = new ObjectOutputStream(baos);	    		           
								    	oos.writeObject(received);
								    	oos.close();
								    	DatagramPacket dp = new DatagramPacket(baos.toByteArray(), baos.size(), ia, port);
								    	_ds.send(dp);
								    	
								    	//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : The (master discovery) Interest: " + name + " is forwarded to " + ia.toString() + " : " + port);
								    	System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : The (master discovery) Interest: " + name + " is forwarded to " + ia.getHostName() + " : " + port);
									}
									catch(IOException e)
									{
										e.printStackTrace();
									}
								}
							}
							
							
							//add entry to pit 131108 modified(not neccessary..because CCNDeamon.pit does not include the key "name" for sure)
							if(CCNDeamon.pit.containsKey(name))
							{
								Vector<Node> nlist = CCNDeamon.pit.get(name);
								boolean conflict = false;
								for(int i=0; i<nlist.size(); i++)
								{
									if(nlist.elementAt(i).getInetAddress().getHostName().equals(node.getInetAddress().getHostName()))
									{
										conflict = true;
										nlist.elementAt(i).setCreationTimeForPIT(new Date().getTime());
										
										System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : PIT entry already existed: " + name + " / " + node.getInetAddress().toString() + ":" + node.getPort());
										//System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : PIT entry already existed: " + name + " / " + node.getInetAddress().getHostName() + ":" + node.getPort());
										
										break;	
									}
								}
								
								if(conflict == false)
								{
									node.setCreationTimeForPIT(new Date().getTime());
									nlist.add(node);
									CCNDeamon.pit.remove(name);
									CCNDeamon.pit.put(name, nlist);
									
									System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : PIT entry added: " + name + " / " + node.getInetAddress().toString() + ":" + node.getPort());
									//System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : PIT entry added: " + name + " / " + node.getInetAddress().getHostName() + ":" + node.getPort());
								}
							}
							else
							{
								Vector<Node> nlist = new Vector<Node>(); 
								node.setCreationTimeForPIT(new Date().getTime());
								nlist.add(node);
								CCNDeamon.pit.put(name, nlist);
								
								System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : PIT entry added: " + name + " / " + node.getInetAddress().toString() + ":" + node.getPort());
								//System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : PIT entry added: " + name + " / " + node.getInetAddress().getHostName() + ":" + node.getPort());
							}

							
							//System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : IS_NOT_MASTER");
							
						}
						
						break;
					
					case "HELLO":	//return router ID
					
						try
						{
							long now = new Date().getTime();
							String data_name = "/ccn/HELLO/ROUTER_" + Entry.localhost.getHostName() + "/" + now;

							//131114
							long time = now - new Long(name.substring(name.lastIndexOf("/")+1));
							
							Data data = new Data(data_name, new Long(time).toString().getBytes());
							Message message = new Message(data);
							
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
					    	ObjectOutputStream oos = new ObjectOutputStream(baos);	    		           
					    	oos.writeObject(message);
					    	oos.close();
					    	DatagramPacket dp = new DatagramPacket(baos.toByteArray(), baos.size(), ia, port);
					    	_ds.send(dp);
					    		
							
							//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : " + new Date().getTime() + " returned data : " + data_name);
					    	System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : " + new Date().getTime() + " returned data : " + data_name);
							//System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : HELLO_MESSAGE");
						}
						catch(IOException e)
						{
							e.printStackTrace();
						}
						
						break;
					
					case "INTEREST_CACHE_MATCH":	//return data to sender
										
						try
						{
							
							Data data = new Data(name, CCNDeamon.cache.get(name));
							Message message = new Message(data);
							
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
					    	ObjectOutputStream oos = new ObjectOutputStream(baos);	    		           
					    	oos.writeObject(message);
					    	oos.close();
					    	DatagramPacket dp = new DatagramPacket(baos.toByteArray(), baos.size(), ia, port);
					    	_ds.send(dp);
						}
						catch(IOException e)
						{
							e.printStackTrace();
						}
						
						CCNDeamon.cacheHit++;
						
						
						
						//System.out.println(Entry.localhost.toString() + " : Cache hit for the data : " + name);
						
						//System.out.println(Entry.localhost.toString() + " : data field: " + new String(CCNDeamon.cache.get(name)));
						
						System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : returned data : " + 
											name + ":" + new String(CCNDeamon.cache.get(name)) + " from cache to : " + 
											ia.toString() + " : " + port);
						
//						System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : returned data : " + 
//								name + ":" + new String(CCNDeamon.cache.get(name)) + " from cache to : " + 
//								ia.getHostName() + " : " + port);
						
						//System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " INTEREST_CACHE_MATCH");
						
						break;
						
						
					case "INTEREST_REPO_MATCH": //return data to sender
										
						//131119 modified
						String s = name;
						if(!name.startsWith("/ccn/"))
						{
							s = "/ccn" + name;
						}
						
						try
						{

							Data data = new Data(name, CCNDeamon.repo.get(s));
							Message message = new Message(data);
							
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
					    	ObjectOutputStream oos = new ObjectOutputStream(baos);	    		           
					    	oos.writeObject(message);
					    	oos.close();
					    	DatagramPacket dp = new DatagramPacket(baos.toByteArray(), baos.size(), ia, port);
					    	_ds.send(dp);
						}
						catch(IOException e)
						{
							e.printStackTrace();
						}
						
						
						
						//System.out.println(Entry.localhost.toString() + " : repo hit for data : " + s);
						
						//System.out.println(Entry.localhost.toString() + " : data field: " + new String(CCNDeamon.repo.get(s)));
						
						System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : returned data : " + 
											s + ":" + new String(CCNDeamon.repo.get(s)) + " from repository to : " + 
											ia.toString() + " : " + port);
						
//						System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : returned data : " + 
//								s + ":" + new String(CCNDeamon.repo.get(s)) + " from repository to : " + 
//								ia.getHostName() + " : " + port);
						
						//System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " INTEREST_REPO_MATCH");
						
						break;
						
						
					case "INTEREST_PIT_MATCH":	//add incoming face to pit
						
						
//						((PIT) CCNDeamon.pit).addFace(name, node);
						
						node.setCreationTimeForPIT(new Date().getTime());
						
						PIT.addFace(name, node);

						System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " PIT face added (updated) for the entry: " + name + " / " + node.getInetAddress().toString() + ":" + node.getPort());
						//System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " PIT face added (updated) for the entry: " + name + " / " + node.getInetAddress().getHostName() + ":" + node.getPort());
						//System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " INTEREST_PIT_MATCH");
						
						break;
						
						
					case "INTEREST_FIB_MATCH":	//forward interest according to fib
						
						//Vector<Node> nodelist = CCNDeamon.fib.get(name.substring(0, name.lastIndexOf("/")));
						Vector<Node> nodelist = CCNDeamon.fib.get(Function.matchFIBLongestPrefix(name));
						
						System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " matching FIB entry: " + Function.matchFIBLongestPrefix(name));
						
						for (int i=0; i<nodelist.size(); i++)
						{
							Node n = nodelist.elementAt(i);
							
							//140111  add lifetime for /prefix (not start with /ccn) 
							//n.setMatchTimeForFIB(new Date().getTime());
							
							//if(n.getInetAddress().toString() != node.getInetAddress().toString())  131223
							if(!n.getInetAddress().getHostName().equals(node.getInetAddress().getHostName()))	
							{
								InetAddress ia = n.getInetAddress();
								int port = n.getPort();
								
								try
								{							
									ByteArrayOutputStream baos = new ByteArrayOutputStream();
							    	ObjectOutputStream oos = new ObjectOutputStream(baos);	    		           
							    	oos.writeObject(received);
							    	oos.close();
							    	DatagramPacket dp = new DatagramPacket(baos.toByteArray(), baos.size(), ia, port);
							    	_ds.send(dp);
							    	
							    	System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " The Interest: " + name + " is (following FIB) forwarded to " + ia.toString() + " : " + port);
							    	//System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " The Interest: " + name + " is (following FIB) forwarded to " + ia.getHostName() + " : " + port);
								}
								catch(IOException e)
								{
									e.printStackTrace();
								}
							}
						}
						
						
						//add entry to pit 
						Vector<Node> nlist = new Vector<Node>(); 
						
						node.setCreationTimeForPIT(new Date().getTime());
						
						nlist.add(node);
						
						CCNDeamon.pit.put(name, nlist);
						
						System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : PIT entry added: " + name + " / " + node.getInetAddress().toString() + ":" + node.getPort());
						//System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : PIT entry added: " + name + " / " + node.getInetAddress().getHostName() + ":" + node.getPort());
						
						//System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " INTEREST_FIB_MATCH");
						
						//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " The Interest: " + name + " is (following FIB) forwarded to " + ia.toString() + " : " + port);
						
						
						
						break;
						
						
					case "INTEREST_NO_MATCH":
						
						//131111 capsulate interest
						if(!name.startsWith("/ccn/"))
						{
							capsulateInterest(name, node);
						}
						
						//System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : INTEREST_NO_MATCH");
						
						break;
						
						
					default:
						
						//System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : (interest) no operation..");
						System.err.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : (interest) no operation..");
						
						break;
						
					}
				}
				else if(message_type.equals("Data"))
				{
					//131222
					CCNDeamon.received++;
					CCNDeamon.received_data++;
					
					Data data = received.getData();
					String name = data.getName();
					byte[] content = data.getData();
					
					//ia.getHostName() takes so much time!!! (4s+)
					System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " received data: " + name + " from " + ia.toString() + ":" + port);
					//System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " received data: " + name + " from " + ia.getHostName() + ":" + port);
					//System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " received data: " + name + " from " + node.getInetAddress().getHostName() + ":" + port);
					//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " data (content): " + new String(content));
					System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " data (content): " + new String(content));
					
					String result = processData(name);
					
					switch(result)
					{
					
					case "CAP_DATA":	//(srcRouter) 
					
						if(Parameters.multipath == true)
						{
							//141218 to do..
						}
						else
						{
							String datafield = new String(content);
							String des1 = datafield.substring(0, datafield.indexOf(":"));
							String temp = datafield.substring(0, datafield.lastIndexOf("/"));
							String des2 = temp.substring(temp.lastIndexOf("/")+1);
							
							if(des1.equals(des2))
							{
								//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " ROUTING INFO　QUERY SUCCESSFUL");
								System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " ROUTING INFO　QUERY SUCCESSFUL");
								
								Interest interest = new Interest("/ccn/PATHS/" + datafield.substring(datafield.indexOf(":")+1) + name.substring(name.lastIndexOf("/ccn/")+1));
								Message message = new Message(interest);
								
								temp = interest.getName().substring(interest.getName().indexOf("/" + Entry.localhost.getHostName() + "/")+Entry.localhost.getHostName().length()+2);
								String next = temp.substring(0, temp.indexOf("/"));
								
								//131118 forward interest according to fib  131127 rectified
								next = "/ccn/ROUTER_" + next;
								Vector<Node> nexthop = CCNDeamon.fib.get(next);	
								
								for (int i=0; i<nexthop.size(); i++)
								{
									Node n = nexthop.elementAt(i);
									
									//140111  add lifetime for /prefix (not start with /ccn) 
									//n.setMatchTimeForFIB(new Date().getTime());
									
									//if(n.getInetAddress().toString() != node.getInetAddress().toString())  131223
									if(!n.getInetAddress().getHostName().equals(node.getInetAddress().getHostName()))	
									{
										InetAddress ia = n.getInetAddress();
										int port = n.getPort();
										
										try
										{							
											ByteArrayOutputStream baos = new ByteArrayOutputStream();
									    	ObjectOutputStream oos = new ObjectOutputStream(baos);	    		           
									    	oos.writeObject(message);
									    	oos.close();
									    	DatagramPacket dp = new DatagramPacket(baos.toByteArray(), baos.size(), ia, port);
									    	_ds.send(dp);
									    	
									    	//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : CAP_DATA");
									    	System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : The Interest: " + interest.getName() + " is (following FIB) forwarded to " + ia.toString() + " : " + port);
									    	//System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : The Interest: " + interest.getName() + " is (following FIB) forwarded to " + ia.getHostName() + " : " + port);
										}
										catch(IOException e)
										{
											e.printStackTrace();
										}
									}
								}
								
								
								//140120 
								Vector<Node> vn1 = new Vector<Node>();
								//Vector<Node> vn2 = new Vector<Node>();
								vn1.addAll(nexthop);
								//vn2.addAll(nexthop);
								for (int i=0; i<vn1.size(); i++)
								{
									Node n = vn1.elementAt(i);
									n.setMatchTimeForFIB(new Date().getTime());  //add lifetime for /prefix (not start with /ccn)
								}
								//131118 add fib entries to prefix
								String fibPrefix = name.substring(name.lastIndexOf("/ccn/")+"/ccn/".length()-1, name.lastIndexOf("/")); 
								
								if(CCNDeamon.fib.containsKey(fibPrefix))
								{
									CCNDeamon.fib.remove(fibPrefix);
								}
							
								CCNDeamon.fib.put(fibPrefix, vn1);
								//CCNDeamon.fib.put(fibPrefix, nexthop);
								//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : fib entry added for prefix: " + fibPrefix);
								System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : fib entry added for prefix: " + fibPrefix);
								
								
								//131119 add fib entries to router
								temp = datafield.substring(datafield.indexOf(Entry.localhost.getHostName())+Entry.localhost.getHostName().length()+1);
								
								while(!temp.equals(""))
								{
									//140120
									Vector<Node> vn2 = new Vector<Node>();
									vn2.addAll(nexthop);
									
									fibPrefix = temp.substring(0, temp.indexOf("/"));
									
									if(!CCNDeamon.fib.containsKey("/ccn/ROUTER_"+fibPrefix))
									{
										CCNDeamon.fib.put("/ccn/ROUTER_"+fibPrefix, vn2);
										//CCNDeamon.fib.put("/ccn/ROUTER_"+fibPrefix, nexthop);
										//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : fib entry added for prefix: " + "/ccn/ROUTER_"+fibPrefix);
										System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : fib entry added for prefix: " + "/ccn/ROUTER_"+fibPrefix);
									}
									else
									{
										CCNDeamon.fib.remove("/ccn/ROUTER_"+fibPrefix);
										CCNDeamon.fib.put("/ccn/ROUTER_"+fibPrefix, vn2);
										//CCNDeamon.fib.put("/ccn/ROUTER_"+fibPrefix, nexthop);
										//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : fib entry updated for prefix: " + "/ccn/ROUTER_"+fibPrefix);
										System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : fib entry updated for prefix: " + "/ccn/ROUTER_"+fibPrefix);
									}
									
									temp = temp.substring(temp.indexOf("/")+1);
								}
								
								
								//131118 update entry to pit 
								if(CCNDeamon.pit.containsKey(name))
								{
									Vector<Node> nlist = CCNDeamon.pit.get(name);
									
									//131119 lifetime recalculation
									for(int i=0; i<nlist.size(); i++)
									{
										Node n = nlist.elementAt(i);
										n.setCreationTimeForPIT(new Date().getTime());
									}

									CCNDeamon.pit.remove(name);
									CCNDeamon.pit.put(interest.getName(), nlist);
									
									//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : PIT entry updated: " + name + " to " + interest.getName());
									System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : PIT entry updated: " + name + " to " + interest.getName());
								}
								else
								{	
									//140118 to do..
									
									System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : PIT entry not existed: " + name);
								}
								
								//System.err.println(Entry.localhost.toString() + " : CAP_DATA");

							}
							else
							{
								//System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " ROUTING INFO　QUERY FAILED");
								System.err.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " ROUTING INFO　QUERY FAILED");
							}
						}
						

						break;
					
					case "NEIGHBORS_ACK":	//(master) record neighbor list of router
						
						String routerID = name.substring(name.indexOf("_")+1, name.lastIndexOf("/"));
						
						if(CCNDeamon.neighborList.containsKey(routerID))
						{
							CCNDeamon.neighborList.remove(routerID);
						}
						
						CCNDeamon.neighborList.put(routerID, new String(content));
						
						//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : router-neighbors map added (updated): " + routerID + " : " + new String(content));
						System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : router-neighbors map added (updated): " + routerID + " : " + new String(content));
						
						//System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : NEIGHBORS_ACK");
						
						break;
					
					case "HELLO_ACK":	//add fib entry to neighbor router
						
						String neighbor = name.substring(name.indexOf("_")+1, name.lastIndexOf("/"));
						
						//131114
						long rtt = Long.parseLong(new String(content)) + new Date().getTime() - new Long(name.substring(name.lastIndexOf("/")+1));
						
						//131104  131114
						CCNDeamon.neighbors += neighbor + "_rtt_" + rtt + ";";
						
						//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : neighbor list updated: " + CCNDeamon.neighbors);
						System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : neighbor list updated: " + CCNDeamon.neighbors);
						
						//131129
						if(Parameters.role == "MASTER")
						{
							if(CCNDeamon.neighborList.containsKey(Entry.localhost.getHostName()))
							{
								CCNDeamon.neighborList.remove(Entry.localhost.getHostName());
							}
							
							CCNDeamon.neighborList.put(Entry.localhost.getHostName(), CCNDeamon.neighbors);
						}
						
						
						String fibPrefix = "/ccn/ROUTER_" + neighbor; 
						
						if(CCNDeamon.fib.containsKey(fibPrefix))
						{
							CCNDeamon.fib.remove(fibPrefix);
						}
						
						Vector<Node> list = new Vector<Node>();	
						list.add(node);
						CCNDeamon.fib.put(fibPrefix, list);
						System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : fib entry added: " + fibPrefix + " : " 
								  									  + node.getInetAddress().toString() + ":" + node.getPort());	
//						System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : fib entry added: " + fibPrefix + " : " 
//								  + node.getInetAddress().getHostName() + ":" + node.getPort());	
						
						//System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : HELLO_ACK");
						
						break;
					
					case "DATA_CACHE_MATCH":
						
						
						//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : data field: " + new String(content));
						System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : data field: " + new String(content));
						
						//System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : DATA_CACHE_MATCH");
						
						break;
						
						
					case "DATA_REPO_MATCH":

						//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : data field: " + new String(content));
						System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : data field: " + new String(content));
						
						//System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : DATA_REPO_MATCH");
						
						break;
						
						
					case "DATA_PIT_MATCH":	//return data according to pit
						
						//140113  add lifetime for /prefix (not start with /ccn) 
						if(!name.startsWith("/ccn/"))	
						{
							if(!Function.matchFIBLongestPrefix(name).equals("/") && CCNDeamon.fib.containsKey(Function.matchFIBLongestPrefix(name)))
							{
								Vector<Node> nodelist = CCNDeamon.fib.get(Function.matchFIBLongestPrefix(name));
								for(int i=0; i<nodelist.size(); i++)
								{
									Node n = nodelist.elementAt(i);
									n.setMatchTimeForFIB(new Date().getTime());
								}
							}
							
						}
						
						
						Vector<Node> nodelist = CCNDeamon.pit.get(name);
						
						for(int i=0; i<nodelist.size(); i++)
						{
							Node n = nodelist.elementAt(i);
							
							//131223 not return to sender
							if(!n.getInetAddress().getHostName().equals(node.getInetAddress().getHostName()))
							{
								InetAddress ia = n.getInetAddress();
								int port = n.getPort();
								
								try
								{
			    		    	 	ByteArrayOutputStream baos = new ByteArrayOutputStream();
			    		    	 	ObjectOutputStream oos = new ObjectOutputStream(baos);	    		           
			    		    	 	oos.writeObject(received);
			    		    	 	oos.close();
			    		    	 	DatagramPacket dp = new DatagramPacket(baos.toByteArray(), baos.size(), ia, port);
			    		    	 	_ds.send(dp); 
			    		    	 	
			    		    	 	System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : returned the data to " + ia.toString() + ":" + port);
			    		    	 	//System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : returned the data to " + ia.getHostName() + ":" + port);
								}
								catch(IOException e)
								{
									e.printStackTrace();
								}
							}
							

						}
							
						
						//put data to cache  131126 modified  131222 modified
						if((name.startsWith("/ccn/MASTER/MASTER_") && name.indexOf("/REGROUTER_ROUTER_") != -1) 
								|| (name.startsWith("/ccn/ROUTER_") && name.endsWith("/NEIGHBORS"))
								|| (name.startsWith("/ccn/MASTER/MASTER_") && name.indexOf("/REGPREFIX_ROUTER_") != -1)
								|| (name.equals("/ccn/MASTER")))
						{
							//NOT put data to cache
						}
						else
						{
							CCNDeamon.cache.put(name, content);
						}
		
						//delete entry in pit
						CCNDeamon.pit.remove(name);
						
						//get my request
						if(CCNDeamon.myrequest.contains(name))
						{
							//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : got my request: " + name);
							System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : got my request: " + name);
							CCNDeamon.myrequest.remove(name);
						
						}
						
						//131101 (intermediate ndoes) add fib entries to master
						if(name.equals("/ccn/MASTER"))
						{
							addRouteToMaster(node, content);
							
							//131224 (similar to DATA_NO_MATCH) not registered in master after forwarding /ccn/MASTER from others and sending its own  
							if(Parameters.registered == false)
							{
								
								//register itself to master
								registerRouter(node, content);
								
								//131105  register data (prefix) to master
								if(Parameters.role == "REPO")
								{
									registerPrefix(node, content);
								}
								
								Parameters.registered = true;
							}
						}
						
						//System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : DATA_PIT_MATCH");
						
						break;
						
						
					case "DATA_MYREQUEST_MATCH":
			
						CCNDeamon.received_my_request++;
						
						//put data to cache
						CCNDeamon.cache.put(name, content);
							
						//get my request
						if(CCNDeamon.myrequest.contains(name))
						{
							
							//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " got my request: " + name);
							System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " got my request: " + name);
							
							//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : data field: " + new String(content));
							System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : data field: " + new String(content));
							
							CCNDeamon.myrequest.remove(name);
							
							//140113
							//CCNDeamon.myrequest.sent.remove(name);
							//CCNDeamon.myrequest.notSatisfied.remove(name);
							CCNDeamon.myrequest.sent.remove(name);
							
//							Set set = CCNDeamon.myrequest.sent.entrySet();
//							if(set.size() > 0)
//							{
//								for(Iterator iterator = set.iterator(); iterator.hasNext();)
//								{
//									   Map.Entry entry = (Map.Entry)iterator.next();
//									   String s = (String)(entry.getKey());
//									   if(s.equals(name))
//									   {
//										   iterator.remove();
//										   break;
//									   }
//								}
//							}
							
							//TODO record receive time to file "RTT"
//							try {
//									FileOutputStream fos;
//									fos = new FileOutputStream("RTT.txt", true);							
//									long receivetime = new Date().getTime();
//									fos.write((receivetime + " received " + name + "\r\n").getBytes());
//									fos.close();
//							} catch (IOException e) {
//									e.printStackTrace();
//							}
					    	//140123  record receiving time and calculate RTT
					    	if(CCNDeamon.rtt.containsKey(name) && CCNDeamon.rtt.get(name)>Integer.MAX_VALUE)
					    	{
					    		long send = CCNDeamon.rtt.get(name);
					    		CCNDeamon.rtt.remove(name);
					    		CCNDeamon.rtt.put(name, new Date().getTime()-send);
					    	}

						}
						//131129 modified
						else if(CCNDeamon.myrequest.contains(name.substring(name.lastIndexOf("/ccn/") + "/ccn/".length() -1)))
						{
							
							//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " got my request: " + name.substring(name.lastIndexOf("/ccn/") + "/ccn/".length() -1));
							System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " got my request: " + name.substring(name.lastIndexOf("/ccn/") + "/ccn/".length() -1));
							
							//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : data field: " + new String(content));
							System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : data field: " + new String(content));
							
							CCNDeamon.myrequest.remove(name.substring(name.lastIndexOf("/ccn/") + "/ccn/".length() -1));
							
							//140113
							//CCNDeamon.myrequest.sent.remove(name.substring(name.lastIndexOf("/ccn/") + "/ccn/".length() -1));
							//CCNDeamon.myrequest.notSatisfied.remove(name.substring(name.lastIndexOf("/ccn/") + "/ccn/".length() -1));
							CCNDeamon.myrequest.sent.remove(name.substring(name.lastIndexOf("/ccn/") + "/ccn/".length() -1));
							
//							Set set = CCNDeamon.myrequest.sent.entrySet();
//							if(set.size() > 0)
//							{
//								for(Iterator iterator = set.iterator(); iterator.hasNext();)
//								{
//									   Map.Entry entry = (Map.Entry)iterator.next();
//									   String s = (String)(entry.getKey());
//									   if(s.equals(name.substring(name.lastIndexOf("/ccn/") + "/ccn/".length() -1)))
//									   {
//										   iterator.remove();
//										   break;
//									   }
//								}
//							}
					    	//140123  record receiving time and calculate RTT
					    	if(CCNDeamon.rtt.containsKey(name.substring(name.lastIndexOf("/ccn/") + "/ccn/".length() -1)) && CCNDeamon.rtt.get(name.substring(name.lastIndexOf("/ccn/") + "/ccn/".length() -1))>Integer.MAX_VALUE)
					    	{
					    		long send = CCNDeamon.rtt.get(name.substring(name.lastIndexOf("/ccn/") + "/ccn/".length() -1));
					    		CCNDeamon.rtt.remove(name.substring(name.lastIndexOf("/ccn/") + "/ccn/".length() -1));
					    		CCNDeamon.rtt.put(name.substring(name.lastIndexOf("/ccn/") + "/ccn/".length() -1), new Date().getTime()-send);
					    	}
							
						}
						
						//System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : DATA_MYREQUEST_MATCH");
	
						break;
						
						
					case "DATA_NO_MATCH":
						
						
						//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " received data (content): " + new String(content));
						System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " received data (content): " + new String(content));
								
						//131102 (requester) add fib entries to master and register itself to master
						if(name.equals("/ccn/MASTER"))
						{
							//(requester) add fib entries to master
							addRouteToMaster(node, content);
							
							//register itself to master
							registerRouter(node, content);
							
							//131105  register data (prefix) to master
							if(Parameters.role == "REPO")
							{
								registerPrefix(node, content);
							}
							
						}
						
						//System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : DATA_NO_MATCH");
						
						break;
						
						
					default:
						
						//System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : (data) no operation..");
						System.err.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : (data) no operation..");
						
						break;
							
					}
				}
		
			} 
			
		}
		finally 
		{
	      	   //System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : datagram socket close..system exit..");
				System.err.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : datagram socket close..system exit..");
	           _ds.close();
	           System.exit(1);
	    }
		
	}
	
	private String processInterest(String interest)
	{
		//boolean cache_match = CCNDeamon.cache.containsKey(interest);
		
		//131119 process paths interest
		if(interest.startsWith("/ccn/PATHS/"))
		{
			System.err.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : PATHS");
			
			return "PATHS";
		}
		//131111 (master) process capsulated interest
		else if(interest.startsWith("/ccn/MASTER/MASTER_") && interest.indexOf("/SRCROUTER_ROUTER_") != -1 && Parameters.role == "MASTER")
		{
			System.err.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : CAP_INTEREST");
			
			return "CAP_INTEREST";
		}
		//131105 process prefix registry message
		else if(interest.startsWith("/ccn/MASTER/MASTER_") && interest.indexOf("/REGPREFIX_ROUTER_") != -1)
		{
			System.err.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : REG_PREFIX");
			
			return "REG_PREFIX";
		}
		//131104 (requester) process neighbor query message
		else if(interest.equals("/ccn/ROUTER_" + Entry.localhost.getHostName() + "/NEIGHBORS"))
		{
			System.err.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : NEIGHBORS_QUERY");
			
			return "NEIGHBORS_QUERY";
		}
		//131103 process router registry message
		else if(interest.startsWith("/ccn/MASTER/MASTER_") && interest.indexOf("/REGROUTER_ROUTER_") != -1)
		{
			System.err.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : REG_ROUTER");
			
			return "REG_ROUTER";
		}
		//131101 process master discovery message as master
		else if(interest.equals("/ccn/MASTER") && Parameters.role == "MASTER")
		{
			System.err.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : IS_MASTER");
			
			return "IS_MASTER";			
		}
		//131101 process hello message
		else if(interest.startsWith("/ccn/HELLO/"))	//interest=/ccn/HELLO/timestamp
		{
			System.err.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : HELLO_MESSAGE");
			
			return "HELLO";
		}
		else if(CCNDeamon.cache.containsKey(interest))	//cache match
		{
			System.err.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " INTEREST_CACHE_MATCH");
			
			return "INTEREST_CACHE_MATCH";
		}
		else if(CCNDeamon.repo.containsKey(interest) || CCNDeamon.repo.containsKey("/ccn"+interest))	//repo match  131119 modified
		{
			System.err.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " INTEREST_REPO_MATCH");
			
			return "INTEREST_REPO_MATCH";
		}
		else if(CCNDeamon.pit.containsKey(interest))	//PIT match	
		{
			System.err.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " INTEREST_PIT_MATCH");
			
			return "INTEREST_PIT_MATCH";
		}
		//else if(CCNDeamon.fib.containsKey(interest.substring(0, interest.lastIndexOf("/"))))	//FIB prefix match  140117 modified
		else if(!Function.matchFIBLongestPrefix(interest).equals("/") && CCNDeamon.fib.containsKey(Function.matchFIBLongestPrefix(interest)))
		{
			System.err.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " INTEREST_FIB_MATCH");
			
			return "INTEREST_FIB_MATCH";
		}
		//else if(interest.startsWith("/ccn/MASTER/"))	//131101 process master discovery message not as master
		else if(interest.equals("/ccn/MASTER"))	//131108 process master discovery message NOT as master
		{
			System.err.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : IS_NOT_MASTER");
			
			return "IS_NOT_MASTER";	
		}
		else	//no match
		{
			System.err.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : INTEREST_NO_MATCH");
			
			return "INTEREST_NO_MATCH";
		}
	}
	
	private String processData(String data)
	{
		//boolean cache_match = CCNDeamon.cache.containsKey(interest);
		
		//131117 (srcRouter) process routing information data
		if(data.startsWith("/ccn/MASTER/MASTER_") && data.indexOf("/SRCROUTER_ROUTER_"+Entry.localhost.getHostName()+"/ccn/") != -1)
		{
			System.err.println(Entry.localhost.getHostName() + " : CAP_DATA");
			
			return "CAP_DATA";
		}
		//131104 process neighbor list message
		else if(data.startsWith("/ccn/ROUTER_") && data.endsWith("/NEIGHBORS") && Parameters.role == "MASTER")
		{
			System.err.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : NEIGHBORS_ACK");
			
			return "NEIGHBORS_ACK";
		}
		////131101 process hello ack message
		else if(data.startsWith("/ccn/HELLO/ROUTER_"))	//data=/ccn/HELLO/ROUTER_hostname/timestamp
		{
			System.err.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : HELLO_ACK");
			
			return "HELLO_ACK";
		}
		else if(CCNDeamon.cache.containsKey(data) && !data.equals("/ccn/MASTER"))	//cache match 131126 modified
		{
			System.err.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : DATA_CACHE_MATCH");
			
			return "DATA_CACHE_MATCH";
		}
		else if(CCNDeamon.repo.containsKey(data))	//repo match
		{
			System.err.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : DATA_REPO_MATCH");
			
			return "DATA_REPO_MATCH";
		}
		else if(CCNDeamon.pit.containsKey(data))	//PIT match	(include the data /ccn/MASTER for intermediate nodes)  131224 /ccn/MASTER processing when not registered in master  140113 /prefix/name processing (not start with "/ccn/")
		{
			System.err.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : DATA_PIT_MATCH");
			
			return "DATA_PIT_MATCH";
		}
		else if(CCNDeamon.myrequest.contains(data) || CCNDeamon.myrequest.contains(data.substring(data.lastIndexOf("/ccn/") + "/ccn/".length() -1)))	//myrequest match  131129 modified
		{
			System.err.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : DATA_MYREQUEST_MATCH");
			
			return "DATA_MYREQUEST_MATCH";
		}
		else	//no match (include the data /ccn/MASTER for requester)
		{
			System.err.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : DATA_NO_MATCH");
			
			return "DATA_NO_MATCH";
		}
	}
	

	private void addRouteToMaster(Node node, byte[] content)
	{
		
		//131101 add fib entries to master
		String fibPrefix1 = "/ccn/MASTER"; 
		String fibPrefix2 = "/ccn/MASTER/MASTER_" + new String(content); 
		
		if(CCNDeamon.fib.containsKey(fibPrefix1))
		{
			CCNDeamon.fib.remove(fibPrefix1);
		}
		
		if(CCNDeamon.fib.containsKey(fibPrefix2))
		{
			CCNDeamon.fib.remove(fibPrefix2);
		}
		
		Vector<Node> list1 = new Vector<Node>();	
		list1.add(node);
		CCNDeamon.fib.put(fibPrefix1, list1);
		System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : fib entry added: " + fibPrefix1 + " : " 
				  									  + node.getInetAddress().toString() + ":" + node.getPort());
//		System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : fib entry added: " + fibPrefix1 + " : " 
//				  + node.getInetAddress().getHostName() + ":" + node.getPort());
		
		Vector<Node> list2 = new Vector<Node>();	
		list2.add(node);
		CCNDeamon.fib.put(fibPrefix2, list2);
		System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : fib entry added: " + fibPrefix2 + " : " 
				  									  + node.getInetAddress().toString() + ":" + node.getPort());
//		System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : fib entry added: " + fibPrefix2 + " : " 
//				  + node.getInetAddress().getHostName() + ":" + node.getPort());
	}
	
	
	private void registerRouter(Node node, byte[] content)
	{
		Interest interest = new Interest("/ccn/MASTER/MASTER_" + new String(content) + "/REGROUTER_ROUTER_" + Entry.localhost.getHostName());
		Message message = new Message(interest);
						
		InetAddress ia = node.getInetAddress();
		int port = node.getPort();
		
		//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " sent router registry Interest to node : " + ia.toString() + ":" + port);
		System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " sent router registry Interest to node : " + ia.toString() + ":" + port);
		
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
	
	
	private void registerPrefix(Node node, byte[] content)
	{
		Vector<String> prefix = new Vector<String>();
		
		Set set = CCNDeamon.repo.entrySet();
		
		if(set.size() == 0)
		{
			System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : repo is empty!!");
		}
		  
		for(Iterator iterator = set.iterator(); iterator.hasNext();)
		  {
			   Map.Entry entry = (Map.Entry)iterator.next();

			   String repoEntry = (String)(entry.getKey());
			   String entryPrefix = repoEntry.substring(0, repoEntry.lastIndexOf("/"));
			   
			   if(!prefix.contains(entryPrefix))
			   {
				   prefix.add(entryPrefix);
			   }
		  }

		for(int i=0; i<prefix.size(); i++)
		{
			String pre = prefix.elementAt(i);
			
			Interest interest = new Interest("/ccn/MASTER/MASTER_" + new String(content) + "/REGPREFIX_ROUTER_" + Entry.localhost.getHostName() + pre);
			Message message = new Message(interest);
							
			InetAddress ia = node.getInetAddress();
			int port = node.getPort();
			
			System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " sent prefix registry Interest to node : " + ia.toString() + ":" + port);
			//System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " sent prefix registry Interest to node : " + ia.getHostName() + ":" + port);
			
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
	
	private void capsulateInterest(String name, Node node)
	{
		Set set = CCNDeamon.fib.entrySet();
		
		if(set.size() > 0)
		{																  
			for(Iterator iterator = set.iterator(); iterator.hasNext();)
			{
				   Map.Entry entry = (Map.Entry)iterator.next();
				   
				   String prefix = (String)(entry.getKey());
				   
				   if(prefix.startsWith("/ccn/MASTER/MASTER_"))
				   {
					   Vector<Node> toMaster = (Vector<Node>)(entry.getValue());
					   
					   String capsulated = prefix + "/SRCROUTER_ROUTER_" + Entry.localhost.getHostName() + "/ccn" + name;
					   Interest in = new Interest(capsulated);
					   Message message = new Message(in);
						
					   for(int i=0; i<toMaster.size(); i++)
					   {
						   Node n = toMaster.elementAt(i);
						   
							//if(n.getInetAddress().toString() != node.getInetAddress().toString())  131223
							if(!n.getInetAddress().getHostName().equals(node.getInetAddress().getHostName()))	
						   {
							   InetAddress ia = n.getInetAddress();
							   int port = n.getPort();
								
							   try
							   {							
								   ByteArrayOutputStream baos = new ByteArrayOutputStream();
								   ObjectOutputStream oos = new ObjectOutputStream(baos);	    		           
								   oos.writeObject(message);
								   oos.close();
								   DatagramPacket dp = new DatagramPacket(baos.toByteArray(), baos.size(), ia, port);
								   _ds.send(dp);
							    	
								   System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : The Interest: " + name + " is capsulated to " + capsulated);
								   System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : The Interest: " + name + " is (following FIB) forwarded to " + ia.toString() + " : " + port);
								   System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " sent capsulated Interest to node : " + ia.toString() + ":" + port);
								   
//								   System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : The Interest: " + name + " is capsulated to " + capsulated);
//								   System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : The Interest: " + name + " is (following FIB) forwarded to " + ia.getHostName() + " : " + port);
//								   System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " sent capsulated Interest to node : " + ia.getHostName() + ":" + port);
							   }
							   catch(IOException e)
							   {
								   e.printStackTrace();
							   }
							   
							   //add entry to pit 
							   Vector<Node> nl = new Vector<Node>(); 
							   node.setCreationTimeForPIT(new Date().getTime());
							   nl.add(node);
							   CCNDeamon.pit.put(capsulated, nl);
								
							   System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : PIT entry added: " + capsulated + " / " + node.getInetAddress().toString() + ":" + node.getPort());
							   //System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : PIT entry added: " + capsulated + " / " + node.getInetAddress().getHostName() + ":" + node.getPort());
							   
						   }
					   }
				   }
			 }
		}
	}
	
}
