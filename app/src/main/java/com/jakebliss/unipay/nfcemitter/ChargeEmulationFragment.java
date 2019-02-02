package com.jakebliss.unipay.nfcemitter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class ChargeEmulationFragment extends Fragment {

    public static final String TAG = "ChargeFragment";

    /** Called when sample is created. Displays generic UI with welcome text. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_charge_creation, container, false);
        EditText description = (EditText) v.findViewById(R.id.product_et);
        EditText price = (EditText) v.findViewById(R.id.amount_et);
        description.setText(ChargeStorage.GetDescription(getActivity()));
        price.setText(ChargeStorage.GetPrice(getActivity()));
        description.addTextChangedListener(new DescriptionUpdater());
        price.addTextChangedListener(new PriceUpdater());
        return v;
    }


    private class DescriptionUpdater implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Not implemented.
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Not implemented.
        }

        @Override
        public void afterTextChanged(Editable s) {
            String description = s.toString();
            ChargeStorage.SetDescription(getActivity(), description);
        }
    }


    private class PriceUpdater implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Not implemented.
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Not implemented.
        }

        @Override
        public void afterTextChanged(Editable s) {
            String price = s.toString();
            ChargeStorage.SetPrice(getActivity(), price);
        }
    }
}
