/**
 * Created by 13wil on 3/2/2017.
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spark.QueryParamsMap;
import spark.Request;

import cockpit.database.*;

import static spark.Spark.*;

public class SparkServer implements Runnable{

    Thread t;
    Gson gson;
    DBHandler handler;

    private static org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SparkServer.class.getName());

    public SparkServer() {
        port(3000);
        t = new Thread(this, "SparkServer");
        gson = new GsonBuilder().setPrettyPrinting().create();
        handler = new DBHandler("SCADA.db","SQLSCHEME/");
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

        get("/dbquery", (req, res) -> {
            QueryParamsMap q = req.queryMap();
            String id = q.get("id").value() != null ? q.get("id").value() : null;
            String sys = q.get("sys").value() != null ? q.get("sys").value() : null;
            String startDate = q.get("startDate").value() != null ? q.get("startDate").value() : null;
            String endDate = q.get("endDate").value() != null ? q.get("endDate").value() : null;
            System.out.println(id);
            System.out.println(sys);
            System.out.println(startDate);
            System.out.println(endDate);
//            System.out.println(gson.toJson(handler.getInfo(id,sys,startDate,endDate)));
            return gson.toJson(handler.getInfo(id, sys, startDate, endDate));
        });

        get("/dbquery/recent", (req,res)-> {
            //select * from Data where timestamp = (select max(timestamp) from Data);
            return gson.toJson(handler.getRecent());
        });

        get("/api/name", (req, res) -> {
            System.out.println("IN NAME");
            System.out.println(req.queryMap().get("name").value());
//            printReq(req);
            return "in name";
        });

//        get("/data", (req, res) -> {
//            DataPacket packet = new DataPacket();
//            System.out.println(gson.toJson(packet));
//            res.type("text/json");
//            res.header("Access-Control-Allow-Origin","*");
//            return gson.toJson(packet);
//        });



        notFound((req,res) -> {
            System.out.println("IN NOT FOUND");
            //System.out.println(req.queryMap().get("name").value());
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
