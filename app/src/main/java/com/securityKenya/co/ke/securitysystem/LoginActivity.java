package com.securityKenya.co.ke.securitysystem;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    private final static String TAG = "LoginActivity";
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    //private UserLoginTask userLoginTask = null;

    private EditText phoneNumberET, idNumberET;
    private TextView versionTV;
    private Button loginB, registerButton;
    private ProgressBar loginPB;
    private ScrollView loginSV;
    private boolean dataSent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        phoneNumberET = (EditText) findViewById(R.id.phone_number);
        if(Permissions.check(this, Manifest.permission.READ_PHONE_STATE)) {
            String phoneNumber = Device.getPhoneNumber(this);
            if(phoneNumber != null) {
                phoneNumberET.setText(phoneNumber);
            }
        }

        idNumberET = (EditText) findViewById(R.id.password);
        idNumberET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login_form || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        loginB = (Button) findViewById(R.id.email_sign_in_button);
        loginB.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        registerButton = (Button) findViewById(R.id.register_btn);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(myIntent);
            }
        });

        loginSV = (ScrollView)findViewById(R.id.login_form);
        loginPB = (ProgressBar)findViewById(R.id.login_progress);

        versionTV = (TextView)findViewById(R.id.version_sv);
        try {
            versionTV.setText("Version "+Device.getVersionName(LoginActivity.this));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        /*if (userLoginTask != null) {
            Snackbar.make(idNumberET, "Please wait...", Snackbar.LENGTH_LONG).show();
            return;
        }*/

        // Reset errors.
        phoneNumberET.setError(null);
        idNumberET.setError(null);

        // Store values at the time of the login attempt.
        String phoneNumber = phoneNumberET.getText().toString();
        String idNumber = idNumberET.getText().toString();

        boolean cancel = false;
        View focusView = null;
        String simCardSN = null;
        if(Permissions.check(this, Manifest.permission.READ_PHONE_STATE)) {
            simCardSN = Device.getSimCardSN(this);
        }
        if(simCardSN == null) {
            cancel = true;
            Snackbar.make(idNumberET, "You will need a simcard to sign in", Snackbar.LENGTH_LONG).show();
        }

        if(cancel == false) {//first things first, make sure the simcard is accessible first
            if(idNumberET.getVisibility() == View.VISIBLE) {
                if (TextUtils.isEmpty(idNumber)) {
                    idNumberET.setError(getString(R.string.error_invalid_phone_number));
                    focusView = idNumberET;
                    cancel = true;
                } else if(!isIdNumberValid(idNumber)) {
                    idNumberET.setError(getString(R.string.error_incorrect_password));
                    focusView = idNumberET;
                    cancel = true;
                }
            } else {
                idNumber = null;
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
                Log.d(TAG,phoneNumber+"  "+idNumber+" "+simCardSN);
                sendData(phoneNumber, simCardSN, idNumber);
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

    private void sendData(String phoneNumber, String simCardSN, String idNumber) {
        if(dataSent == false) {
            dataSent = true;
            loginB.setClickable(false);
            JSONObject request = new JSONObject();
            try {
                request.put("phone_number", phoneNumber);
                request.put("pin", idNumber);

                HTTP.sendRequest(this, HTTP.EP_LOGIN_APP, request, new HTTP.OnHTTPResponseListener() {
                    @Override
                    public void onHTTPResponse(JSONObject response) {
                        Log.d(TAG,"Hamphrey:"+response);
                        try {
                            if (response.getBoolean("status") == false) {
                                String reason = response.getString("reason");
                                Snackbar.make(idNumberET, reason, Snackbar.LENGTH_LONG).show();
                                idNumberET.setVisibility(View.VISIBLE);
                                Log.d(TAG,response.toString());
                            } else {
                                if (response.getBoolean("rider_data_status") == true) {
                                    Log.d(TAG, "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
                                } else {
                                    Snackbar.make(idNumberET, "Could not load your data. Please contact the rider team", Snackbar.LENGTH_LONG).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, e.toString());
                            Snackbar.make(idNumberET, "Sorry, something went wrong. Please try again", Snackbar.LENGTH_LONG).show();
                        }
                        dataSent = false;
                        loginB.setClickable(true);
                        showProgress(false);
                    }

                    @Override
                    public void onHTTPError(VolleyError volleyError) {
                        dataSent = false;
                        loginB.setClickable(true);
                        showProgress(false);
                        Log.d(TAG, volleyError.toString());
                        Snackbar.make(idNumberET, "Sorry, something went wrong. Please try again", Snackbar.LENGTH_LONG).show();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private final String phoneNumber;
        private final String idNumber;
        private final String simCardSN;

        UserLoginTask(String phoneNumber, String idNumber, String simCardSN) {
            this.phoneNumber = phoneNumber;
            this.idNumber = idNumber;
            this.simCardSN = simCardSN;
        }

        @Override
        protected String doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(final String riderJSON) {
            userLoginTask = null;
            showProgress(false);

        }

        @Override
        protected void onCancelled() {
            userLoginTask = null;
            showProgress(false);
        }
    }*/
}

