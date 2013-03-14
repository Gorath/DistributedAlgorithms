package FailureDetectors.StrongFailureDetector;
import FailureDetectors.PerfectFailureDetector.PerfectFailureDetector;
import FailureDetectors.*;
import FailureDetectors.Process;

import java.util.*;



public class StrongFailureDetector extends PerfectFailureDetector {
           
    SFDProcess p ;
    public StrongFailureDetector(SFDProcess p){
	super(p);
	this.p = p;
    }
    

    /* Notifies a blocking thread that ‘process’ has been suspected. 
     * Used only for tasks in §2.1.3 */
    @Override
    public void isSuspected(Integer process){
	//Utils.out(p.pid ,"is suspected was called" + isSuspect(process));
	p.suspectChanged();
    }
    
} 



