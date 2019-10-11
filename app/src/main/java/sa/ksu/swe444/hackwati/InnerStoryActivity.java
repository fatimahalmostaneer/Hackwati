package sa.ksu.swe444.hackwati;



import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;


public class InnerStoryActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView share;
    private ImageView close;
    private SeekBar seekBar;
    private ImageView pause;
    private ImageView backward;
    private ImageView forward;
    private ImageView nightMood;
    private TextView speed;
    private MediaPlayer mediaPlayer;
    private TextView remainingTime;
    private TextView currentTime;
   // private MyService myService;
    private TextView upload;
    private RelativeLayout RL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inner_story_layout);
        init();
        pause.setOnClickListener(this);
        close.setOnClickListener(this);
        share.setOnClickListener(this);
        backward.setOnClickListener(this);
        forward.setOnClickListener(this);
        speed.setOnClickListener(this);
        nightMood.setOnClickListener(this);
        //get Audio from fire base
        mediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.child_story);
        seekBar.setMax(mediaPlayer.getDuration());

        defaultTimer();
        SeekBar();

        Runnable mUpdateSeekbar = new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                seekBar.postDelayed(this, 50);
                currentTime.setText(milliSecondsToTimer(mediaPlayer.getCurrentPosition()));
                remainingTime.setText(milliSecondsToTimer(mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition()));
                SeekBar();
            }// run

        };

        seekBar.postDelayed(mUpdateSeekbar, 100);

    }//end onCreate

    private void init() {
        share = findViewById(R.id.share);
        close = findViewById(R.id.close);
        seekBar = findViewById(R.id.seekBar);
        pause = findViewById(R.id.pause);
        backward = findViewById(R.id.back);
        forward = findViewById(R.id.forward);
        nightMood = findViewById(R.id.night_mood);
        speed = findViewById(R.id.speed);
        remainingTime = findViewById(R.id.remaining_time);
        currentTime = findViewById(R.id.currentTime);
      //  myService = new MyService();
        RL = findViewById(R.id.Dialog);
    }//end init

    private void SeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null && mediaPlayer.isPlaying())
                    mediaPlayer.seekTo(progress);
            }// onProgressChanged
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.start();
                mediaPlayer.pause();
                defaultTimer();
                pause.setImageResource(R.drawable.ic_play_button);
            }// onCompletion

        });

    }// seekbar


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pause:
                // toggle pause/ start button

                if (mediaPlayer.isPlaying()) {
                    pause.setImageResource(R.drawable.ic_play_button);
                    mediaPlayer.pause();
                    // stopService(new Intent(getApplicationContext(), MyService.class));
                } else {
                    pause.setImageResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                    //startService(new Intent(getApplicationContext(), MyService.class));

                    //record();
                }// else
                break;

            case R.id.close:
                finish();

                break;
            case R.id.share:
                shareStory();
                break;

            case R.id.forward:
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 15000);
                break;
            case R.id.back:
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 15000);
                break;

            case R.id.speed:
                if (mediaPlayer.isPlaying()) {
                    if (speed.getText().toString().equals("X2")) {
                        mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(1.5f));
                        speed.setText("X1");
                    } else {
                        mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(1f));
                        speed.setText("X2");

                    }
                }// if
                break;
            case R.id.night_mood:
               // startActivity(new Intent(InnerStoryActivity.this , Test.class));

        }// end switch


    }// onClick()

    private String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }// milliSecondsToTimer


    private void defaultTimer() {
        currentTime.setText("00:00");
        int duration = mediaPlayer.getDuration();
        String time = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        );
        remainingTime.setText(time);

    }//end default timer

    private void shareStory() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "SEND"));
    }// end shareStory()






}// end class
