package utility;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class PlayerThread extends Thread {
	
	private Player player;
	
	public PlayerThread(Player player) {
		this.player = player;
	}
	
	public void run() {
		try {
			player.play();
		} catch (JavaLayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			player.close();
			player = null;
		}
	}
}
