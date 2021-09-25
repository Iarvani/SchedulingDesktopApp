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
import obj.Customer;
import java.io.IOException;
import java.util.*;
import static obj.CustomerList.getCustomerRoster;
import static obj.DBManagement.*;

/** This class is used to control the customerHomeFXML. */
public class CustomerHomeController {

    ResourceBundle rb = setResBundle();
    @FXML private TableView <Customer> custTbl;
    @FXML private TableColumn <Customer, Number> custIDCol;
    @FXML private TableColumn <Customer, String> custNameCol;
    @FXML private TableColumn <Customer, String> custAddressCol;
    @FXML private TableColumn <Customer, String> custPhoneCol;
    @FXML private TableColumn <Customer, String> custPCodeCol;
    @FXML private Label clientLbl;
    @FXML private Label userLbl;
    @FXML private Label langLbl;
    @FXML private Label currentUserLbl;
    @FXML private Label currentLangLbl;
    @FXML private Button custAddBtn;
    @FXML private Button custUpdateBtn;
    @FXML private Button homeBtn;
    @FXML private Button custExitBtn;
    /**
     * Uses the set lang string to pick the resource bundle to translate the page with
     * Applies the values from the resource bundle to all the text elements on the page
     */
    //set language and change text
    private void setLangTitles() {
        if(LoginController.getLang().equals("English")) {
            ResourceBundle rb = ResourceBundle.getBundle("Resources/nat_en");
            custIDCol.setText(rb.getString("iDCol"));
            custNameCol.setText(rb.getString("nameCol"));
            custAddressCol.setText(rb.getString("addressCol"));
            custPhoneCol.setText(rb.getString("phoneCol"));
            custPCodeCol.setText(rb.getString("pCodeCol"));
            clientLbl.setText(rb.getString("clientTitleLbl"));
            userLbl.setText(rb.getString("userLbl"));
            langLbl.setText(rb.getString("langLbl"));
            currentUserLbl.setText(getCurrentUser());
            currentLangLbl.setText(rb.getString("currentLangLbl"));
            custAddBtn.setText(rb.getString("addBtn"));
            custUpdateBtn.setText(rb.getString("updateBtn"));
            homeBtn.setText(rb.getString("homeBtn"));
            custExitBtn.setText(rb.getString("exitBtn"));
        }
        else if(LoginController.getLang().equals("French")) {
            ResourceBundle rb = ResourceBundle.getBundle("Resources/nat_fr");
            custIDCol.setText(rb.getString("iDCol"));
            custNameCol.setText(rb.getString("nameCol"));
            custAddressCol.setText(rb.getString("addressCol"));
            custPhoneCol.setText(rb.getString("phoneCol"));
            custPCodeCol.setText(rb.getString("pCodeCol"));
            clientLbl.setText(rb.getString("clientTitleLbl"));
            userLbl.setText(rb.getString("userLbl"));
            langLbl.setText(rb.getString("langLbl"));
            currentUserLbl.setText(getCurrentUser());
            currentLangLbl.setText(rb.getString("currentLangLbl"));
            custAddBtn.setText(rb.getString("addBtn"));
            custUpdateBtn.setText(rb.getString("updateBtn"));
            homeBtn.setText(rb.getString("homeBtn"));
            custExitBtn.setText(rb.getString("exitBtn"));
        }
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
     * Applies the controls to the FXML exit button
     * exits the program when the button is pressed.
     * provides a confirmation pop-up before exiting the program
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
     * Applies the controls to the FXML home screen button
     * Redirects the user to the home screen when the button is pressed.
     * @throws IOException if there is an error loading the homeScreen.FXML
     */
    //Home screen button controller
    @FXML
    private void openHomeScreen(ActionEvent event) throws IOException {
        Parent addPartParent = FXMLLoader.load(getClass().getResource("HomeScreen.fxml"));
        Scene addPartScene = new Scene(addPartParent);
        Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        addPartStage.setScene(addPartScene);
        addPartStage.show();
    }
    /**
     * updates the observable list of customers
     * applies the values in the list to the customer table view
     */
    //update values to populate the table view
    public void updateCustomerTableView() {
        updateCustomerRoster();
        custTbl.setItems(getCustomerRoster());
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
     * pulls the current user from the DB for display
     * Applies the values in the list to the table view
     * runs the updateCustomerTableView and loginNotification methods
     */
    //populate table values, labels, and language bundle on loading of page
    public void onLoad () {
        userLbl.setText(getCurrentUser());
        setLangTitles();
        custIDCol.setCellValueFactory(cellData -> cellData.getValue().customerIdProperty());
        custNameCol.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        custAddressCol.setCellValueFactory(cellData -> cellData.getValue().getAddressConversion());
        custPCodeCol.setCellValueFactory(cellData -> cellData.getValue().postalCodeProperty());
        custPhoneCol.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        // Update table view
        updateCustomerTableView();
        // Create appointment notifications if any need to be shown
        logInAppointmentNotification();
    }
    /**
     * Runs the listed methods on the pages initial load
     */
    @FXML
    public void initialize() { onLoad(); }
}

