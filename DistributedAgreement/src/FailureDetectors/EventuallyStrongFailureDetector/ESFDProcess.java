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
	   //buffer_r_o[i] =  Integer.parseInt(parts[2]);
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
		String v = handleRecv();
		continue;
		//the d thing is done in handleRecv
	    }
	    
	    Cont cont = collect(c);
	    if ( cont.ret ){
		x = cont.v;
		if (cont.d){
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

    private synchronized String handleRecv(){
	int n = getNo();
	HashMap<String,Integer> messages =  new HashMap<String,Integer>();
	ArrayList<Integer> nodes  = new ArrayList<Integer>(n);
	    A: for ( int i = 0 ; i < n-F -1; i++ ){
	    while( vals.peek()!= null &&  i < getNo()-F -1){
		try{
		    Utils.out(pid,"waiting");
		    wait();
		}catch  (InterruptedException e){
		    Utils.out(pid,"interrupted");
		}
		
		if( i >= n-F-1) {
		    Utils.out(pid,"breaking out");
		    break A;
		} // enough messages have been 
		//read or enough nodes have gone down
		
		
		Integer index = vals.poll();
		
		Utils.out(pid , "asjdklsajadlk " +index);
		
		nodes.add(index +1 );

		Utils.out( pid, "got a message from "+ index);

		/*
		  check if processes have become faulty
		 */



		String msg = buffer_x[index];
		
		Utils.out (pid,msg);
		if ( messages.containsKey(msg)){
		    messages.put(msg, messages.get(msg)+1); // increment message counter
		}else{
		    messages.put(msg,1); // increment message counter
		}
		
	    }
	}
	// have read all the values... take the majority
	
	String majority  = null;
	int max = 0;
	for ( String s : messages.keySet()){
	    int val = messages.get(s);
	    if ( val > max){
		max = val;
		majority = s;
	    }
	}

	Utils.out(pid, "majority " +majority);

	boolean d  =  messages.size() ==1 ;
	//only one value of x yaay
	for ( int id : nodes){
	    unicast(new Message(pid,id,OUTCOME,""+ d + SEP +majority));
	}
	
	return majority;
    }
        



    public synchronized Cont collect(int id){
	
	while ( buffer_v[id-1] == null && !faliureDetector.isSuspect(id)){
	    try{
		Utils.out(pid, "waiting for " + id + " "+ faliureDetector.isSuspect(id));
		wait();
	    }catch (InterruptedException e){
		Utils.out(pid,"interrupted exception");
	    }
	}
	
	Utils.out(pid,"wait over"+ faliureDetector.isSuspect(id) );
	
	Cont cont = new Cont();
	cont.v = buffer_v[id-1];
	cont.d = buffer_d[id-1];
	cont.ret = buffer_v[id-1] != null;
	return cont;
	// message was recieved.. otherwise is suspected

	
     }


    private class Cont{
	String v ;
	boolean d;
	boolean ret;
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
