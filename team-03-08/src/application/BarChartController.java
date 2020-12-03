package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class BarChartController implements Initializable{
    @FXML
    private Button goBack;
    
    @FXML
    private BarChart<?,?> barChart;
    @FXML
    private CategoryAxis x;
    @FXML
    private NumberAxis y;
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		XYChart.Series set1 = new XYChart.Series<>();
		set1.getData().add(new XYChart.Data("Jan.", 800.23));
		set1.getData().add(new XYChart.Data("Feb..", 823.23));
		set1.getData().add(new XYChart.Data("Mar..", 400.23));
		set1.getData().add(new XYChart.Data("Apr..", -1002.23));
		barChart.getData().addAll(set1);
		
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
