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
import com.example.tad_bank_t1.ui.activity.SignUpActivity;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.example.tad_bank_t1.data.repository.otp.OtpCodeRepository;
import com.example.tad_bank_t1.data.repository.otp.FirebaseOtpCodeRepository;
import com.example.tad_bank_t1.data.model.OtpCode;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class PhoneVerifyFragment extends Fragment {

    // --- Argument keys ---
    private static final String ARG_PHONE    = "phone";
    private static final String ARG_USERNAME = "username";
    private static final String ARG_EMAIL    = "email";

    // --- Test account (Auth console) ---
    private static final String TEST_PHONE_NUMBER = "+84706613468";


    // --- State ---
    private boolean remoteTestBypass = false;
    public PhoneVerifyFragment() {}

    public static PhoneVerifyFragment newInstance(String email, String phone, String username) {
        PhoneVerifyFragment f = new PhoneVerifyFragment();
        Bundle b = new Bundle();
        b.putString(ARG_USERNAME, username);
        b.putString(ARG_EMAIL, email);
        b.putString(ARG_PHONE, phone);
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

    // --- Firebase ---
    private FirebaseAuth auth;
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private String enteredCode;
    private OtpCodeRepository otpRepository;

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
        if (getArguments() != null) {
            username = getArguments().getString(ARG_USERNAME);
            email    = getArguments().getString(ARG_EMAIL);
            phone    = getArguments().getString(ARG_PHONE);
        }
        auth = FirebaseAuth.getInstance();

        // Xác định luồng test dựa trên số trong Auth console
        String normalized = normalizePhoneVN(phone);
        remoteTestBypass = (normalized != null && normalized.equals(TEST_PHONE_NUMBER));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_phone_verify, container, false);
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

        tvSDTVerify.setText(maskPhone(phone));
        setupOtpInputs();

        // UI init
        disableInputs(true);
        btnNextToCCCDVerify.setEnabled(false);

        // Gửi OTP ngay (theo luồng test hoặc thật)
        sendOTP(false);

        tvSendAgainOTP.setOnClickListener(v -> {
            if (tvSendAgainOTP.isEnabled()) sendOTP(true);
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
    private void sendOTP(boolean isResend) {
        // UI
        disableInputs(false);
        btnNextToCCCDVerify.setEnabled(false);
        btnNextToCCCDVerify.setText("Đang gửi...");
        clearOtpInputs();

        // 1) Luồng test (Auth console: không gửi SMS, auto-fill)
        if (remoteTestBypass) {
            String newTestOtp = generateRandomOtp6();
            String fakeUid = "test-" + normalizePhoneVN(phone);
            ensureRepo();
            OtpCode otp = new OtpCode();
            otp.setUserId(fakeUid);
            otp.setCode(newTestOtp);
            otp.setPurpose("LOGIN");
            otp.setExpiresAt(new Date(System.currentTimeMillis() + OTP_VALIDITY_MS));
            otp.setUsedAt(null);
            otpRepository.create(otp);
            this.verificationId = "TEST_VERIFICATION_ID";
            resendToken = null;
            startResendCountdown();
            startOtpValidityTimer(OTP_VALIDITY_MS);
            handler.postDelayed(() -> {
                if (isAdded()) {
                    fillOtp(newTestOtp);
                    btnNextToCCCDVerify.setEnabled(true);
                    et6.requestFocus();
                    Toast.makeText(requireContext(), "Mã Test OTP đã được tự động điền: "+newTestOtp, Toast.LENGTH_SHORT).show();
                }
            }, 2400);
            return;
        }

        // 2) Luồng Firebase thật
        PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(normalizePhoneVN(phone))
                .setTimeout(OTP_VALIDITY_MS / 1000L, TimeUnit.SECONDS)
                .setActivity(requireActivity())
                .setCallbacks(callbacks);

        if (isResend && resendToken != null) {
            builder.setForceResendingToken(resendToken);
        }
        PhoneAuthProvider.verifyPhoneNumber(builder.build());
        startResendCountdown();
    }

    // Callbacks Firebase Phone Auth
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                    if (otpValidityTimer != null) otpValidityTimer.cancel();
                    String smsCode = credential.getSmsCode();
                    if (smsCode != null) {
                        fillOtp(smsCode);
                        enteredCode = smsCode;
                    }
                    signInWithPhoneCredential(credential);
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(requireContext(), "Gửi OTP thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    if (resendTimer != null) resendTimer.cancel();
                    if (otpValidityTimer != null) otpValidityTimer.cancel();
                    tvSendAgainOTP.setEnabled(true);
                    tvSendAgainOTP.setText("Gửi lại mã OTP");
                    resetVerifyUI();
                }

                @Override
                public void onCodeSent(@NonNull String sentVerificationId,
                                       @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    PhoneVerifyFragment.this.verificationId = sentVerificationId;
                    resendToken = token;
                    startOtpValidityTimer(OTP_VALIDITY_MS);
                    clearOtpInputs();
                    et1.requestFocus();
                    Toast.makeText(requireContext(), "Đã gửi mã OTP tới " + maskPhone(phone), Toast.LENGTH_SHORT).show();
                }
            };

    private void verifyCode() {
        String code = readOtp().trim();
        if (code.length() != 6) {
            Toast.makeText(requireContext(), "Vui lòng nhập đủ 6 số OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        btnNextToCCCDVerify.setEnabled(false);
        btnNextToCCCDVerify.setText("Đang xác minh...");
        disableInputs(true);

        // 1) Luồng test
        if (remoteTestBypass) {
            String fakeUid = "test-" + normalizePhoneVN(phone);
            long now = System.currentTimeMillis();
            ensureRepo();
            otpRepository.verifyAndConsume(fakeUid, "LOGIN", code)
                    .addOnSuccessListener(valid -> {
                        if (Boolean.TRUE.equals(valid)) {
                            enteredCode = code;
                            // Không tạo thêm bản ghi OTP ở đây để tránh trùng; nếu muốn log, bạn có thể gọi saveOtpRecord(...)
                            navigateToCccdFragment(fakeUid, normalizePhoneVN(phone));
                        } else {
                            Toast.makeText(requireContext(),
                                    "Mã OTP không hợp lệ hoặc đã hết hạn. Vui lòng gửi lại.",
                                    Toast.LENGTH_SHORT).show();
                            resetVerifyUI();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(),
                                "Lỗi xác minh OTP (test): " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        resetVerifyUI();
                    });
            return;
        }

        // 2) Luồng thật
        if (verificationId == null) {
            Toast.makeText(requireContext(), "Chưa gửi OTP hoặc OTP đã hết hạn. Hãy gửi lại.", Toast.LENGTH_LONG).show();
            resetVerifyUI();
            return;
        }

        PhoneAuthCredential credential;
        try {
            credential = PhoneAuthProvider.getCredential(verificationId, code);
        } catch (Exception ex) {
            Toast.makeText(requireContext(), "Mã OTP không hợp lệ.", Toast.LENGTH_SHORT).show();
            resetVerifyUI();
            return;
        }

        enteredCode = code;
        handler.removeCallbacks(watchdog);
        handler.postDelayed(watchdog, 8000);
        signInWithPhoneCredential(credential);
    }

    private void signInWithPhoneCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnSuccessListener(result -> {
                    handler.removeCallbacks(watchdog);
                    if (otpValidityTimer != null) otpValidityTimer.cancel();

                    FirebaseUser fUser = result.getUser();
                    if (fUser != null) {
                        String uid         = fUser.getUid();
                        String phoneNumber = fUser.getPhoneNumber();

                        if (enteredCode != null && enteredCode.length() == 6) {
                            saveOtpRecord(uid, enteredCode, "LOGIN");
                        }
                        navigateToCccdFragment(uid, phoneNumber);
                    } else {
                        Toast.makeText(requireContext(), "Lỗi: Không lấy được thông tin người dùng.", Toast.LENGTH_LONG).show();
                        resetVerifyUI();
                    }
                })
                .addOnFailureListener(e -> {
                    handler.removeCallbacks(watchdog);
                    Toast.makeText(requireContext(), "Xác minh thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    resetVerifyUI();
                });
    }

    private void resetVerifyUI() {
        btnNextToCCCDVerify.setText("Xác nhận");
        disableInputs(false);
        btnNextToCCCDVerify.setEnabled(readOtp().length() == 6);
    }

    // --- Helpers ---

    private void ensureRepo() {
        if (otpRepository == null) otpRepository = new FirebaseOtpCodeRepository();
    }

    private void saveOtpRecord(String userId, String code, String purpose) {
        ensureRepo();
        Date expiresAt = new Date(System.currentTimeMillis() + 5 * 60 * 1000);

        OtpCode otp = new OtpCode();
        otp.setUserId(userId);
        otp.setCode(code);
        otp.setPurpose(purpose);
        otp.setExpiresAt(expiresAt);
        otp.setUsedAt(null);
        otpRepository.create(otp);
    }

    private void navigateToCccdFragment(String uid, String phoneNumber) {
        if (resendTimer != null) resendTimer.cancel();
        if (otpValidityTimer != null) otpValidityTimer.cancel();

        Toast.makeText(requireContext(), "Xác thực số điện thoại thành công!", Toast.LENGTH_SHORT).show();
        btnNextToCCCDVerify.setEnabled(true);
        btnNextToCCCDVerify.setText("Tiếp tục");

        Bundle args = new Bundle();
        args.putString("uid", uid);
        args.putString("phone", phoneNumber != null ? phoneNumber : phone);
        args.putString("username", username);
        args.putString("email", email);

        CCCDVerifyFragment fragment = new CCCDVerifyFragment();
        fragment.setArguments(args);
        ((SignUpActivity) requireActivity()).navigateTo(fragment, false);
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
                PhoneVerifyFragment.this.verificationId = null;
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
}
