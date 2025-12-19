package com.example.tad_bank_t1.ui.base;

public interface UiConfig {
    default boolean showAppBar() { return true; }
    default String getAppBarTitle() { return ""; }

    default boolean showBottomNav() { return false; }


}
