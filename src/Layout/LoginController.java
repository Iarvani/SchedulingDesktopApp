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
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;
import static obj.DBManagement.checkLogInCredentials;
import static obj.DBManagement.setResBundle;

/** This class is used to control the Login FXML screen. */
public class LoginController {
    // Signal as to whether a database error message needs to be shown
    public static int databaseError = 0;
    private static String lang;
    private static final String zoneId = TimeZone.getDefault().toZoneId().toString();
    private static ResourceBundle rb;
    @FXML private TextField loginNameField = null;
    @FXML private TextField loginPassField = null;
    @FXML private Button loginBtn;
    @FXML private Button loginExitBtn;
    @FXML private Button loginChangeLangBtn;
    @FXML private Label loginUsernameLbl;
    @FXML private Label loginPasswordLbl;
    @FXML private Label loginLangLbl;
    @FXML private Label currentLangLbl;
    @FXML private Label timeZoneLbl;
    @FXML private Label currentTimeZoneLbl;
    //Locale locale = Locale.getDefault();

    /**
     * Used by the rest of the program to get the Lang string.
     * This will be used to determine which resource bundle to use.
     * for the language of the program.
     * @return Returns the language string.
     */
    //get lang value to set resource bundle
    public static String getLang () {
        return lang;
    }
    private static void setLang(String langSet) {lang = langSet;}
    /**
     * Set Local variables for English/French language codes
     * Assigns a language string based off the Local vars
     * Language string is used by the rest of the system to get the...
     * proper resource bundle for translation.
     */
    //TODO Clean up
    //used to set lang value, which is then used to pick the resource bundle
    private static void setLangVariable() {
        //set locale variables for comparison
        //System.out.println(timeZone);
        //Locale simpEnglish = new Locale("en","US","");
        //Locale simpFrench = new Locale("en","FR","");
        //use default locale to set initial value of string lang
        //if(simpEnglish.equals(Locale.getDefault())) {
        //    setLang("English");
        //}
        //else if(simpFrench.equals(Locale.getDefault())) {
        //    setLang("French");
        //}
        if(Locale.getDefault().getLanguage().equals("en")) {
            lang = "English";
        }
        else if (Locale.getDefault().getLanguage().equals("fr")) {
            lang = "French";
        }
        //set resource bundle based on lang string
        rb = setResBundle();
    }
    /**
     * Sets the text elements on the page to the current language.
     * */
    private void setLangTitles() {
        rb = setResBundle();
        loginNameField.setPromptText(rb.getString("loginNameFieldPrompt"));
        loginPassField.setPromptText(rb.getString("loginPassFieldPrompt"));
        loginBtn.setText(rb.getString("logInBtn"));
        loginExitBtn.setText(rb.getString("exitBtn"));
        loginChangeLangBtn.setText(rb.getString("changeLangBtn"));
        loginUsernameLbl.setText(rb.getString("userNameLbl"));
        loginPasswordLbl.setText(rb.getString("passwordLbl"));
        loginLangLbl.setText(rb.getString("langLbl"));
        timeZoneLbl.setText(rb.getString("timeZoneLbl"));
        currentLangLbl.setText(getLang());
    }
    /**
     * Added a change lang button if the program translates to french by default,
     * but you would rather have it in english, or visa versa.
     * Also assigns the translation based on the current lang string,
     * this is based on the Local.Default of the operating system.
     */

    //set language and change text
    public void changeLangBtn() {
        if (lang.equals("English")) {
            setLang("French");
            setLangTitles();


        }
        else if (lang.equals("French")) {
            setLang("English");
            setLangTitles();

        }

    }

    /**
     * Applies the controls for the FXML Exit porgram button
     * Provides a confirmation pop-up before the program closes.
     */
    //Button controls
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
     * Applies controls to the FXML login button
     * Runs a check on the database to verify username/password
     * Applies a pop-up alert if username/password entry is null
     * If credentials check out, redirects to homescreen FXML
     */
    // Submit log-in credentials to be checked
    @FXML
    private void logInButton(ActionEvent event) throws IOException{
        // Retrieves user's inputs and clears password field
        String userName = loginNameField.getText();
        String password = loginPassField.getText();
        // Returns error message in window if either username or password fields are blank
        if (userName.equals("") || password.equals("")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initModality(Modality.NONE);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("loginError"));
            alert.setContentText(rb.getString("loginErrorMessage"));
            alert.showAndWait();
            return;
        }
        // Check credentials against database
        boolean correctCredentials = checkLogInCredentials(userName, password);
        // Check if credentials were correct
        if (correctCredentials) {
            try {
                // Show main screen
                Parent mainScreenParent = FXMLLoader.load(getClass().getResource("HomeScreen.fxml"));
                Scene mainScreenScene = new Scene(mainScreenParent);
                Stage mainScreenStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                mainScreenStage.setScene(mainScreenScene);
                mainScreenStage.show();
            }
            catch (IOException e) {
                System.out.println("Log in check error: " + e);
            }
        }
        // Check if databaseError has been set
        else if (databaseError > 0) {
            // Show connection error message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initModality(Modality.NONE);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("loginError"));
            alert.setContentText(rb.getString("lblConnectionError"));
            alert.showAndWait();
        }
    }

    //-----------------------------------------------------------------------------------------------------------------
    //Put this in initialize to auto fill login with correct values, use it for testing
    //-----------------------------------------------------------------------------------------------------------------
    /**
     * This was used to auto fill the username/password fields for
     * quicker access into the program during the testing phase.
     */

    @FXML
    public void loginCredsBtn () {
        loginNameField.setText("admin");
        loginPassField.setText("admin");
    }
    /**
     * Refreshes the DB error var which triggers a pop-up if an
     * error is reached.
     */
    //Check for database error message return
    @FXML
    public static void incrementDatabaseError() {
        databaseError++;
    }
    /**
     * Starts the listed methods on load of the login page.
     * Grabs the current language string to use for the language the
     * program will be presented in.
     * Remove the comment on loginCredsBtn() to autofill the fields.
     */
    @FXML
    public void initialize() {
        //loginCredsBtn();
        //System.out.println(zoneId);
        //System.out.println(Locale.getDefault().getLanguage());
        setLangVariable();
        currentLangLbl.setText(getLang());
        currentTimeZoneLbl.setText(zoneId);
        setLangTitles();
    }
}



