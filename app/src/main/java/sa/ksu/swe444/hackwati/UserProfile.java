package sa.ksu.swe444.hackwati;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kinda.alert.KAlertDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class UserProfile extends AppCompatActivity {
    Button log_out;
    Button record;
    private ImageView img;
    private static int INTENT_CAMERA = 401;
    private static int INTENT_GALLERY = 301;
    private boolean isSelectImage;
    Uri contentURI;
    public static String downloadURLA;
    private File imgFile;
    public FirebaseAuth mAuth;
    private Button uploadImg;
    private String imgPath;
    private ImageView edit1,edit2,edit3;


    private static final String TAG = "UserProfile";
    public FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    String userUid;
    StorageReference storageRef;
    FirebaseStorage storage = FirebaseStorage.getInstance();


    TextView userNameText, emailText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_vistor_row);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userNameText = findViewById(R.id.nameSignUpHin);
        emailText = findViewById(R.id.emailSignUpHin);

        edit1 = findViewById(R.id.edit1);
        edit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog("name");
            }
        });


        edit2 = findViewById(R.id.edit2);
        edit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog("email");
            }
        });

        edit3 = findViewById(R.id.edit3);
        edit3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog("pass");
            }
        });


        record = findViewById(R.id.record_profile);
        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        storageRef = storage.getReference();
        mAuth = FirebaseAuth.getInstance();

        img = findViewById(R.id.userImg);
        uploadImg = findViewById(R.id.uploadImg);
        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImageWithUri();


            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openCameraChooser();

            }
        });

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  CreateAlertDialogWithRadioButtonGroup();

                showChoiceDialog();
            }
        });
        log_out = findViewById(R.id.logout_profile);
        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        retriveUserData();

    }// end onCreate()

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(UserProfile.this, SplashActivity.class));
    }//end of signOut


    public void retriveUserData() {


        DocumentReference docRef = firebaseFirestore.collection("users").document(userUid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        String userName = document.get("username").toString();
                        String email = document.get("email").toString();
                        String thumbnail = document.get("thumbnail").toString();
                        if (userName != null && email != null) {
                            userNameText.setText(userName);
                            emailText.setText(email);

                            Glide.with(UserProfile.this)
                                    .load(thumbnail + "")
                                    .into(img);


                        }


                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


    }

    // IMAGE

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == UserProfile.this.RESULT_CANCELED) {
            return;
        }//end if statement
        if (requestCode == INTENT_GALLERY) {
            if (data != null) {
                contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(UserProfile.this.getContentResolver(), contentURI);
                    Toast.makeText(UserProfile.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    img.setImageBitmap(bitmap);
                    isSelectImage = true;
                    persistImage(bitmap);


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(UserProfile.this, "Failed!", Toast.LENGTH_SHORT).show();
                }// end catch
            }//end if statement

        } else if (requestCode == INTENT_CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            img.setImageBitmap(thumbnail);
            Toast.makeText(UserProfile.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }//end if statement1
    }//end onActivityResult()

    private void persistImage(Bitmap bitmap) {
        File fileDir = UserProfile.this.getFilesDir();
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
        if (ActivityCompat.checkSelfPermission(UserProfile.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UserProfile.this, new String[]{Manifest.permission.CAMERA}, 100);
        }// end if

        showPhotoOptionsDialog();
    }//end of openCameraChooser()

    private void showPhotoOptionsDialog() {
        final CharSequence[] items = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
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

    private void uploadImageWithUri() {
        Log.d(TAG, "aa2");

        if (imgPath != null) {

            final StorageReference filepath = storageRef.child(userUid).child("thumbnail.jpeg");

            //uploading the image
            final UploadTask uploadTask = filepath.putFile(contentURI);

            Log.d(TAG, "aa1");


            // get Uri
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String downloadURL = downloadUri.toString();
                        MySharedPreference.putString(UserProfile.this, Constants.Keys.USER_IMG, downloadURL);


                        DocumentReference updateRef = firebaseFirestore.collection("users").document(userUid);

                        // reset the thumbnail" field
                        updateRef
                                .update("thumbnail", downloadUri + "")
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating document", e);
                                    }
                                });


                        //Log.d(TAG,downloadURL+" Ya2" );

                    }
                }
            });


            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(UserProfile.this, "Upload successful", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UserProfile.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(UserProfile.this, "Select an image", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProfile() {

    }

    public void CreateAlertDialogWithRadioButtonGroup() {


        KAlertDialog pDialog = new KAlertDialog(this, KAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();


        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);


        AlertDialog alertDialog = builder.create();

        alertDialog.show();

        builder.setTitle("تعديل :");






/*        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                switch(item)
                {
                    case 0:

                        Toast.makeText(UserProfile.this, "First Item Clicked", Toast.LENGTH_LONG).show();
                        break;
                    case 1:

                        Toast.makeText(UserProfile.this, "Second Item Clicked", Toast.LENGTH_LONG).show();
                        break;
                    case 2:

                        Toast.makeText(UserProfile.this, "Third Item Clicked", Toast.LENGTH_LONG).show();
                        break;
                }
                alertDialog1.dismiss();
            }
        });
        alertDialog1 = builder.create();
        alertDialog1.show();

    }*/

    }

  public void showDialog(String edit) {

      AlertDialog.Builder builder = new AlertDialog.Builder(this);





      switch(edit)
      {
          case "name":
            //  builder.setTitle("تعديل الاسم");

              Toast.makeText(UserProfile.this, "First Item Clicked", Toast.LENGTH_LONG).show();
              break;
          case "email":
            //  builder.setTitle("تعديل البريد الإلكتروني");

              Toast.makeText(UserProfile.this, "Second Item Clicked", Toast.LENGTH_LONG).show();
              break;
          case "pass":
           //   builder.setTitle("تعديل كلمة المرور");
              Toast.makeText(UserProfile.this, "Third Item Clicked", Toast.LENGTH_LONG).show();
              break;
      }



builder.show();



   /*   AlertDialog.Builder dialog = new AlertDialog.Builder(UserProfile.this);
      dialog.setTitle("Set Target Title & Description");
      dialog.setMessage("Title: ");

      Context context = UserProfile.this;
      LinearLayout layout = new LinearLayout(context);
      layout.setOrientation(LinearLayout.VERTICAL);

// Add a TextView here for the "Title" label, as noted in the comments
      final EditText titleBox = new EditText(context);
      titleBox.setHint("Title");
      layout.addView(titleBox); // Notice this is an add method

// Add another TextView here for the "Description" label
      final EditText descriptionBox = new EditText(context);
      descriptionBox.setHint("Description");
      layout.addView(descriptionBox); // Another add method

      dialog.setView(layout); // Again this is a set method, not add*/


    }

    public void showChoiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
        builder.setTitle("تجربة");

//list of items
        final String[] items = {" الاسم ", " البريد الإلكتروني ", " كلمة المرور "};
// set single choice items
        builder.setSingleChoiceItems(items, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which)
                        {
                            case 0:

                                Toast.makeText(UserProfile.this, "First Item Clicked", Toast.LENGTH_LONG).show();
                                break;
                            case 1:

                                Toast.makeText(UserProfile.this, "Second Item Clicked", Toast.LENGTH_LONG).show();
                                break;
                            case 2:

                                Toast.makeText(UserProfile.this, "Third Item Clicked", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                });

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic here
                        showDialog("");
                        // dismiss dialog too


                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
// display dialog
        dialog.show();
    }
}


