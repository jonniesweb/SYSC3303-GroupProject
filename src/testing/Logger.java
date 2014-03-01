package testing;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Logger {
	private String log;
	PrintWriter out;
	public Logger(){
		
		try {
			out = new PrintWriter("Log.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void update(String log){
		this.log+=log;
		log+= "\n";
	}
	public void toFile(){
		out.println(log);
		this.log = "";
	}
}
