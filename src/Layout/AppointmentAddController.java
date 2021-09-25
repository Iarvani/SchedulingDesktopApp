package Layout;

import Utilitys.DBConnection;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import obj.Customer;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.Date;

import static Layout.LoginController.getLang;
import static obj.CustomerList.getCustomerRoster;
import static obj.DBManagement.*;

/** This class is used to control the appointmentAddFXML. */
public class AppointmentAddController {

    DBConnection DB = new DBConnection();
    ResourceBundle rb = setResBundle();
    @FXML private TextField appAddTitleField;
    @FXML private TextField appAddLocationField;
    @FXML private TextField appAddTypeField;
    @FXML private TextField appAddStartTimeField;
    @FXML private TextField appAddEndTimeField;
    @FXML private TextArea appAddDescriptionField;
    @FXML private TextField appAddIDField;
    @FXML private ComboBox<String> appAddCustomerBox;
    @FXML private ComboBox<String> appAddContactBox;
    @FXML private ComboBox<String> userComboBox;
    @FXML private Label appAddSchedLbl;
    @FXML private Label userLbl;
    @FXML private Label currentLangLbl;
    @FXML private Label currentUserLbl;
    @FXML private Label langLbl;
    @FXML private Label appAddAppIDLbl;
    @FXML private Label appAddTitleLbl;
    @FXML private Label appAddDescriptionLbl;
    @FXML private Label appAddLocationLbl;
    @FXML private Label appAddContactLbl;
    @FXML private Label appAddTypeLbl;
    @FXML private Label appAddEndTimeLbl;
    @FXML private Label appAddStartTimeLbl;
    @FXML private Label userComboLbl;
    @FXML private Button appAddBtn;
    @FXML private Button appAddClearBtn;
    @FXML private Button appAddHomeBtn;
    @FXML private Button appAddExitBtn;

    private final ObservableList<Customer> currentCustomers = getCustomerRoster();

    /**
     * Assigns controls to the FXML exit button
     * exits the program when the button is pressed
     * Provides the user with a confirmation pop-up before exiting
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
     * Assigns controls to the FXML appointment home screen button
     * Redirects the user to the appointment home screen when pressed
     * @throws IOException if loading Scene/Resources occurs
     */
    //Appointment home screen button control
    @FXML
    private void openAppointmentHomeScreen(ActionEvent event) throws IOException {
        Parent addPartParent = FXMLLoader.load(getClass().getResource("AppointmentHome.fxml"));
        Scene addPartScene = new Scene(addPartParent);
        Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        addPartStage.setScene(addPartScene);
        addPartStage.show();
    }
    /**
     * Assigns controls to the FXML clear button
     * provides the user with a confirmation pop-up
     * if confirmed, reset the values of all user text areas to blank
     */
    //clear button controller
    @FXML
    private void clearButton() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle(rb.getString("clear"));
        alert.setHeaderText(rb.getString("clearConfirm"));
        alert.setContentText(rb.getString("clearMessage"));
        Optional<ButtonType> result = alert.showAndWait();
        //reset the fields values to empty
        if (result.isPresent() && result.get() == ButtonType.OK) {
            appAddTitleField.setText("");
            appAddLocationField.setText("");
            appAddTypeField.setText("");
            appAddDescriptionField.setText("");
            appAddContactBox.setValue("Select Contact");
            appAddStartTimeField.setPromptText("Enter Start Time");
            appAddEndTimeField.setPromptText("Enter End Time");
        } else {
            System.out.println("Cancelled.");
        }
    }
    /**
     * Assigns controls to the FXML add appointment button
     * Loops through the observable list to fill the customer combo box values
     * Checks the start/end times with the dateChecker and timeChecker methods
     * then pushed the values to the addAppointment method.
     * Provides the user with a information pop-up that add has succeeded
     * then redirects the user to the appointment home screen
     * @throws ParseException if string to number conversion error occurs
     */
    //appointment add button controller
    @FXML
    private void addAppointmentButton(ActionEvent event) throws ParseException {
        Customer customer = null;
        Date startLocal;
        Date endLocal;
        //set the format for the time string to mutate into timestamp
        SimpleDateFormat localDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //set timezone to user timezone
        localDateFormat.setTimeZone(TimeZone.getDefault());

            //get values from fields to create appointment
            String cust = appAddCustomerBox.getValue();
            String contact = appAddContactBox.getValue();
            for (Customer currentCustomer : currentCustomers) {
                if (currentCustomer.getCustomerName().equals(cust)) {
                    customer = currentCustomer;
                }
            }
            String title = appAddTitleField.getText();
            String description = appAddDescriptionField.getText();
            String type = appAddTypeField.getText();
            String location = appAddLocationField.getText();
            String user = userComboBox.getValue();
            String startTime = appAddStartTimeField.getText();
            String endTime = appAddEndTimeField.getText();
            if(user == null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(rb.getString("error"));
                alert.setHeaderText(rb.getString("errorAddingAppointment"));
                alert.setContentText(rb.getString("AppointmentUserError"));
                alert.setContentText(rb.getString("AppointmentUserErrorMessage"));
                alert.showAndWait();
            }
            else if (!dateMatcher(startTime)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(rb.getString("errorModifyingAppointment"));
                alert.setHeaderText(rb.getString("AppointmentTimeError"));
                alert.setContentText(rb.getString("AppointmentTimeFormat"));
                alert.setContentText(rb.getString("AppointmentTimeSyntax"));
                alert.showAndWait();

            }
            else if (!dateMatcher(endTime)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(rb.getString("errorModifyingAppointment"));
                alert.setHeaderText(rb.getString("AppointmentTimeError"));
                alert.setContentText(rb.getString("AppointmentTimeFormat"));
                alert.setContentText(rb.getString("AppointmentTimeSyntax"));
                alert.showAndWait();

            }
            else {
                startLocal = localDateFormat.parse(startTime);
                endLocal = localDateFormat.parse(endTime);
                //convert the start/end times to UTC
                ZonedDateTime startUTC = ZonedDateTime.ofInstant(startLocal.toInstant(), ZoneId.of("UTC"));
                ZonedDateTime endUTC = ZonedDateTime.ofInstant(endLocal.toInstant(), ZoneId.of("UTC"));
                //add appointment with new values, confirm with popup for success and redirect to home screen
                if (timeChecker(startUTC, endUTC)) {
                    try {
                        addNewAppointment(customer, title, type, description, location, contact, user, startUTC, endUTC);
                        Parent addPartParent = FXMLLoader.load(getClass().getResource("HomeScreen.fxml"));
                        Scene addPartScene = new Scene(addPartParent);
                        Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        addPartStage.setScene(addPartScene);
                        addPartStage.show();
                    } catch (SQLException | IOException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
    }
    /**
     * Uses the set lang string to select the appropriate resource bundle
     * Uses the resource bundle to assign the proper values to all text elements on the page.
     */
    //set language titles
    @FXML
    private void setLangTitles() {
            appAddTitleField.setPromptText(rb.getString("titleFieldPrompt"));
            appAddLocationField.setPromptText(rb.getString("locationFieldPrompt"));
            appAddTypeField.setPromptText(rb.getString("typeFieldPrompt"));
            appAddDescriptionField.setPromptText(rb.getString("descriptionFieldPrompt"));
            appAddIDField.setPromptText(rb.getString("iDFieldPrompt"));
            appAddContactBox.setPromptText(rb.getString("contactBoxPrompt"));
            appAddStartTimeField.setPromptText(rb.getString("startTimeFieldPrompt"));
            appAddEndTimeField.setPromptText(rb.getString("endTimeFieldPrompt"));
            appAddSchedLbl.setText(rb.getString("addAppTitleLbl"));
            userLbl.setText(rb.getString("userLbl"));
            currentLangLbl.setText(getLang());
            currentUserLbl.setText(getCurrentUser());
            langLbl.setText(rb.getString("langLbl"));
            appAddAppIDLbl.setText(rb.getString("iDLbl"));
            appAddTitleLbl.setText(rb.getString("titleLbl"));
            appAddDescriptionLbl.setText(rb.getString("descriptionLbl"));
            appAddLocationLbl.setText(rb.getString("locationLbl"));
            appAddContactLbl.setText(rb.getString("contactLbl"));
            appAddTypeLbl.setText(rb.getString("typeLbl"));
            appAddEndTimeLbl.setText(rb.getString("endTimeLbl"));
            appAddStartTimeLbl.setText(rb.getString("startTimeLbl"));
            appAddBtn.setText(rb.getString("addBtn"));
            appAddClearBtn.setText(rb.getString("clearBtn"));
            appAddHomeBtn.setText(rb.getString("homeBtn"));
            appAddExitBtn.setText(rb.getString("exitBtn"));
    }
    /**
     * Gets the lang string to assign the proper resource bundle
     * Updates the customer roster, fills the combo box with entries from the customer roster
     * then runs the login notification check
     */
    //Items to execute when screen first loads
    private void onLoad() {
        setLangTitles();
        updateCustomerRoster();
        try (DB.connect) {
            currentUserLbl.setText(getCurrentUser());
            String SQLStatement = "SELECT * FROM customers";
            PreparedStatement test = DBConnection.startConnection().prepareStatement(SQLStatement);
            ResultSet result = test.executeQuery();
            //increment through results and assign the names to the customer combo box
            while (result.next()) {
                String dbCustomer = result.getString(2);
                appAddCustomerBox.getItems().add(dbCustomer);
            }
            SQLStatement = "SELECT * FROM contacts";
            test = DBConnection.startConnection().prepareStatement(SQLStatement);
            result = test.executeQuery();
            //increment through results and assign the names to the contact combo box
            while (result.next()) {
                String dbContact = result.getString(2);
                appAddContactBox.getItems().add(dbContact);
            }
            SQLStatement = "SELECT * FROM users";
            test = DBConnection.startConnection().prepareStatement(SQLStatement);
            result = test.executeQuery();
            //increment through results and assign the names to the contact combo box
            while (result.next()) {
                String dbContact = result.getString(2);
                userComboBox.getItems().add(dbContact);
            }
        }
        catch (SQLException e) {
            System.out.println("Contact Box Error: " + e);
        }
    }
    /**
     * Runs listed methods for the pages initial load
     */
    @FXML
    private void initialize() { onLoad(); }
}
