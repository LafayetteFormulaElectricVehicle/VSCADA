package cockpit.database;

/**
 * <h1>Sensor</h1>
 * This class serves as the data container for all sensor data.
 *
 * @author Craig Lombardo
 * @version 1.0
 * @since 2017-02-13
 */

public class Sensor {

    private int id;
    private String tag;
    private int address;
    private int offset;
    private int byteLength;
    private String description;
    private String units;
    private String system;
    private Boolean store;

    private Double correction;
    private String calibValue;

    /**
     * Creates a new Sensor
     *
     * @param i  The ID of the sensor
     * @param t  The tag of the sensor
     * @param a  The address of the sensor (the CAN address it's being sent over)
     * @param o  The byte offset from the address
     * @param b  The byte length (number of bytes) of the sensor data
     * @param d  The description of the sensor
     * @param s  The system of the sensor
     * @param u  The units of the sensor
     * @param st 1 for storage into the DB 0 for not
     * @param c  The correction factor after the CAN read (decimals can't be sent over CAN so we must shift the value)
     */
    public Sensor(int i, String t, int a, int o, int b, String d, String s, String u, int st, Double c) {
        id = i;

        tag = t;
        address = a;
        offset = o;
        byteLength = b;
        description = d;
        system = s;
        units = u;
        store = (st == 1);

        correction = c;

        calibValue = "NaN?";
    }

    /**
     * Gets the calibrated value of the Sensor
     *
     * @return The calibrated value
     */
    public String getCalibValue() {
        return calibValue;
    }

    /**
     * Sets the calibrated value of the Sensor
     *
     * @param v The  to set for the Sensor
     */
    public void setCalibValue(String v) {
        calibValue = String.format("%.2f", (Double.parseDouble(v) * correction));
    }

    /**
     * Returns the ID of the sensor
     *
     * @return The ID
     */
    public int getID() {
        return id;
    }

    /**
     * Sets the ID of the sensor
     *
     * @param i the ID to set
     */
    public void setID(int i) {
        id = i;
        String buff = "" + Integer.toHexString(i);
    }

    /**
     * Gets the Sensor Tag
     *
     * @return The sensor Tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * Sets the sensor tag
     *
     * @param s the tag of the sensor
     */
    public void setTag(String s) {
        tag = s;
    }

    /**
     * Gets the address of the sensor
     *
     * @return the address of the sensor
     */
    public int getAddress() {
        return address;
    }

    /**
     * Sets the address of the sensor
     *
     * @param i the address to set
     */
    public void setAddress(int i) {
        address = i;
    }

    /**
     * Gets the offset of the sensor
     *
     * @return the value
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Sets the offset of the sensor
     *
     * @param i the value to set
     */
    public void setOffset(int i) {
        offset = i;
    }

    /**
     * Gets the byte length of the sensor
     *
     * @return the value
     */
    public int getByteLength() {
        return byteLength;
    }

    /**
     * Sets the byte length of the sensor
     *
     * @param i the value to set
     */
    public void setByteLength(int i) {
        byteLength = i;
    }

    /**
     * Gets the description of the sensor
     *
     * @return the value
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the sensor
     *
     * @param s the value to set
     */
    public void setDescription(String s) {
        description = s;
    }

    /**
     * Gets the system of the sensor
     *
     * @return the value
     */
    public String getSystem() {
        return system;
    }

    /**
     * Sets the system of the sensor
     *
     * @param s the value to set
     */
    public void setSystem(String s) {
        system = s;
    }

    /**
     * Gets the units of the sensor
     *
     * @return the value
     */
    public String getUnits() {
        return units;
    }

    /**
     * Sets the units of the sensor
     *
     * @param s the value to set
     */
    public void setUnits(String s) {
        units = s;
    }

    /**
     * Gets the storage state of the sensor
     *
     * @return the value
     */
    public Boolean getStore() {
        return store;
    }

    /**
     * Sets whether or not to store the sensor data
     *
     * @param b true for store, false for not
     */
    public void setStore(Boolean b) {
        store = b;
    }

    /**
     * Gets the correction factor of the sensor
     *
     * @return the value
     */
    public Double getCorrection() {
        return correction;
    }

    /**
     * Sets the correction factor of the sensor
     *
     * @param c the value to set
     */
    public void setCorrection(Double c) {
        correction = c;
    }
}