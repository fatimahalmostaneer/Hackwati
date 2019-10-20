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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StoryActivity extends AppCompatActivity implements View.OnClickListener {


    private TextView change, bookName, duration;
    private String user_id;
    //private Button listenBtn, subscribedBtn;
    private ImageView back, cover;
    private static final String TAG = StoryActivity.class.getSimpleName();
    public FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public DocumentReference documentReference;
    public FirebaseAuth mAuth;
    String userUid;
    Button subscribe, subscribed;


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


        subscribe = findViewById(R.id.subscribeBtn);

        subscribed = findViewById(R.id.subscribedBtn);


        subscribeUser();

        retriveStory();

        subscribe.setOnClickListener(this); subscribed.setOnClickListener(this);

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


                        Intent intent = getIntent();
                        if (intent.getExtras() != null) {
                            storyId = intent.getExtras().getString(Constants.Keys.STORY_ID);
                            userStoryId = intent.getExtras().getString(Constants.Keys.STORY_USER_ID);
                        }


                        List<String> list = (List<String>) document.get("subscribedUsers");
                        if (list == null) {
                            return;
                        } else for (String subscribedUsers : list) {

                            Log.d(TAG, " ** " + subscribedUsers);
                            Log.d(TAG, " userStoryId: " + userStoryId);
                    /*       if (userStoryId.equals(userUid)){

                                subscribe.setVisibility(View.INVISIBLE);
                                subscribed.setVisibility(View.INVISIBLE);
                                Log.d(TAG, "user");

                            }*/
                            if (userStoryId.equals(subscribedUsers)) {

                                subscribe.setVisibility(View.INVISIBLE);
                                subscribed.setVisibility(View.VISIBLE);
                                // Log.d(TAG, " user not exist");

                            } else {

                                subscribe.setVisibility(View.VISIBLE);
                                subscribed.setVisibility(View.INVISIBLE);
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

                        String title = (String) document.get("title");
                        String description = (String) document.get("description");
                        String pic = document.get("pic").toString();


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
            case R.id.subscribedBtn:
                updateDeleteField();

                break;

            case R.id.subscribeBtn:

                updateDeleteField();
                break;


        }
    }



    public void updateDeleteField() {
       /* // [START update_delete_field]
        DocumentReference docRef = firebaseFirestore.collection("users").document(userUid);

        // Remove the 'capital' field from the document
        Map<String,Object> updates = new HashMap<>();
        updates.put("subscribedUsers", FieldValue.delete());

        docRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {}
            // [START_EXCLUDE]
        });
        // [END update_delete_field]*/
    }

}




