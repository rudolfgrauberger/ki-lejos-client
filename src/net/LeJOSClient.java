package net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class LeJOSClient implements ILeJOSClientInterface {
	
	private final static String COMMAND_FORWARD = "FORWARD";
	private final static String COMMAND_BACKWARD = "BACKWARD";
	private final static String COMMAND_LEFT = "LEFT";
	private final static String COMMAND_RIGHT = "RIGHT";
	private final static String COMMAND_SENSOR = "SENSOR";
	private final static String COMMAND_LOOK = "LOOK";
	private final static String COMMAND_DISCONNECT = "DISCONNECT";
	
	private Socket clientSocket;
	private DataOutputStream outToServer;
	private BufferedReader inFromServer;
	
	public LeJOSClient(String host, int port) throws Exception {
		clientSocket = new Socket(host, port);
		outToServer = new DataOutputStream(clientSocket.getOutputStream());
		inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		inFromServer.readLine();
		/*Runnable r = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while ( String l = inFromServer.readLine())
				
			}
		};*/
	}
	
	private ILeJOSResult sendCommand(String command, int param) throws IOException {
		
		return sendCommand(command, Integer.toString(param));
	}
	
	private ILeJOSResult sendCommand(String command, String param) throws IOException {
		outToServer.writeBytes(String.format("%s %s\r\n", command, param));
		String line, result = "";
		
		while (inFromServer.ready() &&  (line = inFromServer.readLine()) != null) result += line;
		
		return LeJOSResultParser.getResult(result);
	}
	
	
	@Override
	public ILeJOSResult sendForward(int distance) throws IOException {
		
		return sendCommand(COMMAND_FORWARD, distance);
	}


	@Override
	public ILeJOSResult sendBackward(int distance) throws IOException {
		
		return sendCommand(COMMAND_BACKWARD, distance);
	}


	@Override
	public ILeJOSResult sendLeft(int angle) throws IOException {
		
		return sendCommand(COMMAND_LEFT, angle);
	}


	@Override
	public ILeJOSResult sendRight(int angle) throws IOException {
		return sendCommand(COMMAND_RIGHT, angle);
	}


	@Override
	public ILeJOSResult getSensor(String sensortype) throws IOException {
		return sendCommand(COMMAND_SENSOR, sensortype);
	}
	
	public void close() throws IOException {
		sendCommand(COMMAND_DISCONNECT, "");
	}
	
	public String writeRawData(String data) throws IOException {
		outToServer.writeBytes(data + "\r\n");
		
		String line, result = "";
		
		while (inFromServer.ready() && (line = inFromServer.readLine()) != null) result += line;
		
		return result;
	}

	@Override
	public ILeJOSResult sendLookRight() throws IOException {
		return sendCommand(COMMAND_LOOK, "RIGHT");
	}

	@Override
	public ILeJOSResult sendLookCenter() throws IOException {
		return sendCommand(COMMAND_LOOK, "CENTER");
	}

	@Override
	public ILeJOSResult sendLookLeft() throws IOException {
		return sendCommand(COMMAND_LOOK, "LEFT");
	}

}
