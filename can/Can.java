public class Can{
    static{
        System.loadLibrary("can");
    }

    private native String read();
    private native int open_port(String port);
    private native void send_port();
    private native String read_port();
    private native int close_port();
    private native int init();

    public static void main(String[] args) {
        Can can = new Can();
        String s = can.read();
        System.out.println(s);
        can.open_port("can0");
        while(true){
            System.out.println(can.read_port());
        }
        //can.close_port();
    }
}