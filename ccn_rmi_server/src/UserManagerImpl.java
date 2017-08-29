import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.*; 
import java.util.Map;
import java.util.Vector;

public class UserManagerImpl implements UserManagerInterface{
	
    public UserManagerImpl() throws RemoteException { 
        //super(); 
        // TODO Auto-generated constructor stub 
        //UnicastRemoteObject.exportObject(this); 
    } 
    
    private static final long serialVersionUID = -3111492742628447261L; 
    
    public String getID() throws RemoteException {
    	
    	return Entry.localhost.toString();

    }
    
    public Map<String, byte[]> getCache() throws RemoteException {
    	
    	//System.out.println("cache size: ");
    	//return CCNDeamon.cache.size();
    	return CCNDeamon.cache;
    	
    }
    
	public Map<String, Vector<Node>> getFIB() throws RemoteException {
		
		return CCNDeamon.fib;
	}
	
	public Map<String, Vector<Node>> getPIT() throws RemoteException {
		
		return CCNDeamon.pit;
	}
	
	public int getNumOfCacheHit() throws RemoteException {
		
		return CCNDeamon.cacheHit;
	}

	public int getNumOfPITEntry() throws RemoteException {
		
		return CCNDeamon.pit.size();
	}
	
	public int getNumOfReceived() throws RemoteException {
	
		return CCNDeamon.received;
	}
	
	public int getNumOfReceivedInterest() throws RemoteException {
		
		return CCNDeamon.received_interest;
	}
	
	public int getNumOfReceivedData() throws RemoteException {
	
		return CCNDeamon.received_data;
	}
	
	public int getNumOfUnsatisfiedRequests() throws RemoteException {
		
		return CCNDeamon.myrequest.size();
	}
	
	public String getStatisticOfReceived() throws RemoteException {
		
		return CCNDeamon.received_packets;
	}
	
	public String getStatisticOfReceivedInterest() throws RemoteException {
		
		return CCNDeamon.received_packets_interest;
	}
	
	public String getStatisticOfReceivedData() throws RemoteException {
		
		return CCNDeamon.received_packets_data;
	}
	
	public int getNumOfReceivedMyRequest() throws RemoteException {
		
		return CCNDeamon.received_my_request;
	}
	
	public Map<String, Long> getRTT() throws RemoteException {
		
		return CCNDeamon.rtt;
	}
	
	public String run() throws RemoteException {
		
		try {
			CCNDeamon.run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Entry.localhost.getHostName()+" is running.." ;
	}
	
	public String shutdown() throws RemoteException {
		
		CCNDeamon.shutdown();
		return Entry.localhost.getHostName()+" is down.." ;
	}
}

