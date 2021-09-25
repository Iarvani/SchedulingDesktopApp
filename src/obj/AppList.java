package obj;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/** This class is used to create and return the appointments observable list. */
public class AppList {

    private static ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
    /**
     * Method used to return the current appointment observable list.
     * @return Returns appointment list.
     */
    // Getter for appointmentList
    public static ObservableList<Appointment> getAppointmentList() {
        return appointmentList;
    }
}