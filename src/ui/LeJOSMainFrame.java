package ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.LeJOSClient;

public class LeJOSMainFrame extends JFrame {
	
	private JTextField tfHost, tfPort;
	private JButton bConnect;
	private JTextArea taLog;
	
	public LeJOSMainFrame() {
		BorderLayout frameLayout = new BorderLayout();
		this.setLayout(frameLayout);

		GridLayout experimentLayout = new GridLayout(0,5);
		JPanel connection = new JPanel();
		connection.setLayout(experimentLayout);

		
		connection.add(new JLabel("IP-Adresse:"));
		
		tfHost = new JTextField("10.0.1.9");
		connection.add(tfHost);
		
		connection.add(new JLabel("Port:"));
		
		tfPort = new JTextField("6789");
		connection.add(tfPort);
		
		bConnect = new JButton("Connect");
		connection.add(bConnect);
		
		this.add(connection, BorderLayout.PAGE_START);
		
		taLog = new JTextArea();
		this.add(taLog, BorderLayout.CENTER);
		
		bConnect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				connectToLeJOS();
				
			}
		});
	}
	
	private void connectToLeJOS() {
		try {
			LeJOSClient myclient = new LeJOSClient(tfHost.getText(), Integer.parseInt(tfPort.getText()));
			myclient.sendForward(10);
			myclient.close();
		} catch (Exception e) {
			taLog.append(e.getMessage());
		}
	}
}
