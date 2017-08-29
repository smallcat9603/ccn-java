import java.net.InetAddress;
import java.util.*;


public class PITLifetimeCheck extends Thread{

	public void run()
	{
		while(true)
		{
			try {
				//Thread.sleep(Parameters.pit_lifetime);
				//140120
				Thread.sleep(Parameters.pit_lifetime/2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			Set set = CCNDeamon.pit.entrySet();
			
			if(set.size() > 0)
			{
				for(Iterator iterator = set.iterator(); iterator.hasNext();)
				{
					   Map.Entry entry = (Map.Entry)iterator.next();
					   //System.out.println("check lifetime for PIT: " + (String)(entry.getKey()) +" : "); 
					   Vector<Node> nodelist = (Vector<Node>)(entry.getValue());
					   Vector<Node> delete = new Vector<Node>();
					   for (int i=0; i<nodelist.size(); i++)
					   {
							Node n = nodelist.elementAt(i);
							
							if(new Date().getTime() - n.getCreationTimeForPIT() > Parameters.pit_lifetime)
							{
								//nodelist.remove(n);
								delete.add(n);
							}
							
					   }
					   
					   if(delete.size() > 0)
					   {
						   for(int i=0; i<delete.size(); i++)
						   {
							   nodelist.remove((Node)(delete.get(i)));
						   }
						   
						   if(nodelist.size() > 0)
						   {
							   entry.setValue(nodelist);
						   }
						   else
						   {
							   //CCNDeamon.pit.remove((String)(entry.getKey()));
							   //MUST be deleted by iterator!!
							   iterator.remove();
						   }
						   
					   }
					   
				}
			}
		
		}
	}
}
