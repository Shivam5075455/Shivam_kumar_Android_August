package com.example.db_auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.os.Bundle;
import android.widget.Toast;

import com.example.db_auth.Fragments.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView messageTextView;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        progressBar = findViewById(R.id.progressBar);
//        messageTextView = findViewById(R.id.messageTextView);

        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.status_bar));

        auth = FirebaseAuth.getInstance();
        verifyUser();


//        replaceFragment(new ProfileFragment());


        // Simulating a task that takes time
//        simulateTask();
    }//onCreate

//    private void simulateTask() {
//        // Show progress bar
//        progressBar.setVisibility(View.VISIBLE);
//
//        // Simulate a task that takes time
//        new Handler(Looper.getMainLooper()).postDelayed(() -> {
//
//            messageTextView.setText("Task is in progress...");
//            // Hide progress bar
//            progressBar.setVisibility(View.GONE);
//
//            // Update message text
//            messageTextView.setVisibility(View.VISIBLE);
//            messageTextView.setText("Task completed!");
//
//            // You can customize the message and behavior based on your requirements
//        }, 3000); // Simulating a task that takes 3 seconds
//    }//simulateTask

    public void verifyUser() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null || !user.isEmailVerified()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            Toast.makeText(this, "Please verify your email", Toast.LENGTH_SHORT).show();
            finish();
            Log.e("notVerify","null or not verify");
        } else if (user != null && user.isEmailVerified()) {
            replaceFragment(new ProfileFragment());
            Log.e("verify","user is verified");
        }

    }//verify

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

}