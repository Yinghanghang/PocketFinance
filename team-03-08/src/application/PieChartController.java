package application;

import java.net.URL;
import java.util.ResourceBundle;

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
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class PieChartController implements Initializable {
    @FXML
    private Button goBack;
    
    @FXML
    private ChoiceBox<String> monthSelection;
    private static String[] months = {"January", "February","March","April","May","June","July","August","September","October",
    		"November","December"};
    
//    private static MenuItem[] months = {new MenuItem("January"),new MenuItem("February"),new MenuItem("March"),new MenuItem("April"),new MenuItem("May"),
//    		new MenuItem("June"),new MenuItem("July"),new MenuItem("August"),new MenuItem("September"),
//    		new MenuItem("October"),new MenuItem("November"),new MenuItem("December")};
    
    
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

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		monthSelection.getItems().addAll(months);
		
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
				new PieChart.Data("Travel", 1003.20),
				new PieChart.Data("Education", 684.33),
				new PieChart.Data("Utils", 2000.15),
				new PieChart.Data("Food", 1500),
				new PieChart.Data("Health", 202));
		pieChart.setData(pieChartData);
				
		
	}
	
	public void updateChart(ActionEvent event) {
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
				new PieChart.Data("Travel", 1003.20),
				new PieChart.Data("Education", 684.33),
				new PieChart.Data("Utils", 100.15),
				new PieChart.Data("Food", 1500),
				new PieChart.Data("Health", 202));
		pieChart.setData(pieChartData);
	}
}
