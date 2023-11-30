package com.example.db_auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {


    TextView tvLoginSignup,tvForgotPassword;
    EditText etLoginEmail;
    Button btnLogin;
    TextInputEditText textInputLayoutEdittextPassword;
    TextInputLayout textInputLayoutPassword;
    FirebaseAuth auth;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        tvLoginSignup=findViewById(R.id.tvLoginSignup);
        tvForgotPassword=findViewById(R.id.tvForgotPassword);
        etLoginEmail=findViewById(R.id.etLoginEmail);
        textInputLayoutPassword=findViewById(R.id.textInputLayout);
        textInputLayoutEdittextPassword=findViewById(R.id.textInputLayoutEdittextPassword);
        btnLogin=findViewById(R.id.btnLogin);

        auth=FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);


        tvLoginSignup.setOnClickListener(v->{
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            finish();
        });

        btnLogin.setOnClickListener(view->{
            String email = etLoginEmail.getText().toString();
            String password = textInputLayoutEdittextPassword.getText().toString();

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                Toast.makeText(this, "Please fill details", Toast.LENGTH_SHORT).show();
            }else{
                progressDialog.setMessage("Logging...");
                progressDialog.setCancelable(false);
                login(email,password);

            }

        });



    }//onCreate

    public void login(String email, String password){

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Failed to login", Toast.LENGTH_SHORT).show();
            }
        });
    }//login


}