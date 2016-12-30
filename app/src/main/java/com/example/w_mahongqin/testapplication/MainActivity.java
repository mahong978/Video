package com.example.w_mahongqin.testapplication;

import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnVideoSizeChangedListener,
        SeekBar.OnSeekBarChangeListener {

    private Display currDisplay;
    private SurfaceView surfaceView;
    private SurfaceHolder holder;
    private MediaPlayer player;
    private int vWidth,vHeight;
    private int maxLength;

    private SeekBar seekBar;
    private Handler handler = new Handler();
    private class UpdateSeekBarRunnable implements Runnable {
        @Override
        public void run() {
            seekBar.setProgress(seekBar.getProgress() + 1000);
            handler.postDelayed(new UpdateSeekBarRunnable(), 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        surfaceView = (SurfaceView) findViewById(R.id.video);
        holder = surfaceView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        player = new MediaPlayer();
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        player.setOnInfoListener(this);
        player.setOnPreparedListener(this);
        player.setOnSeekCompleteListener(this);
        player.setOnVideoSizeChangedListener(this);

        String path = Environment.getExternalStorageDirectory().getPath() + "/Wildlife.wmv";
        try {
            player.setDataSource(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        currDisplay = this.getWindowManager().getDefaultDisplay();

        seekBar = (SeekBar) findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        player.setDisplay(holder);
        player.prepareAsync();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (player.isPlaying()) {
            player.seekTo(i);
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        Toast.makeText(this, "Seek Completed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        vWidth = player.getVideoWidth();
        vHeight = player.getVideoHeight();

        if (vWidth > currDisplay.getWidth() || vHeight > currDisplay.getHeight()) {
            float wRatio = (float)vWidth / (float)currDisplay.getWidth();
            float hRatio = (float)vHeight / (float)currDisplay.getHeight();

            float ratio = Math.max(wRatio, hRatio);
            vWidth = (int)Math.ceil((float)vWidth / ratio);
            vHeight = (int)Math.ceil((float)vHeight / ratio);
            surfaceView.setLayoutParams(new RelativeLayout.LayoutParams(vWidth, vHeight));

            player.start();
        }

        maxLength = player.getDuration();
        seekBar.setMax(maxLength);
        new UpdateSeekBarRunnable().run();
        Log.d("MainActivity", "Duration: " + maxLength);
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
        switch(i) {
            case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                break;
        }
        return false;
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        switch(i) {
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                break;
        }
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Toast.makeText(this, "Completed", Toast.LENGTH_SHORT).show();
    }
}
