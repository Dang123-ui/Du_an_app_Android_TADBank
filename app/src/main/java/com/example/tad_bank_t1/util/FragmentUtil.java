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
}
