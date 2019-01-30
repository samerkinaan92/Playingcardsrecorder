package com.foxappdevelopment.playingcardsrecorder;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TableActivity extends AppCompatActivity{

    private GameList gameList;
    private ListView listView;
    private RoundViewAdapter mAdapter;
    private TextView[] fp;
    private TextView[] hp;
    FloatingActionButton fab_add, fab_conform;
    private GameListDbHelper mDbHelper;
    private String[] namesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // gets a reference for the database
        mDbHelper = new GameListDbHelper(getApplicationContext());

        gameList = new GameList();


        /* future code for landscape mode
        //checks if the screen was rotated
        if(savedInstanceState != null){
            gameList.setNames(savedInstanceState.getStringArray("names"));
            int roundsNum = savedInstanceState.getInt("roundsNum");
            for(int i = 0; i < roundsNum; i++){
                Round round = new Round(i + 1, savedInstanceState.getIntArray("points" + i));
                round.setFinished(savedInstanceState.getBoolean("finished" + i));
                round.setPassed(savedInstanceState.getBooleanArray("passed" + i));
                gameList.addRound(round);
            }
            gameList.setFinal_score(savedInstanceState.getIntArray("finalScore"));
        }*/

        //get the reference for list view
        listView = (ListView)findViewById(R.id.list_view);
        //set the adapter
        mAdapter = new RoundViewAdapter(getApplicationContext(), gameList.getRounds());

        //sets the fabs and their actions
        fab_add = (FloatingActionButton) findViewById(R.id.fab_add_round);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddRoundDialog(false);
            }
        });

        fab_conform = (FloatingActionButton) findViewById(R.id.fab_conform_points);
        fab_conform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConformRoundDialog(gameList.getRoundsNum() - 1);
            }
        });

        //sets list item actions
        setListViewItem();

        //gets data from the history activity
        Intent data = getIntent();

        //checks if theirs any data
        if(null != data) {
            //checks if new game started
            if (data.getBooleanExtra("new", true)) {
                namesList = data.getStringArrayExtra("namesList");
                showEnterNamesDialog(false, namesList);
            }else {//else opens existing game
                long id = data.getLongExtra("id", 0);
                if(0 != id){
                    openExistingGame(id);
                    setHeaderFooter(gameList.getNames());
                    updateFinalScore();
                }
            }
        }

        //sets the final state of the game
        try {
            if(gameList.getLastRound().isFinished()){
                fab_add.show();
                fab_conform.hide();
            }else {
                fab_add.hide();
                fab_conform.show();
            }
        }catch (NullPointerException e){
            fab_conform.hide();
        }


    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_table, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    public void onBackPressed(){
        //returns the current game data to the history activity
        Intent intent = new Intent();
        intent.putExtra("id", gameList.getId());
        intent.putExtra("names", gameList.getNames());
        intent.putExtra("points", gameList.getFinal_score());
        intent.putExtra("date", gameList.getDate().getTime());
        setResult(RESULT_OK, intent);
        finish();
    }

    /* future code for landscape mode
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if(gameList != null){
            savedInstanceState.putStringArray("names", gameList.getNames());
            savedInstanceState.putInt("roundsNum", gameList.getRoundsNum());
            ArrayList<Round> rounds = gameList.getRounds();
            Round round;
            for(int i = 0; i < gameList.getRoundsNum(); i++){
                round = rounds.get(i);
                savedInstanceState.putIntArray("points" + i, round.getPoints());
                savedInstanceState.putBooleanArray("passed" + i, round.getPassed());
                savedInstanceState.putBoolean("finished" + i, round.isFinished());
            }
            savedInstanceState.putIntArray("finalScore", gameList.getFinal_score());
        }
    }*/

    //sets the header and footer values
    private void setHeaderFooter(String[] names){
        //sets the header and footer layout
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup footer = (ViewGroup) inflater.inflate(R.layout.footer, listView,
                false);

        //sets header onClick action
        LinearLayout header = (LinearLayout)findViewById(R.id.header);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEnterNamesDialog(true, namesList);
            }
        });

        //finds headers names views
        hp = new TextView[4];
        hp[0] = (TextView)findViewById(R.id.first_player_tv_h);
        hp[1] = (TextView)findViewById(R.id.second_player_tv_h);
        hp[2] = (TextView)findViewById(R.id.third_player_tv_h);
        hp[3] = (TextView)findViewById(R.id.fourth_player_tv_h);

        //set the names in textViews
        for(int i = 0; i < 4; i++)
            hp[i].setText(names[i]);

        // add the score to footer
        listView.addFooterView(footer, null, false);

        //finds footer points views
        fp = new TextView[4];
        fp[0] = (TextView) findViewById(R.id.first_player_tv_f);
        fp[1] = (TextView) findViewById(R.id.second_player_tv_f);
        fp[2] = (TextView) findViewById(R.id.third_player_tv_f);
        fp[3] = (TextView) findViewById(R.id.fourth_player_tv_f);
        listView.setAdapter(mAdapter);

        /*AdView mAdView = (AdView) findViewById(R.id.table_banner_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/
    }

    private void updateFinalScore(){
        //sets the final score in the footer layout
        int[] fianl = gameList.getFinal_score();
        for(int i = 0; i < 4; i++)
            fp[i].setText(Integer.toString(fianl[i]));
        mAdapter.notifyDataSetChanged();
    }

    private void conformRoundInDB(Round round){
        // Gets the data repository in read mode
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        boolean[] pass = round.getPassed();
        // New value for passed columns and final score
        ContentValues values = new ContentValues();
        if(!pass[0]) {
            values.put(Round.RoundEntry.COLUMN_NAME_PASS1, 0);
        }
        if(!pass[1]) {
            values.put(Round.RoundEntry.COLUMN_NAME_PASS2, 0);
        }
        if(!pass[2]) {
            values.put(Round.RoundEntry.COLUMN_NAME_PASS3, 0);
        }
        if(!pass[3]) {
            values.put(Round.RoundEntry.COLUMN_NAME_PASS4, 0);
        }
        values.put(Round.RoundEntry.COLUMN_NAME_FINISHED, 1);

        // Which row to update, based on the id
        String selection = Round.RoundEntry._ID + " = " + round.getId();
        db.update(Round.RoundEntry.TABLE_NAME, values, selection, null);

        //clears the contentValues for a new query
        values.clear();
        //gets the new score and updates it in the database
        int[] finalScore = gameList.getFinal_score();
        values.put(GameList.GameListEntry.COLUMN_NAME_SCORE_1, finalScore[0]);
        values.put(GameList.GameListEntry.COLUMN_NAME_SCORE_2, finalScore[1]);
        values.put(GameList.GameListEntry.COLUMN_NAME_SCORE_3, finalScore[2]);
        values.put(GameList.GameListEntry.COLUMN_NAME_SCORE_4, finalScore[3]);

        selection = GameList.GameListEntry._ID + " = " + gameList.getId();
        db.update(GameList.GameListEntry.TABLE_NAME, values, selection, null);
    }

    private void removeRound(int i){
        //sets add new round state
        if(!gameList.getRound(i).isFinished()) {
            fab_add.show();
            fab_conform.hide();
        }
        //delete round from the DB
        deleteRoundInDB(gameList.getRound(i));
        //remove the round from arrayList
        gameList.removeRound(i);
        mAdapter.notifyDataSetChanged();
        updateFinalScore();
    }

    private void showDeleteWarning(final int i){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Add the buttons
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked delete button
                removeRound(i);
                // notify the user that the last was deleted successfully
                Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.delete_toast), Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });
        builder.setMessage(R.string.dialog_message)
                .setTitle(R.string.dialog_title);

        //creates and shows the delete dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showEnterNamesDialog(final boolean edit, String[] namesList){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        //sets the enter names dialog layout
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.enter_names_dialog, null);
        dialogBuilder.setView(dialogView);

        //sets the auto complete textViews vies and adapter
        final AutoCompleteTextView[] plyrs_et = new AutoCompleteTextView[4];

        plyrs_et[0] = (AutoCompleteTextView)dialogView.findViewById(R.id.plyr1_et_dialog);
        plyrs_et[1] = (AutoCompleteTextView)dialogView.findViewById(R.id.plyr2_et_dialog);
        plyrs_et[2] = (AutoCompleteTextView)dialogView.findViewById(R.id.plyr3_et_dialog);
        plyrs_et[3] = (AutoCompleteTextView)dialogView.findViewById(R.id.plyr4_et_dialog);

        //sets all the names that ever played for auto complete
        if(null != namesList) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, namesList);

            for (int i = 0; i < 4; i++) {
                plyrs_et[i].setAdapter(adapter);
            }

        }

        //checks if the trying to edit the names
        if(edit){
            //set the pre set names
            String[] names = gameList.getNames();
            for (int i = 0; i < 4; i++){
                plyrs_et[i].setText(names[i]);
            }

            //enables the cancel button
            dialogBuilder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
        }else {
            dialogBuilder.setCancelable(false);

            //enables the cancel button
            dialogBuilder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            });
        }

        //sets the dialog title and pos buttons
        dialogBuilder.setTitle(getResources().getString(R.string.enter_players_names));
        dialogBuilder.setPositiveButton(getResources().getString(R.string.submit_btn), null);


        //sets the pos button action
        final AlertDialog b = dialogBuilder.create();
        b.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button submit = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // gets the texts input
                        String[] names = new String[4];
                        for(int j = 0; j < 4 ; j++){
                            names[j] = plyrs_et[j].getText().toString();
                        }
                        //checks if the input is valid
                        if(names[0].isEmpty() || names[1].isEmpty() || names[2].isEmpty() || names[3].isEmpty()) {
                            //if not valid notify the user
                            Toast error = Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_alert), Toast.LENGTH_SHORT);
                            error.show();
                        }else {
                            if(edit){
                                //set the names in textViews
                                for(int j = 0; j < 4; j++)
                                    hp[j].setText(names[j]);

                                gameList.setNames(names);
                                updateNamesInDB();
                            }else {
                                gameList.setNames(names);
                                setHeaderFooter(names);
                                addGameToDB();
                            }
                            b.dismiss();
                        }
                    }
                });
            }
        });
        b.show();
    }

    private void showAddRoundDialog(final boolean edit){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        //sets the add round dialog layout
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_round_dialog, null);
        dialogBuilder.setView(dialogView);

        //gets the views references
        final EditText[] points_et = new EditText[4];
        final TextView[] plyrs = new TextView[4];

        points_et[0] = (EditText)dialogView.findViewById(R.id.editText_point1);
        points_et[1] = (EditText)dialogView.findViewById(R.id.editText_point2);
        points_et[2] = (EditText)dialogView.findViewById(R.id.editText_point3);
        points_et[3] = (EditText)dialogView.findViewById(R.id.editText_point4);

        plyrs[0] = (TextView)dialogView.findViewById(R.id.add_round_dialog_tv1);
        plyrs[1] = (TextView)dialogView.findViewById(R.id.add_round_dialog_tv2);
        plyrs[2] = (TextView)dialogView.findViewById(R.id.add_round_dialog_tv3);
        plyrs[3] = (TextView)dialogView.findViewById(R.id.add_round_dialog_tv4);

        //sets the names texts in the views
        String[] names = gameList.getNames();
        for (int i = 0; i < 4; i++){
            plyrs[i].setText(names[i]);
        }

        // if editing the round, sets the pre set bids in the views
        if(edit){
            int[] points = gameList.getLastRound().getPoints();

            for (int i = 0; i < 4; i++){
                points_et[i].setText(Integer.toString(points[i]));
            }
        }

        //sets the title pos button action
        dialogBuilder.setTitle(getResources().getString(R.string.add_round_title));
        dialogBuilder.setPositiveButton(getResources().getString(R.string.submit_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //checks if the input is valid
                try {
                    int[] points = new int[4];
                    for(int j = 0; j < 4; j++){
                        points[j] = Integer.parseInt(points_et[j].getText().toString());
                    }
                    //if editing the round, updates the old values
                    if(edit){
                        Round round = gameList.getLastRound();
                        round.setPoints(points);
                        updatePointsInDB(round.getId(), points);
                        mAdapter.notifyDataSetChanged();
                    }else {//else ads a new round to the arrayList
                        Round round;
                        if(gameList.getRoundsNum() == 0){
                            round = new Round(1, points);
                        }else {
                            round = new Round(gameList.getLastRound().getNum() + 1, points);
                        }
                        mAdapter.addItem(round);
                        addRoundToDB(round);
                        fab_add.hide();
                        fab_conform.show();
                    }
                }catch (Exception e){
                    // notify the user that the input is invalid
                    Toast error = Toast.makeText(getApplicationContext(), getResources().getString(R.string.add_points_error), Toast.LENGTH_SHORT);
                    error.show();
                }
            }
        });

        //sets the neg button for closing the dialog
        dialogBuilder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        //creates and shows the dialog
        AlertDialog b = dialogBuilder.create();
        b.show();

    }

    private void showConformRoundDialog(final int roundNum){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        //sets the dialog layout
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.conform_points_dialog, null);
        dialogBuilder.setView(dialogView);

        //gets the views references
        final EditText[] points_tv = new EditText[4];
        final TextView[] plyrs_tv = new TextView[4];
        final CheckBox[] checkBoxes = new CheckBox[4];

        plyrs_tv[0] = (TextView)dialogView.findViewById(R.id.player1_name_cp);
        plyrs_tv[1] = (TextView)dialogView.findViewById(R.id.player2_name_cp);
        plyrs_tv[2] = (TextView)dialogView.findViewById(R.id.player3_name_cp);
        plyrs_tv[3] = (TextView)dialogView.findViewById(R.id.player4_name_cp);

        points_tv[0] = (EditText)dialogView.findViewById(R.id.player1_points_cp);
        points_tv[1] = (EditText)dialogView.findViewById(R.id.player2_points_cp);
        points_tv[2] = (EditText)dialogView.findViewById(R.id.player3_points_cp);
        points_tv[3] = (EditText)dialogView.findViewById(R.id.player4_points_cp);

        checkBoxes[0] = (CheckBox)dialogView.findViewById(R.id.checkBox1);
        checkBoxes[1] = (CheckBox)dialogView.findViewById(R.id.checkBox2);
        checkBoxes[2] = (CheckBox)dialogView.findViewById(R.id.checkBox3);
        checkBoxes[3] = (CheckBox)dialogView.findViewById(R.id.checkBox4);

        //sets the names and bids in views
        final Round round = gameList.getRound(roundNum);
        String[] names = gameList.getNames();
        final int[] points = round.getPoints();


        for(int i = 0; i < 4; i++){
            plyrs_tv[i].setText(names[i]);
            points_tv[i].setText(Integer.toString(points[i]));
            final int finalI = i;
            //sets the checkBoxes onChange listener
            checkBoxes[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(!b){
                        checkBoxes[finalI].setText(getResources().getString(R.string.passed));
                    }else {
                        checkBoxes[finalI].setText(getResources().getString(R.string.didn_t_pass));
                    }
                }
            });

            if(round.isFinished())
                checkBoxes[i].setChecked(!round.getPassed()[i]);
        }

        //sets neg button for closing the dialog
        dialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        dialogBuilder.setNeutralButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showDeleteWarning(roundNum);
            }
        });

        //sets the pos button
        dialogBuilder.setPositiveButton(getString(R.string.submit_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //gets the checkBoxes values
                try {
                    boolean[] passed = new boolean[4];
                    int[] NewPoints = new int[4];
                    boolean changedPoints = false;
                    for (int j = 0; j < 4; j++) {
                        passed[j] = !checkBoxes[j].isChecked();
                        NewPoints[j] = Integer.parseInt(points_tv[j].getText().toString());
                        if(points[j] != NewPoints[j])
                            changedPoints = true;
                    }
                    //update the new values
                    if (!round.isFinished()) {
                        if(changedPoints)
                            round.setPoints(NewPoints);
                        round.setPassed(passed);
                        round.setFinished(true);
                        gameList.updateFinalScore(roundNum);
                        conformRoundInDB(round);
                    } else {
                        deleteRoundInDB(round);
                        gameList.removeRound(roundNum);
                        Round round = new Round(roundNum + 1, NewPoints);
                        round.setPassed(passed);
                        round.setFinished(true);
                        gameList.addRound(round, roundNum);
                        gameList.updateFinalScore(roundNum);
                        addRoundToDB(round);
                        conformRoundInDB(round);
                    }
                    updateFinalScore();
                    fab_conform.hide();
                    fab_add.show();
                }catch (Exception e){
                    // notify the user that the input is invalid
                    Toast error = Toast.makeText(getApplicationContext(), getResources().getString(R.string.add_points_error), Toast.LENGTH_SHORT);
                    error.show();
                }
            }
        });

        //creates and shows the dialog
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void deleteRoundInDB(Round round){
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //deletes the round from DB by id
        String selection = Round.RoundEntry._ID + " = ?";
        String[] arg = {Long.toString(round.getId())};
        db.delete(Round.RoundEntry.TABLE_NAME, selection, arg);
    }

    private void setListViewItem(){
        //sets on click action
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //if the round is not finished, it opens the add round dialog
                if(!(gameList.getRound(i).isFinished())) {
                    showAddRoundDialog(true);
                }else {//if it is finished, it opens the conform dialog
                    showConformRoundDialog(i);
                }
            }
        });
    }

    private void addGameToDB(){
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String[] names = gameList.getNames();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        //inserts the new games values to the DB
        values.put(GameList.GameListEntry.COLUMN_NAME_PLYR_1, names[0]);
        values.put(GameList.GameListEntry.COLUMN_NAME_PLYR_2, names[1]);
        values.put(GameList.GameListEntry.COLUMN_NAME_PLYR_3, names[2]);
        values.put(GameList.GameListEntry.COLUMN_NAME_PLYR_4, names[3]);
        values.put(GameList.GameListEntry.COLUMN_NAME_SCORE_1, 0);
        values.put(GameList.GameListEntry.COLUMN_NAME_SCORE_2, 0);
        values.put(GameList.GameListEntry.COLUMN_NAME_SCORE_3, 0);
        values.put(GameList.GameListEntry.COLUMN_NAME_SCORE_4, 0);
        values.put(GameList.GameListEntry.COLUMN_NAME_DATE, getDateTime());
        long newGameId = db.insert(GameList.GameListEntry.TABLE_NAME, null, values);
        gameList.setId(newGameId);
    }

    private void addRoundToDB(Round round){
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int[] points = round.getPoints();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Round.RoundEntry.COLUMN_NAME_NUM, round.getNum());
        values.put(Round.RoundEntry.COLUMN_NAME_POINT1, points[0]);
        values.put(Round.RoundEntry.COLUMN_NAME_POINT2, points[1]);
        values.put(Round.RoundEntry.COLUMN_NAME_POINT3, points[2]);
        values.put(Round.RoundEntry.COLUMN_NAME_POINT4, points[3]);
        values.put(Round.RoundEntry.COLUMN_NAME_PASS1, 1);
        values.put(Round.RoundEntry.COLUMN_NAME_PASS2, 1);
        values.put(Round.RoundEntry.COLUMN_NAME_PASS3, 1);
        values.put(Round.RoundEntry.COLUMN_NAME_PASS4, 1);
        values.put(Round.RoundEntry.COLUMN_NAME_FINISHED, 0);
        values.put(Round.RoundEntry.COLUMN_NAME_GAME_ID, gameList.getId());
        //inserts the new round to DB
        long newRoundId = db.insert(Round.RoundEntry.TABLE_NAME, null, values);
        round.setId(newRoundId);
    }

    private void openExistingGame(long id){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        String[] projection = {"*"};

        // Filter results WHERE "id" = id
        String selection = GameList.GameListEntry._ID + " = ?";
        String[] selectionArgs = { Long.toString(id) };

        Cursor c = db.query(GameList.GameListEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        String[] names = new String[4];
        int[] score = new int[4];
        if(null != c) {
            c.moveToFirst();
            //get the names from the cursor
            names[0] = c.getString(c.getColumnIndex(GameList.GameListEntry.COLUMN_NAME_PLYR_1));
            names[1] = c.getString(c.getColumnIndex(GameList.GameListEntry.COLUMN_NAME_PLYR_2));
            names[2] = c.getString(c.getColumnIndex(GameList.GameListEntry.COLUMN_NAME_PLYR_3));
            names[3] = c.getString(c.getColumnIndex(GameList.GameListEntry.COLUMN_NAME_PLYR_4));

            //get the final score from the cursor
            score[0] = c.getInt(c.getColumnIndex(GameList.GameListEntry.COLUMN_NAME_SCORE_1));
            score[1] = c.getInt(c.getColumnIndex(GameList.GameListEntry.COLUMN_NAME_SCORE_2));
            score[2] = c.getInt(c.getColumnIndex(GameList.GameListEntry.COLUMN_NAME_SCORE_3));
            score[3] = c.getInt(c.getColumnIndex(GameList.GameListEntry.COLUMN_NAME_SCORE_4));

            //get the date
            String s = c.getString(c.getColumnIndex(GameList.GameListEntry.COLUMN_NAME_DATE));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d = new Date();
            try {
                d = dateFormat.parse(s);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            gameList.setNames(names);
            gameList.setId(id);
            gameList.setFinal_score(score);
            gameList.setDate(d);
        }

        selection = Round.RoundEntry.COLUMN_NAME_GAME_ID + " = ?";
        // How you want the results sorted in the resulting Cursor
        String sortOrder = Round.RoundEntry.COLUMN_NAME_NUM + " ASC";
        Cursor cursor = db.query(Round.RoundEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

        long roundId;
        int roundNum, temp;
        int[] roundPoints;
        boolean[] pass;
        boolean finished = true;
        Round newRound;

        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            //get round id and num
            roundId = cursor.getLong(cursor.getColumnIndex(Round.RoundEntry._ID));
            roundNum = cursor.getInt(cursor.getColumnIndex(Round.RoundEntry.COLUMN_NAME_NUM));

            //get round points
            roundPoints = new int[4];
            roundPoints[0] = cursor.getInt(cursor.getColumnIndex(Round.RoundEntry.COLUMN_NAME_POINT1));
            roundPoints[1] = cursor.getInt(cursor.getColumnIndex(Round.RoundEntry.COLUMN_NAME_POINT2));
            roundPoints[2] = cursor.getInt(cursor.getColumnIndex(Round.RoundEntry.COLUMN_NAME_POINT3));
            roundPoints[3] = cursor.getInt(cursor.getColumnIndex(Round.RoundEntry.COLUMN_NAME_POINT4));

            //get round passed booleans
            pass = new boolean[4];
            temp = cursor.getInt(cursor.getColumnIndex(Round.RoundEntry.COLUMN_NAME_PASS1));
            if(temp == 1){
                pass[0] = true;
            }else{
                pass[0]= false;
            }
            temp = cursor.getInt(cursor.getColumnIndex(Round.RoundEntry.COLUMN_NAME_PASS2));
            if(temp == 1){
                pass[1] = true;
            }else{
                pass[1]= false;
            }
            temp = cursor.getInt(cursor.getColumnIndex(Round.RoundEntry.COLUMN_NAME_PASS3));
            if(temp == 1){
                pass[2] = true;
            }else{
                pass[2]= false;
            }
            temp = cursor.getInt(cursor.getColumnIndex(Round.RoundEntry.COLUMN_NAME_PASS4));
            if(temp == 1){
                pass[3] = true;
            }else{
                pass[3]= false;
            }

            //get finished boolean
            temp = cursor.getInt(cursor.getColumnIndex(Round.RoundEntry.COLUMN_NAME_FINISHED));
            if(temp == 1)
                finished = true;
            else finished = false;

            newRound = new Round(roundNum, roundPoints);
            newRound.setPassed(pass);
            newRound.setFinished(finished);
            newRound.setId(roundId);

            gameList.addRound(newRound, gameList.getRoundsNum());

            cursor.moveToNext();
        }
        setAddRoundBtn(finished);
    }

    private void setAddRoundBtn(boolean isAdd){
        //sets the fab buttons state
        if(isAdd){
            fab_add.show();
            fab_conform.hide();
        }else {
            fab_conform.show();
            fab_add.hide();
        }
    }

    private String getDateTime() {
        //gets the current date in string format
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void updateNamesInDB(){
        String[] names = gameList.getNames();

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        //updates the new names values in DB
        ContentValues values = new ContentValues();
        values.put(GameList.GameListEntry.COLUMN_NAME_PLYR_1, names[0]);
        values.put(GameList.GameListEntry.COLUMN_NAME_PLYR_2, names[1]);
        values.put(GameList.GameListEntry.COLUMN_NAME_PLYR_3, names[2]);
        values.put(GameList.GameListEntry.COLUMN_NAME_PLYR_4, names[3]);

        String selection = GameList.GameListEntry._ID + " = " + gameList.getId();
        db.update(GameList.GameListEntry.TABLE_NAME, values, selection, null);
    }

    private void updatePointsInDB(long roundId, int[] points){
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Round.RoundEntry.COLUMN_NAME_POINT1, points[0]);
        values.put(Round.RoundEntry.COLUMN_NAME_POINT2, points[1]);
        values.put(Round.RoundEntry.COLUMN_NAME_POINT3, points[2]);
        values.put(Round.RoundEntry.COLUMN_NAME_POINT4, points[3]);

        String selection = Round.RoundEntry._ID + " = " + roundId;
        db.update(Round.RoundEntry.TABLE_NAME, values, selection, null);
    }
}
