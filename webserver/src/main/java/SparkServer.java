/**
 * Created by 13wil on 3/2/2017.
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spark.Request;

import static spark.Spark.*;

public class SparkServer implements Runnable{

    Thread t;
    Gson gson;

    private static org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SparkServer.class.getName());

    public SparkServer() {
        port(3000);
        t = new Thread(this, "SparkServer");
        gson = new GsonBuilder().setPrettyPrinting().create();
        t.start();
    }

    public void run() {
        staticFiles.location("/");
        LOG.info("Server started on port 3000");

        get("/",(req,res) -> {
            res.redirect("/app/home.html");
            res.header("Access-Control-Allow-Origin","*");
            return null;
        });

        get("/data", (req, res) -> {
            DataPacket packet = new DataPacket();
            System.out.println(gson.toJson(packet));
            res.type("text/json");
            res.header("Access-Control-Allow-Origin","*");
            return gson.toJson(packet);
        });

        get("/api/name", (req, res) -> {
            System.out.println("IN NAME");
            System.out.println(req.queryMap().get("name").value());
//            printReq(req);
            return "in name";
        });

        notFound((req,res) -> {
            System.out.println("NOT FOUND");
            System.out.println(req.queryMap().get("name").value());
//            printReq(req);
            return "Not found";
        });
    }

    private static void printReq(Request req) {
        System.out.println(req.url());
        System.out.println(req.pathInfo());
        System.out.println(req.attributes());
        System.out.println(req.params());
        System.out.println(req.headers());
        System.out.println(req.requestMethod());
        System.out.println(req.scheme());
        System.out.println(req.body());
    }

}
