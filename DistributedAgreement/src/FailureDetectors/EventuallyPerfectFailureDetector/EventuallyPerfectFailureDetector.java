package FailureDetectors.EventuallyPerfectFailureDetector;
import FailureDetectors.*;
import FailureDetectors.Process;

import java.util.*;



public class EventuallyPerfectFailureDetector implements IFailureDetector {
    


    // The process this failure detector belongs to
    public Process p;
    
    // A timer that updates suspected processes
    private Timer t;

    // List to store suspected processes
    private boolean[] suspectedProcesses;
    /*
      using an array of ints since can recieve multiple messages in one time window.
      we will count messages recieved and carry them forwards
     */
    private int[] successfulReplies;
    private long[] maxDelays;

    private boolean started;

    // The interval to send heartbeat messages at
    private static final int interval = 1000;

    /**
     * Constructor for FailureDetectors.PerfectFailureDetector
     * @param p - The process this failure detector belongs to
     */
    @SuppressWarnings("unchecked")
    public EventuallyPerfectFailureDetector(Process p){
	this.p = p;
	t = new Timer();
	int n = p.getNo();
	suspectedProcesses = new boolean[n];
	successfulReplies  = new int[n];
	maxDelays  = new long[n];
	
	started = false;

	//initially all processes have delay as utils.Delay
	for(int i = 0; i < n ; i ++){
	    maxDelays[i] = Utils.DELAY;
	}
    }
    	
    /* Initiates communication tasks, e.g. sending heartbeats periodically */
    public void begin (){
    	t.schedule(new PeriodicTask(),0,interval);
    }


    /* Handles in-coming (heartbeat) messages */
    @Override
	public void receive(Message m){
	
	// Get the process ID from the message
	int processID = m.getSource();
	
	long time =  System.currentTimeMillis();
	
	
	long heartbeatTime =  Long.parseLong(m.getPayload());

	
	// Calculate the delay for the message
	long delay = (time- heartbeatTime)*2;
	
        // If this heartbeat is received in the correct time period
	if ( heartbeatTime + maxDelays[processID-1] >= time){
         
            // Make note that this process is still active
	    

	    successfulReplies[processID-1] = successfulReplies[processID-1]+1;
	
	    //if this process was previously suspected .. unsuspect it
	    if (suspectedProcesses[processID-1] ){
		Utils.out(p.pid, "recovered process " + processID);
		suspectedProcesses[processID-1] = false;
		getLeader();
	    } 

	}

	
	Utils.out(p.pid,"Reply process " + processID + " delay " + delay/2 + " max " + maxDelays[processID -1]   );
	maxDelays[processID -1] = Math.max(maxDelays[processID-1],delay);



    }


	
    /* Returns true if ‘process’ is suspected */
    public boolean isSuspect(Integer process){
    	return 	     suspectedProcesses[process-1]; 
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
            

            // If we have a list of replies from a previous repetition
            // calculate suspected processes
	    if (started) {
		Utils.out(p.pid,"timer tick");
		for(int i = 0; i <= p.getNo(); i++) {
		    if (i != 0 && i != p.pid) {
			if (successfulReplies[i-1] <= 0 && !suspectedProcesses[i-1] ) {
			    Utils.out(p.pid,"Process " + i + " is now suspected " );
			    suspectedProcesses[i-1] = true;
			    getLeader();
			}
			//clear the slot for next time
			
			if (successfulReplies[i-1] > 0 ){
			    successfulReplies[i-1] --;
			}
			
		    }
		}
		Utils.out(p.pid,"timer tick ended");
	    }
            
	    started = true;

	    p.broadcast(Utils.HEARTBEAT,""+ System.currentTimeMillis());

	}
    }
    
}
