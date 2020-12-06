package application;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class HomeController implements Initializable{
	
	@FXML
	private ChoiceBox<String> chartMenu;
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
    private String[] chartOptions = {"Bar Chart", "Pie Chart"};


	/*
	 * Function: Initialize the home page with default From and To dates for 'total' fields
	*/
    public void initialize(URL location, ResourceBundle resources) {
      
			try {
				defaultHome();
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
			
    }
    
    private void defaultHome() throws ClassNotFoundException, SQLException {
    	chartMenu.getItems().addAll(chartOptions);
    	chartMenu.setValue("");
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
    	expensesField.setText("$" + String.format("%.2f", getTotalExpenses()));
    	incomeField.setText("$" + String.format("%.2f", getTotalIncome()));
    	netIncomeField.setText("$" + String.format("%.2f", getTotalNetIncome()));
    }
    
    private double getTotalExpenses() throws SQLException { return conn.getExpense(fromDate.getValue(), toDate.getValue()); }
    private double getTotalIncome() throws SQLException { return conn.getIncome(fromDate.getValue(), toDate.getValue()); }
    private double getTotalNetIncome() throws SQLException { return conn.getNetIncome(fromDate.getValue(), toDate.getValue()); }
    
    @FXML
    public void chartMenuAction(ActionEvent event) {
    	chartMenu.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {	
				if(chartMenu.getItems().get(number2.intValue()).equals("Bar Chart")) {
		    		 try {
		    	            Stage stage = (Stage)chartMenu.getScene().getWindow();
		    	            stage.close();
		    	            Stage primaryStagestage = new Stage();
		    	           
		    	            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("BarChart.fxml"));
		    	            Parent root = (Parent)fxmlLoader.load();
		    	            primaryStagestage.setTitle("Bar Chart");
		    	            primaryStagestage.setScene(new Scene(root));
		    	            primaryStagestage.show();
		    	                        
		    	        }
		    	        catch (Exception e) {
		    	            System.out.println("Cannot load new window");
		    	            e.printStackTrace();
		    	        }
		    	}
				else {
					 try {
				            Stage stage = (Stage)chartMenu.getScene().getWindow();
				            stage.close();
				            Stage primaryStagestage = new Stage();
				           
				            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PieChart.fxml"));
				            Parent root = (Parent)fxmlLoader.load();
				            primaryStagestage.setTitle("Chart & Analysis");
				            primaryStagestage.setScene(new Scene(root));
				            primaryStagestage.show();
				                        
				        }
				        catch (Exception e) {
				            System.out.println("Cannot load new window");
				            e.printStackTrace();
				        }
				}
				
			}
		});
  
    }
    
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
