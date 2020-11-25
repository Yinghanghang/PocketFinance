package application;

import java.time.Month;

public class MonthlyRecord {
	private final double amount;
	private final Month month;

	MonthlyRecord(double amount, Month month) {
		this.amount = amount;
		this.month = month;
	}

	public double amount() { return amount; }

	public Month month() { return month; }

}