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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import sa.ksu.swe444.hackwati.Constants;
import sa.ksu.swe444.hackwati.Item;
import sa.ksu.swe444.hackwati.MainActivity;
import sa.ksu.swe444.hackwati.R;
import sa.ksu.swe444.hackwati.Recording.RecordingActivity;
import sa.ksu.swe444.hackwati.UserProfile;


public class StoryActivity extends AppCompatActivity {


    private TextView change, bookName, duration;
    private String user_id;
    //private Button listenBtn, subscribedBtn;
    private ImageView back, cover;
    private static final String TAG = StoryActivity.class.getSimpleName();
    public FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public DocumentReference documentReference;
    public FirebaseAuth mAuth;
    String userUid;
     Button subscribe,subscribed;



    private String storyId, userStoryId;


    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story_activity_main);
        getExtras();
        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        subscribe=findViewById(R.id.subscribeBtn);

       subscribed =findViewById(R.id.subscribedBtn);



        duration = findViewById(R.id.duration);
        bookName = findViewById(R.id.bookName);
        cover = (ImageView) findViewById(R.id.cover);



        subscribeUser();
        retriveStory();

    }
    // end of setOnNavigationItemSelectedListener()


    private void getExtras() {

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            storyId = intent.getExtras().getString(Constants.Keys.STORY_ID);
            userStoryId = intent.getExtras().getString(Constants.Keys.STORY_USER_ID);
        }

    }


    public void subscribeUser() {
        DocumentReference docRef = firebaseFirestore.collection("users").document(userUid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        Log.d(TAG, " 111 document exist");


                        List<String> list = (List<String>) document.get("subscribedUsers");
                        if (list == null) {
                            return;

                        } else for (String subscribedUsers : list) {

                            Log.d(TAG, " ** "+ subscribedUsers);
                            Log.d(TAG, " userStoryId: "+ userStoryId);

                           if (userStoryId.equals(userUid)){

                                subscribe.setVisibility(View.INVISIBLE);
                                subscribed.setVisibility(View.INVISIBLE);
                                Log.d(TAG, "user");

                            }
                            if(userStoryId.equals(subscribedUsers)){

                                subscribe.setVisibility(View.VISIBLE);
                                subscribed.setVisibility(View.INVISIBLE);
                               // Log.d(TAG, " user not exist");

                            }
                            else {

                                subscribe.setVisibility(View.INVISIBLE);
                                subscribed.setVisibility(View.VISIBLE);
                               // Log.d(TAG, "user exist");


                            }



                        }// end for loop


                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    public void retriveStory() {

        DocumentReference docRef = firebaseFirestore.collection("stories").document(storyId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        String title = (String)document.get("title");
                        String description = (String) document.get("description");
                        String pic =  document.get("pic").toString();


                        bookName.setText(title);
                        duration.setText(description);
                        Glide.with(StoryActivity.this)
                                .load(pic+"")
                                .into(cover);

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }


}




