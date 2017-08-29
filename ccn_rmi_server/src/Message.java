import java.io.*;


public class Message implements Serializable{
	
	private String message_type;
	private Interest interest;
	private Data data;
	
	public Message(Interest i)
	{
		message_type = "Interest";
		interest = i;
	}
	
	public Message(Data d)
	{
		message_type = "Data";
		data = d;
	}
	
	public String getMessageType()
	{
		return message_type;
	}
	
	public Interest getInterest()
	{
		return interest;
	}
	
	public Data getData()
	{
		return data;
	}

}
