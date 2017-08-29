import java.io.IOException;
import java.rmi.AlreadyBoundException; 
import java.rmi.RemoteException; 
import java.rmi.registry.LocateRegistry; 
import java.rmi.registry.Registry; 
import java.rmi.server.UnicastRemoteObject; 
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import java.net.*;

public class Entry {

	public static InetAddress localhost;
	
	//140117 "static"!!! to avoid "java.rmi.NoSuchObjectException: no such object in table"
	public static UserManagerImpl userManager; 
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws AlreadyBoundException, RemoteException {
		// TODO Auto-generated method stub
		
        //rmi
		//UserManagerImpl userManager = new UserManagerImpl(); 
		
		userManager = new UserManagerImpl(); 
        UserManagerInterface userManagerI = (UserManagerInterface)UnicastRemoteObject.exportObject(userManager,0); 
        // Bind the remote object's stub in the registry 
        Registry registry = LocateRegistry.createRegistry(Parameters.rmiport); 
        registry.rebind("userManager", userManagerI); 
        try {
        	localhost = InetAddress.getLocalHost();
			System.out.println(localhost.toString() + " : ccn_rmi_server is ready");
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
        
		//140115 keep ssh connection 
		new Nonstop().start();
        
        //ccn
//        try {
//        	//131222
//			//CCNDeamon.run(args[0]);
//        	
//        	//140111
//        	CCNDeamon.run();       	
//        	
//        	//test
////        	CCNDeamon ccn1 = new CCNDeamon(10007);
////        	CCNDeamon ccn2 = new CCNDeamon(10008);
////        	ccn1.start();
////        	ccn2.start();
//        	
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

	}

}
