package Layout;

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
import obj.Appointment;
import java.util.Optional;
import java.io.IOException;
import java.util.ResourceBundle;
import static obj.AppList.getAppointmentList;
import static obj.DBManagement.*;

/** This class is used to control the homeScreen FXML. */
public class HomeScreenController {

    ResourceBundle rb = setResBundle();
    @FXML private Label langLbl;
    @FXML private Label currentUserLbl;
    @FXML private Label currentLangLbl;
    @FXML private Button custUpdateScrBtn;
    @FXML private Button custAddScrBtn;
    @FXML private Button custHomeScrBtn;
    @FXML private Button appUpdateScrBtn;
    @FXML private Button appHomeScrBtn;
    @FXML private Button appAddScrBtn;
    @FXML private RadioButton monthRadioBtn;
    @FXML private RadioButton weekRadioBtn;
    @FXML private RadioButton allRadioBtn;
    @FXML private Button exitBtn;
    @FXML private Button getCustomerReportBtn;
    @FXML private Button getContactReportBtn;
    @FXML private Button getUserReportBtn;
    @FXML private Label appOptionsLbl;
    @FXML private Label custOptionsLbl;
    @FXML private Label scheduleAppLbl;
    @FXML private Label userLbl;
    @FXML private TableView <Appointment> nearAppsTbl;
    @FXML private TableColumn <Appointment, Number> tableIDCol;
    @FXML private TableColumn <Appointment, String> tableTitleCol;
    @FXML private TableColumn <Appointment, String> tableDescCol;
    @FXML private TableColumn <Appointment, String> tableContactCol;
    @FXML private TableColumn <Appointment, Number> tableCustomerCol;
    @FXML private TableColumn <Appointment, String> tableLocCol;
    @FXML private TableColumn <Appointment, String> tableTypeCol;
    @FXML private TableColumn <Appointment, String> tableStartCol;
    @FXML private TableColumn <Appointment, String> tableEndCol;
    @FXML private TableColumn <Appointment, String> tableDateCol;
    private boolean byMonth = true;

    //change the FX:ID from homeUserLbl to userLbl;
    //leave resource bundle ids the same
    /**
     * Uses the lang string from loginController to apply the proper language to fields
     */
    @FXML
    private void setLangTitles () {
        if(LoginController.getLang().equals("English")) {
            ResourceBundle rb = ResourceBundle.getBundle("resources/nat_en");
            userLbl.setText(rb.getString("userLbl"));
            currentUserLbl.setText(getCurrentUser());
            currentLangLbl.setText(rb.getString("currentLangLbl"));
            langLbl.setText(rb.getString("langLbl"));
            custUpdateScrBtn.setText(rb.getString("updateBtn"));
            custAddScrBtn.setText(rb.getString("addBtn"));
            custHomeScrBtn.setText(rb.getString("homeBtn"));
            custOptionsLbl.setText(rb.getString("customerOptionsTitleLbl"));
            appOptionsLbl.setText(rb.getString("appointmentOptionsTitleLbl"));
            scheduleAppLbl.setText(rb.getString("scheduleAppHomeTitleLbl"));
            appUpdateScrBtn.setText(rb.getString("updateBtn"));
            appHomeScrBtn.setText(rb.getString("homeBtn"));
            appAddScrBtn.setText(rb.getString("addBtn"));
            exitBtn.setText(rb.getString("exitBtn"));
            tableIDCol.setText(rb.getString("tableAppIdCol"));
            tableTitleCol.setText(rb.getString("tableTitleCol"));
            tableDescCol.setText(rb.getString("tableDescriptionCol"));
            tableContactCol.setText(rb.getString("tableContactCol"));
            tableCustomerCol.setText(rb.getString("tableCustomerIdCol"));
            tableLocCol.setText(rb.getString("tableLocationCol"));
            tableTypeCol.setText(rb.getString("tableTypeCol"));
            tableStartCol.setText(rb.getString("tableStartTimeCol"));
            tableEndCol.setText(rb.getString("tableEndTimeCol"));
            tableDateCol.setText(rb.getString("dateLbl"));
            monthRadioBtn.setText(rb.getString("homeMonthRadioBtn"));
            weekRadioBtn.setText(rb.getString("homeWeekRadioBtn"));
            getCustomerReportBtn.setText(rb.getString("getCustomerReportBtn"));
            getContactReportBtn.setText(rb.getString("getContactReportBtn"));
            getUserReportBtn.setText(rb.getString("getUserReportBtn"));
        }
        else if (LoginController.getLang().equals("French")) {
            ResourceBundle rb = ResourceBundle.getBundle("Resources/nat_fr");
            userLbl.setText(rb.getString("userLbl"));
            currentUserLbl.setText(getCurrentUser());
            currentLangLbl.setText(rb.getString("currentLangLbl"));
            langLbl.setText(rb.getString("langLbl"));
            custUpdateScrBtn.setText(rb.getString("updateBtn"));
            custAddScrBtn.setText(rb.getString("addBtn"));
            custHomeScrBtn.setText(rb.getString("homeBtn"));
            custOptionsLbl.setText(rb.getString("customerOptionsTitleLbl"));
            appOptionsLbl.setText(rb.getString("appointmentOptionsTitleLbl"));
            scheduleAppLbl.setText(rb.getString("scheduleAppHomeTitleLbl"));
            appUpdateScrBtn.setText(rb.getString("updateBtn"));
            appHomeScrBtn.setText(rb.getString("homeBtn"));
            appAddScrBtn.setText(rb.getString("addBtn"));
            exitBtn.setText(rb.getString("exitBtn"));
            tableIDCol.setText(rb.getString("tableAppIdCol"));
            tableTitleCol.setText(rb.getString("tableTitleCol"));
            tableDescCol.setText(rb.getString("tableDescriptionCol"));
            tableContactCol.setText(rb.getString("tableContactCol"));
            tableCustomerCol.setText(rb.getString("tableCustomerIdCol"));
            tableLocCol.setText(rb.getString("tableLocationCol"));
            tableTypeCol.setText(rb.getString("tableTypeCol"));
            tableStartCol.setText(rb.getString("tableStartTimeCol"));
            tableEndCol.setText(rb.getString("tableEndTimeCol"));
            tableDateCol.setText(rb.getString("dateLbl"));
            monthRadioBtn.setText(rb.getString("homeMonthRadioBtn"));
            weekRadioBtn.setText(rb.getString("homeWeekRadioBtn"));
            getCustomerReportBtn.setText(rb.getString("getCustomerReportBtn"));
            getContactReportBtn.setText(rb.getString("getContactReportBtn"));
            getUserReportBtn.setText(rb.getString("getUserReportBtn"));
        }
    }
    @FXML
    private void allRadioBtn() {
        weekRadioBtn.setSelected(false);
        monthRadioBtn.setSelected(false);
        byMonth = false;
        nearAppsTbl.getItems().clear();
        //ObservableList<Appointment> allAppointments = getAppointmentList();
        //nearAppsTbl.setItems(allAppointments);
        updateAppointmentTableView();
        tableIDCol.setCellValueFactory(cellData -> cellData.getValue().appointmentIdProperty());
        tableTypeCol.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        tableTitleCol.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        tableStartCol.setCellValueFactory(cellData -> cellData.getValue().startStringProperty());
        tableLocCol.setCellValueFactory(cellData -> cellData.getValue().locationProperty());
        tableContactCol.setCellValueFactory(cellData -> cellData.getValue().contactProperty());
        tableCustomerCol.setCellValueFactory(cellData -> cellData.getValue().customerIdProperty());
        tableDescCol.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        tableEndCol.setCellValueFactory(cellData -> cellData.getValue().endStringProperty());
        tableDateCol.setCellValueFactory(cellData -> cellData.getValue().dateStringProperty());
    }
    /**
     * Applies the controls to the search by month FXML radio button .
     * Incriments through the observable list to find all appointments within 1 month
     * from now. Displays those which belong to the current user logged in.
     *<br><br>
     * About Lambda Discussion
     * The 10 lambdas in this methos I decided to implement the lambda format for the JavaFX cell factory values.
     * The reasoning behind this decision was to reduce the amount of methods that were needed to created the same result.
     * This is a more simplified version of the Lambda expression, the previous version of the expression would have needed
     * to declare the return value and be created as an inner class. We could implement the long version of the lambda by
     * writing it out as - tableIDCol.setCellValueFactory(cellData -> {
     * return cellData.getValue().appointmentIdProperty();
     * });
     * With using the simplified Lambda syntax we are not only making the code easier to follow tableIDCol will use
     * the setCellValueFactoryMethod to apply the getValue().SomeGetter() to the tableID column. Furthermore by implementing the Lambda
     * expression we save ourselves from having an addition method creation like -
     * public ObservableValue<String> call(CellDataFeatures<Customer, String> p) {
     *        // p.getValue() returns the value instance for a particular TableView row
     *        return p.getValue().customerNameProperty();
     *        }
     * });
     * for each of our table columns saving us almost 50 lines of code per FXML controller class.
     */
    //customer screen button controllers
    @FXML
    private void searchByMonth(ActionEvent event) {
        byMonth = true;
        weekRadioBtn.setSelected(false);
        allRadioBtn.setSelected(false);
        nearAppsTbl.getItems().clear();
        //get appointments up to 1 month from now based on current logged in user id
        ObservableList<Appointment> monthlyAppointments = getMonthlyAppointments(getUserId(getCurrentUser()));
        nearAppsTbl.setItems(monthlyAppointments);
        tableIDCol.setCellValueFactory(cellData -> cellData.getValue().appointmentIdProperty());
        tableTypeCol.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        tableTitleCol.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        tableStartCol.setCellValueFactory(cellData -> cellData.getValue().startStringProperty());
        tableLocCol.setCellValueFactory(cellData -> cellData.getValue().locationProperty());
        tableContactCol.setCellValueFactory(cellData -> cellData.getValue().contactProperty());
        tableCustomerCol.setCellValueFactory(cellData -> cellData.getValue().customerIdProperty());
        tableDescCol.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        tableEndCol.setCellValueFactory(cellData -> cellData.getValue().endStringProperty());
        tableDateCol.setCellValueFactory(cellData -> cellData.getValue().dateStringProperty());
    }
    /**
     * Applies the controls to the search by week FXML radio button
     * Incriments through the observable list to find all appointments within 1 week
     * from now. Display those which belong to the current logged in user.
     *<br><br>
     * About Lambda Discussion
     * The 10 lambdas in this methos I decided to implement the lambda format for the JavaFX cell factory values.
     * The reasoning behind this decision was to reduce the amount of methods that were needed to created the same result.
     * This is a more simplified version of the Lambda expression, the previous version of the expression would have needed
     * to declare the return value and be created as an inner class. We could implement the long version of the lambda by
     * writing it out as - tableIDCol.setCellValueFactory(cellData -> {
     * return cellData.getValue().appointmentIdProperty();
     * });
     * With using the simplified Lambda syntax we are not only making the code easier to follow tableIDCol will use
     * the setCellValueFactoryMethod to apply the getValue().SomeGetter() to the tableID column. Furthermore by implementing the Lambda
     * expression we save ourselves from having an addition method creation like -
     * public ObservableValue<String> call(CellDataFeatures<Customer, String> p) {
     *        // p.getValue() returns the value instance for a particular TableView row
     *        return p.getValue().customerNameProperty();
     *        }
     * });
     * for each of our table columns saving us almost 50 lines of code per FXML controller class.
     */
    //Search by week radio button controller
    @FXML
    private void searchByWeek(ActionEvent event) {
        byMonth = false;
        monthRadioBtn.setSelected(false);
        allRadioBtn.setSelected(false);
        nearAppsTbl.getItems().clear();
        //Get appointments within 7 days of now based on the current logged in user id
        ObservableList<Appointment> weeklyAppointments = getWeeklyAppointments(getUserId(getCurrentUser()));
        nearAppsTbl.setItems(weeklyAppointments);
        tableIDCol.setCellValueFactory(cellData -> cellData.getValue().appointmentIdProperty());
        tableTypeCol.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        tableTitleCol.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        tableStartCol.setCellValueFactory(cellData -> cellData.getValue().startStringProperty());
        tableLocCol.setCellValueFactory(cellData -> cellData.getValue().locationProperty());
        tableContactCol.setCellValueFactory(cellData -> cellData.getValue().contactProperty());
        tableCustomerCol.setCellValueFactory(cellData -> cellData.getValue().customerIdProperty());
        tableDescCol.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        tableEndCol.setCellValueFactory(cellData -> cellData.getValue().endStringProperty());
        tableDateCol.setCellValueFactory(cellData -> cellData.getValue().dateStringProperty());
    }
    /**
     * Triggers the generate appointment report which writes to file in root folder
     * all appointments in the system as well as all reports based on client.
     * Provides a informational pop up to notify the user the report have been generated.
     */
    // Generate appointment report for clients and customers
    @FXML
    private void getCustomerReport () {
        getReportByCustomer();
    }
    @FXML
    private void getContactReport () {
        generateReportByContact();
        /*
        nearAppsTbl.getItems().clear();
        nearAppsTbl.setItems(getAppointmentList());
        tableIDCol.setCellValueFactory(cellData -> cellData.getValue().appointmentIdProperty());
        tableTypeCol.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        tableTitleCol.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        tableStartCol.setCellValueFactory(cellData -> cellData.getValue().startStringProperty());
        tableLocCol.setCellValueFactory(cellData -> cellData.getValue().locationProperty());
        tableContactCol.setCellValueFactory(cellData -> cellData.getValue().contactProperty());
        tableCustomerCol.setCellValueFactory(cellData -> cellData.getValue().customerIdProperty());
        tableDescCol.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        tableEndCol.setCellValueFactory(cellData -> cellData.getValue().endStringProperty());
        tableDateCol.setCellValueFactory(cellData -> cellData.getValue().dateStringProperty());

         */
    }
    @FXML
    private void getUserReport () {
        try {
            generateReport();
            /*nearAppsTbl.getItems().clear();
            tableIDCol.setCellValueFactory(cellData -> cellData.getValue().appointmentIdProperty());
            tableTypeCol.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
            tableTitleCol.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
            tableStartCol.setCellValueFactory(cellData -> cellData.getValue().startStringProperty());
            tableLocCol.setCellValueFactory(cellData -> cellData.getValue().locationProperty());
            tableContactCol.setCellValueFactory(cellData -> cellData.getValue().contactProperty());
            tableCustomerCol.setCellValueFactory(cellData -> cellData.getValue().customerIdProperty());
            tableDescCol.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
            tableEndCol.setCellValueFactory(cellData -> cellData.getValue().endStringProperty());
            tableDateCol.setCellValueFactory(cellData -> cellData.getValue().dateStringProperty());*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Applies the controls to the FXML customer home screen button
     * Redirects the user to the home screen when the button is pressed.
     * @throws IOException if there is an error loading the customerHome.FXML
     */
    //Home screen button controller
    @FXML
    private void openCustomerHomeScreen(ActionEvent event) throws IOException {
        Parent addPartParent = FXMLLoader.load(getClass().getResource("CustomerHome.fxml"));
        Scene addPartScene = new Scene(addPartParent);
        Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        addPartStage.setScene(addPartScene);
        addPartStage.show();
    }
    /**
     * Applies the controls to the FXML customer add screen button
     * Redirects the user to the customer add screen when the button is pressed.
     * @throws IOException if there is an error loading the customerAdd.FXML
     */
    //Customer add screen button controller
    @FXML
    private void openCustomerAddScreen(ActionEvent event) throws IOException {
        Parent addPartParent = FXMLLoader.load(getClass().getResource("CustomerAdd.fxml"));
        Scene addPartScene = new Scene(addPartParent);
        Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        addPartStage.setScene(addPartScene);
        addPartStage.show();
    }
    /**
     * Applies the controls to the FXML customer update screen button
     * Redirects the user to the customer update screen when the button is pressed.
     * @throws IOException if there is an error loading the customerUpdate.FXML
     */
    //Customer update screen button controller
    @FXML
    private void openCustomerUpdateScreen(ActionEvent event) throws IOException {
        Parent addPartParent = FXMLLoader.load(getClass().getResource("CustomerUpdate.fxml"));
        Scene addPartScene = new Scene(addPartParent);
        Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        addPartStage.setScene(addPartScene);
        addPartStage.show();
    }
    /**
     * Applies the controls to the FXML appointment home screen button
     * Redirects the user to the appointment home screen when the button is pressed.
     * @throws IOException if there is an error loading the appointmentHome.FXML
     */
    //Appointments Home screen button controller
    @FXML
    private void openAppointmentsHomeScreen(ActionEvent event) throws IOException {
        Parent addPartParent = FXMLLoader.load(getClass().getResource("AppointmentHome.fxml"));
        Scene addPartScene = new Scene(addPartParent);
        Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        addPartStage.setScene(addPartScene);
        addPartStage.show();
    }
    /**
     * Applies the controls to the FXML appointment add screen button
     * Redirects the user to the appointment add screen when the button is pressed.
     * @throws IOException if there is an error loading the appointmentAdd.FXML
     */
    //Appointment add screen controller
    @FXML
    private void openAppointmentsAddScreen(ActionEvent event) throws IOException {
        Parent addPartParent = FXMLLoader.load(getClass().getResource("AppointmentAdd.fxml"));
        Scene addPartScene = new Scene(addPartParent);
        Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        addPartStage.setScene(addPartScene);
        addPartStage.show();
    }
    /**
     * Applies the controls to the FXML appointment update screen button
     * Redirects the user to the appointment update screen when the button is pressed.
     * @throws IOException if there is an error loading the appointmentUpdate.FXML
     */
    //Appointment update screen controller
    @FXML
    private void openAppointmentsUpdateScreen(ActionEvent event) throws IOException {
        Parent addPartParent = FXMLLoader.load(getClass().getResource("AppointmentUpdate.fxml"));
        Scene addPartScene = new Scene(addPartParent);
        Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        addPartStage.setScene(addPartScene);
        addPartStage.show();
    }
    /**
     * Applies the controls to the FXML exit button
     * which closes the program if the user presses the button.
     * Provides a confirmation pop-up to the user before program exit.
     */
    //Exit program button controller with confirmation popup
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
     * Uses the update appointment list method to refresh the observable list of appointments
     * Uses the observable list to apply the values to be shown in the nearAppsTbl view
     */
    //Update the values to be populate in the table view
    public void updateAppointmentTableView() {
        updateAppointmentList();
        nearAppsTbl.setItems(getAppointmentList());
    }
    /**
     * Applies the values to be shown on the page when the page is loaded.
     * Shows the current user logged in, applies a list of all appointments in the observable list,
     * and pulls the set language method to translate the fields.
     * Then runs the update table view method just in case not all of the appointments had been in the observable list,
     * and runs the login app notification to alert of any appointments within 15 minutes.
     *<br><br>
     * About Lambda Discussion
     * The 10 lambdas in this methos I decided to implement the lambda format for the JavaFX cell factory values.
     * The reasoning behind this decision was to reduce the amount of methods that were needed to created the same result.
     * This is a more simplified version of the Lambda expression, the previous version of the expression would have needed
     * to declare the return value and be created as an inner class. We could implement the long version of the lambda by
     * writing it out as - tableIDCol.setCellValueFactory(cellData -> {
     * return cellData.getValue().appointmentIdProperty();
     * });
     * With using the simplified Lambda syntax we are not only making the code easier to follow tableIDCol will use
     * the setCellValueFactoryMethod to apply the getValue().SomeGetter() to the tableID column. Furthermore by implementing the Lambda
     * expression we save ourselves from having an addition method creation like -
     * public ObservableValue<String> call(CellDataFeatures<Customer, String> p) {
     *        // p.getValue() returns the value instance for a particular TableView row
     *        return p.getValue().customerNameProperty();
     *        }
     * });
     * for each of our table columns saving us almost 50 lines of code per FXML controller class.
     *
     */

    //List of items to execute when the page first loads
    public void onLoad() {
        currentUserLbl.setText(getCurrentUser());
        setLangTitles();
        tableIDCol.setCellValueFactory(cellData -> cellData.getValue().appointmentIdProperty());
        tableTypeCol.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        tableTitleCol.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        tableStartCol.setCellValueFactory(cellData -> cellData.getValue().startStringProperty());
        tableLocCol.setCellValueFactory(cellData -> cellData.getValue().locationProperty());
        tableContactCol.setCellValueFactory(cellData -> cellData.getValue().contactProperty());
        tableCustomerCol.setCellValueFactory(cellData -> cellData.getValue().customerIdProperty());
        tableDescCol.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        tableEndCol.setCellValueFactory(cellData -> cellData.getValue().endStringProperty());
        tableDateCol.setCellValueFactory(cellData -> cellData.getValue().dateStringProperty());
        //update the table view
        updateAppointmentTableView();
    }
    /**
     * Applies the listed items on load
     * These were places into the onLoad method to keep initialize clean and easy to follow.
     */
    @FXML
    private void initialize() {
        onLoad();
        //Show appointment notifications
        logInAppointmentNotification();
    }

}
