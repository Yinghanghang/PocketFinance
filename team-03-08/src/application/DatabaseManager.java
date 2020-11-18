package application;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
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
