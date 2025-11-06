package com.example.tad_bank_t1.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.ArcMotion;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Fade;
import android.transition.TransitionSet;
import android.util.Patterns;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.User;
import com.example.tad_bank_t1.data.repository.users.FirebaseUserRepository;
import com.example.tad_bank_t1.data.repository.users.UserRepository;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity2 extends AppCompatActivity {
    public static final String EXTRA_UID = "extra_userid";
    private ImageView logoLogin2;
    private TextView tvName;
    private TextInputLayout tilPassword, tilEmailorPhone;
    private TextInputEditText etPassword, etEmailorPhone;
    private Button btnLogin2;
    private ImageButton btnFaceId;
    private TextView tvForgotPassword;
    private LottieAnimationView animationView;

    private boolean isLoading = false;
    private String uid;
    private final UserRepository userRepo = new FirebaseUserRepository();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TransitionSet shared = new TransitionSet()
                    .addTransition(new ChangeBounds())
                    .addTransition(new ChangeTransform())
                    .addTransition(new ChangeImageTransform());
            shared.setDuration(1500);
            getWindow().setSharedElementEnterTransition(shared);
            getWindow().setSharedElementReturnTransition(shared);
            getWindow().setEnterTransition(null);
            getWindow().setExitTransition(null);
        }
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login2);
        tvName = findViewById(R.id.tvName);
        logoLogin2 = findViewById(R.id.logoLogin2);
        etPassword = findViewById(R.id.etPassword);
        etEmailorPhone = findViewById(R.id.edtNumberAccount);
        tilPassword = findViewById(R.id.textInputLayout);
        tilEmailorPhone = findViewById(R.id.tlUsername);
        btnLogin2 = findViewById(R.id.btnLogin2);
        animationView = findViewById(R.id.loading);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnFaceId = findViewById(R.id.btnFaceId);
        ViewCompat.setTransitionName(logoLogin2, "app_logo");
        logoLogin2.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                logoLogin2.getViewTreeObserver().removeOnPreDrawListener(this);
                supportStartPostponedEnterTransition();
                return true;
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login2), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        uid = getIntent().getStringExtra(EXTRA_UID);
        if (TextUtils.isEmpty(uid)) {
            tilEmailorPhone.setVisibility(View.VISIBLE);
            etEmailorPhone.setVisibility(View.VISIBLE);
            tvName.setVisibility(View.INVISIBLE);
            tvForgotPassword.setOnClickListener(v -> {
                if (isLoading) return;
                String identifier = safeText(etEmailorPhone);
                if (TextUtils.isEmpty(identifier)) {
                    tilEmailorPhone.setError("Vui lòng nhập Email hoặc SĐT");
                    return;
                }
                tilEmailorPhone.setError(null);
                setLoading(true);
                resolveUserByIdentifier(identifier).addOnSuccessListener(user -> {
                    setLoading(false);
                    if (user == null) {
                        tilEmailorPhone.setError("Không tìm thấy tài khoản");
                        return;
                    }
                    String uidFound = user.getUserId();
                    Intent intent = SignUpActivity.intentForForgotPassword(this, uidFound);
                    startActivity(intent);
                }).addOnFailureListener(e -> {
                    setLoading(false);
                    tilEmailorPhone.setError("Lỗi tra tài khoản: " + e.getMessage());
                });
            });
        } else {
            tilEmailorPhone.setVisibility(View.INVISIBLE);
            etEmailorPhone.setVisibility(View.INVISIBLE);
            userRepo.getById(uid).addOnSuccessListener(user -> {
                if (user != null) {
                    tvName.setVisibility(View.VISIBLE);
                    tvName.setText(user.getFullName());
                } else {
                    tvName.setVisibility(View.INVISIBLE);
                }
            }).addOnFailureListener(e -> tvName.setVisibility(View.INVISIBLE));

            tvForgotPassword.setOnClickListener(v -> {
                Intent intent = SignUpActivity.intentForForgotPassword(this, uid);
                startActivity(intent);
            });
        }
        etPassword.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilPassword.setError(null);
                if (TextUtils.isEmpty(uid)) {
                    btnLogin2.setEnabled(!TextUtils.isEmpty(safeText(etEmailorPhone)) &&
                            !TextUtils.isEmpty(s) &&
                            !isLoading);
                } else {
                    btnLogin2.setEnabled(!TextUtils.isEmpty(s) && !isLoading);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnLogin2.setOnClickListener(v -> {
            if (isLoading) return;
            String pass = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
            if (TextUtils.isEmpty(pass)) {
                tilPassword.setError("Mật khẩu không được để trống");
                return;
            }
            if(TextUtils.isEmpty(uid)){
                String identifier = safeText(etEmailorPhone);
                if (TextUtils.isEmpty(identifier)) {
                    tilEmailorPhone.setError("Vui lòng nhập Email hoặc SĐT");
                    return;
                }
                tilEmailorPhone.setError(null);
                setLoading(true);
                resolveUserByIdentifier(identifier).addOnSuccessListener(user -> {
                    if (user == null) {
                        setLoading(false);
                        tilEmailorPhone.setError("Không tìm thấy tài khoản");
                        return;
                    }
                    uid = user.getUserId();
                    checkPasswordThenLogin(user, pass);
                }).addOnFailureListener(e -> {
                    setLoading(false);
                    tilEmailorPhone.setError("Lỗi tra tài khoản: " + e.getMessage());
                });
            }else{
                setLoading(true);
                userRepo.getById(uid).addOnSuccessListener(user -> {
                    if (user == null) {
                        setLoading(false);
                        tilPassword.setError("Tài khoản không tồn tại");
                        return;
                    }
                    checkPasswordThenLogin(user, pass);
                }).addOnFailureListener(e -> {
                    setLoading(false);
                    tilPassword.setError("Có lỗi khi kiểm tra. Thử lại.");
                });
            }
        });
        btnFaceId.setOnClickListener(v -> {
            if(isLoading) return;
            if (!TextUtils.isEmpty(uid)) {
                startActivity(SignUpActivity.intentForFaceLogin(this, uid));
                return;
            }
            String identifier = safeText(etEmailorPhone);
            if (TextUtils.isEmpty(identifier)) {
                tilEmailorPhone.setError("Vui lòng nhập Email hoặc SĐT trước khi dùng Face ID");
                return;
            }
            tilEmailorPhone.setError(null);
            setLoading(true);
            resolveUserByIdentifier(identifier).addOnSuccessListener(user -> {
                setLoading(false);
                if(user == null){
                    tilEmailorPhone.setError("Không tìm thấy tài khoản");
                    return;
                }
                String uidFound = user.getUserId();
                startActivity(SignUpActivity.intentForFaceLogin(this, uidFound));
            }).addOnFailureListener(e -> {
                setLoading(false);
                tilEmailorPhone.setError("Lỗi tra tài khoản: " + e.getMessage());
            });
        });
    }
    private void checkPasswordThenLogin(User user, String inputPass) {
        String savedPass = user.getPassword();
        if (TextUtils.equals(inputPass, savedPass)) {
            onLoginSuccess(user.getUserId());
        } else {
            setLoading(false);
            tilPassword.setError("Mật khẩu không đúng");
        }
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }
    private void onLoginSuccess(String uid){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_USERID, uid);
        startActivity(intent);
        finish();
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        btnLogin2.setEnabled(!loading && !TextUtils.isEmpty(etPassword.getText()));
        etPassword.setEnabled(!loading);
        tilPassword.setEnabled(!loading);
        animationView.setVisibility(loading ? View.VISIBLE : View.INVISIBLE);
    }
    private static String safeText(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
    private Task<User> resolveUserByIdentifier(String identifier) {
        UserRepository repo = new FirebaseUserRepository();
        if (TextUtils.isEmpty(identifier)) {
            return Tasks.forException(new IllegalArgumentException("Vui lòng nhập Email hoặc SĐT"));
        }
        boolean isPhone = !TextUtils.isEmpty(identifier) && identifier.matches("0\\d{9}");
        boolean isEmail = Patterns.EMAIL_ADDRESS.matcher(identifier).matches();
        if (isEmail) {
            return repo.findByEmail(identifier);
        } else if(isPhone){
            return repo.findByPhone(identifier);
        }
        return Tasks.forException(new IllegalArgumentException("Định dạng không hợp lệ"));
    }
}