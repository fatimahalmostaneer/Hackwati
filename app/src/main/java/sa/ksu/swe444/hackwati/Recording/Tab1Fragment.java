package sa.ksu.swe444.hackwati.Recording;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import sa.ksu.swe444.hackwati.R;


@RequiresApi(api = Build.VERSION_CODES.O)
public class Tab1Fragment extends Fragment implements View.OnClickListener, IOnFocusListenable {

    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;

    private ImageButton recordButton;
    private MediaRecorder recorder;
    private Button   playButton;
    private MediaPlayer player = null;
    private boolean mStartRecording;
    boolean mStartPlaying;
    private TextView timer ;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private View view;

    private VisualizerView visualizerView;
    private Handler handler;
    Runnable updater;
    private long elapsedTime;
    private long start;
    private SecondFragmentListener  listener;
    private Button next;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.recording_fragment_one, container, false);

        // Record to the external cache directory for visibility
        fileName = getActivity().getExternalCacheDir().getAbsolutePath();
        fileName += "/audiorecordtest.3gp";

        ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        recordButton = view.findViewById(R.id.record_btn);
       // playButton = view.findViewById(R.id.play_btn);
        timer = view.findViewById(R.id.timer);
        visualizerView = view.findViewById(R.id.visualizer);
        next = view.findViewById(R.id.next);
        recorder = new MediaRecorder();
        recordButton.setOnClickListener(this);
        //playButton.setOnClickListener(this);

        //playButton.setText("start playing");
        initRecorder();

        mStartRecording = true;
        mStartPlaying = false;
         handler = new Handler();
         updater = new Runnable() {

            public void run() {
                    handler.postDelayed(this, 1);
                    if(recorder!=null){  /**/
                    int maxAmplitude = recorder.getMaxAmplitude();
                    if (maxAmplitude != 0) {
                        visualizerView.addAmplitude(3*maxAmplitude);
                            elapsedTime = System.currentTimeMillis() - start;
                       String t= milliSecondsToTimer(elapsedTime);
                        timer.setText(t);
                }}
            }// run
        };

         next.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 listener.onNextButton();

             }
         });

        return view;
    }//onCreateView()

    private void initRecorder(){

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        player = new MediaPlayer();

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

    }//initRecorder()

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        //if (!permissionToRecordAccepted )
        //   getActivity().finish();

    }//onRequestPermissionsResult


    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }//onPlay()

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }//startPlaying()

    private void stopPlaying() {
        player.stop();
        player.release();
        player = null;
    }//stopPlaying()


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.record_btn:
                if(mStartRecording){
                    recorder.start();
                    start = System.currentTimeMillis();

                }else {
                    recorder.stop();     // stop recording
                    //recorder.reset();    // set state to idle
                    //recorder.release();  // release resources back to the system
                }
                mStartRecording = !mStartRecording;
                break;
          /*  case R.id.play_btn:
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    playButton.setText("Stop playing");
                } else {
                    playButton.setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
                break;*/

        }// switch

    }//onClick()

    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
        }

        if (player != null) {
            player.release();
            player = null;
        }
    }//onStop()


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        handler.post(updater);

    }//onWindowFocusChanged()

    private String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there


        finalTimerString = minutes +" : "+ seconds;

        // return timer string
        return finalTimerString;
    }// milliSecondsToTimer


    public interface SecondFragmentListener{
        // methods are the action i need
        void onNextButton();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (SecondFragmentListener) getActivity();
    }

}// class