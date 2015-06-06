package in.co.liftplease.myapplication;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SubListActivity extends ActionBarActivity {

    SessionManager session;
    String userProfileImage;
    private Handler mHandler;
    private boolean mCountersActive;
    private TextView timerBox;
    private ArrayList<ListViewItem> mItems = new ArrayList<ListViewItem>();
    private ArrayAdapter<ListViewItem> mListAdapter;
    private JSONArray listToDisplay;
    String session_id;
    ListView listView;
    HashMap<String, String> user;

    public SubListActivity() {
        mHandler = new Handler();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_list);
        timerBox = (TextView)findViewById(R.id.timer);
        listView = (ListView) findViewById(android.R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> arg0, View view,int position, long id) {
                    Object o = listView.getItemAtPosition(position);
                    Intent intent = new Intent(getApplicationContext(), ViewLiftRequestActivity.class);
                    intent.putExtra("name", ((ListViewItem) o).name);
                    intent.putExtra("image_uri", ((ListViewItem) o).image_uri);
                    intent.putExtra("route", ((ListViewItem) o).route);
                    intent.putExtra("org_name", ((ListViewItem) o).org_name);
                    intent.putExtra("org_title", ((ListViewItem) o).org_title);
                    intent.putExtra("route", ((ListViewItem) o).route);
                    intent.putExtra("id", ((ListViewItem) o).id);
                    intent.putExtra("phone", ((ListViewItem) o).phone);
                    startActivity(intent);
                }
            }
        );

        mItems = new ArrayList<ListViewItem>();
        mListAdapter = new ListViewDemoAdapter(getApplicationContext(), mItems);
        listView.setAdapter(mListAdapter);

        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();

        startRunnable();
    }

    public void onSuccess(){


        JSONArray listArray = getListData();

        for(int i = 0; i < listArray.length(); i++)
        {
            try {
                JSONObject object = listArray.getJSONObject(i);
                int waiting_since = 120 - (int) Double.parseDouble(object.getString("waiting_since"));
                if(waiting_since > 0){
                    mItems.add(new ListViewItem(object.getString("image"), object.getString("name"), object.getString("org_title"), object.getString("org_name"), waiting_since, object.getString("subscriber_route"), object.getString("id"), object.getString("phone")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // initialize and set the list adapter
        mListAdapter.notifyDataSetChanged();
        startRunnable();
    }

    public JSONArray getListData(){
        return this.listToDisplay;
    }

    private void startRunnable() {
        if (!mCountersActive) {
            mCountersActive = true;
            mHandler.post(mRunnable);
        }
    }

    private final Runnable mRunnable = new Runnable() {
        public void run() {
            ListViewItem myData;
            // if counters are active
            if (mCountersActive) {
                if (mItems != null) {
                    for (int i=0; i < mItems.size(); i++) {
                        myData = mItems.get(i);
                        if (myData.getCount() >= 0) {
                            myData.reduceCount();
                        } else {
                            mItems.remove(i);
                        }
                    }
                    // notify that data has been changed
                    mListAdapter.notifyDataSetChanged();
                }
                Integer timer = Integer.valueOf(timerBox.getText().toString());
                if(timer > 0){
                    timer--;
                    timerBox.setText(Integer.toString(timer));
                }else{
                    Toast.makeText(getApplicationContext(), "Your offer is now expired.", Toast.LENGTH_LONG).show();
                    if(listToDisplay.length() == 0){
                        finish();
                    }
                }
                // update every second
                mHandler.postDelayed(this, 1000);
            }
        }
    };

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //this returns you an object cast it into the same object you passed to your adapter cast to string if values passed were string
    }

    private class ListViewItem {
        public final String image_uri;
        public final String name;
        public final String org_title;
        public final String org_name;
        private int waiting_since;
        public final String route;
        public final String id;
        public final String phone;
        public ListViewItem(String image_uri, String name, String org_title, String org_name, int waiting_since, String route, String id, String phone) {
            this.image_uri = image_uri;
            this.name = name;
            this.org_title = org_title;
            this.org_name = org_name;
            this.waiting_since = waiting_since;
            this.route = route;
            this.id = id;
            this.phone = phone;
        }
        public String getName() {
            return name;
        }
        public int getCount() {
            return waiting_since;
        }
        public String getCountAsString() {
            return Integer.toString(waiting_since);
        }
        public void reduceCount() {
            if (waiting_since > 0) {
                waiting_since--;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sub_list, menu);
        return super.onCreateOptionsMenu(menu);
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

    public void refreshScreen(){
        String url = getRefreshUrl();
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);
    }

    private String getRefreshUrl(){
        String session_id = "key="+user.get(SessionManager.KEY_SESSION);
        String route = "route="+user.get(SessionManager.KEY_ROUTE);
        String parameters = session_id+"&"+route;
        String url = "http://whenisdryday.in:5000/provider/refresh"+"?"+parameters;
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

    private class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... url) {
            String urltoDownload = url[0];
            String data = "";

            try{
                data = downloadUrl(urltoDownload);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mItems.clear();
            try {
                JSONObject jObject = new JSONObject(result);
                JSONObject dataObject = jObject.getJSONObject("data");
                listToDisplay = dataObject.getJSONArray("subscribers");
                onSuccess();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class ListViewDemoAdapter extends ArrayAdapter<ListViewItem> {

        ViewHolder viewHolder;
        private ArrayList<ListViewItem> items;

        public ListViewDemoAdapter(Context context, List<ListViewItem> items) {
            super(context, R.layout.listview_item, items);
            this.items = (ArrayList<ListViewItem>) items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null) {
                // inflate the GridView item layout
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.listview_item, parent, false);

                // initialize the view holder
                viewHolder = new ViewHolder();
                viewHolder.image_uri = (ImageView) convertView.findViewById(R.id.image_uri);
                viewHolder.name = (TextView) convertView.findViewById(R.id.name);
                viewHolder.org_info = (TextView) convertView.findViewById(R.id.org_info);
                viewHolder.time_elapsed = (TextView) convertView.findViewById(R.id.time_elapsed);
                viewHolder.route = (TextView) convertView.findViewById(R.id.route);
                viewHolder.stop = (TextView) convertView.findViewById(R.id.stop);
                viewHolder.start = (TextView) convertView.findViewById(R.id.start);
                viewHolder.phone = (TextView) convertView.findViewById(R.id.phone);
                convertView.setTag(viewHolder);
            } else {
                // recycle the already inflated view
                viewHolder = (ViewHolder) convertView.getTag();
            }

            // update the item view
            ListViewItem item = getItem(position);


            viewHolder.name.setText(item.name);
            viewHolder.org_info.setText(item.org_title+" at "+item.org_name);
            viewHolder.time_elapsed.setText(item.getCountAsString());
            if (viewHolder.image_uri != null) {
                new ImageDownloaderTask(viewHolder.image_uri).execute(item.image_uri);
            }
            return convertView;
        }

        private class ViewHolder {
            ImageView image_uri;
            TextView name;
            TextView org_info;
            TextView time_elapsed;
            TextView route;
            TextView stop;
            TextView start;
            TextView phone;
        }

        public class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
            private final WeakReference<ImageView> imageViewReference;

            public ImageDownloaderTask(ImageView imageView) {
                imageViewReference = new WeakReference<ImageView>(imageView);
            }

            @Override
            protected Bitmap doInBackground(String... params) {
                return downloadBitmap(params[0]);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (isCancelled()) {
                    bitmap = null;
                }

                if (imageViewReference != null) {
                    ImageView imageView = imageViewReference.get();
                    if (imageView != null) {
                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap);
                        } else {
                            Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.ic_launcher);
                            imageView.setImageDrawable(placeholder);
                        }
                    }
                }
            }
        }
        private Bitmap downloadBitmap(String url) {
            HttpURLConnection urlConnection = null;
            try {
                URL uri = new URL(url);
                urlConnection = (HttpURLConnection) uri.openConnection();
                int statusCode = urlConnection.getResponseCode();
                if (statusCode != HttpStatus.SC_OK) {
                    return null;
                }

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                }
            } catch (Exception e) {
                urlConnection.disconnect();
                Log.w("ImageDownloader", "Error downloading image from " + url);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://whenisdryday.in:5000/provider");

            List nameValuedPairs = new ArrayList();
            nameValuedPairs.add(new BasicNameValuePair("route", params[0]));
            nameValuedPairs.add(new BasicNameValuePair("key", params[1]));
            try {
                // UrlEncodedFormEntity is an entity composed of a list of url-encoded pairs.
                //This is typically useful while sending an HTTP POST request.
                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuedPairs);

                // setEntity() hands the entity (here it is urlEncodedFormEntity) to the request.
                httppost.setEntity(urlEncodedFormEntity);

                try {
                    // HttpResponse is an interface just like HttpPost.
                    //Therefore we can't initialize them
                    HttpResponse httpResponse = httpclient.execute(httppost);

                    // According to the JAVA API, InputStream constructor do nothing.
                    //So we can't initialize InputStream although it is not an interface
                    InputStream inputStream = httpResponse.getEntity().getContent();

                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    StringBuilder stringBuilder = new StringBuilder();

                    String bufferedStrChunk = null;

                    while((bufferedStrChunk = bufferedReader.readLine()) != null){
                        stringBuilder.append(bufferedStrChunk);
                    }

                    return stringBuilder.toString();

                } catch (ClientProtocolException cpe) {
                    System.out.println("First Exception caz of HttpResponese :" + cpe);
                    cpe.printStackTrace();
                } catch (IOException ioe) {
                    System.out.println("Second Exception caz of HttpResponse :" + ioe);
                    ioe.printStackTrace();
                }

            } catch (UnsupportedEncodingException uee) {
                System.out.println("An Exception given because of UrlEncodedFormEntity argument :" + uee);
                uee.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            String result1 = result;
            try {
                JSONObject jObject = new JSONObject(result);
                JSONObject dataObject = jObject.getJSONObject("data");
                listToDisplay = new JSONArray();
            } catch (JSONException e) {
                Log.e("JSONException", "Error: " + e.toString());
            }
        }

    }
    protected void onResume() {
        super.onResume();
        if(listToDisplay == null){
            String session_id = user.get(SessionManager.KEY_SESSION);
            String route = user.get(SessionManager.KEY_ROUTE);
            new MyAsyncTask().execute(route, session_id);
        } else {
            refreshScreen();
        }

    }

}