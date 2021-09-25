package Layout;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;

/** This class creates an app to create and manage customers and appointments. */
public class Main extends Application {
    /**
     * Starts program
     * loads page elements from login.fxml
     * manipulates elements from LoginController
     * @throws Exception if loading Scene/Resources occurs
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
    /**
     * Starts main
     * launches args within
     * @param args Launches arguments.
     */
    public static void main(String[] args) {
        //Locale.setDefault(new Locale("fr"));
        launch(args);
    }
}
