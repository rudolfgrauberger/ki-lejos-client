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
	
	private Socket clientSocket;
	private DataOutputStream outToServer;
	private BufferedReader inFromServer;
	
	public LeJOSClient(String host, int port) throws Exception {
		clientSocket = new Socket(host, port);
		outToServer = new DataOutputStream(clientSocket.getOutputStream());
		inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	}
	
	private ILeJOSResult sendCommand(String command, int param) throws IOException {
		
		return sendCommand(command, Integer.toString(param));
	}
	
	private ILeJOSResult sendCommand(String command, String param) throws IOException {
		outToServer.writeBytes(String.format("%s %s", command, param));
		String result = inFromServer.readLine();
		
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
		clientSocket.close();
	}
	
	public String writeRawData(String data) throws IOException {
		outToServer.writeBytes(data);
		String result = inFromServer.readLine();
		
		return result;
	}

}
