package sa.ksu.swe444.hackwati.Recording;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import io.grpc.Metadata;
import io.opencensus.tags.Tag;
import sa.ksu.swe444.hackwati.Constants;
import sa.ksu.swe444.hackwati.MainActivity;
import sa.ksu.swe444.hackwati.MySharedPreference;
import sa.ksu.swe444.hackwati.R;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class Tab2StoryInfo extends Fragment {

    private View view;
    private EditText storyTitle;
    private EditText storyDiscription;
    private Button publishStory;
    private ImageView img;
    private boolean isSelectImage;
    private static int INTENT_CAMERA = 401;
    private static int INTENT_GALLERY = 301;
    private File imgFile;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref;


    private static final String LOG_TAG = "AudioRecordTest";
    public FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef;
    String userUid;
    private String fileName;
    private String imgPath;
    public FirebaseAuth mAuth;
    Uri contentURI;
    private String audioUri;
    private String imgUri;


    //upload story
    String id = "7o2gIudtyqhIqej2n7TsvHrcATV2";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.recording_fragment_two, container, false);
        storyDiscription = view.findViewById(R.id.pp);
        storyTitle = view.findViewById(R.id.name);
        mAuth = FirebaseAuth.getInstance();

        fileName = getActivity().getExternalCacheDir().getAbsolutePath();
        fileName += "/audiorecordtest.3gp";
        // storyDiscription.setText(storyTitle.getText().toString());
        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        storageRef = storage.getReference();


        publishStory = view.findViewById(R.id.submit);
        publishStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                uploadAudio();
                uploadImage();
                addStoryToCollection();

            }
        });

        img = view.findViewById(R.id.Img);


        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCameraChooser();

            }
        });


        return view;
    }//end of onCreate()


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_CANCELED) {
            return;
        }//end if statement
        if (requestCode == INTENT_GALLERY) {
            if (data != null) {
                contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentURI);
                    // String path = saveImage(bitmap);
                    Toast.makeText(getContext(), "Image Saved!", Toast.LENGTH_SHORT).show();
                    img.setImageBitmap(bitmap);
                    isSelectImage = true;
                    persistImage(bitmap);
                    //uploadImage();


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                }// end catch
            }//end if statement

        } else if (requestCode == INTENT_CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            img.setImageBitmap(thumbnail);
            // saveImage(thumbnail);
            Toast.makeText(getContext(), "Image Saved!", Toast.LENGTH_SHORT).show();
        }//end if statement1
    }//end onActivityResult()


    private void persistImage(Bitmap bitmap) {
        File fileDir = getActivity().getFilesDir();
        String name = "image";

        imgFile = new File(fileDir, name + ".png");
        OutputStream os;
        try {
            os = new FileOutputStream(imgFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            imgPath = imgFile.getPath();
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }//end catch
    }//end of persistImage()


    private void openCameraChooser() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 100);
        }// end if

        showPhotoOptionsDialog();
    }//end of openCameraChooser()

    private void showPhotoOptionsDialog() {
        final CharSequence[] items = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
                if (items[item].equals("Camera")) {
                    cameraIntent();
                } else if (items[item].equals("Gallery")) {
                    galleryIntent();
                }//end if else
            }//end onClick()
        });//end setItems
        builder.show();
    }//end showPhotoOptionsDialog()

    private void galleryIntent() {

        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, INTENT_GALLERY);
    }//END OF galleryIntent()

    private void cameraIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, INTENT_CAMERA);
    }// END OF cameraIntent()

    public void addStoryAdd() {
        Map<String, Object> story = new HashMap<>();
        String title = storyTitle.getText().toString();
        String descripstion = storyDiscription.getText().toString();
        //String  pic = imgFile.toURI().toURL().getFile().toString();
        // String  uid =

        firebaseFirestore.collection("stories").document().set(story)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "successful", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "failed", Toast.LENGTH_LONG).show();


            }
        });
    }

    private void uploadAudio() {
        //FirebaseStorage uploadTask = FirebaseStorage.getInstance().ch;

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("audio/3gp")
                .build();
        String userId = mAuth.getCurrentUser().getUid();
        String storyId = storyTitleToStoryId();
        final StorageReference filepath = storageRef.child(userId).child(storyId).child("audio.3gp");
        Uri uri = Uri.fromFile(new File(fileName));
        MySharedPreference.putString(getContext(), Constants.Keys.STORY_AUDIO, filepath + "");
        Log.d(LOG_TAG, filepath + " audio");
        filepath.putFile(uri, metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getContext(), "success Audio", Toast.LENGTH_SHORT);


            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getContext(), "failed Audio", Toast.LENGTH_SHORT);


            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                //audioUri=  task.getResult().getUploadSessionUri().toString();
            }
        });

        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                audioUri = uri.toString();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
            }
        });
       // audioUri = filepath.getDownloadUrl().toString();


    }// uploadAudio

    private void uploadImage() {

        //FirebaseStorage uploadTask = FirebaseStorage.getInstance().ch;
        String userId = mAuth.getCurrentUser().getUid();
        String storyId = storyTitleToStoryId();
        final StorageReference filepath = storageRef.child(userId).child(storyId).child("img.jpeg");
        // MySharedPreference.putString(getContext( ), Constants.Keys.STORY_COVER,filepath+"");
        Log.d(LOG_TAG, filepath + " cover");
//        Uri uri = Uri.fromFile(new File(imgPath));
        filepath.putFile(contentURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getContext(), "success Image", Toast.LENGTH_SHORT);


            }
        });
        imgUri = filepath.getDownloadUrl()+"";


    }

    private void addStoryToCollection() {

        Map<String, Object> story = new HashMap<>();

        String description = storyDiscription.getText().toString();
        String rate = "5";
        String title = storyTitle.getText().toString();
        String userId = id;
        String pic = MySharedPreference.getString(getContext(), Constants.Keys.STORY_COVER, "");
        String sound = MySharedPreference.getString(getContext(), Constants.Keys.STORY_AUDIO, "");

        story.put("description", description);
        story.put("rate", rate);
        story.put("title", title);
        story.put("userId", userId);
        story.put("pic", imgUri);
        story.put("sound", audioUri);
        story.put("duration", audioUri);


        Log.d(LOG_TAG, description + title + pic + sound);


        firebaseFirestore.collection("stories").document().set(story)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "story added", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getContext(), MainActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error_add_story", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
    }

    private String storyTitleToStoryId() {
        String title = storyTitle.getText().toString();
        title = title.toLowerCase();
        title = title.replace(" ", "_");
        String userId = mAuth.getCurrentUser().getUid();
        ref = database.getReference("stories/" + userId + "/counter");
        title.concat(ref.toString());
        return title;
    }


}