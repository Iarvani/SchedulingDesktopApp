package obj;

import Layout.LoginController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
//import javafx.scene.layout.Region;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import javafx.stage.Modality;

import java.io.BufferedReader;
//import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.Date;
import java.util.regex.Pattern;
import Utilitys.DBConnection;
import static Layout.LoginController.getLang;
import static java.lang.Integer.parseInt;
import static java.time.LocalTime.*;
import static java.util.TimeZone.getTimeZone;

/** This class is used to control all things dealing with the insertion, deletion, updating, and querying of the database. */
public class DBManagement {

    private static final DBConnection DB = new DBConnection();
    private static ResourceBundle rb = setResBundle();
    private static String currentUser;
    private static int openCount = 0;
    private static String SQLStatement;
    private static PreparedStatement test;
    private static ResultSet result;

    /**
     * Sets the resource bundle based on the lang string.
     * This was found to be an easier way than listing every error message in a setLang method.
     *
     * @return Returns English or French resource bundle.
     */
    // Get language string to change the resource bundle accordingly
    public static ResourceBundle setResBundle() {
        if (getLang().equals("English")) {
            rb = ResourceBundle.getBundle("Resources/nat_en");
        } else if (getLang().equals("French")) {
            rb = ResourceBundle.getBundle("Resources/nat_fr");
        }
        // Default language english
        else {
            rb = ResourceBundle.getBundle("Resources/nat_en");
        }
        //rb = ResourceBundle.getBundle("Resource/nat");
        return rb;
    }

    /**
     * Passes user entered name/pass to the database for login authentication
     * also writes a file to the root folder named login_activity.txt with login's
     *
     * @param password User entered password.
     * @param userName User entered login name.
     * @return True if credentials match DB, False if not.
     * @throws IOException in the event the DB cannot be reached.
     */
    // Check log in credentials to see if they are valid
    public static boolean checkLogInCredentials(String userName, String password) throws IOException {
        int userId = getUserId(userName);
        if (userId != 0) {
            boolean correctPassword = checkPassword(userId, password);
            if (correctPassword) {
                // If correct password returns true, set current user variable for rest of the program to use.
                setCurrentUser(userName);
                currentUser = getCurrentUser();
                try {
                    // If correct password, write to log
                    Path path = Paths.get("login_activity.txt");
                    Files.write(path, Collections.singletonList("User " + currentUser + " logged in at " + Date.from(Instant.now()).toString() + "."),
                            StandardCharsets.UTF_8, Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.initModality(Modality.NONE);
                alert.setTitle(rb.getString("error"));
                alert.setHeaderText(rb.getString("loginError"));
                alert.setContentText(rb.getString("wrongPassMessage"));
                alert.showAndWait();
                // If password check fails, write to log
                Path path = Paths.get("login_activity.txt");
                Files.write(path, Collections.singletonList("FAILED LOGIN - User " + currentUser + " attempted at " + Date.from(Instant.now()).toString() + "."),
                        StandardCharsets.UTF_8, Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
                return false;
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initModality(Modality.NONE);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("loginError"));
            alert.setContentText(rb.getString("wrongUserMessage"));
            alert.showAndWait();
            return false;
        }
    }

    /**
     * Uses the username provided to match with the DB and return the users ID number.
     *
     * @param userName User entered user name.
     * @return Returns user ID to match user name, or 0 if no matching username was found.
     */
    // Get userId from userName (returns -1 if no match found for userName)
    public static int getUserId(String userName) {
        // Try-with-resources and catch block for database connection and handling server connection problem
        try (DB.connect) {
            // Retrieves userId for entered username
            SQLStatement = "SELECT User_ID FROM users WHERE User_Name = '" + userName + "'";
            test = DBConnection.startConnection().prepareStatement(SQLStatement);
            result = test.executeQuery();
            // Sets userId to unique value and retrieves int from ResultSet
            if (result.next()) {
                return result.getInt(1);
            }
            System.out.println("No User ID found for the selected user. . .");
            return 0;
        } catch (SQLException e) {
            // Increment databaseError count in LogInController so error message can be displayed
            LoginController.incrementDatabaseError();
            return 0;
        }
    }

    /**
     * Uses the users password provided and the users ID number to match the values to the DB
     * I.E. returns true if uses values match the DB values
     * returns fails and a information error if they dont match
     * Provides an error message if the values are incorrect
     */
    // Check if password matches userId
    private static Boolean checkPassword(int userId, String password) {
        // Try-with-resources and catch block for database connection and handling server connection problem
        try (DB.connect) {
            // Retrieves password by userId
            ResultSet passwordResultSet = test.executeQuery("SELECT Password FROM users WHERE User_ID = '" + userId + "'");
            passwordResultSet.next();
            // Initializes dbPassword and match with entered password
            if (passwordResultSet.getString(1).equals(password)) {
                return true;
            } else {
                System.out.println("Password does not match");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Login check error: " + e);
            return null;
        }
    }

    /**
     * Sets the string with a value of the current user
     * The getUser will return the value of the current user
     */
    // Set/get currentUser string for public access
    private static void setCurrentUser(String userName) {
        currentUser = userName;
    }

    public static String getCurrentUser() {
        return currentUser;
    }

    /**
     * Creates an instance of a calendar 15 minutes from now
     * Cycles through the observable list of appointments to compare dates/time with the calendar created
     * Provides the user with a information pop-up of any appointments within 15 minutes
     * Adds 1 to open count so the login notification will only be shown once this session
     */
    // Create notifications for user appointments occurring in the next 15 minutes
    public static void logInAppointmentNotification() {
        // Checks to see if the main screen has already been opened during this session
        // Create Date object for 15 minutes from now
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Date.from(Instant.now()));
        calendar.add(Calendar.MINUTE, 15);
        Date notificationCutoff = calendar.getTime();
        boolean found = false;
        if (openCount == 0) {
            // Create ObservableList of appointments that were created by the user who just logged in and not past tense
            ObservableList<Appointment> userAppointments = FXCollections.observableArrayList();
            for (Appointment appointment : AppList.getAppointmentList()) {
                if (appointment.getCreatedBy().equals(currentUser) && appointment.getStartDate().after(Date.from(Instant.now()))) {
                    userAppointments.add(appointment);
                }
            }
            // Check each appointment in userAppointments to see if any start in the next 15 minutes
            for (Appointment appointment : userAppointments) {
                // If appointment start date is before 15 minutes from now, create alert with information about appointment
                if (appointment.getStartDate().before(notificationCutoff)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(rb.getString("notifyUpcomingAppointment"));
                    alert.setHeaderText(rb.getString("notifyUpcomingAppointment"));
                    alert.setContentText(rb.getString("notifyUpcomingAppointmentMessage") + "\n" + rb.getString("titleLbl")
                            + ": " + appointment.getTitle() + "\n" + rb.getString("descriptionLbl") + ": " + appointment.getDescription() +
                            "\n" + rb.getString("locationLbl") + ": " + appointment.getLocation() + "\n" + rb.getString("contactLbl") +
                            ": " + appointment.getContact() + "\n" + rb.getString("userLbl") + ": " + appointment.getUser() + "\n" +
                            rb.getString("dateLbl") + ": " + appointment.getDateString() + "\n" + rb.getString("startTimeLbl") + ": " +
                            appointment.getStartString() + "\n" + rb.getString("endTimeLbl") + ": " + appointment.getEndString());
                    alert.showAndWait();
                    found = true;
                }
            }
            if (!found) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(rb.getString("notifyUpcomingAppointment"));
                alert.setHeaderText(rb.getString("notifyUpcomingAppointment"));
                alert.setContentText(rb.getString("notifyNoUpcomingAppointmentMessage"));
                alert.showAndWait();
            }
            // Increment openCount so notifications won't be shown again this session
            openCount++;
        }
    }

    /**
     * Cycles through the customers in the database to build the customer observable list
     * catches an SQL error thrown by the DB.connect in the event of a error
     */
    // Update customerRoster after database change
    public static void updateCustomerRoster() {
        // Try-with-resources block for database connection
        try (DB.connect) {
            // Retrieve customerRoster and clear
            ObservableList<Customer> customerRoster = CustomerList.getCustomerRoster();
            customerRoster.clear();
            // Create list of customerId's for all active customers
            SQLStatement = "SELECT Customer_ID FROM customers";
            test = DBConnection.startConnection().prepareStatement(SQLStatement);
            result = test.executeQuery();
            ArrayList<Integer> customerIdList = new ArrayList<>();
            while (result.next()) {
                customerIdList.add(result.getInt(1));
            }
            // Create Customer object for each customerId in list and add Customer to customerRoster list
            for (int customerId : customerIdList) {
                // Create new Customer object
                Customer customer = new Customer();
                // Retrieve customer information from database
                ResultSet customerResultSet = test.executeQuery("SELECT Customer_ID, Customer_Name, Address," +
                        "Postal_Code, Phone, Division_ID FROM customers WHERE Customer_ID = '" + customerId + "'");
                customerResultSet.next();
                int customersId = customerResultSet.getInt(1);
                String customerName = customerResultSet.getString(2);
                String addressId = customerResultSet.getString(3);
                String postalCode = customerResultSet.getString(4);
                String phone = customerResultSet.getString(5);
                int cityId = customerResultSet.getInt(6);
                //use cityId to get division name and set countryId
                ResultSet cityResultSet = test.executeQuery("SELECT Division, Country_ID FROM first_level_divisions WHERE Division_ID = '" + cityId + "'");
                cityResultSet.next();
                String city = cityResultSet.getString(1);
                int countryId = cityResultSet.getInt(2);
                //use countryId to get country name
                ResultSet countryResultSet = test.executeQuery("SELECT Country FROM countries WHERE Country_ID = '" + countryId + "'");
                countryResultSet.next();
                String country = countryResultSet.getString(1);
                //set all the values
                customer.setCustomerId(customersId);
                customer.setCustomerName(customerName);
                customer.setAddressId(addressId);
                customer.setPostalCode(postalCode);
                customer.setPhone(phone);
                customer.setDivisionId(cityId);
                customer.setDivision(city);
                customer.setCountryId(countryId);
                customer.setCountry(country);
                customer.setAddressConversion(country, addressId, city);
                // Add new Customer object to customerRoster
                customerRoster.add(customer);
            }
        } catch (SQLException e) {
            System.out.println("Update customer roster error: " + e);
        }
    }

    /**
     * Uses the provided country value to call the countryID method.
     * Uses the provided city value and countryID returned to find the division ID.
     * Uses the address1 and 2, and postal values with the division ID to create the addressID.
     * Then uses the address ID and customer name to search for a duplicate customer and provide an informational pop-up if one is found.
     * If no duplicate found, passes the provided values to the addCustomer method for insertion into the DB.
     * Catches any SQL error thrown by the DB.connect method in the event of a DB error.
     * Provides an error message one such error occurs.
     *
     * @param phone        Phone number.
     * @param customerName Customer name.
     * @param country      Country name.
     * @param address      Address.
     * @param address2     Address 2.
     * @param city         Division name.
     * @param postalCode   Postal code.
     */
    // Add customer to database if entry does not already exist
    public static void addNewCustomer(String customerName, String address, String address2,
                                      String postalCode, String phone, String city, String country) {
        // Get country, city and address Id's
        try {
            int countryId = calculateCountryId(country);
            int divisionId = calculateCityId(city, countryId);
            String addressId = calculateAddressId(address, address2, postalCode, divisionId);
            // Check if customer is new or already exists in database
            if (checkIfCustomerExists(customerName, addressId)) {
                // Try-with-resources block for database connection
                try (DB.connect) {
                    SQLStatement = "SELECT CUSTOMER_ID FROM customers WHERE Customer_Name = '"
                            + customerName + "' AND Address = '" + addressId + "'";
                    test = DBConnection.startConnection().prepareStatement(SQLStatement);
                    result = test.executeQuery();
                    result.next();
                    int active = result.getInt(1);
                    // Check if customer exists
                    if (active == 1) {
                        // Show alert if customer already exists
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(rb.getString("error"));
                        alert.setHeaderText(rb.getString("errorAddingCustomer"));
                        alert.setContentText(rb.getString("errorCustomerExists"));
                        alert.showAndWait();
                    }
                }
            }
            // Add new customer entry if customer does not already exist
            else {
                addCustomer(customerName, addressId, postalCode, phone, divisionId);
            }
        } catch (SQLException e) {
            // Create alert notifying user that a database connection is needed for this function
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorAddingCustomer"));
            alert.setContentText(rb.getString("errorDatabaseConnect"));
            alert.showAndWait();
            System.out.println("Add new customer error: " + e);
        }
    }

    /**
     * Uses the provided country string to match with the DB and return the country ID.
     * Catches any SQL error thrown by the DB.connect method in the event of a DB error.
     * Provides an error message one such error occurs.
     *
     * @param country Country name.
     * @return Returns country ID if match found, returns -1 if no match found.
     */
    // Convert country name to COUNTRY_ID and return value
    public static int calculateCountryId(String country) {
        // Try-with-resources block for database connection
        try (DB.connect) {
            SQLStatement = "SELECT COUNTRY_ID FROM countries WHERE Country = '" + country + "'";
            test = DBConnection.startConnection().prepareStatement(SQLStatement);
            result = test.executeQuery();
            result.next();
            return result.getInt(1);
        } catch (SQLException e) {
            System.out.println("Get Country id error: " + e);
            return -1;
        }
    }

    /**
     * Uses the provided city string and the country ID to search the DB and find the division ID.
     * Catches any SQL error thrown by the DB.connect method in the event of a DB error.
     * Provides an error message one such error occurs.
     *
     * @param city      Division name.
     * @param countryId Country ID number.
     * @return Returns City ID if match found, returns -1 if no match found.
     */
    // Return cityId if entry already exists. If no matching entry, create new entry and return new cityId.
    public static int calculateCityId(String city, int countryId) {
        // Try-with-resources block for database connection
        try (DB.connect) {
            ResultSet cityIdCheck = test.executeQuery("SELECT DIVISION_ID FROM first_level_divisions WHERE Division = '" + city +
                    "' AND COUNTRY_ID = '" + countryId + "'");
            // Check if entry already exists and return cityId if it does
            if (cityIdCheck.next()) {
                int cityId = cityIdCheck.getInt(1);
                cityIdCheck.close();
                return cityId;
            }
            return 0;
        } catch (SQLException e) {
            System.out.println("Get City id error: " + e);
            return -1;
        }
    }

    /**
     * Uses the provided address1, 2, and postalcode strings and the division ID to search the DB and find address ID.
     * If no address is found matching, will create a new address ID string and return the value.
     * Catches any SQL error thrown by the DB.connect method in the event of a DB error.
     * Provides an error message one such error occurs.
     *
     * @param postalCode Postal code.
     * @param address2   Address 2.
     * @param address    Address.
     * @param cityId     Division ID number.
     * @return Returns the concatenated string as addressId, or null if an error occurred.
     */
    public static String calculateAddressId(String address, String address2, String postalCode, int cityId) {
        // Try-with-resources block for database connection
        try (DB.connect) {
            ResultSet addressCheck = test.executeQuery("SELECT Address FROM customers WHERE Address = '" + address + address2 + "' AND Postal_Code = '"
                    + postalCode + "' AND DIVISION_ID = '" + cityId + "'");
            // Check if entry already exists and return addressId if it does
            if (addressCheck.next()) {
                //System.out.println("calculateAddressID existing entry: " + addressCheck);
                return addressCheck.getString(1);
            } else {
                // Create new entry with new address value
                String addressNotExist = ("calculateAddressID new entry: " + address + " " + address2 + ", " + postalCode + ", " + cityId);
                System.out.println("Try to create new addressId: " + addressNotExist);
                return address;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Uses the provided customerName and AddressID string to search the DB For an existing entry.
     * Catches any SQL error thrown by the DB.connect method in the event of a DB error.
     * Provides an error message one such error occurs.
     *
     * @param customerName Customer name.
     * @param addressId    Address ID.
     * @return Returns true if name/addressId match, returns false if not.
     */
    // Check if customer entry already exists
    private static boolean checkIfCustomerExists(String customerName, String addressId) {
        // Try-with-resources block for database connection
        try (DB.connect) {
            ResultSet customerIdCheck = test.executeQuery("SELECT Customer_ID FROM customers WHERE Customer_Name =" +
                    " '" + customerName + "' AND Address = '" + addressId + "'");
            // Check if entry already exists and return boolean
            return customerIdCheck.next();
        } catch (SQLException e) {
            System.out.println("Check customer error: " + e);
            return false;
        }
    }

    /**
     * Uses the provided values to add a customer to the DB.
     * Provides the user with an informational pop-up on success.
     * Catches any SQL error thrown by the DB.connect method in the event of a DB error.
     * Provides an error message one such error occurs.
     *
     * @param addressId    Address Id.
     * @param customerName Customer name.
     * @param postalCode   Postal code.
     * @param phone        Phone number.
     * @param divisionId   City ID number.
     */
    // Create new customer entry
    public static void addCustomer(String customerName, String addressId, String postalCode, String phone, int divisionId) {
        // Try-with-resources block for database connection
        try (DB.connect) {
            // Create new entry with new customerId value
            test.executeUpdate("INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone," +
                    " Created_By, Last_Updated_By, Division_ID)" +
                    "VALUES ('" + customerName + "', '" + addressId + "', '" + postalCode + "', '" + phone + "', '"
                    + currentUser + "', '" + currentUser + "', '" + divisionId + "')");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initModality(Modality.NONE);
            alert.setTitle(rb.getString("Success"));
            alert.setHeaderText(rb.getString("AddCustomer"));
            alert.setContentText(rb.getString("AddCustomerSuccess"));
            alert.showAndWait();
        } catch (SQLException e) {
            System.out.println("Add Customer Error: " + e);
        }
    }

    /**
     * Uses the provided values to call the countryID, cityID, then addressId methods.
     * Thenn passes the provided and returned values to the updateCustomer method.
     * Catches any SQL error thrown by the DB.connect method in the event of a DB error.
     * Provides an error message one such error occurs.
     *
     * @param phone        Phone number.
     * @param postalCode   Postal code.
     * @param customerName Customer name.
     * @param address      Address.
     * @param address2     Address 2.
     * @param city         Division name.
     * @param country      Country name.
     * @param customerId   Customer ID.
     */
    // Modify existing customer entry
    public static void modifyCustomer(int customerId, String customerName, String address, String address2,
                                      String city, String country, String postalCode, String phone) {
        try {
            // Find customer's country, city and addressId's
            int countryId = calculateCountryId(country);
            int cityId = calculateCityId(city, countryId);
            String addressId = calculateAddressId(address, address2, postalCode, cityId);
            updateCustomer(customerId, customerName, addressId, postalCode, cityId, phone);
        } catch (SQLException e) {
            // Create alert notifying user that a database connection is needed for this function
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorModifyingCustomer"));
            alert.setContentText(rb.getString("errorRequiresDatabase" + e));
            alert.showAndWait();
        }
    }

    /**
     * Uses the provided values to search the DB for an existing customer.
     * Returns the matching customerID.
     * Catches any SQL error thrown by the DB.connect method in the event of a DB error.
     * Provides an error message one such error occurs.
     *
     * @param customerName Customer name.
     * @param addressId    Address ID.
     * @return Returns Customer ID number if a match has been found, returns 0 if not.
     */
    // Get customerId from customerName and addressId
    private static int getCustomerId(String customerName, String addressId) throws SQLException {
        // Try-with-resources block for database connection
        try (DB.connect) {
            ResultSet customerIdResultSet = test.executeQuery("SELECT Customer_ID FROM customers WHERE Customer_Name = '" + customerName + "' AND Address = '" + addressId + "'");
            customerIdResultSet.next();
            return customerIdResultSet.getInt(1);
        } catch (SQLException e) {
            System.out.println("Get customer id error: " + e);
            return 0;
        }
    }

    /**
     * Uses the provided values to update a customer in the DB.
     * Provides the user with an informational pop-up on success.
     * Catches any SQL error thrown by the DB.connect method in the event of a DB error.
     * Provides an error message one such error occurs.
     *
     * @param addressId    Address ID.
     * @param customerName Customer name.
     * @param customerId   Custoemr ID number.
     * @param city         Division name.
     * @param postalCode   Postal code.
     * @param phone        Phone number.
     */
    // Update customer entry if unique
    private static void updateCustomer(int customerId, String customerName, String addressId, String postalCode, int city, String phone) throws SQLException {
        // Try-with-resources block for database connection
        try (DB.connect) {
            test.executeUpdate("UPDATE customers SET Customer_Name = '" + customerName + "', Address = '" + addressId + "', Postal_Code = '" + postalCode + "'"
                    + ", Phone = '" + phone + "', Division_ID = '" + city + "' WHERE Customer_ID = '" + customerId + "'");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initModality(Modality.NONE);
            alert.setTitle(rb.getString("Success"));
            alert.setHeaderText(rb.getString("ModifyCustomer"));
            alert.setContentText(rb.getString("ModifyCustomerSuccess"));
            alert.showAndWait();
        } catch (SQLException e) {
            System.out.println("Update customer error: " + e);
        }
    }

    /**
     * Uses the provided values to find an existing customerID from the DB.
     * Provides the user with an confirmation pop-up before deletion.
     * after confirmation checks if the customer ID matches an ID currently involved in an appointment.
     * if not, uses the customer ID to delete the customer entry from the DB.
     * if so, provides an informational pop up to delete the customers appointments first.
     * Catches any SQL error thrown by the DB.connect method in the event of a DB error.
     * Provides an error message one such error occurs.
     *
     * @param customerName Customer name.
     * @param Address      Address.
     */
    // check customer address / name against DB for customer id to delete, and confirm deletion
    public static void deleteCustomer(String customerName, String Address) {
        try {
            int idToRemove = getCustomerId(customerName, Address);
            ResultSet customerIdResultSet = test.executeQuery("SELECT * FROM customers");
            while (customerIdResultSet.next()) {
                // Show alert to confirm the removal of customer
                if (idToRemove == customerIdResultSet.getInt(1)) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.initModality(Modality.NONE);
                    alert.setTitle(rb.getString("confirmCustomerDelete"));
                    alert.setHeaderText(rb.getString("confirmDeletingCustomer"));
                    alert.setContentText(rb.getString("confirmDeletingCustomerMessage"));
                    Optional<ButtonType> result = alert.showAndWait();
                    // Check if OK button was clicked and set customer entry to inactive if it was
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        // Try-with-resources block for database connection
                        //check to see if customer belongs to an appointment before deleting it
                        ResultSet appointmentIdResultSet = test.executeQuery("SELECT Appointment_ID FROM appointments WHERE Customer_ID = '" + idToRemove + "'");

                        //testId = appointmentIdResultSet.getInt(1);
                        if (appointmentIdResultSet.next()) {
                            alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.initModality(Modality.NONE);
                            alert.setTitle(rb.getString("ErrorDeletingCustomer"));
                            alert.setHeaderText(rb.getString("CustomerInvolvement"));
                            alert.setContentText(rb.getString("CannotDeleteCustomer"));
                            alert.showAndWait();
                            return;
                        } else {
                            System.out.println("appointment resultSet doesnt match idToRemove, deletion = " + true);
                            try (DB.connect) {
                                test.executeUpdate("DELETE FROM customers WHERE Customer_ID = " + idToRemove);
                                alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.initModality(Modality.NONE);
                                alert.setTitle(rb.getString("Success"));
                                alert.setHeaderText(rb.getString("CustomerDeleted"));
                                alert.setContentText(rb.getString("CustomerDeletedSuccessfully"));
                                alert.showAndWait();
                                break;
                            } catch (SQLException e) {
                                // Create alert notifying user that a database connection is needed for this function
                                System.out.println("error deleting customer: " + e);
                            }
                        }
                    }
                }
            }
        } catch (SQLException throwables) {
            System.out.println("Error in deleting customer method: ");
            throwables.printStackTrace();
        }
        updateCustomerRoster();
    }

    /**
     * Cycles through the appointments in the database to build the appointment observable list.
     * catches an SQL error thrown by the DB.connect in the event of a error.
     */
    // Updates appointmentList with all current appointments that have yet to occur
    public static void updateAppointmentList() {
        // Try-with-resources block for database connection
        try (DB.connect) {
            int currentUserId = getUserId(getCurrentUser());
            // Retrieve appointmentList and clear
            ObservableList<Appointment> appointmentList = AppList.getAppointmentList();
            appointmentList.clear();
            // Create list of appointmentId's for all appointments
            SQLStatement = "SELECT Appointment_ID FROM appointments";
            test = DBConnection.startConnection().prepareStatement(SQLStatement);
            result = test.executeQuery();
            // Create list of appointmentId's for all appointments that are in the future
            ResultSet appointmentResultSet = result;
            ArrayList<Integer> appointmentIdList = new ArrayList<>();
            while (appointmentResultSet.next()) {
                appointmentIdList.add(appointmentResultSet.getInt(1));
            }
            // Create Appointment object for each appointmentId in list and add Appointment to appointmentList
            for (int appointmentId : appointmentIdList) {
                // Retrieve appointment info from database
                appointmentResultSet = test.executeQuery("SELECT Customer_ID, Title, Type, Description, Location, Contact_ID, User_ID, Start, End, Created_By FROM appointments WHERE Appointment_ID = '" + appointmentId + "'");
                appointmentResultSet.next();



                    int customerId = appointmentResultSet.getInt(1);
                    String title = appointmentResultSet.getString(2);
                    String type = appointmentResultSet.getString(3);
                    String description = appointmentResultSet.getString(4);
                    String location = appointmentResultSet.getString(5);
                    String contact = appointmentResultSet.getString(6);
                    String user = appointmentResultSet.getString(7);
                    Timestamp startTimestamp = appointmentResultSet.getTimestamp(8);
                    Timestamp endTimestamp = appointmentResultSet.getTimestamp(9);
                    String createdBy = appointmentResultSet.getString(10);
                    DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                    utcFormat.setTimeZone(getTimeZone("UTC"));
                    java.util.Date startDate = utcFormat.parse(startTimestamp.toString());
                    java.util.Date endDate = utcFormat.parse(endTimestamp.toString());
                    // Assign appointment info to new Appointment object
                    Appointment appointment = new Appointment(appointmentId, customerId, title, type, description, location, contact, user, startTimestamp, endTimestamp, startDate, endDate, createdBy);
                    // Add new Appointment object to appointmentList
                    appointmentList.add(appointment);

            }
        } catch (Exception e) {
            // Create alert notifying user that a database connection is needed for this function
            System.out.println("Update appointment error: " + e);

        }
    }

    /**
     * Uses the provided values to first transform UTC times to time stamps.
     * then call the overlapping appointments method comparing said time stamps.
     * then use the provided UTC times to check against the timeChecker method.
     * if all the tests pass, then the values provided will be used to get the customerID from the DB and pass.
     * the values to the addAppointment method.
     *
     * @param user        User name.
     * @param type        Type of appointment.
     * @param title       Title of appointment.
     * @param location    Location of appointment.
     * @param description Description of appointment.
     * @param contact     Contact name.
     * @param customer    Customer object.
     * @param endUTC      End of appointment in ZonedDateTime.
     * @param startUTC    Start of appointment in ZonedDateTime.
     * @throws SQLException Throws SQL exception if error wil BD occurred.
     */
    // Add appointment to database if entry does not already exist
    public static void addNewAppointment(Customer customer, String title, String type, String description, String location,
                                         String contact, String user, ZonedDateTime startUTC, ZonedDateTime endUTC) throws SQLException {
        // Transform ZonedDateTimes to Timestamps
        String startUTCString = startUTC.toString();
        startUTCString = startUTCString.substring(0, 10) + " " + startUTCString.substring(11, 16) + ":00";
        Timestamp startTimestamp = Timestamp.valueOf(startUTCString);
        String endUTCString = endUTC.toString();
        endUTCString = endUTCString.substring(0, 10) + " " + endUTCString.substring(11, 16) + ":00";
        Timestamp endTimestamp = Timestamp.valueOf(endUTCString);
        int appointmentId = 0;
        // Check if appointment overlaps with existing appointments. Show alert if it does.
        if (doesAppointmentOverlap(startTimestamp, endTimestamp, appointmentId)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorAddingAppointment"));
            alert.setContentText(rb.getString("errorAppointmentOverlaps"));
            alert.showAndWait();
            //System.out.println("error adding new appointment, appointment overlap issue");
        } else if (!timeChecker(startUTC, endUTC)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorAddingAppointment"));
            alert.setContentText(rb.getString("AppointmentTimeError"));
            alert.showAndWait();
        }
        // Add new appointment entry if it does not overlap with others
        else {
            ResultSet idResultSet = test.executeQuery("SELECT Contact_ID FROM contacts WHERE Contact_Name = '" + contact + "';");
            idResultSet.next();
            int contactId = idResultSet.getInt(1);
            int userId = getUserId(user);
            int customerId = customer.getCustomerId();
            addAppointment(customerId, title, type, description, location, contactId, userId, startTimestamp, endTimestamp);
        }
    }

    /**
     * Uses a regex to verify the dateTime pattern matches what is required for conversion to the UTC zonedDateTime.
     *
     * @param dateTime Date/Time string
     * @return Returns True if dateTime matches regex, false if not.
     */
    //uses regex to confirm user entered date time string format with required date time string format
    public static boolean dateMatcher(String dateTime) {
        Pattern datePattern = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}$");
        boolean match;
        match = datePattern.matcher(dateTime).matches();
        return match;
    }

    /**
     * Uses the provided UTC ZonedDateTimes to check if the time is outside of office hours.
     * or the the appointment is scheduled for more than one day.
     * or if the appointment is schedule during a weekend.
     * then returns the appropriate error message.
     *
     * @param startUTC Start time in ZonedDateTime.
     * @param endUTC   End time in ZonedDateTime.
     * @return Returns false if a match is found, true if no matches.
     */
    //TODO clean up
    public static boolean timeChecker(ZonedDateTime startUTC, ZonedDateTime endUTC) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        LocalTime midnight = MIDNIGHT;
        LocalDate apptStartDate = startUTC.toLocalDate();
        LocalTime apptStartTime = startUTC.toLocalTime();
        System.out.println(apptStartTime);
        LocalDate apptEndDate = endUTC.toLocalDate();
        //LocalTime apptEndTime = endUTC.toLocalTime();
        ZonedDateTime estStartZone = ZonedDateTime.ofInstant(startUTC.toInstant(), ZoneId.of("America/New_York"));
        LocalTime estStartCompare = estStartZone.toLocalTime();
        ZonedDateTime estEndZone = ZonedDateTime.ofInstant(endUTC.toInstant(), ZoneId.of("America/New_York"));
        LocalTime estEndCompare = estEndZone.toLocalTime();

        //int weekDay = apptStartDate.getDayOfWeek().getValue();

        if (!apptStartDate.isEqual(apptEndDate)) {
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("AppointmentTimeError"));
            alert.setContentText(rb.getString("AppointmentMultipleDays"));
            alert.showAndWait();
            return false;
        }
        //if (weekDay == 6 || weekDay == 7) {
        //    alert.setTitle(rb.getString("error"));
        //    alert.setHeaderText(rb.getString("AppointmentTimeError"));
        //    alert.setContentText(rb.getString("AppointmentOnWeekend"));
        //    alert.showAndWait();
        //    return false;
        //}
        if (estStartCompare.isBefore(midnight.plusHours(8)) || estStartCompare.isAfter(midnight.plusHours(22))) {
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("AppointmentTimeError"));
            alert.setContentText(rb.getString("AppointmentBeforeOpen") + "\n" + rb.getString("startTimeLbl") + " in EST = " + estStartCompare);
            alert.showAndWait();
            return false;
        }
        if (estEndCompare.isAfter(midnight.plusHours(22)) || estEndCompare.isBefore(midnight.plusHours(8))) {
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("AppointmentTimeError"));
            alert.setContentText(rb.getString("AppointmentAfterClose") + "\n" + rb.getString("endTimeLbl") + " in EST = " + estEndCompare);
            alert.showAndWait();
            return false;
        }
        if (apptStartDate.isBefore(LocalDate.now())) {
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("AppointmentTimeError"));
            alert.setContentText(rb.getString("AppointmentInPast"));
            alert.showAndWait();
            return false;
        }
        if (endUTC.isBefore(startUTC)) {
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("AppointmentTimeError"));
            alert.setContentText(rb.getString("AppointmentNoTime"));
            alert.showAndWait();
            return false;
        } else {
            return true;
        }
    }

    /**
     * Cycles through observable appointment list comparing start/end times for overlaps.
     * uses the appointment ID to exclude throwing a message on the appointment user is trying to update.
     *
     * @param appointmentId  Appointment ID number.
     * @param startTimestamp Start time as Timestamp.
     * @param endTimestamp   End time as Timestamp.
     * @return Returns true if overlap match found, returns false if no match found.
     */
    // Check if new appointment overlaps with any existing appointments and return true if it does
    public static boolean doesAppointmentOverlap(Timestamp startTimestamp, Timestamp endTimestamp, int appointmentId) {
        updateAppointmentList();
        ObservableList<Appointment> appointmentList = AppList.getAppointmentList();
        for (Appointment appointment : appointmentList) {
            Timestamp existingStartTimestamp = appointment.getStartTimestamp();
            Timestamp existingEndTimestamp = appointment.getEndTimestamp();
            int testId = appointment.getAppointmentId();
            // Check various scenarios for where overlap would occur and return true if any occur
            if (appointmentId != testId && startTimestamp.after(existingStartTimestamp) && startTimestamp.before(existingEndTimestamp)) {
                return true;
            }
            if (appointmentId != testId && endTimestamp.after(existingStartTimestamp) && endTimestamp.before(existingEndTimestamp)) {
                return true;
            }
            if (appointmentId != testId && startTimestamp.after(existingStartTimestamp) && endTimestamp.before(existingEndTimestamp)) {
                return true;
            }
            if (appointmentId != testId && startTimestamp.before(existingStartTimestamp) && endTimestamp.after(existingEndTimestamp)) {
                return true;
            }
            if (appointmentId != testId && startTimestamp.equals(existingStartTimestamp)) {
                return true;
            }
            if (appointmentId != testId && endTimestamp.equals(existingEndTimestamp)) {
                return true;
            }
        }
        // If none of the above scenarios occur, return false
        return false;
    }

    /**
     * Uses the provided values to insert into the DB.
     * Provides the user with an informational pop-up upon success.
     * catches an SQL error thrown by the DB.connect in the event of a error.
     *
     * @param endTimestamp   End time in Timestamp
     * @param startTimestamp Start time in Timestamp.
     * @param contact        Contact ID.
     * @param description    Description of appointment.
     * @param location       Location or appointment.
     * @param title          Title of appointment.
     * @param type           Type of appointment.
     * @param user           User ID number.
     * @param customerId     Customer ID number.
     */
    // Create new appointment entry
    private static void addAppointment(int customerId, String title, String type, String description, String location,
                                       int contact, int user, Timestamp startTimestamp, Timestamp endTimestamp) {
        // Try-with-resources block for database connection
        try (DB.connect) {
            ResultSet allAppointmentId = test.executeQuery("SELECT Appointment_ID FROM appointments ORDER BY Appointment_ID");
            allAppointmentId.next();
            int appointmentId = 1;
            // Check last appointmentId value and add one to it for new appointmentId value
            while (allAppointmentId.next()) {
                appointmentId = allAppointmentId.getInt(1) + 1;
            }
            // Create new entry with appointmentId value
            test.executeUpdate("INSERT INTO appointments (Appointment_ID, Title, Description, Location, Type, Start, End, Created_By, Last_Updated_by, Customer_ID, User_ID, Contact_ID)" +
                    "VALUES ('" + appointmentId + "', '" + title + "', '" + description + "', '" + location + "', '" + type + "', '" + startTimestamp + "', '" + endTimestamp + "', '" + currentUser + "', '" + currentUser + "', '" + customerId + "', '" + user + "', '" + contact + "')");
            allAppointmentId.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("Success"));
            alert.setHeaderText(rb.getString("AddAppointment"));
            alert.setContentText(rb.getString("AddAppointmentSuccess"));
            alert.showAndWait();

        } catch (SQLException e) {
            // Create alert notifying user that a database connection is needed for this function
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorAddingAppointment"));
            alert.setContentText(rb.getString("AddAppointmentFailed"));
            alert.showAndWait();
            System.out.println("SQL add appointment error: " + e);

        }
    }

    /**
     * uses the provided values to convert UTC to time stamps, call the overlap method and the timeChecker method.
     * then uses the provided values to pull the customerID add pass the values to the Update customer method.
     * catches an SQL error thrown by the DB.connect in the event of a error.
     *
     * @param user          User ID number.
     * @param type          Type of appointment.
     * @param title         Title of appointment.
     * @param location      Location of appointment.
     * @param description   Description of appointment.
     * @param contact       Contact ID number.
     * @param appointmentId Appointment ID number.
     * @param endUTC        End time in ZonedDateTime.
     * @param startUTC      Start time in ZonedDateTime.
     * @param customer      Customer object.
     * @throws SQLException if a DB error has occurred.
     */
    // Modify existing appointment entry
    public static void modifyAppointment(int appointmentId, Customer customer, String title, String type, String description, String location,
                                         int contact, int user, ZonedDateTime startUTC, ZonedDateTime endUTC) throws SQLException {
        // If timestamps variables are not null, convert them to utc format to update appointment with
        Timestamp startTimestamp = null;
        Timestamp endTimestamp = null;
        // Transform ZonedDateTimes to Timestamps if not null
        if (startUTC != null) {
            String startUTCString = startUTC.toString();
            startUTCString = startUTCString.substring(0, 10) + " " + startUTCString.substring(11, 16) + ":00";
            startTimestamp = Timestamp.valueOf(startUTCString);
            //System.out.println("modify App startTimeStamp not null: " + startTimestamp);
        }
        //if null value on start time set startTimeStamp to current value in DB to check for overlaps
        else {
            try (DB.connect) {
                ResultSet startTimeResult = test.executeQuery("SELECT Start FROM appointments WHERE Appointment_ID = '" + appointmentId + "'");
                startTimeResult.next();
                startTimestamp = startTimeResult.getTimestamp(1);
            } catch (SQLException e) {
                // Create alert notifying user that a database connection is needed for this function
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(rb.getString("error"));
                alert.setHeaderText(rb.getString("errorAddingAppointment"));
                alert.setContentText(rb.getString("errorRequiresDatabase"));
                alert.showAndWait();
                System.out.println("Error setting default startTimeStamp: " + e);
            }
        }
        if (endUTC != null) {
            String endUTCString = endUTC.toString();
            endUTCString = endUTCString.substring(0, 10) + " " + endUTCString.substring(11, 16) + ":00";
            endTimestamp = Timestamp.valueOf(endUTCString);
        }
        //if null value on end time set endTimeStamp to current value in DB to check for overlaps
        else {
            try (DB.connect) {
                ResultSet endTimeResult = test.executeQuery("SELECT End FROM appointments WHERE Appointment_ID = '" + appointmentId + "'");
                endTimeResult.next();
                endTimestamp = endTimeResult.getTimestamp(1);
            } catch (SQLException e) {
                // Create alert notifying user that a database connection is needed for this function
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(rb.getString("error"));
                alert.setHeaderText(rb.getString("errorAddingAppointment"));
                alert.setContentText(rb.getString("errorDatabaseConnect"));
                alert.showAndWait();
                System.out.println("Error setting default endTimestamp: " + e);
            }
        }
        try {
            // Check if appointment overlaps with other existing appointments. Show alert and return false if it does.
            if (doesAppointmentOverlap(startTimestamp, endTimestamp, appointmentId)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(rb.getString("error"));
                alert.setHeaderText(rb.getString("errorModifyingAppointment"));
                alert.setContentText(rb.getString("errorAppointmentOverlaps"));
                alert.showAndWait();
            }
            else if(!doesAppointmentOverlap(startTimestamp, endTimestamp, appointmentId)){
                // If overlap doesn't occur, update appointment entry and return true
                int customerId = customer.getCustomerId();
                updateAppointment(appointmentId, customerId, title, type, description, location, contact, user, startTimestamp, endTimestamp);
            }
        } catch (Exception e) {
            // Create alert notifying user that a database connection is needed for this function
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorAddingAppointment"));
            alert.setContentText(rb.getString("errorDatabaseConnect" + e));
            alert.showAndWait();
        }
    }

    /**
     * Uses the provided values to update the appointment in the DB where the current user and appointment ID match.
     * Provides a informational pop-up upon success.
     * catches an SQL error thrown by the DB.connect in the event of a error.
     *
     * @param appointmentId  Appointment ID number.
     * @param contact        Contact ID number.
     * @param description    Description of appointment.
     * @param location       Location of appointment.
     * @param title          Title of appointment.
     * @param type           Type of appointment.
     * @param user           User ID number.
     * @param customerId     Customer ID number.
     * @param startTimestamp Start time as Timestamp.
     * @param endTimestamp   End time as Timestamp.
     */
    // Update appointment entry if times/dates are valid
    private static void updateAppointment(int appointmentId, int customerId, String title, String type, String description, String location,
                                          int contact, int user, Timestamp startTimestamp, Timestamp endTimestamp) throws SQLException {

        // Try-with-resources block for database connection
        try (DB.connect) {
            ResultSet appToUpdate = test.executeQuery("SELECT Customer_ID, Title, Description, Location, Type, Start, End, User_ID, Contact_ID from appointments WHERE Appointment_ID = '" + appointmentId + "'");
            appToUpdate.next();
            int custIdSet = appToUpdate.getInt(1);
            String titleSet = appToUpdate.getString(2);
            String descSet = appToUpdate.getString(3);
            String locSet = appToUpdate.getString(4);
            String typeSet = appToUpdate.getString(5);
            Timestamp startSet = appToUpdate.getTimestamp(6);
            Timestamp endSet = appToUpdate.getTimestamp(7);
            //int userSet = appToUpdate.getInt(8);
            int contactSet = appToUpdate.getInt(9);

            // Check values based on appointmentId value and add changes where they occur
            test.executeUpdate("UPDATE appointments SET Last_Updated_By = '" + currentUser + "' WHERE Appointment_ID = '" + appointmentId + "'");
            if (customerId != custIdSet && customerId > 0) {
                test.executeUpdate("UPDATE appointments SET Customer_ID = '" + customerId + "' WHERE Appointment_ID = '" + appointmentId + "'");
                System.out.println("Tried to update appointment customerID");
            }
            if (!title.equals(titleSet) && !title.equals("")) {
                test.executeUpdate("UPDATE appointments SET Title = '" + title + "' WHERE Appointment_ID = '" + appointmentId + "'");
                System.out.println("Tried to update appointment title");
            }
            if (!type.equals(descSet) && !type.equals("")) {
                test.executeUpdate("UPDATE appointments SET Type = '" + type + "' WHERE Appointment_ID = '" + appointmentId + "'");
                System.out.println("Tried to update appointment type");
            }
            if (!description.equals(locSet) && !description.equals("")) {
                test.executeUpdate("UPDATE appointments SET Description = '" + description + "' WHERE Appointment_ID = '" + appointmentId + "'");
                System.out.println("Tried to update appointment description");
            }
            if (!location.equals(typeSet) && !location.equals("")) {
                test.executeUpdate("UPDATE appointments SET Location = '" + location + "' WHERE Appointment_ID = '" + appointmentId + "'");
                System.out.println("Tried to update appointment location");
            }
            if (startTimestamp != null && !startTimestamp.equals(startSet)) {
                test.executeUpdate("UPDATE appointments SET Start = '" + startTimestamp + "' WHERE Appointment_ID = '" + appointmentId + "'");
                System.out.println("Tried to update appointment start time");
            }
            if (endTimestamp != null && !endTimestamp.equals(endSet)) {
                test.executeUpdate("UPDATE appointments SET End = '" + endTimestamp + "' WHERE Appointment_ID = '" + appointmentId + "'");
                System.out.println("Tried to update appointment endTime");
            }
            if (contact != contactSet && contact > 0) {
                test.executeUpdate("UPDATE appointments SET Contact_ID = '" + contact + "' WHERE Appointment_ID = '" + appointmentId + "'");
                System.out.println("Tried to update appointment contactID");
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("Success"));
            alert.setHeaderText(rb.getString("ModifyAppointment"));
            alert.setContentText(rb.getString("ModifyAppointmentSuccess"));
            alert.showAndWait();
        } catch (Exception e) {
            System.out.println("Error update values in update appointment: " + e);
            System.out.println("Values to be updated from modifyapp to update app: \n Appointment ID: " + appointmentId + "\n Customer ID: " + customerId + "\n Title: " + title + "\n Type: "
                    + type + "\n Description: " + description + "\n Location: " + location + "\n Contact: "
                    + contact + "\n User: " + user + "\n StartTimeStamp: " + startTimestamp + "\n endTimeStamp: " + endTimestamp);
        }
    }

    /**
     * uses the appointment provided fromt he observable list to obtain the appointment ID.
     * uses the appointment ID to match an appointment for deletion from the DB.
     * Provides a confirmation pop-up before deletion.
     * After confirmation uses appointment ID match to delete the appointment from the DB.
     * catches an SQL error thrown by the DB.connect in the event of a error.
     *
     * @param appointmentToDelete Appointment object.
     */
    // Delete appointment entry from database by appointmentId
    public static void deleteAppointment(Appointment appointmentToDelete) {
        int appointmentId = appointmentToDelete.getAppointmentId();
        // Show alert to confirm deleting appointment entry
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle(rb.getString("confirmDelete"));
        alert.setHeaderText(rb.getString("confirmDeleteAppointment"));
        alert.setContentText(rb.getString("confirmDeleteAppointmentMessage"));
        Optional<ButtonType> result = alert.showAndWait();
        // Check if OK button was clicked and delete appointment entry if it was
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Try-with-resources block for database connection
            try (DB.connect) {
                test.executeUpdate("DELETE FROM appointments WHERE Appointment_ID = '" + appointmentId + "'");

            } catch (Exception e) {
                // Create alert notifying user that a database connection is needed for this function
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(rb.getString("error"));
                alert.setHeaderText(rb.getString("errorModifyingAppointment"));
                alert.setContentText(rb.getString("errorDatabaseConnect"));
                alert.showAndWait();
                return;
            }
            // Update appointmentList to remove deleted appointment
            updateAppointmentList();
        }
    }

    /**
     * Cycles through the appointment in the database to build the appointment observable list.
     * uses the localDate now plus one month as a comparison to populate the list.
     * returns the list of appointments within 1 month of now.
     * catches an SQL error thrown by the DB.connect in the event of a error.
     *
     * @param id User ID.
     * @return Returns appointments by user ID, or null if error occurred.
     */
    public static ObservableList<Appointment> getMonthlyAppointments(int id) {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        Appointment appointment;
        LocalDate begin = LocalDate.now();
        LocalDate end = LocalDate.now().plusMonths(1);
        try (DB.connect) {
            ResultSet results = test.executeQuery("SELECT * FROM appointments WHERE User_ID = '" + id + "' AND " +
                    "Start >= '" + begin + "' AND Start <= '" + end + "'");
            while (results.next()) {
                Timestamp startTimestamp = results.getTimestamp("Start");
                Timestamp endTimestamp = results.getTimestamp("End");
                DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                utcFormat.setTimeZone(getTimeZone("UTC"));
                java.util.Date startDate = utcFormat.parse(startTimestamp.toString());
                java.util.Date endDate = utcFormat.parse(endTimestamp.toString());
                appointment = new Appointment(results.getInt("Appointment_ID"), results.getInt("Customer_ID"), results.getString("Title"),
                        results.getString("Type"), results.getString("Description"), results.getString("Location"), results.getString("Contact_ID"),
                        results.getString("User_ID"),
                        startTimestamp, endTimestamp, startDate, endDate, results.getString("Created_By"));
                appointments.add(appointment);
            }
            test.close();
            return appointments;
        } catch (SQLException | ParseException e) {
            System.out.println("SQLException: " + e.getMessage());
            return null;
        }
    }

    /**
     * Cycles through the appointments in the database to build the appointments observable list.
     * uses the localDate now plus 1 week to compare appointments to populate the list.
     * returns the list of appointments within 1 week of now.
     * catches an SQL error thrown by the DB.connect in the event of a error.
     *
     * @param id User ID.
     * @return Appointments by user ID, or null if error occurred.
     */
    // Get Weekly Appointments
    public static ObservableList<Appointment> getWeeklyAppointments(int id) {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        Appointment appointment;
        LocalDate begin = LocalDate.now();
        LocalDate end = LocalDate.now().plusWeeks(1);
        try (DB.connect) {
            ResultSet results = test.executeQuery("SELECT * FROM appointments WHERE User_ID = '" + id + "' AND " +
                    "Start >= '" + begin + "' AND Start <= '" + end + "'");
            while (results.next()) {
                Timestamp startTimestamp = results.getTimestamp("Start");
                Timestamp endTimestamp = results.getTimestamp("End");
                DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                utcFormat.setTimeZone(getTimeZone("UTC"));
                java.util.Date startDate = utcFormat.parse(startTimestamp.toString());
                java.util.Date endDate = utcFormat.parse(endTimestamp.toString());
                appointment = new Appointment(results.getInt("Appointment_ID"), results.getInt("Customer_ID"), results.getString("Title"),
                        results.getString("Type"), results.getString("Description"), results.getString("Location"), results.getString("Contact_ID"),
                        results.getString("User_ID"),
                        startTimestamp, endTimestamp, startDate, endDate, results.getString("Created_By"));
                appointments.add(appointment);
            }
            test.close();
            return appointments;
        } catch (SQLException | ParseException e) {
            System.out.println("SQLException: " + e.getMessage());
            return null;
        }
    }

    /**
     * Uses an array list to store appointments based on who it was created by.
     * converts the timestamps to UTC, and saves the results to file ScheduleByClient.txt in the root folder.
     * Uses the txt document to pop-up an alert displaying the list of appointments.
     * catches an IOException error thrown by the conversion of timeStamp to ZoneDateTime.
     * @throws IOException Prints stack trace to console.
     */
    // Create report for each client's schedule
    public static void generateReport() throws IOException {
        updateAppointmentList();
        // Initialize report string
        StringBuilder report = new StringBuilder(rb.getString("userScheduleLbl") + "\n\n");
        ArrayList<String> userWithAppointments = new ArrayList<>();
        int appNumber = 1;
        // Check createdBy of each appointment. Add new createdBy's to ArrayList
        for (Appointment appointment : AppList.getAppointmentList()) {
            String user = appointment.getCreatedBy();
            if (!userWithAppointments.contains(user)) {
                userWithAppointments.add(user);
            }
        }
        report.append(rb.getString("userReportMessage")).append("\n\n");
        // Sort clients
        Collections.sort(userWithAppointments);
        for (String user : userWithAppointments) {
            // Add client name to report
            report.append(user).append(": \n");
            for (Appointment appointment : AppList.getAppointmentList()) {
                String appointmentClient = appointment.getCreatedBy();
                // Check if appointment createdBy matches client
                if (user.equals(appointmentClient)) {
                    // Get appointment date and title
                    String date = appointment.getDateString();
                    String type = appointment.getType();
                    Date startDate = appointment.getStartDate();
                    // Modify times to AM/PM format
                    String startTime = startDate.toString().substring(11, 16);
                    if (parseInt(startTime.substring(0, 2)) > 12) {
                        startTime = parseInt(startTime.substring(0, 2)) - 12 + startTime.substring(2, 5) + "PM";
                    } else if (parseInt(startTime.substring(0, 2)) == 12) {
                        startTime = startTime + "PM";
                    } else {
                        startTime = startTime + "AM";
                    }
                    Date endDate = appointment.getEndDate();
                    String endTime = endDate.toString().substring(11, 16);
                    if (parseInt(endTime.substring(0, 2)) > 12) {
                        endTime = parseInt(endTime.substring(0, 2)) - 12 + endTime.substring(2, 5) + "PM";
                    } else if (parseInt(endTime.substring(0, 2)) == 12) {
                        endTime = endTime + "PM";
                    } else {
                        endTime = endTime + "AM";
                    }
                    // Get timezone
                    String timeZone = startDate.toString().substring(20, 23);
                    // Add appointment info to report
                    report.append(appNumber).append(": ")
                            .append(rb.getString("dateLbl")).append(": ").append(date).append(" | ")
                            .append(" ").append(rb.getString("typeLbl")).append(": ").append(type).append(" | ")
                            .append(" ").append(rb.getString("startTimeLbl")).append(": ").append(startTime).append(" -> ")
                            .append(rb.getString("endTimeLbl")).append(": ").append(endTime).append(" | ")
                            .append(" ").append(rb.getString("timeZoneLbl")).append(timeZone).append(". \n");

                    appNumber++;
                }
            }
            // Add paragraph break between consultants
            report.append(rb.getString("totalAppsLbl")).append(": ").append(appNumber-1).append("\n \n");
            appNumber=1;
        }
        // Print report to ScheduleByUser.txt. Overwrite file if exists.
        try {
            Path path = Paths.get("ScheduleByUser.txt");
            Files.write(path, Collections.singletonList(report.toString()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedReader br = new BufferedReader(new FileReader("ScheduleByUser.txt"))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initModality(Modality.NONE);
            alert.setResizable(true);
            alert.getDialogPane().setPrefSize(800, 1020);
            TextArea area = new TextArea(everything);
            area.setWrapText(true);
            area.setEditable(false);
            alert.setTitle(rb.getString("appointmentByTitle"));
            alert.setHeaderText(rb.getString("appointmentByUserHeader"));
            alert.getDialogPane().setContent(area);
            alert.showAndWait();
        }
    }

    /**
     * Uses an array list to store appointments based on which customer it applies too.
     * converts the timestamps to UTC, and saves the results to file ScheduleByCustomer.txt in the root folder.
     * Uses the txt document to pop-up an alert displaying the list of appointments.
     * catches an IOException error thrown by the conversion of timeStamp to ZoneDateTime.
     *<br><br>
     * About Lambda Discussion
     * The 12 lambdas in this section are used with the switch statement cases.
     * Although it was not as necessary or beneficial as the FXML lambdas in the other classes, it did save
     * me from writing out the 12 break; statements in each case. Therefore still saving a total of
     * 12 lines of code by adding an -> to each case.
     */

    public static void getReportByCustomer() {
        updateAppointmentList();
        // Initialize report string
        StringBuilder report = new StringBuilder(rb.getString("customerScheduleLbl") + "\n\n");
        ArrayList<Integer> customerIdsWithAppointments = new ArrayList<>();
        ArrayList<String> typeHolder = new ArrayList<>();
        ArrayList<Integer> typeCounter = new ArrayList<>();
        ArrayList<Integer> monthCounter = new ArrayList<>();
        ArrayList<String> monthNames = new ArrayList<>();
        int appNumber = 1;
        int countHolder;
        monthNames.add("January");monthCounter.add(0);
        monthNames.add("February");monthCounter.add(0);
        monthNames.add("March");monthCounter.add(0);
        monthNames.add("April");monthCounter.add(0);
        monthNames.add("May");monthCounter.add(0);
        monthNames.add("June");monthCounter.add(0);
        monthNames.add("July");monthCounter.add(0);
        monthNames.add("August");monthCounter.add(0);
        monthNames.add("September");monthCounter.add(0);
        monthNames.add("October");monthCounter.add(0);
        monthNames.add("November");monthCounter.add(0);
        monthNames.add("December");monthCounter.add(0);
        // Check customerId of each appointment. Add new customerId's to ArrayList
        for (Appointment appointment : AppList.getAppointmentList()) {
            int customerId = appointment.getCustomerId();
            String typeToCompare = appointment.getType();
            if (!customerIdsWithAppointments.contains(customerId)) {
                customerIdsWithAppointments.add(customerId);
            }
            if (!typeHolder.contains(typeToCompare)) {
                typeHolder.add(typeToCompare);
                typeCounter.add(0);
            }
        }
        // Sort customerId's
        Collections.sort(customerIdsWithAppointments);
        Collections.sort(typeHolder);
        updateCustomerRoster();
        //for (int customerId : customerIdsWithAppointments) {
            /*for (Customer customer : CustomerList.getCustomerRoster()) {
                // Go through customerRoster and find match for customerId
                int customerIdToCheck = customer.getCustomerId();
                if (customerId == customerIdToCheck) {
                    // Add customer name to report
                    report.append(customer.getCustomerName()).append(": \n");
                }
            }*/
            for (Appointment appointment : AppList.getAppointmentList()) {
                //int appointmentCustomerId = appointment.getCustomerId();
                // Check if appointment's customerId matches customer
                //if (customerId == appointmentCustomerId) {
                    // Get appointment date and description
                    String date = appointment.getDateString();
                    String type = appointment.getType();
                    //Date startDate = appointment.getStartDate();
                    String month = date.substring(0, 2);

                    /*
                    // Modify times to AM/PM format
                    String startTime = startDate.toString().substring(11, 16);
                    if (parseInt(startTime.substring(0, 2)) > 12) {
                        startTime = parseInt(startTime.substring(0, 2)) - 12 + startTime.substring(2, 5) + "PM";
                    } else if (parseInt(startTime.substring(0, 2)) == 12) {
                        startTime = startTime + "PM";
                    } else {
                        startTime = startTime + "AM";
                    }
                    Date endDate = appointment.getEndDate();
                    String endTime = endDate.toString().substring(11, 16);
                    if (parseInt(endTime.substring(0, 2)) > 12) {
                        endTime = parseInt(endTime.substring(0, 2)) - 12 + endTime.substring(2, 5) + "PM";
                    } else if (parseInt(endTime.substring(0, 2)) == 12) {
                        endTime = endTime + "PM";
                    } else {
                        endTime = endTime + "AM";
                    }
                    // Get timezone
                    String timeZone = startDate.toString().substring(20, 23);
                    // Add appointment info to report

                    report.append(appNumber).append(": ")
                            .append(rb.getString("dateLbl")).append(": ").append(date).append("\n")
                            .append("    ").append(rb.getString("typeLbl")).append(": ").append(type).append("\n")
                            .append("    ").append(rb.getString("startTimeLbl")).append(": ").append(startTime).append("\n")
                            .append("    ").append(rb.getString("endTimeLbl")).append(": ").append(endTime).append("\n")
                            .append("    ").append(rb.getString("timeZoneLbl")).append(timeZone).append(". \n\n");
                    */
                    appNumber++;

                    for (int i = 0; i < typeHolder.size();) {
                        do {
                            if (typeHolder.get(i) != null && type.equals(typeHolder.get(i))) {
                                countHolder = typeCounter.get(i) + 1;
                                typeCounter.set(i, countHolder);
                                //System.out.println(typeHolder.get(i) + ": " + typeCounter.get(i) + "\n");
                            }
                        }
                            while (++i < typeCounter.size() && typeCounter.get(i).equals(typeCounter.get(i - 1)));

                    }
                    for(int i = 0; i < typeHolder.size();) {
                        do {
                            if (typeHolder.get(i) != null && type.equals(typeHolder.get(i))) {
                                switch (month) {
                                    case "01" -> {
                                        countHolder = monthCounter.get(0);
                                        monthCounter.set(0, countHolder + 1);
                                    }
                                    case "02" -> {
                                        countHolder = monthCounter.get(1);
                                        monthCounter.set(1, countHolder + 1);
                                    }
                                    case "03" -> {
                                        countHolder = monthCounter.get(2);
                                        monthCounter.set(2, countHolder + 1);
                                    }
                                    case "04" -> {
                                        countHolder = monthCounter.get(3);
                                        monthCounter.set(3, countHolder + 1);
                                    }
                                    case "05" -> {
                                        countHolder = monthCounter.get(4);
                                        monthCounter.set(4, countHolder + 1);
                                    }
                                    case "06" -> {
                                        countHolder = monthCounter.get(5);
                                        monthCounter.set(5, countHolder + 1);
                                    }
                                    case "07" -> {
                                        countHolder = monthCounter.get(6);
                                        monthCounter.set(6, countHolder + 1);
                                    }
                                    case "08" -> {
                                        countHolder = monthCounter.get(7);
                                        monthCounter.set(7, countHolder + 1);
                                    }
                                    case "09" -> {
                                        countHolder = monthCounter.get(8);
                                        monthCounter.set(8, countHolder + 1);
                                    }
                                    case "10" -> {
                                        countHolder = monthCounter.get(9);
                                        monthCounter.set(9, countHolder + 1);
                                    }
                                    case "11" -> {
                                        countHolder = monthCounter.get(10);
                                        monthCounter.set(10, countHolder + 1);
                                    }
                                    case "12" -> {
                                        countHolder = monthCounter.get(11);
                                        monthCounter.set(11, countHolder + 1);
                                    }
                                }
                            }
                        }
                        while (++i < typeCounter.size() && typeCounter.get(i).equals(typeCounter.get(i - 1)));
                    }
                }
            //}
            report.append("Month").append("          ").append("Type").append("          ").append("Count\n");
            /*for (int i = 0;  i < typeHolder.size();) {
                if(typeCounter.get(i) != 0) {
                    report.append(rb.getString("totalAppsByTypeLbl")).append(" | ").append(typeHolder.get(i)).append(": ").append(typeCounter.get(i)).append("\n");
                    typeCounter.set(i, 0);
                }
                i++;
            }*/
            for (int j = 0;  j < monthCounter.size();) {

                    for (int i = 0; i < typeHolder.size(); ) {
                        if (monthCounter.get(j) != 0 && typeCounter.get(i) != 0) {
                            report.append(monthNames.get(j)).append("              ").append(typeHolder.get(i)).append("              ").append(monthCounter.get(j)).append("\n");
                            monthCounter.set(j, 0);
                        }
                        i++;
                    }
                    j++;
                }

            report.append("--------------------------------------------------\n\n");
            appNumber=1;
        //}
        // Print report to ScheduleByCustomer.txt. Overwrite file if exists.
        try {
            Path path = Paths.get("ScheduleByCustomer.txt");
            Files.write(path, Collections.singletonList(report.toString()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedReader br = new BufferedReader(new FileReader("ScheduleByCustomer.txt"))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initModality(Modality.NONE);
            alert.setResizable(true);
            alert.getDialogPane().setPrefSize(800, 1020);
            TextArea area = new TextArea(everything);
            area.setWrapText(true);
            area.setEditable(false);
            alert.setTitle(rb.getString("appointmentByTitle"));
            alert.setHeaderText(rb.getString("appointmentByCustHeader"));
            alert.getDialogPane().setContent(area);
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Uses an array list to store appointments based on which customer it applies too.
     * converts the timestamps to UTC, and saves the results to file ScheduleByCustomer.txt in the root folder.
     * Uses the txt document to pop-up an alert displaying the list of appointments.
     * catches an IOException error thrown by the conversion of timeStamp to ZoneDateTime.
     *<br><br>
     * About Lambda Discussion
     * The 3 lambdas in this section are used with the switch statement cases.
     * Although it was not as necessary or beneficial as the FXML lambdas in the other classes, it did save
     * me from writing out the 3 break; statements in each case. Therefore still saving a total of
     * 3 lines of code by adding an -> to each case.
     *
     */
    // Create report for each contact's schedule
    public static void generateReportByContact() { //ObservableList<Appointment>
        updateAppointmentList();
        // Initialize report string
        StringBuilder report = new StringBuilder(rb.getString("contactScheduleLbl") + "\n\n");
        //ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
        //appointmentList.clear();
        ArrayList<String> contactWithAppointments = new ArrayList<>();
        int appNumber = 1;
        // Check contact of each appointment. Add new contact's to ArrayList
        for (Appointment appointment : AppList.getAppointmentList()) {
            String contact = appointment.getContact();
            if (!contactWithAppointments.contains(contact)) {
                contactWithAppointments.add(contact);
            }
        }
        // Sort contacts
        Collections.sort(contactWithAppointments);
        for (String contact : contactWithAppointments) {
            // Add contact name to report
            switch (contact) {
                case "1" -> report.append(contact).append("- Anika Costa: \n");
                case "2" -> report.append(contact).append("- Daniel Garcia: \n");
                case "3" -> report.append(contact).append("- Li Lee: \n");
            }
            for (Appointment appointment : AppList.getAppointmentList()) {
                String appointmentContact = appointment.getContact();
                // Check if appointment matches contact
                if (contact.equals(appointmentContact)) {
                    //appointmentList.add(appointment);
                    //System.out.println(appointmentList);
                    //appointmentList.add(appointment);
                    // Get appointment date, start/end times, type, date, description, title, customer id, appointment id
                    String date = appointment.getDateString();
                    String type = appointment.getType();
                    Date startDate = appointment.getStartDate();
                    String description = appointment.getDescription();
                    String title = appointment.getTitle();
                    int customerId = appointment.getCustomerId();
                    int appId = appointment.getAppointmentId();
                    // Modify times to AM/PM format
                    String startTime = startDate.toString().substring(11, 16);
                    if (parseInt(startTime.substring(0, 2)) > 12) {
                        startTime = parseInt(startTime.substring(0, 2)) - 12 + startTime.substring(2, 5) + "PM";
                    } else if (parseInt(startTime.substring(0, 2)) == 12) {
                        startTime = startTime + "PM";
                    } else {
                        startTime = startTime + "AM";
                    }
                    Date endDate = appointment.getEndDate();
                    String endTime = endDate.toString().substring(11, 16);
                    if (parseInt(endTime.substring(0, 2)) > 12) {
                        endTime = parseInt(endTime.substring(0, 2)) - 12 + endTime.substring(2, 5) + "PM";
                    } else if (parseInt(endTime.substring(0, 2)) == 12) {
                        endTime = endTime + "PM";
                    } else {
                        endTime = endTime + "AM";
                    }
                    // Get timezone
                    String timeZone = startDate.toString().substring(20, 23);
                    // Add appointment info to report
                    report.append(appNumber).append(": ")
                            .append(" ").append(date).append(" @ ").append(startTime).append(" -> ").append(endTime).append(" ").append(timeZone).append(" | ")
                            .append(type).append(" | ").append(description).append(" | ").append(title).append(" | ")
                            .append(rb.getString("customerLbl")).append(": ").append(customerId).append(" | ").append(rb.getString("appIDLbl")).append(": ").append(appId).append("\n");
                    appNumber++;
                }
            }
            // Add paragraph break between consultants
            report.append("--------------------------------------------------------------------\n\n");
            appNumber=1;

        }
        // Print report to ScheduleByContact.txt. Overwrite file if exists.
        try {
            Path path = Paths.get("ScheduleByContact.txt");
            Files.write(path, Collections.singletonList(report.toString()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedReader br = new BufferedReader(new FileReader("ScheduleByContact.txt"))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initModality(Modality.NONE);
            alert.setResizable(true);
            alert.getDialogPane().setPrefSize(800, 1020);
            TextArea area = new TextArea(everything);
            area.setWrapText(true);
            area.setEditable(false);
            alert.setTitle(rb.getString("appointmentByTitle"));
            alert.setHeaderText(rb.getString("appointmentByContactHeader"));
            alert.getDialogPane().setContent(area);
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //return appointmentList;
    }
}