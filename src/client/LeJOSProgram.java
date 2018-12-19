package client;

import lejos.remote.ev3.RemoteEV3;
import ui.LeJOSMainFrame;

public class LeJOSProgram {

	public static void main(String[] args) {
		LeJOSMainFrame frame = new LeJOSMainFrame();
		frame.setTitle("LeJOS - Client");
		frame.setSize(650, 400);
		frame.setResizable(false);
		frame.setLocation(0,  0);
		frame.setVisible(true);
	}

}
