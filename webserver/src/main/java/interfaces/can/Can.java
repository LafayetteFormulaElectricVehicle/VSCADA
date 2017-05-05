package interfaces.can;

/**
 * <h1>CAN Class</h1>
 * This class provides an interface from C's SocketCan to Java
 * Requires libcan.so file to be in root of project directory
 * @author Austin Wiles
 * @version 1.0
 * @since 2017-03-01
 */
public class Can{
    static{
        System.loadLibrary("can");
    }

    // public Can(){

    // }

    /**
     * Calls a native C function through JNI to attempt to open a CAN port
     * @param port "can0" or "vcan0" for testing
     * @return 0 if successfully opened port, else -1
     */
    public static native int open_port(String port);

    /**
     * Calls a native C function through JNI to attempt to read data from a CAN port
     * @return a String containing the last line of CAN data received on the port
     */
    public static native String read_port();

    /**
     * Calls a native C function through JNI to close the CAN port
     * @return 0 if success, else -1
     */
    public static native int close_port();


    private static native String read();

//    public static void main(String[] args) {
//        Can can = new Can();
//        String s = can.read();
//        System.out.println(s);
//        // can.open_port("can0");
//        // while(true){
//        //     System.out.println(can.read_port());
//        // }
//        //can.close_port();
//    }
}