package git.bpr10.instamojotask.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import git.bpr10.instamojotask.model.ContactContainer;
import git.bpr10.instamojotask.model.UserContact;
import git.bpr10.instamojotask.model.UserCredentials;

/**
 * Created by bedprakash on 16/2/16.
 */
public class PrefUtils {
    private static SharedPreferences pref;

    private static String prefName = "app_pref";
    private static SharedPreferences.Editor editor;

    private static String KEY_USER_CREDS = "user_creds";
    private static String KEY_USER_SAVED_CONTACTS = "user_creds";


    /**
     * @param ctx
     */
    public static void init(Context ctx) {
        pref = ctx.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        editor = pref.edit();
        editor.apply();
    }

    public static boolean putUserCreds(UserCredentials credentials) {
        if (credentials == null)
            return false;
        ArrayList<UserCredentials> creds = getUserCreds();
        creds.add(credentials);
        editor.putString(KEY_USER_CREDS, JsonUtils.jsonify(credentials));
        return true;
    }

    public static ArrayList<UserCredentials> getUserCreds() {

        Type TYPE_CREDS = new TypeToken<ArrayList<UserCredentials>>() {
        }.getType();

        ArrayList<UserCredentials> creds = JsonUtils.objectify(pref.getString(KEY_USER_CREDS, ""), TYPE_CREDS);
        if (creds == null) {
            creds = new ArrayList<UserCredentials>();
        }
        return creds;
    }
}
