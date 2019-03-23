package aegis.com.aegis.activity;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import aegis.com.aegis.R;


public class ForgotPasswordDialog extends Dialog  {
    LoginActivity mContext1;
    private View mView;
    private TextView mTxtTitle;
    private EditText mEdtEmail;
    private Button mBtnSend, mBtnCancel;

    private LinearLayout mLnrForgotPassword;
    private RelativeLayout mRlLogin;


    public ForgotPasswordDialog(@NonNull Context context) {
        super(context);
    }

    public ForgotPasswordDialog(LoginActivity context, RelativeLayout mRlLogin) {
        super(context, R.style.AdvanceDialogTheme);
        this.mContext1 = context;
        this.mRlLogin =mRlLogin;
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_forgot_password);
        setCancelable(true);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.dimAmount = 0.9f;
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(true);
        mView = findViewById(R.id.view);
        mTxtTitle = findViewById(R.id.dialogForgotPassword_txtTitle);
        mEdtEmail = findViewById(R.id.dialogForgotPassword_edtEmail);
        mBtnSend = findViewById(R.id.dialogForgotPassword_btnSend);
        mBtnCancel = findViewById(R.id.dialogForgotPassword_btnCancel);
        mLnrForgotPassword = findViewById(R.id.forgotpassword_lnr);


        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               dismiss();
            }
        });
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }


}
