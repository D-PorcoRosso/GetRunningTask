package rosso.d.porco.getrunningtask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MainActivity extends Activity {

    private CountDown mCountDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(GetRunningTaskService.getCallingIntent(this,100));
        mCountDown = new CountDown();
        IntentFilter filter = new IntentFilter();
        filter.addAction(GetRunningTaskService.COUNT_DOWN);
        registerReceiver(mCountDown,filter);
    }

    private class CountDown extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            int time = intent.getIntExtra(GetRunningTaskService.TIME_KEY,3);

            setCountDown(time);
        }
    }

    private void setCountDown(int time){
        TextView countTime = (TextView)findViewById(R.id.count_number);
        if( time == 0 )
            countTime.setText("you can leave app , getting info will do in background");
        else
            countTime.setText(Integer.toString(time));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if ( mCountDown != null ){
            unregisterReceiver(mCountDown);
        }
    }
}
