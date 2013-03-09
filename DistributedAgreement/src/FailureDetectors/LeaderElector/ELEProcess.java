package FailureDetectors.LeaderElector;
import FailureDetectors.Message;
import FailureDetectors.Process;
import FailureDetectors.Utils;
import FailureDetectors.EventuallyPerfectFailureDetector;

public class ELEProcess extends EPFDProcess {


    int highestProcessNo;
    /**
     * PDFProcess constructor
     * @param name - the name of the process
     * @param pid  - the unique ID of the process
     * @param n    - the total number of processes
     */
    public EPFDProcess( String name , int pid , int n){
    	super(name,pid,n);
	faliureDetector = new EventuallyPerfectFailureDetector(this);
	highestProcessNo = pid;
    }
    
    public void begin() {
    	faliureDetector.begin();
    }

    @Override
    public synchronized void receive(Message m) {
	String type = m.getType();
	if (type == Utils.LEADER_MESSAGE) {
	    int processID = Integer.valueOf(m.getPayload());	  
	    if (processID > highestProcessNo) {
		highestProcessNo = processID
	    }

	    
	    return;
	}
	
	// If we dont know how to handle the message ask the base class
	super.receive(m);  
    }

    public static void main(String[] args) {
        ELEProcess p1 = new ELEProcess(args[0],Integer.parseInt(args[1]),Integer.parseInt(args[2]));
        p1.registeR();
        p1.begin();
    }


}
