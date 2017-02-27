/**
 * Created by Austin on 2/7/2017.
 */

//Spark documentation: http://sparkjava.com/documentation.html

import static spark.Spark.*;
import com.google.gson.*;

public class main {

    public static void main(String[] args) {
        port(3000);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        DataPacket packet = new DataPacket();
        System.out.println(gson.toJson(packet));

        get("/",(req,res) -> {
            res.redirect("/data");
            return null;
        });

        get("/data", (req, res) -> {
            res.type("text/json");
            res.header("Access-Control-Allow-Origin","*");
            return gson.toJson(packet);
        });

    }

}
