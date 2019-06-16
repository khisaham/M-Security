package com.securityKenya.co.ke.securitysystem;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private Button registerNewUser;
    private TextView signInAgain;
    private AutoCompleteTextView firstNameText, lastNameText, phoneNumberET;
    private EditText passwordText;
    private ProgressBar loginPB;
    private ScrollView loginSV;
    private String TAG = "REG:";
    private boolean dataSent = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        signInAgain = (TextView) findViewById(R.id.sign_in_again);
        signInAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                RegisterActivity.this.startActivity(myIntent);
            }
        });

        registerNewUser = (Button) findViewById(R.id.email_register_in_button);
        registerNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               attemptSignup();
            }
        });

        firstNameText = (AutoCompleteTextView)findViewById(R.id.first_name);
        lastNameText = (AutoCompleteTextView)findViewById(R.id.last_name);
        phoneNumberET = (AutoCompleteTextView)findViewById(R.id.phone_number);

        passwordText = (EditText)findViewById(R.id.password);


        loginSV = (ScrollView)findViewById(R.id.register_form);
        loginPB = (ProgressBar)findViewById(R.id.register_progress);
    }

    private void attemptSignup() {
        /*if (userLoginTask != null) {
            Snackbar.make(idNumberET, "Please wait...", Snackbar.LENGTH_LONG).show();
            return;
        }*/

        // Reset errors.
        phoneNumberET.setError(null);
        passwordText.setError(null);

        // Store values at the time of the login attempt.
        String phoneNumber = phoneNumberET.getText().toString();
        String passWord = passwordText.getText().toString();
        String firstname = firstNameText.getText().toString();
        String lastname = lastNameText.getText().toString();

        boolean cancel = false;
        View focusView = null;
        String simCardSN = null;
        if(Permissions.check(this, Manifest.permission.READ_PHONE_STATE)) {
            simCardSN = Device.getSimCardSN(this);
        }
        if(simCardSN == null) {
            cancel = true;
            Snackbar.make(phoneNumberET, "You will need a simcard to sign in", Snackbar.LENGTH_LONG).show();
        }

        if(cancel == false) {//first things first, make sure the simcard is accessible first
            if(passwordText.getVisibility() == View.VISIBLE) {
                if (TextUtils.isEmpty(passWord)) {
                    passwordText.setError(getString(R.string.error_invalid_phone_number));
                    focusView = passwordText;
                    cancel = true;
                } else if(!isIdNumberValid(passWord)) {
                    passwordText.setError(getString(R.string.error_incorrect_password));
                    focusView = passwordText;
                    cancel = true;
                }
            } else {
                passWord = null;
            }

            if (TextUtils.isEmpty(phoneNumber)) {
                phoneNumberET.setError(getString(R.string.error_invalid_phone_number));
                focusView = phoneNumberET;
                cancel = true;
            } else if (!isPhoneNumberValid(phoneNumber)) {
                phoneNumberET.setError(getString(R.string.error_incorrect_password));
                focusView = phoneNumberET;
                cancel = true;
            }

            if(cancel == false) {
                showProgress(true);
                Log.d(TAG,phoneNumber+"  "+passWord+" "+simCardSN);
                sendData(phoneNumber, simCardSN, passWord, firstname, lastname);
            } else {
                focusView.requestFocus();
            }
        }
    }

    private boolean isPhoneNumberValid(String phoneNumber) {
        return phoneNumber.length() > 8;
    }

    private boolean isIdNumberValid(String idNo) {
        return idNo.length() > 3;
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

    private void sendData(String phoneNumber, String simCardSN, String password, String firstname, String lastname) {
        if (dataSent == false) {
            dataSent = true;
            registerNewUser.setClickable(false);
            JSONObject request = new JSONObject();
            try {
                request.put("phone_number", phoneNumber);
                request.put("pin", password);
                request.put("surname", firstname);
                request.put("first_name", firstname);
                request.put("last_name", lastname);
                request.put("id_number", simCardSN);

                HTTP.sendRequest(this, HTTP.EP_REGISTER, request, new HTTP.OnHTTPResponseListener() {
                    @Override
                    public void onHTTPResponse(JSONObject response) {
                        Log.w(TAG, "Hamphrey:" + response.toString());
                        try {
                            if (response.getBoolean("status") == true) {
                                Log.w(TAG, " Response: " + response.toString());
                                Intent myIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                RegisterActivity.this.startActivity(myIntent);
                            } else {
                                Log.w(TAG, " Response: " + response.toString());
                                Snackbar.make(firstNameText, "Error creatng New User ", Snackbar.LENGTH_INDEFINITE).show();
                                response.getBoolean("status");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, e.toString());
                            Snackbar.make(firstNameText, "Sorry, something went wrong. Please try again", Snackbar.LENGTH_LONG).show();
                        }
                        dataSent = false;
                        registerNewUser.setClickable(true);
                        showProgress(false);
                    }

                    @Override
                    public void onHTTPError(VolleyError volleyError) {
                        dataSent = false;
                        registerNewUser.setClickable(true);
                        showProgress(false);
                        Log.d(TAG, volleyError.toString());
                        Snackbar.make(firstNameText, "Sorry, something went wrong. Please try again", Snackbar.LENGTH_LONG).show();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
