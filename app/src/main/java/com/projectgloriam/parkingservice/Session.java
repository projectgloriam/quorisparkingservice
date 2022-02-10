package com.projectgloriam.parkingservice;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {

    public static SharedPreferences prefs;
    private SharedPreferences.Editor editor;


    public Session(Context cntx, String userid, String email, String fullname) {
        // TODO Auto-generated constructor stub
        prefs = cntx.getSharedPreferences(cntx.getString(R.string.root_preferences), cntx.MODE_PRIVATE);
        editor = prefs.edit();
        setuserid(userid);
        setusername(email);
        setfullname(fullname);
    }

    //User Id
    public void setuserid(String userid) {
        editor.putString("userid", userid);
        editor.commit();
    }

    public String getuserid() {
        String userid = prefs.getString("userid","");
        return userid;
    }

    //Email
    public void setusername(String username) {
        editor.putString("username", username);
        editor.apply();
    }

    public String getusername() {
        String username = prefs.getString("username","");
        return username;
    }

    //Full name
    public void setfullname(String fullname) {
        editor.putString("fullname", fullname);
        editor.apply();
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
        editor.clear();
        editor.commit();
        return true;
    }
}