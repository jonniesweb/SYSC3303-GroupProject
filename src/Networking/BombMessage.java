package Networking;

public class BombMessage extends Message {
	private int posX;
	private int posY;
	public BombMessage(String data,int x, int y){
		super(data);
		posX = x;
		posY = y;
	}


}
