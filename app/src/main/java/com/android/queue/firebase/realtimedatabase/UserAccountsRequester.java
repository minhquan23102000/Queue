package com.android.queue.firebase.realtimedatabase;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.queue.models.Room;
import com.android.queue.models.UserAccounts;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserAccountsRequester {
    private DatabaseReference mDatabase;
    private Context mContext;

    public UserAccountsRequester(Context context){
        mDatabase = FirebaseDatabase
                .getInstance("https://queue-eb51b-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference();
        this.mContext = context;
    }

    //Tạo một tài khoản
    public String createAnUserAccount(UserAccounts userAccount) {
        String accKey =  mDatabase.child(QueueDatabaseContract.UserEntry.ROOT_NAME).push().getKey();
        if (accKey != null) {
            mDatabase.child(QueueDatabaseContract.UserEntry.ROOT_NAME).child(accKey).setValue(userAccount);
            return accKey;
        }
        return null;
    }

    /**
     * Function to find an account, return its DatabaseReference
     * **/
    public DatabaseReference find(String key) {
        return mDatabase.child(QueueDatabaseContract.UserEntry.ROOT_NAME).child(key);
    }

    public DatabaseReference getmDatabase(){
        return mDatabase.child(QueueDatabaseContract.UserEntry.ROOT_NAME);
    }


    //Test data
    public void initTestData(){
        UserAccounts userAccounts = new UserAccounts("User1","0987654321","abc123456");

        createAnUserAccount(userAccounts);

    }

}
