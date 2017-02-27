/**
 * Created by Austin on 2/7/2017.
 */

//Spark documentation: http://sparkjava.com/documentation.html

import static spark.Spark.*;
import com.google.gson.*;
import spark.Request;

import javax.servlet.MultipartConfigElement;
import java.io.InputStream;

public class main {

    public static void main(String[] args) {
        port(3000);

        staticFiles.location("/");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        DataPacket packet = new DataPacket();
        System.out.println(gson.toJson(packet));

        get("/",(req,res) -> {
            res.redirect("/app/home.html");
            res.header("Access-Control-Allow-Origin","*");
            System.out.println(req.raw());
            return null;
        });

        get("/data", (req, res) -> {
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
//        System.out.println();
//        System.out.println();
//        System.out.println();
//        System.out.println();
//        System.out.println();
//        System.out.println();
//        System.out.println();
//        System.out.println();
//        System.out.println();
    }

}
