package com.projectgloriam.parkingservice;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {

    private SharedPreferences prefs;

    public Session(Context cntx, String userid, String email, String fullname) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
        setuserid(userid);
        setusername(email);
        setfullname(fullname);
    }

    //User Id
    public void setuserid(String userid) {
        prefs.edit().putString("userid", userid).commit();
    }

    public String getuserid() {
        String userid = prefs.getString("userid","");
        return userid;
    }

    //Email
    public void setusername(String username) {
        prefs.edit().putString("username", username).commit();
    }

    public String getusername() {
        String username = prefs.getString("username","");
        return username;
    }

    //Full name
    public void setfullname(String fullname) {
        prefs.edit().putString("fullname", fullname).commit();
    }

    public String getfullname() {
        String fullname = prefs.getString("fullname","");
        return fullname;
    }

    public boolean isset() {
        //check if userid is not null
        if (prefs.getString("userid", null)!=null) return true;

        return false;
    }

    public boolean close(){
        prefs.edit().clear();
        prefs.edit().commit();
        return true;
    }
}