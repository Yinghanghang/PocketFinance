package application;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddController implements Initializable {

	@FXML
	private DatePicker transactionDate;
	@FXML
	private CheckBox expenseCheckBox;
	@FXML
	private CheckBox incomeCheckBox;
	
	private String typeValue;
	
	@FXML
	private TextField amountInput;

	@FXML
	private Button add;

	@FXML
	private Button goBack;

	@FXML
	private ChoiceBox<String> categories;
	private static String[] items = { "Transport", "Education", "Entertainment", "Food", "Health", "Mortgage", "Shopping", "Travel", "Utilities"  };
	
	/**
	 * date after current date or negative amount is not allowed
	 */
	private String errorPrompt = "Invalid Input. Please ensure that these fields have correct data: \n";
	private String invalidFields;
	private DatabaseManager conn;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		populateChoiceBox();
	}

	private void populateChoiceBox() {
		categories.getItems().addAll(items);
	}

	/*
	 * Function: Unchecks the 'Income' option within the GUI when 'Expense' option
	 * is selected
	 */
	@FXML
	public void uncheckIncome(ActionEvent event) {
		this.incomeCheckBox.setSelected(false);
		typeValue = "Expense";
	}
	  
	/*
	 * Function: Unchecks the 'Expense' option within the GUI when 'Income' option
	 * is selected
	 */
	@FXML
	public void uncheckExpense(ActionEvent event) {
		this.expenseCheckBox.setSelected(false);
		typeValue = "Income";
	}
	  
	/*
	 * Function: Checks if all input fields have valid data within them and appends to a 
	 * 			 list of invalid data fields
	 */
	private boolean isValid() {
		
		int errorCount = 0;
		
		if(!this.dateIsValid()) {
			invalidFields += "- Date\n";
			errorCount++;
		}
		if(!this.typeIsValid()) {
			invalidFields += "- Type\n";
			errorCount++;
		}
		if (!this.categoryIsValid()) {
			invalidFields += "- Category\n";
			errorCount++;
		}
		
		if (!this.amountIsValid()) {
			invalidFields += "- Amount\n";
			errorCount++;
		}
		if(errorCount>0)
			return false;

		return true;
	}
	
	/*
	 * Function: Checks if date input is not in the future
	*/
	private boolean dateIsValid() {
		//if input date is a future date
		if(transactionDate.getValue().compareTo(LocalDate.now())>0) {
			return false;
		}
		return true;
	}
	
	/*
	 * Function: Checks if a type has been selected
	*/
	private boolean typeIsValid() {
		if(!expenseCheckBox.isSelected() && !incomeCheckBox.isSelected()) {
			return false;
		}
		return true;
	}
	
	private boolean categoryIsValid() {
		if(categories.getValue()==null) {
			return false;
		}
		return true;
	}
	  
	/*
	 * Function: Checks if amount input is a double
	 */
	private boolean amountIsValid() {
		try {
			double input = Double.parseDouble(amountInput.getText());
			return true;
		} catch (NumberFormatException nfe) {
		}
		return false;
	}
	 
	public void add(ActionEvent event) throws ClassNotFoundException, SQLException {

		this.invalidFields = "";

		if(this.isValid()) {
			double amount = Double.parseDouble(amountInput.getText());
			conn = DatabaseManager.create("jdbc:mysql://localhost:3306/pocket_finance", "root", "Cannucks123!"); //Change this to your information on your machine
			conn.addTransaction(new Transaction(Date.valueOf(transactionDate.getValue()),  amount, categories.getValue(), typeValue));
			Alert addAlert = new Alert(Alert.AlertType.INFORMATION);
			addAlert.setTitle("Confirmation");
			addAlert.setContentText("Added Successfully");
			Optional<ButtonType> result = addAlert.showAndWait();
			if (result.get() == ButtonType.OK) {
				goBack(event);
			} 
		}else {
			Alert errorAlert = new Alert(Alert.AlertType.ERROR);
			errorAlert.setContentText(errorPrompt + invalidFields);
			Optional<ButtonType> result = errorAlert.showAndWait();
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
		} catch (Exception e) {
			System.out.println("Cannot load new window");
		}
	}

}
