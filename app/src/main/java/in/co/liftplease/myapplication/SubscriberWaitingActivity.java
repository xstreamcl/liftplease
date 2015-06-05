package in.co.liftplease.myapplication;

import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class SubscriberWaitingActivity extends ActionBarActivity {

    private Handler mHandler;
    private TextView timerBox;

    public SubscriberWaitingActivity() {
        mHandler = new Handler();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriber_waiting);
        mHandler.post(mRunnable);
        timerBox = (TextView)findViewById(R.id.timer);
    }


    private final Runnable mRunnable = new Runnable() {
        public void run() {
            Integer timer = Integer.valueOf(timerBox.getText().toString());
            if(timer >= 0){
                timer--;
            }



            // update every second
            mHandler.postDelayed(this, 1000);
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_subscriber_waiting, menu);
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
}
