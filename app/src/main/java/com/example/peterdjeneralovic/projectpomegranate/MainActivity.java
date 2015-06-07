package com.example.peterdjeneralovic.projectpomegranate;

import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.ibm.mobile.services.core.http.IBMHttpResponse;
import com.ibm.mobile.services.data.IBMData;
import com.ibm.mobile.services.core.IBMBluemix;
import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.push.IBMPush;
import com.ibm.mobile.services.cloudcode.IBMCloudCode;
import com.ibm.mobile.services.push.IBMPushNotificationListener;
import com.ibm.mobile.services.push.IBMSimplePushNotification;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import bolts.Continuation;
import bolts.Task;

import com.clusterpoint.api.CPSConnection;
import com.clusterpoint.api.request.CPSInsertRequest;
import com.clusterpoint.api.response.CPSModifyResponse;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import com.clusterpoint.api.request.CPSLookupRequest;
import com.clusterpoint.api.response.CPSLookupResponse;
import java.util.List;
//import java.util.Element;
import org.w3c.dom.Element;
import java.util.Iterator;
import com.clusterpoint.api.request.CPSRetrieveRequest;
import com.clusterpoint.api.response.CPSListLastRetrieveFirstResponse;


public class MainActivity extends ActionBarActivity {

    private static final String APP_ID = "28b63d06-eacb-4b8c-9c41-bb752d2306b6";
    private static final String APP_SECRET = "5163ce87b55c398c77f688a958220b12e5823e68";
    private static final String APP_ROUTE = "http://angelhack-mtl.mybluemix.net";
    private static final String CLASS_NAME = "MainActvitity";
    IBMPush push = null;
    IBMCloudCode cloudCodeService =null;
    IBMPushNotificationListener notificationListener = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IBMBluemix.initialize(this, APP_ID, APP_SECRET, APP_ROUTE);
        Item.registerSpecialization(Item.class);
        cloudCodeService = IBMCloudCode.initializeService();
        IBMData.initializeService();
        IBMPush push = IBMPush.initializeService();


        push.register("Nexus5", "PeterDjeneralovic").continueWith(new Continuation<String, Object>() {
            @Override
            public Void then(Task task) throws Exception {
                if (task.isFaulted()) {
                    Exception e = task.getError();

                } else if (task.isCancelled()) {
                    Log.e("MainActivity", "Cancelled Push Notification");
                } else {
                    Log.d("MainActivity", "Registered Device" + task.getResult());
                }

                return null;
            }
        });


        IBMPushNotificationListener notificationListener = new IBMPushNotificationListener() {
            @Override
            public void onReceive(IBMSimplePushNotification ibmSimplePushNotification) {
                Log.d("MainActivity","Received Push Notification");
            }
        };

        push.listen(notificationListener);
        createItem();

        new getData().execute("");

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container,  new ConnectFragment());
        transaction.addToBackStack(null);
        transaction.commit();

    }

    public void createItem() {

        String toAdd = "Awesome This Works!";
        Item item = new Item();
        if (!toAdd.equals("")) {
            item.setName(toAdd);
            // Use the IBMDataObject to create and persist the Item object.
            item.save().continueWith(new Continuation<IBMDataObject, Void>() {

                @Override
                public Void then(Task<IBMDataObject> task) throws Exception {
                    // Log if the save was cancelled.
                    if (task.isCancelled()) {
                        Log.e(CLASS_NAME, "Exception : Task " + task.toString() + " was cancelled.");
                    }
                    // Log error message, if the save task fails.
                    else if (task.isFaulted()) {
                        Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
                    }

                    // If the result succeeds, load the list.
                    else {
                        Log.d("MainActivity", "SUCCESS!");
                    }
                    return null;
                }

            });

        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (push != null) {
            // Request IBMPush to deliver incoming push messages to notificationListener.onReceive() method
            push.listen(notificationListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (push != null) {
            // Request IBMPush to stop delivering incoming push messages to notificationListener.onReceive() method.
            // After hold(), IBMPush will store the latest push message in private shared preference
            // and deliver that message during the next listen().
            push.hold();
        }
    }

    public void CloudPost (JSONObject payload){


        Log.d("CloudService", payload.toString());

        cloudCodeService.post("/write", payload).continueWith(new Continuation<IBMHttpResponse, Void>() {

            @Override
            public Void then(Task<IBMHttpResponse> task) throws Exception {
                if (task.isFaulted()) {
                    Log.d("MainActivity", "didn't work ....");
                } else {
                    IBMHttpResponse response = task.getResult();
                    Log.d("MainActivity", "THIS BETTER WORK 2" + task.getResult());
                    if (response.getHttpResponseCode() == 200) {
                        Log.d("MainActivity", "THIS BETTER WORK" + task.getResult());
                    }
                }
                return null;
            }
        });

    }

    private class getData extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {

            InputStream is = null;
            String result = "";
            JSONObject jArray = null;

            // Download JSON data from URL
            try {
                    //CPSConnection conn = new CPSConnection("tcp://cloud-us-0.clusterpoint.com:9007", "SampleBob", "dan.crisan@live.com", "Totonel1991", "100337", "document", "//document/id");
                    //CPSConnection conn = new CPSConnection("tcp://cloud-us-0.clusterpoint.com:9007", "DATABASE_NAME", "EMAIL", "PASSWORD", "ACCOUNT_ID", "document", "//document/id");

                    URL url = new URL("https://api-us.clusterpoint.com/100337/SampleBob");

                    URLConnection uc = url.openConnection();

                    String userpass = "dan.crisan@live.com" + ":" + "Totonel1991";
                    String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());

                    uc.setRequestProperty ("Authorization", basicAuth);
                    InputStream in = uc.getInputStream();
                    InputStreamReader isr = new InputStreamReader(in);

                    int numCharsRead;
                    char[] charArray = new char[1024];
                    StringBuffer sb = new StringBuffer();
                    while ((numCharsRead = isr.read(charArray)) > 0) {
                        sb.append(charArray, 0, numCharsRead);
                    }
                    String result = sb.toString();
                    System.out.println(result);

            } catch (Exception e) {
                Log.e("log_tag", "Error in http connection " + e.toString());
            }

            // Convert response to string


        return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {}

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
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
        if (id == R.id.menu_new) {
            return true;
        }
        else if (id == R.id.menu_history) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new HistoryFragment());
            transaction.addToBackStack(null);
            transaction.commit();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
