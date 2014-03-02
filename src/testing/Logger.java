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
public class Logger implements Runnable {
	
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
	//Every command player made
	public void commandLog(String command){
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
			}
			catch (IndexOutOfBoundsException e) {
				break;
			}
			log.remove(0);
		}
		
	}
	
	//Board update
	public void boardLog(char[][] board){
		for(int row=0;row < board.length;row++){
			for(int col=0;col < board[0].length;col++){
				out.print(board[row][col]);
			}
			out.println();
		}
	}
	

	
	public void run(){}
	
	public static void main(String[] args) {
		
	}
	
}
