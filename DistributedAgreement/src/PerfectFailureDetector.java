

public class PerfectFailureDetector implements IFailureDetector{
    
    Process p;
    
    Timer t;

    public PerfectFailureDetector(Process p){
	this.p = p;
	t = new Timer();
	
    }
    	
    /* Initiates communication tasks, e.g. sending heartbeats periodically */
    public void begin (){
	
    }
	
    /* Handles in-coming (heartbeat) messages */
    public void receive(Message m){
	
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

    private class PreodicTask extends TimerTask{
	public void run(){
	    
	}
    }
}