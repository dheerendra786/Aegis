package aegis.com.aegis.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import aegis.com.aegis.LocationMonitoringService;
import aegis.com.aegis.R;
import aegis.com.aegis.SessionManager;
import aegis.com.aegis.Utils.Constants;
import aegis.com.aegis.app.CallApplication;
import aegis.com.aegis.app.Storage;
import aegis.com.aegis.services.RecordingService;

import static aegis.com.aegis.services.RecordingService.PERMISSIONS;
import static aegis.com.aegis.services.RecordingService.RESULT_CALL;


public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    FloatingActionButton btnDialler;
    CoordinatorLayout mLayout;
    View diallerLayout;

    private boolean mAlreadyStartedService = false;
    Double latitude, longitude;
    Geocoder geocoder;
    String cityName, stateName, countryName;

    EditText edtPhoneNo;
    TextView lblinfo;

    IntentFilter intentfilter;
    int deviceStatus;
    String currentBatteryStatus = "Battery Info";
    int batteryLevel;
    SessionManager session;

    private EditText phoneEt;
    SharedPreferences.Editor mEditor;
    SharedPreferences preferences;
    Long backPressed = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        session = new SessionManager(this);
        intentfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        MainActivity.this.registerReceiver(broadcastreceiver, intentfilter);

        mLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        btnDialler = (FloatingActionButton) findViewById(R.id.btndialler);
        btnDialler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialler();
            }
        });
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        startLocationService();
        preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        mEditor = preferences.edit();
        startRecordingIfDisabled();
    }

    private void startRecordingIfDisabled() {
        if (!Storage.permitted(MainActivity.this, PERMISSIONS, RESULT_CALL)) {
            Toast.makeText(MainActivity.this, "Permission not granted", Toast.LENGTH_SHORT).show();
        }
        String phone = preferences.getString(Constants.PHONE_NUMBER, "");

        if (phone.isEmpty()) {
            openPhoneDialog();
        } else {
            startRecording();
        }
    }



    private void openPhoneDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_phone_number);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.findViewById(R.id.submitBt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = phoneEt.getText().toString();
                if (!phone.isEmpty() && phone.length() == 10) {
                    mEditor.putString(Constants.PHONE_NUMBER, phoneEt.getText().toString());
                    mEditor.apply();
                    startRecording();
                    dialog.dismiss();
                } else {
                    Toast.makeText(MainActivity.this, "Please Enter valid Phone Number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        phoneEt = dialog.findViewById(R.id.phoneEt);
        dialog.show();
    }

    void startRecording() {
        final SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor edit = shared.edit();
        edit.putBoolean(CallApplication.PREFERENCE_CALL, true);
        edit.apply();
        RecordingService.startService(MainActivity.this);
        Toast.makeText(MainActivity.this, R.string.recording_enabled, Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            session.logoutUser();
            finish();
            return true;
        }
        return false;
    }

    /**
     * Step 3: Start the Location Monitor Service
     */
    private void startLocationService() {
        //And it will be keep running until you close the entire application from task manager.
        //This method will executed only once.
//        if (!mAlreadyStartedService) {
        //Start location sharing service to app server.........
        Intent intent = new Intent(this, LocationMonitoringService.class);
        startService(intent);
//            mAlreadyStartedService = true;
        //Ends................................................
//        }
    }

    @Override
    public void onDestroy() {
        //Stop location sharing service to app server.........
//        stopService(new Intent(this, LocationMonitoringService.class));
        mAlreadyStartedService = false;
        //Ends................................................
        super.onDestroy();
    }

//    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            latitude = Double.valueOf(intent.getStringExtra("extra_latitude"));
//            longitude = Double.valueOf(intent.getStringExtra("extra_longitude"));
//
////            List<Address> addresses = null;
////
////            try {
////                addresses = geocoder.getFromLocation(latitude, longitude, 1);
////                cityName = addresses.get(0).getAddressLine(0);
////                stateName = addresses.get(0).getAddressLine(1);
////                countryName = addresses.get(0).getAddressLine(2);
////            } catch (IOException e1) {
////                e1.printStackTrace();
////            }
//            Toast.makeText(getApplicationContext(), String.valueOf(latitude)+", "+String.valueOf(longitude)+", "+", "+cityName+", "+stateName+", "+countryName,Toast.LENGTH_LONG).show();
//
//        }
//    };

    @Override
    protected void onResume() {
        super.onResume();
//        registerReceiver(broadcastReceiver, new IntentFilter(LocationMonitoringService.ACTION_LOCATION_BROADCAST));
    }

    @Override
    protected void onPause() {
        super.onPause();
//        unregisterReceiver(broadcastReceiver);
    }

    private BroadcastReceiver broadcastreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            deviceStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int batteryLevel = (int) (((float) level / (float) scale) * 100.0f);

            if (deviceStatus == BatteryManager.BATTERY_STATUS_CHARGING)
                session.setBattery(String.valueOf(batteryLevel));

            if (deviceStatus == BatteryManager.BATTERY_STATUS_DISCHARGING)
                session.setBattery(String.valueOf(batteryLevel));

            if (deviceStatus == BatteryManager.BATTERY_STATUS_FULL)
                session.setBattery(String.valueOf(batteryLevel));

            if (deviceStatus == BatteryManager.BATTERY_STATUS_UNKNOWN)
                session.setBattery(String.valueOf(batteryLevel));

            if (deviceStatus == BatteryManager.BATTERY_STATUS_NOT_CHARGING)
                session.setBattery(String.valueOf(batteryLevel));
        }
    };

    public void showDialler() {
        LayoutInflater inflater = getLayoutInflater();
        diallerLayout = inflater.inflate(R.layout.dialler, null);
        edtPhoneNo = (EditText) diallerLayout.findViewById(R.id.edtPhoneNumber);
        lblinfo = (TextView) diallerLayout.findViewById(R.id.lblinfo);
        mLayout.addView(diallerLayout);
        btnDialler.setVisibility(View.GONE);
    }

    public void buttonClickEvent(View v) {
        String phoneNo = edtPhoneNo.getText().toString();
        try {

            switch (v.getId()) {
                case R.id.btnAterisk:
                    lblinfo.setText("");
                    phoneNo += "*";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnHash:
                    lblinfo.setText("");
                    phoneNo += "#";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnZero:
                    lblinfo.setText("");
                    phoneNo += "0";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnOne:
                    lblinfo.setText("");
                    phoneNo += "1";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnTwo:
                    lblinfo.setText("");
                    phoneNo += "2";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnThree:
                    lblinfo.setText("");
                    phoneNo += "3";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnFour:
                    lblinfo.setText("");
                    phoneNo += "4";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnFive:
                    lblinfo.setText("");
                    phoneNo += "5";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnSix:
                    lblinfo.setText("");
                    phoneNo += "6";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnSeven:
                    lblinfo.setText("");
                    phoneNo += "7";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnEight:
                    lblinfo.setText("");
                    phoneNo += "8";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnNine:
                    lblinfo.setText("");
                    phoneNo += "9";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btndel:
                    lblinfo.setText("");
                    if (phoneNo != null && phoneNo.length() > 0) {
                        phoneNo = phoneNo.substring(0, phoneNo.length() - 1);
                    }

                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnClearAll:
                    lblinfo.setText("");
                    edtPhoneNo.setText("");
                    break;
                case R.id.btnClose:
                    lblinfo.setText("");
                    edtPhoneNo.setText("");
                    mLayout.removeView(diallerLayout);
                    btnDialler.setVisibility(View.VISIBLE);
                    break;
                case R.id.btnCall:
                    if (phoneNo.trim().equals("")) {
                        lblinfo.setText("Please enter a number to startRecording on!");
                    } else {
                        Boolean isHash = false;
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        if (phoneNo.subSequence(phoneNo.length() - 1, phoneNo.length()).equals("#")) {
                            phoneNo = phoneNo.substring(0, phoneNo.length() - 1);
                            String callInfo = "tel:" + phoneNo + Uri.encode("#");
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse(callInfo));

                            startActivity(callIntent);
                        } else {
                            String callInfo = "tel:" + phoneNo;
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse(callInfo));
                            startActivity(callIntent);
                        }
                    }
                    break;
            }

        } catch (Exception ex) {

        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (backPressed + 2000 > System.currentTimeMillis()) {
            finish();
        } else {
            Toast.makeText(this, getString(R.string.press_back_twice), Toast.LENGTH_SHORT).show();
        }
        backPressed = System.currentTimeMillis();
    }
}



