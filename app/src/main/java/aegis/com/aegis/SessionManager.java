package aegis.com.aegis;

import aegis.com.aegis.Utils.Constants;
import aegis.com.aegis.activity.LoginActivity;
import aegis.com.aegis.activity.MainActivity;
import aegis.com.aegis.activity.WelcomeActivity;
import aegis.com.aegis.model.Result;
import aegis.com.aegis.services.RecordingService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {
  SharedPreferences pref;
  Editor editor;
  Context _context;

  int PRIVATE_MODE = 0;

  private static final String PREF_NAME = "AegisPref";
  private static final String IS_LOGIN = "IsLoggedIn";

  public static final String KEY_SESSION_ID = "sessionID";
  public static final String KEY_USER_NAME = "userName";
  public static final String KEY_ENABLE_LOCATION = "enableLocation";
  public static final String KEY_RECORD_CALLS = "recordCalls";
  public static final String KEY_BREAK_TIME = "breakTime";
  public static final String KEY_BATTERY_STATUS = "battery";
  public static final String KEY_ADDRESS = "address";

  // Constructor
  public SessionManager(Context context) {
    this._context = context;
    pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    editor = pref.edit();
  }

  /**
   * Create login session
   */
  public void createLoginSession(Result result, String userName) {
    editor.putBoolean(IS_LOGIN, true);
    editor.putString(KEY_SESSION_ID, result.getSesionID());
    editor.putString(KEY_ENABLE_LOCATION, result.getEnableLocation());
    editor.putString(KEY_RECORD_CALLS, result.getRecordCalls());
    editor.putString(KEY_BREAK_TIME, result.getBreaktime());
    editor.putString(KEY_USER_NAME, userName);
    editor.commit();
  }

  public void setBattery(String battery) {
    editor.putString(KEY_BATTERY_STATUS, battery);
    editor.commit();
  }

  public void setAddress(String address) {
    editor.putString(KEY_ADDRESS, address);
    editor.commit();
  }

  public void setEnableLocation(boolean enableLocation) {
    editor.putString(KEY_ENABLE_LOCATION, String.valueOf(enableLocation)).apply();
  }

  /**
   * Check login method wil check user login status
   * If false it will redirect user to login page
   * Else won't do anything
   */
  public void checkLogin() {
    if (!this.isLoggedIn()) {
      Intent i = new Intent(_context, WelcomeActivity.class);
      i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      _context.startActivity(i);
    } else {
      Intent i = new Intent(_context, MainActivity.class);
      i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      _context.startActivity(i);
    }
  }

  /**
   * Get stored session data
   */
  public String getSessionID() {
    return pref.getString(KEY_SESSION_ID, "");
  }

  public String getUserName() {
    return pref.getString(KEY_USER_NAME, "");
  }

  public String getKeyEnableLocation() {
    return pref.getString(KEY_ENABLE_LOCATION, Boolean.toString(false));
  }

  public String getKeyRecordCalls() {
    return pref.getString(KEY_BREAK_TIME, "");
  }

  public String getKeyBreakTime() {
    return pref.getString(KEY_BREAK_TIME, "");
  }

  public String getKeyBattery() {
    return pref.getString(KEY_BATTERY_STATUS, "");
  }

  public String getKeyAddress() {
    return pref.getString(KEY_ADDRESS, "");
  }

  /**
   * Clear session details
   */
  public void logoutUser() {
    editor.clear();
    editor.commit();
    Intent i = new Intent(_context, LoginActivity.class);
    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    _context.startActivity(i);
    RecordingService.stopService(_context);
  }

  /**
   * Quick check for login
   **/
  // Get Login State
  public boolean isLoggedIn() {
    return pref.getBoolean(IS_LOGIN, false);
  }

  public void saveFromTime(String time) {
    editor.putString(Constants.FROM_TIME, time).apply();
  }

  public String getFromTime() {
    return pref.getString(Constants.FROM_TIME, null);
  }

  public String getToTime() {
    return pref.getString(Constants.TO_TIME, null);
  }

  public void saveToTime(String time) {
    editor.putString(Constants.TO_TIME, time).apply();
  }
}