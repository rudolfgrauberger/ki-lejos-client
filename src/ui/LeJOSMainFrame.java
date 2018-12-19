package ui;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.JFrame;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.remote.ev3.RemoteEV3;
import lejos.robotics.SampleProvider;

public class LeJOSMainFrame extends JFrame {
	
	public LeJOSMainFrame() {
		RemoteEV3 ev3 = null;
		try {
			ev3 = new RemoteEV3("10.0.1.9");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Port p = ev3.getPort("S2");
		SensorModes sensor = new EV3UltrasonicSensor(p);
		
		SampleProvider distance = sensor.getMode("Distance");
		float[] sample = new float[distance.sampleSize()];
		
		while(true) {
			distance.fetchSample(sample, 0);
			System.out.println(Float.toString(sample[0]));
		}
	}
}
