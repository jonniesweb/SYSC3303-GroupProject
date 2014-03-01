package testing;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Logger implements Runnable{
	//private String log;
	PrintWriter out;
	public Logger(){
		
		try {
			out = new PrintWriter("Log.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public void update(String log){
		out.println(log);
		//log+= "\n";
	}
	
	public void run(){}
	
}
