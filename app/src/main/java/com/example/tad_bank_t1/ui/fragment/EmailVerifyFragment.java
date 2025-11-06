package com.example.tad_bank_t1.ui.fragment;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Ekyc;
import com.example.tad_bank_t1.data.model.OtpCode;
import com.example.tad_bank_t1.data.model.User;
import com.example.tad_bank_t1.data.model.enums.Role;
import com.example.tad_bank_t1.data.model.enums.UserStatus;
import com.example.tad_bank_t1.data.repository.otp.FirebaseOtpCodeRepository;
import com.example.tad_bank_t1.data.repository.users.FirebaseUserRepository;
import com.example.tad_bank_t1.ui.activity.SignUpActivity;
import com.example.tad_bank_t1.util.EmailSender;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class EmailVerifyFragment extends Fragment {
    private static final String ARG_FLOW = "key_flow";
    private static final int FLOW_SIGN_UP = 0;
    private static final int FLOW_FORGOT = 1;
    private static final String ARG_EKYC = "key_ekyc";
    private static final String ARG_UID = "key_uid";
    private static final String ARG_PHONE = "key_phone";
    private static final String ARG_USERNAME = "key_username";
    private static final String ARG_EMAIL = "key_email";

    private Ekyc ekyc;
    private String uid, phone, username, email;
    private int flow = FLOW_SIGN_UP;
    private TextView tvEmail, tvOTPAgain;
    private EditText et1, et2, et3, et4, et5, et6;
    private Button btnNext, btnChupLai;
    private FirebaseOtpCodeRepository otpRepo;

    private CountDownTimer verifyTimer;
    private CountDownTimer resendTimer;
    private CountDownTimer otpExpiryTimer;
    private long currentOtpExpiresAtMs = 0L;

    private static final long RESEND_COOLDOWN_MS = 60_000L;
    private static final String OTP_PURPOSE = "verify_email";
    private static final long OTP_TTL_MS = 5 * 60 * 1000;

    public EmailVerifyFragment() {}

    public static EmailVerifyFragment newInstance(Ekyc ekyc, String uid, String phone, String username, String email) {
        EmailVerifyFragment fragment = new EmailVerifyFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_EKYC, ekyc);
        args.putString(ARG_UID, uid);
        args.putString(ARG_PHONE, phone);
        args.putString(ARG_USERNAME, username);
        args.putString(ARG_EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }
    public static EmailVerifyFragment newforForgot(String uid) {
        EmailVerifyFragment fragment = new EmailVerifyFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_FLOW, FLOW_FORGOT);
        args.putString(ARG_UID, uid);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        otpRepo = new FirebaseOtpCodeRepository();
        Bundle args = getArguments();
        if (args != null) {
            flow = args.getInt(ARG_FLOW, FLOW_SIGN_UP);
            uid  = args.getString(ARG_UID);

            if (flow == FLOW_SIGN_UP) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ekyc = args.getSerializable(ARG_EKYC, Ekyc.class);
                } else {
                    Object obj = args.getSerializable(ARG_EKYC);
                    if (obj instanceof Ekyc) ekyc = (Ekyc) obj;
                }
                phone    = args.getString(ARG_PHONE);
                username = args.getString(ARG_USERNAME);
                email    = args.getString(ARG_EMAIL);
            } else {
                // FORGOT: không cần các field dưới, sẽ tự load email theo uid
                ekyc = null; phone = null; username = null; email = null;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_email_verify, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvEmail = view.findViewById(R.id.tvEmail);
        et1 = view.findViewById(R.id.et1);
        et2 = view.findViewById(R.id.et2);
        et3 = view.findViewById(R.id.et3);
        et4 = view.findViewById(R.id.et4);
        et5 = view.findViewById(R.id.et5);
        et6 = view.findViewById(R.id.et6);
        btnNext = view.findViewById(R.id.btnNext);
        btnChupLai = view.findViewById(R.id.btn_ChupLai2);
        tvOTPAgain = view.findViewById(R.id.tvOTPAgain);
        tvEmail.setText(email != null ? email : "");

        setupOtpInputs();
        if (flow == FLOW_FORGOT) {
            // Load email theo uid rồi mới gửi OTP
            if (uid == null || uid.trim().isEmpty()) {
                toast("Thiếu uid cho luồng quên mật khẩu");
                return;
            }
            setLoading(true);
            new FirebaseUserRepository().getById(uid)
                    .addOnSuccessListener(u -> {
                        setLoading(false);
                        if (u == null || u.getEmail() == null || u.getEmail().trim().isEmpty()) {
                            toast("Không tìm thấy email để xác thực. Vui lòng cập nhật email trước.");
                            return;
                        }
                        email = u.getEmail();
                        tvEmail.setText(email);
                        sendOtp();
                    })
                    .addOnFailureListener(e -> {
                        setLoading(false);
                        toast("Lỗi tải thông tin người dùng: " + e.getMessage());
                    });
        } else {
            // SIGN_UP: dùng email đã truyền
            tvEmail.setText(email != null ? email : "");
            sendOtp();
        }

        btnNext.setOnClickListener(v -> {
            String code = collectCode();
            if (code.length() != 6) {
                toast("Nhập đủ 6 số OTP");
                return;
            }
            verifyOtp(code);
        });

        tvOTPAgain.setOnClickListener(v -> {
            if (resendTimer == null) {
                sendOtp();
            } else {
                toast("Vui lòng đợi trước khi gửi lại OTP");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (resendTimer != null) resendTimer.cancel();
        if (verifyTimer != null) verifyTimer.cancel();
        if (otpExpiryTimer != null) otpExpiryTimer.cancel();
    }

    private void setupOtpInputs() {
        EditText[] inputs = {et1, et2, et3, et4, et5, et6};
        for (EditText et : inputs) {
            et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
        }

        for (int i = 0; i < inputs.length; i++) {
            final int idx = i;
            inputs[i].addTextChangedListener(new TextWatcher() {
                @Override public void afterTextChanged(Editable s) {
                    if (s != null && s.length() > 1) {
                        String pasted = s.toString().replaceAll("\\D", "");
                        if (pasted.length() == 6) {
                            fillCode(pasted);
                            inputs[5].requestFocus();
                            return;
                        }
                    }
                    if (s != null && s.length() == 1 && idx < inputs.length - 1) {
                        inputs[idx + 1].requestFocus();
                    }
                }
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            });

            inputs[i].setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == android.view.KeyEvent.ACTION_DOWN &&
                        keyCode == android.view.KeyEvent.KEYCODE_DEL &&
                        inputs[idx].getText().length() == 0 && idx > 0) {
                    inputs[idx - 1].requestFocus();
                    inputs[idx - 1].setText("");
                    return true;
                }
                return false;
            });
        }
    }

    private void fillCode(String code6) {
        if (code6 == null) return;
        EditText[] inputs = new EditText[]{et1, et2, et3, et4, et5, et6};
        for (int i = 0; i < 6 && i < code6.length(); i++) {
            inputs[i].setText(String.valueOf(code6.charAt(i)));
        }
    }

    private String collectCode() {
        StringBuilder sb = new StringBuilder(6);
        sb.append(s(et1)).append(s(et2)).append(s(et3))
                .append(s(et4)).append(s(et5)).append(s(et6));
        return sb.toString();
    }

    private String s(EditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
    private String getOtpPurpose() {
        return (flow == FLOW_FORGOT) ? "forgot_password" : "verify_email";
    }
    private void sendOtp() {
        if (email == null || email.trim().isEmpty()) {
            toast("Email không hợp lệ.");
            return;
        }
        setLoading(true);
        int n = new Random().nextInt(1_000_000);
        final String code = String.format(Locale.US, "%06d", n);
        long now = System.currentTimeMillis();
        Date createdAt = new Date(now);
        Date expiresAt = new Date(now + OTP_TTL_MS);
        currentOtpExpiresAtMs = expiresAt.getTime();

        OtpCode otp = new OtpCode();
        otp.setUserId(uid);
        otp.setPurpose(getOtpPurpose());
        otp.setCode(code);
        otp.setCreatedAt(createdAt);
        otp.setExpiresAt(expiresAt);
        otp.setUsedAt(null);

        otpRepo.create(otp).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override public void onSuccess(String otpId) {
                setLoading(false);
                startResendCooldown();
                startOtpExpiryCountdown(currentOtpExpiresAtMs);
                new Thread(() -> {
                    try{
                        EmailSender.sendEmail(email, code);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> toast("Đã gửi OTP tới email. Vui lòng kiểm tra email."));
                        }
                    }catch (final Exception e){
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> toast("Lỗi gửi email: " + e.getMessage()));
                        }
                    }
                }).start();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override public void onFailure(@NonNull Exception e) {
                setLoading(false);
                toast("Lỗi gửi OTP: " + e.getMessage());
            }
        });
    }
    private void startOtpExpiryCountdown(long expiresAtMs) {
        // Hủy timer cũ nếu có
        if (otpExpiryTimer != null) otpExpiryTimer.cancel();

        long remain = expiresAtMs - System.currentTimeMillis();
        if (remain <= 0) {
            btnNext.setText("OTP đã hết hạn");
            btnNext.setEnabled(false);
            return;
        }

        // Cập nhật ngay lần đầu
        btnNext.setEnabled(true);
        btnNext.setText("Xác thực (" + formatMMSS(remain) + ")");

        otpExpiryTimer = new CountDownTimer(remain, 1000) {
            @Override public void onTick(long millisUntilFinished) {
                btnNext.setText("Xác thực (" + formatMMSS(millisUntilFinished) + ")");
            }

            @Override public void onFinish() {
                btnNext.setText("OTP đã hết hạn");
                btnNext.setEnabled(false);
            }
        }.start();
    }
    private String formatMMSS(long millis) {
        long totalSec = Math.max(0, millis / 1000);
        long mm = totalSec / 60;
        long ss = totalSec % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", mm, ss);
    }

    private void startResendCooldown() {
        tvOTPAgain.setEnabled(false);
        resendTimer = new CountDownTimer(RESEND_COOLDOWN_MS, 1000) {
            @Override public void onFinish() {
                tvOTPAgain.setEnabled(true);
                tvOTPAgain.setText("Gửi lại OTP");
                resendTimer = null;
            }

            @Override public void onTick(long millisUntilFinished) {
                long sec = millisUntilFinished / 1000;
                tvOTPAgain.setText("Gửi lại OTP (" + sec + "s)");
            }
        }.start();
    }

    private void verifyOtp(String code) {
        if (currentOtpExpiresAtMs > 0 && System.currentTimeMillis() >= currentOtpExpiresAtMs) {
            toast("OTP đã hết hạn. Vui lòng gửi lại OTP.");
            return;
        }
        setLoading(true);
        otpRepo.verifyAndConsume(uid, getOtpPurpose(), code)
                .addOnSuccessListener(verified -> {
                    setLoading(false);

                    if (Boolean.TRUE.equals(verified)) {
                        // ĐÃ XÁC THỰC OTP THÀNH CÔNG
                        if (flow == FLOW_FORGOT) {
                            Fragment next = CreatePasswordFragment.newForForgot(uid);
                            if (getActivity() instanceof SignUpActivity) {
                                ((SignUpActivity) getActivity()).navigateTo(next, true);
                            }
                            return; // dừng, KHÔNG chạy code đăng ký bên dưới
                        }

                        // === Nhánh ĐĂNG KÝ: tạo user từ dữ liệu đã có (ekyc, email, ...) ===
                        User user = new User();
                        user.setUserId(uid);
                        user.setEmail(email);
                        user.setPhone(phone);
                        user.setUsername(username);
                        user.setRole(Role.CUSTOMER);
                        user.setStatus(UserStatus.LOCKED);
                        user.setCreatedAt(new Date());
                        user.setAvatar(ekyc.getFaceImagePath());
                        // CHỈ dùng ekyc ở flow đăng ký (ekyc != null)
                        user.setIdNumber(ekyc.getNationalIdNumber());
                        user.setFullName(ekyc.getFullName());
                        user.setAddress(ekyc.getAddress());

                        String dobString = ekyc.getDateOfBirth();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        Date dob = null;
                        try {
                            dob = sdf.parse(dobString);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (dob != null) {
                            user.setDateOfBirth(dob);
                        }

                        FirebaseUserRepository userRepo = new FirebaseUserRepository();
                        userRepo.create(user).addOnSuccessListener(avoid -> {
                            toast("Tạo user thành công");
                            ChoseNumberCardFragment choseNumberCardFragment =
                                    ChoseNumberCardFragment.newInstance(uid, username);
                            if (getActivity() instanceof SignUpActivity) {
                                ((SignUpActivity) getActivity()).navigateTo(choseNumberCardFragment, true);
                            }
                        }).addOnFailureListener(e -> {
                            toast("Thêm user thất bại: " + e.getMessage());
                        });

                    } else {
                        // OTP sai hoặc hết hạn
                        toast("OTP không chính xác hoặc đã hết hạn");
                    }
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    toast("Lỗi xác thực OTP: " + e.getMessage());
                });
    }

    private void setLoading(boolean isLoading) {
        // btnNext sẽ bị vô hiệu hoá riêng khi OTP hết hạn, nên ở đây chỉ disable khi đang xử lý
        btnNext.setEnabled(!isLoading && (currentOtpExpiresAtMs == 0 || System.currentTimeMillis() < currentOtpExpiresAtMs));
        tvOTPAgain.setEnabled(!isLoading && resendTimer == null);
    }
    private void toast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
