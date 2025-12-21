package com.royrodriguez.transitionbutton;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.ViewCompat;

import android.os.Looper;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.content.res.Resources;

/**
 * Simplified TransitionButton:
 * - KHÔNG thay đổi background / backgroundTint
 * - Chỉ lo animation width + loader tròn + đổi màu CHỮ khi showErrorMessage
 * => background / selector từ XML sẽ được giữ nguyên.
 */
public class TransitionButton extends AppCompatButton {

    private final int WIDTH_ANIMATION_DURATION = 200;
    private final int SCALE_ANIMATION_DURATION = 600;
    private final int SHAKE_ANIMATION_DURATION = 500;
    private final int COLOR_ANIMATION_DURATION = 350;
    private int messageAnimationDuration = COLOR_ANIMATION_DURATION * 10;

    private State currentState;

    private boolean isMorphingInProgress;

    private int initialWidth;
    private int initialHeight;
    private String initialText;

    // màu chỉ dùng nội bộ, KHÔNG dùng để tô background
    private int defaultTextColor;
    private int errorTextColor;
    private int loaderColor;

    public TransitionButton(Context context) {
        super(context);
        init(context, null);
    }

    public TransitionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TransitionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        currentState = State.IDLE;
        // Lấy màu chữ mặc định từ theme hoặc XML
        defaultTextColor = getCurrentTextColor();
        // Mặc định màu lỗi và màu vòng tròn loader
        errorTextColor  = ContextCompat.getColor(context, android.R.color.holo_red_dark);
    }

    public void startAnimation() {
        currentState = State.PROGRESS;
        isMorphingInProgress = true;
        initialWidth = getWidth();
        initialHeight = getHeight();
        initialText = getText().toString();
        setText(null);
        setClickable(false);

        startWidthAnimation(initialHeight, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationCancel(animation);
                isMorphingInProgress = false;
            }
        });
    }

    public void setMessageAnimationDuration(int messageAnimationDuration) {
        this.messageAnimationDuration = messageAnimationDuration;
    }
    public void stopAnimation(StopAnimationStyle stopAnimationStyle, final OnAnimationStopEndListener onAnimationStopEndListener) {
        switch (stopAnimationStyle) {
            case SHAKE:
                currentState = State.ERROR;

                startWidthAnimation(initialWidth, new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        setText(initialText);
                        startShakeAnimation(new AnimationListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                currentState = State.IDLE;
                                setClickable(true);
                                if (onAnimationStopEndListener != null)
                                    onAnimationStopEndListener.onAnimationStopEnd();
                            }
                        });
                    }
                });
                break;
            case EXPAND:
                currentState = State.TRANSITION;

                startScaleAnimation(new AnimationListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        super.onAnimationEnd(animation);
                        if (onAnimationStopEndListener != null)
                            onAnimationStopEndListener.onAnimationStopEnd();
                    }
                });
                break;
        }
    }

    private void startWidthAnimation(int to, AnimatorListenerAdapter onAnimationEnd) {
        startWidthAnimation(getWidth(), to, onAnimationEnd);
    }

    private void startWidthAnimation(int from, int to, AnimatorListenerAdapter onAnimationEnd) {
        ValueAnimator widthAnimation = ValueAnimator.ofInt(from, to);
        widthAnimation.addUpdateListener(valueAnimator -> {
            int val = (Integer) valueAnimator.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.width = val;
            setLayoutParams(layoutParams);
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(WIDTH_ANIMATION_DURATION);
        animatorSet.playTogether(widthAnimation);
        if (onAnimationEnd != null)
            animatorSet.addListener(onAnimationEnd);

        animatorSet.start();
    }

    private void startShakeAnimation(Animation.AnimationListener animationListener) {
        TranslateAnimation shake = new TranslateAnimation(0, 15, 0, 0);
        shake.setDuration(SHAKE_ANIMATION_DURATION);
        shake.setInterpolator(new CycleInterpolator(4));
        shake.setAnimationListener(animationListener);
        startAnimation(shake);
    }

    private void startScaleAnimation(Animation.AnimationListener animationListener) {
        float screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        float ts = (float) (screenHeight / (float) getHeight() * 2.1);
        Animation anim = new ScaleAnimation(1f, ts,
                1f, ts,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(SCALE_ANIMATION_DURATION);
        anim.setFillAfter(true);
        anim.setAnimationListener(animationListener);
        startAnimation(anim);
    }

    public void showErrorMessage(String message) {
        setText(message);
        setClickable(false);
        startColorAnimation(defaultTextColor, errorTextColor);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                setText(initialText);
                setClickable(true);
                startColorAnimation(errorTextColor, defaultTextColor);
            }
        }, messageAnimationDuration);
    }

    /**
     * Chỉ đổi MÀU CHỮ, không đổi backgroundTint để không phá selector.
     */
    private void startColorAnimation(int from, int to) {
        ValueAnimator anim = ValueAnimator.ofArgb(from, to);
        anim.addUpdateListener(valueAnimator -> {
            int color = (Integer) valueAnimator.getAnimatedValue();
            setTextColor(color);
        });
        anim.setDuration(COLOR_ANIMATION_DURATION);
        anim.start();
    }

    public class AnimationListenerAdapter implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) { }

        @Override
        public void onAnimationEnd(Animation animation) { }

        @Override
        public void onAnimationRepeat(Animation animation) { }

    }

    public interface OnAnimationStopEndListener {
        void onAnimationStopEnd();
    }

    private enum State {
        PROGRESS, IDLE, ERROR, TRANSITION
    }

    public enum StopAnimationStyle {
        EXPAND, SHAKE
    }

}
