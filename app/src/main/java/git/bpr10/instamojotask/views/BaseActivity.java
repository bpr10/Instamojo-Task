package git.bpr10.instamojotask.views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import git.bpr10.instamojotask.utils.Utils;

/**
 * Created by bedprakash on 16/2/16.
 */
public class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;


    public void removeProgressDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        });
    }


    public void showProgressDialog(final String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this,
                    ProgressDialog.THEME_HOLO_LIGHT);
            mProgressDialog.setMessage(message);
            mProgressDialog.setCancelable(false);
        }
        if (!isFinishing()) {
            mProgressDialog.show();
        }
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    public void showToast(int resId) {
        showToast(getString(resId));
    }


    public void openContactsActivity(String pEmail, int pMode) {
        Intent i = new Intent(this, PickContacts.class);
        i.putExtra(Utils.Key.EMAIL, pEmail);
        i.putExtra(Utils.Key.MODE, pMode);
        startActivity(i);
    }
}
