package com.securityKenya.co.ke.securitysystem;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private static Button signInButton;
    private EditText phone_number, full_names, sim_crad_sn,passwordText;
    private ScrollView loginSV;
    private boolean dataSent = false;
    private ProgressBar loginPB;
    private Button sign_up;
    private String TAG = "REGISTER";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        phone_number = (EditText) findViewById(R.id.phone_number);
        if(Permissions.check(this, Manifest.permission.READ_PHONE_STATE)) {
            String phoneNumber = Device.getPhoneNumber(this);
            if(phoneNumber != null) {
                phone_number.setText(phoneNumber);
            }
        }
        full_names = (EditText)findViewById(R.id.full_names);
        passwordText = (EditText)findViewById(R.id.password);

        loginSV = (ScrollView)findViewById(R.id.login_form);
        loginPB = (ProgressBar)findViewById(R.id.login_progress);

        signInButton = (Button) findViewById(R.id.register_btn);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                RegisterActivity.this.startActivity(myIntent);
            }
        });
    }

    private void attemptSignUp() {
        /*if (userLoginTask != null) {
            Snackbar.make(idNumberET, "Please wait...", Snackbar.LENGTH_LONG).show();
            return;
        }*/

        // Reset errors.
        phone_number.setError(null);
        passwordText.setError(null);
        full_names.setError(null);

        // Store values at the time of the registration attempt.
        String phoneNumber = phone_number.getText().toString();
        String password = passwordText.getText().toString();
        String fullNames = full_names.getText().toString();


        boolean cancel = false;
        View focusView = null;
        String simCardSN = null;
        if(Permissions.check(this, Manifest.permission.READ_PHONE_STATE)) {
            simCardSN = Device.getSimCardSN(this);
        }
        if(simCardSN == null) {
            cancel = true;
            Snackbar.make(passwordText, "You will need a simcard to sign in", Snackbar.LENGTH_LONG).show();
        }

        if(cancel == false) {//first things first, make sure the simcard is accessible first
            if(passwordText.getVisibility() == View.VISIBLE) {
                if (TextUtils.isEmpty(password)) {
                    passwordText.setError(getString(R.string.error_incorrect_password));
                    focusView = passwordText;
                    cancel = true;
                }
            } else {
                password = null;
            }

            if (TextUtils.isEmpty(phoneNumber)) {
                phone_number.setError(getString(R.string.error_invalid_phone_number));
                focusView = phone_number;
                cancel = true;
            }

            if(cancel == false) {
                showProgress(true);
                sendData(phoneNumber, simCardSN, password, fullNames);
            } else {
                focusView.requestFocus();
            }
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            loginSV.setVisibility(show ? View.GONE : View.VISIBLE);
            loginSV.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginSV.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            loginPB.setVisibility(show ? View.VISIBLE : View.GONE);
            loginPB.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginPB.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            loginPB.setVisibility(show ? View.VISIBLE : View.GONE);
            loginSV.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    private void sendData(String phoneNumber, String simCardSN, String idNumber, String full_names) {
        if(dataSent == false) {
            dataSent = true;
            sign_up.setClickable(false);
            JSONObject request = new JSONObject();
            try {
                request.put("phone_number", phoneNumber);
                request.put("username", simCardSN);
                request.put("first_name", full_names);
                request.put("last_name", full_names);
                request.put("id_number", phoneNumber);
                request.put("pin", passwordText);
                if (idNumber != null) {
                    request.put("id_no", idNumber);
                }
                HTTP.sendRequest(this, HTTP.EP_REGISTER, request, new HTTP.OnHTTPResponseListener() {
                    @Override
                    public void onHTTPResponse(JSONObject response) {
                        Log.d(TAG,"Hamphrey started:"+response);
                        try {
                            if (response.getBoolean("status") == false) {
                                String reason = response.getString("reason");
                                Snackbar.make(phone_number, reason, Snackbar.LENGTH_LONG).show();
                                phone_number.setVisibility(View.VISIBLE);
                                Log.d(TAG,response.toString());
                            } else {
                                if (response.getBoolean("rider_data_status") == true) {

                                    Log.d(TAG, "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
                                } else {
                                    Snackbar.make(phone_number, "Could not load your data. Please contact the rider team", Snackbar.LENGTH_LONG).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, e.toString());
                            Snackbar.make(phone_number, "Sorry, something went wrong. Please try again", Snackbar.LENGTH_LONG).show();
                        }
                        dataSent = false;
                        sign_up.setClickable(true);
                        showProgress(false);
                    }

                    @Override
                    public void onHTTPError(VolleyError volleyError) {
                        dataSent = false;
                        sign_up.setClickable(true);
                        showProgress(false);
                        Log.d(TAG, volleyError.toString());
                        Snackbar.make(phone_number, "Sorry, something went wrong. Please try again", Snackbar.LENGTH_LONG).show();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
