package ru.edgar.nlremake.utils;

import android.net.Uri;

import ru.edgar.matrp.R;
import ru.edgar.nlremake.ui.FullHeightVideoView;

public class VideoUtils {

    public static void setupVideoPlayer(FullHeightVideoView videoView, String packageName) {
        if (videoView == null) return;

        String videoPath = "android.resource://" + packageName + "/" + R.raw.loading;
        videoView.setVideoURI(Uri.parse(videoPath));

        videoView.setOnCompletionListener(mp -> {
            if (mp != null) {
                mp.setLooping(true);
            }
        });

        videoView.setOnPreparedListener(mp -> {
            if (mp != null) {
                mp.setLooping(true);
                videoView.start();
            }
        });

        videoView.start();
    }

    public static void releaseVideoPlayer(FullHeightVideoView videoView) {
        if (videoView != null) {
            videoView.stopPlayback();
            videoView.setOnCompletionListener(null);
            videoView.setOnPreparedListener(null);
        }
    }
}