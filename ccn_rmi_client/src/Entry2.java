import java.net.InetAddress;
import java.rmi.NotBoundException; 
import java.rmi.RemoteException; 
import java.rmi.registry.LocateRegistry; 
import java.rmi.registry.Registry; 
import java.text.DateFormat;
import java.util.*;


public class Entry2 {

	private static int interval = 500; //5s
	//private static int packets_before_interval = 0;
	
	private static int port = 2021; 
	
	private static float timestamp = 0; 
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

//		rmiRemote("192.168.1.19");
//		System.out.println("\r\n");
//		rmiRemote("192.168.1.15");
//		System.out.println("\r\n");
		
		
		
		//131222
//		while(true)
//		{
//			rmiRemote("localhost");
//			System.out.println("\r\n");
//			System.out.println("\r\n");
//			rmiRemote("planetlab4.goto.info.waseda.ac.jp");
//			System.out.println("\r\n");
//			System.out.println("\r\n");
//			rmiRemote("planetlab6.goto.info.waseda.ac.jp");
//			System.out.println("\r\n");
//			System.out.println("\r\n");
//			rmiRemote("planetlab0.otemachi.wide.ad.jp");
//			System.out.println("\r\n");
//			System.out.println("\r\n");
//			rmiRemote("planetlab1.otemachi.wide.ad.jp");
//			System.out.println("\r\n");
//			System.out.println("\r\n");
//			rmiRemote("planetlab2.sfc.wide.ad.jp");
//			System.out.println("\r\n");
//			System.out.println("\r\n");
//			System.out.println("\r\n");
//			System.out.println("\r\n");			
			
//			try {
//				Thread.sleep(interval); 
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}


		
//        try { 
            //Registry registry = LocateRegistry.getRegistry("localhost",2013); 
        	
//        	Registry registry = LocateRegistry.getRegistry("192.168.1.15",2013); 
//        	
//            UserManagerInterface userManager = (UserManagerInterface) registry.lookup("userManager"); 
//            System.out.println(userManager.getID()); 
//            System.out.println("cache size: " + userManager.getCache().size()); 
//            System.out.println("number of cache hit: " + userManager.getNumOfCacheHit());
//            
//            System.out.println("==========cache==========");
//            printCache(userManager.getCache());
//            
//            System.out.println("==========FIB==========");
//            printTable(userManager.getFIB());
//            
//            System.out.println("==========PIT==========");
//            printTable(userManager.getPIT());
//            
//            
//        } catch (RemoteException e) { 
//            // TODO Auto-generated catch block 
//            e.printStackTrace(); 
//        } catch (NotBoundException e) { 
//            // TODO Auto-generated catch block 
//            e.printStackTrace(); 
//        } 
		
		
		//140116 mobility
//		rmiRemote("planetlab1.dojima.wide.ad.jp");
//		rmiRemote("pub1-s.ane.cmc.osaka-u.ac.jp");
//		rmiRemote("pub2-s.ane.cmc.osaka-u.ac.jp");
//		rmiRemote("planet1.pnl.nitech.ac.jp");
//		rmiRemote("planet2.pnl.nitech.ac.jp");
//		rmiRemote("planetlab0.otemachi.wide.ad.jp");
//		rmiRemote("planetlab1.otemachi.wide.ad.jp");
//		rmiRemote("planetlab2.sfc.wide.ad.jp");
//		rmiRemote("planetlab4.goto.info.waseda.ac.jp");
//		rmiRemote("planetlab6.goto.info.waseda.ac.jp");
//
//		
//		
//		Timer timer0 = new Timer();
//		timer0.schedule(new TimerTask(){
//
//		   @Override
//		   public void run() {
//			   rmiRemote("localhost");
//		   }
//		   
//		  }, 10000);
//		
//		Timer timer1 = new Timer();
//		timer1.schedule(new TimerTask(){
//
//		   @Override
//		   public void run() {
//			   rmiRemote2("planetlab2.sfc.wide.ad.jp");
//		   }
//		   
//		  }, 20000);
//		
//
//		  Timer timer2 = new Timer();
//		  timer2.schedule(new TimerTask(){
//
//		   @Override
//		   public void run() {
//			   rmiRemote3("localhost");
//		   }
//		   
//		  }, 10000, interval);
//		
//		
////		try {
////			Thread.sleep(40000);
////		} catch (InterruptedException e) {
////			e.printStackTrace();
////		}
////		rmiRemote("nis-planet1.doshisha.ac.jp");
//		  
//			Timer timer3 = new Timer();
//			timer3.schedule(new TimerTask(){
//
//			   @Override
//			   public void run() {
//				   rmiRemote("nis-planet1.doshisha.ac.jp");
//			   }
//			   
//			  }, 28000);
//		  
//		
//		
//		Timer timer4 = new Timer();
//		timer4.schedule(new TimerTask(){
//
//		   @Override
//		   public void run() {
//				rmiRemote2("planetlab1.dojima.wide.ad.jp");
//				rmiRemote2("pub1-s.ane.cmc.osaka-u.ac.jp");
//				rmiRemote2("pub2-s.ane.cmc.osaka-u.ac.jp");
//				rmiRemote2("planet1.pnl.nitech.ac.jp");
//				rmiRemote2("planet2.pnl.nitech.ac.jp");
//				rmiRemote2("planetlab0.otemachi.wide.ad.jp");
//				rmiRemote2("planetlab1.otemachi.wide.ad.jp");
//				rmiRemote2("planetlab4.goto.info.waseda.ac.jp");
//				rmiRemote2("planetlab6.goto.info.waseda.ac.jp");
//				rmiRemote2("nis-planet1.doshisha.ac.jp");
//				rmiRemote2("localhost");
//				System.exit(1);
//		   }
//		   
//		  }, 50000);

		
		
		//140123 data requester mobility
		rmiRemote("planetlab-01.kusa.ac.jp");
		rmiRemote("pl2.sos.info.hiroshima-cu.ac.jp");
		rmiRemote("planet1.jaist.ac.jp");
		rmiRemote("planetlab2.sfc.wide.ad.jp");
		rmiRemote("nis-planet1.doshisha.ac.jp");
		rmiRemote("planetlab1.dojima.wide.ad.jp");
		rmiRemote("planetlab4.goto.info.waseda.ac.jp");
		rmiRemote("planetlab0.otemachi.wide.ad.jp");
		rmiRemote("planet1.pnl.nitech.ac.jp");
		rmiRemote("pub1-s.ane.cmc.osaka-u.ac.jp");
		
		Timer timer0 = new Timer();
		timer0.schedule(new TimerTask(){

		   @Override
		   public void run() {
			   rmiRemote("planetlab6.goto.info.waseda.ac.jp");
		   }
		   
		  }, 10000);
		
		Timer timer1 = new Timer();
		timer1.schedule(new TimerTask(){

		   @Override
		   public void run() {
			   getRTT("planetlab6.goto.info.waseda.ac.jp");
			   //rmiRemote2("planetlab6.goto.info.waseda.ac.jp");
		   }
		   
		  }, 30000);
		
		Timer timer2 = new Timer();
		timer2.schedule(new TimerTask(){

		   @Override
		   public void run() {
				rmiRemote("planetlab1.otemachi.wide.ad.jp");
		   }
		   
		  }, 50000);
		
		Timer timer3 = new Timer();
		timer3.schedule(new TimerTask(){

		   @Override
		   public void run() {
			   getRTT("planetlab1.otemachi.wide.ad.jp");
			   //rmiRemote2("planetlab1.otemachi.wide.ad.jp");
		   }
		   
		  }, 80000);
		
		Timer timer4 = new Timer();
		timer4.schedule(new TimerTask(){

		   @Override
		   public void run() {
				rmiRemote("planet2.pnl.nitech.ac.jp");
		   }
		   
		  }, 100000);
		
		Timer timer5 = new Timer();
		timer5.schedule(new TimerTask(){

		   @Override
		   public void run() {
			   getRTT("planet2.pnl.nitech.ac.jp");
			   //rmiRemote2("planet2.pnl.nitech.ac.jp");
		   }
		   
		  }, 200000);
		
		Timer timer6 = new Timer();
		timer6.schedule(new TimerTask(){

		   @Override
		   public void run() {
				rmiRemote("pub2-s.ane.cmc.osaka-u.ac.jp");
		   }
		   
		  }, 220000);
		
		Timer timer7 = new Timer();
		timer7.schedule(new TimerTask(){

		   @Override
		   public void run() {
			   getRTT("pub2-s.ane.cmc.osaka-u.ac.jp");
			   //rmiRemote2("pub2-s.ane.cmc.osaka-u.ac.jp");
		   }
		   
		  }, 235000);
		
		Timer timer8 = new Timer();
		timer8.schedule(new TimerTask(){

		   @Override
		   public void run() {
				rmiRemote2("planetlab-01.kusa.ac.jp");
				rmiRemote2("pl2.sos.info.hiroshima-cu.ac.jp");
				rmiRemote2("planet1.jaist.ac.jp");
				rmiRemote2("planetlab2.sfc.wide.ad.jp");
				rmiRemote2("nis-planet1.doshisha.ac.jp");
				rmiRemote2("planetlab1.dojima.wide.ad.jp");
				rmiRemote2("planetlab4.goto.info.waseda.ac.jp");
				rmiRemote2("planetlab0.otemachi.wide.ad.jp");
				rmiRemote2("planet1.pnl.nitech.ac.jp");
				rmiRemote2("pub1-s.ane.cmc.osaka-u.ac.jp");
				System.exit(1);
		   }
		   
		  }, 245000);

	}
	
	private static void rmiRemote(String hostname)
	{
        try { 
            //Registry registry = LocateRegistry.getRegistry("localhost",2013); 
        	
        	Registry registry = LocateRegistry.getRegistry(hostname, port); 
        	
            UserManagerInterface userManager = (UserManagerInterface) registry.lookup("userManager"); 
            System.err.println(" ==========" + userManager.getID() + " =========="); 
            
//            System.out.println(Calendar.getInstance().getTime() + " : cache size: " + userManager.getCache().size()); 
//            System.out.println(Calendar.getInstance().getTime() + " : number of cache hit: " + userManager.getNumOfCacheHit());
//            System.out.println(Calendar.getInstance().getTime() + " : number of pit entries: " + userManager.getNumOfPITEntry());
            
//            System.out.println(Calendar.getInstance().getTime() + " : number of received packets: " + userManager.getNumOfReceived());
//            System.out.println(Calendar.getInstance().getTime() + " : number of received interest: " + userManager.getNumOfReceivedInterest());
//            System.out.println(Calendar.getInstance().getTime() + " : number of received data: " + userManager.getNumOfReceivedData());
            
//            System.out.println(Calendar.getInstance().getTime() + " : number of unsatisfied requests: " + userManager.getNumOfUnsatisfiedRequests());
            
//            System.out.println(Calendar.getInstance().getTime() + " ==========cache==========");
//            printCache(userManager.getCache());
//            
//            System.out.println(Calendar.getInstance().getTime() + " ==========FIB==========");
//            printTable(userManager.getFIB());
//            
//            System.out.println(Calendar.getInstance().getTime() + " ==========PIT==========");
//            printTable(userManager.getPIT());
            
//            System.out.println(Calendar.getInstance().getTime() + " : number of received packets within last interval: " + (userManager.getNumOfReceived()-packets_before_interval));
//            packets_before_interval = userManager.getNumOfReceived();
            
//            System.out.println(userManager.getStatisticOfReceived());
//            System.out.println(userManager.getStatisticOfReceivedInterest());
//            System.out.println(userManager.getStatisticOfReceivedData());
            
            System.out.println(userManager.run());
    		
            
        } catch (RemoteException e) { 
            // TODO Auto-generated catch block 
            e.printStackTrace(); 
        } catch (NotBoundException e) { 
            // TODO Auto-generated catch block 
            e.printStackTrace(); 
        }
	}
	
	
	private static void rmiRemote2(String hostname)
	{
        try { 
        	
        	Registry registry = LocateRegistry.getRegistry(hostname, port); 
        	
            UserManagerInterface userManager = (UserManagerInterface) registry.lookup("userManager"); 
            System.err.println(" ==========" + userManager.getID() + " =========="); 
 
            System.out.println(userManager.shutdown());
  
        } catch (RemoteException e) { 
            // TODO Auto-generated catch block 
            e.printStackTrace(); 
        } catch (NotBoundException e) { 
            // TODO Auto-generated catch block 
            e.printStackTrace(); 
        }
	}
	
	
	private static void rmiRemote3(String hostname)
	{
        try { 
        	
        	Registry registry = LocateRegistry.getRegistry(hostname, port); 
        	
            UserManagerInterface userManager = (UserManagerInterface) registry.lookup("userManager"); 
 
            timestamp += 0.5;
            
            System.out.println(timestamp + "  " + userManager.getNumOfReceivedMyRequest());
  
        } catch (RemoteException e) { 
            // TODO Auto-generated catch block 
            e.printStackTrace(); 
        } catch (NotBoundException e) { 
            // TODO Auto-generated catch block 
            e.printStackTrace(); 
        }
	}
	
	private static void rmiRemote4(String hostname)
	{
        try { 
        	
        	Registry registry = LocateRegistry.getRegistry(hostname, port); 
        	
            UserManagerInterface userManager = (UserManagerInterface) registry.lookup("userManager"); 
 
          System.out.println(Calendar.getInstance().getTime() + " ==========PIT==========");
          printTable(userManager.getPIT());
  
        } catch (RemoteException e) { 
            // TODO Auto-generated catch block 
            e.printStackTrace(); 
        } catch (NotBoundException e) { 
            // TODO Auto-generated catch block 
            e.printStackTrace(); 
        }
	}
	
	private static void getRTT(String hostname)
	{
        try { 
        	
        	Registry registry = LocateRegistry.getRegistry(hostname, port); 
        	
            UserManagerInterface userManager = (UserManagerInterface) registry.lookup("userManager"); 
 
            Map<String, Long> rtt = userManager.getRTT();
            
    		Set set = rtt.entrySet();
    		
    		if(set.size() == 0)
    		{
    			System.out.println("(empty)");
    			return;
    		}
    		  
    		int n = 0;
    		long total = 0;
    		for(Iterator iterator = set.iterator(); iterator.hasNext();)
    		  {
    			   Map.Entry entry = (Map.Entry)iterator.next();
    			   String name = (String)(entry.getKey());
    			   long value = (long)(entry.getValue());
    			   //System.out.println(userManager.getID() + " : " + (String)(entry.getKey()) +" : " + Long.toString((long)(entry.getValue())));
    			   if(value < Integer.MAX_VALUE)
    			   {
    				     System.out.println(name.substring(name.lastIndexOf("/")+1) +" " + Long.toString(value));
    			   
    				     total += value;
    				     n++;
    			   }

    		  }
    		
    		System.out.println(userManager.getID() + " : " + "average RTT : " + total/n);
  
        } catch (RemoteException e) { 
            // TODO Auto-generated catch block 
            e.printStackTrace(); 
        } catch (NotBoundException e) { 
            // TODO Auto-generated catch block 
            e.printStackTrace(); 
        }
	}
	
	
	//print cache
	private static void printCache(Map<String, byte[]> map)
	{
		Set set = map.entrySet();
		
		if(set.size() == 0)
		{
			System.out.println("(empty)");
		}
		  
		for(Iterator iterator = set.iterator(); iterator.hasNext();)
		  {
			   Map.Entry entry = (Map.Entry)iterator.next();
			   System.out.println((String)(entry.getKey()) +" : " + new String((byte[])(entry.getValue())));
		  }
	}

	
	//print fib or pit
	private static void printTable(Map<String, Vector<Node>> map)
	{
		Set set = map.entrySet();
		
		if(set.size() == 0)
		{
			System.out.println("(empty)");
		}
		  
		for(Iterator iterator = set.iterator(); iterator.hasNext();)
		  {
			   Map.Entry entry = (Map.Entry)iterator.next();
			   System.out.println((String)(entry.getKey()) +" : "); 
			   Vector<Node> nodelist = (Vector<Node>)(entry.getValue());
			   for (int i=0; i<nodelist.size(); i++)
			   {
					Node n = nodelist.elementAt(i);
					
					InetAddress ia = n.getInetAddress();
					int port = n.getPort();
					
					System.out.println(ia.toString() +" : " + port);
			   }
		  }
	}

}



