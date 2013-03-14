package FailureDetectors.PerfectFailureDetector;
import FailureDetectors.Message;
import FailureDetectors.Process;
import FailureDetectors.Utils;
import FailureDetectors.*;

public class PFDProcess extends Process{
    

    public IFailureDetector faliureDetector;
	
	/**
	 * PDFProcess constructor
	 * @param name - the name of the process
	 * @param pid  - the unique ID of the process
	 * @param n    - the total number of processes
	 */
    public PFDProcess( String name , int pid , int n){
    	super(name,pid,n);
    	faliureDetector = new PerfectFailureDetector(this);
    }

    
    public void begin() {
    	faliureDetector.begin();
    }

    @Override
    public synchronized void receive(Message m) {
        String type = m.getType();

        if (type.equals(Utils.HEARTBEAT)) {
            faliureDetector.receive(m);	  
	    //unicast(new Message(pid, m.getSource(), Utils.HEARTBEAT_REPLY, m.getPayload()));
	} //else if (type.equals(Utils.HEARTBEAT_REPLY)) {
	    
	//}
    }

    public static void main(String[] args) {
        PFDProcess p1 = new PFDProcess(args[0],Integer.parseInt(args[1]),Integer.parseInt(args[2]));
        p1.registeR();
        p1.begin();
    }


}
