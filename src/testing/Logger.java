package testing;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

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
	public Logger(){
		
		try {
			out = new PrintWriter("Log.txt");
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	//Every command player made
	public void commandLog(String log){
		out.println(log);
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
	
}
