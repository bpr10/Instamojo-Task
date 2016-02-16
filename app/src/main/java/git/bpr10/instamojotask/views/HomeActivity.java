package git.bpr10.instamojotask.views;

import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParsePush;

import git.bpr10.instamojotask.R;
import git.bpr10.instamojotask.utils.Utils;

import static android.Manifest.permission.READ_CONTACTS;

public class HomeActivity extends BaseActivity implements View.OnClickListener {


    private static final int REQUEST_READ_CONTACTS = 0;

    String mEmail;
    private TextView mTxtName;
    private int mPushCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mEmail = getIntent().getStringExtra(Utils.Key.EMAIL);

        mTxtName = (TextView) findViewById(R.id.user_email);
        mTxtName.setText(mEmail);
        findViewById(R.id.btn_add).setOnClickListener(this);
        findViewById(R.id.btn_get).setOnClickListener(this);
        findViewById(R.id.btn_push).setOnClickListener(this);

//        ParsePush.subscribeInBackground("DEMO_Channel");

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
            Snackbar.make(mTxtName, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
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
