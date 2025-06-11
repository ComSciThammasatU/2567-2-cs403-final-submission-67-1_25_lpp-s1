package com.example.homestray;

import android.content.Context;
import android.content.SharedPreferences;

public class ActivityStateHelper {
    private static final String PREF_NAME = "activity_state";
    private static final String KEY_LAST_ACTIVITY = "last_activity";

    public static void saveLastActivity(Context context, String activityName){
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_LAST_ACTIVITY, activityName).apply();
    }

    public static String getKeyLastActivity(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_LAST_ACTIVITY, null);
    }

    public static void clearLastActivity(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_LAST_ACTIVITY).apply();
    }
}
