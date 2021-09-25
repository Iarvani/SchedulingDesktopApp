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
import obj.Appointment;
import java.io.IOException;
import java.util.Optional;
import java.util.ResourceBundle;
import static obj.AppList.getAppointmentList;
import static obj.DBManagement.*;

/** This class is used to control the appointmentHomeFXML. */
public class AppointmentHomeController {

    ResourceBundle rb = setResBundle();
    @FXML private Label appHomeTitleLbl;
    @FXML private Label userLbl;
    @FXML private Label currentUserLbl;
    @FXML private Label langLbl;
    @FXML private Label currentLangLbl;
    @FXML private Label viewAppByLbl;
    @FXML private Button appAddBtn;
    @FXML private Button appUpdateBtn;
    @FXML private Button homeBtn;
    @FXML private Button exitBtn;
    @FXML private TableView <Appointment> appTbl;
    @FXML private TableColumn <Appointment, Number> appIDCol;
    @FXML private TableColumn <Appointment, String> nameCol;
    @FXML private TableColumn <Appointment, String> titleCol;
    @FXML private TableColumn <Appointment, String> startTimeCol;
    @FXML private TableColumn <Appointment, String> endTimeCol;
    /**
     * Assigns controls to the FXML Appointment add screen button
     * Redirects the user to the appointment add screen when pressed
     * @throws IOException if loading Scene/Resources occurs
     */
    //Appointment add screen button controller
    @FXML
    private void openAppointmentAddScreen(ActionEvent event) throws IOException {
        Parent addPartParent = FXMLLoader.load(getClass().getResource("AppointmentAdd.fxml"));
        Scene addPartScene = new Scene(addPartParent);
        Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        addPartStage.setScene(addPartScene);
        addPartStage.show();
    }
    /**
     * Assigns controls to the FXML Appointment update screen button
     * Redirects the user to the appointment update screen when pressed
     * @throws IOException if loading Scene/Resources occurs
     */
    //Appointment update screen button controller
    @FXML
    private void openAppointmentUpdateScreen(ActionEvent event) throws IOException {
        Parent addPartParent = FXMLLoader.load(getClass().getResource("AppointmentUpdate.fxml"));
        Scene addPartScene = new Scene(addPartParent);
        Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        addPartStage.setScene(addPartScene);
        addPartStage.show();
    }
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
     * Assigns controls to the FXML home screen button
     * Redirects the user to the home screen when pressed
     * @param event button click
     * @throws IOException if loading Scene/Resources occurs
     */
    //home screen button controller
    @FXML
    public void openHomeScreen(ActionEvent event) throws IOException {
        Parent addPartParent = FXMLLoader.load(getClass().getResource("HomeScreen.fxml"));
        Scene addPartScene = new Scene(addPartParent);
        Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        addPartStage.setScene(addPartScene);
        addPartStage.show();
    }
    /**
     * Uses the set lang string to select the appropriate resource bundle
     * Uses the resource bundle to assign the proper values to all text elements on the page.
     */
    //set language and change text
    @FXML
    private void setLangTitles() {
        if (LoginController.getLang().equals("English")) {
            ResourceBundle rb = ResourceBundle.getBundle("Resources/nat_en");
            appHomeTitleLbl.setText(rb.getString("scheduleAppHomeTitleLbl"));
            userLbl.setText(rb.getString("userLbl"));
            currentUserLbl.setText(getCurrentUser());
            langLbl.setText(rb.getString("langLbl"));
            currentLangLbl.setText(rb.getString("currentLangLbl"));
            appAddBtn.setText(rb.getString("addBtn"));
            appUpdateBtn.setText(rb.getString("updateBtn"));
            homeBtn.setText(rb.getString("homeBtn"));
            exitBtn.setText(rb.getString("exitBtn"));
            appIDCol.setText(rb.getString("tableAppIdCol"));
            nameCol.setText(rb.getString("tableContactCol"));
            titleCol.setText(rb.getString("tableTitleCol"));
            startTimeCol.setText(rb.getString("tableStartTimeCol"));
            endTimeCol.setText(rb.getString("tableEndTimeCol"));
        }
        else if (LoginController.getLang().equals("French")) {
            ResourceBundle rb = ResourceBundle.getBundle("Resources/nat_fr");
            appHomeTitleLbl.setText(rb.getString("scheduleAppHomeTitleLbl"));
            userLbl.setText(rb.getString("userLbl"));
            currentUserLbl.setText(getCurrentUser());
            langLbl.setText(rb.getString("langLbl"));
            currentLangLbl.setText(rb.getString("currentLangLbl"));
            appAddBtn.setText(rb.getString("addBtn"));
            appUpdateBtn.setText(rb.getString("updateBtn"));
            homeBtn.setText(rb.getString("homeBtn"));
            exitBtn.setText(rb.getString("exitBtn"));
            appIDCol.setText(rb.getString("tableAppIdCol"));
            nameCol.setText(rb.getString("tableContactCol"));
            titleCol.setText(rb.getString("tableTitleCol"));
            startTimeCol.setText(rb.getString("tableStartTimeCol"));
            endTimeCol.setText(rb.getString("tableEndTimeCol"));
        }
    }
    /**
     * Calls on the update appointment list method to refresh the observable list
     * uses the get appointment list method to fill the values in teh table view
     */
    //update values to populate table views
    public void updateAppointmentTableView() {
        updateAppointmentList();
        appTbl.setItems(getAppointmentList());
    }
    /** About Lambda Discussion
     * The 5 lambdas in this method I decided to implement the lambda format for the JavaFX cell factory values.
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
     * then runs the login notification check
     */
    //items to be executed on initial load of the screen
    public void onLoad() {
        setLangTitles();
        //update the table view
        updateAppointmentTableView();
        currentUserLbl.setText(getCurrentUser());
        appIDCol.setCellValueFactory(cellData -> cellData.getValue().appointmentIdProperty());
        nameCol.setCellValueFactory(cellData -> cellData.getValue().contactProperty());
        titleCol.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        startTimeCol.setCellValueFactory(cellData -> cellData.getValue().startStringProperty());
        endTimeCol.setCellValueFactory(cellData -> cellData.getValue().endStringProperty());
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
