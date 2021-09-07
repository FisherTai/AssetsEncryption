package gw.com.assetsencryption;

import android.app.Application;

public class AppMain extends Application {

    public static AppMain application = null;

    public AppMain() {
        super();
        application = this;
    }

}
