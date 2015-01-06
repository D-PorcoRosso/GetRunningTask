package rosso.d.porco.getrunningtask;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class GetRunningTaskManager {

    private final int mCurrentVersion = Build.VERSION.SDK_INT;
    private final int mCompatVersion = Build.VERSION_CODES.LOLLIPOP;

    private static GetRunningTaskManager sGetRunningTaskManager;
    private ActivityManager mActivityMgr = null;
    private Context mContext;
    private boolean mIsRunning = true;
    private Thread mThread;
    private Queue<String> mRunningAppList = null;
    private List<String> mSystemAppList = null;
    private final int HISTORY_SIZE = 5;

    public static GetRunningTaskManager getInstance( Context context ){
        if ( sGetRunningTaskManager == null ) {
            sGetRunningTaskManager = new GetRunningTaskManager( context );
        }
        return sGetRunningTaskManager;
    }

    private GetRunningTaskManager(Context context) {
        mContext = context;
        if ( isNewAPI() ) {
            mRunningAppList = new LinkedList();
        }
        mActivityMgr = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
    }

    public String getRunningTask(){
        if ( isNewAPI() ) {
            return getCurrentTaskPkgName();
        } else {
            List<ActivityManager.RunningTaskInfo> runningTask = mActivityMgr.getRunningTasks(1);
            ComponentName compName = runningTask.get(0).topActivity;
            String pkgName = compName.getPackageName();
            return pkgName;
        }
    }

    public synchronized void triggerGetRunningTaskThread(){
        if ( isNewAPI() ) {
            if ( mRunningAppList != null )
                mRunningAppList.clear();
            mThread = new Thread(new GetRunningTask());
            mIsRunning = true;
            mThread.start();
        }
    }

    public synchronized void stopThread(){
        if ( isNewAPI() ) {
            if ( mThread != null ) {
                mThread.interrupt();
            }
        }
    }

    private String getCurrentTaskPkgName(){

        String currentPkgName = "";
        ArrayList<Integer> countList = new ArrayList<Integer>();
        ArrayList<String> list = new ArrayList<String>();

        for ( String pkgName : mRunningAppList ) {

            if ( list.contains( pkgName ) ) {
                int index = list.indexOf( pkgName );
                int count = countList.get( index );
                count++;
                countList.set( index , count );
            } else {
                list.add(pkgName);
                countList.add(1);
            }
        }

        int maxIndex = 0;
        int max = 0;

        for ( int i = 0 ; i < countList.size() ; i++ ) {

            if( max < countList.get(i) ){
                max = countList.get(i);
                maxIndex = i;
            }
        }
        if (list.size() > 0)
            currentPkgName = list.get(maxIndex);

        return currentPkgName;
    }

    private class GetRunningTask implements Runnable {

        @Override
        public void run() {
            while ( mIsRunning ) {
                try {
                    getSystemApp();
                    Thread.sleep(500);
                    setRunningAppHistory();
                } catch ( InterruptedException interrupt ) {
                    mIsRunning = false;
                } catch ( Exception e ) {
                    mIsRunning = false;
                }
            }
        }
    }

    private void setRunningAppHistory() {

        if ( mRunningAppList != null ){
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = mActivityMgr.getRunningAppProcesses();

            boolean isExistForeground = false;

            for ( ActivityManager.RunningAppProcessInfo processInfo : runningAppProcesses ){
                if ( processInfo.lru == 0 &&
                        processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                    boolean isSystemApp = false;

                    for ( String systemProcessPkgName : mSystemAppList ) {
                        if ( processInfo.processName.contains(systemProcessPkgName) ){
                            isSystemApp = true;
                            break;
                        }
                    }
                    if ( !isSystemApp && !mContext.getPackageName().contains(processInfo.processName)  ) {
                        isExistForeground = true;
                        pushIntoHistory(processInfo.processName);
                    }
                }
            }
            if(!isExistForeground){
                pushIntoHistory("");
            }
        }

    }

    private void pushIntoHistory(String pkgName){
        if ( mRunningAppList.size() >= HISTORY_SIZE )
            mRunningAppList.poll();
        mRunningAppList.offer(pkgName);
    }

    private void getSystemApp(){

        if ( mSystemAppList == null )
            mSystemAppList = new ArrayList<String>();

        if ( !mSystemAppList.isEmpty() )
            return;

        PackageManager pm = mContext.getPackageManager();
        List<PackageInfo> listAppcations = pm
                .getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);

        if (listAppcations != null && listAppcations.size() > 0) {
            for (PackageInfo pkgInfo : listAppcations) {

                ApplicationInfo appInfo = pkgInfo.applicationInfo;

                if ( Toolkit.isSystemApp(appInfo) ) {
                    mSystemAppList.add(appInfo.processName);
                }
            }

        }
    }

    public boolean isNewAPI(){
        if ( mCurrentVersion >= mCompatVersion )
            return true;
        else
            return false;
    }

}
