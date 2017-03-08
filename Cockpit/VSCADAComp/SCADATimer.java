package VSCADAComp;
public class SCADATimer implements Runnable{
  
  private int delay;
  private SCADASystem sys;
  private SCADAViewer view;
  private volatile boolean running = true;
  
  public SCADATimer(int delayTime, SCADASystem system, SCADAViewer viewer){
    delay = delayTime;
    sys = system;
    view = viewer;
  }
  
  public void run(){
    try{
      while(running){
        Thread.sleep(delay);
        if(view != null) view.update();
        sys.writeToDB();
      }
    }
    catch(Exception e){}
  }
  
  public void shutdown(){
    running = false;
  }
  
}