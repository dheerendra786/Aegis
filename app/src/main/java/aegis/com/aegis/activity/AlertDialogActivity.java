package aegis.com.aegis.activity;


import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import aegis.com.aegis.R;
import aegis.com.aegis.SessionManager;
import aegis.com.aegis.Utils.AegisConfig;
import aegis.com.aegis.Utils.AlertUtils;
import aegis.com.aegis.Utils.Constants;
import aegis.com.aegis.Utils.DeviceUtils;
import aegis.com.aegis.Utils.JsonUtils;
import aegis.com.aegis.apiresponse.UniversalResponse;
import aegis.com.aegis.eventbus.RecordingData;
import aegis.com.aegis.services.UploadForegroundService;
import aegis.com.aegis.widgets.ProgressBarDialog;

//import aegis.com.aegis.TService;

public class AlertDialogActivity extends AppCompatActivity {

    public static final String TAG = AlertDialogActivity.class.getSimpleName();
    private ProgressBarDialog mProgressBarDialog;
    SessionManager session;
    String phNumber, callDate, callDuration, dir;
    Date callDayTime;

    private static final int REQUEST_CODE = 0;
    private DevicePolicyManager mDPM;
    private ComponentName mAdminName;
    private static RecordingData recordingData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(getApplicationContext());

        getCallDetails();

    }

    public void showQuestionnaire(final String checkSumID) {
        final EditText etRemark;
        String strA, strB, strC;
        String remarks = "";
        final RadioGroup rgFlag, rgCall, rgExclude;
        LayoutInflater inflater = getLayoutInflater();
        final View alertLayout = inflater.inflate(R.layout.dialog_questionnaire, null);
        rgFlag = alertLayout.findViewById(R.id.rgFlag);
        rgCall = alertLayout.findViewById(R.id.rgCall);
        rgExclude = alertLayout.findViewById(R.id.rgExclude);
        etRemark = alertLayout.findViewById(R.id.etRmrk);


        rgFlag.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
            }
        });
        rgCall.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
            }
        });
        rgExclude.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Call FeedBack");
        alert.setIcon(R.mipmap.ic_launcher);
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton("Later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                DbHandler dbHandler = new DbHandler(AlertDialogActivity.this);
                dbHandler.insertCallDetails(session.getSessionID(), checkSumID, callDate, callDuration, phNumber, session.getUserName());
                AlertDialogActivity.this.finish();
            }
        });

        alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String flag, call, exclude, remark = etRemark.getText().toString();
                if (rgFlag.getCheckedRadioButtonId() == R.id.rbtnFlagYes)
                    flag = "1";
                else
                    flag = "0";

                if (rgCall.getCheckedRadioButtonId() == R.id.rbtnCallHold)
                    call = "0";
                else if (rgCall.getCheckedRadioButtonId() == R.id.rbtnCallWarm)
                    call = "1";
                else
                    call = "2";

                if (rgExclude.getCheckedRadioButtonId() == R.id.rbtnExcludeYes)
                    exclude = phNumber;
                else
                    exclude = "N";

                updateQuestionnaire(session.getSessionID(), callDate, checkSumID, flag, remark, call, callDuration, exclude, session.getUserName());
                AlertDialogActivity.this.finish();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private void getCallDetails() {

        StringBuffer sb = new StringBuffer();
        Cursor managedCursor = managedQuery(Uri.parse("content://call_log/calls"), null, null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

        managedCursor.moveToLast();
        phNumber = managedCursor.getString(number);
        String callType = managedCursor.getString(type);
        callDate = managedCursor.getString(date);
        callDayTime = new Date(Long.valueOf(callDate));
        callDuration = managedCursor.getString(duration);
        int dircode = Integer.parseInt(callType);

        switch (dircode) {
            case CallLog.Calls.OUTGOING_TYPE:
                dir = "O";
                break;

            case CallLog.Calls.INCOMING_TYPE:
                dir = "I";
                break;

            case CallLog.Calls.MISSED_TYPE:
                dir = "M";
                break;
        }
        managedCursor.close();

    }

    public void updateCallStatus(String sessionID, String location, String mode, String date, String custNumber, String dept) {
        {
            final String keyword = "CL/";
            TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String mPhoneNumber = "";
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                SubscriptionManager subscriptionManager = SubscriptionManager.from(getApplicationContext());
                List<SubscriptionInfo> subsInfoList = subscriptionManager.getActiveSubscriptionInfoList();

                Log.d("Test", "Current list = " + subsInfoList);

                for (SubscriptionInfo subscriptionInfo : subsInfoList) {

                    mPhoneNumber = subscriptionInfo.getNumber();

                    Log.d("Test", " Number is  " + mPhoneNumber);
                }
            }

            mProgressBarDialog = new ProgressBarDialog(AlertDialogActivity.this);
            mProgressBarDialog.show();

            JSONObject obj = new JSONObject();
            try {

                obj.put("SESSION_ID", sessionID);// user id
                obj.put("LOCATION", location);
                obj.put("MODE", mode);
                obj.put("DATE", date);// user id
                obj.put("CUST_NUM", custNumber);// user id
                obj.put("DEPT", dept);// user id
                if (mPhoneNumber == null || mPhoneNumber.length() == 0)
                    obj.put("NUMBR", "0");
                else
                    obj.put("NUMBR", mPhoneNumber);


            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, AegisConfig.WebConstants.HOST_API + "" + keyword, obj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println("Verify Response" + response.toString());
                            mProgressBarDialog.dismiss();

                            if (DeviceUtils.isInternetOn(AlertDialogActivity.this)) {
                                UniversalResponse universalResponse = JsonUtils.fromJson(response.toString(), UniversalResponse.class);

                                // startRecording the constructor of CustomAdapter to send the reference and data to Adapter
                                if (universalResponse.getStatusCode().equalsIgnoreCase("200")) {
                                    showQuestionnaire(universalResponse.getResult());
                                    startUploadService(universalResponse.getResult());
                                } else {
                                    Toast.makeText(AlertDialogActivity.this, universalResponse.getStatus(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mProgressBarDialog.dismiss();

                            if (DeviceUtils.isInternetOn(AlertDialogActivity.this)) {
                                NetworkResponse response = error.networkResponse;
                                if (error instanceof ServerError && response != null) {
                                    try {
                                        String res = new String(response.data,
                                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                        // Now you can use any deserializer to make sense of data
                                        JSONObject obj = new JSONObject(res);
                                        UniversalResponse universalResponse = JsonUtils.fromJson(res.toString(), UniversalResponse.class);
                                        // startRecording the constructor of CustomAdapter to send the reference and data to Adapter
                                        if (!universalResponse.getStatus().equalsIgnoreCase("201")) {
                                            Toast.makeText(AlertDialogActivity.this, universalResponse.getStatus(), Toast.LENGTH_LONG).show();
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
                                AlertUtils.showToast(AlertDialogActivity.this, AegisConfig.WebConstants.NETWORK_MESSAGE);
                                finish();
                            }

                        }
                    }) {
            };


            RequestQueue requestQueue = Volley.newRequestQueue(AlertDialogActivity.this);
            jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsObjRequest);

        }
    }

    public void updateQuestionnaire(String sessionID, String date, String checksumID, String flag, String remark, String callStatus,
                                    String duration, String exclude, String userName) {
        {
            final String keyword = "CU/";

            mProgressBarDialog = new ProgressBarDialog(AlertDialogActivity.this);
            mProgressBarDialog.show();

            JSONObject obj = new JSONObject();
            try {

                obj.put("SESSION_ID", sessionID);// user id
                obj.put("DATE", date);// user id
                obj.put("CHECKSUM", checksumID);// user id
                obj.put("FLAG", flag);// user id
                obj.put("NOTE", remark);// user id
                obj.put("STATUS", callStatus);
                obj.put("DURATION", duration);
                obj.put("EXCLUDE", exclude);
                obj.put("USER_NAME", userName);


            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, AegisConfig.WebConstants.HOST_API + "" + keyword, obj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println("Verify Response" + response.toString());
                            mProgressBarDialog.dismiss();

                            if (DeviceUtils.isInternetOn(AlertDialogActivity.this)) {
                                UniversalResponse universalResponse = JsonUtils.fromJson(response.toString(), UniversalResponse.class);

                                // startRecording the constructor of CustomAdapter to send the reference and data to Adapter
                                if (universalResponse.getStatusCode().equalsIgnoreCase("201")) {
                                    AlertDialogActivity.this.finish();
                                } else {
                                    Toast.makeText(AlertDialogActivity.this, universalResponse.getStatus(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }


                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mProgressBarDialog.dismiss();

                            if (DeviceUtils.isInternetOn(AlertDialogActivity.this)) {
                                NetworkResponse response = error.networkResponse;
                                if (error instanceof ServerError && response != null) {
                                    try {
                                        String res = new String(response.data,
                                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                        // Now you can use any deserializer to make sense of data
                                        JSONObject obj = new JSONObject(res);
                                        UniversalResponse universalResponse = JsonUtils.fromJson(res.toString(), UniversalResponse.class);
                                        // startRecording the constructor of CustomAdapter to send the reference and data to Adapter
                                        if (!universalResponse.getStatus().equalsIgnoreCase("200")) {
                                            Toast.makeText(AlertDialogActivity.this, universalResponse.getStatus(), Toast.LENGTH_LONG).show();
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
                                AlertUtils.showToast(AlertDialogActivity.this, AegisConfig.WebConstants.NETWORK_MESSAGE);
                                AlertDialogActivity.this.finish();
                            }

                        }
                    }) {
            };


            RequestQueue requestQueue = Volley.newRequestQueue(AlertDialogActivity.this);
            jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsObjRequest);

        }
    }



    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(RecordingData recordingData) {
        AlertDialogActivity.recordingData = recordingData;
        updateCallStatus(session.getSessionID(), session.getKeyAddress(), dir, callDate, phNumber, "");
    }

    public void startUploadService(String checkSum) {
        Intent startIntent = new Intent(this, UploadForegroundService.class);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String firstPartyPhoneNumber = preferences.getString(Constants.PHONE_NUMBER, "NotAvailable");

        startIntent.putExtra(UploadForegroundService.FILE_NAME,
                firstPartyPhoneNumber + "_" +
                        recordingData.phone + "_" +
                        recordingData.contactName
                        + "_" + System.currentTimeMillis() + ".ogg");

        startIntent.putExtra(UploadForegroundService.CHECK_SUM, checkSum);
        startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);

        if (recordingData.targetUri.getPath() == null) {
            Toast.makeText(this, "Failed to create File", Toast.LENGTH_SHORT).show();
            return;
        }
        File fileToUpload = new File(recordingData.targetUri.getPath());
        byte[] bytesArray = new byte[(int) fileToUpload.length()];
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(fileToUpload);
            fis.read(bytesArray);
            startIntent.putExtra(UploadForegroundService.FILE, bytesArray);
            fis.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        startService(startIntent);
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);


    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


}
