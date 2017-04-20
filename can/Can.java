public class Can{
    static{
        System.loadLibrary("can");
    }

    private native void read();
    private native int open_port(String port);
    private native void send_port();
    private native void read_port();
    private native int close_port();
    private native int init();

    public static void main(String[] args) {
        Can can = new Can();
        can.open_port("can0");
        can.read_port();
        can.close_port();
    }
}