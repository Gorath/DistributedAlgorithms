package FailureDetectors.PerfectFailureDetectorWithReply;
import FailureDetectors.*;
import FailureDetectors.Process;

import java.util.*;



public class PerfectFailureDetector implements IFailureDetector {
    


    // The process this failure detector belongs to
    public Process p;
    
    // A timer that updates suspected processes
    private Timer t;

    // List to store suspected processes
    private boolean[] suspectedProcesses;
    private boolean[] successfulReplies;

    //The time for the last heartbeat
    private long lastHeartbeat = 0;
    private long req_number = 0;

    // The interval to send heartbeat messages at
    private static final int interval = 1000;

    /**
     * Constructor for FailureDetectors.PerfectFailureDetector
     * @param p - The process this failure detector belongs to
     */
    public PerfectFailureDetector(Process p){
	this.p = p;
	t = new Timer();
	int n = p.getNo();
	suspectedProcesses = new boolean[n];
	successfulReplies  = new boolean[n];
    }
    	
    /* Initiates communication tasks, e.g. sending heartbeats periodically */
    public void begin (){
    	t.schedule(new PeriodicTask(),0,interval);
    }


    /* Handles in-coming (heartbeat) messages */
    @Override
    public void receive(Message m){
    	//Utils.out(p.pid, m.toString());
	
	
	// Get the process ID from the message
	int processID = m.getSource();

	// Extract the information out of the payload
	long heartbeatTime = Long.parseLong(m.getPayload());

	//Utils.out(p.pid, "Received heartbeat reply from at time " + (System.currentTimeMillis() - heartbeatTime));

        // If this heartbeat is received in the correct time period
        if (heartbeatTime + 1.1*Utils.DELAY + interval > System.currentTimeMillis()){
            // Make note that this process is still active
            successfulReplies[processID-1] = true;
	}

    }
	
    /* Returns true if ‘process’ is suspected */
    public boolean isSuspect(Integer process){
    	return suspectedProcesses[process-1];
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
	Utils.out(p.pid,"fuck me");
    }

    class PeriodicTask extends TimerTask{
	public void run(){
            
            // If we have a list of replies from a previous repetition
            // calculate suspected processes
	    if (lastHeartbeat != 0) {

		for(int i = 0; i <= p.getNo(); i++) {
		    if (i != 0 && i != p.pid) {
			if (!successfulReplies[i-1] && !suspectedProcesses[i-1]) {
			    Utils.out(p.pid,"Process " + i + " is now suspected");
			    suspectedProcesses[i-1] = true;
			    isSuspected(i);
			}
			 //clear the slot for next time
			successfulReplies[i-1] = false;

		    }
		}

	    }
            

            lastHeartbeat = System.currentTimeMillis();
	    //Utils.out(p.pid,""+lastHeartbeat);
	    p.broadcast(Utils.HEARTBEAT,"" + lastHeartbeat);
	    
	}
    }
    
}
