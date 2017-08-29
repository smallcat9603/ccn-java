
public class Nonstop extends Thread{

	public void run()
	{		
		while(true)
		{
			
			try {
				Thread.sleep(Parameters.nonstop_interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.print(".");
		}
	}
	
}
