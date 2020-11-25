package application;

import java.time.DayOfWeek;

public class DailyRecord {
	private final double amount;
	private final int day;
	private final DayOfWeek weekDay;

	DailyRecord(double amount, int day, DayOfWeek weekDay) {
		this.amount = amount;
		this.day = day;
		this.weekDay = weekDay;
	}

	public double amount() { return amount; }

	public int day() { return day; }
	
	public DayOfWeek weekDay() { return weekDay; }
}
