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
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import obj.Appointment;
import obj.Customer;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;
import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;
import static obj.AppList.getAppointmentList;
import static obj.CustomerList.getCustomerRoster;
import static obj.DBManagement.*;

/** This class is used to control the appointmentUpdateFXML. */
public class AppointmentUpdateController {

    DBConnection DB = new DBConnection();
    ResourceBundle rb = setResBundle();

    @FXML private TextField locationField;
    @FXML private TextField typeField;
    @FXML private TextField titleField;
    @FXML private TextField endTimeField;
    @FXML private TextField startTimeField;
    @FXML private TextArea descriptionField;
    @FXML private TableView <Appointment> appTbl;
    @FXML private TableColumn <Appointment, Number> appIdCol;
    @FXML private TableColumn <Appointment, String> appTitleCol;
    @FXML private TableColumn <Appointment, String> appDescCol;
    @FXML private TableColumn <Appointment, String> appLocCol;
    @FXML private TableColumn <Appointment, String> appContactCol;
    @FXML private TableColumn <Appointment, String> appTypeCol;
    @FXML private TableColumn <Appointment, String> appStartTimeCol;
    @FXML private TableColumn <Appointment, String> appEndTimeCol;
    @FXML private TableColumn <Appointment, String> appDateCol;
    @FXML private TableColumn <Appointment, Number> appCustIdCol;
    @FXML private Button exitBtn;
    @FXML private Button homeBtn;
    @FXML private Button deleteBtn;
    @FXML private Button clearBtn;
    @FXML private Button updateBtn;
    @FXML private ComboBox<String> contactBox;
    @FXML private ComboBox<String> customerBox;
    @FXML private ComboBox<String> userComboBox;
    @FXML private Label customerLbl;
    @FXML private Label curCustomerLbl;
    @FXML private Label infoUpdateLbl;
    @FXML private Label deleteInstructLbl;
    @FXML private Label instructionsLbl;
    @FXML private Label endTimeLbl;
    @FXML private Label startTimeLbl;
    @FXML private Label typeLbl;
    @FXML private Label contactLbl;
    @FXML private Label locationLbl;
    @FXML private Label descriptionLbl;
    @FXML private Label titleLbl;
    @FXML private Label iDLbl;
    @FXML private Label userLbl;
    @FXML private Label currentUserLbl;
    @FXML private Label langLbl;
    @FXML private Label currentLangLbl;
    @FXML private Label updateAppTitleLbl;
    @FXML private Label curAppIdLbl;
    @FXML private Label curTitleLbl;
    @FXML private Label curDescLbl;
    @FXML private Label curLocLbl;
    @FXML private Label curContactLbl;
    @FXML private Label curTypeLbl;
    @FXML private Label curStartTimeLbl;
    @FXML private Label curEndTimeLbl;
    @FXML private Label displayCurUserLbl;
    @FXML private Label displayUserLbl;

    private final ObservableList<Appointment> currentAppointments = getAppointmentList();
    private final ObservableList<Customer> currentCustomers = getCustomerRoster();
    private static PreparedStatement test;
    /**
     * Assigns control to the FXML exit button
     * Exits the program when the button is pressed
     * Provides a confirmation pop-up before program exit
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
     * Uses the set lang string from the login controller to pick the correct resource bundle
     * Uses the resource bundle to translate all the text elements on the page
     */
    //Set language and change text
    @FXML
    private void setLangTitles() {
        if (LoginController.getLang().equals("English")) {
            ResourceBundle rb = ResourceBundle.getBundle("Resources/nat_en");
            locationField.setPromptText(rb.getString("locationFieldPrompt"));
            typeField.setPromptText(rb.getString("typeFieldPrompt"));
            titleField.setPromptText(rb.getString("titleFieldPrompt"));
            descriptionField.setPromptText(rb.getString("descriptionFieldPrompt"));
            appIdCol.setText(rb.getString("tableAppIdCol"));
            appLocCol.setText(rb.getString("tableLocationCol"));
            appTitleCol.setText(rb.getString("tableTitleCol"));
            appDescCol.setText(rb.getString("tableDescriptionCol"));
            appContactCol.setText(rb.getString("tableContactCol"));
            appStartTimeCol.setText(rb.getString("tableStartTimeCol"));
            appEndTimeCol.setText(rb.getString("tableEndTimeCol"));
            appTypeCol.setText(rb.getString("tableTypeCol"));
            appCustIdCol.setText(rb.getString("tableCustomerIdCol"));
            exitBtn.setText(rb.getString("exitBtn"));
            homeBtn.setText(rb.getString("homeBtn"));
            deleteBtn.setText(rb.getString("deleteBtn"));
            clearBtn.setText(rb.getString("clearBtn"));
            updateBtn.setText(rb.getString("updateBtn"));
            contactBox.setPromptText(rb.getString("contactBoxPrompt"));
            customerBox.setPromptText(rb.getString("customerBoxPrompt"));
            startTimeField.setPromptText(rb.getString("startTimeFieldPrompt"));
            endTimeField.setPromptText(rb.getString("endTimeFieldPrompt"));
            customerLbl.setText(rb.getString("customerLbl"));
            curCustomerLbl.setText(rb.getString("currentCustomerLbl"));
            infoUpdateLbl.setText(rb.getString("infoUpdateLbl"));
            deleteInstructLbl.setText(rb.getString("deleteInstructLbl"));
            instructionsLbl.setText(rb.getString("instructionsLbl"));
            endTimeLbl.setText(rb.getString("endTimeLbl"));
            startTimeLbl.setText(rb.getString("startTimeLbl"));
            typeLbl.setText(rb.getString("typeLbl"));
            contactLbl.setText(rb.getString("contactLbl"));
            locationLbl.setText(rb.getString("locationLbl"));
            descriptionLbl.setText(rb.getString("descriptionLbl"));
            titleLbl.setText(rb.getString("titleLbl"));
            iDLbl.setText(rb.getString("appIDLbl"));
            userLbl.setText(rb.getString("userLbl"));
            currentUserLbl.setText(rb.getString("currentUserLbl"));
            langLbl.setText(rb.getString("langLbl"));
            currentLangLbl.setText(rb.getString("currentLangLbl"));
            updateAppTitleLbl.setText(rb.getString("updateAppTitleLbl"));
            //displayUserLbl.setText(rb.getString("currentUserLbl") + (": "));
        }
        else if (LoginController.getLang().equals("French")) {
            ResourceBundle rb = ResourceBundle.getBundle("Resources/nat_fr");
            locationField.setPromptText(rb.getString("locationFieldPrompt"));
            typeField.setPromptText(rb.getString("typeFieldPrompt"));
            titleField.setPromptText(rb.getString("titleFieldPrompt"));
            descriptionField.setPromptText(rb.getString("descriptionFieldPrompt"));
            appIdCol.setText(rb.getString("appIDLbl"));
            appLocCol.setText(rb.getString("tableLocationCol"));
            appTitleCol.setText(rb.getString("tableTitleCol"));
            appDescCol.setText(rb.getString("tableDescriptionCol"));
            appContactCol.setText(rb.getString("tableContactCol"));
            appStartTimeCol.setText(rb.getString("tableStartTimeCol"));
            appEndTimeCol.setText(rb.getString("tableEndTimeCol"));
            appTypeCol.setText(rb.getString("tableTypeCol"));
            appCustIdCol.setText(rb.getString("tableCustomerIdCol"));
            exitBtn.setText(rb.getString("exitBtn"));
            homeBtn.setText(rb.getString("homeBtn"));
            deleteBtn.setText(rb.getString("deleteBtn"));
            clearBtn.setText(rb.getString("clearBtn"));
            updateBtn.setText(rb.getString("updateBtn"));
            contactBox.setPromptText(rb.getString("contactBoxPrompt"));
            customerBox.setPromptText(rb.getString("customerBoxPrompt"));
            startTimeField.setPromptText(rb.getString("startTimeFieldPrompt"));
            endTimeField.setPromptText(rb.getString("endTimeFieldPrompt"));
            customerLbl.setText(rb.getString("customerLbl"));
            curCustomerLbl.setText(rb.getString("currentCustomerLbl"));
            infoUpdateLbl.setText(rb.getString("infoUpdateLbl"));
            deleteInstructLbl.setText(rb.getString("deleteInstructLbl"));
            instructionsLbl.setText(rb.getString("instructionsLbl"));
            endTimeLbl.setText(rb.getString("endTimeLbl"));
            startTimeLbl.setText(rb.getString("startTimeLbl"));
            typeLbl.setText(rb.getString("typeLbl"));
            contactLbl.setText(rb.getString("contactLbl"));
            locationLbl.setText(rb.getString("locationLbl"));
            descriptionLbl.setText(rb.getString("descriptionLbl"));
            titleLbl.setText(rb.getString("titleLbl"));
            iDLbl.setText(rb.getString("appIDLbl"));
            userLbl.setText(rb.getString("userLbl"));
            currentUserLbl.setText(rb.getString("currentUserLbl"));
            langLbl.setText(rb.getString("langLbl"));
            currentLangLbl.setText(rb.getString("currentLangLbl"));
            updateAppTitleLbl.setText(rb.getString("updateAppTitleLbl"));
            //displayUserLbl.setText(rb.getString("currentUserLbl") + (": "));
        }
    }
    /**
     * Assigns controls to the FXML appointment home screen.
     * redirects the user to the homeScreen.FXML when pressed
     * @throws IOException if loading Scene/Resources occurs
     */
    //Appointment home screen button controller
    @FXML
    private void openAppointmentHomeScreen(ActionEvent event) throws IOException {
        Parent addPartParent = FXMLLoader.load(getClass().getResource("AppointmentHome.fxml"));
        Scene addPartScene = new Scene(addPartParent);
        Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        addPartStage.setScene(addPartScene);
        addPartStage.show();
    }
    /**
     * Assigns control to the FXML clear button
     * Provides a confirmation pop-up to the user
     * If accepted, clears the values in all of the user text areas
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
        //set values in fields to empty
        if (result.isPresent() && result.get() == ButtonType.OK) {
            titleField.setText("");
            locationField.setText("");
            typeField.setText("");
            descriptionField.setText("");
            contactBox.setValue("Select Contact");
            startTimeField.setPromptText("Select Start Time");
            endTimeField.setPromptText("Select End Time");
        } else {
            System.out.println("Cancelled.");
        }
    }
    /**
     * Assigns control to the FXML delete appointment button
     * Uses the value in the curAppIdLbl to select the proper appointment from the observable list
     * Provides the user with a confirmation pop-up before deletion
     * Then passes the current appointment values to the deleteAppointment method
     * Gives an information pop-up that informs the user the deletion was successful
     * Then redirects the user to the home screen
     * @throws IOException if loading Scene/Resources occurs
     */
    //delete appointment button controller
    @FXML
    private void deleteAppointmentButton(ActionEvent event) throws IOException {
        Appointment appointment = null;
        //loop through appointments in object list
        for (Appointment currentAppointments : currentAppointments) {
            //match selected appointments id from id in object list
            if (currentAppointments.getAppointmentId() == parseInt((curAppIdLbl.getText()))) {
                appointment = currentAppointments;
            }
        }
        if (appointment != null) {
           deleteAppointment(appointment);
           Alert alert = new Alert(Alert.AlertType.INFORMATION);
           alert.initModality(Modality.NONE);
           alert.setTitle(rb.getString("Success"));
           alert.setHeaderText(rb.getString("AppDelete"));
           alert.setContentText(rb.getString("AppDeleteText") + "\n" + rb.getString("appLbl") + "#" + appointment.getAppointmentId() + " of Type: " + appointment.getType() + " has been deleted.");
           alert.showAndWait();
           Parent addPartParent = FXMLLoader.load(getClass().getResource("HomeScreen.fxml"));
           Scene addPartScene = new Scene(addPartParent);
           Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
           addPartStage.setScene(addPartScene);
           addPartStage.show();
        }
        else {
            System.out.println("Cancelled.");
        }
    }
    /**
     * Assigns controls to the FXML update appointment button
     * Uses a information pop-up if the user clicks modify without selecting an appointment from the table view
     * Once an appointment is selected this method will fill the Labels with the appointments current information
     * The user can then enter the information they wish to change on the right text areas
     * While changing the start time and end time fields the method will check the values through the dateChecker, timeChecker methods
     * then the inserted values are passed to the modify appointment method
     * @throws ParseException if the conversion between strings and /zonedDateTimes occurs, also catches the SQL exception thrown by DB.Connect
     */
    //Appointment update screen button controller
    @FXML
    private void updateAppointmentButton(ActionEvent event) throws ParseException {
        Customer customer = null;
        Date startLocal;
        Date endLocal;
        ZonedDateTime startUTC = null;
        ZonedDateTime endUTC = null;
        String userSelect = userComboBox.getValue();

        int appointmentId = 0;
        //set format for start/end strings
        SimpleDateFormat localDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //set time to users times
        localDateFormat.setTimeZone(TimeZone.getDefault());
        //set selected appointment field values
        if (curAppIdLbl.getText().equals("") || curAppIdLbl.getText().equals("ID here")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initModality(Modality.NONE);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorModifyingAppointment"));
            alert.setContentText(rb.getString("AppointmentToModify"));
            alert.showAndWait();
            return;
        }
        else {
            try {
                appointmentId = Integer.parseInt(curAppIdLbl.getText());
            }
            catch (NumberFormatException e) {
                System.out.println("Appointment ID conversion error: " + e);
            }
            String cust = curCustomerLbl.getText();
            //set current contact as name rather than id
            int contactId;
            int user = parseInt(curAppIdLbl.getText());
            try (DB.connect) {

                ResultSet getContactId = test.executeQuery("SELECT Contact_ID, Customer_ID from appointments WHERE Appointment_ID = '" + parseInt(curAppIdLbl.getText()) + "'");
                getContactId.next();

                //increment through results and find name in contact box to convert to contact ID
                contactId = getContactId.getInt(1);

                for (Customer currentCustomer : currentCustomers) {
                    //if get cust name  = integer do . . .
                    //must be if custID = cust
                    if (currentCustomer.getCustomerId() == parseInt(cust)) {
                        customer = currentCustomer;
                    }
                }
                if(userSelect != null) {
                    user=getUserId(userSelect);
                }

                String title = titleField.getText();
                String description = descriptionField.getText();
                String type = typeField.getText();
                String location = locationField.getText();
                String startTime = startTimeField.getText();
                String endTime = endTimeField.getText();

                //check if start time field has a date entry and is valid format , if not assign it the current start times from selected entry
                if (!startTime.equals("") && dateMatcher(startTime)) {
                    startLocal = localDateFormat.parse(startTime);
                    startUTC = ZonedDateTime.ofInstant(startLocal.toInstant(), ZoneId.of("UTC"));
                }
                else if (!dateMatcher(startTime) && !startTime.equals("")) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(rb.getString("errorModifyingAppointment"));
                    alert.setHeaderText(rb.getString("AppointmentTimeError"));
                    alert.setContentText(rb.getString("AppointmentTimeFormat"));
                    alert.setContentText(rb.getString("AppointmentTimeSyntax"));
                    alert.showAndWait();
                    return;
                }
                if (!endTime.equals("") && dateMatcher(endTime)) {
                    endLocal = localDateFormat.parse(endTime);
                    endUTC = ZonedDateTime.ofInstant(endLocal.toInstant(), ZoneId.of("UTC"));
                }
                //check if end time field has a date entry and is valid format , if not assign it the current end times from selected entry
                else if (!dateMatcher(endTime) && !endTime.equals("")) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(rb.getString("errorModifyingAppointment"));
                    alert.setHeaderText(rb.getString("AppointmentTimeError"));
                    alert.setContentText(rb.getString("AppointmentTimeFormat"));
                    alert.setContentText(rb.getString("AppointmentTimeSyntax"));
                    alert.showAndWait();
                    return;
                }

                if (!startTimeField.getText().equals("") && !endTimeField.getText().equals("")) {
                    assert startUTC != null;
                    assert endUTC != null;
                    if (timeChecker(startUTC, endUTC)) {
                        modifyAppointment(appointmentId, customer, title, type, description, location, contactId, user, startUTC, endUTC);
                        Parent addPartParent = FXMLLoader.load(getClass().getResource("HomeScreen.fxml"));
                        Scene addPartScene = new Scene(addPartParent);
                        Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        addPartStage.setScene(addPartScene);
                        addPartStage.show();
                        return;
                    }
                }
                else if (startTimeField.getText().equals("") && !endTimeField.getText().equals("") ||
                        !startTimeField.getText().equals("") && endTimeField.getText().equals("")) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(rb.getString("errorModifyingAppointment"));
                    alert.setHeaderText(rb.getString("AppointmentTimeError"));
                    alert.setContentText(rb.getString("AppointmentTimeUpdate"));
                    alert.setContentText(rb.getString("AppointmentBothTimes"));
                    alert.showAndWait();
                    return;
                }
                //System.out.println("Before attempt to modify: " + startUTC + " " + endUTC + " Values before UTC: " + startLocal + " " + endLocal);
                if (startTimeField.getText().equals("") && endTimeField.getText().equals("")) {
                    modifyAppointment(appointmentId, customer, title, type, description, location, contactId, user, startUTC, endUTC);
                    Parent addPartParent = FXMLLoader.load(getClass().getResource("HomeScreen.fxml"));
                    Scene addPartScene = new Scene(addPartParent);
                    Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    addPartStage.setScene(addPartScene);
                    addPartStage.show();
                }
            } catch (SQLException | IOException e) {
                System.out.println("Conversion error in update appointment controller." + e);
            }
        }
        updateAppointmentList();
    }
    /**
     * Assigns an active mouse event listener to the table view
     * When the user clicks an appointment from the table view, it will use the tableviews values to fill
     * the labels on the left with the selected appointment.
     * Then it will unlock use of the users text areas to add values they wish to change.
     * catches an SQL exception thrown by DB.connect
     */
    //Click event when appointment is selected from the table view
    @FXML
    private void clickAppointment(MouseEvent event) {
        String curContactName = null;
        String curCustomerId = valueOf(appTbl.getSelectionModel().getSelectedItem().getCustomerId());
        if (event.getClickCount() == 1) //Checking click, applying values to current appointment labels
        {
            customerBox.getItems().clear();
            contactBox.getItems().clear();
            userComboBox.getItems().clear();
            curAppIdLbl.setText(valueOf(appTbl.getSelectionModel().getSelectedItem().getAppointmentId()));
            curLocLbl.setText(appTbl.getSelectionModel().getSelectedItem().getLocation());
            locationField.setDisable(false);
            curTypeLbl.setText(appTbl.getSelectionModel().getSelectedItem().getType());
            typeField.setDisable(false);
            curTitleLbl.setText(appTbl.getSelectionModel().getSelectedItem().getTitle());
            titleField.setDisable(false);
            curEndTimeLbl.setText(appTbl.getSelectionModel().getSelectedItem().getEndString());
            endTimeField.setDisable(false);
            curStartTimeLbl.setText(appTbl.getSelectionModel().getSelectedItem().getStartString());
            startTimeField.setDisable(false);
            curContactLbl.setText(valueOf(appTbl.getSelectionModel().getSelectedItem().getContact()));
            //displayCurUserLbl.setText(appTbl.getSelectionModel().getSelectedItem().getUserId());
            //System.out.println(appTbl.getSelectionModel().getSelectedItem().getContact());
            try (DB.connect) {
                //currentUserLbl.setText(DBManage.getCurrentUser());
                String SQLStatement = "SELECT * FROM contacts";
                test = DBConnection.startConnection().prepareStatement(SQLStatement);
                ResultSet result = test.executeQuery();
                //increment through results and assign the names to the contact combo box and convert contact Id into name for current customer label
                while (result.next()) {
                    if(parseInt(curContactLbl.getText()) == result.getInt(1)) {
                        curContactName = result.getString(2);
                    }
                    String dbContact = result.getString(2);
                    contactBox.getItems().add(dbContact);
                }
                //increment through results and assign names to the customer combo box
                SQLStatement = "SELECT * FROM customers";
                test = DBConnection.startConnection().prepareStatement(SQLStatement);
                result = test.executeQuery();
                while (result.next()) {
                    String dbCustomer = result.getString(2);
                    customerBox.getItems().add(dbCustomer);
                }
                SQLStatement = "SELECT * FROM users";
                test = DBConnection.startConnection().prepareStatement(SQLStatement);
                result = test.executeQuery();
                while (result.next()) {
                    String dbCustomer = result.getString(2);
                    userComboBox.getItems().add(dbCustomer);
                }
            }
            catch (SQLException throwables) {
                System.out.println("Contact Box Error.");

            }
            curCustomerLbl.setText(curCustomerId);
            curContactLbl.setText(curContactName);
            contactBox.setDisable(false);
            customerBox.setDisable(false);
            userComboBox.setDisable(false);
            curDescLbl.setText(appTbl.getSelectionModel().getSelectedItem().getDescription());
            descriptionField.setDisable(false);
        }
    }
    /**
     * Calls on the update appointment list method to refresh the observable list
     * uses the get appointment list method to fill the values in teh table view
     */
    //Appointment update screen button controller
    public void updateAppointmentTableView() {
        updateAppointmentList();
        appTbl.setItems(getAppointmentList());
    }
    /** About Lambda Discussion
     * The 10 Lambdas in this method I decided to implement the lambda format for the JavaFX cell factory values.
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
     * Gets the lang string to assign the proper resource bundle
     * Updates the tableview, fills the tableview columns with entrys from the appointment list
     * Updates customer roster to fill combo box values
     * then runs the login notification check
     */
    //items to execute on screens initial load
    public void onLoad() {
        setLangTitles();
        updateAppointmentTableView();
        updateCustomerRoster();
        currentUserLbl.setText(getCurrentUser());
        appIdCol.setCellValueFactory(cellData -> cellData.getValue().appointmentIdProperty());
        appTitleCol.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        appDescCol.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        appLocCol.setCellValueFactory(cellData -> cellData.getValue().locationProperty());
        appContactCol.setCellValueFactory(cellData -> cellData.getValue().contactProperty());
        appTypeCol.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        appStartTimeCol.setCellValueFactory(cellData -> cellData.getValue().startStringProperty());
        appEndTimeCol.setCellValueFactory(cellData -> cellData.getValue().endStringProperty());
        appDateCol.setCellValueFactory(cellData -> cellData.getValue().dateStringProperty());
        appCustIdCol.setCellValueFactory(cellData -> cellData.getValue().customerIdProperty());
        // Create appointment notifications if any need to be shown
        logInAppointmentNotification();
    }
    /**
     * Runs listed methods for the pages initial load
     */
    @FXML
    public void initialize() {
        onLoad();
    }
}
