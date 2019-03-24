package aegis.com.aegis.activity;


import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.util.Calendar;

import aegis.com.aegis.R;
import aegis.com.aegis.SessionManager;
import aegis.com.aegis.Utils.AegisConfig;
import aegis.com.aegis.Utils.AlertUtils;
import aegis.com.aegis.Utils.DeviceUtils;
import aegis.com.aegis.Utils.JsonUtils;
import aegis.com.aegis.apiresponse.UniversalResponse;
import aegis.com.aegis.services.RecordingService;
import aegis.com.aegis.widgets.ProgressBarDialog;


public class FragmentC extends Fragment {

    SwitchCompat mLocation;//, mLogout;
    SessionManager session;
    TextView txtFrom, txtTo;
    private ProgressBarDialog mProgressBarDialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        session = new SessionManager(getContext());
        View rootView = inflater.inflate(R.layout.settings_layout, container, false);
        txtFrom = (TextView) rootView.findViewById(R.id.txtFromTime);
        txtTo = (TextView) rootView.findViewById(R.id.txtToTime);
        mLocation = (SwitchCompat) rootView.findViewById(R.id.switchLocation);
        //mLogout = (SwitchCompat) rootView.findViewById(R.id.switchLogout);

        mLocation.setChecked(Boolean.valueOf(session.getKeyEnableLocation()));


        if (!TextUtils.isEmpty(session.getFromTime())) {
            txtFrom.setText(String.format(getString(R.string.from_time), session.getFromTime()));
        }

        if (!TextUtils.isEmpty(session.getToTime())) {
            txtTo.setText(String.format(getString(R.string.from_time), session.getToTime()));
        }
        txtFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        final String time = selectedHour + ":" + selectedMinute;
                        txtFrom.setText("Break Time: From (" + time + ")");
                        session.saveFromTime(time);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select BreakTime (FROM)");
                mTimePicker.show();

            }
        });

        txtTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        final String time = selectedHour + ":" + selectedMinute;
                        txtTo.setText("Break Time: To (" + time + ")");
                        session.saveToTime(time);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select BreakTime (TO)");
                mTimePicker.show();

            }
        });

        /*mLogout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.app_name);
                    builder.setIcon(R.mipmap.ic_launcher);
                    builder.setMessage("Do you want to Logout?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    doLogout();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    mLogout.setChecked(false);
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });*/

        mLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //mLocation.setChecked(isChecked);
                session.setEnableLocation(isChecked);
            }
        });

        return rootView;
    }





    public void doLogout() {
        {
            final String keyword = "LGO/";

            mProgressBarDialog = new ProgressBarDialog(getActivity());
            mProgressBarDialog.show();

            JSONObject obj = new JSONObject();
            try {
                obj.put("SESSION_ID", session.getSessionID());// user id
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, AegisConfig.WebConstants.HOST_API + "" + keyword, obj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println("Verify Response" + response.toString());
                            mProgressBarDialog.dismiss();

                            if (DeviceUtils.isInternetOn(getActivity())) {
                                UniversalResponse universalResponse = JsonUtils.fromJson(response.toString(), UniversalResponse.class);

                                // startRecording the constructor of CustomAdapter to send the reference and data to Adapter
                                if (universalResponse.getStatusCode().equalsIgnoreCase("200")) {
                                    RecordingService.stopService(getActivity());
                                    session.logoutUser();
                                } else {
                                    Toast.makeText(getActivity(), universalResponse.getStatus(), Toast.LENGTH_LONG).show();
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
                                        if (!universalResponse.getStatus().equalsIgnoreCase("200")) {
                                            Toast.makeText(getActivity(), universalResponse.getStatus(), Toast.LENGTH_LONG).show();
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