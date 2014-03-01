package testing;
import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.String;

public class TestDriver implements Runnable{
	
	String file;
	String[] list = new String[100];
	int count = 0;
	
	public TestDriver(String filename){
		this.file = filename;
	}
	
	public void runTest(){
	
		BufferedReader in = null;
		
		try{
			in = new BufferedReader(new FileReader(file));
			
			String read = null;
			while ((read = in.readLine()) != null) {
		        list[count++] = read.trim();
		    }
			for(int i = 0; i < count; i++){
				System.out.println(list[i]);
			}
		} catch (Exception e){
			System.out.println("There was a problem: " + e);
			e.printStackTrace();
		} finally{
			try{
				in.close();
			} catch (Exception e){
				System.out.println("There was a problem: " + e);
				e.printStackTrace();
			}
		}
	}
	
	public void run(){
		
		runTest();
		
	}
	
}
