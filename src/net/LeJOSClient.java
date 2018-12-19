package net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class LeJOSClient implements ILeJOSClientInterface {
	
	private Socket clientSocket;
	private DataOutputStream outToServer;
	private BufferedReader inFromServer;
	
	public LeJOSClient(String host, int port) throws Exception {
		clientSocket = new Socket(host, port);
		outToServer = new DataOutputStream(clientSocket.getOutputStream());
		inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	}
	
	
	@Override
	public ILeJOSResult sendForward(int distance) {
		
		return new LeJOSFailureResult();
	}


	@Override
	public ILeJOSResult sendBackward(int distance) {
		return new LeJOSFailureResult();
	}


	@Override
	public ILeJOSResult sendLeft(int angle) {
		return new LeJOSFailureResult();
	}


	@Override
	public ILeJOSResult sendRight(int angle) {
		return new LeJOSFailureResult();
	}


	@Override
	public ILeJOSResult getSensor(String sensortype) {
		return new LeJOSFailureResult();
	}

}
