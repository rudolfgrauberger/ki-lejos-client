package net;

public interface ILeJOSClientInterface {
	ILeJOSResult sendForward(int distance);
	ILeJOSResult sendBackward(int distance);
	ILeJOSResult sendLeft(int angle);
	ILeJOSResult sendRight(int angle);
	ILeJOSResult getSensor(String sensortype);
}
