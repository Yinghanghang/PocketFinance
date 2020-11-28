package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class PieChartController {
    @FXML
    private Button goBack;
    
    public void goBack(ActionEvent event) {
        try {
            Stage stage = (Stage) goBack.getScene().getWindow();
            stage.close();
            Stage primaryStagestage = new Stage();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Home.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            primaryStagestage.setTitle("Add Record");
            primaryStagestage.setScene(new Scene(root));
            primaryStagestage.show();
        } catch (Exception e) {
            System.out.println("Cannot load new window");
        }
    }
}
