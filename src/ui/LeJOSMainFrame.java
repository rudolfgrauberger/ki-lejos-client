package ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.LeJOSClient;
import util.ILeJOSLogger;

public class LeJOSMainFrame extends JFrame implements ILeJOSLogger {
	
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
				
				executeScript();
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
		
		String host = tfHost.getText();
		int port = Integer.parseInt(tfPort.getText());
		try {
			myclient = new LeJOSClient(host, port, this);
			connected = true;

		} catch (Exception e) {
			error(e.getMessage());
		}
		
		info(String.format("Conntection established to %s on Port %d...", host, port));
		switchConnectedButton();
	}
	
	private void disconnectFromLeJOS() {
		try {
			myclient.close();
			connected = false;
		} catch (IOException e) {
			error(e.getMessage());
		}
		
		switchConnectedButton();
	}
	
	private void executeScript() {
		if (!connected)
			info("Please connect to LeOS first...");
		
		String[] commands = taCommands.getText().split("\r\n|\n");
		
		info(String.format("> Script execution started at %s ####",  new java.util.Date().toString()));
		
		for (String c : commands) {
			String result = "";
			if (c.trim().isEmpty())
				continue;
			
			try {
				result = myclient.writeRawData(c.trim());
			} catch (IOException e) {
				error(e.getMessage());
			} catch (Exception e) {
				error(e.getMessage());
			}
			
			info(String.format("%s -> %s", c, result));
		}
		
		info(String.format("> Script execution ended at %s ####", new java.util.Date().toString()));
	}

	@Override
	public void info(String message) {
		taLog.append(message + "\r\n");
	}
	
	@Override
	public void error(String message) {
		taLog.append(message + "\r\n");
	}
}
