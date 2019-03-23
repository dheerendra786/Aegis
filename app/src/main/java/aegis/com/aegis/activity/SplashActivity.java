package aegis.com.aegis.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.VideoView;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;

import aegis.com.aegis.R;
import aegis.com.aegis.SessionManager;
import aegis.com.aegis.Utils.Constants;


public class SplashActivity extends AppCompatActivity {

    VideoView videoView;
    SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn()) {
            startMainActivity();
            return;
        }
        setContentView(R.layout.activity_splash);
        videoView = findViewById(R.id.videoView);

        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash);
        videoView.setVideoURI(video);

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                startNextActivity();
            }
        });
        videoView.start();

        MediaPlayer mPlayer = MediaPlayer.create(this, R.raw.audio);
        mPlayer.start();
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
        String advertisingId = preferences.getString(Constants.ADVERTISING_ID, "");

        if (advertisingId.isEmpty()) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());
                        String advertisingId = adInfo != null ? adInfo.getId() : null;
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(Constants.ADVERTISING_ID, advertisingId);
                        editor.apply();
                    } catch (IOException | GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException exception) {
                        exception.printStackTrace();
                    }
                }
            };
            // call thread start for background process
            thread.start();
        }
    }

    private void startMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    private void startNextActivity() {
        if (isFinishing())
            return;
        session.checkLogin();
        finish();
    }
}