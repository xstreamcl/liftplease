package in.co.liftplease.myapplication;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListFragment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yogesh.choudhary on 03/06/15.
 */
public class ListViewDemoFragment extends ListFragment {
    SessionManager session;
    String userProfileImage;
    private Handler mHandler;
    private boolean mCountersActive;
    private ArrayList<ListViewItem> mItems = new ArrayList<ListViewItem>();
    private ArrayAdapter<ListViewItem> mListAdapter;

    public ListViewDemoFragment() {
        mHandler = new Handler();
    }

    private void stopStart() {
        if (mCountersActive) {
            mCountersActive = false;
        } else {
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
                        }
                    }
                    // notify that data has been changed
                    mListAdapter.notifyDataSetChanged();
                }
                // update every second
                mHandler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // initialize the items list
        mItems = new ArrayList<ListViewItem>();
        Resources resources = getResources();
        session = new SessionManager(getActivity());

        HashMap<String, String> user = session.getUserDetails();
        userProfileImage = user.get(SessionManager.KEY_IMAGE);


        JSONArray listArray = (JSONArray)((MapsActivity) getActivity()).getListData();

        for(int i = 0; i < listArray.length(); i++)
        {
            try {
                JSONObject object = listArray.getJSONObject(i);
                int trip_elapsed_time = (int) Double.parseDouble(object.getString("trip_elapsed_time"));
                mItems.add(new ListViewItem(object.getString("image"), object.getString("name"), object.getString("org_title"), object.getString("org_name"), trip_elapsed_time));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // initialize and set the list adapter
        mListAdapter = new ListViewDemoAdapter(getActivity(), mItems);
        setListAdapter(mListAdapter);
        stopStart();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // remove the dividers from the ListView of the ListFragment
//        getListView().setDivider(null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // retrieve theListView item
        ListViewItem item = mItems.get(position);

        // do something
        Toast.makeText(getActivity(), item.name, Toast.LENGTH_SHORT).show();
    }

    private class ListViewItem {
        public final String image_uri;
        public final String name;
        public final String org_title;
        public final String org_name;
        private int time_elapsed;
        public ListViewItem(String image_uri, String name, String org_title, String org_name, int time_elapsed) {
            this.image_uri = image_uri;
            this.name = name;
            this.org_title = org_title;
            this.org_name = org_name;
            this.time_elapsed = time_elapsed;
        }
        public String getName() {
            return name;
        }
        public int getCount() {
            return time_elapsed;
        }
        public String getCountAsString() {
            return Integer.toString(time_elapsed);
        }
        public void reduceCount() {
            if (time_elapsed > 0) {
                time_elapsed--;
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
}
