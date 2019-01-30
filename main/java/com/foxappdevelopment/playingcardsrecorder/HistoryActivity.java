package com.foxappdevelopment.playingcardsrecorder;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HistoryActivity extends AppCompatActivity {

    private static final int NEW_GAME_REQUEST = 1;
    private static final int EXISTING_GAME_REQUEST = 2;
    private ArrayList<GameListDemo> gameLists;
    private RecyclerView mRecyclerView;
    private HistoryAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private GameListDbHelper mDbHelper;
    private LinearLayout emptyListView;
    private InterstitialAd interstitialAd;
    private boolean newGame;
    private long gameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        firstOpenTutorial();

        mDbHelper = new GameListDbHelper(getApplicationContext());

        mRecyclerView = (RecyclerView) findViewById(R.id.history_recycler_view);
        emptyListView = (LinearLayout)findViewById(R.id.empty_list_layout);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        gameLists = new ArrayList<GameListDemo>();
        // specify an adapter
        mAdapter = new HistoryAdapter(gameLists);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(GameListDemo item) {
                if(interstitialAd.isLoaded()){
                    newGame = false;
                    gameId = item.getId();
                    interstitialAd.show();
                }else {
                    openGameListItem(item.getId());
                }
            }

            @Override
            public void onDeleteItemClick(GameListDemo item) {
                showDeleteWarning(item.getId());
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(interstitialAd.isLoaded()){
                    newGame = true;
                    interstitialAd.show();
                }else {
                    addNewTarneebGame();
                }
            }
        });

        getAllGames();

        if(gameLists.isEmpty())
            emptyListView.setVisibility(View.VISIBLE);
        else
            emptyListView.setVisibility(View.GONE);

        setAds();

    }

    private void firstOpenTutorial(){
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        if(sharedPref.getBoolean("first open", true)){
            openTutorialActivity();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("first open", false);
            editor.apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_clear_history) {
            showDeleteAllWarning();
            return true;
        }else if(id == R.id.about_item){
            openAboutActivity();
            return true;
        }else if(id == R.id.action_tutorial){
            openTutorialActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openTutorialActivity(){
        Intent intent = new Intent(HistoryActivity.this, TutorialActivity.class);
        startActivity(intent);
    }

    private void openAboutActivity(){
        Intent intent = new Intent(HistoryActivity.this, AboutActivity.class);
        startActivity(intent);
    }

    private void addNewTarneebGame(){
        Intent intent = new Intent(HistoryActivity.this, TableActivity.class);
        intent.putExtra("new", true);
        intent.putExtra("namesList", getAllNames());
        startActivityForResult(intent, NEW_GAME_REQUEST);
    }

    private String[] getAllNames(){
        ArrayList<String> namesList = new ArrayList<>();
        String[] namesTemp;
        for(int i = 0; i < gameLists.size(); i++){
            namesTemp = gameLists.get(i).getNames();
            namesList.add(namesTemp[0]);
            namesList.add(namesTemp[1]);
            namesList.add(namesTemp[2]);
            namesList.add(namesTemp[3]);
        }
        for (int i = namesList.size() - 1; i >= 1; i--){
            for (int j = i - 1; j >= 0; j--){
                if(namesList.get(i).equals(namesList.get(j))){
                    namesList.remove(j);
                    i--;
                }
            }
        }
        namesTemp = new String[namesList.size()];
        namesTemp = namesList.toArray(namesTemp);
        return namesTemp;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if(requestCode == NEW_GAME_REQUEST){
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                if(null != data){
                    long gameId = data.getLongExtra("id", 0);
                    if(0 != gameId){
                        String[] names = data.getStringArrayExtra("names");
                        int[] points = data.getIntArrayExtra("points");
                        Date date = new Date();
                        date.setTime(data.getLongExtra("date", -1));
                        GameListDemo game = new GameListDemo(gameId, date, names, points);
                        gameLists.add(0, game);
                        mAdapter.notifyItemInserted(0);
                        emptyListView.setVisibility(View.GONE);
                    }
                }
            }
        }else if(requestCode == EXISTING_GAME_REQUEST){
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                if (null != data) {
                    long gameId = data.getLongExtra("id", 0);
                    if(0 != gameId){
                        int size = gameLists.size();
                        for (int i = 0; i < size; i++) {
                            if(gameLists.get(i).getId() == gameId){
                                String[] names = data.getStringArrayExtra("names");
                                int[] points = data.getIntArrayExtra("points");
                                String date_text = data.getStringExtra("date");
                                Date date = new Date();
                                date.setTime(data.getLongExtra("date", -1));
                                GameListDemo temp = gameLists.get(i);
                                temp.setNames(names);
                                temp.setFinal_score(points);
                                temp.setDate(date);
                                mAdapter.notifyItemChanged(i);
                                break;
                            }
                        }

                    }
                }
            }
        }
    }

    private void getAllGames(){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        String[] projection = {"*"};

        // How you want the results sorted in the resulting Cursor
        String sortOrder = GameList.GameListEntry.COLUMN_NAME_DATE + " DESC";

        Cursor cursor = db.query(GameList.GameListEntry.TABLE_NAME, projection, null, null, null, null, sortOrder);


        String[] names;
        int[] score;
        String s;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d;
        GameListDemo gameList;
        long id;
        int count = 1;

        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            names = new String[4];
            score = new int[4];

            //get the id from the cursor
            id = cursor.getLong(cursor.getColumnIndex(GameList.GameListEntry._ID));
            //get the names from the cursor
            names[0] = cursor.getString(cursor.getColumnIndex(GameList.GameListEntry.COLUMN_NAME_PLYR_1));
            names[1] = cursor.getString(cursor.getColumnIndex(GameList.GameListEntry.COLUMN_NAME_PLYR_2));
            names[2] = cursor.getString(cursor.getColumnIndex(GameList.GameListEntry.COLUMN_NAME_PLYR_3));
            names[3] = cursor.getString(cursor.getColumnIndex(GameList.GameListEntry.COLUMN_NAME_PLYR_4));

            //get the final score from the cursor
            score[0] = cursor.getInt(cursor.getColumnIndex(GameList.GameListEntry.COLUMN_NAME_SCORE_1));
            score[1] = cursor.getInt(cursor.getColumnIndex(GameList.GameListEntry.COLUMN_NAME_SCORE_2));
            score[2] = cursor.getInt(cursor.getColumnIndex(GameList.GameListEntry.COLUMN_NAME_SCORE_3));
            score[3] = cursor.getInt(cursor.getColumnIndex(GameList.GameListEntry.COLUMN_NAME_SCORE_4));

            //get the date
            s = cursor.getString(cursor.getColumnIndex(GameList.GameListEntry.COLUMN_NAME_DATE));
            d = new Date();
            try {
                d = dateFormat.parse(s);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            gameList = new GameListDemo(id, d, names, score);
            gameLists.add(gameList);

            /*if(count % 4 == 0)
                gameLists.add(null);

            count++;*/
            cursor.moveToNext();
        }
        mAdapter.notifyDataSetChanged();
    }

    private void openGameListItem(long id){
        Intent intent = new Intent(HistoryActivity.this, TableActivity.class);
        intent.putExtra("new", false);
        intent.putExtra("id", id);
        startActivityForResult(intent, EXISTING_GAME_REQUEST);
    }

    private void deleteGameListItem(long id){
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = GameList.GameListEntry._ID + " = ?";
        String[] arg = {Long.toString(id)};
        int status = db.delete(GameList.GameListEntry.TABLE_NAME, selection, arg);
        //all the relevant round will delete automatically became of the "ON DELETE CASCADE"
        int size = gameLists.size();
        for (int i = 0; i < size; i++){
            if(gameLists.get(i).getId() == id){
                gameLists.remove(i);
                mAdapter.notifyItemRemoved(i);
                break;
            }
        }
        if(gameLists.isEmpty())
            emptyListView.setVisibility(View.VISIBLE);
    }

    private void deleteAllGames(){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(GameList.GameListEntry.TABLE_NAME, "1", null);
        db.delete(Round.RoundEntry.TABLE_NAME, "1", null);
        gameLists.clear();
        mAdapter.notifyDataSetChanged();
        emptyListView.setVisibility(View.VISIBLE);
    }

    private void showDeleteWarning(final long gameId){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Add the buttons
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked delete button
                deleteGameListItem(gameId);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });
        builder.setMessage(R.string.delete_game_msg)
                .setTitle(R.string.delete_game_title);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDeleteAllWarning(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Add the buttons
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked delete button
                deleteAllGames();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });
        builder.setMessage(R.string.delete_all_games_msg)
                .setTitle(R.string.delete_all_games_title);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void requestNewInterstitial(){
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        interstitialAd.loadAd(adRequest);
    }

    private void setAds() {
        //load an ad
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_ad));

        requestNewInterstitial();

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                if(newGame){
                    addNewTarneebGame();
                }else {
                    openGameListItem(gameId);
                }
            }
        });
    }


}
