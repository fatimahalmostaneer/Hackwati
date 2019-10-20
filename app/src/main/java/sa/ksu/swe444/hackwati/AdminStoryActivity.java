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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class AdminStoryActivity extends AppCompatActivity {


    private TextView change, bookName, duration;
    private String user_id;
    //private Button listenBtn, subscribedBtn;
    private ImageView back, cover;
    private static final String TAG = AdminStoryActivity.class.getSimpleName();
    public FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public DocumentReference documentReference;
    public FirebaseAuth mAuth;
    String userUid;
     Button approve;



    private String storyId, userStoryId;


    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story_admin_activity);
        getExtras();
        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();





        duration = findViewById(R.id.duration);
        bookName = findViewById(R.id.bookName);
        cover = (ImageView) findViewById(R.id.cover);

        approve = findViewById(R.id.approve);
        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                approveStory();
            }
        });



        retriveStory();

    }
    // end of setOnNavigationItemSelectedListener()


    public void approveStory(){

        DocumentReference docRef = firebaseFirestore.collection("stories").document(storyId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        // get data
                        document.getData();
                        String description = (String) document.get("description");
                        String pic = (String) document.get("pic");
                        String rate = (String) document.get("rate");
                        String sound = (String) document.get("sound");
                        String title = (String) document.get("title");
                        String userId = (String) document.get("userId");

                        // new doc

                        Map<String, Object> publishedStories = new HashMap<>();
                        publishedStories.put("description", description);
                        publishedStories.put("rate", rate);
                        publishedStories.put("title", title);
                        publishedStories.put("pic", pic);
                        publishedStories.put("userId", userId);
                        publishedStories.put("sound", sound);

                        firebaseFirestore.collection("publishedStories").document().set(publishedStories)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {


                                        firebaseFirestore.collection("stories").document(storyId)
                                                .delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error deleting document", e);
                                                    }
                                                });


                                        Toast.makeText(AdminStoryActivity.this, "story publish", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(AdminStoryActivity.this, AdminActivity.class));
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AdminStoryActivity.this, "Error_add_story", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, e.toString());
                                    }
                                });


                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


    }



    private void getExtras() {

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            storyId = intent.getExtras().getString(Constants.Keys.STORY_ID);
            userStoryId = intent.getExtras().getString(Constants.Keys.STORY_USER_ID);
        }

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
                        Glide.with(AdminStoryActivity.this)
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




