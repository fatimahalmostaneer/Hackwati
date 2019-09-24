package sa.ksu.swe444.hackwati;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private EditText login_password;
    private EditText login_email;
    private  String entered_password;
    private String entered_email;
    private Button loginBtn;
    private SignInButton loginGoogleBtn;
    private TextView createAccount;
    private TextView forgetPassword;
    private ProgressBar progressBar;
    private GoogleSignInOptions gso;


    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private final int RC_SIGN_IN = 1;
    private FirebaseAuth.AuthStateListener authStateListener;
    private boolean verify = false;

    // ...
private final String TAG = "Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        init();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    if(verify) {
                        startActivity(new Intent(Login.this, MainActivity.class));
                    }

                }
            }
        };


        loginBtn.setOnClickListener(this);
        createAccount.setOnClickListener(this);
        loginGoogleBtn.setOnClickListener(this);
        forgetPassword.setOnClickListener(this);

        progressBar.setVisibility(View.GONE);

    }// end of onCreate()

    private  void init(){

        //init
        login_email = findViewById(R.id.email_login);
        login_password = findViewById(R.id.password_login);
        loginBtn = findViewById(R.id.loginbutton_login);
        loginGoogleBtn = findViewById(R.id.regBtnGoogle);
        createAccount = findViewById(R.id.createAccount);
        forgetPassword = findViewById(R.id.forgetText);
        progressBar = findViewById(R.id.progress_bar);
        // Initialize Firebase Auth
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // [START initialize_auth]
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
                Log.e("TAG", "google clicked");
                googleSignIn();
                break;
            case R.id.createAccount:
               // register();
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
                break;
            case R.id.forgetText:
                forgetPassword();
                break;
        }//end of switch


    }//end of onClick

    private void forgetPassword() {
        entered_email = login_email.getText().toString();
        mAuth.sendPasswordResetEmail(entered_email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Login.this, "password is sent",
                            Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(Login.this, "password is NOT sent",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void register() {
        //input

        entered_email = login_email.getText().toString();
        entered_password = login_password.getText().toString();

        mAuth.createUserWithEmailAndPassword(entered_email, entered_password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(Login.this, "createUserWithEmail:success",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            //send email by email to verify user account
                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(Login.this, "check your email ",
                                                Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(Login.this, "check your NOT email ",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed register.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }//end of register


    private void googleSignIn() {
        progressBar.setVisibility(View.VISIBLE);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
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

                            if(mAuth.getCurrentUser().isEmailVerified()){
                                startActivity(new Intent(Login.this, MainActivity.class));
                            }else {
                                Toast.makeText(Login.this, "email not verified",
                                        Toast.LENGTH_SHORT).show();
                            }

                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed google.",
                                    Toast.LENGTH_SHORT).show();
                           // updateUI(null);
                        }

                        // ...
                    }
                });

    }//end of signIn

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        mAuth.addAuthStateListener(authStateListener);

    }//end of on start


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                //updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }
    // [END onactivityresult]


    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
       // showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(mAuth.getCurrentUser().isEmailVerified()){
                                startActivity(new Intent(Login.this, MainActivity.class));
                            }else {
                                Toast.makeText(Login.this, "email not verified",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                           // Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // [START_EXCLUDE]
                       // hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    } // [END auth_with_google]

}// end of class
