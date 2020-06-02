package com.example.tictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

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
    private TextInputLayout passConfirmLay, passLay, emailLay;
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
        getSupportActionBar().hide();
        //edit texts
        emailEdit = findViewById(R.id.regEmailEdit);
        usernameEdit = findViewById(R.id.regUsernameEdit);
        passwordEdit = findViewById(R.id.regPasswordEdit);
        passwordConfirmEdit = findViewById(R.id.confirmPassword);

        //progressbar
        progressBar = findViewById(R.id.loading_spinner);

        //check for password mismatch
        passConfirmLay = findViewById(R.id.confirmLayout);
        passLay = findViewById(R.id.intiPass);

        // email validity error display
        emailLay = findViewById(R.id.email_layout);

        //error display on signupfailure
        Errordisp = findViewById(R.id.errorDisp);



        //initializing firebase auth and databas instance
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabaseInstance = FirebaseDatabase.getInstance();

        //checking the login status
        if (mAuth.getCurrentUser() != null) {
            Intent i = new Intent(this, ChoosePlayerActivity.class);
            startActivity(i);
        }



    }



    public void signup(View view) {
        email=emailEdit.getText().toString();
        password=passwordEdit.getText().toString();
        confirmpassword=passwordConfirmEdit.getText().toString();
        username=usernameEdit.getText().toString();
        Log.e("Register " ,email);
        boolean checker=true;

        //checking the password and email for validity
        if (!password.equals(confirmpassword)) {
            passConfirmLay.setError("Passwords do not match");
            passLay.setError("Passwords do not match");
                    checker=false;
        }
        if (password.length() < 8 && !isValidPassword(confirmpassword)) {
            passLay.setError("Passwords is too weak");
            checker=false;
        }
        if (!isValidEmail(email)) {
            emailLay.setError("Invalid email");
            checker=false;
        }

        if(checker){
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
                            //storing shared preferences
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

//    public void clear(View view) {
//        Errordisp.setText("");
//        emailLay.setError(null);
//        passLay.setError("");
//        passConfirmLay.setError("");
//    }
}
