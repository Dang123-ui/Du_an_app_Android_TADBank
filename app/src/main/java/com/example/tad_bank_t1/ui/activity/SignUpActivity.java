package com.example.tad_bank_t1.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.ui.fragment.CCCDVerifyFragment;
import com.example.tad_bank_t1.ui.fragment.Congratulation_Fragment;
import com.example.tad_bank_t1.ui.fragment.CreatePinCodeFragment;
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
        if (savedInstanceState == null) {
            navigateTo(new InfoSignUpFragment(), false);
        }
        btnImageView = findViewById(R.id.btn_SignUp_to_Sigin);
        tvTextView = findViewById(R.id.tvSignupToSignUp);
        btnImageView.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
        });
        tvTextView.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
    public void goToPhoneVerify(String phone) {
        navigateTo(new InfoSignUpFragment(), true);
    }
    public void goToCCCDVerify() {
        navigateTo(new CCCDVerifyFragment(), true);
    }
}