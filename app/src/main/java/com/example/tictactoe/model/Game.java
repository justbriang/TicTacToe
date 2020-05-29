package com.example.tictactoe.model;

public class Game {
    public boolean gameInProgress;
    public String currentTurn;
    public int currentMove;
    public String currentLetter;

    public Game() {
    }

    public Game(String currentTurn) {
        this.currentTurn = currentTurn;
        this.currentLetter="X";
        this.gameInProgress=true;
        this.currentMove=-1;
    }
}
