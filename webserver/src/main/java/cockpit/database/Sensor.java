package cockpit.database;

public class Sensor {

    public int id;
    private String hexID;
    private String tag;
    private int address;
    private int offset;
    private int byteLength;
    private String description;
    private String units;
    private String system;
    private Boolean store;

    private Double correction;
    private String rawValue;
    private String calibValue;

    public Sensor(int i, String t, int a, int o, int b, String d, String s, String u, int st, Double c) {
        id = i;

        String buff = "" + Integer.toHexString(i);

        hexID = "0x000".substring(0, 5 - buff.length()) + buff;

        tag = t;
        address = a;
        offset = o;
        byteLength = b;
        description = d;
        system = s;
        units = u;
        store = (st == 1);

        correction = c;

        rawValue = "NaN?";
        calibValue = "NaN?";
    }

//    public String getRawValue() {
//        return rawValue;
//    }

    public void setRawValue(String v) {
        rawValue = v;
//        System.out.println((Integer.parseInt(v) * valSlope));
//        System.out.println(String.format("%.2f", (Integer.parseInt(v) * valSlope + valOffset)));
        calibValue = String.format("%.2f", (Double.parseDouble(v) * correction));
    }

    public String getCalibValue() {
        return calibValue;
    }

    public void setCalibValue(String v) {
        calibValue = v;
    }

    public int getID() {
        return id;
    }

    public void setID(int i) {
        id = i;
        String buff = "" + Integer.toHexString(i);

        hexID = "0x000".substring(0, 5 - buff.length()) + buff;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String s) {
        tag = s;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int i) {
        address = i;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int i) {
        offset = i;
    }

    public int getByteLength() {
        return byteLength;
    }

    public void setByteLength(int i) {
        byteLength = i;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String s) {
        description = s;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String s) {
        system = s;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String s) {
        units = s;
    }

    public Boolean getStore() {
        return store;
    }

    public void setStore(Boolean b) {
        store = b;
    }

    public void setCorrection(Double c){
        correction = c;
    }

    public Double getCorrection(){
        return correction;
    }
}