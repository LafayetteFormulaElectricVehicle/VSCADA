package cockpit.database;

public class SCADATimer implements Runnable{

    private int delay;
    private SCADASystem sys;
    private volatile boolean running = true;

    public SCADATimer(int delayTime, SCADASystem system){
        delay = delayTime;
        sys = system;
    }

    public void run(){
        try{
            while(running){
                Thread.sleep(delay);
                sys.writeToDB();
            }
        }
        catch(Exception e){}
    }

    public void shutdown(){
        running = false;
    }

}