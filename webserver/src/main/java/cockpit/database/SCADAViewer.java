package cockpit.database;

        import javafx.application.Application;
        import javafx.stage.Stage;
        import javafx.scene.layout.GridPane;
        import javafx.geometry.Rectangle2D;
        import javafx.stage.Screen;
        import javafx.scene.Scene;
        import javafx.scene.control.ScrollPane;
        import javafx.scene.control.Label;
        import javafx.scene.control.Button;
        import javafx.event.ActionEvent;
        import javafx.event.EventHandler;
        import javafx.geometry.Insets;
        import javafx.scene.Node;
        import java.util.ArrayList;
        import java.util.Map;
        import java.util.HashMap;

        import java.util.Timer;
        import java.util.TimerTask;

        import javafx.concurrent.Task;
        import javafx.concurrent.WorkerStateEvent;

        import javafx.application.Platform;

        import cockpit.database.DBHandler;

public class SCADAViewer extends Application {
//hi
    double hmax;
    double vmax;
    GridPane grid;
    int row = 4;
    Boolean updateMe = false;
    private DBHandler handler;

    String file = "/home/pi/Desktop/output.txt";
    Boolean rel = true;

    HashMap<String, Label> sensors;

    SCADASystem sys;

    @Override
    public void start(Stage primaryStage) throws Exception{

        sensors = new HashMap<String, Label>();

        primaryStage.setTitle("SCADA Viewer");

        ScrollPane root = new ScrollPane();
        grid = new GridPane();
        grid.setHgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.setGridLinesVisible(true);
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

        hmax = bounds.getWidth();
        vmax = bounds.getHeight();
        Scene scene = new Scene(root, hmax/2, vmax/2);

        Button quit = new Button("Quit");

        quit.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                System.exit(0);
            }
        });

        primaryStage.setScene(scene);

        grid.add(new Label("Sensor ID:"), 0, 0);
        grid.add(new Label("Sensor Name:"), 1, 0);
        grid.add(new Label("Sensor Value:"), 2, 0);
        grid.add(quit, 3, 0);

        handler = new DBHandler((rel ? "" : "../") + "SCADA.db",(rel ? "" : "../") + "SQLSchema/");
//
//    sys = new SCADASystem(handler, null, file);
//    createMapping(sys.getMap());

        root.setContent(grid);
        primaryStage.show();

        sys = new SCADASystem(handler, file);
        createMapping(handler.getIDNames());
        Thread thr = new Thread(sys);
        thr.start();

        //http://stackoverflow.com/questions/16764549/timers-and-javafx
        Timer timer = new java.util.Timer();

        timer.schedule(new TimerTask() {
            public void run() {
                Platform.runLater(new Runnable() {
                    public void run() {
                        updateNow();
                    }
                });
            }
        }, 1000, 1000);

    }

    public void createMapping(ArrayList<ArrayList<String>> info){
        String id;
        String name;
        for (ArrayList<String> r : info){
            id = r.get(0);
            name = r.get(1);
            Label tmp = new Label("");
            sensors.put(id, tmp);
            grid.add(new Label("0x" + Integer.toHexString(Integer.parseInt(id))), 0, row);
            grid.add(new Label(name), 1, row);
            grid.add(tmp, 2, row++);
        }
    }

    public void update(){
        updateMe = true;
    }

    private void updateNow(){
        updateMe = false;
        for (Map.Entry<String, String> entry : sys.getMap().entrySet()){
            sensors.get(entry.getKey()).setText(entry.getValue());
        }
    }

    public static void main(String[] args){
        launch(args);
    }



}