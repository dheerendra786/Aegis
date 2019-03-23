package aegis.com.aegis.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import aegis.com.aegis.R;
import aegis.com.aegis.SessionManager;
import aegis.com.aegis.Utils.AegisConfig;
import aegis.com.aegis.Utils.AlertUtils;
import aegis.com.aegis.Utils.Constants;
import aegis.com.aegis.Utils.DeviceUtils;
import aegis.com.aegis.Utils.JsonUtils;
import aegis.com.aegis.apiresponse.LoginResponse;
import aegis.com.aegis.apiresponse.UniversalResponse;
import aegis.com.aegis.widgets.ProgressBarDialog;

/**
 * Created by RakeshD on 1/3/2018.
 */

public class LoginActivity extends AppCompatActivity {


    private String mEmail, mPassword, mUserType;
    private EditText mEditEmail, mEdtPassword;
    private Button mBtnLogin;
    private TextView mTxtTOU, mTxtForgotPassword;
    private RelativeLayout mRlLogin;
    private ProgressBarDialog mProgressBarDialog;
    SessionManager session;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new SessionManager(getApplicationContext());
        mEditEmail = findViewById(R.id.login_EdtEmail);
        mEdtPassword = findViewById(R.id.login_EdtPassword);
        mBtnLogin = findViewById(R.id.login_BtnLogin);
        mTxtTOU = findViewById(R.id.login_txtTOU);
        mTxtForgotPassword = findViewById(R.id.login_TxtForgotPassword);
        mRlLogin = findViewById(R.id.login_rlLayout);

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEmail = mEditEmail.getText().toString();
                mPassword = mEdtPassword.getText().toString();
                if (!validateEmail()) {
                    return;
                }
                if (!validatePassword()) {
                    return;
                }
                if (DeviceUtils.isInternetOn1(LoginActivity.this, mRlLogin)) {

                    mEmail = mEditEmail.getText().toString();
                    mPassword = mEdtPassword.getText().toString();
                    doLogin();
                }


            }
        });
        mTxtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ForgotPasswordDialog forgotPasswordDialog = new ForgotPasswordDialog(LoginActivity.this, mRlLogin);
                forgotPasswordDialog.show();
            }
        });


    }

    private boolean validateEmail() {
        String email = mEditEmail.getText().toString().trim();

        if (email.isEmpty()) {
            AlertUtils.showSnackBar(mRlLogin, "Please Enter Valid Email Address ");
            requestFocus(mEditEmail);
            return false;
        } else {

        }
        return true;
    }

    private boolean validatePassword() {
        String password = mEdtPassword.getText().toString().trim();

        if (password.isEmpty() || !isValidPassword(password)) {
            AlertUtils.showSnackBar(mRlLogin, "Please Enter Valid Password");
            requestFocus(mEdtPassword);
            return false;
        } else {

        }
        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private static boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && password.length() > 1;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void doLogin() {
        {
            final String keyword = "LG/";

            mProgressBarDialog = new ProgressBarDialog(LoginActivity.this);
            mProgressBarDialog.show();

            JSONObject obj = new JSONObject();
            try {

                obj.put("USER_NAME", mEmail);// user id
                obj.put("PASSWORD", mPassword);// user id
                SharedPreferences preferences = PreferenceManager
                        .getDefaultSharedPreferences(LoginActivity.this);
                String advertisingId = preferences.getString(Constants.ADVERTISING_ID, "");

                if (advertisingId.isEmpty()) {
                    Toast.makeText(this, "Sorry unable to get your unique Id", Toast.LENGTH_SHORT).show();
                    return;
                }

                obj.put("IMEI", DeviceUtils.getDeviceIMEI(LoginActivity.this));// user id
                obj.put("MAC", DeviceUtils.getMacAddress(LoginActivity.this));// user id
                obj.put("DEVICE_ID", Settings.Secure.ANDROID_ID);// user id
                /// Added a bypass for my device
                if("70d233e7fc1d16a4".equalsIgnoreCase(DeviceUtils.getDeviceIMEI(LoginActivity.this))) {
                    obj.put("USER_NAME", "Rohan Kandwal");// user id
                }
                Log.d("Login", obj.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, AegisConfig.WebConstants.HOST_API + "" + keyword, obj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println("Verify Response" + response.toString());
                            mProgressBarDialog.dismiss();

                            if (DeviceUtils.isInternetOn(LoginActivity.this)) {
                                LoginResponse loginResponse = JsonUtils.fromJson(response.toString(), LoginResponse.class);

                                // startRecording the constructor of CustomAdapter to send the reference and data to Adapter
                                if (loginResponse.getStatusCode().equalsIgnoreCase("200")) {
                                    session.createLoginSession(loginResponse.getResult().get(0), mEmail);
                                    AlertUtils.showSnackBar(mRlLogin, "User Loggedin Successfully!");
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    AlertUtils.showSnackBar(mRlLogin, "Error from server status : " + loginResponse.getStatus());
                                }
                            }
                        }


                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mProgressBarDialog.dismiss();


                            if (DeviceUtils.isInternetOn(LoginActivity.this)) {
                                NetworkResponse response = error.networkResponse;
                                if (error instanceof ServerError && response != null) {
                                    try {
                                        String res = new String(response.data,
                                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                        // Now you can use any deserializer to make sense of data
                                        JSONObject obj = new JSONObject(res);
                                        UniversalResponse universalResponse = JsonUtils.fromJson(res.toString(), UniversalResponse.class);
                                        // startRecording the constructor of CustomAdapter to send the reference and data to Adapter
                                        if (universalResponse.getStatus().equalsIgnoreCase("201")) {
                                            AlertUtils.showSnackBar(mRlLogin, "User Already Exist.");
                                        }

                                    } catch (UnsupportedEncodingException e1) {
                                        // Couldn't properly decode data to string
                                        e1.printStackTrace();
                                    } catch (JSONException e2) {
                                        // returned data is not JSONObject?
                                        e2.printStackTrace();
                                    }
                                }

                            } else {
                                AlertUtils.showToast(LoginActivity.this, AegisConfig.WebConstants.NETWORK_MESSAGE);
                            }

                        }


                    }) {
            };


            RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
            jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsObjRequest);

        }
    }

}
