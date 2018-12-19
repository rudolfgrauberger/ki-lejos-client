package ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
	private JButton bConnect, bSend;
	private JTextArea taLog, taCommands;
	
	LeJOSClient myclient;
	
	private Boolean connected = false;
	
	public LeJOSMainFrame() {
		BorderLayout frameLayout = new BorderLayout();
		frameLayout.setVgap(7);
		this.setLayout(frameLayout);

		GridLayout experimentLayout = new GridLayout(0,5);
		experimentLayout.setHgap(7);
		experimentLayout.setVgap(7);
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
		
		JPanel centerPanel = new JPanel();
		GridLayout gl = new GridLayout(1, 2);
		gl.setHgap(8);
		centerPanel.setLayout(gl);
		
		JPanel commandPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 280;
		c.weightx = 0.0;
		c.gridwidth = 6;
		c.gridx = 0;
		c.gridy = 0;
		
		taCommands = new JTextArea();
		JScrollPane sp = new JScrollPane(taCommands);
		commandPanel.add(sp, c);
		
		GridBagConstraints d = new GridBagConstraints();
		d.fill = GridBagConstraints.HORIZONTAL;
		d.weightx = 0.5;
		d.gridx = 0;
		d.gridy = 1;
		
		bSend = new JButton("Send");
		commandPanel.add(bSend, d);
		
		centerPanel.add(commandPanel);
		taLog = new JTextArea();
		centerPanel.add(taLog);
		

		this.add(centerPanel, BorderLayout.CENTER);
		
		bConnect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (connected)
					disconnectFromLeJOS();
				else
					connectToLeJOS();
			}
		});
		
		bSend.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (connected)
					executeScript();
				else
					taLog.append("Please connect to LeOS first...\n");
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
	
	private void executeScript() {
		String[] commands = taCommands.getText().split("\r\n");
		
		for (String c : commands) {
			String result = "";
			if (c.isEmpty())
				continue;
			
			try {
				result = myclient.writeRawData(c);
			} catch (IOException e) {
				taLog.append(e + "\n");
			} catch (Exception e) {
				taLog.append(e + "\n");
			}
			
			taLog.append(result + "\n");
		}
	}
}
