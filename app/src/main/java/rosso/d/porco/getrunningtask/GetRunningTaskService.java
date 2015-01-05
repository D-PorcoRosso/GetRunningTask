package rosso.d.porco.getrunningtask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class GetRunningTaskService extends Service {

    private GetRunningTaskManager mTaskManager;
    private Thread mThread;

    public static Intent getCallingIntent(Context context) {
        Intent callingIntent = new Intent(context, GetRunningTaskService.class);

        return callingIntent;
    }

    @Override
    public IBinder onBind(Intent argc){
        return null;
    }

    @Override
    public void onCreate(){
        mTaskManager = GetRunningTaskManager.getInstance(this);
        mTaskManager.triggerGetRunningTaskThread();
        mThread = new Thread(new post());
        mThread.start();
    }

    @Override
    public void onDestroy(){
    }

    private class post implements Runnable{
        @Override
        public void run() {
            int i = 0;
            while (true){
                try {
                    Thread.sleep(5000);
                    i++;
                } catch (InterruptedException interrupt) {
                    interrupt.printStackTrace();
                }
            }
        }
    };
}
