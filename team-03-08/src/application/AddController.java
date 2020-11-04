package application;

import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;

public class AddController {
    @FXML
    private Button cancelAdder;
    @FXML
    private Button saveAdder;
    @FXML
    private TextField amountInput;
    @FXML
    private CheckBox expenseCheckBox;
    @FXML
    private CheckBox incomeCheckBox;
    
	/*
	 * Function: Discard all data input and return to home page
	*/
    @FXML
    public void cancelButtonAction(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Home.fxml"));
            Parent root = (Parent)fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("PocketFinance");
            stage.setScene(new Scene(root));
            stage.show();
                       
        }
        catch (Exception e) {
            System.out.println("Cannot load new window");
        }
    }
    
	/*
	 * Function: Uploads data to database if all inputs are valid and return to home page
	*/
    @FXML
    public void saveButtonAction(ActionEvent event) {
        if(!isValid()) {
        	try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddError.fxml"));
                Parent root = (Parent)fxmlLoader.load();
                Stage stage = new Stage();
                stage.setTitle("Add Record");
                stage.setScene(new Scene(root));
                stage.show();                                      
            }
            catch (Exception e) {
                System.out.println("Cannot load new window");
            }
        }
    }

	/*
	 * Function: Unchecks the 'Income' option within the GUI when 'Expense' option is selected
	*/
    @FXML
    public void uncheckIncome(ActionEvent event) {
    	incomeCheckBox.setSelected(false);
    }
    
    /*
	 * Function: Unchecks the 'Expense' option within the GUI when 'Income' option is selected
	*/
    @FXML
    public void uncheckExpense(ActionEvent event) {
    	expenseCheckBox.setSelected(false);
    }
    
	/*
	 * Function: Checks if all input fields have valid data within them
	*/
    private boolean isValid() {
    	if(!this.amountIsValid()) 
    		return false; 
    	
    	return true;
    }
    
	/*
	 * Function: Checks if amount input is a double
	*/
    private boolean amountIsValid() {
    	try {
    		double input = Double.parseDouble(amountInput.getText());
    		return true;
    	}catch (NumberFormatException nfe) {
    		System.out.println("Invalid Amount");
    	}
    	return false;
    }
}
