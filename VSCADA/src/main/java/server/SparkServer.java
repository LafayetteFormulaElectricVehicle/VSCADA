package server; /**
 * Created by 13wil on 3/2/2017.
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spark.QueryParamsMap;
import spark.Request;

import cockpit.database.*;
import spark.Service;

import java.net.InetAddress;

import static spark.Spark.*;

public class SparkServer implements Runnable{

    Thread t;
    Gson gson;
    DBHandler handler;
    SCADASystem sys;

    public String ip;

    private static org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SparkServer.class.getName());

    /**
     * Initializes the Java Spark webserver on port 3000
     * @param DBH DBHandler for SCADA.db
     * @param system Data container for car's generic subsystems
     */
    public SparkServer(DBHandler DBH, SCADASystem system) {
        port(3000);
        t = new Thread(this, "server.SparkServer");
        gson = new GsonBuilder().setPrettyPrinting().create();
//        handler = new DBHandler();
        handler = DBH;
        sys = system;
        t.start();

        try {
            InetAddress IP;
            IP = InetAddress.getLocalHost();
//            System.out.println("Ip: " + IP.getAddress());
            IP = InetAddress.getLocalHost();
//            System.out.println("Ip: " + IP.getHostAddress());
            ip = IP.getHostAddress().toString();
        }
        catch(Exception e){
            ip = "";
        }

    }

    /**
     * Starts the webserver and sets endpoints.
     * Endpoints:
     * /dbquery             Can query based on id, system (sys), startDate or endDate
     * /dbquery/recent      Returns last 5 seconds of data from all subsystems
     * /map                 Returns a map of the SCADASystem
     * /cmap                Returns a Custom Map of the SCADASystem
     */
    public void run() {
        staticFiles.location("/");
        LOG.info("server.Server started on port 3000");


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
            return gson.toJson(handler.getLatestData(5));
        });


        get("/map", (req, res) -> {
            return gson.toJson(sys.getMap());
        });

        get("/cmap", (req, res) -> {
            return gson.toJson(sys.getCustomMapping());
        });


        notFound((req,res) -> {
            System.out.println("IN NOT FOUND");
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
