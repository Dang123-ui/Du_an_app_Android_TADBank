package com.example.tad_bank_t1.ui.fragment;

import android.graphics.Color;
import android.media.browse.MediaBrowser;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Account;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Congratulation_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Congratulation_Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ACCOUNT = "key_account";
    private Account account;;

    public Congratulation_Fragment() {
        // Required empty public constructor
    }
    public static Congratulation_Fragment newInstance(Account account) {
        Congratulation_Fragment fragment = new Congratulation_Fragment();
        Bundle b = new Bundle();
        b.putSerializable(ARG_ACCOUNT, account); // Account implements Serializable
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            Object obj = args.getSerializable(ARG_ACCOUNT);
            if (obj instanceof Account) account = (Account) obj;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_congratulation_, container, false);
    }
}