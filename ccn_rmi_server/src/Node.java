import java.io.Serializable;
import java.net.*;

public class Node implements Serializable {
	
	private InetAddress _ia;
	private int _port;
	
	private long _creationTimeForPIT;
	
	//140111
	private long _matchTimeForFIB;
	
	public Node(InetAddress ia, int port)
	{
		_ia = ia;
		_port = port;
	}
	
	public InetAddress getInetAddress()
	{
		return _ia;
	}
	
	public int getPort()
	{
		return _port;
	}

	public void setCreationTimeForPIT(long creationTimeForPIT)
	{
		_creationTimeForPIT = creationTimeForPIT;
	}
	
	public long getCreationTimeForPIT()
	{
		return _creationTimeForPIT;
	}
	
	public void setMatchTimeForFIB(long matchTimeForFIB)
	{
		_matchTimeForFIB = matchTimeForFIB;
	}
	
	public long getMatchTimeForFIB()
	{
		return _matchTimeForFIB;
	}
}
