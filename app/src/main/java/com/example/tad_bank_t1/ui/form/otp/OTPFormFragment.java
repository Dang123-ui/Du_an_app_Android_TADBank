package com.example.tad_bank_t1.ui.form.otp;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.databinding.FragmentOTPFormBinding;
import com.example.tad_bank_t1.util.TadConstants;

public class OTPFormFragment extends DialogFragment {
    private OnOtpSubmitListener listener;

    // binding
    private FragmentOTPFormBinding binding;

    EditText[] OTPFields;

    private static final long OTP_TIMEOUT = 60_000; // 60s
    private CountDownTimer countDownTimer;


    private boolean verified = false;

    public OTPFormFragment() {}

    public interface OnOtpSubmitListener {
        void onOTPSubmit(String otp);
        void onOTPCancel();
        void onOTPInvalid(String message);
        void onOTPResend();
    }


    public OTPFormFragment(OnOtpSubmitListener listener) {
        this.listener = listener;
    }

    /** ✅ Gọi trước khi dismiss khi OTP đúng */
    public void setVerified(boolean verified) {
        this.verified = verified;
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

        startOtpCountdown();

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
                    binding.txtErrorOTP.setText(R.string.otp_khong_hop_le);
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

        binding.imbtOTPCancel.setOnClickListener(v -> {
            // ✅ user bấm cancel → đóng dialog (onDismiss sẽ tự gọi onOTPCancel nếu chưa verified)
            dismiss();
        });

        binding.txtResendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.txtResendOTP.isEnabled()) {
                    resendOtp();
                    startOtpCountdown(); // reset lại
                }
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


    // bắt đầu đếm ngược
    private void startOtpCountdown() {
        binding.txtResendOTP.setEnabled(false);
        binding.txtResendOTP.setTextColor(
                requireContext().getColor(R.color.bgBlur)
        );

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(TadConstants.OTP_TTL_MS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                int minute = (int) (seconds / 60);
                int secondMod = (int) (seconds % 60);


                binding.txtResendOTP.setText(
                        getString(R.string.gui_lai_otp_count_dowwn, minute, secondMod)
                );
            }

            @Override
            public void onFinish() {
                binding.txtResendOTP.setEnabled(true);
                binding.txtResendOTP.setText(R.string.gui_lai_otp);
                binding.txtResendOTP.setTextColor(
                        requireContext().getColor(R.color.primaryColor)
                );
            }
        }.start();
    }


    // gửi lại OTP
    private void resendOtp() {
        // gọi ViewModel / Repository resend OTP
        listener.onOTPResend();

        Toast.makeText(requireContext(),
                "OTP đã được gửi lại",
                Toast.LENGTH_SHORT).show();
    }

    // ✅ BẮT BACK / OUTSIDE / X (cancel event)
    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        // onDismiss cũng sẽ chạy, nên chỉ cần để onDismiss xử lý 1 chỗ
    }

    // ✅ CHỖ DUY NHẤT để quyết định có gọi onOTPCancel hay không
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (!verified && listener != null) {
            listener.onOTPCancel(); // ✅ mọi kiểu đóng khi chưa verified đều là cancel giao dịch
        }
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
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}