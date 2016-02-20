package uk.co.odeon.androidapp.activity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.MediaController;
import android.widget.VideoView;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.provider.FilmContent.FilmColumns;

public class FilmTrailerActivity extends AbstractODEONBaseActivity implements OnCompletionListener, OnPreparedListener {
    private String TAG;
    private VideoView videoView;

    public FilmTrailerActivity() {
        this.TAG = "TrailerPlayer";
    }

    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.film_trailer);
        showCancelableProgress(R.string.film_trailer, new OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                FilmTrailerActivity.this.close();
            }
        }, true);
        this.videoView = (VideoView) findViewById(R.id.trailerVideoView);
        this.videoView.setOnPreparedListener(this);
        this.videoView.setOnCompletionListener(this);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(this.videoView);
        mediaController.setBackgroundColor(getResources().getColor(R.color.mediacontroller_background));
        String videoUri = getIntent().getStringExtra(FilmColumns.TRAILER_URL);
        if (videoUri != null) {
            Uri video = Uri.parse(videoUri);
            Log.i(this.TAG, "Playing " + videoUri);
            this.videoView.setMediaController(mediaController);
            this.videoView.setVideoURI(video);
        }
    }

    private void close() {
        finish();
    }

    public void onPrepared(MediaPlayer vp) {
        hideProgress(true);
        this.videoView.start();
    }

    public void onCompletion(MediaPlayer mp) {
        finish();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            Log.i(this.TAG, "back pressed");
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
