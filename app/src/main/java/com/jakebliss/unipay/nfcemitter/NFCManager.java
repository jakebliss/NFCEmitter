package com.jakebliss.unipay.nfcemitter;

import android.app.Activity;
import android.nfc.NfcAdapter;
import android.util.Log;

public class NFCManager {
    private Activity activity;
    private NfcAdapter nfcAdpt;

    public NFCManager(Activity activity) {
        this.activity = activity;
    }

    public void verifyNFC()  {

        nfcAdpt = NfcAdapter.getDefaultAdapter(activity);

        if (nfcAdpt == null)
            Log.e("NFC", "not supported");

        if (!nfcAdpt.isEnabled())
            Log.e("NFC", "not enabled");

    }
}
