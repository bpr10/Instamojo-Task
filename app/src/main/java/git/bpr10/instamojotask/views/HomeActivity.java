package git.bpr10.instamojotask.views;

import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import git.bpr10.instamojotask.R;
import git.bpr10.instamojotask.model.UserContact;
import git.bpr10.instamojotask.utils.JsonUtils;
import git.bpr10.instamojotask.utils.Utils;

import static android.Manifest.permission.READ_CONTACTS;

public class HomeActivity extends BaseActivity implements View.OnClickListener {


    private static final int REQUEST_READ_CONTACTS = 0;

    private TextView mTxtEmail;
    private EditText mEdtName;

    private String mEmail;
    private String mName;

    private int mPushCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mEmail = getIntent().getStringExtra(Utils.Key.EMAIL);
        mName = getIntent().getStringExtra(Utils.ParseConstants.NAME);
        if (mName == null)
            mName = "";

        mTxtEmail = (TextView) findViewById(R.id.user_email);
        mTxtEmail.setText(mEmail);

        mEdtName = (EditText) findViewById(R.id.name);
        mEdtName.setText(mName);

        findViewById(R.id.btn_add).setOnClickListener(this);
        findViewById(R.id.btn_get).setOnClickListener(this);
        findViewById(R.id.btn_push).setOnClickListener(this);
        findViewById(R.id.btn_update_name).setOnClickListener(this);

        ParsePush.subscribeInBackground("DEMO_Channel");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add: {
                if (!mayRequestContacts()) {
                    return;
                }

                openContactsActivity(mEmail, ContactsAdapter.MODE_SELECT);
                break;
            }
            case R.id.btn_get: {
                openContactsActivity(mEmail, ContactsAdapter.MODE_DISPLAY);
                break;
            }
            case R.id.btn_push: {
                ParsePush push = new ParsePush();
                push.setChannel("DEMO_Channel");
                push.setMessage("Recieved Push " + mPushCount++);
                push.sendInBackground();
                break;
            }
            case R.id.btn_update_name: {
                ParseQuery<ParseObject> query = ParseQuery.getQuery(Utils.ParseConstants.PARSE_USER);
                query.whereEqualTo(Utils.ParseConstants.PARSE_USER_EMAIL, mEmail);
                showProgressDialog(getString(R.string.progress_wait));
                Utils.hideKeyboard(mEdtName, this);
                query.findInBackground(new FindCallback<ParseObject>() {

                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e != null || objects.size() == 0) {
                            showToast(R.string.error_common);
                            removeProgressDialog();
                            return;
                        }
                        ParseObject obj = objects.get(0);
                        obj.put(Utils.ParseConstants.NAME, mEdtName.getText().toString());
                        obj.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                removeProgressDialog();
                                if (e != null) {
                                    showToast(R.string.error_common);
                                    return;
                                }
                                showToast(R.string.success);
                            }
                        });

                    }
                });
            }
        }
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mTxtEmail, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    }).show();
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }
}
