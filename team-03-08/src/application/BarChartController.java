package application;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ResourceBundle;
import java.util.Comparator;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class BarChartController implements Initializable{
    @FXML
    private Button goBack;
    
    @FXML
	private ChoiceBox<String> typeSelection;
    
    @FXML
	private ChoiceBox<String> periodSelection;
    
    @FXML
	private ChoiceBox<Month> monthSelection;
    
    @FXML
	private ChoiceBox<Integer> yearSelection;
    
    @FXML
    private Label monthLabel;
    
    @FXML
    private Label monthNA;
    
    @FXML Button updateChart;
    
    @FXML
    private BarChart<?,?> barChart;
    
    private CategoryAxis x;
    
    private NumberAxis y;
    
   
    private XYChart.Series barChartData;
        
    private String typeOptions[] = {"Expense", "Income", "Net Income"};
    private String periodOptions[] = {"Daily", "Weekly", "Monthly"};
    private static Integer[] years;
    private DatabaseManager conn;
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	
		try {
			conn = DatabaseManager.create("jdbc:mysql://localhost:3306/pocket_finance", "root", "Cannucks123!");
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initTypeSelection();
		initPeriodSelection();
		initMonthSelection();
		initYearSelection();
		
		barChartData = new XYChart.Series<>();
		try {
			DailyRecord[] recordSet = new DailyRecord[monthSelection.getValue().length(true)]; //create a set that is size of month
			for(DailyRecord dailyExpense: conn.getDailyExpense(yearSelection.getValue(), monthSelection.getValue())) {
				recordSet[dailyExpense.day()-1] = dailyExpense;
//				barChartData.getData().add(new XYChart.Data<>(""+dailyExpense.day(), dailyExpense.amount()));
				System.out.println("Day: " + dailyExpense.day() + " Amount: " + dailyExpense.amount());
			}
			
			//create bar chart data set to display
			for(int i=0; i<recordSet.length; i++) {
				if(recordSet[i] != null) {
					barChartData.getData().add(new XYChart.Data<>(Integer.toString(i+1), recordSet[i].amount()));
				}
				else {
					barChartData.getData().add(new XYChart.Data<>(Integer.toString(i+1), 0.0));
				}
			}
			barChart.getData().removeAll(barChart.getData());
			barChart.getData().addAll(barChartData);
			barChart.setBarGap(0.0);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * Function: Initialize Month dropdown with Months and set default value to January
	*/
	private void initTypeSelection() {
		typeSelection.getItems().addAll(typeOptions);
		typeSelection.setValue(typeOptions[0]);
	}
	
	/*
	 * Function: Initialize Month dropdown with Months and set default value to January
	*/
	private void initPeriodSelection() {
		periodSelection.getItems().addAll(periodOptions);
		periodSelection.setValue(periodOptions[0]);
	}
	
	/*
	 * Function: Initialize Month dropdown with Months and set default value to January
	*/
	private void initMonthSelection() {
		monthSelection.getItems().addAll(Month.values());
		monthSelection.setValue(Month.JANUARY);
	}
	
	/*
	 * Function: Initialize Year dropdown with past 4 years and set default value to current year
	*/
	private void initYearSelection() {
		int numberOfYears = 4; //number of years to select from including current year
		years = new Integer[numberOfYears];
		for(int i=0; i<numberOfYears; i++) {
			years[i] = LocalDate.now().getYear() - i;
		}
		yearSelection.getItems().addAll(years);
		yearSelection.setValue(LocalDate.now().getYear());
	}
	
	
	
	
	
	@FXML
	public void toggleMonthlySelection(ActionEvent event) {
		periodSelection.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {		
				monthSelection.setVisible(true);
				if(periodSelection.getItems().get(number2.intValue()).equals("Monthly")) {
					monthSelection.setVisible(false);
				}
			}
		});
	}

	private void getDaily(String type) {	
		if(type.equals("Expense")) {
			try {
				DailyRecord[] recordSet = new DailyRecord[monthSelection.getValue().length(true)]; //create a set that is size of month
				//map items from List to set representing the data in the month
				for(DailyRecord dailyExpense: conn.getDailyExpense(yearSelection.getValue(), monthSelection.getValue())) {
					recordSet[dailyExpense.day()-1] = dailyExpense;
				}	
				//create bar chart data set to display
				for(int i=0; i<recordSet.length; i++) {
					if(recordSet[i] != null) {
						barChartData.getData().add(new XYChart.Data<>(Integer.toString(i+1), recordSet[i].amount()));
					}
					else {
						barChartData.getData().add(new XYChart.Data<>(Integer.toString(i+1), 0.0));
					}
				}
				barChart.getData().removeAll(barChart.getData());
				barChart.getData().addAll(barChartData);
				barChart.setBarGap(0.0);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else if(type.equals("Income")) {
			try {
				DailyRecord[] recordSet = new DailyRecord[monthSelection.getValue().length(true)]; //create a set that is size of month
				//map items from List to set representing the data in the month
				for(DailyRecord dailyIncome: conn.getDailyIncome(yearSelection.getValue(), monthSelection.getValue())) {
					recordSet[dailyIncome.day()-1] = dailyIncome;
				}
				
				
				//create bar chart data set to display
				for(int i=0; i<recordSet.length; i++) {
					if(recordSet[i] != null) {
						barChartData.getData().add(new XYChart.Data<>(Integer.toString(i+1), recordSet[i].amount()));
					}
					else {
						barChartData.getData().add(new XYChart.Data<>(Integer.toString(i+1), 0.0));
					}
				}
				barChart.getData().removeAll(barChart.getData());
				barChart.getData().addAll(barChartData);
				barChart.setBarGap(0.0);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else if(type.equals("Net Income")) {
			try {
				DailyRecord[] recordSet = new DailyRecord[monthSelection.getValue().length(true)]; //create a set that is size of month
				//map items from List to set representing the data in the month
				for(DailyRecord dailyNetIncome: conn.getDailyNetIncome(yearSelection.getValue(), monthSelection.getValue())) {
					recordSet[dailyNetIncome.day()-1] = dailyNetIncome;
				}
				
				
				//create bar chart data set to display
				for(int i=0; i<recordSet.length; i++) {
					if(recordSet[i] != null) {
						barChartData.getData().add(new XYChart.Data<>(Integer.toString(i+1), recordSet[i].amount()));
					}
					else {
						barChartData.getData().add(new XYChart.Data<>(Integer.toString(i+1), 0.0));
					}
				}
				barChart.getData().removeAll(barChart.getData());
				barChart.getData().addAll(barChartData);
				barChart.setBarGap(0.0);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
    
	private void getWeekly(String type) {
		if(type.equals("Expense")) {
			try {
				
				WeeklyRecord[] recordSet = new WeeklyRecord[5]; //create a set that has amount of weeks in month (at most 5)
				//map items from List to set representing the data in the month
				for(WeeklyRecord weeklyExpense: conn.getWeeklyExpense(yearSelection.getValue(), monthSelection.getValue())) {
					recordSet[weeklyExpense.week()-1] = weeklyExpense;
				}
				
				
				//create bar chart data set to display
				for(int i=0; i<recordSet.length; i++) {
					//start = i*7+1, end = 7(i+1)
					String startDay = Integer.toString(7*i+1);
					String endDay = Integer.toString(7*(i+1));
					if(recordSet[i] != null) {
						barChartData.getData().add(new XYChart.Data<>(startDay + "-" + endDay, recordSet[i].amount()));
					}
					else {
						barChartData.getData().add(new XYChart.Data<>(startDay + "-" + endDay, 0.0));
					}
				}
				barChart.getData().removeAll(barChart.getData());
				barChart.getData().addAll(barChartData);
				barChart.setBarGap(0.0);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(type.equals("Income")) {
			try {
				
				WeeklyRecord[] recordSet = new WeeklyRecord[5]; //create a set that has amount of weeks in month (at most 5)
				//map items from List to set representing the data in the month
				for(WeeklyRecord weeklyIncome: conn.getWeeklyIncome(yearSelection.getValue(), monthSelection.getValue())) {
					recordSet[weeklyIncome.week()-1] = weeklyIncome;
				}
				
				
				//create bar chart data set to display
				for(int i=0; i<recordSet.length; i++) {
					//start = i*7+1, end = 7(i+1)
					String startDay = Integer.toString(7*i+1);
					String endDay = Integer.toString(7*(i+1));
					if(recordSet[i] != null) {
						barChartData.getData().add(new XYChart.Data<>(startDay + "-" + endDay, recordSet[i].amount()));
					}
					else {
						barChartData.getData().add(new XYChart.Data<>(startDay + "-" + endDay, 0.0));
					}
				}
				barChart.getData().removeAll(barChart.getData());
				barChart.getData().addAll(barChartData);
				barChart.setBarGap(0.0);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(type.equals("Net Income")) {
			try {
				
				WeeklyRecord[] recordSet = new WeeklyRecord[5]; //create a set that has amount of weeks in month (at most 5)
				//map items from List to set representing the data in the month
				for(WeeklyRecord weeklyNetIncome: conn.getWeeklyNetIncome(yearSelection.getValue(), monthSelection.getValue())) {
					recordSet[weeklyNetIncome.week()-1] = weeklyNetIncome;
					System.out.printf("Amount: %.2f Week: %d\n", weeklyNetIncome.amount(), weeklyNetIncome.week());
				}
				
				
				//create bar chart data set to display
				for(int i=0; i<recordSet.length; i++) {
					//start = i*7+1, end = 7(i+1)
					String startDay = Integer.toString(7*i+1);
					String endDay = Integer.toString(7*(i+1));
					if(recordSet[i] != null) {
						barChartData.getData().add(new XYChart.Data<>(startDay + "-" + endDay, recordSet[i].amount()));
					}
					else {
						barChartData.getData().add(new XYChart.Data<>(startDay + "-" + endDay, 0.0));
					}
				}
				barChart.getData().removeAll(barChart.getData());
				barChart.getData().addAll(barChartData);
				barChart.setBarGap(0.0);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void getMonthly(String type) {
		if(type.equals("Expense")) {
			try {
				
				MonthlyRecord[] recordSet = new MonthlyRecord[12]; //create a set that has the 12 months
				//map items from List to set representing the data in the month
				for(MonthlyRecord monthlyExpense: conn.getMonthlyExpense(yearSelection.getValue())) {
					recordSet[monthlyExpense.month().getValue()-1] = monthlyExpense;
				}
				
				
				//create bar chart data set to display
				for(int i=0; i<recordSet.length; i++) {
					String monthName = Month.of(i+1).name();
					if(recordSet[i] != null) {
						barChartData.getData().add(new XYChart.Data<>(monthName, recordSet[i].amount()));
					}
					else {
						barChartData.getData().add(new XYChart.Data<>(monthName, 0.0));
					}
				}
				barChart.getData().removeAll(barChart.getData());
				barChart.getData().addAll(barChartData);
				barChart.setBarGap(0.0);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(type.equals("Income")) {
			try {
				
				MonthlyRecord[] recordSet = new MonthlyRecord[12]; //create a set that has the 12 months
				//map items from List to set representing the data in the month
				for(MonthlyRecord monthlyIncome: conn.getMonthlyIncome(yearSelection.getValue())) {
					recordSet[monthlyIncome.month().getValue()-1] = monthlyIncome;
				}
	
				//create bar chart data set to display
				for(int i=0; i<recordSet.length; i++) {
					String monthName = Month.of(i+1).name();
					if(recordSet[i] != null) {
						barChartData.getData().add(new XYChart.Data<>(monthName, recordSet[i].amount()));
					}
					else {
						barChartData.getData().add(new XYChart.Data<>(monthName, 0.0));
					}
				}
				barChart.getData().removeAll(barChart.getData());
				barChart.getData().addAll(barChartData);
				barChart.setBarGap(0.0);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else if(type.equals("Net Income")) {
			try {
				MonthlyRecord[] recordSet = new MonthlyRecord[12]; //create a set that has the 12 months
				//map items from List to set representing the data in the month
				for(MonthlyRecord monthlyNetIncome: conn.getMonthlyNetIncome(yearSelection.getValue())) {
					recordSet[monthlyNetIncome.month().getValue()-1] = monthlyNetIncome;
				}
			
				//create bar chart data set to display
				for(int i=0; i<recordSet.length; i++) {
					String monthName = Month.of(i+1).name();
					if(recordSet[i] != null) {
						barChartData.getData().add(new XYChart.Data<>(monthName, recordSet[i].amount()));
					}
					else {
						barChartData.getData().add(new XYChart.Data<>(monthName, 0.0));
					}
				}
				barChart.getData().removeAll(barChart.getData());
				barChart.getData().addAll(barChartData);
				barChart.setBarGap(0.0);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void updateChart(ActionEvent event) {
		barChartData = new XYChart.Series<>();
		String type = typeSelection.getValue();
		
		if(type.equals("Expense")) {
			if(periodSelection.getValue().equals("Daily")) {
				getDaily(type);
			}
			
			//TODO Fix initial call 
			else if(periodSelection.getValue().equals("Weekly")) {
				getWeekly(type);
			}
			//TODO Fix initial call 
			else if(periodSelection.getValue().equals("Monthly")) {
				getMonthly(type);
			}
		}
		
		else if(type.equals("Income")) {
			if(periodSelection.getValue().equals("Daily")) {
				getDaily(type);
			}
			
			//TODO Fix initial call 
			else if(periodSelection.getValue().equals("Weekly")) {
				getWeekly(type);
			}
			//TODO Fix initial call 
			else if(periodSelection.getValue().equals("Monthly")) {
				getMonthly(type);
			}
		}
		
		else if(type.equals("Net Income")) {
			if(periodSelection.getValue().equals("Daily")) {
				getDaily(type);
			}
			
			//TODO Fix initial call 
			else if(periodSelection.getValue().equals("Weekly")) {
				getWeekly(type);
			}
			//TODO Fix initial call 
			else if(periodSelection.getValue().equals("Monthly")) {
				getMonthly(type);
			}
		}
	}
	public void goBack(ActionEvent event) {
        try {
            Stage stage = (Stage) goBack.getScene().getWindow();
            stage.close();
            Stage primaryStagestage = new Stage();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Home.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            primaryStagestage.setTitle("Home");
            primaryStagestage.setScene(new Scene(root));
            primaryStagestage.show();
        } catch (Exception e) {
            System.out.println("Cannot load new window");
        }
    }


}
