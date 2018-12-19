package net;

public class LeJOSResultParser {
	
	private final static String SUCCESS = "SUCCESS";
	private final static String ERROR = "ERROR";
	
	public static ILeJOSResult getResult(String message) {
		
		if (message.startsWith(SUCCESS))
			return new LeJOSSuccessResult();
		else
			return new LeJOSFailureResult();
	}
}
