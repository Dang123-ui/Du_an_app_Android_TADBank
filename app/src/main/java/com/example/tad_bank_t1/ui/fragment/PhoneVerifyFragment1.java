package com.example.tad_bank_t1.ui.fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.OtpCode;
import com.example.tad_bank_t1.data.repository.otp.FirebaseOtpCodeRepository;
import com.example.tad_bank_t1.data.repository.otp.OtpCodeRepository;
import com.example.tad_bank_t1.data.repository.users.FirebaseUserRepository;
import com.example.tad_bank_t1.data.repository.users.UserRepository;
import com.example.tad_bank_t1.ui.activity.SignUpActivity;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class PhoneVerifyFragment1 extends Fragment {

    private static final String ARG_UID = "key_uid";
    private String uid;
    public PhoneVerifyFragment1() {}

    public static PhoneVerifyFragment1 newInstance(String uid) {
        PhoneVerifyFragment1 f = new PhoneVerifyFragment1();
        Bundle b = new Bundle();
        b.putString(ARG_UID, uid);
        f.setArguments(b);
        return f;
    }

    // --- UI ---
    private String username;
    private String email;
    private String phone;
    private TextView tvSDTVerify;
    private TextView tvSendAgainOTP;
    private Button   btnNextToCCCDVerify;
    private EditText et1, et2, et3, et4, et5, et6;
    private OtpCodeRepository otpRepository;
    private UserRepository userRepository;

    // --- Timers ---
    private CountDownTimer resendTimer;
    private CountDownTimer otpValidityTimer;
    private static final long RESEND_COOLDOWN_MS = 30_000L; // 30s
    private static final long OTP_VALIDITY_MS    = 60_000L; // 60s
    private final Handler handler = new Handler(Looper.getMainLooper());

    // Watchdog: nếu kẹt trạng thái "Đang xác minh..."
    private final Runnable watchdog = () -> {
        if (btnNextToCCCDVerify != null && "Đang xác minh...".contentEquals(btnNextToCCCDVerify.getText())) {
            Toast.makeText(requireContext(), "Không nhận được phản hồi xác minh. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            resetVerifyUI();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            uid = args.getString(ARG_UID);
        }
        userRepository = new FirebaseUserRepository();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_phone_verify1, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvSDTVerify          = view.findViewById(R.id.tvSDTVerify);
        tvSendAgainOTP       = view.findViewById(R.id.tvSendAgainOTP);
        btnNextToCCCDVerify  = view.findViewById(R.id.btnNextToCCCDVerify);
        et1 = view.findViewById(R.id.et1);
        et2 = view.findViewById(R.id.et2);
        et3 = view.findViewById(R.id.et3);
        et4 = view.findViewById(R.id.et4);
        et5 = view.findViewById(R.id.et5);
        et6 = view.findViewById(R.id.et6);
        setupOtpInputs();

        // UI init
        disableInputs(true);
        btnNextToCCCDVerify.setEnabled(false);
        btnNextToCCCDVerify.setText("Xác nhận");
        tvSendAgainOTP.setEnabled(false);
        tvSendAgainOTP.setText("Gửi lại mã OTP");
        // Gửi OTP ngay (theo luồng test hoặc thật)
        loadUserThenSendOtp();

        tvSendAgainOTP.setOnClickListener(v -> {
            if (tvSendAgainOTP.isEnabled()) sendOTP();
        });
        btnNextToCCCDVerify.setOnClickListener(v -> verifyCode());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (resendTimer != null) resendTimer.cancel();
        if (otpValidityTimer != null) otpValidityTimer.cancel();
        handler.removeCallbacks(watchdog);
    }
    private void sendOTP() {
        // UI
        disableInputs(false);
        btnNextToCCCDVerify.setEnabled(false);
        btnNextToCCCDVerify.setText("Đang gửi...");
        clearOtpInputs();

        if (phone == null || phone.trim().isEmpty()) {
            Toast.makeText(requireContext(), "Không tìm thấy số điện thoại để gửi OTP.", Toast.LENGTH_SHORT).show();
            resetVerifyUI();
            return;
        }
        String newTestOtp = generateRandomOtp6();
        ensureRepo();
        OtpCode otp = new OtpCode();
        otp.setUserId(uid);
        otp.setCode(newTestOtp);
        otp.setPurpose("LOGIN");
        otp.setExpiresAt(new Date(System.currentTimeMillis() + OTP_VALIDITY_MS));
        otp.setUsedAt(null);
        otpRepository.create(otp);
        startResendCountdown();
        startOtpValidityTimer(OTP_VALIDITY_MS);
        handler.postDelayed(() -> {
            if (isAdded()) {
                fillOtp(newTestOtp);
                btnNextToCCCDVerify.setEnabled(true);
                et6.requestFocus();
                Toast.makeText(requireContext(), "Mã OTP đã được tự động điền: " + newTestOtp, Toast.LENGTH_SHORT).show();
            }
        }, 2400);
    }
    private void verifyCode() {
        String code = readOtp().trim();
        if (code.length() != 6) {
            Toast.makeText(requireContext(), "Vui lòng nhập đủ 6 số OTP", Toast.LENGTH_SHORT).show();
            return;
        }
        btnNextToCCCDVerify.setEnabled(false);
        btnNextToCCCDVerify.setText("Đang xác minh...");
        disableInputs(true);
        ensureRepo();
        otpRepository.verifyAndConsume(uid, "LOGIN", code).addOnSuccessListener(valid -> {
            if (Boolean.TRUE.equals(valid)) {
                navigateToCreatPin(uid);
            }else{
                Toast.makeText(requireContext(),
                        "Mã OTP không hợp lệ hoặc đã hết hạn. Vui lòng gửi lại.",
                        Toast.LENGTH_SHORT).show();
                resetVerifyUI();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(requireContext(),
                    "Lỗi xác minh OTP: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            resetVerifyUI();
        });
    }
    private void resetVerifyUI() {
        btnNextToCCCDVerify.setText("Xác nhận");
        disableInputs(false);
        btnNextToCCCDVerify.setEnabled(readOtp().length() == 6);
    }
    private void ensureRepo() {
        if (otpRepository == null) otpRepository = new FirebaseOtpCodeRepository();
    }
    private void navigateToCreatPin(String uid) {
        if (resendTimer != null) resendTimer.cancel();
        if (otpValidityTimer != null) otpValidityTimer.cancel();

        Toast.makeText(requireContext(), "Xác thực số điện thoại thành công!", Toast.LENGTH_SHORT).show();
        btnNextToCCCDVerify.setEnabled(true);
        btnNextToCCCDVerify.setText("Tiếp tục");
        CreatePinCodeFragment createPinCodeFragment = CreatePinCodeFragment.newInstance(uid);
        if (getActivity() instanceof SignUpActivity) {
            ((SignUpActivity) getActivity()).navigateTo(createPinCodeFragment, false);
        }
    }

    private void setupOtpInputs() {
        TextWatcher tw = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                if (s.length() == 1) focusNext();
                btnNextToCCCDVerify.setEnabled(readOtp().length() == 6);
            }
        };
        et1.addTextChangedListener(tw);
        et2.addTextChangedListener(tw);
        et3.addTextChangedListener(tw);
        et4.addTextChangedListener(tw);
        et5.addTextChangedListener(tw);
        et6.addTextChangedListener(tw);
    }

    private void startResendCountdown() {
        tvSendAgainOTP.setEnabled(false);
        if (resendTimer != null) resendTimer.cancel();
        resendTimer = new CountDownTimer(RESEND_COOLDOWN_MS, 1000) {
            @Override public void onTick(long ms) {
                long s = ms / 1000;
                tvSendAgainOTP.setText(String.format(Locale.getDefault(), "Gửi lại mã OTP (%ds)", s));
            }
            @Override public void onFinish() {
                tvSendAgainOTP.setEnabled(true);
                tvSendAgainOTP.setText("Gửi lại mã OTP");
                resendTimer = null;
            }
        }.start();
    }

    private void startOtpValidityTimer(long duration) {
        if (otpValidityTimer != null) otpValidityTimer.cancel();
        otpValidityTimer = new CountDownTimer(duration, 1000L) {
            @Override public void onTick(long millisUntilFinished) {
                long s = millisUntilFinished / 1000;
                btnNextToCCCDVerify.setText(String.format(Locale.getDefault(), "Xác nhận (%ds)", s));
            }
            @Override public void onFinish() {
                btnNextToCCCDVerify.setText("Xác nhận");
                Toast.makeText(requireContext(), "Mã OTP đã hết hạn. Vui lòng gửi lại.", Toast.LENGTH_SHORT).show();
                disableInputs(false);
                btnNextToCCCDVerify.setEnabled(false);
                otpValidityTimer = null;
            }
        }.start();
    }

    private void disableInputs(boolean disable) {
        et1.setEnabled(!disable);
        et2.setEnabled(!disable);
        et3.setEnabled(!disable);
        et4.setEnabled(!disable);
        et5.setEnabled(!disable);
        et6.setEnabled(!disable);
    }

    private void clearOtpInputs() {
        et1.setText("");
        et2.setText("");
        et3.setText("");
        et4.setText("");
        et5.setText("");
        et6.setText("");
    }

    private void fillOtp(String code) {
        if (code == null || code.length() < 6) return;
        et1.setText(code.substring(0, 1));
        et2.setText(code.substring(1, 2));
        et3.setText(code.substring(2, 3));
        et4.setText(code.substring(3, 4));
        et5.setText(code.substring(4, 5));
        et6.setText(code.substring(5, 6));
    }

    private void focusNext() {
        if (et1.getText().length() == 0) { et1.requestFocus(); return; }
        if (et2.getText().length() == 0) { et2.requestFocus(); return; }
        if (et3.getText().length() == 0) { et3.requestFocus(); return; }
        if (et4.getText().length() == 0) { et4.requestFocus(); return; }
        if (et5.getText().length() == 0) { et5.requestFocus(); return; }
        et6.requestFocus();
    }

    private String readOtp() {
        return (et1.getText().toString()
                + et2.getText().toString()
                + et3.getText().toString()
                + et4.getText().toString()
                + et5.getText().toString()
                + et6.getText().toString()).trim();
    }

    private String maskPhone(String phone) {
        if (phone != null && phone.length() >= 3) {
            String lastThree = phone.substring(phone.length() - 3);
            return "*******" + lastThree;
        } else return String.valueOf(phone);
    }

    // 0xxxxxxxxx -> +84xxxxxxxxx
    private String normalizePhoneVN(String raw) {
        if (raw == null) return "";
        String s = raw.trim().replace(" ", "");
        if (s.startsWith("+84")) return s;
        if (s.startsWith("0") && s.length() == 10) return "+84" + s.substring(1);
        return s;
    }
    private String generateRandomOtp6() {
        int val = ThreadLocalRandom.current().nextInt(0, 1_000_000);
        return String.format(Locale.getDefault(), "%06d", val);
    }
    private void loadUserThenSendOtp() {
        // If the repository is not available, attempt to use any previously loaded phone number.
        if (userRepository == null) {
            if (phone != null && !phone.trim().isEmpty()) {
                tvSDTVerify.setText(maskPhone(phone));
                sendOTP();
            } else {
                Toast.makeText(requireContext(), "Không tìm thấy số điện thoại để gửi OTP.", Toast.LENGTH_LONG).show();
            }
            return;
        }
        // Perform asynchronous lookup of the user by uid via the repository.
        userRepository.getById(uid)
                .addOnSuccessListener(user -> {
                    // Extract phone, username and email from the user model if available.
                    if (user != null) {
                        try {
                            phone    = user.getPhone();
                            username = user.getUsername();
                            email    = user.getEmail();
                        } catch (Exception ignored) {
                            // Ignore any getter exceptions and proceed with whatever fields were loaded.
                        }
                    }
                    // If we now have a phone number, update the UI and send the OTP. Otherwise, inform the user.
                    if (phone != null && !phone.trim().isEmpty()) {
                        tvSDTVerify.setText(maskPhone(phone));
                        sendOTP();
                    } else {
                        Toast.makeText(requireContext(), "Không tìm thấy số điện thoại để gửi OTP.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // If there was an error retrieving the user, inform the user and do not proceed.
                    Toast.makeText(requireContext(), "Lỗi khi tải thông tin người dùng: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
