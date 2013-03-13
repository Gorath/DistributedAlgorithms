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
    private boolean[] successfulReplies;
    private long[] maxDelays;

    private boolean started;
    private TreeSet<Integer>[] missingMessageIDs;
    private int[] highestMessageIDRecieved;
    private int messageID = 1;

    // If this amount of messages are missed by a process we
    // permanently suspect it
    private static final int TOLERANCE = 50;

    // The interval to send heartbeat messages at
    private static final int interval = 1000;

    /**
     * Constructor for FailureDetectors.PerfectFailureDetector
     * @param p - The process this failure detector belongs to
     */
    public EventuallyPerfectFailureDetector(Process p){
	this.p = p;
	t = new Timer();
	int n = p.getNo();
	suspectedProcesses = new boolean[n];
	successfulReplies  = new boolean[n];
	maxDelays  = new long[n];
	missingMessageIDs = new TreeSet[n];
	highestMessageIDRecieved = new int[n];
	started = false;

	//initially all processes have delay as utils.Delay
	for(int i = 0; i < n ; i ++){
	    maxDelays[i] = Utils.DELAY;
	    missingMessageIDs[i] = new TreeSet<Integer>();
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
	
	String[] messageParts =  m.getPayload().split(" ");

	// Get payload information for  heartbeat ID and when heartbeat was sent
	int heartbeatID = Integer.parseInt(messageParts[0]);
	long heartbeatTime =  Long.parseLong(messageParts[1]);

	// Calculate the delay for the message
	long delay = System.currentTimeMillis() - heartbeatTime;

        // If this heartbeat is received in the correct time period
	if ( heartbeatTime + maxDelays[processID-1] >= System.currentTimeMillis()){
         
            // Make note that this process is still active
            successfulReplies[processID-1] = true;

	    //if this process was previously suspected .. unsuspect it
	    if (suspectedProcesses[processID-1] ){
		suspectedProcesses[processID-1] = false;
		getLeader();
	    } 

	}

	TreeSet<Integer> processTree = missingMessageIDs[processID-1];

	// If there is a huge delay on the transmission this is still deemed to 
	// be correct behaviour if all the packets have the same delay (ie they
	// still arrive in order).  This implies that even if a process is 
	// 50 times slower than the rest we will still consider it correct
	maxDelays[processID -1] = Math.max(maxDelays[processID-1],delay);
	//Utils.out(p.pid,"Reply process " + processID + " delay " + delay + " max " + maxDelays[processID -1]);

	// If processTree is null then this is permanently suspected 
	if (processTree == null) return;  

	if (highestMessageIDRecieved[processID-1] + 1 == heartbeatID ) {
	// Case where we receive the next message we expect to receive
	    highestMessageIDRecieved[processID-1]++;

	} else if (highestMessageIDRecieved[processID-1] + 1 > heartbeatID) {
	    // This is the case where we have previously missed this ID and it has	 
	    // come in late
	    if (processTree.contains(heartbeatID)) {
		    processTree.remove(heartbeatID);
	    } else {
		Utils.out(p.pid, "Error removing heartbeat from tree");
	    }
	    
	} else {
	    // this is the case where we receive a message ID greater than the one we expect
	    for (int i = highestMessageIDRecieved[processID-1] + 1; i < heartbeatID; i++) {
		processTree.add(i);
	    } 

	    if (processTree.size() > TOLERANCE) {
		missingMessageIDs[processID-1] = null;
		getLeader();
		Utils.out(p.pid, "Process now permanently suspected" + processID);
	    }

	    highestMessageIDRecieved[processID-1] = heartbeatID;
	}


    }
	
    /* Returns true if ‘process’ is suspected */
    public boolean isSuspect(Integer process){
    	return missingMessageIDs[process-1] == null  // case where permanently suspected because of out of order replies
	    || suspectedProcesses[process-1] // case where longer delay causes suspision - can be recovered still 
	    || missingMessageIDs[process-1].size() > 0; // case where we are missing heartbeat replies still so can't be assumed correct
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
            

	    boolean suspected = false ;
            // If we have a list of replies from a previous repetition
            // calculate suspected processes
	    if (started) {

		for(int i = 0; i <= p.getNo(); i++) {
		    if (i != 0 && i != p.pid) {
			if (!successfulReplies[i-1] && !suspectedProcesses[i-1] ) {
			    //Utils.out(p.pid,"Process " + i + " is now suspected");
			    suspected = suspectedProcesses[i-1] = true;
			}
			//clear the slot for next time
			successfulReplies[i-1] = false;

			if (missingMessageIDs[i-1] != null 
			    && missingMessageIDs[i-1].size() > TOLERANCE) {
			    missingMessageIDs[i-1] = null;
			    suspected = true;
			}

		    }
		}
	       
		//if some process has been suspected .. call getLeader so that 
		//a new leader can be chosen
		if (suspected){
		    getLeader();
		}
		    
	    }
            
	    started = true;

	    //Utils.out(p.pid,""+lastHeartbeat);
	    p.broadcast(Utils.HEARTBEAT,messageID + " " +
			System.currentTimeMillis());

	    messageID++;
	}
    }
    
}
