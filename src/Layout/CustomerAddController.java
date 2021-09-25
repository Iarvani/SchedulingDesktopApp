package Layout;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import obj.DBManagement;
import obj.Customer;
import Utilitys.DBConnection;
import java.io.IOException;
import java.sql.*;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;

import static obj.Customer.isCustomerValid;
import static obj.DBManagement.setResBundle;

/** This class is used to control the customerAddFXML. */
public class CustomerAddController {

    DBConnection DB = new DBConnection();
    Customer C = new Customer();
    ResourceBundle rb = setResBundle();
    @FXML private Label cAddDivisionLbl;
    @FXML private Label cAddCustIDLbl;
    @FXML private Label cAddNameLbl;
    @FXML private Label langLbl;
    @FXML private Label cAddCountryLbl;
    @FXML private Label cAddPhoneLbl;
    @FXML private Label cAddPCodeLbl;
    @FXML private Label cAddAddressLbl;
    @FXML private Label cAddAddress2Lbl;
    @FXML private Label cAddUserLbl;
    @FXML private Label currentUserLbl;
    @FXML private Label currentLangLbl;
    @FXML private Label cAddClientLbl;
    @FXML private Button cAddHomeBtn;
    @FXML private Button cAddClearBtn;
    @FXML private Button cAddAddBtn;
    @FXML private Button cAddExitBtn;
    @FXML private TextField cAddNameField;
    @FXML private TextField cAddIDField;
    @FXML private TextField cAddPhoneField;
    @FXML private TextField cAddZipField;
    @FXML private TextField cAddAddressField;
    @FXML private TextField cAddAddress2Field;
    @FXML private ComboBox<String> cAddDivisionComboBox;
    @FXML private ComboBox<String> cAddCountryComboBox;

    private static String SQLStatement;
    private static PreparedStatement test;
    private static ResultSet result;
    /**
     * Applies the controls to the FXML customer home screen button
     * Redirects the user to the customer home screen when the button is pressed.
     * @throws IOException if there is an error loading the customerHome.FXML
     */
    //Customer home screen button controller
    @FXML
    private void openCustomerHomeScreen(ActionEvent event) throws IOException {
    //Load Customer home screen - - Exit Customer Add Screen
        Parent addPartParent = FXMLLoader.load(getClass().getResource("CustomerHome.fxml"));
        Scene addPartScene = new Scene(addPartParent);
        Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        addPartStage.setScene(addPartScene);
        addPartStage.show();
    }
    /**
     * pulls the lang string from loginController to select the resource bundle for the page
     * uses the resource bundle to apply the values to the text elements in the page
     */
    //set languages and change text on screen
    @FXML
    private void setLangTitles() {
        if(LoginController.getLang().equals("English")) {
            ResourceBundle rb = ResourceBundle.getBundle("Resources/nat_en");
            cAddNameField.setPromptText(rb.getString("nameFieldPrompt"));
            cAddIDField.setPromptText(rb.getString("iDFieldPrompt"));
            cAddPhoneField.setPromptText(rb.getString("phoneFieldPrompt"));
            cAddZipField.setPromptText(rb.getString("pCodeFieldPrompt"));
            cAddAddressField.setPromptText(rb.getString("addressFieldPrompt"));
            cAddAddress2Field.setPromptText(rb.getString("address2FieldPrompt"));
            cAddCountryComboBox.setPromptText(rb.getString("countryBoxPrompt"));
            cAddDivisionComboBox.setPromptText(rb.getString("divisionBoxPrompt"));
            cAddDivisionLbl.setText(rb.getString("divisionLbl"));
            cAddCustIDLbl.setText(rb.getString("iDLbl"));
            cAddNameLbl.setText(rb.getString("nameLbl"));
            langLbl.setText(rb.getString("langLbl"));
            cAddCountryLbl.setText(rb.getString("countryLbl"));
            cAddPhoneLbl.setText(rb.getString("phoneLbl"));
            cAddPCodeLbl.setText(rb.getString("pCodeLbl"));
            cAddAddressLbl.setText(rb.getString("addressLbl"));
            cAddAddress2Lbl.setText(rb.getString("address2Lbl"));
            cAddUserLbl.setText(rb.getString("userLbl"));
            currentUserLbl.setText(rb.getString("currentUserLbl"));
            currentLangLbl.setText(rb.getString("currentLangLbl"));
            cAddClientLbl.setText(rb.getString("addClientTitleLbl"));
            cAddHomeBtn.setText(rb.getString("homeBtn"));
            cAddClearBtn.setText(rb.getString("clearBtn"));
            cAddAddBtn.setText(rb.getString("addBtn"));
            cAddExitBtn.setText(rb.getString("exitBtn"));
        }
        if(LoginController.getLang().equals("French")) {
            ResourceBundle rb = ResourceBundle.getBundle("Resources/nat_fr");
            cAddNameField.setPromptText(rb.getString("nameFieldPrompt"));
            cAddIDField.setPromptText(rb.getString("iDFieldPrompt"));
            cAddPhoneField.setPromptText(rb.getString("phoneFieldPrompt"));
            cAddZipField.setPromptText(rb.getString("pCodeFieldPrompt"));
            cAddAddressField.setPromptText(rb.getString("addressFieldPrompt"));
            cAddAddress2Field.setPromptText(rb.getString("address2FieldPrompt"));
            cAddCountryComboBox.setPromptText(rb.getString("countryBoxPrompt"));
            cAddDivisionComboBox.setPromptText(rb.getString("divisionBoxPrompt"));
            cAddDivisionLbl.setText(rb.getString("divisionLbl"));
            cAddCustIDLbl.setText(rb.getString("iDLbl"));
            cAddNameLbl.setText(rb.getString("nameLbl"));
            langLbl.setText(rb.getString("langLbl"));
            cAddCountryLbl.setText(rb.getString("countryLbl"));
            cAddPhoneLbl.setText(rb.getString("phoneLbl"));
            cAddPCodeLbl.setText(rb.getString("pCodeLbl"));
            cAddAddressLbl.setText(rb.getString("addressLbl"));
            cAddAddress2Lbl.setText(rb.getString("address2Lbl"));
            cAddUserLbl.setText(rb.getString("userLbl"));
            currentUserLbl.setText(rb.getString("currentUserLbl"));
            currentLangLbl.setText(rb.getString("currentLangLbl"));
            cAddClientLbl.setText(rb.getString("addClientTitleLbl"));
            cAddHomeBtn.setText(rb.getString("homeBtn"));
            cAddClearBtn.setText(rb.getString("clearBtn"));
            cAddAddBtn.setText(rb.getString("addBtn"));
            cAddExitBtn.setText(rb.getString("exitBtn"));
        }
    }
    /**
     * A method used during the testing phase to check for DB connectivity
     * Runs a query through the DB  to pull a value and then prints the value to the console
     * Method is disabled unless it is needed for testing purposes.
     */
    //check for database connectivity and attempt to pull data that prints to console
    //Button for test has been commented out on CustomerAdd.fxml, remove the comment tags to test the button.
    @FXML
    private void checkDatabaseBtn() {
        int ID = 1;
        try (DB.connect) {
            SQLStatement = "SELECT * FROM first_level_divisions WHERE COUNTRY_ID = '" + ID + "'";
            test = DBConnection.startConnection().prepareStatement(SQLStatement);
            result = test.executeQuery();
            result.next();
            String foundType = result.getString(1) + " | " + result.getString(2);
            System.out.println("DB data string: " + foundType);
        } catch (SQLException e) {
            System.out.println("SQL Error - Check Database button: " + e);
        }
    }
    /**
     * pulls the lang string for resource bundle selection
     * queries the DB for the values to be added to the country combo box
     */
    //items to execute on the screens initial load
    private void onLoad() {
        setLangTitles();
        try (DB.connect) {
            currentUserLbl.setText(DBManagement.getCurrentUser());
            SQLStatement = "SELECT Country FROM countries";
            test = DBConnection.startConnection().prepareStatement(SQLStatement);
            result = test.executeQuery();
            //increment through results and assign the names to the country combo box
            while (result.next() && result != null) {
                String dbCountry = result.getString(1);
                cAddCountryComboBox.getItems().add(dbCountry);
            }
        }
        catch (SQLException e) {
            System.out.println("Country Box Error: " + e);
        }
    }
    /**
     * uses the value in the country combo box as a key to pull the values in the DB
     * to populate the division box values. Then allows the user control over the combo box
     * once the values are added, finally clearing the other values in the box if the country is changed manually
     */
    //Country box controller
    @FXML
    private void countryBoxSelected() {
        //set division/state combo box based on country selection
        try (DB.connect) {
            int countryID = 0;
            //if statements to determine the country selected to populate division combo box values
            if (cAddCountryComboBox.getValue().equals("U.S")) {
                countryID = 1;
                cAddDivisionLbl.setText("State");
                cAddDivisionComboBox.setPromptText("Select State");
                cAddDivisionComboBox.setDisable(false);
                cAddDivisionComboBox.getItems().clear();
            }
            if (cAddCountryComboBox.getValue().equals("UK")) {
                countryID = 2;
                cAddDivisionLbl.setText("Region");
                cAddDivisionComboBox.setPromptText("Select Region");
                cAddDivisionComboBox.setDisable(false);
                cAddDivisionComboBox.getItems().clear();
            }
            if (cAddCountryComboBox.getValue().equals("Canada")) {
                countryID = 3;
                cAddDivisionLbl.setText("Providence");
                cAddDivisionComboBox.setPromptText("Select Providence");
                cAddDivisionComboBox.setDisable(false);
                cAddDivisionComboBox.getItems().clear();
            }
            //create the SQL query string
            SQLStatement = "SELECT * FROM first_level_divisions WHERE COUNTRY_ID = '" + countryID + "'";
            test = DBConnection.startConnection().prepareStatement(SQLStatement);
            result = test.executeQuery();
            //increment through results and assign the names to the division combo box
            while (result.next()) {
                String dbDivision = result.getString(2);
                cAddDivisionComboBox.getItems().add(dbDivision);
            }
        }
        //catch error when associated with this method
        catch (SQLException e) {
            System.out.println("SQL Error - Populate DivBox: " + e);
        }
    }
    /**
     * Applies the controls to the FXML exit button
     * exits the program when the button is pressed.
     * provides a confirmation pop-up before exiting the program
     */
    //Exit button controller
    @FXML
    private void exitButton() {
        //Exit program button with confirmation popup
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle(rb.getString("exit"));
        alert.setHeaderText(rb.getString("exitConfirm"));
        alert.setContentText(rb.getString("exitMessage"));
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            System.exit(0);
        } else {
            System.out.println("Cancelled.");
        }
    }
    /**
     * Applies the controls to the FXML clear button
     * provides a confirmation pop-up when the button is pressed.
     * upon confirmation, resets the values in all of the fields
     */
    //clear button controller
    @FXML
    private void clearButton() {
        //clear values in text fields, and reset combo box values. Includes popup prompt verification.
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle(rb.getString("clear"));
        alert.setHeaderText(rb.getString("clearConfirm"));
        alert.setContentText(rb.getString("clearMessage?"));
        Optional<ButtonType> result = alert.showAndWait();
        //set fields to empty
        if (result.isPresent()&& result.get() == ButtonType.OK) {
            cAddNameField.setText("");
            cAddPhoneField.setText("");
            cAddZipField.setText("");
            cAddAddressField.setText("");
            cAddCountryComboBox.setValue("Select Country");
        } else {
            System.out.println("Cancelled.");
        }
    }
    /**
     * Applies the controls to the FXML add customer button
     * pulls the values from the text fields and pushes them to the is valid method for error checks
     * if no errors, pushes the values to the addcustomer method to check for duplicates and add customer
     */
    //Customer add screen button controller
    @FXML
    private void addCustomerButton(ActionEvent event) throws IOException {
        String customerName = cAddNameField.getText();
        String address = cAddAddressField.getText();
        String address2 = cAddAddress2Field.getText();
        String zip = cAddZipField.getText();
        String phone = cAddPhoneField.getText();
        String division = cAddDivisionComboBox.getValue();
        String country = cAddCountryComboBox.getValue();
        String error = isCustomerValid(customerName, address + address2, division, country, zip, phone);
        if(error.equals("")) {
            C.setCustomerName(customerName);
            C.setAddress(address);
            C.setAddress2(address2);
            C.setPostalCode(zip);
            C.setPhone(phone);
            C.setDivision(division);
            C.setCountry(country);
            DBManagement.addNewCustomer(customerName, address, address2, zip, phone, division, country);
            Parent addPartParent = FXMLLoader.load(getClass().getResource("HomeScreen.fxml"));
            Scene addPartScene = new Scene(addPartParent);
            Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            addPartStage.setScene(addPartScene);
            addPartStage.show();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initModality(Modality.NONE);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorCustomerFields"));
            alert.setContentText(rb.getString("errorCustomerFieldsMessage") + error);
            alert.showAndWait();
        }
    }
    /**
     * Method used to generate random numbers for the autofill field.
     * This is part of a method used in testing.
     * @param minNum Minimum number.
     * @param maxNum Maximum number.
     * @return Returns the random number as integer.
     */
    //Generate random number method for autofill button
    public int getRandomNumber(int minNum, int maxNum) {
        Random random = new Random();
        return random.nextInt(maxNum - minNum) + minNum;
    }
    /**
     * Applies the controls to the FXML autoFillButton button
     * Applies a set of values to the text field based on a random number generated
     * Use this to autofill a random customer for testing purposes
     * comment out the autoFill button in the customerAdd.FXML to remove the button
     */
    /* ----------------------------------------------------------------------------------------------------------------
    //comment this button out in the customerAdd.FXML if not wanted ---------------------------------------------------
    //Used to auto populate the fields for testing purposes -----------------------------------------------------------
      ---------------------------------------------------------------------------------------------------------------*/
    //autofill button controller
    @FXML
    private void autoFillButton() {
        //autofill fields based on random number
        //print out number generated if no positive comparison is met
        switch (getRandomNumber(1, 5)) {
            case 1 -> {
                cAddNameField.setText("Jack Nicholson");
                cAddAddressField.setText("1124 Palm Springs Dr.");
                cAddAddress2Field.setText("");
                cAddZipField.setText("11027");
                cAddPhoneField.setText("1-265-808-2500");
                cAddCountryComboBox.setValue("United States");
                cAddDivisionComboBox.setValue("Utah");
            }
            case 2 -> {
                cAddNameField.setText("Alice Cooper");
                cAddAddressField.setText("9724 South Applegate Way");
                cAddAddress2Field.setText("APT#112");
                cAddZipField.setText("88089");
                cAddPhoneField.setText("1-335-292-9902");
                cAddCountryComboBox.setValue("United States");
                cAddDivisionComboBox.setValue("Wyoming");
            }
            case 3 -> {
                cAddNameField.setText("Paul Smirhc");
                cAddAddressField.setText("A9940 Easton St");
                cAddAddress2Field.setText("");
                cAddZipField.setText("2027-33407");
                cAddPhoneField.setText("11212-1-44808-2500");
                cAddCountryComboBox.setValue("France");
                cAddDivisionComboBox.setValue("Hauts-de-France");
            }
            case 4 -> {
                cAddNameField.setText("Abigale Enirche");
                cAddAddressField.setText("E44014 Le Paris Way");
                cAddAddress2Field.setText("Suite:224");
                cAddZipField.setText("3304-27984");
                cAddPhoneField.setText("11212-1-26898-3335");
                cAddCountryComboBox.setValue("France");
                cAddDivisionComboBox.setValue("Bourgogne-Franche-Comté");
            }
            case 5 -> {
                cAddNameField.setText("Martin Schwarts");
                cAddAddressField.setText("3305 Cowboy Drive");
                cAddAddress2Field.setText("");
                cAddZipField.setText("65078");
                cAddPhoneField.setText("1-404-292-3356");
                cAddCountryComboBox.setValue("United States");
                cAddDivisionComboBox.setValue("Texas");
            }
            default ->
                System.out.println(getRandomNumber(1, 5));
        }
    }
    /**
     * runs the listed methods on the pages initial load
     */
    @FXML
    public void initialize() {
        onLoad();
    }
}
