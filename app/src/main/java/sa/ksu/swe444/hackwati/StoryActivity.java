package sa.ksu.swe444.hackwati;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


public class StoryActivity extends AppCompatActivity implements View.OnClickListener {


    private TextView change, bookName, duration;
    private String user_id;
    private Button listenBtn;
        //subscribedBtn;
    private ImageView back, cover;
    private static final String TAG = StoryActivity.class.getSimpleName();
    public FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public DocumentReference documentReference;
    public FirebaseAuth mAuth;
    String userUid;
    Button subscribe;
    String storyDuration;
    String storyUri;
    String storyCover;




    private String storyId, userStoryId;


    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story_activity_main);
        getExtras();
        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        duration = findViewById(R.id.duration);
        bookName = findViewById(R.id.bookName);
        cover = (ImageView) findViewById(R.id.cover);
        listenBtn = findViewById(R.id.listenBtn);
        subscribe = findViewById(R.id.subscribeBtn);
        subscribe.setOnClickListener(this);
        listenBtn.setOnClickListener(this);


        subscribeUser();
        retriveStory();


    }// end of setOnNavigationItemSelectedListener()


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

                        Intent intent = getIntent();
                        if (intent.getExtras() != null) {
                            storyId = intent.getExtras().getString(Constants.Keys.STORY_ID);
                            userStoryId = intent.getExtras().getString(Constants.Keys.STORY_USER_ID);
                        }


                        List<String> list = (List<String>) document.get("subscribedUsers");
                        if (list == null) {
                            return;
                        } else for (String subscribedUsers : list) {


                            if (userStoryId.equals(userUid)) {

                                subscribe.setVisibility(View.INVISIBLE);
                                break;
                            } else if (userStoryId.equals(subscribedUsers)) {

                                subscribe.setVisibility(View.VISIBLE);
                                subscribe.setText("مشترك");
                                break;

                            } else {

                                subscribe.setVisibility(View.VISIBLE);
                                subscribe.setText("اشتراك");
                                break;


                            }


                        }// end for loop


                    }
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

                        String title = (String) document.get("title");
                        String description = (String) document.get("description");
                        String pic = document.get("pic").toString();
                        storyDuration = (String) document.get("duration");
                        storyUri = (String) document.get("sound");
                        storyCover = (String)document.get("pic");

                        bookName.setText(title);
                        duration.setText(description);
                        Glide.with(StoryActivity.this)
                                .load(pic + "")
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


    @Override
    public void onClick(View view) {
        switch (view.getId()) {


            case R.id.subscribeBtn:

                if (subscribe.getText().equals("اشتراك"))
                    subscribUser();
                else if (subscribe.getText().equals("مشترك")) {
                    unsubscribUser();
                } else
                    subscribe.setVisibility(View.INVISIBLE);
                break;
            case R.id.listenBtn:
                Intent intent = new Intent(StoryActivity.this, InnerStoryActivity.class);
                intent.putExtra(Constants.Keys.STORY_AUDIO,storyUri );
                intent.putExtra(Constants.Keys.STORY_COVER ,storyCover);
                startActivity(intent);


        }
    }


    public void subscribUser() {
        DocumentReference washingtonRef = firebaseFirestore.collection("users").document(userUid);
        washingtonRef.update("subscribedUsers", FieldValue.arrayUnion(userStoryId));
        washingtonRef.update("numSubscribers", FieldValue.increment(1));
        subscribe.setText("مشترك");

    }

    public void unsubscribUser() {
        DocumentReference washingtonRef = firebaseFirestore.collection("users").document(userUid);
        washingtonRef.update("subscribedUsers", FieldValue.arrayRemove(userStoryId));
        washingtonRef.update("numSubscribers", FieldValue.increment(-1));
        subscribe.setText("اشتراك");
    }
}



