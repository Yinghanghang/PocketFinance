package application;

import java.sql.Date;

public class Transaction {
	private final double amount;
	private final Date date;
	private final String category;
	private final String type;

	Transaction(Date date, double amount, String category, String type) {
		this.date = date;
		this.amount = amount;
		this.category = category;
		this.type = type;
	}

	public double amount() { return amount; }

	public Date date() { return date; }

	public String category() { return category; }
	
	public String type() { return type; }
	
	public String toString() {
		return date.toString() + type + amount;
	}
}
