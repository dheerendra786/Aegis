package aegis.com.aegis.activity;


import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
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
import java.util.ArrayList;
import java.util.HashMap;

import aegis.com.aegis.R;
import aegis.com.aegis.Utils.AegisConfig;
import aegis.com.aegis.Utils.AlertUtils;
import aegis.com.aegis.Utils.DeviceUtils;
import aegis.com.aegis.Utils.JsonUtils;
import aegis.com.aegis.apiresponse.UniversalResponse;
import aegis.com.aegis.widgets.ProgressBarDialog;


public class FragmentB extends Fragment {

    ListView feedBackView;
    ArrayList<HashMap<String, String>> callList;
    private ProgressBarDialog mProgressBarDialog;
    SwipeRefreshLayout mSwipeRefreshLayout;
    DbHandler db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment, container, false);
        feedBackView = (ListView) rootView.findViewById(R.id.feedbackListView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeToRefreshNotifications);
        db = new DbHandler(getActivity());
        callList = db.GetUsers();
        ListAdapter adapter = new SimpleAdapter(getActivity(), callList, R.layout.feddback_list,new String[]{"phoneNumber","callDate","callDuration"}, new int[]{R.id.number, R.id.date, R.id.duration});
        feedBackView.setAdapter(adapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshList();
                    }
                }, 1500);
            }
        });


        feedBackView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> mCall = callList.get(position);
                Toast.makeText(getActivity(), mCall.get("sessionID")+", "+mCall.get("checkSum")+", "+mCall.get("callDate")+", "+mCall.get("callDuration")+", "+mCall.get("phoneNumber"), Toast.LENGTH_SHORT).show();
                showQuestionnaire(mCall.get("sessionID"), mCall.get("checkSum"), mCall.get("callDate"),mCall.get("callDuration"), mCall.get("phoneNumber"), mCall.get("userName"));
            }
        });

        return rootView;
    }

    public void refreshList() {
        db = new DbHandler(getActivity());
        callList = db.GetUsers();
        ListAdapter adapter = new SimpleAdapter(getActivity(), callList, R.layout.feddback_list,new String[]{"phoneNumber","callDate","callDuration"}, new int[]{R.id.number, R.id.date, R.id.duration});
        feedBackView.setAdapter(adapter);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void showQuestionnaire(final String sessionID, final String checkSumID, final String callDate, final String callDuration, final String phoneNumber, final String userName) {
        final EditText etRemark;
        String strA, strB, strC;
        String remarks="";
        final RadioGroup rgFlag,rgCall,rgExclude;
        LayoutInflater inflater = getActivity().getLayoutInflater();
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

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Call FeedBack");
        alert.setIcon(R.mipmap.ic_launcher);
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String flag, call, exclude, remark = etRemark.getText().toString();
                if(rgFlag.getCheckedRadioButtonId() == R.id.rbtnFlagYes)
                    flag = "1";
                else
                    flag = "0";

                if(rgCall.getCheckedRadioButtonId() == R.id.rbtnCallHold)
                    call = "0";
                else if(rgCall.getCheckedRadioButtonId() == R.id.rbtnCallWarm)
                    call = "1";
                else
                    call = "2";

                if(rgExclude.getCheckedRadioButtonId() == R.id.rbtnExcludeYes)
                    exclude = phoneNumber;
                else
                    exclude = "N";

                updateQuestionnaire(sessionID,callDate,checkSumID,flag,remark,call,callDuration,exclude, userName);

            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    public void updateQuestionnaire(String sessionID, String date, final String checksumID, String flag, String remark, String callStatus,
                                    String duration, String exclude, String userName) {
        {
            final String keyword = "CU/";

            mProgressBarDialog = new ProgressBarDialog(getActivity());
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
            System.out.println(sessionID+", "+date+", "+checksumID+", "+flag+", "+remark+", "+callStatus+", "+duration+", "+exclude+", "+userName);
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, AegisConfig.WebConstants.HOST_API + "" + keyword, obj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println("Verify Response" + response.toString());
                            mProgressBarDialog.dismiss();

                            if (DeviceUtils.isInternetOn(getActivity())) {
                                UniversalResponse universalResponse = JsonUtils.fromJson(response.toString(), UniversalResponse.class);

                                // startRecording the constructor of CustomAdapter to send the reference and data to Adapter
                                if(universalResponse.getStatusCode().equalsIgnoreCase("201")){
                                    db.DeleteUser(checksumID);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            refreshList();
                                        }
                                    }, 1500);
                                }
                                else {
                                    Toast.makeText(getActivity(),universalResponse.getStatus(),Toast.LENGTH_LONG).show();
                                }
                            }
                        }


                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mProgressBarDialog.dismiss();


                            if (DeviceUtils.isInternetOn(getActivity())) {
                                NetworkResponse response = error.networkResponse;
                                if (error instanceof ServerError && response != null) {
                                    try {
                                        String res = new String(response.data,
                                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                        // Now you can use any deserializer to make sense of data
                                        JSONObject obj = new JSONObject(res);
                                        UniversalResponse universalResponse = JsonUtils.fromJson(res.toString(), UniversalResponse.class);
                                        // startRecording the constructor of CustomAdapter to send the reference and data to Adapter
                                        if(!universalResponse.getStatus().equalsIgnoreCase("200")){
                                            Toast.makeText(getActivity(),universalResponse.getStatus(),Toast.LENGTH_LONG).show();
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
                                AlertUtils.showToast(getActivity(), AegisConfig.WebConstants.NETWORK_MESSAGE);
                            }

                        }
                    }) {
            };


            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsObjRequest);

        }
    }
}