import java.io.*;


public class Data implements Serializable{
	
	private String name;
	private byte[] data;
	
	public Data(String s, byte[] dat)
	{
		name = s;
		data = dat;
	}
	
	public String getName()
	{
		
		return name;
	}

	public byte[] getData()
	{
		
		return data;
	}
}
