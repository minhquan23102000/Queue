package com.android.queue.models;

public class UserAccounts {
    public String fullName;
    public String phone;
    public String password;
    public Long createDate;
    public Boolean isLogin = false;
    public Boolean isHost = false;
    public String currentRoomId;

    public UserAccounts() {
    }

    public UserAccounts(String fullName, String phone, String password) {
        this.fullName = fullName;
        this.phone = phone;
        this.password = password;
        this.createDate = System.currentTimeMillis() / 1000;
    }

    public UserAccounts(String fullName, String phone, String password
                        , Boolean isHost, String currentRoomId) {
        this.fullName = fullName;
        this.phone = phone;
        this.password = password;
        this.createDate = System.currentTimeMillis() / 1000;
        this.isHost = isHost;
        this.currentRoomId = currentRoomId;
    }
}
