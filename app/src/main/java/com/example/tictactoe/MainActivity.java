package com.example.tictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tictactoe.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private EditText emailEdit, usernameEdit, passwordEdit, passwordConfirmEdit;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseDatabaseInstance;
    private TextInputLayout passConfirm, passinit, emailLay;
    private TextView Errordisp;
    private String userId;
    private String emaildb, email,password,confirmpassword,username;

    public static final String SHARED_PREF="sharedPrefs";
    public static final String userid="userID";

private View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emailEdit = findViewById(R.id.regEmailEdit);
        usernameEdit = findViewById(R.id.regUsernameEdit);
        passwordEdit = findViewById(R.id.regPasswordEdit);
        passwordConfirmEdit = findViewById(R.id.confirmPassword);
        progressBar = findViewById(R.id.loading_spinner);
        //check for password mismatch
        passConfirm = findViewById(R.id.confirmLayout);
        passinit = findViewById(R.id.intiPass);
        //check for email validity
        emailLay = findViewById(R.id.email_layout);
        //error display on failure
        Errordisp = findViewById(R.id.errorDisp);
        if (savedInstanceState != null) {
            email=savedInstanceState.getString("savedEmail");
            emailEdit.setText(email);
            username=savedInstanceState.getString("savedEmail");
            usernameEdit.setText(username);
            password=savedInstanceState.getString("savedEmail");
            passwordEdit.setText(password);
            confirmpassword=savedInstanceState.getString("savedEmail");
            passwordConfirmEdit.setText(confirmpassword);
        }

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabaseInstance = FirebaseDatabase.getInstance();
        if (mAuth.getCurrentUser() != null) {
            Intent i = new Intent(this, ChoosePlayerActivity.class);
            startActivity(i);
        }



    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("savedEmail",emailEdit.getText().toString());
        outState.putString("savedUsername",usernameEdit.getText().toString());
        outState.putString("savedPass",passwordEdit.getText().toString());
        outState.putString("savedPassCon",passwordConfirmEdit.getText().toString());
    }

    @Override
    protected void onStart() {
        email=emailEdit.getText().toString();
        password=passwordEdit.getText().toString();
        confirmpassword=passwordConfirmEdit.getText().toString();
        username=usernameEdit.getText().toString();
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        email=emailEdit.getText().toString();
        password=passwordEdit.getText().toString();
        confirmpassword=passwordConfirmEdit.getText().toString();
        username=usernameEdit.getText().toString();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void signup(View view) {
        email=emailEdit.getText().toString();
        password=passwordEdit.getText().toString();
        confirmpassword=passwordConfirmEdit.getText().toString();
        username=usernameEdit.getText().toString();
        Log.e("Register " ,email);
        if (!password.equals(confirmpassword)) {
            passConfirm.setError("Passwords do not match");
            passinit.setError("Passwords do not match");

        }
        if (password.length() < 8 && !isValidPassword(confirmpassword)) {
            passinit.setError("Passwords is too weak");
        }
        if (!isValidEmail(email)) {
            emailLay.setError("Invalid email");
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);

                        if (!task.isSuccessful()) {
                            Errordisp.setText(task.getException().toString());
                            Log.e("Register " ,task.getException().toString() );
                        } else {

                            mFirebaseDatabase = mFirebaseDatabaseInstance.getReference("users").child("usingApp");
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            userId = user.getUid();
                            emaildb= user.getEmail();
                            SharedPreferences mPreferences =getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
                            SharedPreferences.Editor preferencesEditor = mPreferences.edit();
                            preferencesEditor.putString(userid, userId);
                            preferencesEditor.putString("userEmail", emaildb);
                            preferencesEditor.putString("username",username);
                            preferencesEditor.putString("password",password);
                            preferencesEditor.apply();
                            User myUser = new User(username,userId,emaildb);

                            mFirebaseDatabase.child(userId).setValue(myUser);
                           // User Cuser = new User(username.getText().toString(), email, userId);
                            startActivity(new Intent(MainActivity.this, ChoosePlayerActivity.class));
                            finish();
                        }
                    }
                });

    }

    public static boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    public void launchLogin(View view) {


        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }
}
