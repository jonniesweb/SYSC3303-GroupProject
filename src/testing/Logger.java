package testing;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

// TODO: add a queue for incoming items
/*
 * eg:
 * create list (FIFO)
 * Loop
 * 		accept message
 * 		add to log list
 * 
 * In another thread:
 * 		write each message from log list to file
 */
public class Logger{
	
	PrintWriter out;
	ArrayList<String> log;
	
	public Logger(){
		log = new ArrayList<String>();
		
		try {
			out = new PrintWriter("Log.txt");
			
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Accept message to be queued into log
	 * 
	 * @param command 
	 */
	public void acceptMessage(String command){
		log.add(command);
		
	}
	
	/**
	 * Write each message in queue log into file
	 * Remove message written
	 * Break out of  loop if there is no more queue
	 */
	public void writeLog(){
		while(true){
			try{
				out.println(log.get(0));
				System.out.println(log.get(0));
			}
			catch (IndexOutOfBoundsException e) {
				break;
			}
			log.remove(0);
		}	
	}
	
	/**
	 * If server ended and no log needed anymore
	 * file will be closed
	 */
	public void endLog(){
		out.close();
	}
		
	
}
