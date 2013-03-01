package PerfectFailureDetector;



public class PFDProcess extends Process{

	PerfectFaliureDetector faliureDetector;
	
	/**
	 * PDFProcess constructor
	 * @param name - the name of the process
	 * @param pid  - the unique ID of the process
	 * @param n    - the total number of processes
	 */
    public PFDProcess( String name , int pid , int n){
    	super(name,pid,n);
    	faliureDetector = new PerfectFaliureDetector(); 
    }
    
    public void begin() {
    	
    }
    
    
}