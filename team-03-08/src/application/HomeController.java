package application;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class HomeController implements Initializable{
    @FXML
    private Button recordAdder;
    @FXML
    private DatePicker fromDate;
    @FXML
    private DatePicker toDate;
    @FXML 
    private TextField expensesField;
    @FXML 
    private TextField incomeField;
    @FXML 
    private TextField netIncomeField;
    
    private DatabaseManager conn;


	/*
	 * Function: Initialize the home page with default From and To dates for 'total' fields
	*/
    public void initialize(URL location, ResourceBundle resources) {
        try {
			defaultHome();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private void defaultHome() throws ClassNotFoundException, SQLException {
	
    	LocalDate now = LocalDate.now();
    	LocalDate firstDayOfMonth = now.with(TemporalAdjusters.firstDayOfMonth());
    	
    	//Set From and To dates with default values
    	toDate.setValue(now);
    	fromDate.setValue(firstDayOfMonth);
    	
    	//Query the database with default values and set the total fields 
    	conn = DatabaseManager.create("jdbc:mysql://localhost:3306/pocket_finance", "root", "Cannucks123!"); //Change this to your information on your machine
    	expensesField.setText("$" + String.format("%.2f", getTotalExpenses()));
    	incomeField.setText("$" + String.format("%.2f", getTotalIncome()));
    	netIncomeField.setText("$" + String.format("%.2f", getTotalNetIncome()));
    }
    
    public void setTotals(ActionEvent event) throws ClassNotFoundException, SQLException {
    	expensesField.setText("$" + String.valueOf(getTotalExpenses()));
    	incomeField.setText("$" + String.valueOf(getTotalIncome()));
    	netIncomeField.setText("$" + String.valueOf(getTotalNetIncome()));
    }
    
    private double getTotalExpenses() throws SQLException { return conn.getExpense(fromDate.getValue(), toDate.getValue()); }
    private double getTotalIncome() throws SQLException { return conn.getIncome(fromDate.getValue(), toDate.getValue()); }
    private double getTotalNetIncome() throws SQLException { return conn.getNetIncome(fromDate.getValue(), toDate.getValue()); }
    
    @FXML
    public void addButtonAction(ActionEvent event) {
        try {
            Stage stage = (Stage)recordAdder.getScene().getWindow();
            stage.close();
            Stage primaryStagestage = new Stage();
           
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Add.fxml"));
            Parent root = (Parent)fxmlLoader.load();
            primaryStagestage.setTitle("Add Record");
            primaryStagestage.setScene(new Scene(root));
            primaryStagestage.show();
                        
        }
        catch (Exception e) {
            System.out.println("Cannot load new window");
            e.printStackTrace();
        }
    }
    
}
