package git.bpr10.instamojotask.utils;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.PushService;

/**
 * Created by bedprakash on 16/2/16.
 */
public class LocalApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "2PUxo322v8B0ZtUtCQKn8Gm6di0OLFkhSRMvM8b5", "jwTzRy9yQvVmwHg4bSa7IFRNW3n7pCtLdJ3998ZO");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
