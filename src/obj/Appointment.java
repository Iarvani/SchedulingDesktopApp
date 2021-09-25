package obj;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
/** This class is used to create an appointment object and get the values associated with an appointment. */
public class Appointment {

    private final IntegerProperty appointmentId;
    private final IntegerProperty customerId;
    private final StringProperty title;
    private final StringProperty description;
    private final StringProperty location;
    private final StringProperty contact;
    private final StringProperty user;
    private final StringProperty type;
    private Timestamp startTimestamp;
    private Timestamp endTimestamp;
    private Date startDate;
    private Date endDate;
    private final StringProperty dateString;
    private final StringProperty startString;
    private final StringProperty endString;
    private final StringProperty createdBy;
    /**
     * Default constructor for the appointment class
     * Sets the values needed to create the appointment object used to build the observable list
     * @param appointmentId Appointment ID number.
     * @param contact Contact ID number.
     * @param createdBy created by name.
     * @param customerId Customer ID number.
     * @param description Description of appointment.
     * @param endDate Ending date of appointment.
     * @param endTimestamp Ending time of appointment.
     * @param location Location of appointment.
     * @param startDate Start date of appointment.
     * @param startTimestamp Start time of appointment.
     * @param title Title of appointment.
     * @param type Type of appointment.
     * @param user User name.
     */
    // Constructor
    public Appointment(int appointmentId, int customerId, String title, String type, String description, String location, String contact,
                       String user, Timestamp startTimestamp, Timestamp endTimestamp, Date startDate, Date endDate, String createdBy) {
        this.appointmentId = new SimpleIntegerProperty(appointmentId);
        this.customerId = new SimpleIntegerProperty(customerId);
        this.title = new SimpleStringProperty(title);
        this.type = new SimpleStringProperty(type);
        this.description = new SimpleStringProperty(description);
        this.location = new SimpleStringProperty(location);
        this.contact = new SimpleStringProperty(contact);
        this.user = new SimpleStringProperty(user);
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.startDate = startDate;
        this.endDate = endDate;
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
        this.dateString = new SimpleStringProperty(format.format(startDate));
        SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm z");
        this.startString = new SimpleStringProperty(formatTime.format(startDate));
        this.endString = new SimpleStringProperty(formatTime.format(endDate));
        this.createdBy = new SimpleStringProperty(createdBy);
    }
    /** AppointmentID setter.
     * @param appointmentId Appointment ID number.
     */
    // Setters
    public void setAppointmentId(int appointmentId) {
        this.appointmentId.set(appointmentId);
    }
    /** Customer ID setter.
     * @param customerId Customer ID number.
     */
    public void setCustomerId(int customerId) {
        this.customerId.set(customerId);
    }
    /** Title setter.
     * @param title Title of appointment.
     */
    public void setTitle(String title) {
        this.title.set(title);
    }
    /** Type setter.
     * @param type Type of appointment.
     */
    public void setType(String type) {
        this.type.set(type);
    }
    /** Description setter.
     * @param description Description of appointment.
     */
    public void setDescription(String description) {
        this.description.set(description);
    }
    /** Location setter.
     * @param location Location of appointment.
     */
    public void setLocation(String location) {
        this.location.set(location);
    }
    /** Contact setter.
     * @param contact Contact number.
     */
    public void setContact(String contact) {
        this.contact.set(contact);
    }
    /** User setter.
     * @param user User name.
     */
    public void setUser(String user) {
        this.user.set(user);
    }
    /** Start timestamp setter.
     * @param startTimestamp Appointment start timestamp.
     */
    public void setStartTimestamp(Timestamp startTimestamp) {
        this.startTimestamp = startTimestamp;
    }
    /** End timestamp setter.
     * @param endTimestamp Appointment end timestamp.
     */
    public void setEndTimestamp(Timestamp endTimestamp) {
        this.endTimestamp = endTimestamp;
    }
    /** Start date setter.
     * @param startDate Appointment start date..
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    /** End date setter.
     * @param endDate Appointment end date.
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    /** Created by setter.
     * @param createdBy User it was created by.
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy.set(createdBy);
    }
    /** Appointment ID getter.
     * @return Appointment ID number.
     */
    // Getters
    public int getAppointmentId() {
        return this.appointmentId.get();
    }
    /** Appointment ID property getter.
     * @return Appointment ID property.
     */
    public IntegerProperty appointmentIdProperty() {
        return this.appointmentId;
    }
    /** Customer ID getter.
     * @return Customer ID number.
     */
    public int getCustomerId() {
        return this.customerId.get();
    }
    /** Customer ID Property getter.
     * @return Customer ID Property.
     */
    public IntegerProperty customerIdProperty() {
        return this.customerId;
    }
    /** Title getter.
     * @return Appointment title.
     */
    public String getTitle() {
        return this.title.get();
    }
    /** Title Property getter.
     * @return Appointment title property.
     */
    public StringProperty titleProperty() {
        return this.title;
    }
    /** Type getter.
     * @return Appointment type.
     */
    public String getType() {
        return this.type.get();
    }
    /** Type property getter.
     * @return Appointment type property.
     */
    public StringProperty typeProperty() {
        return this.type;
    }
    /** Description getter.
     * @return Appointment Description.
     */
    public String getDescription() {
        return this.description.get();
    }
    /** Description property getter.
     * @return Appointment description property.
     */
    public StringProperty descriptionProperty() {
        return this.description;
    }
    /** Location getter.
     * @return Appointment Location.
     */
    public String getLocation() {
        return this.location.get();
    }
    /** Location property getter.
     * @return Appointment location property.
     */
    public StringProperty locationProperty() {
        return this.location;
    }
    /** Contact getter.
     * @return Contact ID number.
     */
    public String getContact() {
        return this.contact.get();
    }
    /** Contact getter.
     * @return  Contact property.
     */
    public StringProperty contactProperty() {
        return this.contact;
    }
    /** User getter.
     * @return User number.
     */
    public String getUser() {
        return this.user.get();
    }
    /** User property getter.
     * @return User property.
     */
    public StringProperty userProperty() {
        return this.user;
    }
    /** Start timestamp getter.
     * @return Appointment starting timestamp.
     */
    public Timestamp getStartTimestamp() {
        return this.startTimestamp;
    }
    /** end timestamp getter.
     * @return Appointment ending timestamp.
     */
    public Timestamp getEndTimestamp() {
        return this.endTimestamp;
    }
    /** Start date getter.
     * @return Appointment starting date.
     */
    public Date getStartDate() {
        return this.startDate;
    }
    /** End date getter.
     * @return Appointment Ending date.
     */
    public Date getEndDate() {
        return this.endDate;
    }
    /** Date String getter.
     * @return Appointment date in a string.
     */
    public String getDateString() {
        return this.dateString.get();
    }
    /** Date string property getter.
     * @return Appointment date in a property.
     */
    public StringProperty dateStringProperty() {
        return this.dateString;
    }
    /** Start string getter.
     * @return Appointment starting string.
     */
    public String getStartString() {
        return this.startString.get();
    }
    /** Start string getter.
     * @return Appointment starting string.
     */
    public StringProperty startStringProperty() {
        return this.startString;
    }
    /** End string getter.
     * @return Appointment end string.
     */
    public String getEndString() {
        return this.endString.get();
    }
    /** End string property getter.
     * @return Appointment ending property.
     */
    public StringProperty endStringProperty() {
        return this.endString;
    }
    /** Created by getter.
     * @return User appointment created by.
     */
    public String getCreatedBy() {
        return this.createdBy.get();
    }
    /** Created by property getter.
     * @return USer appointment was created by property.
     */
    public StringProperty createdByProperty() {
        return this.createdBy;
    }

}
