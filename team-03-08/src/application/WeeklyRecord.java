package application;


public class WeeklyRecord {
	
	private final double amount;
	private final int week;

	WeeklyRecord(double amount, int week) {
		this.amount = amount;
		this.week = week;
	}

	public double amount() { return amount; }

	public int week() { return week; }

}
