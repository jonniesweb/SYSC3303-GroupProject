package testing;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Logger {
	
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
	
}
