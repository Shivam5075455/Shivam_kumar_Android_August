package com.example.db_auth.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.db_auth.LoginActivity;
import com.example.db_auth.Models.UsersModel;
import com.example.db_auth.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {


    FirebaseAuth auth;
    Button btnEdit,btnLogout;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;
    String profileID;
    CircleImageView imgProfileImage;
    TextView tvAddress, tvEmail, tvPhoneNumber, tvUsername,tvGender;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvUsername = view.findViewById(R.id.tvUserName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvPhoneNumber = view.findViewById(R.id.tvPhoneNumber);
        btnEdit = view.findViewById(R.id.btnEdit);
        btnLogout = view.findViewById(R.id.btnLogout);
        tvGender = view.findViewById(R.id.tvGender);
        imgProfileImage = view.findViewById(R.id.imgProfileImage);

        auth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        readLocalUserData();

        btnLogout.setOnClickListener(v->{
            auth.signOut();
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
        });

        btnEdit.setOnClickListener(v->{
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, new EditProfileFragment());
            fragmentTransaction.commit();
        });

        showDetails();
        return view;
    }

    public void readLocalUserData(){

        SharedPreferences sp = getContext().getSharedPreferences("Users", Context.MODE_PRIVATE);
        String name = sp.getString("username", "username");
        String email = sp.getString("email", "email");
        String address = sp.getString("address", "address");
        String phoneNumber = sp.getString("phoneNumber", "1234567890");
        String gender = sp.getString("gender", "");

        tvUsername.setText(name);
        tvEmail.setText(email);
        tvAddress.setText(address);
        tvGender.setText(gender);
        tvPhoneNumber.setText(phoneNumber);
    }
    public void showDetails(){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://authentication-2e1cf-default-rtdb.firebaseio.com/").getReference("Users").child(auth.getCurrentUser().getUid());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    UsersModel usersModel = snapshot.getValue(UsersModel.class);
                    assert usersModel!=null;
                    tvUsername.setText(usersModel.getUsername());
                    tvEmail.setText(usersModel.getEmail());
                    tvAddress.setText(usersModel.getAddress());
                    tvPhoneNumber.setText(usersModel.getPhoneNumber());
                    tvGender.setText(usersModel.getGender());

                    String imageUrl = Objects.requireNonNull(snapshot.child("image").getValue()).toString();

                    if(imageUrl!=null && !imageUrl.equals("default")){
                        Glide.with(getContext()).load(imageUrl).into(imgProfileImage);
                    }else{
                        imgProfileImage.setImageResource(R.drawable.man);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}