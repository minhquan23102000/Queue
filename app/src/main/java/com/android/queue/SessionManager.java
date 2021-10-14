package com.android.queue;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.android.queue.firebase.realtimedatabase.QueueDatabaseContract.UserEntry;

public class SessionManager {
    public static final String TAG = SessionManager.class.getName();
    private final SharedPreferences userSession;
    private final SharedPreferences.Editor userDataEditor;
    private final Context mContext;

    public SessionManager(Context context) {
        mContext = context;
        userSession = mContext.getSharedPreferences("userSession", Context.MODE_PRIVATE);
        userDataEditor = userSession.edit();
    }

    public void initUserSession(String phone, String fullName) {
        userDataEditor.putString(UserEntry.PHONE_ARM, phone);
        userDataEditor.putString(UserEntry.FULL_NAME_ARM, fullName);
        userDataEditor.commit();
    }

    public void putUserCurrentRoomId(String currentRoomId, boolean isHost) {
        userDataEditor.putString(UserEntry.CURRENT_ROOM_ARM, currentRoomId);
        userDataEditor.putBoolean(UserEntry.IS_HOST_ARM, isHost);
        userDataEditor.commit();
    }

    public boolean isLogin() {
        String phone = userSession.getString(UserEntry.PHONE_ARM, null);
        return phone != null;
    }

    public Bundle getUserData() {
        String phone = userSession.getString(UserEntry.PHONE_ARM, null);
        String fullName = userSession.getString(UserEntry.FULL_NAME_ARM, null);
        String currentRoomId = userSession.getString(UserEntry.CURRENT_ROOM_ARM, null);
        boolean isHost = userSession.getBoolean(UserEntry.IS_HOST_ARM, false);
        Bundle bundle = new Bundle();
        bundle.putString(UserEntry.PHONE_ARM, phone);
        bundle.putString(UserEntry.FULL_NAME_ARM, fullName);
        bundle.putString(UserEntry.CURRENT_ROOM_ARM, currentRoomId);
        bundle.putBoolean(UserEntry.IS_HOST_ARM, isHost);
        return bundle;
    }

    public void clearUserData() {
        userDataEditor.clear();
        userDataEditor.commit();
    }
}
