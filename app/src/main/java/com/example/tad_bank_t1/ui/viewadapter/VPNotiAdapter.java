package com.example.tad_bank_t1.ui.viewadapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tad_bank_t1.ui.fragment.BalanceAlertFragment;
import com.example.tad_bank_t1.ui.fragment.NotiItemFragment;

import java.util.ArrayList;
import java.util.List;

public class VPNotiAdapter extends FragmentStateAdapter {
    private List<Fragment> fragments = List.of(new BalanceAlertFragment(), new NotiItemFragment());

    public VPNotiAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public VPNotiAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public VPNotiAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
}
