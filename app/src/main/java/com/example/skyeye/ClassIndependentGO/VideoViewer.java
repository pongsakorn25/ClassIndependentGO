package com.example.skyeye.ClassIndependentGO;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.widget.VideoView;
import android.widget.MediaController;
/**
 * Created by TPK on 22-Mar-17.
 */

public class VideoViewer extends Activity {

    private ProgressDialog progressDialog;
    private MediaController mController;
    private String videoUrl, fileName;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_videoviewer);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        fileName = getIntent().getStringExtra("paste");
//        String link="https://www.youtube.com/watch?v=IQvwFArluqE";
        videoUrl = "https://classindependent.000webhostapp.com/" + fileName;

        VideoView videoView = (VideoView) findViewById(R.id.videoView);

        mController = new MediaController(this);
        mController.setAnchorView(videoView);
        videoView.setMediaController(mController);
        Uri video = Uri.parse(videoUrl);
        videoView.setVideoURI(video);
        videoView.requestFocus();
            videoView.start();

    }


}
