package client.net;

public class LeJOSResultParser {
	
	private final static String SUCCESS = "SUCCESS";
	private final static String ERROR = "ERROR";
	
	public static ILeJOSResult getResult(String message) {
		
		if (message.startsWith(SUCCESS))
			return new LeJOSSuccessResult();
		else if (message.startsWith(ERROR))
			return new LeJOSFailureResult();
		else if (isInteger(message)) {
			return new LeJOSIntResult(Integer.parseInt(message));
		} else if (isDouble(message)) {
			return new LeJOSDoubleResult(Double.parseDouble(message));
		}
		
		return new LeJOSFailureResult();
	}
	
	private static boolean isInteger(String str) {
	    try {
	        Integer.parseInt(str);
	        return true;
	    } catch (NumberFormatException e) {
	        return false;
	    }
	}
	
	private static boolean isDouble(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
