package PerfectFailureDetector;
import java.util.*;



public class PerfectFailureDetector implements IFailureDetector{
    
	// The process this failure detector belongs to
	private Process p;
    
    // A timer that updates suspected processes
    private Timer t;
    
    // List to store suspected processes
    private List<Integer> suspectedProcesses;
    
    // The interval to send heartbeat messages at
    private static final int interval = 1000;

    /**
     * Constructor for PerfectFailureDetector
     * @param p - The process this failure detector belongs to
     */
    public PerfectFailureDetector(Process p){
		this.p = p;
		t = new Timer();
		suspectedProcesses = new ArrayList<Integer>(Utils.MAX_NUM_OF_PROCESSES);
    }
    	
    /* Initiates communication tasks, e.g. sending heartbeats periodically */
    public void begin (){
    	t.schedule(new PeriodicTask(),0,interval);
    }
	
    /* Handles in-coming (heartbeat) messages */
    public void receive(Message m){
    	Utils.out(p.pid, m.toString());
    	suspectedProcesses.remove(m.getSource());
    }
	
    /* Returns true if ‘process’ is suspected */
    public boolean isSuspect(Integer process){
    	return false;
    }
	
    /* Returns the next leader of the system; used only for §2.1.2.
     * Or, it should also be used to notify a process if the leader
     * changed.
     */
    public int getLeader(){
    	return 0;
    }
	
    /* Notifies a blocking thread that ‘process’ has been suspected. 
     * Used only for tasks in §2.1.3 */
    public void isSuspected(Integer process){
	
    }

    class PeriodicTask extends TimerTask{
		public void run(){
		    p.broadcast("Heartbeat","null");
		}
    }
    
}