public class Can{
    static{
        System.loadLibrary("can");
    }

    //public native String read();
    public native int open_port(String port);
    public native void send_port();
    public native String read_port();
    public native int close_port();
    public native int init();

    public static void main(String[] args) {
        Can can = new Can();
        //String s = can.read();
        //System.out.println(s);
        can.open_port("can0");
        while(true){
            System.out.println(can.read_port());
        }
        //can.close_port();
    }
}