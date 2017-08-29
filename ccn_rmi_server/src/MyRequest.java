import java.util.*;


public class MyRequest extends Vector<String> {
	
	//public Vector<String> sent;	
	//public Vector<String> notSatisfied;	
	
	public Map<String, Long> sent;
	
	public MyRequest()
	{
		//sent = new Vector<String>();
		//notSatisfied = new Vector<String>();
		
		sent = Collections.synchronizedMap(new LinkedHashMap());
	}
	
//	  public Object clone() {
//		    Object o = null;
//		    try {
//		      o = super.clone();
//		    } catch (CloneNotSupportedException e) {}
//		    return o;
//		  }
//	


}
