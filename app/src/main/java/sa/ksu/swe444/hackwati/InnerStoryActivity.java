package sa.ksu.swe444.hackwati;


import android.app.DownloadManager;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
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
    File localFile = null;
    FirebaseStorage storage;
    StorageReference storageReference;
    StorageReference ref;
    public FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();


    Uri audio_url;


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


        // download();


        Uri uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/hackwati444.appspot.com/o/YEWKT0YD92bY2bGsG4evggu5X8t1%2Frere%2Faudio.3gp?alt=media&token=0ab52c20-5322-4041-8662-3076ba5827c3");

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
        storage = FirebaseStorage.getInstance();


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


    private void download() {
        storageReference = storage.getReference();
        ref = storageReference.child("YEWKT0YD92bY2bGsG4evggu5X8t1").child("rere").child("audio.3gp");
        String fileName = ref.getPath();
        // Uri uri = Uri.fromFile(new File(fileName));
        // File localFile = File.createTempFile("audio", "3gp");

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("audio/3gp")
                .build();

        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                audio_url = uri;
                Toast.makeText(getBaseContext(), "Success audio", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getBaseContext(), "failed audio", Toast.LENGTH_SHORT).show();


            }
        });
    }

    private void downloadFile(InnerStoryActivity context, String fileName, String fileExtention, String destinationDir, String url) {
        DownloadManager downloadManager =
                (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request =
                new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDir, fileName + fileExtention);

        downloadManager.enqueue(request);


    }


    private void getAudio() {

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

// Create a reference with an initial file path and name
        StorageReference pathReference = storageRef.child("story_audio/new_record.3gp");

// Create a reference to a file from a Google Cloud Storage URI
        StorageReference gsReference = storage.getReferenceFromUrl("gs://bucket/tory_audio/new_record.3gp");

// Create a reference from an HTTPS URL
// Note that in the URL, characters are URL escaped!
        // StorageReference httpsReference = storage.getReferenceFromUrl("https://firebasestorage.googleapis.com/b/bucket/o/images%20stars.jpg");
    }

    private void story() {
        Query subscribedStories = firebaseFirestore.collection("stories").whereEqualTo("userId", "7o2gIudtyqhIqej2n7TsvHrcATV2");
        subscribedStories.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                audio_url = Uri.parse(task.toString());

            }
        });
    }
}// end class
