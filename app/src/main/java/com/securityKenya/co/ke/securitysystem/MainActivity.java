package com.securityKenya.co.ke.securitysystem;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import com.android.volley.VolleyError;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private EditText simpleText;
    private CardView actionEmergency, actionInsecurity, actionCriminal;
    private String simCardSnNumber = null, internalPhoneNumber = null;
    private boolean cancel = false;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private String lng, lat;
    private boolean dataSent = false;
    private String TAG = "MAIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //get phone number and simcard_serial_number
        if (Permissions.check(this, Manifest.permission.READ_PHONE_STATE)) {
            simCardSnNumber = Device.getSimCardSN(this);
            internalPhoneNumber = Device.getPhoneNumber(this);
        }

        //get location service which is a system service
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
            lng = String.valueOf(location.getLongitude());
            lat = String.valueOf(location.getLatitude());
                Log.w("Cord", lng+"   --- "+lat);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);

            }
        };
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String []{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.INTERNET
                },10);
            return;
        }
        }else{

        }
        locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "You cannot Send Custom Message now", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        actionCriminal = (CardView) findViewById(R.id.action_crime_alert);
        actionCriminal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData("0715909266","092929292992929",lat+":"+lng);
                if(simCardSnNumber==null){
                    Snackbar.make(view, "Please insert a sim card in your phone", Snackbar.LENGTH_LONG).setAction("Action",null).show();
                }else{

                }
            }
        });
        actionEmergency = (CardView) findViewById(R.id.action_emergency_alert);
        actionEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData("0715909266","092929292992929","2182:2912919");
                Snackbar.make(view, "Your Location is not enabled "+lng+"  "+lat+"  "+internalPhoneNumber+"  "+simCardSnNumber, Snackbar.LENGTH_LONG).setAction("Action",null).show();
            }
        });



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            Snackbar.make(simpleText,"Error 500: Server Problem", Snackbar.LENGTH_LONG).show();
        } else if (id == R.id.nav_gallery) {
            Snackbar.make(simpleText,"Error 500: Server Problem", Snackbar.LENGTH_LONG).show();

        } //else if (id == R.id.nav_slideshow) {

//        } else? if (id == R.id.nav_manage) {
//
        //}
        else if (id == R.id.nav_share) {

        } //else if (id == R.id.nav_send) {

        //}

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
//
//    /**
//     * Shows the progress UI and hides the login form.
//     */
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
//    private void showProgress(final boolean show) {
//        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
//        // for very easy animations. If available, use these APIs to fade-in
//        // the progress spinner.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
//            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
//
//            loginSV.setVisibility(show ? View.GONE : View.VISIBLE);
//            loginSV.animate().setDuration(shortAnimTime).alpha(
//                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    loginSV.setVisibility(show ? View.GONE : View.VISIBLE);
//                }
//            });
//
//            loginPB.setVisibility(show ? View.VISIBLE : View.GONE);
//            loginPB.animate().setDuration(shortAnimTime).alpha(
//                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    loginPB.setVisibility(show ? View.VISIBLE : View.GONE);
//                }
//            });
//        } else {
//            // The ViewPropertyAnimator APIs are not available, so simply show
//            // and hide the relevant UI components.
//            loginPB.setVisibility(show ? View.VISIBLE : View.GONE);
//            loginSV.setVisibility(show ? View.GONE : View.VISIBLE);
//        }
//    }
private void sendData(String phoneNumber, String simCardSN, String cordonates) {
    if(dataSent == false) {
        dataSent = true;
        JSONObject request = new JSONObject();
        try {
            request.put("phone_number", phoneNumber);
            request.put("sim_card_sn", simCardSN);
            request.put("cordinates", cordonates);

            HTTP.sendRequest(this, HTTP.EP_SEND_DATA, request, new HTTP.OnHTTPResponseListener() {
                @Override
                public void onHTTPResponse(JSONObject response) {
                    Log.d(TAG,"Hamphrey started:"+response);
                    try {
                        if (response.getBoolean("status") == false) {
                            String reason = response.getString("reason");
                            Snackbar.make(simpleText, reason, Snackbar.LENGTH_LONG).show();
                            Log.d(TAG,response.toString());
                        } else {
                            if (response.getBoolean("status") == true) {

                            } else {
                                Snackbar.make(simpleText, "Could not load your data. Please contact the rider team", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, e.toString());
                        Snackbar.make(simpleText, "Sorry, something went wrong. Please try again", Snackbar.LENGTH_LONG).show();
                    }
                    dataSent = false;
                   // showProgress(false);
                }

                @Override
                public void onHTTPError(VolleyError volleyError) {
                    dataSent = false;
                   // showProgress(false);
                    Log.d(TAG, volleyError.toString());
                    Snackbar.make(simpleText, "Sorry, something went wrong. Please try again", Snackbar.LENGTH_LONG).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}


}
