package com.example.tictactoe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tictactoe.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEdit, passwordEdit;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference mFirebaseDatabase, Loggedout;
    private FirebaseDatabase mFirebaseDatabaseInstance;
    private TextInputLayout passlay, emailLay;
    private TextView Errordisp;
    private String userId;
    private String emaildb, email, password, confirmpassword, username;

    public static final String SHARED_PREF = "sharedPrefs";
    public static final String userid = "userID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        emailEdit = findViewById(R.id.logEmailEdit);
        passwordEdit = findViewById(R.id.logPasswordEdit);
        progressBar = findViewById(R.id.loading_spinner);
        Errordisp = findViewById(R.id.errorDisp);
        passlay = findViewById(R.id.logPasslay);
        emailLay = findViewById(R.id.logemaillay);



        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabaseInstance = FirebaseDatabase.getInstance();

        //checking the login status
        if (mAuth.getCurrentUser() != null) {
            Intent i = new Intent(this, ChoosePlayerActivity.class);
            startActivity(i);
        }
    }


//login person who already has an account
    public void login(View view) {

        email = emailEdit.getText().toString();
        password = passwordEdit.getText().toString();
        boolean checker=true;
        if (!isValidEmail(email)) {
            emailLay.setError("Invalid email");
            checker=false;
        }
        if(checker) {
            progressBar.setVisibility(View.VISIBLE);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "createUserWithEmail:success");
                                mFirebaseDatabase = mFirebaseDatabaseInstance.getReference("users").child("usingApp");
                                Loggedout = mFirebaseDatabaseInstance.getReference("users").child("NotusingApp");
                                FirebaseUser user = mAuth.getCurrentUser();
                                userId = user.getUid();
//                            Query query = Loggedout.orderByChild("email").equalTo(email);
                                Loggedout.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {

//

                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                                                mFirebaseDatabase.child(dataSnapshot.getKey()).setValue(dataSnapshot.getValue(),
                                                        new DatabaseReference.CompletionListener() {

                                                            @Override
                                                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                                if (databaseError != null) {
                                                                    System.out.println("Copy failed");
                                                                } else {
                                                                    System.out.println("Success");
                                                                    //store user id in the shared preferences
                                                                    SharedPreferences mPreferences =getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
                                                                    SharedPreferences.Editor preferencesEditor = mPreferences.edit();
                                                                    preferencesEditor.putString(userid, userId);

                                                                    preferencesEditor.apply();

                                                                    Loggedout.child(userId).setValue(null);
                                                                    startActivity(new Intent(LoginActivity.this, ChoosePlayerActivity.class));
                                                                }
                                                            }

                                                        });
                                            }
                                        } else {
                                            Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            } else {
                                // If sign in fails, display a message to the user.

                                Errordisp.setText(task.getException().toString());

                            }

                            // ...
                        }
                    });
        }

    }
    public static boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public void launchRegister(View view) {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }
}
