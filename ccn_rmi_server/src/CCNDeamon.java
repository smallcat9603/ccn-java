//import java.util.Collections;
import java.net.*;
import java.io.*;
//import java.util.Map;
import java.util.*;



/**
 * 
 * @author huyao
 * @version 0.7 (ccn + rmi + new + planetlab)
 *
 */

public class CCNDeamon /*extends Thread*/ {
	
	//private static int ccnport;
	
//	public CCNDeamon(int port)
//	{
//		Parameters.ccnport = port;
//	}
	
	//public static InetAddress localhost ;
	
	public static int cacheHit = 0; 
	
	public static Map<String, byte[]> repo;
	public static Map<String, byte[]> cache;
	public static Map<String, Vector<Node>> fib;
	public static Map<String, Vector<Node>> pit;
	//public static Map<String, Map<Node, Long>> pit;
	
	//131211
	//public static Map<String, String> topo;
	
	//131222
	public static int received = 0;
	public static int received_interest = 0;
	public static int received_data = 0;
	
	//140116
	public static int received_my_request = 0;
	
	public static String received_packets = "";
	public static String received_packets_interest = "";
	public static String received_packets_data = "";
	
//	public static Repo repo;
//	public static Cache cache;
//	public static FIB fib;
//	public static PIT pit;
	
	public static MyRequest myrequest;
	//131224
	public static MyRequest tosend;
	
	private static DatagramSocket ds;

	//131104 neighbor list
	public static String neighbors = "";	//router
	public static Map<String, String> neighborList;	//master
	//131105 prefix-ID map
	public static Map<String, String> prefix_ID;	//master
	
	
	//140123
	public static Map<String, Long> rtt;  //user
	
	/**
	 * @param args
	 */
	//public static void run(String arg) throws IOException{
	public static void run() throws IOException{
		// TODO Auto-generated method stub
//		System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " CCN-JaVEX is starting...");
//		System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " Version: 0.6alpha");
//		System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " Author: huyao");
		
		System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " CCN-JaVEX is starting...");
		System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " Version: 0.7");
		System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " Author: huyao");
		
//		System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " This node is run as a [R]outer (default), [U]ser, Repo[S]itory or [M]aster ?");
		
		//131106 node type
//		String line = null;
//		
//		try
//		{
//			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//			
//			line = br.readLine();
//		}
//		catch(IOException e)
//		{
//			e.printStackTrace();
//		}
		
//		if(line.equals("S") || line.equals("s"))
//		{
//			Parameters.role = "REPO";
//			System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " This node is run as a repoSitory.");
//		}
//		else if(line.equals("M") || line.equals("m"))
//		{
//			Parameters.role = "MASTER";
//			
//			//131125
//			neighborList = Collections.synchronizedMap(new LinkedHashMap());
//			prefix_ID = Collections.synchronizedMap(new LinkedHashMap());
//					
//			System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " This node is run as a Master.");
//		}
//		else if(line.equals("U") || line.equals("u"))
//		{
//			Parameters.role = "USER";
//			System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " This node is run as a User.");
//		}
//		else 
//		{
//			System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " This node is run as a Router.");
//		}

		//131222 node type designated by args[0]	
//		if(arg.equals("S") || arg.equals("s"))
//		{
//			Parameters.role = "REPO";
//			//System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " This node is run as a repoSitory.");
//			System.err.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " This node is run as a repoSitory.");
//		}
//		else if(arg.equals("M") || arg.equals("m"))
//		{
//			Parameters.role = "MASTER";
//			
//			//131125
//			neighborList = Collections.synchronizedMap(new LinkedHashMap());
//			prefix_ID = Collections.synchronizedMap(new LinkedHashMap());
//					
//			//System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " This node is run as a Master.");
//			System.err.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " This node is run as a Master.");
//		}
//		else if(arg.equals("U") || arg.equals("u"))
//		{
//			Parameters.role = "USER";
//			
//			//System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " This node is run as a User.");
//			System.err.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " This node is run as a User.");
//		}
//		else 
//		{
//			//System.err.println(Entry.localhost.toString() + " : " + new Date().getTime() + " This node is run as a Router.");
//			System.err.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " This node is run as a Router.");
//		}
		
		
		//140111 
		Function.readRole();
		
		repoInit();
		cacheInit();
		updateFIB();
		updatePIT();
		
		//myrequestInit();
		myrequestInit2(Parameters.requests);
//		if(Entry.localhost.getHostName().equals("planetlab6.goto.info.waseda.ac.jp"))
//		{
//			myrequestInit3(Parameters.requests*150/Parameters.requests);
//		}
//		if(Entry.localhost.getHostName().equals("planetlab1.otemachi.wide.ad.jp"))
//		{
//			myrequestInit3(Parameters.requests*250/Parameters.requests);
//		}
//		if(Entry.localhost.getHostName().equals("planet2.pnl.nitech.ac.jp"))
//		{
//			myrequestInit3(Parameters.requests*1000/Parameters.requests);
//		}
//		if(Entry.localhost.getHostName().equals("pub2-s.ane.cmc.osaka-u.ac.jp"))
//		{
//			myrequestInit3(Parameters.requests*100/Parameters.requests);
//		}
		//myrequestInit3(Entry.localhost.getHostName());
		
		
		try
		{			
			ds = new DatagramSocket(Parameters.ccnport);		
			ds.setReceiveBufferSize(Parameters.buffer_size);			
			ds.setSendBufferSize(Parameters.buffer_size);
		}
		catch (SocketException e) 
		{
			e.printStackTrace();
	        //System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " datagram socket exception..");
			System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " datagram socket exception..");
		}
		
		//client side	131126 modified
		if(Parameters.role != "MASTER")
		{
			new Client(ds).start();
		}
		
		//new Thread(new Client(ds)).start();
		//System.out.println("Client thread gets started..");
		
		//server side
//		try
//		{
//			
//			while(true) 
//			{				
//				
//				
//				byte[] buffer = new byte[Parameters.buffer_size];
//				DatagramPacket dp = new DatagramPacket(buffer, Parameters.buffer_size);
//		    	         	    
//		    	ds.receive(dp);
//		    	
////	    	    try
////	    	    {
//		    	    new Thread(new Server(ds, dp)).start();
//		    	    System.out.println("Server thread gets started..");
////		    	}
////	    	    catch (InterruptedException e) 
////	    	    {
////	    	    	e.printStackTrace();
////	    	    }
//			} 
//		}
//		finally 
//		{
//	      	   System.out.println("datagram socket close..system exit..");
//	           ds.close();
//	           System.exit(1);
//	    }
		
		//server side
		new Server(ds).start();
		//new Thread(new Server(ds)).start();
		//System.out.println("Server thread gets started..");
		
		//check lifetime of pit entries and delete the timeout ones
		new PITLifetimeCheck().start();
		
		if(Parameters.SETUP == "Advanced")
		{
			//send hello message to neighbor nodes
			new HelloMessage(ds).start();	
			
			//140111 delete timeout fib entries (not start with "/ccn/")
			new FIBLifetimeCheck().start();
		}

		//131226
		//new Statistic().start();
		
		//140107 keep ssh connection 
		//new Nonstop().start();
		
		//140113
		if(Parameters.role == "USER")
		{
			new Retransmission(ds).start();
		}
		
	}
	
	
	//140115
	public static void shutdown() 
	{
		System.err.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " : datagram socket close..system exit..");
        ds.close();
        System.exit(1);
	}
	
	
	public static void repoInit()
	{
		repo = Collections.synchronizedMap(new Repo());
		
		//test
		if(Parameters.role == "REPO")
		{
			repo.put("/ccn/waseda/gotolab/news2012", "huyao san entered into doctor course in goto lab".getBytes());
//			System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " data put in repo: /ccn/waseda/gotolab/news2012 : " 
//														  + new String(repo.get("/ccn/waseda/gotolab/news2012")));
			
			System.out.println(Entry.localhost.getHostName() + " : " + new Date().getTime() + " data put in repo: /ccn/waseda/gotolab/news2012 : " 
					  + new String(repo.get("/ccn/waseda/gotolab/news2012")));
			
			//131222
			for(int i=0; i<Parameters.requests; i++)
			{
				repo.put("/ccn/waseda/gotolab/test/"+i, ("/ccn/waseda/gotolab/test/"+i).getBytes());
			}
		}

	}
	
	public static void cacheInit()
	{
		cache = Collections.synchronizedMap(new Cache(Parameters.cache_capacity));
		
		//test
//		cache.put("/ccn/waseda/gotolab/news2013", "news2013".getBytes());
//		System.out.println(Entry.localhost.toString() + " : data in cache: /ccn/waseda/gotolab/news2013 : " 
//													  + new String(cache.get("/ccn/waseda/gotolab/news2013")));
		
		//test	
//		if(Entry.localhost.getHostName().equals("planetlab0.otemachi.wide.ad.jp"))
//		{
//			int num = Parameters.requests*250/Parameters.requests;
//			for(int i=0; i<num; i++)
//			{
//				//String s = "/waseda/gotolab/test/" + (150+i);
//				String s = "/ccn/waseda/gotolab/test/" + (150+i);
//				cache.put(s, s.getBytes());
//			}
//			System.out.println(Entry.localhost.toString() + " : data in cache"); 
//		}
//		if(Entry.localhost.getHostName().equals("planet1.pnl.nitech.ac.jp"))
//		{
//			int num = Parameters.requests*1000/Parameters.requests;
//			for(int i=0; i<num; i++)
//			{
//				//String s = "/waseda/gotolab/test/" + (400+i);
//				String s = "/ccn/waseda/gotolab/test/" + (400+i);
//				cache.put(s, s.getBytes());
//			}
//			System.out.println(Entry.localhost.toString() + " : data in cache"); 
//		}
//		if(Entry.localhost.getHostName().equals("pub1-s.ane.cmc.osaka-u.ac.jp"))
//		{
//			int num = Parameters.requests*100/Parameters.requests;
//			for(int i=0; i<num; i++)
//			{
//				//String s = "/waseda/gotolab/test/" + (1400+i);
//				String s = "/ccn/waseda/gotolab/test/" + (1400+i);
//				cache.put(s, s.getBytes());
//			}
//			System.out.println(Entry.localhost.toString() + " : data in cache"); 
//		}

	}
	
	public static void updateFIB()
	{
		fib = Collections.synchronizedMap(new FIB());
		
		//131211
		//topo = Collections.synchronizedMap(new LinkedHashMap());
		
		//test	fib entry -> local machine
//		Node node;
//		//localhost = InetAddress.getLocalHost();
//		node = new Node(Entry.localhost, Parameters.ccnport);
//		Vector<Node> nodelist = new Vector<Node>();	
//		nodelist.add(node);
//		fib.put("/ccn/waseda/gotolab", nodelist);
//		System.out.println(Entry.localhost.toString() + " : fib entry added: /ccn/waseda/gotolab : " 
//				  									  + Entry.localhost.toString() + ":" + Parameters.ccnport);	
//		
//		//131101 add the default fib entry
//		fib.put("/", nodelist);
//		System.out.println(Entry.localhost.toString() + " : default fib entry added: / : " 
//				  									  + Entry.localhost.toString() + ":" + Parameters.ccnport);	
		
		
		//test	fib entry -> remote machine
		//Node node;
//		Node node2;
//		//byte ip[] = new byte[] {(byte)192, (byte)168, 1, 11};
//		byte ip2[] = new byte[] {(byte)192, (byte)168, 1, 17};
//		//InetAddress ia;
//		InetAddress ia2;
//		try {
//			//ia = InetAddress.getByAddress(ip);
//			ia2 = InetAddress.getByAddress(ip2);
//			//node = new Node(ia, Parameters.ccnport);
//			node2 = new Node(ia2, Parameters.ccnport);
//			Vector<Node> nodelist = new Vector<Node>();	
//			//nodelist.add(node);
//			nodelist.add(node2);
//			fib.put("/", nodelist);
//			System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " default fib entry added: / ");	
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		}

		Function.readTopo(fib);
		
		//131226 test standard
		//fib.put("/ccn/waseda/gotolab/test", fib.get("/"));
		//fib.put("/ccn/waseda/gotolab", fib.get("/"));
		
	}
	
	public static void updatePIT()
	{
		pit = Collections.synchronizedMap(new PIT());
		
		//test
//		Node node;
//		//localhost = InetAddress.getLocalHost();
//		node = new Node(Entry.localhost, Parameters.ccnport);
//		node.setCreationTimeForPIT(new Date().getTime());
//		Vector<Node> nodelist = new Vector<Node>();	
//		nodelist.add(node);
//		pit.put("/ccn/waseda/gotolab/test", nodelist);
//		Node node1;
//		//localhost = InetAddress.getLocalHost();
//		node1 = new Node(Entry.localhost, Parameters.ccnport);
//		node1.setCreationTimeForPIT(new Date().getTime());
//		Vector<Node> nodelist1 = new Vector<Node>();	
//		nodelist1.add(node1);
//		pit.put("/waseda/gotolab/test", nodelist1);
//		System.out.println(Entry.localhost.toString() + " : pit entry added: /ccn/waseda/gotolab/test : " 
//				  									  + Entry.localhost.toString() + ":" + Parameters.ccnport);	
		
	}

	public static void myrequestInit()
	{
		myrequest = new MyRequest();
		tosend = new MyRequest();
		
		//test	
		if(Parameters.role == "USER")
		{
			String line = null;
			
			//change the text color, not the error message
			System.err.println("[##] please input the interest name under the prefix /ccn/waseda/gotolab/: ");
			
			try
			{
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				
	//			while((line = br.readLine()) != null)
	//			{
	//				
	//			}
				
				line = br.readLine();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			
			
			//String s = "/ccn/waseda/gotolab/news2012";
			String s = "/ccn/waseda/gotolab/" + line;
			
			myrequest.add(s);
			tosend.add(s);
		}
		

	}
	
	//131222 
	public static void myrequestInit2(int num)
	{
		myrequest = new MyRequest();
		tosend = new MyRequest();

		//test	
		if(Parameters.role == "USER")
		{
			
			for(int i=0; i<num; i++)
			{
				String s = "/waseda/gotolab/test/" + i;
				//String s = "/ccn/waseda/gotolab/test/" + i;
				myrequest.add(s);
				tosend.add(s);
			}
		}

	}
	
	//140124 modified
	public static void myrequestInit3(String hostname)
	{
		myrequest = new MyRequest();
		tosend = new MyRequest();
		
		//test	
		if(Parameters.role == "USER")
		{
			if(hostname.equals("planetlab6.goto.info.waseda.ac.jp"))
			{
				int num = Parameters.requests*150/Parameters.requests;
				for(int i=0; i<num; i++)
				{
					String s = "/waseda/gotolab/test/" + i;
					//String s = "/ccn/waseda/gotolab/test/" + i;
					myrequest.add(s);
					tosend.add(s);
				}
			}
			if(hostname.equals("planetlab1.otemachi.wide.ad.jp"))
			{
				int num = Parameters.requests*250/Parameters.requests;
				for(int i=0; i<num; i++)
				{
					String s = "/waseda/gotolab/test/" + (150+i);
					//String s = "/ccn/waseda/gotolab/test/" + (150+i);
					myrequest.add(s);
					tosend.add(s);
				}
			}
			if(hostname.equals("planet2.pnl.nitech.ac.jp"))
			{
				int num = Parameters.requests*1000/Parameters.requests;
				for(int i=0; i<num; i++)
				{
					String s = "/waseda/gotolab/test/" + (400+i);
					//String s = "/ccn/waseda/gotolab/test/" + (400+i);
					myrequest.add(s);
					tosend.add(s);
				}
			}
			if(hostname.equals("pub2-s.ane.cmc.osaka-u.ac.jp"))
			{
				int num = Parameters.requests*100/Parameters.requests;
				for(int i=0; i<num; i++)
				{
					String s = "/waseda/gotolab/test/" + (1400+i);
					//String s = "/ccn/waseda/gotolab/test/" + (1400+i);
					myrequest.add(s);
					tosend.add(s);
				}
			}
			

		}

	}

}
