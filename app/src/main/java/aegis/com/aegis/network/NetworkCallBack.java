package aegis.com.aegis.network;

        import com.android.volley.VolleyError;

/**
 * Created by rakesh on 4/2/2017.
 */

public interface NetworkCallBack {
    public void onSuccess(String response, int requestCode);

    public void onFailure(VolleyError error, int requestCode);
}
