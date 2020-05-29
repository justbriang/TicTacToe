package com.example.tictactoe.model;

public class User {
    public String name;
    public String myid;
    public String email;
    public String opponentID;
    public String opponentEmail;
    public String accepted;
    public boolean request;
    public boolean currentlyPlaying;
    public boolean usingApp;
    public Game myGame;

    public User() {
    }

    public User(String name, String myid, String email) {
        this.name = name;
        this.myid = myid;
        this.email = email;
        opponentID = "";
        opponentEmail = "";
        accepted = "none";
        request = false;
        myGame = null;
        currentlyPlaying = false;

    }

    public boolean isUsingApp() {
        return usingApp;
    }


}
