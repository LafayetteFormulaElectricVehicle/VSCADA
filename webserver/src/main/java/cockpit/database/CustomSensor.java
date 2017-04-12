package cockpit.database;

/**
 * Created by CraigLombardo on 4/10/17.
 */

public class CustomSensor {

    private String tag;
    private Double slope;
    private Double offset;
    private String description;
    private String units;
    private String system;
    private Boolean store;
    private String value;

    private String tagHigh;
    private String tagLow;

    public CustomSensor(String t, String tagH, String tagL, Double sl, Double o, String d, String u, String s, int st) {
        tag = t;
        slope = sl;
        offset = o;
        description = d;
        units = u;
        system = s;

        tagHigh = tagH;
        tagLow = tagL;

        store = (st == 1);

        value = "NaN?";
    }

    public void calculate(int high, int low) {
        String hexVal = Integer.toHexString(high) + Integer.toHexString(low);
        int intVal = Integer.parseInt(hexVal, 16);
        value = String.format("%.2f", (intVal * slope + offset));
    }

    private String getTagHigh() {
        return tagHigh;
    }

    private String getTagLow() {
        return tagLow;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String s) {
        tag = s;
    }

    public Double getSlope() {
        return slope;
    }

    public void setSlope(Double s) {
        slope = s;
    }

    public Double getOffset() {
        return offset;
    }

    public void setOffset(Double i) {
        offset = i;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String s) {
        description = s;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String v) {
        value = v;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String s) {
        units = s;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String s) {
        system = s;
    }

    public Boolean getStore() {
        return store;
    }

    public void setStore(Boolean b) {
        store = b;
    }
}