package ui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.LeJOSClient;

public class LeJOSMainFrame extends JFrame {
	
	public LeJOSMainFrame() {
		try {
			LeJOSClient myclient = new LeJOSClient("10.0.1.19", 6789);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
