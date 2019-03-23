package aegis.com.aegis;

import android.app.Application;


/**
 * Created by Rakesh Dhaundiyal on 11/12/2018.
 * AppContext to create instance
 */
public class AppContext {
    private static AppContext INSTANCE = null;
    private Application mApplication;

    public static AppContext getInstance() {

        if (INSTANCE == null) INSTANCE = new AppContext();
        return INSTANCE;
    }

    public Application getContext() {
        return this.mApplication;
    }

    public void setContext(Application application) {
        this.mApplication = application;
    }


}
