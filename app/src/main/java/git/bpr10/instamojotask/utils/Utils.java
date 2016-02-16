package git.bpr10.instamojotask.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

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

    public static void hideKeyboard(View pView, Activity pActivity) {
        if (pView == null) {
            pView = pActivity.getWindow().getCurrentFocus();
        }
        if (pView != null) {
            InputMethodManager imm = (InputMethodManager) pActivity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(pView.getWindowToken(), 0);
            }
        }
    }



    public static int dpToPx(Context mCtx, int dp) {
        DisplayMetrics displayMetrics = mCtx.getResources().getDisplayMetrics();
        int px = Math.round(dp
                * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}
