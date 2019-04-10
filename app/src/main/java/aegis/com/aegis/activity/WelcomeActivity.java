package aegis.com.aegis.activity;

import aegis.com.aegis.SessionManager;
import aegis.com.aegis.Utils.AegisConfig;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import aegis.com.aegis.R;

public class WelcomeActivity extends AppCompatActivity {
    private Button mBtnLogin, mBtnSignUp;
    public static final int MY_PERMISSIONS_REQUEST_CODE = 97;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mBtnLogin = findViewById(R.id.welcome_btnLogin);
        mBtnSignUp = findViewById(R.id.welcome_btnSignUp);
        findViewById(R.id.ivSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(WelcomeActivity.this);
                dialog.setContentView(R.layout.ip_port_dialog);
                dialog.setTitle(R.string.change_ip_port);
                final EditText etIpAddress = (dialog.findViewById(R.id.etIPAddress));
                final EditText etPort = (dialog.findViewById(R.id.etPort));
                final RadioGroup rgFlag = (RadioGroup) dialog.findViewById(R.id.rgProtocol);
                rgFlag.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                    }
                });
                dialog.findViewById(R.id.btOk).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String ipAddress = etIpAddress.getText().toString().trim();
                        String ipPort = etPort.getText().toString().trim();
                        String ipProtocol="";
                        if (rgFlag.getCheckedRadioButtonId() == R.id.rbtnhttps)
                            ipProtocol = "https";
                        else
                            ipProtocol = "http";
                        //final String portNumber = dialog.findViewById(R.id.etPort).toString().trim();
                        if (TextUtils.isEmpty(ipAddress)) {
                            Toast.makeText(WelcomeActivity.this, R.string.enter_server_ip,
                              Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if (TextUtils.isEmpty(ipPort)) {
                            Toast.makeText(WelcomeActivity.this, R.string.enter_port_number,
                              Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if (TextUtils.isEmpty(ipProtocol)) {
                            Toast.makeText(WelcomeActivity.this, R.string.protocol,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (ipProtocol.equalsIgnoreCase("http")) {
                            ipAddress = "http://" + ipAddress+":"+ipPort;
                        }
                        else if(ipProtocol.equalsIgnoreCase("https")) {
                            ipAddress = "https://" + ipAddress+":"+ipPort;
                        }


//                        if (!ipAddress.startsWith("http://")) {
//                            ipAddress = "http://" + ipAddress;
//                        }
                        if (!ipAddress.endsWith("/")) {
                            ipAddress += "/";
                        }

                        AegisConfig.SERVER_URL = ipAddress+"rec/";
                        new SessionManager(WelcomeActivity.this).saveServerUrl(AegisConfig.SERVER_URL);
                        dialog.cancel();
                    }
                });
                dialog.findViewById(R.id.btCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });
                dialog.show();
                Window window = dialog.getWindow();
                if (window != null)
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        });
        checkPermission();
    }

    protected void checkPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                + ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
                + ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS)
                + ContextCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE)
                + ContextCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE)
                + ContextCompat.checkSelfPermission(this,Manifest.permission.PROCESS_OUTGOING_CALLS)
                + ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO)
                + ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED){

            // Do something, when permissions not granted
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_COARSE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_CONTACTS)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CALL_PHONE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_PHONE_STATE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.PROCESS_OUTGOING_CALLS)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.RECORD_AUDIO)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_CALL_LOG)){
                // If we should give explanation of requested permissions

                // Show an alert dialog here with request explanation
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Contact, Location, Phone Storage and telephone" +
                        " permissions are required to do the task.");
                builder.setTitle("Please Grant Remaining Permissions");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(
                                WelcomeActivity.this,
                                new String[]{
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.READ_CONTACTS,
                                        Manifest.permission.CALL_PHONE,
                                        Manifest.permission.READ_PHONE_STATE,
                                        Manifest.permission.PROCESS_OUTGOING_CALLS,
                                        Manifest.permission.RECORD_AUDIO,
                                        Manifest.permission.READ_CALL_LOG
                                },
                                MY_PERMISSIONS_REQUEST_CODE
                        );
                    }
                });
                builder.setNeutralButton("Cancel",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                // Directly request for required permissions, without explanation
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.READ_CONTACTS,
                                Manifest.permission.CALL_PHONE,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.PROCESS_OUTGOING_CALLS,
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.READ_CALL_LOG
                        },
                        MY_PERMISSIONS_REQUEST_CODE
                );
            }
        }else {
            mBtnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(WelcomeActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
            });
            mBtnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(WelcomeActivity.this,SignupActivity.class);
                    startActivity(intent);
                }
            });
            Toast.makeText(this,"Permissions already granted",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CODE: {
                // When request is cancelled, the results array are empty
                if (
                        (grantResults.length > 0) &&
                                (grantResults[0]
                                        + grantResults[1]
                                        + grantResults[2]
                                        + grantResults[3]
                                        + grantResults[4]
                                        + grantResults[5]
                                        + grantResults[6]
                                        + grantResults[7]
                                        == PackageManager.PERMISSION_GRANTED
                                )
                ) {
                    // Permissions are granted
                    Toast.makeText(this, "Permissions granted.", Toast.LENGTH_SHORT).show();
                    mBtnLogin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (TextUtils.isEmpty(AegisConfig.SERVER_URL)) {
                                Toast.makeText(WelcomeActivity.this,
                                  "Please configure the server url", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Intent intent = new Intent(WelcomeActivity.this,LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    });
                    mBtnSignUp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (TextUtils.isEmpty(AegisConfig.SERVER_URL)) {
                                Toast.makeText(WelcomeActivity.this,
                                  "Please configure the server url", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Intent intent = new Intent(WelcomeActivity.this,SignupActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    });
                } else {
                    // Permissions are denied
                    Toast.makeText(this, "Permissions denied.", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }
}
