package com.example.tad_bank_t1.ui.form.otp;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.databinding.FragmentPINFormBinding;

public class PINFormFragment extends DialogFragment {
    private OnPinSubmitListener listener;

    // binding
    private FragmentPINFormBinding binding;

    EditText[] pinFields;


    public PINFormFragment() {
        // Required empty public constructor
    }

    public interface OnPinSubmitListener {
        void onPinSubmit(String pin);
        void onPinCancel();
        void onPinInvalid(String message);
    }

    public PINFormFragment(OnPinSubmitListener listener) {
        this.listener = listener;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPINFormBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pinFields = new EditText[]{
                binding.et1PINTxn,
                binding.et2PINTxn,
                binding.et3PINTxn,
                binding.et4PINTxn,
                binding.et5PINTxn,
                binding.et6PINTxn
        };

        setUpOtpInput();

        setUpEvents();


    }

    private void setUpOtpInput(){
        for(int i = 0; i < pinFields.length; i++){
            int index = i;

            pinFields[index].addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1 && index < pinFields.length - 1){
                        pinFields[index + 1].requestFocus();
                    }

                    if (s.length() > 1) {
                        // nếu user paste hoặc nhập nhanh → giữ lại ký tự cuối
                        s.replace(0, s.length(), s.subSequence(s.length() - 1, s.length()));
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

            });

            pinFields[index].setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                        // Nếu ô hiện tại trống → nhảy về ô trước
                        if (pinFields[index].getText().length() == 0 && index > 0) {
                            pinFields[index - 1].setText("");  // ❗ clear luôn ô trước
                            pinFields[index - 1].requestFocus();
                            return true;
                        }
                    }
                    return false;
                }
            });
        }

        binding.et1PINTxn.requestFocus();
    }

    private void setUpEvents(){

        binding.btnVerifyPINTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPinValid()){
                    binding.txtErrorPIN.setVisibility(View.VISIBLE);
                    binding.txtErrorPIN.setText("Vui lòng nhập đủ mã PIN");
                    return;
                }

                StringBuilder pin = new StringBuilder();

                pin.append(binding.et1PINTxn.getText().toString());
                pin.append(binding.et2PINTxn.getText().toString());
                pin.append(binding.et3PINTxn.getText().toString());
                pin.append(binding.et4PINTxn.getText().toString());
                pin.append(binding.et5PINTxn.getText().toString());
                pin.append(binding.et6PINTxn.getText().toString());

                if (pin.length() < 6){
                    binding.txtErrorPIN.setVisibility(View.VISIBLE);
                    return;
                }

                binding.txtErrorPIN.setVisibility(View.GONE);

                listener.onPinSubmit(pin.toString());
            }
        });

        binding.imbtPINCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPinCancel();
            }
        });
    }

    public void showPinError(String message) {
        if (binding != null) {
            binding.txtErrorPIN.setText(message);
            binding.txtErrorPIN.setVisibility(View.VISIBLE);
        }
    }


    // check PIN input
    private String getPin() {
        StringBuilder pin = new StringBuilder();
        for (EditText et : pinFields) pin.append(et.getText());
        return pin.toString();
    }

    private boolean isPinValid() {
        return getPin().length() == 6;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}