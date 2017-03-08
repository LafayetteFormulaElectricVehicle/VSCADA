package VSCADAComp;

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

import DBHandler.DBHandler;

public class SCADAViewer extends Application {
  
  double hmax;
  double vmax;
  GridPane grid;
  int row = 4;
  Boolean updateMe = false;
  DBHandler handler;
  
  HashMap<String, Label> sensors;
  
  SCADASystem sys;
  
  @Override
  public void start(Stage primaryStage) {
    
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
    
    Label userName = new Label("Sensor ID:");
    grid.add(new Label("Sensor ID:"), 0, 0);
    grid.add(new Label("Sensor Value:"), 1, 0);
    grid.add(quit, 2, 0);
    
    handler = new DBHandler("SCADA.db","SQLSchema/");
    
    sys = new SCADASystem(handler, null, "/Users/CraigLombardo/Desktop/output.txt");
    createMapping(sys.getMap());
    
    root.setContent(grid);
    primaryStage.show();
    
    handler = new DBHandler("SCADA.db","SQLSchema/");
    
    sys = new SCADASystem(handler, this, "/Users/CraigLombardo/Desktop/output.txt");
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
  
  public void createMapping(HashMap<String, String> map){
    String key;
    String val;
    for (Map.Entry<String, String> entry : map.entrySet()){
      key = entry.getKey();
      val = entry.getValue();
      Label tmp = new Label(val);
      sensors.put(key, tmp);
      grid.add(new Label(key), 0, row);
      grid.add(tmp, 1, row++);
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