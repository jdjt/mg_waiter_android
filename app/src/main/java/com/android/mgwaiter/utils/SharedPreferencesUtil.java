package com.android.mgwaiter.utils;

import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * SharedPreferences工具类
 *
 * @author wmy
 */
public class SharedPreferencesUtil {

    private SharedPreferences sp;
    private Editor editor;
    private String name = "waiter";
    private int mode = Context.MODE_PRIVATE;
    private static SharedPreferencesUtil instence;
    private Context context;

    public static SharedPreferencesUtil getInstence(Context context) {
        if (instence == null)
            instence = new SharedPreferencesUtil(context);
        return instence;
    }

    public SharedPreferencesUtil(Context context) {
        this.sp = context.getSharedPreferences(name, mode);
        this.editor = sp.edit();
        this.context = context;
    }


    public void putSharedPreferences(String key, Object value) {
        String type = value.getClass().getSimpleName();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if ("Integer".equals(type)) {
            editor.putInt(key, (Integer) value);
        } else if ("Boolean".equals(type)) {
            editor.putBoolean(key, (Boolean) value);
        } else if ("String".equals(type)) {
            editor.putString(key, (String) value);
        } else if ("Float".equals(type)) {
            editor.putFloat(key, (Float) value);
        } else if ("Long".equals(type)) {
            editor.putLong(key, (Long) value);
        }
        editor.commit();
    }

    public Object getSharedPreferences(String key, Object defValue) {
        String type = defValue.getClass().getSimpleName();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        //defValue为为默认值，如果当前获取不到数据就返回它
        if ("Integer".equals(type)) {
            return sharedPreferences.getInt(key, (Integer) defValue);
        } else if ("Boolean".equals(type)) {
            return sharedPreferences.getBoolean(key, (Boolean) defValue);
        } else if ("String".equals(type)) {
            return sharedPreferences.getString(key, (String) defValue);
        } else if ("Float".equals(type)) {
            return sharedPreferences.getFloat(key, (Float) defValue);
        } else if ("Long".equals(type)) {
            return sharedPreferences.getLong(key, (Long) defValue);
        }
        return null;
    }

    public void removeSharedPreferences(Context context, String dataBasesName, String key) {
        SharedPreferences user = context.getSharedPreferences(dataBasesName, 0);
        Editor editor = user.edit();
        editor.remove(key);
        editor.commit();
    }

}
