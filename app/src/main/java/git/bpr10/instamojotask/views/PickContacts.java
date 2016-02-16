package git.bpr10.instamojotask.views;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import git.bpr10.instamojotask.model.ContactContainer;
import git.bpr10.instamojotask.utils.JsonUtils;
import git.bpr10.instamojotask.R;
import git.bpr10.instamojotask.model.UserContact;
import git.bpr10.instamojotask.utils.Utils;

public class PickContacts extends BaseActivity {

    private static final String LOG_TAG = PickContacts.class.getSimpleName();

    private LineBreakingLayout mLineBreakingLayout;

    private AsyncTask<Void, Void, Void> mContactsTask;

    private ArrayList<UserContact> mSelectedContacts;
    private String mUserEmail;

    int mViewMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_contacts);

        mLineBreakingLayout = (LineBreakingLayout) findViewById(R.id.choosen_contacts);
        mSelectedContacts = new ArrayList<>();

        mViewMode = getIntent().getExtras().getInt(Utils.Key.MODE);
        mUserEmail = getIntent().getExtras().getString(Utils.Key.EMAIL);
        if (mUserEmail == null) {
            Log.d(LOG_TAG, "no user email in intent");
            finish();
        }
        if (mViewMode == ContactsAdapter.MODE_SELECT) {
            mContactsTask = new LoadContactsTask(this).execute();
        } else {
            mLineBreakingLayout.setVisibility(View.GONE);
            fetchSelectedContacts();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mViewMode == ContactsAdapter.MODE_SELECT) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.save: {
                saveSelectedContacts();
                break;
            }
        }
        return true;
    }

    private void fetchSelectedContacts() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Utils.ParseConstants.USER_CONTACT);
        query.whereEqualTo(Utils.ParseConstants.PARSE_USER_EMAIL, mUserEmail);
        showProgressDialog(getString(R.string.progress_wait));
        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                removeProgressDialog();
                if (e != null) {
                    showToast(R.string.error_common);
                    return;
                }
                if (objects.size() > 0) {
                    Type TYPE_SAVED_CONTACTS = new TypeToken<ArrayList<UserContact>>() {
                    }.getType();
                    ArrayList<UserContact> savedContacts = JsonUtils.objectify((String) objects.get(0).get(Utils.ParseConstants.CONTACTS_JSON), TYPE_SAVED_CONTACTS);
                    if (savedContacts != null) {
                        showContacts(savedContacts);
                    }
                } else {
                    Log.d(LOG_TAG, "NO SUCH EMAIL");
                    showToast(R.string.error_no_saved_contacts);
                    finish();
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        if (mContactsTask != null) {
            mContactsTask.cancel(true);
        }
        super.onDestroy();
    }

    static class LoadContactsTask extends AsyncTask<Void, Void, Void> {

        private final ArrayList<UserContact> contactList;
        PickContacts pickContactsActivity;

        LoadContactsTask(PickContacts pickContactsActivity) {
            this.pickContactsActivity = pickContactsActivity;
            contactList = new ArrayList();

        }

        @Override
        protected Void doInBackground(Void... params) {
            Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

            if (pickContactsActivity == null) {
                cancel(true);
                return null;
            }
            ContentResolver contentResolver = pickContactsActivity.getContentResolver();

            Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone._ID,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            if (phoneCursor != null) {
                try {
                    while (phoneCursor.moveToNext()) {
                        if (isCancelled()) {
                            break;
                        }
                        UserContact tempContact = new UserContact(phoneCursor.getString(1));
                        tempContact.setNumber(phoneCursor.getString(2));
                        tempContact.setId(phoneCursor.getString(0));
                        contactList.add(tempContact);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    phoneCursor.close();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (pickContactsActivity != null) {
                pickContactsActivity.showContacts(contactList);
            }
            super.onPostExecute(aVoid);
        }
    }

    private void showContacts(ArrayList<UserContact> contactList) {
        RecyclerView listContacts = (RecyclerView) findViewById(R.id.list_contacts);
        listContacts.setHasFixedSize(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listContacts.setLayoutManager(layoutManager);
        listContacts.setAdapter(new ContactsAdapter(this, contactList, mViewMode));
    }


    public void onContactChanged(UserContact contact, boolean isChecked) {

        TextView t = new TextView(this);
        t.setText(contact.getName());
        t.setBackgroundResource(R.drawable.round_rect);
        t.setSingleLine(true);
        t.setTextSize(16);
        t.setTextColor(getResources().getColor(R.color.white));

        int dp10 = Utils.dpToPx(this, 10);
        int dp5 = Utils.dpToPx(this, 5);

        t.setPadding(dp10, dp5, dp10, dp5);

        if (isChecked) {
            mSelectedContacts.add(contact);
            mLineBreakingLayout.addView(t, new LineBreakingLayout.LayoutParams(dp10, dp10));

        } else {
            int index = mSelectedContacts.indexOf(contact);
            mSelectedContacts.remove(index);
            mLineBreakingLayout.removeViewAt(index);
        }
    }

    @Override
    public void onBackPressed() {
        saveSelectedContacts();
    }

    private void saveSelectedContacts() {
        if (mViewMode != ContactsAdapter.MODE_SELECT) {
            super.onBackPressed();
            return;
        }
        final ParseQuery<ParseObject> query = ParseQuery.getQuery(Utils.ParseConstants.USER_CONTACT);
        query.whereEqualTo(Utils.ParseConstants.PARSE_USER_EMAIL, mUserEmail);
        showProgressDialog(getString(R.string.progress_wait));

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e != null) {
                    removeProgressDialog();
                    showToast(R.string.error_common);
                    return;
                }
                if (objects.size() == 0) {
                    // create a new record
                    ParseObject newRecord = new ParseObject(Utils.ParseConstants.USER_CONTACT);
                    newRecord.put(Utils.ParseConstants.PARSE_USER_EMAIL, mUserEmail);
                    newRecord.put(Utils.ParseConstants.CONTACTS_JSON, JsonUtils.jsonify(mSelectedContacts));
                    newRecord.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            removeProgressDialog();
                            if (e != null) {
                                showToast(R.string.error_common);
                                return;
                            }
                            finish();

                        }
                    });
                    return;
                }
                query.getInBackground(objects.get(0).getObjectId(), new GetCallback<ParseObject>() {
                    public void done(ParseObject userContact, ParseException e) {
                        if (e != null) {
                            removeProgressDialog();
                            showToast(R.string.error_common);
                            return;
                        }
                        userContact.put(Utils.ParseConstants.CONTACTS_JSON, JsonUtils.jsonify(mSelectedContacts));
                        userContact.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                removeProgressDialog();
                                if (e != null) {
                                    showToast(R.string.error_common);
                                    return;
                                }
                                finish();

                            }
                        });
                    }
                });

            }
        });
    }
}
