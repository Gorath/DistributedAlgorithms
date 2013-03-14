package FailureDetectors.EventuallyPerfectFailureDetector;
import FailureDetectors.Message;
import FailureDetectors.Process;
import FailureDetectors.Utils;
import FailureDetectors.IFailureDetector;

public class EPFDProcess extends Process{

    public IFailureDetector faliureDetector;
    /**
     * PDFProcess constructor
     * @param name - the name of the process
     * @param pid  - the unique ID of the process
     * @param n    - the total number of processes
     */
    public EPFDProcess( String name , int pid , int n){
    	super(name,pid,n);
	faliureDetector = new EventuallyPerfectFailureDetector(this);
    }
    
    public void begin() {
    	faliureDetector.begin();
    }

    @Override
	public synchronized void receive(Message m) {
	    String type = m.getType();
	    if (type.equals(Utils.HEARTBEAT)) {
		faliureDetector.receive(m);
	    }
	}

    public static void main(String[] args) {
        EPFDProcess p1 = new EPFDProcess(args[0],Integer.parseInt(args[1]),Integer.parseInt(args[2]));
        p1.registeR();
        p1.begin();
    }


}
