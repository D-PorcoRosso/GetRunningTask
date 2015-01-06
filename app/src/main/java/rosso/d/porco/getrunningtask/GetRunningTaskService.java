package rosso.d.porco.getrunningtask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.IBinder;
import android.util.Log;

public class GetRunningTaskService extends Service {

    private GetRunningTaskManager mTaskManager;
    private Thread mThread;
    private NotificationManager nm;
    private static int BOUND = 100;

    public static Intent getCallingIntent(Context context,int time) {
        Intent callingIntent = new Intent(context, GetRunningTaskService.class);
        BOUND = time;
        return callingIntent;
    }

    public static String COUNT_DOWN = "porco.d.rosso.countdown";
    public static String TIME_KEY = "count_key";

    @Override
    public IBinder onBind(Intent argc){
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mTaskManager = GetRunningTaskManager.getInstance(this);
        mTaskManager.triggerGetRunningTaskThread();
        mThread = new Thread(new post());
        mThread.start();
    }

    @Override
    public void onDestroy(){
        nm.cancel(5566);
        mTaskManager.stopThread();
        stopSelf();
        super.onDestroy();
    }

    private class post implements Runnable{
        @Override
        public void run() {
            int i = 0;
            while ( i < BOUND ){
                try {
                    postCurrentTaskNotification();
                    Thread.sleep(3000);
                    i++;
                } catch (InterruptedException interrupt) {
                    interrupt.printStackTrace();
                }
            }
            onDestroy();
        }
    };

    private void postCurrentTaskNotification(){

        Notification.Builder builder = new Notification.Builder(this);
        String currentTask = mTaskManager.getRunningTask();
        Bitmap icon = BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher);
        Notification post = builder
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(icon)
                .setContentTitle(getResources().getString(R.string.current_task))
                .setContentText(currentTask)
                .build();
        nm.notify(5566,post);
    }
}
