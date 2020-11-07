package application;

import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class AddController {
    @FXML
    private Button add;
    
    @FXML
    private Button goBack;
    
    public void add(ActionEvent event) {
        Alert addAlert = new Alert(Alert.AlertType.CONFIRMATION);
        addAlert.setContentText("Added Successfully");
        Optional<ButtonType> result = addAlert.showAndWait();
        ButtonType button = result.orElse(ButtonType.CANCEL);
        if (button == ButtonType.OK) {
           goBack(event);
        } else {
            System.out.println("Canceled");
        }
    }
    
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
        }
        catch (Exception e) {
            System.out.println("Cannot load new window");
        }
    }
    
}
