package git.bpr10.instamojotask.views;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;

import git.bpr10.instamojotask.R;
import git.bpr10.instamojotask.model.UserContact;


/**
 * Created by amit on 6/19/2015.
 */
public class ContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int MODE_SELECT = 1;
    public static final int MODE_DISPLAY = 2;
    private final int mViewMode;

    private interface ViewType {
        int LOADER = 0;
        int FRIEND = 1;
    }

    private static final String LOG_TAG = ContactsAdapter.class.getSimpleName();


    int checkedCount;
    private PickContacts mPickContacts;
    private LayoutInflater mInflater;
    private List<UserContact> mfriendsList;

    public ContactsAdapter(PickContacts mActivity, List<UserContact> mfriends, int pViewMode) {
        this.mPickContacts = mActivity;
        this.mfriendsList = mfriends;
        this.mViewMode = pViewMode;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mPickContacts).inflate(
                R.layout.list_item_friend_search, parent, false);
        return new ViewHolderFriend(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolderFriend) holder).populateData(mfriendsList.get(position));
    }

    @Override
    public int getItemCount() {
        return mfriendsList.size();
    }

    private class ViewHolderFriend extends RecyclerView.ViewHolder {

        TextView mTvName;
        CheckBox mCheckBoxSelect;

        public ViewHolderFriend(View convertView) {
            super(convertView);

            mTvName = (TextView) convertView.findViewById(R.id.tv_name);
            mCheckBoxSelect = (CheckBox) convertView.findViewById(R.id.check_add);

            if (mViewMode == MODE_DISPLAY) {
                mCheckBoxSelect.setVisibility(View.INVISIBLE);
                return;
            }

            mCheckBoxSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = (String) v.getTag();
                    UserContact contact = new UserContact("");
                    contact.setId(id);
                    int index = mfriendsList.indexOf(contact);
                    if (index < 0)
                        return;
                    contact = mfriendsList.get(index);

                    if (!contact.isSelected()) {
                        if (checkedCount >= 5) {
                            ((CompoundButton) v).setChecked(false);
                            Toast.makeText(mPickContacts, R.string.toast_max_limit, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        contact.setSelected(true);
                        checkedCount++;
                    } else {
                        contact.setSelected(false);
                        checkedCount--;
                    }

                    mPickContacts.onContactChanged(mfriendsList.get(index), contact.isSelected());

                }
            });
        }


        public void populateData(UserContact userContact) {
            mTvName.setText(userContact.getName());
            mTvName.setTag(userContact.getId());
            if (userContact.isSelected()) {
                mCheckBoxSelect.setChecked(true);
            } else {
                mCheckBoxSelect.setChecked(false);
            }
            mCheckBoxSelect.setTag(userContact.getId());
        }
    }


    private void setChecked(String id, boolean isChecked) {
        UserContact contact = new UserContact("");
        contact.setId(id);
        int index = mfriendsList.indexOf(contact);
        if (index < 0)
            return;
        if (isChecked) {
            checkedCount++;
        } else {
            checkedCount--;
        }
        mfriendsList.get(index).setSelected(isChecked);

        mPickContacts.onContactChanged(mfriendsList.get(index), isChecked);
    }
}