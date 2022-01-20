package com.projectgloriam.parkingservice;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {
    private final MutableLiveData<Session> session = new MutableLiveData<Session>();
    public void setSession(Session sess) {
        session.setValue(sess);
    }
    public LiveData<Session> getSession() {
        return session;
    }
}
