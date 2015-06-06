package in.co.liftplease.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


public class SubscriberWaitingActivity extends ActionBarActivity {

    private Handler mHandler;
    private TextView timerBox;
    private String providerId;
    private String phone;
    private LinearLayout success_container;
    private LinearLayout waiting_container;
    SessionManager session;
    HashMap<String, String> user;

    public SubscriberWaitingActivity() {
        mHandler = new Handler();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriber_waiting);
        mHandler.post(mRunnable);
        timerBox = (TextView)findViewById(R.id.timer);
        success_container = (LinearLayout)findViewById(R.id.success_container);
        waiting_container = (LinearLayout)findViewById(R.id.waiting_container);
        Intent intent = getIntent();
        providerId = intent.getStringExtra("providerId");
        phone = intent.getStringExtra("phone");
        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();
    }

    private String getRefreshUrl(){
        String session_id = "key="+user.get(SessionManager.KEY_SESSION);
        String parameters = session_id+"&provider="+providerId;
        String url = "http://whenisdryday.in:5000/subscriber/request/status"+"?"+parameters;
        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            String urltoDownload = url[0];
            String data = "";

            try{
                data = downloadUrl(urltoDownload);
            }catch(Exception e){
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jObject = new JSONObject(result);
                JSONObject dataObject = jObject.getJSONObject("data");
                String status = dataObject.getString("status");
                if(status == "1"){
                    waiting_container.setVisibility(View.GONE);
                    success_container.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(getApplicationContext(), "No action has been taken on your request so far.",
                            Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private final Runnable mRunnable = new Runnable() {
        public void run() {
            Integer timer = Integer.valueOf(timerBox.getText().toString());
            if(timer > 0){
                timer--;
                timerBox.setText(Integer.toString(timer));
            }else{
                finish();
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

    public void refreshScreen(){
        String url = getRefreshUrl();
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            refreshScreen();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
