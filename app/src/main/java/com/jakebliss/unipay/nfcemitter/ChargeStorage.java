package com.jakebliss.unipay.nfcemitter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class ChargeStorage {
    private static final String PREF_PRICE = "price";
    private static final String PREF_DESCRIPTION = "description";
    private static final String DEFAULT_PRICE = "0";
    private static final String DEFAULT_DESCRIPTION = "description";
    private static final String TAG = "ChargeStorage";
    private static String sPrice = null;
    private static String sDescription = null;
    private static final Object sChargeLock = new Object();

    public static void SetPrice(Context c, String price) {
        synchronized(sChargeLock) {
            Log.i(TAG, "Setting price: " + price);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
            prefs.edit().putString(PREF_PRICE, price).commit();
            sPrice = price;
        }
    }

    public static void SetDescription(Context c, String description) {
        synchronized(sChargeLock) {
            Log.i(TAG, "Setting description: " + description);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
            prefs.edit().putString(PREF_DESCRIPTION, description).commit();
            sDescription = description;
        }
    }

    public static String GetPrice(Context c) {
        synchronized (sChargeLock) {
            if (sPrice == null) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
                String price = prefs.getString(PREF_PRICE, DEFAULT_PRICE);
                sPrice = price;
            }
            return sPrice;
        }
    }

    public static String GetDescription(Context c) {
        synchronized (sChargeLock) {
            if (sDescription == null) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
                String description = prefs.getString(PREF_DESCRIPTION, DEFAULT_DESCRIPTION);
                sDescription = description;
            }
            return sDescription;
        }
    }
}