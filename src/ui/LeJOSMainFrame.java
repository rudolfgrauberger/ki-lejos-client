package ui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.LeJOSClient;

public class LeJOSMainFrame extends JFrame {
	
	public LeJOSMainFrame() {
		try {
			LeJOSClient myclient = new LeJOSClient("10.0.1.9", 6789);
			myclient.sendForward(50);
			myclient.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
