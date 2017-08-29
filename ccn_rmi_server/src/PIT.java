import java.util.*;


public class PIT<K, V> extends LinkedHashMap<K, V> {
//public class PIT<K, V<N, T>> extends LinkedHashMap<K, LinkedHashMap<N, T>> {	


	private PIT<String, Vector<Node>> _pit;
	
	public PIT()
	{
		
	}
	
//	public static boolean isMatch(String s)
//	{
//		
//	}
//	
//	public static Vector<Node> getMatch(String s)
//	{
//		
//	}
	
	public static void addFace(String s, Node n)
	{
//		Vector<Node> nodelist = CCNDeamon.pit.get(s);
//		nodelist.add(n);
//		CCNDeamon.pit.remove(s);
//		CCNDeamon.pit.put(s, nodelist);		
		
		//131226 modified (not insert same node, just update creation time)
		Vector<Node> nlist = CCNDeamon.pit.get(s);
		boolean conflict = false;
		for(int i=0; i<nlist.size(); i++)
		{
			if(nlist.elementAt(i).getInetAddress().getHostName().equals(n.getInetAddress().getHostName()))
			{
				conflict = true;
				
				System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " obselete pit life time: " + nlist.elementAt(i).getCreationTimeForPIT());
				
				nlist.elementAt(i).setCreationTimeForPIT(new Date().getTime());
				
				System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : PIT entry already existed: " + s + " / " + n.getInetAddress().toString() + ":" + n.getPort());
				
				break;	
			}
		}
		
		if(conflict == false)
		{
			nlist.add(n);
			CCNDeamon.pit.remove(s);
			CCNDeamon.pit.put(s, nlist);
			
			System.out.println(Entry.localhost.toString() + " : " + new Date().getTime() + " : PIT entry (face) added: " + s + " / " + n.getInetAddress().toString() + ":" + n.getPort());
		}
	}

}