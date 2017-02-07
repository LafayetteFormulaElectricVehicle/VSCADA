/**
 * Created by Austin on 2/7/2017.
 */

import com.google.gson.JsonObject;

public class DataPacket {

    TSI tsi;
    TSV tsv;
    GLV glv;
    Dyno dyno;
    Physics physics;
    Cooling cooling;
    Dashboard dashboard;

    public DataPacket() {
        tsi = new TSI();
        tsv = new TSV();
        glv = new GLV();
        dyno = new Dyno();
        physics = new Physics();
        cooling = new Cooling();
        dashboard = new Dashboard();
    }
}
