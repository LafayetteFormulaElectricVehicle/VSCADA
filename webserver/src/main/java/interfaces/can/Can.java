package interfaces.can;


public class Can{
    static{
        System.loadLibrary("can");
    }

    // public Can(){

    // }

    public static native String read();
    public static native int open_port(String port);
    public static native void send_port();
    public static native String read_port();
    public static native int close_port();
    public static native int init();

    public static void main(String[] args) {
        Can can = new Can();
        String s = can.read();
        System.out.println(s);
        // can.open_port("can0");
        // while(true){
        //     System.out.println(can.read_port());
        // }
        //can.close_port();
    }
}