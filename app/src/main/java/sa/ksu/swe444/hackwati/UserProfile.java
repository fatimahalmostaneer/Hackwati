package sa.ksu.swe444.hackwati;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

import sa.ksu.swe444.hackwati.Recording.RecordingActivity;

public class UserProfile extends AppCompatActivity {
    Button log_out;
    Button record;
    private FirebaseAuth mAuth;
    private ImageView uploadImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_vistor_row);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


       /* toolbar.setTitle("الإعدادات");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        record=findViewById(R.id.record_profile);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordActivity();
            }

            private void recordActivity() {
                startActivity(new Intent(UserProfile.this, RecordingActivity.class));
            }
        });
        log_out = findViewById(R.id.logout_profile);
        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
    }
    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        startActivity( new Intent(UserProfile.this, SplashActivity.class));
    }//end of signOut



}