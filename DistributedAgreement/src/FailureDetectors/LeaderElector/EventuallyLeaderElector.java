package FailureDetectors.LeaderElector;
import FailureDetectors.EventuallyPerfectFailureDetector.EventuallyPerfectFailureDetector;
import FailureDetectors.*;
import FailureDetectors.Process;

import java.util.*;



public class EventuallyLeaderElector extends EventuallyPerfectFailureDetector {
    
    
    /**
     * Constructor for FailureDetectors.PerfectFailureDetector
     * @param p - The process this failure detector belongs to
     */
    private ELEProcess p;
    public EventuallyLeaderElector(ELEProcess p){
	super(p);
	this.p = p;
    }
    
   
    
    /* Returns the next leader of the system; used only for §2.1.2.
     * Or, it should also be used to notify a process if the leader
     * changed.
     */
    public int getLeader(){
	int leader = p.pid;
	// StringBuilder sb = new StringBuilder();
	// for ( int i = 1 ; i <= p.getNo(); i ++){
	//     sb.append(i);
	//     sb.append(":");
	//     sb.append(isSuspect(i));
	//     sb.append(",");
	// }
	// Utils.out(p.pid, sb.toString());
	for ( int i = p.getNo() ; i > 0 ;  i--){
	    if (!isSuspect(i)){
		leader = i;
		break;
	    }
	}
	Utils.out(p.pid,"leader chosen "+leader);
	p.setLeader(leader);
	return leader;
    }
    
    /* Notifies a blocking thread that ‘process’ has been suspected. 
     * Used only for tasks in §2.1.3 */
    public void isSuspected(Integer process){
	
    }

    
} 
