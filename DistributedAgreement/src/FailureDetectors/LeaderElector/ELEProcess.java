package FailureDetectors.LeaderElector;
import FailureDetectors.Message;
import FailureDetectors.Process;
import FailureDetectors.Utils;
import FailureDetectors.EventuallyPerfectFailureDetector.EPFDProcess;

public class ELEProcess extends EPFDProcess {


    int leader = 0;
    /**
     * PDFProcess constructor
     * @param name - the name of the process
     * @param pid  - the unique ID of the process
     * @param n    - the total number of processes
     */
    public ELEProcess( String name , int pid , int n){
    	super(name,pid,n);
	faliureDetector = new EventuallyLeaderElector(this);
	leader = pid;
    }
    
    public void begin() {
    	faliureDetector.begin();
    }

    @Override
    public synchronized void receive(Message m) {
	super.receive(m);  
    }

    

    public void setLeader(int leader){
	this.leader = leader;
    }


    
    public static void main(String[] args) {
        ELEProcess p1 = new ELEProcess(args[0],Integer.parseInt(args[1]),Integer.parseInt(args[2]));
        p1.registeR();
        p1.begin();
    }


}
