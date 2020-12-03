package application;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ResourceBundle;

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
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class PieChartController implements Initializable {
    @FXML
    private Button goBack;
    
    @FXML
    private Label categoriesArea;
    private String categoryData;
    
    @FXML
    private ChoiceBox<Month> monthSelection;
//    private ChoiceBox<String> monthSelection;
//    private static String[] months = {"January", "February","March","April","May","June","July","August","September","October",
//    		"November","December"};
    
    @FXML
    private ChoiceBox<Integer> yearSelection;
    private static Integer[] years;
    
    @FXML
    private ObservableList<PieChart.Data> pieChartList;
    private DatabaseManager conn;
    
    @FXML
    private PieChart pieChart;
    
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

	/*
	 * Function: Initialize Pie Chart Page
	*/
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initMonthSelection();
		initYearSelection();
		initPieChart();
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
	
	/*
	 * Function: Initialize Pie Chart with default month and year and Initialize category area
	*/
	private void initPieChart() {
		pieChartList = FXCollections.observableArrayList();
		try {
			conn = DatabaseManager.create("jdbc:mysql://localhost:3306/pocket_finance", "root", "Cannucks123!");
			categoryData = "";
			for(PieChartSector pieChartData: conn.getPieChart(yearSelection.getValue(), monthSelection.getValue())) {
				pieChartList.add(new PieChart.Data(pieChartData.category(), pieChartData.percentage()));
				categoryData += "" + pieChartData.category() + ": " + String.format("%.2f",pieChartData.percentage()*100) + "% \n ";
			}
			pieChart.setData(pieChartList);	
			pieChart.setLegendVisible(false);
			
			if(!hasData(categoryData))
				categoryData = "No Data";
			
			categoriesArea.setText(categoryData);
			
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	/*
	 * Function: Update page when a different month is selected
	*/
	public void updateChartFromMonth(ActionEvent event) {
		monthSelection.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {	
				pieChartList = FXCollections.observableArrayList();
				categoryData = "";
				try {
					
					for(PieChartSector pieChartData: conn.getPieChart(yearSelection.getValue(),(Month.of(number2.intValue()+1)))) {
						pieChartList.add(new PieChart.Data(pieChartData.category(), pieChartData.percentage()));
						categoryData += "" + pieChartData.category() + ": " + String.format("%.2f",pieChartData.percentage()*100) + "% \n ";
					}
					pieChart.setData(pieChartList);	
					if(!hasData(categoryData))
						categoryData = "No Data";
					categoriesArea.setText(categoryData);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				
			}
		});
		
	}
	
	/*
	 * Function: Update page when a different year is selected
	*/
	public void updateChartFromYear(ActionEvent event) {
		yearSelection.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {	
				pieChartList = FXCollections.observableArrayList();
				String categoryData = "";
				try {
					for(PieChartSector pieChartData: conn.getPieChart(years[number2.intValue()], monthSelection.getValue())) {
						pieChartList.add(new PieChart.Data(pieChartData.category(), pieChartData.percentage()));
						categoryData += "" + pieChartData.category() + ": " + String.format("%.2f",pieChartData.percentage()*100) + "% \n ";
					}
					pieChart.setData(pieChartList);	
					if(!hasData(categoryData))
						categoryData = "No Data";
					categoriesArea.setText(categoryData);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				
			}
		});
		
	}
	
	private boolean hasData(String data) {
		if(data.length() == 0) {
			data = "No Data";
			return false; 
		}
		return true;
		
	}
	
}//end class
