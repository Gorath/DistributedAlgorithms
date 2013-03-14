package FailureDetectors.EventuallyStrongFailureDetector;
import FailureDetectors.EventuallyPerfectFailureDetector.EventuallyPerfectFailureDetector;
import FailureDetectors.*;
import FailureDetectors.Process;

import java.util.*;



public class EventuallyStrongFailureDetector extends EventuallyPerfectFailureDetector {
           
    ESFDProcess p ;
    public EventuallyStrongFailureDetector(ESFDProcess p){
	super(p);
	this.p = p;
    }
    
    
    /*
    /* Notifies a blocking thread that ‘process’ has been suspected. 
     * Used only for tasks in §2.1.3 *
    @Override
    public void isSuspected(Integer process){
	//Utils.out(p.pid ,"is suspected was called" + isSuspect(process));
	p.suspectChanged();
    }
    */
    @Override
    public int getLeader(){
	p.suspectChanged();
	return super.getLeader(); //this method is called whenever suspect lis is changed
	//use this to notify the process of changing supects
    }
    
} 



