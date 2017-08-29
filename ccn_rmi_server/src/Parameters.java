import java.net.InetAddress;


public class Parameters {
	
	public static String SETUP = "Advanced";	//Standard, Advanced
	
	//141217 multipath
	public static boolean multipath = false;  //Fujita san, please change it to true;
	
	public static String role = "ROUTER"; 
		
	public static int ccnport = 10014;
	public static int rmiport = 2021;
	
	//public static int cache_capacity = 1024;
	public static int cache_capacity = 16;
	public static int repo_capacity = 64;
	public static int buffer_size = 512000;	//byte
	//public static int pit_lifetime = 2000;
	public static int hello_interval = 4000;
	//public static int fib_lifetime = 2000;
	
	//140120
	public static int pit_lifetime = 1000;
	public static int fib_lifetime = 1000;

	//public static int requests = 300;
	public static int requests = 1500;
	public static int requests_per_sec = 10;
	
	public static boolean registered = false;
	
	public static int statistic_interval = 5000;  //5s
	public static int nonstop_interval = 60000;   //60s
	
	public static int retransmission_timeout = 2500;  //2.5s
	
	public static String[] topo = {
		//example
		//"smallcat-PC:MACBOOKAIR-1C82;",
		//"smallcat-PC:smallcat-67513a;MACBOOKAIR-1C82;",
		//"planetlab6.goto.info.waseda.ac.jp:planetlab4.goto.info.waseda.ac.jp;"
		
		
		//ieice2014
//		"smallcat-PC:planetlab4.goto.info.waseda.ac.jp;",
//		"planetlab4.goto.info.waseda.ac.jp:planetlab6.goto.info.waseda.ac.jp;planetlab0.otemachi.wide.ad.jp;",
//		"planetlab6.goto.info.waseda.ac.jp:planetlab1.otemachi.wide.ad.jp;planetlab2.sfc.wide.ad.jp;",
//		"planetlab0.otemachi.wide.ad.jp:planetlab1.otemachi.wide.ad.jp;",
//		"planetlab1.otemachi.wide.ad.jp:planetlab6.goto.info.waseda.ac.jp;"
		
		
		//demo140108
//		"USER-no-MacBook-Air.local:planetlab4.goto.info.waseda.ac.jp;",
//		"planetlab4.goto.info.waseda.ac.jp:planetlab6.goto.info.waseda.ac.jp;"
		
		
		//data source mobility
//		"smallcat-PC:planetlab4.goto.info.waseda.ac.jp;",
//		
//		"planetlab4.goto.info.waseda.ac.jp:planetlab6.goto.info.waseda.ac.jp;planetlab0.otemachi.wide.ad.jp;",
//		"planetlab6.goto.info.waseda.ac.jp:planetlab1.otemachi.wide.ad.jp;",
//		"planetlab0.otemachi.wide.ad.jp:planetlab1.otemachi.wide.ad.jp;planet1.pnl.nitech.ac.jp;planet2.pnl.nitech.ac.jp;",
//		"planetlab1.otemachi.wide.ad.jp:planet2.pnl.nitech.ac.jp;planetlab2.sfc.wide.ad.jp;",
//		"planet1.pnl.nitech.ac.jp:pub1-s.ane.cmc.osaka-u.ac.jp;",
//		"planet2.pnl.nitech.ac.jp:planet1.pnl.nitech.ac.jp;pub2-s.ane.cmc.osaka-u.ac.jp;",
//		"pub1-s.ane.cmc.osaka-u.ac.jp:planetlab1.dojima.wide.ad.jp;",
//		"pub2-s.ane.cmc.osaka-u.ac.jp:pub1-s.ane.cmc.osaka-u.ac.jp;nis-planet1.doshisha.ac.jp;",
//		
//		"planetlab2.sfc.wide.ad.jp:planetlab1.otemachi.wide.ad.jp;",
//		"nis-planet1.doshisha.ac.jp:pub2-s.ane.cmc.osaka-u.ac.jp;"
		
		
		//data requester mobility
//		"planetlab6.goto.info.waseda.ac.jp:planetlab4.goto.info.waseda.ac.jp;",
//		"planetlab1.otemachi.wide.ad.jp:planetlab0.otemachi.wide.ad.jp;",
//		"planet2.pnl.nitech.ac.jp:planet1.pnl.nitech.ac.jp;",
//		"pub2-s.ane.cmc.osaka-u.ac.jp:pub1-s.ane.cmc.osaka-u.ac.jp;",
//		
//		"planetlab4.goto.info.waseda.ac.jp:planet1.jaist.ac.jp;planetlab0.otemachi.wide.ad.jp;",
//		"planetlab0.otemachi.wide.ad.jp:planet1.jaist.ac.jp;planetlab2.sfc.wide.ad.jp;planet1.pnl.nitech.ac.jp;",
//		"planet1.pnl.nitech.ac.jp:nis-planet1.doshisha.ac.jp;planetlab1.dojima.wide.ad.jp;pub1-s.ane.cmc.osaka-u.ac.jp;",
//		"pub1-s.ane.cmc.osaka-u.ac.jp:planetlab1.dojima.wide.ad.jp;",
//		"planet1.jaist.ac.jp:pl2.sos.info.hiroshima-cu.ac.jp;",
//		"planetlab2.sfc.wide.ad.jp:pl2.sos.info.hiroshima-cu.ac.jp;",
//		"nis-planet1.doshisha.ac.jp:pl2.sos.info.hiroshima-cu.ac.jp;",
//		"planetlab1.dojima.wide.ad.jp:pl2.sos.info.hiroshima-cu.ac.jp;",
//		
//		"pl2.sos.info.hiroshima-cu.ac.jp:planetlab-01.kusa.ac.jp;"
		
		//data requester mobility (standard)
//		"planetlab4.goto.info.waseda.ac.jp:planet1.jaist.ac.jp;",
//		"planet1.jaist.ac.jp:pl2.sos.info.hiroshima-cu.ac.jp;",
//		
//		"planetlab0.otemachi.wide.ad.jp:planetlab2.sfc.wide.ad.jp;",
//		"planetlab2.sfc.wide.ad.jp:pl2.sos.info.hiroshima-cu.ac.jp;",
//		
//		"planet1.pnl.nitech.ac.jp:nis-planet1.doshisha.ac.jp;",
//		"nis-planet1.doshisha.ac.jp:pl2.sos.info.hiroshima-cu.ac.jp;",
//		
//		"pub1-s.ane.cmc.osaka-u.ac.jp:planetlab1.dojima.wide.ad.jp;",
//		"planetlab1.dojima.wide.ad.jp:pl2.sos.info.hiroshima-cu.ac.jp;",
//		
//		"planetlab6.goto.info.waseda.ac.jp:planetlab4.goto.info.waseda.ac.jp;",
//		"planetlab1.otemachi.wide.ad.jp:planetlab0.otemachi.wide.ad.jp;",
//		"planet2.pnl.nitech.ac.jp:planet1.pnl.nitech.ac.jp;",
//		"pub2-s.ane.cmc.osaka-u.ac.jp:pub1-s.ane.cmc.osaka-u.ac.jp;",
	};
	
	//140111  u user, s repository, m master, r router(default)
	public static String[] roles = {

		//test
//		"smallcat-PC:u",
//		
//		"nis-planet1.doshisha.ac.jp:s",
//		"planetlab2.sfc.wide.ad.jp:s",
//		"planetlab1.dojima.wide.ad.jp:m",
		//"USER-no-MacBook-Air.local:u"
		
		
		//data requester mobility
//		"planetlab6.goto.info.waseda.ac.jp:u",
//		"planetlab1.otemachi.wide.ad.jp:u",
//		"planet2.pnl.nitech.ac.jp:u",
//		"pub2-s.ane.cmc.osaka-u.ac.jp:u",
//		
//		"pl2.sos.info.hiroshima-cu.ac.jp:s",
//		"planetlab-01.kusa.ac.jp:m"
		
		//data requester mobility (standard)
//		"planetlab6.goto.info.waseda.ac.jp:u",
//		"planetlab1.otemachi.wide.ad.jp:u",
//		"planet2.pnl.nitech.ac.jp:u",
//		"pub2-s.ane.cmc.osaka-u.ac.jp:u",
//		
//		"pl2.sos.info.hiroshima-cu.ac.jp:s",

	};
}
