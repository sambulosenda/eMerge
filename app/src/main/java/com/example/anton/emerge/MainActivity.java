package com.example.anton.emerge;


import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.kairos.Kairos;
import com.kairos.KairosListener;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;


public class MainActivity extends ActionBarActivity {
    private TextView mainTextView;
    private static final int request_code = 5;
    public CallbackManager mCallbackManager;

    public static String userId;
    public static Uri userLink;
    public static String picUri;
    public static String firstName;
    public static String lastName;

    //instatiate Kairos
    public static Kairos myKairos = new Kairos();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_main);
        setTitle("LoginExample Cool App");

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
        loginButton.setReadPermissions("email");
        LoginManager.getInstance().logOut(); //ensures that whenever we start the app we're logged out

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code

                        Profile curProfile = Profile.getCurrentProfile();
                        startNewIntent(curProfile);
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });


        // set authentication
        Resources res = getResources();

        String app_id = res.getString(R.string.kairos_app_id);
        String api_key = res.getString(R.string.kairos_app_key);
        myKairos.setAuthentication(this, app_id, api_key);

    }

    private void startNewIntent(Profile elUsero) {

        // Create an instance of the KairosListener
        KairosListener listener = new KairosListener() {

            @Override
            public void onSuccess(String response) {
                if (response.contains(userId)) {
                    Intent homeIntent = new Intent(MainActivity.this, HomePage.class);
                    homeIntent.putExtra("picURI", picUri);
                    startActivityForResult(homeIntent, request_code);
                }
                else{
                Log.d("KAIROS DEMO", response);
                Intent cameraIntent = new Intent(MainActivity.this, Camera.class);
                cameraIntent.putExtra("picURI",picUri );
                startActivityForResult(cameraIntent, request_code);}
            }

            @Override
            public void onFail(String response) {
                // your code here!
                Log.d("KAIROS DEMO", response);
            }
        };


        firstName = elUsero.getFirstName();
        lastName = elUsero.getLastName();
        userLink = elUsero.getLinkUri();
        picUri = elUsero.getProfilePictureUri(1500, 1500).toString();
        userId = elUsero.getId();

        try {

            // List out all subjects in a given gallery
            String galleryId = "friends";
            myKairos.listSubjectsForGallery(galleryId, listener);

        } catch (JSONException e1) {
        } catch (UnsupportedEncodingException e) {
        }
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
    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == request_code) && (resultCode == RESULT_OK)) {
            LoginManager.getInstance().logOut();
            mainTextView.setText("Status: Logged Out!");
        }
    }
}