package obj;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.util.ResourceBundle;
import static obj.DBManagement.setResBundle;

/** This class is used to create a customer object and retrieve the values associated with a customer. */
public class Customer {

    private final IntegerProperty customerId;
    private final StringProperty customerName;
    private final IntegerProperty active;
    private final StringProperty addressId;
    private final StringProperty address;
    private final StringProperty address2;
    private final StringProperty postalCode;
    private final StringProperty phone;
    private final IntegerProperty divisionId;
    private final StringProperty division;
    private final IntegerProperty countryId;
    private final StringProperty country;
    private final StringProperty addressConversion;
    /**
     * Default constructor for the customer Class.
     * Adds the values necessary to build the customer observable list.
     *
     */
    // Constructor
    public Customer() {
        customerId = new SimpleIntegerProperty();
        customerName = new SimpleStringProperty();
        active = new SimpleIntegerProperty();
        addressId = new SimpleStringProperty();
        address = new SimpleStringProperty();
        address2 = new SimpleStringProperty();
        postalCode = new SimpleStringProperty();
        phone = new SimpleStringProperty();
        divisionId = new SimpleIntegerProperty();
        division = new SimpleStringProperty();
        countryId = new SimpleIntegerProperty();
        country = new SimpleStringProperty();
        addressConversion = new SimpleStringProperty();
    }
    /** Customer ID setter.
     * @param customerId Customer ID number.
     */
    // Setters
    public void setCustomerId(int customerId) {
        this.customerId.set(customerId);
    }
    /** Customer name setter.
     * @param customerName Customer name.
     */
    public void setCustomerName(String customerName) {
        this.customerName.set(customerName);
    }
    /** Address ID setter.
     * @param addressId Address ID number.
     */
    public void setAddressId(String addressId) {
        this.addressId.set(addressId);
    }
    /** Address setter.
     * @param address Customer address.
     */
    public void setAddress(String address) {
        this.address.set(address);
    }
    /** Address 2 setter.
     * @param address2 Customer address 2.
     */
    public void setAddress2(String address2) {
        this.address2.set(address2);
    }
    /** Postal code setter.
     * @param postalCode Postal code number.
     */
    public void setPostalCode(String postalCode) {
        this.postalCode.set(postalCode);
    }
    /** Phone setter.
     * @param phone Customer phone number.
     */
    public void setPhone(String phone) {
        this.phone.set(phone);
    }
    /** Division ID setter.
     * @param divisionId Division ID number.
     */
    public void setDivisionId(int divisionId) {
        this.divisionId.set(divisionId);
    }
    /** Division setter.
     * @param division Division name.
     */
    public void setDivision(String division) {
        this.division.set(division);
    }
    /** Country ID setter.
     * @param countryId Country ID number.
     */
    public void setCountryId(int countryId) {
        this.countryId.set(countryId);
    }
    /** Country setter.
     * @param country Country name.
     */
    public void setCountry(String country) {
        this.country.set(country);
    }
    /** Customer address, address2, postal, country, city concatenate.
     * @param country Country name.
     * @param addressId Address, address2, postal code.
     * @param division Division name.
     */
    public void setAddressConversion(String country, String addressId, String division) {
        this.addressConversion.set(country + ". address: " + addressId + ", " + division);
    }
    /** Customer ID property getter.
     * @return Returns customer ID property.
     */
    // Getters
    public IntegerProperty customerIdProperty() { return customerId; }
    /** Customer name property getter.
     * @return Returns customer name property.
     */
    public StringProperty customerNameProperty() {
        return customerName;
    }
    /** Address ID property getter.
     * @return Returns address ID property.
     */
    public StringProperty addressIdProperty() {
        return addressId;
    }
    /** Address property getter.
     * @return Returns address property.
     */
    public StringProperty addressProperty() { return address; }
    /** Address 2 property getter.
     * @return Returns address 2 property.
     */
    public StringProperty address2Property() {
        return address2;
    }
    /** Postal code property getter.
     * @return Returns postal code property.
     */
    public StringProperty postalCodeProperty() {
        return postalCode;
    }
    /** Phone property getter.
     * @return Returns phone property.
     */
    public StringProperty phoneProperty() {
        return phone;
    }
    /** Division ID property getter.
     * @return Returns division ID property.
     */
    public IntegerProperty divisionIdProperty() {
        return divisionId;
    }
    /** Division property getter.
     * @return Returns division property.
     */
    public StringProperty divisionProperty() {
        return division;
    }
    /** Country ID property getter.
     * @return Returns country ID property.
     */
    public IntegerProperty countryIdProperty() {
        return countryId;
    }
    /** Country property getter.
     * @return Returns country property.
     */
    public StringProperty countryProperty() {
        return country;
    }
    /** Customer ID getter.
     * @return Returns Customer ID.
     */
    public int getCustomerId() { return this.customerId.get(); }
    /** Address conversion getter.
     * @return Returns Address, address2, postal code, country, city concatenated.
     */
    public StringProperty getAddressConversion() {return addressConversion; }
    /** Customer name getter.
     * @return Returns customer name.
     */
    public String getCustomerName() {
        return this.customerName.get();
    }
    /** Address ID getter.
     * @return Returns address, address2, postal code.
     */
    public String getAddressId() {
        return this.addressId.get();
    }
    /** Address getter.
     * @return Returns address.
     */
    public String getAddress() {
        return this.address.get();
    }
    /** Address 2 getter.
     * @return Returns address2.
     */
    public String getAddress2() {
        return this.address2.get();
    }
    /** Postal code getter.
     * @return Returns postal code.
     */
    public String getPostalCode() {
        return this.postalCode.get();
    }
    /** Phone getter.
     * @return Returns phone number.
     */
    public String getPhone() {
        return this.phone.get();
    }
    /** Division ID getter.
     * @return Returns city ID.
     */
    public int getDivisionId() {
        return this.divisionId.get();
    }
    /** Division getter.
     * @return Returns city name.
     */
    public String getDivision() {
        return this.division.get();
    }
    /** Country ID getter.
     * @return Returns country ID property.
     */
    public int getCountryId() {
        return this.countryId.get();
    }
    /** Country getter.
     * @return Returns country name.
     */
    public String getCountry() {
        return this.country.get();
    }
    /**
     * Uses the inputted values to check if the fields have been filled out correctly.
     * Returns error messages based on which value has failed the check.
     * @param address Address string.
     * @param country Country name.
     * @param customerName Customer name.
     * @param division City name.
     * @param phone Phone number string.
     * @param zip Postal code.
     * @return Returns corresponding error message string.
     */
    // Validation
    public static String isCustomerValid(String customerName, String address, String division,
                                         String country, String zip, String phone) {
        ResourceBundle rb = setResBundle();
        String errorMessage = "";
        if (customerName.length() == 0) {
            errorMessage = errorMessage + rb.getString("errorCustomerName");
        }
        if (address.length() == 0) {
            errorMessage = errorMessage + rb.getString("errorAddress");
        }
        if (division.length() == 0) {
            errorMessage = errorMessage + rb.getString("errorCity");
        }
        if (country.length() == 0) {
            errorMessage = errorMessage + rb.getString("errorCountry");
        }
        if (zip.length() == 0) {
            errorMessage = errorMessage + rb.getString("errorPostalCode");
        }
        if (phone.length() == 0) {
            errorMessage = errorMessage + rb.getString("errorPhone");
        }
        return errorMessage;
    }
}
