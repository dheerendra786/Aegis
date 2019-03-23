package aegis.com.aegis.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import aegis.com.aegis.R;


/**
 * Created by Rakesh Dhaundiyal on 11/12/2108.
 * Show Toast with customized message on particular screen
 */
public class AlertUtils {

	private AlertDialog mAdvanceDialog;
	public static void showToast(Context context, String message){
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}
	
	public static void showToast(Context context, int resID){
		Toast.makeText(context, resID, Toast.LENGTH_SHORT).show();
	}
	public static void showSnackBar(View view, String message){
		Snackbar snackbar = Snackbar
				.make(view, message, Snackbar.LENGTH_LONG);
		snackbar.getView().setBackgroundColor(Color.RED);
		snackbar.setActionTextColor(view.getResources().getColor(R.color.colorAccent));
		snackbar.show();
	}

	public static void showSnackBar(View view, int resID){
		Snackbar snackbar = Snackbar
				.make(view, resID, Snackbar.LENGTH_LONG);
		snackbar.getView().setBackgroundColor(Color.RED);
		snackbar.setActionTextColor(view.getResources().getColor(R.color.colorAccent));
		snackbar.show();
	}
	public static void showSnackBarGreen(View view, String message){
		Snackbar snackbar = Snackbar
				.make(view, message, Snackbar.LENGTH_LONG);
		snackbar.getView().setBackgroundColor(Color.GREEN);
		snackbar.setActionTextColor(view.getResources().getColor(R.color.colorAccent));
		snackbar.show();
	}

	public static void showSnackBarGreen(View view, int resID){
		Snackbar snackbar = Snackbar
				.make(view, resID, Snackbar.LENGTH_LONG);
		snackbar.getView().setBackgroundColor(Color.GREEN);
		snackbar.setActionTextColor(view.getResources().getColor(R.color.colorAccent));
		snackbar.show();
	}

	
}
