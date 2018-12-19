package net;

public class LeJOSIntResult extends LeJOSSuccessResult {

	private int value;
	
	public LeJOSIntResult(int value) {
		this.value = value;
	}
	
	int getValue() {
		return this.value;
	}

}
