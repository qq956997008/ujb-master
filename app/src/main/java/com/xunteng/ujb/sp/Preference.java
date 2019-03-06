package com.xunteng.ujb.sp;

import android.content.Context;
import android.content.SharedPreferences;

import com.xunteng.ujb.UjbApp;

public class Preference {

    private static final String isFristOpen = "is_first_open";

    private static final String username = "username";
    private static final String password = "password";
    private static final String mobile = "mobile";
    private static final String avatar = "avatar";

    private static final String isLogin = "is_login";


    // 获取SharedPreference对象
    private static SharedPreferences getSharedPreferences(){
        return UjbApp.getContext().getSharedPreferences("ujb", Context.MODE_PRIVATE);
    }
    // 获取是否是第一次进入的boolean值
    public static boolean getIsFristOpen() {
        return getSharedPreferences().getBoolean(isFristOpen, true);
    }
    // 设置是否是第一次进入的boolean值
    public static void setIsFristOpen(boolean isFristOpen_){
        getSharedPreferences().edit().putBoolean(isFristOpen, isFristOpen_).apply();
    }
    // 获取是否是第一次进入的boolean值
    public static boolean getIsLogin() {
        return getSharedPreferences().getBoolean(isLogin, false);
    }
    // 设置是否是第一次进入的boolean值
    public static void setIsLogin(boolean isLogin_){
        getSharedPreferences().edit().putBoolean(isLogin, isLogin_).apply();
    }

    public static String getUsername() {
        return getSharedPreferences().getString(username, null);
    }

    public static void setUsername(String username_){
        getSharedPreferences().edit().putString(username, username_).apply();
    }

    public static String getPassword() {
        return getSharedPreferences().getString(password, null);
    }

    public static void setPassword(String password_){
        getSharedPreferences().edit().putString(password, password_).apply();
    }

    public static String getMobile() {
        return getSharedPreferences().getString(mobile, null);
    }

    public static void setMobile(String mobile_){
        getSharedPreferences().edit().putString(mobile, mobile_).apply();
    }

    public static String getAvatar() {
        return getSharedPreferences().getString(avatar, null);
    }

    public static void setAvatar(String avatar_){
        getSharedPreferences().edit().putString(avatar, avatar_).apply();
    }
}
