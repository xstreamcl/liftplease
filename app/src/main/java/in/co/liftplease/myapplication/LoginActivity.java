package in.co.liftplease.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
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

public class LoginActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, ResultCallback<People.LoadPeopleResult> {

    private ProgressBar spinner;
    private SignInButton signInButton;
    private TextView signInText;
    private static final int ACCOUNT_PICKER_REQUEST_CODE = 42;

    SessionManager session;


    String g_id = null;
    String name = null;
    String gender = null;
    String about = null;
    String email = null;
    String image_uri = null;
    String org_name = null;
    String org_title = null;
    String device_id = null;
    String phone_number= null;

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    /**
     * True if the sign-in button was clicked.  When true, we know to resolve all
     * issues preventing sign-in without waiting.
     */
    private boolean mSignInClicked;
    private boolean mIntentInProgress = false;
    private GoogleApiClient mGoogleApiClient;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        session = new SessionManager(getApplicationContext());
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        signInText = (TextView)findViewById(R.id.sign_in_text);
        signInButton = (SignInButton)findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();

    }

    protected void onStart() {
        super.onStart();
    }

    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!mIntentInProgress) {
            if (mSignInClicked && result.hasResolution()) {
                try {
                    result.startResolutionForResult(this, RC_SIGN_IN);
                    mIntentInProgress = true;
                } catch (IntentSender.SendIntentException e) {
                    mIntentInProgress = false;
                    mGoogleApiClient.connect();
                }
            } else {
                signInButton.setVisibility(View.VISIBLE);
                signInText.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.GONE);
            }
        } else {
            signInButton.setVisibility(View.VISIBLE);
            signInText.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }
            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.reconnect();
            }
        }
    }

    public void onConnectionSuspended(int cause) {
        signInButton.setVisibility(View.VISIBLE);
        mGoogleApiClient.connect();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_in_button && !mGoogleApiClient.isConnecting()) {
            signInButton.setVisibility(View.GONE);
            signInText.setVisibility(View.GONE);
            spinner.setVisibility(View.VISIBLE);
            mSignInClicked = true;
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mSignInClicked = false;
        HashMap<String, String> user = session.getUserDetails();
        String sessin_id = user.get(SessionManager.KEY_SESSION);
        if(sessin_id != null){
            proceed();
        }else{
            Plus.PeopleApi.loadVisible(mGoogleApiClient, null).setResultCallback(this);
        }
    }

    @Override
    public void onResult(People.LoadPeopleResult loadPeopleResult) {
        if (Plus.AccountApi.getAccountName(mGoogleApiClient) != null) {
            email = Plus.AccountApi.getAccountName(mGoogleApiClient);
        }
//        if(!email.endsWith("@flipkart.com")){
//            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
//            mGoogleApiClient.disconnect();
//            signInButton.setVisibility(View.VISIBLE);
//            signInText.setVisibility(View.VISIBLE);
//            spinner.setVisibility(View.GONE);
//            Toast.makeText(this, "Access restricted to Flipkart employees only", Toast.LENGTH_LONG).show();
//            return;
//        }
        TelephonyManager tManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        device_id = tManager.getDeviceId();
        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            g_id = currentPerson.getId();
            name = currentPerson.getDisplayName();
            gender = String.valueOf(currentPerson.getGender());
            about = currentPerson.getAboutMe();
            image_uri = currentPerson.getImage().getUrl();
            List<Person.Organizations> organizationsList = currentPerson.getOrganizations();
            if(organizationsList != null){
                int i = 0;
                while (i < organizationsList.size()) {
                    if(organizationsList.get(i).getType() == 0){
                        org_name = organizationsList.get(i).getName();
                        org_title = organizationsList.get(i).getTitle();
                    }
                    i++;
                }
            }
        }
        new MyAsyncTask().execute(g_id,name,gender,email,image_uri,org_name,org_title,device_id,about);
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://whenisdryday.in:5000/user");

            List nameValuedPairs = new ArrayList();
            nameValuedPairs.add(new BasicNameValuePair("g_id", params[0]));
            nameValuedPairs.add(new BasicNameValuePair("name", params[1]));
            nameValuedPairs.add(new BasicNameValuePair("gender", params[2]));
            nameValuedPairs.add(new BasicNameValuePair("email", params[3]));
            nameValuedPairs.add(new BasicNameValuePair("image_uri", params[4]));
            nameValuedPairs.add(new BasicNameValuePair("org_name", params[5]));
            nameValuedPairs.add(new BasicNameValuePair("org_title", params[6]));
            nameValuedPairs.add(new BasicNameValuePair("device_id", params[7]));
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
                String key = dataObject.getString("key");
                String phone = dataObject.getString("phone");
                if(phone == "null" || phone == "" || phone == null){
                    showPhoneNumberScreen(name,email,image_uri,key,"0");
                }else{
                    session.createLoginSession(name,email,image_uri,key,"0",phone);
                    proceed();
                }
            } catch (JSONException e) {
                Log.e("JSONException", "Error: " + e.toString());
            }
        }

    }

    public void showPhoneNumberScreen(String name, String email, String image_uri, String key, String status){
        Intent intent = new Intent(getApplicationContext(), UpdatePhone.class);
        intent.putExtra("name", name);
        intent.putExtra("email", email);
        intent.putExtra("image_uri", image_uri);
        intent.putExtra("key", key);
        intent.putExtra("status", status);
        startActivity(intent);
        finish();
    }

    public void proceed(){
        Intent intent = new Intent(this, DefaultActivity.class);
        startActivity(intent);
        finish();
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
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

}
