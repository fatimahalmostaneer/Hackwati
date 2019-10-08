package sa.ksu.swe444.hackwati.storyActivity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import sa.ksu.swe444.hackwati.MainActivity;
import sa.ksu.swe444.hackwati.R;
import sa.ksu.swe444.hackwati.Recording.RecordingActivity;


public class StoryActivity extends AppCompatActivity{

    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private  Tab1Fragment tab1Fragment;
    private  Tab2Fragment tab2Fragment;
    private TextView change;
    private  String user_id;
    private Button listenBtn,subscribedBtn;
    private ImageView back;
    final static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = StoryActivity.class.getSimpleName();

    BottomNavigationView navView;



    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story_activity_main);

        subscribedBtn= findViewById(R.id.subscribedBtn);
        subscribedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                

            }
        });



/*
        back =findViewById(R.id.back1);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StoryActivity.this, MainActivity.class); // from where? and to the distanation
                startActivity(intent);


            }
        });
*/



        listenBtn = findViewById(R.id.listenBtn);

      /*  listenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StoryActivity.this, InnerStoryActivity.class); // from where? and to the distanation
                startActivity(intent);


            }
        });*/



        Button getRating = findViewById(R.id.getRating);
        final RatingBar ratingBar = findViewById(R.id.rating);
        getRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rating = "التقييم" + ratingBar.getRating();
                Toast.makeText(StoryActivity.this, rating, Toast.LENGTH_LONG).show();


            }
        });


        ImageButton shareButton =findViewById(R.id.btnShare);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Share"));
            }
        });

        final Button subscribe=findViewById(R.id.subscribeBtn);

        final Button subscribed =findViewById(R.id.subscribedBtn);



        subscribe.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                subscribe.setVisibility(View.INVISIBLE);

                subscribed.setVisibility(View.VISIBLE);


            }

        });
        subscribed.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {



                subscribe.setVisibility(View.VISIBLE);

                subscribed.setVisibility(View.INVISIBLE);

            }

        });

        change= findViewById(R.id.bookName);



        getExtras();

    }
    // end of setOnNavigationItemSelectedListener()





    private void getExtras() {
        Intent intent = getIntent();
        user_id= intent.getStringExtra("USER_ID");
        db.collection("kids")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }


}




