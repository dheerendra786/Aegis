package aegis.com.aegis.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

/**
 * Created by Rakesh Dhaundiyal on 11/12/2019.
 */
public class DeviceUtils {

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static boolean isAirplaneModeOn(Context context) {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        } else {
            return Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        }
    }

    public static boolean isInternetOn(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
//			Toast.makeText( context, "Mobile Network Connected : " , Toast.LENGTH_SHORT ).show();
            return true;
        } else {
            AlertUtils.showToast(context, "No Internet");

            return false;
        }
    }

    public static boolean isInternetOn1(Context context, View view) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
//			Toast.makeText( context, "Mobile Network Connected : " , Toast.LENGTH_SHORT ).show();
            return true;
        } else {
//            AlertUtils.showSnackBar(view, "No Network Connection");
            Toast.makeText( context, "No Network Connection" , Toast.LENGTH_SHORT ).show();
            return false;
        }
    }

    public static boolean hasNetworkConnection(Context context) {
        //Checks network connection without showing any error message
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                System.out.println("internet available");
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static void hideSoftKeyboard(Activity context) {

        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = context.getCurrentFocus();
        if (inputManager != null && view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            view.clearFocus();
        }
    }

    public static void showSoftKeyboard(Activity context) {

        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = context.getCurrentFocus();
        if (inputManager != null && view != null) {
            inputManager.showSoftInputFromInputMethod(view.getWindowToken(), InputMethodManager.SHOW_FORCED);
        }
    }

    public static Bitmap StringToBitMap(String image) {
        try {
            byte[] encodeByte = Base64.decode(image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] arr = baos.toByteArray();
        String result = Base64.encodeToString(arr, Base64.DEFAULT);
        return result;
    }

    public static void vibrateDevice(Context context) {

        vibrateDevice(context, 250);
    }

    @SuppressLint("MissingPermission")
    public static void vibrateDevice(Context context, long duration) {

        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(duration);
    }

    public static int getAPIVersion() {
        return Build.VERSION.SDK_INT;
    }

    public static String getAndroidID(Context ctx) {

        String android_id = Secure.getString(ctx.getContentResolver(), Secure.ANDROID_ID);
        return android_id;
    }

    public static String getMacAddress(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        return wInfo.getMacAddress().replace(":","");
    }

    @SuppressLint("MissingPermission")
    public static String getDeviceIMEI(Context context) {
        String deviceUniqueIdentifier = null;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != tm) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                deviceUniqueIdentifier = tm.getDeviceId(0);
            } else {
                deviceUniqueIdentifier = tm.getDeviceId();
            }
        }
        if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
            deviceUniqueIdentifier = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        }
        return deviceUniqueIdentifier;
    }

    public static String getDeviceType() {

        String device_type = "android";
        return device_type;
    }

    public static int getPixel(Context context, int dp) {

        Resources r = context.getResources();
        float value = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return (int) value;
    }

    public static void hideKeypad(EditText editView, Context activity) {
        InputMethodManager im = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(editView.getWindowToken(), 0);
    }

    public static void setupUI(View view, final Activity context) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (context != null) {
                        hideSoftKeyboard(context);
                    }
                    return false;
                }

            });
        }

        // If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView, context);
            }
        }
    }

}
