public class Can{
    static{
        System.loadLibrary("can");
    }

    private native void read();

    public static void main(String[] args) {
        Can can = new Can();
        can.read();
    }
}