package com.foxappdevelopment.playingcardsrecorder;

import java.util.Date;

/**
 * Created by sam_f_000 on 12/3/2016.
 */

public class GameListDemo {
    private long id;
    private Date date;
    private String[] names;
    private int[] final_score;

    public GameListDemo(long id, Date date, String[] names, int[] final_score) {
        this.id = id;
        this.date = date;
        this.names = names;
        this.final_score = final_score;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String[] getNames() {
        return names;
    }

    public void setNames(String[] names) {
        this.names = names;
    }

    public int[] getFinal_score() {
        return final_score;
    }

    public void setFinal_score(int[] final_score) {
        this.final_score = final_score;
    }
}
