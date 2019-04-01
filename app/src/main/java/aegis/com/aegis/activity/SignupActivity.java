package aegis.com.aegis.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
import aegis.com.aegis.Utils.AegisConfig;
import aegis.com.aegis.Utils.AlertUtils;
import aegis.com.aegis.Utils.Constants;
import aegis.com.aegis.Utils.DeviceUtils;
import aegis.com.aegis.Utils.JsonUtils;
import aegis.com.aegis.apiresponse.UniversalResponse;
import aegis.com.aegis.widgets.ProgressBarDialog;

/**
 * Created by RakeshD on 1/3/2018.
 */

public class SignupActivity extends AppCompatActivity {


    private String mName, mEmail, mPassword, mPhone, mState, mCity;
    private TextInputEditText mEdtName, mEditEmail, mEdtPassword, mEdtPhone, mEdtConfirmPassword, mEdtState, mEdtCity;
    private Button mBtnLogin;
    String fbTouAndPrivacyPolicyString;
    private ProgressBarDialog mProgressBarDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mEdtName = findViewById(R.id.signup_EdtName);
        mEditEmail = findViewById(R.id.signup_EdtEmail);
        mEdtPassword = findViewById(R.id.signup_EdtPassword);
        mEdtConfirmPassword = findViewById(R.id.signup_EdtConfirmPassword);
        mEdtPhone = findViewById(R.id.signup_EdtPhone);
        mEdtState = findViewById(R.id.signup_EdtState);
        mEdtCity = findViewById(R.id.signup_EdtCity);
        mBtnLogin = findViewById(R.id.signup_BtnSignup);

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validateName()) {
                    return;
                }
                if (!validateEmail()) {
                    return;
                }
                if (!validatePassword()) {
                    return;
                }
                if (!validatePhone()) {
                    return;
                }
                if (!validateState()) {
                    return;
                }
                if (!validateCity()) {
                    return;
                }

                if (DeviceUtils.isInternetOn1(SignupActivity.this, null)) {
                    mName = mEdtName.getText().toString();
                    mEmail = mEditEmail.getText().toString();
                    mPassword = mEdtPassword.getText().toString();
                    mPhone = mEdtPhone.getText().toString();
                    mState = mEdtState.getText().toString();
                    mCity = mEdtCity.getText().toString();
                    System.out.println(mPassword + ">>>" + mEmail + ">>" + mPhone);
                    doSignUp();
                }
            }
        });
    }

    private boolean validateEmail() {
        String email = mEditEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            AlertUtils.showToast(this, "Please Enter Valid Email Address ");
            requestFocus(mEditEmail);
            return false;
        } else {

        }
        return true;
    }

    private boolean validatePassword() {
        String password = mEdtPassword.getText().toString().trim();

        if (password.isEmpty() || !isValidPassword(password)) {
            AlertUtils.showToast(this, "Please Enter Valid Password");
            requestFocus(mEdtPassword);
            return false;
        } else {

        }
        return true;
    }

    private boolean validatePhone() {
        String phone = mEdtPhone.getText().toString().trim();

        if (phone.isEmpty() || !isValidPhone(phone)) {
            AlertUtils.showToast(this, "Please Enter Valid Phone Number");
            requestFocus(mEdtPhone);
            return false;
        } else {

        }
        return true;
    }

    private boolean validateState() {
        String state = mEdtState.getText().toString().trim();

        if (state.isEmpty()) {
            AlertUtils.showToast(this, "Please Enter State");
            requestFocus(mEdtState);
            return false;
        } else {

        }
        return true;
    }

    private boolean validateCity() {
        String city = mEdtCity.getText().toString().trim();

        if (city.isEmpty()) {
            AlertUtils.showToast(this, "Please Enter City");
            requestFocus(mEdtCity);
            return false;
        } else {

        }
        return true;
    }

    private boolean validateName() {
        String name = mEdtName.getText().toString().trim();

        if (name.isEmpty() || !isValidName(name)) {
            AlertUtils.showToast(this, "Please Enter Valid Name");
            requestFocus(mEdtName);
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

    private static boolean isValidPhone(String phone) {
        return !TextUtils.isEmpty(phone) && (phone.length() > 1) && (phone.length() <= 10);
    }

    private static boolean isValidName(String name) {
        return !TextUtils.isEmpty(name) && (name.length() > 4);
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void doSignUp() {
        {
            final String keyword = "cr/";

            mProgressBarDialog = new ProgressBarDialog(SignupActivity.this);
            mProgressBarDialog.show();

            JSONObject obj = new JSONObject();
            try {

                obj.put("USER_NAME", mName);// user id
                obj.put("PASSWORD", mPassword);// user id
                obj.put("EMAIL", mEmail);// user id
                obj.put("CONTACT", mPhone);// user id
                obj.put("STATE", mState);// user id
                obj.put("CITY", mCity);// user id
                SharedPreferences preferences = PreferenceManager
                        .getDefaultSharedPreferences(SignupActivity.this);
                String advertisingId = preferences.getString(Constants.ADVERTISING_ID, "");

                if (advertisingId.isEmpty()) {
                    Toast.makeText(this, "Sorry unable to get your unique Id", Toast.LENGTH_SHORT).show();
                    return;
                }

                obj.put("IMEI", DeviceUtils.getDeviceIMEI(SignupActivity.this));// user id
                obj.put("MAC", DeviceUtils.getMacAddress(SignupActivity.this));// user id
                obj.put("DEVICE_ID", DeviceUtils.getDeviceIMEI(SignupActivity.this));// user id
//                obj.put("DEVICE_ID", Settings.Secure.ANDROID_ID);// user id
                Log.d("Signup", obj.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
                String url = AegisConfig.WebConstants.HOST_API + "" + keyword;
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println("Verify Response" + response.toString());
                            mProgressBarDialog.dismiss();

                            if (DeviceUtils.isInternetOn(SignupActivity.this)) {
                                UniversalResponse universalResponse = JsonUtils.fromJson(response.toString(), UniversalResponse.class);
                                // startRecording the constructor of CustomAdapter to send the reference and data to Adapter
                                if (universalResponse.getStatus().equalsIgnoreCase("Success")) {
                                    AlertUtils.showToast(SignupActivity.this, "User Created Successfully!");
                                    SignupActivity.this.finish();
                                } else {
                                    AlertUtils.showToast(SignupActivity.this, "Error From Server");
                                }
                            }
                        }


                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mProgressBarDialog.dismiss();


                            if (DeviceUtils.isInternetOn(SignupActivity.this)) {
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
                                            AlertUtils.showToast(SignupActivity.this, "User Already Exist.");
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
                                AlertUtils.showToast(SignupActivity.this, AegisConfig.WebConstants.NETWORK_MESSAGE);
                            }

                        }


                    }) {

               /* @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", "Token " + AppPreference.getInstance().getmAccessToken());
                    headers.put("Content-Type", "application/json");
                    System.out.println(">>>header" + headers);
                    return headers;
                }*/
            };


            RequestQueue requestQueue = Volley.newRequestQueue(SignupActivity.this);
            jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsObjRequest);

        }
    }

}

