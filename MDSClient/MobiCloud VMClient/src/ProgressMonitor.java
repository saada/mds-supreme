import java.text.DecimalFormat;


public class ProgressMonitor extends Thread {
	Invoke_Connnection running;
	DecimalFormat df=new DecimalFormat("0.0");
	double monit = 0.00;
	public ProgressMonitor(Thread r)
	{
		try
		{
			running = (Invoke_Connnection)r;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void run()
	{
		while(running.isAlive())
		{
			System.out.println("Progress : "+df.format(running.getProgress()*100) + "%");
			try {
				this.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
