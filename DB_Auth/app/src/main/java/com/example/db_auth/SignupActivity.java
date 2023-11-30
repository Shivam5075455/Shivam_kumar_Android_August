package com.example.db_auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class SignupActivity extends AppCompatActivity {


    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;

    EditText etSignupEmail, etSignupUsername;
    TextInputLayout signupTextInputLayoutPasswordConfirm, signupTextInputLayoutPassword;
    TextInputEditText signupTextInputEditTextPasswordConfirm, signupTextInputEditTextPassword;
    Button btnSignup;
    ProgressDialog progressDialog;
    TextView tvSignupLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        signupTextInputLayoutPassword = findViewById(R.id.signupTextInputLayoutPassword);
        signupTextInputLayoutPasswordConfirm = findViewById(R.id.signupTextInputLayoutPasswordConfirm);
        signupTextInputEditTextPassword = findViewById(R.id.signupTextInputEditTextPassword);
        signupTextInputEditTextPasswordConfirm = findViewById(R.id.signupTextInputEditTextPasswordConfirm);
        etSignupEmail = findViewById(R.id.etSignupEmail);
        etSignupUsername = findViewById(R.id.etSignupUsername);
        btnSignup = findViewById(R.id.btnSignup);
        tvSignupLogin = findViewById(R.id.tvSignupLogin);

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(this);

        btnSignup.setOnClickListener(view -> {
            String username = etSignupUsername.getText().toString();
            String password = Objects.requireNonNull(signupTextInputEditTextPassword.getText()).toString();
            String email = etSignupEmail.getText().toString();
            String confirmPassword = Objects.requireNonNull(signupTextInputEditTextPasswordConfirm.getText()).toString();

            progressDialog.setMessage("Creating account...");

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(email) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show();

            } else if (password.equals(confirmPassword)) { //check password and confirm password is same.
                progressDialog.setCancelable(false);
                progressDialog.show();
                signup(username, email, password);
            } else {
                Toast.makeText(this, "Password didn't match", Toast.LENGTH_SHORT).show();
            }


        });


        tvSignupLogin.setOnClickListener(view -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
    }//onCreate

    public void signup(String username, String email, String password) {


        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://authentication-2e1cf-default-rtdb.firebaseio.com/").getReference("Users").child(uid);
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id", auth.getCurrentUser().getUid());
                    hashMap.put("username", username);
                    hashMap.put("email", email);
                    hashMap.put("address","");
                    hashMap.put("gender","");
                    hashMap.put("phoneNumber","");
                    hashMap.put("image","default");

                    databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //show progress dialog message(Creating account)
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                sendVerfyEmail();
                                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                finish();
                            }else{
                                progressDialog.dismiss();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignupActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }//signup

    public void sendVerfyEmail() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(SignupActivity.this, "Verification email sent successfully ", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignupActivity.this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}