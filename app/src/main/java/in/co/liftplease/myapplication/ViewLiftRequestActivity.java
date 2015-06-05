package in.co.liftplease.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.HttpResponse;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ViewLiftRequestActivity extends ActionBarActivity{

    private static Menu mainMenu;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Marker subSourceMarker;
    private String subscriberId;
    private Marker subDestMarker;
    private Marker proSourceMarker;
    private Marker proDestMarker;
    SessionManager session;
    String session_id;
    String provider_route;
    String user_route;


    public static final String TAG = MapsActivity.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final LatLngBounds BOUNDS_GREATER_INDIA = new LatLngBounds(
            new LatLng(8,  68), new LatLng(38, 98));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_lift);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        getDataFromIntent();
        getDataFromSession();

    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    public void getDataFromSession(){
        HashMap<String, String> user = session.getUserDetails();
        session_id = user.get(SessionManager.KEY_SESSION);
        user_route = user.get(SessionManager.KEY_ROUTE);
    }

    public void getDataFromIntent(){
        Intent intent = getIntent();
        String provider_name = intent.getStringExtra("name");
        String provider_image_uri = intent.getStringExtra("image_uri");
        provider_route = intent.getStringExtra("route");
        String provider_org_name = intent.getStringExtra("org_name");
        String provider_org_title = intent.getStringExtra("org_title");
        String stop = intent.getStringExtra("stop");
        String start = intent.getStringExtra("start");
        subscriberId = intent.getStringExtra("id");
        setTitle(provider_name);
    }

    public void showRoute(){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(subSourceMarker.getPosition());
        builder.include(subDestMarker.getPosition());
        builder.include(proSourceMarker.getPosition());
        builder.include(proDestMarker.getPosition());
        LatLngBounds bounds = builder.build();
        int padding = 100;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);
    }

    public void drawRoutefromPolyline(String route, String person_type){
        String sourceMarkerTite = "person type is not defined";
        String destinationMarkerTite = "person type is not defined";
        int color = Color.RED;
        if(person_type=="Provider"){
            sourceMarkerTite = "Provider's source";
            destinationMarkerTite = "Provider's destination";
            color = Color.GREEN;
        }else if(person_type=="User"){
            sourceMarkerTite = "Your source";
            destinationMarkerTite = "Your destination";
            color = Color.BLUE;
        }


        List<LatLng> list = decodePoly(route);
        List path = new ArrayList<HashMap<String, String>>();
        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>();
        for(int l=0;l<list.size();l++){
            if(l==0){
                LatLng latLng = new LatLng(((LatLng)list.get(l)).latitude, ((LatLng)list.get(l)).longitude);
                if(person_type=="Provider"){
                    proSourceMarker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(sourceMarkerTite));
                }else if(person_type=="User"){
                    subSourceMarker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(sourceMarkerTite));
                }
            }
            if(l==list.size()-1){
                LatLng latLng = new LatLng(((LatLng)list.get(l)).latitude, ((LatLng)list.get(l)).longitude);
                if(person_type=="Provider"){
                    proDestMarker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(destinationMarkerTite));
                }else if(person_type=="User"){
                    subDestMarker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(destinationMarkerTite));
                }
            }
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
            path.add(hm);
        }
        routes.add(path);

        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = null;

        // Traversing through all the routes
        for(int i=0;i<routes.size();i++){
            points = new ArrayList<LatLng>();
            lineOptions = new PolylineOptions();

            // Fetching i-th route
            List<HashMap<String, String>> paths = routes.get(i);

            // Fetching all the points in i-th route
            for(int j=0;j<paths.size();j++){
                HashMap<String,String> point = paths.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);
            lineOptions.width(5);
            lineOptions.color(color);
        }
        mMap.addPolyline(lineOptions);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        this.mainMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_view_lift_request, menu);
        return super.onCreateOptionsMenu(menu);
    }

//    private void handleNewLocation(Location location) {
//        Log.d(TAG, location.toString());
//
//        double currentLatitude = location.getLatitude();
//        double currentLongitude = location.getLongitude();
//        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
//
//        sLocationMarker = mMap.addMarker(new MarkerOptions()
//                .position(latLng)
//                .title("You are here"));
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15F), new GoogleMap.CancelableCallback() {
//            @Override
//            public void onFinish() {
//                sLocationMarker.showInfoWindow();
//            }
//            @Override
//            public void onCancel() {
//
//            }
//        });
//    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        drawRoutefromPolyline(provider_route, "Provider");
        drawRoutefromPolyline(user_route, "User");
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                showRoute();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        }
    }


//    @Override
//    public void onLocationChanged(Location location) {
//        handleNewLocation(location);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_accept:
                requestLift();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void requestLift(){
        new MyAsyncTask().execute(subscriberId, session_id);
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://whenisdryday.in:5000/provider/request");

            List nameValuedPairs = new ArrayList();
            nameValuedPairs.add(new BasicNameValuePair("subscriber", params[0]));
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
            } catch (JSONException e) {
                Log.e("JSONException", "Error: " + e.toString());
            }
        }

    }

}
