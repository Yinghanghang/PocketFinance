package application;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;


public class DatabaseManager {
	private Connection conn;	
	private static DatabaseManager instance = null;

	/**
	 * Creates a connection to database.
	 * @throws ClassNotFoundException 
	 */
	private DatabaseManager(String database, String user, String password) throws SQLException, ClassNotFoundException {
		this.conn = DriverManager.getConnection(database, user, password);	
	}

	/**
	 * Creates a database manager with singleton pattern.
	 */
	public static DatabaseManager create(String database, String user, String password) throws ClassNotFoundException, SQLException { 
		if (instance == null) {
			Class.forName("com.mysql.jdbc.Driver");
			instance = new DatabaseManager(database, user, password);
		}
		return instance;
	}

	/**
	 * Gets transactions from database
	 */
	public List<Transaction> getTransactions() throws SQLException {
		List<Transaction> result = new ArrayList<>();
		Statement statement = conn.createStatement();
		try (ResultSet resultSet = statement.executeQuery("select Date, Amount, Category, Type from transaction")) {
			while (resultSet.next()) {
				result.add(new Transaction(resultSet.getDate(1), resultSet.getDouble(2), resultSet.getString(3), resultSet.getString(4)));
			}
		}
		return result;
	}

	/**
	 * Get the sum of income from date1 to date2
	 */
	public double getIncome(LocalDate from, LocalDate to) throws SQLException {
		try (PreparedStatement statement = conn.prepareStatement("select sum(Amount) from transaction where date between ? and ? and type=\"Income\"")) {
			statement.setDate(1, Date.valueOf(from));
			statement.setDate(2, Date.valueOf(to));
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getDouble(1);					
				}
				return 0;
			}
		}
	}

	/**
	 * Get the sum of expense from date1 to date2
	 */
	public double getExpense(LocalDate from, LocalDate to) throws SQLException {
		try (PreparedStatement statement = conn.prepareStatement("select sum(Amount) from transaction where date between ? and ? and type=\"Expense\"")) {
			statement.setDate(1, Date.valueOf(from));
			statement.setDate(2, Date.valueOf(to));
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getDouble(1);					
				}
				return 0;
			}
		}
	}
	
	/**
	 * Get the sum of net income from date1 to date2
	 */
	public double getNetIncome(LocalDate from, LocalDate to) throws SQLException {
		return getIncome(from, to) - getExpense(from, to);
	}
	
	/**
	 * return a list of daily record 
	 */
	private List<DailyRecord> getDaily(int year, Month month, String type) throws SQLException {
		LocalDate from = LocalDate.of(year, month, 1);
		LocalDate to = LocalDate.of(year, month, month.length(from.isLeapYear()));
		try (PreparedStatement statement = conn.prepareStatement("select date, sum(Amount) from transaction where date between ? and ? and type=? group by date" )) {
			statement.setDate(1, Date.valueOf(from));
			statement.setDate(2, Date.valueOf(to));
			statement.setString(3, type);
			try (ResultSet resultSet = statement.executeQuery()) {
				ArrayList<DailyRecord> list = new ArrayList<>();
				while (resultSet.next()) {
					LocalDate resultDate = resultSet.getDate(1).toLocalDate();
					int day = resultDate.getDayOfMonth();
					DayOfWeek weekDay = resultDate.getDayOfWeek();
					double amount = resultSet.getDouble(2);					
					list.add(new DailyRecord(amount, day, weekDay));			
				}
				return list;
			}
		}
	}
	
	/**
	 * return a list of daily expense record
	 */
	public List<DailyRecord> getDailyExpense(int year, Month month) throws SQLException {
		return getDaily(year, month, "Expense");
	}
	
	/**
	 * return a list of daily income record
	 */
	public List<DailyRecord> getDailyIncome(int year, Month month) throws SQLException {
		return getDaily(year, month, "Income");
	}
	
	/**
	 * return a list of daily net income record
	 */
	public List<DailyRecord> getDailyNetIncome(int year, Month month) throws SQLException {
		LocalDate from = LocalDate.of(year, month, 1);
		LocalDate to = LocalDate.of(year, month, month.length(from.isLeapYear()));
		try (PreparedStatement statement = conn.prepareStatement(
				"SELECT data.Date, data.Income - data.Expense as NetIncome\n" +
				"FROM (\n" +
				"    SELECT transaction.Date as Date,\n" +
				"        sum(IF(transaction.Type = 'Income', transaction.Amount,0)) as Income,\n" +
				"		sum(IF(transaction.Type = 'Expense', transaction.Amount,0)) as Expense\n" +
				"    FROM transaction\n" +
				"    WHERE Date between ? and ?\n" +
				"    GROUP BY transaction.Date\n" +
				") data")) {
			statement.setDate(1, Date.valueOf(from));
			statement.setDate(2, Date.valueOf(to));
			try (ResultSet resultSet = statement.executeQuery()) {
				ArrayList<DailyRecord> list = new ArrayList<>();
				while (resultSet.next()) {
					LocalDate resultDate = resultSet.getDate(1).toLocalDate();
					int day = resultDate.getDayOfMonth();
					DayOfWeek weekDay = resultDate.getDayOfWeek();
					double amount = resultSet.getDouble(2);					
					list.add(new DailyRecord(amount, day, weekDay));	
				}
				return list;
			}
		}
	}
	
	/**
	 * return a list of weekly record
	 */
	private List<WeeklyRecord> getWeekly(int year, Month month, String type) throws SQLException {
		LocalDate from = LocalDate.of(year, month, 1);
		LocalDate to = LocalDate.of(year, month, month.length(from.isLeapYear()));
		try (PreparedStatement statement = conn.prepareStatement(
				"SELECT \n" + 
				"(CASE WHEN day(Date) < 8 THEN '1' \n" + 
				"  ELSE CASE WHEN day(DATE) < 15 then '2' \n" + 
				"    ELSE CASE WHEN  day(Date) < 22 then '3' \n" + 
				"      ELSE CASE WHEN  day(Date) < 29 then '4'     \n" + 
				"        ELSE '5'\n" + 
				"      END\n" + 
				"    END\n" + 
				"  END\n" + 
				"END) as Week, SUM(Amount) From transaction\n" + 
				"WHERE DATE between ? and ?\n" + 
				"and type = ?\n" + 
				"group by Week")) {
			statement.setDate(1, Date.valueOf(from));
			statement.setDate(2, Date.valueOf(to));
			statement.setString(3, type);
			try (ResultSet resultSet = statement.executeQuery()) {
				ArrayList<WeeklyRecord> list = new ArrayList<>();
				while (resultSet.next()) {			
					list.add(new WeeklyRecord(resultSet.getDouble(2),resultSet.getInt(1)));			
				}
				return list;
			}
		}
	}
	
	/**
	 * return a list of weekly expense record
	 */
	public List<WeeklyRecord> getWeeklyExpense(int year, Month month) throws SQLException {
		return getWeekly(year, month, "Expense");
	}
	
	/**
	 * return a list of weekly income record
	 */
	public List<WeeklyRecord> getWeeklyIncome(int year, Month month) throws SQLException {
		return getWeekly(year, month, "Income");
	}
	
	/**
	 * return a list of weekly net income record
	 */
	public List<WeeklyRecord> getWeeklyNetIncome(int year, Month month) throws SQLException {
		LocalDate from = LocalDate.of(year, month, 1);
		LocalDate to = LocalDate.of(year, month, month.length(from.isLeapYear()));
		try (PreparedStatement statement = conn.prepareStatement(
				"SELECT data.Week, data.Income - data.Expense as NetIncome\n" + 
				"FROM (\n" + 
				" SELECT \n" + 
				"  (CASE WHEN day(Date) < 8 THEN '1' \n" + 
				"   ELSE CASE WHEN day(DATE) < 15 then '2' \n" + 
				"    ELSE CASE WHEN  day(Date) < 22 then '3' \n" + 
				"      ELSE CASE WHEN  day(Date) < 29 then '4'     \n" + 
				"        ELSE '5'\n" + 
				"      END\n" + 
				"    END\n" + 
				"  END\n" + 
				"END) as Week, \n" + 
				"    sum(IF(Type = 'Income', Amount,0)) as Income,\n" + 
				"	sum(IF(Type = 'Expense', Amount,0)) as Expense\n" + 
				"FROM transaction\n" + 
				"WHERE DATE between ? and ?\n" + 
				"group by Week\n" + 
				") data")) {
			statement.setDate(1, Date.valueOf(from));
			statement.setDate(2, Date.valueOf(to));
			try (ResultSet resultSet = statement.executeQuery()) {
				ArrayList<WeeklyRecord> list = new ArrayList<>();
				while (resultSet.next()) {			
					list.add(new WeeklyRecord(resultSet.getDouble(2), resultSet.getInt(1)));	
				}
				return list;
			}
		}
	}
	
	/**
	 * return a list of monthly record
	 */
	private List<MonthlyRecord> getMonthly(int year, String type) throws SQLException {
		LocalDate from = LocalDate.ofYearDay(year, 1);
		LocalDate to = LocalDate.ofYearDay(year, from.lengthOfYear());
		try (PreparedStatement statement = conn.prepareStatement(
				"SELECT month(Date) as Month, sum(Amount) from transaction\n" + 
				"WHERE Date between ? and ?\n" + 
				"and type = ?\n" + 
				"group by Month")) {
			statement.setDate(1, Date.valueOf(from));
			statement.setDate(2, Date.valueOf(to));
			statement.setString(3, type);
			try (ResultSet resultSet = statement.executeQuery()) {
				ArrayList<MonthlyRecord> list = new ArrayList<>();
				while (resultSet.next()) {
					list.add(new MonthlyRecord(resultSet.getDouble(2), Month.of(resultSet.getInt(1))));			
				}
				return list;
			}
		}
	}
	
	/**
	 * return a list of monthly expense record
	 */
	public List<MonthlyRecord> getMonthlyExpense(int year) throws SQLException {
		return getMonthly(year, "Expense");
	}
	
	/**
	 * return a list of monthly income record
	 */
	public List<MonthlyRecord> getMonthlyIncome(int year) throws SQLException {
		return getMonthly(year, "Income");
	}
	
	/**
	 * return a list of monthly net income record
	 */
	public List<MonthlyRecord> getMonthlyNetIncome(int year) throws SQLException {
		LocalDate from = LocalDate.ofYearDay(year, 1);
		LocalDate to = LocalDate.ofYearDay(year, from.lengthOfYear());
		try (PreparedStatement statement = conn.prepareStatement(				
				"SELECT data.Month, data.Income - data.Expense as NetIncome\n" +
				"FROM (\n" +
					"    SELECT month(Date) as Month,\n" +
					"        sum(IF(transaction.Type = 'Income', transaction.Amount,0)) as Income,\n" +
					"		sum(IF(transaction.Type = 'Expense', transaction.Amount,0)) as Expense\n" +
					"    FROM transaction\n" +
					"    WHERE Date between ? and ?\n" +
					"    GROUP BY Month\n" +
				") data")) {
			statement.setDate(1, Date.valueOf(from));
			statement.setDate(2, Date.valueOf(to));
			try (ResultSet resultSet = statement.executeQuery()) {
				ArrayList<MonthlyRecord> list = new ArrayList<>();
				while (resultSet.next()) {
					list.add(new MonthlyRecord(resultSet.getDouble(2), Month.of(resultSet.getInt(1))));			
				}
				return list;
			}
		}
	}
	
	/**
	 * return a list of pie chart sector
	 */
	public List<PieChartSector> getPieChart(int year, Month month) throws SQLException {
		LocalDate from = LocalDate.of(year, month, 1);
		LocalDate to = LocalDate.of(year, month, month.length(from.isLeapYear()));
		try (PreparedStatement statement = conn.prepareStatement(
				"SELECT Category, sum(Amount) / data.total from transaction\n" + 
				"CROSS JOIN (\n" + 
				"  SELECT sum(Amount) as total from transaction \n" + 
				"  WHERE DATE between ? and ?\n" + 
				"  and type = 'Expense'\n" + 
				") data\n" + 
				"WHERE DATE between ? and ?\n" + 
				"and type = 'Expense'\n" + 
				"GROUP BY Category, data.total;")) {
			statement.setDate(1, Date.valueOf(from));
			statement.setDate(2, Date.valueOf(to));
			statement.setDate(3, Date.valueOf(from));
			statement.setDate(4, Date.valueOf(to));
			try (ResultSet resultSet = statement.executeQuery()) {
				ArrayList<PieChartSector> list = new ArrayList<>();
				while (resultSet.next()) {			
					list.add(new PieChartSector(resultSet.getString(1), resultSet.getDouble(2)));	
				}
				return list;
			}
		}
	}
	
	/**
	 * add transaction to database
	 */
	public boolean addTransaction(Transaction transaction) throws SQLException {
		
		try (PreparedStatement statement = conn.prepareStatement("INSERT INTO transaction(Date, Type, Category, Amount) VALUES (?, ?, ?, ?)")) {
			statement.setDate(1, transaction.date());
			statement.setString(2, transaction.type());
			statement.setString(3, transaction.category());
			statement.setDouble(4, transaction.amount());
			return statement.executeUpdate() != 0;
		}
	}

	public void close() throws SQLException {
		conn.close();
	}
}
