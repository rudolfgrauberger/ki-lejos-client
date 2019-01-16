package client.net;

import java.io.IOException;

public interface ILeJOSClientInterface {
	ILeJOSResult sendForward(int distance) throws IOException;
	ILeJOSResult sendBackward(int distance) throws IOException;
	ILeJOSResult sendLeft(int angle) throws IOException;
	ILeJOSResult sendRight(int angle) throws IOException;
	ILeJOSResult getSensor(String sensortype) throws IOException;
	ILeJOSResult sendLookRight() throws IOException;
	ILeJOSResult sendLookCenter() throws IOException;
	ILeJOSResult sendLookLeft() throws IOException;
	String writeRawData(String data) throws IOException;	
}
