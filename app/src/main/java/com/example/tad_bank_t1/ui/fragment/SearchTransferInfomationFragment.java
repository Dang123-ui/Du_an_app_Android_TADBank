package com.example.tad_bank_t1.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.util.Constants;
import com.example.tad_bank_t1.util.FragmentUtil;
import com.google.android.material.textfield.TextInputEditText;

public class SearchTransferInfomationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TITLE = "title_search";
    private static final String HINT_SEARCH = "hint_search";
    private static final String LIST_RESULT_TITLE = "list_result_title";
    private static final String SEARCH_FOR = "search_for";

    private TextView txtSearchTransferInfomationTitle, txtSearchResultListTitle;
    private TextInputEditText edtEnterInfoSearch;
    private ImageButton imbtSearchCancel;
    private ImageButton imbtSearchTransfer;

    // TODO: Rename and change types of parameters
    private String title_search, hint_search, list_result_title, search_for;

    public SearchTransferInfomationFragment() {
        // Required empty public constructor
    }

    public static SearchTransferInfomationFragment newInstance(String title_search, String hint_search,
                                                               String list_result_title, String search_for) {
        SearchTransferInfomationFragment fragment = new SearchTransferInfomationFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title_search);
        args.putString(HINT_SEARCH, hint_search);
        args.putString(LIST_RESULT_TITLE, list_result_title);
        args.putString(SEARCH_FOR, search_for);


        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title_search = getArguments().getString(TITLE);
            hint_search = getArguments().getString(HINT_SEARCH);
            list_result_title = getArguments().getString(LIST_RESULT_TITLE);
            search_for = getArguments().getString(SEARCH_FOR);
        }
        // Transition khi Fragment mới xuất hiện (Enter)
        setEnterTransition(new Slide(Gravity.BOTTOM));

        // Transition khi Fragment hiện tại biến mất (Exit)
        setExitTransition(new Slide(Gravity.TOP));


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_transfer_infomation, container, false);

        txtSearchTransferInfomationTitle = view.findViewById(R.id.txtSearchTransferInfomationTitle);
        edtEnterInfoSearch = view.findViewById(R.id.edtEnterInfoSearch);
        txtSearchResultListTitle = view.findViewById(R.id.txtSearchResultListTitle);
        imbtSearchCancel = view.findViewById(R.id.imbtSearchCancel);
        imbtSearchTransfer = view.findViewById(R.id.imbtSearchTransfer);

        txtSearchTransferInfomationTitle.setText(title_search);
        edtEnterInfoSearch.setHint(hint_search);
        txtSearchResultListTitle.setText(list_result_title);

        if(search_for.equals(Constants.SEARCH_BANK)){
            Toast.makeText(getContext(), Constants.SEARCH_BANK, Toast.LENGTH_SHORT).show();
        } else if (search_for.equalsIgnoreCase(Constants.SEARCH_ACCOUNT)) {
            Toast.makeText(getContext(), search_for, Toast.LENGTH_SHORT).show();
        } else if(search_for.equalsIgnoreCase(Constants.SEARCH_BENEFICIARY_ACCOUNT)){
            Toast.makeText(getContext(), search_for, Toast.LENGTH_SHORT).show();
        } else if(search_for.equalsIgnoreCase(Constants.SEARCH_BENEFICIARY_PHONE)){
            Toast.makeText(getContext(), search_for, Toast.LENGTH_SHORT).show();
        } else if(search_for.equalsIgnoreCase(Constants.SEARCH_ELECTRICITY_PROVIDER)){
            Toast.makeText(getContext(), search_for, Toast.LENGTH_SHORT).show();
        } else if(search_for.equalsIgnoreCase(Constants.SEARCH_WATER_PROVIDER)){
            Toast.makeText(getContext(), search_for, Toast.LENGTH_SHORT).show();
        } else if(search_for.equalsIgnoreCase(Constants.SEARCH_TUITION_PROVIDER)){
            Toast.makeText(getContext(), search_for, Toast.LENGTH_SHORT).show();
        }

        imbtSearchCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtil.destroyFragment(SearchTransferInfomationFragment.this, getParentFragmentManager());
            }
        });

        imbtSearchTransfer.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Search for ...", Toast.LENGTH_LONG).show();
        });
        return view;

    }
}