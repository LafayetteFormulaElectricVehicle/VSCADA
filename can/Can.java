public class Can{
    static{
        System.loadLibrary("can");
    }

    private native void read();

    private native void open_port();
    private native void send_port();
    private native void read_port();
    private native int close_port();
    private native int init();

    public static void main(String[] args) {
        Can can = new Can();
        can.read();
        can.open_port();
        can.send_port();
        can.read_port();
        can.close_port();
        can.init();
    }
}