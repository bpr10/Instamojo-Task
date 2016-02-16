package git.bpr10.instamojotask.utils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by bedprakash on 16/2/16.
 */
public class Utils {

    public interface Key {
        String EMAIL = "username";
        String MODE = "mode";
    }

    public interface ParseConstants {
        String PARSE_USER = "DemoUser";
        String PARSE_USER_EMAIL = "email";
        String PARSE_USER_PWD = "password";

        String USER_CONTACT = "UserContact";
        String NAME = "name";
        String CONTACTS_JSON = "contacts_json";
    }


    public static int dpToPx(Context mCtx, int dp) {
        DisplayMetrics displayMetrics = mCtx.getResources().getDisplayMetrics();
        int px = Math.round(dp
                * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}
