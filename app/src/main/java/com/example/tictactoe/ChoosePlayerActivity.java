package com.example.tictactoe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tictactoe.Adapter.UserAdapter;
import com.example.tictactoe.Adapter.RecyclerItemClickListener;

import com.example.tictactoe.model.Game;
import com.example.tictactoe.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ChoosePlayerActivity extends AppCompatActivity {
    private static final String TAG = ChoosePlayerActivity.class.getSimpleName();

    EditText etInviteEMail;
    Button inviteButton;
    private ProgressBar progressBar;

    User currentOpponent;
    User loggedInUser;
    private SharedPreferences mPreferences;
    private DatabaseReference mFirebaseDatabase, notUsingApp;
    private FirebaseDatabase mFirebaseInstance;
    public static final String userid = "userID";
    private String userID;
    private String userEmail, username;
    public static final String SHARED_PREF = "sharedPrefs";
    private UserAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView recyclerView;
    ArrayList<User> arrayOfUsers =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_player);


        recyclerView = (RecyclerView) findViewById(R.id.myListView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);

        SharedPreferences mPreferences = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);

        userID = mPreferences.getString(userid, "userid not set");
        userEmail = mPreferences.getString("userEmail", "email not set");
        username = mPreferences.getString("username", "username not set");
        etInviteEMail = (EditText) findViewById(R.id.InviteEmail);
        progressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        inviteButton = (Button) findViewById(R.id.inviteBtn);
        etInviteEMail.setText(userID);

        adapter = new UserAdapter(this, arrayOfUsers);
        recyclerView.setAdapter(adapter);
    }



    @Override
    protected void onPause() {
        super.onPause();
       arrayOfUsers.clear();
    }

    @Override
    protected void onStart() {
        super.onStart();

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(ChoosePlayerActivity.this,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                currentOpponent = arrayOfUsers.get(position);

                                etInviteEMail.setText(currentOpponent.email);
                            }
                        }));




        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("users").child("usingApp");

        final FirebaseUser currentUserLoggedIn = FirebaseAuth.getInstance().getCurrentUser();

        Query allUsers = mFirebaseDatabase.orderByChild("name");

        allUsers.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                User user = dataSnapshot.getValue(User.class);

                Log.v(TAG, "User data:  " + user.myid + ", " + user.name + ", " + user.email);

                // get the current user from the database
                if (currentUserLoggedIn.getEmail().equals(user.email)) {
                    loggedInUser = user;

                    // go  to the game screen if a game is in progress
                    if (user.currentlyPlaying) {
                        startActivity(new Intent(ChoosePlayerActivity.this, GameActivity.class));
                        finish();
                    }
                }

                // if the other user is not currently playing and they do not have a
                // current request,and they are still using the app then they are a valid opponent to choose
                else if (!user.currentlyPlaying && user.opponentID.isEmpty()) {

                    arrayOfUsers.add(user);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                // get the current user from the database

                arrayOfUsers.remove(user);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
                User user = dataSnapshot.getValue(User.class);
//                String userID = dataSnapshot.getKey();

                // if the other user is not currently playing and they do not have a
                // current request, then they are a valid opponent to choose
                // get the current user from the database
                if (!currentUserLoggedIn.getEmail().equals(user.email)) {
                    if (user.currentlyPlaying || !user.opponentID.isEmpty()) {
                        arrayOfUsers.remove(user);
                        adapter.notifyDataSetChanged();
                    }
                } else {

                    // update your object
                    loggedInUser = user;

                    if (user.request == true) {
                        showAcceptOrDenyInviteDialog();
                        user.request = false;
                        mFirebaseDatabase.child(userID).setValue(user);
                    } else if (user.accepted.equals("true")) {
                        // set values back to initial state and show button
                        progressBar.setVisibility(View.GONE);
                        inviteButton.setEnabled(true);

                        mFirebaseDatabase.child(loggedInUser.myid).child("accepted").setValue("none");

                        // show dialog and go to game screen
                        showAcceptOrDenyStatusDialog(true);
                    } else if (user.accepted.equals("false")) {

                        // set values back to initial state and show button
                        progressBar.setVisibility(View.GONE);
                        mFirebaseDatabase.child(loggedInUser.myid).child("opponentID").setValue("");
                        mFirebaseDatabase.child(loggedInUser.myid).child("opponentEmail").setValue("");
                        mFirebaseDatabase.child(loggedInUser.myid).child("accepted").setValue("none");

                        // show dialog
                        showAcceptOrDenyStatusDialog(false);
                        inviteButton.setEnabled(true);
                    }
                }
            }

        });
        adapter = new UserAdapter(this, arrayOfUsers);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        mFirebaseDatabase = mFirebaseInstance.getReference("users").child("usingApp");
//
//
//        notUsingApp.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(final DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//
//
//                    mFirebaseDatabase.child(dataSnapshot.getKey()).setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
//
//                        @Override
//                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
//                            if (databaseError != null) {
//                                System.out.println("Copy failed");
//                            } else {
//                                System.out.println("Success");
//                                notUsingApp.child(userID).setValue(null);
//                            }
//                        }
//                    });
//
//
//                }
//
//            }
//
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                System.out.println("The read failed: " + databaseError.getCode());
//            }
//        });


    }


//    @Override
//    protected void onPause() {
//
//        notUsingApp = mFirebaseInstance.getReference("users").child("notUsingApp");
//
//        mFirebaseDatabase.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(final DataSnapshot dataSnapshot) {
//                notUsingApp.child(dataSnapshot.getKey()).setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
//                            @Override
//                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
//                                if (databaseError != null) {
//                                    System.out.println("Copy failed");
//                                } else {
//                                    System.out.println("Success");
//                                    mFirebaseDatabase.child(userID).setValue(null);
//
//                                }
//                            }
//                        }
//                );
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                System.out.println("The read failed: " + databaseError.getCode());
//            }
//        });
//
//
//        super.onPause();
//
//    }


    private void showAcceptOrDenyStatusDialog(final boolean status) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Game Invite Status...");

        // Setting Dialog Message
        if (status)
            alertDialog.setMessage("Your game with " + loggedInUser.opponentEmail + " has been accepted");
        else
            alertDialog.setMessage("Your game with " + loggedInUser.opponentEmail + " has been denied");


        // Setting Positive "Yes" Btn
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        // navigate to game screen
                        if (status) {
                            startActivity(new Intent(ChoosePlayerActivity.this, GameActivity.class));
                        }
                    }
                });

        // Showing Alert Dialog
        alertDialog.show();
    }

    private void showAcceptOrDenyInviteDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Accept Game Invite...");

        // Setting Dialog Message
        alertDialog.setMessage("Would you like to play tic tac toe against " + loggedInUser.opponentEmail + "?");

        // Setting Positive "Yes" Btn
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // create game and go there
                        Game game = new Game(loggedInUser.opponentID);

                        mFirebaseDatabase.child(loggedInUser.opponentID).child("myGame").setValue(game);
                        mFirebaseDatabase.child(loggedInUser.myid).child("myGame").setValue(game);

                        // set game status for both players (currently playing)
                        mFirebaseDatabase.child(loggedInUser.opponentID).child("currentlyPlaying").setValue(true);
                        mFirebaseDatabase.child(loggedInUser.myid).child("currentlyPlaying").setValue(true);

                        mFirebaseDatabase.child(loggedInUser.opponentID).child("accepted").setValue("true");

                        // navigate to game screen
                        startActivity(new Intent(ChoosePlayerActivity.this, GameActivity.class));
                    }
                });

        // Setting Negative "NO" Btn
        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mFirebaseDatabase.child(loggedInUser.myid).child("opponentID").setValue("");
                        mFirebaseDatabase.child(loggedInUser.myid).child("opponentEmail").setValue("");
                        mFirebaseDatabase.child(loggedInUser.opponentID).child("accepted").setValue("false");
                        dialog.cancel();
                    }
                });

        // Showing Alert Dialog
        alertDialog.show();
    }

    public void onClickInvite(View view) {
        if (currentOpponent != null) {
            // set opponent id for selected user to invite and let them know they have an invite in database
            mFirebaseDatabase.child(currentOpponent.myid).child("opponentID").setValue(loggedInUser.myid);
            mFirebaseDatabase.child(currentOpponent.myid).child("opponentEmail").setValue(loggedInUser.email);
            mFirebaseDatabase.child(currentOpponent.myid).child("request").setValue(true);

            // set opponent id for current logged in user in database
            mFirebaseDatabase.child(loggedInUser.myid).child("opponentID").setValue(currentOpponent.myid);
            mFirebaseDatabase.child(loggedInUser.myid).child("opponentEmail").setValue(currentOpponent.email);
            mFirebaseDatabase.child(loggedInUser.myid).child("accepted").setValue("pending");

            progressBar.setVisibility(View.VISIBLE);
            inviteButton.setEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();

                startActivity(new Intent(ChoosePlayerActivity.this, MainActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
