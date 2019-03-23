package aegis.com.aegis.network;

/**
 * Created by rakesh on 4/2/2017.
 */

public class NetworkCommunicator{

//        extends AsyncTask {
//    private Context context;
//    public static ProgressBarDialog dialog;
//    private int requestType;
//    private String url;
//    private int method;
//    private List<Pair<String, String>> params;
//    private HashMap<String, String> headers;
//    private int reqCode;
//    private NetworkCallBack networkCallBack;
//    private String progressMessage = "Loading...";
//    private boolean isCancelable = false;
//    private RequestQueue requestQueue;
//    private JsonObjectRequest jsObjRequest;
//    private StringRequest stringRequest;
//    private JSONObject object;
//    private boolean isProgessApplicable=true;
//
//    public NetworkCommunicator(Context context, int requestType, String url, int method, List<Pair<String, String>> params, int reqCode, NetworkCallBack networkCallBack) {
//        this.context = context;
//        this.reqCode = reqCode;
//        this.requestType = requestType;
//        this.url = url;
//        this.method = method;
//        this.params = params;
//        this.networkCallBack = (NetworkCallBack) networkCallBack;
//        headers = new HashMap<String, String>();
//        requestQueue = Volley.newRequestQueue(context);
//    }
//
//    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//        if (DeviceUtils.isInternetOn(context)) {
//            if (isProgessApplicable) {
//                dialog = new ProgressBarDialog(context);
////        dialog.setMessage(progressMessage);
//                dialog.setCancelable(isCancelable);
//                dialog.show();
//            }
//        }
//        else {
//            AlertUtils.showToast(context, AegisConfig.WebConstants.NETWORK_MESSAGE);
//        }
//    }
//
//    @Override
//    protected Object doInBackground(Object[] objects) {
////        if (DeviceUtils.isInternetOn(context)) {
//            switch (requestType) {
//                case 0:
//                    makeJsonObjRequest();
//                    break;
//                case 1:
//                    makeJsonArrayRequest();
//                    break;
//                case 2:
//                    makeStringRequest();
//                    break;
//                case 3:
//                    makeHttpsRequest();
//                    break;
//            }
//
////        }
//
//
//        return null;
//    }
//
//    public void setHeaders(HashMap<String, String> headers) {
//        this.headers = headers;
//    }
//
//    public void setProgressMessage(String progressMessage, boolean isCancelable) {
//        this.progressMessage = progressMessage;
//        this.isCancelable = isCancelable;
//    }
//
//    private void makeJsonObjRequest() {
//        object = new JSONObject();
//        for (Pair<String, String> pair : params) {
//            try {
//                object.put(pair.first, pair.second);
//                LogUtils.info("Request Parameter-->",method+url+object);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        jsObjRequest = new JsonObjectRequest(method, url, object,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        networkCallBack.onSuccess(response.toString(), reqCode);
//                        if (dialog != null) {
//                            dialog.dismiss();
//                            LogUtils.info("Success Response-->",response.toString());
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        error.printStackTrace();
//                        networkCallBack.onFailure(error, reqCode);
//                        if (dialog != null) {
//                            dialog.dismiss();
//                            LogUtils.info("Error Response-->",error.getMessage());
//                        }
//                    }
//                }) {
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                return headers;
//            }
//        };
//        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        requestQueue.add(jsObjRequest);
//    }
//
//    private void makeJsonArrayRequest() {
//
//    }
//
//    private void makeStringRequest() {
//        stringRequest = new StringRequest(method, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        networkCallBack.onSuccess(response.toString(), reqCode);
//                        if (dialog != null) {
//                            dialog.dismiss();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        networkCallBack.onFailure(error, reqCode);
//                        if (dialog != null) {
//                            dialog.dismiss();
//                        }
//                    }
//                }
//
//        ) {
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                return headers;
//            }
//        };
//        HttpStack httpStack = null;
//        if (Build.VERSION.SDK_INT > 19) {
//            httpStack = new CustomHurlStack();
//        } else if (Build.VERSION.SDK_INT >= 9 && Build.VERSION.SDK_INT <= 19) {
//            httpStack = new OkHttpHurlStack();
//        }
//        RequestQueue requestQueue = Volley.newRequestQueue(context, httpStack);
//        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        requestQueue.add(stringRequest);
//    }
//
//    private void makeHttpsRequest() {
//
//    }
//    public void setProgressBarApplicable(boolean b){
//        this.isProgessApplicable=b;
//    }
}
