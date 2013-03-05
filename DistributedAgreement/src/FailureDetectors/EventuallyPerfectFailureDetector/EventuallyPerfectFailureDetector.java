package FailureDetectors.EventuallyPerfectFailureDetector;
import FailureDetectors.*;
import FailureDetectors.Process;

import java.util.*;



public class EventuallyPerfectFailureDetector implements IFailureDetector {
    
    // The process this failure detector belongs to
    Process p;
    
    // A timer that updates suspected processes
    private Timer t;

    // List to store suspected processes
    private List<Integer> suspectedProcesses;
    private List<Integer> successfulReplies;
    

    //The time for the last heartbeat
    private long lastHeartbeat = 0;

    // The interval to send heartbeat messages at
    private static final int interval = 1000;

    /**
     * Constructor for FailureDetectors.PerfectFailureDetector
     * @param p - The process this failure detector belongs to
     */
    public PerfectFailureDetector(Process p){
	this.p = p;
	t = new Timer();
	suspectedProcesses = new ArrayList<Integer>(Utils.MAX_NUM_OF_PROCESSES);
	successfulReplies  = new ArrayList<Integer>(Utils.MAX_NUM_OF_PROCESSES);
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
	

	//if this process was suspected .. remove it from suspects since we recieved a message
	 if (suspectedProcesses.contains(processID)) {
	     suspectedProcesses.remove(suspectedProcesses.indexOf(processID));
	     Utils.out(p.pid,"Process " + processID + " has recovered.");
	 }

        // If this heartbeat is received in the correct time period
        if (lastHeartbeat + 2*Utils.DELAY > System.currentTimeMillis()){
         
            // Make note that this process is still active
            successfulReplies.add(processID);

            // If this process is suspected but has recovered remove it from the suspected list
	    if (suspectedProcesses.contains(processID)) {
		suspectedProcesses.remove(processID);
		Utils.out(p.pid,"Process " + processID + " has recovered.");
	    }
        }

    }
	
    /* Returns true if ‘process’ is suspected */
    public boolean isSuspect(Integer process){
    	return suspectedProcesses.contains(process);
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
            synchronized (suspectedProcesses) {
                if (lastHeartbeat != 0) {
                    for(int i = 0; i <= p.getNo(); i++) {
                        if (i != 0 && i != p.pid) {
                            if (!successfulReplies.contains(i)) {
                                Utils.out(p.pid,"Process " + i + " is now suspected.. bastard..");
                                suspectedProcesses.add(i);
			    }
                        }
                    }
                    successfulReplies.clear();
                }
            }

            lastHeartbeat = System.currentTimeMillis();
	    p.broadcast(Utils.HEARTBEAT,"null");
	    
	}
    }
    
}