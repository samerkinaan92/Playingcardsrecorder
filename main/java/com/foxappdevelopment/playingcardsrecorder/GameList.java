package com.foxappdevelopment.playingcardsrecorder;

import android.provider.BaseColumns;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;


public class GameList implements Serializable{
    private long id;
    private Date date;
    private String[] names;
    private ArrayList<Round> rounds = new ArrayList<>();
    private int[] final_score = {0, 0, 0, 0};

    public GameList(String[] names) {
        this.names = names;
        date = new Date();
    }

    public GameList() {
        date = new Date();
    }

    public String[] getNames() {
        return names;
    }

    public void addRound(Round round, int num){
        rounds.add(num, round);
    }

    public Round getRound(int i){
        return rounds.get(i);
    }

    public int getRoundsNum(){
        return rounds.size();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setNames(String[] names) {
        this.names = names;
    }

    public ArrayList<Round> getRounds() {
        return rounds;
    }

    public void setRounds(ArrayList<Round> rounds) {
        this.rounds = rounds;
    }

    public int[] getFinal_score() {
        return final_score;
    }

    public void setFinal_score(int[] final_score) {
        this.final_score = final_score;
    }

    public Round getLastRound() throws NullPointerException{
        if(rounds.size() > 0)
            return rounds.get(rounds.size() - 1);
        else
            throw new NullPointerException();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void updateFinalScore(int id){
        Round round = getRound(id);
        int[] points = round.getPoints();
        boolean[] passed = round.getPassed();
        for (int i = 0; i < 4; i++){
            if(passed[i]) {
                final_score[i] += points[i];
            }else {
                final_score[i] -= points[i];
            }
        }
    }

    public void removeLastRound(){
        Round round = getLastRound();
        int[] points = round.getPoints();
        boolean[] passed = round.getPassed();
        if(round.isFinished()){
            for (int i = 0; i < 4; i++){
                if(passed[i]) {
                    final_score[i] -= points[i];
                }else {
                    final_score[i] += points[i];
                }
            }
        }
        rounds.remove(round);
    }

    public void removeRound(int id){
        Round round = rounds.get(id);
        int[] points = round.getPoints();
        boolean[] passed = round.getPassed();
        if(round.isFinished()){
            for (int i = 0; i < 4; i++){
                if(passed[i]) {
                    final_score[i] -= points[i];
                }else {
                    final_score[i] += points[i];
                }
            }
        }
        rounds.remove(round);
    }

    public static class GameListEntry implements BaseColumns{
        public static final String TABLE_NAME = "game_list";
        public static final String COLUMN_NAME_PLYR_1 = "player1";
        public static final String COLUMN_NAME_PLYR_2 = "player2";
        public static final String COLUMN_NAME_PLYR_3 = "player3";
        public static final String COLUMN_NAME_PLYR_4 = "player4";
        public static final String COLUMN_NAME_SCORE_1 = "score1";
        public static final String COLUMN_NAME_SCORE_2 = "score2";
        public static final String COLUMN_NAME_SCORE_3 = "score3";
        public static final String COLUMN_NAME_SCORE_4 = "score4";
        public static final String COLUMN_NAME_DATE = "date";
    }
}
