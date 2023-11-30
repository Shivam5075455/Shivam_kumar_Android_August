package com.example.db_auth.Fragments;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.db_auth.Models.UsersModel;
import com.example.db_auth.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class EditProfileFragment extends Fragment {

    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int GALLERY_REQUEST_CODE = 2;
    private static final int REQUEST_CHOOSER = 3;
    FirebaseAuth auth;
    Button btnCancel;
    Button btnSave;
    EditText etEPAddress, etEPEmail, etEPPhone, etEPUsername;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;
    Uri imageUri;
    CircleImageView imgEditProfileImage;
    ProgressDialog progressDialog;
    TextView tvEPChangeProfile;
    StorageReference storageReference;
    RadioGroup radioGroup;
    RadioButton radioButton;
    StorageTask uploadTask;
    View view;
    String gender;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        tvEPChangeProfile = view.findViewById(R.id.tvEPChangeProfile);
        imgEditProfileImage = view.findViewById(R.id.imgEditProfileImage);
        etEPUsername = view.findViewById(R.id.etEPUsername);
        etEPEmail = view.findViewById(R.id.etEPEmail);

        etEPAddress = view.findViewById(R.id.etEPAddress);
        etEPPhone = view.findViewById(R.id.etEPPhone);
        btnSave = view.findViewById(R.id.btnSave);
        btnCancel = view.findViewById(R.id.btnCancel);
        radioGroup = view.findViewById(R.id.radioGroupGender);

        int genderId = radioGroup.getCheckedRadioButtonId();
        radioButton = view.findViewById(genderId);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("/profile_images/");

        progressDialog = new ProgressDialog(getContext());

        progressDialog.setMessage("Image uploading");

        auth = firebaseAuth;
        getAndSetData();

        readLocalStoreData();

        btnSave.setOnClickListener(v -> {
            changeUserDatabyUser();
            replaceFragment(new ProfileFragment());
        });

        btnCancel.setOnClickListener(v -> {
            replaceFragment(new ProfileFragment());
        });

        tvEPChangeProfile.setOnClickListener(v -> {
            openImageChooser();

        });

        return view;
    }//onCreateView

    @Override
    public void onPause() {
        super.onPause();
        localStoreData();
    }

    private void openImageChooser() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Intent chooserIntent = Intent.createChooser(pickIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});

        startActivityForResult(chooserIntent, REQUEST_CHOOSER);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            // Handle the chosen image from either camera or gallery
            if (requestCode == REQUEST_CHOOSER) {
                if (data != null && data.getData() != null) {
                    // Image from gallery
                    imageUri = data.getData();
                    imgEditProfileImage.setImageURI(imageUri);
                    progressDialog.show();
                    uploadProfileImage();
                    Log.e("uploadImage", "Uploaded");
                } else {
                    // Image from camera
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");

//                    getImageUri(getContext(),imageBitmap);
                    imgEditProfileImage.setImageBitmap(imageBitmap);
                    progressDialog.show();
                    uploadProfileImage();
                    Log.e("uploadImage", "Uploaded");
                }
            }
        }
    }


    // get the user data from database and set it on the page
    public void getAndSetData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://authentication-2e1cf-default-rtdb.firebaseio.com/").getReference("Users").child(firebaseUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && databaseReference != null) {

//                    added data into UsersModel class and then get from this UsersModel class
                    UsersModel usersModel = snapshot.getValue(UsersModel.class);
                    assert usersModel != null;
                    etEPUsername.setText(usersModel.getUsername());
                    etEPEmail.setText(usersModel.getEmail());
                    etEPPhone.setText(usersModel.getPhoneNumber());
                    etEPAddress.setText(usersModel.getAddress());

                    if (usersModel.getGender() != null) {
                        if (usersModel.getGender().equals("Male")) {
                            radioGroup.check(R.id.radioButtonMale);
                        } else if (usersModel.getGender().equals("Female")) {
                            radioGroup.check(R.id.radioButtonFemale);
                        }
                    }
                    if (usersModel.getImage() != null && !usersModel.getImage().equals("default")) {
                        Glide.with(getContext()).load(usersModel.getImage()).into(imgEditProfileImage);
                    } else {
                        imgEditProfileImage.setImageResource(R.drawable.man);
                        ;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void changeUserDatabyUser() {

        int genderId = radioGroup.getCheckedRadioButtonId();
        radioButton = view.findViewById(genderId);
        gender = radioButton.getText().toString();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://authentication-2e1cf-default-rtdb.firebaseio.com/").getReference("Users").child(firebaseUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    UsersModel usersModel = snapshot.getValue(UsersModel.class);
                    if (usersModel != null) {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("username", etEPUsername.getText().toString());
                        hashMap.put("email", etEPEmail.getText().toString());
                        hashMap.put("address", etEPAddress.getText().toString());
                        hashMap.put("phoneNumber", etEPPhone.getText().toString());
                        hashMap.put("gender", gender);
                        hashMap.put("image", usersModel.getImage());

                        databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    if (isAdded() && getContext() != null) {
                                        Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                                        Log.e("profile", "profile updated");
                                    }
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void localStoreData() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Users", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("username", etEPUsername.getText().toString());
        editor.putString("email", etEPEmail.getText().toString());
        editor.putString("address", etEPAddress.getText().toString());
        editor.putString("phoneNumber", etEPPhone.getText().toString());
        editor.putString("gender", gender);

        editor.commit();
    }

    public void readLocalStoreData() {
        SharedPreferences sp = getContext().getSharedPreferences("Users", Context.MODE_PRIVATE);
        String name = sp.getString("username", "username");
        String email = sp.getString("email", "email");
        String address = sp.getString("address", "address");
        String phoneNumber = sp.getString("phoneNumber", "1234567890");
        String gender = sp.getString("gender", "");

        etEPUsername.setText(name);
        etEPEmail.setText(email);
        etEPAddress.setText(address);
        etEPPhone.setText(phoneNumber);
        radioGroup.check(R.id.radioButtonMale);
        if (gender != null) {
            if (gender.equals("Male")) {
                radioGroup.check(R.id.radioButtonMale);
            } else if (gender.equals("Female")) {
                radioGroup.check(R.id.radioButtonFemale);
            }
        }

    }

    public void uploadProfileImage() {

        if (imageUri != null) {
            StorageReference fileReference = storageReference.child(this.firebaseUser.getUid() + ".jpg");
            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return fileReference.getDownloadUrl();
            }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {

                if (task.isSuccessful()) {
                    Uri downloadImageUri = task.getResult();
                    String myUri = downloadImageUri.toString();
                    saveImage(myUri);
                    progressDialog.dismiss();
                } else {
                    Toast.makeText(getContext(), "Failed to get download URL", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
    }

    private void saveImage(String myUri) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://authentication-2e1cf-default-rtdb.firebaseio.com/").getReference("Users").child(this.firebaseUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UsersModel usersModel = snapshot.getValue(UsersModel.class);
                if (usersModel == null) {
                    throw new AssertionError();
                } else if (snapshot.exists()) {
                    usersModel.setImage(myUri);
                    databaseReference.setValue(usersModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}