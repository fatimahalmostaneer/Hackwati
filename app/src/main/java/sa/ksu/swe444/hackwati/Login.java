package sa.ksu.swe444.hackwati;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private EditText login_password;
    private EditText login_email;
    private  String entered_password;
    private String entered_email;
    private Button loginBtn;
    private Button loginGoogleBtn;
    private TextView createAccount;

    private FirebaseAuth mAuth;
// ...
private final String TAG = "Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        init();


        loginBtn.setOnClickListener(this);
        createAccount.setOnClickListener(this);




    }// end of onCreate()

    private  void init(){

        //init
        login_email = findViewById(R.id.email_login);
        login_password = findViewById(R.id.password_login);
        loginBtn = findViewById(R.id.loginbutton_login);
        loginGoogleBtn = findViewById(R.id.regBtnGoogle);
        createAccount = findViewById(R.id.createAccount);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

    }//end of init

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.loginbutton_login:
                signIn();
                break;
            case R.id.regBtnGoogle:
                googleSignIn();
                break;
            case R.id.createAccount:
                register();
                break;
        }//end of switch


    }//end of onClick

    private void register() {






        //send message

        ActionCodeSettings actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        // URL you want to redirect back to. The domain (www.example.com) for this
                        // URL must be whitelisted in the Firebase Console.
                        .setUrl("https://hackwati444.firebaseapp.com/__/auth/action?mode=<action>&oobCode=<code>")
                        // This must be true
                        .setHandleCodeInApp(true)
                        .setIOSBundleId("com.example.ios")
                        .setAndroidPackageName(
                                "com.example.android",
                                true, /* installIfNotAvailable */
                                "12"    /* minimumVersion */)
                        .build();


    }//end of register

    private void googleSignIn() {
        mAuth.createUserWithEmailAndPassword(entered_email, entered_password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(
                            @NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            // updateUI(null);
                        }

                        // ...
                    }
                });

    }// end of googleSignIn

    private void signIn() {
        //input
        entered_email = login_email.getText().toString();
        entered_password = login_password.getText().toString();

        Log.i("TAG", "email: "+entered_email);

        Log.i("TAG","password: "+entered_password);

        mAuth.signInWithEmailAndPassword(entered_email, entered_password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(Login.this, "Authentication succeeded",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(Login.this, MainActivity.class));
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                           // updateUI(null);
                        }

                        // ...
                    }
                });

    }//end of signin

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }//end of on start


}// end of class
