package com.foxappdevelopment.playingcardsrecorder;

import android.provider.BaseColumns;
import android.provider.SyncStateContract;

public class Round {

    private long id;
    private int num;
    private int[] points;
    private boolean[] passed = {true, true, true, true};
    private boolean isFinished = false;

    public Round(int num) {
        this.num = num;
    }

    public Round(int num, int[] points) {
        this.num = num;
        this.points = points;
    }

    public int getNum() {
        return num;
    }

    public int[] getPoints() {
        return points;
    }

    public boolean[] getPassed() {
        return passed;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public void setPoints(int[] points) {
        this.points = points;
    }

    public void setPassed(boolean[] passed) {
        this.passed = passed;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public static class RoundEntry implements BaseColumns{
        public static final String TABLE_NAME = "rounds";
        public static final String COLUMN_NAME_NUM = "num";
        public static final String COLUMN_NAME_POINT1 = "point1";
        public static final String COLUMN_NAME_POINT2 = "point2";
        public static final String COLUMN_NAME_POINT3 = "point3";
        public static final String COLUMN_NAME_POINT4 = "point4";
        public static final String COLUMN_NAME_PASS1 = "isPass1";
        public static final String COLUMN_NAME_PASS2 = "isPass2";
        public static final String COLUMN_NAME_PASS3 = "isPass3";
        public static final String COLUMN_NAME_PASS4 = "isPass4";
        public static final String COLUMN_NAME_FINISHED = "isFinished";
        public static final String COLUMN_NAME_GAME_ID = "game_id";

    }
}
