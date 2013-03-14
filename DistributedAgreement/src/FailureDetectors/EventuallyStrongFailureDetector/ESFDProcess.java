package FailureDetectors.EventuallyStrongFailureDetector;
import FailureDetectors.Message;
import FailureDetectors.Process;
import FailureDetectors.Utils;
import FailureDetectors.EventuallyPerfectFailureDetector.EPFDProcess;
import java.util.Queue;
import java.util.LinkedList;
import java.util.*;
public class ESFDProcess extends EPFDProcess {


    private static final String VAL="es_val";
    private 
static final String OUTCOME ="es_outcome";
    private static final String SEP ="<@@>";


    public int F; // the number of failed processes

    private Queue<Integer> vals;
    private String[] buffer_x;
    private boolean[] buffer_d;
    private String[] buffer_v;
    private int[] buffer_r_v;
    private int[] buffer_r_o;

    public ESFDProcess( String name , int pid , int n ){
    	super(name,pid,n );
	faliureDetector = new EventuallyStrongFailureDetector(this);
	buffer_v= new String[n];
	buffer_x= new String[n];
	buffer_d= new boolean[n];
	buffer_r_v= new int[n];
	buffer_r_o= new int[n];
		vals = new LinkedList<Integer>();
    }
    
    
    @Override
    public synchronized void receive(Message m) {
	super.receive(m);
	if ( m.getType().equals(VAL)){
	    int i = m.getSource()-1;
	    String[] parts= m.getPayload().split(SEP);
	    buffer_x[i] = parts[0];
	    buffer_r_v[i]  =  Integer.parseInt(parts[1]);
	    vals.offer(i); //process i +1 deposited a VAL message
	    notifyAll();
	}
	if ( m.getType().equals(OUTCOME)){
	   int i = m.getSource()-1;
	   String[] parts= m.getPayload().split(SEP);
	   buffer_d[i] = Boolean.parseBoolean(parts[0]);
	   buffer_v[i] = parts[1];
	   buffer_r_o[i] =  Integer.parseInt(parts[2]);
	   notifyAll();
	}
    }

    
    public synchronized void suspectChanged(){
	Utils.out(pid, "notifying all");
	notifyAll();
    }



    public synchronized void agree(String x){
	int r = 0;
	int m = 0;
	int n = getNo();
	while( true) {
	    r++;
	    int c = ( r % n) + 1;
	    
	    unicast(new Message(pid,c,VAL,x+SEP+r));


	    if ( pid == c ) {
		handleRecv();
	    }

	    else if ( /*collect()*/ true ){
		x = buffer_v[c-1];
		if (buffer_d[c-1]){
		    Utils.out(pid, "decided " +x );
		    if ( m  == c ){
			return;
		    }else if ( m == 0) {
			m = c;
		    }
		}
		
	    }

		    
	}
    }

    private synchronized void handleRecv(){
	int n = getNo();
	HashMap<String,Integer> messages =  new HashMap<String,Integer>();
	ArrayList<Integer> nodes  = new ArraList<Integer>(n);
	for ( int i = 0 ; i < n-F -1; i++ ){
	    while( vals.isEmpty() &&  i < getNo()-F -1){
		try{
		    Utils.out(pid,"waiting");
		    wait();
		}catch  (InterruptedException e){
		    Utils.out(pid,"interrupted");
		}
L
		if( i >= n-F-1) {
		    Utils.out(pid,"breaking out");
		    break;
		} // enough messages have been 
		//read or enough nodes have gone down

		int index = vals.poll();
		
		Utils.out( pid, "got a message from "+ index);
		String msg = buffer_x[index];
		if ( messages.containsKey(msg)){
		    messages.put(msg, messages.get(msg)+1); // increment message counter
		}else{
		    messages.put(msg, messages.get); // increment message counter
		}
		
	    }
	}
    }
    

    



    public synchronized boolean collect(int id){
	/*
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

	*/
	return true;
    }

   
    public static void main(String[] args) {
        ESFDProcess p1 = new ESFDProcess(args[0],Integer.parseInt(args[1]),Integer.parseInt(args[2]));
        p1.registeR();
        p1.begin();

	//consensus
	p1.agree(args[2+p1.pid]);
	Utils.out(p1.pid, "initial arg " +args[2+p1.pid]);
    }


}
