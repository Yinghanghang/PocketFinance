package application;

public class PieChartSector {
	private final String category;
	private final double percentage;

	PieChartSector(String category, double percentage) {
		this.category = category;
		this.percentage = percentage;
	}

	public String category() { return category; }

	public double percentage() { return percentage; }

}
