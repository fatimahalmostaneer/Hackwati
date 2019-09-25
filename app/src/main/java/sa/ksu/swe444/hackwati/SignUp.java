package sa.ksu.swe444.hackwati;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private final String TAG = "Sign up";
    private EditText register_email;
    private EditText register_password;
    private EditText register_re_password;
    private Button register_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        init();
        register_btn.setOnClickListener(this);

    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        register_email = findViewById(R.id.email_register);
        register_password = findViewById(R.id.pass_register);
        register_re_password = findViewById(R.id.repass_register);
        register_btn = findViewById(R.id.register_btn);


    }

    private boolean checkPassword(String pass, String repass) {
        if (pass.equals(repass))
            return true;
        else
            return false;
    }

    private void register() {



        if (!checkPassword(register_password.getText().toString(), register_re_password.getText().toString()))
            showErrorMsg();

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
                                FirebaseUser user = mAuth.getCurrentUser();
                                //send email by email to verify user account
                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(SignUp.this, "check your email ",
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(SignUp.this, "check your NOT email ",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                //updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SignUp.this, "Authentication failed register.",
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }

                            // ...
                        }
                    });
    }//end of register

    private void showErrorMsg() {
        Toast.makeText(SignUp.this, "password didn't match", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.register_btn:
                register();
        }
    }
}
