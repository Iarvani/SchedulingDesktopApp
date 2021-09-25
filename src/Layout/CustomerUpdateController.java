package Layout;

import Utilitys.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import obj.Customer;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import static java.lang.Integer.parseInt;
import static obj.CustomerList.getCustomerRoster;
import static obj.DBManagement.*;

/** This class is used to control the customerUpdateFXML. */
public class CustomerUpdateController {

    DBConnection DB = new DBConnection();
    ResourceBundle rb = setResBundle();
    String countryBoxValue = "";
    String divisionBoxValue= "";
    @FXML private TextField cUpdateNameField;
    @FXML private TextField cUpdateIDField;
    @FXML private TextField cUpdatePhoneField;
    @FXML private TextField cUpdateZipField;
    @FXML private TextField cUpdateAddressField;
    @FXML private TextField cUpdateAddress2Field;
    @FXML private ComboBox<String> cUpdateDivisionBox;
    @FXML private ComboBox<String> cUpdateCountryBox;
    @FXML private Label cUpdateCountryLbl;
    @FXML private Label cUpdateAddressLbl;
    @FXML private Label cUpdateAddress2Lbl;
    @FXML private Label cUpdateNameLbl;
    @FXML private Label cUpdateDivisionLbl;
    @FXML private Label cUpdatePhoneLbl;
    @FXML private Label cUpdatePCodeLbl;
    @FXML private Label userLbl;
    @FXML private Label langLbl;
    @FXML private Label currentUserLbl;
    @FXML private Label currentLangLbl;
    @FXML private Label clientLbl;
    @FXML private Label cUpdateIDLbl;
    @FXML private Button custDeleteBtn;
    @FXML private Button custUpdateBtn;
    @FXML private Button custHomeBtn;
    @FXML private Button custExitBtn;
    @FXML private Button custClearBtn;
    @FXML private TableColumn <Customer, Number> cUpdateCustIDCol;
    @FXML private TableColumn <Customer, String> cUpdateCustNameCol;
    @FXML private TableColumn <Customer, String> cUpdateCustAddressCol;
    @FXML private TableColumn <Customer, String> cUpdateCustPhoneCol;
    @FXML private TableView<Customer> cUpdateTbl;
    /**
     * Applies the controls to the FXML exit button
     * Closes the program when the button is pressed.
     * Provides the user with a confirmation pop-up before program closes.
     */
    //Exit program button with confirmation popup
    @FXML
    private void exitButton() {
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
     * Applies the controls to the FXML customer update button.
     * Pulls the values from the user text fields and passes them to the modify customer method for
     * the necessary checks and values to update.
     * If successful it will then redirect the user to the home screen.
     * @throws IOException if there is an error loading the homeScreenFXML.
     * @param event Button is clicked.
     */
    //Customer update button controller
    @FXML
    public void updateButton(ActionEvent event) throws IOException {
        //use values in fields to update a customers current fields
        int id = parseInt(cUpdateIDField.getText());
        String name = cUpdateNameField.getText();
        String address = cUpdateAddressField.getText();
        String address2 = cUpdateAddress2Field.getText();
        String phone = cUpdatePhoneField.getText();
        String postalCode = cUpdateZipField.getText();
        String division = divisionBoxValue;
        String country = countryBoxValue;
        modifyCustomer(id, name, address, address2, division, country, postalCode, phone);
        Parent addPartParent = FXMLLoader.load(getClass().getResource("HomeScreen.fxml"));
        Scene addPartScene = new Scene(addPartParent);
        Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        addPartStage.setScene(addPartScene);
        addPartStage.show();
    }
    /**
     * Applies the controls to the FXML delete button.
     * pulls the values from the user text fields and pushes them to the delete customer method.
     * upon success it will redirect the user to the home screen.
     * @throws IOException if there is an error loading the homeScreenFXML.
     * @param event When button is clicked.
     */
    //Customer delete button
    @FXML
    public void deleteButton(ActionEvent event) throws IOException {
        //match selected customer from the array and then execute delete customer method
        String customerName = cUpdateNameField.getText();
        String Address = cUpdateAddressField.getText() + cUpdateAddress2Field.getText();
        deleteCustomer(customerName, Address);
        Parent addPartParent = FXMLLoader.load(getClass().getResource("HomeScreen.fxml"));
        Scene addPartScene = new Scene(addPartParent);
        Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        addPartStage.setScene(addPartScene);
        addPartStage.show();
    }
    /**
     * Applies an active listener on the table to detect a clicked item.
     * Uses the clicked item to pull the values and apply them to the current values area.
     * This allows a side by side view of what is already in the field and what you would like to change it to.
     * @param event When mouse button is clicked.
     */
    //mouse event to capture a customer selected from the table view
    @FXML
    public void clickItem(MouseEvent event)
    {
        if (event.getClickCount() == 1) //Checking click, assign values from clicked entry to current labels
        {
            String s=String.valueOf(cUpdateTbl.getSelectionModel().getSelectedItem().getCustomerId());
            cUpdateIDField.setText(s);
            cUpdateNameField.setText(cUpdateTbl.getSelectionModel().getSelectedItem().getCustomerName());
            cUpdateNameField.setDisable(false);
            cUpdateAddressField.setText(cUpdateTbl.getSelectionModel().getSelectedItem().getAddressId());
            cUpdateAddressField.setDisable(false);
            cUpdateAddress2Field.setDisable(false);
            cUpdatePhoneField.setText(cUpdateTbl.getSelectionModel().getSelectedItem().getPhone());
            cUpdatePhoneField.setDisable(false);
            cUpdateZipField.setText(cUpdateTbl.getSelectionModel().getSelectedItem().getPostalCode());
            cUpdateZipField.setDisable(false);
            cUpdateDivisionBox.setPromptText(cUpdateTbl.getSelectionModel().getSelectedItem().getDivision());
            cUpdateDivisionBox.setDisable(false);
            cUpdateCountryBox.setPromptText(cUpdateTbl.getSelectionModel().getSelectedItem().getCountry());
            cUpdateCountryBox.setDisable(false);
            countryBoxSelected();
        }
        countryBoxValue = cUpdateTbl.getSelectionModel().getSelectedItem().getCountry();
        divisionBoxValue = cUpdateTbl.getSelectionModel().getSelectedItem().getDivision();
    }
    /**
     * Pulls the values currently in the database to populate the country combo box
     * Allows the user to interact with the combo box after values are loaded.
     * Clears the values of the box if the other countries values will be loaded to the box
     */
    //Country box controller
    @FXML
    private void countryBoxSelected() {
        //set division/state combo box based on country selection
        try (DB.connect) {
            int countryID = 0;
            //if statements to determine the country selected to populate division combo box values
            if (cUpdateTbl.getSelectionModel().getSelectedItem().getCountry().equals("U.S") || countryBoxValue.equals("U.S")) {
                countryID = 1;
                cUpdateDivisionLbl.setText("State");
                cUpdateDivisionBox.setDisable(false);
                cUpdateDivisionBox.getItems().clear();
            }
            if (cUpdateTbl.getSelectionModel().getSelectedItem().getCountry().equals("UK") || countryBoxValue.equals("UK")) {
                countryID = 2;
                cUpdateDivisionLbl.setText("Region");
                cUpdateDivisionBox.setDisable(false);
                cUpdateDivisionBox.getItems().clear();
            }
            if (cUpdateTbl.getSelectionModel().getSelectedItem().getCountry().equals("Canada") || countryBoxValue.equals("Canada")) {
                countryID = 3;
                cUpdateDivisionLbl.setText("Providence");
                cUpdateDivisionBox.setDisable(false);
                cUpdateDivisionBox.getItems().clear();
            }
            //create the SQL query string
            String SQLStatement = "SELECT * FROM first_level_divisions WHERE COUNTRY_ID = '" + countryID + "'";
            PreparedStatement test = DBConnection.startConnection().prepareStatement(SQLStatement);
            ResultSet result = test.executeQuery();
            //increment through results and assign the names to the division combo box
            while (result.next()) {
                String dbDivision = result.getString(2);
                cUpdateDivisionBox.getItems().add(dbDivision);
            }
            System.out.println(countryBoxValue);
        }
        //catch error when associated with this method
        catch (SQLException e) {
            System.out.println("SQL Error - Populate DivBox: " + e);
        }
    }
    /**
     * Runs the above method if the country box's region is changed
     * I.E. U.S. is changed to U.K.
     * This changes the available city's shown within the combo box
     */
    //changes the values in division box and value of country box if it has been changed manually
    public void countryBoxChange() {
        countryBoxValue = cUpdateCountryBox.getValue();
        countryBoxSelected();
    }
    /**
     * Changes the value of the division when selected manually from the combo box.
     */
    //changes the value of the division if selected manually in box
    public void divisionBoxChange() {
        divisionBoxValue = cUpdateDivisionBox.getValue();
    }
    /**
     * Applies the controls to the FXML customer home screen button
     * Redirects the user to the customer home screen when the button is pressed.
     * @throws IOException if there is an error loading the customerHome.FXML
     */
    //Customer home screen button controller
    @FXML
    private void openCustomerHomeScreen(ActionEvent event) throws IOException {
        Parent addPartParent = FXMLLoader.load(getClass().getResource("CustomerHome.fxml"));
        Scene addPartScene = new Scene(addPartParent);
        Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        addPartStage.setScene(addPartScene);
        addPartStage.show();
    }
    /**
     * Applies the controls to the FXML clear button
     * Provides a confirmation pop-up
     * upon confirmation clears all the values currently typed into the fields
     */
    //customer clear button controller
    @FXML
    private void clearButton() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle(rb.getString("clear"));
        alert.setHeaderText(rb.getString("clearConfirm"));
        alert.setContentText(rb.getString("clearMessage"));
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            //set text fields values to empty
            cUpdateNameField.setText("");
            cUpdatePhoneField.setText("");
            cUpdateZipField.setText("");
            cUpdateAddressField.setText("");
            cUpdateDivisionBox.setValue("Select Division");
            cUpdateCountryBox.setValue("Select Country");
        } else {
            System.out.println("Cancelled.");
        }
    }
    /**
     * Uses the set lang string from loginController to pick the resource bundle
     * then applies the values in the resource bundle to the fields on the page
     */
    //set language and change text
    @FXML
    private void setLangTitles() {
        if(LoginController.getLang().equals("English")) {
            ResourceBundle rb = ResourceBundle.getBundle("Resources/nat_en");
            cUpdateDivisionBox.setPromptText(rb.getString("divisionBoxPrompt"));
            cUpdateCountryBox.setPromptText(rb.getString("countryBoxPrompt"));
            cUpdateNameField.setPromptText(rb.getString("nameFieldPrompt"));
            cUpdateIDLbl.setText(rb.getString("iDLbl"));
            cUpdateIDField.setPromptText(rb.getString("iDFieldPrompt"));
            cUpdatePhoneField.setPromptText(rb.getString("phoneFieldPrompt"));
            cUpdateZipField.setPromptText(rb.getString("pCodeFieldPrompt"));
            cUpdateAddressField.setPromptText(rb.getString("addressFieldPrompt"));
            cUpdateAddress2Field.setPromptText("address2Lbl");
            cUpdateCountryLbl.setText(rb.getString("countryLbl"));
            cUpdateAddressLbl.setText(rb.getString("addressLbl"));
            cUpdateAddress2Lbl.setText("address2Lbl");
            cUpdateNameLbl.setText(rb.getString("nameLbl"));
            cUpdateDivisionLbl.setText(rb.getString("divisionLbl"));
            cUpdatePhoneLbl.setText(rb.getString("phoneLbl"));
            cUpdatePCodeLbl.setText(rb.getString("pCodeLbl"));
            userLbl.setText(rb.getString("userLbl"));
            langLbl.setText(rb.getString("langLbl"));
            currentUserLbl.setText(getCurrentUser());
            currentLangLbl.setText(rb.getString("currentLangLbl"));
            clientLbl.setText(rb.getString("updateClientTitleLbl"));
            custDeleteBtn.setText(rb.getString("deleteBtn"));
            custUpdateBtn.setText(rb.getString("updateBtn"));
            custHomeBtn.setText(rb.getString("homeBtn"));
            custExitBtn.setText(rb.getString("exitBtn"));
            custClearBtn.setText(rb.getString("clearBtn"));
            cUpdateCustIDCol.setText(rb.getString("iDCol"));
            cUpdateCustNameCol.setText(rb.getString("nameCol"));
            cUpdateCustAddressCol.setText(rb.getString("addressCol"));
            cUpdateCustPhoneCol.setText(rb.getString("phoneCol"));
        }
        else if(LoginController.getLang().equals("French")) {
            ResourceBundle rb = ResourceBundle.getBundle("Resources/nat_fr");
            cUpdateDivisionBox.setPromptText(rb.getString("divisionBoxPrompt"));
            cUpdateCountryBox.setPromptText(rb.getString("countryBoxPrompt"));
            cUpdateNameField.setPromptText(rb.getString("nameFieldPrompt"));
            cUpdateIDLbl.setText(rb.getString("iDLbl"));
            cUpdateIDField.setPromptText(rb.getString("iDFieldPrompt"));
            cUpdatePhoneField.setPromptText(rb.getString("phoneFieldPrompt"));
            cUpdateZipField.setPromptText(rb.getString("pCodeFieldPrompt"));
            cUpdateAddressField.setPromptText(rb.getString("addressFieldPrompt"));
            cUpdateAddress2Field.setPromptText("address2Lbl");
            cUpdateCountryLbl.setText(rb.getString("countryLbl"));
            cUpdateAddressLbl.setText(rb.getString("addressLbl"));
            cUpdateAddress2Lbl.setText("address2Lbl");
            cUpdateNameLbl.setText(rb.getString("nameLbl"));
            cUpdateDivisionLbl.setText(rb.getString("divisionLbl"));
            cUpdatePhoneLbl.setText(rb.getString("phoneLbl"));
            cUpdatePCodeLbl.setText(rb.getString("pCodeLbl"));
            userLbl.setText(rb.getString("userLbl"));
            langLbl.setText(rb.getString("langLbl"));
            currentUserLbl.setText(getCurrentUser());
            currentLangLbl.setText(rb.getString("currentLangLbl"));
            clientLbl.setText(rb.getString("updateClientTitleLbl"));
            custDeleteBtn.setText(rb.getString("deleteBtn"));
            custUpdateBtn.setText(rb.getString("updateBtn"));
            custHomeBtn.setText(rb.getString("homeBtn"));
            custExitBtn.setText(rb.getString("exitBtn"));
            custClearBtn.setText(rb.getString("clearBtn"));
            cUpdateCustIDCol.setText(rb.getString("iDCol"));
            cUpdateCustNameCol.setText(rb.getString("nameCol"));
            cUpdateCustAddressCol.setText(rb.getString("addressCol"));
            cUpdateCustPhoneCol.setText(rb.getString("phoneCol"));
        }
    }
    /**
     * updates the customer observable list and uses the values in the roster
     * to populate the customer update table view
     */
    //update values to populate the table view
    public void updateCustomerTableView() {
        updateCustomerRoster();
        cUpdateTbl.setItems(getCustomerRoster());
    }
    /** About Lambda Discussion
     * The 4 lambdas in this method I decided to implement the lambda format for the JavaFX cell factory values.
     * The reasoning behind this decision was to reduce the amount of methods that were needed to created the same result.
     * This is a more simplified version of the Lambda expression, the previous version of the expression would have needed
     * to declare the return value and be created as an inner class. We could implement the long version of the lambda by
     * writing it out as - tableIDCol.setCellValueFactory(cellData -> {
     *             return cellData.getValue().appointmentIdProperty();
     *         });
     * With using the simplified Lambda syntax we are not only making the code easier to follow tableIDCol will use
     * the setCellValueFactoryMethod to apply the getValue().SomeGetter() to the tableID column. Furthermore by implementing the Lambda
     * expression we save ourselves from having an addition method creation like -
     * public ObservableValue<String> call(CellDataFeatures<Customer, String> p) {
     *          // p.getValue() returns the value instance for a particular TableView row
     *          return p.getValue().customerNameProperty();
     *      }
     *   });
     * for each of our table columns saving us almost 50 lines of code per FXML controller class.
     *<br><br>
     *
     * Pulls the current user from the DB for display
     * Uses the set lang titles method to translate the information on the page
     * Populates the tableview with values in the roster
     * updates the roster in the observable list, and runs the login notification check
     */
    //items to execute on screens initial load
    public void onLoad() {
        currentUserLbl.setText(getCurrentUser());
        setLangTitles();
        cUpdateCustIDCol.setCellValueFactory(cellData -> cellData.getValue().customerIdProperty());
        cUpdateCustNameCol.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        cUpdateCustAddressCol.setCellValueFactory(cellData -> cellData.getValue().getAddressConversion());
        cUpdateCustPhoneCol.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        //adds country values to country box
        cUpdateCountryBox.getItems().addAll(
                "U.S",
                "UK",
                "Canada");
        // Update table view
        updateCustomerTableView();
        // Create appointment notifications if any need to be shown
        logInAppointmentNotification();
    }
    /**
     * Runs the onLoad method when the page is loaded
     */
    @FXML
    private void initialize() { onLoad(); }
}
