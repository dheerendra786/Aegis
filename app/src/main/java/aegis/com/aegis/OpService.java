package aegis.com.aegis;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;

import aegis.com.aegis.Utils.AegisConfig;
import aegis.com.aegis.Utils.AlertUtils;
import aegis.com.aegis.Utils.DeviceUtils;
import aegis.com.aegis.Utils.JsonUtils;
import aegis.com.aegis.activity.AlertDialogActivity;
import aegis.com.aegis.apiresponse.UniversalResponse;

public class OpService extends IntentService {

	public static final String OP_POSTLOCATION 	= "postlocation";
	String cityName="", countryName="", address="", postalcode="";


	public OpService() {
		super("OpService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle bundle = intent.getExtras();


		try {
			if (bundle != null) {
				String opcode = bundle.getString("OP_OPCODE");
				if (opcode != null) {
					if (opcode.equals(OP_POSTLOCATION)) {
						PostLocationtoServer(bundle);
					}
				}

			}
		} catch (Exception ex) {
		}
	}

	public static String getNetworkClass(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if(info==null || !info.isConnected())
			return "-"; //not connected
		if(info.getType() == ConnectivityManager.TYPE_WIFI)
			return "WIFI";
		if(info.getType() == ConnectivityManager.TYPE_MOBILE){
			int networkType = info.getSubtype();
			switch (networkType) {
				case TelephonyManager.NETWORK_TYPE_GPRS:
				case TelephonyManager.NETWORK_TYPE_EDGE:
				case TelephonyManager.NETWORK_TYPE_CDMA:
				case TelephonyManager.NETWORK_TYPE_1xRTT:
				case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
					return "2G";
				case TelephonyManager.NETWORK_TYPE_UMTS:
				case TelephonyManager.NETWORK_TYPE_EVDO_0:
				case TelephonyManager.NETWORK_TYPE_EVDO_A:
				case TelephonyManager.NETWORK_TYPE_HSDPA:
				case TelephonyManager.NETWORK_TYPE_HSUPA:
				case TelephonyManager.NETWORK_TYPE_HSPA:
				case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
				case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
				case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
				case TelephonyManager.NETWORK_TYPE_TD_SCDMA:  //api<25 : replace by 17
					return "3G";
				case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
				case TelephonyManager.NETWORK_TYPE_IWLAN:  //api<25 : replace by 18
				case 19:  //LTE_CA
					return "4G";
				default:
					return "?";
			}
		}
		return "?";
	}


	public void PostLocationtoServer(Bundle bundle) {
		{
			TelephonyManager manager = (TelephonyManager)getApplication().getSystemService(Context.TELEPHONY_SERVICE);
			String carrierName = manager.getNetworkOperatorName();
			Double latitude = Double.valueOf(bundle.getString("latutide"));
			Double longitude = Double.valueOf(bundle.getString("longitude"));
			SessionManager sessionManager = new SessionManager(OpService.this);
			List<Address> addresses = null;
			Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                cityName = addresses.get(0).getLocality();
                countryName = addresses.get(0).getCountryName();
                postalcode =  addresses.get(0).getPostalCode();
                address =  addresses.get(0).getAddressLine(0);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
//          sessionManager.setAddress("Country: "+countryName+"\nCity: "+cityName+"\nAddress: "+address+"\nPostal Code: "+postalcode);
			sessionManager.setAddress(cityName);

			final String keyword = "DT/";
			JSONObject obj = new JSONObject();
			try {

				obj.put("SESSION_ID", sessionManager.getSessionID());
				obj.put("BATTERY", sessionManager.getKeyBattery());
				obj.put("NETWORK", getNetworkClass(OpService.this));
				obj.put("CARRIER", carrierName);
				obj.put("LATITUDE", latitude);
				obj.put("LONGITUDE", longitude);


			} catch (JSONException e) {
				e.printStackTrace();
			}

			JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, AegisConfig.WebConstants.HOST_API + "" + keyword, obj,
					new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							System.out.println("Verify Response" + response.toString());

							if (DeviceUtils.isInternetOn(OpService.this)) {
								UniversalResponse universalResponse = JsonUtils.fromJson(response.toString(), UniversalResponse.class);

								// call the constructor of CustomAdapter to send the reference and data to Adapter
								if(universalResponse.getStatusCode().equalsIgnoreCase("201")){
									System.out.println("success Response" + response.toString());
								}

							}
						}


					},
					new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {

							if (DeviceUtils.isInternetOn(OpService.this)) {
								NetworkResponse response = error.networkResponse;
								if (error instanceof ServerError && response != null) {
									try {
										String res = new String(response.data,
												HttpHeaderParser.parseCharset(response.headers, "utf-8"));
										// Now you can use any deserializer to make sense of data
										JSONObject obj = new JSONObject(res);
										UniversalResponse universalResponse = JsonUtils.fromJson(res.toString(), UniversalResponse.class);
										// call the constructor of CustomAdapter to send the reference and data to Adapter
//										if(!universalResponse.getStatus().equalsIgnoreCase("201")){
//											Toast.makeText(AlertDialogActivity.this,universalResponse.getStatus(),Toast.LENGTH_LONG).show();
//										}

									} catch (UnsupportedEncodingException e1) {
										// Couldn't properly decode data to string
										e1.printStackTrace();
									} catch (JSONException e2) {
										// returned data is not JSONObject?
										e2.printStackTrace();
									}
								}

							} /*else {
								AlertUtils.showToast(AlertDialogActivity.this, AegisConfig.WebConstants.NETWORK_MESSAGE);
							}*/

						}
					}) {
			};


			RequestQueue requestQueue = Volley.newRequestQueue(OpService.this);
			jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
			requestQueue.add(jsObjRequest);

		}
	}


}