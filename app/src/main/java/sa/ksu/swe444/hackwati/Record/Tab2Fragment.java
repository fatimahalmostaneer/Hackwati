package sa.ksu.swe444.hackwati.Record;


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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import sa.ksu.swe444.hackwati.R;


public class Tab2Fragment extends Fragment {

    private View view;
    private String name;
    private  TextView text;
    private Button button ;
    private ImageView img;
    private boolean isSelectImage;
    private static int INTENT_CAMERA = 401;
    private static int INTENT_GALLERY = 301;
    private File imgFile;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.recording_fragment_two, container, false);
        text = view.findViewById(R.id.pp);
        text.setText(name);
        button = view.findViewById(R.id.submit);
        img = view.findViewById(R.id.Img);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCameraChooser();

            }
        });


        return view;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_CANCELED) {
            return;
        }//end if statement
        if (requestCode == INTENT_GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentURI);
                    // String path = saveImage(bitmap);
                    Toast.makeText(getContext(), "Image Saved!", Toast.LENGTH_SHORT).show();
                    img.setImageBitmap(bitmap);
                    isSelectImage = true;
                    persistImage(bitmap);

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

}