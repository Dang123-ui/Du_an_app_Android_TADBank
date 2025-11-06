package com.example.tad_bank_t1.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.browse.MediaBrowser;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.User;
import com.example.tad_bank_t1.data.repository.users.FirebaseUserRepository;
import com.example.tad_bank_t1.data.repository.users.UserRepository;
import com.example.tad_bank_t1.ui.activity.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Congratulation_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Congratulation_Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USERID = "key_userid";
    private String userid;
    private final UserRepository userRepo = new FirebaseUserRepository();
    private ImageView imgLogo;

    public Congratulation_Fragment() {
        // Required empty public constructor
    }
    public static Congratulation_Fragment newInstance(String uid) {
        Congratulation_Fragment fragment = new Congratulation_Fragment();
        Bundle b = new Bundle();
        b.putSerializable(ARG_USERID, uid);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);Bundle args = getArguments();
        if (args != null) {
            userid = args.getString(ARG_USERID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_congratulation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imgLogo = view.findViewById(R.id.imgLogo);
        androidx.core.view.ViewCompat.setTransitionName(imgLogo, "app_logo");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            android.transition.TransitionSet exit = new android.transition.TransitionSet()
                    .addTransition(new android.transition.ChangeBounds())
                    .addTransition(new android.transition.ChangeTransform())
                    .addTransition(new android.transition.ChangeImageTransform());
            exit.setDuration(900); // mượt hơn 600–800ms, bạn có thể nâng 1000–1200
            exit.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
            exit.setPathMotion(new android.transition.ArcMotion()); // cong nhẹ, ít giật
            requireActivity().getWindow().setSharedElementExitTransition(exit);
            requireActivity().getWindow().setSharedElementsUseOverlay(true);
        }
        requireContext()
                .getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                .edit()
                .putBoolean("hasRegistered", true)
                .putString("lastUserId", userid)   // 👈 sửa đúng key
                .apply();
        new Handler(Looper.getMainLooper()).postDelayed(() -> goToMainActivity(imgLogo), 2500);
    }
    private void goToMainActivity(@Nullable View sharedLogo) {
        Intent intent = new Intent(requireContext(), MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_USERID, userid);
        if (sharedLogo != null) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(),
                    new androidx.core.util.Pair<>(sharedLogo, "app_logo")
            );
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
        requireActivity().finish();
    }
}