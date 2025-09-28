package com.example.tad_bank_t1.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.ui.activity.MainActivity;
import com.example.tad_bank_t1.util.FragmentUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HeaderActionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HeaderActionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "title";

    private ImageButton imbtBackPrevious, imbtHomeBack;

    private String fragment_action_title;
    public HeaderActionFragment() {
        // Required empty public constructor
    }

    public static HeaderActionFragment newInstance(String fragment_action_title) {
        HeaderActionFragment fragment = new HeaderActionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, fragment_action_title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fragment_action_title = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_header_action, container, false);

        // Ánh xạ view từ layout fragment
        imbtBackPrevious = view.findViewById(R.id.imbtBackPrevious);
        imbtHomeBack = view.findViewById(R.id.imbtHomeBack);

        // Bắt sự kiện click
        imbtBackPrevious.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else {
                // If the back stack is empty, finish the activity.
                requireActivity().finish();
            }
        });

        imbtHomeBack.setOnClickListener(v -> {
            FragmentUtil.replaceFragment(new HomeCustomerFragment(), getParentFragmentManager(), R.id.frame_main_container);
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Hide the bottom navigation bar when this fragment starts
        Log.d("TAG", "HEADER ACTION TRANSFER onstart");

        ((MainActivity) requireActivity()).setBottomNavigationVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Show the bottom navigation bar when the user leaves this fragment
        Log.d("TAG", "HEADER ACTION TRANSFER onstop");

//        ((MainActivity) requireActivity()).setBottomNavigationVisibility(View.VISIBLE);
    }
}