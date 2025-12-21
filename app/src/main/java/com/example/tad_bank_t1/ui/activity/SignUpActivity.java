package com.example.tad_bank_t1.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.ui.fragment.CCCDInfoVerifyFragment;
import com.example.tad_bank_t1.ui.fragment.CCCDVerifyFragment;
import com.example.tad_bank_t1.ui.fragment.Congratulation_Fragment;
import com.example.tad_bank_t1.ui.fragment.CreatePinCodeFragment;
import com.example.tad_bank_t1.ui.fragment.EmailVerifyFragment;
import com.example.tad_bank_t1.ui.fragment.FaceVerify1Fragment;
import com.example.tad_bank_t1.ui.fragment.InfoSignUpFragment;

public class SignUpActivity extends AppCompatActivity {
    public void navigateTo(Fragment fragment, boolean addToBackStack) {
        var tx = getSupportFragmentManager().beginTransaction().setCustomAnimations(
                R.anim.windmill_enter,
                R.anim.windmill_exit,
                R.anim.windmill_pop_enter,
                R.anim.windmill_pop_exit
        ).replace(R.id.signup_fragment_container, fragment);
        if (addToBackStack) {
            tx.addToBackStack(null);
        }
        tx.commit();
    }
    private ImageView btnImageView;
    private TextView tvTextView;
    public static final String EXTRA_FLOW = "extra_flow";
    public static final String EXTRA_UID  = "extra_uid";
    public static final int FLOW_SIGN_UP = 0;
    public static final int FLOW_FORGOT_PASSWORD = 1;
    public static final int FLOW_FACE_LOGIN = 2;
    public static Intent intentForForgotPassword(Context ctx, String uid) {
        return new Intent(ctx, SignUpActivity.class)
                .putExtra(EXTRA_FLOW, FLOW_FORGOT_PASSWORD)
                .putExtra(EXTRA_UID, uid);
    }
    public static Intent intentForFaceLogin(Context ctx, String uid) {
        return new Intent(ctx, SignUpActivity.class)
                .putExtra(EXTRA_FLOW, FLOW_FACE_LOGIN)
                .putExtra(EXTRA_UID, uid);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TransitionSet shared = new TransitionSet()
                    .addTransition(new ChangeBounds())
                    .addTransition(new ChangeTransform())
                    .addTransition(new ChangeImageTransform());
            shared.setDuration(500);

            getWindow().setSharedElementEnterTransition(shared);
            getWindow().setSharedElementReturnTransition(shared);
        }
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signup), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnImageView = findViewById(R.id.btn_SignUp_to_Sigin);
        tvTextView   = findViewById(R.id.tvSignupToSignUp);

        // ĐỌC flow từ Intent
        int flow = getIntent().getIntExtra(EXTRA_FLOW, FLOW_SIGN_UP);
        String uid = getIntent().getStringExtra(EXTRA_UID);
        applyFlowUi(flow);
        if (savedInstanceState == null) {
            if (flow == FLOW_FORGOT_PASSWORD) {
                navigateTo(EmailVerifyFragment.newforForgot(uid), false);
            } else if (flow == FLOW_FACE_LOGIN){
                navigateTo(FaceVerify1Fragment.newForFaceLogin(uid), false);
            } else {
                navigateTo(new InfoSignUpFragment(), false);
            }
        }
    }
    private void applyFlowUi(int flow) {
        // Đổi header/title tuỳ theo flow
        if (tvTextView != null) {
            tvTextView.setText(flow == FLOW_FORGOT_PASSWORD
                    ? "Forgot password": flow == FLOW_FACE_LOGIN ? "Face ID" :
                    getString(R.string.signup));
        }
    }
    public void setHeaderBackEnabled(boolean enabled) {
        if(btnImageView != null) btnImageView.setEnabled(enabled);
        if (tvTextView   != null) tvTextView.setEnabled(enabled);
    }
}