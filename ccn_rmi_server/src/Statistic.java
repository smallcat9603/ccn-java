import java.util.Calendar;


public class Statistic extends Thread {
	
	public void run()
	{		
		while(true)
		{
			
			try {
				Thread.sleep(Parameters.statistic_interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			CCNDeamon.received_packets = CCNDeamon.received_packets + Calendar.getInstance().getTime() + " : received packets : " + CCNDeamon.received + "\r\n";
			CCNDeamon.received_packets_interest = CCNDeamon.received_packets_interest + Calendar.getInstance().getTime() + " : received interest packets : " + CCNDeamon.received_interest + "\r\n";
			CCNDeamon.received_packets_data = CCNDeamon.received_packets_data + Calendar.getInstance().getTime() + " : received data packets : " + CCNDeamon.received_data + "\r\n";
		}
	}

}
