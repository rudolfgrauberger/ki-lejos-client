package net;

public class LeJOSDoubleResult extends LeJOSSuccessResult {
	private double value;
	
	public LeJOSDoubleResult(double value) {
		this.value = value;
	}
	
	double getValue() {
		return this.value;
	}

}
