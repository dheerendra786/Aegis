package aegis.com.aegis.services;

import aegis.com.aegis.activity.LoginActivity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import aegis.com.aegis.R;
import aegis.com.aegis.Utils.AegisConfig;
import aegis.com.aegis.Utils.AlertUtils;
import aegis.com.aegis.Utils.Constants;
import aegis.com.aegis.Utils.DeviceUtils;
import aegis.com.aegis.activity.MainActivity;

public class UploadForegroundService extends Service {
    public static final String FILE_NAME = "FileName";
    public static final String FILE_PATH = "FilePath";
    public static final String FILE = "File";
    private static final String LOG_TAG = "UploadForegroundService";
    public static final String TAG = UploadForegroundService.class.getSimpleName();
    public static String CHECK_SUM = "CHECKSUM";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getAction()) {
            case Constants.ACTION.STARTFOREGROUND_ACTION:
                Log.i(LOG_TAG, "Received Start Foreground Intent ");
                Intent notificationIntent = new Intent(this, MainActivity.class);
                notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                        notificationIntent, 0);

                Bitmap icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.ic_call_black_24dp);

                Notification notification = new NotificationCompat.Builder(this, getString(R.string.app_name))
                        .setContentTitle("Audio Uploading")
                        .setContentText("Uploading...")
                        .setSmallIcon(R.drawable.ic_call_black_24dp)
                        .setContentIntent(pendingIntent)
                        .setOngoing(true)
                        .build();


                startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                        notification);

                uploadRecordedFile(intent);

                break;
            case Constants.ACTION.PREV_ACTION:
                Log.i(LOG_TAG, "Clicked Previous");
                break;
            case Constants.ACTION.PLAY_ACTION:
                Log.i(LOG_TAG, "Clicked Play");
                break;
            case Constants.ACTION.NEXT_ACTION:
                Log.i(LOG_TAG, "Clicked Next");
                break;
            case Constants.ACTION.STOPFOREGROUND_ACTION:
                Log.i(LOG_TAG, "Received Stop Foreground Intent");
//            stopForeground(true);
                stopSelf();
                break;
        }
        return START_STICKY;
    }

    private void uploadRecordedFile(Intent intent) {

        final String keyword = "FU/";
        /// Added a bypass for my device
        if("70d233e7fc1d16a4".equalsIgnoreCase(DeviceUtils.getDeviceIMEI(UploadForegroundService.this))) {
            return;
        }
        final byte[] bytes = intent.getExtras().getByteArray(UploadForegroundService.FILE);
        HashMap<String, Object> map = new HashMap<>();
        map.put("FNAME", intent.getExtras().getString(UploadForegroundService.FILE_NAME));
        map.put("CHECKSUM", intent.getExtras().getString(UploadForegroundService.CHECK_SUM));
        map.put("FBYTES", Base64.encodeToString(bytes,Base64.DEFAULT));

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, AegisConfig.WebConstants.HOST_API + "" + keyword,  new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("Verify Response Call" + response.toString());
                        stopForegroundService(getBaseContext());
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (DeviceUtils.isInternetOn(getBaseContext())) {
                            NetworkResponse response = error.networkResponse;
                            if (error instanceof ServerError && response != null) {
                                try {
                                    String res = new String(response.data,
                                            HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                    // Now you can use any deserializer to make sense of data
                                    JSONObject obj = new JSONObject(res);

                                    stopForegroundService(getBaseContext());


                                } catch (UnsupportedEncodingException e1) {
                                    // Couldn't properly decode data to string
                                    e1.printStackTrace();
                                } catch (JSONException e2) {
                                    // returned data is not JSONObject?
                                    e2.printStackTrace();
                                }
                            }

                        } else {
                            AlertUtils.showToast(getBaseContext(), AegisConfig.WebConstants.NETWORK_MESSAGE);
                        }
                    }
                }) {
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsObjRequest);

    }

    private void stopForegroundService(Context baseContext) {
        Log.d(TAG, "stopForeGroundService: ");
        Intent startIntent = new Intent(baseContext, UploadForegroundService.class);
        startIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
        startService(startIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "In onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case of bound services.
        return null;
    }

    public static String calculateMD5(File updateFile) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Exception while getting digest", e);
            return null;
        }

        InputStream is;
        try {
            is = new FileInputStream(updateFile);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Exception while getting FileInputStream", e);
            return null;
        }

        byte[] buffer = new byte[8192];
        int read;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0');
            return output;
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for MD5", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e(TAG, "Exception on closing MD5 input stream", e);
            }
        }
    }
}