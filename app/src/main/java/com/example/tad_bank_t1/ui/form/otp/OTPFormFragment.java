package com.example.tad_bank_t1.ui.form.otp;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.tad_bank_t1.databinding.FragmentOTPFormBinding;

public class OTPFormFragment extends DialogFragment {
    private OnOtpSubmitListener listener;

    // binding
    private FragmentOTPFormBinding binding;

    EditText[] OTPFields;


    public OTPFormFragment() {
        // Required empty public constructor
    }

    public interface OnOtpSubmitListener {
        void onOTPSubmit(String otp);
        void onOTPCancel();
        void onOTPInvalid(String message);
    }

    public OTPFormFragment(OnOtpSubmitListener listener) {
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
        binding = FragmentOTPFormBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        OTPFields = new EditText[]{
                binding.et1OTPTxn,
                binding.et2OTPTxn,
                binding.et3OTPTxn,
                binding.et4OTPTxn,
                binding.et5OTPTxn,
                binding.et6OTPTxn
        };

        setUpOTPInput();

        setUpEvents();


    }

    private void setUpOTPInput(){
        for(int i = 0; i < OTPFields.length; i++){
            int index = i;

            OTPFields[index].addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1 && index < OTPFields.length - 1){
                        OTPFields[index + 1].requestFocus();
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

            OTPFields[index].setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                        // Nếu ô hiện tại trống → nhảy về ô trước
                        if (OTPFields[index].getText().length() == 0 && index > 0) {
                            OTPFields[index - 1].setText("");  // ❗ clear luôn ô trước
                            OTPFields[index - 1].requestFocus();
                            return true;
                        }
                    }
                    return false;
                }
            });
        }

        binding.et1OTPTxn.requestFocus();
    }

    private void setUpEvents(){

        binding.btnVerifyOTPTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOTPValid()){
                    binding.txtErrorOTP.setVisibility(View.VISIBLE);
                    binding.txtErrorOTP.setText("Vui lòng nhập đủ mã OTP");
                    return;
                }

                StringBuilder OTP = new StringBuilder();

                OTP.append(binding.et1OTPTxn.getText().toString());
                OTP.append(binding.et2OTPTxn.getText().toString());
                OTP.append(binding.et3OTPTxn.getText().toString());
                OTP.append(binding.et4OTPTxn.getText().toString());
                OTP.append(binding.et5OTPTxn.getText().toString());
                OTP.append(binding.et6OTPTxn.getText().toString());

                if (OTP.length() < 6){
                    binding.txtErrorOTP.setVisibility(View.VISIBLE);
                    return;
                }

                binding.txtErrorOTP.setVisibility(View.GONE);

                listener.onOTPSubmit(OTP.toString());
            }
        });

        binding.imbtOTPCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onOTPCancel();
            }
        });
    }

    public void showOTPError(String message) {
        if (binding != null) {
            binding.txtErrorOTP.setText(message);
            binding.txtErrorOTP.setVisibility(View.VISIBLE);
        }
    }


    // check OTP input
    private String getOTP() {
        StringBuilder OTP = new StringBuilder();
        for (EditText et : OTPFields) OTP.append(et.getText());
        return OTP.toString();
    }

    private boolean isOTPValid() {
        return getOTP().length() == 6;
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