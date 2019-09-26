package sa.ksu.swe444.hackwati;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class UserProfile extends AppCompatActivity {
    Button log_out;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        log_out = findViewById(R.id.logout_profile);
        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
    }
    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        startActivity( new Intent(UserProfile.this, Login.class));
    }//end of signOut
}
