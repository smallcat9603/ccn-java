import java.rmi.*;
import java.util.Map;
import java.util.Vector;

public interface UserManagerInterface extends Remote{
	
	//get remote machine ID
	public String getID() throws RemoteException;
	
	//get cache of remote machine
	public Map<String, byte[]> getCache() throws RemoteException;
	
	//get FIB of remote machine
	public Map<String, Vector<Node>> getFIB() throws RemoteException;
	
	//get PIT of remote machine
	public Map<String, Vector<Node>> getPIT() throws RemoteException;
	
	//get No. of cache hits
	public int getNumOfCacheHit() throws RemoteException;
	
	//get No. of pit entries
	public int getNumOfPITEntry() throws RemoteException;
	
	//get No. of received packets
	public int getNumOfReceived() throws RemoteException;
	
	//get No. of received interests
	public int getNumOfReceivedInterest() throws RemoteException;
	
	//get No. of received data
	public int getNumOfReceivedData() throws RemoteException;
	
	//get No. of unsatisfied requests
	public int getNumOfUnsatisfiedRequests() throws RemoteException;
	
	//get statistic of received packets
	public String getStatisticOfReceived() throws RemoteException;
	
	//get statistic of received interest packets
	public String getStatisticOfReceivedInterest() throws RemoteException;
	
	//get statistic of received interest packets
	public String getStatisticOfReceivedData() throws RemoteException;
	
	//get No. of received my requests
	public int getNumOfReceivedMyRequest() throws RemoteException;
	
	//get RTT for requester
	public Map<String, Long> getRTT() throws RemoteException;
	
	//140116 (node up & down) controlled by ccn manager
	public String run() throws RemoteException;
	public String shutdown() throws RemoteException;

}
