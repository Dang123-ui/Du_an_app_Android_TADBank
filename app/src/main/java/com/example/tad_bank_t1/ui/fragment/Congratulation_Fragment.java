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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Congratulation_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Congratulation_Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ExoPlayer player;
    private PlayerView playerView;

    public Congratulation_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Congratulation_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Congratulation_Fragment newInstance(String param1, String param2) {
        Congratulation_Fragment fragment = new Congratulation_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_congratulation_, container, false);
    }
    private static final long FIRST_FRAME_EXTRA_DELAY_MS = 300;
    private final Handler handler = new Handler(Looper.getMainLooper());
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playerView = view.findViewById(R.id.playerView);
        playerView.setVisibility(View.INVISIBLE);
        playerView.setAlpha(0f);
        player = new ExoPlayer.Builder(requireContext()).build();
        playerView.setPlayer(player);
        MediaItem mediaItem = MediaItem.fromUri("asset:///fireworks.mp4");
        player.setMediaItem(mediaItem);
        player.setRepeatMode(Player.REPEAT_MODE_ALL);
        player.prepare();
        player.addListener(new Player.Listener() {
            @Override
            public void onRenderedFirstFrame() {
                handler.postDelayed(() -> {
                    if (playerView != null && playerView.getVisibility() != View.VISIBLE) {
                        playerView.setVisibility(View.VISIBLE);
                        playerView.animate().alpha(1f).setDuration(180).start();
                    }
                    if (player != null && !player.isPlaying()) player.play();
                }, FIRST_FRAME_EXTRA_DELAY_MS);
            }

            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_READY && playerView.getVisibility() != View.VISIBLE) {
                    handler.postDelayed(() -> {
                        if (playerView != null) {
                            playerView.setVisibility(View.VISIBLE);
                            playerView.setAlpha(1f);
                        }
                    }, FIRST_FRAME_EXTRA_DELAY_MS);
                }
            }
        });
        player.setPlayWhenReady(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player != null) player.play();
    }

    @Override
    public void onPause() {
        if (player != null) player.pause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (player != null) {
            player.release();
            player = null;
        }
        playerView = null;
        super.onDestroyView();
    }
}