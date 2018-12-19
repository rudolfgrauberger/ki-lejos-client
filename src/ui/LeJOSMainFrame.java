package ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

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
	
	LeJOSClient myclient;
	
	private Boolean connected = false;
	
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
				
				if (connected)
					disconnectFromLeJOS();
				else
					connectToLeJOS();
			}
		});
	}
	
	private void switchConnectedButton() {
		if (connected)
			bConnect.setText("Disconnect");
		else
			bConnect.setText("Connect");
	}
	
	private void connectToLeJOS() {
		try {
			myclient = new LeJOSClient(tfHost.getText(), Integer.parseInt(tfPort.getText()));
			connected = true;

		} catch (Exception e) {
			taLog.append(e.getMessage() + "\n");
		}
		
		switchConnectedButton();
	}
	
	private void disconnectFromLeJOS() {
		try {
			myclient.close();
			connected = false;
		} catch (IOException e) {
			taLog.append(e.getMessage() + "\n");
		}
		
		switchConnectedButton();
	}
}
