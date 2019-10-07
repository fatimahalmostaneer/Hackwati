package sa.ksu.swe444.hackwati;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity implements View.OnClickListener {
    public FirebaseAuth mAuth;
    private final String TAG = "Sign up";
    private EditText register_email;
    private EditText register_password;
    private EditText register_re_password;
    private EditText register_name;
    private Button register_btn;
    private String str;
    private TextView haveAccount ;
    public FirebaseFirestore firebaseFirestore;
    private  FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        init();
        register_btn.setOnClickListener(this);
        haveAccount.setOnClickListener(this);

        this.firebaseFirestore = FirebaseFirestore.getInstance();


    }

    @Override
    protected void onStart() {
        super.onStart();
          FirebaseUser user= mAuth.getCurrentUser();

    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        register_email = findViewById(R.id.email_register);
        register_password = findViewById(R.id.pass_register);
        register_re_password = findViewById(R.id.repass_register);
        register_btn = findViewById(R.id.register_btn);
        haveAccount = findViewById(R.id.haveAccount);
        register_name = findViewById(R.id.username_login);


    }

    private boolean checkPassword(String pass, String repass) {
        if (pass.equals(repass))
            return true;
        else
            return false;
    }

    private void register() {


        if (!checkPassword(register_password.getText().toString(), register_re_password.getText().toString())){
            showErrorMsg();
             return;}
            //input
        String entered_email = register_email.getText().toString();
        String entered_password =register_password.getText().toString();

            mAuth.createUserWithEmailAndPassword(entered_email, entered_password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                Toast.makeText(SignUp.this, "createUserWithEmail:success",
                                        Toast.LENGTH_SHORT).show();
                              user = mAuth.getCurrentUser();
                                //send email by email to verify user account

/*
                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                         *//*   Toast.makeText(SignUp.this, "check your email ",
                                                    Toast.LENGTH_SHORT).show();*//*
                                            showDialogWithOkButton("الرجاء توثيق بريدك الإلكتروني");

                                        } else {
                                            Toast.makeText(SignUp.this, "check your NOT email ",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });*/

                                createUserCollection ();

                                        Intent i = new Intent(SignUp.this, MainActivity.class);
                                i.putExtra("object", mAuth.getInstance().getUid());
                                startActivity(i);

                                        //updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                              /*  Toast.makeText(SignUp.this, "Authentication failed register.",
                                        Toast.LENGTH_SHORT).show();*/
                              showDialogWithOkButton("تحقق من البيانات المدخلة");
                                //updateUI(null);
                            }

                            // ...
                        }
                    });


    }//end of register

    private void showErrorMsg() {
       // Toast.makeText(SignUp.this, "password didn't match", Toast.LENGTH_LONG).show();
        showDialogWithOkButton("كلمتا المرور غير متطابقتين");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.register_btn:
                register();
                break;
            case R.id.haveAccount:
                startActivity(new Intent(SignUp.this, Login.class));
        }
    }

    private void showDialogWithOkButton(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

private void createUserCollection (){

   ;
    Map<String,Object> user = new HashMap<>();
    user.put("username",register_name.getText().toString());
    user.put("email",register_email.getText().toString());
    user.put("urlImg","http://www.i2clipart.com/cliparts/6/d/3/d/clipart-cartoon-reindeer-512x512-6d3d.png");

    MySharedPreference.clearData(this);
    MySharedPreference.putString(this, Constants.Keys.ID, mAuth.getInstance().getUid());
    MySharedPreference.putString(this, Constants.Keys.USER_NAME, register_name.getText().toString());
  //  MySharedPreference.putString(this, Constants.Keys.USER_IMG, register_email.getText().toString());
    MySharedPreference.putString(this, Constants.Keys.USER_EMAIL, register_email.getText().toString());



    firebaseFirestore.collection("users").document(mAuth.getInstance().getUid()).set(user)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(SignUp.this, "user added", Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignUp.this, "Error_add_user", Toast.LENGTH_SHORT).show();
                    Log.d(TAG,e.toString());
                }
            });
}


}
