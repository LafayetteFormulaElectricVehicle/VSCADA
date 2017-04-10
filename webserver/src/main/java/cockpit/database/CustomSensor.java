package cockpit.database;

/**
 * Created by CraigLombardo on 4/10/17.
 */

public class CustomSensor {

    private String tag;
    private int slope;
    private int offset;
    private String description;
    private String units;
    private String system;
    private Boolean store;
    private String value;

    public CustomSensor(String t, int sl, int o, String d, String u, String s, int st) {
        tag = t;
        slope = sl;
        offset = o;
        description = d;
        units = u;
        system = s;
        store = (st == 1);

        value = "NaN?";
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String s) {
        tag = s;
    }

    public int getSlope(){
        return slope;
    }

    public void setSlope(int s){
        slope = s;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int i) {
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