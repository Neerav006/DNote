package com.codefuelindia.dnote.Common;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.security.MessageDigest;

public class SessionManager {

    private static final String MAIN_PREF_NAME = "LoginPref";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String KEY_NAME = "name";
    private static final String KEY_NUMBER = "number";
    private static final String KEY_U_ID = "u_id";
    private static final String KEY_ADDR = "addr";
    private static final String KEY_MOBILE = "mobile";
    private static final String KEY_PWD = "pwd";
    private static final String IS_FIRST_PHOTO_UPLOAD = "first_photo";
    SharedPreferences mainPref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;


    public SessionManager(Context context) {
        this._context = context;
        mainPref = _context.getSharedPreferences(MAIN_PREF_NAME, PRIVATE_MODE);
        editor = mainPref.edit();
    }

    public void createLoginSession(String name, String number, String u_id, String mobile, String addr) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_NAME, name);
        editor.putString(KEY_NUMBER, number);
        editor.putString(KEY_U_ID, u_id);
        editor.putString(KEY_MOBILE, mobile);
        editor.putString(KEY_ADDR, addr);

        editor.commit();
        Toast.makeText(_context, "Logged In", Toast.LENGTH_SHORT).show();
    }



    public String getUserName() {
        return mainPref.getString(KEY_NAME, null);
    }

    public String getNumber() {
        return mainPref.getString(KEY_NUMBER, null);
    }

    public String getKeyUId() {
        return mainPref.getString(KEY_U_ID, null);
    }

    public String getAddr() {
        return mainPref.getString(KEY_ADDR, null);
    }

    public String getMobile() {
        return mainPref.getString(KEY_MOBILE, null);
    }

    public boolean checkLogin() {
        boolean status = false;

        if (this.isLoggedIn()) {
            status = true;
            // user is not logged in redirect him to Login Activity
            /*Intent i = new Intent(_context, ChooseDashActivity.class);

            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            _context.startActivity(i);*/

        }
        return status;
    }

    public void deleteSession() {
        editor.clear();
        editor.commit();
    }

    // Get Login State
    public boolean isLoggedIn() {
        return mainPref.getBoolean(IS_LOGIN, false);
    }


    public static String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

}
