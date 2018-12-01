// Followed tutorial from: https://www.survivingwithandroid.com/2016/01/nfc-tag-writer-android.html
package com.jakebliss.unipay.nfcemitter;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Locale;

import static java.text.Normalizer.Form.NFC;

public class MessageCreatorActivity extends AppCompatActivity implements NfcAdapter.OnNdefPushCompleteCallback,
        NfcAdapter.CreateNdefMessageCallback{
    private NFCManager nfcMger = null;
    final private int NFC_PERMISSION_CODE = 1000;
    private Button mPayButton;
    private TextView mAmount;
    private TextView mProduct;
    private NfcAdapter mNFCAdapter = null;
    private ArrayList<String> messagesToSendArray = new ArrayList<>();
    private ArrayList<String> messagesReceivedArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //nfcMger = new NFCManager(this);
        setContentView(R.layout.activity_message_creator);

        mPayButton = (Button) findViewById(R.id.pay_button);
        mAmount = (TextView) findViewById(R.id.amount_tv);
        mProduct = (TextView) findViewById(R.id.product_tv);

        setPayment(3.50, "Yerba Mate");

        if (ContextCompat.checkSelfPermission( MessageCreatorActivity.this, Manifest.permission.NFC)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MessageCreatorActivity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE}, NFC_PERMISSION_CODE);
        }


        mPayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPaymentRecords(mAmount.getText().toString(), mProduct.getText().toString());
            }
        });

        mNFCAdapter = NfcAdapter.getDefaultAdapter(this);

        if(mNFCAdapter != null) {
            mNFCAdapter.setNdefPushMessageCallback(this, this);
            mNFCAdapter.setOnNdefPushCompleteCallback(this, this);
        }

        handleNfcIntent(getIntent());
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {
        messagesToSendArray.clear();
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        if (messagesToSendArray.size() == 0) {
            return null;
        }
        NdefRecord[] recordsToAttach = createRecords();
        return new NdefMessage(recordsToAttach);
    }

    public NdefRecord[] createRecords() {
        NdefRecord[] records = new NdefRecord[messagesToSendArray.size() + 1];

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            for (int i = 0; i < messagesToSendArray.size(); i++) {
                byte[] payload = messagesToSendArray.get(i).
                        getBytes(Charset.forName("UTF-8"));

                NdefRecord record = new NdefRecord(
                        NdefRecord.TNF_WELL_KNOWN,
                        NdefRecord.RTD_TEXT,
                        new byte[0],
                        payload);

                records[i] = record;
            }
        }
        else {
            for (int i = 0; i < messagesToSendArray.size(); i++){
                byte[] payload = messagesToSendArray.get(i).
                        getBytes(Charset.forName("UTF-8"));

                NdefRecord record = NdefRecord.createMime("text/plain",payload);
                records[i] = record;
            }
        }
        records[messagesToSendArray.size()] =
                NdefRecord.createApplicationRecord(getPackageName());
        return records;
    }

    private void handleNfcIntent(Intent NfcIntent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(NfcIntent.getAction())) {
            Parcelable[] receivedArray =
                    NfcIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if(receivedArray != null) {
                messagesReceivedArray.clear();
                NdefMessage receivedMessage = (NdefMessage) receivedArray[0];
                NdefRecord[] attachedRecords = receivedMessage.getRecords();

                for (NdefRecord record:attachedRecords) {
                    String string = new String(record.getPayload());
                    if (string.equals(getPackageName())) { continue; }
                    messagesReceivedArray.add(string);
                }
                Toast.makeText(this, "Received " + messagesReceivedArray.size() +
                        " Messages", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "Received Blank Parcel", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        Log.d("Jake", "newIntent");
        handleNfcIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        handleNfcIntent(getIntent());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case NFC_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    }
                break;
        }
    }

    public NdefMessage createUriMessage(String content, String type) {
        NdefRecord record = NdefRecord.createUri(type + content);
        NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
        return msg;
    }

    public NdefMessage createTextMessage(String content) {
        try {
            // Get UTF-8 byte
            byte[] lang = Locale.getDefault().getLanguage().getBytes("UTF-8");
            byte[] text = content.getBytes("UTF-8"); // Content in UTF-8

            int langSize = lang.length;
            int textLength = text.length;

            ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + langSize + textLength);
            payload.write((byte) (langSize & 0x1F));
            payload.write(lang, 0, langSize);
            payload.write(text, 0, textLength);
            NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                    NdefRecord.RTD_TEXT, new byte[0],
                    payload.toByteArray());
            return new NdefMessage(new NdefRecord[]{record});
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void setPayment(Double amount, String product) {
        mAmount.setText(String.valueOf(amount));
        mProduct.setText(product);
    }

    private void addPaymentRecords(String amount, String product) {
        messagesToSendArray.add(amount);
        messagesToSendArray.add(product);
    }

}
