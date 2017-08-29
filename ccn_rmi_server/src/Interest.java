import java.io.*;


public class Interest implements Serializable{
	
	private String name;
	private String prefix;
	
	public Interest(String s)
	{
		name = s;
	}
	
	public String getName()
	{
		
		return name;
	}
	
	public String getPrefix()
	{
		prefix = name.substring(0,name.lastIndexOf("/"));
		
		return prefix;
	}

}
