package client.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import client.localization.Intersect;
import client.localization.Point;
import client.montecarlo.ActionException;
import client.montecarlo.IMoveController;
import client.montecarlo.SensorDataSet;
import client.util.ILeJOSLogger;
import javafx.scene.paint.Color;

public class LeJOSClient implements ILeJOSClientInterface, IMoveController {

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

	public LeJOSClient(ILeJOSLogger logger) throws Exception {
		this.logger = logger;
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
			distCenter = getDoubleFromResult(getSensor(sensorType))*100;
			//left
			moveResult = sendLookLeft();
			if(!moveResult.isSuccess())
				throw new ActionException("Robot move");
			distLeft = getDoubleFromResult(getSensor(sensorType))*100;
			//right
			moveResult = sendLookRight();
			if(!moveResult.isSuccess())
				throw new ActionException("Robot move");
			distRight = getDoubleFromResult(getSensor(sensorType))*100;
			//center
			moveResult = sendLookCenter();
			if(!moveResult.isSuccess())
				throw new ActionException("Robot move");
			return new SensorDataSet(distCenter,distLeft,distRight);
		}
		catch (IOException ex){
			throw new ActionException("Connection");
		}
	}

	@Override
	public Point getPoint() {
		return null;
	}

	@Override
	public double getCurrentRotation() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Color getColor() {
		return null;
	}

	@Override
	public double getBelief() {
		return 0;
	}

	@Override
	public void setBelief(double belief) {

	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public Intersect getForwardIntersect() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Intersect getLeftIntersect() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Intersect getRightIntersect() {
		throw new UnsupportedOperationException();
	}


	@Override
	public void moveForward(double cm) throws ActionException {
		try {
			ILeJOSResult result = sendForward((int)cm);
			if(!result.isSuccess())
				throw new ActionException("Robot move");
		}
		catch (IOException ex){
			throw new ActionException("Connection");
		}
	}

	@Override
	public void moveBackward(double cm) throws ActionException {
		try {
			ILeJOSResult result = sendBackward((int)cm);
			if(!result.isSuccess())
				throw new ActionException("Robot move");
		}
		catch (IOException ex){
			throw new ActionException("Connection");
		}
	}

	@Override
	public void turnLeft(double angle) throws ActionException {
		try {
			ILeJOSResult result = sendLeft((int)angle);
			if(!result.isSuccess())
				throw new ActionException("Robot move");
		}
		catch (IOException ex){
			throw new ActionException("Connection");
		}
	}

	@Override
	public void turnRight(double angle) throws ActionException {
		try {
			ILeJOSResult result = sendRight((int)angle);
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

	@Override
	public void connect(String host, int port) throws IOException {
		clientSocket = new Socket(host, port);
		outToServer = new DataOutputStream(clientSocket.getOutputStream());
		inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		this.logger = logger;

		String message = getMessageFromServer(true);
		this.logger.info(message);
	}

	@Override
	public void disconnect() throws IOException {
		ILeJOSResult result = sendCommand(COMMAND_DISCONNECT, "");
		clientSocket = null;
		outToServer = null;
		inFromServer = null;
	}

	@Override
	public boolean isConnected() {
		return clientSocket != null;
	}
}
