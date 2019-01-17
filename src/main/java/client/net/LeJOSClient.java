package client.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import client.montecarlo.ActionException;
import client.montecarlo.SensorDataSet;
import client.util.ILeJOSLogger;
import client.montecarlo.IRobotController;

public class LeJOSClient implements ILeJOSClientInterface, IRobotController {
	
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
	private ILeJOSLogger logger;
	
	public LeJOSClient(String host, int port, ILeJOSLogger logger) throws Exception {
		clientSocket = new Socket(host, port);
		outToServer = new DataOutputStream(clientSocket.getOutputStream());
		inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		this.logger = logger;
		
		String message = getMessageFromServer(true);
		this.logger.info(message);
	}
	
	private ILeJOSResult sendCommand(String command, int param) throws IOException {
		
		return sendCommand(command, Integer.toString(param));
	}
	
	private ILeJOSResult sendCommand(String command, String param) throws IOException {
		outToServer.writeBytes(String.format("%s %s\r\n", command, param));
		
		String result = getMessageFromServer(true);
		
		return LeJOSResultParser.getResult(result);
	}

	public ILeJOSResult sendForward(int distance) throws IOException {
		
		return sendCommand(COMMAND_FORWARD, distance);
	}

	public ILeJOSResult sendBackward(int distance) throws IOException {
		
		return sendCommand(COMMAND_BACKWARD, distance);
	}

	public ILeJOSResult sendLeft(int angle) throws IOException {
		
		return sendCommand(COMMAND_LEFT, angle);
	}

	public ILeJOSResult sendRight(int angle) throws IOException {
		return sendCommand(COMMAND_RIGHT, angle);
	}

	public ILeJOSResult getSensor(String sensortype) throws IOException {
		return sendCommand(COMMAND_SENSOR, sensortype);
	}
	
	public void close() throws IOException {
		sendCommand(COMMAND_DISCONNECT, "");
	}

	public String writeRawData(String data) throws IOException {
		outToServer.writeBytes(data + "\r\n");
		
		return getMessageFromServer(true);
	}

	public ILeJOSResult sendLookRight() throws IOException {
		return sendCommand(COMMAND_LOOK, "RIGHT");
	}

	public ILeJOSResult sendLookCenter() throws IOException {
		return sendCommand(COMMAND_LOOK, "CENTER");
	}


	public ILeJOSResult sendLookLeft() throws IOException {
		return sendCommand(COMMAND_LOOK, "LEFT");
	}
	
	private String getMessageFromServer(Boolean waitForAnwser) throws IOException {
		String line, result = "";
		Boolean firstMessage = !waitForAnwser;
		
		while ((!firstMessage || inFromServer.ready()) && (line = inFromServer.readLine()) != null) {
			if (line.trim().isEmpty())
				continue;
			
			result += line.trim();
			firstMessage = true;
		}
		
		return result;
	}

	@Override
	public SensorDataSet getSensorDataSet() throws ActionException {
		String sensorType = "DISTANCE";
		Double distLeft;
		Double distRight;
		Double distCenter;
		ILeJOSResult moveResult;
		try {
			//center
			moveResult = sendLookCenter();
			if(!moveResult.isSuccess())
				throw new ActionException("Robot move");
			distCenter = getDoubleFromResult(getSensor(sensorType));
			//left
			moveResult = sendLookLeft();
			if(!moveResult.isSuccess())
				throw new ActionException("Robot move");
			distLeft = getDoubleFromResult(getSensor(sensorType));
			//right
			moveResult = sendLookRight();
			if(!moveResult.isSuccess())
				throw new ActionException("Robot move");
			distRight = getDoubleFromResult(getSensor(sensorType));
			return new SensorDataSet(distCenter,distLeft,distRight);
		}
		catch (IOException ex){
			throw new ActionException("Connection");
		}
	}

	@Override
	public void moveForward(int cm) throws ActionException {

		try {
			ILeJOSResult result = sendForward(cm);
			if(!result.isSuccess())
				throw new ActionException("Robot move");
		}
		catch (IOException ex){
			throw new ActionException("Connection");
		}
	}

	@Override
	public void moveBackward(int cm) throws ActionException {
		try {
			ILeJOSResult result = sendBackward(cm);
			if(!result.isSuccess())
				throw new ActionException("Robot move");
		}
		catch (IOException ex){
			throw new ActionException("Connection");
		}
	}

	@Override
	public void turnLeft(int angle) throws ActionException {
		try {
			ILeJOSResult result = sendLeft(angle);
			if(!result.isSuccess())
				throw new ActionException("Robot move");
		}
		catch (IOException ex){
			throw new ActionException("Connection");
		}
	}

	@Override
	public void turnRight(int angle) throws ActionException {
		try {
			ILeJOSResult result = sendRight(angle);
			if(!result.isSuccess())
				throw new ActionException("Robot move");
		}
		catch (IOException ex){
			throw new ActionException("Connection");
		}
	}

	private double getDoubleFromResult(ILeJOSResult result) throws ActionException {
		if(!result.isSuccess())
			throw new ActionException("Robot sensor");
		else
			return ((LeJOSDoubleResult) result).getValue();
	}
}
