import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;


public class Function {
	
	public static void print(String s)
	{
		System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " " + s);
	}
	
	public static String matchFIBLongestPrefix(String name)
	{
		String interest = name;
		//String prefix = null;  140106 interest-fib-match
		String prefix = "/";
		
		Set set = CCNDeamon.fib.entrySet();
		
		if(set.size() > 0 && name.split("/").length > 2)	// /a=2, /=0, a/=1, /a/b=3 
		{
			boolean match = false;
			
			for(int i=0; i<name.split("/").length-1; i++)
			{
				for(Iterator iterator = set.iterator(); iterator.hasNext();)
				{
					   Map.Entry entry = (Map.Entry)iterator.next();
					   
					   if(interest.equals((String)(entry.getKey())))
					   {
							match = true;
							break;
					   }
				}
				
				if(match == true)
				{
					prefix = interest;
					break;
				}
				
				interest = interest.substring(0, interest.lastIndexOf("/"));
			}
		}
		return prefix;
	}
	
	
//	//131117 Dijkstra
//	public static String calculateRoute(String srcRouter, String desRouter)
//	{
//		//shortest path
//		String paths = srcRouter + "/";
//		
//		//shortest paths record  131127 modified
//		Map<String, Long> shortest = Collections.synchronizedMap(new LinkedHashMap());
//		
//		//init
//		shortest.put(paths, (long)0);
//		
//		if(CCNDeamon.neighborList.containsKey(srcRouter))
//		{
//			String neighborlist = CCNDeamon.neighborList.get(srcRouter);
//			
//			//temporary value			
//			String id = neighborlist.substring(0, neighborlist.indexOf("_rtt_"));
//			long min = Long.parseLong(neighborlist.substring(neighborlist.indexOf("_rtt_")+"_rtt_".length(), neighborlist.indexOf(";")));
//
//			long before = 0;
//			
//			while(neighborlist != null)
//			{
//				while(!neighborlist.equals(""))
//				{
//					String neighbor = neighborlist.substring(0, neighborlist.indexOf("_rtt_"));
//					long current = Long.parseLong(neighborlist.substring(neighborlist.indexOf("_rtt_")+"_rtt_".length(), neighborlist.indexOf(";")));
//									
//					if(desRouter.equals(neighbor))
//					{
//						paths += (neighbor+"/");
//						//break;
//						return paths;
//					}
//					
//					if(!srcRouter.equals(neighbor))
//					{
//						Set set = shortest.entrySet();
//						boolean match = false;
//						
//						for(Iterator iterator = set.iterator(); iterator.hasNext();)
//						  {
//							   Map.Entry entry = (Map.Entry)iterator.next();
//							   String nlist = (String)(entry.getKey());
//							   long time = (long)(entry.getValue());
//
//							   if(nlist.endsWith(neighbor+"/"))
//							   {
//								   match = true;
//								   if(before + current < time)
//								   {
//									   	shortest.remove(nlist);
//									   	shortest.put(paths+neighbor+"/", before+current);
//								   }
//
//							   }
//						  }
//						
//						if(match == false)
//						{
//							shortest.put(paths+neighbor+"/", before+current);
//							
//							if(current < min)
//							{
//								min = current;
//								id = neighbor;
//							}
//						}
//						
//					}
//
//					neighborlist = neighborlist.substring(neighborlist.indexOf(";")+1);
//				}
//				
//				paths += (id+"/");
//				before += min;
//				
//				neighborlist = CCNDeamon.neighborList.get(id);
//			}
//		}
//		
//		return paths;
//	}
	
	
	//140117 Dijkstra re-written (to do: can be also implemented by pure loop)
	public static String calculateRoute(String srcRouter, String desRouter)
	{
		//shortest path
		String paths = srcRouter + "/";
		long min = 0;
		
		//shortest paths record
		Map<String, Long> shortest = Collections.synchronizedMap(new LinkedHashMap());
		
		//init
		shortest.put(paths, (long)0);
		
		if(CCNDeamon.neighborList.containsKey(srcRouter))
		{
			String neighborlist = CCNDeamon.neighborList.get(srcRouter);
			
			//current vertex (path) in Dijkstra
			String id = srcRouter;

			while(true)
			{				
				while(!neighborlist.equals(""))
				{
					String neighbor = neighborlist.substring(0, neighborlist.indexOf("_rtt_"));
					long current = Long.parseLong(neighborlist.substring(neighborlist.indexOf("_rtt_")+"_rtt_".length(), neighborlist.indexOf(";")));
									
					if(desRouter.equals(neighbor))
					{
						paths += (neighbor+"/");
						//break;
						return paths;
					}
					
					if(!srcRouter.equals(neighbor))
					{
						Set set = shortest.entrySet();
						boolean match = false;
						
						for(Iterator iterator = set.iterator(); iterator.hasNext();)
						  {
							   Map.Entry entry = (Map.Entry)iterator.next();
							   String nlist = (String)(entry.getKey());
							   long time = (long)(entry.getValue());

							   if(nlist.endsWith(neighbor+"/"))
							   {
								   match = true;
								   if(min + current < time)
								   {
									   	shortest.remove(nlist);
									   	shortest.put(paths+neighbor+"/", min+current);
									   	break;
								   }
							   }
						  }
						
						if(match == false)
						{
							shortest.put(paths+neighbor+"/", min+current);
						}	
					}

					neighborlist = neighborlist.substring(neighborlist.indexOf(";")+1);
				}
				
				do 
				{
					//delete selected vertex (path)
					Set set = shortest.entrySet();			
					for(Iterator iterator = set.iterator(); iterator.hasNext();)
					{
						   Map.Entry entry = (Map.Entry)iterator.next();
						   String nlist = (String)(entry.getKey());
	
						   if(nlist.endsWith(id + "/"))
						   {
							   iterator.remove();
						   }	  
					 }
					
					//140118 avoid dead loop
					if(shortest.isEmpty())
						return paths;
					
					//select new id
					set = shortest.entrySet();		
					min= Long.MAX_VALUE;
					for(Iterator iterator = set.iterator(); iterator.hasNext();)
					{
							Map.Entry entry = (Map.Entry)iterator.next();
						   long time = (long)(entry.getValue());
	
						   if(time < min)
						   {
							   paths = (String)(entry.getKey());
							   min = time;
							   id = paths.substring(paths.substring(0, paths.lastIndexOf("/")).lastIndexOf("/")+1, paths.lastIndexOf("/"));
						   }
					 }
					
					neighborlist = CCNDeamon.neighborList.get(id);
				}while(neighborlist == null); //leaf node
			}
		}
		
		return paths;
	}

	
	//141217 calculate route based on number of hops (pure loop)
	public static String calculateRouteByHops(String srcRouter, String desRouter)
	{
		//shortest path based on hops
		String paths = srcRouter + "/";

		Vector<String> selectedNodes = new Vector<String>();

		if(CCNDeamon.neighborList.containsKey(srcRouter))
		{
			String neighborlist = CCNDeamon.neighborList.get(srcRouter);

			//hops == 1
			while(!neighborlist.equals(""))
			{
				String neighbor = neighborlist.substring(0, neighborlist.indexOf("_rtt_"));

				if(desRouter.equals(neighbor))
				{
					paths += (neighbor+"/");
					//break;
					return paths;
				}
				else
				{
					String s = paths + neighbor + "/";
					selectedNodes.add(s);
				}

				neighborlist = neighborlist.substring(neighborlist.indexOf(";")+1);
			}
			
			//hops > 1
			int hops = 2;
			Vector<String> store = new Vector<String>();
			
			while (hops < 100)
			{
				for (int i=0; i<selectedNodes.size(); i++)
				{
					String n = selectedNodes.elementAt(i);
					
					String temp = n.substring(0, n.lastIndexOf("/"));
					String current = temp.substring(temp.lastIndexOf("/")+1);
					
					String nl = CCNDeamon.neighborList.get(current);

					while(!nl.equals(""))
					{
						String neighbor = nl.substring(0, nl.indexOf("_rtt_"));

						if(desRouter.equals(neighbor))
						{
							paths = n + neighbor + "/";
							return paths;
						}
						else
						{
							String s = n + neighbor + "/";
							store.add(s);
						}

						nl = nl.substring(nl.indexOf(";")+1);
					}
				}
				
				selectedNodes = (Vector<String>)store.clone();
				store.clear();
				hops++;
			}
		}
		
		return paths;
	}
	
	
	//131211 construct fib according to topo which is represented by host names (ip address is not considered currently)
	public static void readTopo(Map<String, Vector<Node>> fib)
	{
		for(int i=0; i<Parameters.topo.length; i++)
		{
			String s = Parameters.topo[i];
			if(s.substring(0, s.indexOf(":")).equals(Entry.localhost.getHostName()) || s.substring(0, s.indexOf(":")).equals(Entry.localhost.getHostAddress()))
			{
				s = s.substring(s.indexOf(":")+1);
				
				Vector<Node> nodelist = new Vector<Node>();
				
				while(!s.equals(""))
				{
					try
					{
						String host = s.substring(0, s.indexOf(";"));
						InetAddress ia = InetAddress.getByName(host);
						Node node = new Node(ia, Parameters.ccnport);
						nodelist.add(node);

					} 
					catch (UnknownHostException e) 
					{
							e.printStackTrace();
					}
					
					s = s.substring(s.indexOf(";")+1);
				}
				
				fib.put("/", nodelist);
				
				//System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " default fib entry added: / ");	
				
			}
		}
	}
	
	//140111 read role 
	public static void readRole()
	{
		for(int i=0; i<Parameters.roles.length; i++)
		{
			String s = Parameters.roles[i];
			if(s.substring(0, s.indexOf(":")).equals(Entry.localhost.getHostName()) || s.substring(0, s.indexOf(":")).equals(Entry.localhost.getHostAddress()))
			{
				s = s.substring(s.indexOf(":")+1);
				
				if(s.equals("S") || s.equals("s"))
				{
					Parameters.role = "REPO";
					
					//System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " This node is run as a repoSitory.");
					System.err.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " This node is run as a repoSitory.");
					
					return;
				}
				else if(s.equals("M") || s.equals("m"))
				{
					Parameters.role = "MASTER";

					CCNDeamon.neighborList = Collections.synchronizedMap(new LinkedHashMap());
					CCNDeamon.prefix_ID = Collections.synchronizedMap(new LinkedHashMap());
							
					//System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " This node is run as a Master.");
					System.err.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " This node is run as a Master.");
					
					return;
				}
				else if(s.equals("U") || s.equals("u"))
				{
					Parameters.role = "USER";
					
					//140123
					CCNDeamon.rtt = Collections.synchronizedMap(new LinkedHashMap());
					
					//System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " This node is run as a User.");
					System.err.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " This node is run as a User.");
					
					return;
				}	
				
			}
//			else 
//			{
//				//System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " This node is run as a Router.");
//				System.err.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " This node is run as a Router.");
//			}
		}
		
		System.err.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " This node is run as a Router.");
	}
}
