package com.example.tad_bank_t1.util;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tad_bank_t1.R;

public class FragmentUtil {
    public static void replaceFragment(Fragment fragment,
                                       FragmentManager fragmentManager,
                                       int containerId){
        replaceFragment(fragment, fragmentManager, containerId, true);
    }

    public static void replaceFragment(Fragment fragment,
                                       FragmentManager fragmentManager,
                                       int containerId,
                                       boolean mustAddBackStack){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerId, fragment);
        if(mustAddBackStack){
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    public static void destroyFragment(Fragment fragment, FragmentManager fragmentManager){

        // Kiểm tra xem Fragment hiện tại có được thêm vào Back Stack không
        if (fragmentManager.getBackStackEntryCount() > 0) {
            // Nếu có, chỉ cần pop back stack
            fragmentManager.popBackStack();
        } else {
            // Nếu không, sử dụng FragmentTransaction để gỡ bỏ Fragment
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.remove(fragment); // `this` ở đây là Fragment hiện tại
            transaction.commit();
        }
    }
}
