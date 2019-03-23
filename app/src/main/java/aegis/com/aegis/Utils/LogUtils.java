package aegis.com.aegis.Utils;

import android.util.Log;


/**
 * Created by Rakesh Dhaundiyal on 11/12/2018.
 * @author This class is used for adding log in-line. 
 * Need to change "mIsDebug" value "false" when deploy.
 *
 */
public class LogUtils {
	
	private static boolean mIsDebug = true;
	
	public static void info(Object object){
		if(mIsDebug){
			Log.i("BrightKids_Log : ", ""+object);
		}
	}
	
	public static void info(String tag, Object object){
		if(mIsDebug){
			Log.i("BrightKids_Log : " + tag, ""+object);
		}
	}
	
	public static void debug(Object object){
		if(mIsDebug){
			Log.d("BrightKids_Log : ", ""+object);
		}
	}
	
	public static void debug(String tag, Object object){
		if(mIsDebug){
			Log.d("BrightKids_Log : " + tag, ""+object);
		}
	}

	public static void debug(String tag, Object object, Throwable exception){
		if(mIsDebug){
			Log.d("BrightKids_Log : " + tag, ""+object,exception);
		}
	}

	public static void error(Object object){
		if(mIsDebug){
			Log.e("BrightKids_Log : ", ""+object);
			if(object instanceof Exception){
				((Exception) object).printStackTrace();
			}
		}
	}
	
	public static void error(String tag, Object object){
		if(mIsDebug){
			Log.e("BrightKids_Log : " + tag, ""+object);
			if(object instanceof Exception){
				((Exception) object).printStackTrace();
			}
		}
	}
	
	public static void print(Object object){
		if(mIsDebug){
			System.out.println("BrightKids_Log : " + object);
		}
	}
}
