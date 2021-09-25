package obj;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;

/** This class is used to create and obtain the customer observable list. */
public class CustomerList {

    private static ObservableList<Customer> customerRoster = FXCollections.observableArrayList();
    /**
     * Used to return the current customer observable list
     * @return Returns customer observable list.
     */
    // Getter for customerRoster
    public static ObservableList<Customer> getCustomerRoster() {
        return customerRoster;
    }
}