package com.example.tad_bank_t1.ui.base;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public interface BaseCustomFragment {
    void initView();

    void initViewModel();

    void setUpEvents();

    default void initFragment() {
        initView();
        initViewModel();
        setUpEvents();
    }

     // modal loi
     default void showError(Context context, String title, String message) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true) // người dùng có thể bấm ra ngoài để tắt
                .setPositiveButton("OK", (d, w) -> {
                    d.dismiss();
                })
                .show();
    }

    default void toggleLoading(boolean isLoading){

    }
}
