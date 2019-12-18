

package co.siarhei.apps.android.geochat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashscreenActivity extends AppCompatActivity {

    Intent intent;
    SharedPreferences prefs;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        prefs = this.getSharedPreferences("geochat", Context.MODE_PRIVATE);


        FirebaseUser currentUser = mAuth.getCurrentUser();





        if (currentUser!=null){

            intent = new Intent(this, MainActivity.class);
        } else {

            intent = new Intent(this, RegistrationActivity.class);
        }


        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        startActivity(intent);
        finish();

    }
}
