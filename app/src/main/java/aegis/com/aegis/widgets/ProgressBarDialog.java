package aegis.com.aegis.widgets;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;


import aegis.com.aegis.R;
import pl.droidsonroids.gif.GifTextView;

/**
 * Created by Rakesh Dhaundiyal on 1/9/2107.
 */
public class ProgressBarDialog extends Dialog {

	private TextView mTxtMessage;

    GifTextView mGifTextView;

    public ProgressBarDialog(Context context) {
        super(context, R.style.Theme_AppCompat_Light_Dialog);
        init();
    }

    public ProgressBarDialog(Context context, int theme) {
        super(context, R.style.Theme_AppCompat_Light_Dialog);
        init();
    }

    private void init() {

        setContentView(R.layout.dialog_progres_bar);
        setCancelable(true);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.dimAmount = 0.9f; // Dim level. 0.0 - no dim, 1.0 - completely opaque
        getWindow().setAttributes(lp);

		mTxtMessage = (TextView) findViewById(R.id.dialogProgress_txtMessage);
        mGifTextView = (GifTextView) findViewById(R.id.gifTextView) ;
		mTxtMessage.setVisibility(View.GONE);
    }

    public void setMessage(String message) {
        mGifTextView.setVisibility(View.VISIBLE);
		mTxtMessage.setText(message);
		mTxtMessage.setVisibility(View.GONE);
    }

    public void setMessage(int resId) {
		mTxtMessage.setText(resId);
		mTxtMessage.setVisibility(View.GONE);
    }

    @Override
    public void dismiss() {

        if (this != null && isShowing()) {
            try {
                super.dismiss();
            } catch (Exception e) {// nothing }
            }
        }

    }
}
