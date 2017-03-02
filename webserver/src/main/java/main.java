import java.io.IOException;

/**
 * Created by Austin on 2/7/2017.
 */

//Spark documentation: http://sparkjava.com/documentation.html



public class main {

    public static void main(String[] args) {
        //SparkServer sparkServer = new SparkServer();
        Server s = new Server();
        Client c = new Client();
        c.connect("127.0.0.1", 3002);
        c.send("bob");
    }

}
