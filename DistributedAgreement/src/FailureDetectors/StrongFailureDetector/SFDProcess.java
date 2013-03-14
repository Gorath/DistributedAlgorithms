package FailureDetectors.StrongFailureDetector;
import FailureDetectors.Message;
import FailureDetectors.Process;
import FailureDetectors.Utils;
import FailureDetectors.PerfectFailureDetector.PFDProcess;

public class SFDProcess extends PFDProcess {
    String[] buffer;

    
    public SFDProcess( String name , int pid , int n ){
    	super(name,pid,n );
	faliureDetector = new StrongFailureDetector(this);
	buffer= new String[n];
    }
    
    
    @Override
    public synchronized void receive(Message m) {
	super.receive(m);
	if ( m.getType().equals(Utils.CONSENSUS)){
	    buffer[m.getSource() -1] = m.getPayload();
	    notifyAll();
	}
    }

    
    public synchronized void suspectChanged(){
	Utils.out(pid, "notifying all");
	notifyAll();
    }


    public synchronized void agree(String x){
	Utils.out(pid,"x " + x);
	for ( int i = 1 ; i <= getNo(); i++){
	    if ( i == pid){
	        if ( i == 4){
		    while (true);
		}
		broadcast(Utils.CONSENSUS,x);
	    }
	    else if (collect(i)){
		x = buffer[i-1];
	    }
	    
	}

	Utils.out(pid,"decided " + x);
	
	
    }


    public synchronized boolean collect(int id){
	while ( buffer[id-1] == null && !faliureDetector.isSuspect(id)){
	    try{
		Utils.out(pid, "waiting for " + id + " "+ faliureDetector.isSuspect(id));
		wait();
	    }catch (InterruptedException e){
		Utils.out(pid,"interrupted exception");
	    }
	}
	
	Utils.out(pid,"wait over"+ faliureDetector.isSuspect(id) );
	return buffer[id-1] != null; // message was recieved.. otherwise is suspected
	
    }

   
    public static void main(String[] args) {
        SFDProcess p1 = new SFDProcess(args[0],Integer.parseInt(args[1]),Integer.parseInt(args[2]));
        p1.registeR();
        p1.begin();

	//consensus
	p1.agree(args[2+p1.pid]);
	Utils.out(p1.pid, "initial arg " +args[2+p1.pid]);
    }


}
