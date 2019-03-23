package aegis.com.aegis.activity;

import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class DbHandler extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "call_feedback_db";
    private static final String TABLE_CALL_FEEDBACK = "callfeedback";
    private static final String KEY_CHECKSUM = "checkSum";
    private static final String KEY_CALL_DATE = "callDate";
    private static final String KEY_CALL_DURATION = "callDuration";
    private static final String KEY_SESSION_ID = "sessionID";
    private static final String KEY_PHONE_NUMBER = "phoneNumber";
    private static final String KEY_USER_NAME = "userName";

    public DbHandler(Context context){
        super(context,DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_TABLE = "CREATE TABLE " + TABLE_CALL_FEEDBACK + "("
                 + KEY_SESSION_ID + " TEXT,"
                + KEY_CHECKSUM + " TEXT,"
                + KEY_CALL_DATE + " TEXT,"
                + KEY_CALL_DURATION + " TEXT,"
                + KEY_PHONE_NUMBER + " TEXT,"
                + KEY_USER_NAME + " TEXT"
                +")";
        db.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        // Drop older table if exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALL_FEEDBACK);
        // Create tables again
        onCreate(db);
    }
    // **** CRUD (Create, Read, Update, Delete) Operations ***** //

    // Adding new Call Details
    void insertCallDetails(String sessionID, String checkSum, String callDate, String callDuration, String phoneNumber, String userName){
        //Get the Data Repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();
        //Create a new map of values, where column names are the keys
        ContentValues cValues = new ContentValues();
        cValues.put(KEY_SESSION_ID, sessionID);
        cValues.put(KEY_CHECKSUM, checkSum);
        cValues.put(KEY_CALL_DATE, callDate);
        cValues.put(KEY_CALL_DURATION, callDuration);
        cValues.put(KEY_PHONE_NUMBER, phoneNumber);
        cValues.put(KEY_USER_NAME, userName);
        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TABLE_CALL_FEEDBACK,null, cValues);
        db.close();
    }
    // Get Call Details
    public ArrayList<HashMap<String, String>> GetUsers(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> callList = new ArrayList<>();
        String query = "SELECT * FROM "+ TABLE_CALL_FEEDBACK;
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put(KEY_SESSION_ID,cursor.getString(cursor.getColumnIndex(KEY_SESSION_ID)));
            user.put(KEY_CHECKSUM,cursor.getString(cursor.getColumnIndex(KEY_CHECKSUM)));
            Date currentDate = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_CALL_DATE))));
            DateFormat df = new SimpleDateFormat("dd:MM:yy:HH:mm:ss");
            user.put(KEY_CALL_DATE,df.format(currentDate));
            if(cursor.getString(cursor.getColumnIndex(KEY_CALL_DURATION)) != null ||
                    cursor.getString(cursor.getColumnIndex(KEY_CALL_DURATION)).length() >  0 ||
                    !cursor.getString(cursor.getColumnIndex(KEY_CALL_DURATION)).equalsIgnoreCase("")) {
                int s = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_CALL_DURATION)));
                int sec = s % 60;
                int min = (s / 60)%60;
                int hours = (s/60)/60;
                user.put(KEY_CALL_DURATION,String.valueOf(String.format("%02d",hours) + ":" + String.format("%02d",min) + ":" + String.format("%02d",sec)));
            }
            else
                user.put(KEY_CALL_DURATION,"No Call Duration");
           user.put(KEY_PHONE_NUMBER,cursor.getString(cursor.getColumnIndex(KEY_PHONE_NUMBER)));
            user.put(KEY_USER_NAME,cursor.getString(cursor.getColumnIndex(KEY_USER_NAME)));
            callList.add(user);
        }
        return  callList;
    }
    // Get Call Details based on checkSum
    public ArrayList<HashMap<String, String>> GetCallBycheckSum(String checkSum){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> callList = new ArrayList<>();
        String query = "SELECT * FROM "+ TABLE_CALL_FEEDBACK;
        Cursor cursor = db.query(TABLE_CALL_FEEDBACK, new String[]{KEY_SESSION_ID, KEY_CALL_DATE, KEY_CALL_DURATION, KEY_PHONE_NUMBER, KEY_USER_NAME}, KEY_CHECKSUM+ "=?",new String[]{checkSum},null, null, null, null);
        if (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put(KEY_SESSION_ID,cursor.getString(cursor.getColumnIndex(KEY_SESSION_ID)));
            user.put(KEY_CALL_DATE,cursor.getString(cursor.getColumnIndex(KEY_CALL_DATE)));
            user.put(KEY_CALL_DURATION,cursor.getString(cursor.getColumnIndex(KEY_CALL_DURATION)));
            user.put(KEY_PHONE_NUMBER,cursor.getString(cursor.getColumnIndex(KEY_PHONE_NUMBER)));
            user.put(KEY_USER_NAME,cursor.getString(cursor.getColumnIndex(KEY_USER_NAME)));
            callList.add(user);
        }
        return  callList;
    }
    // Delete User Details
    public void DeleteUser(String checkSum){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CALL_FEEDBACK, KEY_CHECKSUM+" = ?",new String[]{checkSum});
        db.close();
    }


    /*// Update User Details
    public int UpdateUserDetails(String location, String designation, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cVals = new ContentValues();
        cVals.put(KEY_LOC, location);
        cVals.put(KEY_DESG, designation);
        int count = db.update(TABLE_Users, cVals, KEY_ID+" = ?",new String[]{String.valueOf(id)});
        return  count;
    }*/
}
